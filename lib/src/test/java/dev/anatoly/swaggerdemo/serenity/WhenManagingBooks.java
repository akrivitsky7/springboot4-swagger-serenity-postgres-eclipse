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
package dev.anatoly.swaggerdemo.serenity;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.annotations.Title;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static net.serenitybdd.rest.SerenityRest.given;
import static net.serenitybdd.rest.SerenityRest.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SerenityJUnit5Extension.class)
class WhenManagingBooks {

    @Test
    @Title("List all books")
    void should_list_all_books() {
        when()
                .get("http://localhost:8080/api/books")
        .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    @Title("Find books by author")
    void should_find_books_by_author() {
        given()
                .queryParam("author", "Martin")
        .when()
                .get("http://localhost:8080/api/books/search/by-author")
        .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("[0].author", equalTo("Robert C. Martin"));
    }

    @Test
    @Title("Get one book by ISBN")
    void should_get_book_by_isbn() {
        given()
                .queryParam("isbn", "9780132350884")
        .when()
                .get("http://localhost:8080/api/books/search/by-isbn")
        .then()
                .statusCode(200)
                .body("title", equalTo("Clean Code"))
                .body("author", equalTo("Robert C. Martin"));
    }
}