package io.github.opensri.infrastructure.services;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.TaxInfo;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.domain.valueobjects.IssueDate;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class SRIAccessKeyGenerator implements AccessKeyGenerator {
    @Override
    public String generate(IssueDate date, DocumentNumber documentNumber,
                           TaxInfo taxInfo, Environment environment) {

        // Separación de campos que conforman la clave de acceso del SRI

        String issueDate = date.format().replace("-","");

        // Tipo de comprobante | Tabla Nro. 3 SRI
        String documentCode = documentNumber.documentCode();
        String rucNumber = taxInfo.issuer().ruc().number();
        String environmentType = String.valueOf(environment.getCode());
        String serie = documentNumber.establishment() + documentNumber.emissionPoint();
        String sequential = documentNumber.sequentialNumber();

        /*
         Código Numérico random, creado por el SDK (Algoritmo para crear un
         número de 8 dígitos aleatorios, con baja probabilidad de similitud)
         */
        String codeNumber = generateNumberCode();
        String emissionType = String.valueOf(taxInfo.emissionType());

        String keyWithoutDigit = issueDate + documentCode + rucNumber +
                environmentType + serie + sequential + codeNumber + emissionType;

        int digito = modulo11(keyWithoutDigit);

        return keyWithoutDigit + digito;
    }

    /**
     *
     * @return codeNumber
     */
    private String generateNumberCode(){

        // Últimos 5 dígitos del timestamp del sistema
        String timeRandom = String.valueOf(System.currentTimeMillis());
        timeRandom = timeRandom.substring(timeRandom.length() - 5);

        // Generación de un número random de 3 dígitos
        int normalRandom = ThreadLocalRandom.current().nextInt(1000);
        String normalRandomString = String.format("%03d", normalRandom);

        // Combinación final del codeNumber
        return timeRandom + normalRandomString;
    }

    private int modulo11(String keyWithoutDigit){
        int sum = 0;
        int factor = 7;

        String[] digits = keyWithoutDigit.split("");

        for( String digit : digits){
            sum += Integer.parseInt(digit) * factor;

            factor = factor -1;

            if(factor == 1)
                factor = 7;
        }

        int verifierDigit = sum % 11;

        verifierDigit = 11- verifierDigit;

        if(verifierDigit == 11)
            verifierDigit = 0;

        if(verifierDigit == 10)
            verifierDigit = 1;

        return verifierDigit;
    }
}