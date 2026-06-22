// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.common.taxes;

import io.github.nulldoomer.opensri.domain.entities.common.issuer.Issuer;

/**
 * Agrupa la información tributaria reutilizable del emisor.
 *
 * <p>Contiene los datos fiscales base que suelen repetirse entre documentos, como el tipo de
 * emisión, el emisor y la dirección matriz. Esta información sirve como insumo para generar claves
 * de acceso y estructuras XML del SRI.
 *
 * @param emissionType código del tipo de emisión (Normal o Indisponibilidad)
 * @param issuer información del remitente o emisor del documento electrónico
 * @param parentAddress dirección de la oficina matriz del negocio
 * @see Issuer
 */
public record TaxInfo(
    // Código del tipo de emisión | Tabla Nro. 2 | < tipoEmision >
    Integer emissionType,
    // Información del remitente del documento electrónico
    Issuer issuer,
    // Dirección de la Matriz del negocio
    String parentAddress) {}
