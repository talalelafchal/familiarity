package data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by Artyom on 6/1/13.
 */
public class Category {
    public int id;
    public String name;
    public Integer parent;
    Category(JSONObject object){
        if(object!=null){
            this.id=object.optInt("id");
            this.name=object.optString("name");
            this.parent=object.optInt("parent");
        }
    }
    public static Vector<Category> fromJSONArray(JSONArray array){
        Vector<Category> categories=new Vector<Category>();
        for(int i=0; i<array.length(); i++){
            categories.add(new Category(array.optJSONObject(i)));
        }
        return categories;
    }
}
