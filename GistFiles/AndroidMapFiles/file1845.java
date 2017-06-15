public String commonUpload(Context cxt, String url, Map<String, String> params,
                               List<UploadFile> uploadFiles, Map<String, String> properties, String encoding) throws IOException {
        // 定义POST体中分割线
        String BOUNDARY = "124324471239807512395795";
        String PREFIX = "--";
        String ENDLINE = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";
// long contentLen = 0;
        // 1. 拼接HTTP 请求头
        HttpURLConnection httpConn = NetworkUtils.openHttpURLConnection(cxt, url);
        httpConn.setConnectTimeout(mConnTimeout);
        httpConn.setReadTimeout(mReadTimeout);
        httpConn.setDoInput(true);
        httpConn.setDoOutput(true);
        httpConn.setUseCaches(false);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Accept-Encoding", "gzip,deflate");
        httpConn.setRequestProperty("Charset", encoding);
// httpConn.setRequestProperty("Content-Encoding", "gzip");
        httpConn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

        if (properties != null && properties.size() > 0) {
            for (Entry<String, String> property : properties.entrySet()) {
                httpConn.setRequestProperty(property.getKey(), property.getValue());
            }
        }

        DataOutputStream out = new DataOutputStream(httpConn.getOutputStream());

        // 2. 拼接HTTP POST请求体
        // 首先拼接键值对
        StringBuilder nameValue = new StringBuilder();
        if (params != null && params.size() > 0) {
            for (Entry<String, String> param : params.entrySet()) {
                nameValue.append(PREFIX)
                        .append(BOUNDARY)
                        .append(ENDLINE)
                        .append("Content-Disposition: form-data; name=\"")
                        .append(param.getKey())
                        .append("\"")
                        .append(ENDLINE)
                        .append(ENDLINE)
                        .append(param.getValue())
                        .append(ENDLINE);
            }
        }
// contentLen += nameValue.length();
        out.write(nameValue.toString().getBytes());
        out.flush();
        // 然后拼接文件
        if (uploadFiles != null && uploadFiles.size() > 0) {
            for (UploadFile uploadFile : uploadFiles) {
                StringBuilder temp = new StringBuilder();
                temp.append(PREFIX)
                        .append(BOUNDARY)
                        .append(ENDLINE)
                        .append("Content-Disposition: form-data;name=\"")
                        .append(uploadFile.getParameterName())
                        .append("\";filename=\"")
                        .append(uploadFile.getFileName())
                        .append("\"")
                        .append(ENDLINE)
                        .append("Content-Type: ")
                        .append(uploadFile.getContentType())
                        .append(ENDLINE)
                        .append(ENDLINE);
// contentLen += temp.length();
                out.write(temp.toString().getBytes());
                File data = uploadFile.getFile();
                if (data != null && data.exists()) {
                    InputStream in = null;
                    BufferedOutputStream bufout = null;
                    try {
                        in = new BufferedInputStream(new FileInputStream(data));
                        bufout = new BufferedOutputStream(out);
// contentLen += data.length();
                        byte[] buf = new byte[1024];
                        int len = 0;
                        while ((len = in.read(buf)) != -1) {
                            bufout.write(buf, 0, len);
                        }
                        bufout.flush();
                    } finally {
                        FileHelper.close(in);
                    }
                } else if (uploadFile.getData() != null) {
// contentLen += uploadFile.getData().length;
                    out.write(uploadFile.getData());
                    out.flush();
                }
// contentLen += ENDLINE.length();
                out.write(ENDLINE.getBytes());
                out.flush();
            }
        }
        // 最后一条结束线
        StringBuilder endLine = new StringBuilder();
        endLine.append(PREFIX)
                .append(BOUNDARY)
                .append(PREFIX)
                .append(ENDLINE);
// contentLen += endLine.length();
        out.write(endLine.toString().getBytes());
// httpConn.setRequestProperty("Content-Length: ", String.valueOf(contentLen));
        out.flush();
        try {
            return getResponseHandleStatusCode(httpConn);
        } catch (Exception e) {
            if (DEBUG) {
                LibLogger.d(TAG, "commonUpload getResponseHandleStatusCode exp");
            }
            throw new IOException(e.toString());
        } finally {
            FileHelper.close(out);
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
    }