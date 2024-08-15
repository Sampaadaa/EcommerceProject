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
import com.example.Ecommerce.models.User;

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
        // Attempt to find an address in the database that matches the given address details (country, state, city, pincode, street, buildingName).
        Address addressFromDB = addressRepository.findByCountryAndCityAndState(
                address.getCountry(), address.getState(), address.getCity());

        // If the address does not exist in the database (i.e., addressFromDB is null):
        if (addressFromDB == null) {
            // Retrieve the address by its ID. If not found, throw a ResourceNotFoundException.
            addressFromDB = addressRepository.findById(addressId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

            // Update the existing address with the new address details.
            addressFromDB.setCountry(address.getCountry());
            addressFromDB.setState(address.getState());
            addressFromDB.setCity(address.getCity());

            // Save the updated address back to the database.
            Address updatedAddress = addressRepository.save(addressFromDB);

            // Return the updated address as an AddressDTO object using modelMapper.
            return modelMapper.map(updatedAddress, AddressDto.class);

        } else {
            // If the address exists in the database (i.e., addressFromDB is not null):
            // Find all users associated with the given address ID.
            List<User> users = userRepository.findByAddress(addressId);
            final Address a = addressFromDB;

            // Update each user's address list by adding the existing address (addressFromDB).
            users.forEach(user -> user.getAddresses().add(a));

            // Delete the old address from the database by calling the deleteAddress method.
            deleteAddress(addressId);

            // Return the found address as an AddressDTO object using modelMapper.
            return modelMapper.map(addressFromDB, AddressDto.class);
        }
    }

    @Override
    public String deleteAddress(Long addressId) {
        // Retrieve the address by its ID. If not found, throw a ResourceNotFoundException.
        Address addressFromDB = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        // Find all users associated with the given address ID.
        List<User> users = userRepository.findByAddress(addressId);

        // For each user, remove the address from the user's address list.
        users.forEach(user -> {
            user.getAddresses().remove(addressFromDB);

            // Save the updated user back to the database.
            userRepository.save(user);
        });

        // Delete the address from the database using its ID.
        addressRepository.deleteById(addressId);

        // Return a confirmation message that the address was successfully deleted.
        return "Address deleted successfully with addressId: " + addressId;
    }

}
