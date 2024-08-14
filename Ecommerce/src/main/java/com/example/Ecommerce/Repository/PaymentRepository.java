package com.example.Ecommerce.Repository;

import com.example.Ecommerce.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
