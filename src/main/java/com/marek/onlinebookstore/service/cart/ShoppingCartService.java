package com.marek.onlinebookstore.service.cart;

import com.marek.onlinebookstore.dto.cart.CartItemDto;
import com.marek.onlinebookstore.dto.cart.CartItemRequestDto;
import com.marek.onlinebookstore.dto.cart.CartItemUpdatedDto;
import com.marek.onlinebookstore.dto.cart.ShoppingCartDto;
import com.marek.onlinebookstore.model.User;

public interface ShoppingCartService {
    ShoppingCartDto findUserCart(User user);

    ShoppingCartDto addItem(User user, CartItemRequestDto cartItemRequestDto);

    CartItemDto updateItemQuantity(Long itemId, CartItemUpdatedDto updatedDto);

    void deleteById(Long itemId);
}
