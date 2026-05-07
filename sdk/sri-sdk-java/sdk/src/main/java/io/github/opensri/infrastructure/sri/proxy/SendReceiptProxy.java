// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri.proxy;

import ec.sri.recepcion.RecepcionComprobantesOffline;
import ec.sri.recepcion.RecepcionComprobantesOfflineService;
import ec.sri.recepcion.RespuestaSolicitud;
import jakarta.xml.ws.BindingProvider;
import java.net.URL;
import java.util.Map;
import java.util.function.Consumer;
import javax.xml.namespace.QName;

/**
 * Encapsula el cliente SOAP de recepción del SRI generado desde el WSDL.
 *
 * <p>Centraliza la creación del puerto JAX-WS y ofrece un punto único para ajustar el request
 * context antes de invocar {@code validarComprobante}.
 */
public class SendReceiptProxy {
  private final RecepcionComprobantesOffline port;

  /**
   * Crea el proxy usando la configuración por defecto del cliente SOAP.
   *
   * @param wsdlLocation ubicación del WSDL de recepción
   */
  public SendReceiptProxy(URL wsdlLocation) {
    this(wsdlLocation, null);
  }

  /**
   * Crea el proxy y permite personalizar el contexto de la petición SOAP antes del primer envío.
   *
   * @param wsdLocation ubicación del WSDL de recepción
   * @param requestContextConfig callback opcional para ajustar propiedades del request context
   */
  public SendReceiptProxy(URL wsdLocation, Consumer<Map<String, Object>> requestContextConfig) {
    QName qname =
        new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
    RecepcionComprobantesOfflineService service =
        new RecepcionComprobantesOfflineService(wsdLocation, qname);

    port = service.getRecepcionComprobantesOfflinePort();
    Map<String, Object> requestConfig = ((BindingProvider) port).getRequestContext();

    if (requestConfig != null && requestContextConfig != null) {
      requestContextConfig.accept(requestConfig);
    }
  }

  /**
   * Envía al SRI el contenido XML ya codificado como arreglo de bytes.
   *
   * @param bytesFile XML firmado codificado en UTF-8
   * @return respuesta SOAP directa del servicio de recepción
   */
  public RespuestaSolicitud sendReceipt(byte[] bytesFile) {
    return port.validarComprobante(bytesFile);
  }
}
