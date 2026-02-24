package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.common.TaxInfo;

public interface TaxInfoStep {
    DocumentNumberStep taxInfo(TaxInfo taxInfo);
}
