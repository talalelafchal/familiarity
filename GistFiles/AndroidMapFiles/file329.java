/*
 * Copyright (C) 2015 Aron List
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by Aron List on 9-4-2015.
 * Based largely on the PersistentCookieStore by Lukas Zorich (lezorich) https://gist.github.com/lezorich/8f3f3a54f07515881581
 */
public class PersistentCookieStore implements CookieStore
{
    private CookieStore mStore;
    private Context mContext;
    private Gson mGson;
    private const String prefsFileName = "cookieStore";
    SharedPreferences prefs;

    public PersistentCookieStore(Context context)
    {
        mContext = context.getApplicationContext();
        mGson = new Gson();
        mStore = new CookieManager().getCookieStore();
        prefs = context.getSharedPreferences(prefsFileName, Context.MODE_PRIVATE);
        
        //get all cookies in the cookieStore preference file and add them to the cookieStore
        Map<String, ?> cookies = prefs.getAll();
        for (Map.Entry<String, ?> entry : cookies.entrySet())
        {
            HttpCookie cookie = mGson.fromJson(entry.getValue().toString(), HttpCookie.class);
            mStore.add(URI.create(cookie.getDomain()), cookie);
        }
    }

    /**
     * Adds a cookie to the store and also stores it in the prefs file
     * Uses the uri and name separated with a pipe as identifier this ahould be unique.
     * if a cookie with the same name and uri is recieved it is overwritten.
     */
    @Override
    public void add(URI uri, HttpCookie cookie)
    {
        prefs.edit().putString(cookie.getDomain() + "|" + cookie.getName(),mGson.toJson(cookie)).commit();
        mStore.add(URI.create(cookie.getDomain()), cookie);
    }

    @Override
    public List<HttpCookie> get(URI uri)
    {
        return mStore.get(uri);
    }

    @Override
    public List<HttpCookie> getCookies()
    {
        return mStore.getCookies();
    }

    @Override
    public List<URI> getURIs()
    {
        return mStore.getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie)
    {
        prefs.edit().remove(cookie.getDomain() + "|" + cookie.getName()).commit();
        return mStore.remove(uri, cookie);
    }

    @Override
    public boolean removeAll()
    {
        prefs.edit().clear().commit();
        return mStore.removeAll();
    }
}