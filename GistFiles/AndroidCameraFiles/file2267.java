package com.riot.projetoriotboothrfid;
import android.graphics.Bitmap;
import java.util.Date;
import java.util.List;

/**
 * Created by joaopalacio on 03/07/13.
 */
public class User {
    private String uid;
    private String rfid;
    private String Nome;
    private Bitmap Face;

    public User() {

    }

    public String getuid(){ return uid;}
    public String getNome(){ return Nome;}
    public Bitmap getFace(){ return Face;}

    public void setuid(String value){ this.uid=value;}
    public void setrfid(String value){ this.rfid=value;}
    public void setNome(String value){ this.Nome=value;}
    public void setFace(Bitmap value){ this.Face=value;}
}
