public class MainActivity extends Activity{
 
        EditText userInput;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        userInput = (EditText) findViewById(R.id.editText1);
        userInput.getText().toString(); // to pass it as a String in order to pass as an argument to summonerTask
 
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FetchSummonerTask summonerTask = new FetchSummonerTask();
                summonerTask.execute(userInput);
            }
        });
}
 
public class FetchSummonerTask extends AsyncTask<String, Void, Summoner> {
 
        @Override
        protected Summoner doInBackground(String... params) {
 
            try {
                Summoner summoner = ((GlobalClass)getActivity().getApplication()).getApi()
                        .getSummonerByName(params[0]) // was unclear as to why the params[0] is passed here
                        .get(params[0].replaceAll("\\s+", "").toLowerCase()); // also what is the ("\\s", "") used for? formatting?
 
                /*
                params[0] is the string of the summoner name. As you can see, the doInBackground method is taking in multiple
                String parameters, but you are only passing in one, which is the summoner name, so to access this name, you
                use the first index which is 0.
 
                replaceAll("\\s+", "") takes out all the spaces in the summoner name by replacing them with the empty string "".
                The summoner name is also made into all lower case letters with the toLowerCase method. The reason these two things
                are done is because when the library makes a map of summoner names and summoner objects, the "keys" are the
                corresponding summoner name in all lower case with no spaces.
 
                The lines above can be made more readable by splitting it into multiple lines:
 
                Map<String, Summoner> summoners = ((GlobalClass)getActivity().getApplication()).getApi()
                        .getSummonersByName(params);
               
                Summoner summoner = summoners.get(params[0].replaceAll("\\s+", "").toLowerCase());
                */
                if(summoner != null) {
                    return summoner;
                }
            } catch (RiotApiException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        @Override
        protected void onPostExecute(Summoner result) {
            super.onPostExecute(result);
            if(result != null) {
                TextView dis = (TextView) findViewById(R.id.textView1);
                dis.setText(String.valueOf((result.getSummonerLevel())));
                /* notice here we are setting the textview to the retrieved summoners level*/
            }
 
        /*
        After the doInBackground method is called, and fetches the Summoner object for the corresponding summoner name, this method
        is run (it takes in the result as a parameter). Here, you can set TextViews, ImageViews, etc, using all the Summoner
        object's information.
       
        The Summoner object contains information such as summoner level (getSummonerLevel()), summoner name (getSummonerName()),
        and the summoners profile icon id (getProfileIconId()) which can be used to retrieve the corresponding icon image.
        */
        }
    }
}
 
/*
For ranked stats, you will need to make another API call, similar to the way you did in the doInBackgroundMethod to get the Summoner object.
 
Note every time you want to make an api call, you will need to do it off the main thread by utilizing AsyncTask or other methods.
 
Objects like the Summoner object can be passed as Intents if you want to use them in another Activity.
*/