package io.quarkus.jfr.runtime.infinispan;

import io.quarkus.jfr.runtime.SpanIdRelational;
import io.quarkus.jfr.runtime.TraceIdRelational;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;

abstract class AbstractRemoteCacheEvent extends Event {

    @Label("Trace ID")
    @Description("Trace ID to identify the request")
    @TraceIdRelational
    protected String traceId;

    @Label("Span ID")
    @Description("Span ID to identify the request if necessary")
    @SpanIdRelational
    protected String spanId;

    @Label("Method")
    @Description("Method executed")
    protected String method;

    @Label("Cache Name")
    @Description("Name of the remote cache")
    protected String cacheName;

    @Label("Cluster Name")
    @Description("Name of the cluster containing the remote cache")
    protected String clusterName;
}
