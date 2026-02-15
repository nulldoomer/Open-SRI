package io.github.opensri.domain.valueobjects;

/**
 * Represents a client identification used in electronic tax documents.
 *
 * <p>This is a sealed hierarchy that models all valid identification
 * types accepted by the tax authority (SRI).
 *
 * <p>Each implementation encapsulates its own validation rules and
 * guarantees that the identification number is always valid according
 * to its type.
 *
 * <p>The permitted implementations are:
 * <ul>
 *     <li>{@link Ruc}</li>
 *     <li>{@link NationalId}</li>
 *     <li>{@link Passport}</li>
 *     <li>{@link ForeignId}</li>
 *     <li>{@link FinalConsumer}</li>
 * </ul>
 *
 * <p>This interface ensures that invalid identification states
 * cannot be represented in the domain model.
 */
public sealed interface ClientIdentification
permits Ruc, NationalId, Passport, ForeignId, FinalConsumer{
}
