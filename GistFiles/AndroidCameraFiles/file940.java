package com.andyco.airtime.scenes;

//import java.io.IOException;
import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
//import org.andengine.entity.IEntity;
//import org.w3c.dom.Text;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
//import org.andengine.opengl.font.Font;
//import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
//import org.andengine.util.level.EntityLoader;
//import org.andengine.util.level.constants.LevelConstants;
//import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
//import org.andengine.util.level.simple.SimpleLevelLoader;
//import org.andengine.entity.modifier.LoopEntityModifier;
//import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
//import org.andengine.entity.scene.background.Background;
//import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
//import org.andengine.extension.physics.box2d.PhysicsConnector;
//import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.input.touch.TouchEvent;
//import org.xml.sax.Attributes;

import android.content.ContentValues;
//import android.content.Context;
//import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
//import android.util.Log;

import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.Body;
//import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
//import com.badlogic.gdx.physics.box2d.Contact;
//import com.badlogic.gdx.physics.box2d.ContactImpulse;
//import com.badlogic.gdx.physics.box2d.ContactListener;
//import com.badlogic.gdx.physics.box2d.Fixture;
//import com.badlogic.gdx.physics.box2d.FixtureDef;
//import com.badlogic.gdx.physics.box2d.Manifold;
//import com.andyco.airtime.activity.GameActivity;
import com.andyco.airtime.managers.ResourceManager;
import com.andyco.airtime.managers.SceneManager;
//import com.example.falldown.LevelCompleteWindow.StarsCount;
import com.andyco.airtime.managers.SceneManager.SceneType;
import com.andyco.airtime.objects.Player;

public class GameScene extends BaseScene implements IOnSceneTouchListener,IOnMenuItemClickListener  {

	private MenuScene menuChildScene;
	private final int MENU_RETRY = 0;
	private final int MENU_BACK = 1;
	
	private HUD gameHUD;
	private Text totalTimeText;
	private Text remainText;
	private int timeRemaining = 30;
	private int totalTime = 0;
	private PhysicsWorld physicsWorld;
	
	private static int SCREEN_HEIGHT = 480;
	private static int SCREEN_WIDTH = 800;
	Sprite help;
	
//	private static final String TAG_ENTITY = "entity";
//	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
//	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
//	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
//	    
//	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1 = "platform1";
//	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2 = "platform2";
//	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3 = "platform3";
//	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN = "coin";
//	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
//	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE = "levelComplete";

	private Player player;
	
	//private boolean firstTouch = false;

	private Text gameOverText;
	private boolean gameOverDisplayed = false;
	
	private Text startText;
	
	//private LoopEntityModifier mod = new LoopEntityModifier(new ScaleModifier(1, 1, 1.1f));

	//private LevelCompleteWindow levelCompleteWindow;
	private int bestTime;
	private boolean isRunning = false;
	private boolean firstTry= false;
	private float[] paths = {32,64,96,128,160,192,224,256,288,320,352,384,416,448,480};
	
	TimerHandler updater;
	TimerHandler itemMaker;
	TimerHandler enemyMaker;
	//TimerHandler increase;

