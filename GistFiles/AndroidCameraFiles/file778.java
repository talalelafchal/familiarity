/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soal.pertama2017;

/**
 *
 * @author Windows 10
 */
public class Android implements Smartphone {

    @Override
    public String Panggilan() {
        return "Yes";
    }

    @Override
    public String Sms() {
        return "Yes";
    }

    @Override
    public String Wifi() {
        return "Yes";
    }

    @Override
    public String Sinyal() {
        return "Support 3G/4G";
    }

    @Override
    public String Browsing() {
        return "Yes";
    }

    @Override
    public String Camera() {
        return "13 Mp";
    }
    
}

class implementasi{
    public static void main(String[] args) {
        Android android = new Android();
        System.out.println("==========================================");
        System.out.println("\tSMARTPHONE SAMSUNG GALAXY Z");
        System.out.println("==========================================");
        System.out.println(" Support Wifi \t: "+android.Wifi());
        System.out.println(" Signal \t: "+android.Sinyal());
        System.out.println(" Camera \t: "+android.Camera());
        System.out.println(" Message \t: "+android.Sms());
        System.out.println(" Internet \t: "+android.Browsing());
        System.out.println(" Call \t\t: "+android.Panggilan());
        System.out.println("==========================================");
    }
}
