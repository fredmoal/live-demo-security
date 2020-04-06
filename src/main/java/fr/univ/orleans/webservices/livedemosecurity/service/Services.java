package fr.univ.orleans.webservices.livedemosecurity.service;

import fr.univ.orleans.webservices.livedemosecurity.modele.Message;
import fr.univ.orleans.webservices.livedemosecurity.modele.Utilisateur;

import java.util.Collection;
import java.util.Optional;

public interface Services {
    Collection<Message> findAllMessages();

    Optional<Message> findMessageById(long id);

    Message saveMessage(Message message);

    void deleteMessage(long id);

    Optional<Utilisateur> findUtilisateurById(String id);

    Utilisateur saveUtilisateur(Utilisateur utilisateur);

    Collection<Utilisateur> findAllUtilisateur();

    void deleteUtilisateur(String login);
}
