// Put this line under onCreate method of your application class(MyApp)

TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/yo
public class MyApp extends Application{
  @Override
  public void onCreate(){
      TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/your_font_file.ttf");
  }
}
