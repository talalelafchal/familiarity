package com.ztt.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by 123 on 14-11-12.
 */
public class CrimeCameraFragment extends Fragment {
    public static final String EXTRA_FILENAME="com.ztt.criminalintent.photo_filename";

    private static final String TAG="CrimeCameraFragment";


    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;

    private ShutterCallback mShutterCallback=new ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };
    private PictureCallback mJPEGCallback=new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            String filename= UUID.randomUUID().toString()+".jpg";
            FileOutputStream os=null;
            boolean success=true;
            try {
                os=getActivity().openFileOutput(filename,getActivity().MODE_PRIVATE);
                os.write(bytes);
            } catch (Exception e) {
                Log.e(TAG,"Error writing to file"+filename,e);

                success=false;
            }
            finally {
                if(os!=null)
                    try {
                        os.close();
                    } catch (IOException e) {
                        Log.e(TAG,"Error in closing file"+filename,e);
                        success=false;
                    }
            }
            if (success){
                Intent intent=new Intent();
                intent.putExtra(EXTRA_FILENAME,filename);
                getActivity().setResult(Activity.RESULT_OK,intent);
                Log.i(TAG,"JPEG saved at"+filename);
            }
            else
            {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if(mCamera!=null)
        {
            mCamera.release();
            mCamera=null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamera=Camera.open(0);
    }

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v=getActivity().getLayoutInflater().inflate(R.layout.fragment_crime_camera,null);
        mProgressContainer=v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        Button takePictureButton=(Button)v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCamera!=null)
                {
                    mCamera.takePicture(mShutterCallback,null,mJPEGCallback);
                }
            }
        });
        mSurfaceView=(SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
        final SurfaceHolder holder=mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if(mCamera!=null)
                {
                    try {
                        mCamera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        Log.e(TAG,"error setting up preview display"+e);
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

                if(mCamera==null)return;
                Parameters parameters=mCamera.getParameters();
                Size size=getBestSupportSize(parameters.getSupportedPreviewSizes());
                parameters.setPreviewSize(size.width,size.height);
                size=getBestSupportSize(parameters.getSupportedPictureSizes());
                parameters.setPictureSize(size.width,size.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                }
                catch (Exception e)
                {
                    Log.e(TAG,"Could not start preview"+e);
                    mCamera.release();
                    mCamera=null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                if(mCamera!=null)
                    mCamera.stopPreview();
            }
        });
        return  v;
    }

    private Size getBestSupportSize(List<Size> sizes)
    {
        Size bestSize=sizes.get(0);
        int largestArea=bestSize.width*bestSize.height;
        for(Size s:sizes)
        {
            int area=s.height*s.width;
            if(area>largestArea)
            {
                bestSize=s;
                largestArea=area;
            }
        }
        return bestSize;
    }
}
