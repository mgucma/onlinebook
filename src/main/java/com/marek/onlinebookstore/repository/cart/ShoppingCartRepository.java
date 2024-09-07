package com.marek.onlinebookstore.repository.cart;

import com.marek.onlinebookstore.model.ShoppingCart;
import com.marek.onlinebookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long>,
        JpaSpecificationExecutor<ShoppingCart> {

    @Query("SELECT sc "
            + "FROM ShoppingCart sc "
            + "JOIN FETCH sc.cartItems "
            + "WHERE sc.user = :user")
    ShoppingCart findShoppingCartByUser(User user);
}


