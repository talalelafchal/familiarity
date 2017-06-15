package com.stonete.qrtoken.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;


public class XmlUtils {
    /**
     * 生成xml
     * createXml(HashMap<String,String>, map) returnType xmlString
     */
    public static String createXml(HashMap<String, String> map) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("gbk", null);
            // <result>
            serializer.startTag("", "result");// 第一个参数是命名空间，第二个是标签名
            // <status>0</status><#!– 返回状态 0成功, 2000失败 ,1001未绑定–>
            Set set = map.entrySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                serializer.startTag("", (String) entry.getKey());
                serializer.text((String) entry.getValue());
                serializer.endTag("", (String) entry.getKey());
            }
            // </result>
            serializer.endTag("", "result");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    /**
     * 解析xml
     * parseXml(String xml) resturnType HashMap<String,String>
     */
    public static HashMap<String, String> parseXml(String xml) {
        HashMap<String, String> map = null;
        try {
            InputStream sis = new StringBufferInputStream(xml);

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(sis, "gbk");

            // 开始解析事件
            int eventType = parser.getEventType();

            // 处理事件，不碰到文档结束就一直处理
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // 因为定义了一堆静态常量，所以这里可以用switch
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        // 不做任何操作或初开始化数据
                        map = new HashMap<String, String>();
                        break;
                    case XmlPullParser.START_TAG:
                        String tagName = parser.getName();
                        if (!"result".equals(tagName)) {
                            // 属性值为：
//                        String statue_id = parser.getAttributeName(0);
//                        map.put(statue_id, statue_id);
                            // 节点值为：
                            parser.next();
                            String value = parser.getText();
                            map.put(tagName, value);
                        }
                        break;
                    case XmlPullParser.END_TAG:

                        break;
                    case XmlPullParser.END_DOCUMENT:

                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
