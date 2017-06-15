package com.wh.movie;

import com.wh.utils.DbHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by WH-2013 on 2017/4/1.
 */
public class Main {

    public String doGet(String url) throws IOException {
        CloseableHttpClient client = DbHttpClient.getClient();
        HttpGet get = new HttpGet(url);   
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
       return EntityUtils.toString(entity,"utf-8");
    }

    public void parse(String html) throws IOException {
//        Document document = Jsoup.connect("https://movie.douban.com/subject/25934014").get();
//        System.out.println(document.html());
        Document doc = Jsoup.parse(html);
        Element info = doc.getElementById("info");
        Elements spans = info.getElementsByTag("span");
        Element child = info.child(0);
        System.out.println(child.child(1).html());
    }

}
