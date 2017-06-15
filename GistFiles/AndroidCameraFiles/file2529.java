package processing.test.sketch_3d_en_linea;

import processing.core.PApplet;
import android.view.MotionEvent;

import java.util.ArrayList;

import ketai.ui.*;

public class sketch_3d_en_linea extends PApplet {
    ArrayList<Celda> celdas;
    ArrayList<X> xs;
    ArrayList<Y> ys;

    KetaiGesture gesture;
    float lastx = 0;
    float lasty = 0;
    int ancho = Global.ancho;
    int largo = Global.largo;
    int separacion = 80;
    double velArrastre = 1.5;
    private float sumarx;
    private float sumary;

    public void setup()
    {
        celdas=new ArrayList<Celda>();
        xs=new ArrayList<X>();
        ys=new ArrayList<Y>();

        gesture = new KetaiGesture(this);
        //Turn on smoothing to make everything pretty.
        smooth();
        stroke(64, 64, 64);
        fill(127, 127, 127, 63);
        //Tell the rectangles to draw from the center point (the default is the TL corner)
        rectMode(CENTER);
    }

    float angleX = 0;
    float angleY = 0;

    public void draw() {
        background(220);
        //se centra
        translate(width / 2, height / 2);
        //se dibuja la "caja"
        pushMatrix();

            //se calculan movimientos
            calcularMovimiento();
            //se rota al angulo resultante
            rotateY(angleY);
            rotateX(angleX);

            insertCube();
        drawXs();
        drawYs();
        //se borra la "caja"
        popMatrix();
    }

    private void drawXs() {

        for(int i=0;i<xs.size();i++)
        {
           xs.get(i).draw();
        }
    }


    private void drawYs() {
        for(int i=0;i<ys.size();i++)
        {
           // ys.get(i).draw();
        }
    }

    public void calcularMovimiento() {
        float difx = mouseX - lastx;
        float dify = mouseY - lasty;
        if (difx > velArrastre) {
            sumarx += (PI / width);
        } else if (difx < -velArrastre) {
            sumarx -= (PI / width);
        } else if (dify > velArrastre) {
            sumary -= (PI / height);
        } else if (dify < -velArrastre) {
            sumary += (PI / height);
        } else {
            sumarx = 0;
            sumary = 0;
        }
        lastx = mouseX;
        lasty = mouseY;
        angleY += sumarx;
        angleX += sumary;
    }

    public void insertCube() {
        //ultimo plano
        translate(0, 0, -separacion);
        insertPlano();
        //plano central
        translate(0, 0, separacion);
        insertPlano();
        //se dibuja el indicador de no activo
        rect(0, 0, (ancho / 5)*2, (largo / 5)*2);
        //primer plano
        translate(0, 0, separacion);
        insertPlano();
    }


    public void insertPlano() {
        for(int i=0;i<9;i++)
        {
            celdas.add(new Celda(separacion,5,5,5));
        }
        rect(0, 0, ancho, largo);
        //verticales
        line(-ancho / 5, -largo / 2, -ancho / 5, largo / 2); //x1,y1,x2,y2
        line(ancho / 5, -largo / 2, ancho / 5, largo / 2);
        //horizontales
        line(-ancho / 2, -largo / 5, ancho / 2, -largo / 5);
        line(-ancho / 2, largo / 5, ancho / 2, largo / 5);
    }

    public String sketchRenderer() {
        return P3D;
    }

    public boolean surfaceTouchEvent(MotionEvent event)
    {
        //call to keep mouseX, mouseY, etc updated
        super.surfaceTouchEvent(event);
        //forward event to class for processing
        return gesture.surfaceTouchEvent(event);
    }


    public void onDoubleTap(float x, float y)
    {

    }

    public void onTap(float x, float y)
    {
       xs.add(new X(celdas.get(0),x,y));
    }

    public void onLongPress(float x, float y)
    {

    }

    //the coordinates of the start of the gesture,
// end of gesture and velocity in pixels/sec
    public void onFlick( float x, float y, float px, float py, float v)
    {

    }

    public void onPinch(float x, float y, float d)
    {

    }


    class X
    {
        private Celda celda;
        private float x;
        private float y;

        public X(Celda celda,float x,float y)
        {
           this.x=x-(width/2);
           this.y=y-(height/2);
           this.celda=celda;
        }

        public void draw()
        {
            //translate(0,0,-80);
            pushStyle();
            stroke(2);
            fill(255);
            //paintX();
            paintO();
            //rotateZ(0.5f);
            //rect(x, y, ancho/5, largo/5);
            //rotateZ(-0.5f);
            popStyle();
            //translate(0,0,80);
        }

        void paintX () {
            // diagonal
            // Length
            final int Len1 = 90;

            // red
            fill(color(137,1,1));

            // Lines?
            // stroke (111);
            noStroke();

            //pushMatrix();
            //PVector MyPVector = new PVector( 410.0, 710.0, 220.0 );
            // MyPVector =  GetValuePVector (i,j);
            //translate ( MyPVector.x, MyPVector.y, MyPVector.z );

            pushMatrix();
            rotateZ(radians(45));
            box (Len1,20,15);
            popMatrix();

            pushMatrix();
            rotateZ(radians(-45));
            box (Len1,20,15);
            popMatrix();
            noStroke();
            //popMatrix();
        }

        void paintO() {
            strokeWeight(2);
            stroke(121, 76, 31);
            fill(155, 101, 44);
            ellipse(238, 158, 210, 210);

//hole of donut
            strokeWeight(1);
            stroke(121, 76, 31);
            fill(255);
            ellipse(238, 158, 30, 30);


        }
    }
}