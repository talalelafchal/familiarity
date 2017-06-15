package tw.com.fet.ecs.manager;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.hiiir.toolkit.debug.HrLog;
import com.hiiir.toolkit.network.HttpClientEx;
import com.hiiir.toolkit.util.DateUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import tw.com.fet.ecs.BaseApplication;
import tw.com.fet.ecs.Const;
import tw.com.fet.ecs.db.provider.ECSDataProvider;
import tw.com.fet.ecs.db.table.TransferFileTable;
import tw.com.fet.ecs.image.VolleySingleton;
import tw.com.fet.ecs.image.watermark.WaterMarker;
import tw.com.fet.ecs.intent.IntentAction;
import tw.com.fet.ecs.manager.listener.ECSReceiveMessageListener;
import tw.com.fet.ecs.modle.Member;
import tw.com.fet.ecs.modle.Message;
import tw.com.fet.ecs.modle.TransferFile;
import tw.com.fet.ecs.modle.Voice;
import tw.com.fet.ecs.net.HttpClientHelper;
import tw.com.fet.ecs.util.DBUtil;
import tw.com.fet.ecs.util.ImageUtil;
import tw.com.fet.ecs.util.Security;
import tw.com.fet.ecs.xmpp.level1.XmppSdkAdapterLevel1;

/**
 * @author Salister
 */
public class FileTransferManager {

    private static final String TAG = FileTransferManager.class.getSimpleName();
    private static FileTransferManager instance;
    private static boolean mShouldWatermark = false;
    private Context mContext = BaseApplication.getContext();

    public static FileTransferManager getInstance() {
        if (instance == null) {
            instance = new FileTransferManager();
        }

        return instance;
    }

    private FileTransferManager() {
        super();
        mContext = BaseApplication.getContext();
    }

    public JSONObject apiRequestUploadUrl(String to, boolean isGroup) throws Exception {
        String api = "/user/request-upload-url.api";

        HrLog.d(TAG, "try to RequestUploadUrl api...");
        HrLog.d(TAG, "url: " + BasicManager.getInstance().getApiUrl() + api);
        HrLog.d(TAG, "psid: " + BasicManager.getInstance().getPsid());

        String url = BasicManager.getInstance().getApiUrl() + api;
        Map<String, String> map = new HashMap<>();
        map.put("psid", BasicManager.getInstance().getPsid());
        map.put("type", isGroup ? "1" : "0");
        map.put("id", to.split("@")[0]);

        JSONObject jsonResult = HttpClientEx.post(url, map);
        HrLog.d(TAG, "result: " + jsonResult.toString());
        return jsonResult;
    }

    public void apiUpdateProfile(Uri trimPhotoUri) throws Exception {
        String api = "/user/update-profile.api";
        HrLog.d(TAG, "try to UpdateProfile api...");

        String sc = Security.getSc();

        HrLog.d(TAG, "url: " + BasicManager.getInstance().getApiUrl() + api);
        HrLog.d(TAG, "sc: " + sc);
        HrLog.d(TAG, "psid: " + BasicManager.getInstance().getPsid());

        /**
         * ******************縮小圖檔 避免拿圖的時間過久********************
         */
        File file = new File(BaseApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), getFileNameAsTime());
        HrLog.d(TAG, "filename: " + file.getAbsolutePath());

        Bitmap bmp = ImageUtil.loadBitmap(trimPhotoUri.getPath());
        bmp = ImageUtil.scaleDown(bmp, 240, true);
//    	Bitmap bmp = ImageUtil.sampling(new File(trimPhotoUri.getPath()));
        FileOutputStream out = new FileOutputStream(file);
        if (bmp.getWidth() <= ImageUtil.MAX_LENGTH && bmp.getHeight() <= ImageUtil.MAX_LENGTH) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } else {
            bmp.compress(Bitmap.CompressFormat.JPEG, 30, out);
        }
        out.flush();
        out.close();

        HttpClient client = HttpClientHelper.getHttpClient();
        HttpPost post = new HttpPost(BasicManager.getInstance().getApiUrl() + api);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addTextBody("sc", sc);
        builder.addTextBody("psid", BasicManager.getInstance().getPsid());

        builder.addBinaryBody("photo", file, ContentType.create("image/jpeg"), file.getName());
