package com.example.tudelft.moodpizza.Selfie;

/**
 * Created by tudelft on 20/09/2016.
 */
import java.io.Serializable;

public class SelfieItem implements Serializable {

    private final int id;
    private final int vindikvos;

    public SelfieItem(int id, int vindikvos) {
        this.id = id;
        this.vindikvos = vindikvos;
    }

    public int getId() {
        return id;
    }

    public int getVindikvos() { return vindikvos; }

}
