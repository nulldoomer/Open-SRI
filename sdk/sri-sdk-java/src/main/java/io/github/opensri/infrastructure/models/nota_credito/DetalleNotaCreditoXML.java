// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models.nota_credito;

import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.infrastructure.models.factura.DetalleAdicionalXML;
import io.github.opensri.infrastructure.models.factura.ImpuestoXML;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * JAXB model for a credit-note line item ({@code detalle}).
 *
 * <p>Mirrors the invoice detail but uses the {@code codigoInterno}/{@code codigoAdicional} element
 * names required by the credit-note schema and omits the {@code unidadMedida} field.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DetalleNotaCreditoXML {

  @XmlElement(name = "codigoInterno")
  private String codigoInterno;

  @XmlElement(name = "codigoAdicional")
  private String codigoAdicional;

  @XmlElement(name = "descripcion")
  private String descripcion;

  @XmlElement(name = "cantidad")
  private String cantidad;

  @XmlElement(name = "precioUnitario")
  private String precioUnitario;

  @XmlElement(name = "descuento")
  private String descuento;

  @XmlElement(name = "precioTotalSinImpuesto")
  private String precioTotalSinImpuesto;

  @XmlElementWrapper(name = "detallesAdicionales")
  @XmlElement(name = "detAdicional")
  private List<DetalleAdicionalXML> detallesAdicionales;

  @XmlElementWrapper(name = "impuestos")
  @XmlElement(name = "impuesto")
  private List<ImpuestoXML> impuestos;

  /** Required no-arg constructor for JAXB deserialization. */
  public DetalleNotaCreditoXML() {}

  /**
   * Creates the XML model for a single credit-note line item.
   *
   * @param item domain line item to transform
   * @return JAXB-ready XML model for the provided detail line
   */
  public static DetalleNotaCreditoXML fromDomain(InvoiceItem item) {
    DetalleNotaCreditoXML xml = new DetalleNotaCreditoXML();

    xml.codigoInterno = item.mainCode();
    xml.codigoAdicional = item.auxCode();
    xml.descripcion = item.description();
    xml.cantidad = String.valueOf(item.quantity());
    xml.precioUnitario = String.valueOf(item.unitPrice());
    xml.descuento = String.valueOf(item.discount());
    xml.precioTotalSinImpuesto = String.valueOf(item.totalPriceWithoutTax());

    if (item.additionalDetails() != null && !item.additionalDetails().isEmpty()) {
      xml.detallesAdicionales =
          item.additionalDetails().stream().map(DetalleAdicionalXML::fromDomain).toList();
    }

    xml.impuestos = item.taxes().stream().map(ImpuestoXML::fromDomain).toList();

    return xml;
  }
}
