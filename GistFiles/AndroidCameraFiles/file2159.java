package mdesl.lumos;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * "Lerp Blur" - faux real-time blurring for Android
 * 
 * Code from OpenGL ES Blurs article:
 * https://github.com/mattdesl/lwjgl-basics/wiki/OpenGL-ES-Blurs
 * 
 * @author davedes
 */
public class LerpBlurB implements ApplicationListener {

  
	final String VERT =  
			"attribute vec4 "+ShaderProgram.POSITION_ATTRIBUTE+";\n" +
			"attribute vec2 "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
			"attribute vec2 "+ShaderProgram.TEXCOORD_ATTRIBUTE+"1;\n" +
			"uniform mat4 u_projTrans;\n" + 
			" \n" + 
			"varying vec2 vTexCoordA;\n" +
			"varying vec2 vTexCoordB;\n" +
			"void main() {\n" +  
			"	vTexCoordA = "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
			"	vTexCoordB = "+ShaderProgram.TEXCOORD_ATTRIBUTE+"1;\n" +
			"	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
			"}";
	
	final String FRAG = 
			  "#ifdef GL_ES\n" //
			+ "precision mediump float;\n" //
			+ "#endif\n" + //
			"varying vec2 vTexCoordA;\n" +
			"varying vec2 vTexCoordB;\n" +
			"uniform sampler2D u_texture;\n" +
			"uniform float lerp;\n" +
			"void main() {\n" +  
			"	vec4 texColorA = texture2D(u_texture, vTexCoordA);\n" +
			"	vec4 texColorB = texture2D(u_texture, vTexCoordB);\n" +
			"	\n" + 
			"	gl_FragColor = mix(texColorA, texColorB, lerp);\n" + 
			"}";
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 600;
		cfg.resizable = true;

