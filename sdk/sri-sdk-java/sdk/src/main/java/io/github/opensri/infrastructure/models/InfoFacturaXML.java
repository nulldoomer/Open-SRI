// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.invoice.Invoice;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * JAXB model for the {@code infoFactura} section of the invoice XML.
 *
 * <p>This class maps buyer data, establishment information, invoice totals, and grouped tax totals
 * into the structure required by the SRI invoice schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoFacturaXML {

  @XmlElement(name = "fechaEmision")
  private String fechaEmision;

  @XmlElement(name = "dirEstablecimiento")
  private String dirEstablecimiento;

  @XmlElement(name = "contribuyenteEspecial")
  private String contribuyenteEspecial;

  // SI - NO
  @XmlElement(name = "obligadoContabilidad")
  private String obligadoContabilidad;

  @XmlElement(name = "tipoIdentificacionComprador")
  private String tipoIdentificacionComprador;

  @XmlElement(name = "razonSocialComprador")
  private String razonSocialComprador;

  @XmlElement(name = "identificacionComprador")
  private String identificacionComprador;

  @XmlElement(name = "totalSinImpuestos")
  private String totalSinImpuestos;

  @XmlElement(name = "totalDescuento")
  private String totalDescuento;

  @XmlElementWrapper(name = "totalConImpuestos")
  @XmlElement(name = "totalImpuesto")
  private List<TotalImpuestoXML> totalConImpuestos;

  @XmlElement(name = "propina")
  private String propina;

  @XmlElement(name = "importeTotal")
  private String importeTotal;

  @XmlElement(name = "moneda")
  private String moneda;

  @XmlElementWrapper(name = "pagos")
  @XmlElement(name = "pago")
  private List<PagosXML> pagos;

  // Constructor vacío para generar la instancia del contexto de JAXB
  public InfoFacturaXML() {}

  /**
   * Creates the XML model for the invoice information section.
   *
   * @param invoice domain invoice containing buyer, totals, and issue data
   * @param profile issuer profile used to complete fiscal configuration fields
   * @return JAXB-ready model for the {@code infoFactura} block
   */
  public static InfoFacturaXML fromDomain(Invoice invoice, IssuerProfile profile) {
    InfoFacturaXML xml = new InfoFacturaXML();

    xml.fechaEmision = invoice.issueDate().format();
    xml.dirEstablecimiento = invoice.establishmentDirection();
    if (profile.specialTaxPayer() != null) {
      xml.contribuyenteEspecial = profile.specialTaxPayer().number();
    }
    xml.obligadoContabilidad = profile.accountingObligation().name();

    xml.tipoIdentificacionComprador =
        invoice.client().identification().identificationType().getCode();
    xml.razonSocialComprador = invoice.client().names();
    xml.identificacionComprador = invoice.client().identification().value();

    xml.totalSinImpuestos = String.valueOf(invoice.totals().totalWithoutTaxes());
    xml.totalDescuento = String.valueOf(invoice.totals().totalDiscount());

    xml.totalConImpuestos =
        invoice.totals().totalTaxes().stream().map(TotalImpuestoXML::fromDomain).toList();

    xml.propina = String.valueOf(invoice.totals().totalTipValue());
    xml.importeTotal = String.valueOf(invoice.totals().totalValue());

    if (invoice.currency() != null) {
      xml.moneda = invoice.currency().getCurrency();
    }

    xml.pagos = invoice.payments().stream().map(PagosXML::fromDomain).toList();

    return xml;
  }
}
