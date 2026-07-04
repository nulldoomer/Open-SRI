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

export interface PipelineStepState {
  id: PipelineStepId;
  status: StepStatus;
  detail?: string;
}

export type TraceLevel = "INFO" | "OK" | "WARN" | "ERROR";

export interface TraceEvent {
  timestamp: string;
  level: TraceLevel;
  message: string;
}

export interface SriMessage {
  identifier: string;
  message: string;
  additionalInfo?: string;
  type: "INFO" | "ERROR";
}

export interface SriResponse {
  status: string;
  accessKey: string;
  authorizationDate?: string;
  authorizedXml?: string;
  messages: SriMessage[];
}

export type PlaygroundLanguage = "JAVA" | "CSHARP" | "GO";
export type PaymentMethod = "SIN_SISTEMA_FINANCIERO";
export type DocumentVersion = "VERSION_100";

export interface InvoiceItem {
  mainCode: string;
  auxiliaryCode: string;
  description: string;
  quantity: number;
  price: number;
  taxCode: string;
  taxPercentageCode: string;
  taxRate: number;
}

export interface InvoicePayload {
  issuerRuc: string;
  issuerName: string;
  establishmentAddress: string;
  codDoc: string;
  estab: string;
  ptoEmi: string;
  secuencial: string;
  buyerName: string;
  buyerIdentification: string;
  buyerIdentificationType: string;
  paymentMethod: PaymentMethod;
  documentVersion: DocumentVersion;
  items: InvoiceItem[];
}

export interface PlaygroundSessionRequest {
  invoicePayload: InvoicePayload;
  language: PlaygroundLanguage;
  sdkVersion: string;
}

export interface PlaygroundResult {
  sriResponse: SriResponse;
  unsignedXml: string;
  signedXml: string;
}
