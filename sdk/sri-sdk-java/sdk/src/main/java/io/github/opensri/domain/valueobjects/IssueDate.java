package io.github.opensri.domain.valueobjects;

import java.time.LocalDate;
import java.util.Objects;

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
