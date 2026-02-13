package io.github.opensri.domain.enums;

import java.util.Arrays;

/**
 * Enum del tipo de documento de identificación del cliente, con base en la
 * tabla de la documentación del SRI Nro. 6
 */
public enum IdentificationType {
    RUC("04","RUC"),
    CEDULA("05", "CÉDULA"),
    PASAPORTE("06","PASAPORTE"),
    VENTA_CONSUMIDOR_FINAL("07","VENTA CONSUMIDOR FINAL"),
    IDENTIFICATION_DEL_EXTERIOR("08","IDENTIFICATION DEL " +
            "EXTERIOR");

    private String code;
    private String description;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    IdentificationType(String code, String description){
        this.code = code;
        this.description = description;
    }

    public static IdentificationType fromCode(String code){
        return Arrays.stream(values()).filter(
                        v-> v.code.equals(code)
                ).findFirst()
                .orElseThrow(
                        ()-> new IllegalArgumentException(
                                "No such IdentificationTypeEnum code " + code)
                );
    }}
