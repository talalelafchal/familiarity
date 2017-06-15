/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartphone;

/**
 *
 * @author VikriMuhammad
 */
public class Spesifikasi implements Android{
    

    @Override
    public String melakukanPanggilan() {
        return "Ya";
                                                                                         }

    @Override
    public String mengirimPesan() {
        return "Ya";
    }

    @Override
    public String Wifi() {
        return "Ya";
    }

    @Override
    public String Sinyal() {
        return "3G/4G";
    }

    @Override
    public String Camera() {
        return "13Mp";
    }

    @Override
    public String Internet() {
        return "Ya";
    }
    
}
    class implementasi{
        public static void main(String[] args) {
            Spesifikasi spesifikasi = new Spesifikasi();
            
           System.out.println("panggilan : " +spesifikasi.melakukanPanggilan());
           System.out.println("SMS : " +spesifikasi.mengirimPesan());
           System.out.println("Internet : " +spesifikasi.Internet());
           System.out.println("Sinyal : " +spesifikasi.Sinyal());
           System.out.println("Suport Wifi : " +spesifikasi.Wifi());
           System.out.println("Kamera : " +spesifikasi.Camera());
        }
    }