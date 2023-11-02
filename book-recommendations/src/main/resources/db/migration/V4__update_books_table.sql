ALTER TABLE books
    ADD description text;

ALTER TABLE books
    ADD google_books_id VARCHAR(255);

ALTER TABLE books
    ADD CONSTRAINT uc_books_googlebooksid UNIQUE (google_books_id);

ALTER TABLE books
    DROP COLUMN published_date;

ALTER TABLE books
    ADD published_date TIMESTAMP WITHOUT TIME ZONE;
