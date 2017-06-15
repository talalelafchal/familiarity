package com.wh.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Created by WH-2013 on 2017/4/1.
 */
public class DbHttpClient {
    private static PoolingHttpClientConnectionManager connMr=null;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT=8000;

    static {

        RequestConfig.Builder builder = RequestConfig.custom();
        //连接超时
        builder.setConnectTimeout(MAX_TIMEOUT);
        //获取连接池实例超时
        builder.setConnectionRequestTimeout(MAX_TIMEOUT);
        //读取超时
        builder.setSocketTimeout(MAX_TIMEOUT);
        //连接前测试连接是否可用
        builder.setStaleConnectionCheckEnabled(true);
        requestConfig=builder.build();
    }

    public static CloseableHttpClient getClient(){
        if (connMr==null){
            connMr=new PoolingHttpClientConnectionManager();
            connMr.setMaxTotal(100);
            connMr.setDefaultMaxPerRoute(connMr.getMaxTotal());
        }
        return HttpClients.custom().setConnectionManager(connMr).build();
    }
}
