package fr.univ.orleans.webservices.livedemosecurity.config;

import fr.univ.orleans.webservices.livedemosecurity.config.erreurs.MauvaisTokenException;
import fr.univ.orleans.webservices.livedemosecurity.modele.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokens {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long EXPIRATION_TIME = 1_000_000;

    @Autowired
    private Key secretKey;

    public String genereToken(Utilisateur user) {
        String login = user.getLogin();
        Claims claims = Jwts.claims().setSubject(login);
        claims.put("roles", user.getRoles());
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
        return token;
    }

    public UsernamePasswordAuthenticationToken decodeToken(String token) throws MauvaisTokenException {
        // le token a un entete ?
        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.replaceFirst(TOKEN_PREFIX, "");
        }
        try {
            Jws<Claims> jwsClaims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            // Signature vérifiée : le token est fiable
            String login = jwsClaims.getBody().getSubject();
            List<String> roles = jwsClaims.getBody().get("roles",List.class);
            List<SimpleGrantedAuthority> authorities =
                    roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(login,null,authorities);

            return authentication;
        } catch (JwtException e) {
            // mauvais format de token !
            throw new MauvaisTokenException(e.getMessage());
        }
    }
}
