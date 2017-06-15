/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soal.pertama;

/**
 *
 * @author Windows 10
 */
public class Android17 implements Smartphone {

    @Override
    public String panggilan() {
        return "ya";
    }

    @Override
    public String Sms() {
        return "ya";
    }

    @Override
    public String Wifi() {
        return "ya";
    }

    @Override
    public String Sinyal() {
        return "3G/4G";
    }

    @Override
    public String Browsing() {
        return "ya";
    }

    @Override
    public String Camera() {
        return "13mp";
    }
    
}

class implementasi{
    public static void main(String[] args) {
        Android17 android17 = new Android17();
        System.out.println("====== Smartphone berbasis android terbaru ======");
        System.out.println("Support Wifi : "+android17.Wifi());
        System.out.println("Sinyal : "+android17.Sinyal());
        System.out.println("Camera : "+android17.Camera());
        System.out.println("SMS : "+android17.Sms());
        System.out.println("Internet : "+android17.Browsing());
        System.out.println("Panggilan : "+android17.panggilan());
    }
}