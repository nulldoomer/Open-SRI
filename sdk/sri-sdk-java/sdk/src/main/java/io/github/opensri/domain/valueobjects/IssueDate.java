package io.github.opensri.domain.valueobjects;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
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

    // Static factory method for input to create a date from a string

    public static IssueDate from(String value){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDate date = LocalDate.parse(value, formatter);

            return new IssueDate(date);
        } catch (DateTimeParseException e){
            throw new IllegalArgumentException("Invalid IssueDate format"
                    + e.getMessage()
            );
        }
    }


    // Method to format the actual date

    public String format(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }
}
