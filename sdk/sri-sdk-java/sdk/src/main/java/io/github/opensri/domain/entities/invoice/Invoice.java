package io.github.opensri.domain.entities.invoice;

import io.github.opensri.domain.entities.common.Client;
import io.github.opensri.domain.entities.common.Totals;
import io.github.opensri.domain.entities.taxinfo.TaxInfo;

import java.time.LocalDate;
import java.util.List;

public record Invoice(

        LocalDate issueDate,
        TaxInfo taxInfo,
        String documentNumber,
        Client client,
        Totals totals,
        List <InvoiceItem> items
) {
    public Invoice{
        items = items == null ? List.of():List.copyOf(items);
    }
}
