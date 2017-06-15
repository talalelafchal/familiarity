Helpshift hs;

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    hs = new Helpshift (MyActivity.this);
}