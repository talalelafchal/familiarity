package gturedi.gist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GeneralUtil {

    public static String replaceIfEmpty(String input, String defaultValue) {
        return isNullOrEmpty(input) ? defaultValue : input;
    }

    public static boolean isNullOrEmpty(String target) {
        return target == null || target.equals("");
    }

    public static boolean isNullOrEmpty(List target) {
        return target == null || target.size() == 0;
    }

    public static boolean isNullOrEmpty(Object[] target) {
        return target == null || target.length == 0;
    }

    public static List<String> selectField(List items, String fieldName) {
        List<String> result = new ArrayList<>(items.size());
        for (Object item : items) {
            try {
                Field field = item.getClass().getField(fieldName);
                String val = field.get(item).toString();
                result.add(val);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void runAsync(Runnable runnable) {
        new Thread(runnable).start();
    }

}