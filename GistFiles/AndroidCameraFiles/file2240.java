package net.pixelstatic.Home;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.PixmapIO.PNG;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Sine;
import net.pixelstatic.Home.AI.Pathfinder;
import net.pixelstatic.Home.editor.aieditor.gen.GifSequenceWriter;
import net.pixelstatic.Home.entities.Entity;
import net.pixelstatic.Home.entities.EntityQueue;
import net.pixelstatic.Home.entities.Monster;
import net.pixelstatic.Home.entities.Player;
import net.pixelstatic.Home.entities.SolidEntity;
import net.pixelstatic.Home.entities.Syncable;
import net.pixelstatic.Home.gui.Box;
import net.pixelstatic.Home.gui.Button;
import net.pixelstatic.Home.gui.GUIElement;
import net.pixelstatic.Home.gui.Input;
import net.pixelstatic.Home.gui.MenuRenderer;
import net.pixelstatic.Home.gui.Text;
import net.pixelstatic.Home.gui.TextArea;
import net.pixelstatic.Home.gui.TextureElement.BAlign;
import net.pixelstatic.Home.io.Encryptor;
import net.pixelstatic.Home.io.Save;
import net.pixelstatic.Home.layers.CacheLayer;
import net.pixelstatic.Home.layers.Layer;
import net.pixelstatic.Home.layers.LayerList;
import net.pixelstatic.Home.layers.LightLayer;
import net.pixelstatic.Home.layers.RotatedCacheLayer;
import net.pixelstatic.Home.layers.SpriteLayer;
import net.pixelstatic.Home.packets.AbilityUsePacket;
import net.pixelstatic.Home.packets.AccountInfoPacket;
import net.pixelstatic.Home.packets.AttackCooldownPacket;
import net.pixelstatic.Home.packets.BagClickedPacket;
import net.pixelstatic.Home.packets.BagRemovePacket;
import net.pixelstatic.Home.packets.BlockDamagePacket;
import net.pixelstatic.Home.packets.BlockSetPacket;
import net.pixelstatic.Home.packets.ChatPacket;
import net.pixelstatic.Home.packets.ChunkPacket;
import net.pixelstatic.Home.packets.ChunkRequestPacket;
import net.pixelstatic.Home.packets.ConnectInfoPacket;
import net.pixelstatic.Home.packets.ConnectPacket;
import net.pixelstatic.Home.packets.CraftOpenPacket;
import net.pixelstatic.Home.packets.CraftPacket;
import net.pixelstatic.Home.packets.DashPacket;
import net.pixelstatic.Home.packets.DataPacket;
import net.pixelstatic.Home.packets.DeathPacket;
import net.pixelstatic.Home.packets.DisconnectPacket;
import net.pixelstatic.Home.packets.EffectApplyPacket;
import net.pixelstatic.Home.packets.EntityAnimationPacket;
import net.pixelstatic.Home.packets.EntityRemovePacket;
import net.pixelstatic.Home.packets.EntityUpdatePacket;
import net.pixelstatic.Home.packets.EquipListPacket;
import net.pixelstatic.Home.packets.IndicatorPacket;
import net.pixelstatic.Home.packets.InputPacket;
import net.pixelstatic.Home.packets.InventorySyncPacket;
import net.pixelstatic.Home.packets.ItemAddPacket;
import net.pixelstatic.Home.packets.ItemDropPacket;
import net.pixelstatic.Home.packets.KickPacket;
import net.pixelstatic.Home.packets.LoginInfoPacket;
import net.pixelstatic.Home.packets.LootDropPacket;
import net.pixelstatic.Home.packets.ParticleEffectPacket;
import net.pixelstatic.Home.packets.PlacePacket;
import net.pixelstatic.Home.packets.PlayerAnimationPacket;
import net.pixelstatic.Home.packets.PlayerDamagePacket;
import net.pixelstatic.Home.packets.PolygonChangePacket;
import net.pixelstatic.Home.packets.PolygonPacket;
import net.pixelstatic.Home.packets.PositionCorrectPacket;
import net.pixelstatic.Home.packets.PositionPacket;
import net.pixelstatic.Home.packets.RecipeLoadPacket;
import net.pixelstatic.Home.packets.SlotClickedPacket;
import net.pixelstatic.Home.packets.StatUpdatePacket;
import net.pixelstatic.Home.packets.TeleportPacket;
import net.pixelstatic.Home.packets.TokenPacket;
import net.pixelstatic.Home.packets.TokenVerificationPacket;
import net.pixelstatic.Home.packets.WorldUpdatePacket;
import net.pixelstatic.Home.projectiles.ProjectileType;
import net.pixelstatic.Home.settings.Settings;
import net.pixelstatic.Home.shaders.PostProcessor;
import net.pixelstatic.Home.shaders.effects.Bloom;
import net.pixelstatic.Home.shaders.effects.Light;
import net.pixelstatic.Home.shaders.effects.MotionBlur;
import net.pixelstatic.Home.shaders.effects.Outline;
import net.pixelstatic.Home.shaders.effects.Shadow;
import net.pixelstatic.Home.shaders.utils.ShaderLoader;
import net.pixelstatic.Home.util.InputType;
import net.pixelstatic.Home.util.SyncBuffer;
import net.pixelstatic.Home.weapons.WeaponSpriter;

public class Home extends ApplicationAdapter implements InputProcessor{
	ShaderProgram shade;
	SpriteBatch batch;
	PolygonSpriteBatch polybatch;
	static public int cursor = -1;
	static public Player player;
	public static BetterAtlas textures;
	public static World world;
	static boolean[][] chunkloaded;
	float torchtime;
	public static OrthographicCamera camera;
	public Client client;
	static final int port = 7576;
	static ArrayList<Tile> tiles = new ArrayList<Tile>();
	static HashMap<String, Integer> tilenames = new HashMap<String, Integer>();
	static StringBuffer console = new StringBuffer();
	static public ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<Integer, Player>();
	static HashMap<String, FrameBuffer> buffers = new HashMap<String, FrameBuffer>();
	static public LayerList sprites = new LayerList();
	public static BitmapFont font, rausfont;
	String ip = System.getProperty("user.name").equals("anuke") ? "localhost" : "107.11.42.20"; //"75.179.179.175";
	static public boolean connected;
	boolean failed;
	// ConcurrentHashMap<Integer,String> Names = new
	// ConcurrentHashMap<Integer,String>();
	public static SpriteBatch gui;
	static public int chargetime;
	static ArrayList<ProjectileType> types = new ArrayList<ProjectileType>();
	CopyOnWriteArrayList<DamageIndicator> indicators = new CopyOnWriteArrayList<DamageIndicator>();
	static CopyOnWriteArrayList<TextureIndicator> textureindicators = new CopyOnWriteArrayList<TextureIndicator>();
	ArrayList<ChatMessage> chat = new ArrayList<ChatMessage>();
	boolean ChatOpen;
	String currentChat = "";
	int lastslotx, lastsloty;
	public static boolean InventoryOpen;
	static public boolean recievedData;
	Items items;
	float guiX, guiY, bagX, selectX, selectY;
	boolean lclick, rclick;
	int xrange = 20, xro;
	int yrange = 20, yro;
	static CopyOnWriteArrayList<LootBag> bags = new CopyOnWriteArrayList<LootBag>();
	public static LootBag selectedbag;
	String errorMessage = "", accounterror = "";
	// int atime;
	// static HashMap<Byte,DropType> drops = new HashMap<Byte,DropType>();
	int flashtime;
	int[] abilitycooldown = new int[4];
	int effectime, shaketime;
	byte effectype;
	TweenManager tweenManager = new TweenManager();
	static HashMap<Integer, SoundData> monstersounds = new HashMap<Integer, SoundData>();
	static CopyOnWriteArrayList<ParticleLayer> particles = new CopyOnWriteArrayList<ParticleLayer>();
	static ConcurrentHashMap<Integer, PolygonEffect> polygons = new ConcurrentHashMap<Integer, PolygonEffect>();
	static public HashMap<String, ParticleEffectPool> particletypes = new HashMap<String, ParticleEffectPool>();
	static public ArrayList<Cursor> cursors = new ArrayList<Cursor>();
	static HashMap<String, Sound> sounds = new HashMap<String, Sound>();
	static HashMap<String, Music> music = new HashMap<String, Music>();
	Sprite currentSprite;
	int speedtime;
	float speed;
	Color flashcolor = Color.WHITE;
	int selectedScreen;
	int bckspacePressed;
	int divis = 3;
	public static int datatime;
	float aspectRatio;
	public static boolean debug = false;
	boolean accountconnected;
	int selectedAbility;
	int mode;
	int pixels = 5;
	int[] customvalues = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
	int[] maxvalues = new int[]{7, 22, 5, 22, 2, 22, 13, 2};
	RGB[] skincolors = new RGB[]{new RGB(0.9f, 0.8f, 0.5f), new RGB(0.8f, 0.7f, 0.4f), new RGB(0.6f, 0.4f, 0.3f),
			new RGB(0.4f, 0.3f, 0.2f), new RGB(0.6f, 0.3f, 0.3f), new RGB(0.2f, 0.2f, 0.4f), new RGB(0.2f, 0.4f, 0.6f),
			new RGB(0.2f, 0.5f, 0.3f), new RGB(0.4f, 0.6f, 0.3f), new RGB(0.7f, 0.6f, 0.0f), new RGB(0.6f, 0.3f, 0.0f),
			new RGB(0.5f, 0.1f, 0.1f), new RGB(0.3f, 0.3f, 0.3f), new RGB(0.6f, 0.6f, 0.6f), new RGB(0.9f, 0.9f, 0.9f)};
	RGB[] robecolors = new RGB[]{new RGB(0.3f, 0.4f, 0.7f), new RGB(0.1f, 0.2f, 0.5f), new RGB(0.2f, 0.2f, 0.8f),
			new RGB(0.5f, 0.5f, 0.8f), new RGB(0.3f, 0.6f, 0.7f), new RGB(0, 190, 85), new RGB(0, 128, 128),
			new RGB(0.2f, 0.5f, 0.5f), new RGB(0.1f, 0.4f, 0.3f), new RGB(0.2f, 0.6f, 0.2f), new RGB(0.4f, 0.6f, 0.3f),
			new RGB(0.5f, 0.5f, 0.1f), new RGB(0.7f, 0.5f, 0.2f), new RGB(0.3f, 0.1f, 0.1f), new RGB(0.5f, 0.2f, 0.2f),
			new RGB(0.7f, 0.3f, 0.3f), new RGB(0.6f, 0.1f, 0.1f), new RGB(0.6f, 0.2f, 0.5f),
			new RGB(0.45f, 0.1f, 0.5f), new RGB(0.4f, 0.0f, 0.2f), new RGB(0.2f, 0.2f, 0.2f),
			new RGB(0.4f, 0.4f, 0.4f), new RGB(0.6f, 0.6f, 0.6f), new RGB(0.8f, 0.8f, 0.8f)};
	// RGB[] robecolors = new RGB[]{new RGB(50,50,50), new RGB(128,128,128), new
	// RGB(175,175,175), new RGB(255,255,255), new RGB(130,40,40), new
	// RGB(128,60,0), new RGB(255,80,0), new RGB(255,128,0), new RGB(255,200,0),
	// new RGB(150,225,50), new RGB(30,200,30), new RGB(0,255,128), new
	// RGB(0,128,128), new RGB(0,150,255), new RGB(0,80,128), new
	// RGB(50,50,180), new RGB(128,230,230), new RGB(120,120,230), new
	// RGB(150,70,230), new RGB(180,0,255), new RGB(230,70,230)};
	static CopyOnWriteArrayList<HitAnimation> hits = new CopyOnWriteArrayList<HitAnimation>();
	float minimapx, minimapy;
	ShapeRenderer minimap;
	static HashMap<Integer, Color> minimapcolors = new HashMap<Integer, Color>();
	static Color[][] displaycolors;
	int antime;
	public Texture fog, fog1, fog2, fog3, sky, vsky, vfog, vfog1, vfog2;
	float distort;
	boolean fullscreen = true;
	float torchsize;
	float ambientIntensity = 1f;
	Vector3 ambientColor = new Vector3(0.0005f, 0.0005f, 0.002f);
	static public Sprite light;
	int torchframe;
	static Vertice[] tweenmoves = new Vertice[12];
	public static HashMap<String, Sprite> spritemap = new HashMap<String, Sprite>();
	static public int attacktype = 0;
	static public boolean chargetype; // false=left, true=right
	int pixelsize = 5;
	int magmacolor;
	int originallavax;
	float lavax;
	int owaterx;
	float waterx;
	// boolean[] times = new boolean[4];
	static public boolean loaded;
	static public float deltafloat;
	Pixmap temp;
	Texture temptex;
	Color coloradd = new Color(245 / 255f, 150 / 255f, 90 / 255f, 1f);
	static float lightrange;
	ArrayList<LightLayer> lights = new ArrayList<LightLayer>();
	boolean craftopen;
	String recipetype;
	ArrayList<Recipe> recipes;
	int selectedrecipe;
	int pushtime;
	Sprite point;
	float shadowx, shadowy;
	boolean valid;
	float zoomx = 5;
	static public Home h;
	ShaderProgram shader;
	PostProcessor postProcessor;
	Light lighteffect;
	Shadow shadoweffect;
	Outline outlineeffect;
	static public int dashcooldown, attackcooldown;;
	boolean attacking;
	static public Texture blank;
	float fadescale = 1.9f;
	float chatfade;
	float chatwidth = 710;
	static final int messagelength = 20, messageduration = 270;
	int tooltiptime = 0, tipx = -1, tipy = -1, totaltip = 50;
	static Matrix4 square;
	float lastfade;
	static public float fontmessagescale = 1f, fontspacing = 4f;
	static int chatamount = 8;
	Bloom bloom;
	float framer;
	static public String error = "";
	static boolean crash;
	static boolean menuopen;
	Exception exc;
	static public GlyphLayout glyphs;
	int playerwidth = 8, playerheight = 8;
	int[][] bodypixels = new int[playerwidth][playerheight];
	static final int maxdataping = 500;
	static int leftmousetime, rightmousetime;
	static public Sprite terrain;
	float rightpadx, rightpady, leftpadx, leftpady;
	public static int rightpointer = -1, leftpointer = -1;
	static public float walkangle, shootangle;
	static int padsize = 40;
	static public int lastping = 0;
	static public int nearpix = 0;
	float chunkloadtime = 0;
	String lastpacket;
	static public float bloomintensity;
	EntityQueue queue = new EntityQueue();
	static final float slotsize = 16;
	int layercount;
	static final float cameraspeed = 0.1f;
	static Vector2 camerashake = new Vector2();
	float gifx, gify, gifwidth, gifheight, giftime;
	boolean recording, ingif;
	String gifexport = null;
	ArrayList<Pixmap> recordframes = new ArrayList<Pixmap>();
	static final int gif_fps = 20;

	void ClearAll(){
		players.clear();
		player.statuseffects.clear();
		chat.clear();
		polygons.clear();
		spritemap.clear();
		sprites.clear();
		Entity.entities.clear();
		currentChat = "";
		ChatOpen = false;
		player.messagetime = 0;
		for(int x = 0;x < chunkloaded.length;x ++){
			for(int y = 0;y < chunkloaded[x].length;y ++){
				chunkloaded[x][y] = false;
			}
		}
	}

	@Override
	public void resize(int width, int height){
		ResizeScreen(width, height, fullscreen);
	}

	void LoadMusic(){
		CreateSound("menuclick");
		CreateSound("menuconnect");
		CreateSound("playerdie");
		CreateMusic("sine");
		SetMonsterSound(27, "jellyhit", "jellydeath");
		SetMonsterSound(28, "jellyhit", "jellydeath");
		// LoopMusic("menu");
	}

	/*
	    void CreateAttributes(){
		SetAttribute(6, "10frames");
		SetAttribute(6, "darkencompletely");
		SetAttribute(27, "noanimation");
		SetAttribute(28, "noanimation");
		SetAttribute(29, "noanimation");
		SetAttribute(29, "invisible");
		SetAttribute(30, "noanimation");
		SetAttribute(31, "noanimation");
		SetAttribute(32, "noanimation");
		SetAttribute(30, "invisible");
		SetAttribute(31, "invisible");
		SetAttribute(32, "invisible");
		SetAttribute(27, "fade");
		SetAttribute(28, "fade");
		SetAttribute(25, "fade");
		SetAttribute(29, "darken");
		SetAttribute(32, "darken");
		SetAttribute(29, "straight");
		SetAttribute(30, "straight");
		SetAttribute(31, "straight");
		SetAttribute(32, "straight");
		SetAttribute(34, "noanimation");
		// SetAttribute(35,"noanimation");
		SetAttribute(34, "flash");
		SetAttribute(35, "flash");
		// SetAttribute(35,"case");
		// SetAttribute(35,"endflash");
		// SetAttribute(34,"darken");
		SetAttribute(35, "darken");
		SetAttribute(34, "fade");
		SetAttribute(35, "slowfade");

		SetAttribute(33, "stay");
		SetAttribute(34, "stay");
		SetAttribute(35, "stay");
		SetAttribute(36, "stay");
		SetAttribute(38, "stay");
		SetAttribute(36, "noangle");
		SetAttribute(37, "invisible");
		SetAttribute(39, "6frames");
		SetAttribute(40, "10frames");
		SetAttribute(40, "flip");
		SetAttribute(40, "hover");
		// SetAttribute(41,"darken");
		SetAttribute(41, "slowfade");
		SetAttribute(41, "noanimation");
		SetAttribute(42, "slowfade");
		SetAttribute(42, "darken2");
		SetAttribute(44, "noanimation");
		SetAttribute(44, "flash");
		SetAttribute(44, "darken");
		SetAttribute(44, "slowfade");
		SetAttribute(45, "stay");
		SetAttribute(45, "foreground");
		SetAttribute(45, "flash");
		// SetAttribute(45,"shake");
		SetAttribute(46, "stay");
		SetAttribute(46, "foreground");
		SetAttribute(46, "reverse");
		SetAttribute(23, "arrow");
		SetAttribute(47, "endflash");
		SetAttribute(49, "6frames");
		SetAttribute(48, "6frames");
		SetAttribute(49, "flash");
		SetAttribute(50, "flash");
		SetAttribute(51, "flash");
		SetAttribute(52, "flash");
		SetAttribute(50, "6frames");
		SetAttribute(51, "6frames");
		SetAttribute(53, "noanimation");
		SetAttribute(53, "straight");
		SetAttribute(53, "slowfade");
		SetAttribute(53, "darken");
		SetAttribute(54, "noanimation");
		SetAttribute(54, "endflash");
		SetAttribute(54, "flicker");
		SetAttribute(55, "transparent");
		SetAttribute(57, "2fade");
		SetAttribute(58, "8frames");
		SetAttribute(58, "fade");
		// SetAttribute(59,"noanmiation");
		SetAttribute(59, "fade");
		SetAttribute(60, "fade");
		// SetAttribute(59,"straight");
		// SetAttribute(49,"stay");

	    }
	    */

	void CreateMinimapColors(){
		CreateParticleType("flurry");
		CreateParticleType("ember");
		CreateParticleType("flame");
		CreateParticleType("wisp");
		CreateParticleType("spark");
		CreateParticleType("spark2");
		CreateParticleType("arrowbreak");
		CreateParticleType("orebreak");
		CreateParticleType("menufire", "");
		CreateParticleType("glimmer", "");
		CreateParticleType("glimmer2", "");
		CreateParticleType("dash");
		MinimapColors.CreateColors();
	}

	@Override
	public void render(){
		try{
			DoUniversalInput();
			if( !crash){
				if(connected && recievedData && MenuRenderer.ready){
					DoInput();
					UpdateCameraPosition();
					Draw();
					SendPosition();
				}else{
					DrawMainMenu();
					if( !loaded){
						DrawLoadScreen();
					}
				}
				if(Settings.displayconsole) DrawConsole();
				Input.Update();
			}else{
				DrawException();
			}
		}catch(Exception e){
			HandleException(e);
		}
	}

	void DrawExtraDebug(){
		gui.begin();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		font.draw(gui, Gdx.graphics.getFramesPerSecond() + " fps (" + Math.round(Gdx.graphics.getDeltaTime() * 60) + ")", Gdx.graphics.getWidth() / 2 - bounds(font, Gdx.graphics.getFramesPerSecond() + " fps") / 2, Gdx.graphics.getHeight() / 2);
		gui.end();
	}

	void HandleException(Exception e){
		crash = true;
		Disconnect();
		log("crash");
		if(sprites != null) sprites.clear();
		postProcessor.captureEnd();
		exc = e;
		e.printStackTrace();
		try{
			FileHandle file = Gdx.files.local("error.log");
			file.writeString(exc.toString() + "\n", false);
			for(int i = 0;i < exc.getStackTrace().length;i ++){
				file.writeString(exc.getStackTrace()[i] + "\n", true);
			}

		}catch(Exception e2){
			e2.printStackTrace();
		}
	}

