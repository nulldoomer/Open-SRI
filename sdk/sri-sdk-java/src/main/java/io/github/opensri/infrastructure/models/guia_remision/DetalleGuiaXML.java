// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models.guia_remision;

import io.github.opensri.domain.entities.remissionguide.RemissionGuideItem;
import io.github.opensri.infrastructure.models.factura.DetalleAdicionalXML;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * JAXB model for a remission-guide line item ({@code detalle}).
 *
 * <p>Describes a transported good with its description and quantity; remission-guide details carry
 * no prices or taxes.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DetalleGuiaXML {

  @XmlElement(name = "codigoInterno")
  private String codigoInterno;

  @XmlElement(name = "codigoAdicional")
  private String codigoAdicional;

  @XmlElement(name = "descripcion")
  private String descripcion;

  @XmlElement(name = "cantidad")
  private String cantidad;

  @XmlElementWrapper(name = "detallesAdicionales")
  @XmlElement(name = "detAdicional")
  private List<DetalleAdicionalXML> detallesAdicionales;

  /** Required no-arg constructor for JAXB deserialization. */
  public DetalleGuiaXML() {}

  /**
   * Creates the XML model for a single remission-guide line item.
   *
   * @param item domain line item to transform
   * @return JAXB-ready XML model for the provided detail line
   */
  public static DetalleGuiaXML fromDomain(RemissionGuideItem item) {
    DetalleGuiaXML xml = new DetalleGuiaXML();

    xml.codigoInterno = item.mainCode();
    xml.codigoAdicional = item.auxCode();
    xml.descripcion = item.description();
    xml.cantidad = String.valueOf(item.quantity());

    if (!item.additionalDetails().isEmpty()) {
      xml.detallesAdicionales =
          item.additionalDetails().stream().map(DetalleAdicionalXML::fromDomain).toList();
    }

    return xml;
  }
}
