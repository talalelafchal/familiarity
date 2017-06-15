package com.collosteam.bestbuttonsthe;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created with IntelliJ IDEA.
 * User: Miroshnychenko Andre
 * Date: 12.11.13
 * Time: 22:42
 * To change this template use File | Settings | File Templates.
 */
public class BaseGame extends Game{
    SpriteBatch batch;
    BitmapFont font;
    TextureAtlas atlas;

    public void create() {
        batch = new SpriteBatch();
        // LibGDX по умолчанию использует Arial шрифт.
        font = new BitmapFont(Gdx.files.internal("data/fonts/Lasco_Bold48.fnt"));
        atlas = new TextureAtlas("ui/button.pack");
        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render(); // важно!
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        atlas.dispose();

    }
}
