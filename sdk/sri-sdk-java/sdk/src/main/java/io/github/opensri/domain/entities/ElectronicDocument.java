package io.github.opensri.domain.entities;

import io.github.opensri.domain.entities.invoice.InvoiceItem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public record ElectronicDocument(
        String authorizationStatus,
        String authorizationAccessKey,
        Date authorizationDate,
        String environment,
        // Clave de acceso del SRI
        String documentAccessKey,
        // Numero del comprobante (001-001-000000001)
        String documentNumber,
        String documentNumberRef,
        Date issueDateRed,
        Date accountingRecordDate,
        Date supportingDocumentIssueDate,
        // Tabla 3
        String reason,
        Date issueDate,
        String documentTypeId,
        String documentTypeDescription,
        //-------------------------------------------
        // Cliente
        //-------------------------------------------
        String clientIdentification,
        String clientNames,
        //-------------------------------------------
        // Proveedor (emisor)
        //-------------------------------------------
        String providerIdentification,
        String providerNames,

        //-------------------------------------------
        // Valores generales
        //-------------------------------------------

        // Valor total final del comprobante
        BigDecimal totalValue,
        // Base imponible gravada con impuestos
        BigDecimal totalTaxableValue,
        // Valor total de impuestos
        BigDecimal totalTaxValue,
        // Valor exento de impuestos
        BigDecimal totalExemptValue,
        // Descuento aplicado al subtotal antes de impuestos
        BigDecimal totalDiscount,
        // Valor de propina (Depende de las reglas del negocio)
        BigDecimal totalTipValue,
        // Valor total retenido (IVA + RENTA)
        BigDecimal totalWithholdingValue,
        List<TotalTaxes> totalTaxes,
        // Detalle de retencion (si aplica)
        List<Retention> retentionDetails,
        // Lista de items facturados
        List<InvoiceItem> invoiceItems
) {
}
