package io.quarkus.jfr.runtime.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "quarkus.jfr.build")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface JfrBuildConfig {

    /**
     * If set to false, the mechanism for recording JFR events will not be built into Infinispan during the build process.
     * If the integration with Infinispan conflicts with other features and you wish to disable it, set this to false.
     * Default value is <code>true</code>
     */
    @WithName("infinispan.enabled")
    @WithDefault("true")
    boolean infinispanEnabled();
}
