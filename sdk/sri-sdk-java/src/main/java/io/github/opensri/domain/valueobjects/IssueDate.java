// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.valueobjects;

import io.github.opensri.shared.exceptions.OpenSRIValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Representa la fecha de emisión de un documento tributario.
 *
 * <p>Este objeto de valor asegura que la fecha sea válida y esté formateada de acuerdo con los
 * requisitos de la autoridad tributaria.
 *
 * <p>Las instancias son inmutables, se validan al momento de su creación y se garantiza que no
 * representan una fecha futura.
 *
 * @param date objeto LocalDate que representa la fecha de emisión
 */
public record IssueDate(LocalDate date) {
  /**
   * Constructor compacto para validar que la fecha no sea nula ni futura.
   *
   * @param date fecha de emisión
   */
  public IssueDate {
    if (date == null) {
      throw new OpenSRIValidationException("IssueDate cannot be null");
    }

    if (date.isAfter(LocalDate.now())) {
      throw new OpenSRIValidationException("IssueDate cannot be after the current date");
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
   * @throws OpenSRIValidationException if the input cannot be parsed or represents an invalid date
   */
  public static IssueDate from(String value) {
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    try {
      LocalDate date = LocalDate.parse(value, formatter);

      return new IssueDate(date);
    } catch (DateTimeParseException e) {
      throw new OpenSRIValidationException("Invalid IssueDate format" + e.getMessage());
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
