package io.github.opensri.domain.valueobjects;

public sealed interface ClientIdentification
permits Ruc, NationalId, Passport, ForeignId, FinalConsumer{
}
