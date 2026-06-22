// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.serializers.purchasesettlement;

import io.github.nulldoomer.opensri.application.ports.XmlSerializer;
import io.github.nulldoomer.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.nulldoomer.opensri.domain.entities.purchasesettlement.PurchaseSettlement;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.infrastructure.models.liquidaction_compra.LiquidacionCompraXML;
import io.github.nulldoomer.opensri.shared.exceptions.XmlSerializationException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * Implementación de {@link XmlSerializer} que serializa liquidaciones de compra a XML compatible
 * con el SRI.
 *
 * <p>Transforma una {@link PurchaseSettlement} de dominio y su contexto de serialización en el
 * payload XML final esperado por el SRI, utilizando JAXB para el marshalling de los datos.
 */
class PurchaseSettlementXmlSerializer implements XmlSerializer<PurchaseSettlement> {

  /**
   * Produce la representación XML de la liquidación de compra y su contexto de serialización.
   *
   * @param settlement liquidación de compra de dominio lista para ser serializada
   * @param accessKey clave de acceso generada para incluir en el XML
   * @param environment ambiente del SRI al que se dirige el comprobante
   * @param issuerProfile perfil del emisor para completar campos fiscales
   * @return documento XML serializado
   */
  @Override
  public String serialize(
      PurchaseSettlement settlement,
      String accessKey,
      Environment environment,
      IssuerProfile issuerProfile) {

    try {

      JAXBContext context = JAXBContext.newInstance(LiquidacionCompraXML.class);

      Marshaller marshaller = context.createMarshaller();

      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

      LiquidacionCompraXML dto =
          LiquidacionCompraXML.fromDomain(settlement, accessKey, environment, issuerProfile);

      StringWriter writer = new StringWriter();
      marshaller.marshal(dto, writer);

      return writer.toString();
    } catch (JAXBException exception) {

      throw new XmlSerializationException(
          "Error serializando la liquidación de compra del SRI", exception);
    }
  }
}
