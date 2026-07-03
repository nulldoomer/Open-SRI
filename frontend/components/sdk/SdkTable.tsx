import { siOpenjdk, siDotnet, siGo, siPython } from "simple-icons";
import type { SimpleIcon } from "simple-icons";
import SimpleIconComponent from "@/components/ui/SimpleIcon";
import InstallSnippet from "./InstallSnippet";
import { cn } from "@/lib/utils";

type SdkStatus = "stable" | "beta" | "wip" | "planned";

interface Sdk {
  language: string;
  icon: SimpleIcon;
  version: string | null;
  status: SdkStatus;
  installCode: string | null;
  docsUrl: string | null;
}

const sdks: Sdk[] = [
  {
    language: "Java",
    icon: siOpenjdk,
    version: "1.2.4",
    status: "stable",
    installCode:
      "<dependency>\n  <groupId>io.github.nulldoomer</groupId>\n  <artifactId>opensri</artifactId>\n  <version>1.2.4</version>\n</dependency>",
    docsUrl: "/docs",
  },
  {
    language: "C#",
    icon: siDotnet,
    version: null,
    status: "wip",
    installCode: null,
    docsUrl: null,
  },
  {
    language: "Go",
    icon: siGo,
    version: null,
    status: "planned",
    installCode: null,
    docsUrl: null,
  },
  {
    language: "Python",
    icon: siPython,
    version: null,
    status: "planned",
    installCode: null,
    docsUrl: null,
  },
];

const statusStyles: Record<SdkStatus, string> = {
  stable: "text-emerald-600 dark:text-emerald-400",
  beta: "text-blue-600 dark:text-blue-400",
  wip: "text-amber-600 dark:text-amber-400",
  planned: "text-muted-foreground",
};

const statusLabels: Record<SdkStatus, string> = {
  stable: "● Stable",
  beta: "● Beta",
  wip: "● WIP",
  planned: "○ Planned",
};

export default function SdkTable() {
  return (
    <div className="border border-border rounded-sm overflow-hidden">
      <table className="w-full text-sm">
        <thead>
          <tr className="border-b border-border bg-muted/40">
            <th className="text-left px-6 py-3 font-semibold text-xs tracking-widest uppercase">
              Lenguaje
            </th>
            <th className="text-left px-6 py-3 font-semibold text-xs tracking-widest uppercase">
              Versión
            </th>
            <th className="text-left px-6 py-3 font-semibold text-xs tracking-widest uppercase">
              Estado
            </th>
            <th className="text-left px-6 py-3 font-semibold text-xs tracking-widest uppercase">
              Instalación (Maven)
            </th>
          </tr>
        </thead>
        <tbody>
          {sdks.map((sdk, i) => (
            <tr
              key={sdk.language}
              className={cn(
                "border-b border-border last:border-0",
                i % 2 === 1 && "bg-muted/20"
              )}
            >
              <td className="px-6 py-4">
                <span className="flex items-center gap-2.5 font-medium">
                  <SimpleIconComponent icon={sdk.icon} size={16} className="text-neutral-700 dark:text-neutral-300 shrink-0" />
                  {sdk.language}
                </span>
              </td>
              <td className="px-6 py-4 font-mono text-xs">
                {sdk.version ?? "—"}
              </td>
              <td className="px-6 py-4">
                <span
                  className={cn(
                    "text-xs font-semibold tracking-wide",
                    statusStyles[sdk.status]
                  )}
                >
                  {statusLabels[sdk.status]}
                </span>
              </td>
              <td className="px-6 py-4">
                <InstallSnippet
                  code={sdk.installCode ?? ""}
                  available={sdk.installCode !== null}
                />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
