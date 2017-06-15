package processing.test.sketch_3d_en_linea;

import processing.core.PVector;

public class Celda
{
    private int mAncho=(Global.ancho / 5)*2;
    private int mLargo=(Global.largo / 5)*2;
    private int plano;

    private int id;
    boolean activa;
    private PVector location;

    public Celda(int plano,float x,float y,int id)
    {
        this.plano=plano;
        location=new PVector(x, y);
        this.id=id;
        activa=false;
    }

    public boolean isActiva()
    {
        return activa;
    }

    public void setActiva(char a)
    {
        switch (a)
        {
            case 's': activa=true;break;
            case 'n': activa=false;
        }
    }

}