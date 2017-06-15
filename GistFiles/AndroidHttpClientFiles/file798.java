package library;

/**
 * Created by Artyom on 6/8/13.
 */

import android.content.Context;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
/**
 * JSON кэш
 * @author Арт
 *
 */
public class JSONCache {
    /**
     * Префикс для файлов кэша.
     */
    static String cacheBasePath = "api_cache_";
    /**
     * Контекст песочницы.
     */
    static Context context = null;
    /**
     * Возраст кэша. По умолчанию - 5 минут.
     */
    public static int cacheAge = 1000 * 60 * 5;

    /**
     * Передача контекста
     * @param ctxt
     */
    public static void setContext(Context ctxt) {
        if (context == null)
            context = ctxt;
    }
    /**
     * Получение пути кэша внутри песочницы.
     * @param key ключ
     * @return
     */
    static String getCachePath(String key) {
        return cacheBasePath + Math.abs(key.hashCode());

    }
    /**
     * Добавление кэша. Если кэш уже был, он перезапишется.
     * @param key ключ, по которому можно будет получить кэш
     * @param value значение кэша. JSON объект.
     */
    static void putCache(String key, JSONObject value) {
        FileOutputStream fout;
        try {
            fout = context.openFileOutput(getCachePath(key),
                    Context.MODE_PRIVATE);
            PrintWriter pout = new PrintWriter(fout);
            pout.println(new Date().getTime());
            pout.println(value.toString());
            pout.close();
            fout.flush();
            fout.close();

            Log.d("json", "Записано в кэш " + key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Получение кэша в виоде json объекта. Если кэша нет, вернется null.
     * @param key ключ, по которому кэш был добавлен.
     * @return
     */
    static JSONObject getCache(String key){
        return getCache(key,false);
    }
    /**
     * Получение кэша в виоде json объекта. Если кэша нет, вернется null. При указании параметра ignoreCacheAge=true, возраст кэша будет проигнорирован, и если какие-то данные есть, они будут выданы.
     * @param key ключ, по которому кэш был добавлен
     * @param ignoreCacheAge игнорирование возраста кэша. Если true, но кэш будет отдан, если он существует, независимо от его возраста.
     * @return
     */
    static JSONObject getCache(String key,boolean ignoreCacheAge) {
        JSONObject jsonCacheObject=null;
        try {
            String cachePath=getCachePath(key);
            File file = context.getFileStreamPath(cachePath);
            if(!file.exists())
                return null;

            FileInputStream fin = context.openFileInput(cachePath);

            BufferedReader bin = new BufferedReader(new InputStreamReader(fin));
            Date cacheDate = new Date(Long.parseLong(bin.readLine()));
            Long currentCacheAge = new Date().getTime() - cacheDate.getTime();
            if (!ignoreCacheAge && currentCacheAge > cacheAge) {
                return null;
            }
            StringBuilder responseBuilder = new StringBuilder();
            while (bin.ready()) {
                responseBuilder.append(bin.readLine()).append("\n");
            }
            jsonCacheObject = new JSONObject(responseBuilder.toString());

            Log.d("json", "Взято из кэша " + key);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonCacheObject;
    }

}
