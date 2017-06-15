//This class will be used to create a custom ArrayAdapter and to bind the objects with the ListView

public class Weather {
    public int icon;        //Here will go the resource id of the image we want to display
    public String title;    //String for the text we want to display
    
    //Constructors
    public Weather(){
        super();
    }
    
    public Weather(int icon, String title) {
        super();
        this.icon = icon;
        this.title = title;
    }
}