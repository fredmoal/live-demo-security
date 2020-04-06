package fr.univ.orleans.webservices.livedemosecurity.controller;

import fr.univ.orleans.webservices.livedemosecurity.config.JwtTokens;
import fr.univ.orleans.webservices.livedemosecurity.modele.Utilisateur;
import fr.univ.orleans.webservices.livedemosecurity.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Services services;
    @Autowired
    private JwtTokens jwtTokens;

    static class AuthDTO {
        public String username;
        public String password;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthDTO auth) {
        if (auth.username==null||auth.password==null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Utilisateur> user = services.findUtilisateurById(auth.username);
        if (user.isPresent()&&passwordEncoder.matches(auth.password, user.get().getPassword())) {
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, JwtTokens.TOKEN_PREFIX+jwtTokens.genereToken(user.get())).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
