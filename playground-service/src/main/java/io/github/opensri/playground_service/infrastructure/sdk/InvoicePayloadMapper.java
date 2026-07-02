// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.sdk;

import io.github.nulldoomer.opensri.api.builders.invoice.InvoiceBuilder;
import io.github.nulldoomer.opensri.domain.entities.common.Client;
import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;
import io.github.nulldoomer.opensri.domain.entities.common.Totals;
import io.github.nulldoomer.opensri.domain.entities.common.issuer.Issuer;
import io.github.nulldoomer.opensri.domain.entities.common.payment.ImmediatePayment;
import io.github.nulldoomer.opensri.domain.entities.common.payment.Payment;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.Tax;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.nulldoomer.opensri.domain.entities.invoice.Invoice;
import io.github.nulldoomer.opensri.domain.entities.invoice.InvoiceItem;
import io.github.nulldoomer.opensri.domain.enums.DocumentVersion;
import io.github.nulldoomer.opensri.domain.enums.PaymentMethod;
import io.github.nulldoomer.opensri.domain.valueobjects.ClientIdentification;
import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;
import io.github.nulldoomer.opensri.domain.valueobjects.NationalId;
import io.github.nulldoomer.opensri.domain.valueobjects.Ruc;
import io.github.opensri.playground_service.domain.model.InvoicePayload;
import io.github.opensri.playground_service.domain.model.Item;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class InvoicePayloadMapper {

  Invoice toInvoice(InvoicePayload dto) {

    List<InvoiceItem> items = dto.items().stream().map(this::mapItem).toList();
    Totals totals = Totals.from(items);

    Ruc issuerRuc = new Ruc(dto.issuerRuc());
    Issuer issuer = new Issuer(dto.issuerName(), issuerRuc);
    TaxInfo taxInfo = new TaxInfo(1, issuer, dto.establishmentAddress());

    DocumentNumber documentNumber =
        new DocumentNumber(dto.codDoc(), dto.estab(), dto.ptoEmi(), dto.secuencial());

    Client client = new Client(resolveIdentification(dto), dto.buyerName());

    Payment payment =
        new ImmediatePayment(PaymentMethod.valueOf(dto.paymentMethod()), totals.totalValue());

    return InvoiceBuilder.builder()
        .issueDate(IssueDate.now())
        .establishmentDirection(dto.establishmentAddress())
        .taxInfo(taxInfo)
        .documentNumber(documentNumber)
        .documentVersion(DocumentVersion.valueOf(dto.documentVersion()))
        .client(client)
        .addItems(items)
        .addPayments(List.of(payment))
        .build();
  }

  private InvoiceItem mapItem(Item dto) {

    BigDecimal cantidad =
        dto.quantity() != null ? BigDecimal.valueOf(dto.quantity()) : BigDecimal.ONE;
    BigDecimal precioUnitario = dto.price() != null ? dto.price() : BigDecimal.ZERO;
    BigDecimal subtotal = cantidad.multiply(precioUnitario);

    List<Tax> taxes = buildTaxes(dto, subtotal);

    return new InvoiceItem(
        dto.mainCode(),
        dto.auxiliaryCode(),
        dto.description(),
        cantidad,
        precioUnitario,
        BigDecimal.ZERO,
        subtotal,
        List.of(),
        taxes);
  }

  private List<Tax> buildTaxes(Item dto, BigDecimal subtotal) {

    if (dto.taxCode() == null || dto.taxPercentageCode() == null || dto.taxRate() == null) {
      return List.of();
    }
    BigDecimal taxValue =
        subtotal.multiply(dto.taxRate()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    return List.of(new Tax(dto.taxCode(), dto.taxPercentageCode(), dto.taxRate(), taxValue));
  }

  private ClientIdentification resolveIdentification(InvoicePayload dto) {

    return switch (dto.buyerIdentificationType().toUpperCase()) {
      case "RUC" -> new Ruc(dto.buyerIdentification());
      default -> new NationalId(dto.buyerIdentification());
    };
  }
}
