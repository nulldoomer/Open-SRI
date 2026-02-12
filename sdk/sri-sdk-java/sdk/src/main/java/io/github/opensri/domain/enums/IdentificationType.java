package io.github.opensri.domain.enums;

import java.util.Arrays;

/**
 * Enum del tipo de documento de identificación del cliente, con base en la
 * tabla de la documentación del SRI Nro. 6
 */
public enum IdentificationType {
    RUC(4,"RUC"),
    CEDULA(5, "CÉDULA"),
    PASAPORTE(6,"PASAPORTE"),
    VENTA_CONSUMIDOR_FINAL(7,"VENTA CONSUMIDOR FINAL"),
    IDENTIFICATION_DEL_EXTERIOR(8,"IDENTIFICATION DEL " +
            "EXTERIOR");

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    private int code;
    private String description;

    IdentificationType(int code, String description){
        this.code = code;
        this.description = description;
    }

    public static IdentificationType fromCode(int code){
        return Arrays.stream(values()).filter(
                v-> v.code == code
        ).findFirst()
                .orElseThrow(
                ()-> new IllegalArgumentException(
                        "No such IdentificationTypeEnum code " + code)
        );
    }
}
