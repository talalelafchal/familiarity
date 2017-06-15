package com.sogou.game.dp.adminweb.config;

import net.erdfelt.android.apk.AndroidApk;
import net.erdfelt.android.apk.io.IO;
import net.erdfelt.android.apk.xml.Attribute;
import net.erdfelt.android.apk.xml.BinaryXmlListener;
import net.erdfelt.android.apk.xml.BinaryXmlParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * User: zhaoyao
 * Date: 7/24/14
 * Time: 13:55
 */
public class Test {

    public static void main(String[] args) throws IOException {

        ZipFile zip = new ZipFile("/Users/zhaoyao/Downloads/02_wan_15.apk");
        ZipEntry manifestEntry = zip.getEntry("AndroidManifest.xml");
        if (manifestEntry == null) {
            throw new FileNotFoundException("Cannot find AndroidManifest.xml in apk");
        }

        InputStream in = null;
        try {
            in = zip.getInputStream(manifestEntry);
            parseStream(in);
        } finally {
            IO.close(in);
            try {
                if(zip != null) {
                    zip.close();
                }
            } catch(IOException ignore) {
                /* ignore */
            }
        }


    }

    private static void parseStream(InputStream in) throws IOException {
        final BinaryXmlParser parser = new BinaryXmlParser();
        final Map<String, Object> map = new HashMap<>();

        parser.addListener(new BinaryXmlListener() {
            @Override
            public void onXmlEntry(String path, String name, Attribute... attrs) {
               if (!map.containsKey(path)) {
                    map.put(path, new ArrayList<>());
                }

                for (Attribute attr : attrs) {
                    ((List)map.get(path)).add(attr);
                }

            }
        });
        parser.parse(in);

        System.out.println(map);
    }


}
