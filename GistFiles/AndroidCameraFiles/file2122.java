package com.soalinterface;

/**
 *
 * @author user
 */
public class SmartphoneAndroid implements SmartPhone {

    @Override
    public String supportwifi() {
        return "ya";
    }

    @Override
    public String sinyal() {
        return "3G/4G";
    }

    @Override
    public String camera() {
        return "13mp";
    }

    @Override
    public String sms() {
        return "ya";
    }

    @Override
    public String internet() {
        return "ya";
    }

    @Override
    public String panggilan() {
        return "ya";
    }
   
    
}
class Implementasi{
    public static void main(String[] args){
        
        //object
        SmartphoneAndroid SmartPhone = new SmartphoneAndroid();
        System.out.println("INI PERUSAHAAN A");
        System.out.println("support wifi : "+SmartPhone.supportwifi());
        System.out.println("sinyal : "+SmartPhone.sinyal());
        System.out.println("camera : "+SmartPhone.camera());
        System.out.println("sms : "+SmartPhone.sms());
        System.out.println("internet : "+SmartPhone.internet());
        System.out.println("panggilan : "+SmartPhone.panggilan());
        
        
    }
}