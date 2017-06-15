// fetch the campaigns
    private void loadCampaigns() {
      progressDialog = ProgressDialog.show(this, null, getString(R.string.waiting));
		
		Thread t = new Thread() {
        	public void run () {
        		PadgetsClient client = new PadgetsClient();
        		ArrayList<Campaign> results = client.searchCampaigns(getApplicationContext(),keyword,location);
        		if (results == null ) {
        	        h.sendEmptyMessage(PadgetsClient.FAILURE);
        		} else if (results.size() == 0 ){
        			h.sendEmptyMessage(PadgetsClient.NO_RESULTS);
        		} else {
        			items = results;
        			h.sendEmptyMessage(PadgetsClient.SUCCESS);
        		}
        	}
        };
        t.start();
    }
    
    
   //When you are using a thread it is often useful to also use a Handler as a means of communication between
   //the work thread that you are starting and the main thread (UI usually)
   //So if you want to update the UI after a thread has ended you need to use a handler
    
    //Here h is a handler:
    // handles search results
  final Handler h = new Handler() {
        public void handleMessage(Message msg) {  
        	progressDialog.dismiss();
        	switch (msg.what) {
			case PadgetsClient.SUCCESS:
				mAdapter.changeData(items);
				break;

			case PadgetsClient.NO_RESULTS:
				// do nothing
				break;
				
			case PadgetsClient.FAILURE:
				Toast.makeText(CampaignListActivity.this, R.string.error_occured, Toast.LENGTH_SHORT).show();
				break;
				
			default:
				break;
			}
        }           
    };
    
    
    
    //Actual call
    public ArrayList<Campaign> searchCampaigns(Context ctx, String keyword,String location) {
  	ArrayList<Campaign> results = new ArrayList<Campaign>();

		try {
			String url = _urlProvider.searchCampaignsURL(keyword,location)
					+ _urlProvider.buildCampaignsArgs(ctx);
			if (Utils.LOG)
				Log.d("searchCampaigns", url);
			HttpGet getMethod = new HttpGet(url);

			// set header
			getMethod.setHeader("Accept", "application/json");

			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				public String handleResponse(final HttpResponse response)
						throws HttpResponseException, IOException {
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() >= 300) {
						throw new HttpResponseException(
								statusLine.getStatusCode(),
								statusLine.getReasonPhrase());
					}

					HttpEntity entity = response.getEntity();
					return entity == null ? null : EntityUtils.toString(entity,
							"UTF-8");
				}
			};

			String responseBody = client.execute(getMethod, responseHandler);

			JSONArray campaigns = new JSONArray(responseBody);

			// get the campaigns
			for (int i = 0; i < campaigns.length(); i++) {
				JSONObject object = campaigns.getJSONObject(i);
				
				/******************************************/
				//By hand
				// Storing each json item in variable
			        String id = object.getString(TAG_ID);
			        String name = object.getString(TAG_NAME);
			        String email = object.getString(TAG_EMAIL);
			        String address = object.getString(TAG_ADDRESS);
			        String gender = object.getString(TAG_GENDER);
			        /******************************************/
				
				//OR (parseCampaign is a custom function, you have to implement it)
				Campaign p = parseCampaign(object);
				results.add(p);
			}

			return results;
		} catch (Exception e) {
			if (Utils.LOG)
				Log.e("searchCampaigns",
						e.getMessage() != null ? e.getMessage() : "mistake");
			return null;
		}
	}
	

	/***************************************************************/
	//Post example
	
	public boolean postCampaign(Context ctx, Campaign c) {
		try {
			String url = _urlProvider.getCampaignsURL()
					+ _urlProvider.buildCampaignsArgs(ctx);
			if (Utils.LOG)
				Log.d("postCampaign", url);
			HttpPost postMethod = new HttpPost(url);

			// set header
			postMethod.setHeader("Accept", "application/json");

			JSONObject object = new JSONObject();
			object.put("title", c.title);
			object.put("active", c.isActive);
			object.put("startdate", c.startDate.getTime());
			object.put("enddate", c.endDate.getTime());
			object.put("notes", c.notes);
			object.put("url", c.url);
			object.put("hashTag", c.hashTag);

			JSONObject loc = new JSONObject();
			loc.put("idLocation", c.location.id);
			object.put("location", loc);

			JSONArray topics = new JSONArray();
			for (String topic : c.topics) {
				JSONObject top = new JSONObject();
				top.put("topic", topic);
				topics.put(top);
			}
			object.put("topics", topics);

			JSONArray channels = new JSONArray();
			for (PublishChannel channel : c.channels) {
				JSONObject ch = new JSONObject();
				ch.put("idPublishChannel", channel.id);
				channels.put(ch);
			}
			object.put("publishchannels", channels);

			String reportEntry = object.toString();
			StringEntity postData = new StringEntity(reportEntry, "UTF-8");
			postData.setContentType("application/json; charset=UTF-8");
			postMethod.setEntity(postData);

			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			String responseBody = client.execute(postMethod, responseHandler);

			Utils.DLog(responseBody);
			return true;
		} catch (Exception e) {
			if (Utils.LOG)
				Log.e("createCampaign", e.getMessage() != null ? e.getMessage()
						: "mistake");
			return false;
		}
	}