//        builder.addPart("photo", fb);
        final HttpEntity yourEntity = builder.build();

        post.setEntity(yourEntity);
        HttpResponse response = client.execute(post);

        // Responses from the server (code and message)
        int serverResponseCode = response.getStatusLine().getStatusCode();
        String serverResponseMessage = response.getStatusLine().getReasonPhrase();

        HrLog.d(TAG, "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
        if (serverResponseCode == 200) {
            HrLog.d(TAG, "File Upload Complete.");
            String result = getContent(response);
            HrLog.d(TAG, "result: " + result);
            JSONObject jsonResult = new JSONObject(result);

            HrLog.d(TAG, "apiUpdateProfile result: " + jsonResult.toString());

            if (jsonResult.getInt("error") == 0) {
                MemberManager.getInstance().syncContactDB();
                // 發布一個廣播於更新畫面
                mContext.sendBroadcast(new Intent(IntentAction.REFLASH_UI));
            }
        }
    }

    public interface SendPhotoListener {
        void onImpressPhoto();
    }

    /**
     * *
     * 選擇圖片
     *
     * @param data      intent
     * @param to        to
     * @param tag       tag
     * @param isGroup   isGroup
     * @param messageId msg id
     * @throws FileNotFoundException
     * @throws IOException
     * @throws Exception
     */
    public void sendPhoto(Intent data, String to, String tag, boolean isGroup, String messageId, SendPhotoListener listener) throws Exception {
//        Bitmap bitmap = getBitmap(data);
        Uri selectedImage = data.getData();
        FileObject fileObject = getPath(selectedImage);
        File fileTemp = new File(fileObject.path);
        sendPhoto(fileTemp, getBitmap(data), to, tag, isGroup, messageId, listener);
    }

    /**
     * ge, transferFile); }
     * <p/>
     * /**
     * *
     * 拍照
     *
     * @param fileTemp       File
     * @param originalBitmap Bitmap
     * @param to             to
     * @param tag            tag
     * @param isGroup        is group
     * @param messageId      msgid
     * @throws FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     * @throws IOException
     * @throws Exception
     */
    public void sendPhoto(File fileTemp, Bitmap originalBitmap, String to, String tag, boolean isGroup, String messageId, SendPhotoListener listener) throws Exception {
        Message message = MessageManager.getInstance().getSingleMessage(messageId);
        TransferFile transferFile;
        if (message == null) {
            if (fileTemp == null) {
                throw new NullPointerException("fileTemp can not be null");
            }
            message = MessageManager.getInstance().createMessage(to, fileTemp.getAbsolutePath(), tag, messageId, isGroup);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss", Locale.US);
            String fileName = sdf.format(new Date(System.currentTimeMillis())) + ".jpg";
            HrLog.d(TAG, "filename: " + fileName);
            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

            FileOutputStream out = new FileOutputStream(file);
            if (originalBitmap == null) {
                originalBitmap = ImageUtil.loadBitmap(fileTemp.getAbsolutePath(), 2560);
            }

            HrLog.d(TAG, "sendPhoto original bitmap Width x Height " + originalBitmap.getWidth() + "x" + originalBitmap.getHeight());

            Bitmap waterMarkedBitmap = doShrinkAndWaterMark(originalBitmap);

            HrLog.d(TAG, "sendPhoto resized bitmap Width x Height " + waterMarkedBitmap.getWidth() + "x" + waterMarkedBitmap.getHeight());

            WeakReference<Bitmap> reference = new WeakReference<Bitmap>(waterMarkedBitmap);
            reference.get().compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            message.setBody(file.getAbsolutePath());
            reference.clear();

            transferFile = createTransferFile(file, to, messageId, file);
            updateOrInsertTransferData(transferFile);
        } else {
            transferFile = FileTransferManager.getInstance().getTransferFile(messageId);
        }

        message.setSendStatus(Message.STATUS_UPLOADING);

        MessageManager.getInstance().insertOrUpdateMessage(message);
        mContext.getContentResolver().notifyChange(ECSDataProvider.URI_GET_LAST_MESSAGE_WITHOUT_ME, null);
        for (Map.Entry<String, ECSReceiveMessageListener> entry : MessageManager.getInstance().getMessageListener().entrySet()) {
            entry.getValue().messageReceived(message);
        }
        if (listener != null) {
            listener.onImpressPhoto();
        }
        upload(to, tag, isGroup, message, transferFile);
    }

    public Bitmap doShrinkAndWaterMark(Bitmap originalBitmap) {
        Bitmap compressedBitmap = ImageUtil.shrinkBitmapMaxEdge(originalBitmap, ImageUtil.MAX_LENGTH);
        if (compressedBitmap != originalBitmap) {
            originalBitmap.recycle();
        }
        if (!mShouldWatermark) return compressedBitmap;

        Member member = MemberManager.getInstance().getSelfProfileMember();
        String displayName = "";
        if (member != null) {
            displayName = member.getDisplayName();
        }
        Bitmap waterMarkedBitmap = WaterMarker.getMarkImageFile(compressedBitmap, displayName + "\n" + DateUtils.format("yyyy/MM/dd HH:mm", new Date()));
        if (waterMarkedBitmap != compressedBitmap) {
            compressedBitmap.recycle();
        }
        return waterMarkedBitmap;
    }

    public static void setWaterMarkFlag(boolean bol) {
        mShouldWatermark = bol;
    }

    public void updateOrInsertTransferData(TransferFile transferFile) {
        String select = TransferFileTable.KEY_FILE_ID + " = ? ";
        String[] selectionArgs = new String[]{transferFile.getFileId()};
        Cursor cursor = mContext.getContentResolver().query(ECSDataProvider.URI_TRANSFER_FILE, null, select, selectionArgs, null);

        if (cursor == null || (cursor.getCount() == 0)) {
            Uri uri = mContext.getContentResolver().insert(ECSDataProvider.URI_TRANSFER_FILE, TransferFile.toContentValue(transferFile));
            boolean isertSuccessful = ContentUris.parseId(uri) > -1;
            HrLog.d(TAG, "updateOrInsertTransferData isertSuccessful " + isertSuccessful);
            DBUtil.closeCursor(cursor);
        } else {
            if (DBUtil.notEmptyCursorAndMoveToFirst(cursor)) {
                DBUtil.closeCursor(cursor);
                int affect = mContext.getContentResolver().update(ECSDataProvider.URI_TRANSFER_FILE,
                    TransferFile.toContentValue(transferFile), select, selectionArgs);
                HrLog.d(TAG, "updateOrInsertTransferData update affect " + affect);
            }
        }
    }

    public TransferFile getTransferFile(String messageId) {
        String select = TransferFileTable.KEY_FILE_ID + " = ? ";
        String[] selectionArgs = new String[]{messageId};
        Cursor cursor = mContext.getContentResolver().query(ECSDataProvider.URI_TRANSFER_FILE, null, select,
            selectionArgs, null);
        TransferFile file = null;
        if (DBUtil.notEmptyCursorAndMoveToFirst(cursor)) {
            file = TransferFile.toCurrentObject(cursor);
        }
        DBUtil.closeCursor(cursor);
        return file;
    }

    public boolean deleteTransferFile(String messageId) {
        String select = TransferFileTable.KEY_FILE_ID + " = ? ";
        String[] selectionArgs = new String[]{messageId};
        return mContext.getContentResolver().delete(ECSDataProvider.URI_TRANSFER_FILE, select, selectionArgs) > 0;
    }

    private TransferFile createTransferFile(File file, String to, String messageId, File uploadFile) {
        TransferFile transferFile = new TransferFile();
        FileBody fb = new FileBody(file);
        transferFile.setFileId(messageId);
        transferFile.setFileLength((int) fb.getContentLength());
        transferFile.setComplete(false);
        transferFile.setFaild(true);
        transferFile.setMessageFrom(AccountManager.getInstance().getEmail());
        transferFile.setMessageTo(to);
        transferFile.setPath(uploadFile.getAbsolutePath());
        transferFile.setRoomId(to);
        transferFile.setFileUrl("");
        transferFile.setWriteEnd(0);
        return transferFile;
    }

    /**
     * @param transferFile transfer file
     * @param to           to
     * @param tag          tag
     * @param isGroup      is group
     * @throws IOException
     * @throws UnsupportedEncodingException
     * @throws Exception
     * @deprecated use upload
     */
    public void reSendPhoto(TransferFile transferFile, String to, String tag, boolean isGroup) throws Exception {
        HrLog.d(TAG, "reSendPhoto..... ");
        // 這裡需要檢查db的狀況，在重新requestUrl的時候似乎有問題
        HrLog.d(TAG, "reSendPhoto transferFile.isComplete " + transferFile.isComplete());
        if (!transferFile.isComplete()) {
            Message message = MessageManager.getInstance().getSingleMessage(transferFile.getFileId());
            message.setSendStatus(Message.STATUS_UPLOADING);
            MessageManager.getInstance().updateMessage(message);
            File file = new File(transferFile.getPath());
            HrLog.d(TAG, "reUpload File transferFile getPath " + transferFile.getPath());
            HrLog.d(TAG, "reUpload File transferFile getFileUrl " + transferFile.getFileUrl());

            if (TextUtils.isEmpty(transferFile.getFileUrl())) {
                JSONObject jsonRequest = apiRequestUploadUrl(to, isGroup);
                if (jsonRequest.getInt("error") == 0) {
                    transferFile.setWriteEnd(0);
                    transferFile.setFileUrl(jsonRequest.getString(Const.URL));
                    HrLog.d(TAG, "reUpload File transferFile new fileUrl " + transferFile.getFileUrl());
                } else {
                    return;
                }
            }

            HrLog.d(TAG, "reUpload File transferFile file.length " + file.length());
            HrLog.d(TAG, "reUpload entering update ");
            HrLog.d(TAG, "reUpload entering update getWriteEnd " + transferFile.getWriteEnd());

            HttpClient client = HttpClientHelper.createMyHttpClient();
            HttpPost post = new HttpPost(transferFile.getFileUrl());

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addTextBody("sc", Security.getSc());
            builder.addTextBody("psid", BasicManager.getInstance().getPsid());
            builder.addBinaryBody("file", file, ContentType.create("image/jpeg"), getFileNameAsTime());

            final HttpEntity entity = builder.build();
            ProgressiveEntity progressiveEntity = new ProgressiveEntity(entity, transferFile);
            post.addHeader("Content-Range",
                "bytes " + transferFile.getWriteEnd() + "-" + file.length() + "/" + file.length());
            post.setEntity(progressiveEntity);

            HttpResponse response = client.execute(post);

            // Responses from the server (code and message)
            int serverResponseCode = response.getStatusLine().getStatusCode();
            String serverResponseMessage = response.getStatusLine().getReasonPhrase();
            HrLog.d(TAG, "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

            if (serverResponseCode == HttpStatus.SC_OK || serverResponseCode == HttpStatus.SC_ACCEPTED) {
                String result = getContent(response);
                HrLog.d(TAG, "reUpload serverResponseCode result " + result);
                Message messageFromDB = MessageManager.getInstance().getSingleMessage(message.getMessageId());
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getInt("error") == 0 && messageFromDB.getSendStatus() != Message.STATUS_FAILED) {
                    transferFile.setComplete(true);
                    updateOrInsertTransferData(transferFile);
                    // 刷新db完成後，移除目前進行的tf
                    // 發布一個廣播於更新上傳列表
                    mContext.sendBroadcast(new Intent(IntentAction.SEND_FILE));

                    // 全部完成後發送訊息給對方
                    String url = jsonResult.getString(Const.URL);
                    message.setBody(url);
                    MessageManager.getInstance().updateMessage(message);

                }
            }
            if (transferFile.isComplete()) {
                XmppSdkAdapterLevel1.getInstance().sendMessage(message);
                if (file.exists()) {
                    file.delete();
                }
//                deleteTransferFile(transferFile.getFileId());
            }
        }
    }

    private String getFileNameAsTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    /**
     * @param to      to
     * @param tag     tag
     * @param isGroup isgroup
     * @return MessageID
     * @throws IOException
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    private void upload(String to, String tag, boolean isGroup, Message message, TransferFile transferFile) throws Exception {
//        updateOrInsertTransferData(transferFile);
        HrLog.d(TAG, "upload...");
        HrLog.d(TAG, "upload transferFile.isComplete " + transferFile.isComplete());
        if (!transferFile.isComplete()) {
            JSONObject jsonRequest = apiRequestUploadUrl(to, isGroup);
            if (jsonRequest.getInt("error") == 0) {
                HrLog.d(TAG, "Send file to user / chatroom...");

                String uploadUrl = jsonRequest.getString("url");
                File file = new File(transferFile.getPath());
                FileBody fb = new FileBody(file);
                HrLog.d(TAG, "file size: " + (int) fb.getContentLength());

                transferFile.setFileUrl(uploadUrl);
                updateOrInsertTransferData(transferFile);

                HttpClient client = HttpClientHelper.createMyHttpClient();
                HttpPost post = new HttpPost(uploadUrl);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                String sc = Security.getSc();

                HrLog.d(TAG, "url: " + uploadUrl);
                HrLog.d(TAG, "sc: " + sc);
                HrLog.d(TAG, "psid: " + BasicManager.getInstance().getPsid());
                builder.addTextBody("sc", sc);
                builder.addTextBody("psid", BasicManager.getInstance().getPsid());

//            builder.addPart("file", fb);
                HrLog.d(TAG, "Send file getPath " + file.getAbsolutePath());

                builder.addBinaryBody("file", file, ContentType.create("image/jpeg"), getFileNameAsTime());
                final HttpEntity yourEntity = builder.build();
                ProgressiveEntity progressiveEntity = new ProgressiveEntity(yourEntity, transferFile);

                post.setEntity(progressiveEntity);
                HttpResponse response = client.execute(post);

                // Responses from the server (code and message)
                int responseCode = response.getStatusLine().getStatusCode();
                String responseMessage = response.getStatusLine().getReasonPhrase();
                HrLog.d(TAG, "HTTP Response is : " + responseMessage + ": " + responseCode);

                if (responseCode == HttpStatus.SC_OK || responseCode == HttpStatus.SC_ACCEPTED) {
                    HrLog.d(TAG, "File Upload Complete.");
                    String result = getContent(response);
                    HrLog.d(TAG, "result: " + result);
                    Message messageFromDB = MessageManager.getInstance().getSingleMessage(message.getMessageId());
                    JSONObject jsonResult = new JSONObject(result);
                    if (jsonResult.getInt("error") == 0 && messageFromDB.getSendStatus() != Message.STATUS_FAILED) {
                        transferFile.setComplete(true);
                        updateOrInsertTransferData(transferFile);

                        TransferFile transferFileTemp = getTransferFile(message.getMessageId());
                        if (transferFileTemp != null) {
                            HrLog.d(TAG, "File Upload transferFileTemp isComplete " + transferFileTemp.isComplete());
                            HrLog.d(TAG, "File Upload transferFileTemp getFileUrl " + transferFileTemp.getFileUrl());
                            HrLog.d(TAG, "File Upload transferFileTemp getFileId " + transferFileTemp.getFileId());
                        }

                        String messagePhotoUrl = jsonResult.getString(Const.URL);
                        HrLog.d(TAG, "we try to find bitmap from key: " + messagePhotoUrl);
                        Display display = ((WindowManager) BaseApplication.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                        int cacheWidth = (int) (display.getWidth() / 2.4f);

                        VolleySingleton.getInstance().addBitmapToCache(messagePhotoUrl,
                            new BitmapDrawable(BaseApplication.getContext().getResources(), BitmapFactory.decodeFile(file.getAbsolutePath())));

                        message.setBody(messagePhotoUrl);
                        MessageManager.getInstance().updateMessage(message);
                        for (Map.Entry<String, ECSReceiveMessageListener> entry : MessageManager.getInstance().getMessageListener().entrySet()) {
                            entry.getValue().messageReceived(message);
                        }
                        mContext.sendBroadcast(new Intent(IntentAction.SEND_FILE));
                        if (file.exists()) {
                            file.delete();
                        }
                        // 刷新db完成後，移除目前進行的tf
                        // 發布一個廣播於更新上傳列表
                        // 全部完成後發送訊息給對方
                        XmppSdkAdapterLevel1.getInstance().sendMessage(message);
//						deleteTransferFile(transferFile.getFileId());
                    }
                }
            } else {
                message.setSendStatus(Message.STATUS_FAILED);
                for (Map.Entry<String, ECSReceiveMessageListener> entry : MessageManager.getInstance().getMessageListener().entrySet()) {
                    entry.getValue().messageReceived(message);
                }
                throw new Exception(String.valueOf(jsonRequest.getInt("error")));
            }
        } else {
            XmppSdkAdapterLevel1.getInstance().sendMessage(message);
        }
    }

    public void sendVoice(File file, String to, String tag, boolean isGroup, String messageId, long lastMessagetime) throws Exception {
        sendVoice(file, to, tag, isGroup, messageId, lastMessagetime, null);
    }

    /**
     * ge, transferFile); }
     * <p/>
     * /**
     * *
     * 拍照
     *
     * @param to        to
     * @param tag       tag
     * @param isGroup   is group
     * @param messageId msg id
     * @throws FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     * @throws IOException
     * @throws Exception
     */
    public void sendVoice(File file, String to, String tag, boolean isGroup, String messageId, long lastMessagetime, SendPhotoListener listener) throws Exception {
        Message message = MessageManager.getInstance().getSingleMessage(messageId);
        TransferFile transferFile;
        if (message == null) {
            message = MessageManager.getInstance().createMessage(to, file.getAbsolutePath(), tag, messageId, isGroup);
            message.setBody(file.getAbsolutePath());
            transferFile = createTransferFile(file, to, messageId, file);
            updateOrInsertTransferData(transferFile);
        } else {
            transferFile = FileTransferManager.getInstance().getTransferFile(messageId);
        }
        message.setSendStatus(Message.STATUS_UPLOADING);
        message.setTimeStamp(String.valueOf(lastMessagetime + 500));
        MessageManager.getInstance().insertOrUpdateMessage(message);

        Voice voice = new Voice();
        voice.setComplete(true);
        voice.setMessageId(message.getMessageId());
        voice.setPath(file.getAbsolutePath());
        voice.setRoomId(message.getRoomId());
        voice.setFileLength(new File(voice.getPath()).length());

        MediaPlayer player = MediaPlayer.create(BaseApplication.getContext(), Uri.parse(voice.getPath()));
        HrLog.d(TAG, " processDownloadVoice getDuration " + player.getDuration());
        voice.setPlayLength(player.getDuration());
        VoiceManager.getInstance().updateOrInsertVoice(voice);

        mContext.getContentResolver().notifyChange(ECSDataProvider.URI_GET_LAST_MESSAGE_WITHOUT_ME, null);
        for (Map.Entry<String, ECSReceiveMessageListener> entry : MessageManager.getInstance().getMessageListener().entrySet()) {
            entry.getValue().messageReceived(message);
        }
        if (listener != null) {
            listener.onImpressPhoto();
        }
        try {
            uploadVoice(to, tag, isGroup, message, transferFile);
        } catch (Exception ex) {
            HrLog.e(TAG, ex.toString(), ex);
            message.setSendStatus(Message.STATUS_FAILED);
            MessageManager.getInstance().updateMessage(message);
            for (Map.Entry<String, ECSReceiveMessageListener> entry : MessageManager.getInstance().getMessageListener()
                .entrySet()) {
                entry.getValue().messageReceived(message);
            }
            throw new Exception();
        }
    }

    private void uploadVoice(String to, String tag, boolean isGroup, Message message, TransferFile transferFile) throws IOException, UnsupportedEncodingException, Exception {
        // updateOrInsertTransferData(transferFile);
        HrLog.d(TAG, "upload...");
        HrLog.d(TAG, "upload transferFile.isComplete " + transferFile.isComplete());
        if (!transferFile.isComplete()) {
            JSONObject jsonRequest = apiRequestUploadUrl(to, isGroup);
            if (jsonRequest.getInt("error") == 0) {
                HrLog.d(TAG, "Send file to user / chatroom...");

                String uploadUrl = jsonRequest.getString("url");
                File file = new File(transferFile.getPath());
                FileBody fb = new FileBody(file);
                HrLog.d(TAG, "file size: " + (int) fb.getContentLength());

                transferFile.setFileUrl(uploadUrl);
                updateOrInsertTransferData(transferFile);

                HttpClient client = HttpClientHelper.createMyHttpClient();
                HttpPost post = new HttpPost(uploadUrl);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                String sc = Security.getSc();

                HrLog.d(TAG, "url: " + uploadUrl);
                HrLog.d(TAG, "sc: " + sc);
                HrLog.d(TAG, "psid: " + BasicManager.getInstance().getPsid());
                builder.addTextBody("sc", sc);
                builder.addTextBody("psid", BasicManager.getInstance().getPsid());

                // builder.addPart("file", fb);
                HrLog.d(TAG, "Send file getPath " + file.getAbsolutePath());

                builder.addBinaryBody("file", file, ContentType.create("audio"), getFileNameAsTime());
                final HttpEntity yourEntity = builder.build();
                ProgressiveEntity progressiveEntity = new ProgressiveEntity(yourEntity, transferFile);

                post.setEntity(progressiveEntity);
                HttpResponse response = client.execute(post);

                // Responses from the server (code and message)
                int responseCode = response.getStatusLine().getStatusCode();
                String responseMessage = response.getStatusLine().getReasonPhrase();
                HrLog.d(TAG, "HTTP Response is : " + responseMessage + ": " + responseCode);

                if (responseCode == HttpStatus.SC_OK || responseCode == HttpStatus.SC_ACCEPTED) {
                    HrLog.d(TAG, "File Upload Complete.");
                    String result = getContent(response);
                    HrLog.d(TAG, "result: " + result);
                    Message messageFromDB = MessageManager.getInstance().getSingleMessage(message.getMessageId());
                    JSONObject jsonResult = new JSONObject(result);
                    if (jsonResult.getInt("error") == 0 && messageFromDB.getSendStatus() != Message.STATUS_FAILED) {
                        transferFile.setComplete(true);
                        updateOrInsertTransferData(transferFile);

                        TransferFile transferFileTemp = getTransferFile(message.getMessageId());
                        if (transferFileTemp != null) {
                            HrLog.d(TAG, "File Upload transferFileTemp isComplete " + transferFileTemp.isComplete());
                            HrLog.d(TAG, "File Upload transferFileTemp getFileUrl " + transferFileTemp.getFileUrl());
                            HrLog.d(TAG, "File Upload transferFileTemp getFileId " + transferFileTemp.getFileId());
                        }

                        message.setBody(jsonResult.getString(Const.URL));
                        MessageManager.getInstance().updateMessage(message);

                        for (Map.Entry<String, ECSReceiveMessageListener> entry : MessageManager.getInstance().getMessageListener().entrySet()) {
                            entry.getValue().messageReceived(message);
                        }
                        mContext.sendBroadcast(new Intent(IntentAction.SEND_FILE));

                        // 刷新db完成後，移除目前進行的tf
                        // 發布一個廣播於更新上傳列表
                        // 全部完成後發送訊息給對方
                        XmppSdkAdapterLevel1.getInstance().sendMessage(message);
                        // deleteTransferFile(transferFile.getFileId());

                    }
                }
            } else {
                message.setSendStatus(Message.STATUS_FAILED);
                for (Map.Entry<String, ECSReceiveMessageListener> entry : MessageManager.getInstance().getMessageListener().entrySet()) {
                    entry.getValue().messageReceived(message);
                }
                throw new Exception(String.valueOf(jsonRequest.getInt("error")));
            }
        } else {
            XmppSdkAdapterLevel1.getInstance().sendMessage(message);
        }
    }


    private Bitmap getBitmap(Intent data) {
        Bitmap photo = null;
        Uri selectedImage = data.getData();

        FileObject fileObject = getPath(selectedImage);
        HrLog.d(TAG, "path: " + fileObject.path);
        HrLog.d(TAG, "rotate: " + fileObject.rotate);
        try {
            photo = ImageUtil.loadBitmap(fileObject.path, 2560);
            int orientation = fileObject.rotate;
            int rotate = 0;
            switch (orientation) {
                case 90:
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case 180:
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case 270:
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                default:
            }
            if (rotate > 0) {
                Matrix mtx = new Matrix();
                mtx.postRotate(rotate);
                photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), mtx, false);
            }
        } catch (Exception ex) {
            HrLog.e(TAG, ex.toString(), ex);
        }

        return photo;
    }

    /**
     * @param uri uri
     * @return file object
     */
    public FileObject getPath(Uri uri) {
        FileObject fileObject = new FileObject();

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && getPathByNewApi(uri, fileObject)) {
            return fileObject;
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                fileObject.path = uri.getLastPathSegment();
                return fileObject;
            }

            getDataColumn(mContext, uri, null, null, fileObject);
            return fileObject;
        } // F
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            fileObject.path = uri.getPath();
            return fileObject;
        }

        return fileObject;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean getPathByNewApi(Uri uri, FileObject fileObject) {
        if (!DocumentsContract.isDocumentUri(mContext, uri)) return false;

        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                fileObject.path = Environment.getExternalStorageDirectory() + "/" + split[1];
                return true;
            }

            // TODO handle non-primary volumes
        } // DownloadsProvider
        else if (isDownloadsDocument(uri)) {

            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            getDataColumn(mContext, contentUri, null, null, fileObject);
            return true;
        } // MediaProvider
        else if (isMediaDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{
                split[1]
            };

            getDataColumn(mContext, contentUri, selection, selectionArgs, fileObject);
            return true;
        }
        return false;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs, FileObject fileObject) {

        Cursor cursor = null;
        final String column = "_data";

        try {
            cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION}, selection, selectionArgs,
                null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                fileObject.path = cursor.getString(index);
                fileObject.rotate = cursor.getInt(1);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private String getContent(HttpResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        String readLine;
        String encoding = "UTF-8";
        BufferedReader responseReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), encoding));
        while ((readLine = responseReader.readLine()) != null) {
            sb.append(readLine).append("\n");
        }
        responseReader.close();
        return sb.toString().trim();
    }

    public static class FileObject {

        public String path;
        public int rotate;

        public FileObject() {
        }
    }
}
