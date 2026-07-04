"use client";

import { useMemo, useState } from "react";
import dynamic from "next/dynamic";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import {
  Invoice01Icon,
  Key01Icon,
  Xml01Icon,
  LockKeyIcon,
  Wifi01Icon,
  Clock01Icon,
} from "@hugeicons/core-free-icons";
import { usePlaygroundSession } from "@/hooks/usePlaygroundSession";
import type { InvoicePayload, PipelineStep, PlaygroundSessionRequest } from "@/types/playground";

const InvoiceForm = dynamic(() => import("@/components/playground/InvoiceForm"), { ssr: false });
const PipelineViewer = dynamic(() => import("@/components/playground/PipelineViewer"), { ssr: false });
const XmlViewer = dynamic(() => import("@/components/playground/XmlViewer"), { ssr: false });
const TraceLog = dynamic(() => import("@/components/playground/TraceLog"), { ssr: false });
const SriResponse = dynamic(() => import("@/components/playground/SriResponse"), { ssr: false });

const INITIAL_STEPS: PipelineStep[] = [
  { id: "build", icon: Invoice01Icon, label: "Construir Factura", status: "idle" },
  { id: "access_key", icon: Key01Icon, label: "Clave de Acceso (49 dígitos)", status: "idle" },
  { id: "serialize", icon: Xml01Icon, label: "Serializar XML", status: "idle" },
  { id: "sign", icon: LockKeyIcon, label: "Firmar XAdES-BES", status: "idle" },
  { id: "send", icon: Wifi01Icon, label: "Enviar SOAP", status: "idle" },
  { id: "response", icon: Clock01Icon, label: "Respuesta SRI", status: "idle" },
];

const PLAYGROUND_DEFAULTS = {
  language: "JAVA" as const,
  sdkVersion: "1.2.4",
};

export default function Playground() {
  const [activeTab, setActiveTab] = useState("pipeline");
  const {
    traces,
    sriResponse,
    authorizedXml,
    pipelineSteps,
    elapsedMs,
    isRunning,
    error,
    runSession,
  } =
    usePlaygroundSession();

  const steps = useMemo(
    () =>
      INITIAL_STEPS.map((step) => {
        const progress = pipelineSteps.find((state) => state.id === step.id);
        return {
          ...step,
          status: progress?.status ?? step.status,
          detail: progress?.detail,
        };
      }),
    [pipelineSteps]
  );

  const unsignedXml = "";
  const signedXml = authorizedXml;

  const handleSubmit = async (data: InvoicePayload) => {
    const request: PlaygroundSessionRequest = {
      invoicePayload: data,
      ...PLAYGROUND_DEFAULTS,
    };

    setActiveTab("pipeline");
    await runSession(request);
  };

  return (
    <main className="max-w-5xl mx-auto px-8 py-12">
      <div className="mb-8">
        <h1 className="font-heading text-4xl font-bold mb-3">Playground</h1>
        <p className="text-muted-foreground">
          Genera una factura y observa la sesión real del backend en tiempo real.
        </p>
      </div>

      <div className="p-4 mb-6 border border-emerald-200 bg-emerald-50 dark:border-emerald-900/50 dark:bg-emerald-950/20 rounded-sm text-xs text-emerald-800 dark:text-emerald-200">
        <strong>Backend conectado:</strong> el playground consume sesiones, trazas y respuesta
        desde tu API.
      </div>

      {error && (
        <div className="p-4 mb-6 border border-destructive/30 bg-destructive/5 rounded-sm text-xs text-destructive">
          No se pudo iniciar la sesión: {error}
        </div>
      )}

      <div className="border border-border/70 rounded-xl p-6 mb-6 bg-background/70 shadow-sm">
        <InvoiceForm onSubmit={handleSubmit} isRunning={isRunning} />
      </div>

      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList variant="line">
          <TabsTrigger value="pipeline">Pipeline</TabsTrigger>
          <TabsTrigger value="xml">XML Generado</TabsTrigger>
          <TabsTrigger value="traces">Trazas</TabsTrigger>
          <TabsTrigger value="response">Respuesta SRI</TabsTrigger>
        </TabsList>

        <div className="mt-4 border border-border/70 rounded-xl p-6 min-h-64 bg-background/70 shadow-sm">
          <TabsContent value="pipeline">
            <PipelineViewer steps={steps} elapsed={elapsedMs} isRunning={isRunning} />
          </TabsContent>

          <TabsContent value="xml">
            <XmlViewer unsignedXml={unsignedXml} signedXml={signedXml} />
          </TabsContent>

          <TabsContent value="traces">
            <TraceLog traces={traces} />
          </TabsContent>

          <TabsContent value="response">
            <SriResponse response={sriResponse} />
          </TabsContent>
        </div>
      </Tabs>
    </main>
  );
}
