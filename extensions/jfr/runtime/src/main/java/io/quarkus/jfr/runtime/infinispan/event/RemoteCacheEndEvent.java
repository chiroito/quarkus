package io.quarkus.jfr.runtime.infinispan.event;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Enabled;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label("RemoteCacheEnd")
@Category({ "Quarkus", "Cache" })
@Name("quarkus.RemoteCacheEnd")
@Description("Remote cache operation has completed")
@Enabled(false)
public class RemoteCacheEndEvent extends AbstractRemoteCacheEvent {
}
