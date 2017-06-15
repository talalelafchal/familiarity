/*
Talking about this:
https://github.com/reittes/AndEngine-Training-All-Codes/issues/1
*/

package com.semicolon.pandachaser;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.entity.util.FPSLogger;

import android.graphics.Color;
import android.graphics.Typeface;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class PandaChaser extends BaseGameActivity implements
IOnMenuItemClickListener, SensorEventListener {

private static final int CAMERA_WIDTH = 800;
private static final int CAMERA_HEIGHT = 480;

private Camera camera;
private TextureRegion bgRegion;
private TiledTextureRegion tiledRegion;
private SensorManager sensorManager;

private Font mFont;
private static final int MENU_RESTART = 0;
private static final int MENU_QUIT = 1;

private static final int LAYER_COUNT = 2;
private static final int LAYER_BACKGROUND = 0;
private static final int LAYER_SCORE = LAYER_BACKGROUND + 1;

private int mScore = 0;
private ChangeableText mScoreText;

private int accellerometerSpeedX;
private int accellerometerSpeedY;
private float sX, sY; // Sprite coordinates

private Scene gameScene;

private MenuScene menuScene;

private Music mLoop;
private Sound sMove;

public void onLoadComplete() {
// TODO Auto-generated method stub

}

public Engine onLoadEngine() {

	this.camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	final EngineOptions engineOptions = new EngineOptions(true,
		ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(
				CAMERA_WIDTH, CAMERA_HEIGHT), this.camera);

	engineOptions.setNeedsMusic(true);
	engineOptions.setNeedsSound(true);
	return new Engine(engineOptions);
}

public void onLoadResources() {
	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
	final BitmapTextureAtlas bgTextture = new BitmapTextureAtlas(1024, 512,
		TextureOptions.BILINEAR_PREMULTIPLYALPHA);

	bgRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
		bgTextture, getApplicationContext(), "blue_bg.png", 0, 0);

	final BitmapTextureAtlas pandaTexture = new BitmapTextureAtlas(128, 64,
		TextureOptions.BILINEAR_PREMULTIPLYALPHA);

	tiledRegion = BitmapTextureAtlasTextureRegionFactory
		.createTiledFromAsset(pandaTexture, getApplicationContext(),
				"panda_anim.png", 0, 0, 2, 1);

	final BitmapTextureAtlas mFontTexture = new BitmapTextureAtlas(256,
		256, TextureOptions.NEAREST_PREMULTIPLYALPHA);
	
	this.mFont = new Font(mFontTexture, Typeface.create(Typeface.DEFAULT,
		Typeface.BOLD), 40, true, Color.WHITE);
	
	getEngine().getFontManager().loadFont(this.mFont);
	getEngine().getTextureManager().loadTextures(bgTextture, pandaTexture,mFontTexture);

	MusicFactory.setAssetBasePath("sounds/");
	SoundFactory.setAssetBasePath("sounds/");

	try{
		this.mLoop=MusicFactory.createMusicFromAsset(getEngine().getMusicManager(), this, "main_music.mp3");
		this.mLoop.setLooping(true);
		this.sMove=SoundFactory.createSoundFromAsset(getEngine().getSoundManager(),this, "jump.ogg");
	}catch(final IOException e){
		//Debug.e(e);
	}
}

@SuppressWarnings("static-access")
public Scene onLoadScene() {

// final ColorBackground redBG= new ColorBackground(1,0,0);
// gameScene.setBackground(redBG);

// final Rectangle greenRectangle = new Rectangle(100,100,100,50);
// greenRectangle.setColor(0, 1, 0);
// gameScene.attachChild(greenRectangle);

	sensorManager = (SensorManager) this.getSystemService(this.SENSOR_SERVICE);
	sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),	sensorManager.SENSOR_DELAY_GAME);

	this.mEngine.registerUpdateHandler(new FPSLogger());

	return createGameScene();

}

/** Called when the activity is first created. */

public boolean onMenuItemClicked(MenuScene arg0, IMenuItem item,
	float arg2, float arg3) {
switch (item.getID()) {
case MENU_RESTART:
	restart();
	return true;
case MENU_QUIT:
	finish();
	return true;
default:
	return false;
}

}

