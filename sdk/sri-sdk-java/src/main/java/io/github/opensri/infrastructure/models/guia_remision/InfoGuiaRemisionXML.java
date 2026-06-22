// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models.guia_remision;

import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.remissionguide.RemissionGuide;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * JAXB model for the {@code infoGuiaRemision} section of the remission-guide XML.
 *
 * <p>Maps the carrier data and the transport period following the element order required by the SRI
 * schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoGuiaRemisionXML {

  @XmlElement(name = "dirEstablecimiento")
  private String dirEstablecimiento;

  @XmlElement(name = "dirPartida")
  private String dirPartida;

  @XmlElement(name = "razonSocialTransportista")
  private String razonSocialTransportista;

  @XmlElement(name = "tipoIdentificacionTransportista")
  private String tipoIdentificacionTransportista;

  @XmlElement(name = "rucTransportista")
  private String rucTransportista;

  @XmlElement(name = "obligadoContabilidad")
  private String obligadoContabilidad;

  @XmlElement(name = "fechaIniTransporte")
  private String fechaIniTransporte;

  @XmlElement(name = "fechaFinTransporte")
  private String fechaFinTransporte;

  @XmlElement(name = "placa")
  private String placa;

  /** Required no-arg constructor for JAXB deserialization. */
  public InfoGuiaRemisionXML() {}

  /**
   * Creates the XML model for the {@code infoGuiaRemision} block.
   *
   * @param guide remission-guide domain object
   * @param profile issuer profile used to complete the accounting obligation field
   * @return JAXB-ready model for the section
   */
  public static InfoGuiaRemisionXML fromDomain(RemissionGuide guide, IssuerProfile profile) {
    InfoGuiaRemisionXML xml = new InfoGuiaRemisionXML();

    xml.dirEstablecimiento = guide.establishmentDirection();
    xml.dirPartida = guide.departureAddress();
    xml.razonSocialTransportista = guide.carrierName();
    xml.tipoIdentificacionTransportista = guide.carrierIdType();
    xml.rucTransportista = guide.carrierId();
    xml.obligadoContabilidad = profile.accountingObligation().name();
    xml.fechaIniTransporte = guide.transportStartDate();
    xml.fechaFinTransporte = guide.transportEndDate();
    xml.placa = guide.plate();

    return xml;
  }
}
