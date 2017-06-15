public class DemoActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ConstraintLayout layout = new ConstraintLayout(this);
    setContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    View greenBox = new View(this);
    greenBox.setBackgroundColor(Color.rgb(139, 195, 74));
    layout.addView(greenBox);
    View yellowBox = new View(this);
    yellowBox.setBackgroundColor(Color.rgb(244, 196, 0));
    layout.addView(yellowBox);
    View purpleBox = new View(this);
    purpleBox.setBackgroundColor(Color.rgb(102, 39, 190));
    layout.addView(purpleBox);

    // See other files for actual layout
  }
}