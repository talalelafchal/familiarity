import android.media.MediaRecorder;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

public class RxMediaRecorder {

    public static @NonNull MediaRecorder into(@NonNull File file) {
        final MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(file.getAbsolutePath());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        return recorder;
    }

    public static @NonNull Observable<Long> record(@NonNull MediaRecorder recorder) {
        return prepare(recorder).flatMap(new Func1<MediaRecorder, Observable<Long>>() {
            @Override
            public Observable<Long> call(MediaRecorder recorder) {
                return stream(recorder);
            }
        });
    }

    private static @NonNull Observable<MediaRecorder> prepare(@NonNull final MediaRecorder recorder) {
        return Observable.fromCallable(new Callable<MediaRecorder>() {
            @Override
            public MediaRecorder call() throws Exception {
                recorder.prepare();
                return recorder;
            }
        });
    }

    private static @NonNull Observable<Long> stream(@NonNull final MediaRecorder recorder) {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                // This block is executed when unsubscribe of this observable is invoked
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        recorder.stop();
                        recorder.reset();
                        recorder.release();
                    }
                }));

                recorder.start();

                // Notify every second elapsed since recording is started
                final long start = System.currentTimeMillis();
                subscriber.add(Observable.interval(16, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                        .map(new Func1<Long, Long>() {
                            @Override
                            public Long call(Long value) {
                                long millisElapsed = System.currentTimeMillis() - start;
                                return millisElapsed / 1000;
                            }
                        })
                        .subscribe(subscriber));
            }
        });
    }
}
