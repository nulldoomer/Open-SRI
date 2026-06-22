// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.valueobjects;

/**
 * Represents the special taxpayer registration assigned by the tax authority.
 *
 * <p>This value object encapsulates the registration number used when an issuer must declare the
 * {@code contribuyenteEspecial} field in an electronic document. It keeps the value explicit in the
 * domain so infrastructure components can map it directly into the corresponding XML element.
 *
 * @param number número de registro de contribuyente especial asignado por el SRI
 */
public record SpecialTaxPayer(String number) {}
