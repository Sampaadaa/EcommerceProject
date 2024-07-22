package com.example.Ecommerce.services;

import com.example.Ecommerce.Repository.AddressRepository;
import com.example.Ecommerce.Repository.RoleRepository;
import com.example.Ecommerce.Repository.UserRepository;
import com.example.Ecommerce.config.AppConstant;
import com.example.Ecommerce.dto.AddressDto;
import com.example.Ecommerce.dto.UserDto;
import com.example.Ecommerce.dto.UserResponse;
import com.example.Ecommerce.exceptions.APIException;
import com.example.Ecommerce.models.Address;
import com.example.Ecommerce.models.Role;
import com.example.Ecommerce.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Service

public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

//    @Autowired
//    private CartService cartService;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public UserDto registerUser(UserDto userDto) {
        try {
            //maps the UserDto object to a User entity
            User user = modelMapper.map(userDto, User.class);

//            Cart cart = new Cart();
//            user.setCart(cart);

            //Role object is fetched from the roleRepo using the USER_ID constant
            Role role = roleRepository.findById(AppConstant.USER_ID).get();
            user.getRoles().add(role);

            //extract the address from userDto
            String country = userDto.getAddress().getCountry();
            String state = userDto.getAddress().getState();
            String city = userDto.getAddress().getCity();

            //check if the address already exist
            Address address = addressRepository.findByCountryAndStateAndCity(country, state,
                    city);

            // if address isn't found new address object is created
            if (address == null) {
                address = new Address(country, state, city);

                address = addressRepository.save(address);
            }

            //address is set to user
            user.setAddresses(List.of(address));

            //saving user entity to userRepository
            User registeredUser = userRepository.save(user);

//            cart.setUser(registeredUser);

           // registered user entity is mapped back to a UserDto
            userDto = modelMapper.map(registeredUser, UserDto.class);

            //address is  mapped to an AddressDto and set on the UserDto
            userDto.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDto.class));

            return userDto;
        } catch (DataIntegrityViolationException e) {
            throw new APIException("User already exists with emailId: " + userDto.getEmail());
        }



    }

    @Override
    public UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        return null;
    }

    @Override
    public UserDto getUserById(Long userId) {
        return null;
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDTO) {
        return null;
    }

    @Override
    public String deleteUser(Long userId) {
        return "";
    }
}
