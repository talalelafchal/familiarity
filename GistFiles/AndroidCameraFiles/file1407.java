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
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.graphics.Color;




public class MainActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener {

	private Camera mCamera;
	private static int WIDTH = 800;
	private static int HEIGHT= 480;

	private BitmapTextureAtlas miAtlas;
	private ITextureRegion texturaChar;
	private Sprite charSprite;
	private ITiledTextureRegion texturaAnimada;
	private AnimatedSprite spriteAnimado;	
	private Font miFuente;
	private Sound miSonido;
	private Music miMusica;
	private Text miTexto;
	private int cuenta = 0;
	
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
		
		
		//para que el callback onSceneTouchEvent funcione
		//debemos asignarlo a una escena, de esta forma:
		sceneEjemplo.setOnSceneTouchListener(this);
	
		//(posicion x, posicion y, textura, elemento de andengine ignorenlo)
		charSprite = new Sprite(100, 200, texturaChar, getVertexBufferObjectManager());
		
		
		
		//(posicion x, posicion y, textura, elemento de andengine ignorenlo) - igual que arriba		
		spriteAnimado = new AnimatedSprite(200, 200,texturaAnimada, getVertexBufferObjectManager());
		//para animar el sprite debemos otorgarle tiempos en milisegundos a cada frame
		long[] duracionFrame = { 200, 200, 200, 200, 200, 200};
		
		//( tiempos x frame, del frame 1 al 6, siempre repitiendose)
		spriteAnimado.animate(duracionFrame, 1, 6, true);
		//y lo añadimos a la escena
		sceneEjemplo.attachChild(spriteAnimado);	
		sceneEjemplo.attachChild(charSprite);
		
		
		//ok cuando cargamos una fuente, debemos cargar los caracteres 
		//que vamos a usar para no causar lentitud en su carga posteriormente
		//que quiere decir esto, que si tenemos un Score: (numeros), 
		//es mejor precargar las opciones posibles:
		//osea todo esto ----> "Score:1234567890"
		//de lo contrario en tiempo de ejecucion, la carga 
		//causa momentos desagradables para la vista
		miTexto = new Text(400, 200, miFuente, "Score:1234567890", getVertexBufferObjectManager());
		miTexto.setText("Score: " + cuenta);
		
		sceneEjemplo.attachChild(miTexto);		
				
		return sceneEjemplo;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		
		if(pSceneTouchEvent.isActionDown()){
			
			//Pära el sonido, debemos diferenciar Sonido y Musica. En este caso hemos usado un sonido, 
			//la recomendacion para los sonidos es que no se repitan constantemente
			//para reproducirlo, usaremos esto:
			miSonido.play();
			cuenta++;
			miTexto.setText("Score: " + cuenta);
			
			
		}
		
		
		return false;
	}
	
}
