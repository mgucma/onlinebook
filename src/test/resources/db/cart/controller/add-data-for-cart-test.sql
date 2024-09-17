INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (2, 'Title', 'author', '123123', 12.12, 'image', 'description', false);

INSERT INTO categories (name, description, is_deleted)
VALUES ('category', 'ok category', false);

insert into books_categories(books_id, categories_id)
values(2, 1);

INSERT INTO users (email, password, first_name, last_name, shipping_address, is_deleted)
VALUES ('costam@email.com', '$2b$12$YTnxUtW6bzr0uqVdqP03F.6SIxrtXWZ8a0jF7ZB9tFiC8byuIl6B.'
       ,'First Name', 'Last Name', 'address', false);
SET @user_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role_id)
VALUES (@user_id, 1);

INSERT INTO shopping_carts (user_id, is_deleted)
VALUES (@user_id, false);
SET @cart_id = LAST_INSERT_ID();

INSERT INTO cart_items (id, cart_id, books_id, quantity)
VALUES (2, @cart_id, 2, 1);