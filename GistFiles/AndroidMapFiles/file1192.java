import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyCharacterMap;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

        Method getIntentForShortcut = null;
        Method add = null;
        Class<?>[] subclasses = Settings.class.getDeclaredClasses();
        for (Class<?> clazz: subclasses) {
        	if (clazz.getName().equals("android.provider.Settings$Bookmarks")) {
        		Method[] methods = clazz.getDeclaredMethods();
        		for (Method method: methods) {
        			if (method.getName().equals("getIntentForShortcut")) {
        				getIntentForShortcut = method;
        			} else if (method.getName().equals("add")) {
        				add = method;
        			}
        		}
        	}
        }
        KeyCharacterMap map = KeyCharacterMap.load(KeyCharacterMap.BUILT_IN_KEYBOARD);
        char ch = map.getDisplayLabel(117);
        
        try {
        	Intent intent = new Intent("android.intent.action.MAIN");
            intent.setComponent(ComponentName.unflattenFromString("some.package/some.package.SomeActivity"));
            intent.addCategory("android.intent.category.LAUNCHER");
        	Object result = add.invoke(null, getContentResolver(), intent, null, null, ch, 0);
        	result.toString();
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        
        try {
			Object result = getIntentForShortcut.invoke(null, getContentResolver(), ch);
			result.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}