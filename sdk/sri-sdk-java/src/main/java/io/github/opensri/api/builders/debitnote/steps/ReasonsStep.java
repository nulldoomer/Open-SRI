// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.debitnote.steps;

import io.github.opensri.domain.entities.common.Compensation;
import io.github.opensri.domain.entities.common.payment.Payment;
import io.github.opensri.domain.entities.debitnote.DebitNoteReason;
import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import java.util.List;

/**
 * Paso del constructor para configurar los motivos, compensaciones, pagos e información adicional
 * de la nota de débito, y construir el documento.
 */
public interface ReasonsStep extends BuildStep {
  /**
   * Agrega los motivos de la nota de débito.
   *
   * @param reasons lista de motivos (razón y valor)
   * @return esta misma instancia para seguir configurando el documento
   */
  ReasonsStep addReasons(List<DebitNoteReason> reasons);

  /**
   * Agrega compensaciones a la nota de débito.
   *
   * @param compensations lista de compensaciones aplicadas
   * @return esta misma instancia para seguir configurando el documento
   */
  ReasonsStep addCompensations(List<Compensation> compensations);

  /**
   * Agrega las formas de pago de la nota de débito.
   *
   * @param payments lista de métodos de pago
   * @return esta misma instancia para seguir configurando el documento
   */
  ReasonsStep addPayments(List<Payment> payments);

  /**
   * Agrega información adicional personalizada a la nota de débito.
   *
   * @param infos lista de información adicional (clave-valor)
   * @return esta misma instancia para seguir configurando el documento
   */
  ReasonsStep addInfos(List<AdditionalInfo> infos);
}
