/*
 * Copyright (c) 2026 Anatoly S. Krivitsky, Ph.D.
 * All rights reserved.
 *
 * Permission is granted, free of charge, to use, copy, distribute, and publish
 * this SQL script, provided that this copyright notice is preserved.
 *
 * This script is provided "as is", without warranty of any kind.
 */
INSERT INTO book (title, author, isbn, published_date, price, stock_quantity, description)
VALUES
('Clean Code', 'Robert C. Martin', '9780132350884', '2008-08-01', 39.99, 12,
 'A practical handbook of software craftsmanship and clean coding principles.'),
('Effective Java', 'Joshua Bloch', '9780134685991', '2018-01-06', 49.99, 7,
 'Best practices for the Java platform.'),
('Spring in Action', 'Craig Walls', '9781617297571', '2022-09-14', 44.95, 4,
 'Hands-on guide to building Spring-based applications.');