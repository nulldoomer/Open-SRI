// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.worker.mapping;

import io.github.opensri.api.builders.invoice.InvoiceBuilder;
import io.github.opensri.domain.entities.common.Client;
import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.Totals;
import io.github.opensri.domain.entities.common.issuer.Issuer;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.common.payment.ImmediatePayment;
import io.github.opensri.domain.entities.common.payment.Payment;
import io.github.opensri.domain.entities.common.taxes.Tax;
import io.github.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.enums.AccountingObligation;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.domain.enums.PaymentMethod;
import io.github.opensri.domain.enums.TaxRate;
import io.github.opensri.domain.enums.TaxType;
import io.github.opensri.domain.valueobjects.ClientIdentification;
import io.github.opensri.domain.valueobjects.FinalConsumer;
import io.github.opensri.domain.valueobjects.IssueDate;
import io.github.opensri.domain.valueobjects.NationalId;
import io.github.opensri.domain.valueobjects.Ruc;
import io.github.opensri.domain.valueobjects.SpecialTaxPayer;
import io.github.opensri.playground_service.api.dto.InvoicePayload;
import io.github.opensri.playground_service.api.dto.PlaygroundRunRequest;
import io.github.opensri.shared.exceptions.OpenSRIValidationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Translates the playground JSON request into the SDK domain model.
 *
 * <p>This keeps the wire contract ({@link InvoicePayload}) decoupled from the SDK builders and
 * value objects. Line totals and the default payment are derived here so the request stays small;
 * domain validation (RUC, national id, payment totals) is delegated to the SDK and surfaces as
 * {@link OpenSRIValidationException} or {@link IllegalStateException}.
 */
@Component
public class InvoiceMapper {

  private static final int MONEY_SCALE = 2;

  /**
   * Builds the SDK {@link Invoice} from the request payload and issuer data.
   *
   * @param request the playground run request
   * @return an immutable, validated invoice ready to be sent
   */
  public Invoice toInvoice(PlaygroundRunRequest request) {
    InvoicePayload payload = request.invoice();
    TaxInfo taxInfo = toTaxInfo(request.issuer());

    List<InvoiceItem> items = toItems(payload.items());
    List<Payment> payments = toPayments(payload.payments(), items);

    return InvoiceBuilder.builder()
        .issueDate(toIssueDate(payload.issueDate()))
        .establishmentDirection(payload.establishmentDirection())
        .taxInfo(taxInfo)
        .documentNumber(
            new DocumentNumber(
                payload.documentCode(),
                payload.establishment(),
                payload.emissionPoint(),
                payload.sequentialNumber()))
        .documentVersion(toDocumentVersion(payload.documentVersion()))
        .client(toClient(payload.client()))
        .addItems(items)
        .addPayments(payments)
        .build();
  }

  /**
   * Builds the issuer profile (accounting obligation, special taxpayer) from the request.
   *
   * @param issuer issuer data from the request
   * @return the SDK issuer profile
   */
  public IssuerProfile toIssuerProfile(PlaygroundRunRequest.Issuer issuer) {
    SpecialTaxPayer specialTaxPayer =
        issuer.specialTaxPayer() == null || issuer.specialTaxPayer().isBlank()
            ? null
            : new SpecialTaxPayer(issuer.specialTaxPayer());

    return new IssuerProfile(
        new Ruc(issuer.ruc()),
        specialTaxPayer,
        toAccountingObligation(issuer.accountingObligation()));
  }

  /**
   * Resolves the SRI environment from its name.
   *
   * @param environment {@code PRUEBAS} or {@code PRODUCCION}
   * @return the matching environment
   */
  public Environment toEnvironment(String environment) {
    try {
      return Environment.valueOf(environment);
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new OpenSRIValidationException("Unknown environment: " + environment);
    }
  }

