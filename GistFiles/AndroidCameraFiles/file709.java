package app.vitorueda.com.urnaeletronica.app.vitorueda.com.model;

import java.util.UUID;

public class Candidato {
    public UUID getID() {
        return mID;
    }

    private UUID mID;
    private String mNome;

    public String getNome() {
        return mNome;
    }

    public void setNome(String nome) {
        mNome = nome;
    }

    public String getPartido() {
        return mPartido;
    }

    public void setPartido(String partido) {
        mPartido = partido;
    }

    private String mPartido;
}
