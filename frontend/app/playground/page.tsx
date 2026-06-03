"use client";

import { useState, useRef, useCallback } from "react";
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

// Heavy components lazy-loaded per bundle-dynamic-imports rule
const InvoiceForm = dynamic(() => import("@/components/playground/InvoiceForm"), { ssr: false });
const PipelineViewer = dynamic(() => import("@/components/playground/PipelineViewer"), { ssr: false });
const XmlViewer = dynamic(() => import("@/components/playground/XmlViewer"), { ssr: false });
const TraceLog = dynamic(() => import("@/components/playground/TraceLog"), { ssr: false });
const SriResponse = dynamic(() => import("@/components/playground/SriResponse"), { ssr: false });
import type {
  InvoiceFormData,
  PipelineStep,
  TraceEvent,
  SriResponse as SriResponseType,
} from "@/types/playground";

const INITIAL_STEPS: PipelineStep[] = [
  { id: "build", icon: Invoice01Icon, label: "Construir Factura", status: "idle" },
  { id: "access_key", icon: Key01Icon, label: "Clave de Acceso (49 dígitos)", status: "idle" },
  { id: "serialize", icon: Xml01Icon, label: "Serializar XML", status: "idle" },
  { id: "sign", icon: LockKeyIcon, label: "Firmar XAdES-BES", status: "idle" },
  { id: "send", icon: Wifi01Icon, label: "Enviar SOAP", status: "idle" },
  { id: "response", icon: Clock01Icon, label: "Respuesta SRI", status: "idle" },
];

function delay(ms: number) {
  return new Promise<void>((resolve) => setTimeout(resolve, ms));
}

function generateAccessKey(ruc: string): string {
  const date = new Date();
  const d = String(date.getDate()).padStart(2, "0");
  const m = String(date.getMonth() + 1).padStart(2, "0");
  const y = String(date.getFullYear());
  const base = `${d}${m}${y}${ruc}0001000100000001`;
  const padded = base.padEnd(48, "1");
  return padded.slice(0, 48) + "2";
}

function buildUnsignedXml(data: InvoiceFormData, accessKey: string): string {
  const total = data.items.reduce((sum, i) => sum + i.cantidad * i.precio, 0);
  const iva = +(total * 0.15).toFixed(2);
  const items = data.items
    .map(
      (item) =>
        `  <detalle>\n    <descripcion>${item.descripcion}</descripcion>\n    <cantidad>${item.cantidad}</cantidad>\n    <precioUnitario>${item.precio.toFixed(2)}</precioUnitario>\n    <subtotal>${(item.cantidad * item.precio).toFixed(2)}</subtotal>\n  </detalle>`
    )
    .join("\n");

  return `<?xml version="1.0" encoding="UTF-8"?>
<factura id="comprobante" version="2.1.0">
  <infoTributaria>
    <ambiente>1</ambiente>
    <tipoEmision>1</tipoEmision>
    <razonSocial>${data.nombreEmisor}</razonSocial>
    <ruc>${data.rucEmisor}</ruc>
    <claveAcceso>${accessKey}</claveAcceso>
    <codDoc>01</codDoc>
    <estab>001</estab>
    <ptoEmi>001</ptoEmi>
    <secuencial>000000001</secuencial>
    <dirMatriz>Quito, Ecuador</dirMatriz>
  </infoTributaria>
  <infoFactura>
    <fechaEmision>${new Date().toLocaleDateString("es-EC")}</fechaEmision>
    <tipoIdentificacionComprador>05</tipoIdentificacionComprador>
    <razonSocialComprador>${data.nombreCliente}</razonSocialComprador>
    <identificacionComprador>${data.identificacionCliente}</identificacionComprador>
    <totalSinImpuestos>${total.toFixed(2)}</totalSinImpuestos>
    <totalImpuesto>${iva.toFixed(2)}</totalImpuesto>
    <importeTotal>${(total + iva).toFixed(2)}</importeTotal>
    <moneda>DOLAR</moneda>
  </infoFactura>
  <detalles>
${items}
  </detalles>
</factura>`;
}

function buildSignedXml(unsignedXml: string): string {
  return (
    unsignedXml.replace(
      "</factura>",
      `  <Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
    <SignedInfo>
      <CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/>
      <SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"/>
    </SignedInfo>
    <SignatureValue>MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...</SignatureValue>
    <KeyInfo>
      <X509Data>
        <X509Certificate>MIICpDCCAYwCCQDU7pQ...</X509Certificate>
      </X509Data>
    </KeyInfo>
  </Signature>
</factura>`
    )
  );
}

