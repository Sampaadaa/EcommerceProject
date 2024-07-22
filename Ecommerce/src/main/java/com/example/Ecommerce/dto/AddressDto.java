package com.example.Ecommerce.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AddressDto {
    private Long addressId;
    private String country;
    private String state;
    private String city;

}
