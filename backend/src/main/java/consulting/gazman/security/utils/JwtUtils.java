package consulting.gazman.security.utils;

import consulting.gazman.security.entity.User;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;


import com.nimbusds.jwt.*;
import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;

@Component
public class JwtUtils {

    @Value("${jwt.access.secret}")
    private String accessTokenSecret;

    @Value("${jwt.refresh.secret}")
    private String refreshTokenSecret;

    @Value("${jwt.expiration.access}")
    private long accessTokenExpiration;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenExpiration;

    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenSecret, accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenSecret, refreshTokenExpiration);
    }
    private Key getKey(String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateToken(User user, String secret, Long expiration) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(expiration)))
                .signWith(getKey(secret), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token,accessTokenSecret);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token,refreshTokenSecret);
    }

    public boolean validateToken(String token,String secret) {
        try {

            Jwts.parserBuilder()
                    .setSigningKey(getKey(secret))
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            // Token has expired
            return false;
        } catch (JwtException e) {
            // Other JWT validation errors (malformed, invalid signature, etc)
            return false;
        }
    }

    public String extractSubject(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey(accessTokenSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
    public String extractSubjectFromRefresh(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey(refreshTokenSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
    public String extractEmail(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            return claimsSet.getStringClaim("sub");
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing token", e);
        }
    }

    public String extractUsername(String jwt) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwt);
            String userEmail = signedJWT.getJWTClaimsSet().getStringClaim("email");
            return userEmail;
        } catch (java.text.ParseException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }



}
