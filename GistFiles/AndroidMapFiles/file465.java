public class AVUpdateUtils {

    public static final long RESULT_OK = 0L;
    public static final long RESULT_CANCEL = 1L;
    public static final long RESULT_INSTALLING = 2L;

    private static long downloadInBackground(Context context, String url, File file) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        request.setMimeType("application/vnd.android.package-archive");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationUri(Uri.fromFile(file));
        return ((DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
    }

    private static long downloadAndInstall(Context context, String url, File file, String versionName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setMimeType("application/vnd.android.package-archive");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationUri(Uri.fromFile(file));
        request.setTitle(context.getString(R.string.app_name) + " " + versionName);
        final long requestId = ((DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
        context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (requestId == reference) {
                    installPackage(context, file);
                }
            }
        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        Toast.makeText(context, R.string.message_update_download_started, Toast.LENGTH_SHORT).show();
        return requestId;
    }

    private static void installPackage(Context context, File file) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

    private static int checkDownloadState(Context context, long requestId, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(requestId);
        query.setFilterByStatus(DownloadManager.STATUS_FAILED
                | DownloadManager.STATUS_PAUSED
                | DownloadManager.STATUS_SUCCESSFUL
                | DownloadManager.STATUS_RUNNING
                | DownloadManager.STATUS_PENDING);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            if (url.equals(c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI)))) {
                return c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        }
        return 0;
    }

    private static final Object configLock = new Object();

    static {
        AVAnalytics.setOnlineConfigureListener(jsonObject -> {
            synchronized (configLock) {
                configLock.notify();
            }
        });
    }

    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    public static Observable<Long> checkUpdate(Context context) {
        return Observable
                .fromCallable(() -> {
                    AVAnalytics.updateOnlineConfig(context);
                    synchronized (configLock) {
                        // Wait for online config parameters.
                        configLock.wait(5000);
                    }
                    return 0L;
                })
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(ignored -> {

                    // Remove downloaded package of current installed application
                    getPackageFile(context, BuildConfig.VERSION_CODE).delete();

                    // Parse update information from AVAnalytics
                    AVUpdateInfo info = AVUpdateInfo.getInstance(context);

                    if (info.versionCode <= BuildConfig.VERSION_CODE) {
                        // Already up to date just ok to continue
                        return Observable.just(RESULT_OK);
                    }

                    // Check download status
                    int downloadStatus = checkDownloadState(context, getLastDownloadRequest(context), info.binaryUrl);

                    File packageFile = getPackageFile(context, info.versionCode);

                    // Update information dialog and subject
                    AsyncSubject<Long> subject = AsyncSubject.create();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.title_version_update, info.versionName));
                    builder.setMessage(new MarkupFormatter(context).format(info.changeLog));
                    builder.setOnCancelListener(dialog -> {
                        if (info.minimumCode <= BuildConfig.VERSION_CODE) {
                            // Ok to continue when reject a non-mandatory update
                            subject.onNext(RESULT_OK);
                        } else if (downloadStatus == DownloadManager.STATUS_RUNNING || downloadStatus == DownloadManager.STATUS_PENDING) {
                            // Waiting for an ongoing download of mandatory update
                            subject.onNext(RESULT_INSTALLING);
                        } else {
                            // User rejects a mandatory update
                            subject.onNext(RESULT_CANCEL);
                        }
                        subject.onCompleted();
                    });

                    if (info.minimumCode > BuildConfig.VERSION_CODE) {
                        // If there be a mandatory update, check download status immediately
                        switch (downloadStatus) {
                            case DownloadManager.STATUS_RUNNING:
                            case DownloadManager.STATUS_PENDING:
                                Toast.makeText(context, R.string.message_update_download_already_started, Toast.LENGTH_SHORT).show();
                                return Observable.just(RESULT_INSTALLING);
                            case DownloadManager.STATUS_SUCCESSFUL:
                                if (packageFile.exists()) {
                                    // Show update dialog to open install
                                    builder.setPositiveButton(R.string.action_update, (dialog, i) -> {
                                        installPackage(context, packageFile);
                                        subject.onNext(RESULT_INSTALLING);
                                        subject.onCompleted();
                                    }).show();
                                    return subject;
                                }
                            default:
                                // Show update dialog to start download
                                builder.setPositiveButton(R.string.action_update, (dialog, i) -> {
                                    packageFile.delete();
                                    setLastDownloadRequest(context,
                                            downloadAndInstall(context, info.binaryUrl, packageFile, info.versionName));
                                    subject.onNext(RESULT_INSTALLING);
                                    subject.onCompleted();
                                }).show();
                                return subject;
                        }
                    } else {
                        // If the update is not mandatory, checks whether the update package is downloaded
                        if (packageFile.exists() && downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                            // Package file is downloaded and ready for install
                            builder.setPositiveButton(R.string.action_update, (dialog, i) -> {
                                installPackage(context, packageFile);
                                subject.onNext(RESULT_INSTALLING);
                                subject.onCompleted();
                            }).show();
                            return subject;
                        } else {
                            // Package is downloading, otherwise start a background download request
                            switch (downloadStatus) {
                                case DownloadManager.STATUS_RUNNING:
                                case DownloadManager.STATUS_PENDING:
                                    return Observable.just(RESULT_OK);
                                default:
                                    packageFile.delete();
                                    setLastDownloadRequest(context,
                                            downloadInBackground(context, info.binaryUrl, packageFile));
                                    return Observable.just(RESULT_OK);
                            }
                        }
                    }
                });
    }

    private static File getPackageFile(Context context, int versionCode) {
        return new File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                BuildConfig.APPLICATION_ID + "-" + versionCode + ".apk");
    }

    private static long getLastDownloadRequest(Context context) {
        return context.getSharedPreferences("__update", Context.MODE_PRIVATE).getLong("requestId", 0L);
    }

    @SuppressLint("CommitPrefEdits")
    private static void setLastDownloadRequest(Context context, long requestId) {
        context.getSharedPreferences("__update", Context.MODE_PRIVATE).edit().putLong("requestId", requestId).commit();
    }

    private static class AVUpdateInfo {
        int versionCode;
        int minimumCode;
        String versionName;
        String changeLog;
        String binaryUrl;

        public static AVUpdateInfo getInstance(Context context) {
            AVUpdateInfo info = new AVUpdateInfo();
            info.versionCode = Integer.valueOf(AVAnalytics.getConfigParams(context, "ANDROID_LATEST_VERSION_CODE"));
            info.minimumCode = Integer.valueOf(AVAnalytics.getConfigParams(context, "ANDROID_MINIMUM_VERSION_CODE"));
            info.versionName = AVAnalytics.getConfigParams(context, "ANDROID_LATEST_VERSION_NAME");
            info.changeLog = AVAnalytics.getConfigParams(context, "ANDROID_LATEST_CHANGELOG");
            info.binaryUrl = AVAnalytics.getConfigParams(context, "ANDROID_LATEST_BINARY_URL");
            return info;
        }
    }
}