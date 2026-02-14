package io.github.opensri.domain.entities.common;

import io.github.opensri.domain.valueobjects.ClientIdentification;

public record Client(
        ClientIdentification identification,
        String names
) {
}
