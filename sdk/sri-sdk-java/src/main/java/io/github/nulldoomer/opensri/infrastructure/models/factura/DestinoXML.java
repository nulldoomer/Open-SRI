// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.models.factura;

import io.github.nulldoomer.opensri.domain.entities.invoice.Destination;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * JAXB model for a single transport destination within the substitutive shipping guide.
 *
 * <p>Each instance maps one leg of the shipment route, including customs and destination
 * establishment data.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DestinoXML {

  @XmlElement(name = "motivoTraslado")
  private String motivoTraslado;

  @XmlElement(name = "docAduaneroUnico")
  private String docAduaneroUnico;

  @XmlElement(name = "codEstabDestino")
  private String codEstabDestino;

  @XmlElement(name = "ruta")
  private String ruta;

  /** Required no-arg constructor for JAXB deserialization. */
  public DestinoXML() {}

  /**
   * Creates the XML model for a transport destination.
   *
   * @param destination domain destination to transform
   * @return JAXB-ready XML destination model
   */
  public static DestinoXML fromDomain(Destination destination) {
    DestinoXML xml = new DestinoXML();

    xml.motivoTraslado = destination.motivoTraslado();
    xml.docAduaneroUnico = destination.docAduaneroUnico();
    xml.codEstabDestino = destination.codEstabDestino();
    xml.ruta = destination.ruta();

    return xml;
  }
}
