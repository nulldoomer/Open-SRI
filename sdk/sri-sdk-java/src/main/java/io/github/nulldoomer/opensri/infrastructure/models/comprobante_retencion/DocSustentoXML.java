// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.models.comprobante_retencion;

import io.github.nulldoomer.opensri.domain.entities.withholding.SupportDocument;
import io.github.nulldoomer.opensri.infrastructure.models.common.PagosXML;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * JAXB model for a {@code docSustento} entry in a withholding receipt (version 2.0.0).
 *
 * <p>Maps the supporting-document reference, totals, document taxes, withholdings and payments,
 * following the element order required by the SRI schema. Optional reimbursement sections and
 * optional flags are omitted.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DocSustentoXML {

  @XmlElement(name = "codSustento")
  private String codSustento;

  @XmlElement(name = "codDocSustento")
  private String codDocSustento;

  @XmlElement(name = "numDocSustento")
  private String numDocSustento;

  @XmlElement(name = "fechaEmisionDocSustento")
  private String fechaEmisionDocSustento;

  @XmlElement(name = "pagoLocExt")
  private String pagoLocExt;

  @XmlElement(name = "totalSinImpuestos")
  private String totalSinImpuestos;

  @XmlElement(name = "importeTotal")
  private String importeTotal;

  @XmlElementWrapper(name = "impuestosDocSustento")
  @XmlElement(name = "impuestoDocSustento")
  private List<ImpuestoDocSustentoXML> impuestosDocSustento;

  @XmlElementWrapper(name = "retenciones")
  @XmlElement(name = "retencion")
  private List<RetencionDocSustentoXML> retenciones;

  @XmlElementWrapper(name = "pagos")
  @XmlElement(name = "pago")
  private List<PagosXML> pagos;

  /** Required no-arg constructor for JAXB deserialization. */
  public DocSustentoXML() {}

  /**
   * Creates the XML model for a support document.
   *
   * @param doc domain support document to transform
   * @return JAXB-ready XML model
   */
  public static DocSustentoXML fromDomain(SupportDocument doc) {
    DocSustentoXML xml = new DocSustentoXML();

    xml.codSustento = doc.sustentoCode();
    xml.codDocSustento = doc.docCode();
    xml.numDocSustento = doc.docNumber();
    xml.fechaEmisionDocSustento = doc.docIssueDate().format();
    xml.pagoLocExt = doc.paymentLocation();
    xml.totalSinImpuestos = doc.totalWithoutTaxes().toPlainString();
    xml.importeTotal = doc.totalAmount().toPlainString();

    xml.impuestosDocSustento =
        doc.taxes().stream().map(ImpuestoDocSustentoXML::fromDomain).toList();
    xml.retenciones = doc.withholdings().stream().map(RetencionDocSustentoXML::fromDomain).toList();
    xml.pagos = doc.payments().stream().map(PagosXML::fromDomain).toList();

    return xml;
  }
}
