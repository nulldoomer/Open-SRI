// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.client.steps;

import io.github.opensri.api.client.OpenSRIClient;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.enums.Environment;
import java.util.Objects;

/**
 * Drives the step-by-step construction of an {@link OpenSRIClient}.
 *
 * <p>This builder implementation enforces the required client configuration order through the step
 * interfaces it implements, ensuring that environment, certificate data, issuer profile, and
 * timeout are all provided before the SDK client is created.
 *
 * <p>It is used internally by {@link io.github.opensri.api.builders.client.OpenSRIClientBuilder} to
 * expose a fluent API without leaking mutable configuration state to SDK consumers.
 *
 * @see OpenSRIClient
 */
public final class Steps
    implements EnvironmentStep,
        CertificateStep,
        CertificatePasswordStep,
        CertificateAliasStep,
        IssuerProfileStep,
        TimeoutStep,
        BuildStep {

  /** Creates a new builder Steps instance. */
  public Steps() {}

  private Environment environment;
  private byte[] certificate;
  private String certificatePassword;
  private String certificateAlias;
  private IssuerProfile issuerProfile;
  private int timeoutSeconds;

  /**
   * Stores the password used to unlock the signing certificate.
   *
   * @param password password associated with the certificate bytes previously provided
   * @return next step that requires the certificate alias
   */
  @Override
  public CertificateAliasStep certificatePassword(String password) {
    this.certificatePassword =
        Objects.requireNonNull(password, "Password for the signing certificate is required");
    return this;
  }

  /**
   * Stores the PKCS#12 certificate bytes that will be used to sign XML documents.
   *
   * @param certificate certificate contents in binary form
   * @return next step that requires the certificate password
   */
  @Override
  public CertificatePasswordStep certificate(byte[] certificate) {
    this.certificate =
        Objects.requireNonNull(certificate, "Must have a certificate for signing the XML");
    return this;
  }

  /**
   * Selects the SRI environment used by the client.
   *
   * @param environment target SRI environment for request routing
   * @return next step that requires the signing certificate
   */
  @Override
  public CertificateStep environment(Environment environment) {
    this.environment = Objects.requireNonNull(environment, "Environment must not be null");
    return this;
  }

  /**
   * Stores the issuer profile that will be reused across client operations.
   *
   * @param issuerProfile issuer tax profile associated with the client instance
   * @return next step that requires the timeout configuration
   */
  @Override
  public TimeoutStep issuerProfile(IssuerProfile issuerProfile) {
    this.issuerProfile = Objects.requireNonNull(issuerProfile, "Issuer profile is required");
    return this;
  }

  /**
   * Creates an {@link OpenSRIClient} with the configuration collected by the builder flow.
   *
   * <p>The returned client is ready to use with the selected environment, certificate, issuer
   * profile, and timeout gathered in the previous steps.
   *
   * @return fully configured SDK client instance
   */
  @Override
  public OpenSRIClient build() {
    return new OpenSRIClient(
        environment,
        certificate,
        certificatePassword,
        certificateAlias,
        issuerProfile,
        timeoutSeconds);
  }

  /**
   * Stores the alias of the certificate entry that will be used for signing.
   *
   * @param alias certificate alias inside the provided keystore
   * @return next step that requires the issuer profile
   */
  @Override
  public IssuerProfileStep certificateAlias(String alias) {
    this.certificateAlias = Objects.requireNonNull(alias, "Certificate alias must not be null");
    return this;
  }

  /**
   * Sets the HTTP connection timeout for SRI SOAP requests.
   *
   * @param seconds maximum number of seconds to wait for a response; must be greater than zero
   * @return next step that allows building the client
   * @throws IllegalArgumentException if {@code seconds} is zero or negative
   */
  @Override
  public BuildStep timeout(int seconds) {
    if (seconds <= 0) {
      throw new IllegalArgumentException("Timeout must be greater than zero seconds");
    }
    this.timeoutSeconds = seconds;
    return this;
  }
}
