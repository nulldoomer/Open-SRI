package io.github.opensri.domain.entities.invoice;


import io.github.opensri.domain.entities.common.Tax;

import java.math.BigDecimal;
import java.util.List;

public record InvoiceItem(
        String mainCode,
        String auxCode,
        String description,
        String quantity,
        BigDecimal unitPrice,
        BigDecimal discount,
        BigDecimal totalPriceWithoutTax,
        List<AdditionalDetail> additionalDetails,
        /*
        Lista de impuestos a un producto, para cubrir casos en donde se cobre
        el IVA y el producto aplique el ICE
         */
        List<Tax> taxes

) {
    public InvoiceItem {
        additionalDetails = additionalDetails == null ? List.of(): List.copyOf(additionalDetails);
        taxes = taxes == null ? List.of(): List.copyOf(taxes);
    }
}

