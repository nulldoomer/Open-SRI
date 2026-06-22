// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.services;

import io.github.nulldoomer.opensri.application.ports.AccessKeyGenerator;
import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementación de {@link AccessKeyGenerator} que genera claves de acceso para comprobantes
 * electrónicos del SRI.
 *
 * <p>La clave de acceso es un identificador de 49 dígitos requerido por el SRI para identificar de
 * forma única un comprobante. Esta implementación construye la clave a partir de los metadatos del
 * documento, información del emisor, ambiente, un código numérico aleatorio y el dígito verificador
 * obtenido mediante módulo 11.
 */
class SRIAccessKeyGenerator implements AccessKeyGenerator {
  /**
   * Genera la clave de acceso completa para un documento.
   *
   * <p>La clave resultante sigue la estructura definida por el SRI: fecha de emisión, tipo de
   * comprobante, RUC del emisor, tipo de ambiente, serie (establecimiento + punto de emisión),
   * número secuencial, código numérico, tipo de emisión y dígito verificador.
   *
   * @param date fecha de emisión del comprobante
   * @param documentNumber número del documento que contiene el establecimiento, punto de emisión y
   *     secuencial
   * @param taxInfo información tributaria del emisor utilizada en la estructura de la clave
   * @param environment ambiente del SRI al que se dirige el comprobante
   * @return clave de acceso completa de 49 dígitos incluyendo el dígito verificador
   */
  @Override
  public String generate(
      IssueDate date, DocumentNumber documentNumber, TaxInfo taxInfo, Environment environment) {

    String issueDate = date.format().replace("/", "");

    String documentCode = documentNumber.documentCode();
    String rucNumber = taxInfo.issuer().ruc().number();
    String environmentType = String.valueOf(environment.getCode());
    String serie = documentNumber.establishment() + documentNumber.emissionPoint();
    String sequential = documentNumber.sequentialNumber();

    String codeNumber = generateNumberCode();
    String emissionType = String.valueOf(taxInfo.emissionType());

    String keyWithoutDigit =
        issueDate
            + documentCode
            + rucNumber
            + environmentType
            + serie
            + sequential
            + codeNumber
            + emissionType;

    int digito = modulo11(keyWithoutDigit);

    return keyWithoutDigit + digito;
  }

  private String generateNumberCode() {
    String timeRandom = String.valueOf(System.currentTimeMillis());
    timeRandom = timeRandom.substring(timeRandom.length() - 5);

    int normalRandom = ThreadLocalRandom.current().nextInt(1000);
    String normalRandomString = String.format("%03d", normalRandom);

    return timeRandom + normalRandomString;
  }

  private int modulo11(String keyWithoutDigit) {
    int sum = 0;
    int factor = 7;

    for (String digit : keyWithoutDigit.split("")) {
      sum += Integer.parseInt(digit) * factor;
      factor = (factor == 2) ? 7 : factor - 1;
    }

    int verifierDigit = 11 - (sum % 11);

    if (verifierDigit >= 10) verifierDigit = verifierDigit == 11 ? 0 : 1;

    return verifierDigit;
  }
}
