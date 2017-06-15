import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.support.annotation.NonNull;
import rx.Emitter;
import rx.Observable;

import java.util.List;

public class RxBluetoothScanner {
    public static class ScanResultException extends RuntimeException {
        public ScanResultException(int errorCode) {
            super("Bluetooth scan failed. Error code: " + errorCode);
        }
    }
    
    private RxBluetoothScanner() {
    }

    @NonNull
    public static Observable<ScanResult> scan(@NonNull final BluetoothLeScanner scanner) {
        return Observable.fromEmitter(scanResultEmitter -> {
            final ScanCallback scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, @NonNull ScanResult result) {
                    scanResultEmitter.onNext(result);
                }

                @Override
                public void onBatchScanResults(@NonNull List<ScanResult> results) {
                    for (ScanResult r : results) {
                        scanResultEmitter.onNext(r);
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    scanResultEmitter.onError(new ScanResultException(errorCode));
                }
            };
            
            scanResultEmitter.setCancellation(() -> scanner.stopScan(scanCallback));
            scanner.startScan(scanCallback);
        }, Emitter.BackpressureMode.BUFFER);
    }
}