package io.github.opensri.infrastructure.services;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.TaxInfo;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.domain.valueobjects.IssueDate;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates SRI access keys for electronic tax documents.
 *
 * <p>The access key is a 49-digit identifier required by the SRI to uniquely
 * recognize a document before reception and authorization. This implementation
 * builds the key from document metadata, issuer tax information, environment,
 * a generated numeric code, and the final modulo 11 verifier digit.
 *
 * <p>It implements {@link AccessKeyGenerator} and encapsulates the concrete
 * SRI-specific key generation algorithm used by the SDK.
 *
 * @see AccessKeyGenerator
 */
class SRIAccessKeyGenerator implements AccessKeyGenerator {
    /**
     * Generates the complete access key for a document.
     *
     * <p>The resulting key follows the SRI composition order:
     * issue date, document type, issuer RUC, environment, series, sequential number,
     * generated numeric code, emission type, and verifier digit.
     *
     * @param date issue date of the document
     * @param documentNumber document number containing type and sequence data
     * @param taxInfo issuer tax information used in the access key structure
     * @param environment target SRI environment
     * @return complete 49-digit access key including verifier digit
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
