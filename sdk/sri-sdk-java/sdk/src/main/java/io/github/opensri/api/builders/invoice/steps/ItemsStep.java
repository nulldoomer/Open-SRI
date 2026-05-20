// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.common.payment.Payment;
import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.enums.Currency;
import java.util.List;

/**
 * Paso del builder para configurar los ítems, moneda, información adicional y formas de pago de la
 * factura.
 *
 * <p>Este paso permite agregar los detalles de los productos o servicios que se están facturando.
 */
public interface ItemsStep {
  /**
   * Agrega una lista de ítems o detalles a la factura.
   *
   * @param items lista de productos o servicios facturados
   * @return esta misma instancia para seguir configurando la factura
   */
  ItemsStep addItems(List<InvoiceItem> items);

  /**
   * Establece la moneda en la que se emite la factura.
   *
   * @param currency la moneda del documento (ej. DOLAR)
   * @return esta misma instancia para seguir configurando la factura
   */
  ItemsStep addCurrency(Currency currency);

  /**
   * Agrega información adicional personalizada a la factura.
   *
   * <p>Útil para incluir datos que no tienen un campo específico en el estándar del SRI, como
   * dirección, teléfono o notas especiales.
   *
   * @param infos lista de información adicional (clave-valor)
   * @return esta misma instancia para seguir configurando la factura
   */
  ItemsStep addInfos(List<AdditionalInfo> infos);

  /**
   * Agrega las formas de pago de la factura.
   *
   * @param payments lista de métodos de pago aplicados a la factura
   * @return el siguiente paso para finalizar la construcción de la factura
   */
  PaymentStep addPayments(List<Payment> payments);
}
