import android.webkit.DownloadListener;
import android.webkit.URLUtil;

public class MyDownloadListener implements DownloadListener
{
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
    {
        String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

        ((DownloadManager) tantraView.context().getSystemService(DOWNLOAD_SERVICE)).enqueue(request);
        tantraView.showToastMsg("Start to download " + filename + "...", Toast.LENGTH_SHORT);
    }
}
