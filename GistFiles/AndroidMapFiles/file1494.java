Why no Action Bar?
The reason the action bar doesn't appear is that the current version of appcompat - which is a library that provides backwards compatibility - styles our application. Appcompat only adds an action bar to activities that derive from ActionBarActivity and SettingsActivity does not subclass ActionBarActivity. SettingsActivity is a subclass of PreferenceActivity.
So why are we using PreferenceActivity? It’s an easy way to get the preference UI working on Gingerbread devices.
#######################################################################################
make a popup under the view when click
https://developer.android.com/guide/topics/ui/menus.html
How to change the text color and size of a pop up menu in android?
http://stackoverflow.com/questions/25037418/how-to-change-the-text-color-and-size-of-a-pop-up-menu-in-android
I made this menu, but it is only displaying the text of the items (not the icons). Is it possible to display both the title and icon in a PopupMenu?
http://stackoverflow.com/questions/23400732/how-to-create-a-custom-popupmenu-in-android
#######################################################################################
Menus
https://developer.android.com/guide/topics/ui/menus.html
#######################################################################I made this menu, but it is only displaying the text of the items (not the icons). Is it possible to display both the title and icon in a PopupMenu?

Setting Up the App Bar
https://developer.android.com/training/appbar/setting-up.html
#######################################################################################
TOOLBAR ACTIVITY
// hiện thực trong Activity
protected void onCreate(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);
}

// Hiện thực trong Fragment
 @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true); // http://stackoverflow.com/questions/8308695/android-options-menu-in-fragment
    }
    
    // tao menu 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action ba
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // listen check icon
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
##################################################################################################
TOOLBAR LAYOUT
<android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/map_coordinator"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true" -> nếu false: 1 góc thanh toolbar tràn ra ngoài -> xấu. true: ko cho toolbar tràn ra ngoài, phần tràn ra ngoài ko phải toolbar
    tools:context=".Activity.DirectionActivity"
    tools:showIn="@layout/activity_direction">

    <!--toolbar-->
    <android.support.design.widget.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:theme="@style/AppTheme.AppBarOverlay">

        ĐÂY LÀ VIEW GROUP CHO NÊN TRONG TOOLBAR TA ĐƯỢC QUYỀN TẠO CÁC GROUP CON
        <android.support.v7.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:minHeight="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:popupTheme="@style/AppTheme.PopupOverlay"
            local:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar">
        </android.support.v7.widget.Toolbar>
    </android.support.design.widget.AppBarLayout>
</android.support.design.widget.CoordinatorLayout>

//     Ngoài popupTheme sd theme sáng trong android, ta có thể chỉ theme bằng màu sắc như sau:
https://github.com/PhongHuynh93/TestDesignAndroid/blob/8172ac3d333a2d1f903ed3b7422e5b1206ac4547/app/src/main/res/layout/include_toolbar.xml

##################################################################################################
TOOLBAR LAYOUT
Toolbar inside Appbar: https://github.com/PhongHuynh93/MediaPlayerTest/commit/95bf7ef93d52cee31862f997ed71b44a89d5a16b
Toolbar with text inside: https://github.com/PhongHuynh93/MaterialTemplate2/blob/f51ce96ce04a406abdd8c98614266f6933b6bf73/app/src/main/res/layout/toolbar.xml

##################################################################################################
MENU TRÊN TOOLBAR
menu with search icon: https://github.com/PhongHuynh93/MaterialTemplate2/blob/f51ce96ce04a406abdd8c98614266f6933b6bf73/app/src/main/res/menu/main.xml
##################################################################################################
LIBRARY
A morphing toolbar that can expand/collapse at anytime
https://github.com/badoualy/morphy-toolbar?utm_source=Android+Weekly&utm_campaign=976701a07c-Android_Weekly_191&utm_medium=email&utm_term=0_4eb677ad19-976701a07c-338009597

##################################################################################################
NO ACTION, SO WE CAN ADD TOOLBAR
project no actionbar: https://github.com/PhongHuynh93/MaterialTemplate2
style no action bar: https://github.com/PhongHuynh93/MaterialTemplate2/blob/f51ce96ce04a406abdd8c98614266f6933b6bf73/app/src/main/res/values/styles.xml

##################################################################################################
How to style PopupMenu?
http://stackoverflow.com/questions/12636101/how-to-style-popupmenu
##################################################################################################
##################################################################################################
##################################################################################################
##################################################################################################



