package sagarpreet97.reminder;

import android.graphics.Bitmap;

/**
 * Created by sagarpreet chadha on 21-07-2016.
 */
public class Reminder_listview_data {
    String title ;
    String desc ;
    Bitmap bm ;

    public Reminder_listview_data(String title, String desc, Bitmap bm) {
        this.title = title;
        this.desc = desc;
        this.bm = bm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Bitmap getBm() {
        return bm;
    }

    public void setBm(Bitmap bm) {
        this.bm = bm;
    }
}
