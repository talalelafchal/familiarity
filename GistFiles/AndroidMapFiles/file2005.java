package ru.vitoz80.text;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Txt extends Game {
    private SpriteBatch batch;
    private Text text;

    @Override
    public void create() {
        batch = new SpriteBatch();
        text = new Text("alphabet.atlas");
    }

    @Override
    public void render() {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        text.scrollRL(batch, "жуйте французские булки господа... и пейте чай", 1280f, 150f, 1f, 2f, Color.WHITE);
        text.draw(batch, "съешь ещё этих мягких французских булок, да выпей чаю", 0f, 100f, 0.7f, Color.BLUE);
        text.draw(batch, "съешь ещё этих мягких французских булок, да выпей чаю", 0f, 150f, 0.5f);
        text.draw(batch, "съешь ещё этих мягких французских булок, да выпей чаю");
        text.draw(batch);
        batch.end();
    }
}
