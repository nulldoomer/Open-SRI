// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.responses;

/**
 * Representa el resultado del proceso de envío de un comprobante electrónico al SRI.
 *
 * <p>Contiene la clave de acceso generada, el contenido XML firmado que fue enviado y la respuesta
 * oficial de recepción obtenida del servicio SOAP del SRI. Es común a todos los tipos de documento
 * (factura, nota de crédito, nota de débito, liquidación de compra, guía de remisión y comprobante
 * de retención).
 *
 * @param accessKey clave de acceso generada para el comprobante
 * @param signedXml contenido del comprobante en formato XML firmado
 * @param response respuesta de recepción devuelta por el SRI
 */
public record SendDocumentResult(String accessKey, String signedXml, ReceiptResponse response) {}
