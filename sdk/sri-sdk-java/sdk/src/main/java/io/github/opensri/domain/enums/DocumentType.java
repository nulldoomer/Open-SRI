package io.github.opensri.domain.enums;

import java.util.Arrays;

/**
 * Enum del tipo de documento a emitir, con base en la tabla de la documentación
 * del SRI Nro. 3
 */
public enum DocumentType {
    FACTURA("01", "Factura"),
    LIQUIDACION_COMPRA("03", "Liquidación de Compra de Bienes" +
            " y Prestación de Servicios"),
    NOTA_CREDITO("04", "Nota de Crédito"),
    NOTA_DEBITO("05", "Nota de Débito"),
    GUIA_REMISION("06", "Guía de Remisión"),
    COMPROBANTE_RETENCION("07", "Comprobante de Retención");

    private String code;
    private String description;

    public String getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }

    DocumentType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DocumentType fromCode(String code) {
        return Arrays.stream(values()).filter(
                        v-> v.code.equals(code)
                ).findFirst()
                .orElseThrow(
                        ()-> new IllegalArgumentException(
                                "No such DocumentTypeEnum code " + code)
                );
    }}
