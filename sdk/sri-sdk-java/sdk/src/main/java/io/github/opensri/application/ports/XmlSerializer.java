// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.application.ports;

import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.enums.Environment;

/**
 * Serializes a domain document into its XML representation.
 *
 * <p>This port abstracts the transformation of a document model into XML without exposing JAXB or
 * any other serialization technology to the application layer. Implementations are expected to
 * produce XML that matches the corresponding SRI schema and version for the selected environment.
 *
 * @param <T> type of document accepted by the serializer
 */
public interface XmlSerializer<T> {

  /**
   * Converts the provided document into XML.
   *
   * @param document document instance to serialize
   * @param accessKey generated access key needed in the serialization
   * @param environment environment used to set on the XML
   * @param version document version to write in the XML root element
   * @param issuerProfile issuer profile information needed to complete the XML
   * @return XML representation of the given document
   */
  String serialize(
      T document,
      String accessKey,
      Environment environment,
      DocumentVersion version,
      IssuerProfile issuerProfile);
}
