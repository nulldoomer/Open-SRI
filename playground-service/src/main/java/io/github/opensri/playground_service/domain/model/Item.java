// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.domain.model;

import java.math.BigDecimal;

public record Item(
    String mainCode, // codigoPrincipal
    String auxiliaryCode, // codigoAuxiliar
    String description, // descripcion
    Integer quantity, // cantidad
    BigDecimal price, // precioUnitario

    // Impuesto (opcional — si es null no se agrega Tax al ítem)
    String taxCode, // "2" para IVA
    String taxPercentageCode, // "2" para 15%
    BigDecimal taxRate // 15.00
    ) {}
