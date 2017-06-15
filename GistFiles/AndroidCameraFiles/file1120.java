try {
                    JSONArray flimsJSON = new JSONArray(s);
                    int numVideos = flimsJSON.length();
                    for(int i =0; i<numVideos;i++){
                        JSONObject titleJSON = (JSONObject) flimsJSON.get(i);
                        String titleString = titleJSON.getString("title");
                        Log.d("TITLE:",titleString);
                    }
                    Log.d("Number of MOVIES FETCHED","is"+numVideos);
                } catch (JSONException e) {
                    Log.e("Error","ALL ERROR");
                }