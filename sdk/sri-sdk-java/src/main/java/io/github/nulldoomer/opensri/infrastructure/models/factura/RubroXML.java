// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.models.factura;

import io.github.nulldoomer.opensri.domain.entities.invoice.OtherThirdCategory;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * JAXB model for a third-party charge entry included in the invoice.
 *
 * <p>Applies to schema versions 2.0.0 and 2.1.0, where the SRI allows the issuer to reflect amounts
 * collected on behalf of third parties within the same electronic receipt.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RubroXML {

  @XmlElement(name = "concepto")
  private String concepto;

  @XmlElement(name = "total")
  private String total;

  /** Required no-arg constructor for JAXB deserialization. */
  public RubroXML() {}

  /**
   * Creates the XML model for a third-party charge.
   *
   * @param rubro domain third-party charge to transform
   * @return JAXB-ready XML rubro model
   */
  public static RubroXML fromDomain(OtherThirdCategory rubro) {
    RubroXML xml = new RubroXML();

    xml.concepto = rubro.concepto();
    xml.total = rubro.total().toPlainString();

    return xml;
  }
}
