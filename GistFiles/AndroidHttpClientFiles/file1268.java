// By passing in layout XML here, we don't need to manually call `onCreateView()`.
@EFragment(R.layout.fragment_main)
public class MainFragment extends Fragment {

    // This is the minimum needed to bind a variable to widget in XML.
    // AndroidAnnotations will look for an id `R.id.jokeTextView`.
    // Or, you could manually specify the id by passing it in as an
    // argument to the annotation.
    @ViewById TextView jokeTextView;

    // Required no-args constructor.
    public MainFragment() {}

    // This annotation will auto-generate code to find a View with id of
    // `R.id.jokeButton` and set the onClickListener to run this method.
    @Click
    void jokeButtonClicked() {
        loadNewJoke();
    }

    // This annotation makes sure this method is ran in a background thread.
    @Background
    void loadNewJoke() {
        String joke = "N/A";
        String jokeJsonString = null;
        try {
            jokeJsonString = HttpUtils.get("http://api.icndb.com/jokes/random");
            JSONObject jokeJson = new JSONObject(jokeJsonString);
            if (jokeJson.getString("type").equals("success")) {
                joke = jokeJson.getJSONObject("value").getString("joke");
            }
        } catch (IOException | JSONException | URISyntaxException e) {
            e.printStackTrace();
        }
        setJokeText(joke);
    }

    // This annotation makes sure this method is ran on the main/UI thread.
    @UiThread
    void setJokeText(String joke) {
        jokeTextView.setText(Html.fromHtml(joke));
    }

}