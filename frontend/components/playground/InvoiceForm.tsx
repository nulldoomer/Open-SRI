"use client";

import { useForm, useFieldArray } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { HugeiconsIcon } from "@hugeicons/react";
import { Cancel01Icon } from "@hugeicons/core-free-icons";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import type { InvoiceFormData } from "@/types/playground";

const schema = z.object({
  rucEmisor: z
    .string()
    .length(13, "El RUC debe tener exactamente 13 dígitos")
    .regex(/^\d+$/, "Solo dígitos"),
  nombreEmisor: z.string().min(1, "Campo requerido"),
  identificacionCliente: z
    .string()
    .min(10, "Mínimo 10 dígitos")
    .regex(/^\d+$/, "Solo dígitos"),
  nombreCliente: z.string().min(1, "Campo requerido"),
  ambiente: z.enum(["PRUEBAS", "PRODUCCION"]),
  lenguaje: z.enum(["Java", "CSharp", "Go"]),
  items: z
    .array(
      z.object({
        descripcion: z.string().min(1, "Campo requerido"),
        cantidad: z.number().min(1, "Mínimo 1"),
        precio: z.number().min(0.01, "Precio debe ser mayor a 0"),
      })
    )
    .min(1, "Agrega al menos un ítem"),
});

interface InvoiceFormProps {
  onSubmit: (data: InvoiceFormData) => void;
  isRunning: boolean;
}

export default function InvoiceForm({ onSubmit, isRunning }: InvoiceFormProps) {
  const {
    register,
    handleSubmit,
    control,
    setValue,
    watch,
    formState: { errors },
  } = useForm<InvoiceFormData>({
    resolver: zodResolver(schema),
    defaultValues: {
      rucEmisor: "1790000000001",
      nombreEmisor: "Mi Empresa S.A.",
      identificacionCliente: "1234567890",
      nombreCliente: "Juan Pérez",
      ambiente: "PRUEBAS",
      lenguaje: "Java",
      items: [{ descripcion: "Producto de prueba", cantidad: 1, precio: 100 }],
    },
  });

  const { fields, append, remove } = useFieldArray({ control, name: "items" });

  const lenguaje = watch("lenguaje");
  const ambiente = watch("ambiente");

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="space-y-1.5">
          <Label htmlFor="rucEmisor">RUC del emisor</Label>
          <Input
            id="rucEmisor"
            placeholder="1790000000001"
            maxLength={13}
            {...register("rucEmisor")}
          />
          {errors.rucEmisor && (
            <p className="text-xs text-destructive">{errors.rucEmisor.message}</p>
          )}
        </div>

        <div className="space-y-1.5">
          <Label htmlFor="nombreEmisor">Nombre del emisor</Label>
          <Input id="nombreEmisor" placeholder="Mi Empresa S.A." {...register("nombreEmisor")} />
          {errors.nombreEmisor && (
            <p className="text-xs text-destructive">{errors.nombreEmisor.message}</p>
          )}
        </div>

        <div className="space-y-1.5">
          <Label htmlFor="identificacionCliente">Identificación del cliente</Label>
          <Input
            id="identificacionCliente"
            placeholder="1234567890"
            maxLength={13}
            {...register("identificacionCliente")}
          />
          {errors.identificacionCliente && (
            <p className="text-xs text-destructive">{errors.identificacionCliente.message}</p>
          )}
        </div>

        <div className="space-y-1.5">
          <Label htmlFor="nombreCliente">Nombre del cliente</Label>
          <Input id="nombreCliente" placeholder="Juan Pérez" {...register("nombreCliente")} />
          {errors.nombreCliente && (
            <p className="text-xs text-destructive">{errors.nombreCliente.message}</p>
          )}
        </div>
      </div>

      <div className="flex gap-4 flex-wrap">
        <div className="space-y-1.5">
          <Label>Ambiente</Label>
          <div className="flex gap-2">
            {(["PRUEBAS", "PRODUCCION"] as const).map((a) => (
              <button
                key={a}
                type="button"
                onClick={() => setValue("ambiente", a)}
                className={`px-4 py-2 text-xs font-semibold tracking-widest uppercase border rounded-sm transition-colors ${
                  ambiente === a
                    ? "border-foreground bg-foreground text-background"
                    : "border-border text-muted-foreground hover:border-foreground/50"
                }`}
              >
                {a}
              </button>
            ))}
          </div>
        </div>

        <div className="space-y-1.5">
          <Label>SDK</Label>
          <div className="flex gap-2">
            {(["Java", "CSharp", "Go"] as const).map((l) => (
              <button
                key={l}
                type="button"
                onClick={() => setValue("lenguaje", l)}
                className={`px-4 py-2 text-xs font-semibold tracking-widest uppercase border rounded-sm transition-colors ${
                  lenguaje === l
                    ? "border-foreground bg-foreground text-background"
                    : "border-border text-muted-foreground hover:border-foreground/50"
                }`}
              >
                {l === "CSharp" ? "C#" : l}
              </button>
            ))}
          </div>
        </div>
      </div>

      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <Label>Ítems</Label>
          <Button
            type="button"
            variant="ghost"
            size="xs"
            onClick={() => append({ descripcion: "", cantidad: 1, precio: 0 })}
          >
            + Añadir ítem
          </Button>
        </div>

        {fields.map((field, i) => (
          <div key={field.id} className="flex gap-2 items-start">
            <div className="flex-1 space-y-1">
              <Input
                placeholder="Descripción"
                {...register(`items.${i}.descripcion`)}
              />
              {errors.items?.[i]?.descripcion && (
                <p className="text-xs text-destructive">
                  {errors.items[i]?.descripcion?.message}
                </p>
              )}
            </div>
            <div className="w-20 space-y-1">
              <Input
                type="number"
                placeholder="Cant."
                min={1}
                {...register(`items.${i}.cantidad`, { valueAsNumber: true })}
              />
              {errors.items?.[i]?.cantidad && (
                <p className="text-xs text-destructive">
                  {errors.items[i]?.cantidad?.message}
                </p>
              )}
            </div>
            <div className="w-28 space-y-1">
              <Input
                type="number"
                placeholder="Precio"
                step="0.01"
                min={0.01}
                {...register(`items.${i}.precio`, { valueAsNumber: true })}
              />
              {errors.items?.[i]?.precio && (
                <p className="text-xs text-destructive">
                  {errors.items[i]?.precio?.message}
                </p>
              )}
            </div>
            {fields.length > 1 && (
              <Button
                type="button"
                variant="ghost"
                size="icon-sm"
                onClick={() => remove(i)}
                className="text-muted-foreground hover:text-destructive mt-0.5"
              >
                <HugeiconsIcon icon={Cancel01Icon} size={14} strokeWidth={1.5} />
              </Button>
            )}
          </div>
        ))}
        {errors.items?.root && (
          <p className="text-xs text-destructive">{errors.items.root.message}</p>
        )}
      </div>

      <Button type="submit" disabled={isRunning} size="lg" className="w-full md:w-auto">
        {isRunning ? "Procesando..." : "Generar + enviar al SRI (sandbox)"}
      </Button>
    </form>
  );
}
