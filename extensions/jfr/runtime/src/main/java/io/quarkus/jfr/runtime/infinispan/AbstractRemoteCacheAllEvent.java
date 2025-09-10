package io.quarkus.jfr.runtime.infinispan;

import jdk.jfr.Description;
import jdk.jfr.Label;

public class AbstractRemoteCacheAllEvent extends AbstractRemoteCacheEvent {

    @Label("Element Count")
    @Description("Number of elements processed in the cache operation")
    protected int elementCount;
}
