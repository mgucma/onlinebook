CREATE DATABASE IF NOT EXISTS book_app;

-- Usunięcie użytkownika, jeśli istnieje
DROP USER IF EXISTS 'book'@'%';

-- Utworzenie nowego użytkownika z hasłem
CREATE USER 'book'@'%' IDENTIFIED BY 'password';

-- Przyznanie uprawnień tylko do nowo utworzonej bazy danych (zamiast wszystkich)
GRANT ALL PRIVILEGES ON book_app.* TO 'book'@'%';

-- Odświeżenie uprawnień
FLUSH PRIVILEGES;
