package fr.univ.orleans.webservices.livedemosecurity.service;

import fr.univ.orleans.webservices.livedemosecurity.modele.Message;
import fr.univ.orleans.webservices.livedemosecurity.modele.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ServicesImpl implements Services {
    // "bd"
    private static Map<Long, Message> messages = new TreeMap<>();
    private static Map<String, Utilisateur> utilisateurs = new TreeMap<>();
    private final AtomicLong counter = new AtomicLong(1L);
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Collection<Message> findAllMessages() {
        return messages.values();
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public Message saveMessage(Message message) {
        if (message.getId()==null) {
            message.setId(counter.getAndIncrement());
        }
        messages.put(message.getId(), message);
        message.getUtilisateur().addMessage(message);
        return message;
    }

    @Override
    public void deleteMessage(long id) {
        Optional<Message> message = findMessageById(id);
        messages.remove(message.get().getId());
        message.get().getUtilisateur().getMessages().remove(message);
    }

    @Override
    public Optional<Utilisateur> findUtilisateurById(String id) {
        return Optional.ofNullable(utilisateurs.get(id));
    }

    @Override
    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        // encode le mot de passe
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        utilisateurs.put(utilisateur.getLogin(),utilisateur);
        return utilisateur;
    }

    @Override
    public Collection<Utilisateur> findAllUtilisateur() {
        return utilisateurs.values();
    }

    @Override
    public void deleteUtilisateur(String login) {

    }
}
