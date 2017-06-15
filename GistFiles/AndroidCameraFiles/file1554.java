/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.belajar.contohintervace;

   
/**
 *
 * @author Oding Hermansah
 */
public class advan implements android{

        @Override
        public String calling() {
            return "yes";
        }

        @Override
        public String sendmessage() {
            return "yes";
        }

        @Override
        public String suportwifi() {
            return "yes";
        }

        @Override
        public String suport4G() {
            return "yes";
        }

        @Override
        public String speedbrowse() {
            return "fast";
        }

        @Override
        public String camera() {
            return "13mp";
        }
       
   }
class implementasi{
    public static void main(String[] args){
        advan mouselogitech = new advan();
        System.out.println("HP ANDROID BARU ADVAN");
        System.out.println("memanggil    : "+ mouselogitech.calling());
        System.out.println("mengirim sms : "+ mouselogitech.sendmessage());
        System.out.println("suport wifi  : "+ mouselogitech.suportwifi());
        System.out.println("suport 4G/3G : "+ mouselogitech.suport4G());
        System.out.println("kecepatan    : "+ mouselogitech.speedbrowse());
        System.out.println("kamera       : "+ mouselogitech.camera());
    }
    }
