import android.content.Context;
import android.view.MotionEvent;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.renderer.Renderer;

@EBean
class MyRenderer extends org.rajawali3d.renderer.Renderer {

    @RootContext
    MainActivity activity;

    MyRenderer(Context context) {
        super(context);
    }

    @Override
    protected void initScene() {
        VRSphere vrSphere = new VRSphere();
        getCurrentScene().addChild(vrSphere);
        
        // Start playing (I put this to simple description but I recommend that you should put this to right place.)
        try {
            vrSphere.bindMediaPlayer(activity.mediaPlayer);
            activity.mediaPlayer.start();
        } catch (ATexture.TextureException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
    }
}