package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.common.Client;

public interface ClientStep {
    FirstItemStep client(Client client);
}
