package com.example.Ecommerce;

import com.example.Ecommerce.Repository.RoleRepository;
import com.example.Ecommerce.config.AppConstant;
import com.example.Ecommerce.models.Role;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication

public class EcommerceApplication implements CommandLineRunner {


	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {

		SpringApplication.run(EcommerceApplication.class, args);
	}

//	@Bean
//	public ModelMapper modelMapper() {
//		return new ModelMapper();
//	}

	@Bean
	public ModelMapper ecommerceModelMapper() {
		return new ModelMapper();
	}


	@Override
	public void run(String... args) throws Exception {
		try {
			//Creates Role entities for admin and user roles
			Role adminRole = new Role();
			adminRole.setRoleId(AppConstant.ADMIN_ID);
			adminRole.setRoleName("ADMIN");

			Role userRole = new Role();
			userRole.setRoleId(AppConstant.USER_ID);
			userRole.setRoleName("USER");

			//Adds the created roles to a list
			List<Role> roles = List.of(adminRole, userRole);
			// save all roles to the database
			List<Role> savedRoles = roleRepository.saveAll(roles);


			savedRoles.forEach(System.out::println);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
