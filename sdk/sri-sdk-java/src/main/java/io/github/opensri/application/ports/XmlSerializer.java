// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.application.ports;

import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.enums.Environment;

/**
 * Serializa un documento de dominio en su representación XML.
 *
 * <p>Este puerto abstrae la transformación de un modelo de documento a XML sin exponer JAXB ni
 * cualquier otra tecnología de serialización a la capa de aplicación. Se espera que las
 * implementaciones produzcan un XML que coincida con el esquema y la versión del SRI
 * correspondientes para el entorno seleccionado.
 *
 * @param <T> tipo de documento aceptado por el serializador
 */
public interface XmlSerializer<T> {

  /**
   * Convierte el documento proporcionado en XML.
   *
   * @param document instancia del documento a serializar
   * @param accessKey clave de acceso generada necesaria en la serialización
   * @param environment entorno utilizado para establecer en el XML
   * @param issuerProfile información del perfil del emisor necesaria para completar el XML
   * @return representación XML del documento dado
   */
  String serialize(
      T document, String accessKey, Environment environment, IssuerProfile issuerProfile);
}
