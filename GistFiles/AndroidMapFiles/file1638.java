protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // setContetnViewでレイアウトをセットしておく
    setContentView(R.layout.activity_main);
    aQuery = new AQuery(this);
    // 色・文字サイズ・テキストの内容をTextViewセットする
    aQuery.id(R.id.activity_main_text).textColor(Color.RED).textSize(20).text("リス");
    // Buttonが押された時に実行するメソッド名を指定する
    aQuery.id(R.id.activity_main_button).clicked(this, "onButtonClicked");
}

public void onButtonClicked() {
    //ボタンが押された時にTextViewの内容が変更される
    aQuery.id(R.id.activity_main_text).text("押されたリス");
}