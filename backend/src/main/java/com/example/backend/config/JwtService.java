package com.example.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for handling JWT (JSON Web Token) operations, including
 * token generation, validation, and extraction of claims.
 */
@Service
public class JwtService {

    // Secret key used for signing the JWT
    private final String secretKey="f3c3e248b1d4c0e7e5c8a1e0d7b9a5b8c3b8e6d2e9a6e2c2a7e8b1d4e8f3e2c6";

    // Token expiration time (24 hours)
    private final long jwtExpiration=1000*60*60*24;

    /**
     * Extracts the username (subject) from the provided JWT.
     *
     * @param token The JWT token.
     * @return The username extracted from the token.
     */
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the token using a claims resolver function.
     *
     * @param token The JWT token.
     * @param claimsResolver A function to extract specific claims.
     * @param <T> The type of the claim to extract.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims= extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses and extracts all claims from the provided JWT token.
     *
     * @param token The JWT token.
     * @return All claims contained in the token.
     */
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates the signing key using the secret key.
     *
     * @return The generated signing key.
     */
    private Key getSigningKey(){
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for the provided user details.
     *
     * @param userDetails The user's details.
     * @return A JWT token.
     */
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }

    /**
     * Generates a JWT token with extra claims for the provided user details.
     *
     * @param extraClaims Additional claims to include in the token.
     * @param userDetails The user's details.
     * @return A JWT token.
     */
    public String generateToken (
            Map<String, Object> extraClaims,
            UserDetails userDetails){
        return buildToken(extraClaims,userDetails,jwtExpiration);
    }

    /**
     * Builds a JWT token with the specified claims, user details, and expiration time.
     *
     * @param extraClaim Additional claims to include.
     * @param userDetails The user's details.
     * @param expiration The expiration time for the token.
     * @return The constructed JWT token.
     */
    private String buildToken(
            Map<String, Object> extraClaim,
            UserDetails userDetails,
            long expiration){
        return Jwts.builder()
                .setClaims(extraClaim)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the provided token by checking its username and expiration status.
     *
     * @param token The JWT token to validate.
     * @param userDetails The user details to compare against.
     * @return True if the token is valid, otherwise false.
     */
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username= extractUsername(token);
        return (username.equals(userDetails.getUsername()))&& !isTokenExpired(token);
    }

    /**
     * Checks whether the token has expired.
     *
     * @param token The JWT token.
     * @return True if the token has expired, otherwise false.
     */
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the provided token.
     *
     * @param token The JWT token.
     * @return The expiration date of the token.
     */
    private Date extractExpiration(String token){
        return  extractClaim(token,Claims::getExpiration);
    }
}

