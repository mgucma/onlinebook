package com.marek.onlinebookstore.repository.cart;

import com.marek.onlinebookstore.model.Book;
import com.marek.onlinebookstore.model.CartItem;
import com.marek.onlinebookstore.model.ShoppingCart;
import com.marek.onlinebookstore.model.User;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("""
            Should return the ShoppingCart associated with the User having ID 1
            """)
    @Sql(scripts = {"classpath:db/cart/repository/delete_existing_carts.sql",
            "classpath:db/cart/repository/add_shopping_cart_with_necessities.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/cart/repository/delete_existing_carts.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findShoppingCartByUser_UserWithIdEqualOne_returnsShoppingCart() {
        Long userId = 1L;
        ShoppingCart testShoppingCart = getTestShoppingCart();
        ShoppingCart actual = shoppingCartRepository.findShoppingCartByUser(userId);

        Assertions.assertEquals(testShoppingCart.getId(), actual.getId());
        Assertions.assertEquals(testShoppingCart.getUser().getEmail(),
                actual.getUser().getEmail());
        Assertions.assertEquals(takeItemsAndGetString(testShoppingCart),
                takeItemsAndGetString(actual));
    }

    @Test
    @DisplayName("Should return null for an invalid User ID")
    @Sql(scripts = {"classpath:db/cart/repository/delete_existing_carts.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db/cart/repository/delete_existing_carts.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findShoppingCartByInvalidUserId_returnsNull() {
        Long invalidUserId = 999L;
        ShoppingCart actual = shoppingCartRepository.findShoppingCartByUser(invalidUserId);

        Assertions.assertNull(actual, "Expected null when user ID is invalid");
    }

    private String takeItemsAndGetString(ShoppingCart testShoppingCart) {
        return testShoppingCart.getCartItems()
                .stream()
                .map(this::getStringOfBookNameAndQuantity)
                .collect(Collectors.joining());
    }

    private String getStringOfBookNameAndQuantity(CartItem cartItem) {
        String title = cartItem.getBook().getTitle();
        BigDecimal price = cartItem.getBook().getPrice();
        int quantity = cartItem.getQuantity();
        return title + "-" + price + "-" + quantity;
    }

    private ShoppingCart getTestShoppingCart() {
        User user = new User();
        user.setId(1L);
        user.setEmail("okok@email.com");
        user.setPassword("okok");
        user.setFirstName("First Name");
        user.setLastName("Last Name");
        user.setShippingAddress("address");
        user.setDeleted(false);
        user.setRoles(new HashSet<>());

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Title");
        book.setAuthor("bw");
        book.setIsbn("12345");
        book.setPrice(BigDecimal.valueOf(12.19));
        book.setDescription("ok");
        book.setCoverImage("image");
        book.setDeleted(false);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());
        shoppingCart.setDeleted(false);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(1);
        cartItem.setDeleted(false);

        shoppingCart.setCartItems(Set.of(cartItem));
        return shoppingCart;
    }
}
