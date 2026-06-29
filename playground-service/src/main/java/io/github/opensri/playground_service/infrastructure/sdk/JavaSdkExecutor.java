// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.sdk;

import io.github.opensri.playground_service.domain.model.SdkLanguage;
import io.github.opensri.playground_service.domain.port.SdkExecutor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JavaSdkExecutor implements SdkExecutor {

  @Override
  public SdkLanguage supports() {
    return SdkLanguage.JAVA;
  }

  @Override
  public Mono<SdkExecutionResult> execute(SdkExecutionRequest request) {
    return Mono.fromCallable(this::executeSync).onErrorResume(this::handleError);
  }

  private SdkExecutionResult executeSync() {
    long startTime = System.currentTimeMillis();
    List<String> logs = new ArrayList<>();

    logs.add("Iniciando ejecución del SDK Java...");
    logs.add("Versión SDK: " + "1.2.4");
    logs.add("Lenguaje: JAVA");

    try {
      logs.add("Validando payload del comprobante...");

      logs.add("Creando cliente OpenSRI...");

      logs.add("Ejecutando solicitud SOAP...");

      String responsePayload = "<sri-response>Simulado por ahora</sri-response>";

      logs.add("Respuesta recibida.");

      long executionTime = System.currentTimeMillis() - startTime;

      return new SdkExecutionResult(responsePayload, logs, executionTime, null, true);
    } catch (Exception e) {
      long executionTime = System.currentTimeMillis() - startTime;
      logs.add("Error durante la ejecución: " + e.getMessage());

      return new SdkExecutionResult(null, logs, executionTime, e.getMessage(), false);
    }
  }

  private Mono<SdkExecutionResult> handleError(Throwable error) {
    List<String> logs = new ArrayList<>();
    logs.add("Error en ejecución del SDK: " + error.getMessage());

    long executionTime = System.currentTimeMillis();

    return Mono.just(new SdkExecutionResult(null, logs, executionTime, error.getMessage(), false));
  }
}
