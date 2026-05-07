package io.github.opensri.infrastructure.crypto.signing;

import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.infrastructure.crypto.certificates.model.SigningKey;

public class XAdEsSignerFactory {
    public static DocumentSigner create(SigningKey signingKey){
        return new XAdEsSigner(signingKey);
    }
}
