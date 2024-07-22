package com.example.Ecommerce.Security;


import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.Ecommerce.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//executed once per request
//This filter intercepts incoming HTTP requests to check for the presence of a JWT token in the Authorization header,
// validates it, and sets the authentication in the SecurityContextHolder
@Service
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;


    // method from OncePerRequestFilter that must be overridden,and  it is executed once per request
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        // Retrieves the value of the "Authorization" header from the HTTP request
        String authHeader = request.getHeader("Authorization");

        // Checks if the Authorization header is present, not blank, and starts with "Bearer "
        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            // Extracts the JWT token by removing the "Bearer " prefix
            String jwt = authHeader.substring(7);

            //Sends a 400 Bad Request response if the JWT token is invalid
            if (jwt.isBlank()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invlaid JWT token in Bearer Header");
            } else {
                //Starts a try block to handle potential exceptions during token validation
                try {
                    // Validates the JWT token and retrieves the email claim using the JWTUtil class
                    String email = jwtUtil.validateTokenAndRetrieveSubject(jwt);

                    //load the user details using email claim
                    UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);

                    //creates new Authentication token on the basis of user email,password and Authorities
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(email, userDetails.getPassword(), userDetails.getAuthorities());

                    //check if the current context have any authentication set
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        //Sets the authentication in the SecurityContextHolder, establishing the user's authentication
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                    //catches exception thrown during jwt verification
                } catch (JWTVerificationException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token");
                }
            }
        }

        //Passes the request and response to the next filter in the filter chain,
        // allowing the request to proceed if authentication was successful or no JWT token was found
        filterChain.doFilter(request, response);
    }
}