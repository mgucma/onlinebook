package com.marek.onlinebookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marek.onlinebookstore.dto.cart.CartItemDto;
import com.marek.onlinebookstore.dto.cart.CartItemUpdatedDto;
import com.marek.onlinebookstore.model.Book;
import com.marek.onlinebookstore.model.CartItem;
import com.marek.onlinebookstore.model.Role;
import com.marek.onlinebookstore.model.ShoppingCart;
import com.marek.onlinebookstore.model.User;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    public static final int QUANTITY = 1;
    private static final String EMAIL = "costam@email.com";
    private static final String PASSWORD =
            "$2b$12$YTnxUtW6bzr0uqVdqP03F.6SIxrtXWZ8a0jF7ZB9tFiC8byuIl6B.";
    private static final String FIRST_NAME = "First Name";
    private static final String LAST_NAME = "Last Name";
    private static final String SHIPPING_ADDRESS = "address";
    private static final Set<Role> EMPTY_ROLES = new HashSet<>();
    private static final String TITLE = "Title";
    private static final String AUTHOR = "author";
    private static final String ISBN = "123123";
    private static final BigDecimal PRICE = BigDecimal.valueOf(12.12);
    private static final String DESCRIPTION = "image";
    private static final String COVER_IMAGE = "description";
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("db/cart/controller/cleanup-data-for-cart-test.sql")
            );
        }
    }

    @Test
    @DisplayName("Should update item in cart with valid input and return CartItemDto")
    @WithMockUser(username = "user", roles = "USER")
    @SneakyThrows
    @Sql(scripts = "classpath:db/cart/controller/add-data-for-cart-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db/cart/controller/cleanup-data-for-cart-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldUpdateItemInCartWithValidInputAndReturnCartItemDto() {
        //Given
        long quantity = QUANTITY + 1;
        CartItemUpdatedDto cartItemUpdateDto = getCartItemUpdateDto((int) quantity);
        CartItem cartItem = getCartItem(
                getShoppingCart(getUser()), getBook());
        Long cartItemId = 2L;
        cartItem.setId(cartItemId);

        String jsonRequest = objectMapper.writeValueAsString(cartItemUpdateDto);
        //When
        MvcResult result = mockMvc.perform(
                        put("/cart/cart-items/" + cartItemId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        CartItemDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartItemDto.class
        );
        Assertions.assertEquals(quantity, actual.quantity());
    }

    @Test
    @DisplayName("Should delete item from cart with valid input")
    @WithMockUser(username = "user", roles = "USER")
    @SneakyThrows
    void shouldDeleteItemFromCartWithValidInput() {
        CartItem cartItem = getCartItem(getShoppingCart(getUser()), getBook());
        cartItem.setId(2L);
        Long cartItemId = cartItem.getId();
        mockMvc.perform(delete("/cart/cart-items/" + cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private static CartItem getCartItem(ShoppingCart shoppingCart, Book book) {
        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(QUANTITY);
        cartItem.setDeleted(false);
        return cartItem;
    }

    private static User getUser() {
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setShippingAddress(SHIPPING_ADDRESS);
        user.setRoles(EMPTY_ROLES);
        user.setDeleted(false);
        return user;
    }

    private static ShoppingCart getShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());
        shoppingCart.setDeleted(false);
        return shoppingCart;
    }

    private static Book getBook() {
        Book book = new Book();
        book.setTitle(TITLE);
        book.setAuthor(AUTHOR);
        book.setIsbn(ISBN);
        book.setPrice(PRICE);
        book.setDescription(DESCRIPTION);
        book.setCoverImage(COVER_IMAGE);
        book.setCategories(new HashSet<>());
        book.setDeleted(false);
        return book;
    }

    private static CartItemUpdatedDto getCartItemUpdateDto(int quantity) {
        return new CartItemUpdatedDto(quantity);
    }
}
