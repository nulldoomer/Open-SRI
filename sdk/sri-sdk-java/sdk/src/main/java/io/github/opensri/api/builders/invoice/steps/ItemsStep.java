package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.invoice.InvoiceItem;

public interface ItemsStep {
    ItemsStep addItem(InvoiceItem item);
    BuildStep doneItems();
}
