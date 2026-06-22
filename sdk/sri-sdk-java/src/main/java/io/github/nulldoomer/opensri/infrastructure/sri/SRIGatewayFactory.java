// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.sri;

import io.github.nulldoomer.opensri.application.ports.SRIGateway;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.infrastructure.sri.client.AuthorizationSoapClient;
import io.github.nulldoomer.opensri.infrastructure.sri.client.SendReceiptSoapClient;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

/** Factory for creating SRIGateway implementations configured for the target environment. */
public final class SRIGatewayFactory {
  /** Private constructor to prevent instantiation. */
  private SRIGatewayFactory() {}

  /**
   * Creates a configured SRIGateway.
   *
   * @param environment target environment (PRUEBAS or PRODUCCION)
   * @param timeout connection timeout in seconds
   * @return a configured SRIGateway instance
   */
  public static SRIGateway create(Environment environment, int timeout) {
    HttpClient httpClient =
        HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(timeout)).build();

    return new SRISOAPGateway(
        new SendReceiptSoapClient(httpClient, receiptEndpoint(environment)),
        new AuthorizationSoapClient(httpClient, authorizationEndpoint(environment)));
  }

  private static URI receiptEndpoint(Environment environment) {
    return switch (environment) {
      case PRUEBAS -> SRIEndpoints.SRI_PRUEBAS_RECEPCION_COMPROBANTES_WSDL;
      case PRODUCCION -> SRIEndpoints.SRI_PRODUCCION_RECEPCION_COMPROBANTES_WSDL;
    };
  }

  private static URI authorizationEndpoint(Environment environment) {
    return switch (environment) {
      case PRUEBAS -> SRIEndpoints.SRI_PRUEBAS_AUTORIZACION_COMPROBANTES_WSDL;
      case PRODUCCION -> SRIEndpoints.SRI_PRODUCCION_AUTORIZACION_COMPROBANTES_WSDL;
    };
  }
}
