// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.serializers.withholding;

import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.withholding.WithholdingReceipt;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.infrastructure.models.comprobante_retencion.ComprobanteRetencionXML;
import io.github.opensri.shared.exceptions.XmlSerializationException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * Implementación de {@link XmlSerializer} que serializa comprobantes de retención a XML compatible
 * con el SRI.
 *
 * <p>Transforma un {@link WithholdingReceipt} de dominio y su contexto de serialización en el
 * payload XML final esperado por el SRI, utilizando JAXB para el marshalling de los datos.
 */
class WithholdingReceiptXmlSerializer implements XmlSerializer<WithholdingReceipt> {

  /**
   * Produce la representación XML del comprobante de retención y su contexto de serialización.
   *
   * @param receipt comprobante de retención de dominio listo para ser serializado
   * @param accessKey clave de acceso generada para incluir en el XML
   * @param environment ambiente del SRI al que se dirige el comprobante
   * @param issuerProfile perfil del emisor para completar campos fiscales
   * @return documento XML serializado
   */
  @Override
  public String serialize(
      WithholdingReceipt receipt,
      String accessKey,
      Environment environment,
      IssuerProfile issuerProfile) {

    try {

      JAXBContext context = JAXBContext.newInstance(ComprobanteRetencionXML.class);

      Marshaller marshaller = context.createMarshaller();

      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

      ComprobanteRetencionXML dto =
          ComprobanteRetencionXML.fromDomain(receipt, accessKey, environment, issuerProfile);

      StringWriter writer = new StringWriter();
      marshaller.marshal(dto, writer);

      return writer.toString();
    } catch (JAXBException exception) {

      throw new XmlSerializationException(
          "Error serializando el comprobante de retención del SRI", exception);
    }
  }
}
