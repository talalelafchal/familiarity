/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soal.pertama;

/**
 *
 * @author ANGGIT
 */
public class Android17 implements smartphone {

    @Override
    public String panggilan() {
      return "ya"; 
    }

    @Override
    public String sms() {
      return "ya";
    }

    @Override
    public String wifi() {
      return "ya";
    }

    @Override
    public String sinyal() {
      return "3G/4G";
    }

    @Override
    public String camera() {
      return "13 mp";
    }

    @Override
    public String bowsing() {
      return "ya";
    }
}

class implementasi{
    public static void main(String[] args) {

        Android17 android = new Android17();
        
        System.out.println("          SMARTPHONE ANDROID");
        System.out.println("=========================================");
        System.out.println("       Support wifi : "+android.wifi());
        System.out.println("       sinyal       : "+android.sinyal());
        System.out.println("       camera       : "+android.camera());
        System.out.println("       sms          : "+android.sms());
        System.out.println("       internet     : "+android.bowsing());
        System.out.println("       panggilan    : "+android.panggilan());
        System.out.println("=========================================");
        
    }
    
    
}