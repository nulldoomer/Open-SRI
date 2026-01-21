package io.github.opensri.domain.enums;

import lombok.Getter;

/**
 * Enum del tipo de documento a emitir, con base en la tabla de la documentación
 * del SRI Nro. 3
 */
@Getter
public enum DocumentTypeEnum {
    FACTURA("01", "Factura"),
    LIQUIDACION_COMPRA("03", "Liquidación de Compra de Bienes" +
            " y Prestación de Servicios"),
    NOTA_CREDITO("04", "Nota de Crédito"),
    NOTA_DEBITO("05", "Nota de Débito"),
    GUIA_REMISION("06", "Guía de Remisión"),
    COMPROBANTE_RETENCION("07", "Comprobante de Retención");

    private String code;
    private String description;

    DocumentTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DocumentTypeEnum findByCode(String code) {
        for (DocumentTypeEnum documentTypeEnum : DocumentTypeEnum.values()) {
            if (documentTypeEnum.getCode().equals(code)) {
                return documentTypeEnum;
            }
        }
        return null;
    }
}
