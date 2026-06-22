// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models.comprobante_retencion;

import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.withholding.WithholdingReceipt;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * JAXB model for the {@code infoCompRetencion} section of the withholding-receipt XML (version
 * 1.0.0).
 *
 * <p>Maps the withheld-subject data and the fiscal period following the element order required by
 * the SRI schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoCompRetencionXML {

  @XmlElement(name = "fechaEmision")
  private String fechaEmision;

  @XmlElement(name = "dirEstablecimiento")
  private String dirEstablecimiento;

  @XmlElement(name = "contribuyenteEspecial")
  private String contribuyenteEspecial;

  @XmlElement(name = "obligadoContabilidad")
  private String obligadoContabilidad;

  @XmlElement(name = "tipoIdentificacionSujetoRetenido")
  private String tipoIdentificacionSujetoRetenido;

  @XmlElement(name = "tipoSujetoRetenido")
  private String tipoSujetoRetenido;

  @XmlElement(name = "parteRel")
  private String parteRel;

  @XmlElement(name = "razonSocialSujetoRetenido")
  private String razonSocialSujetoRetenido;

  @XmlElement(name = "identificacionSujetoRetenido")
  private String identificacionSujetoRetenido;

  @XmlElement(name = "periodoFiscal")
  private String periodoFiscal;

  /** Required no-arg constructor for JAXB deserialization. */
  public InfoCompRetencionXML() {}

  /**
   * Creates the XML model for the {@code infoCompRetencion} block.
   *
   * @param receipt withholding-receipt domain object
   * @param profile issuer profile used to complete the fiscal fields
   * @return JAXB-ready model for the section
   */
  public static InfoCompRetencionXML fromDomain(WithholdingReceipt receipt, IssuerProfile profile) {
    InfoCompRetencionXML xml = new InfoCompRetencionXML();

    xml.fechaEmision = receipt.issueDate().format();
    xml.dirEstablecimiento = receipt.establishmentDirection();

    if (profile.specialTaxPayer() != null) {
      xml.contribuyenteEspecial = profile.specialTaxPayer().number();
    }
    xml.obligadoContabilidad = profile.accountingObligation().name();

    xml.tipoIdentificacionSujetoRetenido =
        receipt.subject().identification().identificationType().getCode();
    xml.tipoSujetoRetenido = receipt.subjectType();
    xml.parteRel = receipt.relatedParty();
    xml.razonSocialSujetoRetenido = receipt.subject().names();
    xml.identificacionSujetoRetenido = receipt.subject().identification().value();

    xml.periodoFiscal = receipt.fiscalPeriod();

    return xml;
  }
}