export default function Playground() {
  const [steps, setSteps] = useState<PipelineStep[]>(INITIAL_STEPS);
  const [traces, setTraces] = useState<TraceEvent[]>([]);
  const [sriResponse, setSriResponse] = useState<SriResponseType | null>(null);
  const [unsignedXml, setUnsignedXml] = useState("");
  const [signedXml, setSignedXml] = useState("");
  const [isRunning, setIsRunning] = useState(false);
  const [elapsed, setElapsed] = useState(0);
  const [activeTab, setActiveTab] = useState("pipeline");
  const startTime = useRef<number>(0);
  const elapsedInterval = useRef<ReturnType<typeof setInterval> | null>(null);

  const addTrace = useCallback((level: TraceEvent["level"], message: string) => {
    const ms = Date.now() - startTime.current;
    const s = Math.floor(ms / 60000);
    const sec = Math.floor((ms % 60000) / 1000);
    const mils = ms % 1000;
    const timestamp = `${String(s).padStart(2, "0")}:${String(sec).padStart(2, "0")}.${String(mils).padStart(3, "0")}`;
    setTraces((prev) => [...prev, { timestamp, level, message }]);
  }, []);

  const updateStep = useCallback(
    (id: PipelineStep["id"], status: PipelineStep["status"], detail?: string) => {
      setSteps((prev) =>
        prev.map((s) => (s.id === id ? { ...s, status, detail } : s))
      );
    },
    []
  );

  const runMockSimulation = useCallback(
    async (data: InvoiceFormData) => {
      const accessKey = generateAccessKey(data.rucEmisor);

      // Step 1: Build
      updateStep("build", "running");
      addTrace("INFO", `InvoiceBuilder: construyendo factura para ${data.nombreEmisor}`);
      await delay(350);
      updateStep("build", "ok", "Invoice.build() ✓");
      addTrace("OK", "InvoiceBuilder: factura construida correctamente");

      // Step 2: Access key
      updateStep("access_key", "running");
      await delay(220);
      updateStep("access_key", "ok", `${accessKey.slice(0, 12)}...✓`);
      addTrace("OK", `AccessKeyGenerator: clave de 49 dígitos generada (módulo 11 válido)`);

      // Step 3: Serialize XML
      updateStep("serialize", "running");
      addTrace("INFO", "InvoiceXmlSerializer: serializando con JAXB (XSD v2.1.0)");
      await delay(420);
      const unsigned = buildUnsignedXml(data, accessKey);
      setUnsignedXml(unsigned);
      const xmlSizeKb = (new Blob([unsigned]).size / 1024).toFixed(1);
      updateStep("serialize", "ok", `v2.1.0 — ${xmlSizeKb} KB ✓`);
      addTrace("OK", `InvoiceXmlSerializer: XML 2.1.0 (factura) — ${xmlSizeKb} KB`);

      // Step 4: Sign XAdES-BES
      updateStep("sign", "running");
      addTrace("INFO", "XadesSigner: cargando certificado P12 (demo)");
      await delay(680);
      const signed = buildSignedXml(unsigned);
      setSignedXml(signed);
      updateStep("sign", "ok", "RSA-SHA256 ✓");
      addTrace("OK", "XadesSigner: firma XAdES-BES RSA-SHA256 aplicada");

      // Step 5: Send SOAP
      updateStep("send", "running", "celcer.sri.gob.ec");
      addTrace("INFO", "SriReceiptService: conectando a celcer.sri.gob.ec");
      addTrace("INFO", "SOAP validarComprobante — timeout: 30s");
      await delay(1400);
      updateStep("send", "ok", "SOAP enviado ✓");
      addTrace("OK", "SOAP request enviado correctamente");

      // Step 6: SRI Response
      updateStep("response", "running", "esperando...");
      addTrace("INFO", "Esperando respuesta del SRI...");
      await delay(900);
      const response: SriResponseType = {
        status: "RECIBIDA",
        accessKey,
        messages: [],
      };
      setSriResponse(response);
      updateStep("response", "ok", "RECIBIDA ✓");
      addTrace("OK", "SRI respondió: RECIBIDA — comprobante aceptado");
    },
    [addTrace, updateStep]
  );

  const handleSubmit = useCallback(
    async (data: InvoiceFormData) => {
      setIsRunning(true);
      setSteps(INITIAL_STEPS);
      setTraces([]);
      setSriResponse(null);
      setUnsignedXml("");
      setSignedXml("");
      setElapsed(0);
      setActiveTab("pipeline");

      startTime.current = Date.now();
      elapsedInterval.current = setInterval(() => {
        setElapsed(Date.now() - startTime.current);
      }, 100);

      try {
        await runMockSimulation(data);
      } finally {
        if (elapsedInterval.current) clearInterval(elapsedInterval.current);
        setIsRunning(false);
      }
    },
    [runMockSimulation]
  );

  return (
    <main className="max-w-5xl mx-auto px-8 py-12">
      <div className="mb-8">
        <h1 className="font-heading text-4xl font-bold mb-3">Playground</h1>
        <p className="text-muted-foreground">
          Genera una factura y observa cada paso del pipeline del SDK en tiempo real.
        </p>
      </div>

      <div className="p-4 mb-6 border border-amber-200 bg-amber-50 dark:border-amber-900/50 dark:bg-amber-950/20 rounded-sm text-xs text-amber-800 dark:text-amber-200">
        ⚠️ <strong>Modo demo:</strong> esta versión simula el pipeline localmente. La integración
        real con el backend del SRI (sandbox) estará disponible próximamente.
      </div>

      <div className="border border-border rounded-sm p-6 mb-6">
        <InvoiceForm onSubmit={handleSubmit} isRunning={isRunning} />
      </div>

      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList variant="line">
          <TabsTrigger value="pipeline">Pipeline</TabsTrigger>
          <TabsTrigger value="xml">XML Generado</TabsTrigger>
          <TabsTrigger value="traces">Trazas</TabsTrigger>
          <TabsTrigger value="response">Respuesta SRI</TabsTrigger>
        </TabsList>

        <div className="mt-4 border border-border rounded-sm p-6 min-h-64">
          <TabsContent value="pipeline">
            <PipelineViewer steps={steps} elapsed={elapsed} isRunning={isRunning} />
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
