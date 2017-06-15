package com.example;


public class Main extends Activity {
 
    static final String KEY_ITEM = "item"; // parent node
    static final String KEY_TITLE = "title";
    static final String KEY_DATE = "pubDate";
    static final String KEY_LINK = "link";
    static final String KEY_IMG = "img";
    static final String KEY_CONT = "content:encoded";

    NodeList NodeList_Object;
    Document Doc;
    XMLParser Parser;


    ListView lv;
     
 


    public void getMore(String Url) {

        try {

 

            ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
            Parser = new XMLParser(context);

            String xml = Parser.getXmlFromUrl(Url);


            Doc = Parser.getDomElement(xml);

            NodeList_Object = Doc.getElementsByTagName(KEY_ITEM);

           
            for (int i = 0; i < NodeList_Object.getLength(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                Element e = (Element) NodeList_Object.item(i);
                map.put(KEY_TITLE, Parser.getValue(e, KEY_TITLE));
                map.put(KEY_DATE, Parser.parseDate(Parser.getValue(e, KEY_DATE)));
                map.put(KEY_IMG, getImageURL(Parser.getValue(e, KEY_CONT)));


                menuItems.add(map);
            }
            final CustomAdapter adapter = new CustomAdapter(this, menuItems);

            lv.setAdapter(adapter);
        } catch (Exception ex) {
            utils.message(ex.getMessage());
        }

    }

    public void getCategoryList() {

        try {
            XMLParser category_parser = new XMLParser(context);
            String xml = category_parser.getXmlFromUrl(getString(R.string.rss_category));
            final ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();

            Doc = category_parser.getDomElement(xml);

            NodeList NodeList_Category = Doc.getElementsByTagName(KEY_ITEM);
            String[] data = new String[NodeList_Category.getLength() + 1];

            data[0] = getString(R.string.show_all).toUpperCase();

            HashMap<String, String> map_all = new HashMap<String, String>();
            map_all.put("title", data[0]);
            map_all.put("link", getResources().getString(R.string.rss));
            menuItems.add(map_all);


            for (int i = 0; i < NodeList_Category.getLength(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                Element e = (Element) NodeList_Category.item(i);
                map.put("title", category_parser.getValue(e, KEY_TITLE));
                map.put("link", category_parser.getValue(e, KEY_LINK));
                data[i + 1] = category_parser.getValue(e, KEY_TITLE);
                menuItems.add(map);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(actionBar.getThemedContext(), android.R.layout.simple_list_item_1, data);

            navList.setAdapter(adapter);
 
        } catch (Exception ex) {
            utils.message(ex.getMessage());
        }

    }

  

}