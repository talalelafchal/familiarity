public class MyTwitterApiClient extends TwitterApiClient {


    public MyTwitterApiClient(Session session) {
        super(session);
    }

    public MyStatusesService getMyStatusesService() {
        return this.getService(MyStatusesService.class);
    }

    public interface MyStatusesService {
        @FormUrlEncoded
        @POST("/1.1/statuses/update.json")
        void update(@Field("status") String status, @Field("in_reply_to_status_id") Long inReplyToStatusId,@Field("media_ids") String mediaIds, Callback<Tweet> cb);
    }
}