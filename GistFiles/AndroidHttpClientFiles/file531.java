/**
 * Original: https://github.com/twitter/hbc/blob/master/hbc-core/src/main/java/com/twitter/hbc/httpclient/auth/OAuth1.java
 * 
 * It is compatible with scala-less joauth 6.0.+, but hbc is not 
 * compatible with Android. #EpicFail
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 **/

/**
 * Copyright 2013 Twitter, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.twitter.hbc.core.HttpConstants;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.joauth.Normalizer;
import com.twitter.joauth.OAuthParams;
import com.twitter.joauth.Request;
import com.twitter.joauth.Signer;
import com.twitter.joauth.UrlCodec;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.AbstractHttpClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OAuth1 implements Authentication {

    private final Normalizer normalizer;
    private final Signer signer;

    private final String consumerKey;
    private final String consumerSecret;
    private final String token;
    private final String tokenSecret;

    private final SecureRandom secureRandom;

    public OAuth1(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.consumerKey = Preconditions.checkNotNull(consumerKey);
        this.consumerSecret = Preconditions.checkNotNull(consumerSecret);

        this.token = Preconditions.checkNotNull(token);
        this.tokenSecret = Preconditions.checkNotNull(tokenSecret);

        this.normalizer = new Normalizer.StandardNormalizer();
        this.signer = new Signer.StandardSigner();

        this.secureRandom = new SecureRandom();
    }

    @Override
    public void setupConnection(AbstractHttpClient client) {}

    @Override
    public void signRequest(HttpUriRequest request, String postParams) {
        // TODO: this is a little odd: we already encoded the values earlier, but using URLEncodedUtils.parse will decode the values,
        // which we will encode again.
        List<NameValuePair> httpGetParams = URLEncodedUtils.parse(request.getURI(), "UTF-8");
        List<Request.Pair> javaParams = new ArrayList<Request.Pair>(httpGetParams.size());
        for (NameValuePair params : httpGetParams) {
            Request.Pair tuple = new Request.Pair(UrlCodec.encode(params.getName()), UrlCodec.encode(params.getValue()));
            javaParams.add(tuple);
        }

        if (postParams != null) {
            List<NameValuePair> httpPostParams = null;
            try {
                httpPostParams = URLEncodedUtils.parse(new URI(postParams), "UTF-8");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            for (NameValuePair params : httpPostParams) {
                Request.Pair tuple = new Request.Pair(UrlCodec.encode(params.getName()), UrlCodec.encode(params.getValue()));
                javaParams.add(tuple);
            }
        }

        long timestampSecs = generateTimestamp();
        String nonce = generateNonce();

        OAuthParams.OAuth1Params oAuth1Params = new OAuthParams.OAuth1Params(
                token, consumerKey, nonce, timestampSecs, Long.toString(timestampSecs), "",
                OAuthParams.HMAC_SHA1, OAuthParams.ONE_DOT_OH
        );

        int port = request.getURI().getPort();
        if (port <= 0) {
            // getURI can return a -1 for a port
            if (request.getURI().getScheme().equalsIgnoreCase(HttpConstants.HTTP_SCHEME)) {
                port = HttpConstants.DEFAULT_HTTP_PORT;
            } else if (request.getURI().getScheme().equalsIgnoreCase(HttpConstants.HTTPS_SCHEME)) {
                port = HttpConstants.DEFAULT_HTTPS_PORT;
            } else {
                throw new IllegalStateException("Bad URI scheme: " + request.getURI().getScheme());
            }
        }

        String normalized = normalizer.normalize(
                request.getURI().getScheme(), request.getURI().getHost(), port, request.getMethod().toUpperCase(),
                request.getURI().getPath(), javaParams, oAuth1Params
        );

        String signature = null;
        try {
            signature = signer.getString(normalized, tokenSecret, consumerSecret);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Map<String, String> oauthHeaders = new HashMap<String, String>();
        oauthHeaders.put(OAuthParams.OAUTH_CONSUMER_KEY, quoted(consumerKey));
        oauthHeaders.put(OAuthParams.OAUTH_TOKEN, quoted(token));
        oauthHeaders.put(OAuthParams.OAUTH_SIGNATURE, quoted(signature));
        oauthHeaders.put(OAuthParams.OAUTH_SIGNATURE_METHOD, quoted(OAuthParams.HMAC_SHA1));
        oauthHeaders.put(OAuthParams.OAUTH_TIMESTAMP, quoted(Long.toString(timestampSecs)));
        oauthHeaders.put(OAuthParams.OAUTH_NONCE, quoted(nonce));
        oauthHeaders.put(OAuthParams.OAUTH_VERSION, quoted(OAuthParams.ONE_DOT_OH));
        String header = Joiner.on(", ").withKeyValueSeparator("=").join(oauthHeaders);

        request.setHeader("Authorization", "OAuth " + header);

    }

    private String quoted(String str) {
        return "\"" + str + "\"";
    }

    private long generateTimestamp() {
        long timestamp = System.currentTimeMillis();
        return timestamp / 1000;
    }

    private String generateNonce() {
        return Long.toString(Math.abs(secureRandom.nextLong())) + System.currentTimeMillis();
    }
}
