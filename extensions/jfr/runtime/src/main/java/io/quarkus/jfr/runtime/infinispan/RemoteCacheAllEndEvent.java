package io.quarkus.jfr.runtime.infinispan;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Enabled;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label("RemoteCacheAllEnd")
@Category({ "Quarkus", "Cache" })
@Name("quarkus.RemoteCacheAllEnd")
@Description("Batch remote cache operation has completed")
@Enabled(false)
public class RemoteCacheAllEndEvent extends AbstractRemoteCacheAllEvent {
}
