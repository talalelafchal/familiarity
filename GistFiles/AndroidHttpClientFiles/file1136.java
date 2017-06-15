// http://stackoverflow.com/questions/601503/how-do-i-obtain-crash-data-from-my-android-application
// 단 android 23 이상이면 runtime permission 이 등록이 되어 있어야 한다. 

private void testForceCrash() {
	throw new RuntimeException("This is a crash");
}

public void testCrashListener() {
    if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(
                "/sdcard/Download", "http://<desired_url>/upload.php"));
    }
}

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultUEH;
    private String localPath;
    private String url;

    /*
     * if any of the parameters is null, the respective functionality
     * will not be used
     */
    public CustomExceptionHandler(String localPath, String url) {
        this.localPath = localPath;
        this.url = url;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
//            String timestamp = TimestampFormatter.getInstance().getTimestamp();
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = "errorlog.stacktrace";

        if (localPath != null) {
            writeToFile(stacktrace, filename);
        }
//            if (url != null) {
//                sendToServer(stacktrace, filename);
//            }

        defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String stacktrace, String filename) {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(
                    localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//        private void sendToServer(String stacktrace, String filename) {
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//            HttpPost httpPost = new HttpPost(url);
//            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//            nvps.add(new BasicNameValuePair("filename", filename));
//            nvps.add(new BasicNameValuePair("stacktrace", stacktrace));
//            try {
//                httpPost.setEntity(
//                        new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
//                httpClient.execute(httpPost);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
}