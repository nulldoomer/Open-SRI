package io.github.opensri.domain.valueobjects;

public final class FinalConsumer implements ClientIdentification {
    private static final String VALUE = "9999999999999";

    private FinalConsumer() {}

    public static String value() {
        return VALUE;
    }
}
