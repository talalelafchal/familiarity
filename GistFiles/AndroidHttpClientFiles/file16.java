


 */
public class PopMovies {

    String Name;
    String URL;
    String idmovie;
    String viewmovie;
    String rating;
    String youtubecode;
    String seen="0";

    public PopMovies(String name, String URL) {

        Name = name;
        this.URL = URL;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getViewmovie() {
        return viewmovie;
    }

    public void setViewmovie(String viewmovie) {
        this.viewmovie = viewmovie;
    }

    public String getYoutubecode() {
        return youtubecode;
    }

    public void setYoutubecode(String youtubecode) {
        this.youtubecode = youtubecode;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }


    public String getIdmovie() {
        return idmovie;
    }

    public void setIdmovie(String idmovie) {
        this.idmovie = idmovie;
    }

    public String getName() {
        return Name;
    }

    public String getURL() {
        return URL;
    }


}
