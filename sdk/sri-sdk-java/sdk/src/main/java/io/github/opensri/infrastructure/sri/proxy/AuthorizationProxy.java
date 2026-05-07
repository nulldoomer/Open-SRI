// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri.proxy;

import ec.sri.autorizacion.AutorizacionComprobantesOffline;
import ec.sri.autorizacion.AutorizacionComprobantesOfflineService;
import ec.sri.autorizacion.RespuestaComprobante;
import ec.sri.autorizacion.RespuestaLote;
import jakarta.xml.ws.BindingProvider;
import java.net.URL;
import java.util.Map;
import java.util.function.Consumer;
import javax.xml.namespace.QName;

/**
 * Encapsula el cliente SOAP de autorización del SRI.
 *
 * <p>Inicializa el puerto JAX-WS con timeouts por defecto y expone operaciones específicas para
 * consultar autorizaciones individuales o por lote.
 */
public class AuthorizationProxy {

  private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
  private static final int DEFAULT_REQUEST_TIMEOUT = 10000;

  private final AutorizacionComprobantesOffline port;

  /**
   * Crea el proxy usando la configuración de timeout por defecto.
   *
   * @param wsdlLocation ubicación del WSDL de autorización
   */
  public AuthorizationProxy(URL wsdlLocation) {
    this(wsdlLocation, null);
  }

  /**
   * Crea el proxy y permite extender el request context con configuración adicional.
   *
   * @param wsdlLocation ubicación del WSDL de autorización
   * @param requestContextConfig callback opcional para complementar el request context
   */
  public AuthorizationProxy(URL wsdlLocation, Consumer<Map<String, Object>> requestContextConfig) {
    QName qName =
        new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService");
    AutorizacionComprobantesOfflineService service =
        new AutorizacionComprobantesOfflineService(wsdlLocation, qName);

    port = service.getAutorizacionComprobantesOfflinePort();

    Map<String, Object> requestConfig = ((BindingProvider) port).getRequestContext();
    requestConfig.put("com.sun.xml.internal.ws.connect.timeout", DEFAULT_CONNECT_TIMEOUT);
    requestConfig.put("com.sun.xml.internal.ws.request.timeout", DEFAULT_REQUEST_TIMEOUT);

    if (requestContextConfig != null) {
      requestContextConfig.accept(requestConfig);
    }
  }

  /**
   * Consulta la autorización de un único comprobante.
   *
   * @param accessKey clave de acceso del comprobante
   * @return respuesta SOAP directa del servicio de autorización
   */
  public RespuestaComprobante singleAuthorize(String accessKey) {
    return port.autorizacionComprobante(accessKey);
  }

  /**
   * Consulta la autorización de un lote de comprobantes asociado a una clave de acceso.
   *
   * @param accessKey clave de acceso del lote o comprobante relacionado
   * @return respuesta SOAP del servicio de autorización por lote
   */
  public RespuestaLote batchAuthorize(String accessKey) {
    return port.autorizacionComprobanteLote(accessKey);
  }
}
