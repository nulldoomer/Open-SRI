package io.github.opensri.domain.enums;

import java.util.Arrays;


/**
 * Representa los tipos de impuesto manejados por el SDK para procesos tributarios.
 *
 * <p>Este catálogo corresponde a la tabla Nro. 19 de la documentación del SRI
 * y define los códigos oficiales utilizados para identificar cada clase de impuesto.
 */
public enum TaxType {
    RENTA(1, "Renta"),
    IVA(2, "IVA"),
    ISD(6, "ISD");

    private final int code;
    private final String description;

    /**
     * Obtiene el código oficial del tipo de impuesto según la tabla Nro. 19 del SRI.
     *
     * @return código tributario del impuesto
     */
    public int getCode() {
        return code;
    }

    /**
     * Obtiene la descripción legible del tipo de impuesto.
     *
     * @return nombre funcional del impuesto
     */
    public String getDescription() {
        return description;
    }

    TaxType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Resuelve el tipo de impuesto correspondiente al código del SRI.
     *
     * @param code código del impuesto según la tabla Nro. 19 del SRI
     * @return tipo de impuesto asociado al código indicado
     * @throws IllegalArgumentException si el código no existe en el catálogo
     */
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
