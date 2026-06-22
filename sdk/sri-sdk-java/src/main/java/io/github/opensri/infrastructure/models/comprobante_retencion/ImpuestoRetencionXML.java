// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models.comprobante_retencion;

import io.github.opensri.domain.entities.withholding.WithholdingTax;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * JAXB model for an {@code impuesto} entry in a withholding receipt (version 1.0.0).
 *
 * <p>Maps the withheld tax data and the optional supporting-document reference following the
 * element order required by the SRI schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ImpuestoRetencionXML {

  @XmlElement(name = "codigo")
  private String codigo;

  @XmlElement(name = "codigoRetencion")
  private String codigoRetencion;

  @XmlElement(name = "baseImponible")
  private String baseImponible;

  @XmlElement(name = "porcentajeRetener")
  private String porcentajeRetener;

  @XmlElement(name = "valorRetenido")
  private String valorRetenido;

  @XmlElement(name = "codDocSustento")
  private String codDocSustento;

  @XmlElement(name = "numDocSustento")
  private String numDocSustento;

  @XmlElement(name = "fechaEmisionDocSustento")
  private String fechaEmisionDocSustento;

  /** Required no-arg constructor for JAXB deserialization. */
  public ImpuestoRetencionXML() {}

  /**
   * Creates the XML model for a withholding entry.
   *
   * @param withholding domain withholding to transform
   * @return JAXB-ready XML withholding model
   */
  public static ImpuestoRetencionXML fromDomain(WithholdingTax withholding) {
    ImpuestoRetencionXML xml = new ImpuestoRetencionXML();

    xml.codigo = withholding.code();
    xml.codigoRetencion = withholding.withholdingCode();
    xml.baseImponible = withholding.taxableBase().toPlainString();
    xml.porcentajeRetener = withholding.withholdingPercentage().toPlainString();
    xml.valorRetenido = withholding.withheldValue().toPlainString();
    xml.codDocSustento = withholding.supportDocCode();
    xml.numDocSustento = withholding.supportDocNumber();
    if (withholding.supportDocIssueDate() != null) {
      xml.fechaEmisionDocSustento = withholding.supportDocIssueDate().format();
    }

    return xml;
  }
}
