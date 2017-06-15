package gloird.fr.testspinner;

/**
 * Created by nicolas on 04/12/2014.
 */
public class classe {
    private int id;
    private String nom;
    private int nb;

    public classe(int id, String nom, int nb) {
        this.id = id;
        this.nom = nom;
        this.nb = nb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getNb() {
        return nb;
    }

    public void setNb(int nb) {
        this.nb = nb;
    }

    @Override
    public String toString() {
        return "Classe : "+nom;
    }
}
