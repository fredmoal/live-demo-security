package fr.univ.orleans.webservices.livedemosecurity.controller;

import fr.univ.orleans.webservices.livedemosecurity.controller.errors.MessageNotFound;
import fr.univ.orleans.webservices.livedemosecurity.controller.errors.UtilisateurNotFound;
import fr.univ.orleans.webservices.livedemosecurity.dto.MessageDTO;
import fr.univ.orleans.webservices.livedemosecurity.dto.UtilisateurDTO;
import fr.univ.orleans.webservices.livedemosecurity.modele.Message;
import fr.univ.orleans.webservices.livedemosecurity.modele.Utilisateur;
import fr.univ.orleans.webservices.livedemosecurity.service.Services;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MessageController {
    @Autowired
    private Services services;

    @Autowired
    private ModelMapper mapper;

    @PostMapping("/messages")
    public ResponseEntity<MessageDTO> create(Principal principal, @RequestBody MessageDTO messageDTO, UriComponentsBuilder base) {
        // récupère l'utilisateur AUTHENTIFIE qui poste le message
        Objects.requireNonNull(principal);
        String nom = principal.getName();
        Utilisateur utilisateur = services.findUtilisateurById(nom).orElseThrow(()->new UsernameNotFoundException(nom));
        // enregistrement du nouveau message
        Message message = services.saveMessage(new Message( null, messageDTO.getTexte(),utilisateur));

        URI location = base.path("/api/messages/{id}").buildAndExpand(message.getId()).toUri();
        MessageDTO messageRecDTO = mapper.map(message, MessageDTO.class);

        // notification d'un nouveau message dans le Stream
        notifications.onNext(messageRecDTO);

        return ResponseEntity.created(location).body(messageRecDTO);
    }

    @GetMapping(value = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<MessageDTO>>  getAll() {
        final Collection<Message> messages = services.findAllMessages();
        final List<MessageDTO> messagesDTO = messages.stream()
                .map(message -> mapper.map(message,MessageDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(messagesDTO);
    }


    @GetMapping("/messages/{id}")
    public ResponseEntity<MessageDTO>  findById(@PathVariable("id") Long id) {
        Message message = services
                .findMessageById(id)
                .orElseThrow(()->new MessageNotFound(id));
        return ResponseEntity.ok().body(mapper.map(message,MessageDTO.class));
    }


    @DeleteMapping("/messages/{id}")
    public ResponseEntity  deleteById(Principal principal,@PathVariable("id") Long id) {
        Message message = services
                .findMessageById(id)
                .orElseThrow(()->new MessageNotFound(id));
        if (!message.getUtilisateur().getLogin().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        services.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/utilisateurs")
    public ResponseEntity<UtilisateurDTO> register(@RequestBody UtilisateurDTO utilisateurDTO, UriComponentsBuilder base) {
        Predicate<String> isOk = s -> (s!=null)&&(s.length()>=2);
        if (!isOk.test(utilisateurDTO.getLogin()) || !isOk.test(utilisateurDTO.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        // vérifie si le login est déjà utilisé
        if (services.findUtilisateurById(utilisateurDTO.getLogin()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        // sauve l'utilisateur
        Utilisateur utilisateur = services.saveUtilisateur(new Utilisateur(utilisateurDTO.getLogin(),utilisateurDTO.getPassword(),false));

        URI location = base.path("/api/utilisateurs/{id}").buildAndExpand(utilisateur.getLogin()).toUri();

        return ResponseEntity.created(location).body(mapper.map(utilisateur,UtilisateurDTO.class));
    }

    @GetMapping("/utilisateurs/{login}")
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<UtilisateurDTO>  findUtilisateurById(@PathVariable("login") String login) {
        Utilisateur utilisateur = services
                .findUtilisateurById(login)
                .orElseThrow(()->new UtilisateurNotFound(login));
        return ResponseEntity.ok().body(mapper.map(utilisateur,UtilisateurDTO.class));
    }

    // STREAM de notifications
    private ReplayProcessor<MessageDTO> notifications = ReplayProcessor.create(0, false);;

    @GetMapping(value = "/messages/subscribe", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<MessageDTO> notification() {
        return Flux.from(notifications);
    }


    @ExceptionHandler
    public ResponseEntity<String> handleMessageNotFound(MessageNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler
    public ResponseEntity<String> handleUtilisateurNotFound(UtilisateurNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}