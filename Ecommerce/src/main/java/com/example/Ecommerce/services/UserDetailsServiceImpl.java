package com.example.Ecommerce.services;

//load user specific data during authentication process

import com.example.Ecommerce.Repository.UserRepository;
import com.example.Ecommerce.config.UserInfoConfig;
import com.example.Ecommerce.exceptions.ResourceNotFoundException;
import com.example.Ecommerce.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //find the user based on email from user repository
        Optional<User> user = userRepository.findByEmail(username);
        return user.map(UserInfoConfig::new).orElseThrow(() -> new ResourceNotFoundException("User", "email", username));


    }
}
