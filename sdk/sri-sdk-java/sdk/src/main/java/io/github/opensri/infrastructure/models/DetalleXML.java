// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.invoice.InvoiceItem;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * JAXB model for an invoice line item in the SRI XML schema.
 *
 * <p>This class maps the commercial values, tax data, and optional additional details of a single
 * {@code detalle} element inside the invoice document.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DetalleXML {

  @XmlElement(name = "codigoPrincipal")
  private String codigoPrincipal;

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

  // Constructor vacío para generar la instancia del contexto de JAXB
  public DetalleXML() {}

  /**
   * Creates the XML model for a single invoice item.
   *
   * @param invoiceItem domain invoice item to transform
   * @return JAXB-ready XML model for the provided detail line
   */
  public static DetalleXML fromDomain(InvoiceItem invoiceItem) {

    DetalleXML xml = new DetalleXML();

    xml.codigoPrincipal = invoiceItem.mainCode();
    xml.descripcion = invoiceItem.description();
    xml.cantidad = String.valueOf(invoiceItem.quantity());
    xml.precioUnitario = String.valueOf(invoiceItem.unitPrice());
    xml.descuento = String.valueOf(invoiceItem.discount());
    xml.precioTotalSinImpuesto = String.valueOf(invoiceItem.totalPriceWithoutTax());

    xml.detallesAdicionales =
        invoiceItem.additionalDetails().stream().map(DetalleAdicionalXML::fromDomain).toList();

    // TODO: Añadir comentario explicativo
    xml.impuestos = invoiceItem.taxes().stream().map(ImpuestoXML::fromDomain).toList();

    return xml;
  }
}
