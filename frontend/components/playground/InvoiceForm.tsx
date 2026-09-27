"use client";

import { useFieldArray, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import type { ReactNode } from "react";
import { HugeiconsIcon } from "@hugeicons/react";
import { Add01Icon, Cancel01Icon } from "@hugeicons/core-free-icons";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { cn } from "@/lib/utils";
import type { InvoicePayload } from "@/types/playground";

const itemSchema = z.object({
  mainCode: z.string().min(1, "Campo requerido"),
  auxiliaryCode: z.string().min(1, "Campo requerido"),
  description: z.string().min(1, "Campo requerido"),
  quantity: z.number().min(1, "Mínimo 1"),
  price: z.number().min(0.01, "Precio debe ser mayor a 0"),
  taxCode: z.string().min(1, "Campo requerido"),
  taxPercentageCode: z.string().min(1, "Campo requerido"),
  taxRate: z.number().min(0, "Campo requerido"),
});

const schema = z.object({
  issuerRuc: z
    .string()
    .length(13, "El RUC debe tener exactamente 13 dígitos")
    .regex(/^\d+$/, "Solo dígitos"),
  issuerName: z.string().min(1, "Campo requerido"),
  establishmentAddress: z.string().min(1, "Campo requerido"),
  codDoc: z.string().length(2, "Código de documento inválido"),
  estab: z.string().length(3, "El establecimiento debe tener 3 dígitos"),
  ptoEmi: z.string().length(3, "El punto de emisión debe tener 3 dígitos"),
  secuencial: z.string().length(9, "El secuencial debe tener 9 dígitos"),
  buyerName: z.string().min(1, "Campo requerido"),
  buyerIdentification: z.string().min(1, "Campo requerido"),
  buyerIdentificationType: z.string().min(2, "Campo requerido"),
  paymentMethod: z.literal("SIN_SISTEMA_FINANCIERO"),
  documentVersion: z.literal("VERSION_100"),
  items: z.array(itemSchema).min(1, "Agrega al menos un ítem"),
});

interface InvoiceFormProps {
  onSubmit: (data: InvoicePayload) => void;
  isRunning: boolean;
}

export default function InvoiceForm({ onSubmit, isRunning }: InvoiceFormProps) {
  const {
    register,
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<InvoicePayload>({
    resolver: zodResolver(schema),
    defaultValues: {
      issuerRuc: "1791248678001",
      issuerName: "OpenSRI Demo S.A.",
      establishmentAddress: "Av. Amazonas y Naciones Unidas, Quito",
      codDoc: "01",
      estab: "001",
      ptoEmi: "001",
      secuencial: "000000012",
      buyerName: "Juan Pérez",
      buyerIdentification: "1101160032",
      buyerIdentificationType: "05",
      paymentMethod: "SIN_SISTEMA_FINANCIERO",
      documentVersion: "VERSION_100",
      items: [
        {
          mainCode: "P001",
          auxiliaryCode: "A001",
          description: "Laptop Lenovo ThinkPad E16",
          quantity: 1,
          price: 1200,
          taxCode: "2",
          taxPercentageCode: "4",
          taxRate: 15,
        },
      ],
    },
  });

  const { fields, append, remove } = useFieldArray({ control, name: "items" });

  // Wires label, error text and invalid state together for screen readers.
  const a11y = (id: string, error?: string) => ({
    id,
    "aria-invalid": error ? true : undefined,
    "aria-describedby": error ? `${id}-error` : undefined,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-12" noValidate>
      <section>
        <SectionHeader n={1} title="Emisor y comprador">
          Quién emite la factura y a quién va dirigida.
        </SectionHeader>

        <div className="grid gap-x-5 gap-y-5 md:grid-cols-2">
          <Field id="issuerRuc" label="RUC del emisor" error={errors.issuerRuc?.message}>
            <Input size="sm" inputMode="numeric" placeholder="1791248678001" maxLength={13} className={mono} {...a11y("issuerRuc", errors.issuerRuc?.message)} {...register("issuerRuc")} />
          </Field>

          <Field id="issuerName" label="Nombre del emisor" error={errors.issuerName?.message}>
            <Input size="sm" placeholder="OpenSRI Demo S.A." {...a11y("issuerName", errors.issuerName?.message)} {...register("issuerName")} />
          </Field>

          <Field id="establishmentAddress" label="Dirección del establecimiento" error={errors.establishmentAddress?.message} className="md:col-span-2">
            <Input size="sm" placeholder="Av. Amazonas y Naciones Unidas, Quito" {...a11y("establishmentAddress", errors.establishmentAddress?.message)} {...register("establishmentAddress")} />
          </Field>

          <Field id="buyerIdentification" label="Identificación del comprador" error={errors.buyerIdentification?.message}>
            <Input size="sm" inputMode="numeric" placeholder="1101160032" className={mono} {...a11y("buyerIdentification", errors.buyerIdentification?.message)} {...register("buyerIdentification")} />
          </Field>

          <Field id="buyerName" label="Nombre del comprador" error={errors.buyerName?.message}>
            <Input size="sm" placeholder="Juan Pérez" {...a11y("buyerName", errors.buyerName?.message)} {...register("buyerName")} />
          </Field>

          <Field id="buyerIdentificationType" label="Tipo de identificación" tag="tipoIdentificacionComprador" error={errors.buyerIdentificationType?.message}>
            <Input size="sm" placeholder="05" maxLength={2} className={mono} {...a11y("buyerIdentificationType", errors.buyerIdentificationType?.message)} {...register("buyerIdentificationType")} />
          </Field>
        </div>
      </section>

      <section>
        <SectionHeader n={2} title="Serie y configuración">
          Numeración fiscal del comprobante. Forma parte de la clave de acceso.
        </SectionHeader>

        <div className="grid grid-cols-2 gap-x-5 gap-y-5 md:grid-cols-4">
          <Field id="codDoc" label="Tipo de documento" tag="codDoc" error={errors.codDoc?.message}>
            <Input size="sm" maxLength={2} className={mono} {...a11y("codDoc", errors.codDoc?.message)} {...register("codDoc")} />
          </Field>

          <Field id="estab" label="Establecimiento" tag="estab" error={errors.estab?.message}>
            <Input size="sm" maxLength={3} className={mono} {...a11y("estab", errors.estab?.message)} {...register("estab")} />
          </Field>

          <Field id="ptoEmi" label="Punto de emisión" tag="ptoEmi" error={errors.ptoEmi?.message}>
            <Input size="sm" maxLength={3} className={mono} {...a11y("ptoEmi", errors.ptoEmi?.message)} {...register("ptoEmi")} />
          </Field>

          <Field id="secuencial" label="Secuencial" tag="secuencial" error={errors.secuencial?.message}>
            <Input size="sm" maxLength={9} className={mono} {...a11y("secuencial", errors.secuencial?.message)} {...register("secuencial")} />
          </Field>

          <Field id="paymentMethod" label="Método de pago" error={errors.paymentMethod?.message} className="col-span-2">
            <Input size="sm" readOnly className={mono} {...a11y("paymentMethod", errors.paymentMethod?.message)} {...register("paymentMethod")} />
          </Field>

          <Field id="documentVersion" label="Versión del documento" error={errors.documentVersion?.message} className="col-span-2">
            <Input size="sm" readOnly className={mono} {...a11y("documentVersion", errors.documentVersion?.message)} {...register("documentVersion")} />
          </Field>
        </div>
      </section>

      <section>
        <div className="mb-6 flex flex-wrap items-start justify-between gap-4">
          <SectionHeader n={3} title="Ítems y tributos" className="mb-0">
            Cada línea de la factura con su código de impuesto.
          </SectionHeader>
          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={() =>
              append({
                mainCode: "",
                auxiliaryCode: "",
                description: "",
                quantity: 1,
                price: 0,
                taxCode: "2",
                taxPercentageCode: "4",
                taxRate: 15,
              })
            }
          >
            <HugeiconsIcon icon={Add01Icon} size={16} strokeWidth={1.8} />
            Añadir ítem
          </Button>
        </div>

        <ol className="divide-y divide-dashed divide-border border-y border-dashed border-border">
          {fields.map((field, index) => {
            const e = errors.items?.[index];
            const id = (name: string) => `item-${index}-${name}`;
            return (
              <li key={field.id} className="py-6">
                <div className="mb-4 flex items-center justify-between">
                  <p className="font-mono text-[11px] uppercase tracking-[0.16em] text-muted-foreground">
                    Ítem <span className="tabular-nums text-foreground">{String(index + 1).padStart(2, "0")}</span>
                  </p>
                  {fields.length > 1 && (
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      onClick={() => remove(index)}
                      aria-label={`Quitar ítem ${index + 1}`}
                      className="w-[var(--ctl-sm)] px-0 text-muted-foreground"
                    >
                      <HugeiconsIcon icon={Cancel01Icon} size={16} strokeWidth={1.6} />
                    </Button>
                  )}
                </div>

                <div className="grid grid-cols-2 gap-x-5 gap-y-5 md:grid-cols-4">
                  <Field id={id("description")} label="Descripción" error={e?.description?.message} className="col-span-2 md:col-span-4">
                    <Input size="sm" placeholder="Laptop Lenovo ThinkPad E16" {...a11y(id("description"), e?.description?.message)} {...register(`items.${index}.description`)} />
                  </Field>
                  <Field id={id("mainCode")} label="Código principal" error={e?.mainCode?.message}>
                    <Input size="sm" placeholder="P001" className={mono} {...a11y(id("mainCode"), e?.mainCode?.message)} {...register(`items.${index}.mainCode`)} />
                  </Field>
                  <Field id={id("auxiliaryCode")} label="Código auxiliar" error={e?.auxiliaryCode?.message}>
                    <Input size="sm" placeholder="A001" className={mono} {...a11y(id("auxiliaryCode"), e?.auxiliaryCode?.message)} {...register(`items.${index}.auxiliaryCode`)} />
                  </Field>
                  <Field id={id("quantity")} label="Cantidad" error={e?.quantity?.message}>
                    <Input size="sm" type="number" min={1} step="1" className={mono} {...a11y(id("quantity"), e?.quantity?.message)} {...register(`items.${index}.quantity`, { valueAsNumber: true })} />
                  </Field>
                  <Field id={id("price")} label="Precio unitario" error={e?.price?.message}>
                    <Input size="sm" type="number" min={0.01} step="0.01" className={mono} {...a11y(id("price"), e?.price?.message)} {...register(`items.${index}.price`, { valueAsNumber: true })} />
                  </Field>
                  <Field id={id("taxCode")} label="Código de impuesto" tag="codigo" error={e?.taxCode?.message}>
                    <Input size="sm" placeholder="2" className={mono} {...a11y(id("taxCode"), e?.taxCode?.message)} {...register(`items.${index}.taxCode`)} />
                  </Field>
                  <Field id={id("taxPercentageCode")} label="Código de tarifa" tag="codigoPorcentaje" error={e?.taxPercentageCode?.message}>
                    <Input size="sm" placeholder="4" className={mono} {...a11y(id("taxPercentageCode"), e?.taxPercentageCode?.message)} {...register(`items.${index}.taxPercentageCode`)} />
                  </Field>
                  <Field id={id("taxRate")} label="IVA %" error={e?.taxRate?.message}>
                    <Input size="sm" type="number" min={0} step="0.01" className={mono} {...a11y(id("taxRate"), e?.taxRate?.message)} {...register(`items.${index}.taxRate`, { valueAsNumber: true })} />
                  </Field>
                </div>
              </li>
            );
          })}
        </ol>

        {errors.items?.root && (
          <p role="alert" className="mt-3 text-sm text-[var(--v-danger-ink)]">
            {errors.items.root.message}
          </p>
        )}
      </section>

      <div className="flex flex-wrap items-center gap-4 border-t border-border pt-8">
        <Button type="submit" size="lg" loading={isRunning} className="min-w-[15rem]">
          {isRunning ? "Enviando al SRI…" : "Generar y enviar al SRI"}
        </Button>
        <p className="text-sm text-muted-foreground">Se envía al ambiente de pruebas; no tiene validez tributaria.</p>
      </div>
    </form>
  );
}

const mono = "font-mono tabular-nums";

function Field({
  id,
  label,
  tag,
  error,
  className,
  children,
}: {
  id: string;
  label: string;
  /** XML element name in the SRI schema, shown for developers mapping their own data. */
  tag?: string;
  error?: string;
  className?: string;
  children: ReactNode;
}) {
  return (
    // Label grows to fill the row so inputs line up even when a tag wraps.
    <div className={cn("flex flex-col gap-2", className)}>
      <Label htmlFor={id} size="sm" className="flex flex-1 flex-wrap items-end gap-x-2">
        {label}
        {tag && <span className="font-mono text-[11px] font-normal text-muted-foreground">{tag}</span>}
      </Label>
      {children}
      {error && (
        <p id={`${id}-error`} className="text-[13px] text-[var(--v-danger-ink)]">
          {error}
        </p>
      )}
    </div>
  );
}

function SectionHeader({
  n,
  title,
  className,
  children,
}: {
  n: number;
  title: string;
  className?: string;
  children: ReactNode;
}) {
  return (
    <div className={cn("mb-6", className)}>
      <h2 className="flex items-center gap-3 font-heading text-xl font-bold tracking-tight">
        <span
          aria-hidden="true"
          className="flex size-7 shrink-0 items-center justify-center rounded-full border border-foreground font-mono text-xs tabular-nums"
        >
          {n}
        </span>
        {title}
      </h2>
      <p className="mt-2 text-sm text-muted-foreground">{children}</p>
    </div>
  );
}
