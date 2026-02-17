package io.github.opensri.domain.entities.profile;

import io.github.opensri.domain.enums.AccountingObligation;
import io.github.opensri.domain.valueobjects.Ruc;

public record IssuerProfile (
        Ruc ruc,
        SpecialTaxPayer specialTaxPayer,
        AccountingObligation accountingObligation
){
}
