package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.taxinfo.TaxInfo;

public interface TaxInfoStep {
    DocumentNumberStep taxInfo(TaxInfo taxInfo);
}
