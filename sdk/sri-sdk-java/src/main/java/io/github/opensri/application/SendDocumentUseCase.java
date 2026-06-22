// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.application;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.common.ElectronicDocument;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.responses.ReceiptResponse;
import io.github.opensri.domain.entities.responses.SendDocumentResult;
import io.github.opensri.domain.enums.Environment;

/**
 * Caso de uso genérico para emitir cualquier comprobante electrónico del SRI.
 *
 * <p>El flujo es idéntico para todos los tipos de documento: se genera la clave de acceso a partir
 * de la fecha de emisión, la numeración fiscal y la información tributaria; se serializa el
 * documento a XML; se firma; y se envía al servicio de recepción del SRI. El serializador concreto
 * se inyecta por invocación para mantener la seguridad de tipos sin acoplar el caso de uso a un
 * documento específico.
 */
class SendDocumentUseCase {

  private final AccessKeyGenerator accessKeyGenerator;
  private final DocumentSigner documentSigner;
  private final SRIGateway sriGateway;

  SendDocumentUseCase(
      AccessKeyGenerator accessKeyGenerator, DocumentSigner documentSigner, SRIGateway sriGateway) {

    this.accessKeyGenerator = accessKeyGenerator;
    this.documentSigner = documentSigner;
    this.sriGateway = sriGateway;
  }

  //  La misma clave de acceso se reutiliza para solicitar la autorización al SRI.
  //  Importante: no recuperar la clave parseando el XML; debe permanecer como un
  //  valor explícito del flujo de la aplicación.

  /**
   * Orquesta la preparación y el envío de un comprobante electrónico.
   *
   * @param document comprobante de dominio a emitir
   * @param serializer serializador específico del tipo de documento
   * @param environment ambiente de destino (PRUEBAS/PRODUCCION)
   * @param issuerProfile perfil tributario del emisor
   * @param <T> tipo concreto de comprobante electrónico
   * @return resultado combinado de la generación y el envío
   */
  <T extends ElectronicDocument> SendDocumentResult execute(
      T document,
      XmlSerializer<T> serializer,
      Environment environment,
      IssuerProfile issuerProfile) {

    String accessKey =
        accessKeyGenerator.generate(
            document.issueDate(), document.documentNumber(), document.taxInfo(), environment);

    String unsignedXml = serializer.serialize(document, accessKey, environment, issuerProfile);

    String signedXml = documentSigner.signDocument(unsignedXml);

    ReceiptResponse response = sriGateway.sendDocument(signedXml);

    return new SendDocumentResult(accessKey, signedXml, response);
  }
}
