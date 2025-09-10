package io.quarkus.jfr.test.infinispan;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.infinispan.client.Remote;
import io.quarkus.jfr.runtime.infinispan.RemoteCacheEndEvent;
import io.quarkus.jfr.runtime.infinispan.RemoteCachePeriodEvent;
import io.quarkus.jfr.runtime.infinispan.RemoteCacheStartEvent;
import io.quarkus.test.QuarkusUnitTest;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingStream;

@ActivateRequestContext
public class InfinispanJfrTest {

    @RegisterExtension
    static final QuarkusUnitTest TEST = new QuarkusUnitTest();

    @Remote(DEFAULT_CACHE_NAME)
    @Inject
    RemoteCache<String, String> remoteCache;

    private static final String DEFAULT_CLUSTER = "___DEFAULT-CLUSTER___";
    private static final String DEFAULT_CACHE_NAME = "test";

    @Test
    public void RemoteCacheEventTest() {
        testEvent("quarkus.RemoteCache");
        testEvent("quarkus.RemoteCacheStart");
        testEvent("quarkus.RemoteCacheEnd");
        testAllEvent("quarkus.RemoteCacheAll");
        testAllEvent("quarkus.RemoteCacheStartAll");
        testAllEvent("quarkus.RemoteCacheEndAll");
        testEventAsync("quarkus.RemoteCache");
        testEventAsync("quarkus.RemoteCacheStart");
        testEventAsync("quarkus.RemoteCacheEnd");
        testAllEventAsync("quarkus.RemoteCacheAll");
        testAllEventAsync("quarkus.RemoteCacheStartAll");
        testAllEventAsync("quarkus.RemoteCacheEndAll");
    }

    private void test(String jfrEventName, Runnable remoteCacheRun, Consumer<RecordedEvent> assertRun) {
        try (RecordingStream stream = new RecordingStream()) {
            stream.enable(jfrEventName);
            CountDownLatch latch = new CountDownLatch(1);
            stream.onEvent(jfrEventName, e -> {
                latch.countDown();
                assertRun.accept(e);
            });
            stream.startAsync();

            remoteCacheRun.run();

            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void testEvent(String jfrEventName) {
        test(jfrEventName, () -> remoteCache.put("key", "value"),
                e -> assertEvent(e, DEFAULT_CACHE_NAME, DEFAULT_CLUSTER, "put"));
    }

    private void testAllEvent(String jfrEventName) {
        test(jfrEventName, () -> remoteCache.getAll(Set.of("key1", "key2")),
                e -> assertAllEvent(e, DEFAULT_CACHE_NAME, DEFAULT_CLUSTER, "getAll", 2));
    }

    private void testEventAsync(String jfrEventName) {
        test(jfrEventName, () -> remoteCache.putAsync("key", "value"),
                e -> assertEvent(e, DEFAULT_CACHE_NAME, DEFAULT_CLUSTER, "putAsync"));
    }

    private void testAllEventAsync(String jfrEventName) {
        test(jfrEventName, () -> remoteCache.getAllAsync(Set.of("key1", "key2")),
                e -> assertAllEvent(e, DEFAULT_CACHE_NAME, DEFAULT_CLUSTER, "getAllAsync", 2));
    }

    private void assertEvent(RecordedEvent recordedEvent, String cacheName, String clusterName, String methodName) {
        Assertions.assertNotNull(recordedEvent.getString("traceId"));
        Assertions.assertEquals(cacheName, recordedEvent.getString("cacheName"));
        Assertions.assertEquals(clusterName, recordedEvent.getString("clusterName"));
        Assertions.assertEquals(methodName, recordedEvent.getString("method"));
    }

    private void assertAllEvent(RecordedEvent recordedEvent, String cacheName, String clusterName, String methodName,
            int elementCount) {
        Assertions.assertNotNull(recordedEvent.getString("traceId"));
        Assertions.assertEquals(cacheName, recordedEvent.getString("cacheName"));
        Assertions.assertEquals(clusterName, recordedEvent.getString("clusterName"));
        Assertions.assertEquals(methodName, recordedEvent.getString("method"));
        Assertions.assertEquals(elementCount, recordedEvent.getInt("elementCount"));
    }

    @Test
    public void RemoteCacheEventsTraceIdTest() {
        testTraceId(Set.of("quarkus.RemoteCache", "quarkus.RemoteCacheStart", "quarkus.RemoteCacheEnd"),
                () -> remoteCache.put("key", "value"));
        testTraceId(Set.of("quarkus.RemoteCache", "quarkus.RemoteCacheStart", "quarkus.RemoteCacheEnd"),
                () -> remoteCache.putAsync("key", "value"));
        testTraceId(Set.of("quarkus.RemoteCacheAll", "quarkus.RemoteCacheAllStart", "quarkus.RemoteCacheAllEnd"),
                () -> remoteCache.getAll(Set.of("key1", "key2")));
        testTraceId(Set.of("quarkus.RemoteCacheAll", "quarkus.RemoteCacheAllStart", "quarkus.RemoteCacheAllEnd"),
                () -> remoteCache.getAllAsync(Set.of("key1", "key2")));
    }

    private void testTraceId(Set<String> jfrEventNames, Runnable remoteCacheRun) {
        try (RecordingStream stream = new RecordingStream()) {
            stream.enable(RemoteCachePeriodEvent.class);
            stream.enable(RemoteCacheStartEvent.class);
            stream.enable(RemoteCacheEndEvent.class);
            Set<String> traceIds = new HashSet<>(1);
            CountDownLatch latch = new CountDownLatch(3);
            jfrEventNames.forEach(name -> {
                stream.onEvent(name, e -> {
                    latch.countDown();
                    traceIds.add(e.getString("traceId"));
                });
            });
            stream.startAsync();

            remoteCacheRun.run();

            latch.await(2, TimeUnit.SECONDS);
            Assertions.assertEquals(1, traceIds.size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void RemoteCacheEventExceptionTest() {
        testException("quarkus.RemoteCache", () -> remoteCache.compute("key", (k, v) -> {
            throw new RuntimeException();
        }),
                e -> assertEvent(e, DEFAULT_CACHE_NAME, DEFAULT_CLUSTER, "compute"));
        testException("quarkus.RemoteCache", () -> {
            remoteCache.computeAsync("key", (k, v) -> {
                throw new RuntimeException();
            }).join();
        },
                e -> assertEvent(e, DEFAULT_CACHE_NAME, DEFAULT_CLUSTER, "computeAsync"));
    }

    private void testException(String jfrEventName, Runnable remoteCacheRun, Consumer<RecordedEvent> assertRun) {
        try (RecordingStream stream = new RecordingStream()) {
            stream.enable(jfrEventName);
            CountDownLatch latch = new CountDownLatch(1);
            stream.onEvent(jfrEventName, e -> {
                latch.countDown();
                assertRun.accept(e);
            });
            stream.startAsync();

            try {
                remoteCacheRun.run();
                Assertions.fail("Expected exception");
            } catch (RuntimeException e) {
                // expected
            }

            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Assertions.fail("Time outed");
        }
    }
}
