/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.handphone;

/**
 *
 * @author ACER D255
 */
public class SmartPhone implements HandPhone {

    @Override
    public String OS() {
    return "ANDROID 6.1";
    }

    @Override
    public String supportWifi() {
    return "YA";
    }

    @Override
    public String sinyal() {
    return "3G/4G"  ;
    }

    @Override
    public int camera() {
    return 13 ;
    }

    @Override
    public String sms() {
    return "Ya";
    }

    @Override
    public String internet() {
     return "Ya";
    }

    @Override
    public String panggilan() {
     return "Ya";
    }
}
    class Implementasi{
        
     public static void main(String [] args){
    
       SmartPhone smartPhone = new SmartPhone();
       
       System.out.println("OS:" +smartPhone.OS());
       System.out.println("Support Wifi :" +smartPhone.supportWifi());
       System.out.println("Sinyal:" +smartPhone.sinyal());
       System.out.println("Camera  :" +smartPhone.camera()+"MP");
       System.out.println("Sms  :" +smartPhone.sms());
       System.out.println("Internet:"+smartPhone.internet());
       System.out.println("Panggilan :"+smartPhone.panggilan());
       
       
    }
}
    