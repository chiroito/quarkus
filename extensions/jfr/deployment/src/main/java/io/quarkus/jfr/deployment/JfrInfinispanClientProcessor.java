package io.quarkus.jfr.deployment;

import java.util.List;

import org.infinispan.client.hotrod.logging.Log;
import org.infinispan.client.hotrod.logging.LogFactory;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.Feature;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.jfr.runtime.infinispan.JfrRemoteCacheInterceptor;

public class JfrInfinispanClientProcessor {

    private static final Log LOG = LogFactory.getLog(JfrInfinispanClientProcessor.class);

    @BuildStep
    void addInterceptorForInfinispan(List<FeatureBuildItem> features,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans,
            BuildProducer<NativeImageProxyDefinitionBuildItem> nativeImageProxyDefinition) {

        if (features.stream().anyMatch(f -> f.getName().equals(Feature.INFINISPAN_CLIENT.getName()))) {
            LOG.info("Adding JFR support for Infinispan Client");
            additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(JfrRemoteCacheInterceptor.class));
            nativeImageProxyDefinition.produce(new NativeImageProxyDefinitionBuildItem(
                    "java.util.Map",
                    "java.util.concurrent.ConcurrentMap",
                    "java.util.concurrent.ConcurrentHashMap",
                    "org.infinispan.client.hotrod.RemoteCache"));
        }
    }
}
