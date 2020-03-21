package fr.univ.orleans.webservices.livedemosecurity.config;

import fr.univ.orleans.webservices.livedemosecurity.config.erreur.MauvaisTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokens {

    private static final long EXPIRATION_TIME = 1_000_000;
    public static final String PREFIX = "Bearer ";

    @Autowired
    private Key secretKey;

    public String genereToken(UserDetails userDetails) {
        String login = userDetails.getUsername();
        var roles = userDetails.getAuthorities().stream().map(auth->auth.getAuthority()).collect(Collectors.toList());
        Claims claims = Jwts.claims().setSubject(login);
        claims.put("roles", roles);
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
        return token;
    }

    public UsernamePasswordAuthenticationToken decodeToken(String token) throws MauvaisTokenException {
        // le token a une entete ? Bearer
        if (token.startsWith(PREFIX)) {
            token = token.replaceFirst(PREFIX,"");
        }

        try {
            Jws<Claims> jwsClaims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            // OK le token est bon
            String login = jwsClaims.getBody().getSubject();
            List<String> roles = jwsClaims.getBody().get("roles",List.class);

            List<SimpleGrantedAuthority> authorities =
                    roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(login, null, authorities);

            return authentication;

        } catch (JwtException e) {
            throw new MauvaisTokenException();
        }
    }
}
