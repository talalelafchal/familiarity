import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

public class RxMediaPlayer {

    public static @NonNull MediaPlayer from(@NonNull File file) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer;
    }
    
    public static @NonNull MediaPlayer from(@NonNull String url) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer;
    }

    public static @NonNull Observable<Pair<Integer, Integer>> play(@NonNull MediaPlayer mediaPlayer) {
        return prepare(mediaPlayer).flatMap(new Func1<MediaPlayer, Observable<Pair<Integer, Integer>>>() {
            @Override
            public Observable<Pair<Integer, Integer>> call(MediaPlayer mediaPlayer) {
                return stream(mediaPlayer);
            }
        });
    }

    private static @NonNull Observable<MediaPlayer> prepare(@NonNull final MediaPlayer mediaPlayer) {
        return Observable.fromCallable(new Callable<MediaPlayer>() {
            @Override
            public MediaPlayer call() {
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    e.printStackTrace();
                }
                return mediaPlayer;
            }
        });
    }

    private static @NonNull Observable<Pair<Integer, Integer>> stream(@NonNull final MediaPlayer mediaPlayer) {
        return Observable.create(new Observable.OnSubscribe<Pair<Integer, Integer>>() {
            @Override
            public void call(Subscriber<? super Pair<Integer, Integer>> subscriber) {
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }
                }));

                mediaPlayer.start();

                subscriber.add(ticks(mediaPlayer).takeUntil(complete(mediaPlayer)).subscribe(subscriber));
            }
        });
    }

    private static @NonNull Observable<Pair<Integer, Integer>> ticks(@NonNull final MediaPlayer mediaPlayer) {
        return Observable.interval(16, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, Pair<Integer, Integer>>() {
                    @Override
                    public Pair<Integer, Integer> call(Long value) {
                        int currentPositionInSeconds = mediaPlayer.getCurrentPosition() / 1000;
                        int durationInSeconds = mediaPlayer.getDuration() / 1000;
                        return Pair.create(currentPositionInSeconds, durationInSeconds);
                    }
                });
    }

    private static @NonNull Observable<MediaPlayer> complete(@NonNull final MediaPlayer mediaPlayer) {
        return Observable.create(new Observable.OnSubscribe<MediaPlayer>() {
            @Override
            public void call(final Subscriber<? super MediaPlayer> subscriber) {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer player) {
                        subscriber.onNext(player);
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }
}
