package io.github.opensri.infrastructure.services;

import io.github.opensri.application.ports.AccessKeyGenerator;

public class SRIAccessKeyGeneratorFactory {
    public static AccessKeyGenerator create(){
        return new SRIAccessKeyGenerator();
    }
}
