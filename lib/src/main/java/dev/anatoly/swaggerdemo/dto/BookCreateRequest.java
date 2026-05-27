/*
 * Copyright (c) 2026 Anatoly S. Krivitsky, Ph.D.
 * All rights reserved.
 *
 * Permission is granted, free of charge, to use, copy, distribute, and publish
 * this code, provided that this copyright notice is preserved.
 *
 * This code is provided "as is", without warranty of any kind. The user assumes
 * all responsibility and risk for its use. Under no circumstances shall the
 * author be liable for any direct, indirect, incidental, special, consequential,
 * exemplary, or punitive damages, lost profits, data loss, or other losses
 * arising from the use of, inability to use, or reliance on this code.
 *
 * Questions and comments:
 *   akrivitsky@yahoo.com
 *   akrivitsky@gmail.com
 */
package dev.anatoly.swaggerdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.ISBN;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Spring Boot 4.0.5 idiomatic DTO.
 * Using a Java record removes boilerplate getters/setters.
 * Jakarta Validation works directly on record components.
 * Springdoc OpenAPI reads @Schema on components for docs.
 */
@Schema(name = "BookCreateRequest", description = "Payload used to create a new book record")
public record BookCreateRequest(

        @NotBlank
        @Size(max = 200)
        @Schema(description = "Book title", example = "Clean Code")
        String title,

        @NotBlank
        @Size(max = 120)
        @Schema(description = "Book author full name", example = "Robert C. Martin")
        String author,

        @NotBlank
        @ISBN
        @Size(max = 20)
        @Schema(description = "ISBN-10 or ISBN-13 identifier", example = "9780132350884")
        String isbn,

        @PastOrPresent
        @Schema(description = "Publication date in ISO format", example = "2008-08-01")
        LocalDate publishedDate,

        @DecimalMin(value = "0.00")
        @Digits(integer = 8, fraction = 2)
        @Schema(description = "Unit price in USD", example = "39.99")
        BigDecimal price,

        @NotNull
        @Min(0)
        @Schema(description = "Quantity currently in stock", example = "12")
        Integer stockQuantity,

        @Size(max = 1000)
        @Schema(description = "Human-readable summary", example = "A practical handbook of software craftsmanship.")
        String description
) {}