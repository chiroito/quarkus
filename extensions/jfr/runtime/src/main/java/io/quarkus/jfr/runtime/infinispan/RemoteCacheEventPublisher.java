package io.quarkus.jfr.runtime.infinispan;

import java.lang.reflect.Method;

import org.infinispan.client.hotrod.RemoteCache;

public interface RemoteCacheEventPublisher {

    void start();

    void end();

    static RemoteCacheEventPublisher create(RemoteCache remoteCache, Method method, String traceId, String spanId) {
        return new RemoteCacheEventPublisherImpl(remoteCache, method, traceId, spanId);
    }

    static RemoteCacheEventPublisher createAll(RemoteCache remoteCache, Method method, String traceId, String spanId,
            int size) {
        return new RemoteCacheEventPublisherImpl.RemoteCacheAllEventPublisherImpl(remoteCache, method, traceId, spanId, size);
    }

    class RemoteCacheEventPublisherImpl implements RemoteCacheEventPublisher {

        RemoteCachePeriodEvent periodEvent = null;
        private final RemoteCache remoteCache;
        private final Method method;
        private final String traceId;
        private final String spanId;

        public RemoteCacheEventPublisherImpl(RemoteCache remoteCache, Method method, String traceId, String spanId) {
            this.remoteCache = remoteCache;
            this.method = method;
            this.traceId = traceId;
            this.spanId = spanId;
        }

        public void start() {
            RemoteCacheStartEvent startEvent = new RemoteCacheStartEvent();
            if (startEvent.shouldCommit()) {
                setRemoteCacheInfo(startEvent, remoteCache, method, traceId, spanId);
                startEvent.commit();
            }

            periodEvent = new RemoteCachePeriodEvent();
            periodEvent.begin();
        }

        public void end() {
            if (periodEvent != null) {
                periodEvent.end();
                if (periodEvent.shouldCommit()) {
                    setRemoteCacheInfo(periodEvent, remoteCache, method, traceId, spanId);
                    periodEvent.commit();
                }
            }

            RemoteCacheEndEvent endEvent = new RemoteCacheEndEvent();
            if (endEvent.shouldCommit()) {
                setRemoteCacheInfo(endEvent, remoteCache, method, traceId, spanId);
                endEvent.commit();
            }
        }

        private void setRemoteCacheInfo(AbstractRemoteCacheEvent event, RemoteCache remoteCache, Method method,
                String traceId, String spanId) {
            event.traceId = traceId;
            event.spanId = spanId;
            event.clusterName = remoteCache.getRemoteCacheContainer().getCurrentClusterName();
            event.cacheName = remoteCache.getName();
            event.method = method.getName();
        }
    }

    class RemoteCacheAllEventPublisherImpl implements RemoteCacheEventPublisher {

        RemoteCacheAllPeriodEvent periodEvent = null;
        private final RemoteCache remoteCache;
        private final Method method;
        private final String traceId;
        private final String spanId;
        private int size;

        public RemoteCacheAllEventPublisherImpl(RemoteCache remoteCache, Method method, String traceId, String spanId,
                int size) {
            this.size = size;
            this.spanId = spanId;
            this.traceId = traceId;
            this.method = method;
            this.remoteCache = remoteCache;
        }

        public void start() {
            RemoteCacheAllStartEvent startEvent = new RemoteCacheAllStartEvent();
            if (startEvent.shouldCommit()) {
                setRemoteCacheAllInfo(startEvent, remoteCache, method, traceId, spanId, size);
                startEvent.commit();
            }

            periodEvent = new RemoteCacheAllPeriodEvent();
            periodEvent.begin();
        }

        public void end() {
            if (periodEvent != null) {
                periodEvent.end();
                if (periodEvent.shouldCommit()) {
                    setRemoteCacheAllInfo(periodEvent, remoteCache, method, traceId, spanId, size);
                    periodEvent.commit();
                }
            }
            RemoteCacheAllEndEvent endEvent = new RemoteCacheAllEndEvent();
            if (endEvent.shouldCommit()) {
                setRemoteCacheAllInfo(endEvent, remoteCache, method, traceId, spanId, size);
                endEvent.commit();
            }
        }

        private void setRemoteCacheAllInfo(AbstractRemoteCacheAllEvent event, RemoteCache remoteCache, Method method,
                String traceId, String spanId, int size) {
            event.traceId = traceId;
            event.spanId = spanId;
            event.clusterName = remoteCache.getRemoteCacheContainer().getCurrentClusterName();
            event.cacheName = remoteCache.getName();
            event.method = method.getName();
            event.elementCount = size;
        }
    }
}
