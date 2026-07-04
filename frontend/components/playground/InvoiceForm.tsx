"use client";

import { useFieldArray, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import type { ReactNode } from "react";
import { HugeiconsIcon } from "@hugeicons/react";
import { Cancel01Icon } from "@hugeicons/core-free-icons";
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

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <section className="space-y-3">
        <SectionHeader
          eyebrow="Datos del comprobante"
          title="Emisor y comprador"
          description="Estos campos identifican quién emite la factura y a quién va dirigida."
        />

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 rounded-xl border border-border/70 bg-background/60 p-4">
          <Field label="RUC del emisor" error={errors.issuerRuc?.message}>
            <Input
              id="issuerRuc"
              placeholder="1791248678001"
              maxLength={13}
              className={fieldInputClass}
              {...register("issuerRuc")}
            />
          </Field>

          <Field label="Nombre del emisor" error={errors.issuerName?.message}>
            <Input
              id="issuerName"
              placeholder="OpenSRI Demo S.A."
              className={fieldInputClass}
              {...register("issuerName")}
            />
          </Field>

          <Field label="Dirección del establecimiento" error={errors.establishmentAddress?.message}>
            <Input
              id="establishmentAddress"
              placeholder="Av. Amazonas y Naciones Unidas, Quito"
              className={fieldInputClass}
              {...register("establishmentAddress")}
            />
          </Field>

          <Field label="Identificación del comprador" error={errors.buyerIdentification?.message}>
            <Input
              id="buyerIdentification"
              placeholder="1101160032"
              className={fieldInputClass}
              {...register("buyerIdentification")}
            />
          </Field>

          <Field label="Nombre del comprador" error={errors.buyerName?.message}>
            <Input id="buyerName" placeholder="Juan Pérez" className={fieldInputClass} {...register("buyerName")} />
          </Field>

          <Field label="Tipo de identificación" error={errors.buyerIdentificationType?.message}>
            <Input
              id="buyerIdentificationType"
              placeholder="05"
              maxLength={2}
              className={fieldInputClass}
              {...register("buyerIdentificationType")}
            />
          </Field>
        </div>
      </section>

      <section className="space-y-3">
        <SectionHeader
          eyebrow="Numeración fiscal"
          title="Serie y configuración"
          description="La serie fiscal y la versión del documento deben quedar legibles y separadas del resto."
        />

        <div className="grid grid-cols-2 md:grid-cols-5 gap-4 rounded-xl border border-border/70 bg-background/60 p-4">
          <Field label="codDoc" error={errors.codDoc?.message}>
            <Input id="codDoc" maxLength={2} className={fieldInputClass} {...register("codDoc")} />
          </Field>

          <Field label="estab" error={errors.estab?.message}>
            <Input id="estab" maxLength={3} className={fieldInputClass} {...register("estab")} />
          </Field>

          <Field label="ptoEmi" error={errors.ptoEmi?.message}>
            <Input id="ptoEmi" maxLength={3} className={fieldInputClass} {...register("ptoEmi")} />
          </Field>

          <Field label="secuencial" error={errors.secuencial?.message}>
            <Input id="secuencial" maxLength={9} className={fieldInputClass} {...register("secuencial")} />
          </Field>

          <Field label="Método de pago" error={errors.paymentMethod?.message}>
            <Input id="paymentMethod" readOnly className={fieldInputClass} {...register("paymentMethod")} />
          </Field>
        </div>

        <Field label="Versión del documento" error={errors.documentVersion?.message}>
          <Input id="documentVersion" readOnly className={fieldInputClass} {...register("documentVersion")} />
        </Field>
      </section>

      <section className="space-y-3">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-[11px] uppercase tracking-[0.22em] text-muted-foreground font-semibold">
              Detalle de la factura
            </p>
            <h3 className="text-sm font-semibold text-foreground mt-1">Ítems y tributos</h3>
          </div>
          <Button
            type="button"
            variant="outline"
            size="xs"
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
            + Añadir ítem
          </Button>
        </div>

        <div className="space-y-4">
          {fields.map((field, index) => (
            <article key={field.id} className="rounded-xl border border-border/70 bg-background/70 p-4">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <p className="text-[11px] uppercase tracking-[0.22em] text-muted-foreground font-semibold">
                    Ítem {String(index + 1).padStart(2, "0")}
                  </p>
                  <h4 className="text-sm font-semibold text-foreground mt-1">{`Línea de producto ${index + 1}`}</h4>
                </div>

                {fields.length > 1 && (
                  <Button
                    type="button"
                    variant="ghost"
                    size="icon-sm"
                    onClick={() => remove(index)}
                    className="text-muted-foreground hover:text-destructive"
                  >
                    <HugeiconsIcon icon={Cancel01Icon} size={14} strokeWidth={1.5} />
                  </Button>
                )}
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-5 gap-4">
                <Field label="Código principal" error={errors.items?.[index]?.mainCode?.message}>
                  <Input placeholder="P001" className={fieldInputClass} {...register(`items.${index}.mainCode`)} />
                </Field>

                <Field label="Código auxiliar" error={errors.items?.[index]?.auxiliaryCode?.message}>
                  <Input placeholder="A001" className={fieldInputClass} {...register(`items.${index}.auxiliaryCode`)} />
                </Field>

                <Field label="Descripción" className="md:col-span-2 xl:col-span-1" error={errors.items?.[index]?.description?.message}>
                  <Input
                    placeholder="Laptop Lenovo ThinkPad E16"
                    className={fieldInputClass}
                    {...register(`items.${index}.description`)}
                  />
                </Field>

                <Field label="Cantidad" error={errors.items?.[index]?.quantity?.message}>
                  <Input
                    type="number"
                    min={1}
                    step="1"
                    className={fieldInputClass}
                    {...register(`items.${index}.quantity`, { valueAsNumber: true })}
                  />
                </Field>

                <Field label="Precio" error={errors.items?.[index]?.price?.message}>
                  <Input
                    type="number"
                    min={0.01}
                    step="0.01"
                    className={fieldInputClass}
                    {...register(`items.${index}.price`, { valueAsNumber: true })}
                  />
                </Field>

                <Field label="taxCode" error={errors.items?.[index]?.taxCode?.message}>
                  <Input placeholder="2" className={fieldInputClass} {...register(`items.${index}.taxCode`)} />
                </Field>

                <Field label="taxPct" error={errors.items?.[index]?.taxPercentageCode?.message}>
                  <Input placeholder="4" className={fieldInputClass} {...register(`items.${index}.taxPercentageCode`)} />
                </Field>

                <Field label="IVA %" error={errors.items?.[index]?.taxRate?.message}>
                  <Input
                    type="number"
                    min={0}
                    step="0.01"
                    className={fieldInputClass}
                    {...register(`items.${index}.taxRate`, { valueAsNumber: true })}
                  />
                </Field>
              </div>
            </article>
          ))}
        </div>

        {errors.items?.root && <p className="text-xs text-destructive">{errors.items.root.message}</p>}
      </section>

      <Button type="submit" disabled={isRunning} size="lg" className="w-full md:w-auto">
        {isRunning ? "Procesando..." : "Generar + enviar al SRI"}
      </Button>
    </form>
  );
}

function Field({
  label,
  error,
  className,
  children,
}: {
  label: string;
  error?: string;
  className?: string;
  children: ReactNode;
}) {
  return (
    <div className={cn("space-y-1.5", className)}>
      <Label className="text-[11px] text-muted-foreground/80 tracking-[0.22em]">
        {label}
      </Label>
      <div className="rounded-md border border-border/70 bg-background px-3 py-2 shadow-sm">
        {children}
      </div>
      {error && <p className="text-xs text-destructive">{error}</p>}
    </div>
  );
}

function SectionHeader({
  eyebrow,
  title,
  description,
}: {
  eyebrow: string;
  title: string;
  description: string;
}) {
  return (
    <div className="space-y-1">
      <p className="text-[11px] uppercase tracking-[0.22em] text-muted-foreground font-semibold">
        {eyebrow}
      </p>
      <h2 className="text-lg font-semibold text-foreground">{title}</h2>
      <p className="text-sm text-muted-foreground max-w-2xl">{description}</p>
    </div>
  );
}

const fieldInputClass =
  "h-10 border-0 bg-transparent px-0 text-sm text-foreground placeholder:text-muted-foreground/60 focus-visible:ring-0 focus-visible:outline-none";
