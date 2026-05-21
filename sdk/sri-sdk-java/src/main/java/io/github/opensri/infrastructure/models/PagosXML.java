// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.common.payment.DeferredPayment;
import io.github.opensri.domain.entities.common.payment.Payment;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/** XML mapping for payment information used in document serialization. */
@XmlAccessorType(XmlAccessType.FIELD)
public class PagosXML {

  @XmlElement(name = "formaPago")
  String formaPago;

  @XmlElement(name = "total")
  String total;

  @XmlElement(name = "plazo")
  String plazo;

  @XmlElement(name = "unidadTiempo")
  String unidadTiempo;

  /** Default no-arg constructor required by JAXB. */
  public PagosXML() {}

  /**
   * Converts a domain Payment into its XML representation.
   *
   * @param payment the domain payment to convert
   * @return the converted PagosXML instance
   */
  public static PagosXML fromDomain(Payment payment) {
    PagosXML xml = new PagosXML();

    xml.formaPago = payment.paymentMethod().getCode();
    xml.total = payment.total().toString();
    if (payment instanceof DeferredPayment deferredPayment) {
      xml.plazo = deferredPayment.paymentMethod().toString();
      xml.unidadTiempo = deferredPayment.timeUnit().toString();
    }

    return xml;
  }
}
