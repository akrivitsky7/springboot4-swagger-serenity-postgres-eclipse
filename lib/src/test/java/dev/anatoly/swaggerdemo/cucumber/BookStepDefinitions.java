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
package dev.anatoly.swaggerdemo.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ValidatableResponse;
import net.serenitybdd.rest.SerenityRest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class BookStepDefinitions {

    private ValidatableResponse response;

    @When("I request all books")
    public void iRequestAllBooks() {
        response = SerenityRest
                .when()
                .get("http://localhost:8080/api/books")
                .then();
    }

    @When("I search books by author {string}")
    public void iSearchBooksByAuthor(String author) {
        response = SerenityRest
                .given()
                .queryParam("author", author)
                .when()
                .get("http://localhost:8080/api/books/search/by-author")
                .then();
    }

    @When("I search book by ISBN {string}")
    public void iSearchBookByIsbn(String isbn) {
        response = SerenityRest
                .given()
                .queryParam("isbn", isbn)
                .when()
                .get("http://localhost:8080/api/books/search/by-isbn")
                .then();
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        response.statusCode(statusCode);
    }

    @And("the response should contain at least {int} book")
    public void theResponseShouldContainAtLeastBook(int minSize) {
        response.body("$", hasSize(greaterThanOrEqualTo(minSize)));
    }

    @And("the response should contain a book by {string}")
    public void theResponseShouldContainABookBy(String author) {
        response.body("author", hasItem(author));
    }

    @And("the response should contain the title {string}")
    public void theResponseShouldContainTheTitle(String title) {
        response.body("title", equalTo(title));
    }
}