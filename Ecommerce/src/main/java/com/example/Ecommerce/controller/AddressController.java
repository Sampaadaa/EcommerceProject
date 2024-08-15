package com.example.Ecommerce.controller;


import com.example.Ecommerce.dto.AddressDto;
import com.example.Ecommerce.models.Address;
import com.example.Ecommerce.services.AddressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application Project")


public class AddressController {

    @Autowired
    private AddressService addressService;

    // Endpoint to create a new address
    @PostMapping("/address")
    public ResponseEntity<AddressDto> createAddress(@Valid @RequestBody AddressDto addressDTO) {
        // Call the service to create a new address and return the saved AddressDTO.
        AddressDto savedAddressDTO = addressService.createAddress(addressDTO);

        // Return the created AddressDTO with an HTTP status of CREATED (201).
        return new ResponseEntity<AddressDto>(savedAddressDTO, HttpStatus.CREATED);
    }

    // Endpoint to retrieve all addresses
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getAddresses() {
        // Call the service to get a list of all AddressDTOs.
        List<AddressDto> addressDTOs = addressService.getAddresses();

        // Return the list of AddressDTOs with an HTTP status of FOUND (302).
        return new ResponseEntity<List<AddressDto>>(addressDTOs, HttpStatus.FOUND);
    }

    // Endpoint to retrieve a specific address by its ID
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> getAddress(@PathVariable Long addressId) {
        // Call the service to get the AddressDTO for the given address ID.
        AddressDto addressDTO = addressService.getAddress(addressId);

        // Return the AddressDTO with an HTTP status of FOUND (302).
        return new ResponseEntity<AddressDto>(addressDTO, HttpStatus.FOUND);
    }

    // Endpoint to update an existing address
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable Long addressId, @RequestBody Address address) {
        // Call the service to update the address and return the updated AddressDTO.
        AddressDto addressDTO = addressService.updateAddress(addressId, address);

        // Return the updated AddressDTO with an HTTP status of OK (200).
        return new ResponseEntity<AddressDto>(addressDTO, HttpStatus.OK);
    }

    // Endpoint to delete an address by its ID
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        // Call the service to delete the address and return the status message.
        String status = addressService.deleteAddress(addressId);

        // Return the status message with an HTTP status of OK (200).
        return new ResponseEntity<String>(status, HttpStatus.OK);
    }
}

