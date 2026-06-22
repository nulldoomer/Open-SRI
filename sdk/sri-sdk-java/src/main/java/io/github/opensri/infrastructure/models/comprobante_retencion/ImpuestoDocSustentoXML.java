// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models.comprobante_retencion;

import io.github.opensri.domain.entities.withholding.SupportDocumentTax;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/** JAXB model for an {@code impuestoDocSustento} entry in a withholding receipt (version 2.0.0). */
@XmlAccessorType(XmlAccessType.FIELD)
public class ImpuestoDocSustentoXML {

  @XmlElement(name = "codImpuestoDocSustento")
  private String codImpuestoDocSustento;

  @XmlElement(name = "codigoPorcentaje")
  private String codigoPorcentaje;

  @XmlElement(name = "baseImponible")
  private String baseImponible;

  @XmlElement(name = "tarifa")
  private String tarifa;

  @XmlElement(name = "valorImpuesto")
  private String valorImpuesto;

  /** Required no-arg constructor for JAXB deserialization. */
  public ImpuestoDocSustentoXML() {}

  /**
   * Creates the XML model for a support-document tax.
   *
   * @param tax domain support-document tax to transform
   * @return JAXB-ready XML model
   */
  public static ImpuestoDocSustentoXML fromDomain(SupportDocumentTax tax) {
    ImpuestoDocSustentoXML xml = new ImpuestoDocSustentoXML();

    xml.codImpuestoDocSustento = tax.code();
    xml.codigoPorcentaje = tax.rateCode();
    xml.baseImponible = tax.taxableBase().toPlainString();
    xml.tarifa = tax.rate().toPlainString();
    xml.valorImpuesto = tax.value().toPlainString();

    return xml;
  }
}
