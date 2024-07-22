package com.example.Ecommerce.config;


import com.example.Ecommerce.Security.JWTFilter;
import com.example.Ecommerce.services.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


//Indicates that this class contains Spring configuration
@Configuration
//Enable web security in the application
@EnableWebSecurity
//allows the use of method level security annotations
@EnableMethodSecurity

//this class configure security in application
public class SecurityConfig {

    //custom implementation od userDetailsService for loading user specific data
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    //custom filter for handling jwt validation
    @Autowired
    private JWTFilter jwtFilter;

    @Bean
    //method that describe security policies
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrf()
                .disable()
//                .and()

                //url Authorization
                .authorizeHttpRequests()

                //specifies url matching AppConstant.PUBLIC_URLS can be accessed by anyone without authentication
                .requestMatchers(AppConstant.PUBLIC_URLS).permitAll()

                //specifies url matching AppConstant.USER_URLS can be accessed by user which have USER or ADMIN Authorities
                .requestMatchers(AppConstant.USER_URLS).hasAnyAuthority("USER", "ADMIN")

                .requestMatchers(AppConstant.ADMIN_URLS).hasAuthority("ADMIN")
                //AppConstant.USER_URLS
                .anyRequest()
                .authenticated()

                // Sends a 401 Unauthorized response when an authentication attempt fails
                .and()
                .exceptionHandling().authenticationEntryPoint(
                        (request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))

                //Configures session management to be stateless
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // filter will process JWT tokens in the request headers to authenticate users
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        http.authenticationProvider(daoAuthenticationProvider());

        DefaultSecurityFilterChain defaultSecurityFilterChain = http.build();

        return defaultSecurityFilterChain;
    }


    @Bean
    //retrieves user details and validates passwords during authentication
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsServiceImpl);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }






}
