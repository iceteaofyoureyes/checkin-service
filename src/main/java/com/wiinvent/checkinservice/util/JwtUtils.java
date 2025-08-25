package com.wiinvent.checkinservice.util;

import com.wiinvent.checkinservice.entity.Role;
import com.wiinvent.checkinservice.entity.User;
import com.wiinvent.checkinservice.entity.Permission;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtUtils {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expirationMs}")
    private long jwtExpirationMs;

    public TokenInfo generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", user.getRoles().stream()
                .map(Role::getRoleName)
                .toList());
        claims.put("permissions", user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermissionName)
                .toList());
        long expiry = System.currentTimeMillis() + jwtExpirationMs;
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(expiry))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return new TokenInfo(token, expiry);
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public List<String> getRoles(String token) {
        Object roles = getClaims(token).get("roles");
        if (roles instanceof List<?> list) {
            return list.stream()
                    .map(Object::toString)
                    .toList();
        }
        return Collections.emptyList();
    }

    public List<String> getPermissions(String token) {
        Object permissions = getClaims(token).get("permissions");
        if (permissions instanceof List<?> list) {
            return list.stream()
                    .map(Object::toString)
                    .toList();
        }
        return Collections.emptyList();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public record TokenInfo(String token, long expiryTime) {}
}
