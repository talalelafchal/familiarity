package com.theo5970.project01;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MainGame extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Texture img;
	Texture img2;
	private World box2d_world;

	private FPSLogger fpsLogger;
	private OrthographicCamera camera;
	ArrayList<MovingBody> movingBodies = new ArrayList<MovingBody>();
	@Override
	public void create() {

		camera = new OrthographicCamera(320, 180);
		camera.position.x = camera.viewportWidth / 2;
		camera.position.y = camera.viewportHeight / 2;
		camera.update();

		fpsLogger = new FPSLogger();

		batch = new SpriteBatch();
		img = new Texture("box.png");
		img2 = new Texture("circle.png");

		box2d_world = new World(new Vector2(0, -10.8f), true);
		Box2DBuilder.init(box2d_world);
		Box2DBuilder.buildBox(BodyType.StaticBody, 10000, 0, 20000, 0);
		Box2DBuilder.buildBox(BodyType.StaticBody, 0, 5000, 10, 10000);
		Box2DBuilder.buildBox(BodyType.StaticBody, 320, 5000, 10, 10000);

		Box2DBuilder.buildBox(BodyType.DynamicBody, 100, 500, 1, 1);
		Box2DBuilder.buildBox(BodyType.DynamicBody, 200, 500, 100, 100);

		Gdx.input.setInputProcessor(this);
	}

	public void update() {
		for (MovingBody body : movingBodies) {
			body.step();
		}
		fpsLogger.log();
		float deltaTime = Gdx.graphics.getDeltaTime();
		box2d_world.step(deltaTime, 10, 10);
	}
	public void clearScreen() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	@Override
	public void render() {
		update();
		clearScreen();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		renderWorld();
		batch.end();
	}

	// Temporary array of rendering the world.
	Array<Body> bodies = new Array<Body>();

	// Rendering the world
	public void renderWorld() {
		box2d_world.getBodies(bodies);
		for (int i = 0; i < box2d_world.getBodyCount(); i++) {
			renderBody(bodies.get(i));
		}
	}

	public boolean checkInRange(float x, float y, float startX, float startY,
			float endX, float endY) {
		return (startX <= x && x <= endX) && (startY <= y && y <= endY);
	}
	// Rendering each body
	public void renderBody(Body body) {
		GameObject object = (GameObject) body.getUserData();

		Vector2 position = body.getWorldCenter();

		float width = object.shape.width;
		float height = object.shape.height;
		if (object.shape.type == ShapeType.Box) {
			batch.draw(img, position.x - (width / 2), position.y - (height / 2),
					width / 2, height / 2, width, height, 1, 1,
					body.getAngle() * MathUtils.radiansToDegrees, 0, 0,
					img.getWidth(), img.getHeight(), false, false);
		} else {
			batch.draw(img2, position.x - width, position.y - width, width,
					width, width * 2, width * 2, 1, 1,
					body.getAngle() * MathUtils.radiansToDegrees, 0, 0,
					img2.getWidth(), img2.getHeight(), false, false);
		}

	}

	// Free memory.
	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		float transX = screenX / 4.0f;
		float transY = (Gdx.graphics.getHeight() - screenY) / 4.0f;
		Box2DBuilder.buildBox(BodyType.DynamicBody, transX, transY, 10, 10);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
