public Dialog chooseImageDialog(){
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.chooseimagedialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(!RecyclerImg.IMG_LINLK.equalsIgnoreCase("")){
                    Log.wtf("IMG", RecyclerImg.IMG_LINLK);
                    URL_IMAGE =RecyclerImg.IMG_LINLK;
                    Intent i = new Intent(activity, PhotoFilterActivity.class);
                    i.putExtra("path", URL_IMAGE);
                    RecyclerImg.IMG_LINLK = "";
                    startActivity(i);
                }
            }
        });
        WindowManager manager = (WindowManager) activity.getSystemService(Activity.WINDOW_SERVICE);
        int width;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            width = manager.getDefaultDisplay().getWidth();
        } else {
            Point point = new Point();
            manager.getDefaultDisplay().getSize(point);
            width = point.x;
        }

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = width;
        dialog.getWindow().setAttributes(lp);
        ImageView imCam =  (ImageView) dialog.findViewById(R.id.imageView6);
        ImageView allery =  (ImageView) dialog.findViewById(R.id.imageView8);
        SuperRecyclerView rv =  (SuperRecyclerView) dialog.findViewById(R.id.recyclerView);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(gridLayoutManager);
        List<String> arr =  fBdialog.getResult();
        RecyclerImg adapter = new RecyclerImg(arr ,activity , dialog);
        rv.setAdapter(adapter);
        imCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseFromCamera();
                dialog.dismiss();
            }
        });
        allery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromSD();
                dialog.dismiss();

            }
        });
        return dialog;
    }
    
//style
<style name="DialogAnimation">
    <item name="android:windowEnterAnimation">@android:anim/fade_in</item>
    <item name="android:windowExitAnimation">@android:anim/fade_out</item>
</style>

//xml file
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/tools"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:orientation="vertical"
            android:background="@color/primary_color"
            android:layout_height="wrap_content">

            <com.boxopen.tailieuso.widgets.superrecyclerview.SuperRecyclerView
            android:id="@+id/recyclerView"
            android:layout_width="match_parent"
            android:layout_height="150dp"
            android:layout_alignParentTop="true"
            app:recyclerClipToPadding="false"
            app:recyclerPadding="5dp"
            android:padding="3dp"
                android:background="@color/nliveo_blue_gray_alpha_colorPrimaryDark"
            android:layout_marginTop="4dp"
            app:scrollbarStyle="insideInset"/>
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_gravity="bottom"
            android:padding="5dp"
            android:layout_height="wrap_content">


            <ImageView
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:src="@drawable/camerai"
                android:id="@+id/imageView6"
                android:layout_alignParentTop="true"
                android:layout_alignParentLeft="true"
                android:layout_alignParentStart="true"
                android:layout_marginLeft="45dp"
                android:layout_marginStart="45dp" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textAppearance="?android:attr/textAppearanceSmall"
                android:background="@color/primary_color"
                android:id="@+id/textView5"
                android:layout_centerVertical="true"
                android:layout_centerHorizontal="true" />

            <ImageView
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:id="@+id/imageView8"
                android:src="@drawable/gallery"
                android:layout_alignParentTop="true"
                android:layout_toRightOf="@+id/textView5"
                android:layout_toEndOf="@+id/textView5"
                android:layout_marginLeft="45dp"
                android:layout_marginStart="45dp" />
        </RelativeLayout>

        </LinearLayout>

</LinearLayout>

