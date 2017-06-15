package com.android.systemui.statusbar.preferences;

import android.content.*;
import android.view.*;
import android.widget.*;
import android.provider.*;
import java.util.List;
import java.util.ArrayList;
import android.net.Uri;
import java.io.*;
import com.android.systemui.statusbar.preferences.*;

public class TorchController extends SettingsController {

    private ContentResolver mContentResolver;

    public TorchController (Context context, View button) {
        super (context, button);

        mContentResolver = context.getContentResolver();
    }

    protected int getPreferenceStatus () {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/sys/class/leds/torch/brightness"));
            int currentValue = Integer.parseInt(reader.readLine());
            reader.close();
            if (currentValue == 0)
                return 0;
            return 1;
        }catch (IOException e){
            return 0;
        }
    }

    protected void setPreferenceStatus(int status) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("/sys/class/leds/torch/brightness"));
            String output = "" + (status * 48);
            writer.write(output.toCharArray(), 0, output.toCharArray().length);
            writer.close();
        }catch (IOException e){

        }
    }

    protected String getSettingsIntent() {
        return null;
    }

    protected int getDrawableIcon() {
        int status = getPreferenceStatus();

        switch (status) {
            case 0: return 0x7f0200f6;
            case 1: return 0x7f0200f5;
        }

        return 0x0;
    }

}