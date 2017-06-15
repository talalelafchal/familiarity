package com.dt.cstag.scanner;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import com.dt.cstag.CsTagAdvertiser;
import com.dt.cstag.CsTagRequirementChecker;
import com.dt.cstag.CsTagUiHandlerImpl;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Traditional scanner implementation.
 */
@Keep
public class CsTagTraditionalScanner implements ICsTagTraditionalScanner {

    private Set<CsTagBtScannerListener> btScannerListeners = Collections.newSetFromMap(new ConcurrentHashMap<CsTagBtScannerListener, Boolean>());
    private Subscription scannerSubscription;
    private Subscription timerSubscription;
    private CsTagRequirementChecker mPermissionChecker;
    private ICsTagRxScanner mRxScanner;
    private long scanTimeoutMilliseconds = 10000;
    private boolean isFiltered = true;

    public CsTagTraditionalScanner (Context context) {
        mRxScanner = new CsTagRxBluetoothScanner(context);
        mPermissionChecker = new CsTagRequirementChecker(context)
                  .setWriteStorageRequired(false);
    }

    @Override
    public void addScanListener (CsTagBtScannerListener listener) {
        btScannerListeners.add(listener);
    }

    @Override
    public void removeListener (CsTagBtScannerListener listener) {
        btScannerListeners.remove(listener);
    }

    @Override
    public void clearListeners () {
        btScannerListeners.clear();
    }

    public void startScanning () {
        if (scannerSubscription != null && !scannerSubscription.isUnsubscribed()) {
            scannerSubscription.unsubscribe();
        }
        if (areAllRequirementSatisfied()) {
            scannerSubscription = mRxScanner.getScanningObservable(isFiltered)
                      .subscribeOn(Schedulers.newThread())
                      .observeOn(AndroidSchedulers.mainThread())
                      .doOnUnsubscribe(() -> onScanningCompleted())
                      .subscribe(getSubscriber());
            if (scanTimeoutMilliseconds > 0) {
                timerSubscription = new CsTagUiHandlerImpl().postDelayed(() -> {
                    stopScanning();
                }, scanTimeoutMilliseconds);
            }
        } else {
            checkRequirements();
        }
    }

    @NonNull private Subscriber<CsTagAdvertiser> getSubscriber () {
        return new Subscriber<CsTagAdvertiser>() {

            @Override
            public void onCompleted () {
                onScanningCompleted();
            }

            @Override
            public void onError (Throwable e) {
                if (!(e instanceof TimeoutException)) {
                    onScanningError(e);
                }
            }

            @Override
            public void onNext (CsTagAdvertiser advertiser) {
                onAdvertiserFound(advertiser);
            }
        };
    }

    private void onAdvertiserFound (CsTagAdvertiser advertiser) {
        for (CsTagBtScannerListener listener : btScannerListeners) {
            listener.onDeviceFound(advertiser);
        }
    }

    private void onScanningError (Throwable e) {
        for (CsTagBtScannerListener listener : btScannerListeners) {
            listener.onScanningError(e);
        }
    }

    private void onScanningCompleted () {
        for (CsTagBtScannerListener listener : btScannerListeners) {
            listener.onScanningCompleted();
        }
    }

    @Override
    public void stopScanning () {
        if (scannerSubscription != null && !scannerSubscription.isUnsubscribed()) {
            scannerSubscription.unsubscribe();
        }
        if (timerSubscription != null && !timerSubscription.isUnsubscribed()) {
            timerSubscription.unsubscribe();
        }
    }

    private void checkRequirements () {
        mPermissionChecker.check(type -> {
            for (CsTagBtScannerListener listener : btScannerListeners) {
                listener.onRequirementsError(type);
            }
        });
    }

    private boolean areAllRequirementSatisfied () {
        return mPermissionChecker.checkSilently();
    }

    public void setRequirementsChecker (CsTagRequirementChecker checker) {
        this.mPermissionChecker = checker;
    }

    @Override public void setScanTimeoutMilliseconds (final long scanTimeoutMilliseconds) {
        this.scanTimeoutMilliseconds = scanTimeoutMilliseconds;
    }

    @Override public void setIsFiltered (final boolean isFiltered) {
        this.isFiltered = isFiltered;
    }
}