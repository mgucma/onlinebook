package com.marek.onlinebookstore.controller;

import com.marek.onlinebookstore.dto.cart.CartItemDto;
import com.marek.onlinebookstore.dto.cart.CartItemRequestDto;
import com.marek.onlinebookstore.dto.cart.CartItemUpdatedDto;
import com.marek.onlinebookstore.dto.cart.ShoppingCartDto;
import com.marek.onlinebookstore.model.User;
import com.marek.onlinebookstore.service.cart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management",
        description = "Endpoints for managing shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Get user Cart",
            description = "get a cart your user")
    public ShoppingCartDto getUserCart(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return shoppingCartService.findUserCart(principal);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "adding item to user cart",
            description = "create new item and add to user cart")
    public ShoppingCartDto addItemToUserCart(Authentication authentication,
                                             @RequestBody @Valid CartItemRequestDto requestDto) {
        User principal = (User) authentication.getPrincipal();
        return shoppingCartService.addItem(principal, requestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/cart-items/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Update item",
            description = "update item in user cart with your id")
    public CartItemDto updateItemInCart(@RequestBody @Valid CartItemUpdatedDto updatedDto,
                                        @PathVariable Long id) {
        return shoppingCartService.updateItemQuantity(id, updatedDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/cart-items/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Delete item ",
            description = "delete item from cart with your id")
    public void deleteItemFromCart(@PathVariable Long id) {
        shoppingCartService.deleteById(id);
    }
}

