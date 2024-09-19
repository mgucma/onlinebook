INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'Title', 'author', '123123', 12.12, 'image', 'description', false);


INSERT INTO categories (id, name, description, is_deleted)
VALUES (1, 'category', 'ok category', false);

insert into books_categories(books_id, categories_id)
values(1, 1);