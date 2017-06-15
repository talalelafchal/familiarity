package net.deltaplay.progress;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ProgressTest extends ApplicationAdapter {

    SpriteBatch batch;
    FitViewport viewport;

    RadialProgress progress;

   @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        viewport = new FitViewport(12, 12);

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/atlas.atlas"));
        TextureRegion textureRegion = atlas.findRegion("progress");

        if (textureRegion == null) {
            throw new GdxRuntimeException("Could not load texture!");
        }

        this.progress = new RadialProgress(textureRegion, 5, 5, 2, 2, true);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        progress.setPercent(progress.getPercent() + Gdx.graphics.getDeltaTime() / 10);

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        progress.draw(batch);
        batch.end();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

}