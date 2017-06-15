// sacado de este snipsets y cambiado las rutas de osc para que pille el programa andorid osc. 
import processing.opengl.*;
import oscP5.*;

OscP5 oscP5;

float xrot = 0;
float zrot = 0;

float xrot_targ = 0;
float zrot_targ = 0;
float orientation = 0;
float xx = 0;
float yy = 0;

float dampSpeed = 5;

void setup() {
  size(400,400, OPENGL);
  oscP5 = new OscP5(this,8000);
  smooth();
}

void draw() {
  camera(  0, 0, 300,
         0, 0, 0,
         0.0, 1.0, 0.0
     );
  background(0); 

  // Basic value smoothing

  if (xrot_targ > xrot) {
    xrot = xrot + ((xrot_targ - xrot) / dampSpeed);
  } else {
    xrot = xrot - ((xrot - xrot_targ) / dampSpeed);
  }

  if (zrot_targ > zrot) {
    zrot = zrot + ((zrot_targ - zrot) / dampSpeed);
  } else {
    zrot = zrot - ((zrot - zrot_targ) / dampSpeed);
  }

 // Detection for if the iPhone is upsidedown or not

  if (orientation < 0) {
    fill(255,0,yy*5);
	translate(xx, yy);
    rotateX(radians(xrot));
    rotateZ(radians(zrot));
	
  } else {
    fill(255,0,xx*10);
	translate(xx, yy);
    rotateX(radians(xrot*-1));
    rotateZ(radians(zrot*-1));
  }
  box(130,10,60);

}

void oscEvent(OscMessage theOscMessage) {
	println(theOscMessage.addrPattern());
  if(theOscMessage.checkAddrPattern("/acc")==true) {
	 
      xrot_targ = (theOscMessage.get(0).floatValue()*90);
      zrot_targ = (theOscMessage.get(1).floatValue()*90)*-1;
      orientation = theOscMessage.get(2).floatValue();
  }
  if(theOscMessage.checkAddrPattern("/touch")==true) {
	 
      xx = (theOscMessage.get(0).floatValue()/10);
      yy = (theOscMessage.get(1).floatValue()/10);
	  println(xx);
  }
}