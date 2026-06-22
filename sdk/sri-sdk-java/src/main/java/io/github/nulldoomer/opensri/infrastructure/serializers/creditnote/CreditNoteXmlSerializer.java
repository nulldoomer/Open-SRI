// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.serializers.creditnote;

import io.github.nulldoomer.opensri.application.ports.XmlSerializer;
import io.github.nulldoomer.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.nulldoomer.opensri.domain.entities.creditnote.CreditNote;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.infrastructure.models.nota_credito.NotaCreditoXML;
import io.github.nulldoomer.opensri.shared.exceptions.XmlSerializationException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * Implementación de {@link XmlSerializer} que serializa notas de crédito a XML compatible con el
 * SRI.
 *
 * <p>Transforma una {@link CreditNote} de dominio y su contexto de serialización en el payload XML
 * final esperado por el SRI, utilizando JAXB para el marshalling de los datos.
 */
class CreditNoteXmlSerializer implements XmlSerializer<CreditNote> {

  /**
   * Produce la representación XML de la nota de crédito y su contexto de serialización.
   *
   * @param note nota de crédito de dominio lista para ser serializada
   * @param accessKey clave de acceso generada para incluir en el XML
   * @param environment ambiente del SRI al que se dirige el comprobante
   * @param issuerProfile perfil del emisor para completar campos fiscales
   * @return documento XML serializado
   */
  @Override
  public String serialize(
      CreditNote note, String accessKey, Environment environment, IssuerProfile issuerProfile) {

    try {

      JAXBContext context = JAXBContext.newInstance(NotaCreditoXML.class);

      Marshaller marshaller = context.createMarshaller();

      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

      NotaCreditoXML dto = NotaCreditoXML.fromDomain(note, accessKey, environment, issuerProfile);

      StringWriter writer = new StringWriter();
      marshaller.marshal(dto, writer);

      return writer.toString();
    } catch (JAXBException exception) {

      throw new XmlSerializationException(
          "Error serializando la nota de crédito del SRI", exception);
    }
  }
}
