// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.purchasesettlement;

import io.github.nulldoomer.opensri.domain.valueobjects.ClientIdentification;

/**
 * Representa al proveedor registrado en una liquidación de compra.
 *
 * <p>En una liquidación de compra la contraparte es el proveedor de los bienes o servicios.
 * Reutiliza {@link ClientIdentification} para la identificación fiscal y añade la dirección
 * opcional del proveedor.
 *
 * @param identification identificación fiscal del proveedor (RUC, cédula, pasaporte, etc.)
 * @param socialReason nombre o razón social del proveedor
 * @param address dirección del proveedor; opcional
 */
public record Provider(ClientIdentification identification, String socialReason, String address) {}
