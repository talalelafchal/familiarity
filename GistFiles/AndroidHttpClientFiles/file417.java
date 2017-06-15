package com.zenoffice.app.services;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

public class HttpOperation {

    public HttpClient           client          = HttpClientBuilder.create().build();

    private String              cookies;
    private List<NameValuePair> paramList;
    private String              url;

    private final String        USER_AGENT      = "Mozilla/5.0; ZenOffice/1.0";
    private final String        CONTENT_TYPE    = "application/x-www-form-urlencoded; charset=UTF-8";
    private final String        ACCEPT          = "text/json,application/json;q=0.9,*/*;q=0.8";
    private final String        CONNECTION      = "keep-alive";
    private final String        ACCEPT_LANGUAGE = "en-US,en;q=0.5";

    public HttpOperation(String url) {

        this.url        = url;
        this.paramList  = new ArrayList<NameValuePair>();

        CookieHandler.setDefault(new CookieManager());

    }

    public void addParam(String key, String value) {

        this.paramList.add(
            new BasicNameValuePair(key, value)
        );

    }

    public void head(iApiCallback clb) {

        HttpHead req = new HttpHead(this.url);

        // Устанавливаем заголовки
        this.initHeadersFor(req);

        // Выполняем задачу
        new BaseTask(this, req, clb).execute();

    }

    public void get(iApiCallback clb) {

        HttpGet req = new HttpGet(this.url);

        // Устанавливаем заголовки
        this.initHeadersFor(req);

        // Выполняем задачу
        new BaseTask(this, req, clb).execute();

    }

    public void post(iApiCallback clb) {

        HttpPost req = new HttpPost(this.url);

        // Устанавливаем заголовки
        this.initHeadersFor(req);

        // Установка параметров
        try {
            req.setEntity(
                new UrlEncodedFormEntity(this.paramList)
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Выполняем задачу
        new BaseTask(this, req, clb).execute();

    }

    public void put(iApiCallback clb) {

        HttpPut req = new HttpPut(this.url);

        // Устанавливаем заголовки
        this.initHeadersFor(req);

        // Установка параметров
        try {
            req.setEntity(
                new UrlEncodedFormEntity(this.paramList)
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Выполняем задачу
        new BaseTask(this, req, clb).execute();

    }

    public void delete(iApiCallback clb) {

        HttpDelete req = new HttpDelete(this.url);

        // Устанавливаем заголовки
        this.initHeadersFor(req);

        // Выполняем задачу
        new BaseTask(this, req, clb).execute();

    }

    public String getCookies() {
        return this.cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    private void initHeadersFor(HttpRequestBase request) {

        request.setHeader("User-Agent",         this.USER_AGENT);
        request.setHeader("Content-Type",       this.CONTENT_TYPE);
        request.setHeader("Accept",             this.ACCEPT);
        request.setHeader("Accept-Language",    this.ACCEPT_LANGUAGE);
        request.setHeader("Cookie",             this.getCookies());
        request.setHeader("Connection",         this.CONNECTION);

    }


    private class BaseTask extends AsyncTask<String, String, String> {

        private iApiCallback    clb;
        private HttpOperation   op;
        private HttpRequestBase request;
        private int             statusCode;
        private Boolean         success = false;
        private String          error   = "";

        public BaseTask(HttpOperation op, HttpRequestBase request, iApiCallback clb) {

            this.op         = op;
            this.clb        = clb;
            this.request    = request;

        }

        @Override
        protected String doInBackground(String... params) {

            // Выполняем запрос
            HttpResponse response = null;
            try {
                response = this.op.client.execute(this.request);
            } catch (IOException e) {
                this.error    = e.toString();
                this.success  = false;
                return "";
            }

            // код ответа севрера
            this.statusCode = response.getStatusLine().getStatusCode();

            // Получаем содержимое ответа сервера
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent())
                );
            } catch (IOException e) {
                this.error    = e.toString();
                this.success  = false;
                return "";
            }

            StringBuffer    result = new StringBuffer();
            String          line = "";

            assert rd != null;
            try {
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            } catch (IOException e) {
                this.error    = e.toString();
                this.success  = false;
                return "";
            }

            // Обновляем куки,полученные с сервера
            this.op.setCookies(
               response.getFirstHeader("Set-Cookie") == null ? "" :
               response.getFirstHeader("Set-Cookie").toString()
            );

            this.success = true;
            this.error   = "";
            return result.toString();

        }

        @Override
        protected void onPostExecute(String result) {

            JSONObject data = null;
            try {
                data = new JSONObject(result);
            } catch (JSONException e) {
                data = new JSONObject();
            }

            this.clb.onDataReceived(this.success, data, this.statusCode, this.error);
            super.onPostExecute(result);

        }

    } // BaseTask
}

//
// Usage example
//
// HttpOperation op = new HttpOperation(HOST + "/users/send_token.js");
// op.addParam("phone", "1234567");
// op.post(callback);
//