		new LwjglApplication(new LerpBlurB(), cfg);
	}
	
	SpriteBatch batch;
	OrthographicCamera cam;
	
	BitmapFont fps;
	ShaderProgram shader;
	
	Texture blurMap;
	TextureRegion[] blurs;
	public float maxBlur;
	
	Mesh mesh;
	int origWidth;
	int origHeight;
	
	float[] verts;
	
	void recreateBlurMap() {
		//generate a "blur map" from our image
		BlurMap map = new BlurMap(Gdx.files.internal("data/lenna.png"), false, blurMap);
		
		//grab the texture
		blurMap = map.texture;
		blurMap.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		blurMap.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		//grab the regions
		blurs = map.regions;
		origWidth = blurs[0].getRegionWidth();
		origHeight = blurs[0].getRegionHeight();
		maxBlur = blurs.length-1;
		
	}
	
	@Override
	public void create() {
		batch = new SpriteBatch(1000);
		
		fps = new BitmapFont();
		
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.setToOrtho(false);
		
		recreateBlurMap();
		
		ShaderProgram.pedantic = false;
		shader = new ShaderProgram(VERT, FRAG);
		if (!shader.isCompiled()) {
			Gdx.app.log("ShaderLessons", "Could not compile shaders: "+shader.getLog());
			Gdx.app.exit();
		}
		
		//setup shader; we need to explicitly send u_projTrans and u_texture
		shader.begin();
		shader.setUniformf("lerp", 0f);
		shader.setUniformMatrix("u_projTrans", cam.combined);
		shader.setUniformi("u_texture", 0);
		shader.end();
		
		//mesh of our sprite
		mesh = new Mesh(false, 4 * 6, 6, new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
										 new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0"),
										 new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"1"));
		
		// vertices are laid out like so: { x, y, u, v, u2, v2 }
		
		//we'll use triangles
		mesh.setIndices(new short[] {
			0, 1, 2, 
			2, 3, 0
		});
		verts = new float[6*4];
	}
	
	float blur = 0;
	
	void draw(int x, int y, int width, int height, float blurStrength) {
		//get integer (i.e. index of blur texture region)
		int iblurStrength = (int)blurStrength;
		//get fractional (i.e. amount to mix between the two regions)
		float lerp = blurStrength - iblurStrength;
		TextureRegion A, B;
		if (iblurStrength<=0) {
			//make both texcoords the same
			A = B = blurs[0];
		} else {
			//the previous strength
			A = blurs[iblurStrength-1];
			//the current strength
			B = blurs[iblurStrength];
		}
		int idx = 0;
		//bottom left
		verts[idx++] = x;
		verts[idx++] = y;
		verts[idx++] = A.getU();
		verts[idx++] = A.getV2();
		verts[idx++] = B.getU();
		verts[idx++] = B.getV2();
		
		//top left
		verts[idx++] = x;
		verts[idx++] = y + height;
		verts[idx++] = A.getU();
		verts[idx++] = A.getV();
		verts[idx++] = B.getU();
		verts[idx++] = B.getV();
		
		//top right
		verts[idx++] = x + width;
		verts[idx++] = y + height;
		verts[idx++] = A.getU2();
		verts[idx++] = A.getV();
		verts[idx++] = B.getU2();
		verts[idx++] = B.getV();
		
		//bottom right
		verts[idx++] = x + width;
		verts[idx++] = y;
		verts[idx++] = A.getU2();
		verts[idx++] = A.getV2();
		verts[idx++] = B.getU2();
		verts[idx++] = B.getV2();
		
		//set the vertices to the above
		mesh.setVertices(verts);
		
		//bind our blur map texture
		blurMap.bind();
		
		//begin our shader and set the "lerp" value
		shader.begin();
		shader.setUniformf("lerp", lerp);
		
		//render our mesh with the shader
		mesh.render(shader, GL10.GL_TRIANGLES);
		shader.end();
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
		
		float blur = maxBlur * (Gdx.input.getX() / (float)Gdx.graphics.getWidth());
		
		draw(0, 0, origWidth, origHeight, blur);
		
		batch.begin();
		
		//if you want to see what the blur map looks like, uncomment this
//		batch.draw(blurMap, 0, 0);
		
		String str = String.valueOf(Gdx.graphics.getFramesPerSecond()) 
				+ "\nBlur Strength: "+blur;
		fps.drawMultiLine(batch, str, 5, Gdx.graphics.getHeight()-5);
		
		batch.end();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		recreateBlurMap();
	}

	@Override
	public void dispose() {
		batch.dispose();
		blurMap.dispose();
	}
	
	public static class BlurMap {
		Texture texture;
		TextureRegion[] regions;
		
		/**
		 * Generates a "blur map" of the given file, and uploads it to
		 * the given reusable Texture object. If Texture is null, a new
		 * texture will be created. Otherwise, the pixmap will simply
		 * be uploaded using glTexSubImage2D.
		 * 
		 * @param file the original image to load
		 * @param smaller whether use a smaller pack layout, using non-power-of-two sizes
		 * @param sharedTex the texture to reuse, or null to create a new one
		 */
		public BlurMap(FileHandle file, boolean smaller, Texture sharedTex) {
			Blending blend = Pixmap.getBlending();
			Pixmap.setBlending(Blending.None);
			
			Pixmap lenna = new Pixmap(file);
			
			//convert if necessary to RGBA8888
			if (lenna.getFormat()!=Format.RGBA8888) {
				Pixmap tmp = new Pixmap(lenna.getWidth(), lenna.getHeight(), Format.RGBA8888);
				tmp.drawPixmap(lenna, 0, 0);
				lenna.dispose();
				lenna = tmp;
			}
			
			//our texture, initially empty
			int fullWidth = smaller ? lenna.getWidth()+lenna.getWidth()/2 : lenna.getWidth()*2;
			int fullHeight = smaller ? lenna.getHeight() : lenna.getHeight()*2;
			
			texture = sharedTex!=null ? sharedTex : new Texture(fullWidth, fullHeight, Format.RGBA8888);
			
			//our texture regions...
			int pos = 0;
			regions = new TextureRegion[smaller ? 6 : 10];
			
			//the full blur map, which contains all our images
			Pixmap pixmap = new Pixmap(fullWidth, fullHeight, Format.RGBA8888);
			
			//draw the original image to the full map
			pixmap.drawPixmap(lenna, 0, 0);
			//create a region of it
			regions[pos++] = new TextureRegion(texture, 0, 0, lenna.getWidth(), lenna.getHeight());
			
			int radius = 1; //blur radius
			
			if (smaller) {
				Pixmap half = new Pixmap(lenna.getWidth()/2, lenna.getHeight()/2, Format.RGBA8888);
				half.drawPixmap(lenna, 0, 0, lenna.getWidth(), lenna.getHeight(), 0, 0, half.getWidth(), half.getHeight());
				
				Pixmap qt = new Pixmap(lenna.getWidth()/4, lenna.getHeight()/4, Format.RGBA8888);
				qt.drawPixmap(half, 0, 0, half.getWidth(), half.getHeight(), 0, 0, qt.getWidth(), qt.getHeight());
				
				int x = lenna.getWidth();
				int y = 0;
				Pixmap p = half;
				for (int i=0; i<5; i++) {
					if (i==3) {
						x = lenna.getWidth();
						y = lenna.getHeight()/2 + qt.getHeight();
					}
					//blur it
					Pixmap blurred = BlurUtils.blur(p, radius++, 2, false);
					pixmap.drawPixmap(blurred, x, y);
					blurred.dispose();
					
					regions[pos++] = new TextureRegion(texture, x, y, p.getWidth(), p.getHeight()); 
					
					//done with first pass, now downsample again
					if (i==0) {
						y = lenna.getHeight()/2;
						p = qt;
					} else {
						p.getPixels().flip();
						x += p.getWidth();
					}
				}
				half.dispose();
				qt.dispose();
			} else {
				//draw the original to half size
				Pixmap half = new Pixmap(lenna.getWidth()/2, lenna.getHeight()/2, Format.RGBA8888);
				half.drawPixmap(lenna, 0, 0, lenna.getWidth(), lenna.getHeight(), 0, 0, half.getWidth(), half.getHeight());
				
				//blur the original, then draw at full size to the top right
				Pixmap blurred1 = BlurUtils.blur(lenna, radius++, 2, false); //don't dispose
				pixmap.drawPixmap(blurred1, lenna.getWidth(), 0);
				//create a region
				regions[pos++] = new TextureRegion(texture, lenna.getWidth(), 0, lenna.getWidth(), lenna.getHeight());
				//dispose it since we no longer need it
				blurred1.dispose();
				
				//create the increasing blurs
				int x = 0;
				int y = lenna.getHeight();
				for (int i=0; i<8; i++, x+=half.getWidth()) {
					if (i == 4) {
						x = 0;
						y += half.getHeight();
					}	
					
					//create a blurred pixmap
					Pixmap blurred = BlurUtils.blur(half, radius++, 2, false);
					pixmap.drawPixmap(blurred, x, y);
					blurred.dispose();
					half.getPixels().flip();
					regions[pos++] = new TextureRegion(texture, x, y, half.getWidth(), half.getHeight()); 
				}
				half.dispose();
			}
			
			//upload the Pixmap data to the GL texture object
			texture.draw(pixmap, 0, 0);
			
			//dispose the texture data
			pixmap.dispose();
			lenna.dispose();
			Pixmap.setBlending(blend);
		}
	}
}