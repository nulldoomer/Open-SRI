// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.domain.model;

import java.util.List;

public record InvoicePayload(
    // Emisor (para TaxInfo e IssuerProfile)
    String issuerRuc,
    String issuerName,
    String establishmentAddress,

    // Número de documento: DocumentNumber("01", "001", "001", "000000001")
    String codDoc,
    String estab,
    String ptoEmi,
    String secuencial,

    // Comprador (para Client)
    String buyerName,
    String buyerIdentification,
    String buyerIdentificationType,

    // Pago
    String paymentMethod, // PaymentMethod enum name, ej: "SIN_SISTEMA_FINANCIERO"

    // Versión XML
    String documentVersion, // DocumentVersion enum name, ej: "VERSION_100"

    // Ítems
    List<Item> items) {}
