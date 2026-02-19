package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.common.Client;
import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.Totals;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.entities.taxinfo.TaxInfo;
import io.github.opensri.domain.valueobjects.IssueDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Steps implements
        IssueDateStep,
        EstablishmentDirectionStep,
        TaxInfoStep,
        DocumentNumberStep,
        ClientStep,
        TotalsStep,
        FirstItemStep,
        ItemsStep,
        BuildStep {

    private IssueDate issueDate;
    private String establishmentDirection;
    private TaxInfo taxInfo;
    private DocumentNumber documentNumber;
    private Client client;
    private Totals totals;
    private final List<InvoiceItem> items = new ArrayList<>();

    @Override
    public EstablishmentDirectionStep issueDate(IssueDate issueDate) {
        this.issueDate = Objects.requireNonNull(issueDate,
                "IssueDate is required");
        return this;
    }

    @Override
    public TaxInfoStep establishmentDirection(String establishmentDirection) {
        this.establishmentDirection =
                Objects.requireNonNull(establishmentDirection,
                        "Establishment direction is required");
        return this;
    }

    @Override
    public DocumentNumberStep taxInfo(TaxInfo taxInfo) {
        this.taxInfo = Objects.requireNonNull(taxInfo,
                "TaxInfo is required");
        return this;
    }

    @Override
    public ClientStep documentNumber(DocumentNumber documentNumber) {
        this.documentNumber =
                Objects.requireNonNull(documentNumber,
                        "DocumentNumber is required");
        return this;
    }

    @Override
    public TotalsStep client(Client client) {
        this.client = Objects.requireNonNull(client,
                "Client is required");
        return this;
    }

    @Override
    public FirstItemStep totals(Totals totals) {
        this.totals = Objects.requireNonNull(totals,
                "Totals are required");
        return this;
    }

    @Override
    public ItemsStep addItem(InvoiceItem item) {
        this.items.add(Objects.requireNonNull(item,
                "InvoiceItem cannot be null"));
        return this;
    }

    @Override
    public BuildStep doneItems() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Invoice must contain at least one item");
        }
        return this;
    }

    @Override
    public Invoice build() {

        if (items.isEmpty()) {
            throw new IllegalStateException("Invoice must contain at least one item");
        }

        return new Invoice(
                issueDate,
                establishmentDirection,
                taxInfo,
                documentNumber,
                client,
                totals,
                List.copyOf(items)
        );
    }
}
