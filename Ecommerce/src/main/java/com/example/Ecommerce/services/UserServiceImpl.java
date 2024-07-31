package com.example.Ecommerce.services;

import com.example.Ecommerce.Repository.AddressRepository;
import com.example.Ecommerce.Repository.RoleRepository;
import com.example.Ecommerce.Repository.UserRepository;
import com.example.Ecommerce.config.AppConstant;
import com.example.Ecommerce.dto.*;
import com.example.Ecommerce.exceptions.APIException;
import com.example.Ecommerce.exceptions.ResourceNotFoundException;
import com.example.Ecommerce.models.Address;
import com.example.Ecommerce.models.CartItem;
import com.example.Ecommerce.models.Role;
import com.example.Ecommerce.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


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

    //responsible for retrieving the paginated list from db

    @Override
    public UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<User> pageUsers = userRepository.findAll(pageDetails);

        List<User> users = pageUsers.getContent();

        if (users.isEmpty()) {
            throw new APIException("No User exists !!!");
        }

        List<UserDto> userDTOs = users.stream().map(user -> {
            UserDto dto = modelMapper.map(user, UserDto.class);

            if (!user.getAddresses().isEmpty()) {
                dto.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDto.class));
            }

            CartDto cart = modelMapper.map(user.getCart(), CartDto.class);

            List<ProductDto> products = user.getCart().getCartItems().stream()
                    .map(item -> modelMapper.map(item.getProduct(), ProductDto.class)).collect(Collectors.toList());

            dto.setCart(cart);

            dto.getCart().setProducts(products);

            return dto;

        }).collect(Collectors.toList());

        UserResponse userResponse = new UserResponse();

        userResponse.setContent(userDTOs);
        userResponse.setPageNumber(pageUsers.getNumber());
        userResponse.setPageSize(pageUsers.getSize());
        userResponse.setTotalElements(pageUsers.getTotalElements());
        userResponse.setTotalPages(pageUsers.getTotalPages());
        userResponse.setLastPage(pageUsers.isLast());

        return userResponse;
    }


    //This method retrieves a user by their ID
    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        //Converts the retrieved User entity to a UserDTO object
        UserDto userDTO = modelMapper.map(user, UserDto.class);

        userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDto.class));

        CartDto cart = modelMapper.map(user.getCart(), CartDto.class);

        //Retrieves the cart items from the user's cart.
        List<ProductDto> products = user.getCart().getCartItems().stream()
                //Converts each product in the cart items to a ProductDTO using modelMapper
                .map(item -> modelMapper.map(item.getProduct(), ProductDto.class))
                    //Collects the ProductDTO objects into a list
                       .collect(Collectors.toList());

        userDTO.setCart(cart);

        userDTO.getCart().setProducts(products);

        return userDTO;
    }


    //This method updates an existing user's details, including their address and password, in the database
    @Override
    //Retrieve the user if not found through exception
    public UserDto updateUser(Long userId, UserDto userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        //encode the password
        String encodedPass = passwordEncoder.encode(userDTO.getPassword());

        //update the user details
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setMobileNumber(userDTO.getMobileNumber());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encodedPass);

        if (userDTO.getAddress() != null) {
            String country = userDTO.getAddress().getCountry();
            String state = userDTO.getAddress().getState();
            String city = userDTO.getAddress().getCity();


            Address address = addressRepository.findByCountryAndStateAndCity(country, state,
                    city);

            if (address == null) {
                address = new Address(country, state, city);

                address = addressRepository.save(address);

                user.setAddresses(List.of(address));
            }
        }

        userDTO = modelMapper.map(user, UserDto.class);

        userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDto.class));

        CartDto cart = modelMapper.map(user.getCart(), CartDto.class);

        List<ProductDto> products = user.getCart().getCartItems().stream()
                .map(item -> modelMapper.map(item.getProduct(), ProductDto.class)).collect(Collectors.toList());

        userDTO.setCart(cart);

        userDTO.getCart().setProducts(products);

        return userDTO;
    }



    @Override
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        List<CartItem> cartItems = user.getCart().getCartItems();
        Long cartId = user.getCart().getCartId();

        cartItems.forEach(item -> {

            Long productId = item.getProduct().getProductId();

            //cartService.deleteProductFromCart(cartId, productId);
        });

        userRepository.delete(user);

        return "User with userId " + userId + " deleted successfully!!!";
    }
}
