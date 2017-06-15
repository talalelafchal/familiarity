package data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by Artyom on 6/1/13.
 */
public class Genre {
    public int id;
    public String name;
    public Integer parent;
    Genre(JSONObject object){
        if(object!=null){
            this.id=object.optInt("id");
            this.name=object.optString("name");
            this.parent=object.optInt("parent");
        }
    }
    public static Vector<Genre> fromJSONArray(JSONArray array){
        Vector<Genre> genres=new Vector<Genre>();
        for(int i=0; i<array.length(); i++){
            genres.add(new Genre(array.optJSONObject(i)));
        }
        return genres;
    }
}
