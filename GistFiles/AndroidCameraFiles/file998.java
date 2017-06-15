package net.aldemir.myapp.gui.activity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import at.ingdiba.ingdibaapp.R;
import at.ingdiba.ingdibaapp.gui.activity.base.BaseActionBarActivity;

public class MyActivity extends BaseActionBarActivity {
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_barcode_reader);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
    }
}