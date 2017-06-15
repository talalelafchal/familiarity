// DIESER CODE HABE ICH NIE KOMPILIERT!!
// ZUERST HIRN EINSCHALTEN BEVOR VERWENDUNG DES CODES!!

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
 
public class BluetoothDeviceHookingClass implements InvocationHandler {
 
    public BluetoothHandlingClassFromAndroid realObject;
 
    public BluetoothDeviceHookingClass(BluetoothHandlingClassFromAndroid obj) {
        this.realObject = obj;
    }

    public static BluetoothHandlingClassFromAndroid createAndroidBluetooth(Object[] args) {
         BluetoothHandlingClassFromAndroid realObject = new BluetoothHandlingClassFromAndroid(args); // Je nach dem was der für Parameter beim Konstruktor braucht, weisst nicht ob das so geht mit einem Array of Objects?
         Handler handler = new BluetoothDeviceHookingClass(realObject); // Handled ja unsere Aufrufe als Proxy
         Class[] interfacesArray = new Class[] {BluetoothHandlingClassFromAndroid__INTERFACE__.class}; // Geht nur mit öffentlichen Methoden, logisch
 
         return (BluetoothHandlingClassFromAndroid) Proxy.newProxyInstance(BluetoothHandlingClassFromAndroid.class.getClassLoader(), interfacesArray, handler); // Unser BluetoothHandlingClassFromAndroid Objekt, aber …
    }

    // … alle Aufrufe gehen zuerst hier durch:
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            // toString wolltest du ja abfangen? Here you go!
            if (method.getName() == "toString") {
                return realObject.toString() + realObject.getID() + "Blabla";
            }
            // Für alles andere, einfach weiterleiten, hier gibt's nichts zu sehen!
            return method.invoke(realObject, args);
        } catch ( Exception e ) {
            return new Integer(0);
        }
    }
}