package io.quarkus.opentelemetry.runtime.logs;

import java.util.Optional;
import java.util.logging.Handler;

import io.opentelemetry.api.OpenTelemetry;
import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.opentelemetry.runtime.config.runtime.OTelRuntimeConfig;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class OpenTelemetryLogRecorder {
    public RuntimeValue<Optional<Handler>> initializeHandler(final BeanContainer beanContainer,
            final OTelRuntimeConfig config) {
        if (config.sdkDisabled() || !config.logs().handlerEnabled()) {
            return new RuntimeValue<>(Optional.empty());
        }
        final OpenTelemetry openTelemetry = beanContainer.beanInstance(OpenTelemetry.class);
        final OpenTelemetryLogHandler logHandler = new OpenTelemetryLogHandler(openTelemetry);
        logHandler.setLevel(config.logs().level());
        return new RuntimeValue<>(Optional.of(logHandler));
    }
}
