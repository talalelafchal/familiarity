    // タップされた場合 Toastを表示
    if ( Gdx.input.justTouched()) {
        // Toast表示※Android側の機能を利用
        handler_.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Touched! (" + count + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }