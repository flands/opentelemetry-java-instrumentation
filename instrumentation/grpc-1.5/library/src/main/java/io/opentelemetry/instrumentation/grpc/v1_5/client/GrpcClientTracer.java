/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.grpc.v1_5.client;

import static io.opentelemetry.trace.Span.Kind.CLIENT;

import io.grpc.Status;
import io.opentelemetry.instrumentation.api.tracer.RpcClientTracer;
import io.opentelemetry.instrumentation.grpc.v1_5.common.GrpcHelper;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Span.Builder;
import io.opentelemetry.trace.Tracer;
import io.opentelemetry.trace.attributes.SemanticAttributes;

public class GrpcClientTracer extends RpcClientTracer {

  protected GrpcClientTracer() {}

  protected GrpcClientTracer(Tracer tracer) {
    super(tracer);
  }

  public Span startSpan(String name) {
    Builder spanBuilder = tracer.spanBuilder(name).setSpanKind(CLIENT);
    spanBuilder.setAttribute(SemanticAttributes.RPC_SYSTEM, "grpc");
    return spanBuilder.startSpan();
  }

  public void endSpan(Span span, Status status) {
    span.setStatus(GrpcHelper.statusFromGrpcStatus(status), status.getDescription());
    end(span);
  }

  @Override
  protected void onError(Span span, Throwable throwable) {
    Status grpcStatus = Status.fromThrowable(throwable);
    super.onError(span, grpcStatus.getCause());
    span.setStatus(GrpcHelper.statusFromGrpcStatus(grpcStatus), grpcStatus.getDescription());
  }

  @Override
  protected String getInstrumentationName() {
    return "io.opentelemetry.auto.grpc-1.5";
  }
}
