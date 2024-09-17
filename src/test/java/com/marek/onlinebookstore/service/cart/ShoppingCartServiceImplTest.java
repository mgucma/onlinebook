package com.marek.onlinebookstore.service.cart;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.marek.onlinebookstore.dto.cart.CartItemDto;
import com.marek.onlinebookstore.dto.cart.CartItemRequestDto;
import com.marek.onlinebookstore.dto.cart.CartItemUpdatedDto;
import com.marek.onlinebookstore.dto.cart.ShoppingCartDto;
import com.marek.onlinebookstore.exception.EntityNotFoundException;
import com.marek.onlinebookstore.mapper.CartItemMapper;
import com.marek.onlinebookstore.mapper.ShoppingCartMapper;
import com.marek.onlinebookstore.model.Book;
import com.marek.onlinebookstore.model.CartItem;
import com.marek.onlinebookstore.model.Category;
import com.marek.onlinebookstore.model.Role;
import com.marek.onlinebookstore.model.ShoppingCart;
import com.marek.onlinebookstore.model.User;
import com.marek.onlinebookstore.repository.book.BookRepository;
import com.marek.onlinebookstore.repository.cart.CartItemRepository;
import com.marek.onlinebookstore.repository.cart.ShoppingCartRepository;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    public static final int QUANTITY = 1;
    private static final long ID = 1L;
    private static final String EMAIL = "costam@email.com";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String SHIPPING_ADDRESS = "address";
    private static final Set<Role> EMPTY_ROLES = new HashSet<>();
    private static final String TITLE = "Title";
    private static final String AUTHOR = "Author";
    private static final String ISBN = "123123";
    private static final BigDecimal PRICE = BigDecimal.valueOf(123.12);
    private static final String DESCRIPTION = "Description";
    private static final String COVER_IMAGE = "Cover Image";
    private static final Set<Category> EMPTY_CATEGORIES = new HashSet<>();
    private static final Set<CartItem> EMPTY_CART_ITEMS = new HashSet<>();

    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("""
            Find user cart with correct user returns ShoppingCartDto
            """)
    void findUserCart_withCorrectUser_returnsShoppingCartDto() {
        User user = getUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        ShoppingCartDto expected = getShoppingCartDto(user, Set.of());

        when(shoppingCartRepository.findShoppingCartByUser(user.getId()))
                .thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.findUserCart(user);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(user.getId(), actual.userId());

        verify(shoppingCartRepository, times(1)).findShoppingCartByUser(user.getId());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("""
            Add item with valid input returns ShoppingCartDto
            """)
    void addItem_ValidInput_returnsShoppingCartDto() {
        User user = getUser();
        ShoppingCart shoppingCart = getShoppingCart(user);

        Book book = getBook();
        int quantity = 1;

        CartItemRequestDto cartItemRequestDto = getCartItemRequestDto(quantity, book);
        CartItem cartItem = getCartItem(shoppingCart, book, cartItemRequestDto);
        CartItemDto cartItemDto = getCartItemDto(book, (long) quantity);
        Set<CartItemDto> itemDtoSet = Set.of(cartItemDto);
        ShoppingCartDto expected = getShoppingCartDto(
                user, itemDtoSet);

        when(cartItemMapper.toModel(cartItemRequestDto)).thenReturn(cartItem);
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(shoppingCartRepository.findShoppingCartByUser(user.getId())).thenReturn(shoppingCart);
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.addItem(user, cartItemRequestDto);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(user.getId(), actual.userId());

        verify(cartItemMapper, times(1)).toModel(cartItemRequestDto);
        verify(bookRepository, times(1)).findById(book.getId());
        verify(shoppingCartRepository, times(1)).findShoppingCartByUser(user.getId());
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper,
                bookRepository, cartItemMapper);

    }

    @Test
    @DisplayName("""
            Add item with invalid input throws EntityNotFoundException
            """)
    void addItem_InvalidInput_returnsShoppingCartDto() {
        User user = getUser();
        ShoppingCart shoppingCart = getShoppingCart(user);

        Book book = getBook();
        book.setId(-1231L);

        CartItemRequestDto cartItemRequestDto = getCartItemRequestDto(QUANTITY, book);
        CartItem cartItem = getCartItem(shoppingCart, book, cartItemRequestDto);

        when(cartItemMapper.toModel(cartItemRequestDto)).thenReturn(cartItem);
        when(bookRepository.findById(book.getId())).thenThrow(
                new EntityNotFoundException()
        );

        Exception expected = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookRepository.findById(book.getId())
        );
        Exception actual = Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addItem(user, cartItemRequestDto)
        );

        Assertions.assertEquals(expected, actual);

        verify(cartItemMapper, times(1)).toModel(cartItemRequestDto);
        verify(bookRepository, times(2)).findById(book.getId());
        verifyNoMoreInteractions(cartItemMapper, bookRepository);
    }

    @Test
    @DisplayName("""
            Update item quantity with valid input returns CartItemDto
            """)
    void updateItemQuantity_ValidInput_returnCartItemDto() {
        int newQuantity = QUANTITY + 1;
        Book book = getBook();
        ShoppingCart shoppingCart = getShoppingCart(getUser());
        CartItem cartItem = getCartItem(shoppingCart, book);
        cartItem.setId(ID);
        CartItemUpdatedDto cartItemUpdateDto = getCartItemUpdateDto(newQuantity);
        CartItemDto expected = getCartItemDto(book, (long) newQuantity);

        when(cartItemRepository.findById(ID)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);

        CartItemDto actual = shoppingCartService
                .updateItemQuantity(cartItem.getId(), cartItemUpdateDto);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);

        verify(cartItemRepository, times(1)).findById(ID);
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(cartItemMapper, times(1)).toDto(cartItem);
        verifyNoMoreInteractions(cartItemRepository, cartItemMapper);
    }

    @Test
    @DisplayName("""
            Update item quantity with invalid input throws EntityNotFoundException
            """)
    void updateItemQuantity_InvalidInput_returnCartItemDto() {
        int newQuantity = QUANTITY + 1;
        Book book = getBook();
        ShoppingCart shoppingCart = getShoppingCart(getUser());
        CartItem cartItem = getCartItem(shoppingCart, book);
        cartItem.setId(-123L);
        CartItemUpdatedDto cartItemUpdateDto = getCartItemUpdateDto(newQuantity);

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateItemQuantity(cartItem.getId(), cartItemUpdateDto)
        );
        Assertions.assertEquals("Item not found"
                + " with item id: " + cartItem.getId(), exception.getMessage());
        verify(cartItemRepository, times(1)).findById(cartItem.getId());
        verifyNoMoreInteractions(cartItemRepository);
    }

    private static CartItem getCartItem(
            ShoppingCart shoppingCart, Book book, CartItemRequestDto cartItemRequestDto) {
        CartItem cartItem = new CartItem();
        cartItem.setId(ID);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(cartItemRequestDto.quantity());
        cartItem.setDeleted(false);
        return cartItem;
    }

    private static CartItem getCartItem(ShoppingCart shoppingCart, Book book) {
        CartItem cartItem = new CartItem();
        cartItem.setId(ID);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(QUANTITY);
        cartItem.setDeleted(false);
        return cartItem;
    }

    private static User getUser() {
        User user = new User();
        user.setId(ID);
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
        shoppingCart.setId(ID);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(EMPTY_CART_ITEMS);
        shoppingCart.setDeleted(false);
        return shoppingCart;
    }

    private static ShoppingCartDto getShoppingCartDto(
            User user,
            Set<CartItemDto> cartItemDto) {
        return new ShoppingCartDto(
                ID,
                user.getId(),
                cartItemDto
        );
    }

    private static Book getBook() {
        Book book = new Book();
        book.setId(ID);
        book.setTitle(TITLE);
        book.setAuthor(AUTHOR);
        book.setIsbn(ISBN);
        book.setPrice(PRICE);
        book.setDescription(DESCRIPTION);
        book.setCoverImage(COVER_IMAGE);
        book.setCategories(EMPTY_CATEGORIES);
        book.setDeleted(false);
        return book;
    }

    private static CartItemDto getCartItemDto(Book book, Long quantity) {
        return new CartItemDto(
                ID,
                book.getId(),
                book.getTitle(),
                quantity
        );
    }

    private static CartItemRequestDto getCartItemRequestDto(
            int quantity,
            Book book) {
        return new CartItemRequestDto(
                quantity,
                book.getId()
        );
    }

    private static CartItemUpdatedDto getCartItemUpdateDto(int quantity) {
        return new CartItemUpdatedDto(quantity);
    }
}
