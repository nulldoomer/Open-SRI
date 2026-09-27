"use client";

import { useMemo, useRef, useState } from "react";
import dynamic from "next/dynamic";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import { usePlaygroundSession } from "@/hooks/usePlaygroundSession";
import type { InvoicePayload, PipelineStep, PlaygroundSessionRequest } from "@/types/playground";

const InvoiceForm = dynamic(() => import("@/components/playground/InvoiceForm"), { ssr: false });
const PipelineViewer = dynamic(() => import("@/components/playground/PipelineViewer"), { ssr: false });
const XmlViewer = dynamic(() => import("@/components/playground/XmlViewer"), { ssr: false });
const TraceLog = dynamic(() => import("@/components/playground/TraceLog"), { ssr: false });
const SriResponse = dynamic(() => import("@/components/playground/SriResponse"), { ssr: false });

const INITIAL_STEPS: PipelineStep[] = [
  { id: "build", label: "Factura construida", status: "idle" },
  { id: "access_key", label: "Clave de acceso (49 dígitos)", status: "idle" },
  { id: "serialize", label: "XML serializado", status: "idle" },
  { id: "sign", label: "Firmado XAdES-BES", status: "idle" },
  { id: "send", label: "Enviado por SOAP", status: "idle" },
  { id: "response", label: "Respuesta del SRI", status: "idle" },
];

const PLAYGROUND_DEFAULTS = {
  language: "JAVA" as const,
  sdkVersion: "1.2.4",
};

export default function Playground() {
  const [activeTab, setActiveTab] = useState("pipeline");
  const resultsRef = useRef<HTMLElement>(null);
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
    // Stacked layout: bring the live tracking into view instead of leaving it below the form.
    if (window.matchMedia("(max-width: 1023px)").matches) {
      resultsRef.current?.scrollIntoView({ behavior: "smooth", block: "start" });
    }
    await runSession(request);
  };

  return (
    <main className="mx-auto w-full max-w-7xl px-4 py-10 sm:px-8 lg:px-16 lg:py-14">
      <header className="mb-10 flex flex-wrap items-end justify-between gap-6 border-b border-border pb-8">
        <div>
          <h1 className="font-heading text-[clamp(2.5rem,5vw,3.75rem)] font-extrabold uppercase leading-[0.9] tracking-[-0.015em] [font-stretch:80%]">
            Playground
          </h1>
          <p className="mt-3 max-w-xl text-base leading-relaxed text-muted-foreground">
            Llena la guía, envíala y sigue la sesión real del SDK contra el ambiente de pruebas del SRI.
          </p>
        </div>
        <p className="flex items-center gap-2 font-mono text-[11px] uppercase tracking-[0.16em] text-muted-foreground">
          <span aria-hidden="true" className="size-1.5 rounded-full bg-[var(--v-olive-deep)]" />
          SDK Java {PLAYGROUND_DEFAULTS.sdkVersion} · SRI pruebas
        </p>
      </header>

      {error && (
        <div role="alert" className="mb-8 rounded-[var(--r-card-sm)] bg-[var(--status-danger-bg)] px-5 py-4 text-sm text-[var(--status-danger-ink)]">
          <p className="font-semibold">No se pudo iniciar la sesión.</p>
          <p className="mt-1">
            {error}. Revisa tu conexión e inténtalo de nuevo; si persiste, el servicio del Playground puede estar
            fuera de línea.
          </p>
        </div>
      )}

      <div className="grid gap-12 lg:grid-cols-[minmax(0,1fr)_24rem] lg:gap-10 xl:grid-cols-[minmax(0,1fr)_28rem]">
        <InvoiceForm onSubmit={handleSubmit} isRunning={isRunning} />

        <aside ref={resultsRef} aria-label="Seguimiento del envío" className="scroll-mt-24 self-start lg:sticky lg:top-24">
          <Tabs value={activeTab} onValueChange={setActiveTab}>
            <TabsList aria-label="Vista del seguimiento">
              <TabsTrigger value="pipeline">Pipeline</TabsTrigger>
              {/* <TabsTrigger value="xml">XML Generado</TabsTrigger> */}
              <TabsTrigger value="traces">Trazas</TabsTrigger>
              <TabsTrigger value="response">Respuesta SRI</TabsTrigger>
            </TabsList>

            <div className="rounded-[var(--r-card)] bg-card p-6 ring-1 ring-border">
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
        </aside>
      </div>
    </main>
  );
}
