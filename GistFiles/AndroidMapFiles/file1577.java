package com.trailbehind.downloads;

import android.util.Log;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.crashlytics.android.Crashlytics;
import com.trailbehind.MapApplication;
import com.trailbehind.R;
import com.trailbehind.stats.LongBuffer;
import com.trailbehind.util.ApplicationConstants;
import com.trailbehind.util.Connectivity;
import com.trailbehind.util.IOUtils;
import com.trailbehind.util.SntpClient;

import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Download status for AWS s3 transfers
 */
public class S3DownloadStatus extends DownloadStatus {
  String bucket;
  String key;
  File destination;
  File downloadDestination;
  S3DownloadObserver observer;
  String originalDescription = null;
  ObjectMetadata mObjectMetadata;
  private AmazonS3Client mAmazonS3Client;
  private boolean cancelRequested = false;

  int timeOffset = 0;
  int retryCount = 0;
  int maxRetryCount = 3;

  HashMap<String, DownloadStatus> currentDownloads;

  public void start() {
    java.util.logging.Logger.getLogger("com.amazonaws").setLevel(java.util.logging.Level.FINEST);

    Log.d(ApplicationConstants.TAG, "S3DownloadStatus start() " + uri);
    cancelRequested = false;
    setSizeReceived(0);

    if(downloadDestination == null) {
      downloadDestination = new File(destination.getParent(), destination.getName() + ".download");
    }

    if(downloadDestination.exists()) {
        setSizeReceived(downloadDestination.length());
        resume();
    } else {
      startDownloadForRequest(null);
    }
  }


  public void startDownloadForRequest(GetObjectRequest getObjectRequest) {
    Log.d(ApplicationConstants.TAG, "S3DownloadStatus startDownloadForRequest");
    final S3DownloadStatus downloadStatus = this;

    if(!Connectivity.internetAvailable()) {
      description = MapApplication.mainApplication.getString(R.string.no_internet_connection);
      setStatus(PAUSED);
      MapApplication.mainApplication.getDownloadStatusController().downloadUpdated(this);
      return;
    }

    if(getObjectRequest == null) {
      getObjectRequest = new GetObjectRequest(this.bucket, this.key);
    }

    final GetObjectRequest downloadGetObjectRequest = getObjectRequest;

    new Thread(new Runnable() {
      @Override
      public void run() {
        InputStream reader = null;
        OutputStream writer = null;
        try {
          if(timeOffset != 0 && timeOffset != -1) {
            Log.d(ApplicationConstants.TAG, "Using time offset:" + timeOffset);
            mAmazonS3Client.setTimeOffset(timeOffset);
          }

          //get metadata so we know file size
          if(mObjectMetadata == null) {
            fetchMetadata();
            if(mObjectMetadata == null) {
              throw new Exception("Error fetching metadata");
            }
          }

          downloadStatus.setTotalSize(mObjectMetadata.getContentLength());

          //we've got the metadata, assume download will really start
          if(observer != null) {
            observer.downloadStarted();
          }
          downloadStatus.setStatus(DownloadStatus.DOWNLOADING);

          S3Object s3Object = mAmazonS3Client.getObject(downloadGetObjectRequest);
          reader = s3Object.getObjectContent();

          if(downloadGetObjectRequest.getRange() != null) {
            if(downloadDestination.exists()) {
              Log.d(ApplicationConstants.TAG, "Appending to existing file: " +
                  downloadDestination.getAbsolutePath());
              downloadStatus.setSizeReceived(downloadDestination.length());
              writer = new BufferedOutputStream(new FileOutputStream(downloadDestination, true));
            } else {
              throw new Exception("download range was set, but file does not exist");
            }
          } else {
            writer = new BufferedOutputStream(new FileOutputStream(downloadDestination));
          }

          //speed calculation
          LongBuffer transferSpeedBuffer = new LongBuffer(5);
          long lastTime = System.currentTimeMillis();
          long bytesSinceLastSpeedCalculation = 0;

          //read loop
          int bufferSize = 1024 * 64;//read 64k at a time
          byte[] buffer = new byte[bufferSize];
          int readLength;
          while((readLength = reader.read(buffer)) != -1 && !cancelRequested) {
            downloadStatus.setSizeReceived(downloadStatus.getSizeReceived() + readLength);
            writer.write(buffer, 0, readLength);
            //calculate download speed
            bytesSinceLastSpeedCalculation += readLength;
            if(System.currentTimeMillis() - lastTime > 5000) {
              transferSpeedBuffer.setNext((long) (bytesSinceLastSpeedCalculation /
                  ((System.currentTimeMillis() - lastTime) / 1000.0)));
              downloadStatus.setCurrentSpeed(transferSpeedBuffer.getAverage());
              lastTime = System.currentTimeMillis();
              bytesSinceLastSpeedCalculation = 0;
              MapApplication.mainApplication.getDownloadStatusController().downloadUpdated(downloadStatus);
            }
          }
          if(cancelRequested) {
            s3Object.getObjectContent().abort();
          }
          reader.close();
          writer.flush();
          writer.close();
          writer = null;
          reader = null;

          if(cancelRequested) {
            Log.d(ApplicationConstants.TAG, "Paused download due to cancel");
            downloadStatus.setStatus(DownloadStatus.PAUSED);
          } else {
            moveCompletedDownloadToDestination();
          }
          s3Object.close();
        } catch (Exception e) {
          Log.e(ApplicationConstants.TAG, "Exception in download loop", e);

          if(retryCount < maxRetryCount) {
            //Maybe it's clock skew
            if(timeOffset == 0) {
              try {
                SntpClient client = new SntpClient();
                if (client.requestTime("0.us.pool.ntp.org", 30000)) {
                  long time = client.getNtpTime();
                  timeOffset = (int)((System.currentTimeMillis() - time)/1000);
                  Log.e(ApplicationConstants.TAG, "system time: " + System.currentTimeMillis() +
                      " network time: " + time + " skew: " + timeOffset);
                  if(timeOffset != 0) {
                    downloadStatus.startDownloadForRequest(downloadGetObjectRequest);
                  }
                }
              } catch (Exception e1) {
                Crashlytics.logException(e1);
                timeOffset = -1;
              }
            }

            //retry
            if(Connectivity.internetAvailable()) {
              retryCount++;
              startDownloadForRequest(downloadGetObjectRequest);
            } else {
              description = MapApplication.mainApplication.getString(R.string.no_internet_connection);
              setStatus(PAUSED);
              MapApplication.mainApplication.getDownloadStatusController().downloadUpdated(downloadStatus);
            }
          } else { //we're out of retries, so it's officially an error
            Crashlytics.logException(e);
            downloadStatus.setStatus(DownloadStatus.ERROR);
            description = MapApplication.mainApplication.getString(R.string.an_unknown_error_has_occurred);
            MapApplication.mainApplication.getDownloadStatusController().downloadUpdated(downloadStatus);
          }
        } finally {
          IOUtils.closeStream(reader);
          IOUtils.closeStream(writer);
        }
      }
    }).start();
  }


