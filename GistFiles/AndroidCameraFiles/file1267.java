package com.ivoryworks.android.andenginegarage.sprite;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class CatFaceSpriteActivity extends SimpleBaseGameActivity {
    private static final int CAMERA_WIDTH = 480;
    private static final int CAMERA_HEIGHT = 720;
    private static final String ASSET_BASE_PATH = "gfx/";
    private static final String IMAGE_FILE_NAME = "blue_cat.png";

    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TextureRegion mBlueCatTextureRegion;

    @Override
    public EngineOptions onCreateEngineOptions() {
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

    @Override
    protected void onCreateResources() {
        BitmapTextureAtlasTextureRegionFactory
                .setAssetBasePath(ASSET_BASE_PATH);
        mBitmapTextureAtlas = new BitmapTextureAtlas(getTextureManager(), 64, 64,
                TextureOptions.BILINEAR);
        mBlueCatTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBitmapTextureAtlas, this, IMAGE_FILE_NAME, 0, 0);
        mBitmapTextureAtlas.load();
    }

    @Override
    protected Scene onCreateScene() {
        final Scene scene = new Scene();
        scene.setBackground(new Background(0.75f, 0.75f, 0.75f));
        Sprite sp = new Sprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, 
                mBlueCatTextureRegion, getVertexBufferObjectManager());
        scene.attachChild(sp);
        return scene;
    }
}
