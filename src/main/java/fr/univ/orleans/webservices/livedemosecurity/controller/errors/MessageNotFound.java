package fr.univ.orleans.webservices.livedemosecurity.controller.errors;

public class MessageNotFound extends RuntimeException {
    public MessageNotFound(Long id) {
        super("Message "+id+" not found");
    }
}
