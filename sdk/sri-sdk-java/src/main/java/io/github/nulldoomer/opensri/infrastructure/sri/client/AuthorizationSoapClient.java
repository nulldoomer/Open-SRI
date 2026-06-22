// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.sri.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Cliente especializado en la consulta del servicio SOAP de autorización del SRI.
 *
 * <p>Esta clase construye manualmente la petición SOAP para la operación {@code
 * autorizacionComprobante} y procesa la respuesta HTTP, devolviendo el XML crudo para su posterior
 * mapeo al dominio.
 */
public final class AuthorizationSoapClient {

  private final HttpClient httpClient;
  private final URI endpoint;

  /**
   * Crea un cliente de autorización apuntando a un endpoint específico del SRI.
   *
   * @param httpClient cliente HTTP reutilizable para ejecutar la llamada SOAP
   * @param endpoint ubicación del servicio de autorización en el ambiente deseado
   */
  public AuthorizationSoapClient(HttpClient httpClient, URI endpoint) {
    this.httpClient = httpClient;
    this.endpoint = endpoint;
  }

  /**
   * Consulta la autorización asociada a una clave de acceso.
   *
   * @param accessKey clave de acceso del comprobante electrónico
   * @return envelope SOAP de autorización devuelto por el SRI
   * @throws IOException si la llamada HTTP falla o la respuesta no puede leerse
   * @throws InterruptedException si el hilo actual es interrumpido mientras espera la respuesta
   */
  public String authorizeDocument(String accessKey) throws IOException, InterruptedException {
    String soapRequest = buildSoapRequest(accessKey);

    HttpRequest request =
        HttpRequest.newBuilder(endpoint)
            .header("Content-Type", "text/xml; CHARSET=UTF-8")
            .POST(HttpRequest.BodyPublishers.ofString(soapRequest, StandardCharsets.UTF_8))
            .build();

    HttpResponse<String> response =
        httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

    return response.body();
  }

  /**
   * Construye el envelope SOAP de autorización esperado por el SRI.
   *
   * @param accessKey clave de acceso consultada en el servicio
   * @return request SOAP listo para ser enviado por HTTP
   */
  private String buildSoapRequest(String accessKey) {
    return """
      <soapenv:Envelope
          xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
          xmlns:ec="http://ec.gob.sri.ws.autorizacion">
        <soapenv:Header/>
        <soapenv:Body>
          <ec:autorizacionComprobante>
            <claveAccesoComprobante>%s</claveAccesoComprobante>
          </ec:autorizacionComprobante>
        </soapenv:Body>
      </soapenv:Envelope>
      """
        .formatted(accessKey);
  }
}
