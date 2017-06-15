package data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by artyomvursalov on 04.06.13.
 */
public class Publication {
    public int id;
    public String name;
    Publication(JSONObject object){
        if(object!=null){
            this.id=object.optInt("id");
            this.name=object.optString("name");
        }
    }
    public static Vector<Publication> fromJSONArray(JSONArray array){
        Vector<Publication> publications=new Vector<Publication>();
        for(int i=0; i<array.length(); i++){
            publications.add(new Publication(array.optJSONObject(i)));
        }
        return publications;
    }
}
