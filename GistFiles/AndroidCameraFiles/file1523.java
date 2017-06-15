package com.example.ratanak.bottompopup;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Daravuth on 3/30/2017.
 */

public class DialogMenu implements View.OnClickListener {
    private Dialog dialog;
    private OnDialogMenuListener listener;

    public DialogMenu(Context context) {
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_item);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();


        TextView picture = (TextView) dialog.findViewById(R.id.camera);
        TextView gallery = (TextView) dialog.findViewById(R.id.gallery);
        picture.setOnClickListener(this);
        gallery.setOnClickListener(this);

    }

    public void setListener(OnDialogMenuListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.camera:
                listener.onPicturePress();
                dialog.dismiss();
                break;
            case R.id.gallery:
                listener.onGalleryPress();
                dialog.dismiss();
                break;
        }
    }

    public interface OnDialogMenuListener{
        void onPicturePress();
        void onGalleryPress();
    }
}
