// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.withholding.steps;

import io.github.nulldoomer.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.nulldoomer.opensri.domain.entities.withholding.SupportDocument;
import io.github.nulldoomer.opensri.domain.entities.withholding.WithholdingTax;
import java.util.List;

/**
 * Paso del constructor para configurar las retenciones y la información adicional del comprobante
 * de retención, y construir el documento.
 *
 * <p>Para la versión 1.0.0 se usan las retenciones planas ({@link #addWithholdings}); para la
 * versión 2.0.0 se usan los documentos sustento ({@link #addSupportDocuments}) junto con {@link
 * #relatedParty} y, opcionalmente, {@link #subjectType}.
 */
public interface WithholdingsStep extends BuildStep {
  /**
   * Agrega las retenciones practicadas (versión 1.0.0).
   *
   * @param withholdings retenciones a registrar
   * @return esta misma instancia para seguir configurando el documento
   */
  WithholdingsStep addWithholdings(List<WithholdingTax> withholdings);

  /**
   * Agrega los documentos sustento con sus retenciones (versión 2.0.0).
   *
   * @param supportDocuments documentos sustento a registrar
   * @return esta misma instancia para seguir configurando el documento
   */
  WithholdingsStep addSupportDocuments(List<SupportDocument> supportDocuments);

  /**
   * Indica si el sujeto retenido es parte relacionada (parteRel); requerido en versión 2.0.0.
   *
   * @param relatedParty valor {@code SI} o {@code NO}
   * @return esta misma instancia para seguir configurando el documento
   */
  WithholdingsStep relatedParty(String relatedParty);

  /**
   * Define el tipo de sujeto retenido (tipoSujetoRetenido); opcional en versión 2.0.0.
   *
   * @param subjectType código del tipo de sujeto retenido
   * @return esta misma instancia para seguir configurando el documento
   */
  WithholdingsStep subjectType(String subjectType);

  /**
   * Agrega información adicional personalizada al comprobante de retención.
   *
   * @param infos lista de información adicional (clave-valor)
   * @return esta misma instancia para seguir configurando el documento
   */
  WithholdingsStep addInfos(List<AdditionalInfo> infos);
}
