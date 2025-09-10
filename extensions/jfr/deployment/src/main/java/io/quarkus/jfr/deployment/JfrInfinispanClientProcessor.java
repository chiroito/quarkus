package io.quarkus.jfr.deployment;

import java.util.function.BooleanSupplier;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.jfr.runtime.infinispan.JfrRemoteCacheInterceptor;

public class JfrInfinispanClientProcessor {

    @BuildStep(onlyIf = IsInfinispanClientPresent.class)
    void add(BuildProducer<AdditionalBeanBuildItem> additionalBeans, BuildProducer<UnremovableBeanBuildItem> unremovableBeans) {
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(JfrRemoteCacheInterceptor.class));
    }

    static class IsInfinispanClientPresent implements BooleanSupplier {
        @Override
        public boolean getAsBoolean() {
            return (QuarkusClassLoader.isClassPresentAtRuntime("org.infinispan.client.hotrod.RemoteCache")
                    && QuarkusClassLoader.isClassPresentAtRuntime("io.quarkus.infinispan.client.Remote"));
        }
    }
}
