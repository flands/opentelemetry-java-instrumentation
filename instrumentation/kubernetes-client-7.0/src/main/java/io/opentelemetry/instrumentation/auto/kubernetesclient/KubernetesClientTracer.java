/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.auto.kubernetesclient;

import static io.opentelemetry.trace.Span.Kind.CLIENT;

import io.opentelemetry.context.propagation.TextMapPropagator.Setter;
import io.opentelemetry.instrumentation.api.tracer.HttpClientTracer;
import io.opentelemetry.trace.Span;
import java.net.URI;
import okhttp3.Request;
import okhttp3.Response;

public class KubernetesClientTracer extends HttpClientTracer<Request, Request, Response> {
  public static final KubernetesClientTracer TRACER = new KubernetesClientTracer();

  @Override
  protected String method(Request httpRequest) {
    return httpRequest.method();
  }

  @Override
  protected URI url(Request httpRequest) {
    return httpRequest.url().uri();
  }

  @Override
  protected Integer status(Response httpResponse) {
    return httpResponse.code();
  }

  @Override
  protected String requestHeader(Request request, String name) {
    return request.header(name);
  }

  @Override
  protected String responseHeader(Response response, String name) {
    return response.header(name);
  }

  @Override
  protected Setter<Request> getSetter() {
    // TODO (trask) no propagation implemented yet?
    return null;
  }

  @Override
  protected String getInstrumentationName() {
    return "io.opentelemetry.auto.kubernetes-client-7.0";
  }

  /** This method is overridden to allow other classes in this package to call it. */
  @Override
  protected Span onRequest(Span span, Request request) {
    return super.onRequest(span, request);
  }

  /**
   * This method is used to generate an acceptable CLIENT span (operation) name based on a given
   * KubernetesRequestDigest.
   */
  public Span startSpan(KubernetesRequestDigest digest) {
    return tracer
        .spanBuilder(digest.toString())
        .setSpanKind(CLIENT)
        .setAttribute("namespace", digest.getResourceMeta().getNamespace())
        .setAttribute("name", digest.getResourceMeta().getName())
        .startSpan();
  }
}
