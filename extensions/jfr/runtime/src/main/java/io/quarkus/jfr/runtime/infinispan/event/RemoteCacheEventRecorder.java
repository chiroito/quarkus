package io.quarkus.jfr.runtime.infinispan.event;

import java.lang.reflect.Method;

import org.infinispan.client.hotrod.RemoteCache;

public interface RemoteCacheEventRecorder {

    RemoteCacheEventRecorder createAndCommitStartEvent();

    RemoteCacheEventRecorder createPeriodEvent();

    RemoteCacheEventRecorder commitPeriodEvent();

    RemoteCacheEventRecorder createAndCommitEndEvent();

    static RemoteCacheEventRecorder create(RemoteCache remoteCache, Method method, String traceId, String spanId) {
        return new RemoteCacheEventPublisherImpl(remoteCache, method, traceId, spanId);
    }

    static RemoteCacheEventRecorder createAll(RemoteCache remoteCache, Method method, String traceId, String spanId,
            int size) {
        return new RemoteCacheEventPublisherImpl.RemoteCacheAllEventPublisherImpl(remoteCache, method, traceId, spanId, size);
    }

    class RemoteCacheEventPublisherImpl implements RemoteCacheEventRecorder {

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

        @Override
        public RemoteCacheEventRecorder createAndCommitStartEvent() {
            RemoteCacheStartEvent startEvent = new RemoteCacheStartEvent();
            if (startEvent.shouldCommit()) {
                setRemoteCacheInfo(startEvent, remoteCache, method, traceId, spanId);
                startEvent.commit();
            }
            return this;
        }

        @Override
        public RemoteCacheEventRecorder createPeriodEvent() {
            periodEvent = new RemoteCachePeriodEvent();
            periodEvent.begin();
            return this;
        }

        @Override
        public RemoteCacheEventRecorder commitPeriodEvent() {
            if (periodEvent != null) {
                periodEvent.end();
                if (periodEvent.shouldCommit()) {
                    setRemoteCacheInfo(periodEvent, remoteCache, method, traceId, spanId);
                    periodEvent.commit();
                }
            }
            return this;
        }

        @Override
        public RemoteCacheEventRecorder createAndCommitEndEvent() {
            RemoteCacheEndEvent endEvent = new RemoteCacheEndEvent();
            if (endEvent.shouldCommit()) {
                setRemoteCacheInfo(endEvent, remoteCache, method, traceId, spanId);
                endEvent.commit();
            }
            return this;
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

    class RemoteCacheAllEventPublisherImpl implements RemoteCacheEventRecorder {

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

        @Override
        public RemoteCacheEventRecorder createAndCommitStartEvent() {
            RemoteCacheAllStartEvent startEvent = new RemoteCacheAllStartEvent();
            if (startEvent.shouldCommit()) {
                setRemoteCacheAllInfo(startEvent, remoteCache, method, traceId, spanId, size);
                startEvent.commit();
            }
            return this;
        }

        @Override
        public RemoteCacheEventRecorder createPeriodEvent() {
            periodEvent = new RemoteCacheAllPeriodEvent();
            periodEvent.begin();
            return this;
        }

        @Override
        public RemoteCacheEventRecorder commitPeriodEvent() {
            if (periodEvent != null) {
                periodEvent.end();
                if (periodEvent.shouldCommit()) {
                    setRemoteCacheAllInfo(periodEvent, remoteCache, method, traceId, spanId, size);
                    periodEvent.commit();
                }
            }
            return this;
        }

        @Override
        public RemoteCacheEventRecorder createAndCommitEndEvent() {
            RemoteCacheAllEndEvent endEvent = new RemoteCacheAllEndEvent();
            if (endEvent.shouldCommit()) {
                setRemoteCacheAllInfo(endEvent, remoteCache, method, traceId, spanId, size);
                endEvent.commit();
            }
            return this;
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
