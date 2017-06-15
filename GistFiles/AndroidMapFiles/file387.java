package com.stonete.qrtoken.utils;


import com.stonete.qrtoken.statics.StaticUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HttpUtil {


    /**
     * @param url       发送请求的URL
     * @param rawParams 请求参数
     * @return 服务器响应字符串
     * @throws Exception
     */
    private static HttpResponse postRequest(final String url, final Map<String, String> rawParams) {


        HttpClient httpClient = new DefaultHttpClient();
        String result = null;
        if(url == null){
            throw new RuntimeException("url == null");
        }
        try {
            HttpPost post = new HttpPost(url);
            // 如果传递参数个数比较多的话可以对传递的参数进行封装
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (String key : rawParams.keySet()) {
                // 封装请求参数
                params.add(new BasicNameValuePair(key, rawParams.get(key)));
            }
            // 设置请求参数
            post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            // 设置请求头
            post.setHeader("content-type", "application/x-www-form-urlencoded; charset=utf-8");
            // 请求超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            // 读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
            // 发送POST请求
            HttpResponse httpResponse = httpClient.execute(post);
            // 如果服务器成功地返回响应

            return httpResponse;

        } catch (Exception e) {

        }
        return null;
    }

    public static HttpUtilJsonResponse doHttpPostAndJsonValidCheck(final String url, final Map<String, String> rawParams) {

        if(rawParams != null){
            rawParams.put("app_pf", "1");
        }


        HttpUtilJsonResponse result = new HttpUtilJsonResponse();

        result.requestAndResponseBuffer.append("HTTP===START=======================\n");
        result.requestAndResponseBuffer.append("HTTP=url=>" + url+"\n");
        result.requestAndResponseBuffer.append("HTTP=REQUEST=>" + rawParams + "\n");

        result.url = url;
        if(MyLog.DEBUG){
            result.e = requestParamCheck(url, rawParams);
        }


        //验证request 参数是否正确
        if (result.isError()) {
            //request 参数验证失败
            return result;
        } else {
            //request 参数验证成功，进行http请求
            HttpResponse httpResponse = postRequest(url, rawParams);

            if (httpResponse == null) {
                //请求数据失败
                result.e = new IError();
                result.e.errorMsg = "网络异常，请重试";

            } else if (httpResponse.getStatusLine().getStatusCode() != 200) {
                // 获取服务器响应字符串
                result.e = new IError();
                try {
                    result.e.errorMsg = "网络异常，请重试。 error:" + httpResponse.getStatusLine().getStatusCode();
                    result.e.errorCode = httpResponse.getStatusLine().getStatusCode();
                    if(MyLog.DEBUG){
                        String lines = EntityUtils.toString(httpResponse.getEntity());
                        result.e.errorMsg = result.e.errorMsg + lines;
                        MyLog.e(lines);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                //请求数据成功
                String scanResultStr = null;
                try {
                    scanResultStr = EntityUtils.toString(httpResponse.getEntity());
                    result.requestAndResponseBuffer.append("HTTP=RESPONSE=>" + scanResultStr+"\n");
                    result.requestAndResponseBuffer.append("HTTP_END--------------------------\n");
                    JSONObject resultJson = new JSONObject(scanResultStr);
                    result.jo = resultJson;
                    IError responseParamCheck = responseParamCheck(url, rawParams, resultJson);
                    result.e = responseParamCheck;
                    if (!resultJson.isNull("ret")) {
                        result.ret = resultJson.getString("ret");
                    }
                } catch (JSONException e) {
                    result.e = new IError();
                    result.e.errorMsg = "HTTP ERROR Response is not json error==>" + url + "\nparams==>" + rawParams + "\nresponse==>" + scanResultStr;
                } catch (Exception e) {
                    result.e = new IError();
                    result.e.errorMsg = "HTTP ERROR Response unKnow error==>" + url + "\nparams==>" + rawParams + "\nresponse==>" + scanResultStr + e.getLocalizedMessage();
                    e.printStackTrace();
                }
            }
        }

        if(result.isError()){
            result.requestAndResponseBuffer.append(result.e.errorMsg);
        }
        MyLog.i(result.getRequestAndResponse());
        return result;
    }

    public static IError requestParamCheck(String url, final Map<String, String> requestParams) {
        //判断请求参数是否正确，如果正确，返回null
        if(url == null){
            IError e = new IError();
            e.errorMsg = "HTTP REQUEST ERROR==>您请求了一个空的url";
            return e;
        }
        for (int i = 0; i < StaticUtil.url_param_map.length; i = i + 3)
            if (StaticUtil.url_param_map[i].equals(url)) {
                String[] requestChecks = StaticUtil.url_param_map[i + 1].split(",");
                trimAndSortStringArray(requestChecks);

                Object[] requestKeys = requestParams.keySet().toArray();
                Arrays.sort(requestKeys);

                if (!Arrays.equals(requestChecks, requestKeys)) {
                    IError e = new IError();
                    e.errorMsg = "HTTP REQUEST ERROR==>" + url + " requestKeys: " + Arrays.toString(requestKeys) + " need 2 check " + Arrays.toString(requestChecks);
                    return e;
                }
                if(MyLog.DEBUG){
                    for(String key : requestChecks){
                        if(requestParams.get(key) == null){
                            IError e = new IError();
                            e.errorMsg = "HTTP REQUEST ERROR==>" + url + " request: " + requestParams + " key == null " + key;
                            return e;
                        }
                    }
                }

            }
        return null;
    }


    private static void trimAndSortStringArray(String[] strs){
        for(int j = 0; j<strs.length; j++){
            strs[j] = strs[j].trim();
        }
        Arrays.sort(strs);
    }
    public static IError responseParamCheck(String url, final Map<String, String> rawParams, JSONObject resultJson) {
        //判断返回的参数是否正确，如果正确，返回null
        for (int i = 0; i < StaticUtil.url_param_map.length; i = i + 3) {
            if (StaticUtil.url_param_map[i].equals(url)) {
                String[] requestChecks = StaticUtil.url_param_map[i + 2].split(",");
                trimAndSortStringArray(requestChecks);

                IError jsonError = JsonUtils.isJsonValid(resultJson, requestChecks);
                if (jsonError != null) {
                    jsonError.errorMsg = "HTTP RESPONSE ERROR==>" + url + " param=>" + rawParams + " " + resultJson + "  has no " + jsonError.errorMsg;
                    return jsonError;
                }
                break;
            }
        }
        return null;
    }

    public static byte[] doHttpGet(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        if(url == null){
            throw new RuntimeException("url == null");
        }
        try {
            HttpGet getRequest = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(getRequest);
            return EntityUtils.toByteArray(httpResponse.getEntity());

        } catch (Exception e) {

        }
        return null;
    }

    public static class HttpUtilJsonResponse {
        public String url = null;
        public JSONObject jo = null;
        public IError e = null;
        public String ret = null;
        private StringBuffer requestAndResponseBuffer;
        public HttpUtilJsonResponse() {
            requestAndResponseBuffer = new StringBuffer();
        }

        public boolean isError() {
            return e != null;
        }

        public boolean checkRetSuccess() {

            if (!isError() && !jo.isNull("ret")) {
                try {
                    return jo.getString("ret").equals("0");
                } catch (JSONException e1) {
                }
            }
            return false;
        }

        public String getSuccessDialogMsg() {
            if (!isError() && checkRetSuccess()) {
                return QrtUtils.getSuccessMsg(url);
            }

            return "无法获取成功信息==" + this.jo.toString();
        }


        public String getErrorDialogMsg(String errorCode) {
            if (!isError() && !checkRetSuccess()) {
                return QrtUtils.getErrorMsg(errorCode).get("message");
            }
            return "无法获取错误信息==" + this.jo.toString();
        }


        public String getDialogMsg() {

            String dialogMsg = null;
            if (checkRetSuccess()) {
                    dialogMsg = getSuccessDialogMsg();
            } else {
                dialogMsg = getErrorDialogMsg(getRet());
            }
            return dialogMsg;
        }

        public String getRet() {
            if (!isError() && !jo.isNull("ret")) {
                try {
                    return jo.getString("ret");
                } catch (JSONException e1) {
                }
            }
            return null;
        }

        public String getRequestAndResponse(){
            return requestAndResponseBuffer.toString();
        }
    }
}
