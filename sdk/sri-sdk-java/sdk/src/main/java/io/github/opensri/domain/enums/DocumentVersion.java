// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.enums;

/**
 * Represents the XML schema version used when serializing SRI documents.
 *
 * <p>This enum centralizes the supported version strings for electronic document
 * payloads so builders and serializers can select the appropriate XML version
 * without hardcoding literal values across the SDK.
 */
public enum DocumentVersion {

    VERSION_100("1.0.0"),
    VERSION_110("1.1.0"),
    VERSION_200("2.0.0"),
    VERSION_210("2.1.0");
    private String  version;

    DocumentVersion(String version) {
        this.version = version;
    }

    /**
     * Returns the raw version string that must be written into the XML root element.
     *
     * @return document version identifier such as {@code 1.1.0} or {@code 2.1.0}
     */
    public String getVersion() {
        return version;
    }

    /**
     * Updates the underlying version string.
     *
     * @param version version value to assign to this enum entry
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
