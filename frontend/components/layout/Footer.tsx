import Link from "next/link";
import Mark from "./Mark";

const project = [
  { href: "https://github.com/nulldoomer/Open-SRI", label: "GitHub" },
  { href: "https://github.com/nulldoomer/Open-SRI/issues", label: "Issues" },
  { href: "https://central.sonatype.com/artifact/io.github.nulldoomer/opensri", label: "Maven Central" },
  { href: "https://ko-fi.com/nulldoomer", label: "Ko-fi" },
];

const site = [
  { href: "/doc", label: "Docs" },
  { href: "/playground", label: "Playground" },
  { href: "/sdk", label: "SDK" },
  { href: "/about", label: "Acerca de" },
];

export default function Footer() {
  return (
    <footer className="border-t border-border bg-background">
      <div className="mx-auto grid max-w-7xl gap-10 px-4 py-12 sm:px-8 md:grid-cols-[1.4fr_1fr_1fr] lg:px-16">
        <div className="space-y-3">
          <p className="flex items-center gap-3 font-mono text-[11px] uppercase tracking-[0.18em] text-muted-foreground">
            <Mark count={3} />
            Null-privatization / OpenSRI
          </p>
          <p className="max-w-sm text-sm leading-relaxed text-muted-foreground">
            SDK open source para la facturación electrónica del SRI. Publicado bajo{" "}
            <a
              href="https://github.com/nulldoomer/Open-SRI/blob/main/LICENSE"
              target="_blank"
              rel="noopener noreferrer"
              className="text-foreground underline decoration-border underline-offset-4 hover:decoration-signal"
            >
              Apache 2.0
            </a>
            .
          </p>
        </div>

        <FooterList title="Proyecto" items={project} external />
        <FooterList title="Sitio" items={site} />
      </div>
    </footer>
  );
}

function FooterList({
  title,
  items,
  external = false,
}: {
  title: string;
  items: { href: string; label: string }[];
  external?: boolean;
}) {
  return (
    <div>
      <h2 className="mb-3 font-mono text-[11px] uppercase tracking-[0.18em] text-muted-foreground">{title}</h2>
      <ul className="space-y-2 text-sm">
        {items.map((item) => (
          <li key={item.href}>
            {external ? (
              <a
                href={item.href}
                target="_blank"
                rel="noopener noreferrer"
                className="text-foreground/80 transition-colors hover:text-foreground"
              >
                {item.label}
              </a>
            ) : (
              <Link href={item.href} className="text-foreground/80 transition-colors hover:text-foreground">
                {item.label}
              </Link>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}
