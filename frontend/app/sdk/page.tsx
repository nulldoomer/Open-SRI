import SdkTable from "@/components/sdk/SdkTable";

export default function SDK() {
  return (
    <main className="max-w-5xl mx-auto px-8 py-16">
      <div className="mb-12">
        <h1 className="font-heading text-4xl font-bold mb-3">SDK Explorer</h1>
        <p className="text-muted-foreground text-lg">
          Integra OpenSRI en tu lenguaje preferido. El SDK Java está disponible hoy;
          los demás están en desarrollo.
        </p>
      </div>
      <SdkTable />
      <div className="mt-8 p-4 border border-border rounded-sm bg-muted/20">
        <p className="text-xs text-muted-foreground">
          <span className="font-semibold text-foreground">¿Quieres contribuir?</span>{" "}
          Revisa los issues abiertos en{" "}
          <a
            href="https://github.com/nulldoomer/Open-SRI"
            target="_blank"
            rel="noopener noreferrer"
            className="underline underline-offset-2 hover:text-foreground transition-colors"
          >
            github.com/nulldoomer/Open-SRI
          </a>
          . Las portaciones de SDK son el mejor punto de entrada.
        </p>
      </div>
    </main>
  );
}
