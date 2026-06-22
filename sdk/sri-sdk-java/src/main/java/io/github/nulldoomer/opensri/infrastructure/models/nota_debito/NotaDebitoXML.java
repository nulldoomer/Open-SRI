// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.models.nota_debito;

import io.github.nulldoomer.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.nulldoomer.opensri.domain.entities.debitnote.DebitNote;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.infrastructure.models.common.InfoAdicionalXML;
import io.github.nulldoomer.opensri.infrastructure.models.common.InfoTributariaXML;
import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * Root JAXB model for the SRI debit-note XML document.
 *
 * <p>Assembles the {@code notaDebito} structure: tax information, the debit-note info block, the
 * reason entries, and optional additional fields. Reuses the shared {@link InfoTributariaXML} and
 * {@link InfoAdicionalXML} models.
 */
@XmlRootElement(name = "notaDebito")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotaDebitoXML {

  @XmlAttribute private String id = "comprobante";

  @XmlAttribute private String version;

  @XmlElement(name = "infoTributaria")
  private InfoTributariaXML infoTributaria;

  @XmlElement(name = "infoNotaDebito")
  private InfoNotaDebitoXML infoNotaDebito;

  @XmlElementWrapper(name = "motivos")
  @XmlElement(name = "motivo")
  private List<MotivoXML> motivos;

  @XmlElementWrapper(name = "infoAdicional")
  @XmlElement(name = "campoAdicional")
  private List<InfoAdicionalXML> infoAdicional;

  /** Required no-arg constructor for JAXB deserialization. */
  public NotaDebitoXML() {}

  /**
   * Creates the complete XML model for a debit note.
   *
   * @param note debit-note domain object to transform
   * @param accessKey access key previously generated for the document
   * @param env target SRI environment of the XML
   * @param profile issuer profile used to complete the fiscal sections
   * @return root JAXB model representing the debit-note XML document
   */
  public static NotaDebitoXML fromDomain(
      DebitNote note, String accessKey, Environment env, IssuerProfile profile) {
    NotaDebitoXML xml = new NotaDebitoXML();

    xml.version = note.documentVersion().getVersion();

    xml.infoTributaria =
        InfoTributariaXML.fromDomain(note.taxInfo(), note.documentNumber(), accessKey, env);

    xml.infoNotaDebito = InfoNotaDebitoXML.fromDomain(note, profile);

    xml.motivos = note.reasons().stream().map(MotivoXML::fromDomain).toList();

    if (!note.additionalInfo().isEmpty()) {
      xml.infoAdicional = note.additionalInfo().stream().map(InfoAdicionalXML::fromDomain).toList();
    }

    return xml;
  }
}
