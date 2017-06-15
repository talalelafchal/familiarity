import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * "Lerp Blur" - faux real-time blurring for Android
 * 
 * Code from OpenGL ES Blurs article:
 * https://github.com/mattdesl/lwjgl-basics/wiki/OpenGL-ES-Blurs
 * 
 * @author davedes
 */
public class LerpBlurA implements ApplicationListener {

  
	final String VERT =  
			"attribute vec4 "+ShaderProgram.POSITION_ATTRIBUTE+";\n" +
			"attribute vec4 "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
			"attribute vec2 "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
			
			"uniform mat4 u_projTrans;\n" + 
			" \n" + 
			"varying vec4 vColor;\n" +
			"varying vec2 vTexCoord;\n" +
			
			"void main() {\n" +  
			"	vColor = "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
			"	vTexCoord = "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
			"	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
			"}";
	
	final String FRAG = 
			//GL ES specific stuff
			  "#ifdef GL_ES\n" //
			+ "#define LOWP lowp\n" //
			+ "precision mediump float;\n" //
			+ "#else\n" //
			+ "#define LOWP \n" //
			+ "#endif\n" + //
			"varying LOWP vec4 vColor;\n" +
			"varying vec2 vTexCoord;\n" + 
			"uniform sampler2D u_texture;\n" +	
			"uniform float bias;\n" +
			"void main() {\n" +  
			"	vec4 texColor = texture2D(u_texture, vTexCoord, bias);\n" +
			"	\n" + 
			"	gl_FragColor = texColor * vColor;\n" + 
			"}";
	
	//Further applications:
		//Blurring the entire screen
		//Blurring in a circle area only, i.e. pinhole lens/vignette
		//Blurring with an alpha map, similar to Photoshop's lens blur
		//Blurring with different modes like sreen/overlay
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.useGL20 = true;
		cfg.width = 640;
		cfg.height = 480;
		cfg.resizable = true;

		new LwjglApplication(new LerpBlurA(), cfg);
	}
	
	Texture tex, tex2;
	
	SpriteBatch batch;
	OrthographicCamera cam;
	
	public static final float MAX_BLUR = 5f;
	BitmapFont fps;
	ShaderProgram shader;
	 
	@Override
	public void create() {
		//load the unblurred atlas, can be in any format
		Pixmap pixmap = new Pixmap(Gdx.files.internal("data/lenna2.png"));
		
		//upload the original data, this will be put to mipmap level 0
		//NOTE: we need to ensure RGBA8888 format is used, otherwise it may not render correctly
		tex = new Texture(pixmap, Format.RGBA8888, false);
		
		//bind before we generate mipmaps
		tex.bind();
		
		//generate our blurred mipmaps
		BlurUtils.generateBlurredMipmaps(pixmap, pixmap.getWidth(), pixmap.getHeight(), 1, 3, true);
		
		//with mipmaps, clamping to edge works a bit better
		tex.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
		
		//any mipmap setting will work; this will give us the smoothest result
		tex.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
		
		shader = new ShaderProgram(VERT, FRAG);
		if (!shader.isCompiled()) {
			Gdx.app.log("ShaderLessons", "Could not compile shaders: "+shader.getLog());
			Gdx.app.exit();
		}

		batch = new SpriteBatch(1000, shader);
		
		shader.begin();
		shader.setUniformf("bias", 0f);
		shader.end();
		
		fps = new BitmapFont();
		
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.setToOrtho(false);
		
	}

	@Override
	public void resize(int width, int height) {
	}
	
	void resizeBatch(int width, int height) {
		cam.setToOrtho(false, width, height);
		batch.setProjectionMatrix(cam.combined);
	}
	
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		
		float bias = MAX_BLUR * (Gdx.input.getX() / (float)Gdx.graphics.getWidth());
		
		shader.setUniformf("bias", bias);
		batch.draw(tex, 0, 0);
		
		batch.flush();
		
		String str = String.valueOf(Gdx.graphics.getFramesPerSecond()) 
				+ "\nBlur Strength: "+bias
				+ "\nTexture Size: "+tex.getWidth()+"x"+tex.getHeight();
		fps.drawMultiLine(batch, str, 5, Gdx.graphics.getHeight()-5);
		
		batch.end();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		tex.dispose();;
	}	
}