// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri;

import java.net.URI;

/**
 * Centraliza los endpoints SOAP oficiales del SRI para recepción y autorización.
 *
 * <p>Estas constantes permiten configurar clientes HTTP sin repetir direcciones del servicio en
 * varios puntos del SDK y distinguen explícitamente entre los ambientes de pruebas y producción.
 *
 * <p>Aunque los nombres conservan la referencia histórica al WSDL, las URIs se usan como
 * destinations directos de los envelopes SOAP enviados por {@code infrastructure.sri.client}.
 */
public class SRIEndpoints {

  /** Private constructor to prevent instantiation of this constants holder. */
  private SRIEndpoints() {}

  /**
   * URL <a
   * href="https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl">https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl</a>
   */
  public static final URI SRI_PRUEBAS_RECEPCION_COMPROBANTES_WSDL =
      createURI(
          "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl");

  /**
   * URL <a
   * href="https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl">https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl</a>
   */
  public static final URI SRI_PRUEBAS_AUTORIZACION_COMPROBANTES_WSDL =
      createURI(
          "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl");

  /**
   * URL <a
   * href="https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl">https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl</a>
   */
  public static final URI SRI_PRODUCCION_RECEPCION_COMPROBANTES_WSDL =
      createURI(
          "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl");

  /**
   * URL <a
   * href="https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl">https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl</a>
   */
  public static final URI SRI_PRODUCCION_AUTORIZACION_COMPROBANTES_WSDL =
      createURI(
          "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl");

  /**
   * Convierte una cadena en {@link URI} y falla inmediatamente si la constante no es válida.
   *
   * @param urlString ubicación absoluta del endpoint SOAP del SRI
   * @return instancia {@link URI} lista para usar en los clientes HTTP del SDK
   */
  private static URI createURI(String urlString) {
    return URI.create(urlString);
  }
}
