
protected void onPostExecute(JSONObject jsonRoot) {
  if (mSomeThing == null) {
    return ;
  }
  if (getActivity() == null) {
    return ;
  }
}




public void onStop() {
  mRequestQueue.cancelAll(tag);
}