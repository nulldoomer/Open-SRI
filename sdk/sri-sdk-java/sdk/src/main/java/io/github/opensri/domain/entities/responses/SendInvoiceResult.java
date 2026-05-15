package io.github.opensri.domain.entities.responses;

public record SendInvoiceResult(
        String accessKey,
        String signedXml,
        ReceiptResponse response
) {
}
