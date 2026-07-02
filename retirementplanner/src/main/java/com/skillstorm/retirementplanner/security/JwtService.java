package com.skillstorm.retirementplanner.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    /**
     * All JWT logic lives here, create tokens, read claims, validate tokens.
     * 
     * Methods:
     * Generate:
     * - generateToken(UserDetails)
     * - generateToken(Map extraClaims, UserDetails)
     *
     * Read:
     * - extractUsername(token)
     * - extractClaim(token, resolver)
     *
     * Validate:
     * - isTokenValid(token, UserDetails)
     *
     * Config:
     * - getExpirationTime()
     */

    // base64-encoded hs256 secret
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    // token lifetime in milliseconds, injected from application.yml
    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    /**
     * extractUsername:
     * pulls the email out of token
     * 
     * args: 
     * String token: the JWT token
     * 
     * return: 
     * String: the sub claim user email
     * 
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * extractClaims:
     * generic helper to read any single claim from a token
     * 
     * args:
     * receives jwt token string and claimsResolver (which claim to pull out)
     * 
     * returns:
     * Claims: the resolved claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * generateToken:
     * creates a signed token for the current/given user
     * 
     * args:
     * userDetails: the user to issue the token for
     * 
     * return:
     * String: a signed JWT
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * generateToken (extraClaims):
     * creates a signed token including any extra claims.
     * 
     * args: 
     * extraClaims: additional key/values to embed
     * userDetails: user to issue the token for
     * 
     * return: 
     * String: a signed JWT
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * getExpirationTime:
     * exposes the token lifetime (ms) so the controller can return it
     * 
     * returns:
     * long: expiration time in ms
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * buildToken:
     * assembles and signs the JWT (private helper).
     *
     * args:
     * Map extraClaims: extra claims to embed
     * UserDetails userDetails: user the token is for
     * long expiration: lifetime in ms
     *
     * return:
     * String: the compact, signed JWT
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * isTokenValid:
     * checks the token belongs to this user and hasn't expired.
     *
     * args:
     * token: the JWT string
     * userDetails: the user to match against
     *
     * return:
     * boolean:
     * true: subject matches the user AND token not expired
     * false: otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * isTokenExpired:
     * check if the token expiration is in the past (private helper).
     *
     * args:
     * token: the JWT string
     *
     * return:
     * boolean: true: token already expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * extractExpiration:
     * reads the "exp" claim (private helper).
     *
     * args:
     * token: the JWT string
     *
     * return:
     * Date: the expiration timestamp
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * extractAllClaims:
     * verifies the signature and returns the full claims set (private helper).
     *
     * args:
     * token: the JWT string
     *
     * return:
     * Claims: all claims in the payload
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * getSignInKey:
     * builds the HMAC signing key from the Base64 secret (private helper).
     *
     * return:
     * SecretKey: key used to sign and verify tokens
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
