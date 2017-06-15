package io.coreflodev.openchat.api;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import static io.coreflodev.openchat.common.network.HttpService.HEADER_CACHE;

public interface ChatService {

    @GET("/messages")
    @Headers(HEADER_CACHE + ": 60")
    Observable<List<ChatMessage>> getMessages();
}