package io.github.opensri.api.builders.invoice;

import io.github.opensri.api.builders.invoice.steps.IssueDateStep;
import io.github.opensri.api.builders.invoice.steps.Steps;

public final class InvoiceBuilder {

    private InvoiceBuilder (){}

    public static IssueDateStep builder(){
        return new Steps();
    }
}
