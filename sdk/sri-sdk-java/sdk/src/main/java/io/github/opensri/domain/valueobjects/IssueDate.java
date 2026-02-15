package io.github.opensri.domain.valueobjects;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents the issuance date of a tax document.
 *
 * <p>This value object ensures that the date is valid
 * and formatted according to tax authority requirements.
 *
 * <p>Instances are immutable and validated at creation time.
 */
public record IssueDate (
        LocalDate date
){
    public IssueDate {
        Objects.requireNonNull(date, "IssueDate cannot be null");

        if(date.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("IssueDate cannot be after the current date");
        }
    }

    public static IssueDate now(){
        return new IssueDate(LocalDate.now());
    }
}
