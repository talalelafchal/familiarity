  public void doShareToFacebook(View view){
        Log.d(TAG, "share to facebook");

        final Session.NewPermissionsRequest newPermissionsRequest = new Session
                .NewPermissionsRequest(this, Arrays.asList("publish_actions"));

        if(Session.getActiveSession()!=null){
            Session session = Session.getActiveSession();
            shareToFacebook(session.getAccessToken());
        }else{
            // start Facebook Login
            Session.openActiveSession(this, true, new Session.StatusCallback() {
                // callback when session changes state
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    if(session.isOpened()){ //already login
                        Session.getActiveSession().requestNewPublishPermissions(newPermissionsRequest);
                        Log.d(TAG, "facebook get permission "+ session.getPermissions());
                        shareToFacebook(session.getAccessToken());
                    }else if(session.isClosed()){
                        Log.d(TAG, "facebook session get failed ");
                    }
                }
            });
        }
    }


   private void shareToFacebook(String accessToken){
       LwImage lwImage = mImageList.get(mPager.getCurrentItem());
       String objectId = lwImage.getObjectId();
       final ArrayList<String> objectIDs = new ArrayList<String>();
       objectIDs.add(objectId);

       final LwApiBasicCallback callback = new LwApiBasicCallback() {
           @Override
           public void onCallback(HashMap<String, String> result) {
               if (LiveWeddingApiHelper.getStatus(result)) {
                   mToastMsgHandler.sendMessage(getString(R.string.slideShowShareSuccessMessage));
               } else {    // failed to sign-in
                   Log.d(TAG, "share photo failed");
                   mToastMsgHandler.sendMessage(getString(R.string.slideShowShareFailedMessage));
               }
           }
       };

       Log.d(TAG, "share image object id : " + objectId);
       Log.d(TAG, "facebook access token : " + accessToken);
       mLwApiHelper.shareToFacebook(mAuthToken, accessToken, objectIDs, callback);
   }

