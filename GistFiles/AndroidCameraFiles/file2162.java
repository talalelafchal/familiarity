
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

import java.util.List;


public class FlashLightEngine {
    public FlashLightEngine(final List<Morse> currentRythm, final Context context) {
        new Thread() {
            @Override
            public void run() {
//                    this.setDaemon(true);
                synchronized (this.getClass()) {
                    Camera cam = null;
                    try {
                        long flashOffTime = 0;
                        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                            for (Morse currentSignal : currentRythm) {
                                cam = Camera.open();
                                if (cam == null) {
                                    return;
                                }
                                Camera.Parameters p = cam.getParameters();
                                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                cam.setParameters(p);

                                long deltaToSleep = System.nanoTime() - flashOffTime;
                                if (deltaToSleep < 250_000_000 && deltaToSleep > 0) {  // todo: replace hardcode with Constants
                                    Thread.sleep(deltaToSleep);
                                }
                                cam.startPreview();
                                long flashingTime = currentSignal.getDuration();
                                Thread.sleep(flashingTime);
                                cam.stopPreview();
                                flashOffTime = System.nanoTime();

//                                    Thread.sleep(250 / 2); // replace hardcode with Constants, flash is slower than vibrate, so we make less interval between flashes
                                cam.release();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        if (cam != null) {
                            cam.release();
                        }
                    }
                }
            }
        }.start();
    }
}