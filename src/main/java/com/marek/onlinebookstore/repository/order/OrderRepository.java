package com.marek.onlinebookstore.repository.order;

import com.marek.onlinebookstore.model.Order;
import com.marek.onlinebookstore.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {
    @EntityGraph(attributePaths = {"status"})
    Optional<Order> findById(Long id);

    @Query("SELECT o "
            + "FROM Order o "
            + "JOIN FETCH o.user u "
            + "WHERE  u = :user")
    Page<Order> findAllByUser(User user, Pageable pageable);
}
