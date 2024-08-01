package com.example.Ecommerce.Repository;

import com.example.Ecommerce.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    //used to retrieve a single Order entity that matches both the specified email and order ID
    @Query("SELECT o FROM Order o WHERE o.email = ?1 AND o.id = ?2")
    //Finds a single order by email and order ID
    Order findOrderByEmailAndOrderId(String email, Long cartId);

    //Finds all orders by email
    List<Order> findAllByEmail(String emailId);

}