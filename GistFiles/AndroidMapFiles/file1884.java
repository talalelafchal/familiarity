import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

public class ApiManager {

    private static ApiManager instance;

    public BookmarkService bookmarkService;
    public TagService tagService;

    RestAdapter catalogRestAdapter;

    public static ApiManager getInstance() {
        if (instance == null) {
            instance = new ApiManager();
        }
        return instance;
    }

    public ApiManager() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader(Constants.api.HEADER_API_KEY, Constants.api.API_TEST_KEY);
            }
        };

        catalogRestAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.api.URL)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel((BuildConfig.DEBUG ?
                        RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE))
                .build();

        bookmarkService = catalogRestAdapter.create(BookmarkService.class);
        tagService = catalogRestAdapter.create(TagService.class);
    }

    public void getBookmarks(RestCallback<List<Bookmark>> callback) {
        bookmarkService.get(callback);
    }

    public void postBookmark(Bookmark bookmark, RestCallback<Bookmark> callback) {
        bookmarkService.post(bookmark, callback);
    }

    public void putBookmark(Bookmark bookmark, RestCallback<Bookmark> callback) {
        bookmarkService.put(bookmark.getId(), bookmark, callback);
    }

    public void getTags(RestCallback<List<Tag>> callback) {
        tagService.get(callback);
    }

    // ----------- Services.

    public interface BookmarkService {
        // -- The api errors code
        int BOOKMARK_URL_EMPTY = 4001;
        int BOOKMARK_ALREADY_EXISTS = 4002;
        int INVALID_URL = 4002;
        int WEBSITE_NO_TITLE = 4003;
        int MONGODB_ERROR = 5001; // Failed to save the bookmark
        int BOOKMARK_NOT_FOUND = 1002;

        // -- The routes

        @GET("/bookmark")
        void get(RestCallback<List<Bookmark>> callback);

        @POST("/bookmark")
        void post(@Body Bookmark bookmark, RestCallback<Bookmark> callback);

        @PUT("/bookmark/{id}")
        void put(@Path("id") String id, @Body Bookmark bookmark, RestCallback<Bookmark> callback);
    }

    public interface TagService {
        // -- The api errors code
        // TODO

        // -- The routes

        @GET("/tag")
        void get(RestCallback<List<Tag>> callback);

        @POST("/tag")
        void post(@Body Tag bookmark, RestCallback<Tag> callback);

        @PUT("/tag")
        void put(@Body Tag bookmark, RestCallback<Tag> callback);
    }

}