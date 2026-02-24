package io.github.opensri.domain.enums;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Enum del tipo de la tarifa del iva que se va a usar, con base en la tabla de
 * la documentación del SRI Nro. 17
 */
public enum TaxRate {
    PORCENTAJE_0(0, "0%", BigDecimal.ZERO),
    PORCENTAJE_12(2, "12%", BigDecimal.valueOf(12f)),
    PORCENTAJE_14(3, "14%", BigDecimal.valueOf(14f)),
    PORCENTAJE_15(4, "15%", BigDecimal.valueOf(15f)),
    PORCENTAJE_5(5, "5%", BigDecimal.valueOf(5f)),
    NO_OBJETO_IMPUESTO(6, "No objeto de Impuesto", null),
    EXENTO_IVA(7, "Exento de IVA", null),
    IVA_DIFERENCIADO(8, "IVA diferenciado", null),
    PORCENTAJE_13(10, "13%", BigDecimal.valueOf(13f));

    private final int code;
    private final String description;
    private final BigDecimal value;

    public int getCode() {return code;}

    public String getDescription() {
        return description;
    }

    public BigDecimal getValue() {return value;}

    TaxRate(int code, String description, BigDecimal value) {
        this.code = code;
        this.description = description;
        this.value = value;
    }

    public static TaxRate fromCode(int code) {
        return Arrays.stream(values()).filter(
                        v-> v.code == code
                ).findFirst()
                .orElseThrow(
                        ()-> new IllegalArgumentException(
                                "No such TaxRateEnum code " + code)
                );
    }
}
