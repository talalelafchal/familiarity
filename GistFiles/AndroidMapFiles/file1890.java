import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Profiler {
    private String retBuffer;

    Profiler() {
        this.retBuffer = new String();
    }

    public void profileClass(String cn) {
        try {
            Class<?> c = Class.forName(cn);
            retBuffer += "\nClass : " + c.getPackage() + "." + c.getSimpleName();
            retBuffer += "\nField(s) :\n";
            for (Field f : c.getDeclaredFields())
                retBuffer += new Modifier().toString(f.getModifiers()) + " " + f.getType() + " " + f.getName() + "\n";
            retBuffer += "\nMethod(s) :\n";
            for (Method m : c.getDeclaredMethods()) {
                retBuffer += new Modifier().toString(m.getModifiers()) + " " + m.getReturnType() + " " + m.getName() + "(";
                int i = 0;
                for (Class<?> t : m.getParameterTypes()) {
                    retBuffer += t.getName();
                    if (++i < m.getParameterTypes().length)
                        retBuffer += ", ";
                }
                retBuffer += ")\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logcatBigText(String tag) {
        longTextLogger(tag, retBuffer);
    }

    private void longTextLogger(String tag, String str) {
        if (str.length() > 4000) {
            Log.v(tag, str.substring(0, 4000));
            longTextLogger(tag, str.substring(4000));
        } else
            Log.v(tag, str);
    }
}