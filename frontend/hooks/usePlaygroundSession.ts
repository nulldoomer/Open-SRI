import { useCallback, useEffect, useRef, useState } from "react";
import type {
  PipelineStepId,
  PipelineStepState,
  PlaygroundSessionRequest,
  TraceEvent,
  SriResponse as SriResponseType,
  StepStatus,
} from "@/types/playground";

type SessionStatus = "PENDING" | "RUNNING" | "COMPLETED" | "FAILED";

export interface ResponseMessage {
  identifier: string;
  message: string;
  additionalInformation?: string;
  additionalInfo?: string;
  type: string;
}

export interface ResponsePayload {
  accessKey: string;
  status: string;
  authorizationDate?: string;
  authorizedXml?: string;
  authorization?: {
    authorizationDate?: string;
    authorizedXML?: string;
  };
  messages: ResponseMessage[];
}

export interface SessionLog {
  timestamp: string;
  message: string;
}

export interface SessionResponse {
  id: string;
  status: SessionStatus;
  logs: SessionLog[];
  responsePayload: ResponsePayload | null;
}

const PIPELINE_ORDER: PipelineStepId[] = [
  "build",
  "access_key",
  "serialize",
  "sign",
  "send",
  "response",
];

const INITIAL_PIPELINE: PipelineStepState[] = PIPELINE_ORDER.map((id) => ({
  id,
  status: "idle",
}));

function parseResponsePayload(payload: ResponsePayload): SriResponseType {
  const messages = payload.messages ?? [];

  return {
    status: payload.status,
    accessKey: payload.accessKey,
    authorizationDate: payload.authorizationDate ?? payload.authorization?.authorizationDate,
    authorizedXml: payload.authorizedXml ?? payload.authorization?.authorizedXML,
    messages: messages.map((message) => ({
      identifier: message.identifier,
      message: message.message,
      additionalInfo: message.additionalInformation ?? message.additionalInfo,
      type: message.type === "ERROR" ? "ERROR" : "INFO",
    })),
  };
}

function inferTraceLevel(message: string): TraceEvent["level"] {
  const normalized = message.toLowerCase();

  if (normalized.includes("error") || normalized.includes("fall")) {
    return "ERROR";
  }

  if (normalized.includes("warn") || normalized.includes("alert")) {
    return "WARN";
  }

  if (normalized.includes("ok") || normalized.includes("correct") || normalized.includes("exito")) {
    return "OK";
  }

  return "INFO";
}

function getPipelineMatch(message: string): { id: PipelineStepId; detail?: string; final?: boolean } | null {
  const normalized = message.toLowerCase();

  if (
    normalized.includes("invoicebuilder") ||
    normalized.includes("construyendo factura") ||
    normalized.includes("factura construida")
  ) {
    return {
      id: "build",
      detail: normalized.includes("construida") ? "Factura construida" : "Construyendo factura",
      final: normalized.includes("construida"),
    };
  }

  if (normalized.includes("accesskey") || normalized.includes("clave de acceso")) {
    return {
      id: "access_key",
      detail: normalized.includes("generada") ? "Clave generada" : "Generando clave de acceso",
      final: normalized.includes("generada"),
    };
  }

  if (
    normalized.includes("xmlserializer") ||
    normalized.includes("serializando") ||
    normalized.includes("xml 2.1.0")
  ) {
    return {
      id: "serialize",
      detail: normalized.includes("xml 2.1.0") ? "XML serializado" : "Serializando XML",
      final: normalized.includes("xml 2.1.0"),
    };
  }

  if (normalized.includes("xades") || normalized.includes("firma") || normalized.includes("certificado p12")) {
    return {
      id: "sign",
      detail: normalized.includes("aplicada") ? "XML firmado" : "Firmando XML",
      final: normalized.includes("aplicada"),
    };
  }

  if (
    normalized.includes("soap") ||
    normalized.includes("srireceiptservice") ||
    normalized.includes("validarcomprobante")
  ) {
    return {
      id: "send",
      detail: normalized.includes("enviado") ? "SOAP enviado" : "Enviando SOAP",
      final: normalized.includes("enviado"),
    };
  }

  if (
    normalized.includes("sri respondió") ||
    normalized.includes("recibida") ||
    normalized.includes("devuelta") ||
    normalized.includes("autorizado") ||
    normalized.includes("no autorizado")
  ) {
    return {
      id: "response",
      detail: message,
      final: true,
    };
  }

  return null;
}

