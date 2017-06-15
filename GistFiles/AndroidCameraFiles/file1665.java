/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tugas.contohinterface;

/**
 *
 * @author Ruslan
 */
public class DigiCoop implements Smartphone {

    @Override
    public String Wifi() {
        return "Ya";
    }

    @Override
    public String Sinyal() {
        return "3G/4G/5G";
    }

    @Override
    public int Camera() {
        return 46;
    }

    @Override
    public String SMS() {
        return "Ya";
    }

    @Override
    public String Internet() {
        return "Ya";
    }

    @Override
    public String Panggilan() {
        return "Ya";
    }
     @Override
    public String OS() {
        return "Android 6.1";
    }
}

class Implement {
    public static void main(String[] args) {
        DigiCoop DigiCoop = new DigiCoop();
        
        System.out.println("Smartphone DigiCoop");
        System.out.println("-------------------");
        System.out.println(" ");
        System.out.println("Operating System : "+DigiCoop.OS());
        System.out.println("Support WiFi     : "+DigiCoop.Wifi());
        System.out.println("Sinyal           : "+DigiCoop.Sinyal());
        System.out.println("Kamera           : "+DigiCoop.Camera()+"mp");
        System.out.println("SMS              : "+DigiCoop.SMS());
        System.out.println("Internet         : "+DigiCoop.Internet());
        System.out.println("Panggilan        : "+DigiCoop.Panggilan());
    }
}

/*
    NIS  : 10150260
    Nama : Ruslan Wanandi
    Kelas : XI RPL3
*/