package com.fresconews.fresco.framework.databinding.bindingAdapters;

import android.content.SharedPreferences;
import android.databinding.BindingAdapter;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.VideoView;

import com.fresconews.fresco.Fresco2;
import com.fresconews.fresco.MainActivity;
import com.fresconews.fresco.framework.databinding.bindingTypes.BindableString;
import com.fresconews.fresco.utils.Logger;
import com.fresconews.fresco.utils.Utils;

import java.io.IOException;
import java.net.URL;

public class VideoBindingAdapters {

    public static final String TAG = VideoBindingAdapters.class.getSimpleName();
    // mediaPlayer is static so that it can be accessed globally, video playback is not stoping when the
    // user transitions to another scene

    // VideoView (TextureView) Singleton Object Managed in Fresco2
    //private static MediaPlayer mediaPlayer;
    public static MediaPlayer mediaPlayer;

    /**
     * SurfaceView adapter
     * <p/>
     * SurfaceView uses less resources than a TextureView.  Works by punching a hole through the UI to the layer
     * where the video is rendered.  Because the UI layer and video are playing on different levels it doesn't always
     * look good in lists views where the scrolling can give a laggy appearance.
     * <p/>
     * Also it is hard to put anything on top of a SurfaceView (like text) or to provide a background for it.
     */

    /*
     * tmrdev: slows list scrolling done considerably, surface gets destroyed frequently in current implementation
     */

    /*
    @BindingAdapter({"app:streamVideo"})
    public static void onStreamSurfaceView(SurfaceView surfaceView, final BindableString streamingUrl) {

        final MediaPlayer mediaPlayer = new MediaPlayer();

        final SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                final MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                };

                Logger.i("view-video", "hit viewer!!!!");

                try {
                    mediaPlayer.setDisplay(surfaceHolder);
                    mediaPlayer.setDataSource(streamingUrl.get());
                    mediaPlayer.prepare();
                    mediaPlayer.setOnPreparedListener(onPreparedListener);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    Logger.i("view-video", "IllegalArgumentException : " + e.getMessage());
                            e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    Logger.i("view-video", "SecurityException : " + e.getMessage());
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    Logger.i("view-video", "IllegalStateException : " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Logger.i("view-video", "IOException : " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Logger.i("view-video", "view video surface changed");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Logger.i("view-video", "view video surface destroyed");
            }


        });
    }
    */

    /**
     * TextureView adapter
     * <p/>
     * Renders the video into a view.  Uses more resources than a SurfaceView and requires Graphics H/W Accelleration.
     * Much more flexible as the TextureView can be resized or rotatated.
     */

    @BindingAdapter({"app:streamVideo"})
    public static void onStreamTextureView(final TextureView textureView, final BindableString streamingUrl) {
        Logger.i(TAG, "--:display video url -> " + streamingUrl.get());
        URL url = Utils.urlEncode(streamingUrl);
        Logger.i(TAG, "--:display active postId in View -> " + Fresco2.getActivePostIdInView() + " :: streamUrl postId -> " + streamingUrl.getPostId());
        Logger.i(TAG, "--:display video load state ->  " + streamingUrl.getLoadState());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Fresco2.getContext());
        // dev
        //Utils.memoryInfo("on stream video texture, right before video loads");

        // do not play videos if overlay onboarding screen is showing (onboarding not done yet, boolean does not get set to true)
        boolean onBoardingComplete = preferences.getBoolean("onboardingComplete", false);
        // Logger.i(TAG, "--:video player onboarding complete ? > " + onBoardingComplete);
        // NOTE: leaving check for onBoardingComplete, the flag is not getting set yet, leaving as reference

        /*
         * Need to find a way if globally the code can sense which active view is being displayed to the user
         * For example videos should not be playing back while the user is in the onboarding process
         * Also there is something in the Screen and releated classes that makes onboarding not noticeable to the ReclycerView ( videos continue to play )
         * See if setting a toolbar in each of the relevant onboarding screens helps, look at how each ViewBinding is set in MainActivity when transitions to
         * onboarding
         */

