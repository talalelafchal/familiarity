/**
 * Created by Mac on 3/24/16.
 */
public class DisplaySongs4host {
    private String songName;

    public DisplaySongs4host(){
        this.songName="";

    }
    public DisplaySongs4host( String songName){
        this.songName= songName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}

