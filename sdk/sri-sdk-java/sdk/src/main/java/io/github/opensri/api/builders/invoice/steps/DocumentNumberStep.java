package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.common.DocumentNumber;

public interface DocumentNumberStep {
    ClientStep documentNumber(DocumentNumber documentNumber);
}
