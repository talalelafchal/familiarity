import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
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
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
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
import org.andengine.util.modifier.IModifier;

import android.graphics.Color;
import android.util.Log;




public class MainActivity extends SimpleBaseGameActivity {

	private Camera mCamera;
	private static int WIDTH = 800;
	private static int HEIGHT= 480;

	private BitmapTextureAtlas miAtlas;
	private ITextureRegion texturaChar;
	private ITextureRegion texturaAcc;
	private Sprite charSprite;
	private Sprite accesorioSprite;
	private Sprite Boton;
	private ITiledTextureRegion texturaAnimada;
	private ITextureRegion texturaMoneda;
	private Sprite spriteMoneda;

	private boolean botonSuspendido = false;
	private ITextureRegion texturaBoton;
	
	private Font miFuente;
	private Sound miSonido;
	private Music miMusica;
	private Text miTexto;
	private int cuenta = 0;
	private float factorDeMovimiento = 50;
	private float rotacionAccesorio = 0.5f;
	
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

		//primero debemos indicar donde estan las imagenes
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		//luego crear el atlas, darle medidas y un tipo de textura.
	    miAtlas = new BitmapTextureAtlas(getTextureManager(), 800, 800, TextureOptions.DEFAULT);
	    //ubicamos nuestra imagen en el atlas
	    texturaChar = BitmapTextureAtlasTextureRegionFactory.createFromAsset(miAtlas, this, "char.png", 0, 0);
	    texturaAcc = BitmapTextureAtlasTextureRegionFactory.createFromAsset(miAtlas, this, "accesorio.png", 0, 96);
	    texturaBoton = BitmapTextureAtlasTextureRegionFactory.createFromAsset(miAtlas, this, "boton.png", 0, 200);
	    texturaMoneda = BitmapTextureAtlasTextureRegionFactory.createFromAsset(miAtlas, this, "moneda.png", 0, 250);
	    //Aqui ubicamos el sprite para que no se ubique sobre el anterior
	    //Osea teniendo en cuenta que el anterior estaba en 0,0 y ocupa hasta 75,95
	    //Lo ubicaremos despues 76 en x, 0 en y, e indicamos que tiene 10 columnas y 10 filas.
	    texturaAnimada = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(miAtlas, this, "animado.png", 76, 0, 10, 10);
	    
	    //y la cargamos
	    miAtlas.load();    
		
	    //Aqui hemos creado un atlas de 256 x 256 para cargar una imagen de 108 x 253 
	    //en el punto 0,0 de miAtlas, osea nos sobra espacio
	    
	    
	    
	    //Indicamos donde se ubican las fuentes
	    FontFactory.setAssetBasePath("fuentes/");
	    //aqui creamos la textura donde cargaremos la fuente
	    final ITexture fontTexture = new BitmapTextureAtlas(getTextureManager(), 256, 256, TextureOptions.BILINEAR);
	    //Aqui definimos cual es la fuente, tamaño, si usara antialias y su color
	    miFuente = FontFactory.createFromAsset(getFontManager(), fontTexture, getAssets(), "fuente.ttf", 40, true, Color.WHITE);
	    //La cargamos
	    miFuente.load();
	    
	    
	    
	    
	    try 
	    {
	    	//creo que la explicacion está demas
	    	//lo que si indico es que tambien hay una clase musica
	    	//pero se usa para canciones más largas
	    	//y que se repiten constantemente	    	
	        miSonido = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "sonido/sonido.mp3");	        
	        //esto quiere decir que no se repite constantemente	        
	        miSonido.setLooping(false);
	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	    }
	    
	    
	    
	    
	    
	    try
	    {
	        miMusica = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), this,"musica/musica.mp3");
	        miMusica.setLooping(true);
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }
	    
	    
	    
	    
	    
	}

	@Override
	protected Scene onCreateScene() {
		
		miMusica.setVolume(0.5f);
		miMusica.play();

		Scene sceneEjemplo = new Scene();			
		
		
		miTexto = new Text(mCamera.getWidth()*0.2f, mCamera.getHeight()*0.8f, miFuente, "Click:1234567890", getVertexBufferObjectManager());
		miTexto.setText("Click: " + cuenta);
		
		
		
		charSprite = new Sprite(200, 200, texturaChar, getVertexBufferObjectManager());
		
		//si la vamos a unir a un Sprite, debemos tener en cuenta que nuestras coordenadas ahora son diferentes
		//porque son relativas al sprite al que la vamos a agregar
		accesorioSprite = new Sprite(charSprite.getWidth()*0.5f, charSprite.getHeight(), texturaAcc, getVertexBufferObjectManager()){
			
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {

				accesorioSprite.setRotation(accesorioSprite.getRotation() + rotacionAccesorio);
				
				if(accesorioSprite.getRotation() > 10.5f)
					rotacionAccesorio*=-1;
				
				if(accesorioSprite.getRotation() < -10.5f)
					rotacionAccesorio*=-1;
				
				
				super.onManagedUpdate(pSecondsElapsed);
			}
			
		};
		charSprite.attachChild(accesorioSprite);
		
		
		
		
		
		
		Boton = new Sprite(100, 100, texturaBoton, getVertexBufferObjectManager()){
			
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				
				//boolean de control
				if(!botonSuspendido)
					
					
					//Crearemos nuestro modifier, en este caso un JumpModifier (hay otros, movetoxmodifier, alphamodifier,etc etc etc)
					//JumpModofier(tiempo del salto, desdeX, hastaX, desdeY, hastaY, altura del Salto);	
					charSprite.registerEntityModifier(new JumpModifier(1, charSprite.getX(), charSprite.getX() + 50, charSprite.getY(), charSprite.getY(), -100, new IEntityModifierListener() {
						
						//Cuando el modifier empiecza
						@Override
						public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
							botonSuspendido = true;
						}
						
						//cuando termina
						@Override
						public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
							//este booleano es para no sobre cargar el personaje con muchos modifiers 
							//y saltar cuando el salto haya finalizado :D
							botonSuspendido = false;	
							cuenta++;
							miTexto.setText("Click: " + cuenta);
						}
					}));	
					

					
				
				
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
			
		};
		//nunca se olviden de esto
		sceneEjemplo.registerTouchArea(Boton);
		sceneEjemplo.attachChild(Boton);
		
		
		
		sceneEjemplo.attachChild(charSprite);
		sceneEjemplo.attachChild(miTexto);
		
		
		
		
		
		
		
		//No se olviden de 
		//declarar la textura
		//private ItextureRegion texturaMoneda;
		//y ubicarla en el atlas
		//texturaMoneda = BitmapTextureAtlasTextureRegionFactory.createFromAsset(miAtlas, this, "moneda.png", 0, 250);
		//y obvio un sprite
		//private Sprite spriteMoneda;
		spriteMoneda = new Sprite(700, 250, texturaMoneda, getVertexBufferObjectManager());
		
		
		
		//loop entity modifier recibe como parametro otro modifier y lo repite hasta el fin del mundo XD
		//y esta vez un loop entity modifier recibe un sequence e.m. que tiene dos scales
		//osea repite la secuencia
		spriteMoneda.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.6f, 1, 1.5f), new ScaleModifier(0.6f, 1.5f, 1))));
		
		
		
		
		sceneEjemplo.attachChild(spriteMoneda);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return sceneEjemplo;
	}
	
	
}