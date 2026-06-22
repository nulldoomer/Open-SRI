// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.crypto.certificates.model;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Bundles the private key and certificate used to sign XML documents.
 *
 * <p>This record is the immutable bridge between certificate loading and the XML signing
 * implementation. It keeps the signing material grouped as a single domain-neutral infrastructure
 * value.
 *
 * @param privateKey llave privada utilizada para realizar la firma digital
 * @param certificate certificado X.509 que acompaña a la llave privada
 */
public record SigningKey(PrivateKey privateKey, X509Certificate certificate) {}
