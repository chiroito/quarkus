package io.quarkus.jfr.test;

import java.lang.annotation.*;

import org.junit.jupiter.api.extension.ExtendWith;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ExtendWith(ContainerCondition.class)
public @interface EnabledIfContainerAvailable {
}
