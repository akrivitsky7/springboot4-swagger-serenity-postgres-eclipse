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

import dev.anatoly.swaggerdemo.dto.BookResponse;
import dev.anatoly.swaggerdemo.exception.ResourceNotFoundException;
import dev.anatoly.swaggerdemo.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void getAllBooks_returns200AndJsonArray() throws Exception {
        List<BookResponse> books = List.of(
                new BookResponse(
                        1L,
                        "Clean Code",
                        "Robert C. Martin",
                        "9780132350884",
                        LocalDate.of(2008, 8, 1),
                        new BigDecimal("39.99"),
                        12,
                        "A practical handbook of software craftsmanship and clean coding principles."
                ),
                new BookResponse(
                        2L,
                        "Effective Java",
                        "Joshua Bloch",
                        "9780134685991",
                        LocalDate.of(2018, 1, 6),
                        new BigDecimal("49.99"),
                        7,
                        "Best practices for the Java platform."
                )
        );

        when(bookService.findAll()).thenReturn(books);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[1].title").value("Effective Java"));
    }

    @Test
    void getBookById_returns200AndBook() throws Exception {
        BookResponse book = new BookResponse(
                1L,
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                LocalDate.of(2008, 8, 1),
                new BigDecimal("39.99"),
                12,
                "A practical handbook of software craftsmanship and clean coding principles."
        );

        when(bookService.findById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"));
    }

    @Test
    void getBookById_whenMissing_returns404() throws Exception {
        when(bookService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Book with id 999 was not found"));

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Book with id 999 was not found"))
                .andExpect(jsonPath("$.instance").value("/api/books/999"));
    }

    @Test
    void getBooksByAuthor_returnsMatchingBooks() throws Exception {
        List<BookResponse> books = List.of(
                new BookResponse(
                        1L,
                        "Clean Code",
                        "Robert C. Martin",
                        "9780132350884",
                        LocalDate.of(2008, 8, 1),
                        new BigDecimal("39.99"),
                        12,
                        "A practical handbook of software craftsmanship and clean coding principles."
                )
        );

        when(bookService.findByAuthor("Martin")).thenReturn(books);

        mockMvc.perform(get("/api/books/search/by-author").param("author", "Martin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].author").value("Robert C. Martin"))
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    void getBookByIsbn_returns200AndBook() throws Exception {
        BookResponse book = new BookResponse(
                1L,
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                LocalDate.of(2008, 8, 1),
                new BigDecimal("39.99"),
                12,
                "A practical handbook of software craftsmanship and clean coding principles."
        );

        when(bookService.findByIsbn("9780132350884")).thenReturn(book);

        mockMvc.perform(get("/api/books/search/by-isbn")
                        .param("isbn", "9780132350884"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("9780132350884"))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void createBook_withValidPayload_returns201AndCreatedBody() throws Exception {
        BookResponse created = new BookResponse(
                10L,
                "Domain-Driven Design",
                "Eric Evans",
                "9780306406157",
                LocalDate.of(2025, 1, 1),
                new BigDecimal("54.99"),
                5,
                "Classic book on strategic and tactical domain-driven design."
        );

        when(bookService.create(any())).thenReturn(created);

        String requestJson = """
                {
                  "title": "Domain-Driven Design",
                  "author": "Eric Evans",
                  "isbn": "9780306406157",
                  "publishedDate": "2025-01-01",
                  "price": 54.99,
                  "stockQuantity": 5,
                  "description": "Classic book on strategic and tactical domain-driven design."
                }
                """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Domain-Driven Design"))
                .andExpect(jsonPath("$.isbn").value("9780306406157"));
    }

    @Test
    void createBook_withInvalidPayload_returns400() throws Exception {
        String invalidJson = """
                {
                  "title": "",
                  "author": "",
                  "isbn": "bad-isbn",
                  "stockQuantity": -1
                }
                """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Validation failed"));
    }

    @Test
    void updateBook_returns200AndUpdatedBody() throws Exception {
        BookResponse updated = new BookResponse(
                1L,
                "Clean Code Updated",
                "Robert C. Martin",
                "9780132350884",
                LocalDate.of(2008, 8, 1),
                new BigDecimal("44.99"),
                20,
                "Updated description"
        );

        when(bookService.update(eq(1L), any())).thenReturn(updated);

        String requestJson = """
                {
                  "title": "Clean Code Updated",
                  "author": "Robert C. Martin",
                  "isbn": "9780132350884",
                  "publishedDate": "2008-08-01",
                  "price": 44.99,
                  "stockQuantity": 20,
                  "description": "Updated description"
                }
                """;

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code Updated"))
                .andExpect(jsonPath("$.price").value(44.99))
                .andExpect(jsonPath("$.stockQuantity").value(20));
    }

    @Test
    void deleteBook_returns204() throws Exception {
        doNothing().when(bookService).delete(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }
}