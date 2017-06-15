package com.example.androidautocompletetextview;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements TextWatcher {

    //text area where user will type
    MultiAutoCompleteTextView myAutoComplete;

    //** toms stuff
    private static HashMap<String, HashMap> outerMap;
    private static HashMap<String, Integer> innerMap;
    private static BufferedReader reader2;
    private static String[] tokens;
    ArrayList<String> token = new ArrayList<String>();

    //button objects
    Button buttonOK;
    Button buttonTom1;
    Button buttonTom2;
    TextView matches;
    Button dynamicSuggestion1;
    Button dynamicSuggestion2;
    Button dynamicSuggestion3;

    //dictionary list
    List<String> myList;
    //array adapter for use with predictive dropdown list
    ArrayAdapter<String> myAutoCompleteAdapter;
    //list view object
    ListView listView ;
    //holds list view items
    ArrayList<String> list = new ArrayList<String>();
    //adapter for use with list view
    ArrayAdapter<String> adapter;

    //treemap used to keep track of word popularity
    private static TreeMap<String, Integer> dictionaryMap = new TreeMap<String, Integer>();
    //treemap for use with suggestion words to be returned to user as user types
    private static TreeMap<String, Integer> filteredWords = new TreeMap<String, Integer>();
    //used in filtering calculation
    public static List<String> tasks;

    //when app launches, this method will be called
    public void onCreate(Bundle savedInstanceState)
    {
        //calls any saved state (not currently in use by this app)
        super.onCreate(savedInstanceState);


        //loads current activity in apps content viewer
        setContentView(R.layout.activity_main);

        //find autocomplete textview object from xml
        myAutoComplete = (MultiAutoCompleteTextView) findViewById(R.id.myautocomplete);



        //call method to load words into dictionary
        loadDictionary();
        //used so we can detect any changes in text area status
        myAutoComplete.addTextChangedListener(this);

        //new adapter objectfor use with autoCompleteTextView
        myAutoCompleteAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, myList);
        //tokenizes the user words so suggestions can be loaded for each word typed, instead of only first word
        myAutoComplete.setTokenizer(new SpaceTokenizer());
        //sets autoCompleteTextView adapter
        myAutoComplete.setAdapter(myAutoCompleteAdapter);


        //set up button objects and ability to detect button presses
        buttonOK = (Button) findViewById(R.id.ok);
        buttonOK.setOnClickListener(OkOnClickListener);
        matches = (TextView)findViewById(R.id.matched);

        dynamicSuggestion1 = (Button) findViewById(R.id.dynamicSuggestion1);
        dynamicSuggestion1.setOnClickListener(dynamicSuggestion1Listener);
        dynamicSuggestion2 = (Button) findViewById(R.id.dynamicSuggestion2);
        dynamicSuggestion2.setOnClickListener(dynamicSuggestion2Listener);
        //dynamicSuggestion3 = (Button) findViewById(R.id.dynamicSuggestion3);
        //dynamicSuggestion3.setOnClickListener(dynamicSuggestion3Listener);
        fillList();
        filteredWords.clear();
        outerMap = new HashMap<String,HashMap>();
        try
        {
            getSource();

        } catch (IOException e)
        {}

        createWordPairs();
    }

    //ok button listener
    OnClickListener OkOnClickListener = new OnClickListener()
    {
        //when the button is clicked
		public void onClick(View arg0)
        {
            //user typed input is assigned to string variable 'input'
            String input = myAutoComplete.getText().toString();
            //user inputted words is split into individual words
            String[] tokenizeInput = input.split("[,\\s]+");
            //steps through list of tokenized words
            for (int i = 0; i < tokenizeInput.length; i++)
            {
                try
                {
                    //send current word to 'suggestion' method
                    suggestion(tokenizeInput[i].toLowerCase());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //if dictionary does not contain current word
                if (!myList.contains(tokenizeInput[i].toLowerCase()))
                   //add word to dictionary
                    myList.add(tokenizeInput[i].toLowerCase());
            }
            //add typed sentence/word to list for displaying in listView
            list.add(input);
            //set autocomplete dropdown menu type
            myAutoCompleteAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, myList);
            //set custome tokenizer for autocomplete suggestions
            myAutoComplete.setTokenizer(new SpaceTokenizer());
            //set autocomplete adapter
            myAutoComplete.setAdapter(myAutoCompleteAdapter);
            //clears current text from user input area
            myAutoComplete.setText("");
            //fill on screen list
            fillList();
            dynamicSuggestion1.setText("");
            dynamicSuggestion2.setText("");
            //dynamicSuggestion3.setText("");
            //tokens.add(myAutoComplete.getText());
            try {
                updateTomsWords(input);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "save error >", Toast.LENGTH_SHORT).show();
            }
            try
            {
                getSource();

            } catch (IOException e)
            {}
            createWordPairs();
        }
    };

    public void updateTomsWords(String current) throws IOException
    {


        try {
            File root = Environment.getExternalStorageDirectory();
            File fileCheck = new File(root, "/test.txt");

                Toast.makeText(MainActivity.this, "File Exists Already >", Toast.LENGTH_SHORT).show();
                File textFile = new File(root, "/test.txt");
                FileWriter textWriter = new FileWriter(textFile);
                BufferedWriter out = new BufferedWriter(textWriter);
                ArrayList<String> saveWords = null; // whatever
                for (int i = 0; i < tokens.length; i++)
                {
                    out.write(tokens[i]+" ");
                }
                out.write(current);
                out.close();
        } catch (IOException e) {
            Log.v(getString(R.string.app_name), e.getMessage());
        }
        getSource();
    }

    //button listener for dynamic suggestion 1
    OnClickListener dynamicSuggestion1Listener = new OnClickListener()
    {
        public void onClick(View arg0)
        {
            CharSequence spaceTest = myAutoComplete.getText();
            String[] words = (myAutoComplete.getText().toString()).split(" ");
            StringBuffer result = new StringBuffer();
            char space = ' ';
            if(spaceTest.charAt(spaceTest.length() - 1) == space)
            {
                myAutoComplete.setText(myAutoComplete.getText()+""+dynamicSuggestion1.getText()+" ");
            }
            else {

                for (int i = 0; i < words.length - 1; i++) {
                    result.append(words[i]);
                    result.append(" ");
                }

                String newSentence = result.toString();
                myAutoComplete.setText(newSentence + dynamicSuggestion1.getText() + " ");
            }
            //myAutoComplete.setText(newSentence+word(1)+" ");
            //myAutoComplete.append(" "+word(1)+ " ");
            myAutoComplete.setSelection(myAutoComplete.getText().toString().length());
        }
    };
    //button listener for dynamic suggestion 2
    OnClickListener dynamicSuggestion2Listener = new OnClickListener()
    {
        public void onClick(View arg0)
        {
            CharSequence spaceTest = myAutoComplete.getText();
            String[] words = (myAutoComplete.getText().toString()).split(" ");
            StringBuffer result = new StringBuffer();
            char space = ' ';
            if(spaceTest.charAt(spaceTest.length() - 1) == space)
            {
                myAutoComplete.setText(myAutoComplete.getText()+""+dynamicSuggestion2.getText()+" ");
            }
            else {

                for (int i = 0; i < words.length - 1; i++) {
                    result.append(words[i]);
                    result.append(" ");
                }

                String newSentence = result.toString();
                myAutoComplete.setText(newSentence + dynamicSuggestion2.getText() + " ");
            }
            //myAutoComplete.setText(newSentence+word(1)+" ");
            //myAutoComplete.append(" "+word(1)+ " ");
            myAutoComplete.setSelection(myAutoComplete.getText().toString().length());
        }
    };
    //button listener for dynamic suggestion 3
    OnClickListener dynamicSuggestion3Listener = new OnClickListener()
    {
        public void onClick(View arg0)
        {

            CharSequence spaceTest = myAutoComplete.getText();
            String[] words = (myAutoComplete.getText().toString()).split(" ");
            StringBuffer result = new StringBuffer();
            char space = ' ';
            if(spaceTest.charAt(spaceTest.length() - 1) == space)
            {
                myAutoComplete.setText(myAutoComplete.getText()+""+dynamicSuggestion3.getText()+" ");
            }
            else {

                for (int i = 0; i < words.length - 1; i++) {
                    result.append(words[i]);
                    result.append(" ");
                }

                String newSentence = result.toString();
                myAutoComplete.setText(newSentence + dynamicSuggestion3.getText() + " ");
            }
            //myAutoComplete.setText(newSentence+word(1)+" ");
            //myAutoComplete.append(" "+word(1)+ " ");
            myAutoComplete.setSelection(myAutoComplete.getText().toString().length());
        }
    };
    //fills list to contain user input history
    private void fillList()
    {
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        //puts dictionary words into array
        //String [] temp = (String[]) dictionaryMap.keySet().toArray(new String[dictionaryMap.size()]);

        //adds array into array adapter for filling list
        //when an item is added to the array, it is added to the listView
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, list);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_SHORT)
                        .show();

            }

        });

    }

    //load slang words text file into arraylist in app
    private void loadSlang()
    {
        myList = new ArrayList<String>();
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("slang.txt")));
            String word = reader.readLine();
            for(int i = 0; i < 100000000; i++)
            {
                if(word == null)
                    break;
                else
                    myList.add(word);
                word = reader.readLine();

            }
            Toast.makeText(MainActivity.this, "Slang Dictionary Loaded!", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            System.out.println("Got an IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //load curse words text file into arraylist in app
    private void loadBadWords()
    {
        myList = new ArrayList<String>();
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("badwords.txt")));
            String word = reader.readLine();
            for(int i = 0; i < 100000000; i++)
            {
                if(word == null)
                    break;
                else
                    myList.add(word);
                word = reader.readLine();

            }
            Toast.makeText(MainActivity.this, "Bad Word Dictionary Loaded!", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            System.out.println("Got an IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //load dictionary text file into arraylist in app
    private void loadDictionary()
    {
        //new arrayList initializer
        myList = new ArrayList<String>();

        try
        {
            //new treemap initialiser to keep track of popularity of words
            dictionaryMap = new TreeMap<String, Integer>();

            //read in dictionary.txt file
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("dictionary.txt")));
            //assigns first word to string variable 'word'
            String word = reader.readLine();
            //iterate through dictionary file
            for(int i = 0; i < 100000000; i++)
            {
                //if reached the last word
                if(word == null)
                    //jump out of the for loop
                    break;
                else
                {
                   //add the current word to the dictionary ArrayList
                    myList.add(word.toLowerCase());
                    dictionaryMap.put(word.toLowerCase(), 1);
                }
                //select next word in txt file
                word = reader.readLine();
            }
            //tell user the dictionary file has been loaded (debugging)
            Toast.makeText(MainActivity.this, "Dictionary Words Loaded!", Toast.LENGTH_SHORT).show();

        }
        catch (IOException e)
        {
            //if an error is encountered
            System.out.println("Got an IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //takes user inputted words and if looks if they are in the pre-loaded dictionary.
    // If so, the value for that word is incremented by 1.If not they are added.
    private void suggestion(String user) throws IOException
    {
        //if word is in dictionary (case insensitive)
        if (dictionaryMap.containsKey(user))
        {
            //increment the value of the word
            dictionaryMap.put(user, dictionaryMap.get(user)+1);
            //tell user the word is in the dictionary
            //Toast.makeText(MainActivity.this, "\""+user+"\" is in Dictionary -> ("+dictionaryMap.get(user)+")", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //tell user the word is not in the dictionary and has been added
            //Toast.makeText(MainActivity.this, "\""+user+"\" wasn't in Dictionary.I now have Added it!", Toast.LENGTH_SHORT).show();
            //increment value for that word
            dictionaryMap.put(user, 1);
            //add word to ordinary arrayList dictionary
            myList.add(user);
            //add word to arraylist 'list' for use with listView of past typed words
        }
    }


    //Got this custom class from http://stackoverflow.com/questions/4209339/how-to-replace-multiautocompletetextview-drop-down-list
    //this helps to tokenize words from user by space instead of multiAutoCompleteTextView default tokenizer which is comma
    public class SpaceTokenizer implements MultiAutoCompleteTextView.Tokenizer {

        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;

            while (i > 0 && text.charAt(i - 1) != ' ') {
                i--;
            }
            while (i < cursor && text.charAt(i) == ' ') {
                i++;
            }

            return i;
        }

        public int findTokenEnd(CharSequence text, int cursor) {
            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (text.charAt(i) == ' ') {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        public CharSequence terminateToken(CharSequence text) {
            int i = text.length();

            while (i > 0 && text.charAt(i - 1) == ' ') {
                i--;
            }

            if (i > 0 && text.charAt(i - 1) == ' ') {
                return text;
            } else {
                if (text instanceof Spanned) {
                    SpannableString sp = new SpannableString(text + " ");
                    TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                            Object.class, sp, 0);
                    return sp;
                } else {
                    return text + " ";
                }
            }
        }
    }

    //
    private String word(int choice)
    {
        //list to track suggested words
        Map.Entry<String,Integer> maxEntry = null;
        Map.Entry<String,Integer> maxEntry2 = null;

        if(filteredWords.size() != 0)
        {
            if(filteredWords.size() > 0)
            {
                for(Map.Entry<String,Integer> entry : filteredWords.entrySet()) {
                    if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                        maxEntry = entry;
                    }
                    if (maxEntry2 == null || entry.getValue() > maxEntry2.getValue() || entry.getValue() <= maxEntry.getValue())
                    {
                        maxEntry2 = entry;
                    }

                }
                if(choice == 1 && myAutoComplete.getText().toString() != "")
                {
                    return maxEntry.getKey();
                }
                if(maxEntry2.getValue() > 0 && choice == 2 && myAutoComplete.getText().toString() != "")
                {
                    return maxEntry2.getKey();
                }


            }
        }
        return "-";
    }
    //match typed words from user input to dictionary words
    //if letters typed by users matches any word in the dictionary it is loaded into
    //a secondary tree map for use with suggesting words to user based on these letters
    //treemap is erased every time this method is called to ensure
    //words in the treemap are always based on the current text in the user input area
    public void filterDictionary()
    {
        //gets input typed by user
        String userTyped = myAutoComplete.getText().toString();
        //splits up the user input into seperate words. In case user types a full sentence.
        String[] sentence = userTyped.split("[,\\s]+");
        //sets current word as word to be used for suggestion
        String input = sentence[sentence.length-1];

        //clears the map of filtered words
        filteredWords.clear();

        //matches user input to entries in the dictionary
        //for loop will iterate through the whole dictionary
        for (Map.Entry<String, Integer> entry : dictionaryMap.entrySet())
        {
            //if current selected entry in dictionaryMap contains the current word
            //we only want to match the most recent typed word to entries in the dictionaryMap
            if(entry.getKey().contains(input.toLowerCase()))
            {
                //get current key and value, and add the to arrayList "filteredWords"
                filteredWords.put(entry.getKey(), entry.getValue());
            }
        }
    }
    public void detectSpace()
    {

        CharSequence spaceTest = myAutoComplete.getText();
        String[] words = (myAutoComplete.getText().toString()).split(" ");
        char space = ' ';
        if(spaceTest.length() > 1 && spaceTest.charAt(spaceTest.length() - 1)== space && words.length >= 2)
        {
            dynamicSuggestion1.setText(maxValue(previousTwoWords(), 1));
            dynamicSuggestion2.setText(maxValue(previousTwoWords(), 2));
            filteredWords.clear();
        }
    }
    public void onStop()
    {
        super.onStop();
        list.clear();
        filteredWords.clear();
        fillList();
    }
    public void onResume()
    {
        super.onResume();
        dynamicSuggestion1.setText("");
        dynamicSuggestion2.setText("");
        //dynamicSuggestion3.setText("");
    }
    //**toms code
    public void getSource1() throws IOException
    {
        //old code i used to read txt file from apps internal directory into the app
        try
        {
            reader2 = new BufferedReader(new InputStreamReader(getAssets().open("test.txt")));
            Toast.makeText(MainActivity.this, "Toms Words Loaded -> ", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            System.out.println("fileread prob");
        }

        String line = null;
        StringBuilder sInput = new StringBuilder();
        while ((line = reader2.readLine()) != null)
        {
            sInput.append(line);
        }

        String output = sInput.toString();
        //Get rid of all non-alpha characters (except spaces)
        String alphaString = output.replaceAll("[^a-zA-Z ]", "");

        //Split the text into words
        tokens = alphaString.split(" ");
        Toast.makeText(MainActivity.this, "Strings made!", Toast.LENGTH_SHORT).show();

    }

    public void getSource() throws IOException
    {
        try{
            File f = new File(Environment.getExternalStorageDirectory()+"/test.txt");
            FileInputStream fileIS = new FileInputStream(f);
            BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
            String readString = new String();
            StringBuilder sInput = new StringBuilder();

            while ((readString = buf.readLine()) != null) {

                sInput.append(readString);
            }
            String output = sInput.toString();
            //Get rid of all non-alpha characters (except spaces)
            String alphaString = output.replaceAll("[^a-zA-Z ]", "");

            //Split the text into words
            tokens = alphaString.split(" ");

            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            Toast.makeText(MainActivity.this, "Word Pairs > "+outerMap.toString(), Toast.LENGTH_SHORT).show();
    }

    //**toms code
    public void createWordPairs()
    {
        for (int i = 0; i <= tokens.length - 3; i++)
        {
            //create wordpairs
            String wordPair = tokens[i] + tokens[i + 1];
            //create thirdWord
            String thirdWord = tokens[i + 2];
            addElement(wordPair, thirdWord);
        }
        //Toast.makeText(MainActivity.this, "Finished Word Pairs", Toast.LENGTH_SHORT).show();
    }
    //**toms code
    public void addElement(String key1, String key2)
    {
        HashMap innerMap = outerMap.get(key1);
        if (innerMap == null) {
            innerMap = new HashMap<String, Integer>();
            innerMap.put(key2, 1);
            outerMap.put(key1, innerMap);
            //innerMap.put(key2,1);
        }
        else if (innerMap.containsKey(key2)) {

            int temp = (Integer) innerMap.get(key2);
            temp++;
            innerMap.put(key2, temp);
        }
        else innerMap.put(key2, 1);
    }
    //**toms code
    public Object getElement(String key1, String key2)
    {
        HashMap innerMap = outerMap.get(key1);
        if (innerMap == null) {
            return null;
        }
        return innerMap.get(key2);
    }
    //**toms code
    public void displayKeys()
    {

        System.out.println("\nFor-each method");
        for (Object key : outerMap.keySet()) {
            System.out.println("\t" + key);
            HashMap<String, Integer> innerMap = (HashMap<String, Integer>) outerMap.get(key);

            for (Object innerkey : innerMap.keySet())
            {
                System.out.println("\t\tinner loop value " + innerkey + " ," + innerMap.get(innerkey));
            }
        }
    }
    //**toms code
    public void outerMapSize()
    {
        Toast.makeText(MainActivity.this, "Outer Map is this size -> "+outerMap.size(), Toast.LENGTH_SHORT).show();
    }

    public String lastWord()
    {
        String[] words = (myAutoComplete.getText().toString()).split(" ");
        String last = words[words.length-1];
        return last;
    }
    public String previousTwoWords()
    {
        String[] words = (myAutoComplete.getText().toString()).split(" ");

        String lastTwo = words[words.length-2]+words[words.length-1];
        return lastTwo;

    }

    public String maxValue(String wordKey, int order){

        Map.Entry<String,Integer> maxEntry = null;
        Map.Entry<String,Integer> maxEntry2 = null;
        Map.Entry<String,Integer> maxEntry3 = null;
        if(outerMap.get(wordKey) != null) {
            HashMap<String, Integer> innerMap = (HashMap<String, Integer>) outerMap.get(wordKey);
            TreeMap<String, Integer> treeMap = new TreeMap<String, Integer>();
            treeMap.putAll(innerMap);

            String result = null;

            for (Map.Entry<String, Integer> entry : innerMap.entrySet())
            {
                if (maxEntry == null || entry.getValue() > maxEntry.getValue())
                {
                    maxEntry = entry;
                }
                if (maxEntry2 == null || entry.getValue() > maxEntry2.getValue() || entry.getValue() <= maxEntry.getValue() || !maxEntry2.getKey().contains(maxEntry.getKey()))
                {
                    maxEntry2 = entry;
                }
            }

            if (order == 1) {

                result = maxEntry.getKey();
                Toast.makeText(MainActivity.this, maxEntry.getKey()+" first > "+maxEntry.getValue(), Toast.LENGTH_SHORT).show();
            }
            if (order == 2 ) {

                if(!maxEntry2.getKey().contains(maxEntry.getKey()))
                    result = maxEntry2.getKey();
                else
                    result = "";
                Toast.makeText(MainActivity.this, maxEntry2.getKey()+" second > "+maxEntry2.getValue(), Toast.LENGTH_SHORT).show();
            }
            return result;
        }
        return "no match";

    }
    public void afterTextChanged(Editable s)
    {
        String userTyped = myAutoComplete.getText().toString();
        if(userTyped == "" || userTyped.length() == 0)
        {
            dynamicSuggestion1.setText("");
            dynamicSuggestion2.setText("");
            filteredWords.clear();
            matches.setText(filteredWords.size()+ " total matched words");
        }
    }
    //called before a user types
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        filterDictionary();
        dynamicSuggestion1.setText(word(1));
        dynamicSuggestion2.setText(word(2));
        detectSpace();//if space is pressed
        //dynamicSuggestion3.setText(filteredWords.size()+ " total filtered words");
        matches.setText(filteredWords.size()+ " total matched words");
    }
}
