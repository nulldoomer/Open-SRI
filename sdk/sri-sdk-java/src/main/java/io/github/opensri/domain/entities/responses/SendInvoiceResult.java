// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.responses;

/**
 * Representa el resultado del proceso de envío de una factura al SRI.
 *
 * <p>Contiene la clave de acceso generada, el contenido XML firmado que fue enviado y la respuesta
 * oficial de recepción obtenida del servicio SOAP del SRI.
 *
 * @param accessKey clave de acceso generada para el comprobante
 * @param signedXml contenido del comprobante en formato XML firmado
 * @param response respuesta de recepción devuelta por el SRI
 */
public record SendInvoiceResult(String accessKey, String signedXml, ReceiptResponse response) {}
