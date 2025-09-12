package io.quarkus.jfr.deployment;

import java.util.List;
import java.util.function.BooleanSupplier;

import org.jboss.jandex.DotName;
import org.jboss.logging.Logger;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.Feature;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.infinispan.client.deployment.RemoteCacheInterceptorBuildItem;
import io.quarkus.jfr.runtime.config.JfrBuildConfig;
import io.quarkus.jfr.runtime.infinispan.*;

public class JfrInfinispanClientProcessor {

    private static Logger log = Logger.getLogger(JfrInfinispanClientProcessor.class.getName());
    private static final DotName INFINISPAN_REMOTE_CACHE = DotName
            .createSimple("io.quarkus.infinispan.client.Remote");

    @BuildStep(onlyIf = IsInfinispanAvailable.class)
    @Record(ExecutionTime.RUNTIME_INIT)
    void addInterceptorForInfinispan(BuildProducer<AdditionalBeanBuildItem> additionalBeans,
            CombinedIndexBuildItem index,
            List<FeatureBuildItem> features,
            BuildProducer<RemoteCacheInterceptorBuildItem> interceptorBean,
            JfrInfinispanQuarkusRecorder jfrInfinispanRecorder) {

        if (features.stream().filter(f -> f.getName().equals(Feature.INFINISPAN_CLIENT.getName())).findFirst().isEmpty()) {
            return;
        }
        log.info("Adding JDK Flight Recorder for Infinispan RemoteCache");
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(AsyncAllRemoteCacheInterceptor.class));
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(SyncAllRemoteCacheInterceptor.class));
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(AsyncRemoteCacheInterceptor.class));
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(SyncRemoteCacheInterceptor.class));
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(JfrRemoteCache.class));

        interceptorBean.produce(new RemoteCacheInterceptorBuildItem(DotName.createSimple(JfrRemoteCacheWrapper.class),
                DotName.createSimple(JfrRemoteCacheWrapper.class),
                jfrInfinispanRecorder.createRemoteCacheWrapper()));
    }

    static class IsInfinispanAvailable implements BooleanSupplier {

        JfrBuildConfig cfg;

        @Override
        public boolean getAsBoolean() {
            return cfg.infinispanEnabled();
        }
    }
}
