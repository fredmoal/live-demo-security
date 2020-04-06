package fr.univ.orleans.webservices.livedemosecurity.controller.errors;

public class UtilisateurNotFound extends RuntimeException {
    public UtilisateurNotFound(String login) {
        super("Utilisateur "+login+" not found");
    }
}
