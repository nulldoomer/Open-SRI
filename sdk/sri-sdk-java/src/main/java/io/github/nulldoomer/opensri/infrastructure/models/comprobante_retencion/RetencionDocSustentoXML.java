// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.models.comprobante_retencion;

import io.github.nulldoomer.opensri.domain.entities.withholding.WithholdingDetail;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * JAXB model for a {@code retencion} entry inside a {@code docSustento} (version 2.0.0).
 *
 * <p>Optionally includes dividend or banana-box-purchase information.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RetencionDocSustentoXML {

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

  @XmlElement(name = "dividendos")
  private DividendosXML dividendos;

  @XmlElement(name = "compraCajBanano")
  private CompraCajBananoXML compraCajBanano;

  /** Required no-arg constructor for JAXB deserialization. */
  public RetencionDocSustentoXML() {}

  /**
   * Creates the XML model for a withholding detail.
   *
   * @param detail domain withholding detail to transform
   * @return JAXB-ready XML model
   */
  public static RetencionDocSustentoXML fromDomain(WithholdingDetail detail) {
    RetencionDocSustentoXML xml = new RetencionDocSustentoXML();

    xml.codigo = detail.code();
    xml.codigoRetencion = detail.withholdingCode();
    xml.baseImponible = detail.taxableBase().toPlainString();
    xml.porcentajeRetener = detail.percentage().toPlainString();
    xml.valorRetenido = detail.withheldValue().toPlainString();

    if (detail.dividend() != null) {
      xml.dividendos = DividendosXML.fromDomain(detail.dividend());
    }
    if (detail.bananaBox() != null) {
      xml.compraCajBanano = CompraCajBananoXML.fromDomain(detail.bananaBox());
    }

    return xml;
  }
}
