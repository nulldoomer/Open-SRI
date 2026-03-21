package io.github.opensri.application.ports;

import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.TaxInfo;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.domain.valueobjects.IssueDate;

public interface AccessKeyGenerator {

    String generate(IssueDate date, DocumentNumber documentNumber,
                    TaxInfo taxInfo, Environment environment);
}
