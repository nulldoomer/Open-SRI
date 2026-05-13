// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Envía comprobantes firmados al servicio SOAP de recepción del SRI usando {@link HttpClient}.
 *
 * <p>Este cliente construye manualmente el envelope SOAP de {@code validarComprobante} para evitar
 * dependencias con DTOs generados desde WSDL y devolver el XML crudo de la respuesta al mapper de
 * infraestructura.
 *
 * <p>La serialización del comprobante se envía en Base64, tal como lo exige la operación de
 * recepción del SRI.
 */
public final class SendReceiptSoapClient {
  private final HttpClient httpClient;
  private final URI endpoint;

  /**
   * Crea un cliente de recepción apuntando a un endpoint específico del SRI.
   *
   * @param httpClient cliente HTTP reutilizable para ejecutar la llamada SOAP
   * @param endpoint ubicación del servicio de recepción en el ambiente deseado
   */
  public SendReceiptSoapClient(HttpClient httpClient, URI endpoint) {
    this.httpClient = httpClient;
    this.endpoint = endpoint;
  }

  /**
   * Envía un comprobante firmado al servicio de recepción del SRI.
   *
   * @param signedXml comprobante electrónico firmado en formato XML
   * @return envelope SOAP devuelto por el SRI como cadena UTF-8
   * @throws IOException si la llamada HTTP no puede completarse o la respuesta no puede leerse
   * @throws InterruptedException si el hilo actual es interrumpido durante la espera de la
   *     respuesta
   */
  public String sendReceipt(String signedXml) throws IOException, InterruptedException {
    String soapRequest = buildSoapRequest(signedXml);

    HttpRequest request =
        HttpRequest.newBuilder(endpoint)
            .header("Content-Type", "text/xml; charset=UTF-8")
            .POST(HttpRequest.BodyPublishers.ofString(soapRequest, StandardCharsets.UTF_8))
            .build();

    HttpResponse<String> response =
        httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

    return response.body();
  }

  /**
   * Construye el envelope SOAP de recepción esperado por el SRI.
   *
   * @param signedXml comprobante firmado que será codificado en Base64
   * @return request SOAP listo para ser enviado por HTTP
   */
  private String buildSoapRequest(String signedXml) {
    String base64Xml =
        Base64.getEncoder().encodeToString(signedXml.getBytes(StandardCharsets.UTF_8));
    return """
             <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                xmlns:ec="http://ec.gob.sri.ws.recepcion">
                <soapenv:Header/>
                <soapenv:Body>
                  <ec:validarComprobante>
                    <xml>%s</xml>
                  </ec:validarComprobante>
                </soapenv:Body>
              </soapenv:Envelope>
                """
        .formatted(base64Xml);
  }
}
