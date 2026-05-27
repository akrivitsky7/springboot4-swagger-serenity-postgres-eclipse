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
package dev.anatoly.swaggerdemo.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
    }

    @Test
    void handleNotFound_returns404ProblemDetail() {
        request.setRequestURI("/api/books/999");
        ResourceNotFoundException ex = new ResourceNotFoundException("Book with id 999 was not found");

        ProblemDetail response = handler.handleNotFound(ex, request);

        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getTitle());
        assertEquals("Book with id 999 was not found", response.getDetail());
        assertEquals("/api/books/999", response.getInstance().toString());
        assertEquals("/api/books/999", response.getProperties().get("path"));
        assertNotNull(response.getProperties().get("timestamp"));
    }

    @Test
    void handleValidation_returns400WithDetails() throws Exception {
        request.setRequestURI("/api/books");

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "bookCreateRequest");
        bindingResult.addError(new FieldError("bookCreateRequest", "isbn", "must be a valid ISBN"));
        bindingResult.addError(new FieldError("bookCreateRequest", "stockQuantity",
                "must be greater than or equal to 0"));

        Method method = DummyController.class.getDeclaredMethod("dummyMethod", DummyPayload.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ProblemDetail response = handler.handleValidation(ex, request);

        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getTitle());
        assertEquals("Validation failed", response.getDetail());
        assertEquals("/api/books", response.getInstance().toString());

        @SuppressWarnings("unchecked")
        List<String> details = (List<String>) response.getProperties().get("details");
        assertEquals(2, details.size());
        assertTrue(details.contains("isbn: must be a valid ISBN"));
        assertTrue(details.contains("stockQuantity: must be greater than or equal to 0"));
    }

    @Test
    void handleConstraintViolation_returns400() {
        request.setRequestURI("/api/books/search/by-isbn");

        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Path propertyPath = mock(Path.class);

        when(propertyPath.toString()).thenReturn("isbn");
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn("must be a valid ISBN");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ProblemDetail response = handler.handleConstraintViolation(ex, request);

        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getTitle());
        assertEquals("Constraint violation", response.getDetail());

        @SuppressWarnings("unchecked")
        List<String> details = (List<String>) response.getProperties().get("details");
        assertEquals(1, details.size());
        assertEquals("isbn: must be a valid ISBN", details.get(0));
    }

    @Test
    void handleDataIntegrity_returns409() {
        request.setRequestURI("/api/books");
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("duplicate key value violates unique constraint");

        ProblemDetail response = handler.handleDataIntegrity(ex, request);

        assertEquals(409, response.getStatus());
        assertEquals("Conflict", response.getTitle());
        assertEquals("Database integrity violation", response.getDetail());
    }

    @Test
    void handleGeneric_returns500() {
        request.setRequestURI("/api/books");
        Exception ex = new Exception("Unexpected failure");

        ProblemDetail response = handler.handleGeneric(ex, request);

        assertEquals(500, response.getStatus());
        assertEquals("Internal Server Error", response.getTitle());
        assertEquals("Unexpected failure", response.getDetail());
    }
    
    @Test
    void handleValidation_withObjectError() throws Exception {
        request.setRequestURI("/api/books");

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "bookCreateRequest");
        bindingResult.reject("book.invalid", "general object-level validation message");

        Method method = DummyController.class.getDeclaredMethod("dummyMethod", DummyPayload.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ProblemDetail response = handler.handleValidation(ex, request);

        assertEquals(400, response.getStatus());
        @SuppressWarnings("unchecked")
        List<String> details = (List<String>) response.getProperties().get("details");
        assertEquals(1, details.size());
        assertEquals("general object-level validation message", details.get(0));
    }

    static class DummyController {
        @SuppressWarnings("unused")
        public void dummyMethod(DummyPayload payload) {
        }
    }

    static class DummyPayload {
    }
}