package io.quarkus.jfr.runtime.infinispan;

import java.util.function.UnaryOperator;

import org.infinispan.client.hotrod.RemoteCache;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class JfrInfinispanQuarkusRecorder {
    public RuntimeValue<UnaryOperator<RemoteCache<?, ?>>> createRemoteCacheWrapper() {
        return new RuntimeValue<>(delegate -> new JfrRemoteCacheWrapper<>(delegate));
    }
}
