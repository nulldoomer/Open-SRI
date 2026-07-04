// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.sdk;

import io.github.nulldoomer.opensri.api.client.OpenSRIClient;
import io.github.nulldoomer.opensri.domain.entities.invoice.Invoice;
import io.github.nulldoomer.opensri.domain.entities.responses.SendDocumentResult;
import io.github.nulldoomer.opensri.shared.exceptions.OpenSRICommunicationException;
import io.github.nulldoomer.opensri.shared.exceptions.OpenSRIInfrastructureException;
import io.github.nulldoomer.opensri.shared.exceptions.OpenSRIValidationException;
import io.github.opensri.playground_service.domain.model.InvoicePayload;
import io.github.opensri.playground_service.domain.model.PayloadMessage;
import io.github.opensri.playground_service.domain.model.ResponsePayload;
import io.github.opensri.playground_service.domain.model.SdkLanguage;
import io.github.opensri.playground_service.domain.port.SdkExecutor;
import io.github.opensri.playground_service.infrastructure.sdk.dto.SdkExecutionRequest;
import io.github.opensri.playground_service.infrastructure.sdk.dto.SdkExecutionResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JavaSdkExecutor implements SdkExecutor {

  private final OpenSRIClientFactory clientFactory;
  private final InvoicePayloadMapper invoiceMapper;

  public JavaSdkExecutor(OpenSRIClientFactory clientFactory, InvoicePayloadMapper invoiceMapper) {
    this.clientFactory = clientFactory;
    this.invoiceMapper = invoiceMapper;
  }

  @Override
  public SdkLanguage supports() {
    return SdkLanguage.JAVA;
  }

  @Override
  public Mono<SdkExecutionResult> execute(SdkExecutionRequest request) {
    long startTime = System.currentTimeMillis();
    return Mono.fromCallable(() -> executeSync(request, startTime))
        .onErrorResume(error -> handleError(error, startTime));
  }

  private SdkExecutionResult executeSync(SdkExecutionRequest request, long startTime) {

    List<String> logs = new ArrayList<>();
    logs.add("[SDK] Iniciando ejecución del SDK Java OpenSRI...");
    logs.add("[SDK] Versión: " + request.sdkVersion());

    try {
      InvoicePayload payload = request.invoicePayload();
      OpenSRIClient client = clientFactory.create(payload.issuerRuc());
      Invoice invoice = invoiceMapper.toInvoice(payload);

      logs.add("[SDK] Enviando documento al SRI...");
      SendDocumentResult result = client.sendInvoice(invoice);
      logs.add("[SDK] Documento enviado, estado: " + result.response().status());

      long executionTime = System.currentTimeMillis() - startTime;

      ResponsePayload responsePayload =
          new ResponsePayload(
              result.accessKey(),
              result.response().status(),
              result.response().messages().stream()
                  .map(
                      message ->
                          new PayloadMessage(
                              message.identifier(),
                              message.message(),
                              message.AdditionalInfo(),
                              message.type()))
                  .toList());

      return new SdkExecutionResult(responsePayload, logs, executionTime, null, true);

    } catch (OpenSRIValidationException
        | OpenSRICommunicationException
        | OpenSRIInfrastructureException
        | IOException e) {
      logs.add("[SDK] " + errorLabel(e) + ": " + e.getMessage());
      return failedResult(logs, e, startTime);
    }
  }

  // ------------ Handle multi catching with a switch ----------------------
  private String errorLabel(Exception e) {

    return switch (e) {
      case OpenSRIValidationException ignored -> "Error de validación";
      case OpenSRICommunicationException ignored -> "Error de comunicación con SRI";
      case OpenSRIInfrastructureException ignored -> "Error de infraestructura";
      case IOException ignored -> "Error al cargar la firma";
      default -> "Error";
    };
  }

  private SdkExecutionResult failedResult(List<String> logs, Exception error, long startTime) {
    long executionTime = System.currentTimeMillis() - startTime;
    return new SdkExecutionResult(null, logs, executionTime, error.getMessage(), false);
  }

  private Mono<SdkExecutionResult> handleError(Throwable error, long startTime) {
    List<String> logs = new ArrayList<>();
    logs.add("[SDK] Error fatal en ejecución: " + error.getClass().getSimpleName());
    logs.add("[SDK] Mensaje: " + error.getMessage());
    long executionTime = System.currentTimeMillis() - startTime;
    return Mono.just(new SdkExecutionResult(null, logs, executionTime, error.getMessage(), false));
  }
}
