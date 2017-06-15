package org.gleason.gametutorial;

import javax.microedition.khronos.opengles.GL10;

import org.gleason.gametutorial.model.ArenaBarrier;
import org.gleason.gametutorial.model.Hero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GameScreen implements Screen {

  private World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;
	private Hero hero;
	private int count;

	float accelX;
	float accelY;
	float accelZ;

	public GameScreen() {
		count = 0;

		Vector2 gravity = new Vector2(0, -10f);
		world = new World(gravity, true);
		// Setup our hero
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(50f, 70f);
		bodyDef.angle = 0;
		hero = new Hero();
		hero.setBody(world.createBody(bodyDef));
		hero.createFixture();

		// Here we setup the walls
		BodyDef floorBodyDef = new BodyDef();
		floorBodyDef.type = BodyType.StaticBody;
		floorBodyDef.position.set((Gdx.graphics.getWidth() / 2), 10);
		floorBodyDef.angle = 0;
		ArenaBarrier floor = new ArenaBarrier(
				(Gdx.graphics.getWidth() / 2) - 10, 0);
		floor.setBody(world.createBody(floorBodyDef));
		floor.createFixture();

		BodyDef roofBodyDef = new BodyDef();
		roofBodyDef.type = BodyType.StaticBody;
		roofBodyDef.position.set(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() - 10);
		roofBodyDef.angle = 0;
		ArenaBarrier roof = new ArenaBarrier(
				(Gdx.graphics.getWidth() / 2 - 10), 0);
		roof.setBody(world.createBody(roofBodyDef));
		roof.createFixture();

		BodyDef leftBodyDef = new BodyDef();
		leftBodyDef.type = BodyType.StaticBody;
		leftBodyDef.position.set(10, Gdx.graphics.getHeight() / 2);
		leftBodyDef.angle = 0;
		ArenaBarrier left = new ArenaBarrier(0,
				(Gdx.graphics.getHeight() / 2) - 10);
		left.setBody(world.createBody(leftBodyDef));
		left.createFixture();

		BodyDef rightBodyDef = new BodyDef();
		rightBodyDef.type = BodyType.StaticBody;
		rightBodyDef.position.set(Gdx.graphics.getWidth() - 10,
				Gdx.graphics.getHeight() / 2);
		rightBodyDef.angle = 0;
		ArenaBarrier right = new ArenaBarrier(0,
				Gdx.graphics.getHeight() / 2 - 10);
		right.setBody(world.createBody(rightBodyDef));
		right.createFixture();

		camera = new OrthographicCamera();
		camera.viewportHeight = Gdx.graphics.getHeight();
		camera.viewportWidth = Gdx.graphics.getWidth();
		camera.position.set(camera.viewportWidth * .5f,
				camera.viewportHeight * .5f, 0f);
		camera.update();
		debugRenderer = new Box2DDebugRenderer();

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float arg0) {
		// TODO Auto-generated method stub
		itterate();
		count++;
	}

	private void itterate() {
		float timeStep = 1.0f / 45.0f;
		int velocityIterations = 2;
		int positionIterations = 8;
		float accelX = Gdx.input.getAccelerometerX();
		float accelY = Gdx.input.getAccelerometerY();
		float accelZ = Gdx.input.getAccelerometerZ();
		if (count != 0) {
			if (accelX != this.accelX) {
				// X has change
				this.accelX = accelX;
				float rad = accelX / 10;
				hero.setTilt(-rad);
			}
			if (accelY != this.accelY) {
				// y has changed
				this.accelY = accelY;
				float rad = accelY / 10;
				hero.getBody().setTransform(hero.getBody().getPosition(), rad);

			}
			if (accelZ != this.accelZ) {
				// Z has changed
				this.accelZ = accelZ;
			}
		}
		hero.startBody();
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		debugRenderer.render(world, camera.combined);
		world.step(timeStep, velocityIterations, positionIterations);
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

}
