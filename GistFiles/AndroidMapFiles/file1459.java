package utils;

import android.util.Log;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * <p/>
 * Date: 14-2-3
 * Author: Administrator
 */
public class ObjectUtils {
    private static Map<String, Method> methodCache = new HashMap<String, Method>();

    public static Object getFieldValue(Object model, String field) {
        Object res = null;
        try {
            Class modelClazz = model.getClass();
            String[] items = field.split("\\.");
            Object currentModel = model;
            Method getter;
            for (String item : items) {
                getter = getMethodByArbitraryName(modelClazz, "get" + StringUtils.capitalize(item));
                if (getter != null) {
                    currentModel = getter.invoke(currentModel, null);
                } else {
                    res = null;
                    break;
                }
            }
            res = currentModel;
        } catch (Exception e) {
            Log.e(ObjectUtils.class.getName(), "error", e);
        }
        return res;
    }

    public static boolean setFieldValue(Object model, String field, Object fieldValue) {
        boolean res = false;
        try {
            Class modelClazz = model.getClass();
            String[] items = field.split("\\.");
            Object currentModel = model;
            Method getter;
            for (int i = 0; i < items.length; i++) {
                String item = items[i];
                if (i < items.length - 1) {
                    getter = getMethodByArbitraryName(modelClazz, "get" + StringUtils.capitalize(item));
                    if (getter != null) {
                        currentModel = getter.invoke(currentModel, null);
                    } else {
                        break;
                    }
                } else {
                    Method setter = getMethodByArbitraryName(modelClazz, "set" + StringUtils.capitalize(item));
                    if (setter != null) {
                        setter.invoke(currentModel, fieldValue);
                        res = true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(ObjectUtils.class.getName(), "error", e);
        }
        return res;
    }

    public static Class getCanonicalClazz(Class clazz) {
        Class _clazz = clazz;
        if (clazz.getName().indexOf("$") != -1) {
            _clazz = clazz.getSuperclass();
        }
        if (_clazz.getName().indexOf("$") != -1) {
            _clazz = _clazz.getSuperclass();
        }
        return _clazz;
    }

    public static Method getMethodByArbitraryName(Class objClazz, String methodName) {
        Class _objClazz = getCanonicalClazz(objClazz);
        String key = _objClazz.getName() + "." + methodName;
        Method method = methodCache.get(key);
        if (!methodCache.containsKey(key)) {
            Method[] methods = _objClazz.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(methodName)) {
                    method = methods[i];
                    break;
                }
            }
            methodCache.put(key, method);
        }
        return method;
    }
}
