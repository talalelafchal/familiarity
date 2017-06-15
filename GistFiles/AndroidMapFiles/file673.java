package com.example.mindxxxd.festivaling;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by Mindxxxd on 03.02.2017. ChecklistUsual
 */

public class Checklist extends Fragment {

    //Default File with the Contents saved in it.
    public final String DefaultSaveFile = "CheckList.clt";
    //Keywords for the ContentArrayList
    public final String DefaultMain = "[MAIN]"; // if you change this, make shure you will change the String.xml array
    public final String DefaultPersonally = "[PERSONALLY]";
    public final String DefaultNotThisTime = "[NOTTHISTIME]";
    public final String DefaultNever = "[NEVER]";
    //Container for the different Listpoints
    public TableLayout Main;
    public TableLayout Personally;
    public TableLayout NotThisTime;
    public TableLayout Never;

    public final int[] pos = {0,0}; //Array to handle the Checklist Table
    public final int columCount = 2; //Developer Handler
    public boolean newList = false; //Developer Handler true-> initialize a Blank list everytime when onResume() is called!
    public boolean newListBoolean = false; //needed to Reset the CheckedState of the Checkpoints
    //The Content holder
    //IMPORTANT: the "contingents" is an Array with 4 ArrayLists which represent 4 categorys of different contents
    protected ArrayList[] contingents;
    public final TableRow.LayoutParams DefaultRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f); //Default Parameter
    public final TableLayout.LayoutParams DefaultTableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT); //Default Parameter
    protected Activity activity; //MainActivity
    protected Resources res; //equals getResources()
    protected boolean initialize; //Handler to make sure onResume() does not add a new list
    protected Button apply; //Button to add personal stuff
    protected EditText ET; //Textfield with the personal stuff
    protected long lastAdd=0; //needed to handle the onTouch action for Button apply
    //The isChecked holder of ArrayList[] contingents
    //IMPORTANT: "checked" is like "contingents" an array of 4 ArrayLists which represent 4 categories of different contents
    protected ArrayList<Boolean>[] checked;
    protected Context context; //equals getContext()

    protected int count=0; //Total number of Checkboxes


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initialize=true;
        return inflater.inflate(R.layout.check_list, container, false);
    }


    public View findViewById(int id){
        //just a handler
        return activity.findViewById(id);
    }


    public boolean isHeader(String text){
        //checks if the "text" is a header of the List, needed to initialize the Checklist
        String[] array = res.getStringArray(R.array.headers);
        for (String el : array){
            if (text.equals(el)) return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void addPersonalText(String text){
        /*
         * Adds the personal stuff to the List and "contingents", introduces "false" to "checked"
         */
        contingents[1].add(text);
        checked[1].add(false);
        TableRow TER = new TableRow(getContext());
        CheckBox CED = new CheckBox(getContext());
        CED.setText(text);
        CED.setId(count);
        TER.addView(CED, DefaultRowParams);
        Personally.addView(TER, DefaultTableParams);
        Personally.requestLayout();
        count++;
    }

    @Override
    public void onResume() {
        super.onResume();
        // This Method introduces some stuff and builds the Checklist programmatically

        //Introduce global variables
        activity = getActivity();
        res = getResources();
        context = getContext();
        apply = (Button) findViewById(R.id.applyPersonally);
        Main = (TableLayout) findViewById(R.id.ChecklistMain);
        Personally = (TableLayout) findViewById(R.id.ChecklistPersonally);
        NotThisTime = (TableLayout) findViewById(R.id.ChecklistNotThisTime);
        Never = (TableLayout) findViewById(R.id.ChecklistNever);
        //Introduces blank Content handler "contingents" and "checked"
        contingents = newArrayList(4);
        checked = newArrayListBoolean(4);
        //Reads the Contents and CklickedStates from an external file, if there is no File, it introduces a new one from the resource strings.xml
        readFromFile();

        if (newListBoolean) checked = introduceNewListBoolean(); //the handler to UNCHECK everythink if called!
        //Sets OnTouchListener and OnKeyListener for the personal stuff
        apply.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId()==R.id.applyPersonally){
                    if ((System.currentTimeMillis()-lastAdd)>100){
                        lastAdd=System.currentTimeMillis();
                        addPersonalText(((EditText)findViewById(R.id.newPersonally)).getText().toString());
                    }
                }
                return false;
            }
        });
        ET = (EditText) findViewById(R.id.newPersonally);
        ET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (v.getId()==R.id.newPersonally){
                    if (keyCode==KeyEvent.KEYCODE_NUMPAD_ENTER){
                        if ((System.currentTimeMillis()-lastAdd)>100) {
                            lastAdd = System.currentTimeMillis();
                            addPersonalText(((EditText) v).getText().toString());
                        }
                    }
                }
                return false;
            }
        });

        //This builds the Checklist from "contingents" and "checked"
        if (initialize) {
            initialize=false;
            for (int i = 0; i < contingents.length; i++) { //calls the main categorys of "contingents", horizontally. See: introduceNewArrayList()
                pos[0] = 0;
                pos[1] = 0;
                TableLayout TL;
                TableRow TR = new TableRow(getContext());
                switch (i) {
                    case 0:
                        TL = Main;
                        break;
                    case 1:
                        TL = Personally;
                        break;
                    case 2:
                        TL = NotThisTime;
                        break;
                    case 3:
                        TL = Never;
                        break;
                    default:
                        throw new NullPointerException("No or Wrong Table Given");
                }
                //If there is nothing in the category it hides the category
                if (contingents[i].size()<=1){
                    if (i!=1) TL.setVisibility(View.INVISIBLE);
                }
                //works through the "contingents" vertically
                for (int n = 1; n < contingents[i].size(); n++) {
                    String now = (String) contingents[i].get(n);
                    boolean header=isHeader(now); //checks if the row is a "header" according to strings.xml
                    if (header){ //if it is a header, set a TextView and reset the Table position saved in global int[] "pos"
                        //set TextView
                        TL.addView(TR, DefaultTableParams);
                        TextView TV = new TextView(getContext());
                        TV.setText(now);
                        TV.setId(count);
                        TV.setTextSize(30);
                        TableLayout.LayoutParams P = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        P.setMargins(0,15,10,0);
                        //start new Table
                        TR = new TableRow(getContext());
                        TL.addView(TV, P);
                        //reset Table position
                        pos[0]=0;
                        pos[1]=0;
                        //increases "count" for the reading method
                        count++;
                    } else { //if "now" is no header introduces a new Checkbox
                        //new Checkbox
                        CheckBox CB = new CheckBox(getContext());
                        CB.setText(now);
                        CB.setId(count);
                        CB.setChecked(checked[i].get(n));
                        CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                Integer[] pos = getPosition(contingents, buttonView.getText().toString());
                                checked[pos[0]].remove((int) pos[1]);
                                checked[pos[0]].add(pos[1], isChecked);
                            }
                        });
                        CB.setTextSize(15);
                        //adds the Checkbox to a TableRow
                        TR.addView(CB, DefaultRowParams);
                        pos[1]++;
                        if (pos[1] == columCount) { //Calculating all the Cell Orden, left to Right, Head to end, and if needed creating a new TableRow
                            pos[0]++;
                            pos[1] = 0;
                            //starts a new Row if the actual one is full
                            TL.addView(TR, DefaultTableParams);
                            TR = new TableRow(getContext());
                        }
                        count++;
                    }
                }
                //adds the last TableRow to the Table even if this one is not full
                TL.addView(TR, DefaultTableParams);
                TL.requestLayout();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //makes sure that on every onPause() the Content will be saved
        writeToFile();
    }

    public Integer[] getPosition(ArrayList[] list, String text){
        //returns the position of "text" in a given ArrayList[]-array, if the "list" does not contain "text" returns [1]=-1
        for (int i = 0; i<list.length; i++){
            for (int n = 0; n<list[i].size(); n++){
                if (text.equals(list[i].get(n))) {
                    return new Integer[]{i,n};
                }
            }
        }
        return new Integer[]{-1};
    }

    protected void writeToFile() {
        //writes the content to the "DefaultSaveFile"
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(DefaultSaveFile, Context.MODE_PRIVATE)));
            String line;
            for (int i = 0; i<contingents.length; i++){
                for (int n = 0; n<contingents[i].size(); n++){
                    //constructs the Line before adding it to Writer
                    line=((contingents[i].get(n))+"["+checked[i].get(n)+"]");
                    //the Line looks like this: Thing[boolean] example: Pillow[true]
                    out.append(line);
                    out.newLine();
                }
            }
            out.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    @SuppressWarnings("unchecked")
    protected void readFromFile() {
        //reads the "DefaultSaveFile" and puts it to the "contingents" and "checked", if there is no File yet, introduces new List from strings.xml
        if (newList) {
           introduceNewLists();
        } else {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(context.openFileInput(DefaultSaveFile)));
                String line = "anything"; //just to be nonNull at the first WHILE-check
                Boolean isChecked;
                int category=-1;//Because there will add "DefaultMain" one to 0!
                while (line!=null){
                    line=in.readLine();
                    if (line!=null){
                        String nl = line.substring(0, line.indexOf("]")+1); //IF there is a category header (wich looks like [MAIN]) it will be extracted
                        switch (nl){
                            case DefaultMain:
                            case DefaultPersonally:
                            case DefaultNotThisTime:
                            case DefaultNever:
                                category++;
                                break;
                            default: break;
                        }
                        String s = line.substring(line.indexOf("[", 2)+1, line.length()-1); //extracts the boolean
                        isChecked = Boolean.parseBoolean(s); //parses the boolean
                        /*
                         * line.substring(0, line.indexOf("[",2)) extracts the things name even if it is a category header
                         */
                        ((ArrayList<String>)contingents[category]).add(line.substring(0, line.indexOf("[",2))); //adds the things Name
                        this.checked[category].add(isChecked); //adds the boolean
                    }
                }
                in.close();
            }
            catch (FileNotFoundException e) {
                //if there is no File yet, it introduces a new list according to strings.xml
                introduceNewLists();
            } catch (IOException e) {
                Log.e("Checklist Fragment", "Can not read file: " + e.toString());
            }
        }
    }

    public void introduceNewLists(){
        contingents = introduceNewListString();
        checked = introduceNewListBoolean();
    }

    public ArrayList<Boolean>[] introduceNewListBoolean(){
        //adds "false" to "checked" for every element in "contingents"
        ArrayList<Boolean>[] result = newArrayListBoolean(4);
        for (int i = 0; i<contingents.length; i++) {
            for (int n = 0; n<contingents[i].size(); n++){
                result[i].add(false);
            }

        }
        return result;
    }

    public ArrayList<String>[] introduceNewListString(){
        //introduces the default list according to strings.xml
        ArrayList<String>[] result = newArrayList(4);
        String[] main = res.getStringArray(R.array.MainCheck);
        //here schould come the Language check!
        for (String e : main) {
            result[0].add(e);
        }
        //0=Main, 1=Personally, 2=NotThisTime, 3=Never;
        //First String of every Colum is the Header;
        result[1].add(DefaultPersonally);
        result[2].add(DefaultNotThisTime);
        result[3].add(DefaultNever);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<String>[] newArrayList(int count){
        //creates BLANK Array
        ArrayList<String>[] result = (ArrayList<String>[]) new ArrayList[count];
        for (int i=0; i<count; i++){
            result[i] = new ArrayList<>();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Boolean>[] newArrayListBoolean(int count){
        //creates BLANK Array
        ArrayList<Boolean>[] result = (ArrayList<Boolean>[]) new ArrayList[count];
        for (int i=0; i<count; i++){
            result[i] = new ArrayList<>();
        }
        return result;
    }


}
