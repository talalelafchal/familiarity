package com.newtecsolutions.photometo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.af.jutils.DisplayUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by pedja on 11/18/16 11:34 AM.
 * This class is part of the Photometo
 * Copyright © 2016 ${OWNER}
 */

public class PickPhotoDialogFragment extends DialogFragment implements View.OnClickListener
{
    private static final int PICK_PHOTO_REQUEST = 1001;
    private static final int TAKE_PHOTO_REQUEST = 1002;
    private static final int FROM_FAVORITES_REQUEST = 1003;
    private static final int FROM_USER_PHOTOS_REQUEST = 1004;
    private static final String EXTRA_ANCHOR_Y = "anchor_y";
    private static final String EXTRA_IS_BACKGROUND = "is_background";

    public static PickPhotoDialogFragment newInstance(int anchorY, boolean isBackground)
    {
        Bundle args = new Bundle();
        args.putInt(EXTRA_ANCHOR_Y, anchorY);
        args.putBoolean(EXTRA_IS_BACKGROUND, isBackground);

        if(isBackground)
        {
            SeamlessCloning.cleanupTempPhotoDir();
        }

        PickPhotoDialogFragment fragment = new PickPhotoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String mCameraPhotoPath;
    private boolean isBackground;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (!(context instanceof OnPhotoSelectedListener))
        {
            throw new IllegalStateException("Activity must implement OnPhotoSelectedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        isBackground = getArguments().getBoolean(EXTRA_IS_BACKGROUND, true);
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_pick_photo_source_dialog, null);

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);

        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText(isBackground ? R.string.browse_background : R.string.browse_foreground);

        view.findViewById(R.id.ivPCamera).setOnClickListener(this);
        view.findViewById(R.id.ivPGallery).setOnClickListener(this);
        view.findViewById(R.id.ivPFavorite).setOnClickListener(this);
        view.findViewById(R.id.ivPProfile).setOnClickListener(this);

        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        view.post(() -> {
            Window window1 = dialog.getWindow();
            WindowManager.LayoutParams wlp = window1.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.y = new DisplayUtils(getActivity()).screenHeight - getArguments().getInt(EXTRA_ANCHOR_Y);
            window1.setAttributes(wlp);
        });

        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_PHOTO_REQUEST && resultCode == Activity.RESULT_OK)
        {
            ((OnPhotoSelectedListener) getActivity()).onPhotoSelected(data.getData(), isBackground);
            dismissAllowingStateLoss();
        }
        else if (requestCode == FROM_FAVORITES_REQUEST && resultCode == Activity.RESULT_OK)
        {
            ((OnPhotoSelectedListener) getActivity()).onPhotoSelected(data.getData(), isBackground);
            dismissAllowingStateLoss();
        }
        else if (requestCode == FROM_USER_PHOTOS_REQUEST && resultCode == Activity.RESULT_OK)
        {
            ((OnPhotoSelectedListener) getActivity()).onPhotoSelected(data.getData(), isBackground);
            dismissAllowingStateLoss();
        }
        else if(requestCode == TAKE_PHOTO_REQUEST && resultCode == Activity.RESULT_OK)
        {
            ((OnPhotoSelectedListener) getActivity()).onPhotoSelected(Uri.parse(mCameraPhotoPath), isBackground);
            dismissAllowingStateLoss();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ivPCamera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try
                {
                    photoFile = createImageFile();
                }
                catch (IOException ex)
                {
                    // Error occurred while creating the File
                }
                if (photoFile != null)
                {
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.newtectsolutions.photometo.fileprovider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(Intent.createChooser(intent, null), TAKE_PHOTO_REQUEST);
                }
                break;
            case R.id.ivPGallery:
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, null), PICK_PHOTO_REQUEST);
                break;
            case R.id.ivPFavorite:
                PickPhotoActivity.start(this, FROM_FAVORITES_REQUEST, true);
                break;
            case R.id.ivPProfile:
                PickPhotoActivity.start(this, FROM_USER_PHOTOS_REQUEST, false);
                break;
        }
    }

    private File createImageFile() throws IOException
    {
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                String.valueOf(System.currentTimeMillis()),  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCameraPhotoPath = image.getAbsolutePath();
        return image;
    }

    public interface OnPhotoSelectedListener
    {
        void onPhotoSelected(Uri uri, boolean isBackground);
    }
}
