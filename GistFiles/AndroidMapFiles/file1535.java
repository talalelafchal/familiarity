// Usage:
// blacklist
String[] blacklist = new String[]{"com.any.package", "net.other.package"};
// your share intent
Intent intent = new Intent(Intent.ACTION_SEND);
intent.setType("text/plain");
intent.putExtra(Intent.EXTRA_TEXT, "some text");
intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "a subject");
// ... anything else you want to add
// invoke custom chooser
startActivity(generateCustomChooserIntent(intent, blacklist));

// Method:
private Intent generateCustomChooserIntent(Intent prototype, String[] forbiddenChoices) {
	List<Intent> targetedShareIntents = new ArrayList<Intent>();
	List<HashMap<String, String>> intentMetaInfo = new ArrayList<HashMap<String, String>>();
	Intent chooserIntent;

	Intent dummy = new Intent(prototype.getAction());
	dummy.setType(prototype.getType());
	List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(dummy, 0);

	if (!resInfo.isEmpty()) {
		for (ResolveInfo resolveInfo : resInfo) {
			if (resolveInfo.activityInfo == null || Arrays.asList(forbiddenChoices).contains(resolveInfo.activityInfo.packageName))
				continue;

			HashMap<String, String> info = new HashMap<String, String>();
			info.put("packageName", resolveInfo.activityInfo.packageName);
			info.put("className", resolveInfo.activityInfo.name);
			info.put("simpleName", String.valueOf(resolveInfo.activityInfo.loadLabel(getPackageManager())));
			intentMetaInfo.add(info);
		}

		if (!intentMetaInfo.isEmpty()) {
			// sorting for nice readability
			Collections.sort(intentMetaInfo, new Comparator<HashMap<String, String>>() {
				@Override
				public int compare(HashMap<String, String> map, HashMap<String, String> map2) {
					return map.get("simpleName").compareTo(map2.get("simpleName"));
				}
			});

			// create the custom intent list
			for (HashMap<String, String> metaInfo : intentMetaInfo) {
				Intent targetedShareIntent = (Intent) prototype.clone();
				targetedShareIntent.setPackage(metaInfo.get("packageName"));
				targetedShareIntent.setClassName(metaInfo.get("packageName"), metaInfo.get("className"));
				targetedShareIntents.add(targetedShareIntent);
			}

			chooserIntent = Intent.createChooser(targetedShareIntents.remove(targetedShareIntents.size() - 1), getString(R.string.share));
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
			return chooserIntent;
		}
	}

	return Intent.createChooser(prototype, getString(R.string.share));
}