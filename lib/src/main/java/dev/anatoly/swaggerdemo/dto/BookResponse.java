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
 *   akrivitsky.dev@proton.me
 */
package dev.anatoly.swaggerdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response DTO as a record.
 * Immutable, perfect for JSON serialization in Boot 4.
 */
@Schema(name = "BookResponse", description = "Book resource returned by the API")
public record BookResponse(

        @Schema(description = "Database identifier", example = "1")
        Long id,

        @Schema(description = "Book title", example = "Clean Code")
        String title,

        @Schema(description = "Book author full name", example = "Robert C. Martin")
        String author,

        @Schema(description = "ISBN-10 or ISBN-13 identifier", example = "9780132350884")
        String isbn,

        @Schema(description = "Publication date in ISO format", example = "2008-08-01")
        LocalDate publishedDate,

        @Schema(description = "Unit price in USD", example = "39.99")
        BigDecimal price,

        @Schema(description = "Quantity currently in stock", example = "12")
        Integer stockQuantity,

        @Schema(description = "Human-readable summary", example = "A practical handbook of software craftsmanship.")
        String description
) {}