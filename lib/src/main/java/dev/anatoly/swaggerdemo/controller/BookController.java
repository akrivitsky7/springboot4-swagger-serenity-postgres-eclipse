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
package dev.anatoly.swaggerdemo.controller;

import dev.anatoly.swaggerdemo.dto.BookCreateRequest;
import dev.anatoly.swaggerdemo.dto.BookResponse;
import dev.anatoly.swaggerdemo.dto.BookUpdateRequest;
import dev.anatoly.swaggerdemo.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "CRUD operations for book catalog entries")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    @Operation(
            summary = "List all books",
            description = "Returns every book currently stored in the PostgreSQL database."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Books returned successfully"
    )
    public List<BookResponse> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get one book by id",
            description = "Finds a single book using its numeric database identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "bookNotFound",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "Book with id 999 was not found",
                                              "instance": "/api/books/999",
                                              "timestamp": "2026-04-09T12:34:56Z",
                                              "path": "/api/books/999"
                                            }
                                            """
                            )
                    )
            )
    })
    public BookResponse getBookById(
            @PathVariable("id")
            @Parameter(description = "Book primary key", example = "1")
            Long id
    ) {
        return bookService.findById(id);
    }

    @GetMapping("/search/by-author")
    @Operation(
            summary = "Search books by author",
            description = "Returns all books whose author contains the supplied text, case-insensitively."
    )
    @ApiResponse(responseCode = "200", description = "Matching books returned successfully")
    public List<BookResponse> getBooksByAuthor(
            @RequestParam("author")
            @Parameter(
                    description = "Partial or full author name",
                    example = "Martin"
            )
            String author
    ) {
        return bookService.findByAuthor(author);
    }

    @GetMapping("/search/by-isbn")
    @Operation(
            summary = "Get one book by ISBN",
            description = "Returns a single book by its ISBN code."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public BookResponse getBookByIsbn(
            @RequestParam("isbn")
            @Parameter(
                    description = "ISBN-10 or ISBN-13",
                    example = "9780132350884"
            )
            String isbn
    ) {
        return bookService.findByIsbn(isbn);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new book",
            description = "Creates a new book record. This operation demonstrates request-body examples and field-level @Schema examples."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "validationError",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Bad Request",
                                              "status": 400,
                                              "detail": "Validation failed",
                                              "instance": "/api/books",
                                              "timestamp": "2026-04-09T12:34:56Z",
                                              "path": "/api/books",
                                              "details": [
                                                "isbn: must be a valid ISBN",
                                                "stockQuantity: must be greater than or equal to 0"
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "409", description = "ISBN already exists")
    })
    public BookResponse createBook(
            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Book payload to create",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "createBookExample",
                                    value = """
                                            {
                                              "title": "Domain-Driven Design",
                                              "author": "Eric Evans",
                                              "isbn": "9780321125217",
                                              "publishedDate": "2003-08-30",
                                              "price": 54.99,
                                              "stockQuantity": 5,
                                              "description": "Classic book on strategic and tactical domain-driven design."
                                            }
                                            """
                            )
                    )
            )
            BookCreateRequest request
    ) {
        return bookService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing book",
            description = "Replaces the current data of a book with the supplied payload."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    public BookResponse updateBook(
            @PathVariable("id")
            @Parameter(description = "Book primary key", example = "1")
            Long id,
            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Book payload to update",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookUpdateRequest.class),
                            examples = @ExampleObject(
                                    name = "updateBookExample",
                                    value = """
                                            {
                                              "title": "Clean Code, Updated Printing",
                                              "author": "Robert C. Martin",
                                              "isbn": "9780132350884",
                                              "publishedDate": "2008-08-01",
                                              "price": 44.99,
                                              "stockQuantity": 8,
                                              "description": "Updated warehouse record for the Clean Code title."
                                            }
                                            """
                            )
                    )
            )
            BookUpdateRequest request
    ) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a book",
            description = "Deletes a book by id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public void deleteBook(
            @PathVariable("id")
            @Parameter(description = "Book primary key", example = "2")
            Long id
    ) {
        bookService.delete(id);
    }
}