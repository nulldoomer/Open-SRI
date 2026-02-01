package io.github.opensri.domain.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * Enum del tipo de la tarifa del iva que se va a usar, con base en la tabla de
 * la documentación del SRI Nro. 17
 */
@Getter
public enum TaxRate {
    PORCENTAJE_0("0", "0%"),
    PORCENTAJE_12("2", "12%"),
    PORCENTAJE_14("3", "14%"),
    PORCENTAJE_15("4", "15%"),
    PORCENTAJE_5("5", "5%"),
    NO_OBJETO_IMPUESTO("6", "No objeto de Impuesto"),
    EXENTO_IVA("7", "Exento de IVA"),
    IVA_DIFERENCIADO("8", "IVA diferenciado"),
    PORCENTAJE_13("10", "13%");

    private final String code;
    private final String description;

    TaxRate(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static TaxRate fromCode(String code) {
        return Arrays.stream(values()).filter(
                        v-> v.code.equals(code)
                ).findFirst()
                .orElseThrow(
                        ()-> new IllegalArgumentException(
                                "No such TaxRateEnum code " + code)
                );
    }
}
