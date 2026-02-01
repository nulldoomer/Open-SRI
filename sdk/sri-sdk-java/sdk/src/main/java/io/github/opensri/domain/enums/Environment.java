package io.github.opensri.domain.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * Enum del ambiente del web service del SRI, con base en la tabla de la
 * documentación del SRI Nro. 4
 */
@Getter
public enum Environment {
    PRUEBAS(1,"Ambiente de pruebas"),
    PRODUCCION(2,"Ambiente de produccion");

    private int code;
    private String description;

    Environment(int code, String description){
        this.code = code;
        this.description = description;
    }

    public static Environment fromCode(int code){
        return Arrays.stream(values()).filter(
                v-> v.code == code
        ).findFirst()
                .orElseThrow(()-> new IllegalArgumentException(
                        "No such EnvironmentEnum exists with code " + code)
                );
    }
}
