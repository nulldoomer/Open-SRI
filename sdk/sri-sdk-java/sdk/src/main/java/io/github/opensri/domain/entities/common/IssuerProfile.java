// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.common;

import io.github.opensri.domain.enums.AccountingObligation;
import io.github.opensri.domain.valueobjects.Ruc;
import io.github.opensri.domain.valueobjects.SpecialTaxPayer;

/**
 * Agrupa la configuración tributaria complementaria del emisor.
 *
 * <p>Este perfil concentra datos reutilizables que no forman parte directa de la numeración del
 * documento, pero sí afectan su contenido fiscal, como la obligación de llevar contabilidad y la
 * condición de contribuyente especial.
 *
 * @see AccountingObligation
 */
public record IssuerProfile(
    Ruc ruc, SpecialTaxPayer specialTaxPayer, AccountingObligation accountingObligation) {}
