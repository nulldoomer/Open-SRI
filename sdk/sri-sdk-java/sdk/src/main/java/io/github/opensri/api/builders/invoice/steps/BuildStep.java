package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.invoice.Invoice;

public interface BuildStep {
    Invoice build();
}
