package cardexc.com.practicework;

import android.graphics.Bitmap;

public class Place {

    private String place;
    private String dateTime;
    private Bitmap image;

    public Place(String place, String dateTime, Bitmap image) {
        this.place = place;
        this.dateTime = dateTime;
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getPlace() {
        return place;
    }

    public String getDateTime() {
        return dateTime;
    }

}
