"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";

interface XmlViewerProps {
  unsignedXml: string;
  signedXml: string;
}

function copyToClipboard(text: string) {
  navigator.clipboard.writeText(text);
}

function downloadXml(content: string, filename: string) {
  const blob = new Blob([content], { type: "application/xml" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  a.click();
  URL.revokeObjectURL(url);
}

function XmlCode({ xml }: { xml: string }) {
  if (!xml) {
    return (
      <div className="flex items-center justify-center py-12 text-muted-foreground text-sm">
        El XML aparecerá aquí después de generar la factura.
      </div>
    );
  }
  return (
    <pre className="p-4 overflow-x-auto text-xs font-mono leading-relaxed text-foreground/80 bg-transparent">
      <code>{xml}</code>
    </pre>
  );
}

export default function XmlViewer({ unsignedXml, signedXml }: XmlViewerProps) {
  const [tab, setTab] = useState("unsigned");

  const currentXml = tab === "signed" ? signedXml : unsignedXml;

  return (
    <div className="space-y-4">
      <div className="space-y-1">
        <p className="text-[11px] uppercase tracking-[0.22em] text-muted-foreground font-semibold">
          Artefactos
        </p>
        <h3 className="text-lg font-semibold text-foreground">XML generado</h3>
        <p className="text-sm text-muted-foreground">
          Revisa el XML sin firmar y el XML firmado cuando el backend los exponga.
        </p>
      </div>

      <Tabs value={tab} onValueChange={setTab}>
        <div className="flex items-center justify-between">
          <TabsList variant="line">
            <TabsTrigger value="unsigned">Sin firma</TabsTrigger>
            <TabsTrigger value="signed">Con firma XAdES</TabsTrigger>
          </TabsList>
          {currentXml && (
            <div className="flex gap-2">
              <Button
                variant="ghost"
                size="xs"
                onClick={() => copyToClipboard(currentXml)}
              >
                Copiar XML
              </Button>
              <Button
                variant="ghost"
                size="xs"
                onClick={() =>
                  downloadXml(currentXml, tab === "signed" ? "firmado.xml" : "factura.xml")
                }
              >
                Descargar .xml
              </Button>
            </div>
          )}
        </div>

        <div className="mt-3 border border-border/70 rounded-xl bg-muted/10 min-h-64 max-h-96 overflow-y-auto shadow-sm">
          <TabsContent value="unsigned">
            <XmlCode xml={unsignedXml} />
          </TabsContent>
          <TabsContent value="signed">
            <XmlCode xml={signedXml} />
          </TabsContent>
        </div>
      </Tabs>
    </div>
  );
}
