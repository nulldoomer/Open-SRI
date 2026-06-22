// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.creditnote.steps;

import io.github.opensri.domain.entities.common.Compensation;
import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.enums.Currency;
import java.util.List;

/**
 * Paso del constructor para configurar los ítems, moneda, compensaciones e información adicional de
 * la nota de crédito, y construir el documento.
 */
public interface ItemsStep extends BuildStep {
  /**
   * Agrega una lista de ítems o detalles a la nota de crédito.
   *
   * @param items líneas de detalle de los bienes o servicios afectados
   * @return esta misma instancia para seguir configurando el documento
   */
  ItemsStep addItems(List<InvoiceItem> items);

  /**
   * Establece la moneda en la que se emite la nota de crédito.
   *
   * @param currency la moneda del documento
   * @return esta misma instancia para seguir configurando el documento
   */
  ItemsStep addCurrency(Currency currency);

  /**
   * Agrega compensaciones a la nota de crédito.
   *
   * @param compensations lista de compensaciones aplicadas
   * @return esta misma instancia para seguir configurando el documento
   */
  ItemsStep addCompensations(List<Compensation> compensations);

  /**
   * Agrega información adicional personalizada a la nota de crédito.
   *
   * @param infos lista de información adicional (clave-valor)
   * @return esta misma instancia para seguir configurando el documento
   */
  ItemsStep addInfos(List<AdditionalInfo> infos);
}
