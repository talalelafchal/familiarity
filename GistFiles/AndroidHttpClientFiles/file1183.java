
public class TwitterFriend {
	// id
	public String id_str;
	public in id;
	
	// name
	public String name;
	public String screen_name;
	
	// image
	public String profile_image_url;
	public String profile_image_url_https;
	
	// color
	public String profile_sidebar_fill_color;
	public String profile_sidebar_border_color;
	public String profile_link_color;
	public String profile_text_color;
	public String profile_background_color;
	
	public static Friend from(JSONObject jsonObject) {
		Friend friend = new Friend();
		try {
			friend.id_str = jsonObject.getString("id_str");
			friend.id = jsonObject.getInt("id");
			friend.name = jsonObject.getString("name");
			friend.screen_name = jsonObject.getString("screen_name");
			friend.profile_image_url = jsonObject.getString("profile_image_url");
			friend.profile_image_url_https = jsonObject.getString("profile_image_url_https");
			friend.profile_sidebar_fill_color = jsonObject.getString("profile_sidebar_fill_color");
			friend.profile_sidebar_border_color = jsonObject.getString("profile_sidebar_border_color");
			friend.profile_link_color = jsonObject.getString("profile_link_color");
			friend.profile_text_color = jsonObject.getString("profile_text_color");
			friend.profile_background_color = jsonObject.getString("profile_background_color");
		} catch (JSONException e) {
			//
		}
		return friend;
	}
}