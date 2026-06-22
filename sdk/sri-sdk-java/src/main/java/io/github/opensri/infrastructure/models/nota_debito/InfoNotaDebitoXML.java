// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models.nota_debito;

import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.debitnote.DebitNote;
import io.github.opensri.infrastructure.models.common.CompensacionXML;
import io.github.opensri.infrastructure.models.common.PagosXML;
import io.github.opensri.infrastructure.models.factura.ImpuestoXML;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * JAXB model for the {@code infoNotaDebito} section of the debit-note XML.
 *
 * <p>Maps the buyer data, the reference to the modified document, the document-level taxes and the
 * totals, following the element order required by the SRI schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoNotaDebitoXML {

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

  @XmlElementWrapper(name = "impuestos")
  @XmlElement(name = "impuesto")
  private List<ImpuestoXML> impuestos;

  @XmlElementWrapper(name = "compensaciones")
  @XmlElement(name = "compensacion")
  private List<CompensacionXML> compensaciones;

  @XmlElement(name = "valorTotal")
  private String valorTotal;

  @XmlElementWrapper(name = "pagos")
  @XmlElement(name = "pago")
  private List<PagosXML> pagos;

  /** Required no-arg constructor for JAXB deserialization. */
  public InfoNotaDebitoXML() {}

  /**
   * Creates the XML model for the {@code infoNotaDebito} block.
   *
   * @param note debit-note domain object
   * @param profile issuer profile used to complete the fiscal fields
   * @return JAXB-ready model for the section
   */
  public static InfoNotaDebitoXML fromDomain(DebitNote note, IssuerProfile profile) {
    InfoNotaDebitoXML xml = new InfoNotaDebitoXML();

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

    xml.totalSinImpuestos = String.valueOf(note.totalSinImpuestos());

    xml.impuestos = note.taxes().stream().map(ImpuestoXML::fromDomain).toList();

    if (!note.compensations().isEmpty()) {
      xml.compensaciones = note.compensations().stream().map(CompensacionXML::fromDomain).toList();
    }

    xml.valorTotal = String.valueOf(note.valorTotal());

    if (!note.payments().isEmpty()) {
      xml.pagos = note.payments().stream().map(PagosXML::fromDomain).toList();
    }

    return xml;
  }
}
