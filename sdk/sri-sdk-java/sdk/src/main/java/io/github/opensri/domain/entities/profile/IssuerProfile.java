package io.github.opensri.domain.entities.profile;

import io.github.opensri.domain.enums.AccountingObligation;
import io.github.opensri.domain.valueobjects.Ruc;
import io.github.opensri.domain.valueobjects.SpecialTaxPayer;

public record IssuerProfile (
        Ruc ruc,
        SpecialTaxPayer specialTaxPayer,
        AccountingObligation accountingObligation
){
}
