package io.quarkus.infinispan.client.runtime;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.util.AnnotationLiteral;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.counter.api.CounterManager;

import io.quarkus.arc.Arc;
import io.quarkus.arc.InterceptionProxy;
import io.quarkus.arc.SyntheticCreationalContext;
import io.quarkus.arc.runtime.BeanContainerListener;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.runtime.annotations.RelaxedValidation;

@Recorder
public class InfinispanRecorder {

    public BeanContainerListener configureInfinispan(@RelaxedValidation Map<String, Properties> properties) {
        return container -> {
            InfinispanClientProducer instance = container.beanInstance(InfinispanClientProducer.class);
            instance.setProperties(properties);
        };
    }

    public Supplier<RemoteCacheManager> infinispanClientSupplier(String clientName) {
        return new InfinispanClientSupplier<>(new Function<InfinispanClientProducer, RemoteCacheManager>() {
            @Override
            public RemoteCacheManager apply(InfinispanClientProducer infinispanClientProducer) {
                return infinispanClientProducer.getNamedRemoteCacheManager(clientName);
            }
        });
    }

    public Supplier<CounterManager> infinispanCounterManagerSupplier(String clientName) {
        return new InfinispanClientSupplier<>(new Function<InfinispanClientProducer, CounterManager>() {
            @Override
            public CounterManager apply(InfinispanClientProducer infinispanClientProducer) {
                return infinispanClientProducer.getNamedCounterManager(clientName);
            }
        });
    }

    public Function<SyntheticCreationalContext<RemoteCache<?, ?>>, RemoteCache<?, ?>> infinispanRemoteCacheClientFunction2(
            String clientName, String cacheName, RuntimeValue<UnaryOperator<RemoteCache<?, ?>>> wrapper) {
        return new Function<SyntheticCreationalContext<RemoteCache<?, ?>>, RemoteCache<?, ?>>() {
            @Override
            public RemoteCache<?, ?> apply(SyntheticCreationalContext<RemoteCache<?, ?>> ctx) {
                InfinispanClientProducer infinispanClientProducer = Arc.container().instance(InfinispanClientProducer.class)
                        .get();
                RemoteCache<?, ?> remoteCache = infinispanClientProducer.getRemoteCache(clientName, cacheName);

                if (wrapper == null) {
                    return remoteCache;
                } else {
                    InterceptionProxy<RemoteCache<?, ?>> proxy = ctx.getInterceptionProxy();
                    return proxy.create(wrapper.getValue().apply(remoteCache));
                }
            }
        };
    }

    public RuntimeValue<RemoteCacheManager> getClient(String name) {
        return new RuntimeValue<>(Arc.container().instance(RemoteCacheManager.class, literal(name)).get());
    }

    @SuppressWarnings("rawtypes")
    private AnnotationLiteral literal(String name) {
        if (name.startsWith(InfinispanClientUtil.DEFAULT_INFINISPAN_CLIENT_NAME)) {
            return Default.Literal.INSTANCE;
        }
        return NamedLiteral.of(name);
    }

    /** Helper to lazily create Infinispan clients. */
    static final class InfinispanClientSupplier<T> implements Supplier<T> {
        private final Function<InfinispanClientProducer, T> producer;

        InfinispanClientSupplier(Function<InfinispanClientProducer, T> producer) {
            this.producer = producer;
        }

        @Override
        public T get() {
            InfinispanClientProducer infinispanClientProducer = Arc.container().instance(InfinispanClientProducer.class).get();
            return producer.apply(infinispanClientProducer);
        }
    }
}
