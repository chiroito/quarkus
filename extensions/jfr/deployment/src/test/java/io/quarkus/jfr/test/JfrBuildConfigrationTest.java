package io.quarkus.jfr.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.arc.ClientProxy;
import io.quarkus.infinispan.client.Remote;
import io.quarkus.test.QuarkusUnitTest;

public class JfrBuildConfigrationTest {

    @RegisterExtension
    static final QuarkusUnitTest TEST = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.devservices.enabled", "true")
            .overrideConfigKey("quarkus.infinispan-client.devservices.enabled", "true")
            .overrideConfigKey("quarkus.jfr.build.infinispan.enabled", "false");

    @Inject
    @Remote("test")
    RemoteCache<String, String> cache;

    @Test
    void wrapperMustNotBeInjectedWhenDisabled() {
        Object unwrapped = ClientProxy.unwrap(cache);
        assertTrue(!unwrapped.getClass().getName().contains("JfrRemoteCacheWrapper"));
    }
}
