// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.serializers.debitnote;

import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.debitnote.DebitNote;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.infrastructure.models.nota_debito.NotaDebitoXML;
import io.github.opensri.shared.exceptions.XmlSerializationException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * Implementación de {@link XmlSerializer} que serializa notas de débito a XML compatible con el
 * SRI.
 *
 * <p>Transforma una {@link DebitNote} de dominio y su contexto de serialización en el payload XML
 * final esperado por el SRI, utilizando JAXB para el marshalling de los datos.
 */
class DebitNoteXmlSerializer implements XmlSerializer<DebitNote> {

  /**
   * Produce la representación XML de la nota de débito y su contexto de serialización.
   *
   * @param note nota de débito de dominio lista para ser serializada
   * @param accessKey clave de acceso generada para incluir en el XML
   * @param environment ambiente del SRI al que se dirige el comprobante
   * @param issuerProfile perfil del emisor para completar campos fiscales
   * @return documento XML serializado
   */
  @Override
  public String serialize(
      DebitNote note, String accessKey, Environment environment, IssuerProfile issuerProfile) {

    try {

      JAXBContext context = JAXBContext.newInstance(NotaDebitoXML.class);

      Marshaller marshaller = context.createMarshaller();

      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

      NotaDebitoXML dto = NotaDebitoXML.fromDomain(note, accessKey, environment, issuerProfile);

      StringWriter writer = new StringWriter();
      marshaller.marshal(dto, writer);

      return writer.toString();
    } catch (JAXBException exception) {

      throw new XmlSerializationException(
          "Error serializando la nota de débito del SRI", exception);
    }
  }
}