  @Override
  public void pause() {
    Log.d(ApplicationConstants.TAG, "S3DownloadStatus pause() " + uri);
    this.setStatus(DownloadStatus.PAUSED);
    MapApplication.mainApplication.getDownloadStatusController().downloadUpdated(this);
    cancelRequested = true;
    currentDownloads.remove(uri);
  }


  @Override
  public boolean resume() {
    Log.d(ApplicationConstants.TAG, "S3DownloadStatus resume() " + uri);
    cancelRequested = false;

    this.description = originalDescription;
    final GetObjectRequest getObjectRequest = new GetObjectRequest(this.bucket, this.key);
    final long size = downloadDestination.length();

    if(mObjectMetadata != null) {
      getObjectRequest.setRange(size, mObjectMetadata.getContentLength());
      Log.d(ApplicationConstants.TAG, "Resuming download start: " + getObjectRequest.getRange()[0] +
          " end:" + getObjectRequest.getRange()[1]);

      startDownloadForRequest(getObjectRequest);
    } else {
      new Thread(new Runnable() {
        @Override
        public void run() {
          if(mObjectMetadata == null) {
            fetchMetadata();
          }

          if(mObjectMetadata != null) {
            getObjectRequest.setRange(size, mObjectMetadata.getContentLength());
            Log.d(ApplicationConstants.TAG, "Resuming download start: " + getObjectRequest.getRange()[0] +
                " end:" + getObjectRequest.getRange()[1]);

            startDownloadForRequest(getObjectRequest);
          }
        }
      }).start();
    }
    return true;
  }


  public void setDescription(String description) {
    if(originalDescription == null) {
      this.originalDescription = description;
    }
    this.description = description;
  }


  private void fetchMetadata() {
    Log.d(ApplicationConstants.TAG, "Fetching Metadata");
    GetObjectMetadataRequest metadataRequest = new GetObjectMetadataRequest(bucket, key);
    mObjectMetadata = mAmazonS3Client.getObjectMetadata(metadataRequest);
  }


  private void moveCompletedDownloadToDestination() {
    try {
      if(destination.exists()) {
        if(!destination.delete()) {
          Log.e(ApplicationConstants.TAG, "Error deleting existing file at destination " +
              destination.getAbsolutePath());
        }
      }
      FileUtils.moveFile(downloadDestination, destination);

      if(observer != null) {
        observer.downloadFinished(destination, this);
      }
      this.setStatus(DownloadStatus.FINISHED);

      Log.d(ApplicationConstants.TAG, "Finished moving file to destination");
    } catch (Throwable e) {
      Log.e(ApplicationConstants.TAG, "Error moving file to destination", e);
      if(observer != null) {
        observer.downloadFailed();
        setStatus(ERROR);
      }
    } finally {
      MapApplication.mainApplication.getDownloadStatusController().downloadUpdated(this);
      currentDownloads.remove(uri);
    }
  }


  public void setAmazonS3Client(AmazonS3Client amazonS3Client) {
    mAmazonS3Client = amazonS3Client;
  }

}
