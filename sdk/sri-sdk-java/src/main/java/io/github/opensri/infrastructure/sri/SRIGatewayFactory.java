// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri;

import static io.github.opensri.domain.enums.Environment.PRODUCCION;
import static io.github.opensri.domain.enums.Environment.PRUEBAS;

import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.infrastructure.sri.client.AuthorizationSoapClient;
import io.github.opensri.infrastructure.sri.client.SendReceiptSoapClient;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

public final class SRIGatewayFactory {
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