export function usePlaygroundSession() {
  const [traces, setTraces] = useState<TraceEvent[]>([]);
  const [status, setStatus] = useState<SessionStatus>("PENDING");
  const [sriResponse, setSriResponse] = useState<SriResponseType | null>(null);
  const [authorizedXml, setAuthorizedXml] = useState<string>("");
  const [pipelineSteps, setPipelineSteps] = useState<PipelineStepState[]>(INITIAL_PIPELINE);
  const [elapsedMs, setElapsedMs] = useState(0);
  const [isRunning, setIsRunning] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const eventSourceRef = useRef<EventSource | null>(null);
  const seenLogCount = useRef(0);
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const startTimeRef = useRef<number>(0);

  useEffect(() => {
    return () => {
      eventSourceRef.current?.close();
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, []);

  const addTrace = useCallback((level: TraceEvent["level"], message: string) => {
    const timestamp = new Date().toISOString();
    setTraces((prev) => [...prev, { timestamp, level, message }]);
  }, []);

  const stopTimer = useCallback(() => {
    if (timerRef.current) clearInterval(timerRef.current);
    timerRef.current = null;
    setElapsedMs(Date.now() - startTimeRef.current);
  }, []);

  const applyPipelineCompletion = useCallback((payload: ResponsePayload) => {
    const receiptOk = payload.status === "RECIBIDA" || payload.status === "AUTORIZADO";
    const finalStatus: StepStatus = receiptOk ? "ok" : "error";

    setPipelineSteps((prev) =>
      prev.map((step) => {
        if (step.id === "response") {
          return { ...step, status: finalStatus, detail: payload.status };
        }

        return step.status === "error" ? step : { ...step, status: "ok" };
      })
    );
  }, []);

  const applyPipelineUpdate = useCallback((message: string, level: TraceEvent["level"]) => {
    const match = getPipelineMatch(message);
    if (!match) {
      return;
    }

    setPipelineSteps((prev) => {
      const next = prev.map((step) => ({ ...step }));
      const index = next.findIndex((step) => step.id === match.id);
      if (index === -1) {
        return prev;
      }

      const status: StepStatus = level === "ERROR" ? "error" : match.final ? "ok" : "running";
      next[index].status = status;
      next[index].detail = match.detail;

      if (status === "ok") {
        for (let i = 0; i < index; i += 1) {
          if (next[i].status === "idle" || next[i].status === "running") {
            next[i].status = "ok";
          }
        }
      } else if (status === "running") {
        for (let i = 0; i < index; i += 1) {
          if (next[i].status === "idle") {
            next[i].status = "ok";
          }
        }
      }

      return next;
    });
  }, []);

  const runSession = useCallback(
    async (request: PlaygroundSessionRequest) => {
      eventSourceRef.current?.close();
      if (timerRef.current) clearInterval(timerRef.current);

      setIsRunning(true);
      setStatus("PENDING");
      setTraces([]);
      setSriResponse(null);
      setAuthorizedXml("");
      setPipelineSteps(INITIAL_PIPELINE);
      setElapsedMs(0);
      setError(null);
      seenLogCount.current = 0;
      startTimeRef.current = Date.now();

      timerRef.current = setInterval(() => {
        setElapsedMs(Date.now() - startTimeRef.current);
      }, 100);

      addTrace("INFO", "Playground: creando sesión remota");

      try {
        const createResponse = await fetch("/api/sessions", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(request),
        });

        if (!createResponse.ok) {
          const errorBody = await createResponse.text();
          throw new Error(errorBody || `Failed to create session (${createResponse.status})`);
        }

        const payload = (await createResponse.json()) as { id?: string };
        if (!payload.id) {
          throw new Error("Session response did not include an id");
        }

        addTrace("OK", `Playground: sesión creada (${payload.id})`);
        setStatus("RUNNING");

        const eventSource = new EventSource(`/api/sessions/${payload.id}/events`);
        eventSourceRef.current = eventSource;
        addTrace("INFO", "Playground: suscrito al stream SSE");

        eventSource.onmessage = (event) => {
          const session: SessionResponse = JSON.parse(event.data);
          setStatus(session.status);

          const newLogs = session.logs.slice(seenLogCount.current);
          seenLogCount.current = session.logs.length;
          newLogs.forEach((log) => {
            const level = inferTraceLevel(log.message);
            setTraces((prev) => [...prev, { timestamp: log.timestamp, level, message: log.message }]);
            applyPipelineUpdate(log.message, level);
          });

          if (session.status === "COMPLETED" || session.status === "FAILED") {
            if (session.status === "COMPLETED" && session.responsePayload) {
              setSriResponse(parseResponsePayload(session.responsePayload));
              setAuthorizedXml(
                session.responsePayload.authorizedXml ??
                  session.responsePayload.authorization?.authorizedXML ??
                  ""
              );
              applyPipelineCompletion(session.responsePayload);
            }

            eventSource.close();
            eventSourceRef.current = null;
            stopTimer();
            setIsRunning(false);
          }
        };

        eventSource.onerror = () => {
          addTrace("ERROR", "Playground: error en la conexión SSE");
          eventSource.close();
          eventSourceRef.current = null;
          stopTimer();
          setStatus("FAILED");
          setIsRunning(false);
        };
      } catch (error) {
        const message = error instanceof Error ? error.message : "Unknown error";
        addTrace("ERROR", `Playground: no se pudo iniciar la sesión (${message})`);
        stopTimer();
        setStatus("FAILED");
        setError(message);
        setIsRunning(false);
      }
    },
    [addTrace, applyPipelineCompletion, applyPipelineUpdate, stopTimer]
  );

  return {
    traces,
    status,
    sriResponse,
    authorizedXml,
    pipelineSteps,
    elapsedMs,
    isRunning,
    error,
    runSession,
  };
}
