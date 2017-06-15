package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.sun.glass.ui.Screen;

public class MyGdxGameTest extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Texture img;
	private OrthographicCamera camera;
	Vector3 tp = new Vector3();
	private ExtendViewport viewport;

	boolean tocado = false;

	int pix[] = {30, 30};

	// we will use 32px/unit in world
	public final static float SCALE = 32f;
	public final static float INV_SCALE = 1.f/SCALE;
	// this is our "target" resolution, not that the window can be any size, it is not bound to this one
	public final static float VP_WIDTH = 1280 * INV_SCALE;		// Por suerte la resolucion coincide
	public final static float VP_HEIGHT = 720 * INV_SCALE;

	private int coord[] = new int[2];
	
	@Override
	public void create () {
		// Camara de la "normalita"
		camera = new OrthographicCamera();
		// Viewport.... ni idea para que es necesario
		viewport = new ExtendViewport(VP_WIDTH, VP_HEIGHT, camera);
		// El "lote" encargado de dibujar los Sprites
		batch = new SpriteBatch();
		// Imprimir la resulcion de la pantalla
		System.out.println(Gdx.graphics.getWidth() + " x " + Gdx.graphics.getHeight());
		// Cargar la textura de "badlogic.jpg"
		img = new Texture("badlogic.jpg");
		// Entrada de pantalla
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
		// LIMPIAR LA PANTALLA!!
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Empezar a dibujar
		batch.begin();
		// Dibujar las cosas
		batch.draw(img, 0, 0);
		if (tocado)
			batch.draw(img, coord[0], coord[1]);

		batch.draw(img, pix[0], pix[1]);
		batch.draw(img, 500, Gdx.graphics.getHeight() - img.getHeight());
		// Acabar el dibujado
		batch.end();

		pix[0] += 5;
		pix[1] += 5;

		pix[0] %= 1280;
		pix[1] %= 720;
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
		// ignore if its not left mouse button or first touch pointer
		if (button != Input.Buttons.LEFT || pointer > 0) return false;

		camera.unproject(tp.set(screenX, screenY, 0));

		tocado = true;
		coord[0] = (int) tp.x;
		coord[1] = (int) tp.y;

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button != Input.Buttons.LEFT || pointer > 0) return false;
		camera.unproject(tp.set(screenX, screenY, 0));
		tocado = false;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!tocado) return false;
		camera.unproject(tp.set(screenX, screenY, 0));
		return true;
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
