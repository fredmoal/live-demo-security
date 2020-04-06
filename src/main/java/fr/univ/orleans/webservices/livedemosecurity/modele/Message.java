package fr.univ.orleans.webservices.livedemosecurity.modele;

public class Message {
    private Long id;
    private String texte;
    private Utilisateur utilisateur;

    public Message() {
    }

    public Message(Long id, String texte, Utilisateur utilisateur) {
        this.id = id;
        this.texte = texte;
        this.utilisateur = utilisateur;
    }

    public Long getId() {
        return id;
    }

    public String getTexte() {
        return texte;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return id.equals(message.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
