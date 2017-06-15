    // 画面の実際のサイズ(pixel)
    float w = Gdx.graphics.getWidth();
    float h = Gdx.graphics.getHeight();

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