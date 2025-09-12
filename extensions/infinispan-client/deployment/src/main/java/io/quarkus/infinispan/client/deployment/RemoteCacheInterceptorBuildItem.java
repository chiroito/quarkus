package io.quarkus.infinispan.client.deployment;

import java.util.function.UnaryOperator;

import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.jandex.DotName;

import io.quarkus.builder.item.SimpleBuildItem;
import io.quarkus.runtime.RuntimeValue;

public final class RemoteCacheInterceptorBuildItem extends SimpleBuildItem {

    private final DotName providerTypeName;
    private final DotName bindingsSourceName;

    private final RuntimeValue<UnaryOperator<RemoteCache<?, ?>>> wrapperFactory;

    public RemoteCacheInterceptorBuildItem(DotName providerTypeName, DotName bindingsSourceName,
            RuntimeValue<UnaryOperator<RemoteCache<?, ?>>> wrapperFactory) {
        this.providerTypeName = providerTypeName;
        this.bindingsSourceName = bindingsSourceName;
        this.wrapperFactory = wrapperFactory;
    }

    public DotName getProviderTypeName() {
        return providerTypeName;
    }

    public DotName getBindingsSourceName() {
        return bindingsSourceName;
    }

    public RuntimeValue<UnaryOperator<RemoteCache<?, ?>>> getWrapperFactory() {
        return wrapperFactory;
    }
}
