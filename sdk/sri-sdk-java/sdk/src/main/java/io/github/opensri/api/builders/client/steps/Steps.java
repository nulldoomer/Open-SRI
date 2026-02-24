package io.github.opensri.api.builders.client.steps;

import io.github.opensri.api.client.OpenSRIClient;
import io.github.opensri.domain.entities.common.IssuerProfile;
import io.github.opensri.domain.enums.Environment;


import java.util.Objects;

public final class Steps implements EnvironmentStep, CertificateStep,
        CertificatePasswordStep, CertificateAliasStep, IssuerProfileStep, TimeoutStep,
        BuildStep

{

    private Environment environment;
    private byte[] certificate;
    private String certificatePassword;
    private String certificateAlias;
    private IssuerProfile issuerProfile;
    private int timeoutSeconds;


    @Override
    public CertificateAliasStep certificatePassword(String password) {
        this.certificatePassword = Objects.requireNonNull(password,
        "Password for the signing certificate is required");
        return this;
    }

    @Override
    public CertificatePasswordStep certificate(byte[] certificate) {
        this.certificate = Objects.requireNonNull(certificate,
                "Must have a certificate for signing the XML");
        return this;
    }

    @Override
    public CertificateStep environment(Environment environment) {
        this.environment = Objects.requireNonNull(environment,
                "Environment must not be null");
        return this;
    }

    @Override
    public TimeoutStep issuerProfile(IssuerProfile issuerProfile) {
        this.issuerProfile = Objects.requireNonNull(issuerProfile,
                "Issuer profile is required");
        return this;
    }

    @Override
    public OpenSRIClient build() {
        return new OpenSRIClient(
                environment, certificate, certificatePassword,
                certificateAlias, issuerProfile, timeoutSeconds
        );
    }


    @Override
    public IssuerProfileStep certificateAlias(String alias) {
        this.certificateAlias = Objects.requireNonNull(alias,
                "Certificate alias must not be null");
        return this;
    }

    @Override
    public BuildStep timeout(int seconds) {
        this.timeoutSeconds = seconds;
        return this;
    }
}
