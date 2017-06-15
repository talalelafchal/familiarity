/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tugas.Interface;

/**
 *
 * @author USER
 */
public class RMN implements SmartPhone{

    @Override
    public String wifi() {
        return "Ya";
    }

    @Override
    public String sinyal() {
        return "3G/4G";
    }

    @Override
    public int camera() {
        return 13;
    }

    @Override
    public String sms() {
        return "Ya";
    }

    @Override
    public String panggilan() {
        return "Ya";
    }

    @Override
    public String internet() {
        return "Ya";
    }

    @Override
    public String os() {
        return "Android 6.1";
    }
    
}

class implementasi{
    public static void main(String[] args) {
        
        RMN Sumsang = new RMN() ;
        System.out.println("SmartPhone Sumsang");
        
        System.out.println("_______________________________________________");
        
        System.out.println("Sistem Operasi      : "+Sumsang.os());
        System.out.println("Suport Wifi         : "+Sumsang.wifi());
        System.out.println("Sinyal              : "+Sumsang.sinyal());
        System.out.println("Kamera              : "+Sumsang.camera()+" MP");
        System.out.println("Sms                 : "+Sumsang.sms());
        System.out.println("Internet            : "+Sumsang.internet());
        System.out.println("Panggilan           : "+Sumsang.panggilan());
    }
}