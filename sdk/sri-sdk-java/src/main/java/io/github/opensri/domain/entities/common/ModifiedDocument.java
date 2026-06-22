// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.common;

import io.github.opensri.domain.valueobjects.IssueDate;

/**
 * Referencia al comprobante original que un documento modifica.
 *
 * <p>Las notas de crédito y de débito siempre afectan a un comprobante previamente emitido. Esta
 * estructura agrupa los datos que el SRI exige para identificarlo: el código del tipo de documento
 * modificado, su número y la fecha en que fue emitido.
 *
 * @param documentCode código del tipo de documento modificado (ej. "01" para factura)
 * @param number número del documento modificado en formato {@code 000-000-000000000}
 * @param issueDate fecha de emisión del documento modificado
 */
public record ModifiedDocument(String documentCode, String number, IssueDate issueDate) {}