private Scene createGameScene() {
	
	this.gameScene = new Scene();
    for(int i = 0; i < LAYER_COUNT; i++) {
            this.gameScene.attachChild(new Entity());
    }
        
	
	final Sprite background_image = new Sprite(0, 0, bgRegion);
	final SpriteBackground background = new SpriteBackground(background_image);
	gameScene.setBackground(background);
	final AnimatedSprite panda = new AnimatedSprite(100, 100, tiledRegion);
	panda.animate(100);
	gameScene.getChild(LAYER_BACKGROUND).attachChild(panda);
	
	sX = (CAMERA_WIDTH - panda.getWidth()) / 2;
	sY = (CAMERA_HEIGHT - panda.getHeight()) / 2;
	
	final AnimatedSprite panda2 = new AnimatedSprite(sX, sY, tiledRegion.deepCopy()){
	@Override
	public boolean onAreaTouched(TouchEvent event, float touchX, float touchY){		
	this.setPosition(event.getX() - this.getWidth() / 2, event.getY() - this.getHeight() / 2);
        		
		/*int eventAction = event.getAction();
        float X = event.getX();
        float Y = event.getY();
        */
        /*

        switch (eventAction) {
           case TouchEvent.ACTION_DOWN:
        	   	break;
           case TouchEvent.ACTION_MOVE: {
            	this.setPosition(X, Y);
            	break;}
           case TouchEvent.ACTION_UP:
                break;
        }
        */
        
        sMove.play();
        return true;
        
        
		/*if(event.isActionDown()){
			if(this.getY()==100){
				this.setPosition(this.getX(),200);				
			}
			else{
				this.setPosition(this.getX(), 100);
			}
			sMove.play();
			return true;
		}
		
		else if (event.isActionUp()){
			return true;
		}
		else{
			return false;
		}*/
		
	}
	};

	panda2.setFlippedHorizontal(true);
	panda2.setCurrentTileIndex(0);
	
	
	
	gameScene.getChild(LAYER_BACKGROUND).attachChild(panda2);
	gameScene.registerTouchArea(panda2);
	
	
	/* The ScoreText showing how many points the pEntity scored. */
    this.mScoreText = new ChangeableText(5, 5, this.mFont, "Score: 0", "Score: XXXX".length());
    this.mScoreText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    this.mScoreText.setAlpha(0.5f);
    this.gameScene.getChild(LAYER_SCORE).attachChild(this.mScoreText);
    
    

	final PhysicsHandler pandaHandler = new PhysicsHandler(panda);
	panda.registerUpdateHandler(pandaHandler);
	//pandaHandler.setVelocityX(150);
	pandaHandler.setVelocity(1);

	gameScene.registerUpdateHandler(new IUpdateHandler() {

	public void onUpdate(float arg0) {
		
		float pandaX = panda.getX();
		float pandaY = panda.getY();
		
		float panda2X = panda2.getX();
		float panda2Y = panda2.getY();
		
		
		if (panda2X > pandaX && panda2Y > pandaY){
			panda.setPosition(panda.getX() + 1, panda.getY()  + 1);
			
		}else if (panda2X < pandaX && panda2Y < pandaY){
			panda.setPosition(panda.getX() - 1, panda.getY()  - 1);
			
		}else if (panda2X > pandaX && panda2Y < pandaY){
			panda.setPosition(panda.getX() + 1, panda.getY()  - 1);
		}else if (panda2X < pandaX && panda2Y > pandaY){
			panda.setPosition(panda.getX() - 1, panda.getY()  + 1);
		}
		
		//Accelerometer
		if ((accellerometerSpeedX != 0) || (accellerometerSpeedY != 0)) {
			// Set the Boundary limits
			int tL = 0;
			int lL = 0;
			int rL = CAMERA_WIDTH - (int) panda2.getWidth();
			int bL = CAMERA_HEIGHT - (int) panda2.getHeight();

			// Calculate New X,Y Coordinates within Limits
			if (sX >= lL)
				sX += accellerometerSpeedX;
			else
				sX = lL;
			if (sX <= rL)
				sX += accellerometerSpeedX;
			else
				sX = rL;
			if (sY >= tL)
				sY += accellerometerSpeedY;
			else
				sY = tL;
			if (sY <= bL)
				sY += accellerometerSpeedY;
			else
				sY = bL;

			// Double Check That New X,Y Coordinates are within Limits
			if (sX < lL)
				sX = lL;
			else if (sX > rL)
				sX = rL;
			if (sY < tL)
				sY = tL;
			else if (sY > bL)
				sY = bL;

			panda2.setPosition(sX, sY);
		}
		
		//Camera boundaries		
		if (pandaX >= CAMERA_WIDTH) {
			panda.setPosition(0, pandaY); //panda.setPosition(0, panda2Y);
		}
		
		if (pandaY >= CAMERA_HEIGHT) {
			panda.setPosition(pandaX, 0);
		}
				
		//simple collision detection 	
		if (panda.collidesWith(panda2) && panda.getScaleX() == 1) {
			final ScaleModifier vanish = new ScaleModifier(0.3f, 1, 0);
			panda.registerEntityModifier(vanish);
			pandaHandler.setVelocityX(0);
			
		}
	
		if (panda != null) {
			if (panda.getScaleX() == 0) {
				runOnUpdateThread(new Runnable() {
	
					public void run() {
						panda.detachSelf();
						menuScene = createMenuScene();
						gameScene.setChildScene(menuScene, false, true,true);
					}
				});
			}
		}
		
		
		mScore++;
        mScoreText.setText("Score: " + mScore);
			
        
	}
	
	public void reset() {
		// TODO Auto-generated method stub
	
		}
	
	});
	
	this.mLoop.play();
	return gameScene;
}

protected MenuScene createMenuScene() {
final MenuScene menuScene = new MenuScene(this.camera);

final IMenuItem resetMenuItem = new ColorMenuItemDecorator(
		new TextMenuItem(MENU_RESTART, this.mFont, "RESTART"), 1.0f,
		0.0f, 0.0f, 0.0f, 0.0f, 0.0f);

resetMenuItem.setBlendFunction(GL10.GL_ALPHA,
		GL10.GL_ONE_MINUS_SRC_ALPHA);
menuScene.addMenuItem(resetMenuItem);

final IMenuItem quitMenuItem = new ColorMenuItemDecorator(
		new TextMenuItem(MENU_QUIT, this.mFont, "QUIT"), 1.0f, 0.0f,
		0.0f, 0.0f, 0.0f, 0.0f);
resetMenuItem.setBlendFunction(GL10.GL_ALPHA,
		GL10.GL_ONE_MINUS_SRC_ALPHA);
menuScene.addMenuItem(quitMenuItem);

menuScene.buildAnimations();
menuScene.setBackgroundEnabled(false);

menuScene.setOnMenuItemClickListener(this);
return menuScene;
}

public void restart() {
gameScene.reset();
gameScene = createGameScene();
menuScene.back();
getEngine().setScene(gameScene);
}


public void onSensorChanged(SensorEvent event) {
	synchronized (this) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			accellerometerSpeedX = (int) event.values[1];
			accellerometerSpeedY = (int) event.values[0];
			break;
		}
	}
}

public void onAccuracyChanged(Sensor sensor, int accuracy) {
	//
}

}

    
    

