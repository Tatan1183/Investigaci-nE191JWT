package com.app.login.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String SECRET_KEY = "119b0c2187b06d6f96abdb69b333a8433f524a80704a4a05671f3b501f438744acdaa4dfc81ab4be20172e38f29ef71112f708ba78064bb978e936b64a60fb09ec5d975a00e5c0c905ac757625dd729b3c935b57fdc6f9f6838ff73dc8594ba1cd6abe59352099b1db0503726b1cb5902f3e81b5b50fb0d8a56c19d43d8dc6cd84908fbfed33d1d8c37711aca6718f8890adc0b7b1460f0fb9f710c4499e4b59c2e545d0ab3896d75d29a012f0c06b3343354498f553b2cac5dee23f7f94883bc55204c7000f3135ab0cd4d838ec948958c9120ce36afef8b94c93bc637c0d1be56fcc5a655f64642ac752150e56ddf2b3bf58b324b7b8a114e4a4e00406487a";

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);

    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserName(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        //byte[]keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

}
