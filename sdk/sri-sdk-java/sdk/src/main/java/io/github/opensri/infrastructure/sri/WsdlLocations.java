// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Agrupa las ubicaciones oficiales de los WSDL del SRI para recepción y autorización.
 *
 * <p>Estas constantes permiten construir proxies SOAP sin hardcodear URLs en distintos puntos del
 * código y distinguen explícitamente entre los ambientes de pruebas y producción.
 */
public class WsdlLocations {

  /**
   * URL <a
   * href="https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl">https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl</a>
   */
  public static final URL SRI_PRUEBAS_RECEPCION_COMPROBANTES_WSDL =
      createURL(
          "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl");

  /**
   * URL <a
   * href="https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl">https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl</a>
   */
  public static final URL SRI_PRUEBAS_AUTORIZACION_COMPROBANTES_WSDL =
      createURL(
          "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl");

  /**
   * URL <a
   * href="https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl">https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl</a>
   */
  public static final URL SRI_PRODUCCION_RECEPCION_COMPROBANTES_WSDL =
      createURL(
          "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl");

  /**
   * URL <a
   * href="https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl">https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl</a>
   */
  public static final URL SRI_PRODUCCION_AUTORIZACION_COMPROBANTES_WSDL =
      createURL(
          "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl");

  /**
   * Convierte una cadena en {@link URL} y falla inmediatamente si la constante no es válida.
   *
   * @param urlString ubicación absoluta del WSDL
   * @return instancia {@link URL} lista para usar en los proxies SOAP
   */
  private static URL createURL(String urlString) {
    try {
      return new URL(urlString);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
