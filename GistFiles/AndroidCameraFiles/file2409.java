package edu.smcm.ButterSealGame;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Seal implements ApplicationListener
{
	private int width;
	private int height;
	private int camWidth = 300;
	private int camHeight = 180;
	private int mapWidth;
	private int mapHeight;
	
	private int dpadHeight;
	private int dpadWidth;
	private int dpadHeight3;
	private int dpadWidth3;
	
	private Texture pikaImage;
	private Texture dpadImage;
	private Rectangle dpad;
	private Music DreamMusic;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Vector3 touchPos;
	private TileMapRenderer tileMapRenderer;
	
	private boolean isMoving;
	private int moveAmount;
	
	private enum MapQuadrant
	{
		NORTH,
		EAST,
		SOUTH,
		WEST,
		INVALID
	};
	
	private MapQuadrant quadrant;
	
	@Override
	public void create() 
	{
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		
		Gdx.gl.glClearColor(0f,0,0.0f,1);
		
		pikaImage = new Texture(Gdx.files.internal("pika.png"));
		dpadImage = new Texture(Gdx.files.internal("dpad.png"));
		dpadHeight = dpadImage.getHeight();
		dpadWidth = dpadImage.getWidth();
		dpadHeight3 = dpadHeight/3;
		dpadWidth3 = dpadWidth/3;
		DreamMusic = Gdx.audio.newMusic(Gdx.files.internal("Dream.mp3"));
		
		dpad = new Rectangle();
		dpad.x = 0;
		dpad.y = 0;
		dpad.width = dpadWidth;
		dpad.height = dpadHeight;
		
		batch = new SpriteBatch();
		
		touchPos = new Vector3();
		
		TiledMap tiledMap = TiledLoader.createMap(Gdx.files.internal("collisionmap/collisionTestDesert.tmx"));
		TileAtlas tileAtlas = new TileAtlas(tiledMap, Gdx.files.internal("collisionmap"));
		tileMapRenderer = new TileMapRenderer(tiledMap, tileAtlas, 12, 12);
		
		mapWidth = tiledMap.width * tiledMap.tileWidth;
		mapHeight = tiledMap.height * tiledMap.tileHeight;
		
		//DreamMusic.setLooping(true);
		//DreamMusic.play();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, camWidth*2, camHeight*2);
		camera.position.set(camWidth, camHeight, 0);
		
		quadrant = MapQuadrant.INVALID;
		isMoving = false;
		moveAmount = 0;
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	private void moveCamera()
	{
		if(!isMoving)
			handleInput();
		else if(moveAmount > 0)
		{
			switch(quadrant)
			{
			case NORTH:
				if(camera.position.y < (mapHeight-camHeight))
				{
					camera.translate(0, -3, 0);
					moveAmount -= 3;					
				}
				break;
			case EAST:
				if(camera.position.x < (mapWidth-camWidth))
				{
					camera.translate(3, 0, 0);
					moveAmount -= 3;
				}
				break;
			case SOUTH:
				if(camera.position.y > (camHeight))
				{
					camera.translate(0, 3, 0);
					moveAmount -= 3;
				}
				break;	
			case WEST:
				if(camera.position.x > camWidth)
				{
					camera.translate(-3, 0, 0);
					moveAmount -= 3;
				}
				break;
			default:
				break;			
			}			
		}
		else
			isMoving = false;			
	}

	@Override
	public void render() 
	{		

		moveCamera();
		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT );
		
		camera.update();
		tileMapRenderer.render(camera);
		batch.begin();
		batch.draw(dpadImage, dpad.x, dpad.y);
		batch.end();
	}
	
	// Helper functions for handleInput
	private boolean isInDpad()
	{
		if((touchPos.y < height - dpadHeight))
			return false;
		if(touchPos.x > dpadWidth)
			return false;
		
		return true;		
	}
	
	private void findQuadrant()
	{		
		if(touchPos.y > (height - (dpadHeight3*2)) && (touchPos.y < (height - dpadHeight3)))
			quadrant = MapQuadrant.WEST;
		else if(touchPos.y > (height - (dpadHeight3*2)) && (touchPos.y < (height - dpadHeight3)))
			quadrant = MapQuadrant.EAST;
		else if((touchPos.y > (height - dpadHeight)) && (touchPos.y < (height - dpadHeight3*2)))
			quadrant = MapQuadrant.NORTH;
		else if((touchPos.y > (height - dpadHeight3)) && (touchPos.x > dpadWidth3))
			quadrant = MapQuadrant.SOUTH;
		else
			quadrant = MapQuadrant.INVALID;
	}
	
	private void handleInput()
	{
		if(Gdx.input.isTouched()) 
		{
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			
			if(!isInDpad())
				return;
			
			findQuadrant();
			
			switch(quadrant)
			{
			case WEST:
			case EAST:
			case NORTH:
			case SOUTH:
					moveAmount = 32;
					isMoving = true;	
			default:
				break;
			}			
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}