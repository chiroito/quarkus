package io.quarkus.jfr.runtime.infinispan;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.logging.Logger;

import io.quarkus.infinispan.client.InfinispanClientName;
import io.quarkus.infinispan.client.Remote;
import io.quarkus.jfr.runtime.IdProducer;

@Remote
@InfinispanClientName(value = "")
@Any
@Dependent
@Interceptor
@Priority(0)
public class JfrRemoteCacheInterceptor {

    @Inject
    IdProducer idProducer;

    private static final Logger LOG = Logger.getLogger(JfrRemoteCacheInterceptor.class);

    @AroundInvoke
    Object logEvent(InvocationContext context) throws Exception {

        if (context.getTarget() instanceof RemoteCache remoteCache) {
            Method method = context.getMethod();
            Class<?> returnType = method.getReturnType();
            boolean isAsync = returnType.equals(CompletableFuture.class);
            RemoteCacheEventPublisher publisher = null;

            if (context.getParameters().length > 0 && context.getParameters()[0] instanceof Collection allParams) {
                publisher = RemoteCacheEventPublisher.createAll(remoteCache, method, idProducer.getTraceId(),
                        idProducer.getSpanId(), allParams.size());
            } else {
                publisher = RemoteCacheEventPublisher.create(remoteCache, method, idProducer.getTraceId(),
                        idProducer.getSpanId());
            }

            if (isAsync) {
                publisher.start();
                CompletableFuture<?> ret = null;
                try {
                    ret = ((CompletableFuture<?>) (context.proceed())).whenComplete(new RemoteCacheEventEndHandler(publisher));
                    return ret;
                } catch (Exception e) {
                    publisher.end();
                    throw e;
                }
            } else {
                publisher.start();
                try {
                    return context.proceed();
                } finally {
                    publisher.end();
                }
            }
        } else {
            return context.proceed();
        }
    }

    record RemoteCacheEventEndHandler(RemoteCacheEventPublisher publisher) implements BiConsumer<Object, Throwable> {

        @Override
        public void accept(Object result, Throwable throwable) {
            publisher.end();

            if (throwable != null) {
                throw new RuntimeException(throwable);
            }
        }
    }
}
