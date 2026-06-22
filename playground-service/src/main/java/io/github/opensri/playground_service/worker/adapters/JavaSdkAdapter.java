// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.worker.adapters;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.responses.Authorization;
import io.github.opensri.domain.entities.responses.AuthorizationResponse;
import io.github.opensri.domain.entities.responses.ReceiptResponse;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.infrastructure.crypto.signing.XAdEsSignerFactory;
import io.github.opensri.infrastructure.serializers.InvoiceXmlSerializerFactory;
import io.github.opensri.infrastructure.services.SRIAccessKeyGeneratorFactory;
import io.github.opensri.infrastructure.sri.SRIGatewayFactory;
import io.github.opensri.playground_service.api.dto.PipelineResult;
import io.github.opensri.playground_service.api.dto.PlaygroundRunRequest;
import io.github.opensri.playground_service.api.dto.SdkEvent;
import io.github.opensri.playground_service.worker.SdkGateway;
import io.github.opensri.playground_service.worker.mapping.InvoiceMapper;
import java.util.Arrays;
import java.util.Base64;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Runs the SRI pipeline using the in-process Java SDK, emitting one event per step.
 *
 * <p>The SDK's high-level {@code OpenSRIClient.sendInvoice} performs the whole flow in a single
 * call and exposes no per-step hooks. To produce the granular trace the playground needs, this
 * adapter reconstructs the same sequence as {@code SendDocumentUseCase.execute} using the SDK's
 * public factories and ports, emitting a {@link SdkEvent} after each stage.
 *
 * <p>All SDK work (JAXB serialization, XAdES signing, SOAP calls) is blocking, so the whole
 * sequence runs on {@link Schedulers#boundedElastic()} to keep the Netty event loop free.
 */
@Component
public class JavaSdkAdapter implements SdkGateway {

  private static final int TIMEOUT_SECONDS = 30;

  private final InvoiceMapper invoiceMapper;
  private final AccessKeyGenerator accessKeyGenerator = SRIAccessKeyGeneratorFactory.create();
  private final XmlSerializer<Invoice> invoiceSerializer = InvoiceXmlSerializerFactory.create();

  /**
   * Creates the adapter.
   *
   * @param invoiceMapper maps the request payload to the SDK domain model
   */
  public JavaSdkAdapter(InvoiceMapper invoiceMapper) {
    this.invoiceMapper = invoiceMapper;
  }

  @Override
  public String language() {
    return "java";
  }

  @Override
  public Flux<SdkEvent> run(PlaygroundRunRequest request) {
    return Flux.<SdkEvent>create(sink -> runPipeline(request, sink))
        .subscribeOn(Schedulers.boundedElastic());
  }

  private void runPipeline(
      PlaygroundRunRequest request, reactor.core.publisher.FluxSink<SdkEvent> sink) {
    byte[] certificate = Base64.getDecoder().decode(request.certificate().p12Base64());
    try {
      Environment environment = invoiceMapper.toEnvironment(request.environment());

      Invoice invoice = invoiceMapper.toInvoice(request);
      sink.next(SdkEvent.step("build"));

      String accessKey =
          accessKeyGenerator.generate(
              invoice.issueDate(), invoice.documentNumber(), invoice.taxInfo(), environment);
      sink.next(SdkEvent.step("access_key", accessKey));

      String unsignedXml =
          invoiceSerializer.serialize(
              invoice, accessKey, environment, invoiceMapper.toIssuerProfile(request.issuer()));
      sink.next(SdkEvent.step("serialize"));
      sink.next(SdkEvent.xml("raw", unsignedXml));

      DocumentSigner signer =
          XAdEsSignerFactory.create(
              certificate, request.certificate().passphrase(), request.certificate().alias());
      String signedXml = signer.signDocument(unsignedXml);
      sink.next(SdkEvent.step("sign"));
      sink.next(SdkEvent.xml("signed", signedXml));

      SRIGateway gateway = SRIGatewayFactory.create(environment, TIMEOUT_SECONDS);
      ReceiptResponse receipt = gateway.sendDocument(signedXml);
      sink.next(SdkEvent.step("send", receipt.status()));

      AuthorizationResponse authorization = gateway.sendAuthorization(accessKey);
      Authorization detail = authorization.authorization();
      sink.next(SdkEvent.step("authorize", detail == null ? "SIN AUTORIZACION" : detail.status()));

      sink.next(SdkEvent.done(toResult(accessKey, signedXml, receipt, detail)));
      sink.complete();
    } catch (RuntimeException e) {
      sink.error(e);
    } finally {
      Arrays.fill(certificate, (byte) 0);
    }
  }

  private PipelineResult toResult(
      String accessKey, String signedXml, ReceiptResponse receipt, Authorization authorization) {
    if (authorization == null) {
      return new PipelineResult(accessKey, receipt.status(), null, null, null, signedXml, null);
    }
    return new PipelineResult(
        accessKey,
        receipt.status(),
        authorization.status(),
        authorization.authorizationNumber(),
        authorization.authorizationDate(),
        signedXml,
        authorization.authorizedXML());
  }
}
