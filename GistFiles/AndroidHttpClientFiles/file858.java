package com.topchart;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.w3c.dom.Element;

import android.util.Log;

/*
 * USAGE
 * ------
 * static final String URL = "your url";
 * // XML node keys
 * static final String KEY_ITEM = "item"; // parent node
 * static final String KEY_NAME = "name";
 * XMLParser parser = new XMLParser();
 * String xml = parser.getXmlFromUrl(URL); // getting XML
 * Document doc = parser.getDomElement(xml); // getting DOM element
 * NodeList nl = doc.getElementsByTagName(KEY_ITEM);
 * 
 *  // looping through all item nodes <item>
 *  
 * for (int i = 0; i < nl.getLength(); i++) {
 *     String name = parser.getValue(e, KEY_NAME); // name child value
 *     
 * }
 * */

public class XMLParser {
  
	
	//Getting XML content by making HTTP Request
	public String getXmlFromUrl(String url) {
        String xml = null;
 
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
 
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return XML
        return xml;
    }
	
	
	//Parsing XML content and getting DOM element
	public Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
 
            DocumentBuilder db = dbf.newDocumentBuilder();
 
            InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                doc = db.parse(is); 
 
            } catch (ParserConfigurationException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }
                // return DOM
            return doc;
    }
	
	
	//Get each xml child element value by passing element node name
	public String getValue(Element item, String str) {
	    NodeList nodeList = item.getElementsByTagName(str);
	    return this.getElementValue(nodeList.item(0));
	}
	
	public final String getElementValue( Node element ) {
	         Node child;
	         if( element != null){
	             if (element.hasChildNodes()){
	                 for( child = element.getFirstChild(); child != null; child = child.getNextSibling() ){
	                     if( child.getNodeType() == Node.TEXT_NODE  ){
	                         return child.getNodeValue();
	                     }
	                 }
	             }
	         }
	         return "";
	  } 

}
