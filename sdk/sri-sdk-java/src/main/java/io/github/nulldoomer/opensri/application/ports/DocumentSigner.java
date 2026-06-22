// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.application.ports;

/**
 * Firma documentos electrónicos utilizando certificados digitales.
 *
 * <p>Este puerto es responsable de aplicar la firma electrónica (generalmente en formato XAdES-BES)
 * al contenido XML de un documento tributario, asegurando su integridad y validez legal ante el
 * SRI.
 */
public interface DocumentSigner {

  /**
   * Firma el documento XML proporcionado.
   *
   * @param xmlDocument el contenido XML del documento sin firmar
   * @return el contenido XML del documento con la firma electrónica aplicada
   */
  String signDocument(String xmlDocument);
}