  private IssueDate toIssueDate(String value) {
    // The SDK's IssueDate.from uses a STRICT "yyyy" (year-of-era) pattern that rejects every
    // plain ISO date, so we parse with the standard ISO formatter and let IssueDate's own
    // constructor apply its not-null / not-future validation.
    try {
      return new IssueDate(LocalDate.parse(value));
    } catch (DateTimeParseException e) {
      throw new OpenSRIValidationException("Invalid issueDate, expected yyyy-MM-dd: " + value);
    }
  }

  private TaxInfo toTaxInfo(PlaygroundRunRequest.Issuer issuer) {
    return new TaxInfo(
        issuer.emissionType(),
        new Issuer(issuer.socialReason(), new Ruc(issuer.ruc())),
        issuer.parentAddress());
  }

  private Client toClient(InvoicePayload.Client client) {
    ClientIdentification identification =
        switch (client.identificationType()) {
          case "RUC" -> new Ruc(client.identification());
          case "CEDULA" -> new NationalId(client.identification());
          case "FINAL_CONSUMER" -> FinalConsumer.instance();
          default ->
              throw new OpenSRIValidationException(
                  "Unsupported identification type: " + client.identificationType());
        };

    return new Client(identification, client.names());
  }

  private List<InvoiceItem> toItems(List<InvoicePayload.Item> items) {
    if (items == null || items.isEmpty()) {
      throw new OpenSRIValidationException("Invoice must contain at least one item");
    }

    List<InvoiceItem> result = new ArrayList<>(items.size());
    for (InvoicePayload.Item item : items) {
      result.add(toItem(item));
    }
    return result;
  }

  private InvoiceItem toItem(InvoicePayload.Item item) {
    BigDecimal discount = item.discount() == null ? BigDecimal.ZERO : item.discount();
    BigDecimal totalWithoutTax =
        item.quantity()
            .multiply(item.unitPrice())
            .subtract(discount)
            .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

    List<Tax> taxes = new ArrayList<>();
    if (item.taxes() != null) {
      for (InvoicePayload.Tax tax : item.taxes()) {
        taxes.add(toTax(tax, totalWithoutTax));
      }
    }

    return new InvoiceItem(
        item.mainCode(),
        item.auxCode(),
        item.description(),
        item.quantity(),
        item.unitPrice(),
        discount,
        totalWithoutTax,
        List.of(),
        taxes);
  }

  private Tax toTax(InvoicePayload.Tax tax, BigDecimal taxableBase) {
    TaxType taxType = TaxType.fromCode(tax.taxType());
    TaxRate taxRate = TaxRate.fromCode(tax.rateCode());
    BigDecimal rate = taxRate.getValue() == null ? BigDecimal.ZERO : taxRate.getValue();

    return new Tax(
        String.valueOf(taxType.getCode()), String.valueOf(taxRate.getCode()), rate, taxableBase);
  }

  private List<Payment> toPayments(List<InvoicePayload.Payment> payments, List<InvoiceItem> items) {
    if (payments == null || payments.isEmpty()) {
      BigDecimal total = Totals.from(items).totalValue();
      return List.of(new ImmediatePayment(PaymentMethod.SIN_SISTEMA_FINANCIERO, total));
    }

    List<Payment> result = new ArrayList<>(payments.size());
    for (InvoicePayload.Payment payment : payments) {
      result.add(new ImmediatePayment(toPaymentMethod(payment.paymentMethod()), payment.total()));
    }
    return result;
  }

  private PaymentMethod toPaymentMethod(String paymentMethod) {
    try {
      return PaymentMethod.valueOf(paymentMethod);
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new OpenSRIValidationException("Unknown payment method: " + paymentMethod);
    }
  }

  private AccountingObligation toAccountingObligation(String value) {
    try {
      return AccountingObligation.valueOf(value);
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new OpenSRIValidationException("Unknown accounting obligation: " + value);
    }
  }

  private DocumentVersion toDocumentVersion(String version) {
    return Arrays.stream(DocumentVersion.values())
        .filter(v -> v.getVersion().equals(version))
        .findFirst()
        .orElseThrow(
            () -> new OpenSRIValidationException("Unsupported document version: " + version));
  }
}
