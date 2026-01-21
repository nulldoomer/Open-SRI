package io.github.opensri.domain.enums;

import lombok.Getter;


/**
 * Enum del tipo de impuesto a retener, con base en la tabla de la
 * documentación del SRI Nro. 19
 */
@Getter
public enum TaxTypeEnum {
    RENTA("1", "Renta"),
    IVA("2", "IVA"),
    ISD("6", "ISD");

    private final String code;
    private final String description;


    TaxTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static TaxTypeEnum findByCode(String code) {
        for (TaxTypeEnum taxTypeEnum: TaxTypeEnum.values()) {
            if(taxTypeEnum.code.equals(code)) {
                return taxTypeEnum;
            }
        }
        return null;
    }
}
