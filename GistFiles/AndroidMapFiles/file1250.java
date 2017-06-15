public static Document readXmlDocument(InputStream is) throws 
        ParserConfigurationException, SAXException, IOException{
        if (is == null) {
            return null;
        }
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(is);
        return document;
    }
    
    public static Document readXmlDocument(String s) throws 
    ParserConfigurationException, SAXException, IOException{
        return readXmlDocument(new ByteArrayInputStream(s.getBytes()));
    }



