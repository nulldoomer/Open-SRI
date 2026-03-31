// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * JAXB model for an invoice-level additional field.
 *
 * <p>This structure maps the SRI {@code campoAdicional} element, where the field name is serialized
 * as an attribute and the associated value is written as the element text content.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoAdicionalXML {

  @XmlAttribute private String nombre;

  @XmlValue private String valor;

  public InfoAdicionalXML() {}

  /**
   * Creates the XML model for an invoice-level additional field.
   *
   * @param addInfo domain additional information entry to transform
   * @return JAXB-ready XML model for the provided additional field
   */
  public static InfoAdicionalXML fromDomain(AdditionalInfo addInfo) {
    InfoAdicionalXML xml = new InfoAdicionalXML();

    xml.nombre = addInfo.name();
    xml.valor = addInfo.value();

    return xml;
  }
}