	private void createPhysics()
	{
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0,-5),false);
	    //physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}

	private void addToTime(int i)
	{
		timeRemaining +=i;
		if(timeRemaining<0)
		{
			timeRemaining=0;
		}
		remainText.setText("Time Left: "+timeRemaining/60+":"+(Integer.toString(timeRemaining%60).length()<2?"0"+timeRemaining%60:timeRemaining%60));
	}
	private void createHUD()
	{
	    gameHUD = new HUD();
	    
	 // CREATE SCORE TEXT
	    remainText = new Text(500, 390, resourceManager.font, "Time Left: 00:00", new TextOptions(HorizontalAlign.RIGHT), vbom);
	    remainText.setAnchorCenter(0, 0);    
	    remainText.setText("Time Left: 0:30");
	    
	    totalTimeText = new Text(20, 390, resourceManager.font, "Your Time: 00:00", new TextOptions(HorizontalAlign.LEFT), vbom);
	    totalTimeText.setAnchorCenter(0, 0);    
	    totalTimeText.setText("Your Time: 0:00");
	    gameHUD.attachChild(remainText);
	    gameHUD.attachChild(totalTimeText);
	    
	    camera.setHUD(gameHUD);
	}
	private void loadData()
	{
		//GameActivity.;
		String query = "SELECT * FROM SCORE";
		Cursor c = ResourceManager.getInstance().dbHelper.getDB().rawQuery(query, null);
        c.moveToFirst();
        bestTime = c.getInt(c.getColumnIndex("Score"));
        
        Text bestText = new Text(20, 420, resourceManager.font, "Best Time: 00:00", new TextOptions(HorizontalAlign.LEFT), vbom);
        bestText.setAnchorCenter(0, 0); 
        bestText.setText("Best Time: "+bestTime/60+":"+(Integer.toString(bestTime%60).length()<2?"0"+bestTime%60:bestTime%60));
	
        this.attachChild(bestText);
        ResourceManager.getInstance().dbHelper.getDB().close();
	}
	private void saveData()
	{
		if(totalTime>bestTime)
		{
			//String query = "UPDATE SCORE SET SCORE="+totalTime;
			ResourceManager.getInstance().dbHelper.openDataBase();
			ContentValues args = new ContentValues();
		    args.put("SCORE", totalTime);
		    ResourceManager.getInstance().dbHelper.getDB().update("SCORE", args, "SCORE="+bestTime, null);
		    ResourceManager.getInstance().dbHelper.getDB().close();
		}
	}
	@Override
	public void createScene() {
		// TODO Auto-generated method stub
		
		createBackground();
		createHUD();
		createPhysics();
		
		if(!firstTry)
		{
			createSprites();
		    firstTry = true;
			
		}
		loadLevel(1);
	    createGameOverText();
	    loadData();
	    //levelCompleteWindow = new LevelCompleteWindow(vbom);

		setOnSceneTouchListener(this);
		
	}
	public void createSprites()
	{
		updater =  new TimerHandler(1f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	        	
	            updateTime();
	            /*if(timeRemaining <=0)
	            {
	            	player.onDie();
	            }*/
	            pTimerHandler.reset();
	        }
	    });
		itemMaker = new TimerHandler(5f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	            createItem();
	            pTimerHandler.reset();
	        }
	    });
		enemyMaker = new TimerHandler(2.5f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	            createEnemy();
	            pTimerHandler.reset();
	        }
	    });
		/*increase = new TimerHandler(120f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	        	
	        	registerUpdateHandler(new TimerHandler(2.5f, new ITimerCallback() 
	            {
	                public void onTimePassed(final TimerHandler pTimerHandler) 
	                {
	                    createItem();
	                    pTimerHandler.reset();
	                }
	            }));

	    	    registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() 
	            {
	                public void onTimePassed(final TimerHandler pTimerHandler) 
	                {
	                    createEnemy();
	                    pTimerHandler.reset();
	                }
	            }));
	        }
	    });*/
		player = new Player(SCREEN_WIDTH/10,SCREEN_HEIGHT-ResourceManager.getInstance().player_region.getHeight(), vbom, camera, physicsWorld)
	    {

			@Override
			public void onDie() {
				// TODO Auto-generated method stub
				if (!gameOverDisplayed)
                {
                    displayGameOverText();
                    saveData();
                    createMenuChildScene();
                    //Log.d("TEST","CALLED");
                    physicsWorld.unregisterPhysicsConnector(this.fall);
                    //unregisterUpdateHandler(updater);
            	    //unregisterUpdateHandler(itemMaker);
            	    //unregisterUpdateHandler(enemyMaker);
            	    //unregisterUpdateHandler(increase);
                }
			}
	    	
	    };
	    this.registerUpdateHandler(updater);
	    this.registerUpdateHandler(itemMaker);
	    this.registerUpdateHandler(enemyMaker);
	    //this.registerUpdateHandler(increase);
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub
		saveData();
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		camera.setHUD(null);
		camera.setCenter(400, 240);
		camera.setChaseEntity(null);

		
	}
	private void createBackground()
	{
	    //setBackground(new Background(Color.BLUE));
		AutoParallaxBackground background = new AutoParallaxBackground (0,0,0,-5);
		//ParallaxBackground background = new ParallaxBackground(0, 0, 0);
	    background.attachParallaxEntity(new ParallaxEntity(15f, new Sprite(ResourceManager.getInstance().game_background_region.getWidth()/2, SCREEN_HEIGHT/2, ResourceManager.getInstance().game_background_region, vbom)));
	    background.attachParallaxEntity(new ParallaxEntity(15f, new Sprite((ResourceManager.getInstance().game_background_region.getWidth()*1.5f)-1, SCREEN_HEIGHT/2, ResourceManager.getInstance().game_background_region, vbom)));
	    
	    setBackground(background);
	}
	private void loadLevel(int levelID)
	{
	    
	    //camera.setBounds(0, 0, 800, 480); // here we set camera bounds
	    //camera.setBoundsEnabled(true);
	    //isRunning= true;
	    timeRemaining=30;
	    createStartText();
	    displayStartText();
	    this.setIgnoreUpdate(true);

	    player.setCullingEnabled(true);
	    this.attachChild(player);

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		
		if(pSceneTouchEvent.isActionDown())
		{
			
			if(!this.isIgnoreUpdate()&&timeRemaining>0)
			{
		        player.swoop();
		        engine.registerUpdateHandler(new TimerHandler(0.1f,new ITimerCallback()
		        {
	
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						// TODO Auto-generated method stub
						player.upMulti +=0.001;
						if(player.upMulti >=2)
						{
							player.upMulti = 2.0f;
							engine.unregisterUpdateHandler(pTimerHandler);
						}
					}
		        	
		        }));
			}
			return(true);
		}
		if(pSceneTouchEvent.isActionUp()&&timeRemaining>0)
		{
			
			if(!this.isIgnoreUpdate())
			{
				
				player.up();
			}
			if(!isRunning)
			{
				isRunning = true;
				this.setIgnoreUpdate(false);
				this.detachChild(startText);
				this.detachChild(help);
			}
			return(true);
		}
		return false;
	}
	private void createGameOverText()
	{
	    gameOverText = new Text(0, 0, resourceManager.font, "Game Over!", vbom);
	}
	private void createStartText()
	{
		startText  = new Text(0, 0, resourceManager.font, "Touch to Start", vbom);
	}

	private void displayGameOverText()
	{
	    camera.setChaseEntity(null);
	    gameOverText.setPosition(camera.getCenterX(), camera.getCenterY()+50);
	    attachChild(gameOverText);
	    gameOverDisplayed = true;
	    if(this.isIgnoreUpdate() == false)
	    {
	    	//isRunning = false;
	    	this.setIgnoreUpdate(true);
	    }
	    saveData();
	}
	private void displayStartText()
	{
	    
	    help = new Sprite(camera.getCenterX(), camera.getCenterY(),ResourceManager.getInstance().help_region, vbom);
	    attachChild(help);
	    startText.setPosition(camera.getCenterX(), camera.getCenterY()-help.getHeight()/2);
	    attachChild(startText);
	}
