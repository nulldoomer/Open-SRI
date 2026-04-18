// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.serializers;

import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.common.IssuerProfile;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.infrastructure.models.FacturaXML;
import io.github.opensri.shared.exceptions.XmlSerializationException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * Serializes invoices into SRI-compliant XML using JAXB.
 *
 * <p>This infrastructure component transforms an {@link Invoice} plus its serialization context
 * into the final XML payload expected by the SRI. The serializer receives the generated access key,
 * target environment, document version, and issuer profile so the XML model can be completed
 * before marshalling.
 *
 * <p>It implements {@link XmlSerializer}{@code <Invoice>}.
 */
class InvoiceXmlSerializer implements XmlSerializer<Invoice> {

  /**
   * Produces the XML representation of the given invoice and serialization context.
   *
   * <p>The serializer maps the domain invoice to {@link FacturaXML}, applies the provided
   * access key, environment, version, and issuer profile, and then marshals the result
   * as UTF-8 XML through JAXB.
   *
   * @param invoice domain invoice ready to be serialized
   * @param accessKey generated access key to embed in the XML
   * @param environment target SRI environment to encode in the payload
   * @param version XML schema version to write in the root element
   * @param issuerProfile issuer profile used to complete fiscal fields
   * @return serialized XML document
   */
  @Override
  public String serialize(
          Invoice invoice, String accessKey, Environment environment,
          DocumentVersion version, IssuerProfile issuerProfile) {

    try {

      JAXBContext context = JAXBContext.newInstance(FacturaXML.class);

      Marshaller marshaller = context.createMarshaller();

      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

      FacturaXML facturaDto = FacturaXML.fromDomain(invoice, accessKey,
              environment, version, issuerProfile);

      StringWriter writer = new StringWriter();
      marshaller.marshal(facturaDto, writer);

      return writer.toString();
    } catch (JAXBException exception) {

      throw new XmlSerializationException("Error serializando la factura del SRI", exception);
    }
  }
}
