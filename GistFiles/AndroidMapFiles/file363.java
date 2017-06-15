package com.example;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class CheckStringWidth {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        final File resDir = new File("App/src/main/res");
        final Font font = new Font("Helvetica", Font.PLAIN, 100);
        final FontMetrics fm = new Canvas().getFontMetrics(font);
        final String[] langs = {"zh-rCN", "ru", "bn-rIN", "th", "pt-rBR", "tr", "de", "ko", "fr", "it", "ja", "ms", "in", "zh-rTW", "pt-rPT", "id", "es"};


        final Map<String, String> base = readStrings(new File(resDir, "values/strings.xml"));
        final Map<String, Map<String, String>> trans = new LinkedHashMap<>();

        for (String lang : langs) {
            final File strings = new File(resDir, "values-" + lang + "/strings.xml");
            trans.put(lang, readStrings(strings));
        }

        System.out.println("Key\t" + StringUtils.join(langs, '\t'));

        for (String key : base.keySet()) {
            final List list = new ArrayList();
            final float baseWidth = fm.stringWidth(base.get(key));
            list.add(key);

            for (Map.Entry<String, Map<String, String>> t : trans.entrySet()) {
                final float transWidth = fm.stringWidth(t.getValue().get(key));
                list.add(transWidth / baseWidth);
            }

            System.out.println(StringUtils.join(list, '\t'));
        }

    }

    static Map<String, String> readStrings(File stringRes) throws ParserConfigurationException, IOException, SAXException {
        final Map<String, String> result = new TreeMap<>();
        final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document doc = builder.parse(stringRes);
        final Node resNode = doc.getDocumentElement();
        final NodeList stringNodes = resNode.getChildNodes();

        for (int i = 0; i < stringNodes.getLength(); i++) {
            Node stringNode = stringNodes.item(i);
            if (stringNode.getNodeType() == Node.ELEMENT_NODE) {
                final String name = stringNode.getAttributes().getNamedItem("name").getTextContent();
                final String content = stringNode.getTextContent();

                result.put(name, content);
            }
        }

        return result;
    }
}
