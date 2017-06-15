package com.example;
 
public class XMLParser {

    Context context;
 
    public XMLParser(Context c) {
        context = c;
    }
 
    public String getXmlFromUrl(String url) {
        String xml = "";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

 
        try {
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                xml = EntityUtils.toString(httpEntity);
 
            } catch (UnsupportedEncodingException e) {
               
            } catch (ClientProtocolException e) {
              
            } catch (IOException e) {
               
            } catch (Exception e) {
               
            }
        } catch (Exception e) {
          
        }


        return xml;
    }
 
    public Document getDomElement(String xml) {


        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {

            return null;
        } catch (SAXException e) {

            return null;
        } catch (IOException e) {

            return null;
        } catch (Exception e) {

            return null;
        }

        return doc;
    }

     
    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                        return child.getTextContent();
                    }
                }
            } else {
                return elem.getTextContent();
            }
        }
        return "";
    }
 
    public String getValue(Element item, String str) {
        Node n = item.getElementsByTagName(str).item(0);

        return n.getTextContent().toString();
    }
 

}
