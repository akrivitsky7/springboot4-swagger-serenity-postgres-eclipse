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
package dev.anatoly.swaggerdemo.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void setId_setsIdValue() {
        Book book = new Book();

        book.setId(100L);

        assertEquals(100L, book.getId());
    }

    @Test
    void equals_sameId_returnsTrue() {
        Book book1 = new Book();
        book1.setId(1L);

        Book book2 = new Book();
        book2.setId(1L);

        assertEquals(book1, book2);
    }

    @Test
    void equals_differentId_returnsFalse() {
        Book book1 = new Book();
        book1.setId(1L);

        Book book2 = new Book();
        book2.setId(2L);

        assertNotEquals(book1, book2);
    }

    @Test
    void equals_nonBookObject_returnsFalse() {
        Book book = new Book();
        book.setId(1L);

        assertNotEquals(book, "not a book");
    }

    @Test
    void equals_null_returnsFalse() {
        Book book = new Book();
        book.setId(1L);

        assertNotEquals(null, book);
    }

    @Test
    void hashCode_sameId_returnsSameHashCode() {
        Book book1 = new Book();
        book1.setId(1L);

        Book book2 = new Book();
        book2.setId(1L);

        assertEquals(book1.hashCode(), book2.hashCode());
    }

    @Test
    void hashCode_nullId_doesNotThrow() {
        Book book = new Book();

        assertDoesNotThrow(book::hashCode);
    }

    @Test
    void noArgsConstructorAndSettersAndGetters_workCorrectly() {
        Book book = new Book();

        LocalDate publishedDate = LocalDate.of(2008, 8, 1);
        BigDecimal price = new BigDecimal("39.99");

        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setIsbn("9780132350884");
        book.setPublishedDate(publishedDate);
        book.setPrice(price);
        book.setStockQuantity(12);
        book.setDescription("A practical handbook of software craftsmanship.");

        assertEquals(1L, book.getId());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert C. Martin", book.getAuthor());
        assertEquals("9780132350884", book.getIsbn());
        assertEquals(publishedDate, book.getPublishedDate());
        assertEquals(price, book.getPrice());
        assertEquals(12, book.getStockQuantity());
        assertEquals("A practical handbook of software craftsmanship.", book.getDescription());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        LocalDate publishedDate = LocalDate.of(2018, 1, 6);
        BigDecimal price = new BigDecimal("49.99");

        Book book = new Book(
                2L,
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                publishedDate,
                price,
                7,
                "Best practices for the Java platform."
        );

        assertEquals(2L, book.getId());
        assertEquals("Effective Java", book.getTitle());
        assertEquals("Joshua Bloch", book.getAuthor());
        assertEquals("9780134685991", book.getIsbn());
        assertEquals(publishedDate, book.getPublishedDate());
        assertEquals(price, book.getPrice());
        assertEquals(7, book.getStockQuantity());
        assertEquals("Best practices for the Java platform.", book.getDescription());
    }
}