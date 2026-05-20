// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.responses;

public record SendInvoiceResult(String accessKey, String signedXml, ReceiptResponse response) {}
