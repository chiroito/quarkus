package io.quarkus.jfr.runtime.infinispan;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.interceptor.InterceptorBinding;

@InterceptorBinding
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
public @interface JfrRemoteCache {

    Type type();

    Target target();

    enum Type {
        SYNC,
        ASYNC
    }

    enum Target {
        SINGLE,
        ALL
    }
}
