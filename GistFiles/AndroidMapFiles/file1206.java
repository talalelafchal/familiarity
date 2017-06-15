public class TwitterUploadClient {

    private final String UPLOAD_ENDPOINT = "https://upload.twitter.com";

    MediaService service;
    final RestAdapter adapter;

    TwitterUploadClient(TwitterAuthConfig authConfig, Session session, SSLSocketFactory sslSocketFactory, ExecutorService executorService) {
        if(session == null) {
            throw new IllegalArgumentException("Session must not be null.");
        } else {
            Gson gson = (new GsonBuilder()).registerTypeAdapterFactory(new SafeListAdapter()).registerTypeAdapterFactory(new SafeMapAdapter()).create();
            this.adapter = (new RestAdapter.Builder()).setClient(new AuthenticatedClient(authConfig, session, sslSocketFactory)).setEndpoint(UPLOAD_ENDPOINT).setConverter(new GsonConverter(gson)).setExecutors(executorService, new MainThreadExecutor()).build();
        }
    }

    public TwitterUploadClient(Session session) {
        this(TwitterCore.getInstance().getAuthConfig(), session,  TwitterCore.getInstance().getSSLSocketFactory(), TwitterCore.getInstance().getFabric().getExecutorService());
    }

    public MediaService getMediaService() {
        if(this.service == null) {
            this.service = this.adapter.create(MediaService.class);
        }

        return (MediaService)this.service;
    }

    public interface MediaService {
        @Multipart
        @POST("/1.1/media/upload.json")
        void upload(@Part("media") TypedFile media, Callback<MediaEntity> cb);
        @Multipart
        @POST("/1.1/media/upload.json")
        void upload(@Part("media_data") String mediaData, Callback<MediaEntity> cb);
    }
}