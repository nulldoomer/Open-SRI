// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.serializers;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.infrastructure.models.FacturaXML;

/**
 * Serializes invoice XML models for a specific SRI environment.
 *
 * <p>This infrastructure component is responsible for turning the intermediate {@link FacturaXML}
 * representation into the final XML payload expected by the SRI. It also collaborates with {@link
 * AccessKeyGenerator} so the generated document can include environment-dependent identification
 * data when the serialization flow is completed.
 *
 * <p>It implements {@link XmlSerializer}{@code <FacturaXML>}.
 */
class InvoiceXmlSerializer implements XmlSerializer<FacturaXML> {

  final AccessKeyGenerator accessKeyGenerator;
  final Environment environment;

  InvoiceXmlSerializer(AccessKeyGenerator accessKeyGenerator, Environment environment) {
    this.accessKeyGenerator = accessKeyGenerator;
    this.environment = environment;
  }

  /**
   * Produces the XML representation of the given invoice model.
   *
   * <p>The serializer is expected to transform the JAXB-compatible invoice structure into the final
   * XML string required by the SRI invoice schema.
   *
   * @param document invoice XML model ready to be marshalled
   * @return serialized XML document
   */
  @Override
  public String serialize(FacturaXML document) {
    return "";
  }
}
