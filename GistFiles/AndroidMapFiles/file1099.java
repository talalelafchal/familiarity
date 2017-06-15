package ru.vitoz80.text;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * static and moving text
 * need alphabet.atlas + alphabet.png
 * init Text with TextureAtlas path (alphabet.atlas)
 * space should be a "space" in the alphabet.atlas
 * TextureAtlas should contain your letters, numbers and punctuation marks. Optional
 */

public class Text {

    private Sprite letter;
    private TextureAtlas atlas;
    private StringBuilder blank;
    private ArrayMap<StringBuilder, TextureRegion> alphabet;

    private float size;
    private float x;
    private float y;
    private String str;
    private float accelerate;

    private boolean runLR;

    /**
     * init Text with TextureAtlas path
     */
    public Text(String path) {
        blank = new StringBuilder();
        letter = new Sprite();
        atlas = new TextureAtlas(Gdx.files.internal(path));
        alphabet = new ArrayMap<StringBuilder, TextureRegion>(atlas.getRegions().size);
        str = "съешь ещё этих мягких французских булок, да выпей чаю";
        addRegions();
    }

    private void addRegions() {
        StringBuilder letter;
        for (int region = 0; region < atlas.getRegions().size; region++) {
            //  digits & marks & letters
            letter = new StringBuilder(atlas.getRegions().get(region).name);
            alphabet.put(letter, atlas.getRegions().get(region));
            //  space
            if (atlas.getRegions().get(region).name.equals("space")) {
                letter = new StringBuilder(" ");
                alphabet.put(letter, atlas.getRegions().get(region));
            }
        }
    }

    /**
     * static text
     *
     * @param batch SpriteBatch
     *              str = "съешь ещё этих мягких французских булок, да выпей чаю"
     *              x = 0
     *              y = 0
     *              size = 1
     *              Color.DARK_GRAY
     */
    public void draw(SpriteBatch batch) {
        size = 1f;
        x = 0;
        y = 0;
        for (int i = 0; i < str.length(); i++) {
            blank.delete(0, blank.length);
            blank.append(str.charAt(i));
            letter.setRegion(alphabet.get(blank));
            letter.setColor(Color.DARK_GRAY);
            letter.setBounds(x, y, alphabet.get(blank).getRegionWidth() * size, alphabet.get(blank).getRegionHeight() * size);
            letter.draw(batch);
            x += alphabet.get(blank).getRegionWidth() * size;
        }
    }

    /**
     * static text
     *
     * @param batch SpriteBatch
     * @param str   ""
     *              x = 0
     *              y = 0
     *              size = 1
     *              Color.DARK_GRAY
     */
    public void draw(SpriteBatch batch, CharSequence str) {
        size = 1f;
        x = 0;
        y = 0;
        for (int i = 0; i < str.length(); i++) {
            blank.delete(0, blank.length);
            blank.append(str.charAt(i));
            letter.setRegion(alphabet.get(blank));
            letter.setColor(Color.DARK_GRAY);
            letter.setBounds(x, y, alphabet.get(blank).getRegionWidth() * size, alphabet.get(blank).getRegionHeight() * size);
            letter.draw(batch);
            x += alphabet.get(blank).getRegionWidth() * size;
        }
    }

    /**
     * static text
     *
     * @param batch SpriteBatch
     * @param str   ""
     * @param x     X start
     * @param y     Y start
     *              size = 1
     *              Color.DARK_GRAY
     */
    public void draw(SpriteBatch batch, CharSequence str, float x, float y) {
        size = 1f;
        for (int i = 0; i < str.length(); i++) {
            blank.delete(0, blank.length);
            blank.append(str.charAt(i));
            letter.setRegion(alphabet.get(blank));
            letter.setColor(Color.DARK_GRAY);
            letter.setBounds(x, y, alphabet.get(blank).getRegionWidth() * size, alphabet.get(blank).getRegionHeight() * size);
            letter.draw(batch);
            x += alphabet.get(blank).getRegionWidth() * size;
        }
    }

    /**
     * static text
     *
     * @param batch SpriteBatch
     * @param str   ""
     * @param x     X start
     * @param y     Y start
     * @param size  letter size
     *              Color.DARK_GRAY
     */
    public void draw(SpriteBatch batch, CharSequence str, float x, float y, float size) {
        for (int i = 0; i < str.length(); i++) {
            blank.delete(0, blank.length);
            blank.append(str.charAt(i));
            letter.setRegion(alphabet.get(blank));
            letter.setColor(Color.DARK_GRAY);
            letter.setBounds(x, y, alphabet.get(blank).getRegionWidth() * size, alphabet.get(blank).getRegionHeight() * size);
            letter.draw(batch);
            x += alphabet.get(blank).getRegionWidth() * size;
        }
    }

    /**
     * static text
     *
     * @param batch SpriteBatch
     * @param str   ""
     * @param x     X start
     * @param y     Y start
     * @param size  letter size
     * @param color sequence color
     */
    public void draw(SpriteBatch batch, CharSequence str, float x, float y, float size, Color color) {
        for (int i = 0; i < str.length(); i++) {
            blank.delete(0, blank.length);
            blank.append(str.charAt(i));
            letter.setRegion(alphabet.get(blank));
            letter.setColor(color);
            letter.setBounds(x, y, alphabet.get(blank).getRegionWidth() * size, alphabet.get(blank).getRegionHeight() * size);
            letter.draw(batch);
            x += alphabet.get(blank).getRegionWidth() * size;
        }
    }

    /**
     * moving text right to left
     *
     * @param batch SpriteBatch
     * @param str   ""
     * @param x     X start
     * @param y     Y start
     * @param size  letter size
     * @param speed speed motion
     * @param color sequence color
     */

    public void scrollRL(SpriteBatch batch, CharSequence str, float x, float y, float size, float speed, Color color) {
        if (!runLR) {
            runLR = true;
            accelerate = speed;
        }
        if (runLR) {
            accelerate = accelerate + speed;
            x -= accelerate;
        }
        for (int i = 0; i < str.length(); i++) {
            blank.delete(0, blank.length);
            blank.append(str.charAt(i));
            letter.setRegion(alphabet.get(blank));
            letter.setColor(color);
            letter.setBounds(x, y, alphabet.get(blank).getRegionWidth() * size, alphabet.get(blank).getRegionHeight() * size);
            letter.draw(batch);
            x += alphabet.get(blank).getRegionWidth() * size;
        }
    }
}
