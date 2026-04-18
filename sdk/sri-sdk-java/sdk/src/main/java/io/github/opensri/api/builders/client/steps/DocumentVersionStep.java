// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.client.steps;

import io.github.opensri.domain.enums.DocumentVersion;

/**
 * Defines the builder step that selects the XML document version used by the client.
 *
 * <p>This step allows SDK consumers to choose which SRI document schema version
 * should be used by serializers before the client instance is created.
 */
public interface DocumentVersionStep {
    /**
     * Stores the XML document version to be used by the generated client.
     *
     * @param version target document version for XML serialization
     * @return final step that can build the client
     */
    BuildStep documentVersion(DocumentVersion version);
}
