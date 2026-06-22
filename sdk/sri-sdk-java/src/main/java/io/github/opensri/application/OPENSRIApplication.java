// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.application;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.common.ElectronicDocument;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.responses.AuthorizationResponse;
import io.github.opensri.domain.entities.responses.SendDocumentResult;
import io.github.opensri.domain.enums.Environment;

/**
 * Facade class that orchestrates the application layer of the Open-SRI SDK.
 *
 * <p>This class acts as a coordination point for various use cases, delegating the execution of
 * specific business processes to specialized use case handlers. Document emission is handled
 * generically: any {@link ElectronicDocument} can be sent by providing its matching {@link
 * XmlSerializer}.
 */
public final class OPENSRIApplication {

  private final SendDocumentUseCase sendDocumentUseCase;
  private final CheckAuthorizationUseCase authorizationUseCase;

  /**
   * Initializes the application facade with the necessary ports.
   *
   * @param accessKeyGenerator port for generating SRI access keys
   * @param documentSigner port for digitally signing XML documents
   * @param sriGateway port for communicating with SRI web services
   */
  public OPENSRIApplication(
      AccessKeyGenerator accessKeyGenerator, DocumentSigner documentSigner, SRIGateway sriGateway) {

    this.sendDocumentUseCase =
        new SendDocumentUseCase(accessKeyGenerator, documentSigner, sriGateway);
    this.authorizationUseCase = new CheckAuthorizationUseCase(sriGateway);
  }

  /**
   * Executes the process of preparing, signing, and sending an electronic document to the SRI.
   *
   * @param document the electronic document to be processed
   * @param serializer the serializer matching the document type
   * @param environment the target SRI environment
   * @param profile the issuer's tax profile
   * @param <T> concrete electronic document type
   * @return the result of the document submission
   */
  public <T extends ElectronicDocument> SendDocumentResult send(
      T document, XmlSerializer<T> serializer, Environment environment, IssuerProfile profile) {

    return sendDocumentUseCase.execute(document, serializer, environment, profile);
  }

  /**
   * Executes the process of checking the authorization status of a document.
   *
   * @param accessKey the 49-digit access key of the document
   * @return the authorization response from the SRI
   */
  public AuthorizationResponse checkAuthorization(String accessKey) {

    return authorizationUseCase.execute(accessKey);
  }
}
