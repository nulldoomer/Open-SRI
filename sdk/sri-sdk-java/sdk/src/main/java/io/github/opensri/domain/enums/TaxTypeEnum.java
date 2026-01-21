package io.github.opensri.domain.enums;

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
