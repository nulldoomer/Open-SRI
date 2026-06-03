import type { HugeiconsIconProps } from "@hugeicons/react";

export type HugeIcon = NonNullable<HugeiconsIconProps["icon"]>;

export type PipelineStepId =
  | "build"
  | "access_key"
  | "serialize"
  | "sign"
  | "send"
  | "response";

export type StepStatus = "idle" | "running" | "ok" | "error";

export interface PipelineStep {
  id: PipelineStepId;
  icon: HugeIcon;
  label: string;
  detail?: string;
  status: StepStatus;
  durationMs?: number;
}

export type TraceLevel = "INFO" | "OK" | "WARN" | "ERROR";

export interface TraceEvent {
  timestamp: string;
  level: TraceLevel;
  message: string;
}

export type SriStatus = "RECIBIDA" | "DEVUELTA" | "AUTORIZADO" | "NO_AUTORIZADO";

export interface SriMessage {
  identifier: string;
  message: string;
  additionalInfo?: string;
  type: "INFO" | "ERROR";
}

export interface SriResponse {
  status: SriStatus;
  accessKey: string;
  messages: SriMessage[];
  authorizationDate?: string;
}

export type Ambiente = "PRUEBAS" | "PRODUCCION";
export type Lenguaje = "Java" | "CSharp" | "Go";

export interface InvoiceItem {
  descripcion: string;
  cantidad: number;
  precio: number;
}

export interface InvoiceFormData {
  rucEmisor: string;
  nombreEmisor: string;
  identificacionCliente: string;
  nombreCliente: string;
  ambiente: Ambiente;
  lenguaje: Lenguaje;
  items: InvoiceItem[];
}

export interface PlaygroundResult {
  sriResponse: SriResponse;
  unsignedXml: string;
  signedXml: string;
}
