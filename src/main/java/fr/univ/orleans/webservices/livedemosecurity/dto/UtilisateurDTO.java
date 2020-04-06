package fr.univ.orleans.webservices.livedemosecurity.dto;

import java.util.List;

public class UtilisateurDTO {
    private String login;
    private String password;
    private boolean isAdmin;
    private List<Long> messagesIds;

    public UtilisateurDTO(String login, String password, boolean isAdmin) {
        this.login = login;
        this.password = password;
        this.isAdmin = isAdmin;
    }


    public UtilisateurDTO() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public List<Long> getMessagesIds() {
        return messagesIds;
    }

    public void setMessagesIds(List<Long> messagesIds) {
        this.messagesIds = messagesIds;
    }
}
