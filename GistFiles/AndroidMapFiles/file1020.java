for (String name : names) {
	int id = Resources.getSystem().getIdentifier(name, defType, defPackage);
//	if (id != 0)
		mapResources.put(name, id);
}

Log.i(TAG, mapResources.toString());

