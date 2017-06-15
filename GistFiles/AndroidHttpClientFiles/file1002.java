package jp.shts.android.nogirepo;

import jp.shts.android.nogirepo.Person.Type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Pair;

/**
 * 
{
    "talk": [
        {
            "person": {
                "type": "member",
                "_id": "a",
                "name": "a",
                "url": "a"
            },
            "say": "hello"
        },
        {
            "person": {
                "type": "app_user",
                "_id": "a",
                "name": "a",
                "url": "a"
            },
            "say": "hello"
        },
        {
            "person": {
                "type": "member",
                "_id": "a",
                "name": "a",
                "url": "a"
            },
            "say": "hello"
        }
    ]
}
 * 
 */
public class Talk extends PairList<Person, String> {

	public static Talk createFrom(JSONObject jsonObject) {
		
		Talk talk = new Talk();
		if (jsonObject == null) {
			// return Empty Object
			return talk;
		}

		try {

			JSONArray rootObject = (JSONArray) jsonObject.get("talk");
			final int N = rootObject.length();
			for (int i = 0; i < N; i++) {
				JSONObject object = (JSONObject) rootObject.get(i);

				JSONObject personObject = object.getJSONObject("person");

				String typeString = personObject.getString("type");
				String _id = personObject.getString("_id");
				String name = personObject.getString("name");
				String url = personObject.getString("url");
				
				String say = object.getString("say");
				
				Type personType = Type.from(typeString);
				if (personType == Type.APP_USER) {
					talk.add(new AppUser(_id, name, url), say);
					
				} else if (personType == Type.MEMBER) {
					talk.add(new Member(_id, name, url), say);
				}
			}
			
		} catch (JSONException e) {
			//
		}
		
		return talk;
	}
	
	public JSONArray toJSONArray() {
		
		JSONArray talk = new JSONArray();
		try {
			for (Pair<Person, String> pair : this) {
				JSONObject jsonObject = new JSONObject();
				
				JSONObject person = new JSONObject();
				person.put("_id", pair.first.id);
				person.put("name", pair.first.name);
				person.put("url", pair.first.url);
				
				jsonObject.put("person", person);
				jsonObject.put("say", pair.second);
				
				talk.put(jsonObject);
			}
			return talk;
		} catch (JSONException e) {
			
		}
		return talk;
	}
	
	public void load(String id) {
		
	}
	
}
