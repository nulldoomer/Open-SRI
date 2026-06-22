// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models.guia_remision;

import io.github.opensri.domain.entities.remissionguide.Recipient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * JAXB model for a {@code destinatario} entry in a remission guide.
 *
 * <p>Maps the recipient identification, the transfer reason and the transported goods following the
 * element order required by the SRI schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DestinatarioXML {

  @XmlElement(name = "identificacionDestinatario")
  private String identificacionDestinatario;

  @XmlElement(name = "razonSocialDestinatario")
  private String razonSocialDestinatario;

  @XmlElement(name = "dirDestinatario")
  private String dirDestinatario;

  @XmlElement(name = "motivoTraslado")
  private String motivoTraslado;

  @XmlElement(name = "docAduaneroUnico")
  private String docAduaneroUnico;

  @XmlElement(name = "codEstabDestino")
  private String codEstabDestino;

  @XmlElement(name = "ruta")
  private String ruta;

  @XmlElementWrapper(name = "detalles")
  @XmlElement(name = "detalle")
  private List<DetalleGuiaXML> detalles;

  /** Required no-arg constructor for JAXB deserialization. */
  public DestinatarioXML() {}

  /**
   * Creates the XML model for a remission-guide recipient.
   *
   * @param recipient domain recipient to transform
   * @return JAXB-ready XML recipient model
   */
  public static DestinatarioXML fromDomain(Recipient recipient) {
    DestinatarioXML xml = new DestinatarioXML();

    xml.identificacionDestinatario = recipient.identification();
    xml.razonSocialDestinatario = recipient.socialReason();
    xml.dirDestinatario = recipient.address();
    xml.motivoTraslado = recipient.transferReason();
    xml.docAduaneroUnico = recipient.customsDoc();
    xml.codEstabDestino = recipient.destEstablishmentCode();
    xml.ruta = recipient.route();

    xml.detalles = recipient.items().stream().map(DetalleGuiaXML::fromDomain).toList();

    return xml;
  }
}
