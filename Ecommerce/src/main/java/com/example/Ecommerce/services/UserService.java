package com.example.Ecommerce.services;

import com.example.Ecommerce.dto.UserDto;
import com.example.Ecommerce.dto.UserResponse;

public interface UserService {
    UserDto registerUser(UserDto userDto);
    UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    UserDto getUserById(Long userId);

    UserDto updateUser(Long userId, UserDto userDTO);

    String deleteUser(Long userId);


}
