package io.github.opensri.domain.valueobjects;

import io.github.opensri.domain.enums.IdentificationType;

/**
 * Represents the special "Final Consumer" identification.
 *
 * <p>This identification is a fixed value defined by
 * the tax authority and does not vary.
 *
 * <p>Instances of this class always represent
 * the constant final consumer identification number.
 */
public final class FinalConsumer implements ClientIdentification {
    private static final FinalConsumer INSTANCE = new FinalConsumer();
    private static final String VALUE = "9999999999999";

    private FinalConsumer() {}

    public static FinalConsumer instance() {
        return INSTANCE;
    }

    @Override
    public IdentificationType identificationType() {
        return IdentificationType.VENTA_CONSUMIDOR_FINAL;
    }

    @Override
    public String value() {
        return VALUE;
    }

}
