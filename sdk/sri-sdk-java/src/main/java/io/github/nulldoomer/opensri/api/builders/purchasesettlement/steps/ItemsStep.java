// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.purchasesettlement.steps;

import io.github.nulldoomer.opensri.domain.entities.common.payment.Payment;
import io.github.nulldoomer.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.nulldoomer.opensri.domain.entities.invoice.InvoiceItem;
import io.github.nulldoomer.opensri.domain.enums.Currency;
import java.util.List;

/**
 * Paso del constructor para configurar los ítems, moneda, pagos e información adicional de la
 * liquidación de compra, y construir el documento.
 */
public interface ItemsStep extends BuildStep {
  /**
   * Agrega una lista de ítems o detalles a la liquidación de compra.
   *
   * @param items líneas de detalle de los bienes o servicios adquiridos
   * @return esta misma instancia para seguir configurando el documento
   */
  ItemsStep addItems(List<InvoiceItem> items);

  /**
   * Establece la moneda en la que se emite la liquidación de compra.
   *
   * @param currency la moneda del documento
   * @return esta misma instancia para seguir configurando el documento
   */
  ItemsStep addCurrency(Currency currency);

  /**
   * Agrega las formas de pago de la liquidación de compra.
   *
   * @param payments lista de métodos de pago
   * @return esta misma instancia para seguir configurando el documento
   */
  ItemsStep addPayments(List<Payment> payments);

  /**
   * Agrega información adicional personalizada a la liquidación de compra.
   *
   * @param infos lista de información adicional (clave-valor)
   * @return esta misma instancia para seguir configurando el documento
   */
  ItemsStep addInfos(List<AdditionalInfo> infos);
}
