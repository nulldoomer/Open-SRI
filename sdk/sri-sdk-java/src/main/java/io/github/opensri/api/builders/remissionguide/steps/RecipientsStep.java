// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.remissionguide.steps;

import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.entities.remissionguide.Recipient;
import java.util.List;

/**
 * Paso del constructor para configurar los destinatarios, la dirección del establecimiento y la
 * información adicional de la guía de remisión, y construir el documento.
 */
public interface RecipientsStep extends BuildStep {
  /**
   * Agrega los destinatarios de la guía de remisión.
   *
   * @param recipients destinatarios de los bienes trasladados
   * @return esta misma instancia para seguir configurando el documento
   */
  RecipientsStep addRecipients(List<Recipient> recipients);

  /**
   * Define la dirección del establecimiento emisor.
   *
   * @param establishmentDirection dirección del establecimiento
   * @return esta misma instancia para seguir configurando el documento
   */
  RecipientsStep establishmentDirection(String establishmentDirection);

  /**
   * Agrega información adicional personalizada a la guía de remisión.
   *
   * @param infos lista de información adicional (clave-valor)
   * @return esta misma instancia para seguir configurando el documento
   */
  RecipientsStep addInfos(List<AdditionalInfo> infos);
}
