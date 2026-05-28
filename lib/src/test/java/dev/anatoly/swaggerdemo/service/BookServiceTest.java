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
package dev.anatoly.swaggerdemo.service;

import dev.anatoly.swaggerdemo.dto.BookCreateRequest;
import dev.anatoly.swaggerdemo.dto.BookResponse;
import dev.anatoly.swaggerdemo.dto.BookUpdateRequest;
import dev.anatoly.swaggerdemo.entity.Book;
import dev.anatoly.swaggerdemo.exception.ResourceNotFoundException;
import dev.anatoly.swaggerdemo.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void findAll_returnsMappedDtos() {
        List<Book> books = List.of(
                new Book(
                        1L,
                        "Clean Code",
                        "Robert C. Martin",
                        "9780132350884",
                        LocalDate.of(2008, 8, 1),
                        new BigDecimal("39.99"),
                        12,
                        "A practical handbook of software craftsmanship and clean coding principles."
                ),
                new Book(
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

        when(bookRepository.findAll()).thenReturn(books);

        List<BookResponse> result = bookService.findAll();

        assertEquals(2, result.size());
        assertEquals("Clean Code", result.get(0).title());
        assertEquals("Robert C. Martin", result.get(0).author());
        assertEquals("9780134685991", result.get(1).isbn());
    }

    @Test
    void findById_whenExists_returnsMappedDto() {
        Book book = new Book(
                1L,
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                LocalDate.of(2008, 8, 1),
                new BigDecimal("39.99"),
                12,
                "A practical handbook of software craftsmanship and clean coding principles."
        );

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponse result = bookService.findById(1L);

        assertEquals(1L, result.id());
        assertEquals("Clean Code", result.title());
        assertEquals("Robert C. Martin", result.author());
    }

    @Test
    void findById_whenMissing_throwsException() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex =
                assertThrows(ResourceNotFoundException.class, () -> bookService.findById(999L));

        assertTrue(ex.getMessage().contains("999"));
    }

    @Test
    void findByAuthor_returnsMappedDtos() {
        List<Book> books = List.of(
                new Book(
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

        when(bookRepository.findByAuthorContainingIgnoreCase("Martin")).thenReturn(books);

        List<BookResponse> result = bookService.findByAuthor("Martin");

        assertEquals(1, result.size());
        assertEquals("Robert C. Martin", result.get(0).author());
        assertEquals("Clean Code", result.get(0).title());
    }

    @Test
    void findByIsbn_whenExists_returnsMappedDto() {
        Book book = new Book(
                1L,
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                LocalDate.of(2018, 1, 6),
                new BigDecimal("49.99"),
                7,
                "Best practices for the Java platform."
        );

        when(bookRepository.findByIsbn("9780134685991")).thenReturn(Optional.of(book));

        BookResponse result = bookService.findByIsbn("9780134685991");

        assertEquals("Effective Java", result.title());
        assertEquals("Joshua Bloch", result.author());
    }

    @Test
    void findByIsbn_whenMissing_throwsException() {
        when(bookRepository.findByIsbn("x")).thenReturn(Optional.empty());

        ResourceNotFoundException ex =
                assertThrows(ResourceNotFoundException.class, () -> bookService.findByIsbn("x"));

        assertTrue(ex.getMessage().contains("ISBN"));
    }

    @Test
    void create_savesMappedEntityAndReturnsResponse() {
        BookCreateRequest request = new BookCreateRequest(
                "Domain-Driven Design",
                "Eric Evans",
                "9780306406157",
                LocalDate.of(2025, 1, 1),
                new BigDecimal("54.99"),
                5,
                "Classic book on strategic and tactical domain-driven design."
        );

        Book saved = new Book(
                10L,
                "Domain-Driven Design",
                "Eric Evans",
                "9780306406157",
                LocalDate.of(2025, 1, 1),
                new BigDecimal("54.99"),
                5,
                "Classic book on strategic and tactical domain-driven design."
        );

        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        BookResponse result = bookService.create(request);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());

        Book entity = captor.getValue();
        assertEquals("Domain-Driven Design", entity.getTitle());
        assertEquals("Eric Evans", entity.getAuthor());
        assertEquals("9780306406157", entity.getIsbn());

        assertEquals(10L, result.id());
        assertEquals("Domain-Driven Design", result.title());
    }

    @Test
    void update_whenExists_updatesEntityAndReturnsResponse() {
        Book existing = new Book(
                1L,
                "Old Title",
                "Old Author",
                "1111111111111",
                LocalDate.of(2000, 1, 1),
                new BigDecimal("10.00"),
                1,
                "Old description"
        );

        BookUpdateRequest request = new BookUpdateRequest(
                "Clean Code Updated",
                "Robert C. Martin",
                "9780132350884",
                LocalDate.of(2008, 8, 1),
                new BigDecimal("44.99"),
                20,
                "Updated description"
        );

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookResponse result = bookService.update(1L, request);

        assertEquals(1L, result.id());
        assertEquals("Clean Code Updated", result.title());
        assertEquals("9780132350884", result.isbn());
        assertEquals(20, result.stockQuantity());
    }

    @Test
    void update_whenMissing_throwsException() {
        BookUpdateRequest request = new BookUpdateRequest(
                "title", "author", "9780132350884", null, null, 0, null
        );
        when(bookRepository.findById(777L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.update(777L, request));
    }

    @Test
    void delete_whenExists_callsRepositoryDelete() {
        Book existing = new Book(
                1L,
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                LocalDate.of(2008, 8, 1),
                new BigDecimal("39.99"),
                12,
                "A practical handbook of software craftsmanship and clean coding principles."
        );

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));

        bookService.delete(1L);

        verify(bookRepository).delete(existing);
    }

    @Test
    void delete_whenMissing_throwsException() {
        when(bookRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.delete(123L));
    }
}