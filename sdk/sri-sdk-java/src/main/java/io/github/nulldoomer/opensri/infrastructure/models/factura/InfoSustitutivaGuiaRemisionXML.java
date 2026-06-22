// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.models.factura;

import io.github.nulldoomer.opensri.domain.entities.invoice.RemisionGuideSubstituteInfo;
import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * JAXB model for the substitutive shipping guide section of the invoice.
 *
 * <p>Applies to schema versions 2.0.0 and 2.1.0. Embeds transport data that would normally appear
 * in a separate guía de remisión when both documents are issued simultaneously.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoSustitutivaGuiaRemisionXML {

  @XmlElement(name = "dirPartida")
  private String dirPartida;

  @XmlElement(name = "dirDestinatario")
  private String dirDestinatario;

  @XmlElement(name = "fechaIniTransporte")
  private String fechaIniTransporte;

  @XmlElement(name = "fechaFinTransporte")
  private String fechaFinTransporte;

  @XmlElement(name = "razonSocialTransportista")
  private String razonSocialTransportista;

  @XmlElement(name = "tipoIdentificacionTransportista")
  private String tipoIdentificacionTransportista;

  @XmlElement(name = "rucTransportista")
  private String rucTransportista;

  @XmlElement(name = "placa")
  private String placa;

  @XmlElementWrapper(name = "destinos")
  @XmlElement(name = "destino")
  private List<DestinoXML> destinos;

  /** Required no-arg constructor for JAXB deserialization. */
  public InfoSustitutivaGuiaRemisionXML() {}

  /**
   * Creates the XML model for a substitutive shipping guide section.
   *
   * @param info domain shipping guide data to transform
   * @return JAXB-ready XML shipping guide model
   */
  public static InfoSustitutivaGuiaRemisionXML fromDomain(RemisionGuideSubstituteInfo info) {
    InfoSustitutivaGuiaRemisionXML xml = new InfoSustitutivaGuiaRemisionXML();

    xml.dirPartida = info.dirPartida();
    xml.dirDestinatario = info.dirDestinatario();
    xml.fechaIniTransporte = info.fechaIniTransporte();
    xml.fechaFinTransporte = info.fechaFinTransporte();
    xml.razonSocialTransportista = info.razonSocialTransportista();
    xml.tipoIdentificacionTransportista = info.tipoIdentificacionTransportista();
    xml.rucTransportista = info.rucTransportista();
    xml.placa = info.placa();
    xml.destinos = info.destinations().stream().map(DestinoXML::fromDomain).toList();

    return xml;
  }
}
