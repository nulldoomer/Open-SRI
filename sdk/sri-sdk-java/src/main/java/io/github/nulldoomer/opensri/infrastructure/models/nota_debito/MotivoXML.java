// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.models.nota_debito;

import io.github.nulldoomer.opensri.domain.entities.debitnote.DebitNoteReason;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * JAXB model for a {@code motivo} entry in a debit note.
 *
 * <p>Maps the reason description and its monetary value as required by the debit-note schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MotivoXML {

  @XmlElement(name = "razon")
  private String razon;

  @XmlElement(name = "valor")
  private String valor;

  /** Required no-arg constructor for JAXB deserialization. */
  public MotivoXML() {}

  /**
   * Creates the XML model for a debit-note reason.
   *
   * @param reason domain reason to transform
   * @return JAXB-ready XML reason model
   */
  public static MotivoXML fromDomain(DebitNoteReason reason) {
    MotivoXML xml = new MotivoXML();

    xml.razon = reason.razon();
    xml.valor = reason.value().toPlainString();

    return xml;
  }
}
