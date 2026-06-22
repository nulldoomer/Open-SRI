// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.models.liquidaction_compra;

import io.github.nulldoomer.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.nulldoomer.opensri.domain.entities.purchasesettlement.PurchaseSettlement;
import io.github.nulldoomer.opensri.infrastructure.models.common.PagosXML;
import io.github.nulldoomer.opensri.infrastructure.models.factura.TotalImpuestoXML;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * JAXB model for the {@code infoLiquidacionCompra} section of the purchase-settlement XML.
 *
 * <p>Maps the provider data and the totals following the element order required by the SRI schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoLiquidacionCompraXML {

  @XmlElement(name = "fechaEmision")
  private String fechaEmision;

  @XmlElement(name = "dirEstablecimiento")
  private String dirEstablecimiento;

  @XmlElement(name = "contribuyenteEspecial")
  private String contribuyenteEspecial;

  @XmlElement(name = "obligadoContabilidad")
  private String obligadoContabilidad;

  @XmlElement(name = "tipoIdentificacionProveedor")
  private String tipoIdentificacionProveedor;

  @XmlElement(name = "razonSocialProveedor")
  private String razonSocialProveedor;

  @XmlElement(name = "identificacionProveedor")
  private String identificacionProveedor;

  @XmlElement(name = "direccionProveedor")
  private String direccionProveedor;

  @XmlElement(name = "totalSinImpuestos")
  private String totalSinImpuestos;

  @XmlElement(name = "totalDescuento")
  private String totalDescuento;

  @XmlElementWrapper(name = "totalConImpuestos")
  @XmlElement(name = "totalImpuesto")
  private List<TotalImpuestoXML> totalConImpuestos;

  @XmlElement(name = "importeTotal")
  private String importeTotal;

  @XmlElement(name = "moneda")
  private String moneda;

  @XmlElementWrapper(name = "pagos")
  @XmlElement(name = "pago")
  private List<PagosXML> pagos;

  /** Required no-arg constructor for JAXB deserialization. */
  public InfoLiquidacionCompraXML() {}

  /**
   * Creates the XML model for the {@code infoLiquidacionCompra} block.
   *
   * @param settlement purchase-settlement domain object
   * @param profile issuer profile used to complete the fiscal fields
   * @return JAXB-ready model for the section
   */
  public static InfoLiquidacionCompraXML fromDomain(
      PurchaseSettlement settlement, IssuerProfile profile) {
    InfoLiquidacionCompraXML xml = new InfoLiquidacionCompraXML();

    xml.fechaEmision = settlement.issueDate().format();
    xml.dirEstablecimiento = settlement.establishmentDirection();

    if (profile.specialTaxPayer() != null) {
      xml.contribuyenteEspecial = profile.specialTaxPayer().number();
    }
    xml.obligadoContabilidad = profile.accountingObligation().name();

    xml.tipoIdentificacionProveedor =
        settlement.provider().identification().identificationType().getCode();
    xml.razonSocialProveedor = settlement.provider().socialReason();
    xml.identificacionProveedor = settlement.provider().identification().value();
    xml.direccionProveedor = settlement.provider().address();

    xml.totalSinImpuestos = String.valueOf(settlement.totals().totalWithoutTaxes());
    xml.totalDescuento = String.valueOf(settlement.totals().totalDiscount());

    xml.totalConImpuestos =
        settlement.totals().totalTaxes().stream().map(TotalImpuestoXML::fromDomain).toList();

    xml.importeTotal = String.valueOf(settlement.totals().totalValue());

    if (settlement.currency() != null) {
      xml.moneda = settlement.currency().getCurrency();
    }

    if (!settlement.payments().isEmpty()) {
      xml.pagos = settlement.payments().stream().map(PagosXML::fromDomain).toList();
    }

    return xml;
  }
}
