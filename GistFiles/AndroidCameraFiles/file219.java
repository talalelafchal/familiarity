/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tugas.java;

/**
 *
 * @author Ramadhan
 */
public class sistemAndroid implements smartphone {
    
   
    
@Override
    public String os() {
        return "android 6.1"; //To change body of generated methods, choose Tools | Templates.
    }

    
    @Override
    public String wifi() {
        return " ya"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String sinyal() {
        return "3G/4G"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int camera() {
        return 13 ;  //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String sms() {
        return "ya"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String internet() {
        return "ya"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String panggilan() {
        return "ya"; //To change body of generated methods, choose Tools | Templates.
    }

    
}

class implementasi{
    
    public static void main(String[] args) {
        sistemAndroid  sitemAndroid = new sistemAndroid();
        
        
     
        System.out.println(" SPESIFIKASI HP TERBARU SAMSUNG");
        System.out.println(" ================================");
        System.out.println("OS \t \t: "+sitemAndroid.os());
        System.out.println("Support Wifi \t:"+sitemAndroid.wifi());
        System.out.println("Sinyal \t\t: "+sitemAndroid.sinyal());
        System.out.println("Kamera \t\t: "+sitemAndroid.camera()+" mp");
        System.out.println("sms \t\t: "+sitemAndroid.sms());
        System.out.println("Internet \t: "+sitemAndroid.internet());
        System.out.println("Panggilan \t: "+sitemAndroid.panggilan());
      
        
        
        
    }
}