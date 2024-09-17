DELETE FROM cart_items WHERE books_id =
                             (SELECT id FROM books WHERE title = 'Title'
                                                     AND author = 'author'
                                                     AND isbn = '1231231'
                             );
DELETE FROM users WHERE email = 'email@email.com';

DELETE FROM shopping_carts WHERE user_id =
                                 (SELECT id FROM users
                                  WHERE email = 'email@email.com'
                                 );
