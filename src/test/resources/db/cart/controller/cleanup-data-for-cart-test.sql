DELETE FROM cart_items WHERE books_id = 2;

DELETE FROM books_categories WHERE books_id = 2;

DELETE FROM books WHERE id = 2;

DELETE FROM categories WHERE name = 'category';

DELETE FROM user_roles WHERE user_id IN (
    SELECT id FROM users WHERE email = 'costam@email.com'
);

DELETE FROM shopping_carts WHERE user_id IN (
    SELECT id FROM users WHERE email = 'costam@email.com'
);

DELETE FROM users WHERE email = 'costam@email.com';

