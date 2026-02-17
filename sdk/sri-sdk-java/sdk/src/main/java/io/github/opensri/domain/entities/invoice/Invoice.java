package io.github.opensri.domain.entities.invoice;

import io.github.opensri.domain.entities.common.Client;
import io.github.opensri.domain.entities.common.Totals;
import io.github.opensri.domain.entities.taxinfo.TaxInfo;
import io.github.opensri.domain.valueobjects.IssueDate;

import java.util.List;

/**
 * Entidad que contiene todos los datos fiscales necesarios para realizar la
 * facturación electronica
 * @param issueDate
 * @param establishmentDirection
 * @param taxInfo
 * @param documentNumber
 * @param client
 * @param totals
 * @param items
 */
public record Invoice(

        IssueDate issueDate,
        String establishmentDirection,
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
