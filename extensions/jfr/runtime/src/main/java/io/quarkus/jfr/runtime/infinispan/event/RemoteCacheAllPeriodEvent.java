package io.quarkus.jfr.runtime.infinispan.event;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label("RemoteCacheAll")
@Category({ "Quarkus", "Cache" })
@Name("quarkus.RemoteCacheAll")
@Description("Batch remote cache operation has been processing during this period")
public class RemoteCacheAllPeriodEvent extends AbstractRemoteCacheAllEvent {
}
