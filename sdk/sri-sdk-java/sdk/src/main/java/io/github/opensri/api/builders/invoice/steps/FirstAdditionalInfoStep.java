package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.entities.invoice.Invoice;

public interface FirstAdditionalInfoStep {
    AdditionalInfoStep addInfo(AdditionalInfo info);
    Invoice build();
}
