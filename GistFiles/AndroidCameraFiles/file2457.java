package com.example.contactlist;

import android.graphics.Bitmap;

public class User {
	String name;
	String number;
    String uri;
    String id;
    Bitmap bit;

    public String getName() {
     return name;
    }

    public void setName(String name) {
     this.name = name;
    }
    
    public String getNumber() {
        return number;
       }

       public void setNumber(String number) {
        this.number = number;
       }

    public Bitmap getbi() {
     return bit;
    }

    public void setbi(Bitmap bit) {
     this.bit = bit;
    }

    public String getUri() {
     return uri;
    }

    public void setUri(String uri) {
     this.uri = uri;
    }

   
       	    	 public String getidt() {
       	    	     return id;
       	    	    }

       	    	    public void setid(String id) {
       	    	     this.id = id;
       	    	    }
       	    	



    public User(String id,String name,String number,String uri,Bitmap bit) {
     super();
     this.id=id;
     this.name = name;
     this.number= number;
     this.uri=uri;
     this.bit=bit;

     
    }
}