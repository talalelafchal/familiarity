package jp.shts.android.nogirepo;

public class AppUser extends Person {

	AppUser(String id, String url, String name) {
		super(Type.APP_USER, id, url, name);
	}
	
	public static List<AppUser> findByIds(List<String> idList) {
		// ParseUser
		ParseQuery<ParseObject> query = ParseQuery.getQuery("AppUser");
		query.whereContainedIn("social_id", idList);
		List<ParseUser> users = query.findInBackground();
		return users;
	}
	
	public static void findByIds(List<String> idList, FindCallback<AppUser> callback) {
		// ParseUser
		ParseQuery<ParseObject> query = ParseQuery.getQuery("AppUser");
		query.whereContainedIn("social_id", idList);
		query.findInBackground(callback);
	}
}
