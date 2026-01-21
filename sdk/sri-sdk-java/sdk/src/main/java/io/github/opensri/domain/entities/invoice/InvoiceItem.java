package io.github.opensri.domain.entities.invoice;


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
        List<Tax> taxes

) {
}

