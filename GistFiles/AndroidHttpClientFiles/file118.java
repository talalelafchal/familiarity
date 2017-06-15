
public class TwitterFriends extends ArrayList<TwitterFriend> {
	
	private TwitterFriends() {}

	public static TwitterFriends from(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return new TwitterFriends();
		}
		JSONArray rootObject = new JSONArray(jsonString);
		final int N = rootObject.size();
		for (int i = 0; i < N; i++) {
			add(TwitterFriend.from(rootObject.get(i)));
		}
	}
	
	public ArrayList<String> getIdList() {
		ArrayList<String> list = new ArrayList<String>();
		for (TwitterFriend friend : this) {
			list.add(friend.id_str);
		}
		return list;
	}

}
