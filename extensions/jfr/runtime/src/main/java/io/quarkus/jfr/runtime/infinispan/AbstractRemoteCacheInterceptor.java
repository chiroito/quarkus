package io.quarkus.jfr.runtime.infinispan;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

import org.infinispan.client.hotrod.RemoteCache;

import io.quarkus.jfr.runtime.IdProducer;
import io.quarkus.jfr.runtime.infinispan.event.RemoteCacheEventRecorder;

public abstract class AbstractRemoteCacheInterceptor {

    @Inject
    IdProducer idProducer;

    @AroundInvoke
    Object aroundInvoke(InvocationContext context) throws Exception {
        if (context.getTarget() instanceof RemoteCache remoteCache) {
            RemoteCacheEventRecorder eventRecorder = getPublisher(context, remoteCache);
            eventRecorder.createAndCommitStartEvent().createPeriodEvent();
            try {
                return invoke(context, eventRecorder);
            } finally {
                eventRecorder.commitPeriodEvent().createAndCommitEndEvent();
            }
        } else {
            return context.proceed();
        }
    }

    abstract RemoteCacheEventRecorder getPublisher(InvocationContext context, RemoteCache remoteCache);

    protected RemoteCacheEventRecorder getPublisherForAllOperation(InvocationContext context, RemoteCache remoteCache) {
        Method method = context.getMethod();
        Collection allParams = (Collection) context.getParameters()[0];
        return RemoteCacheEventRecorder.createAll(remoteCache, method, idProducer.getTraceId(), idProducer.getSpanId(),
                allParams.size());
    }

    protected RemoteCacheEventRecorder getPublisherForSingleOperation(InvocationContext context, RemoteCache remoteCache) {
        Method method = context.getMethod();
        return RemoteCacheEventRecorder.create(remoteCache, method, idProducer.getTraceId(), idProducer.getSpanId());
    }

    abstract Object invoke(InvocationContext context, RemoteCacheEventRecorder publisher) throws Exception;

    protected Object invokeSync(InvocationContext context, RemoteCacheEventRecorder publisher) throws Exception {
        return context.proceed();
    }

    protected Object invokeAsync(InvocationContext context, RemoteCacheEventRecorder publisher) throws Exception {
        return ((CompletableFuture<?>) (context.proceed())).whenComplete(new RemoteCacheEventEndHandler(publisher));
    }

    record RemoteCacheEventEndHandler(RemoteCacheEventRecorder publisher) implements BiConsumer<Object, Throwable> {

        @Override
        public void accept(Object result, Throwable throwable) {
            publisher.commitPeriodEvent().createAndCommitEndEvent();

            if (throwable != null) {
                throw new RuntimeException(throwable);
            }
        }
    }
}
