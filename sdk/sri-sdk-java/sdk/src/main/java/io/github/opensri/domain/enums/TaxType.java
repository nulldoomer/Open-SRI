package io.github.opensri.domain.enums;

import lombok.Getter;

import java.util.Arrays;


/**
 * Enum del tipo de impuesto a retener, con base en la tabla de la
 * documentación del SRI Nro. 19
 */
@Getter
public enum TaxType {
    RENTA(1, "Renta"),
    IVA(2, "IVA"),
    ISD(6, "ISD");

    private final int code;
    private final String description;


    TaxType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static TaxType fromCode(int code) {
        return Arrays.stream(values()).filter(
                        v-> v.code == code
                ).findFirst()
                .orElseThrow(
                        ()-> new IllegalArgumentException(
                                "No such TaxTypeEnum code " + code)
                );
    }
}
