// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.models.comprobante_retencion;

import io.github.nulldoomer.opensri.domain.entities.withholding.Dividend;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/** JAXB model for a {@code dividendos} entry in a withholding receipt (version 2.0.0). */
@XmlAccessorType(XmlAccessType.FIELD)
public class DividendosXML {

  @XmlElement(name = "fechaPagoDiv")
  private String fechaPagoDiv;

  @XmlElement(name = "imRentaSoc")
  private String imRentaSoc;

  @XmlElement(name = "ejerFisUtDiv")
  private String ejerFisUtDiv;

  /** Required no-arg constructor for JAXB deserialization. */
  public DividendosXML() {}

  /**
   * Creates the XML model for a dividend entry.
   *
   * @param dividend domain dividend to transform
   * @return JAXB-ready XML model
   */
  public static DividendosXML fromDomain(Dividend dividend) {
    DividendosXML xml = new DividendosXML();

    xml.fechaPagoDiv = dividend.paymentDate();
    xml.imRentaSoc = dividend.corporateIncomeTax().toPlainString();
    xml.ejerFisUtDiv = dividend.fiscalYear();

    return xml;
  }
}
