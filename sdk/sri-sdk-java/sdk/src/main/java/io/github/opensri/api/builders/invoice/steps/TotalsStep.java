package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.common.Totals;

public interface TotalsStep {
    FirstItemStep totals(Totals totals);
}
