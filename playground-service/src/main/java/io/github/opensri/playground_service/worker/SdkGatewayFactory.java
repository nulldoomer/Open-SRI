// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.worker;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Resolves the {@link SdkGateway} for a requested language.
 *
 * <p>All registered gateways are injected and indexed by their {@link SdkGateway#language()}, so
 * new languages become available simply by adding a Spring-managed implementation.
 */
@Component
public class SdkGatewayFactory {

  private final Map<String, SdkGateway> gatewaysByLanguage;

  /**
   * Indexes the available gateways by language.
   *
   * @param gateways all gateway implementations on the classpath
   */
  public SdkGatewayFactory(List<SdkGateway> gateways) {
    this.gatewaysByLanguage =
        gateways.stream().collect(Collectors.toMap(SdkGateway::language, Function.identity()));
  }

  /**
   * Reports whether a gateway exists for the language.
   *
   * @param language the language id
   * @return {@code true} if supported
   */
  public boolean supports(String language) {
    return language != null && gatewaysByLanguage.containsKey(language);
  }

  /**
   * Returns the gateway for the language.
   *
   * @param language the language id
   * @return the matching gateway
   * @throws IllegalArgumentException if the language is not supported
   */
  public SdkGateway get(String language) {
    SdkGateway gateway = gatewaysByLanguage.get(language);
    if (gateway == null) {
      throw new IllegalArgumentException("Unsupported SDK language: " + language);
    }
    return gateway;
  }
}
