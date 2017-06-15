package com.example.zandlevelselector;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
import android.util.Log;



public class Main_Activity extends SimpleBaseGameActivity {
  EngineOptions engine;
	Camera mCamera;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 480;
	LevelSelector ls;
	Scene mScene;
	public org.andengine.opengl.font.Font	mFont;
	ITextureRegion mTextureRegion;

	@Override
	public EngineOptions onCreateEngineOptions() {	
		mCamera = new Camera(0, 0, WIDTH, HEIGHT);
		engine = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), mCamera);
		engine.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		
		return engine;
	}

	@Override
	protected void onCreateResources() {
	BuildableBitmapTextureAtlas mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager() ,32 , 32);
		
		mTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, "marble.png");
		
		try {
			mBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1));
			mBitmapTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Log.e("", ""+e);
		}
		
		this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		this.mFont.load();
	}

	@Override
	protected Scene onCreateScene() {
		mScene = new Scene();
		mScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		ls = new LevelSelector(5, 2, WIDTH, HEIGHT, mScene, mEngine);
		ls.createTiles(mTextureRegion, mFont);
		ls.show();
		return mScene;
	}
	
}