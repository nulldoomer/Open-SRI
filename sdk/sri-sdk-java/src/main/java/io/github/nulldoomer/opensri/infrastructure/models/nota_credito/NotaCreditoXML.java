// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.models.nota_credito;

import io.github.nulldoomer.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.nulldoomer.opensri.domain.entities.creditnote.CreditNote;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.infrastructure.models.common.InfoAdicionalXML;
import io.github.nulldoomer.opensri.infrastructure.models.common.InfoTributariaXML;
import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * Root JAXB model for the SRI credit-note XML document.
 *
 * <p>Assembles the {@code notaCredito} structure: tax information, the credit-note info block, the
 * affected detail lines, and optional additional fields. Reuses the shared {@link
 * InfoTributariaXML} and {@link InfoAdicionalXML} models.
 */
@XmlRootElement(name = "notaCredito")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotaCreditoXML {

  @XmlAttribute private String id = "comprobante";

  @XmlAttribute private String version;

  @XmlElement(name = "infoTributaria")
  private InfoTributariaXML infoTributaria;

  @XmlElement(name = "infoNotaCredito")
  private InfoNotaCreditoXML infoNotaCredito;

  @XmlElementWrapper(name = "detalles")
  @XmlElement(name = "detalle")
  private List<DetalleNotaCreditoXML> detalles;

  @XmlElementWrapper(name = "infoAdicional")
  @XmlElement(name = "campoAdicional")
  private List<InfoAdicionalXML> infoAdicional;

  /** Required no-arg constructor for JAXB deserialization. */
  public NotaCreditoXML() {}

  /**
   * Creates the complete XML model for a credit note.
   *
   * @param note credit-note domain object to transform
   * @param accessKey access key previously generated for the document
   * @param env target SRI environment of the XML
   * @param profile issuer profile used to complete the fiscal sections
   * @return root JAXB model representing the credit-note XML document
   */
  public static NotaCreditoXML fromDomain(
      CreditNote note, String accessKey, Environment env, IssuerProfile profile) {
    NotaCreditoXML xml = new NotaCreditoXML();

    xml.version = note.documentVersion().getVersion();

    xml.infoTributaria =
        InfoTributariaXML.fromDomain(note.taxInfo(), note.documentNumber(), accessKey, env);

    xml.infoNotaCredito = InfoNotaCreditoXML.fromDomain(note, profile);

    xml.detalles = note.items().stream().map(DetalleNotaCreditoXML::fromDomain).toList();

    if (!note.additionalInfo().isEmpty()) {
      xml.infoAdicional = note.additionalInfo().stream().map(InfoAdicionalXML::fromDomain).toList();
    }

    return xml;
  }
}
