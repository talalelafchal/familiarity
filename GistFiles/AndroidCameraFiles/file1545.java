<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <LinearLayout
        android:id="@+id/card_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="5dp"
        android:orientation="vertical">


        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal">

                <TextView
                    android:id="@+id/txt_topic"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center_vertical"
                    android:layout_marginLeft="10dp"
                    android:text="Medium Text"
                    android:textAppearance="?android:attr/textAppearanceMedium" />

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical">

                    <TextView
                        android:id="@+id/textView4"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_marginRight="10dp"
                        android:text="3.00 pm"
                        android:textSize="12dp"
                        android:layout_gravity="right" />

                </LinearLayout>


            </LinearLayout>

            <TextView
                android:id="@+id/txt_detail"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center_vertical"
                android:layout_marginLeft="10dp"
                android:text="สวัสดี ทุกๆวัน"
                android:textSize="12dp" />


        </LinearLayout>


    </LinearLayout>

</LinearLayout>
