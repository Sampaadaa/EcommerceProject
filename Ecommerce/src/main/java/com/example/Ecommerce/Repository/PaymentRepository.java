package com.example.Ecommerce.Repository;

import com.example.Ecommerce.models.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
}
