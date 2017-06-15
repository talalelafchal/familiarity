package com.danilov.supermanga.core.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.danilov.supermanga.core.http.ExtendedHttpClient;
import com.danilov.supermanga.core.util.IoUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Semyon on 19.12.2015.
 */
public abstract class CloudFlareBypassEngine implements RepositoryEngine {

    private CookieStore cookieStore = null;

    public Map<String, String> getCFCookies() {
        return null;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    private final static Pattern OPERATION_PATTERN = Pattern.compile("setTimeout\\(function\\(\\)\\{\\s+(var t,r,a,f.+?\\r?\\n[\\s\\S]+?a\\.value =.+?)\\r?\\n");
    private final static Pattern PASS_PATTERN = Pattern.compile("name=\"pass\" value=\"(.+?)\"");
    private final static Pattern CHALLENGE_PATTERN = Pattern.compile("name=\"jschl_vc\" value=\"(\\w+)\"");

    @Nullable
    private HttpResponse parseCFResponse(final HttpResponse response, final DefaultHttpClient httpClient, final HttpContext context, final String uri) throws IOException {
        byte[] result = IoUtils.convertStreamToBytes(response.getEntity().getContent());
        String responseString = IoUtils.convertBytesToString(result);


        // инициализируем Rhino
        Context rhino = Context.enter();
        try {
            String domain = getDomain();

            // CF ожидает ответа после некоторой задержки
            Thread.sleep(5000);

            // вытаскиваем арифметику
            Matcher operationSearch = OPERATION_PATTERN.matcher(responseString);
            Matcher challengeSearch = CHALLENGE_PATTERN.matcher(responseString);
            Matcher passSearch = PASS_PATTERN.matcher(responseString);
            if(!operationSearch.find() || !passSearch.find() || !challengeSearch.find()) {
                return null;
            }

            String rawOperation = operationSearch.group(1); // операция
            String challengePass = passSearch.group(1); // ключ
            String challenge = challengeSearch.group(1); // хэш

            // вырезаем присвоение переменной
            String operation = rawOperation
                    .replaceAll("a\\.value =(.+?) \\+ .+?;", "$1")
                    .replaceAll("\\s{3,}[a-z](?: = |\\.).+", "");
            String js = operation.replace("\n", "");

            rhino.setOptimizationLevel(-1); // без этой строки rhino не запустится под Android
            Scriptable scope = rhino.initStandardObjects(); // инициализируем пространство исполнения

            // either do or die trying
            int res = ((Double) rhino.evaluateString(scope, js, "CloudFlare JS Challenge", 1, null)).intValue();
            String answer = String.valueOf(res + domain.length()); // ответ на javascript challenge

            final List<NameValuePair> params = new ArrayList<>(3);
            params.add(new BasicNameValuePair("jschl_vc", challenge));
            params.add(new BasicNameValuePair("pass", challengePass));
            params.add(new BasicNameValuePair("jschl_answer", answer));

            HashMap<String, String> headers = new HashMap<>(1);

            String url = "http://" + domain + "/cdn-cgi/l/chk_jschl?" + URLEncodedUtils.format(params, "windows-1251");

            HttpGet httpGet = new HttpGet();
            httpGet.setURI(URI.create(url));
            httpGet.setHeader("Referer", "http://" + domain + "/"); // url страницы, с которой было произведено перенаправление

            HttpResponse httpResponse = context != null ? httpClient.execute(httpGet, context) : httpClient.execute(httpGet);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // в ответе придёт страница, указанная в Referer
                cookieStore = httpClient.getCookieStore();
                return httpResponse;
            }
        } catch (Exception e) {
            return null;
        } finally {
            Context.exit(); // выключаем Rhino
        }
        return null;
    }

    public void emptyRequest() throws IOException {
        loadPage(new HttpGet(getEmptyRequestURL()));
    }

    private Lock lock = new ReentrantLock();

    public HttpResponse loadPage(final HttpUriRequest request, final HttpContext context) throws IOException {
        DefaultHttpClient httpClient = new ExtendedHttpClient();
        boolean lockLocked = false;
        try {
            if (cookieStore == null) {
                lock.lock();
                lockLocked = true;
            }
            if (cookieStore != null) {
                httpClient.setCookieStore(cookieStore);
            }
            HttpResponse response = context != null ? httpClient.execute(request, context) : httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() >= 400) {
                response = parseCFResponse(response, httpClient, context, request.getURI().toString());
                return response;
            }
            return response;
        } finally {
            if (lockLocked) {
                lock.unlock();
            }
        }
    }

    public HttpResponse loadPage(final HttpUriRequest request) throws IOException {
        return loadPage(request, null);
    }

    @NonNull
    public abstract String getDomain();

    /**
     * Пустой запрос, чтобы получить куки CloudFlare
     * @return url
     */
    @NonNull
    public abstract String getEmptyRequestURL();

}