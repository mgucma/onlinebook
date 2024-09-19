package com.marek.onlinebookstore.repository.cart;

import com.marek.onlinebookstore.model.ShoppingCart;
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
            + "JOIN FETCH sc.user u "
            + "WHERE u.id = :user")
    ShoppingCart findShoppingCartByUser(Long user);
}


