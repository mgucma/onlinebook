package com.marek.onlinebookstore.service.cart;

import com.marek.onlinebookstore.dto.cart.CartItemDto;
import com.marek.onlinebookstore.dto.cart.CartItemRequestDto;
import com.marek.onlinebookstore.dto.cart.CartItemUpdatedDto;
import com.marek.onlinebookstore.dto.cart.ShoppingCartDto;
import com.marek.onlinebookstore.exception.EntityNotFoundException;
import com.marek.onlinebookstore.mapper.CartItemMapper;
import com.marek.onlinebookstore.mapper.ShoppingCartMapper;
import com.marek.onlinebookstore.model.CartItem;
import com.marek.onlinebookstore.model.ShoppingCart;
import com.marek.onlinebookstore.model.User;
import com.marek.onlinebookstore.repository.book.BookRepository;
import com.marek.onlinebookstore.repository.cart.CartItemRepository;
import com.marek.onlinebookstore.repository.cart.ShoppingCartRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto findUserCart(User user) {
        validateUser(user);
        return shoppingCartMapper.toDto(
                shoppingCartRepository.findShoppingCartByUser(user.getId())
        );
    }

    @Override
    public ShoppingCartDto addItem(User user, CartItemRequestDto cartItemRequestDto) {
        validateUser(user);
        CartItem item = cartItemMapper.toModel(cartItemRequestDto);
        item.setBook(
                bookRepository.findById(item.getBook().getId())
                        .orElseThrow(EntityNotFoundException::new)
        );
        ShoppingCart cart = shoppingCartRepository.findShoppingCartByUser(user.getId());
        item.setShoppingCart(cart);
        Set<CartItem> cartItems = cart.getCartItems();
        cartItems.add(item);
        cart.setCartItems(cartItems);
        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Override
    public CartItemDto updateItemQuantity(Long itemId, CartItemUpdatedDto updatedDto) {
        CartItem cartItem = cartItemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException("Item not found with item id: " + itemId)
        );

        cartItem.setQuantity(updatedDto.quantity());
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void deleteById(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    private void validateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User cannot be null or have a null ID");
        }
    }
}
