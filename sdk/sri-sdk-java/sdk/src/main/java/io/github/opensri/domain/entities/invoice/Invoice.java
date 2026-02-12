package io.github.opensri.domain.entities.invoice;

import io.github.opensri.domain.entities.common.Client;
import io.github.opensri.domain.entities.common.Issuer;
import io.github.opensri.domain.entities.common.Totals;

import java.util.Date;
import java.util.List;

public record Invoice(

        String documentNumber,
        Date issueDate,
        Issuer issuer,
        Client client,
        Totals totals,
        List <InvoiceItem> items
) {
    public Invoice{
        items = items == null ? List.of():List.copyOf(items);
    }
}
