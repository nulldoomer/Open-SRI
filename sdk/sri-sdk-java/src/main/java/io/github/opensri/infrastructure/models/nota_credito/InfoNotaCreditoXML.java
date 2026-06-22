// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models.nota_credito;

import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.creditnote.CreditNote;
import io.github.opensri.infrastructure.models.common.CompensacionXML;
import io.github.opensri.infrastructure.models.factura.TotalImpuestoXML;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * JAXB model for the {@code infoNotaCredito} section of the credit-note XML.
 *
 * <p>Maps the buyer data, the reference to the modified document, the totals and the reason for the
 * credit note, following the element order required by the SRI schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoNotaCreditoXML {

  @XmlElement(name = "fechaEmision")
  private String fechaEmision;

  @XmlElement(name = "dirEstablecimiento")
  private String dirEstablecimiento;

  @XmlElement(name = "tipoIdentificacionComprador")
  private String tipoIdentificacionComprador;

  @XmlElement(name = "razonSocialComprador")
  private String razonSocialComprador;

  @XmlElement(name = "identificacionComprador")
  private String identificacionComprador;

  @XmlElement(name = "contribuyenteEspecial")
  private String contribuyenteEspecial;

  @XmlElement(name = "obligadoContabilidad")
  private String obligadoContabilidad;

  @XmlElement(name = "codDocModificado")
  private String codDocModificado;

  @XmlElement(name = "numDocModificado")
  private String numDocModificado;

  @XmlElement(name = "fechaEmisionDocSustento")
  private String fechaEmisionDocSustento;

  @XmlElement(name = "totalSinImpuestos")
  private String totalSinImpuestos;

  @XmlElementWrapper(name = "compensaciones")
  @XmlElement(name = "compensacion")
  private List<CompensacionXML> compensaciones;

  @XmlElement(name = "valorModificacion")
  private String valorModificacion;

  @XmlElement(name = "moneda")
  private String moneda;

  @XmlElementWrapper(name = "totalConImpuestos")
  @XmlElement(name = "totalImpuesto")
  private List<TotalImpuestoXML> totalConImpuestos;

  @XmlElement(name = "motivo")
  private String motivo;

  /** Required no-arg constructor for JAXB deserialization. */
  public InfoNotaCreditoXML() {}

  /**
   * Creates the XML model for the {@code infoNotaCredito} block.
   *
   * @param note credit note domain object
   * @param profile issuer profile used to complete the fiscal fields
   * @return JAXB-ready model for the section
   */
  public static InfoNotaCreditoXML fromDomain(CreditNote note, IssuerProfile profile) {
    InfoNotaCreditoXML xml = new InfoNotaCreditoXML();

    xml.fechaEmision = note.issueDate().format();
    xml.dirEstablecimiento = note.establishmentDirection();

    xml.tipoIdentificacionComprador = note.client().identification().identificationType().getCode();
    xml.razonSocialComprador = note.client().names();
    xml.identificacionComprador = note.client().identification().value();

    if (profile.specialTaxPayer() != null) {
      xml.contribuyenteEspecial = profile.specialTaxPayer().number();
    }
    xml.obligadoContabilidad = profile.accountingObligation().name();

    xml.codDocModificado = note.modifiedDocument().documentCode();
    xml.numDocModificado = note.modifiedDocument().number();
    xml.fechaEmisionDocSustento = note.modifiedDocument().issueDate().format();

    xml.totalSinImpuestos = String.valueOf(note.totals().totalWithoutTaxes());

    if (!note.compensations().isEmpty()) {
      xml.compensaciones = note.compensations().stream().map(CompensacionXML::fromDomain).toList();
    }

    xml.valorModificacion = String.valueOf(note.totals().totalValue());

    if (note.currency() != null) {
      xml.moneda = note.currency().getCurrency();
    }

    xml.totalConImpuestos =
        note.totals().totalTaxes().stream().map(TotalImpuestoXML::fromDomain).toList();

    xml.motivo = note.motivo();

    return xml;
  }
}
