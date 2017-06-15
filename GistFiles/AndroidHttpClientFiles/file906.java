package com.cheerfulinc.flipagram.client.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import android.net.Uri;
import android.util.Log;

import com.cheerfulinc.flipagram.client.UploadCredentials;
import com.cheerfulinc.flipagram.db.FlipagramDao;
import com.cheerfulinc.flipagram.http.*;
import com.cheerfulinc.flipagram.encoder.AVProfile;
import com.cheerfulinc.flipagram.model.Flipagram;
import com.cheerfulinc.flipagram.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * An {@link HttpCommand} for creating a flipagram.
 */
public class CreateFlipagramCommand
        extends AbstractPlatformHttpCommand<CreateFlipagramCommand, CreateFlipagramCommand.Callbacks> {

    private static final SimpleDateFormat HTTP_DATE_FORMAT = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    private static final String TAG = "Flipagram/CreateFlipagramCommand";

    private static final int MSG_CREATED = 100;
    private static final int MSG_COVER_PROGRESS = 200;
    private static final int MSG_UPLOADED_COVER = 300;
    private static final int MSG_VIDEO_PROGRESS = 400;
    private static final int MSG_UPLOADED_VIDEO = 500;
    private static final int MSG_UPLOADED = 600;
    private static final int MSG_CREDENTIALS = 700;
    private AtomicInteger failureCount = new AtomicInteger(0);

    public static class Callbacks
            extends AbstractPlatformHttpCommand.Callbacks {
        public void onCredentials(UploadCredentials credentials) {
        }

        public void onCoverUploadProgress(long total, long current) {
        }

        public void onUploadedCover(Flipagram flipagram) {
        }

        public void onVideoUploadProgress(long total, long current) {
        }

        public void onUploadedVideo(Flipagram flipagram) {
        }

        public void onUploaded(Flipagram flipagram) {
        }

        public void onCreated(Flipagram flipagram) {
        }
    }

    private Flipagram flipagram;

    public CreateFlipagramCommand(Flipagram flipagram) {
        this.flipagram = flipagram;
    }

    @Override
    protected void handleMessage(int what, Object[] args) {
        switch (what) {
            case MSG_CREDENTIALS:
                getCallbacks().onCredentials(getArg(UploadCredentials.class, args, 0));
                return;
            case MSG_COVER_PROGRESS:
            	getCallbacks().onCoverUploadProgress(
                        getArg(Long.class, args, 0), getArg(Long.class, args, 1));
                return;
            case MSG_UPLOADED_COVER:
            	getCallbacks().onUploadedCover(getArg(Flipagram.class, args, 0));
                return;
            case MSG_VIDEO_PROGRESS:
            	getCallbacks().onVideoUploadProgress(
                        getArg(Long.class, args, 0), getArg(Long.class, args, 1));
                return;
            case MSG_UPLOADED_VIDEO:
            	getCallbacks().onUploadedVideo(getArg(Flipagram.class, args, 0));
                return;
            case MSG_UPLOADED:
            	getCallbacks().onUploaded(getArg(Flipagram.class, args, 0));
                return;
            case MSG_CREATED:
            	getCallbacks().onCreated(getArg(Flipagram.class, args, 0));
                return;
            default:
                super.handleMessage(what, args);
        }
    }

    /**
     * Executes.
     */
    @Override
    protected void doExecute(HttpClient client)
            throws HttpException,
            IOException {

        // get credentials
        UploadCredentials creds = getUploadCredentials();
        Log.d(TAG, "Got upload credentials: " + creds.uploadId);

        // upload it
        uploadFlipagram(creds);
        Log.d(TAG, "Uploaded flipagram");

        // create it
        createFlipagram(creds);
        Log.d(TAG, "Created flipagram");
    }

    /**
     * Gets upload credentials.
     *
     * @return the credentials
     * @throws HttpException on error
     * @throws IOException on error
     */
    private UploadCredentials getUploadCredentials()
            throws HttpException,
            IOException {

        // execute it
        Response response = execute(
                post(endpoint("/media/upload_credentials"))
                        .setParam("coverFileName", flipagram.coverImageFile.getName())
                        .setParam("videoFileName", flipagram.finalRenderFile.getName()));
        assertOK(response);

        // get data
        JsonNode data = getData(response);

        // get credentials
        UploadCredentials ret = new UploadCredentials();
        ret.bucketName = data.get("bucketName").asText();
        ret.coverId = data.get("coverId").asText();
        ret.videoId = data.get("videoId").asText();
        ret.uploadId = data.get("uploadId").asText();
        ret.accessKey = data.get("uploadCredentials").get("accessKey").asText();
        ret.secretAccessKey = data.get("uploadCredentials").get("secretAccessKey").asText();
        ret.sessionToken = data.get("uploadCredentials").get("sessionToken").asText();
        ret.tokenExpiration = data.get("uploadCredentials").get("tokenExpiration").asText();

        // send the message
        sendMessage(MSG_CREDENTIALS, ret);

        // return it
        return ret;
    }

    /**
     * Uploads the flipagram and returns the upload token.
     *
     * @return the token
     * @throws HttpException on error
     * @throws IOException on error
     */
    private void uploadFlipagram(UploadCredentials credentials)
            throws HttpException,
            IOException {

    	// reset the failure count
        failureCount.set(0);

        // get profile
        AVProfile profile = AVProfile.get();

        // upload the cover
        uploadFile(credentials, flipagram.coverImageFile,
                credentials.coverId, "image/jpeg",
                new ProgressListener() {
                    @Override
                    public void onProgress(long total, long current) {
                        sendMessage(MSG_COVER_PROGRESS, Long.valueOf(total), Long.valueOf(current));
                    }
                });
        sendMessage(MSG_UPLOADED_COVER, flipagram);

        // upload the video
        uploadFile(credentials, flipagram.finalRenderFile,
                credentials.videoId, profile.getContentType(),
                new ProgressListener() {
                    @Override
                    public void onProgress(long total, long current) {
                        sendMessage(MSG_VIDEO_PROGRESS, Long.valueOf(total), Long.valueOf(current));
                    }
                });
        sendMessage(MSG_UPLOADED_VIDEO, flipagram);

        // send the message
        sendMessage(MSG_UPLOADED, flipagram);
    }

    /**
     * Creates the flipagram.
     *
     * @throws HttpException on error
     * @throws IOException on error
     */
    private void createFlipagram(UploadCredentials credentials)
            throws HttpException,
            IOException {

        // generate json
        ObjectNode json = Json.getMapper().createObjectNode();

        json.put("uploadId", credentials.uploadId);
        json.put("frameCount", flipagram.frameCount());
        json.put("frameDuration", flipagram.frameDuration);
        json.put("clientDateCreated", flipagram.createdTs);

        if (flipagram.hasAudio()) {
            json.put("trackStart", flipagram.audioStart);
            json.put("trackName", flipagram.audioTrackTitle);
            json.put("trackNumber", flipagram.audioTrackNumber);
            json.put("albumName", flipagram.audioTrackAlbum);
            json.put("artistName", flipagram.audioTrackArtist);
            json.put("trackSource", flipagram.audioTrackSource);
            json.put("trackSourceId", flipagram.audioTrackSourceId);
            json.put("trackSourceBuyUrl", flipagram.audioTrackSourceBuyUrl);
        }

        if (flipagram.hasTitle()) {
            json.put("title", flipagram.title.text);
            json.put("titleFont", flipagram.title.fontName);
            json.put("titleColor", flipagram.title.color);
        }

        if (flipagram.hasWatermark()) {
            json.put("watermark", flipagram.watermark.text);
            json.put("watermarkFont", flipagram.watermark.fontName);
            json.put("watermarkLocation", "BOTTOM_RIGHT");
            json.put("watermarkColor", flipagram.watermark.color);
        }

        // post it
        Response response = execute(
                post(endpoint("/flipagram/save"))
                        .withBody(Json.asJsonString(json))
                        .withContentType("application/json"));
        assertOK(response);

        // get flipagram data
        JsonNode remoteFlipagram = getData(response).get("flipagram");
        flipagram.uploaded = true;
        flipagram.remoteId = remoteFlipagram.get("id").asText();
        flipagram.videoUrl = remoteFlipagram.get("videoUrl").asText();
        flipagram.coverUrl = remoteFlipagram.get("coverUrl").asText();
        flipagram.url = remoteFlipagram.get("url").asText();
        flipagram.shortUrl = remoteFlipagram.get("shortUrl").asText();

        // save it
        new FlipagramDao().updateFlipagram(flipagram);

        // send the message
        sendMessage(MSG_CREATED, flipagram);
    }

    /**
     * Uploads a file.
     *
     * @param credentials the credentials
     * @param file        the file
     * @param destination where to upload
     * @param contentType the content type
     */
    private void uploadFile(
            UploadCredentials credentials,
            File file, String destination, String contentType,
            ProgressListener uploadListener)
            throws HttpException,
            IOException {

    	// get time skew
        long skew = Prefs.getInstance().getTimeSkew();

    	// create calendar
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeZone(TimeZone.getTimeZone("GMT"));
    	cal.setTimeInMillis(System.currentTimeMillis() + skew);

        // Get system date + previously obtained skew
        String date = HTTP_DATE_FORMAT.format(cal.getTime());

        // generate string to sign
        String stringToSign = ""
                + "PUT\n"
                + "" + "\n" // md5
                + contentType + "\n"
                + date + "\n"
                + "x-amz-security-token:" + credentials.sessionToken + "\n" // headers
                + "/" + credentials.bucketName + "/" + destination;

        // sign it
        String signature = Strings.base64(Strings.hmacSha1(
                stringToSign.getBytes(), credentials.secretAccessKey));

        // create auth
        String authorization = "AWS " + credentials.accessKey + ":" + signature;

        // get base url
        String baseUrl = (!Strings.isEmpty(credentials.baseUrl)
	        		? credentials.baseUrl
	        		: "http://"+credentials.bucketName+".s3.amazonaws.com")
        		.trim();;
        while (baseUrl.endsWith("/")) {
        	baseUrl = baseUrl.substring(0, baseUrl.length()-1);
        }

        // upload it
        InputStream ips = new FileInputStream(file);
        Request s3Request = put(baseUrl+"/"+ destination)
                .withContentType(contentType)
                .setHeader("Content-Length", file.length() + "")
                .setHeader("Host", Uri.parse(baseUrl).getHost())
                .setHeader("Date", date)
                .setHeader("x-amz-security-token", credentials.sessionToken)
                .setHeader("Authorization", authorization)
                .withBody(ips, file.length())
                .withContentType(contentType)
                .withUploadListener(uploadListener);

        try {
            assertOK(execute(s3Request));
        }
        catch (UnExpectedStatusCodeException e) {

            // Amazon S3 requires a 'Date' parameter close to the current time. If the device time sent
            // is too different from their time, we'll get a 403 response. This response contains the
            // current time however, which lets us determine a skew to add to the next request,
            // which we retry immediately.
            if (e.getCode() == 403) {
                Log.w(TAG, "Device time too skewed for S3, saving obtained offset");

                Pairs headers = e.getResponse().getHeaders();
                List<Pairs.Pair> pair = headers.get("Date");
                if (pair.size() == 0
                		|| failureCount.incrementAndGet()>=3) {
                    throw e;
                }

                try {
	                long s3Time = HTTP_DATE_FORMAT.parse(pair.get(0).value).getTime();
	                skew = s3Time - System.currentTimeMillis();
	                Prefs.getInstance().setTimeSkew(skew);
                } catch(ParseException pe) {
                	throw new HttpException("Unable to parse date from server", pe);
                }

                Log.i(TAG, "Retrying S3 request with time: " + date);
                uploadFile(credentials, file, destination, contentType, uploadListener);
            }
            else
                throw e;
        }
        finally {
            IO.closeQuietly(ips);
        }
    }

}