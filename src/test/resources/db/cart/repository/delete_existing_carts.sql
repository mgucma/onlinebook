-- Clean tables in the reverse order of dependency
DELETE FROM cart_items;
DELETE FROM shopping_carts;
DELETE FROM books_categories;
DELETE FROM categories;
DELETE FROM books;
DELETE FROM user_roles;
DELETE FROM roles;
DELETE FROM users;