	void DrawException(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Keys.BACK)){
			Quit();
		}
		if(gui.isDrawing()) gui.end();
		if(exc == null || exc.getStackTrace() == null) return;

		gui.begin();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		font.getData().setScale(1f);
		String line = exc.toString();
		font.draw(gui, line, Gdx.graphics.getWidth() / 2 - bounds(font, line) / 2, Gdx.graphics.getHeight() / 2);
		for(int i = 0;i < exc.getStackTrace().length;i ++){
			font.draw(gui, exc.getStackTrace()[i] + "", Gdx.graphics.getWidth() / 2 - bounds(font, exc.getStackTrace()[i] + "") / 2, Gdx.graphics.getHeight() / 2 - 40 * (i + 1));
		}
		gui.end();
	}

	void DrawConsole(){
		gui.begin();
		font.getData().setScale(0.5f);
		glyphs.setText(font, console, Color.WHITE, Gdx.graphics.getWidth() / 2, Align.topLeft, true);
		if(glyphs.height > Gdx.graphics.getHeight()) console.setLength(0);
		font.setColor(Color.ORANGE);
		font.draw(gui, console, 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth() / 2, Align.topLeft, true);
		gui.end();
		font.getData().setScale(1f);
		font.setColor(Color.WHITE);
	}

	void DoUniversalInput(){

		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE) && Settings.GetValue("fastquit", Boolean.class)){
			Quit();
		}

		// screen size change
		if(Gdx.input.isKeyJustPressed(Keys.F1)){
			if( !fullscreen){
				ResizeScreen(Gdx.graphics.getDisplayMode().width, Gdx.graphics.getDisplayMode().height, true);
			}else{
				ResizeScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - 25, false);
			}
		}

		// debug menu
		if(Gdx.input.isKeyJustPressed(Keys.INSERT)){
			Settings.displayconsole = !Settings.displayconsole;
		}
		if(connected && !recievedData){
			datatime += Math.round(Gdx.graphics.getDeltaTime() * 60);
			MenuRenderer.stage = "Loading server data...";
			if(datatime > maxdataping){
				connected = false;
				datatime = 0;
				MenuRenderer.stage = "Failed to recieve data!";
				log("Failed to recieve data!");
			}
		}
	}

	public void SetCursor(int frame){
		if(cursor != frame) Gdx.graphics.setCursor(cursors.get(frame));
		cursor = frame;
	}

	void AddCursor(String name){
		Pixmap p = new Pixmap(Gdx.files.internal("sprites/" + name + ".png"));
		cursors.add(Gdx.graphics.newCursor(p, 32, 32));
		p.dispose();
	}

	void LoadCursors(){
		Pixmap p = new Pixmap(Gdx.files.internal("sprites/cursor.png"));
		Pixmap glow = new Pixmap(Gdx.files.internal("sprites/cursor-glow.png"));
		for(int i = 0;i < 11;i ++){
			Cursor c = null;
			Pixmap clone;
			if(i != 0){
				clone = DrawAlpha(glow, p, i / 10f);
			}else{
				clone = p;
			}
			c = Gdx.graphics.newCursor(clone, 32, 32);
			cursors.add(c);
			if(p != clone){
				clone.dispose();
			}
		}
		p.dispose();
		glow.dispose();
		AddCursor("cursor-inventory");
		SetCursor(11);
	}

	void SetAlpha(Pixmap p, float a){
		for(int x = 0;x < p.getWidth();x ++){
			for(int y = 0;y < p.getHeight();y ++){
				Color c = new Color(p.getPixel(x, y));
				if(c.a > 0){
					c.a = a;
					p.drawPixel(x, y, Color.rgba8888(c));
				}
			}
		}
	}

	Color mix(Color a, Color b, float m){
		float r = 1f - m;
		return new Color(r * a.r + m * b.r, r * a.g + m * b.g, r * a.b + m * b.b, 1f);
	}

	Pixmap DrawAlpha(Pixmap p, Pixmap source, float a){
		Pixmap clone = new Pixmap(p.getWidth(), p.getHeight(), Format.RGBA8888);
		for(int x = 0;x < p.getWidth();x ++){
			for(int y = 0;y < p.getHeight();y ++){
				Color to = new Color(source.getPixel(x, y));
				clone.setColor(to);
				clone.drawPixel(x, y);
				Color from = new Color(p.getPixel(x, y));
				from.a *= a;
				if(a == 1f){
					from.r = 1f;
					if(to.a > 0.5f && from.a == 0){
						from.a = 0.7f;
					}
				}
				clone.setColor(from);
				clone.drawPixel(x, y);
			}
		}
		return clone;
	}

	void CreateObjects(){
		h = this;
		Colors.put("LIGHTBLUE", new Color(51 / 255f, 153 / 255f, 1f, 1));
		Colors.put("BLUE", new Color(0, 102 / 255f, 204 / 255f, 1f));
		glyphs = new GlyphLayout();
		square = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());;
		minimap = new ShapeRenderer();
		displaycolors = new Color[46][46];
		player = new Player();
		player.IsOwn = true;
		batch = new SpriteBatch(2900);
		polybatch = new PolygonSpriteBatch();
		gui = new SpriteBatch();
		aspectRatio = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / zoomx, Gdx.graphics.getHeight() / zoomx);
		world = new World();
		terrain = new Sprite();

		for(int i = 0;i < 12;i ++){
			tweenmoves[i] = new Vertice();
		}
		//tilethread = new TileDrawThread();
		//new Thread(tilethread).start();
		Log.setLogger(new Loggy());
		Log.INFO();
		LoadPlayerPixels();
		LoadCursors();
	}

	void LoadTextures(){
		blank = new Texture("sprites/blank.png");
		blank.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		fog = new Texture("sprites/fog.png");
		fog.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		fog1 = new Texture("sprites/fog1.png");
		fog1.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		fog2 = new Texture("sprites/fog2.png");
		fog2.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		fog3 = new Texture("sprites/fog3.png");
		fog3.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		sky = new Texture("sprites/sky.png");
		sky.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		vfog = new Texture("sprites/vmist1.png");
		vfog.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		vfog1 = new Texture("sprites/vmist2.png");
		vfog1.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		vfog2 = new Texture("sprites/vmist3.png");
		vfog2.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		vsky = new Texture("sprites/vbackground.png");
		vsky.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		String n = "raus";
		rausfont = new BitmapFont(Gdx.files.internal("fonts/" + n + "font.fnt"), Gdx.files.internal("fonts/" + n + "font.png"), false);
		font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"), Gdx.files.internal("fonts/font.png"), false);
		textures = new BetterAtlas(Gdx.files.internal("sprites/Sprites.pack"));
		light = new Sprite(new Texture("light.png"));
		point = new Sprite(textures.findRegion("blank"));
		point.setSize(1, 1);
		CreateTileTransitions();
	}

	void Initialize(){
		WeaponSpriter.RegisterWeapons();
		Settings.Create();
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		CreateMinimapColors();
		PolygonEffect.Load();
		InitShader();
		Gdx.input.setInputProcessor(this);
		Tween.registerAccessor(Vertice.class, new SpriteAccessor());
		Tween.call(windCallback).start(tweenManager);
		LoadAccountDetails();
		Encryptor.init();
		Gdx.input.setCatchBackKey(true);
		new Pathfinder();
	}

	void CreateClient(){
		client = new Client(1024 * 1024, 1024 * 1024);
		client.setTimeout(10000);
		client.start();
		Registrator.Register(client.getKryo());
		new Thread(new songLoader()).start();
		client.addListener(new Network());
	}

	void LoadPlayerPixels(){
		Texture player = new Texture("sprites/playerpixels.png");
		player.getTextureData().prepare();
		Pixmap p = player.getTextureData().consumePixmap();
		for(int x = 0;x < playerwidth;x ++){
			for(int y = 0;y < playerheight;y ++){
				Color c = new Color(p.getPixel(x, y));
				if(c.a > 0.5f){

					if(c.r == 1f && c.g == 1f && c.b == 1f){
						// is face
						bodypixels[x][playerheight - 1 - y] = 1;
					}else if(c.r == 1f && c.b == 1f){
						// is arms
						bodypixels[x][playerheight - 1 - y] = 2;
					}else if(c.r == 0f && c.b == 0f && c.g == 0f){
						// is hands
						bodypixels[x][playerheight - 1 - y] = 3;
					}else if(c.r == 1f){
						// is body
						bodypixels[x][playerheight - 1 - y] = 4;
					}else if(c.b == 1f){
						// is head
						bodypixels[x][playerheight - 1 - y] = 5;
					}else if(c.g == 1f){
						// is legs
						bodypixels[x][playerheight - 1 - y] = 6;
					}
				}
			}
		}
	}

	void TakeScreenshot(){
		Pixmap p = ScreenUtils.getFrameBufferPixmap(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		PNG f = new PNG();
		f.setFlipY(true);
		try{
			log("Screenshot taken.");
			f.write(Gdx.files.local("home-screenshot-" + sdf.format(Calendar.getInstance().getTime()) + ".png"), p);
		}catch(Exception e){
			e.printStackTrace();
		}
		p.dispose();
	}

	void UpdateValues(){

		deltafloat = (Gdx.graphics.getDeltaTime() * 60f);

		if((int)(100f / Entity.Deltafloat()) != 0 && Gdx.graphics.getFrameId() % (int)(100f / Entity.Deltafloat()) == 0){
			client.updateReturnTripTime();
		}
		minimapx = Gdx.graphics.getWidth() - scale() * 48;
		minimapy = Gdx.graphics.getHeight() - scale() * 48;
		float sc = camera.zoom;
		sc += 0.2f;
		if(sc > 1f) sc = 1f;
		xrange = (int)(xro * sc);
		yrange = (int)(yro * sc);
		Time.Count();
		ambientColor.x = Time.skyColor();
		ambientColor.y = Time.skyColor();
		ambientColor.z = Time.skyColor();
		if(pushtime > 0) pushtime --;
		if( !Settings.android){
			for(int x = 0;x < 46;x ++)
				for(int y = 0;y < 46;y ++)
					displaycolors[x][y] = null;
		}
		if( !client.isConnected()){
			errorMessage = "Timed out.";
			Disconnect();
			log("Client connection timed out!");
			MenuRenderer.DisconnectError("Connection timed out.");
		}

		bagX = ((Gdx.input.getX()) - ((Gdx.graphics.getWidth() / 2 - textures.findRegion("dropbar").getRegionWidth() * 2.5f + 15))) / 60;
		selectX = ((Gdx.input.getX()) - ((Gdx.graphics.getWidth() / 2 - textures.findRegion("recipes").getRegionWidth() * scale(0.5f) + scale(2)))) / scale(12);
		selectY = (Gdx.graphics.getHeight() - Gdx.input.getY() - (Gdx.graphics.getHeight() / divis + scale(8f / 5f))) / scale(12);
		guiX = (Gdx.input.getX() - scale() * 2) / (scale() * slotsize);
		guiY = (((Gdx.graphics.getHeight() - Gdx.input.getY()) - scale() * 2) / (scale() * slotsize));
		if(effectime > 0){
			effectime --;
		}
		for(int i = 0;i < 4;i ++){
			if(abilitycooldown[i] > 0){
				abilitycooldown[i] --;
			}
		}
	}

	void RequestChunks(){
		chunkloadtime += deltafloat;
		if(chunkloadtime > 60){
			SendChunkRequest(false);
			chunkloadtime = 0;
		}
	}

	void SendChunkRequest(boolean update){
		if(update) UpdateCameraPosition();
		for(int x = -3;x <= 3;x ++){
			for(int y = -3;y <= 3;y ++){
				if((int)(camera.position.x / 120 + x) >= 0 && (int)(camera.position.y / 120 + y) >= 0 && (int)(camera.position.x / 120 + x) < world.worldwidth / 10 && (int)(camera.position.y / 120 + y) < world.worldheight / 10){
					if( !chunkloaded[(int)(camera.position.x / 120 + x)][(int)(camera.position.y / 120 + y)]){
						ChunkRequestPacket p = new ChunkRequestPacket();
						p.x = (int)(camera.position.x / 120 + x) * 10;
						p.y = (int)(camera.position.y / 120 + y) * 10;
						client.sendUDP(p);
					}
				}
			}
		}
	}

	void UpdatePlayer(){
		if( !CanMove()) return;

		player.Input();
		Vector3 u = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		if((player.attackframe > 0 || chargetime > 0 || (CanAttack() && (Gdx.input.isButtonPressed(Buttons.LEFT) || Gdx.input.isButtonPressed(Buttons.RIGHT)))) && !InventoryOpen && !Settings.android){
			//float ang = MathUtils.atan2(u.x - player.x, u.y - player.y - 5);
			//log((u.x - player.x) + ", " + (u.y - player.y));
			//log(u);
			float ang = new Vector2(u.x, u.y).angle();
			ang -= 90f;
			ang = 360f-ang;
			if(ang > 180) ang -= 360f;
			
			player.UpdateDir(ang);
		}
	}

	void UpdateChat(){
		if(CanChat()){
			if(Gdx.input.isKeyJustPressed(Keys.ENTER) && !ChatOpen){
				if( !Settings.android){
					ChatOpen = true;
					lastfade = chatfade;
					currentChat = "";
					//  if(Gdx.input.isKeyPressed(Keys.P)) currentChat = "very very very very very very very very very very very very very very very very very very very very very very very long chat";
				}
			}else if(Gdx.input.isKeyJustPressed(Keys.ENTER) && currentChat.length() > 0 && ChatOpen){
				ChatPacket p = new ChatPacket();
				p.message = currentChat;
				client.sendTCP(p);
				ChatOpen = false;
				currentChat = "";
				chatfade = lastfade;
				chatfade += fadescale;
				if(chatfade > chatamount * fadescale){
					chatfade = chatamount * fadescale;
				}
				Gdx.input.setOnscreenKeyboardVisible(false);
			}else if(Gdx.input.isKeyJustPressed(Keys.ENTER) && ChatOpen){
				ChatOpen = false;
				currentChat = "";
				chatfade = lastfade;
				Gdx.input.setOnscreenKeyboardVisible(false);
			}

		}
	}

	void DoInput(){
		// menu
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE) || (Settings.android && Gdx.input.isKeyJustPressed(Keys.BACK))){
			menuopen = !menuopen;
		}
		UpdateCursor();
		UpdateValues();
		RequestChunks();
		player.Update();
		if(menuopen) return;
		UpdatePlayer();
		UpdateChat();
		if(Settings.android && CanMove() && !InventoryOpen) DoStickInput();
		if( !InventoryOpen) DoWeaponInput();

		if(Gdx.input.isKeyJustPressed(Keys.SPACE)){
			//  for(int i = 0;i < 10;i ++)
			//	new LightningTree(player.x, player.y, angle(), true).AddSelf();
		}

		// debug menu
		if(Gdx.input.isKeyJustPressed(Keys.TAB)){
			debug = !debug;
		}

		// gif menu
		if(Gdx.input.isKeyJustPressed(Keys.F3)){
			if( !ingif){
				float size = 300;
				gifx = Gdx.graphics.getWidth() / 2 - size / 2;
				gify = Gdx.graphics.getHeight() / 2 - size / 2;
				gifwidth = size;
				gifheight = size;
				ingif = true;
			}else{
				ingif = false;
				giftime = 0;
				gifexport = null;
				ClearGifFrames();
			}
		}

		if(Gdx.input.isKeyJustPressed(Keys.R) && ingif){
			if(recording){
				EndRecord();
			}else{
				recording = true;
				giftime = 0;
				ClearGifFrames();
			}
		}

		//close android chat
		if(ChatOpen && Settings.android && Gdx.input.isKeyJustPressed(Keys.BACK)){
			currentChat = "";
			ChatOpen = false;
		}
		// ability type change
		if(Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT) && !ChatOpen){
			if(mode == 0){
				mode = 1;
			}else if(mode == 1){
				mode = 0;
			}
		}
		//close craft menu
		if(Gdx.input.isKeyJustPressed(Keys.R) && craftopen){
			craftopen = false;
		}

		// open inventory
		if(Gdx.input.isKeyJustPressed(Keys.E) && CanOpenInventory()){
			if( !InventoryOpen) SendInput(InputType.LEFTCLICKUP);
			InventoryOpen = !InventoryOpen;
		}

		if( !Gdx.input.isButtonPressed(Buttons.LEFT)) lclick = true;

		if( !Gdx.input.isButtonPressed(Buttons.RIGHT)) rclick = true;

		Vector3 u = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		// craft menu clicked
		if(lclick && craftopen && Gdx.input.isButtonPressed(Buttons.LEFT) && selectX >= 0 && selectY >= 0 && selectX < 7 && selectY < 5){
			SelectClicked((int)selectX, (int)selectY);
		}
		// recipe clicked
		if(lclick && Gdx.input.isButtonPressed(Buttons.LEFT) && craftopen && selectX >= 2 && selectX <= 5 && ((Gdx.graphics.getHeight() - Gdx.input.getY() - (Gdx.graphics.getHeight() / divis + (scale(8f / 5)))) / scale(12)) < 0 && ((Gdx.graphics.getHeight() - Gdx.input.getY() - (Gdx.graphics.getHeight() / divis + scale(8 / 5f))) / scale(12)) >= -1 && selectedrecipe != -1 && ContainsItems(recipes.get(selectedrecipe).requirements) && !InventoryFull()){
			pushtime = 6;
			CraftClicked();
		}
		// block placed
		if(Gdx.input.isKeyJustPressed(Keys.F) && !ChatOpen){
			PlacePacket p = new PlacePacket();
			p.x = (int)(u.x / 12);
			p.y = (int)(u.y / 12);
			client.sendTCP(p);
		}
		// bag clicked
		if(selectedbag != null && Gdx.input.isButtonPressed(Buttons.LEFT) && lclick && !InventoryFull()){
			if((int)bagX >= 0 && (int)bagX <= 4 && (int)guiY == 0){
				BagClickedPacket p = new BagClickedPacket();
				p.id = selectedbag.id;
				p.x = (byte)bagX;
				client.sendUDP(p);
			}
		}
		// inventory slot
		if((Gdx.input.isButtonPressed(Buttons.LEFT) || (player.selecteditem != null && Settings.android && !Gdx.input.isTouched())) && guiX >= 0 && guiY >= 0 && guiX < 5 && guiY < 6 && lclick && InventoryOpen){
			SlotClicked((int)guiX, (int)guiY);
		}
		// item dropped
		if((Gdx.input.isButtonPressed(Buttons.LEFT) || (Settings.android && !Gdx.input.isTouched())) && (guiX >= 5 || guiY >= 6) && lclick && InventoryOpen && player.selecteditem != null){
			ItemDropped();
		}

		if(Gdx.input.isButtonPressed(Buttons.LEFT)) lclick = false;
		if(Gdx.input.isButtonPressed(Buttons.RIGHT)) rclick = false;

		if(Gdx.input.isKeyJustPressed(Keys.SPACE)){
			AbilityUsed(selectedAbility);
		}
		if(Gdx.input.isKeyJustPressed(Keys.NUM_1)){
			AbilityUsed(0);
		}
		if(Gdx.input.isKeyJustPressed(Keys.NUM_2)){
			AbilityUsed(1);
		}
		if(Gdx.input.isKeyJustPressed(Keys.NUM_3)){
			AbilityUsed(2);
		}
		if(Gdx.input.isKeyJustPressed(Keys.NUM_4)){
			AbilityUsed(3);
		}
	}

	void UpdateCursor(){
		if(InventoryOpen){
			SetCursor(11);
		}else if(player.GetCursor() != -1){
			SetCursor(player.GetCursor());
		}else if(player.dashcooldown > 0 && !InventoryOpen){
			SetCursor((int)(player.dashcooldown / Player.dash_cooldown * 10));
		}else{
			SetCursor(0);
		}
	}

	void DoStickInput(){
		for(int i = 0;i < 20;i ++){
			if(Gdx.input.isTouched(i)){
				float x = Gdx.input.getX(i);
				float y = Gdx.graphics.getHeight() - Gdx.input.getY(i);
				if(leftpointer == -1 && x < padsize * scale() && y < padsize * scale()){
					leftpointer = i;
				}
				if(rightpointer == -1 && x > Gdx.graphics.getWidth() - padsize * scale() && y < padsize * scale()){
					rightpointer = i;
				}
			}
		}

		if(leftpointer == -1){
			leftpadx = 0;
			leftpady = 0;
		}else if( !Gdx.input.isTouched(leftpointer)){
			leftpointer = -1;
		}else{
			float x = Gdx.input.getX(leftpointer);
			float y = Gdx.graphics.getHeight() - Gdx.input.getY(leftpointer);
			float rx = x - scale(padsize / 2);
			float ry = y - scale(padsize / 2);
			Vector2 v = new Vector2(rx, ry);
			v.limit(60f * iscale());
			leftpadx = v.x;
			leftpady = v.y;
			walkangle = v.angle();
		}

		if(rightpointer == -1){
			rightpadx = 0;
			rightpady = 0;
		}else if( !Gdx.input.isTouched(rightpointer)){
			rightpointer = -1;
		}else{
			float x = Gdx.input.getX(rightpointer);
			float y = Gdx.graphics.getHeight() - Gdx.input.getY(rightpointer);
			float rx = x - (Gdx.graphics.getWidth() - scale(padsize / 2));
			float ry = y - scale(padsize / 2);
			Vector2 v = new Vector2(rx, ry);
			v.limit(60f * iscale());
			rightpadx = v.x;
			rightpady = v.y;
			shootangle = v.angle();
		}
	}

	void DrawLoadScreen(){
		font.getData().setScale(1.8f);
		font.setColor(Color.WHITE);
		gui.begin();
		// font.draw(gui, "Loading..", Gdx.graphics.getWidth() / 2 -
		// bounds(font,"Loading..") / 2, Gdx.graphics.getHeight() / 2 + 280);
		gui.end();
	}

	void DrawTexLoadScreen(){
		font.getData().setScale(1.8f);
		font.setColor(Color.WHITE);
		gui.begin();
		font.draw(gui, "Loading Textures..", Gdx.graphics.getWidth() / 2 - bounds(font, "Loading Textures..") / 2, Gdx.graphics.getHeight() / 2 + 280);
		gui.end();
		font.getData().setScale(1f);
	}

	void DrawMinimap(){
		int camx = (int)camera.position.x / 12, camy = (int)camera.position.y / 12;

		if(camx < 23){
			camx = 23;

		}
		if(camy < 23){
			camy = 23;
		}

		if(camx > world.worldwidth - 23){
			camx = world.worldwidth - 23;
		}
		if(camy > world.worldheight - 23){
			camy = world.worldheight - 23;
		}

		minimap.begin(ShapeType.Filled);
		minimap.setProjectionMatrix(square);
		for(int x = 0;x < 46;x ++){
			for(int y = 0;y < 46;y ++){
				int acx = camx + x - 23, acy = camy + y - 23;
				Color pix = displaycolors[x][y];
				if(pix == null || pix.equals(new Color(0, 0, 0, 1))){

					if(world.block(1, acx, acy) == 0 || !minimapcolors.containsKey(world.block(1, acx, acy))){
						minimap.setColor(GetMinimapColor(world.block(0, acx, acy)));

						if(acy < world.worldheight - 1 && (GetBlock(world.block(1, acx, acy + 1)).contains("wall") || GetBlock(world.block(1, acx, acy + 1)).contains("pillar"))){
							minimap.getColor().sub(new Color(0.05f, 0.05f, 0.05f, 0));
						}
						if(world.block(0, acx, acy) == 58){
							if( !IsUnderground()){
								minimap.setColor(new Color(200f / 255f, 220f / 255f, 230f / 255f, 1f));
							}else{
								minimap.setColor(Color.BLACK);
							}
						}
					}else{
						minimap.setColor(GetMinimapColor(world.block(1, acx, acy)));
					}
				}else{
					minimap.setColor(pix);
				}
				Color add = Color.BLACK;
				if(coloradd != null){
					if(WithinCircle(x, y, 23, 23, lightrange / 12 / 2)){
						add = coloradd;
					}else if(lightrange != 0){
						if(WithinCircle(x, y, 23, 23, lightrange / 12 / 2 + 3)){
							add = new Color(coloradd).mul(0.7f);
						}else if(WithinCircle(x, y, 23, 23, lightrange / 12 / 2 + 5)){
							add = new Color(coloradd).mul(0.2f);
						}
					}
				}

				if(minimap.getColor().equals(new Color(200f / 255f, 220f / 255f, 230f / 255f, 1f))){
					add = Color.BLACK;
				}
				if( !minimap.getColor().equals(new Color(0.7f, 0.1f, 0.1f, 1))){
					minimap.getColor().mul(new Color(Time.skyColor() + 0.12f + add.r, Time.skyColor() + 0.12f + add.g, Time.skyColor() + 0.12f + add.b, 1f));
				}else{
					minimap.getColor().mul(new Color(Time.skyColor() + 0.3f + add.r, Time.skyColor() + 0.3f + add.g, Time.skyColor() + 0.3f + add.b, 1f));
				}
				minimap.rect(minimapx + x * GUIElement.scale, minimapy + y * GUIElement.scale, GUIElement.scale, GUIElement.scale);

			}

		}
		minimap.end();
	}

	void DrawPauseMenu(){
		// darken screen
		GUIElement.focus = true;
		gui.setColor(new Color(0, 0, 0, 0.5f));
		gui.draw(textures.findRegion("blank"), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gui.setColor(Color.WHITE);
		font.getData().setScale(1.5f * ifscale());
		float height = 40 * ifscale();
		float width = 250 * ifscale();
		if(new Button("").SetUnscaledSize(width, height).SetTextColors(Color.WHITE, new Color(Color.WHITE).sub(0.5f, 0.5f, 0.5f, 0)).SetFont(false).SetText("Back to Game").SetCenter(0, height * 2).Draw()) menuopen = false;
		if(new Button("").SetUnscaledSize(width, height).SetTextColors(Color.WHITE, new Color(Color.WHITE).sub(0.5f, 0.5f, 0.5f, 0)).SetFont(false).SetText("Settings").SetCenter(0, height).Draw()) ;
		if(new Button("").SetUnscaledSize(width, height).SetTextColors(Color.WHITE, new Color(Color.WHITE).sub(0.5f, 0.5f, 0.5f, 0)).SetFont(false).SetText("Back to Menu").SetCenter().Draw()){
			Disconnect();
			MenuRenderer.connecting = false;
			MenuRenderer.loginerror = null;
			MenuRenderer.fading = false;
			MenuRenderer.outtime = 100;
		}
		if(new Button("").SetUnscaledSize(width, height).SetTextColors(Color.WHITE, new Color(Color.WHITE).sub(0.5f, 0.5f, 0.5f, 0)).SetFont(false).SetText("Quit").SetCenter(0, -height).Draw()) Quit();
	}

	void Draw(){
		GUIElement.focus = true;
		antime ++;
		tweenManager.update(deltafloat / 60f);

		batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		lighteffect.SetColor(ambientColor.x, ambientColor.y, ambientColor.z, ambientIntensity);
		batch.setColor(new Color(ambientColor.x + 0.04f, ambientColor.y + 0.04f, ambientColor.z + 0.04f, 1));
		postProcessor.capture();
		batch.begin();
		if( !IsUnderground()){
			// Buffer("sky").begin();

			batch.draw(sky, camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, 0, 0, (int)camera.viewportWidth, (int)camera.viewportHeight);
			batch.draw(fog, camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, antime / 5, 0, (int)camera.viewportWidth, (int)camera.viewportHeight);
			batch.draw(fog1, camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, antime / 3, 0, (int)camera.viewportWidth, (int)camera.viewportHeight);
			batch.draw(fog2, camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, antime / 7, 0, (int)camera.viewportWidth, (int)camera.viewportHeight);
			batch.draw(fog3, camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, antime / 2, 0, (int)camera.viewportWidth, (int)camera.viewportHeight);
			// batch.end();
			// Buffer("sky").getColorBufferTexture().bind(8);
			// Buffer("sky").end();
			Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		}
		batch.setColor(Color.WHITE);

		// batch.begin();

		DrawWorld();

		FrameBuffer f = postProcessor.captureEnd();
		postProcessor.render();
		gui.begin();
		Shaders.AfterDraw(gui, f.getColorBufferTexture());
		gui.end();

		if(Gdx.input.isKeyPressed(Keys.F2)) TakeScreenshot();

		if( !Settings.android) DrawMinimap();
		gui.begin();
		if( !Settings.android) gui.draw(textures.findRegion("minimap"), minimapx - scale() * 2, minimapy - scale() * 2, 50 * scale(), 50 * scale());

		if(debug){
			DrawDebug();
		}

		if(Settings.android){

			new Box("pad").SetAlign(BAlign.LEFT, BAlign.BOTTOM).Draw();
			new Box("pad").SetAlign(BAlign.RIGHT, BAlign.BOTTOM).SetPos(Gdx.graphics.getWidth(), 0).Draw();
			new Box("padstick").SetPos(scale(padsize / 2f) + leftpadx, scale(padsize / 2f) + leftpady).Draw();
			new Box("padstick").SetPos(Gdx.graphics.getWidth() - scale(padsize / 2f) + rightpadx, scale(padsize / 2f) + rightpady).Draw();
			if(new Button("buttonzoomin").SetAlign(BAlign.RIGHT, BAlign.TOP).SetPos(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - scale() * 8).Draw()){
				zoom( -0.1f);
			};

			if(new Button("buttonzoomout").SetAlign(BAlign.RIGHT, BAlign.TOP).SetPos(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - scale() * 16).Draw()){
				zoom(0.1f);
			};

			if( !ChatOpen){
				if(new Button("chatbutton").SetAlign(BAlign.LEFT, BAlign.TOP).SetPos(textures.findRegion("healthbar").getRegionWidth() * scale(), Gdx.graphics.getHeight()).Draw()){
					ChatOpen = true;
					Gdx.input.setOnscreenKeyboardVisible(true);
				};
			}else{
				//if(new Button("chatbutton").SetAlign(BAlign.LEFT, BAlign.TOP).SetPos(0, Gdx.graphics.getHeight() - (textures.findRegion("healthbar").getRegionHeight() * scale()) - scale() * 8).Draw()){
				//    ChatOpen = false;
				//    Gdx.input.setOnscreenKeyboardVisible(false);
				//};
			}

			if(new Button("inventorybutton").SetAlign(BAlign.LEFT, BAlign.TOP).SetPos(0, Gdx.graphics.getHeight() - (textures.findRegion("healthbar").getRegionHeight() * scale())).Draw()){
				InventoryOpen = !InventoryOpen;
			};

			if(new Button("debugbutton").SetAlign(BAlign.RIGHT, BAlign.TOP).SetPos(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()).Draw()){
				debug = !debug;
			};

		}
		font.getData().setScale(0.8f);
		Vector3 u = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		if( !craftopen && !ChatOpen && GetTile(world.block(1, (int)(u.x / 12), (int)(u.y / 12))).type.equals(TileType.bench) && WithinCircle(player.x + 4, player.y + 4, (int)(u.x / 12) * 12 + 6, (int)(u.y / 12) * 12 + 6, 60) && u.y % 12 >= 6){
			tooltiptime ++;
			if(tooltiptime > totaltip){
				tooltiptime = totaltip;
			}
			Vector3 pos = camera.project(new Vector3((int)(u.x / 12) * 12 + 6, (int)(u.y / 12) * 12 + 18, 0));
			String c = "[R] to craft";
			font.setColor(new Color(1, 1, 1, tooltiptime / (float)totaltip));
			font.draw(gui, c, pos.x - bounds(font, c) / 2, pos.y);
			if(Gdx.input.isKeyJustPressed(Keys.R) && !craftopen){
				CraftOpenPacket p = new CraftOpenPacket();
				p.x = (int)(u.x / 12);
				p.y = (int)(u.y / 12);
				client.sendTCP(p);
			}
			font.setColor(Color.WHITE);
			tipx = (int)(u.x / 12);
			tipy = (int)(u.y / 12);
		}else if(tooltiptime > 0){

			if(tipx != -1 && tipy != -1){
				Vector3 pos = camera.project(new Vector3((int)tipx * 12 + 6, (int)tipy * 12 + 18, 0));
				String c = "[R] to craft";
				font.setColor(new Color(1, 1, 1, tooltiptime / (float)totaltip));
				font.draw(gui, c, pos.x - bounds(font, c) / 2, pos.y);
			}
			tooltiptime --;
		}

		DrawChat();

		if(selectedbag != null){

			gui.draw(textures.findRegion("dropbar"), Gdx.graphics.getWidth() / 2 - textures.findRegion("dropbar").getRegionWidth() * 2.5f, 0, textures.findRegion("dropbar").getRegionWidth() * 5, textures.findRegion("dropbar").getRegionHeight() * 5);

			for(int i = 0;i < 5;i ++){
				if(selectedbag.items[i] != null){
					AtlasRegion item = textures.findRegion(Items.GetItem(selectedbag.items[i].id).texname);
					gui.draw(item, i * 60 + (Gdx.graphics.getWidth() / 2 - textures.findRegion("dropbar").getRegionWidth() * 2.5f + 15), 15, item.getRegionWidth() * 5, item.getRegionHeight() * 5);
					if(selectedbag.items[i].amount > 1){

						font.draw(gui, selectedbag.items[i].amount + "", i * 60 + (Gdx.graphics.getWidth() / 2 - textures.findRegion("dropbar").getRegionWidth() * 2.5f + 15), 65);
					}
				}
			}
		}
		float hw = textures.findRegion("healthbar").getRegionWidth(), hh = textures.findRegion("healthbar").getRegionHeight();
		gui.draw(textures.findRegion("healthbar"), 0, Gdx.graphics.getHeight() - hh * scale(), hw * scale(), hh * scale());
		font.getData().setScale(0.9f);

		for(int i = 0;i < 7;i ++){
			if(i < player.statuseffects.size()){
				gui.draw(textures.findRegion("statuseffect" + player.statuseffects.get(i).type), i * 60, Gdx.graphics.getHeight() - hh * 5 - 30, 40, 40);
				if((int)(player.statuseffects.get(i).duration / 60) % 60 >= 10){
					font.draw(gui, (int)(player.statuseffects.get(i).duration / 3600) + ":" + (int)(player.statuseffects.get(i).duration / 60) % 60, i * 60, Gdx.graphics.getHeight() - hh * 5 - 30);
				}else{
					font.draw(gui, (int)(player.statuseffects.get(i).duration / 3600) + ":0" + (int)(player.statuseffects.get(i).duration / 60) % 60, i * 60, Gdx.graphics.getHeight() - hh * 5 - 30);
				}
			}
		}
		AtlasRegion h = textures.findRegion("health");
		if(player.health >= 0){
			h.setRegionWidth((int)(player.health * (h.getRotatedPackedWidth() / 100f)));
		}else{
			h.setRegionWidth(0);
		}
		gui.draw(h, scale() * 2, Gdx.graphics.getHeight() - hh * scale() + 9 * scale(), h.getRegionWidth() * scale(), h.getRegionHeight() * scale());
		AtlasRegion m = textures.findRegion("mana");
		if(player.mana >= 0){
			m.setRegionWidth((int)(player.mana * (m.getRotatedPackedWidth() / (float)player.maxmana)));
		}else{
			m.setRegionWidth(0);
		}
		gui.draw(m, scale() * 2, Gdx.graphics.getHeight() - hh * scale() + 2 * scale(), m.getRegionWidth() * scale(), m.getRegionHeight() * scale());

		font.getData().setScale(1f * scale() / 5f);
		font.draw(gui, player.health + "/100", scale() * 43 - bounds(font, player.health + "/100") / 2, Gdx.graphics.getHeight() - hh * scale() + scale() * 13.2f);
		font.draw(gui, player.mana + "/" + player.maxmana, scale() * 43 - bounds(font, player.mana + "/" + player.maxmana) / 2, Gdx.graphics.getHeight() - hh * scale() + scale() * 6.24f);
		/* float hw = textures.findRegion("healthbar").getRegionWidth(), hh = textures.findRegion("healthbar").getRegionHeight(); gui.draw(textures.findRegion("healthbar"),Gdx.graphics.getWidth() -hw * 5,0,hw * 5,hh * 5); AtlasRegion h = textures.findRegion("health"); if(health >= 0){ h.setRegionWidth((int)(health * (h.getRotatedPackedWidth() / 100f))); }else{ h.setRegionWidth(0); } gui.draw(h,Gdx.graphics.getWidth() -hw * 5 + 20,40,h.getRegionWidth() * 5,h.getRegionHeight() * 5); AtlasRegion m = textures.findRegion("mana"); if(mana >= 0){ m.setRegionWidth((int)(mana * (m.getRotatedPackedWidth() / 50f))); }else{ m.setRegionWidth(0); } gui.draw(m,Gdx.graphics.getWidth() -hw * 5 + 20,5,m.getRegionWidth() * 5,m.getRegionHeight() * 5); font.getData().setScale(0.99f); font.draw(gui, health + "/100", Gdx.graphics.getWidth() -hw * 5 + 185,61); font.draw(gui, mana + "/50", Gdx.graphics.getWidth() -hw * 5 + 195,26); */

		if(craftopen){
			DrawCraftMenu();
		}

		if(InventoryOpen){
			DrawInventory();
		}
		/*
		if(selectedbag != null && (int)bagX >= 0 && (int)bagX <= 4 && (int)guiY == 0 && selectedbag.items[(int)bagX] != null){
		    float sp = 30 * fscale();
		    float offset = 30f;
		    float x = Gdx.input.getX(), y = Gdx.graphics.getHeight() - Gdx.input.getY() + offset;
		    font.getData().setScale(1.1f);
		    font.draw(gui, Items.GetItem(selectedbag.items[(int)bagX].id).name, x, y + sp*3 );
		    font.setColor(Color.PURPLE);
		    font.draw(gui, itemType((byte)selectedbag.items[(int)bagX].id), x, y + sp*2);
		    font.setColor(Color.YELLOW);
		    font.draw(gui, Stats((byte)selectedbag.items[(int)bagX].id), x, y + sp);
		    font.setColor(Color.YELLOW);
		    font.draw(gui, ExtraStats((byte)selectedbag.items[(int)bagX].id), x, y);
		    font.setColor(Color.WHITE);
		}
		*/
		if(flashtime > 0){
			gui.setColor(flashcolor);
			gui.draw(textures.findRegion("blank"), -1, -1, Gdx.graphics.getWidth() + 5, Gdx.graphics.getHeight() + 5);
			gui.setColor(Color.WHITE);
			flashtime --;
		}
		//TODO RECORD GIF
		if(ingif){
			//mouse resizing
			gui.setColor(Color.YELLOW);
			if(Gdx.input.isButtonPressed(Buttons.LEFT) && Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)){
				float xs = Math.abs(Gdx.graphics.getWidth() / 2 - Gdx.input.getX());
				float ys = Math.abs(Gdx.graphics.getHeight() / 2 - (Gdx.graphics.getHeight() - Gdx.input.getY()));
				gui.setColor(Color.GREEN);
				gifx = Gdx.graphics.getWidth() / 2 - xs;
				gify = Gdx.graphics.getHeight() / 2 - ys;
				gifwidth = xs * 2;
				gifheight = ys * 2;
			}
			//draw recording box

			gui.draw(textures.findRegion("blank"), gifx, gify, gifwidth, 1f);
			gui.draw(textures.findRegion("blank"), gifx, gify + gifheight, gifwidth, 1f);
			gui.draw(textures.findRegion("blank"), gifx, gify, 1f, gifheight);
			gui.draw(textures.findRegion("blank"), gifx + gifwidth, gify, 1f, gifheight + 1f);
			//draw gui and staph
			gui.setColor(Color.WHITE);
			boolean pressed = new Button("defaultskin").SetAlign(BAlign.CENTER, BAlign.BOTTOM).SetText(recording ? "Stop" : "Record").SetPos(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + gifheight / 2 + 1).Draw();
			int tseconds = (int)(giftime / 60f);

			new Text("Time: " + (int)(tseconds / 60) + ":" + (tseconds % 60 > 9 ? "" : "0") + tseconds % 60).SetPos(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + gifheight / 2 + (recording ? 75 : 75 + 48)).Draw();
			if(recordframes.size() > 0 && !recording){
				boolean e = new Button("defaultskin").SetAlign(BAlign.CENTER, BAlign.BOTTOM).SetText("Export").SetPos(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + gifheight / 2 + 48).Draw();
				if(e){
					ExportGif();
				}
			}
			if(gifexport != null){
				new Text(gifexport).SetPos(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + gifheight / 2 + 70).Draw();

			}
			if( !recording && pressed){
				recording = true;
				giftime = 0;
				ClearGifFrames();
			}else if(pressed){
				EndRecord();
			}
			if(recording){
				giftime += Entity.Deltafloat();
				new Text("=RECORDING=").SetShadow(false).SetTextColor(Color.RED).SetPos(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + gifheight / 2 + 105).Draw();
				if(Gdx.graphics.getFrameId() % (60 / gif_fps) == 0){
					Pixmap pix = ScreenUtils.getFrameBufferPixmap((int)gifx + 1, (int)gify + 1, (int)gifwidth - 2, (int)gifheight - 2);
					recordframes.add(pix);
				}
			}
		}
		if(menuopen) DrawPauseMenu();
		MenuRenderer.DrawFade();
		gui.end();
	}

	void ClearGifFrames(){
		for(Pixmap p : recordframes){
			p.dispose();
		}
		recordframes.clear();
		gifexport = null;
	}

	void ExportGif(){
		int i = 0;
		ArrayList<String> strings = new ArrayList<String>();
		for(Pixmap p : recordframes){
			FlipPixmap(p);
			PixmapIO.writePNG(Gdx.files.local("gif_frames/frame" + i + ".png"), p);
			strings.add("gif_frames/frame" + i + ".png");
			i ++;
		}

		String time = WriteGIF(strings);
		ClearGifFrames();
		giftime = 0;
		gifexport = "GIF exported to " + Gdx.files.local("gif_export/recording" + time + ".gif").file().getAbsolutePath();
	}

	String WriteGIF(ArrayList<String> strings){
		if(strings.size() == 0) return "";
		try{
			String time = "" + (int)(System.currentTimeMillis() / 1000);
			new File("gif_export/").mkdir();
			BufferedImage firstImage = ImageIO.read(new File(strings.get(0)));
			ImageOutputStream output = new FileImageOutputStream(new File("gif_export/recording" + time + ".gif"));
			GifSequenceWriter writer = new GifSequenceWriter(output, firstImage.getType(), (int)(1f / gif_fps * 1000f), true);

			writer.writeToSequence(firstImage);

			for(int i = 1;i < strings.size();i ++){
				BufferedImage after = ImageIO.read(new File(strings.get(i)));
				writer.writeToSequence(after);
			}
			writer.close();
			output.close();
			return time;
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}

	void FlipPixmap(Pixmap pixmap){
		int w = pixmap.getWidth();
		int h = pixmap.getHeight();
		ByteBuffer pixels = pixmap.getPixels();
		int numBytes = w * h * 4;
		byte[] lines = new byte[numBytes];
		int numBytesPerLine = w * 4;
		for(int i = 0;i < h;i ++){
			pixels.position((h - i - 1) * numBytesPerLine);
			pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
		}
		pixels.clear();
		pixels.put(lines);
	}

	void EndRecord(){
		recording = false;
	}

	void DrawDebug(){
		font.setColor(Color.WHITE);
		font.getData().setScale(0.5f * fscale());
		float ascent = font.getLineHeight();
		int align = Align.topRight;
		float x = 0;
		if( !Settings.android){
			x = minimapx - scale() * 3;
		}else{
			x = Gdx.graphics.getWidth() - scale() * 8f;
		}
		Vector3 u = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		int overid = world.GetObject((int)(u.x / 12), (int)(u.y / 12));
		int underid = world.GetTile((int)(u.x / 12), (int)(u.y / 12));
		font.draw(gui, "FPS: " + Gdx.graphics.getFramesPerSecond() + " (" + Math.round(Gdx.graphics.getDeltaTime() * 60f) + ")", x, Gdx.graphics.getHeight(), 0, align, true);
		font.draw(gui, "POS: " + player.x / 12 + " , " + player.y / 12, x, Gdx.graphics.getHeight() - ascent, 0, align, true);
		Runtime r = Runtime.getRuntime();
		font.draw(gui, "Memory: " + (r.totalMemory()) / 1024 / 1024, x, Gdx.graphics.getHeight() - ascent * 2, 0, align, true);
		font.draw(gui, "Block: " + GetBlock(overid) + " (" + overid + ") Tile: " + GetBlock(underid) + " (" + underid + ")", x, Gdx.graphics.getHeight() - ascent * 3, 0, align, true);
		font.draw(gui, "Ping: " + lastping, x, Gdx.graphics.getHeight() - ascent * 4, 0, align, true);
		font.draw(gui, "Last Packet: " + lastpacket, x, Gdx.graphics.getHeight() - ascent * 5, 0, align, true);
		font.draw(gui, "Layers: " + layercount, x, Gdx.graphics.getHeight() - ascent * 6, 0, align, true);

		int i = 0;
		if(Entity.entities.size() < 10){
			for(Entity e : Entity.entities.values()){
				font.draw(gui, e.toString(), x, Gdx.graphics.getHeight() - ascent * (7 + i), 0, align, true);
				i ++;
			}
		}else{
			font.draw(gui, Entity.entities.size() + " entities.", x, Gdx.graphics.getHeight() - ascent * (7), 0, align, true);

		}
		font.getData().setScale(1f);
	}

	void DrawCraftMenu(){
		gui.draw(textures.findRegion("recipes"), Gdx.graphics.getWidth() / 2 - textures.findRegion("recipes").getRegionWidth() * (scale() / 2f), Gdx.graphics.getHeight() / divis - (scale() * 10), textures.findRegion("recipes").getRegionWidth() * scale(), textures.findRegion("recipes").getRegionHeight() * scale());

		if(pushtime > 0){
			gui.draw(textures.findRegion("buttonpush"), Gdx.graphics.getWidth() / 2 - textures.findRegion("recipes").getRegionWidth() * (scale() / 2f), Gdx.graphics.getHeight() / divis - (scale() * 10), textures.findRegion("buttoninvalid").getRegionWidth() * scale(), textures.findRegion("buttoninvalid").getRegionHeight() * scale());
		}else if(selectedrecipe == -1 || !ContainsItems(recipes.get(selectedrecipe).requirements) || InventoryFull()){
			gui.draw(textures.findRegion("buttoninvalid"), Gdx.graphics.getWidth() / 2 - textures.findRegion("recipes").getRegionWidth() * (scale() / 2f), Gdx.graphics.getHeight() / divis - (scale() * 10), textures.findRegion("buttoninvalid").getRegionWidth() * scale(), textures.findRegion("buttoninvalid").getRegionHeight() * scale());
		}else if( !OnCraftButton()){
			gui.draw(textures.findRegion("buttonvalid"), Gdx.graphics.getWidth() / 2 - textures.findRegion("recipes").getRegionWidth() * (scale() / 2f), Gdx.graphics.getHeight() / divis - (scale() * 10), textures.findRegion("buttoninvalid").getRegionWidth() * scale(), textures.findRegion("buttoninvalid").getRegionHeight() * scale());
		}else{
			gui.draw(textures.findRegion("buttonselect"), Gdx.graphics.getWidth() / 2 - textures.findRegion("recipes").getRegionWidth() * (scale() / 2f), Gdx.graphics.getHeight() / divis - (scale() * 10), textures.findRegion("buttoninvalid").getRegionWidth() * scale(), textures.findRegion("buttoninvalid").getRegionHeight() * scale());
		}

		for(int y = 0;y < 5;y ++){
			for(int x = 6;x > -1;x --){
				int index = (6 - x) + y * 7;
				if(recipes.size() > index){
					Recipe r = recipes.get(index);
					if(selectedrecipe == index){
						// gui.setColor(new Color(0.5f,1f,0.5f,1f));
						gui.draw(textures.findRegion("recipeselect"), (6 - x) * scale(12) + Gdx.graphics.getWidth() / 2 - textures.findRegion("recipes").getRegionWidth() * scale(0.5f) + scale(2), (4 - y) * scale(12) + Gdx.graphics.getHeight() / divis + scale(2), scale(12), scale(12));
					}
					gui.draw(textures.findRegion(Items.GetItem(r.resultid).texname), (6 - x) * scale(12) + Gdx.graphics.getWidth() / 2 - textures.findRegion("recipes").getRegionWidth() * scale(0.5f) + scale(3), (4 - y) * scale(12) + Gdx.graphics.getHeight() / divis + scale(3), scale(10), scale(10));
					gui.setColor(Color.WHITE);
				}
			}
		}

		if(selectedrecipe != -1){
			if(ContainsItems(recipes.get(selectedrecipe).requirements)){
				if( !InventoryFull()){
					font.draw(gui, "Craft", Gdx.graphics.getWidth() / 2 - bounds(font, "craft") / 2, Gdx.graphics.getHeight() / divis - scale(2));
				}else{
					font.draw(gui, "Inventory full", Gdx.graphics.getWidth() / 2 - bounds(font, "Inventory full") / 2, Gdx.graphics.getHeight() / divis - scale(2));
				}
			}else{
				font.draw(gui, "No items", Gdx.graphics.getWidth() / 2 - bounds(font, "No Items") / 2, Gdx.graphics.getHeight() / divis - scale(2));
			}
		}

		if(selectedrecipe != -1){

			Recipe r = recipes.get(selectedrecipe);

			font.draw(gui, Items.GetItem(r.resultid).name, Gdx.graphics.getWidth() / 2 - bounds(font, Items.GetItem(r.resultid).name) / 2, Gdx.graphics.getHeight() / divis + scale(83.6f));
			float x = (Gdx.graphics.getWidth() / 2 - textures.findRegion("recipes").getRegionWidth() * scale(0.5f)) + scale(3);
			font.getData().setScale(iscale());
			for(int i = 0;i < 3;i ++){
				if(r.requirements.length > i){
					gui.draw(textures.findRegion(Items.GetItem(r.requirements[i].id).texname), x + scale(24 + i * 12), Gdx.graphics.getHeight() / divis + scale(68), scale(10), scale(10));
					if( !ContainsItem(r.requirements[i])){
						font.setColor(Color.RED);
					}
					font.draw(gui, r.requirements[i].amount + "x", x + scale(24), Gdx.graphics.getHeight() / divis + scale(77.2f));
					font.setColor(Color.WHITE);
				}
			}

		}else{
			font.draw(gui, "Select a Recipe", Gdx.graphics.getWidth() / 2 - bounds(font, "Select a Recipe") / 2, Gdx.graphics.getHeight() / divis + scale(84.6f));
		}

	}

	void DrawInventory(){
		gui.draw(textures.findRegion("inventory"), 0, 0, textures.findRegion("inventory").getRegionWidth() * scale(), textures.findRegion("inventory").getRegionHeight() * scale());
		for(int x = 0;x < 5;x ++){
			for(int y = 0;y < 6;y ++){
				if(player.inventory[x][y] != null){
					AtlasRegion item = textures.findRegion(Items.GetItem(player.inventory[x][y].id).texname);
					int w = item.getRegionWidth();
					int h = item.getRegionHeight();
					gui.draw(item, scale() * slotsize / 2 + scale() * 2 + x * scale() * slotsize - w * scale() / 2, scale() * slotsize / 2 + scale() * 2 + y * scale() * slotsize - h * scale() / 2, w * scale(), h * scale());

					if(player.inventory[x][y].amount > 1){
						font.draw(gui, player.inventory[x][y].amount + "", scale() * 3 + x * scale() * slotsize, y * scale() * slotsize + scale() * 13);
					}
					/*something */
					/* if(x < 5 && x > 0 && y == 5 && abilitycooldown[x] > 0){ gui.draw(textures.findRegion("abilityu"),15 + x * 60,15 +y * 60,(abilitycooldown[x] / (Items.GetItem(player.inventory[x][5].id).value("speed") / 50f)),50); font.draw(gui, (int)(abilitycooldown[x] / 60 + 1) + "", 15 + x * 60,0 + 65); } */
				}else if(y == 5){
					float w = slotsize - 2;
					float h = slotsize - 2;
					gui.draw(textures.findRegion("slot-" + x), scale() * 3 + x * scale() * slotsize, scale() * 3 + y * scale() * slotsize, w * scale(), h * scale());
				}
			}
		}

		if(player.selecteditem != null){
			AtlasRegion item = textures.findRegion(Items.GetItem(player.selecteditem.id).texname);
			int w = item.getRegionWidth();
			int h = item.getRegionHeight();
			gui.draw(item, Gdx.input.getX() - scale() * w / 2, Gdx.graphics.getHeight() - Gdx.input.getY() - scale() * h / 2, scale() * w, scale() * h);
			if(player.selecteditem.amount > 1){
				font.getData().setScale(1f);
				font.draw(gui, player.selecteditem.amount + "", Gdx.input.getX() - scale() * 5, Gdx.graphics.getHeight() - Gdx.input.getY() - scale() * 5 + scale() * 10);
			}
		}

		if(guiX >= 0 && guiY >= 0 && guiX < 5 && guiY < 6 && player.inventory[(int)guiX][(int)guiY] != null && player.selecteditem == null){
			font.getData().setScale(ifscale());
			int id = player.inventory[(int)guiX][(int)guiY].id;
			Item item = Items.GetItem(id);
			float sp = 30 * fscale();
			float offset = sp + (item.HasExtraStats() ? sp : 0) + (item.HasStats() ? sp : 0);
			float x = Gdx.input.getX(), y = Gdx.graphics.getHeight() - Gdx.input.getY() + offset;

			font.draw(gui, item.name, x, sp + y);
			font.setColor(Color.PURPLE);
			font.draw(gui, /*ItemType(id)*/item.TypeName(), x, y);
			font.setColor(Color.YELLOW);
			font.draw(gui, /*Stats(id)*/item.Stats(), x, y - sp);
			font.setColor(Color.YELLOW);
			font.draw(gui, /*ExtraStats(id)*/item.ExtraStats(), x, y - sp * 2);
			font.setColor(Color.WHITE);
		}

		font.getData().setScale(1f);
	}

	void DrawChat(){
		gui.setColor(Color.WHITE);
		font.getData().setScale(1f);

		if( !InventoryOpen){
			if(Settings.android){
				DrawAndroidChat();
				font.getData().markupEnabled = false;
				font.setColor(Color.WHITE);
				return;
			}

			float chatwidth = this.chatwidth * fscale();
			font.setColor(Color.WHITE);
			font.getData().setScale(fscale());
			float cy = 26 * fscale(), ca = 20 * fscale(), spacing = 26 * fscale();
			if(Settings.android) spacing = 0;
			float cwidth = 760 * fscale();
			int cl = 0;
			font.getData().markupEnabled = true;
			for(int i = 0;i < chatamount;i ++){
				float fade = chatfade - (fadescale * (i));
				if(fade <= 1 && fade > 0){
				}else if(fade < 0){
					continue;
				}
				if(chat.size() - i - 1 >= 0 && chat.size() - i - 1 < chat.size()){
					ChatMessage message = chat.get(chat.size() - i - 1);
					glyphs.setText(font, message.finalmessage, coloradd, chatwidth, Align.bottomLeft, true);
					cl += Math.round(glyphs.height / font.getData().lineHeight);
				}
			}

			gui.setColor(new Color(0, 0, 0, 0.2f));
			if(ChatOpen){
				gui.draw(textures.findRegion("blank"), ca, ca, Gdx.graphics.getWidth(), 26 * fscale());
			}
			gui.draw(textures.findRegion("blank"), ca, cy + ca + spacing, cwidth, 26 * (cl) * fscale());
			gui.setColor(Color.WHITE);

			if(ChatOpen){
				font.draw(gui, "> " + currentChat, ca, cy + ca);
				chatfade = chatamount * fadescale;
			}
			if(chatfade > 0){
				chatfade -= 0.01f;
			}

			int chatlength = 0;
			font.getData().down = -26 * fscale();
			for(int i = 0;i < chatamount;i ++){
				float fade = chatfade - (fadescale * (i));
				float finalfade = 1f;

				if(fade <= 1 && fade > 0){
					finalfade = fade;
				}else if(fade < 0){
					finalfade = 0;
					continue;
				}
				if(chat.size() - i - 1 >= 0 && chat.size() - i - 1 < chat.size()){

					ChatMessage message = chat.get(chat.size() - i - 1);
					glyphs.setText(font, message.finalmessage, coloradd, chatwidth, Align.bottomLeft, true);
					chatlength += Math.round(glyphs.height / font.getData().lineHeight) - 1;
					BitmapFontCache cache = font.getCache();
					cache.clear();
					cache.addText(message.finalmessage, ca, (1 + i + chatlength) * 26 * fscale() + cy + ca + spacing - 1, chatwidth, Align.bottomLeft, true);
					cache.setAlphas(finalfade);
					cache.draw(gui);
				}
			}
		}
		font.getData().markupEnabled = false;
		font.setColor(Color.WHITE);
	}

	void DrawAndroidChat(){
		float chatwidth = this.chatwidth * fscale();
		font.setColor(Color.WHITE);
		font.getData().setScale(fscale());
		float cy = 26 * fscale(), ca = 20 * fscale(), spacing = 26 * fscale();
		if(Settings.android) spacing = 0;
		float cwidth = 760 * fscale();
		int cl = 0;
		if(ChatOpen){
			gui.setColor(new Color(0, 0, 0, 0.2f));
			gui.draw(textures.findRegion("blank"), ca, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), 26 * fscale());
			font.draw(gui, "> " + currentChat, ca, Gdx.graphics.getHeight() / 2 + 26 * fscale());
			chatfade = chatamount * fadescale;

			font.getData().markupEnabled = true;
			for(int i = 0;i < chatamount;i ++){
				float fade = chatfade - (fadescale * (i));
				if(fade <= 1 && fade > 0){
				}else if(fade < 0){
					continue;
				}
				if(chat.size() - i - 1 >= 0 && chat.size() - i - 1 < chat.size()){
					ChatMessage message = chat.get(chat.size() - i - 1);
					glyphs.setText(font, message.finalmessage, coloradd, chatwidth, Align.bottomLeft, true);
					cl += Math.round(glyphs.height / font.getData().lineHeight);
				}
			}

			gui.setColor(new Color(0, 0, 0, 0.2f));
			gui.draw(textures.findRegion("blank"), ca, Gdx.graphics.getHeight() / 2 + cy + ca + spacing, cwidth, 26 * (cl) * fscale());
			gui.setColor(Color.WHITE);

			if(chatfade > 0){
				chatfade -= 0.01f;
			}

			int chatlength = 0;
			font.getData().down = -26 * fscale();
			for(int i = 0;i < chatamount;i ++){
				float fade = chatfade - (fadescale * (i));
				float finalfade = 1f;

				if(fade <= 1 && fade > 0){
					finalfade = fade;
				}else if(fade < 0){
					finalfade = 0;
					continue;
				}
				if(chat.size() - i - 1 >= 0 && chat.size() - i - 1 < chat.size()){

					ChatMessage message = chat.get(chat.size() - i - 1);
					glyphs.setText(font, message.finalmessage, coloradd, chatwidth, Align.bottomLeft, true);
					chatlength += Math.round(glyphs.height / font.getData().lineHeight) - 1;
					BitmapFontCache cache = font.getCache();
					cache.clear();
					cache.addText(message.finalmessage, ca, Gdx.graphics.getHeight() / 2f + (1 + i + chatlength) * 26 * fscale() + cy + ca + spacing - 1, chatwidth, Align.bottomLeft, true);
					cache.setAlphas(finalfade);
					cache.draw(gui);
				}
			}
			if(ChatOpen){
				if(new Button("chatbutton").SetAlign(BAlign.RIGHT, BAlign.CENTER).SetPos(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2 + 13 * fscale()).Draw()){
					SendChat();
				}
			}
		}
	}

	void DrawWorld(){
		bloomintensity = 0.5f;
		sprites.addAll(particles);
		if(bloomintensity > 0){
			//    bloom.setEnabled(true);
			//bloom.setBloomSaturation(0.5f);
			//   bloom.setThreshold(bloom.getThreshold());
			//  bloom.setBaseIntesity(bloomintensity);
		}else{
			bloom.setEnabled(false);
		}
		for(ParticleLayer p : particles){
			ParticleEffect e = p.particle;
			if(e.getEmitters().first().isComplete()){
				particles.remove(p);
			}
		}

		for(StatusEffect e : player.statuseffects){
			e.duration -= Entity.Deltafloat();
			if(e.duration <= 0){
				player.statuseffects.remove(e);
			}
		}
		queue.Iterate();
		for(Entity e : Entity.entities.values()){
			if( !e.equals(player)) e.Update();
			if(Entity.entities.containsKey(e.GetID()) && e.InRange(camera.position.x, camera.position.y)) e.Draw();
		}
		UpdateCameraPosition();
		TileDraw();
		DrawLayers();
	}

	void DrawMainMenu(){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gui.begin();
		font.getData().setScale(1f);
		MenuRenderer.Render();
		gui.end();
		if(Gdx.input.isKeyPressed(Keys.F2)) TakeScreenshot();
	}

	boolean PlayersInCursor(){
		Vector3 u = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		u.x = (int)(u.x / 12) * 12;
		u.y = (int)(u.y / 12) * 12;
		if(NearRange(player.x + 4, u.x + 6, 7) && NearRange(player.y + 2, u.y + 6, 6)){
			return true;
		}
		for(Player p : players.values()){
			if(NearRange(p.x + 4, u.x + 6, 7) && NearRange(p.y + 2, u.y + 6, 6)){
				return true;
			}
		}
		return false;
	}

	boolean CursorInRange(){
		Vector3 u = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		u.x = (int)(u.x / 12) * 12;
		u.y = (int)(u.y / 12) * 12;
		float range = 40;
		if(Math.sqrt(((u.x - player.x) * (u.x - player.x) + (u.y - player.y) * (u.y - player.y))) <= range){
			return true;
		}
		return false;
	}

	static boolean WithinCircle(float x, float y, float x2, float y2, float range){
		if(Math.sqrt(((x - x2) * (x - x2) + (y - y2) * (y - y2))) <= range){
			return true;
		}
		return false;
	}

	float Distance(float x, float y, float x2, float y2){
		return (float)Math.sqrt(((x - x2) * (x - x2) + (y - y2) * (y - y2)));
	}

	boolean AddItemToInventory(ItemStack item){
		if(item != null && item.id != 0){
			new DropIndicator(player.x, player.y, (byte)item.id, item.amount);
			int nullx = 0, nully = 0;
			boolean nulled = false;
			for(int y = 5;y >= 0;y --){
				for(int x = 0;x < 5;x ++){
					ItemType t = Items.GetItem(item.id).type;
					if( !((y == 5 && ((x == 0 && t.equals(ItemType.WEAPON)) || (x == 1 && t.equals(ItemType.HELMET)) || (x == 2 && t.equals(ItemType.CHESTPLATE)) || (x == 3 && t.equals(ItemType.BOOTS)) || (x == 4 && t.equals(ItemType.GAUNTLET)))) || y != 5)) continue;
					if(player.inventory[x][y] != null && player.inventory[x][y].id == item.id && Items.GetItem(item.id).type == ItemType.MATERIAL){
						player.inventory[x][y].amount += item.amount;
						return false;
					}else if(player.inventory[x][y] == null && !nulled){
						nullx = x;
						nully = y;
						nulled = true;
					}
				}
			}
			player.inventory[nullx][nully] = item;
		}
		return true;
	}

	boolean InventoryFull(){
		for(int y = 5;y >= 0;y --){
			for(int x = 0;x < 5;x ++){
				if(player.inventory[x][y] == null){
					return false;
				}
			}
		}
		return true;
	}

	/*
	    public boolean Connect(String name, String password){
		try{
		    if( !accountconnected){
			client.connect(1000, ip, port, port);
			log("Connected");
		    }
		    ConnectPacket p = new ConnectPacket();
		    p.name = name;
		    p.pswd = password;
		    client.sendTCP(p);
		    connected = true;

		    return true;
		}catch(Exception e){
		    log("Connection failed.");
		    log(e);
		    connected = false;
		    errorMessage = "Failed to connect.";
		    return false;
		}
	    }
	    */

	public void InitializeNetworking(){
		try{
			CreateSprites();
			client.connect(1000, ip, port, port);
			log("Connected");
			ConnectPacket p = new ConnectPacket();
			p.token = Settings.GetValue("token", Long.class);
			client.sendTCP(p);
			connected = true;
		}catch(Exception e){
			log("Connection failed.");
			e.printStackTrace();
			connected = false;
			errorMessage = "Failed to connect.";
			MenuRenderer.ping = MenuRenderer.maxconnectping;
		}
	}

	public boolean InitializeAccount(){
		try{
			client.connect(10000, ip, port, port);
			log("Account Connected");
			accountconnected = true;
			return true;
		}catch(Exception e){
			log("Account Connection failed.");
			e.printStackTrace();
			connected = false;
			return false;
		}
	}

	void SlotClicked(int x, int y){
		boolean disable = false;
		if(player.inventory[x][y] != null && player.selecteditem == null){
			player.selecteditem = player.inventory[x][y];
			player.inventory[x][y] = null;
			lastslotx = x;
			lastslotx = y;
		}else if(player.inventory[x][y] == null && player.selecteditem != null){
			if(y != 5 || ItemValid(player.selecteditem, x)){
				player.inventory[x][y] = player.selecteditem;
				player.selecteditem = null;
			}
		}else if(player.inventory[x][y] != null && player.selecteditem != null){
			if(y != 5 || ItemValid(player.selecteditem, x)){
				if( !Settings.android){
					ItemStack selTEMP = player.selecteditem;
					player.selecteditem = player.inventory[x][y];
					player.inventory[x][y] = selTEMP;

				}else{
					disable = true;
				}
			}
		}
		if(disable) return;
		SlotClickedPacket p = new SlotClickedPacket();
		p.x = (byte)guiX;
		p.y = (byte)guiY;
		client.sendTCP(p);
	}

	void ItemDropped(){
		if(selectedbag != null){
			for(int i = 0;i < 5;i ++){
				if(selectedbag.items[i] == null){
					ItemDropPacket p = new ItemDropPacket();
					p.lootbagId = selectedbag.id;
					client.sendTCP(p);
					player.selecteditem = null;
					break;
				}
			}
		}else{
			ItemDropPacket p = new ItemDropPacket();
			p.lootbagId = 0;
			client.sendTCP(p);
			player.selecteditem = null;
		}

	}

	static boolean TypeEquals(int id, TileType type){
		if(id < 0) id += 256;
		if(id >= tiles.size()){
			return false;
		}
		return GetTile(id).type.equals(type);
	}

	static Tile GetTile(int id){
		if(id < 0) id += 256;
		if(id >= tiles.size()){
			return tiles.get(2);
		}

		return tiles.get(id);
	}

	static Tile GetTile(int z, int x, int y){

		return GetTile(world.block(z, x, y));
	}

	static String GetBlock(int id){
		return GetTile(id).name;
	}

	void RemoveItem(ItemStack a){
		int quantity = a.amount;
		for(int y = 5;y >= 0;y --){
			for(int x = 0;x < 5;x ++){
				if(player.inventory[x][y] != null && player.inventory[x][y].id == a.id){
					if(player.inventory[x][y].amount <= quantity){
						quantity -= player.inventory[x][y].amount;
						player.inventory[x][y] = null;
					}else{
						player.inventory[x][y].amount -= quantity;
						quantity = 0;
						return;
					}
				}
				if(quantity <= 0){
					return;
				}
			}
		}
	}

	void RemoveItems(ItemStack[] a){
		for(ItemStack i : a){
			RemoveItem(i);
		}

	}

	boolean ContainsItem(ItemStack a){
		int quantity = 0;
		for(int y = 5;y >= 0;y --){
			for(int x = 0;x < 5;x ++){
				if(player.inventory[x][y] != null && player.inventory[x][y].id == a.id){
					quantity += player.inventory[x][y].amount;
				}
				if(quantity >= a.amount){
					return true;
				}
			}
		}
		return false;
	}

	boolean ContainsItems(ItemStack[] items){
		for(ItemStack i : items){
			if( !ContainsItem(i)){
				return false;
			}
		}
		return true;
	}

	void Flash(int duration){
		flashcolor = Color.WHITE;
		flashtime += duration;
	}

	void Flash(int duration, Color c){
		flashcolor = c;
		flashtime += duration;
	}

	public static void Shake(Vector2 v){
		camerashake = v;
	}

	void UpdateCameraPosition(){
		camera.position.set(player.x + 0.5f, player.y + 4f, 0);

		if(camerashake.len() >= 0.0001f){
			camerashake.scl(1f - cameraspeed);
		}
		camera.position.set(camera.position.x + camerashake.x, camera.position.y + camerashake.y, 0);

		if(camera.position.y + camera.viewportHeight / 2 > world.worldheight * 12){
			camera.position.y = world.worldheight * 12 - camera.viewportHeight / 2;
		}
		if(camera.position.y - camera.viewportHeight / 2 < 0){
			camera.position.y = camera.viewportHeight / 2;
		}
		if(camera.position.x - camera.viewportWidth / 2 < 0){
			camera.position.x = camera.viewportWidth / 2;
		}
		if(camera.position.x + camera.viewportWidth / 2 > world.worldwidth * 12){
			camera.position.x = world.worldwidth * 12 - camera.viewportWidth / 2;
		}

		if(shaketime > 0){
			if(camera.position.y == camera.viewportHeight / 2 || camera.position.x == camera.viewportWidth / 2){
				camera.position.set(camera.position.x + (float)Math.random() * 2, camera.position.y + (float)Math.random() * 2, 0);
			}
		}

		camera.update();
	}

	void DrawPlayer(float x, float y, boolean chargetype, int attacktype, int attackframe, int chargetime, int walkframe, Direction direction, ItemStack[] equips, RGB[] colors, int[] extra){
		/* String walking = ""; String frame = ""; if(walkframe != 0){ walking = "walk"; if(!Settings.insanity){ if((walkframe / 15 )% 2 == 1){ frame = "1"; }else{ frame = "2"; } }else{ if(Beat.GetBeat()){ frame = "1"; }else{ frame = "2"; } }
		 * 
		 * } String zweapon = ""; int zmaxc = 0; if(equips[0] != null && Items.GetItem(equips[0].id).type == ItemType.WEAPON){ zweapon = Items.GetItem(equips[0].id).texname.replace("item", ""); zmaxc = Items.GetItem(equips[0].id).value("speed"); } WeaponSpriter.BeginDraw(x,y, attacktype, frame, direction, attackframe, chargetime,zweapon, zmaxc);
		 * 
		 * Sprite p = new Sprite(textures.findRegion("player" + walking + direction + frame + "skin")); p.setPosition(x, y); p.setSize(8, 8); SpriteLayer playerlayer = new SpriteLayer(p,y); if(InWater(x+4,y)){ SetToWater(p,"player" + walking + direction + frame + "skin"); } p.flip(direction.flipped(), false); p.setColor(new Color(colors[0].r, colors[0].g, colors[0].b, 1)); //SetPlayerEffects(p,effectime,effectype, -1, flip, direction);
		 * 
		 * 
		 * SpriteLayer weaponlayer = null, fistlayer = null; if(equips[0] != null && Items.GetItem(equips[0].id).type == ItemType.WEAPON){ WeaponSprite weapon = WeaponSpriter.Draw(); weaponlayer = new SpriteLayer(weapon.weapon,y); Sprite fist = weapon.fist; fist.setColor(new Color(colors[0].r, colors[0].g, colors[0].b, 1)); fistlayer = new SpriteLayer(fist,y);
		 * 
		 * if(direction == Direction.back){ sprites.add(weaponlayer); sprites.add(fistlayer); } }else{
		 * 
		 * Sprite fist = WeaponSpriter.DrawDefaultFist(x,y, frame, direction,chargetime, attacktype, attackframe, "",0); fist.setColor(new Color(colors[0].r, colors[0].g, colors[0].b, 1)); fistlayer = new SpriteLayer(fist,y);
		 * 
		 * if(direction == Direction.back){ sprites.add(fistlayer); } } //WEAPON CODE BEGIN
		 * 
		 * //DRAW PLAYER sprites.add(playerlayer); //sprites.add(playerlayer2);
		 * 
		 * //LEGGING CODE BEGIN if(!InWater(x+4,y)){ String legname2 = ""; if(equips[3] != null&& Items.GetItem(equips[3].id).type == ItemType.BOOTS){ legname2 = Items.GetItem(equips[3].id).texname + direction; }else{ legname2 = "legs" + (extra[2]+1) + direction;
		 * 
		 * }
		 * 
		 * legname2 += walking + frame; Sprite legs2 = new Sprite(textures.findRegion(legname2)); legs2.setPosition(x, y); legs2.setSize(8, 8); if(!(equips[3] != null&& Items.GetItem(equips[3].id).type == ItemType.BOOTS)){ legs2.setColor(new Color(colors[3].r, colors[3].g, colors[3].b, 1)); }
		 * 
		 * 
		 * legs2.flip(direction.flipped(), false); SetEffects(legs2,effectime,effectype); sprites.add(new SpriteLayer(legs2,y )); } //CHESTPLATE CODE BEGIN String chestplatename2; if(equips[2] != null && Items.GetItem(equips[2].id).type == ItemType.CHESTPLATE){ chestplatename2 = Items.GetItem(equips[2].id).texname + direction; }else{ chestplatename2 = "shirt" + (extra[1]+1) + direction; }
		 * 
		 * Sprite chestplate2 = new Sprite(textures.findRegion(chestplatename2)); chestplate2.setPosition(x, y); chestplate2.setSize(8, 8); if(!(equips[2] != null && Items.GetItem(equips[2].id).type == ItemType.CHESTPLATE)){ chestplate2.setColor(new Color(colors[2].r, colors[2].g, colors[2].b, 1)); } SetEffects(chestplate2,effectime,effectype); if((frame.equals( "1") && (direction == Direction.left || direction == Direction.right || direction == Direction.back )) && WeaponSpriter.MoveBody()){ if(direction == Direction.left || direction == Direction.right){ chestplate2.setRegionWidth((int)chestplate2.getRegionWidth() - 3); chestplate2.setSize(3, 8); }else{ chestplate2.setSize(6, 8); } chestplate2.setPosition(chestplate2.getX() + 2f, chestplate2.getY()); chestplate2.setRegionX(chestplate2.getRegionX() +2); if(direction.flipped()){ chestplate2.setPosition(chestplate2.getX()+1f, chestplate2.getY()); }
		 * 
		 * 
		 * }
		 * 
		 * if(InWater(x+4,y)){ chestplate2.setPosition(chestplate2.getX(), chestplate2.getY()-2); } chestplate2.flip(direction.flipped(), false);
		 * 
		 * sprites.add(new SpriteLayer(chestplate2,y ));
		 * 
		 * //HELMET CODE BEGIN
		 * 
		 * 
		 * String helmname2 = ""; if(equips[1] != null&& Items.GetItem(equips[1].id).type == ItemType.HELMET){ helmname2 = Items.GetItem(equips[1].id).texname + direction; }else{ helmname2 = "hat" + (extra[0]+1) + direction; } Sprite helm2 = new Sprite(textures.findRegion(helmname2)); helm2.setPosition(x, y); helm2.setSize(8, 8); if(!(equips[1] != null&& Items.GetItem(equips[1].id).type == ItemType.HELMET)){ helm2.setColor(new Color(colors[1].r, colors[1].g, colors[1].b, 1)); } if(InWater(x+4,y)){ helm2.setPosition(helm2.getX(), helm2.getY()-2); } helm2.flip(direction.flipped(), false); SetEffects(helm2,effectime,effectype); sprites.add(new SpriteLayer(helm2,y ));
		 * 
		 * if(direction != Direction.back){ if(weaponlayer != null){ sprites.add(weaponlayer); }
		 * 
		 * if(fistlayer != null){ sprites.add(fistlayer); } } //if((direction == Direction.front|| direction == "side") && frame != ""){ Sprite frontfist = new Sprite(textures.findRegion(direction + "fist" + frame)); frontfist.setPosition(x, y); frontfist.setSize(8, 8); frontfist.flip(direction.flipped(), false); frontfist.setColor(new Color(colors[0].r, colors[0].g, colors[0].b, 1)); SetWeaponEffects(frontfist,effectime,effectype); String weapon = null; int maxc = 0; if(equips[0] != null && Items.GetItem(equips[0].id).type == ItemType.WEAPON){ weapon = Items.GetItem(equips[0].id).texname; maxc = Items.GetItem(equips[0].id).value("speed"); } WeaponSpriter.EditFist(frontfist); if(InWater(x+4,y)){ frontfist.setY(frontfist.getY() - 2); } sprites.add(new SpriteLayer(frontfist,y )); //}
		 * 
		 * 
		 * ArrayList<TerrainLayer> pixels = new ArrayList<TerrainLayer>();
		 * 
		 * /* for(int lx = 0; lx < playerwidth; lx ++){ for(int ly = 0; ly < playerwidth; ly ++){ if(lx > 5 || lx < 2){ // continue; } TerrainLayer t = new TerrainLayer("blank",y,x+lx,y+ly); //t.x += (float)Math.random()-0.5f; //t.y += (float)Math.random()-0.5f;
		 * 
		 * pixels.add(t); } } */

		// cool rainbow effect, but why?
		/* for(int i = 0; i < 64; i ++){ int lx = i % 8,ly = i/8; if(lx > 5 || lx < 2){ // continue; } float xadd=(float)Math.sin(Time.unitime/20f+ly), yadd=(float)Math.sin(Time.unitime/40f+lx); pixels.add(new TerrainLayer("blank", y,x+lx+xadd,y+ly+yadd ).SetColor(lx /7f, 1f - (ly /7f), ly /7f)); } */
		/* //is face = 1
		 * 
		 * //is arms = 2
		 * 
		 * //is hands = 3
		 * 
		 * //is body = 4
		 * 
		 * //is head = 5
		 * 
		 * //is legs = 6 */

		// pure derpyness, rainbows were better
		/* for(int lx = 0; lx < playerwidth; lx ++){ for(int ly = 0; ly < playerheight; ly ++){ float xadd=0, yadd=0; float xscale=1f,yscale=1f; int type = bodypixels[lx][ly]; if(type == 0) continue; float scale = 10f; float change = (float)(Math.sin(Time.unitime/scale))/2f; RGB color = new RGB(1f,1f,1f);
		 * 
		 * //if(direction.left()) lx = 7 - lx;
		 * 
		 * //xadd = (float)(Math.sin(Time.unitime/scale+new Random(type*40000).nextFloat()*8f)); //yadd = (float)(Math.sin(Time.unitime/scale+new Random(type*3000).nextFloat()*20f));
		 * 
		 * if(lx == 1 && ly == 7){ //yadd = change; }else if(lx == 2 && ly == 7){ //yadd -= 0.5f; } if(type == 6){ //xadd -= (walkframe/10f) % 2 - 0.1f; if(lx < 4){ // xadd = -xadd; //if(walkframe/10 % 4 < 2) //yadd += (float)Math.abs(Math.sin(walkframe/20f+3.14f/2f)); }else{ //if(walkframe/10 % 4 >= 2) //yadd += (float)Math.abs(Math.sin(walkframe/20f)/2f); } if(direction.left()) xadd = -xadd; }
		 * 
		 * if(type < 6 && type != 3){ // yadd += 1f; // yadd = change; //if(type == 5 || type == 1) yadd *=2f; } if(lx > 3 && type != 5 && type != 1){ // xadd -= 0.5f;
		 * 
		 * } if(type == 3 && walkframe > 0){
		 * 
		 * //xadd += (walkframe/10f) % 2; // if(lx < 4) xadd = -xadd; //yadd = change/4f ; }
		 * 
		 * 
		 * if(type == 1 || type == 3){ color = colors[0]; }else{ color = colors[1]; } if(!direction.left()){ pixels.add(new TerrainLayer("blank", y,x+lx+xadd,y+ly+yadd ).SetColor(color.r, color.g, color.b)); }else{ pixels.add(new TerrainLayer("blank", y,x+7-lx+xadd,y+ly+yadd ).SetColor(color.r, color.g, color.b)); } } }
		 * 
		 * 
		 * 
		 * for(TerrainLayer l : pixels){ sprites.add(l); } */
		Color color = new Color(65 / 255f, 53 / 255f, 114 / 255f, 1f);
		CacheLayer l = (CacheLayer)new CacheLayer("playerlegs1", player.y, player.x, player.y).SetColor(color).Draw();
		CacheLayer b = (CacheLayer)new CacheLayer("playerbody1", player.y, player.x, player.y).SetColor(color).Draw();
		CacheLayer h = (CacheLayer)new CacheLayer("playerhead1", player.y, player.x, player.y).SetColor(color).Draw();
		CacheLayer sk = (CacheLayer)new CacheLayer("playerskin", player.y + 0.001f, player.x, player.y).Draw();
		l.flipx = player.direction.equals(Direction.left);
		b.flipx = player.direction.equals(Direction.left);
		h.flipx = player.direction.equals(Direction.left);
		sk.flipx = player.direction.equals(Direction.left);

		walkframe /= 6;
		if(walkframe > 0){
			l.region += "walk" + (walkframe % 4 + 1);
			if((walkframe) % 4 >= 1){
				sk.region += "walk";
			}else{

			}
			/*
			if(walkframe % 2 == 0){
			b.y --;
			h.y --;
			sk.y --;
			}
			*/
			// sk.region += "walk" + ((walkframe/3)%4+1);
		}

		Sprite s = new Sprite(textures.findRegion("playershadow"));
		s.setPosition(x - 2, y - 2);
		s.setSize(12, 4);
		sprites.add(new SpriteLayer(s, 99999));

	}

	void SendPosition(){
		try{
			PositionPacket p = new PositionPacket();
			p.x = player.x;
			p.y = player.y;
			p.angle = angle();
			//  p.dashing = player.dashing;
			// p.shooting = (Gdx.input.isButtonPressed(Buttons.LEFT) &&
			// !InventoryOpen);
			p.walkdirection = player.walkdirection;
			p.direction = player.direction;
			client.sendTCP(p);
		}catch(Exception e){
			log(e);
			MenuRenderer.DisconnectError("Connection timed out.");
			Disconnect();
		}
	}

	public String ItemType(int id){
		String type = "";
		switch(Items.GetItem(id).type){
		case HELMET:
			type = "Helmet";
			break;
		case CHESTPLATE:
			type = "Chestplate";
			break;
		case BOOTS:
			type = "Boots";
			break;
		case GAUNTLET:
			type = "Gauntlet";
			break;
		case WEAPON:
			type = "Weapon";
			break;
		case ABILITY:
			type = "Ability";
			break;
		case MATERIAL:
			type = "Material";
			break;
		case CONSUMABLE:
			type = "Consumable";
			break;
		}
		return type;
	}

	public String Stats(int id){
		String type = "";
		switch(Items.GetItem(id).type){
		case HELMET:
			type += "Defense: " + Items.GetItem(id).value("defense");
			break;
		case CHESTPLATE:
			type += "Defense: " + Items.GetItem(id).value("defense");
			break;
		case BOOTS:
			type += "Defense: " + Items.GetItem(id).value("defense");
			break;
		case GAUNTLET:
			type += "Defense: " + Items.GetItem(id).value("defense");
			break;
		case WEAPON:
			type += "Damage: " + Items.GetItem(id).value("damage");
			break;
		case ABILITY:
			type = "WIP";
			break;
		default:
			break;
		}
		return type;
	}

	public String ExtraStats(int id){
		String type = "";
		switch(Items.GetItem(id).type){
		case WEAPON:
			if(Math.round(60f / +Items.GetItem(id).value("speed")) == 0){
				type += "Speed: " + Items.GetItem(id).value("speed");
				break;
			}
			type += "Speed: " + Math.round(60f / +Items.GetItem(id).value("speed"));
			break;
		default:
			break;
		}
		return type;
	}

	@Override
	public boolean keyDown(int keycode){
		return false;
	}

	@Override
	public boolean keyUp(int keycode){
		return false;
	}

	public void SendChat(){
		if(currentChat.length() > 0){
			ChatPacket p = new ChatPacket();
			p.message = currentChat;
			client.sendTCP(p);
			ChatOpen = false;
			currentChat = "";
			chatfade = lastfade;
			chatfade += fadescale;
			if(chatfade > chatamount * fadescale){
				chatfade = chatamount * fadescale;
			}
			Gdx.input.setOnscreenKeyboardVisible(false);
		}else{
			ChatOpen = false;
			currentChat = "";
			chatfade = lastfade;
			Gdx.input.setOnscreenKeyboardVisible(false);
		}
	}

	@Override
	public boolean keyTyped(char character){
		if( !Gdx.input.isKeyPressed(Keys.ENTER) && !Gdx.input.isKeyPressed(Keys.BACKSPACE) && ChatOpen && font.getData().hasGlyph(character) && currentChat.length() < 90){
			currentChat += character;
		}
		if(Settings.android && ChatOpen && (int)character == 13){
			SendChat();
		}

		if(font.getData().hasGlyph(character)){
			TextArea.KeyTyped(character);
		}

		if(currentChat.length() > 0 && ChatOpen && Gdx.input.isKeyPressed(Keys.BACKSPACE)){
			currentChat = currentChat.substring(0, currentChat.length() - 1);
		}

		if((int)character == 8){
			TextArea.BackSpace();
			if(Settings.android && ChatOpen && currentChat.length() > 0){
				currentChat = currentChat.substring(0, currentChat.length() - 1);
			}
		}
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button){
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer){
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY){
		return false;
	}

	@Override
	public boolean scrolled(int amount){
		if(amount < 0){
			if(selectedAbility > 0){
				selectedAbility --;
			}else{
				// selectedAbility = 3;
			}
		}else if(amount > 0){
			if(selectedAbility < 3){
				selectedAbility ++;
			}else{
				// selectedAbility = 0;
			}
		}
		zoom(amount / 10f);

		/* if(camera.zoom < 0){ camera.zoom = 0.1f; } */
		return false;
	}

	void zoom(float amount){
		if((amount < 0 && camera.zoom >= 0.1f) || (amount > 0 && camera.zoom < 1)){
			camera.zoom += amount;
		}
	}

	private final TweenCallback windCallback = new TweenCallback(){
		@Override
		public void onEvent(int type, BaseTween<?> source){

			float d = 1.3f + (float)Math.random() / 2f; // duration
			float t = 0;

			if(Math.random() > 0.5){
				t = (float)(2 + Math.random() * 2f); // amplitude
			}else{
				t = -(float)(2 + Math.random() * 2f); // amplitude
			}

			/* Timeline.createParallel().push (Tween.to(grassSprite, SpriteAccessor.SKEW_X2X3, d) .target(t, t) .ease(Sine.INOUT) .repeatYoyo(1, 0) .setCallback(windCallback) .start(tweenManager)).push (Tween.to(grassSprite2, SpriteAccessor.SKEW_X2X3, d) .target(t, t) .ease(Sine.INOUT) .repeatYoyo(1, 0).delay(d/2) .start(tweenManager)).push (Tween.to(grassSprite3, SpriteAccessor.SKEW_X2X3, d) .target(t, t) .ease(Sine.INOUT) .repeatYoyo(1, 0).delay(d/3 * 6) .start(tweenManager)).push (Tween.to(treesprite, SpriteAccessor.SKEW_X2X3, d) .target(t2, t2) .ease(Sine.INOUT) .repeatYoyo(1, 0).delay(d2) .start(tweenManager)).push (Tween.to(treesprite2, SpriteAccessor.SKEW_X2X3, d) .target(t2, t2) .ease(Sine.INOUT) .repeatYoyo(1, 0).delay(d2 / 5) .start(tweenManager)).push (Tween.to(treeb1, SpriteAccessor.ROTATE,db1) .target(tb1, tb1) .ease(Sine.INOUT) .repeatYoyo(1, 0).delay(d2/ 3 * 2) .start(tweenManager)).push (Tween.to(treeb2, SpriteAccessor.ROTATE,db1) .target(tb1, tb1) .ease(Sine.INOUT) .repeatYoyo(1, 0).delay(d2) .start(tweenManager)).push (Tween.to(treeb3, SpriteAccessor.ROTATE,db1) .target(tb1, tb1) .ease(Sine.INOUT) .repeatYoyo(1, 0).delay(d2 / 3) .start(tweenManager)).push (Tween.to(treeb4, SpriteAccessor.ROTATE,db1) .target(tb1, tb1) .ease(Sine.INOUT) .repeatYoyo(1, 0).delay(d2 / 3 * 3) .start(tweenManager).start(tweenManager)).push (Tween.to(treeb5, SpriteAccessor.ROTATE,db1) .target(tb1, tb1) .ease(Sine.INOUT) .repeatYoyo(1, 0).delay(d2 / 3 * 5) .start(tweenManager)); */
			Timeline time = Timeline.createParallel();
			time.push(Tween.to(tweenmoves[0], SpriteAccessor.SKEW_X2X3, d).target(t, t).ease(Sine.INOUT).repeatYoyo(1, 0).setCallback(windCallback).start(tweenManager));

			for(int i = 1;i < 10;i ++){
				if(i < 5){
					time.push(Tween.to(tweenmoves[i], SpriteAccessor.SKEW_X2X3, d).target(t, t).ease(Sine.INOUT).repeatYoyo(1, 0).delay((i / 2f)).start(tweenManager));
				}else{
					time.push(Tween.to(tweenmoves[i], SpriteAccessor.SKEW_X2X3, d).target(t / 2f, t / 2f).ease(Sine.INOUT).repeatYoyo(1, 0).delay((float)Math.random() * 2).start(tweenManager));
				}
			}

			/* Timeline.createParallel().push (Tween.to(tweenmoves[0], SpriteAccessor.SKEW_X2X3, d) .delay(d) .target(t, t) .ease(Sine.INOUT) .repeatYoyo(1, 0) .setCallback(windCallback) .start(tweenManager)).push (Tween.to(tweenmoves[1], SpriteAccessor.SKEW_X2X3, d) .delay(d/2) .target(t, t) .ease(Sine.INOUT) .repeatYoyo(1, 0) .setCallback(windCallback) .start(tweenManager)).push (Tween.to(tweenmoves[2], SpriteAccessor.SKEW_X2X3, d) .delay(d/3) .target(t, t) .ease(Sine.INOUT) .repeatYoyo(1, 0) .setCallback(windCallback) .start(tweenManager)).push (Tween.to(tweenmoves[3], SpriteAccessor.SKEW_X2X3, d) .delay(d/4) .target(t, t) .ease(Sine.INOUT) .repeatYoyo(1, 0) .setCallback(windCallback) .start(tweenManager)).push (Tween.to(tweenmoves[4], SpriteAccessor.SKEW_X2X3, d) .delay(d/5) .target(t, t) .ease(Sine.INOUT) .repeatYoyo(1, 0) .setCallback(windCallback) .start(tweenManager)); */
		}
	};

	boolean PlayerInWater(){
		if((world.GetTile((int)((player.x + 4) / 12), (int)(player.y / 12)) == 6 || world.GetTile((int)((player.x + 4) / 12), (int)(player.y / 12)) == 10)){
			return true;
		}
		return false;
	}

	/* boolean PlayerInWater(Player p){ if((world.GetTile((int)((p.x + 4) / 12), (int)(p.y / 12)) == 6 || world.GetTile((int)((p.x + 4) / 12), (int)(p.y / 12)) == 10)){ return true; } return false; } */
	static boolean InWater(Sprite p){
		if((world.GetTile((int)((p.getX() + p.getWidth() / 2f) / 12f), (int)(p.getY() / 12)) == -128 || world.GetTile((int)((p.getX() + p.getWidth() / 2f) / 12f), (int)(p.getY() / 12)) == 6 || world.GetTile((int)((p.getX() + p.getWidth() / 2f) / 12f), (int)(p.getY() / 12)) == 94)){
			return true;
		}
		return false;
	}

	public static boolean InWater(float x, float y){
		if((world.GetTile((int)((x) / 12f), (int)(y / 12)) == 6 || world.GetTile((int)((x) / 12f), (int)(y / 12)) == -128 || world.GetTile((int)((x) / 12f), (int)(y / 12)) == 10 || world.GetTile((int)((x) / 12f), (int)(y / 12)) == 94)){
			return true;
		}
		return false;
	}

	void AbilityUsed(int slot){
		if( !InventoryOpen && !ChatOpen){

			// if( inventory[slot+1][5] != null && abilitycooldown[slot] <= 0 &&
			// ((slot == 0 && Items.GetItem(inventory[slot+1][5].id).type ==
			// ItemType.HELMET) || (slot == 1 &&
			// Items.GetItem(inventory[slot+1][5].id).type ==
			// ItemType.CHESTPLATE)|| (slot == 2 &&
			// Items.GetItem(inventory[slot+1][5].id).type == ItemType.BOOTS) ||
			// (slot == 3 && Items.GetItem(inventory[slot+1][5].id).type ==
			// ItemType.GAUNTLET)) &&
			// Items.GetItem(inventory[slot+1][5].id).value("manause") <= mana
			// ){
			Vector3 u = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
			AbilityUsePacket g = new AbilityUsePacket();
			g.x = u.x;
			g.y = u.y;
			g.slot = (byte)(slot);
			client.sendTCP(g);
			// abilitycooldown[slot] =
			// Items.GetItem(inventory[slot+1][5].id).value("speed");
			// }
		}
	}

	public static void SetToWater(Sprite p, String region){
		p.setRegionHeight((int)p.getRegionHeight() - 2);
		p.setSize(p.getWidth(), p.getHeight() - 2);
	}

	static void SetBToWater(Sprite p, String region){
		AtlasRegion r = textures.findRegion(region);
		if(InWater(p)){
			r.setRegionHeight((int)r.getRotatedPackedHeight() - 2);
			p.setSize(p.getWidth(), p.getHeight() - 2);
			p.setRegion(r);
		}else{
			r.setRegionHeight((int)r.getRotatedPackedHeight());
			p.setSize(p.getWidth(), p.getHeight());
			p.setRegion(r);
		}
	}

	/* void SetToWater(Player g, Sprite p, String region){ AtlasRegion r = textures.findRegion(region); if(PlayerInWater(g)){ r.setRegionHeight((int)r.getRotatedPackedHeight() - 2); p.setSize(p.getWidth(), p.getHeight() - 2); p.setRegion(r); }else{ r.setRegionHeight((int)r.getRotatedPackedHeight()); p.setSize(p.getWidth(), p.getHeight()); p.setRegion(r); } } */
	@Override
	public void dispose(){
		Disconnect();
		for(FrameBuffer b : buffers.values()){
			b.dispose();
		}
		textures.dispose();
		light.getTexture().dispose();
		sprites.clear();
		spritemap.clear();
		loaded = false;

	}

	boolean GetFlipped(byte direction){
		boolean flip = false;
		if(direction == 0){
			flip = false;
		}else if(direction == 2){
			flip = false;
		}else if(direction == 3){
			flip = true;
		}
		return flip;
	}

	/* Direction GetDirection(byte direction){ Direction dir = "side";
	 * 
	 * if(direction == 0){ dir = Direction.back; }else if(direction == 2){ dir = Direction.front; } return dir; } */
	void CreateHitAnimation(float x, float y, int duration, String type){
		hits.add(new HitAnimation((int)x, (int)y, duration, type));
	}

	void CreateHitAnimation(float x, float y, int duration, float angle, String type){
		hits.add(new HitAnimation((int)x, (int)y, duration, angle, type));
	}

	void CreateMinimapColor(int id, int r, int g, int b){
		minimapcolors.put(id, new Color(r / 255f, g / 255f, b / 255f, 1));
	}

	Color GetMinimapColor(int id){
		if(minimapcolors.containsKey(id)){
			return minimapcolors.get(id);
		}
		return new Color(0, 0, 0, 0);
	}

	static boolean DoorOpen(int x, int y){
		return false;
		/* if(x > world.worldwidth || y > world.worldheight || x <0 || y < 0){ return false; }else{ return open[x][y]; } */
	}

	static boolean BlockIsSolid(float x, float y){
		if((GetBlock((byte)world.GetObject((int)(x / 12), (int)(y / 12))).contains("wall") && !GetBlock((byte)world.GetObject((int)(x / 12), (int)(y / 12))).contains("door")) || GetBlock((byte)world.GetObject((int)(x / 12), (int)(y / 12))).contains("pillar") || (GetBlock((byte)world.GetObject((int)(x / 12), (int)(y / 12))).contains("door") && !DoorOpen((int)(x / 12), (int)(y / 12)))){
			return true;
		}
		return false;
	}

	void LoadObjects(){
		ObjectLoader.CreateAll();
		ObjectLoader.tiles = world;
		log("Objects created");
		types = ObjectLoader.GetProjectiles();
		items = ObjectLoader.GetItems();
		log("World array creation started...");
		world.Create(ObjectLoader.worldwidth, ObjectLoader.worldheight);
		log("Chunk array creation started...");
		chunkloaded = new boolean[world.worldwidth / 10][world.worldheight / 10];
		log("World created.");
		log("Objects loaded.");
	}

	class songLoader implements Runnable{
		@Override
		public void run(){
			LoadObjects();
			LoadMusic();
			LoadBlocks();
			CreateSprites();
			log("All assets loaded.");
			// MenuRenderer.stage = "Loading complete.";
			loaded = true;
		}
	}

	void LoadBlocks(){
		tiles = Save.LoadBlocks();
		for(Tile t : tiles){
			tilenames.put(t.name, t.id);
		}
	}

	void UpdateShaders(){
		float dt = Gdx.graphics.getRawDeltaTime();
		torchsize += dt * 6.0f;
		while(torchsize > Math.PI * 2f)
			torchsize -= Math.PI * 2f;
	}

	static void SetVertices(Sprite sprite, float amount){
		sprite.getVertices()[SpriteBatch.X2] += amount;
		sprite.getVertices()[SpriteBatch.X3] += amount;
	}

	void SetBottomVertices(Sprite sprite, float amount){
		sprite.getVertices()[SpriteBatch.X1] += amount;
		sprite.getVertices()[SpriteBatch.X4] += amount;
	}

	void SetTopVertices(Sprite sprite, float amount){
		sprite.getVertices()[SpriteBatch.Y1] += amount;
		sprite.getVertices()[SpriteBatch.Y4] += amount;
	}

	void InitShader(){
		ShaderLoader.BasePath = "shaders/";
		shade = ShaderLoader.fromFile("default", "default");
		postProcessor = new PostProcessor(false, false, true);
		postProcessor.setClearColor(new Color(0, 0, 0, 1));
		bloom = new Bloom((int)(Gdx.graphics.getWidth() * 0.25f), (int)(Gdx.graphics.getHeight() * 0.25f));
		bloom.setBloomIntesity(3.1f);
		// bloom.setBaseSaturation(0f);
		bloom.setThreshold(0.5f);
		MotionBlur blur = new MotionBlur();
		blur.setBlurOpacity(0.8f);
		lighteffect = new Light(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shadoweffect = new Shadow(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// outlineeffect = new Outline(Gdx.graphics.getWidth(),
		// Gdx.graphics.getHeight());
		// postProcessor.addEffect(shadoweffect);
		// postProcessor.addEffect(outlineeffect);
		// postProcessor.addEffect( blur);
		postProcessor.addEffect(lighteffect);
		postProcessor.addEffect(bloom);
		bloom.setEnabled(false);

	}

	Sprite CreateSprite(String name){
		Sprite s = new Sprite(textures.findRegion(name));
		s.setSize(textures.findRegion(name).getRegionWidth(), textures.findRegion(name).getRegionHeight());
		spritemap.put(name, s);
		return s;
	}

	void CreateSprite(String name, int width, int height){
		Sprite s = new Sprite(textures.findRegion(name));
		s.setSize(width, height);
		spritemap.put(name, s);
	}

	String[] transitions = {"grass", "burntgrass"};

	void CreateTileTransitions(){
		textures.findRegion("grass").getTexture().getTextureData().prepare();
		Pixmap tilep = textures.findRegion("grass").getTexture().getTextureData().consumePixmap();
		HashMap<String, Pixmap> pmap = new HashMap<String, Pixmap>();
		//create base array of pixmaps..
		for(String s : transitions){
			TextureRegion r = textures.findRegion(s);
			Pixmap p = new Pixmap(r.getRegionWidth(), r.getRegionHeight(), Format.RGBA8888);
			CopyPixels(r, tilep, p);
			pmap.put(s, p);
		}
		HashSet<String> iterated = new HashSet<String>();
		for(String s : transitions){
			for(String o : transitions){
				if(iterated.contains(o)) continue;
				for(int i = 1;i <= 6;i ++){
					Pixmap p = CopyPixmap(pmap.get(s));
					BlendPixmap(p, pmap.get(o), i / 6f);
					Sprite sprite = new Sprite(new Texture(p));
					sprite.setSize(p.getWidth(), p.getHeight());
					spritemap.put(s + /*"transition_" + o + "" +*/"_" + i, sprite);
				}
			}
			iterated.add(s);

		}
	}

	int seed = (int)(Math.random() * 1000);

	void BlendPixmap(Pixmap source, Pixmap blend, float amount){
		seed = (int)(Math.random() * 1000);
		for(int x = 0;x < source.getWidth();x ++){
			for(int y = 0;y < source.getHeight();y ++){
				Color b = new Color(blend.getPixel(x, y));
				if(Math.random() < amount) source.drawPixel(x, y, Color.rgba8888(b));
			}
		}
	}

	Pixmap CopyPixmap(Pixmap p){
		Pixmap c = new Pixmap(p.getWidth(), p.getHeight(), Format.RGBA8888);
		for(int x = 0;x < p.getWidth();x ++){
			for(int y = 0;y < p.getHeight();y ++){
				c.drawPixel(x, y, p.getPixel(x, y));
			}
		}
		return c;
	}

	void CopyPixels(TextureRegion r, Pixmap from, Pixmap p){
		for(int x = 0;x < r.getRegionWidth();x ++){
			for(int y = 0;y < r.getRegionHeight();y ++){
				int color = from.getPixel(r.getRegionX() + x, r.getRegionY() + y);
				p.drawPixel(x, y, color);
			}
		}
	}

	void CreateSprites(){
		CreateSprite("error", 12, 12);
		CreateSprite("occlusion", 24, 24);
		originallavax = GetSprite("lava").getRegionX();
		GetSprite("lava").setRegionWidth(12);
		owaterx = GetSprite("water").getRegionX();
		GetSprite("water").setRegionWidth(12);

		/* Sprite e2 = new Sprite(textures.findRegion("wallshadow")); e2.setSize(textures.findRegion("wallshadow").getRegionWidth(), textures.findRegion("wallshadow").getRegionHeight()); spritemap.put("wallshadow", e2);
		 * 
		 * Sprite g = new Sprite(textures.findRegion("riverrock")); g.setSize(12,12); spritemap.put("riverrock", g);
		 * 
		 * CreateSprite("error",12,12); CreateSprite("grassblockleft"); CreateSprite("grassblockright"); CreateSprite("grassblock2left"); CreateSprite("grassblock2right");
		 * 
		 * 
		 * for (Tile ti : tiles){ String m = ti.name; if (textures.findRegion(m) != null){
		 * 
		 * if ((m.contains("lantern")) || (m.contains("door") || m.contains("ore")) ) { for (int i = 1; i < 6; i++){ Sprite s2 = new Sprite(textures.findRegion(m + i)); s2.setSize(textures.findRegion(m + i).getRegionWidth(), textures.findRegion(m + i).getRegionHeight()); spritemap.put(m + i, s2); } }
		 * 
		 * if ((m.contains("spikes")) ) { for (int i = 1; i < 4; i++){ Sprite s2 = new Sprite(textures.findRegion(m + i)); s2.setSize(textures.findRegion(m + i).getRegionWidth(), textures.findRegion(m + i).getRegionHeight()); spritemap.put(m + i, s2); } }
		 * 
		 * }
		 * 
		 * if (textures.findRegion(m + "edge") != null){
		 * 
		 * Sprite s = new Sprite(textures.findRegion(m)); s.setSize(textures.findRegion(m).getRegionWidth(), textures.findRegion(m).getRegionHeight()); spritemap.put(m, s);
		 * 
		 * Sprite e = new Sprite(textures.findRegion(m + "edge")); e.setSize(textures.findRegion(m + "edge").getRegionWidth(), textures.findRegion(m + "edge").getRotatedPackedHeight()); spritemap.put(m + "edge", e); }else if(m.contains("wall")){ Sprite s = new Sprite(textures.findRegion(m)); s.setSize(textures.findRegion(m).getRegionWidth(), textures.findRegion(m).getRegionHeight()); spritemap.put(m, s); } }
		 * 
		 * 
		 * originallavax = GetSprite("lava").getRegionX(); GetSprite("lava").setRegionWidth(12);
		 * 
		 * owaterx = GetSprite("water").getRegionX(); GetSprite("water").setRegionWidth(12); */

	}

	boolean CanAttack(){
		return attackcooldown == 0 && !craftopen && player.attackframe == 0 && !InventoryOpen && player.inventory[0][5] != null && Items.GetItem(player.inventory[0][5].id).type == ItemType.WEAPON;
	}

	public static void SendInput(InputType type){
		InputPacket p = new InputPacket();
		p.type = type;
		h.client.sendTCP(p);
	}

	void DoWeaponInput(){
		if( !Settings.android){
			if(Gdx.input.isButtonPressed(Buttons.LEFT)){
				if(leftmousetime == 0){
					SendInput(InputType.LEFTCLICKDOWN);
				}
				leftmousetime += Entity.Deltafloat();
			}else{
				if(leftmousetime > 0){
					leftmousetime = 0;
					SendInput(InputType.LEFTCLICKUP);
				}
			}

			if(Gdx.input.isButtonPressed(Buttons.RIGHT)){
				if(rightmousetime == 0){
					SendInput(InputType.RIGHTCLICKDOWN);
				}
				rightmousetime += Entity.Deltafloat();
			}else{
				if(rightmousetime > 0){
					rightmousetime = 0;
					SendInput(InputType.RIGHTCLICKUP);
				}
			}
		}else{
			if(rightpointer != -1){
				if(rightmousetime == 0){
					SendInput(InputType.LEFTCLICKDOWN);
				}
				rightmousetime += Entity.Deltafloat();
				if(rightmousetime > 20){
					rightmousetime = 0;
					player.chargeframe = 0;
					SendInput(InputType.LEFTCLICKUP);
				}
			}else{
				if(rightmousetime > 0){
					rightmousetime = 0;
					SendInput(InputType.LEFTCLICKUP);
				}
			}
		}
		if(Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT)){
			SendInput(InputType.SHIFTDOWN);

			//    Home.h.CreateParticleEffect(player.x, player.y, player.y, "dash");
		}
		//Vector3 u = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		/*
		if(attacking && (chargetype && !Gdx.input.isButtonPressed(Buttons.RIGHT)) || ( !chargetype && Gdx.input.isButtonPressed(Buttons.RIGHT))){
		    chargetype = Gdx.input.isButtonPressed(Buttons.RIGHT);
		    ChargePacket p = new ChargePacket();
		    p.angle = angle();
		    p.rightclick = chargetype;
		    p.begin = true;
		    client.sendTCP(p);
		}

		if(CanAttack() && (((Gdx.input.isButtonPressed(Buttons.LEFT) || Gdx.input.isButtonPressed(Buttons.RIGHT)) && !Settings.android) || (touches() > 1))){
		    if( !Settings.android){

		    }else{
			chargetype = touches() == 3;
		    }

		    if( !attacking){
			chargetype = Gdx.input.isButtonPressed(Buttons.RIGHT);
			ChargePacket p = new ChargePacket();
			p.angle = angle();
			p.rightclick = chargetype;
			p.begin = true;
			client.sendTCP(p);
		    }
		    chargetime ++;
		    attacking = true;
		}else{
		    chargetime = 0;
		    if(attacking){
			ChargePacket p = new ChargePacket();
			p.angle = angle();
			p.rightclick = chargetype;
			p.begin = false;
			client.sendTCP(p);
			attacking = false;
		    }
		}

		if(player.attackframe > 0){
		    player.attackframe -= deltaframe;
		}

		if(player.attackframe < 0){
		    player.attackframe = 0;
		}
		*/
	}

	public void CreateParticleEffect(float x, float y, float layer, String name){
		ParticleEffect particle = particletypes.get(name).obtain();
		particle.setPosition(x, y);
		ParticleLayer l = new ParticleLayer(layer, particle);
		l.x = x;
		l.y = y;
		particles.add(l);
	}

	public void CreateParticleEffect(float x, float y, float layer, float rotation, String name){
		ParticleEffect particle = particletypes.get(name).obtain();
		particle.setPosition(x, y);
		ParticleEmitter m = particle.getEmitters().first();
		m.getAngle().setHighMin(m.getAngle().getHighMin() + rotation);
		m.getAngle().setHighMax(m.getAngle().getHighMax() + rotation);
		if(m.getRotation().isActive()){
			m.getRotation().setHighMin(m.getRotation().getHighMin() + rotation);
			m.getRotation().setHighMax(m.getRotation().getHighMax() + rotation);
		}
		ParticleLayer l = new ParticleLayer(layer, particle);
		l.x = x;
		l.y = y;
		particles.add(l);
	}

	public void CreateParticleEffect(float x, float y, float layer, float rotation, float velocity, String name){
		ParticleEffect particle = particletypes.get(name).obtain();
		particle.setPosition(x, y);
		ParticleEmitter m = particle.getEmitters().first();
		m.getAngle().setHighMin(m.getAngle().getHighMin() + rotation);
		m.getAngle().setHighMax(m.getAngle().getHighMax() + rotation);
		m.getVelocity().setHighMin(m.getVelocity().getHighMin() * velocity);
		m.getVelocity().setHighMax(m.getVelocity().getHighMax() * velocity);
		ParticleLayer l = new ParticleLayer(layer, particle);
		l.x = x;
		l.y = y;
		particles.add(l);
	}

	public void CreateParticleEffect(String name, float x, float y, float anglemin, float anglemax){
		ParticleEffect particle = particletypes.get(name).obtain();
		particle.setPosition(x, y);
		ParticleEmitter m = particle.getEmitters().first();
		m.getAngle().setHighMin(anglemin);
		m.getAngle().setHighMax(anglemax);
		ParticleLayer l = new ParticleLayer(y, particle);
		l.x = x;
		l.y = y;
		particles.add(l);
	}

	ParticleEffect ParticleEffect(float x, float y, String name){
		ParticleEffect particle = particletypes.get(name).obtain();
		particle.setPosition(x, y);
		return particle;
	}

	void DashtoServer(int time, int cooldown, float speed, float angle){
		// dashangle = angle;
		// dashspeed = speed;
		// dashtime = time;
		DashPacket p = new DashPacket();
		p.angle = angle;
		p.amount = speed;
		p.time = time;
		client.sendTCP(p);
		dashcooldown = cooldown;
	}

	void CreateParticleType(String name){
		ParticleEffect p = new ParticleEffect();
		p.load(Gdx.files.internal("particles/" + name), Gdx.files.internal("particles/"));
		ParticleEffectPool o = new ParticleEffectPool(p, 0, 20);
		particletypes.put(name, o);
		// particles.put(name, p);
	}

	void CreateParticleType(String name, String effect){
		ParticleEffect p = new ParticleEffect();
		p.load(Gdx.files.internal("particles/" + name), textures);
		ParticleEffectPool o = new ParticleEffectPool(p, 0, 20);
		particletypes.put(name, o);
		// particles.put(name, p);
	}

	static void writefile(String name, List<String> list){
		try{
			FileHandle file = Gdx.files.external(name);
			for(String s : list){

				file.writeString(s + "-", true);
				String type = "tile";
				if(s.contains("wall") || s.contains("pillar")){
					type = "block";
				}else if(s.contains("koru") || s.contains("rock") || s.contains("tree") || s.contains("bush") || s.contains("tallgrass") || s.contains("log") || s.contains("wheatgrass") || s.contains("mushy") || s.contains("torch") || s.contains("cattails") || s.contains("log")){
					type = "object";
				}

				file.writeString(type + "\n", true);
			}
		}catch(Exception e){
			e.printStackTrace();
			log("Loading file failed!");
		}
		log("File Written");
	}

	boolean OnCraftButton(){
		return selectX >= 2 && selectX <= 5 && ((Gdx.graphics.getHeight() - Gdx.input.getY() - (Gdx.graphics.getHeight() / divis + 8)) / 60f) < 0 && ((Gdx.graphics.getHeight() - Gdx.input.getY() - (Gdx.graphics.getHeight() / divis + 8)) / 60f) >= -1;
	}

	Sprite GetSprite(String name){
		// if(s== null) return GetSprite("error");
		Sprite s = spritemap.get(name);
		if(s != null){
			return s;
		}else if( !textures.IsBlank(name)){
			s = CreateSprite(name);
			return s;
		}else{
			Sprite error = spritemap.get("error");
			spritemap.put(name, error);
			return error;
		}
	}

	boolean ColorEquals(Color a, Color b){
		return (NearRange(a.r, b.r, 0.01f) && NearRange(a.g, b.g, 0.01f) && NearRange(a.b, b.b, 0.01f));
	}

	boolean ColorEquals(Color a, Color b, float range){
		return (NearRange(a.r, b.r, range) && NearRange(a.g, b.g, range) && NearRange(a.b, b.b, range));
	}

	boolean Button(String texture, String label, float x, float y){
		return true;
	}

	static boolean IsUnderground(){
		return camera.position.y / 12f > world.worldheight / 2;
	}

	static boolean more(Color one, Color two){
		return (one.r >= two.r && one.g >= two.g && one.b >= two.b);
	}

	static String[] LineLength(String line, int linelength){
		String input = line;
		// int MAX_LINE_LENGTH = 30;
		int maxCharInLine = 50;
		StringTokenizer tok = new StringTokenizer(input, " ");
		java.lang.StringBuilder output = new java.lang.StringBuilder(input.length());
		int lineLen = 0;
		while(tok.hasMoreTokens()){
			String word = tok.nextToken();

			while(word.length() > maxCharInLine){
				output.append(word.substring(0, maxCharInLine - lineLen) + "\n");
				word = word.substring(maxCharInLine - lineLen);
				lineLen = 0;
			}

			if(lineLen + word.length() > maxCharInLine){
				output.append("\n");
				lineLen = 0;
			}
			output.append(word + " ");

			lineLen += word.length() + 1;
		}
		return output.toString().split("\n");
	}

	static String[] LineLength(String line){
		return LineLength(line, 30);
	}

	int touches(){
		int activeTouch = 0;
		for(int i = 0;i < 20;i ++){
			if(Gdx.app.getInput().isTouched(i)) activeTouch ++;
		}
		return activeTouch;
	}

	void SelectClicked(int x, int y){
		y = 4 - y;
		int index = (x) + (y) * 7;

		if(index < recipes.size()){
			selectedrecipe = index;
		}

	}

	void CraftClicked(){
		CraftPacket p = new CraftPacket();
		p.id = selectedrecipe;
		client.sendTCP(p);
	}

	float GetDamage(int x, int y){
		return world.getdamage(x, y);
	}

	void SetDamage(int x, int y, float amount){
		world.setdamage(x, y, amount);
	}

	void AddDamage(int x, int y, float amount){
		world.adddamage(x, y, amount);
	}

	int GetFrame(int x, int y){
		return world.getframe(x, y);
	}

	void SetFrame(int x, int y, int amount){
		world.setframe(x, y, amount);
	}

	void AddFrame(int x, int y, int amount){
		world.addframe(x, y, amount);
	}

	/* float GetDamage(int x,int y){ Point p = new Point(x,y); if(blockdata.containsKey(p)){ return blockdata.get(p).damage; } return 0; }
	 * 
	 * void SetDamage(int x,int y, float amount){ blockdata.put(new Point(x,y), new BlockData(amount)); }
	 * 
	 * void AddDamage(int x,int y, float amount){ blockdata.get(new Point(x,y)).damage += amount; }
	 * 
	 * int GetFrame(int x,int y){ Point p = new Point(x,y); if(blockdata.containsKey(p)){ return blockdata.get(p).frame; } return 0; }
	 * 
	 * void SetFrame(int x,int y, int amount){ blockdata.put(new Point(x,y), new BlockData(amount)); }
	 * 
	 * void AddFrame(int x,int y, int amount){ blockdata.get(new Point(x,y)).frame += amount; } */

	boolean ItemValid(ItemStack item, int x){
		if(((x == 0 && Items.GetItem(item.id).type == ItemType.WEAPON) || (x == 1 && Items.GetItem(item.id).type == ItemType.HELMET) || (x == 2 && Items.GetItem(item.id).type == ItemType.CHESTPLATE) || (x == 3 && Items.GetItem(item.id).type == ItemType.BOOTS) || (x == 4 && Items.GetItem(item.id).type == ItemType.GAUNTLET))){
			return true;
		}
		return false;
	}

	public void TileDraw(){
		//Vector3 u = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		float scale = 100f;
		magmacolor += 2.4f;
		if(magmacolor > 180 * scale){
			magmacolor = 0;
		}

		float speed = 1.4f;
		lavax += speed;
		if(lavax > 120) lavax = 0;

		float wspeed = 1.6f;
		waterx += wspeed;
		if(waterx > 120) waterx = 0;

		GetSprite("lava").setRegionX(originallavax + (int)(lavax / 10f));
		GetSprite("lava").setRegionWidth(12);
		GetSprite("lava").setSize(12, 12);

		GetSprite("water").setRegionX(owaterx + (int)(waterx / 10f));
		GetSprite("water").setRegionWidth(12);
		GetSprite("water").setSize(12, 12);

		torchtime += 0.2F * deltafloat;
		if(torchtime >= 5.0F){
			torchtime = 0.0F;
		}

		for(int x = (int)(camera.position.x / 12) - xrange;x < (int)(camera.position.x / 12) + xrange;x ++){
			for(int y = (int)(camera.position.y / 12) - yrange;y < (int)(camera.position.y / 12) + yrange;y ++){
				if((x >= 0) && (y >= 0) && (x < world.worldwidth) && (y < world.worldheight)){

					int overid = world.block(1, x, y), underid = world.block(0, x, y);
					Tile overtile = GetTile(world.block(1, x, y));
					TileType overtype = overtile.type;
					Tile undertile = GetTile(world.block(0, x, y));
					TileType undertype = undertile.type;

					if(underid != 0 && underid != 58){
						undertype.Draw(x, y, undertile, overtile, undertile);
					}
					if(overid != 0){
						overtype.Draw(x, y, overtile, overtile, undertile);
					}
				}
			}
		}

	}

	public static float RelativeX(float x){
		float r = 1f / nearpix;
		float relative = x - camera.position.x;
		relative = ((int)(relative / r)) * r;
		return relative + camera.position.x;
	}

	public static float RelativeY(float y){
		float r = 1f / nearpix;
		float relative = y - camera.position.y;
		relative = ((int)(relative / r)) * r;
		return relative + camera.position.y;
	}

	public void DrawLayers(){
		sprites.sort();
		int layers = 0;
		for(int i = 0;i < sprites.count;i ++){
			layers ++;
			Layer g = sprites.layers[i];

			if((g instanceof RotatedCacheLayer)){
				RotatedCacheLayer sprite = (RotatedCacheLayer)g;
				Sprite terrain = GetSprite(sprite.region);
				if(terrain == null) continue;
				terrain.setPosition(sprite.x, sprite.y);
				//terrain.setFlip(sprite.flipx, sprite.flipy);
				if(sprite.originy < -999){
					terrain.setOriginCenter();
				}else{
					terrain.setOrigin(sprite.originx, sprite.originy);
				}
				terrain.setRotation(sprite.rotation);
				terrain.setFlip(sprite.flipx, sprite.flipy);
				if(sprite.color != null) terrain.setColor(sprite.color);
				terrain.draw(batch);
			}else if((g instanceof CacheLayer)){
				CacheLayer sprite = (CacheLayer)g;
				//sprite.x = RelativeX(sprite.x);
				//sprite.y = RelativeY(sprite.y);
				Sprite terrain = GetSprite(sprite.region);
				if(terrain == null) continue;

				terrain.setPosition(sprite.x, sprite.y);
				terrain.getVertices()[SpriteBatch.X2] = sprite.x + sprite.vxadd;
				terrain.getVertices()[SpriteBatch.X3] = sprite.x + terrain.getWidth() + sprite.vxadd;
				terrain.setFlip(sprite.flipx, sprite.flipy);
				if(sprite.flipx && camera.zoom == 1f){
					//    terrain.setX(terrain.getX() + 1f / nearpix);
				}
				if(sprite.color != null) terrain.setColor(sprite.color);
				terrain.draw(batch);
			}else if((g instanceof SpriteLayer)){
				SpriteLayer sprite = (SpriteLayer)g;
				if(sprite.sprite != null){
					sprite.sprite.draw(batch);
				}

				// font.draw(batch, "AAAAAAAAAAaaa", Gdx.graphics.getWidth()/2,
				// Gdx.graphics.getHeight()/2);

			}else if((g instanceof ParticleLayer)){
				ParticleLayer sprite = (ParticleLayer)g;
				sprite.particle.getEmitters().first().update(Gdx.graphics.getDeltaTime());
				sprite.particle.getEmitters().first().draw(batch);
			}else if((g instanceof PointLayer)){
				PointLayer sprite = (PointLayer)g;
				// point.setPosition((int)sprite.x, (int)sprite.y);
				point.setPosition(sprite.x, sprite.y);
				point.setColor(sprite.color);
				point.draw(batch);
			}
		}

		if(Gdx.graphics.getFrameId() % 60 == 0) layercount = layers;
		lightrange = 0;
		lights.clear();

		batch.end();

		gui.begin();
		for(int i = 0;i < sprites.count;i ++){
			Layer g = sprites.layers[i];
			if((g instanceof FontLayer)){
				FontLayer f = (FontLayer)g;
				Vector3 v = camera.project(new Vector3(f.x, f.y, 0));

				font.getData().setScale(f.scale);
				rausfont.getData().setScale(f.scale);
				font.getData().markupEnabled = f.markup;
				rausfont.getData().markupEnabled = f.markup;
				float yadd = (f.alignh) ? (f.raus) ? boundsh(rausfont, f.string) : boundsh(font, f.string) : 0;
				if( !f.raus){
					font.setColor(f.color);
					font.draw(gui, f.string, v.x - bounds(font, f.string) / 2, v.y + yadd);
				}else{
					rausfont.setColor(f.color);
					rausfont.draw(gui, f.string, v.x - bounds(rausfont, f.string) / 2, v.y + yadd);
				}
				font.getData().markupEnabled = false;
				rausfont.getData().markupEnabled = false;
			}
		}
		gui.end();
		polybatch.begin();
		polybatch.setProjectionMatrix(camera.combined);
		for(PolygonEffect g : polygons.values()){
			if(g.r == null) g.LoadTex();
			g.ltime ++;
			if(g.ltime > 3000){
				g.type = "end";
			}
			PolygonRenderer.DrawPolygon(polybatch, g);
		}
		polybatch.end();

		minimap.begin(ShapeType.Line);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		minimap.setProjectionMatrix(camera.combined);
		Gdx.gl20.glLineWidth(5);
		for(PolygonEffect g : polygons.values()){
			PolygonRenderer.DrawPolygonShape(minimap, g);
		}
		minimap.end();

		Buffer("light").begin();
		Gdx.gl.glClear(16384);
		batch.begin();
		Buffer("light").getColorBufferTexture().bind(6);
		light.getTexture().bind(0);
		light.setColor(new Color(1.0F - Time.skyColor() - 0.65F, 1.0F - Time.skyColor() - 0.65F, 1.0F - Time.skyColor() - 0.5F, 1.0F));
		light.setPosition(player.x - 40f, player.y - 40f + 5f);
		light.setSize(80, 80);
		light.draw(batch);
		torchsize ++;
		for(Layer g : sprites.layers){
			if((g instanceof LightLayer)){
				LightLayer lightlayer = (LightLayer)g;
				float maxc = 0.1f;

				float colorc = maxc * (float)Math.random();
				if(Math.random() < 1) colorc = 0;

				light.setColor(new Color(lightlayer.color.r - Time.skyColor(), lightlayer.color.g - Time.skyColor(), lightlayer.color.b - Time.skyColor(), 1.0f - colorc));
				float lightsize = lightlayer.size + 2.4F * (float)Math.sin(torchsize / 23f) + 5f * (float)Math.random();
				light.setSize(lightsize, lightsize);
				light.setPosition(lightlayer.x - lightsize / 2.0f, lightlayer.y - lightsize / 2.0f);
				light.draw(batch);
				float scale = 1.3f;
				float size = lightlayer.size;

				if(lightrange < size * scale && WithinCircle(lightlayer.x, lightlayer.y, player.x, player.y, size / 3.5f)){
					if(coloradd != null){
						coloradd = new Color((coloradd.r + light.getColor().r) / 2f, (coloradd.g + light.getColor().g) / 2f, (coloradd.b + light.getColor().b) / 2f, 1f);
					}else{
						coloradd = light.getColor();
					}
					lightrange = size * scale;
				}else if(lightrange != size * scale && WithinCircle(lightlayer.x, lightlayer.y, player.x, player.y, size / 2.1f)){
					float s = size * scale * (size / 2.1f - Distance(lightlayer.x, lightlayer.y, player.x, player.y)) / (lightlayer.size / 5.18f);
					if(s > lightrange && s <= size * scale){
						lightrange = s;
					}
				}
			}
		}

		batch.end();
		Buffer("light").end();
		batch.setColor(Color.WHITE);

		sprites.clear();
	}

	float round(float coord){
		return (int)(coord / 0.2f) * 0.2f;
	}

	public static float angle(){
		if( !Settings.android){
			Vector3 u = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
			Vector2 v = new Vector2(u.x - player.x - 0.5f, u.y - player.y - 5f);
			return v.angle();
		}else{
			return shootangle;
		}
	}

	public boolean CanMove(){
		return !ChatOpen && !craftopen && !menuopen;
	}

	public boolean CanChat(){
		return !InventoryOpen && !menuopen;
	}

	public boolean CanOpenInventory(){
		return !ChatOpen && !menuopen;
	}

	void ResizeScreen(int width, int height, boolean full){

		square.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		int vwidth = 350;
		Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		//Gdx.graphics.setVSync(true);
		fullscreen = full;
		//float aspectRatio = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		// camera = new OrthographicCamera( Gdx.graphics.getWidth() /zoomx,
		// Gdx.graphics.getHeight()/zoomx);
		nearpix = width / vwidth + 1;
		camera = new OrthographicCamera(width / nearpix, height / nearpix);

		gui.setProjectionMatrix(square);
		minimap.setProjectionMatrix(square);
		lighteffect.SetSize(width, height);
		/* int dx =90; int dy =70; xrange = Gdx.graphics.getWidth() / dx; yrange = Gdx.graphics.getHeight() / dy; */
		int dx = 15;
		int dy = height / nearpix / 16;
		xro = dx;
		yro = dy;

	}

	void CreateSound(String name){
		Sound s = Gdx.audio.newSound(Gdx.files.internal("sounds/" + name + ".ogg"));
		sounds.put(name, s);
	}

	void CreateMusic(String name){
		Music s = Gdx.audio.newMusic(Gdx.files.internal("sounds/" + name + ".ogg"));
		music.put(name, s);
	}

	void PlaySound(String name){
		if(sounds.containsKey(name)){
			sounds.get(name).play(1.0f);
		}
	}

	public Music GetMusic(String name){
		return music.get(name);
	}

	void LoopMusic(String name){
		if(music.containsKey(name)){
			music.get(name).play();
			music.get(name).setLooping(true);
			// music.get(name).setVolume(0.5f);
		}
	}

	void PlayMusic(String name){
		if(music.containsKey(name)){
			music.get(name).play();
		}
	}

	void PlayMonsterHit(int id){
		if(monstersounds.containsKey(id)){
			PlaySound(monstersounds.get(id).hit);
		}
	}

	void PlayMonsterDeath(int id){
		if(monstersounds.containsKey(id)){
			PlaySound(monstersounds.get(id).death);
		}
	}

	void SetMonsterSound(int id, String hit){
		SoundData d = new SoundData();
		if( !sounds.containsKey(hit)) CreateSound(hit);
		d.hit = hit;
		monstersounds.put(id, d);
	}

	void SetMonsterSound(int id, String hit, String death){
		SoundData d = new SoundData();
		if( !sounds.containsKey(hit)) CreateSound(hit);
		if( !sounds.containsKey(death)) CreateSound(death);
		d.hit = hit;
		d.death = death;
		monstersounds.put(id, d);
	}

	boolean Button(float x, float y, String texture){
		AtlasRegion r = textures.findRegion(texture);
		gui.draw(r, x - r.getRegionWidth() / 2, y - r.getRegionHeight() / 2, r.getRegionWidth(), r.getRegionHeight());
		return false;
	}

	static public FrameBuffer Buffer(String name, int w, int h){
		if(buffers.containsKey(name)){
			return buffers.get(name);
		}else{
			log("Framebuffer \"" + name + "\" created.");
			FrameBuffer buffer = new FrameBuffer(Format.RGBA8888, w, h, false);
			buffers.put(name, buffer);
			return buffer;
		}
	}

	static public FrameBuffer Buffer(String name){
		if(buffers.containsKey(name)){
			return buffers.get(name);
		}else{
			log("Framebuffer \"" + name + "\" created.");
			FrameBuffer buffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
			buffers.put(name, buffer);
			return buffer;
		}
	}/* static Rune GetRune(int id){ return runeids.get(id); }
	  * 
	  * static Rune GetRune(String name){ return runenames.get(name); }
	  * 
	  * static int GetRuneId(String name){ return runenames.get(name).id; }
	  * 
	  * static String GetRuneName(int id){ return runeids.get(id).name; } */

	public void CreateTextureIndicator(String name, int cid){
		if( !players.containsKey(cid)) cid = -1;
		textureindicators.add(new TextureIndicator(name, cid));
	}

	void SaveDetails(){
		// if(Settings.android)return;
		Save.WriteClientConfig();
	}

	void LoadAccountDetails(){
		Save.LoadClientConfig();
		//MenuRenderer.token = Settings.GetValue("token");
		MenuRenderer.Initialize();
	}

	float FrameRand(){
		return (float)Math.random() * framer - framer / 2;
	}

	public boolean IsInInventory(){
		return InventoryOpen && ((guiX >= 0 && guiY >= 0 && guiX < 5 && guiY < 6) || player.selecteditem != null);
	}

	static float rand(float max){
		return (float)Math.random() * max * 2 - max;
	}

	class Network extends Listener{
		public void received(Connection connection, Object object){
			if( !object.getClass().equals(WorldUpdatePacket.class) && !(object instanceof Entity)) lastpacket = object.getClass().getSimpleName();
			try{
				if(connected){
					if(object instanceof DataPacket){
						DataPacket p = (DataPacket)object;
						log("recieved data packet..");
						SaveDetails();
						Time.minute = p.time;
						player.extra = p.extra;
						player.colors = p.colors;
						player.inventory = p.inventory;
						Entity.entities = p.entities;
						for(Entity e : Entity.entities.values())
							e.Init();
						player.ResetID(p.entityID);
						player.x = p.x;
						player.y = p.y;
						player.AddSelf();
						Entity.SetLastID(player.GetID() + 1);
						recievedData = true;
						SendChunkRequest(true);
						Input.Update();
						Home.log(Entity.entities.size() + " entities recieved");
						MenuRenderer.FadeOut( -1);
						log("Data packet recieved!");
					}else if(object instanceof PlayerAnimationPacket){
						PlayerAnimationPacket p = (PlayerAnimationPacket)object;
						Player player = (Player)Entity.Get(p.id);
						player.attacktype = p.attacktype;
						player.attackframe = p.attacktime;
						if(player.equals(Home.player)){
							WeaponSpriter.AnimationEvent();
						}
					}else if(object instanceof IndicatorPacket){
						IndicatorPacket p = (IndicatorPacket)object;
						CreateTextureIndicator(p.texture, p.cid);
					}else if(object instanceof Entity){
						Entity p = (Entity)object;
						p.Init();
						//queue.Add(p);
						p.AddSelf();
					}else if(object instanceof EntityRemovePacket){
						EntityRemovePacket p = (EntityRemovePacket)object;

						Entity e = Entity.entities.get(p.id);
						if(e == null) return;
						e.Removed();
						e.RemoveSelf();
					}else if(object instanceof DashPacket){
						//DashPacket p = (DashPacket)object;
						player.dashcooldown += Player.dash_cooldown;
					}else if(object instanceof InventorySyncPacket){
						InventorySyncPacket p = (InventorySyncPacket)object;
						player.inventory = p.inventory;
					}else if(object instanceof PolygonChangePacket){
						PolygonChangePacket p = (PolygonChangePacket)object;
						try{
							polygons.get(p.id).type = p.type;
						}catch(Exception e){
							log("[Error] polygon not found!");
						}
					}else if(object instanceof RecipeLoadPacket){
						RecipeLoadPacket p = (RecipeLoadPacket)object;
						craftopen = true;
						recipetype = p.type;
						recipes = p.recipes;
						selectedrecipe = -1;
					}else if(object instanceof EffectApplyPacket){
						EffectApplyPacket p = (EffectApplyPacket)object;
						if( !players.containsKey(p.cid)){
							StatusEffect effect = new StatusEffect();
							effect.duration = p.duration;
							effect.type = p.type;
							player.statuseffects.add(effect);
						}
					}else if(object instanceof ParticleEffectPacket){
						ParticleEffectPacket p = (ParticleEffectPacket)object;
						if(p.anglemax < -999){
							CreateParticleEffect(p.x, p.y, (int)(p.y / 12) * 12 - 0.1f, p.rotation, p.type);
						}else{
							CreateParticleEffect(p.type, p.x, p.y - 1f, p.anglemin, p.anglemax);
						}
					}else if(object instanceof BlockSetPacket){
						BlockSetPacket p = (BlockSetPacket)object;
						if(p.up){
							SetDamage(p.x, p.y, 0);
							if(p.id == 0){
								new BlockBreakAnimation(p.x, p.y, world.block(1, p.x, p.y));
							}
							world.setblock(1, p.x, p.y, p.id);
						}else{
							world.setblock(0, p.x, p.y, p.id);
						}
					}else if(object instanceof TeleportPacket){
						TeleportPacket p = (TeleportPacket)object;
						player.x = p.x;
						player.y = p.y;
					}else if(object instanceof AttackCooldownPacket){
						AttackCooldownPacket p = (AttackCooldownPacket)object;
						attackcooldown = p.cooldown;
					}else if(object instanceof PolygonPacket){
						PolygonPacket p = (PolygonPacket)object;
						PolygonEffect e = new PolygonEffect(p.p, p.type, p.id);
						polygons.put(p.id, e);
					}else if(object instanceof ItemAddPacket){
						ItemAddPacket p = (ItemAddPacket)object;
						if( !players.containsKey(p.cid)){
							if(p.amount > 0){
								new DropIndicator(player.x, player.y, (byte)p.id, p.amount);
							}
						}else{
							new DropIndicator(players.get(p.cid).x, players.get(p.cid).y, (byte)p.id, p.amount);
						}
					}else if(object instanceof ChunkPacket){
						ChunkPacket p = (ChunkPacket)object;
						chunkloaded[p.x / 10][p.y / 10] = true;
						for(int x = 0;x < 10;x ++){
							for(int y = 0;y < 10;y ++){
								if(p.x + x < world.worldwidth && p.y + y < world.worldheight){
									world.setblock(0, x + p.x, y + p.y, p.blocks[0][x][y]);
									world.setblock(1, x + p.x, y + p.y, p.blocks[1][x][y]);
								}
							}
						}

					}else if(object instanceof LootDropPacket){
						LootDropPacket p = (LootDropPacket)object;
						LootBag b = new LootBag(p.x, p.y);
						b.items = p.contents;
						b.id = p.id;
						bags.add(b);
					}else if(object instanceof BlockDamagePacket){
						BlockDamagePacket p = (BlockDamagePacket)object;
						if( !p.set){
							AddDamage(p.x, p.y, p.amount);
						}else{
							SetDamage(p.x, p.y, p.amount);
						}
					}else if(object instanceof BagRemovePacket){
						BagRemovePacket p = (BagRemovePacket)object;
						for(LootBag b : bags){
							if(b.id == p.id){
								bags.remove(b);
								break;
							}
						}
						if(selectedbag != null && p.id == selectedbag.id){
							selectedbag = null;
						}
					}else if(object instanceof WorldUpdatePacket){
						WorldUpdatePacket p = (WorldUpdatePacket)object;
						player.mana = p.mana;
						player.health = p.health;
						lastping = connection.getReturnTripTime();
						for(SyncBuffer b : p.positions){
							if(Entity.Exists(b.entityid)){
								((Syncable)Entity.Get(b.entityid)).ReadSync(b);
							}else{
								//	Home.log("entity " + b.entityid + " does not exist");
							}
						}
					}else if(object instanceof EntityUpdatePacket){
						EntityUpdatePacket p = (EntityUpdatePacket)object;
						if(Entity.Exists(p.id) && Entity.Get(p.id) instanceof SolidEntity){
							SolidEntity e = (SolidEntity)Entity.Get(p.id);
							e.velocity = p.velocity;
						}
					}else if(object instanceof EquipListPacket){
						EquipListPacket p = (EquipListPacket)object;
						Player player = players.get(p.id);
						for(int i = 0;i < 5;i ++){
							player.inventory[i][5] = p.equips[i];
						}
					}else if(object instanceof PositionCorrectPacket){
						PositionCorrectPacket p = (PositionCorrectPacket)object;
						player.x = p.x;
						player.y = p.y;
					}else if(object instanceof DisconnectPacket){
						DisconnectPacket p = (DisconnectPacket)object;
						Player player = players.get(p.id);
						player.RemoveSelf();
						players.remove(p.id);
					}else if(object instanceof ItemDropPacket){
						ItemDropPacket p = (ItemDropPacket)object;
						for(LootBag b : bags){
							if(b.id == p.lootbagId){
								for(int i = 0;i < 5;i ++){
									if(b.items[i] == null){
										b.items[i] = p.a;
										break;
									}
								}
								break;
							}
						}
					}else if(object instanceof DeathPacket){
						DeathPacket p = (DeathPacket)object;
						if(players.containsKey(p.id)){
						}else{
							// PlaySound("playerdie");
							// Flash(7,Color.RED);
							player.x = p.x;
							player.y = p.y;
							player.health = 100;
						}
					}else if(object instanceof ChatPacket){
						ChatPacket p = (ChatPacket)object;
						chatfade += fadescale;

						if(players.containsKey(p.id)){ // packet is a player's
							// chat message
							players.get(p.id).message = LineLength(p.rawmessage, messagelength);
							players.get(p.id).messagetime += messageduration;
							chat.add(new ChatMessage(p.message, p.rawmessage, players.get(p.id).name));
						}else if(p.id != -1){ // the packet is your own chat message
							player.message = LineLength(p.rawmessage, messagelength);
							player.messagetime += messageduration;
							chat.add(new ChatMessage(p.message, p.rawmessage, player.name));
						}else{ // packet is an announcement/server message
							chat.add(new ChatMessage(p.message, "", ""));
						}
					}else if(object instanceof EntityAnimationPacket){
						EntityAnimationPacket p = (EntityAnimationPacket)object;
						Monster m = (Monster)Entity.Get(p.id);
						if(m.animation == null) return;
						m.animation.GetUpdate(p);
					}else if(object instanceof StatUpdatePacket){
						StatUpdatePacket p = (StatUpdatePacket)object;
						if( !p.type){
							player.health = p.amount;
						}else{
							player.mana = p.amount;
						}
					}else if(object instanceof PlayerDamagePacket){
						PlayerDamagePacket p = (PlayerDamagePacket)object;
						if(players.containsKey(p.id)){
							DamageIndicator d = new DamageIndicator();
							d.x = players.get(p.id).x;
							d.y = players.get(p.id).y;
							d.message = p.amount + "";
							indicators.add(d);
							if(world.GetTile((int)((players.get(p.id).x + 4f) / 12f), (int)((players.get(p.id).y) / 12f)) == 120){
								SetFrame((int)((players.get(p.id).x + 4f) / 12f), (int)((players.get(p.id).y) / 12f), 17);
							}
						}else{
							DamageIndicator d = new DamageIndicator();
							d.x = player.x;
							d.y = player.y;
							d.message = p.amount + "";
							indicators.add(d);
							if(world.GetTile((int)((player.x + 4f) / 12f), (int)((player.y) / 12f)) == 120){
								SetFrame((int)((player.x + 4f) / 12f), (int)((player.y) / 12f), 17);
							}
						}
					}else if(object instanceof KickPacket){
						KickPacket p = (KickPacket)object;
						errorMessage = p.reason;
						Disconnect();

					}else if(object instanceof BagClickedPacket){
						BagClickedPacket p = (BagClickedPacket)object;
						for(LootBag b : bags){
							if(b.id == p.id){
								if(b.items[p.x] != null){
									if(players.containsKey(p.cid)){
										new DropIndicator(b.x, b.y, (byte)b.items[p.x].id, b.items[p.x].amount);
									}else{
										AddItemToInventory(b.items[p.x]);
									}
								}
								b.items[p.x] = null;
								break;
							}
						}
					}else if(object instanceof ConnectInfoPacket){
						ConnectInfoPacket p = (ConnectInfoPacket)object;
						Home.log("Connect info packet: " + p.info);
						MenuRenderer.ConnectInfoSent(p.info);
					}
				}else if(object instanceof AccountInfoPacket){
					AccountInfoPacket p = (AccountInfoPacket)object;
					MenuRenderer.AccountInfoSent(p.message, p.error);
				}else if(object instanceof TokenPacket){
					TokenPacket p = (TokenPacket)object;
					Home.log("Recieved login token.");
					Settings.SetValue("token", p.token);
					MenuRenderer.TokenRecieved(p.name, true);
					Save.WriteClientConfig();
				}else if(object instanceof TokenVerificationPacket){
					TokenVerificationPacket p = (TokenVerificationPacket)object;
					MenuRenderer.TokenRecieved(p.name, p.valid);
				}else if(object instanceof LoginInfoPacket){
					LoginInfoPacket p = (LoginInfoPacket)object;
					Home.log("Login info packet: " + p.info);
					MenuRenderer.LoginInfoSent(p.info);
				}else if(object instanceof ConnectInfoPacket){
					ConnectInfoPacket p = (ConnectInfoPacket)object;
					Home.log("Connect info packet: " + p.info);
					MenuRenderer.ConnectInfoSent(p.info);
				}
			}catch(Exception e){
				log(e);
			}
		}
	}

	public static float bounds(BitmapFont f, String s){
		glyphs.setText(f, s);
		return glyphs.width;
	}

	public static float boundsh(BitmapFont f, String s){
		glyphs.setText(f, s);
		return glyphs.height;
	}

	public static String hex(int i){
		String hex = Integer.toHexString(i);
		if(hex.length() == 1) hex = "0" + hex;
		return hex;
	}

	static public void Quit(){
		Gdx.app.exit();
	}

	void Disconnect(){
		ClearAll();
		connected = false;
		menuopen = false;
		recievedData = false;
		MenuRenderer.stage = "";
		MenuRenderer.ready = false;
		client.close();
		log("Disconnected.");
	}

	public static boolean NearRange(float one, float two, float range){
		if(one + range >= two && one - range <= two){
			return true;
		}
		return false;
	}

	static boolean NearRange(float x1, float y1, float x2, float y2, float range){
		return NearRange(x1, x2, range) && NearRange(y1, y2, range);
	}

	static public void log(Object o){
		if(o instanceof Exception){
			log(o.toString());
			((Exception)o).printStackTrace();
			return;
		}

		System.out.println(o);
		console.append(o + "\n");
	}

	static public void DrawOnMinimap(float x, float y, Color color, float range){
		int camx = (int)camera.position.x / 12, camy = (int)camera.position.y / 12, mx = (int)x / 12, my = (int)y / 12;
		if(camx < 23) camx = 23;
		if(camy < 23) camy = 23;
		if(camx > world.worldwidth - 23) camx = world.worldwidth - 23;
		if(camy > world.worldheight - 23) camy = world.worldheight - 23;
		if(NearRange(x, y, player.x, player.y, range) && mx - camx + 23 >= 0 && mx - camx + 23 < 46 && my - camy + 23 >= 0 && my - camy + 23 < 46) displaycolors[mx - camx + 23][my - camy + 23] = color;
	}

	static public void DrawOnMinimap(float x, float y, Color color, float range, float blightrange, float blightrange2){
		int camx = (int)camera.position.x / 12, camy = (int)camera.position.y / 12, mx = (int)x / 12, my = (int)y / 12;
		if(camx < 23) camx = 23;
		if(camy < 23) camy = 23;
		if(camx > world.worldwidth - 23) camx = world.worldwidth - 23;
		if(camy > world.worldheight - 23) camy = world.worldheight - 23;
		if(NearRange(x, y, player.x, player.y, range) && mx - camx + 23 >= 0 && mx - camx + 23 < 46 && my - camy + 23 >= 0 && my - camy + 23 < 46 && (WithinCircle(x, y, player.x, player.y, blightrange + lightrange / 2) || ( !IsUnderground() && (Time.skyColor() > 0.3f || (WithinCircle(x, y, player.x, player.y, blightrange2)))))) displaycolors[mx - camx + 23][my - camy + 23] = color;
	}

	static public float scale(){
		return GUIElement.scale;
	}

	static public float scale(float f){
		return GUIElement.scale * f;
	}

	static public float iscale(){
		return scale() / 5f;
	}

	static public float ifscale(){
		return GUIElement.fontscale;
	}

	static public float fscale(){
		return GUIElement.fontscale;
	}

	static public boolean IsLeftStickHeld(){
		return Home.leftpointer != -1;
	}

	@Override
	public void pause(){
		if(Settings.android){
			Disconnect();
		}else{
			menuopen = true;
		}
	}

	@Override
	public void create(){
		CreateObjects();
		LoadTextures();
		Initialize();
		CreateClient();
	}

}