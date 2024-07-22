package com.example.Ecommerce.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity

@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 4, message = "Country name must contain at least 4 characters")
    private String country;

    @NotBlank
    @Size(min = 2, message = "State name must contain at least 2 characters")
    private String state;

    @NotBlank
    @Size(min = 4, message = "City name must contain at least 4 characters")
    private String city;

    // Address has many users
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();

    public Address(String country, String state, String city) {
        this.country = country;
        this.state = state;
        this.city = city;

    }


}
