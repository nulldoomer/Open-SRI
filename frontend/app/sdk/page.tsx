import SdkList from "@/components/sdk/SdkList";

export default function SDK() {
  return (
    <main className="mx-auto w-full max-w-4xl px-4 py-10 sm:px-8 lg:py-16">
      <header className="mb-12">
        <h1 className="font-heading text-[clamp(2.5rem,5vw,3.75rem)] font-extrabold uppercase leading-[0.9] tracking-[-0.015em] [font-stretch:80%]">
          SDK
        </h1>
        <p className="mt-4 max-w-2xl text-lg leading-relaxed text-muted-foreground">
          Integra OpenSRI en tu lenguaje preferido. El SDK Java está disponible hoy; C# está en desarrollo y Go y Python están planificados.
        </p>
      </header>

      <SdkList />

      <aside className="mt-12 rounded-[var(--r-card-sm)] bg-muted px-6 py-5">
        <p className="text-[15px] leading-relaxed text-muted-foreground">
          <span className="font-semibold text-foreground">¿Quieres contribuir?</span> Las portaciones de SDK son el
          mejor punto de entrada. Revisa los issues abiertos en{" "}
          <a
            href="https://github.com/nulldoomer/Open-SRI/issues"
            target="_blank"
            rel="noopener noreferrer"
            className="text-foreground underline decoration-border underline-offset-4 hover:decoration-signal"
          >
            github.com/nulldoomer/Open-SRI
          </a>
          .
        </p>
      </aside>
    </main>
  );
}
