// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.serializers;

import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.infrastructure.models.FacturaXML;
import io.github.opensri.shared.exceptions.XmlSerializationException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * Implementación de {@link XmlSerializer} que serializa facturas a formato XML compatible con el
 * SRI.
 *
 * <p>Este componente de infraestructura transforma una {@link Invoice} y su contexto de
 * serialización en el payload XML final esperado por el SRI, utilizando JAXB para el marshalling de
 * los datos.
 */
class InvoiceXmlSerializer implements XmlSerializer<Invoice> {

  /**
   * Produce la representación XML de la factura y su contexto de serialización.
   *
   * <p>Mapea la factura de dominio a {@link FacturaXML}, aplica la clave de acceso, el ambiente y el
   * perfil del emisor proporcionados, y genera el XML resultante en codificación UTF-8.
   *
   * @param invoice factura de dominio lista para ser serializada
   * @param accessKey clave de acceso generada para incluir en el XML
   * @param environment ambiente del SRI al que se dirige el comprobante
   * @param issuerProfile perfil del emisor para completar campos fiscales
   * @return documento XML serializado
   */
  @Override
  public String serialize(
      Invoice invoice, String accessKey, Environment environment, IssuerProfile issuerProfile) {

    try {

      JAXBContext context = JAXBContext.newInstance(FacturaXML.class);

      Marshaller marshaller = context.createMarshaller();

      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

      FacturaXML facturaDto = FacturaXML.fromDomain(invoice, accessKey, environment, issuerProfile);

      StringWriter writer = new StringWriter();
      marshaller.marshal(facturaDto, writer);

      return writer.toString();
    } catch (JAXBException exception) {

      throw new XmlSerializationException("Error serializando la factura del SRI", exception);
    }
  }
}
