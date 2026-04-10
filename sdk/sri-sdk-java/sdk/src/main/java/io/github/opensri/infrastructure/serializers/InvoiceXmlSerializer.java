// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.serializers;

import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.common.IssuerProfile;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.infrastructure.models.FacturaXML;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.StringWriter;

/**
 * Serializes invoice XML models for a specific SRI environment.
 *
 * <p>This infrastructure component is responsible for turning the intermediate {@link Invoice}
 * representation into the final XML payload expected by the SRI.
 *
 * <p>It implements {@link XmlSerializer}{@code <Invoice>}.
 */
class InvoiceXmlSerializer implements XmlSerializer<Invoice> {

  /**
   * Produces the XML representation of the given invoice model.
   *
   * <p>The serializer is expected to transform the JAXB-compatible invoice structure into the final
   * XML string required by the SRI invoice schema.
   *
   * @param invoice invoice XML model ready to be marshall-ed
   * @return serialized XML document
   */
  @Override
  public String serialize(Invoice invoice, String accessKey,
                          Environment environment,
                          IssuerProfile issuerProfile) {

    try{

      JAXBContext context = JAXBContext.newInstance(FacturaXML.class);

      Marshaller marshaller = context.createMarshaller();

      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

      FacturaXML facturaDto = FacturaXML.fromDomain(invoice, accessKey,
              environment, issuerProfile);


      StringWriter writer = new StringWriter();
      marshaller.marshal(facturaDto,writer);

      return writer.toString();
    }catch (JAXBException exception){

      throw new RuntimeException("Error serializando la factura del SRI",exception);
    }

  }
}
