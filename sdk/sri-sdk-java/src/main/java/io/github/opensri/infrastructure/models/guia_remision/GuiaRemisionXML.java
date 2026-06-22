// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models.guia_remision;

import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.remissionguide.RemissionGuide;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.infrastructure.models.common.InfoAdicionalXML;
import io.github.opensri.infrastructure.models.common.InfoTributariaXML;
import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * Root JAXB model for the SRI remission-guide XML document.
 *
 * <p>Assembles the {@code guiaRemision} structure: tax information, the guide info block, the
 * recipients, and optional additional fields. Reuses the shared {@link InfoTributariaXML} and
 * {@link InfoAdicionalXML} models.
 */
@XmlRootElement(name = "guiaRemision")
@XmlAccessorType(XmlAccessType.FIELD)
public class GuiaRemisionXML {

  @XmlAttribute private String id = "comprobante";

  @XmlAttribute private String version;

  @XmlElement(name = "infoTributaria")
  private InfoTributariaXML infoTributaria;

  @XmlElement(name = "infoGuiaRemision")
  private InfoGuiaRemisionXML infoGuiaRemision;

  @XmlElementWrapper(name = "destinatarios")
  @XmlElement(name = "destinatario")
  private List<DestinatarioXML> destinatarios;

  @XmlElementWrapper(name = "infoAdicional")
  @XmlElement(name = "campoAdicional")
  private List<InfoAdicionalXML> infoAdicional;

  /** Required no-arg constructor for JAXB deserialization. */
  public GuiaRemisionXML() {}

  /**
   * Creates the complete XML model for a remission guide.
   *
   * @param guide remission-guide domain object to transform
   * @param accessKey access key previously generated for the document
   * @param env target SRI environment of the XML
   * @param profile issuer profile used to complete the fiscal sections
   * @return root JAXB model representing the remission-guide XML document
   */
  public static GuiaRemisionXML fromDomain(
      RemissionGuide guide, String accessKey, Environment env, IssuerProfile profile) {
    GuiaRemisionXML xml = new GuiaRemisionXML();

    xml.version = guide.documentVersion().getVersion();

    xml.infoTributaria =
        InfoTributariaXML.fromDomain(guide.taxInfo(), guide.documentNumber(), accessKey, env);

    xml.infoGuiaRemision = InfoGuiaRemisionXML.fromDomain(guide, profile);

    xml.destinatarios = guide.recipients().stream().map(DestinatarioXML::fromDomain).toList();

    if (!guide.additionalInfo().isEmpty()) {
      xml.infoAdicional =
          guide.additionalInfo().stream().map(InfoAdicionalXML::fromDomain).toList();
    }

    return xml;
  }
}
