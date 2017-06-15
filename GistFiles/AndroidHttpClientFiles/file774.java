package com.example.nawfal.caridata;

import static com.example.nawfal.caridata.R.id.name;

/**
 * Created by nawfal on 27/01/2017.
 */

public class Biodata extends Koneksi {
    String URL = "http://192.168.1.5:8080/caridata/server.php";
    String url = "";
    String response = "";

    public String getBiodataById(int toilet_id) {
        try {
            url = URL + "?operasi=get_biodata_by_toilet_id&toilet_id=" + toilet_id;
            System.out.println("URL Insert Biodata : " + url);
            response = call(url);
        } catch (Exception e) {
        }
        return response;
    }

    public String updateBiodata(String toilet_id, String name, String latitude, String longitude, String price) {
        try {
            url = URL + "?operasi=update&toilet_id=" + toilet_id + "&name=" + name + "&latitude=" + latitude +
                    "&longitude=" + longitude + "&price=" + price;
            System.out.println("URL Insert Biodata : " + url);
            response = call(url);
        } catch (Exception e) {
        }
        return response;
    }

    public String deleteBiodata(int toilet_id) {
        try {
            url = URL + "?operasi=delete&toilet_id=" + toilet_id;
            System.out.println("URL Insert Biodata : " + url);
            response = call(url);
        } catch (Exception e) {
        }
        return response;
    }

}