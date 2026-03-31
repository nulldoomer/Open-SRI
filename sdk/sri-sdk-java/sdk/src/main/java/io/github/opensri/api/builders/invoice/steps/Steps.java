package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.common.Client;
import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.Totals;
import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.entities.common.TaxInfo;
import io.github.opensri.domain.valueobjects.IssueDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Orchestrates the fluent construction of an {@link Invoice} through compile-time steps.
 *
 * <p>This builder implementation collects the mandatory tax data, buyer information,
 * line items, and optional additional information required to assemble an invoice entity.
 * It also calculates {@link Totals} automatically from the registered items during the
 * final build step.
 *
 * <p>It backs {@link io.github.opensri.api.builders.invoice.InvoiceBuilder} and centralizes
 * the mutable state needed to enforce the step-builder flow while keeping the resulting
 * domain entity immutable.
 *
 * @see Invoice
 * @see Totals
 */
public final class Steps implements
        IssueDateStep,
        EstablishmentDirectionStep,
        TaxInfoStep,
        DocumentNumberStep,
        ClientStep,
        FirstItemStep,
        ItemsStep,
        FirstAdditionalInfoStep,
        AdditionalInfoStep,
        BuildStep {

    private IssueDate issueDate;
    private String establishmentDirection;
    private TaxInfo taxInfo;
    private DocumentNumber documentNumber;
    private Client client;
    private final List<InvoiceItem> items = new ArrayList<>();
    private final List<AdditionalInfo> additionalInfos = new ArrayList<>();

    /**
     * Stores the issue date of the invoice being built.
     *
     * @param issueDate validated issue date of the document
     * @return next step that requires the establishment address
     */
    @Override
    public EstablishmentDirectionStep issueDate(IssueDate issueDate) {
        this.issueDate = Objects.requireNonNull(issueDate,
                "IssueDate is required");
        return this;
    }

    /**
     * Stores the establishment address printed in the invoice.
     *
     * @param establishmentDirection address of the issuing establishment
     * @return next step that requires reusable tax information
     */
    @Override
    public TaxInfoStep establishmentDirection(String establishmentDirection) {
        this.establishmentDirection =
                Objects.requireNonNull(establishmentDirection,
                        "Establishment direction is required");
        return this;
    }

    /**
     * Stores the reusable tax information of the issuer.
     *
     * @param taxInfo issuer tax data required by the invoice
     * @return next step that requires the document number
     */
    @Override
    public DocumentNumberStep taxInfo(TaxInfo taxInfo) {
        this.taxInfo = Objects.requireNonNull(taxInfo,
                "TaxInfo is required");
        return this;
    }

    /**
     * Stores the document numbering data for the invoice.
     *
     * @param documentNumber document code, establishment, emission point, and sequential number
     * @return next step that requires the buyer information
     */
    @Override
    public ClientStep documentNumber(DocumentNumber documentNumber) {
        this.documentNumber =
                Objects.requireNonNull(documentNumber,
                        "DocumentNumber is required");
        return this;
    }


    /**
     * Stores the buyer data associated with the invoice.
     *
     * @param client buyer tax identification and display name
     * @return first step that requires at least one invoice item
     */
    @Override
    public FirstItemStep client(Client client) {
        this.client = Objects.requireNonNull(client,
                "Client is required");
        return this;
    }

    /**
     * Adds a line item to the invoice being built.
     *
     * @param item invoice item to include in the final document
     * @return same step so additional items can be appended
     */
    @Override
    public ItemsStep addItem(InvoiceItem item) {
        this.items.add(Objects.requireNonNull(item,
                "InvoiceItem cannot be null"));
        return this;
    }

    /**
     * Finishes the mandatory item section of the builder flow.
     *
     * <p>At least one item must have been registered before the builder can advance
     * to optional additional information or final invoice creation.
     *
     * @return step that allows either adding optional additional information or building
     *         the invoice directly
     * @throws IllegalStateException if no items have been added
     */
    @Override
    public FirstAdditionalInfoStep doneItems() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Invoice must contain at least one item");
        }
        return this;
    }

    /**
     * Creates an immutable {@link Invoice} and calculates its totals from the added items.
     *
     * <p>The build step derives {@link Totals} automatically, preserving the builder
     * contract that invoice totals are consistent with the registered detail lines.
     *
     * @return invoice entity populated with mandatory data, derived totals, items, and
     *         optional additional information
     * @throws IllegalStateException if no items have been added
     */
    @Override
    public Invoice build() {

        if (items.isEmpty()) {
            throw new IllegalStateException("Invoice must contain at least one item");
        }

        Totals totals = Totals.from(items);

        return new Invoice(
                issueDate,
                establishmentDirection,
                taxInfo,
                documentNumber,
                client,
                totals,
                List.copyOf(items),
                List.copyOf(additionalInfos)
        );
    }

    /**
     * Finishes the optional additional information section of the builder flow.
     *
     * @return final step that can build the invoice
     */
    @Override
    public BuildStep doneAdditionalInfo() {
        return this;
    }

    /**
     * Adds an optional additional information field to the invoice.
     *
     * @param info additional invoice field that will be serialized under {@code infoAdicional}
     * @return same step so more additional fields can be appended
     */
    @Override
    public AdditionalInfoStep addInfo(AdditionalInfo info) {
        this.additionalInfos.add(Objects.requireNonNull(info,
                "AdditionalInfo is required"));
        return this;
    }
}
