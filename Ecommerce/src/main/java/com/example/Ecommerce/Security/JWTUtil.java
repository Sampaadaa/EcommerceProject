package com.example.Ecommerce.Security;


//This class generate and validate jwt tokens

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {
    //injects the value of the jwt_secret property from the application’s properties file into the secret field
    @Value("${jwt_secret}")
    //secret key is used to sign and verify
    private String secret;

    //method is used for token generation
    //generates a JWT token for the given email address, and  It throws IllegalArgumentException or JWTCreationException if token creation fails.
    public String generateToken(String email) throws IllegalArgumentException, JWTCreationException {
        return JWT.create()
                .withSubject("User Details")
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .withIssuer("Event Scheduler")
                .sign(Algorithm.HMAC256(secret));
    }


    //validate the token
    //validates the provided JWT token and extracts the email claim if the token is valid, and It throws JWTVerificationException if token verification fails.
    public String validateTokenAndRetrieveSubject(String token) throws JWTVerificationException {
        //Creates a JWT verifier using the HMAC256 algorithm and the secret key
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                // Ensures the token has the subject "User Details"
                .withSubject("User Details")
                //Ensures the token was issued by "Event Scheduler"
                //Builds the JWT verifier instance
                .withIssuer("Event Scheduler").build();

        //Verifies the token using the built verifier
        DecodedJWT jwt = verifier.verify(token);
        //Retrieves the "email" claim from the verified token and returns it as a string
        return jwt.getClaim("email").asString();
    }
}
