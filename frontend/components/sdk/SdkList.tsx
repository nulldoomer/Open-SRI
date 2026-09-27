import Link from "next/link";
import { siOpenjdk, siDotnet, siGo, siPython } from "simple-icons";
import type { SimpleIcon } from "simple-icons";
import SimpleIconComponent from "@/components/ui/SimpleIcon";
import LanguageTabs from "@/components/docs/LanguageTabs";
import Mark from "@/components/layout/Mark";

const JAVA_VERSION = "1.2.4";

const install = [
  {
    label: "Maven",
    lang: "xml",
    code: `<dependency>\n  <groupId>io.github.nulldoomer</groupId>\n  <artifactId>opensri</artifactId>\n  <version>${JAVA_VERSION}</version>\n</dependency>`,
  },
  { label: "Gradle", lang: "groovy", code: `implementation 'io.github.nulldoomer:opensri:${JAVA_VERSION}'` },
  { label: "Kotlin DSL", lang: "kotlin", code: `implementation("io.github.nulldoomer:opensri:${JAVA_VERSION}")` },
];

const upcoming: { language: string; icon: SimpleIcon; status: "wip" | "planned" }[] = [
  { language: "C#", icon: siDotnet, status: "wip" },
  { language: "Go", icon: siGo, status: "planned" },
  { language: "Python", icon: siPython, status: "planned" },
];

const statusLabel = { wip: "En desarrollo", planned: "Planificado" };

const link = "underline decoration-border underline-offset-4 transition-colors hover:decoration-signal";

export default function SdkList() {
  return (
    <div className="space-y-10">
      <section aria-labelledby="sdk-java">
        <header className="flex flex-wrap items-center justify-between gap-4">
          <h2 id="sdk-java" className="flex items-center gap-3 font-heading text-2xl font-bold">
            <SimpleIconComponent icon={siOpenjdk} size={24} />
            Java
          </h2>
          <p className="flex items-center gap-3">
            <span className="font-mono text-sm tabular-nums">{JAVA_VERSION}</span>
            <span className="inline-flex items-center gap-2 rounded-full bg-[var(--status-ok-bg)] px-3 py-1 text-sm text-[var(--status-ok-ink)]">
              <span aria-hidden="true" className="size-1.5 rounded-full bg-current" />
              Estable
            </span>
          </p>
        </header>

        <div className="mt-6">
          <LanguageTabs tabs={install} />
        </div>

        <p className="mt-5 flex flex-wrap gap-x-6 gap-y-2 text-sm">
          <Link href="/doc" className={link}>
            Quick Start
          </Link>
          <a
            href="https://central.sonatype.com/artifact/io.github.nulldoomer/opensri"
            target="_blank"
            rel="noopener noreferrer"
            className={link}
          >
            Maven Central
          </a>
        </p>
      </section>

      <section>
        <h2 className="mb-4 flex items-center gap-2 font-mono text-[10.5px] uppercase tracking-[0.16em] text-muted-foreground">
          <Mark /> Próximos SDKs
        </h2>
        <ul className="divide-y divide-dashed divide-border border-y border-dashed border-border">
          {upcoming.map((sdk) => (
            <li key={sdk.language} className="flex items-center justify-between gap-4 py-4">
              <p className="flex items-center gap-2.5 font-heading text-lg font-bold text-muted-foreground">
                <SimpleIconComponent icon={sdk.icon} size={18} />
                {sdk.language}
              </p>
              <p
                className={
                  sdk.status === "wip"
                    ? "font-mono text-[11px] uppercase tracking-[0.14em] text-[var(--status-warn-ink)]"
                    : "font-mono text-[11px] uppercase tracking-[0.14em] text-muted-foreground"
                }
              >
                {statusLabel[sdk.status]}
              </p>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
