package io.github.opensri.domain.valueobjects;

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
    private static final String VALUE = "9999999999999";

    private FinalConsumer() {}

    public static String value() {
        return VALUE;
    }
}
