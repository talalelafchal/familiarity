-----------------------
BluetoothChat.java

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }
------------------------

-----------------------
option_menu.xml

<?xml version="1.0" encoding="utf-8"?>

<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:id="@+id/secure_connect_scan"
        android:icon="@android:drawable/ic_menu_search"
        android:title="@string/secure_connect"
        android:showAsAction="ifRoom|withText" />
    <item android:id="@+id/insecure_connect_scan"
        android:icon="@android:drawable/ic_menu_search"
        android:title="@string/insecure_connect"
        android:showAsAction="ifRoom|withText" />
    <item android:id="@+id/discoverable"
        android:icon="@android:drawable/ic_menu_mylocation"
        android:title="@string/discoverable"
        android:showAsAction="ifRoom|withText" />
</menu>
--------------------------

-----------------------
compile errors:

Information:Gradle tasks [:bcr:assembleDebug]
Information:2 errors
Information:0 warnings
Information:See complete output in console
Error:The prefix "xliff" for element "xliff:g" is not bound.
Error:Execution failed for task ':bcr:mergeDebugResources'.
> /run/media/Technician/Expansion/Programming/Android_01/Tutorials/Code/BluetoothChat_Redux/bcr/src/main/res/values/strings.xml:10:77: Error: The prefix "xliff" for element "xliff:g" is not bound.
-----------------------
