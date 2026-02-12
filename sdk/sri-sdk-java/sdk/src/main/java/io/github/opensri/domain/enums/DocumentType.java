package io.github.opensri.domain.enums;

import java.util.Arrays;

/**
 * Enum del tipo de documento a emitir, con base en la tabla de la documentación
 * del SRI Nro. 3
 */
public enum DocumentType {
    FACTURA(1, "Factura"),
    LIQUIDACION_COMPRA(3, "Liquidación de Compra de Bienes" +
            " y Prestación de Servicios"),
    NOTA_CREDITO(4,"Nota de Crédito"),
    NOTA_DEBITO(5, "Nota de Débito"),
    GUIA_REMISION(6, "Guía de Remisión"),
    COMPROBANTE_RETENCION(7, "Comprobante de Retención");

    private int code;
    private String description;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    DocumentType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DocumentType fromCode(int code) {
        return Arrays.stream(values()).filter(
                        v-> v.code == code
                ).findFirst()
                .orElseThrow(
                        ()-> new IllegalArgumentException(
                                "No such DocumentTypeEnum code " + code)
                );
    }
}
