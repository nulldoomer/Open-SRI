// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.api.dto;

/**
 * A single event emitted while a playground job runs through the SRI pipeline.
 *
 * <p>Events are streamed to the client over SSE. The {@code type} drives the SSE event name and how
 * the frontend renders it:
 *
 * <ul>
 *   <li>{@code step} — a pipeline step finished (build, access_key, serialize, sign, send,
 *       authorize); {@code step} holds the step id and {@code message} optional context.
 *   <li>{@code xml} — generated XML; {@code message} is the kind ({@code raw}/{@code signed}) and
 *       {@code payload} the XML content.
 *   <li>{@code done} — the pipeline finished; {@code payload} carries the final result.
 *   <li>{@code error} — the pipeline failed; {@code message} carries a human-readable reason.
 * </ul>
 *
 * @param type event category that drives the SSE event name
 * @param step pipeline step id for {@code step} events, otherwise {@code null}
 * @param status step outcome ({@code ok}/{@code error}) when applicable
 * @param message human-readable context, xml kind, or error reason depending on {@code type}
 * @param payload structured data for {@code xml}/{@code done} events
 */
public record SdkEvent(String type, String step, String status, String message, Object payload) {

  /**
   * Builds a successful step event.
   *
   * @param step pipeline step id
   * @return step event with {@code ok} status
   */
  public static SdkEvent step(String step) {
    return new SdkEvent("step", step, "ok", null, null);
  }

  /**
   * Builds a successful step event carrying additional context.
   *
   * @param step pipeline step id
   * @param message context such as the access key or the SRI receipt status
   * @return step event with {@code ok} status and a message
   */
  public static SdkEvent step(String step, String message) {
    return new SdkEvent("step", step, "ok", message, null);
  }

  /**
   * Builds an XML payload event.
   *
   * @param kind {@code raw} for the unsigned XML or {@code signed} for the signed one
   * @param xml XML content
   * @return xml event
   */
  public static SdkEvent xml(String kind, String xml) {
    return new SdkEvent("xml", null, null, kind, xml);
  }

  /**
   * Builds the terminal success event.
   *
   * @param payload final pipeline result
   * @return done event
   */
  public static SdkEvent done(Object payload) {
    return new SdkEvent("done", null, "ok", null, payload);
  }

  /**
   * Builds the terminal error event.
   *
   * @param message human-readable failure reason
   * @return error event
   */
  public static SdkEvent error(String message) {
    return new SdkEvent("error", null, "error", message, null);
  }

  /**
   * Reports whether this event terminates the stream.
   *
   * @return {@code true} for {@code done} and {@code error} events
   */
  public boolean isTerminal() {
    return "done".equals(type) || "error".equals(type);
  }
}
