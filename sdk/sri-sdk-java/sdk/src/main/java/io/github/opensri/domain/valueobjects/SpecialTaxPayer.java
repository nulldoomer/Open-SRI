package io.github.opensri.domain.valueobjects;

/**
 * Represents the special taxpayer registration assigned by the tax authority.
 *
 * <p>This value object encapsulates the registration number used when an issuer
 * must declare the {@code contribuyenteEspecial} field in an electronic document.
 * It keeps the value explicit in the domain so infrastructure components can map it
 * directly into the corresponding XML element.
 */
public record SpecialTaxPayer(
        String number
) {
}
