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
package dev.anatoly.swaggerdemo.service;

import dev.anatoly.swaggerdemo.dto.BookCreateRequest;
import dev.anatoly.swaggerdemo.dto.BookResponse;
import dev.anatoly.swaggerdemo.dto.BookUpdateRequest;
import dev.anatoly.swaggerdemo.entity.Book;
import dev.anatoly.swaggerdemo.exception.ResourceNotFoundException;
import dev.anatoly.swaggerdemo.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        Book book = getBookOrThrow(id);
        return toResponse(book);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookResponse findByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ISBN " + isbn + " was not found"));
        return toResponse(book);
    }

    public BookResponse create(BookCreateRequest request) {
        Book book = new Book();
        applyCreate(book, request);
        return toResponse(bookRepository.save(book));
    }

    public BookResponse update(Long id, BookUpdateRequest request) {
        Book book = getBookOrThrow(id);
        applyUpdate(book, request);
        return toResponse(bookRepository.save(book));
    }

    public void delete(Long id) {
        Book book = getBookOrThrow(id);
        bookRepository.delete(book);
    }

    private Book getBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id " + id + " was not found"));
    }

    private void applyCreate(Book book, BookCreateRequest request) {
        // Boot 4: records expose component methods directly (request.title() not getTitle())
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setIsbn(request.isbn());
        book.setPublishedDate(request.publishedDate());
        book.setPrice(request.price());
        book.setStockQuantity(request.stockQuantity());
        book.setDescription(request.description());
    }

    private void applyUpdate(Book book, BookUpdateRequest request) {
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setIsbn(request.isbn());
        book.setPublishedDate(request.publishedDate());
        book.setPrice(request.price());
        book.setStockQuantity(request.stockQuantity());
        book.setDescription(request.description());
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublishedDate(),
                book.getPrice(),
                book.getStockQuantity(),
                book.getDescription()
        );
    }
}