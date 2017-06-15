public final class MyFonts extends Font {
    public static final String HelveticaRG = "fonts/HelveticaRg.ttf";
    public static final String HelveticaBLD = "helvetica-bold";

    @Override
    public void initialize() {
        add(HelveticaRG); //add directly with font path
        //or you can add with a custom name so if you are planning to use Font.setByTag(...) method
        //you can make your xml more readable like android:tag="helvetica-bold"
        add(HelveticaBLD,"fonts/HelveticaBld.ttf"); 
    }
}