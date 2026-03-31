package io.github.opensri.domain.entities.invoice;

import io.github.opensri.domain.entities.common.Client;
import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.Totals;
import io.github.opensri.domain.entities.common.TaxInfo;
import io.github.opensri.domain.valueobjects.IssueDate;

import java.util.List;

/**
 * Representa una factura electrónica completa dentro del dominio del SDK.
 *
 * <p>Reúne la información fiscal del emisor, los datos del comprador, la
 * numeración tributaria, los detalles de productos o servicios y los valores
 * totales necesarios para construir el comprobante electrónico.
 *
 * <p>También admite información adicional opcional de nivel factura que luego
 * puede reflejarse en la sección {@code infoAdicional} del XML del SRI.
 */
public record Invoice(

        IssueDate issueDate,
        String establishmentDirection,
        TaxInfo taxInfo,
        DocumentNumber documentNumber,
        Client client,
        Totals totals,
        List <InvoiceItem> items,
        List<AdditionalInfo> additionalInfo
) {

    public Invoice {
        items = items == null ? List.of():List.copyOf(items);
        additionalInfo = additionalInfo == null ? List.of():List.copyOf(additionalInfo);
    }

}
