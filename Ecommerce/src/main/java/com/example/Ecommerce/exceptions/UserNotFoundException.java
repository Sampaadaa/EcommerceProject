package com.example.Ecommerce.exceptions;

import java.io.Serial;
import java.io.Serializable;

public class UserNotFoundException extends Throwable implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserNotFoundException() {}

    public UserNotFoundException(String message) {
        super();
    }
}
