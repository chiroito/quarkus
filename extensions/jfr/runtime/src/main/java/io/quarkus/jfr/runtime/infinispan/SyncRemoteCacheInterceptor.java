package io.quarkus.jfr.runtime.infinispan;

import static io.quarkus.jfr.runtime.infinispan.JfrRemoteCache.Target.SINGLE;
import static io.quarkus.jfr.runtime.infinispan.JfrRemoteCache.Type.SYNC;

import jakarta.annotation.Priority;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.infinispan.client.hotrod.RemoteCache;

import io.quarkus.jfr.runtime.infinispan.event.RemoteCacheEventRecorder;

@JfrRemoteCache(type = SYNC, target = SINGLE)
@Interceptor
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class SyncRemoteCacheInterceptor extends AbstractRemoteCacheInterceptor {

    @Override
    RemoteCacheEventRecorder getPublisher(InvocationContext context, RemoteCache remoteCache) {
        return getPublisherForSingleOperation(context, remoteCache);
    }

    @Override
    Object invoke(InvocationContext context, RemoteCacheEventRecorder publisher) throws Exception {
        return invokeSync(context, publisher);
    }
}
