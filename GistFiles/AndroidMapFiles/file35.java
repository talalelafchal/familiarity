/**
 * This is the game app Which First Display the Word  and Show user the five options
 * and user has to select Which is Correct 
 * 
 */

package com.savi.sampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimpleList;

public class MainActivity extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         
        //This is the Method that loads the Word on the data structure
        loadWords();
        //This Contains the code for the main game
        mainGame();
    }

    private void mainGame() {

        //Shuffle the wordlist so that we always have the Different five words
        Collections.shuffle(WordList);
        
        //Clear the fiveWords(Which Contains the fiveWords) and fiveDefns(This Contains the definition of those five words) if they
        //have any data from previous.
        fiveWords.clear();
        fiveDefns.clear();
        
        //This gets the five Words from the main Words list
        for(int i=0;i<5;i++){
            fiveWords.add(WordList.get(i));
        }
        
        //This get's the definitions of those five words so that can be displayed on to the screen
        for (String word:fiveWords){
            fiveDefns.add(Dictionary.get(word));
        }
        //Now We shuffle the definition so they are displayed in random order
        Collections.shuffle(fiveDefns);

        //Now get "TheWord" , whose definition the user will try to guess
        theWord=fiveWords.get(0);
        
        //" theWord"TextView is the view Where the Word is Displayed 
        findTextView(R.id.the_Word).setText(theWord);
        
        //ListView Shows the list of definitions
        ListView listView=findListView(R.id.definition);
        
        //This is like an adapter that helps to show the list of definitions on the screen
        SimpleList.with(this).setItems(listView,fiveDefns);
        
        //This listen to the click and calls the method "onItemClick"
        listView.setOnItemClickListener(this);


    }
     /*
     * This loads the word into the data structures
     * Word list Contains all the words
     * Dictionary Contains all the word and there Definitions
     */
    private void loadWords() {

        for(int i=0;i<WORDS.length;i+=2){
            
            WordList.add(WORDS[i]);
            Dictionary.put(WORDS[i],WORDS[i+1]);
        }
    }

    /**
     * This method listens to the event and Compare the user answer With the actual answer.
     * this takes the index that user clicked and find the definition from the (fiveDefns)
     * Which is the user answer and compare it to the actual answer by looking the (thsWord)
     * in Dictionary.then it compares the answer to the user and display the appropriate msg.
     * if the answer is wrong then it displayes the correct answer in the Answer textField. 
     * @param list
     * @param index
     */
    @Override
    public void onItemClick(ListView list, int index) {

        String UserAnswer= fiveDefns.get(index);
        String ActualAnswer=Dictionary.get(theWord);

        if(UserAnswer.equals(ActualAnswer)){
            toast("You are awesome",Toast.LENGTH_LONG);
        }
        else{
            toast("You are Worng ",Toast.LENGTH_LONG);
            TextView AnswerView=findTextView(R.id.Answer);
            AnswerView.setText(ActualAnswer);
        }

        mainGame();
    }

    //Initialization of the variables
    public static final String[] WORDS={
            "abate","to listen to subside",
            "abeyance","suspended  action",
            "abjure","promise or swear to give up",
            "abrogate","repeal or annul by authority",
            "abstruct","difficult to comprehend obscure",
            "acarpous","effect no longer fertile worn out",
            "accretion","the growing of seprate things into one",
            "agog","eager/excited",
            "alloy","to debase by mixing with something inferior",
            "amotize","end (a debt) by setting aside money"

    };

    private ArrayList<String>WordList=new ArrayList<String>();
    private Map<String,String>Dictionary=new HashMap<String, String>();
    private ArrayList<String>fiveWords=new ArrayList<String>();
    private ArrayList<String>fiveDefns=new ArrayList<String>();
    private String theWord="";
  //  private ArrayAdapter<String>adapter;
}
