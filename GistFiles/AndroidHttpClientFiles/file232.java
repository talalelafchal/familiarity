protected JSONObject doInBackground(URL... params) {
  HttpURLConnection conn = (HttpURLConnection) params[0].openConnection();
  InputStream input = conn.getInputStream();
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  copy(input, baos);
  JSONObject jsonRoot = new JSONObject(baos.toString());
  return jsonRoot;
}

protected void onPostExecute(JSONObject jsonRoot) {
  List<Items> items = parseJson(jsonRoot);
  appendItemsToList(item);
  notifyDataSetChanged();
}