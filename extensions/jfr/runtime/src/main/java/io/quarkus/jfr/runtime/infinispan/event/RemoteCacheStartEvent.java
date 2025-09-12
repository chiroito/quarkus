package io.quarkus.jfr.runtime.infinispan.event;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Enabled;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label("RemoteCacheStart")
@Category({ "Quarkus", "Cache" })
@Name("quarkus.RemoteCacheStart")
@Description("Remote cache operation has started")
@Enabled(false)
public class RemoteCacheStartEvent extends AbstractRemoteCacheEvent {
}
