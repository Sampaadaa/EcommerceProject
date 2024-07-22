package com.example.Ecommerce.services;

import com.example.Ecommerce.Repository.AddressRepository;
import com.example.Ecommerce.Repository.UserRepository;
import com.example.Ecommerce.dto.AddressDto;
import com.example.Ecommerce.exceptions.APIException;
import com.example.Ecommerce.exceptions.ResourceNotFoundException;
import com.example.Ecommerce.models.Address;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service

public class AddressServiceImpl implements AddressService{
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ModelMapper modelMapper;


    @Override
    public AddressDto createAddress(AddressDto addressDto) {
        //extract fields from Dto
        String country = addressDto.getCountry();
        String city =addressDto.getCity();
        String state = addressDto.getState();

        // Check if the address already exists in the database
        Address addressFromDB = addressRepository.findByCountryAndCityAndState(country, city, state);


        // If the address already exists, throw an exception
        if(addressFromDB != null){
            throw new APIException("Address already exists with addressId: " + addressFromDB.getAddressId());
        }

        // Map the DTO to an Address entity and save it
        Address address = modelMapper.map(addressDto, Address.class);
        Address savedAddress = addressRepository.save(address);

        // Map the saved entity back to a DTO and return it
        return modelMapper.map(savedAddress, AddressDto.class);


    }

    @Override
    public List<AddressDto> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        List<AddressDto> addressDtos =addresses.stream().map(address -> modelMapper.map(address,AddressDto.class)).collect(Collectors.toList());
        return addressDtos;
    }

    @Override
    public AddressDto getAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        return modelMapper.map(address, AddressDto.class);
    }


    @Override
    public AddressDto updateAddress(Long addressId, Address address) {

        return null;
    }

    @Override
    public AddressDto deleteAddress(Long addressId) {
        return null;
    }
}
