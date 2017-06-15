class MyGdxApp implements ApplicationListener  {
    // 画面の論理座標
    static private final float WIDTH  =320;
    static private final float HEIGHT  =640;

    // カウンター(renderが呼ばれるたびにカウントアップ）
    int count;

    // Gdx用変数
    OrthographicCamera camera;
    SpriteBatch batch;
    ShapeRenderer renderer;
    Texture texture;
    Sprite sprite;
    BitmapFont font;


    @Override
    public void create() {
        // 画面の実際のサイズ(pixel)
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.app.log("", "size w:" + w + " h:" + h);

        // カメラ設定　
        // ※画面のサイズにあわせ縦横比が正しい状態で最大限のサイズで設定
        float scrRatio =  w/h;              // 実際の画面の縦横比
        float logicalRatio =  WIDTH /HEIGHT;    // 論理座標の縦横比
        camera = new OrthographicCamera();
        if ( scrRatio>logicalRatio) {
            // 実際の画面のほうが論理座標より横長
            camera.setToOrtho(false, WIDTH/logicalRatio*scrRatio, HEIGHT);
            camera.translate(new Vector2( - (WIDTH/logicalRatio*scrRatio - WIDTH)/2,0));
            camera.update();
        }else{
            // 実際の画面のほうが論理座標より縦長
            camera.setToOrtho(false, WIDTH, HEIGHT*logicalRatio/scrRatio  );
            camera.translate(new Vector2(0,  -(HEIGHT*logicalRatio/scrRatio - HEIGHT)/2));
            camera.update();
        }

        // スプライトバッチ初期化
        batch = new SpriteBatch();
        // レンダラー初期化
        renderer = new ShapeRenderer();

        // フォント初期化
        font = new BitmapFont();

        // テクスチャーをロードしスプライトにセットする
        texture = new Texture(Gdx.files.internal("libgdx-logo.png"));   // 200x250pixel
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture, 0, 0, 200, 250);

        sprite = new Sprite(region);
        sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);                // スプライトの中心を原点に
        sprite.setPosition(WIDTH/2-sprite.getWidth()/2, HEIGHT/2-sprite.getHeight()/2); // 画面中央に配置
    }

    @Override
    public void dispose() {
        texture.dispose();
        font.dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void render() {
        count++;
        // 赤で塗りつぶす
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 描画
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        sprite.setRotation(count);
        sprite.draw(batch);
        font.setColor(Color.WHITE);
        font.draw(batch, "for Android only!", 0, HEIGHT);
        batch.end();

        // 画面の枠を描画（論理座標 WIDTH,HEIGHTの範囲）
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeType.Line);
        renderer.setColor(1,1,1f,1);
        renderer.rect(0, 0,WIDTH,HEIGHT);
        renderer.end();


    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void resume() {
    }
}