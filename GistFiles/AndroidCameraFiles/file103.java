package introLibGDX;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

/**

 * Esta clase contiene el codigo completo del videotutorial de introducción a libgdx que 
 * puedes encontrar en la siguiente direccion:
 *
 *  http://openwebinars.net/webinar-desarrollo-de-juegos-para-android/

 * @author: Alberto Beiztegui Casado
 */

 

public class Juego implements ApplicationListener {
	
	public Stage stage;
	
	public Texture fruitsTexture;
	public Array<TextureRegion> fruitRegions;
	public TextureRegion bombRegion;
	
	public Timer.Task launchTask;
	public int fruitsPerRound = 1;
	
	public Sound newRoundSound;
	public Array<Sound> smashSounds;
	public Sound bombSound;
	
	@Override
	public void create() {
		
		//Cargamos la texura
		fruitsTexture = new Texture("fruits.png");
		
		//tamaño en pixeles del alto y ancho de cada una de las regiones
		final int size = 256;
		
		//región de la textura que contiene la bomba
		bombRegion = new TextureRegion(fruitsTexture, 0, size, size, size);
		
		//Separamos las frutas y las guardamos en un array
		fruitRegions = new Array<TextureRegion>();
		fruitRegions.add(new TextureRegion(fruitsTexture, 0, 0, size, size));
		fruitRegions.add(new TextureRegion(fruitsTexture, size, 0, size, size));
		fruitRegions.add(new TextureRegion(fruitsTexture, size, size, size, size));
		
		
		
		//Cargamos el sonido de inicio de ronda y de la bomba 
		newRoundSound = Gdx.audio.newSound(Gdx.files.internal("newround.mp3"));
		bombSound = Gdx.audio.newSound(Gdx.files.internal("bomb.mp3"));
		
		//Cargamos los sonidos de las frutas en un array
		smashSounds = new Array<Sound>();
		smashSounds.add(Gdx.audio.newSound(Gdx.files.internal("smash1.mp3")));
		smashSounds.add(Gdx.audio.newSound(Gdx.files.internal("smash2.mp3")));
		smashSounds.add(Gdx.audio.newSound(Gdx.files.internal("smash3.mp3")));
		
		
		//Creamos el escenario
		stage = new Stage();
		
		//Indicamos que el escenario manejara el input del jugador
		//(toques de pantalla, teclado, raton, etc). Necesario para que
		//funcionen los Listeners
		Gdx.input.setInputProcessor(stage);
		
		
		//Tarea que lanza frutas
		launchTask = new Timer.Task() {
			
			@Override
			public void run() {
				for(int number = 0; number < fruitsPerRound; number++){
					
					//añadimos de manera aleatoria 10% de bombas y 90% de frutas
					final Image fruit = MathUtils.random() < .1f? getBomb() : getFruit();
					stage.addActor(fruit);
					newRoundSound.play();
				}
				
				fruitsPerRound++;
			}
		};
		
		//Lanzamos la tarea anterior, la primera vez esperamos 2 segundos
		//y volvemos a lanzarla cada 5 segundos
		Timer.schedule(launchTask, 2, 5);
	}
	
	
	//Generador de bombas
	private Image getBomb() {
		//creamos un actor Image con la imagen de la bomba
		final Image bomb = new Image(bombRegion);
		
		//añadimos las acciones
		setStartActions(bomb);
		
		//añadimos un listener para el input del jugador
		bomb.addListener(new InputListener(){
			
			
			//Metodo que se ejecuta al pulsar sobre la bomba
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				//Explotamos todas las bombas y frutas y volvemos a empezar
				//con solo una fruta por ronda
				for(Actor actor : stage.getActors()) setEndActions(actor);
				fruitsPerRound = 1;
				bombSound.play();
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		
		return bomb;
	}
	
	//generador de frutas
	protected Image getFruit() {
		//Elegimos al azar una de las imagenes de frutas
		final Image fruit = new Image(fruitRegions.random());
		
		setStartActions(fruit);
		
		fruit.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				
				//En este caso solo quitamos la fruta y reproducimos
				//un sonido al azar
				setEndActions(fruit);
				smashSounds.random().play();
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		
		return fruit;
	}

	
	//Acciones de inicio, se aplican al crear una bomba o fruta
	private void setStartActions(Actor image) {
		
		//situamos el origen en el centro de la imagen
		//para escalar y rotar adecuadamente
		image.setOrigin(image.getWidth()/2, image.getHeight()/2);
		
		//damos un tamaño y posicion aleatorio
		image.setScale(MathUtils.random(.5f, 1));
		image.setPosition(MathUtils.random(300, 1280 -300), -300);
		
		//cantidad de movimiento vertical, en pixeles
		final float amountY = MathUtils.random(400f, 900f);
		
		//accion de subida y bajada
		image.addAction(Actions.sequence(
								Actions.moveBy(0, amountY, 2, Interpolation.pow2Out),
								Actions.moveBy(0, -amountY, 2, Interpolation.pow2In),
								Actions.removeActor()
				));
		
		//rotacion aleatoria
		image.addAction(Actions.rotateBy(MathUtils.random(-500, 500), 5));
		
	}
	
	//Acciones al quitar una bomba o fruta
	private void setEndActions(Actor image) {
		//quitamos las acciones previas
		image.clearActions();
		
		//escalamos y hacemos que desaparezca a la vez
		//y posteriormente eliminamos el actor
		image.addAction(Actions.sequence(
								Actions.parallel(
										Actions.scaleTo(2f, 2f, .3f),
										Actions.alpha(0, .3f)
								),
								Actions.removeActor()
				));
		
		//evitamos que el actor pueda ser tocado de nuevo
		image.setTouchable(Touchable.disabled);
	}

	//Limpia el escenario
	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void render() {		
		//pintamos la pantalla de gris
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		//Los actores actuan y se pinta el escenario en pantalla
        stage.act();
        stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		//pantalla original de 1280x720, escalandose manteniendo el aspect ratio
		stage.setViewport(1280, 720, true);
		
		//desplazamos el escenario para situarlo en el centro de la pantalla
        stage.getCamera().translate(-stage.getGutterWidth(), -stage.getGutterHeight(), 0);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
 	