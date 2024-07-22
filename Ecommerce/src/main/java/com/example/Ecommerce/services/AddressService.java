package com.example.Ecommerce.services;

import com.example.Ecommerce.dto.AddressDto;
import com.example.Ecommerce.models.Address;

import java.util.List;

public interface AddressService {
    AddressDto createAddress(AddressDto addressDto);
    List<AddressDto> getAddresses();
    AddressDto getAddress(Long addressId);
    AddressDto updateAddress(Long addressId , Address address);
    AddressDto deleteAddress(Long addressId );

}
