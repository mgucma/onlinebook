DELETE FROM cart_items WHERE books_id = (
    SELECT id FROM books WHERE title = 'Title'
                           AND author = 'author'
                           AND isbn = '1231231'
);

DELETE FROM books_categories WHERE books_id = (
    SELECT id FROM books WHERE title = 'Title'
                           AND author = 'author'
                           AND isbn = '1231231'
);

DELETE FROM books WHERE title = 'Title'
                    AND author = 'author'
                    AND isbn = '1231231'
                    AND price = 123.12
                    AND description = 'ok book'
                    AND cover_image = 'image';
