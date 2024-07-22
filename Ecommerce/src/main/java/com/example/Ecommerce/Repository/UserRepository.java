package com.example.Ecommerce.Repository;

import com.example.Ecommerce.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //selects User entities by joining them with their associated addresses and fetching users who have an address with the specified addressId
    //find users associated with specific addressId
    @Query("SELECT u FROM User u JOIN FETCH u.addresses a WHERE a.addressId =?1")


    List<User>findByAddress(Long addressId);


    Optional<User> findByEmail(String email);
}
