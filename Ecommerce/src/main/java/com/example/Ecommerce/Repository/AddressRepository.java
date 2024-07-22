package com.example.Ecommerce.Repository;

import com.example.Ecommerce.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address ,Long> {




    Address findByCountryAndCityAndState(String country, String city, String state);

    Address findByCountryAndStateAndCity(String country, String state, String city);
}
