package io.github.opensri.domain.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * Enum del tipo de pago del cliente, con base en la tabla de la documentación
 * del SRI Nro. 24, actualmente no se encuentra en la documentación oficial,
 * se encuentra aparte.
 */
@Getter
public enum PaymentMethod {
    SIN_SISTEMA_FINANCIERO(1,"Sin utilización del sistema" +
            " financiero"),
    COMPENSACION_DE_DEUDAS(15,"Compensacion de deudas"),
    TARJETA_DE_DEBITO(16,"Tarjeta de debito"),
    DINERO_ELECTRONICO(17,"Dinero Electronico"),
    TARJETA_PREPAGO(18,"Tarjeta prepago"),
    TARJETA_DE_CREDITO(19,"Tarjeta de credito"),
    OTROS_CON_SISTEMA_FINANCIERO(20,"Otros con utilización" +
            "del sistema financiero"),
    ENDOSO_DE_TITULOS(21,"Endoso de titulos");

    private int code;
    private String description;

    PaymentMethod(int code, String description){
        this.code = code;
        this.description = description;
    }

    public static PaymentMethod fromCode(int code){
        return Arrays.stream(values()).filter(
                v->v.code == code
        ).findFirst().orElseThrow(
                ()-> new IllegalArgumentException(
                        "No such PaymentMethodEnum exists with code " + code
                )
        );
    }
}
