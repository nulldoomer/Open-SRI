// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.valueobjects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Objects;

/**
 * Represents the issuance date of a tax document.
 *
 * <p>This value object ensures that the date is valid and formatted according to tax authority
 * requirements.
 *
 * <p>Instances are immutable, validated at creation time, and guaranteed not to represent a future
 * date.
 */
public record IssueDate(LocalDate date) {
  public IssueDate {
    Objects.requireNonNull(date, "IssueDate cannot be null");

    if (date.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("IssueDate cannot be after the current date");
    }
  }

  /**
   * Creates an {@code IssueDate} using the current system date.
   *
   * @return issue date representing today
   */
  public static IssueDate now() {
    return new IssueDate(LocalDate.now());
  }

  /**
   * Creates an {@code IssueDate} from an ISO-like input string in {@code yyyy-MM-dd} format.
   *
   * @param value textual date representation to parse
   * @return validated issue date built from the provided string
   * @throws IllegalArgumentException if the input cannot be parsed or represents an invalid date
   */
  public static IssueDate from(String value) {
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    try {
      LocalDate date = LocalDate.parse(value, formatter);

      return new IssueDate(date);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid IssueDate format" + e.getMessage());
    }
  }

  /**
   * Formats the issue date using the SRI document representation.
   *
   * @return date formatted as {@code dd/MM/yyyy}
   */
  public String format() {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    return date.format(formatter);
  }
}
