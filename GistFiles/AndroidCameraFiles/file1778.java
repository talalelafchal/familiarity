/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.belajar.contohinterface;

/**
 *
 * @author Ismi Istantia
 */
public class SmartFrenSpesifikasi implements SmartFren {

    @Override
    public String tipehape() {
        return "SMARTFREN C";
    }

    @Override
    public String os() {
        return "Android";
    }

    @Override
    public String support() {
         return "ya";
    }

    @Override
    public String sinyal() {
        return"ya";
    }

    @Override
    public int camera() {
        return 13;
    }

    @Override
    public String sms() {
        return "ya";
    }

    @Override
    public String internet() {
        return"ya";
    }

    @Override
    public String panggilan() {
      return"ya" ;  
    }
    
}

class implementasi {
    public static void main (String[] args){
     SmartFrenSpesifikasi smartfren = new SmartFrenSpesifikasi();
     System.out.println("INI hape");
          System.out.println("------------------------------");
          System.out.println("OS:"+smartfren.os());
          System.out.println("Support sinyal:"+smartfren.support());
          System.out.println("Sinyal:"+smartfren.sinyal());
          System.out.println("Camera:"+smartfren.camera());
          System.out.println("Internet:"+smartfren.internet());
          System.out.println("panggilan:"+smartfren.panggilan());
    }
} 