        //if(url != null && streamingUrl.getLoadState() != "preload" && onBoardingComplete ) {

        // uncomment to enable
        //if(url != null && streamingUrl.getLoadState() != "preload" && Fresco2.getActivePostIdInView() != "stop") {

        // stop all video playback for stability with new user creation and to avoid disturbing your stream of consciousness
        //int x = 1;
        //if(x != 1) {

        Logger.i(TAG, "--:video active binding -> " + MainActivity.activeViewBinding);
        // testing new activeViewBinding to make sure videos do not play under certain ViewBindings like onboarding
        // first try testing if activeViewBinding contains HomeViewBinding or StoryGalleryListViewBinding
        // if( url != null && Fresco2.isConnected(Fresco2.getContext()) && !Utils.stringContainsWord(MainActivity.activeViewBinding, "OnboardingViewBinding")) {

        Logger.i(TAG, "activeViewBinding -> " + MainActivity.activeViewBinding);

        if( url != null && Fresco2.isConnected(Fresco2.getContext()) &&
                ( Utils.stringContainsWord(MainActivity.activeViewBinding, "HomeViewBinding") || Utils.stringContainsWord(MainActivity.activeViewBinding, "StoryGalleryListViewBinding") ) ) {
            textureView.setVisibility(View.VISIBLE);
            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

                /*
                 * NOTE: Known issues info about TextureView player
                 * - MediaPlayer release is now being handled by Singleton Instance in Fresco2 Application
                 *
                 * - Need to map out all possible scenarios that could exist for playing back Video
                 *  * what main issue that can occur is that the first two entries in the list view have videos that need to be played
                 *      - need to make sure the most viewable videos plays only
                 *      - eliminate the possibility of two videos trying to playback at the same time
                 *      - map out ways the user could create issues navigating away from the current view
                 *      - need more detailed control over where videos are vertically in the list
                 *
                 * - Causing slow down on tablets with notifyDataSetChanged being called in GalleryViewModel for OnPageSelected
                 *      - recent builds are not showing a significant slow down
                 *
                 *  - Look into using the preload flag to keep the entire textureview from rendering, right now it just prevents playback on the onPreparedListener
                 *      ** IMPORTANT: with implementation of Singleton Class the preload flag needs to be integrated or removed
                 *
                 *
                 *  - Need to resize video and look for other optimizations that can be done
                 *
                 * - App keeps taking up more and more memory as it progresses through endless scrolls and memory is not getting released frequently enough
                 *      ** When memory does get released it happens in small chunks and not often enough
                 *
                 * check out -> http://google.github.io/ExoPlayer/
                 *
                 * *** Is there a way to stop the video player from the RecylerView listener that is in the relevant binding?
                 * *** Could the mediaplayer instance be passed through the Fresco2 Application context and stopped globally when needed?
                 *       - there should be only one video playing at a time so this could work
                 *       - with implementation of global singleton instance of mediaplayer video, see if recyclerview can stop video if need be
                 */

                /**
                 * Invoked when a TextureView's SurfaceTexture is ready for use.
                 * @param surface
                 * @param width
                 * @param height
                 */
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    // NOTE: running into release issues, memory leaks
                    //mediaPlayer = new MediaPlayer();
                    /*
                     * MediaPlayer uses a singleton instance for managing stop and release of all videos
                     * the onPrepared statement below starts the MediaPlayer
                     */
                    mediaPlayer = Fresco2.getVideoViewListSingleton(false);
                    // calling mySurface after mediaPlayer
                    //final Surface mySurface = new Surface(surface);
                    Surface mySurface = null;
                    try {
                        if( mediaPlayer != null) {
                            mySurface = new Surface(surface);
                        }
                    } catch (Exception e) {
                        Logger.i(TAG, "video: exception -> " + e.getMessage());
                    }
                    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            Logger.i(TAG, "--:video--> mediaplayer listener start");
                            if(mp.isPlaying()){
                                Logger.i(TAG, "--:video onPrepared - video IS playing");
                            }
                            else{
                                Logger.i(TAG, "--:video onPrepared - video IS NOT playing ");
                                mp.start();
                                Logger.i(TAG, "--:video onPrepared - mp.start executed ");
                                // dev
                                //Utils.memoryInfo("on stream video texture, right after video starts");

                            }
                            //mediaPlayer.start();

                        }
                    };
                    // end onprepared listener

                    //if(!mediaPlayer.isPlaying()) {
                        try {
                            URL myVideoURL = Utils.urlEncode(streamingUrl);
                            Logger.i(TAG, "--:video in video try setDataSource! _> streamingUrl " + streamingUrl);
                            mediaPlayer.setDataSource(myVideoURL.toString());
                            mediaPlayer.setSurface(mySurface);
                            //mediaPlayer.setDisplay((SurfaceHolder) mySurface);
                            //textureView.setLayoutParams(new RelativeLayout.LayoutParams(Utils.deviceWidthInPixels(), (int) (Utils.deviceWidthInPixels() / 1.25)));
                            // Need a Surface Holder to make setScreenOnWhilePlaying work properly
                            //mediaPlayer.setScreenOnWhilePlaying(true);
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(onPreparedListener);
                            // audio lingers on still playing while video stops when scrollig or navigating away from active video
                            // look into thread processing for video/audio in textureviews
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            //mediaPlayer.notifyAll();
                            Logger.i(TAG, "--:display video--> after on prepared listener before play!");

                            //mediaPlayer.start();
                        /* follow example of onprepared listener above
                        mediaPlayer.setOnBufferingUpdateListener((MediaPlayer.OnBufferingUpdateListener) Fresco2.getContext());
                        mediaPlayer.setOnCompletionListener((MediaPlayer.OnCompletionListener) Fresco2.getContext());
                        mediaPlayer.setOnVideoSizeChangedListener((MediaPlayer.OnVideoSizeChangedListener) Fresco2.getContext());
                        */

/* this may be slowing down the ui, using texture destroyed method for trying to stop videos
                        if(Fresco2.isScrolling){
                            if(mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                Logger.i(TAG, "--:display scrolling stopping video playback");
                            }

                        }
                        */
                        } catch (IllegalArgumentException e) {
                            Logger.i("--:Error", "--:video-->** TextureView IllegalArgumentException : " + e.getMessage());
                            e.printStackTrace();
                        } catch (SecurityException e) {
                            Logger.i("--:Error", "--:video-->** TextureView SecurityException : " + e.getMessage());
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            Logger.i("--:Error", "--:video-->** TextureView IllegalStateException : " + e.getMessage());
                            e.printStackTrace();
                        } catch (IOException e) {
                            Logger.i("--:Error", "--:video-->** TextureView IOException : " + e.getMessage());
                            e.printStackTrace();
                        }
                        // end try
                    //}

                }
                // end onSurfaceTextureAvailable ***

                /**
                 * Invoked when the specified SurfaceTexture is about to be destroyed. It is important to note that only one producer can use the TextureView.
                 * For instance, if you use a TextureView to display the camera preview, you cannot use lockCanvas() to draw onto the TextureView at the same time
                 *
                 * @param surface - The surface returned by getSurfaceTexture()
                 * @param width of surface
                 * @param height of surface
                 */
                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    Logger.i(TAG, "--:display video--> onSurfaceTextureSizeChanged hit");
                }

                /**
                 * Invoked when the SurfaceTexture's buffers size changed.
                 * @param surface
                 * @return
                 */
                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    // no need to try and stop videos from playing here, notifyDataSetChanged will do that
                    Logger.i(TAG, "--:video onSurfaceTextureDestroyed executed");
                    // TODO: could try calling video view singleton here and try stopping here, could be redundant, also try stopping video in other listeners here
                    return false;
                }

                /**
                 * Invoked when the specified SurfaceTexture is updated through updateTexImage()
                 * @param surface
                 */
                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                    /*
                     * NOTE: this method gets executed consistently
                     */
                    // Using special case for active post id to stop videos when a user navigates away from a video playing
                    //Logger.i(TAG, "--:video onSurfaceTextureUpdated - getActivePostIdInView value ->  " + Fresco2.getActivePostIdInView());
                    if(Fresco2.getActivePostIdInView() == "stop"){
                        Logger.i(TAG, "--:video display active:: video playback true, stop video! ");
                        // using reset works the best, other actions may cause crashes
                        // was using reset, now trying additional actions
                        /*
                        mediaPlayer.reset();
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        */
                    }
                }
            });

        } else{
            //Logger.i(TAG, "--:video--> url string is empty, textureview has not been created");
        }

    }


    /**
     * VideoVideo adapter
     * <p/>
     * Simplest Video Player for Android
     *
     * NOTE: relevant xml file -> post_media_adapter -> VideoView
     * Also review methods in VideoView -> Goto Declaration
     */
    @BindingAdapter({"app:streamVideo"})
    public static void onStreamVideoView(final VideoView videoView, final BindableString streamingUrl) {
        // TODO: Need to make videos fill out entire canvas area, the image behind video frame is showing through.
        // NOTE: videoViewer view is gone in the xml when the view load, it gets enabled below with setVisibility
        // this has been done as this videoViewer view breaks the imageView, so it had to be disabled
        // need to look into a more efficient procedure for seamless going from video image thumbnail view to video
        // TODO: see ~/Documents/fresco-app/fresco-log-output/video-view-error for all the log output pertaining to VideoView error in Galleries -> Stories -> list view

        /*
         * Need to the find the most comment aspect ratio in the list of videos (average that occurs the most) before scrolling begins
         * Then resize image and video to that common ratio.  See if ImageView can be removed and have just video display as it would be more efficient not to
         * have to render ImageView
         *
         * Look into scrolling indicators in GalleryViewModel for assessing which posts have videos, could use Otto for messaging details to this method
         *
         * Need to find a way to know when a video is active in the view and to also que up videos in advance (might look into downloading ViewPager views in a que,
         * but this will not work well for anyone that is moving quickly through the interface by scrolling fast both up and down the main list and right and left through viewpager
         *
         * - Try setting an active post id globally in Application for testing.
          *  - This post.id could then be matched with the post.video url that gets processed here to verify it is the active video in the view
         *   - Should post data verification be done? Is it possible that multiple posts use the exact same video url? What is the best way to validate the active post.video url in the view?
         *    - Could add a post.id attribute
         *
         *   - Two Main Areas where this is needed, for the main list that scrolls and also for when the user starts scrolling Right and Left through the ViewPager
         *
         * - Multiple ViewPager videos are loading simultaneously and creating performance/stability issues, work only having one video loading at a time and performance should be smooth
         *
         * - Also when the user starts to scroll after a video starts playing then it should stop playing, code has been put in place belowe to do this. Needs further testing
         *
         * Need a way to turn off videos when they are not active in the view
         */

        /*
        Logger.i(TAG, "--:display active postId in View -> " + Fresco2.getActivePostIdInView() + " :: streamUrl postId -> " + streamingUrl.getPostId());
        Logger.i(TAG, "--:display scroll boolean -> " + Fresco2.getIsScrolling());
        Logger.i(TAG, "--:display current load state -> " + streamingUrl.getLoadState());
        */

        // http://www.informit.com/articles/article.aspx?p=2143148&seqNum=3

        Logger.i(TAG, "--:display current load state -> " + streamingUrl.get());

        //URL url = Utils.urlEncode(streamingUrl);
        /*
         * Testing VideoView for Content Submission, change how video gets set
         */

        //if(url != null && Fresco2.getActivePostIdInView() == streamingUrl.getPostId() && Fresco2.isScrolling == false)  {
        // isScrolling never gets the false setting in time when scrolling stops, so videos will not play, scrolling stays true at this point in execution
        //if(url != null && Fresco2.getActivePostIdInView() == streamingUrl.getPostId())  {

        //if(url != null)  {

        if(!Utils.isTextEmpty(streamingUrl.get()))  {
                Logger.i(TAG, "--:display video (inside video play loop!! --> final video url ???? -> " + streamingUrl.get());

            /*
            LayoutParams videoViewLayout =videoView.getLayoutParams();
            videoViewLayout.height=150;
            videoViewLayout.setLayoutParams(params);
            */
            // set  url to final innerURL for inner class access below

            //final URL innerUrl;
            //innerUrl = url;

            try {
                //set the media controller in the VideoView
                //videoView.setMediaController(MediaController);

                //set the uri of the video to be played
                videoView.setVisibility(View.VISIBLE);
                // videoView.setVideoURI(Uri.parse(url.toString()));
                //videoView.setVideoURI(Uri.parse(streamingUrl.get().toString()));
                //videoView.setVideoURI(Uri.parse(streamingUrl.get()));
                videoView.setVideoPath(streamingUrl.get());

                /* can not get video preview to work for videoview, video stays black
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(streamingUrl.getImageUrl(), MediaStore.Images.Thumbnails.MINI_KIND);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
                videoView.setBackgroundDrawable(bitmapDrawable);
                */

                // can set layout left right top bottom
                //videoView.layout(20, 20, 20,20);

            } catch (Exception e) {
                Logger.i("Error", "--:video--> video viewer error : " + e.getMessage());
                e.printStackTrace();
                videoView.setVisibility(View.GONE);
            }

            //videoView.getLayoutParams().width = 1200;
            //videoView.getLayoutParams().width = 1000;
            //Logger.i(TAG, "--:device width --> " + Utils.deviceWidthInPixels());
            // Does requestFocus need to be set?
            //videoView.requestFocus();
            //Logger.i(TAG, "--:scrolling boolean -> " + Fresco2.getIsScrolling());
            // Is it better to keep this view from even being visible if scrolling?
            // Still need to try and stop scrolling here once an active video starts playing

            /*
            videoView.resolveAdjustedSize(2, 1);
            // Scaling
            Animation scaling = new ScaleAnimation(0.2f, 1.0f, 0.2f, 1.0f);
            scaling.setDuration(5000);
            videoView.startAnimation(scaling);
            */

/*
            if(Fresco2.isScrolling){
                videoView.stopPlayback();
                Logger.i(TAG, "--:display scrolling stopping video playback");

            }
*/

            //we also set an setOnPreparedListener in order to know when the video file is ready for playback
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Logger.i(TAG, "--:video--> onPrepared video - start video playback");

                    videoView.start();
                    // where is progress bar appearing?
                    // close the progress bar and play the video
                    //progressDialog.dismiss();

                    //if we have a position on savedInstanceState, the video playback should start from here
                    //videoView.seekTo();
//                if (position == 0) {
//                    videoView.start();
//                } else {
//                    //if we come from a resumed activity, video playback will be paused
//                    videoView.pause();
//                }
                }
            });

            // setZOrderOnTop will allow an image preview to be displayed
            // may also look into setting background to transparent
            //videoView.setZOrderOnTop(true);
            //videoView.start();

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    videoView.stopPlayback();
                    //videoView.setVisibility(View.GONE);
                    //videoView.setVisibility(View.VISIBLE);
                    Logger.i(TAG, "--:video--> video completed!!!");

                }

            });

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Logger.i(TAG, "--:video--> Exception :: url: " + what + " extra : -> " + extra);
                    return false;
                }
            });

        } // end if streamURL is not empty
        else {
            Logger.i(TAG, "--:video--> VideoView encoded url empty");
            videoView.setVisibility(View.GONE);
        }
    }

}
