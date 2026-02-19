package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.invoice.InvoiceItem;

public interface FirstItemStep {
    ItemsStep addItem(InvoiceItem item);
}
