package io.github.opensri.playground_service.domain.model;

public record PayloadMessage (
        String identifier,
        String message,
        String additionalInformation,
        String type
){
}
