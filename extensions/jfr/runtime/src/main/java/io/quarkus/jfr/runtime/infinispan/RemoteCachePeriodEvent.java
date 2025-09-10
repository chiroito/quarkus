package io.quarkus.jfr.runtime.infinispan;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label("RemoteCache")
@Category({ "Quarkus", "Cache" })
@Name("quarkus.RemoteCache")
@Description("Remote cache operation has been processing during this period")
public class RemoteCachePeriodEvent extends AbstractRemoteCacheEvent {
}
