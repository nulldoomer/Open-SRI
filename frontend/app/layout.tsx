import type { Metadata } from "next";
import Script from "next/script";
import { Bricolage_Grotesque, DM_Sans, Geist_Mono } from "next/font/google";
import "./globals.css";
import { cn } from "@/lib/utils";
import Navbar from "@/components/layout/navbar";
import Footer from "@/components/layout/Footer";

const bricolage = Bricolage_Grotesque({
  subsets: ["latin"],
  variable: "--font-bricolage",
  axes: ["opsz", "wdth"],
});

const dmSans = DM_Sans({
  subsets: ["latin"],
  variable: "--font-dm-sans",
  axes: ["opsz"],
});

const geistMono = Geist_Mono({
  subsets: ["latin"],
  variable: "--font-geist-mono",
});

export const metadata: Metadata = {
  title: "OpenSRI — Facturación electrónica para Ecuador",
  description:
    "SDK open-source multi-lenguaje para integrar facturación electrónica del SRI. Clave de acceso, XML, XAdES-BES y SOAP ya resueltos.",
};

// ponytail: sets data-mode before paint instead of shipping next-themes; the toggle writes the same key.
const themeScript = `try{var m=localStorage.getItem("theme");if(m!=="dark"&&m!=="light")m=matchMedia("(prefers-color-scheme: dark)").matches?"dark":"light";document.documentElement.dataset.mode=m}catch(e){}`;

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="es"
      suppressHydrationWarning
      className={cn("h-full antialiased", bricolage.variable, dmSans.variable, geistMono.variable)}
    >
      <body className="min-h-full flex flex-col">
        <Script id="theme-mode" strategy="beforeInteractive">
          {themeScript}
        </Script>
        <Navbar />
        <div className="flex-1 flex flex-col">{children}</div>
        <Footer />
      </body>
    </html>
  );
}
