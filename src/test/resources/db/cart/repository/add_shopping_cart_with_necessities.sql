
INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1, 'okok@email.com', 'okok','First Name', 'Last Name', 'address', false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'Title', 'bw', '12345', 12.19, 'ok', 'image', false);

INSERT INTO shopping_carts (id, user_id, is_deleted)
VALUES (1, 1, false);

INSERT INTO cart_items (id, cart_id, books_id, quantity, is_deleted)
VALUES (1, 1, 1, 1, false);