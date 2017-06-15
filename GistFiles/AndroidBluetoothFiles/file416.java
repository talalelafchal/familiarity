package com.example.scan

import org.uribeacon.scan.compat.BluetoothLeScannerCompat;
import org.uribeacon.scan.compat.BluetoothLeScannerCompatProvider;
import org.uribeacon.scan.compat.ScanCallback;
import org.uribeacon.scan.compat.ScanResult;
import android.content.Context;
import android.util.Log;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

public class Tooth {
    public static final String TAG = Tooth.class.getSimpleName();

    BluetoothLeScannerCompat mScanner;
    PublishSubject<ScanResult> mScanSubject = PublishSubject.create();

    public Tooth(Context context){
        mScanner = BluetoothLeScannerCompatProvider.getBluetoothLeScannerCompat(context);
    }
    
    ScanCallback mScanCallback = new ScanCallback() {
      
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Observable.from(results).map(scanResult -> {
                mScanSubject.onNext(scanResult);
                return scanResult;
            }).subscribe();
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            mScanSubject.onNext(result);
        }

        @Override
        public void onScanFailed(@SuppressWarnings("unused") int errorCode) {
            super.onScanFailed(errorCode);
            mScanSubject.onError(new ToothScanException(errorCode));
        }
    };

    public Observable<ScanResult> startScan(){
        mScanner.startScan(mScanCallback);
        return mScanSubject;
    }
    
    public void stopScan(){
        mScanner.stopScan(mScanCallback);
    }

    public class ToothScanException extends Exception{
        public int errorCode;

        public ToothScanException(int errorCode) {
            this.errorCode = errorCode;
        }
    }

    public void setCustomScanTiming(int scanMillis, int idleMillis, long serialScanDurationMillis) {
        mScanner.setCustomScanTiming(scanMillis, idleMillis, serialScanDurationMillis);
    }

    public void setScanLostOverride(long lostOverrideMillis) {
        mScanner.setScanLostOverride(lostOverrideMillis);
    }
}
