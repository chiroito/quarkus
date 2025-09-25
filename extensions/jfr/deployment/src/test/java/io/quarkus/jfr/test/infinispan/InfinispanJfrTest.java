package io.quarkus.jfr.test.infinispan;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.infinispan.client.Remote;
import io.quarkus.jfr.test.EnabledIfContainerAvailable;
import io.quarkus.test.QuarkusUnitTest;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingStream;

@ActivateRequestContext
@EnabledIfContainerAvailable
public class InfinispanJfrTest {

    @RegisterExtension
    static final QuarkusUnitTest TEST = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.devservices.enabled", "true")
            .overrideConfigKey("quarkus.infinispan-client.devservices.enabled", "true");

    @Remote(DEFAULT_CACHE_NAME)
    @Inject
    RemoteCache<String, String> remoteCache;

    private static final String DEFAULT_CLUSTER = "___DEFAULT-CLUSTER___";
    private static final String DEFAULT_CACHE_NAME = "test";

    @Test
    void testSyncSingle() {
        String[] testEvents = { "quarkus.RemoteCache", "quarkus.RemoteCacheStart", "quarkus.RemoteCacheEnd" };

        try (RecordingStream stream = new RecordingStream()) {
            CountDownLatch latch = new CountDownLatch(3);
            Set<String> traceIds = new HashSet<>(1);
            for (String testEvent : testEvents) {
                stream.enable(testEvent);
                stream.onEvent(testEvent, e -> {
                    latch.countDown();
                    traceIds.add(e.getString("traceId"));
                    assertEvent(e, DEFAULT_CACHE_NAME, DEFAULT_CLUSTER, "put");
                });
            }
            stream.startAsync();

            remoteCache.put("key", "value");

            latch.await(2, TimeUnit.SECONDS);
            if (latch.getCount() != 0) {
                Assertions.fail("Timed out waiting for remote cache event");
            }
            Assertions.assertEquals(1, traceIds.size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSyncAll() {
        String[] testEvents = { "quarkus.RemoteCacheAll", "quarkus.RemoteCacheAllStart", "quarkus.RemoteCacheAllEnd" };

        try (RecordingStream stream = new RecordingStream()) {
            CountDownLatch latch = new CountDownLatch(3);
            Set<String> traceIds = new HashSet<>(1);
            for (String testEvent : testEvents) {
                stream.enable(testEvent);
                stream.onEvent(testEvent, e -> {
                    latch.countDown();
                    traceIds.add(e.getString("traceId"));
                    assertAllEvent(e, DEFAULT_CACHE_NAME, DEFAULT_CLUSTER, "getAll", 2);
                });
            }
            stream.startAsync();

            remoteCache.getAll(Set.of("key1", "key2"));

            latch.await(5, TimeUnit.SECONDS);
            if (latch.getCount() != 0) {
                Assertions.fail("Timed out waiting for remote cache event");
            }
            Assertions.assertEquals(1, traceIds.size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAsyncSingle() {
        String[] testEvents = { "quarkus.RemoteCache", "quarkus.RemoteCacheStart", "quarkus.RemoteCacheEnd" };

        try (RecordingStream stream = new RecordingStream()) {
            CountDownLatch latch = new CountDownLatch(3);
            Set<String> traceIds = new HashSet<>(1);
            for (String testEvent : testEvents) {
                stream.enable(testEvent);
                stream.onEvent(testEvent, e -> {
                    latch.countDown();
                    traceIds.add(e.getString("traceId"));
                    assertEvent(e, DEFAULT_CACHE_NAME, DEFAULT_CLUSTER, "putAsync");
                });
            }
            stream.startAsync();

            remoteCache.putAsync("key", "value");

            latch.await(2, TimeUnit.SECONDS);
            if (latch.getCount() != 0) {
                Assertions.fail("Timed out waiting for remote cache event");
            }
            Assertions.assertEquals(1, traceIds.size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAsyncAll() {
        String[] testEvents = { "quarkus.RemoteCacheAll", "quarkus.RemoteCacheAllStart", "quarkus.RemoteCacheAllEnd" };

        try (RecordingStream stream = new RecordingStream()) {
            CountDownLatch latch = new CountDownLatch(3);
            Set<String> traceIds = new HashSet<>(1);
            for (String testEvent : testEvents) {
                stream.enable(testEvent);
                stream.onEvent(testEvent, e -> {
                    latch.countDown();
                    traceIds.add(e.getString("traceId"));
                    assertAllEvent(e, DEFAULT_CACHE_NAME, DEFAULT_CLUSTER, "getAllAsync", 2);
                });
            }
            stream.startAsync();

            remoteCache.getAllAsync(Set.of("key1", "key2"));

            latch.await(10, TimeUnit.SECONDS);
            if (latch.getCount() != 0) {
                Assertions.fail("Timed out waiting for remote cache event");
            }
            Assertions.assertEquals(1, traceIds.size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
    void testSyncException() {
        try (RecordingStream stream = new RecordingStream()) {
            stream.enable("quarkus.RemoteCache");
            CountDownLatch latch = new CountDownLatch(1);
            stream.onEvent("quarkus.RemoteCache", e -> {
                latch.countDown();
                assertEvent(e, DEFAULT_CACHE_NAME, DEFAULT_CLUSTER, "compute");
            });
            stream.startAsync();

            try {
                remoteCache.compute("key", (k, v) -> {
                    throw new RuntimeException();
                });
                Assertions.fail("Expected exception");
            } catch (RuntimeException e) {
                // expected
            }

            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Assertions.fail("Time outed");
        }
    }

    @Test
    void testAsyncException() {
        try (RecordingStream stream = new RecordingStream()) {
            stream.enable("quarkus.RemoteCache");
            CountDownLatch latch = new CountDownLatch(1);
            stream.onEvent("quarkus.RemoteCache", e -> {
                latch.countDown();
                assertEvent(e, DEFAULT_CACHE_NAME, DEFAULT_CLUSTER, "computeAsync");
            });
            stream.startAsync();

            try {
                remoteCache.computeAsync("key", (k, v) -> {
                    throw new RuntimeException();
                }).join();
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
