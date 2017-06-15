package com.example.watertest;



import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierMatcher;
import org.andengine.entity.modifier.JumpModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.color.Color;
import org.andengine.util.modifier.IModifier;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import android.util.Log;




public class MainActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener{

	private Camera mCamera;
	private static int WIDTH = 800;
	private static int HEIGHT= 480;
	
	private PhysicsWorld pW;
	private Scene sceneEjemplo;
	private FixtureDef fd = new FixtureDef(); 
	private Body b_caja;
	private float touchX = 0;
	
	@Override
	public EngineOptions onCreateEngineOptions() {


		// Definimos nuestra camara
		mCamera = new Camera(0, 0, WIDTH, HEIGHT);
		// Ahora declaramos las opciones del motor 
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), mCamera);
		//EngineOptions(es full screen?, Cual es la orientacion de la pantalla?, Como actuaremos ante distintas resoluciones?, y la camara)
	
		//para activar el uso de sonido, debemos activar la opcion
		engineOptions.getAudioOptions().setNeedsSound(true);
		//para activar la musica
		engineOptions.getAudioOptions().setNeedsMusic(true);
		
		// impedimos que la pantalla se apague por inactividad		
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		// Return the engineOptions object, passing it to the engine
		return engineOptions;
		
		
		
	}

	@Override
	protected void onCreateResources() throws IOException {
	
	    
	    
	}

	@Override
	protected Scene onCreateScene() {

		
		
		
		pW = new PhysicsWorld(new Vector2(0,-10), false);		
		sceneEjemplo = new Scene();	
		sceneEjemplo.setBackground(new Background(new Color(0.0f, 0.4f, 0.5f)));
		
		sceneEjemplo.registerUpdateHandler(pW);
		
		
		
		
		Rectangle s_piso = new Rectangle(0 + WIDTH/2, 0, WIDTH, 50, getVertexBufferObjectManager());
		s_piso.setColor(0.5f, 0.5f, 0.5f);		
		Body b_piso = PhysicsFactory.createBoxBody(pW, s_piso, BodyType.StaticBody, fd);
		
		pW.registerPhysicsConnector(new PhysicsConnector(s_piso, b_piso));
		
		
		
		
		
		///CAJA UNO
		Rectangle s_caja = new Rectangle(200, 400, 50, 50, getVertexBufferObjectManager());
		s_caja.setColor(0.4f, 0, 0.1f);	
		FixtureDef fd1 = PhysicsFactory.createFixtureDef(1, 0, 1);
		b_caja = PhysicsFactory.createBoxBody(pW, s_caja, BodyType.DynamicBody, fd1);
		
		pW.registerPhysicsConnector(new PhysicsConnector(s_caja, b_caja));
		
		
		
		///CAJA DOS
		Rectangle s_caja2 = new Rectangle(400, 400, 50, 50, getVertexBufferObjectManager());
		s_caja2.setColor(0.1f, 0.5f, 0.1f);	
		FixtureDef fd2 = PhysicsFactory.createFixtureDef(5, 0, 1);
		Body b_caja2 = PhysicsFactory.createBoxBody(pW, s_caja2, BodyType.DynamicBody, fd2);
				
		pW.registerPhysicsConnector(new PhysicsConnector(s_caja2, b_caja2));
		
		
		///CAJA DOS
		Rectangle s_caja3 = new Rectangle(600, 400, 50, 50, getVertexBufferObjectManager());
		s_caja3.setColor(0.1f, 0.1f, 0.5f);	
		FixtureDef fd3 = PhysicsFactory.createFixtureDef(10, 1, 1);
		Body b_caja3 = PhysicsFactory.createBoxBody(pW, s_caja3, BodyType.DynamicBody, fd3);
						
		pW.registerPhysicsConnector(new PhysicsConnector(s_caja3, b_caja3));
		
		
		
		sceneEjemplo.attachChild(s_piso);
		sceneEjemplo.attachChild(s_caja);	
		
		sceneEjemplo.attachChild(s_caja2);
		sceneEjemplo.attachChild(s_caja3);
		
		
		sceneEjemplo.setOnSceneTouchListener(this);
		
		
		return sceneEjemplo;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		
			
		if(pSceneTouchEvent.isActionDown()){
		
			touchX = pSceneTouchEvent.getX();
			
			
			if(touchX > (WIDTH/2 + 150) && touchX < WIDTH){
				
				
				b_caja.applyLinearImpulse(new Vector2(2, 10), b_caja.getWorldCenter());
				
				
			}else if(touchX > 0 && touchX < (WIDTH/2 - 150)){
				
				
				b_caja.applyLinearImpulse(new Vector2(-2, 10), b_caja.getWorldCenter());
				
			}
			
		}
		
		
		
		
		
		return false;
	}
	
		
	
	
}
