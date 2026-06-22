// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models.liquidaction_compra;

import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.purchasesettlement.PurchaseSettlement;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.infrastructure.models.common.InfoAdicionalXML;
import io.github.opensri.infrastructure.models.common.InfoTributariaXML;
import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * Root JAXB model for the SRI purchase-settlement XML document.
 *
 * <p>Assembles the {@code liquidacionCompra} structure: tax information, the settlement info block,
 * the detail lines, and optional additional fields. Reuses the shared {@link InfoTributariaXML} and
 * {@link InfoAdicionalXML} models.
 */
@XmlRootElement(name = "liquidacionCompra")
@XmlAccessorType(XmlAccessType.FIELD)
public class LiquidacionCompraXML {

  @XmlAttribute private String id = "comprobante";

  @XmlAttribute private String version;

  @XmlElement(name = "infoTributaria")
  private InfoTributariaXML infoTributaria;

  @XmlElement(name = "infoLiquidacionCompra")
  private InfoLiquidacionCompraXML infoLiquidacionCompra;

  @XmlElementWrapper(name = "detalles")
  @XmlElement(name = "detalle")
  private List<DetalleLiquidacionXML> detalles;

  @XmlElementWrapper(name = "infoAdicional")
  @XmlElement(name = "campoAdicional")
  private List<InfoAdicionalXML> infoAdicional;

  /** Required no-arg constructor for JAXB deserialization. */
  public LiquidacionCompraXML() {}

  /**
   * Creates the complete XML model for a purchase settlement.
   *
   * @param settlement purchase-settlement domain object to transform
   * @param accessKey access key previously generated for the document
   * @param env target SRI environment of the XML
   * @param profile issuer profile used to complete the fiscal sections
   * @return root JAXB model representing the purchase-settlement XML document
   */
  public static LiquidacionCompraXML fromDomain(
      PurchaseSettlement settlement, String accessKey, Environment env, IssuerProfile profile) {
    LiquidacionCompraXML xml = new LiquidacionCompraXML();

    xml.version = settlement.documentVersion().getVersion();

    xml.infoTributaria =
        InfoTributariaXML.fromDomain(
            settlement.taxInfo(), settlement.documentNumber(), accessKey, env);

    xml.infoLiquidacionCompra = InfoLiquidacionCompraXML.fromDomain(settlement, profile);

    xml.detalles = settlement.items().stream().map(DetalleLiquidacionXML::fromDomain).toList();

    if (!settlement.additionalInfo().isEmpty()) {
      xml.infoAdicional =
          settlement.additionalInfo().stream().map(InfoAdicionalXML::fromDomain).toList();
    }

    return xml;
  }
}
