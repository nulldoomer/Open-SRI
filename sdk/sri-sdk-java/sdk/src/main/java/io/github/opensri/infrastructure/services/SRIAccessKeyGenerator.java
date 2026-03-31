package io.github.opensri.infrastructure.services;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.TaxInfo;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.domain.valueobjects.IssueDate;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Genera la clave de acceso para documentos electrónicos del SRI.
 *
 * <p>La clave de acceso es un código de 49 dígitos que identifica unívocamente
 * cada documento autorizado. Se construye concatenando campos del documento
 * (fecha, tipo, RUC, ambiente, serie, secuencial) con un código numérico
 * aleatorio de 8 dígitos generado por el SDK.
 *
 * <p>El dígito verificador se calcula aplicando el algoritmo módulo 11
 * con factor inicial 7, según la especificación del SRI.
 *
 * <p>Implementa {@link AccessKeyGenerator}.
 *
 * @see AccessKeyGenerator
 */
class SRIAccessKeyGenerator implements AccessKeyGenerator {
    /**
     * Genera una clave de acceso de 49 dígitos para un documento electrónico.
     *
     * <p>La clave sigue el formato: fecha(8) + tipoDoc(2) + RUC(13) + ambiente(1)
     * + serie(4) + secuencial(9) + códigoAleatorio(8) + tipoEmisión(1) + dígitoVerificador(1)
     *
     * @param date fecha de emisión del documento
     * @param documentNumber número de documento con código, serie y secuencial
     * @param taxInfo información tributaria del emisor
     * @param environment ambiente del SRI (prueba o producción)
     * @return clave de acceso completa con dígito verificador
     */
    @Override
    public String generate(IssueDate date, DocumentNumber documentNumber,
                           TaxInfo taxInfo, Environment environment) {

        String issueDate = date.format().replace("/","");

        String documentCode = documentNumber.documentCode();
        String rucNumber = taxInfo.issuer().ruc().number();
        String environmentType = String.valueOf(environment.getCode());
        String serie = documentNumber.establishment() + documentNumber.emissionPoint();
        String sequential = documentNumber.sequentialNumber();

        String codeNumber = generateNumberCode();
        String emissionType = String.valueOf(taxInfo.emissionType());

        String keyWithoutDigit = issueDate + documentCode + rucNumber +
                environmentType + serie + sequential + codeNumber + emissionType;

        int digito = modulo11(keyWithoutDigit);

        return keyWithoutDigit + digito;
    }

    private String generateNumberCode(){
        String timeRandom = String.valueOf(System.currentTimeMillis());
        timeRandom = timeRandom.substring(timeRandom.length() - 5);

        int normalRandom = ThreadLocalRandom.current().nextInt(1000);
        String normalRandomString = String.format("%03d", normalRandom);

        return timeRandom + normalRandomString;
    }

    private int modulo11(String keyWithoutDigit){
        int sum = 0;
        int factor = 7;

        for (String digit : keyWithoutDigit.split("")) {
            sum += Integer.parseInt(digit) * factor;
            factor = (factor == 2) ? 7 : factor - 1;
        }

        int verifierDigit = 11 - (sum % 11);

        if (verifierDigit >= 10)
            verifierDigit = verifierDigit == 11 ? 0 : 1;

        return verifierDigit;
    }
}
