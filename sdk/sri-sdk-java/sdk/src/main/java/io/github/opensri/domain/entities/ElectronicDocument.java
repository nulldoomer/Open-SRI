package io.github.opensri.domain.entities;

import io.github.opensri.domain.entities.invoice.InvoiceItem;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Template document for all the electronic documents that can be emitted by
 * the SRI
 * @param authorizationStatus
 * @param authorizationAccessKey
 * @param authorizationDate
 * @param environment
 * @param documentAccessKey
 * @param documentNumber
 * @param documentNumberRef
 * @param issueDateRef
 * @param accountingRecordDate
 * @param supportingDocumentIssueDate
 * @param reason
 * @param issueDate
 * @param documentTypeId
 * @param documentTypeDescription
 * @param clientIdentification
 * @param clientNames
 * @param providerIdentification
 * @param providerNames
 * @param totalValue
 * @param totalTaxableValue
 * @param totalTaxValue
 * @param totalExemptValue
 * @param totalDiscount
 * @param totalTipValue
 * @param totalWithholdingValue
 * @param totalTaxes
 * @param invoiceItems
 */
public record ElectronicDocument(
        // Estado de autorización del documento luego de recibir la respuesta
        String authorizationStatus,
        // AccessKey creada por el SRI para la revision de la autorización del doc.
        String authorizationAccessKey,
        // Fecha en la que el doc. fue autorizado
        Date authorizationDate,
        // Ambiente del Web Service del SRI
        int environment,
        // Clave de acceso del SRI
        String documentAccessKey,
        // Numero del comprobante (001-001-000000001)
        String documentNumber,
        /*
        ========================================================================
        ========================================================================
        TODO: Referencia a un documento original normalmente usado para notas de
             crédito, etc. Hacer la implementación con una relación luego del MVP
        ========================================================================
        ========================================================================
         */
        String documentNumberRef,
        Date issueDateRef,
        Date accountingRecordDate,
        Date supportingDocumentIssueDate,
        /*
        Razón de una nota de crédito, el motivo por el cual se esta haciendo.
        ========================================================================
        ========================================================================
         TODO: Implementar validación para que solo se cree con el builder de
               nota de crédito.
        ========================================================================
        ========================================================================
         */
        String reason,
        // Fecha de emisión del documento que se está creando.
        Date issueDate,
        int documentTypeId,
        String documentTypeDescription,
        // -------------------------------------------
        // Cliente
        // -------------------------------------------
        String clientIdentification,
        String clientNames,
        // -------------------------------------------
        // Proveedor (emisor)
        // -------------------------------------------
        String providerIdentification,
        String providerNames,

        // -------------------------------------------
        // Valores generales
        // -------------------------------------------

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

        // Detalle de retención (si aplica)

        /*
        ========================================================================
        ========================================================================
        TODO: Habilitar cuando aplique, luego de crear el MVP con la
              facturación electronica.
        ========================================================================
        ========================================================================
         */

        // List<Retention> retentionDetails,
        // Lista de items facturados
        List<InvoiceItem> invoiceItems
) {
}
