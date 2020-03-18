package fr.univ.orleans.webservices.livedemosecurity.controller;

import fr.univ.orleans.webservices.livedemosecurity.modele.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api")
public class MessageController {
    private static List<Message> messages = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong(1L);

    @PostMapping("/messages")
    public ResponseEntity<Message> create(@RequestBody Message message) {
        // il n'a pas d'id, juste un texte
        Message messageRec = new Message( counter.getAndIncrement(), message.getTexte() );
        messages.add(messageRec);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(messageRec.getId()).toUri();

        return ResponseEntity.created(location).body(messageRec);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>>  getAll() {
        return ResponseEntity.ok().body(messages);
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<Message>  findById(@PathVariable("id") Long id) {
        Optional<Message> message = messages.stream().filter(m->m.getId()==id).findAny();
        if (message.isPresent()) {
            return ResponseEntity.ok().body(message.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity  deleteById(@PathVariable("id") Long id) {
        for(int index=0; index<messages.size();index++) {
            if (messages.get(index).getId()==id) {
                messages.remove(index);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}