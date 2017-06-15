/*
 * Copyright (C) 2016 Elmar Rhex Gomez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadRequest<T> extends Request<T> {
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final byte[] DEFAULT_BYTES = new byte[1024];

    private final byte[] mBytes;
    private final Response.Listener<T> mListener;
    private final File mImageFile;

    public UploadRequest(File imageFile, String url, Listener<T> success, ErrorListener error) {
        this(DEFAULT_BYTES, imageFile, Method.POST, url, success, error);
    }

    public UploadRequest(byte[] bytes, File imageFile, int method, String url, Listener<T> success,
                         ErrorListener error) {
        super(method, url, error);
        mBytes = bytes;
        mListener = success;
        mImageFile = imageFile;
    }

    @Override
    public String getBodyContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", getBodyContentType());
        return map;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        FileInputStream is = null;
        try {
            is = new FileInputStream(mImageFile);

            int nRead;
            while ((nRead = is.read(mBytes, 0, mBytes.length)) != -1) {
                buffer.write(mBytes, 0, nRead);
            }

            return buffer.toByteArray();
        } catch (FileNotFoundException e) {
            throw new AuthFailureError("Volley File not found error: " + e.getMessage());
        } catch (IOException e) {
            throw new AuthFailureError("Volley IO error: " + e.getMessage());
        } finally {
            try {
                buffer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        T result = null;
        return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
}