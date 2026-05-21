// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.enums;

/**
 * Representa la versión del esquema XML utilizada al serializar documentos del SRI.
 *
 * <p>Este enum centraliza las cadenas de versión soportadas para los payloads de los documentos
 * electrónicos, permitiendo que constructores y serializadores seleccionen la versión XML apropiada
 * sin codificar valores literales en el SDK.
 */
public enum DocumentVersion {
  /** Versión 1.0.0 del esquema XML. */
  VERSION_100("1.0.0"),
  /** Versión 1.1.0 del esquema XML. */
  VERSION_110("1.1.0"),
  /** Versión 2.0.0 del esquema XML. */
  VERSION_200("2.0.0"),
  /** Versión 2.1.0 del esquema XML. */
  VERSION_210("2.1.0");

  /** Cadena que representa la versión del esquema XML. */
  private String version;

  /**
   * Constructor de la versión del documento.
   *
   * @param version valor de la versión
   */
  DocumentVersion(String version) {
    this.version = version;
  }

  /**
   * Retorna la cadena de versión que debe escribirse en el elemento raíz del XML.
   *
   * @return identificador de la versión del documento, como {@code 1.1.0} o {@code 2.1.0}
   */
  public String getVersion() {
    return version;
  }

  /**
   * Actualiza la cadena de versión subyacente.
   *
   * @param version valor de la versión a asignar a esta entrada del enum
   */
  public void setVersion(String version) {
    this.version = version;
  }
}
