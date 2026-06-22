// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models.comprobante_retencion;

import io.github.opensri.domain.entities.withholding.BananaBoxPurchase;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/** JAXB model for a {@code compraCajBanano} entry in a withholding receipt (version 2.0.0). */
@XmlAccessorType(XmlAccessType.FIELD)
public class CompraCajBananoXML {

  @XmlElement(name = "numCajBan")
  private String numCajBan;

  @XmlElement(name = "precCajBan")
  private String precCajBan;

  /** Required no-arg constructor for JAXB deserialization. */
  public CompraCajBananoXML() {}

  /**
   * Creates the XML model for a banana-box purchase entry.
   *
   * @param purchase domain banana-box purchase to transform
   * @return JAXB-ready XML model
   */
  public static CompraCajBananoXML fromDomain(BananaBoxPurchase purchase) {
    CompraCajBananoXML xml = new CompraCajBananoXML();

    xml.numCajBan = String.valueOf(purchase.boxCount());
    xml.precCajBan = purchase.boxPrice().toPlainString();

    return xml;
  }
}