/*	private ContactListener contactListener()
	{
	    ContactListener contactListener = new ContactListener()
	    {
	        public void beginContact(Contact contact)
	        {
	            final Fixture x1 = contact.getFixtureA();
	            final Fixture x2 = contact.getFixtureB();

	            if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
	            {
	                if (x1.getBody().getUserData().equals("platform3") && x2.getBody().getUserData().equals("player"))
	                {
	                    x1.getBody().setType(BodyType.DynamicBody);
	                }
	                if (x1.getBody().getUserData().equals("platform2") && x2.getBody().getUserData().equals("player"))
	                {
	                    engine.registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback()
	                    {                                    
	                        public void onTimePassed(final TimerHandler pTimerHandler)
	                        {
	                            pTimerHandler.reset();
	                            engine.unregisterUpdateHandler(pTimerHandler);
	                            x1.getBody().setType(BodyType.DynamicBody);
	                        }
	                    }));
	                }
	            }
	            
	        }

	        public void endContact(Contact contact)
	        {
	            final Fixture x1 = contact.getFixtureA();
	            final Fixture x2 = contact.getFixtureB();

	            if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
	            {

	            }
	        }

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
	    };
	    return contactListener;
	}*/
	public void createEnemy()
	{
		Random rand = new Random();
		float y = paths[rand.nextInt(13)];
		Sprite enemy = new Sprite(SCREEN_WIDTH, y, ResourceManager.getInstance().player_region, vbom)
        {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) 
            {
                super.onManagedUpdate(pSecondsElapsed);
                setPosition(getX()-(5f+Math.round(totalTime/30)), getY());
                
                if(getX()<0)
				{
					setVisible(false);
					setIgnoreUpdate(true);
				}
                if (player.collidesWith(this))
                {
                    addToTime(-30);
                    ResourceManager.getInstance().badSFX.play();
                    this.setVisible(false);
                    this.setIgnoreUpdate(true);
                }
            }
        };
        enemy.setScale(0.5f);
        enemy.setFlippedHorizontal(true);
		this.attachChild(enemy);
	}
	public void createItem()
	{
		Random rand = new Random();
		int item = rand.nextInt(20);
		//float y = (SCREEN_HEIGHT/(rand.nextInt((8-1)+1)+1) )+20;
		float y = paths[rand.nextInt(13)];
		Sprite power;
		switch(item)
		{
			case 1:case 2:
			power = new Sprite(SCREEN_WIDTH, y, ResourceManager.getInstance().timeup1_region, vbom)
            {
                @Override
                protected void onManagedUpdate(float pSecondsElapsed) 
                {
                    super.onManagedUpdate(pSecondsElapsed);
                    setPosition(getX()-(1f/*+Math.round(totalTime/60)*/), getY());
					if(getX()<0)
					{
						setVisible(false);
						setIgnoreUpdate(true);
					}
                    if (player.collidesWith(this))
                    {
                        addToTime(30);
                        ResourceManager.getInstance().hitSFX.play();
                        this.setVisible(false);
                        this.setIgnoreUpdate(true);
                    }
                }
            };
			break;
			
			case 3:
				power = new Sprite(SCREEN_WIDTH, y, ResourceManager.getInstance().timeup2_region, vbom)
	            {
	                @Override
	                protected void onManagedUpdate(float pSecondsElapsed) 
	                {
	                    super.onManagedUpdate(pSecondsElapsed);
	                    setPosition(getX()-(1f/*+Math.round(totalTime/60)*/), getY());
						if(getX()<0)
						{
							setVisible(false);
							setIgnoreUpdate(true);
						}
	                    if (player.collidesWith(this))
	                    {
	                        addToTime(60);
	                        ResourceManager.getInstance().hitSFX.play();
	                        this.setVisible(false);
	                        this.setIgnoreUpdate(true);
	                    }
	                }
	            };	
			break;
			case 4:case 5:case 6:case 7:case 8:
				power = new Sprite(SCREEN_WIDTH, y, ResourceManager.getInstance().timeup3_region, vbom)
	            {
	                @Override
	                protected void onManagedUpdate(float pSecondsElapsed) 
	                {
	                    super.onManagedUpdate(pSecondsElapsed);
	                    setPosition(getX()-(1f/*+Math.round(totalTime/60)*/), getY());
						if(getX()<0)
						{
							setVisible(false);
							setIgnoreUpdate(true);
						}
	                    if (player.collidesWith(this))
	                    {
	                        addToTime(5);
	                        ResourceManager.getInstance().hitSFX.play();
	                        this.setVisible(false);
	                        this.setIgnoreUpdate(true);
	                    }
	                }
	            };
				break;
				
				case 9:case 10:case 11:case 12:
					power = new Sprite(SCREEN_WIDTH, y, ResourceManager.getInstance().timeup4_region, vbom)
		            {
		                @Override
		                protected void onManagedUpdate(float pSecondsElapsed) 
		                {
		                    super.onManagedUpdate(pSecondsElapsed);
		                    setPosition(getX()-(1f/*+Math.round(totalTime/60)*/), getY());
							if(getX()<0)
							{
								setVisible(false);
								setIgnoreUpdate(true);
							}
		                    if (player.collidesWith(this))
		                    {
		                        addToTime(10);
		                        ResourceManager.getInstance().hitSFX.play();
		                        this.setVisible(false);
		                        this.setIgnoreUpdate(true);
		                    }
		                }
		            };	
				break;
			default:
				power = new Sprite(SCREEN_WIDTH, y, ResourceManager.getInstance().timedown1_region, vbom)
	            {
	                @Override
	                protected void onManagedUpdate(float pSecondsElapsed) 
	                {
	                    super.onManagedUpdate(pSecondsElapsed);
	                    setPosition(getX()-(1f/*+(Math.round(totalTime/60))*/), getY());
						if(getX()<0)
						{
							setVisible(false);
							setIgnoreUpdate(true);
						}
	                    if (player.collidesWith(this))
	                    {
	                        addToTime(-15);
	                        ResourceManager.getInstance().badSFX.play();
	                        this.setVisible(false);
	                        this.setIgnoreUpdate(true);
	                    }
	                }
	            };
			break;
	        	
		}
		power.setScale(3);
		this.attachChild(power);
		
	}
	public void updateTime()
	{
		if(timeRemaining>0)
		{
			timeRemaining--;
			remainText.setText("Time Left: "+timeRemaining/60+":"+(Integer.toString(timeRemaining%60).length()<2?"0"+timeRemaining%60:timeRemaining%60));
			if(timeRemaining <=15 && !remainText.getColor().equals(Color.RED))
			{
				remainText.setColor(Color.RED);
				//remainText.registerEntityModifier(mod);
			}
			if(timeRemaining >15 && remainText.getColor().equals(Color.RED))
			{
				remainText.setColor(Color.WHITE);
				//remainText.unregisterEntityModifier(mod);
			}
		}
		else if(!remainText.getColor().equals(Color.RED))
		{
			remainText.setColor(Color.RED);
		}
		totalTime++;
		totalTimeText.setText("Your Time: "+totalTime/60+":"+(Integer.toString(totalTime%60).length()<2?"0"+totalTime%60:totalTime%60));
	
		/*if(totalTime%120 == 0)
		{
			this.registerUpdateHandler(new TimerHandler(5f, new ITimerCallback() 
	        {
	            public void onTimePassed(final TimerHandler pTimerHandler) 
	            {
	                createItem();
	                pTimerHandler.reset();
	            }
	        }));

		    this.registerUpdateHandler(new TimerHandler(2.5f, new ITimerCallback() 
	        {
	            public void onTimePassed(final TimerHandler pTimerHandler) 
	            {
	                createEnemy();
	                pTimerHandler.reset();
	            }
	        }));
		}*/
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID())
        {
        case MENU_RETRY:
        	ResourceManager.getInstance().selectSFX.play();
        	cleanUp();
        	ResourceManager.getInstance().dbHelper.openDataBase();
        	createScene();
        	//this.setIgnoreUpdate(false);
        	
        	//SceneManager.getInstance().restartGameScene(engine);
            return true;
        case MENU_BACK:
        	ResourceManager.getInstance().selectSFX.play();
        	SceneManager.getInstance().loadMenuScene(engine);
            return true;
        default:
            return false;
        }
	}
	private void createMenuChildScene()
	{
	    menuChildScene = new MenuScene(camera);
	    menuChildScene.setPosition(400, 240);
	    
	    final IMenuItem retryMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_RETRY, resourceManager.retry_region, vbom), 1.2f, 1);
	    final IMenuItem backMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_BACK, resourceManager.back_region, vbom), 1.2f, 1);
	    
	    menuChildScene.addMenuItem(retryMenuItem);
	    menuChildScene.addMenuItem(backMenuItem);
	    
	    menuChildScene.buildAnimations();
	    menuChildScene.setBackgroundEnabled(false);

	    retryMenuItem.setPosition(retryMenuItem.getX()-400, retryMenuItem.getY()-250);
	    backMenuItem.setPosition(backMenuItem.getX()-400, backMenuItem.getY()-280);
	    menuChildScene.setOnMenuItemClickListener(this);
	    
	    setChildScene(menuChildScene);
	}
	private void cleanUp()
	{
		this.detachChildren();
		menuChildScene.back();
		
		timeRemaining = 30;
		totalTime = 0;
		isRunning=false;
		gameOverDisplayed = false;
		//Log.d("TEST","PLayer Y:"+ player.getY());
		//player.setY(SCREEN_HEIGHT-ResourceManager.getInstance().player_region.getHeight());
		//player.
		//updater.reset();
	    //itemMaker.reset();
	    //enemyMaker.reset();
	    //increase.reset();
		this.unregisterUpdateHandler(updater);
	    this.unregisterUpdateHandler(itemMaker);
	    this.unregisterUpdateHandler(enemyMaker);
	    //this.unregisterUpdateHandler(increase);
	    Log.d("TEST","Handlers Unregistered");
	    //physicsWorld.registerPhysicsConnector(player.fall);
	    player = new Player(SCREEN_WIDTH/10,SCREEN_HEIGHT-ResourceManager.getInstance().player_region.getHeight(), vbom, camera, physicsWorld)
	    {

			@Override
			public void onDie() {
				// TODO Auto-generated method stub
				if (!gameOverDisplayed)
                {
                    displayGameOverText();
                    saveData();
                    createMenuChildScene();
                    //Log.d("TEST","CALLED");
                    physicsWorld.unregisterPhysicsConnector(this.fall);
                    //unregisterUpdateHandler(updater);
            	    //unregisterUpdateHandler(itemMaker);
            	    //unregisterUpdateHandler(enemyMaker);
            	    //unregisterUpdateHandler(increase);
                }
			}
	    	
	    };
		//this.unregisterUpdateHandlers(pUpdateHandlerMatcher);
	}

}