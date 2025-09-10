package io.quarkus.jfr.runtime.infinispan;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Enabled;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label("RemoteCacheAllStart")
@Category({ "Quarkus", "Cache" })
@Name("quarkus.RemoteCacheAllStart")
@Description("Batch remote cache operation has started")
@Enabled(false)
public class RemoteCacheAllStartEvent extends AbstractRemoteCacheAllEvent {
}
