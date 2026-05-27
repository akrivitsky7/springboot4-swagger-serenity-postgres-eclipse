# Copyright (c) 2026 Anatoly S. Krivitsky, Ph.D.
# All rights reserved.
#
# Permission is granted, free of charge, to use, copy, distribute, and publish
# this Cucumber feature file, provided that this copyright notice is preserved.
#
# This file is provided "as is", without warranty of any kind.
Feature: Book search API

  Scenario: List all books
    When I request all books
    Then the response status should be 200
    And the response should contain at least 1 book

  Scenario: Find books by author
    When I search books by author "Martin"
    Then the response status should be 200
    And the response should contain a book by "Robert C. Martin"

  Scenario: Get one book by ISBN
    When I search book by ISBN "9780132350884"
    Then the response status should be 200
    And the response should contain the title "Clean Code"