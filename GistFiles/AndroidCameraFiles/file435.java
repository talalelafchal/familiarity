/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spesifikasi.smartphone;


/**
 *
 * @author User-2
 */
public class Spesifikasi implements Android{

    @Override
    public String panggilan() {
        return "Ya";
    }

    @Override
    public String wifi() {
        return "Ya";
    }

    @Override
    public String sinyal() {
        return "3G/4G";
    }

    @Override
    public String camera() {
        return "13Mp";
    }

    @Override
    public String sms() {
        return "Ya";
    }

    @Override
    public String internet() {
        return "Ya";
    }
    
}
class Implementasi{
    public static void main(String[] args){
        Spesifikasi spesifikasi = new Spesifikasi();
        System.out.println("\tSMARTPHONE");
        System.out.println("Panggilan\t: "+spesifikasi.panggilan());
        System.out.println("Support Wifi\t: "+spesifikasi.wifi());
        System.out.println("Sinyal\t\t: "+spesifikasi.sinyal());
        System.out.println("Camera\t\t: "+spesifikasi.camera());
        System.out.println("SMS\t\t: "+spesifikasi.sms());
        System.out.println("Internet\t: "+spesifikasi.internet());
    }
}
