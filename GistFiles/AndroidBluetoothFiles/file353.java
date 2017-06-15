package com.example.mathieu.obdiireader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;

import java.io.IOException;
im
port java.math.BigDecimal;
import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Mathieu on 02/06/2017.
 */

public class Values extends AppCompatActivity  implements View.OnClickListener{
    String[] choiceGraphType = new String[] {"Graph","Values","Graph + Values"};

    String[] choiceZone = new String[] {"Zone 1","Zone 2","Zone 3","Zone 4","All"};

    String stringHelp = "On this page you can display informations about your engine, to do so : \n\n" +
            "1.   Select the zone where you want your data to be displayed by clicking on one of the 4 zones available\n\n" +
            "2.   Select the category of information you want \n\n" +
            "3.   Select the information you want to acquire from your engine\n" +
            "4.   Select how you want your data to be displayed :\n" +
            "       -Graph creates a graph on which the data will be updated so you can follow the tendancies of the information\n" +
            "       -Values shows up the data on a simple text and updates it constantly\n" +
            "       -Graph + Values allows you to do both\n\n" +
            "You can modify your choices at any moment by clicking on the three dots in the Top Right corner of the screen";

    String[] strListCmds = new String[]{"Control","Engine","Fuel","Pressure","Sensors"};

    String[] strListControl = new String[]{"Distance MIL On","Distance Since CC","Monitor Status since DTC","Ratio Fuel/Air","Voltage of the Module","Vehicle Identification Number"};

    String[] strListEngine = new String[]{"Absolute Air Load","Air Load","Air flow rate","Oil temperature","Engine RPM","Engine speed","Runtime since Startup","Throttle position"};

    String[] strListFuel = new String[]{"Fuel/Air ratio","Fuel consumption rate","Fuel Type","Fuel level","Fuel trim","Oxygen 1 and Fuel mix"};

    String[] strListPressure = new String[]{"Barometric Pressure","Fuel Pressure","Fuel Rail Pressure","Intake manifold pressure"};

    String[] strListSensors = new String[]{"Accelerometer","Gyroscope","GPS"};

    String[] initCmds = new String[]{"AT E0","AT L0","AT SP 0"};

    String[][] strCmds = new String[][]{{"0121","0131","0101","0144","0142","0902"},        //two dimension table to stock the codes
            {"0143","0104","0110","015C","010C","010D","011F","0111"},
            {"0144","015E","0151","012F","0134"},
            {"0133","010A","0123","010B"},
            {"","",""}};

    int nbActiveZones=0,activeZone=1;       //variables used to synchronize the 4 display zones

    Button btnZone1,btnZone2,btnZone3,btnZone4;     //Buttons on the activity activity_Values 
    EditText txtZone1,txtZone2,txtZone3,txtZone4,X1,Y1,Z1,X2,Y2,Z2,X3,Y3,Z3,X4,Y4,Z4;       //TextBox of the activity activity_values

    LineChart chart1,chart2,chart3,chart4;      //Graphs we will use to display datas 
    //Properties of the textboxes and the charts 
    LinearLayout.LayoutParams chart1Param,chart2Param,chart3Param,chart4Param,txt1Param,txt2Param,txt3Param,txt4Param;

    LinearLayout both1,both2,both3,both4,Sens1,Sens2,Sens3,Sens4;       //Layouts to display the datas

    String strCmdZone1,strCmdZone2,strCmdZone3,strCmdZone4,unityA,unityB,unityC,unityD;     //Variables of each zone

    int intAdr2 = 0,intAdr1 = 0;        //variables used as adresses to get the right code in strCmds

    BTState BT;     //Objet ef the class BTState to get the Bluetooth components

    public ArrayList<Entry> entries1,entries2,entries3,entries4;//Lists of datas for each graph
	
    LineDataSet dataSet1, dataSet2, dataSet3, dataSet4;     //Objects of datas for each graph

    Long start1,start2,start3,start4,end1,end2,end3,end4,time1,time2,time3,time4;   //variables of timing measurement

    ArrayAdapter adapter;       //variable used to display dialogs
    AlertDialog.Builder alertDialog;        //variable modified for each dialog

    Handler OBDHandlerInit,OBDHandler1,OBDHandler2,OBDHandler3,OBDHandler4;     //Definition of the handlers
	//state variables for each Handler (0=SENDING, 1=RECEIVING)
    int stateG=0,state1=1,state2=1,state3=1,state4=1,stateInit=0,sens1=0,sens2=0,sens3=0,sens4=0,sensType1=0,sensType2=0,sensType3=0,sensType4=0;       
    String strTmp1,strTmp2,strTmp3,strTmp4,strTmpInit,strCmd1,strCmd2,strCmd3,strCmd4;      //Temporary variables for each handler
    String[] strT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_values);

        entries1 = new ArrayList<>();
        entries2 = new ArrayList<>();
        entries3 = new ArrayList<>();
        entries4 = new ArrayList<>();

        //initialization of all the graphs
        chart1 = (LineChart) findViewById(R.id.chart1);
        chart2 = (LineChart) findViewById(R.id.chart2);
        chart3 = (LineChart) findViewById(R.id.chart3);
        chart4 = (LineChart) findViewById(R.id.chart4);

        //initialization of all the layouts
        both1 = (LinearLayout)findViewById(R.id.both1);
        both2 = (LinearLayout)findViewById(R.id.both2);
        both3 = (LinearLayout)findViewById(R.id.both3);
        both4 = (LinearLayout)findViewById(R.id.both4);
        Sens1 = (LinearLayout)findViewById(R.id.Sens1);
        Sens2 = (LinearLayout)findViewById(R.id.Sens2);
        Sens3 = (LinearLayout)findViewById(R.id.Sens3);
        Sens4 = (LinearLayout)findViewById(R.id.Sens4);

        //initialization of all the Layouts
        btnZone1 = (Button)findViewById(R.id.btnZone1);
        btnZone1.setOnClickListener(this);
        btnZone2 = (Button)findViewById(R.id.btnZone2);
        btnZone2.setOnClickListener(this);
        btnZone3 = (Button)findViewById(R.id.btnZone3);
        btnZone3.setOnClickListener(this);
        btnZone4 = (Button)findViewById(R.id.btnZone4);
        btnZone4.setOnClickListener(this);

        //initialization of all the Textboxes
        txtZone1 = (EditText)findViewById(R.id.txtZone1);
        txtZone2 = (EditText)findViewById(R.id.txtZone2);
        txtZone3 = (EditText)findViewById(R.id.txtZone3);
        txtZone4 = (EditText)findViewById(R.id.txtZone4);
        X1 = (EditText)findViewById(R.id.X1);
        Y1 = (EditText)findViewById(R.id.Y1);
        Z1 = (EditText)findViewById(R.id.Z1);
        X2 = (EditText)findViewById(R.id.X2);
        Y2 = (EditText)findViewById(R.id.Y2);
        Z2 = (EditText)findViewById(R.id.Z2);
        X3 = (EditText)findViewById(R.id.X3);
        Y3 = (EditText)findViewById(R.id.Y3);
        Z3 = (EditText)findViewById(R.id.Z3);
        X4 = (EditText)findViewById(R.id.X4);
        Y4 = (EditText)findViewById(R.id.Y4);
        Z4 = (EditText)findViewById(R.id.Z4);

        //getting the Layouts parameters
        chart1Param = (LinearLayout.LayoutParams) chart1.getLayoutParams();
        chart2Param = (LinearLayout.LayoutParams) chart2.getLayoutParams();
        chart3Param = (LinearLayout.LayoutParams) chart3.getLayoutParams();
        chart4Param = (LinearLayout.LayoutParams) chart4.getLayoutParams();

        //getting the Textboxes parameters
        txt1Param = (LinearLayout.LayoutParams) txtZone1.getLayoutParams();
        txt2Param = (LinearLayout.LayoutParams) txtZone2.getLayoutParams();
        txt3Param = (LinearLayout.LayoutParams) txtZone3.getLayoutParams();
        txt4Param = (LinearLayout.LayoutParams) txtZone4.getLayoutParams();





        //Creation of a HandlerThread
        HandlerThread OBD = new HandlerThread("OBD");
        OBD.start();
        Looper OBDLooper = OBD.getLooper();

        //initialization of the Handlers
        OBDHandlerInit = new Handler(OBDLooper) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                }
            }
        };
        OBDHandler1 = new Handler(OBDLooper) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                }
            }
        };
        OBDHandler2 = new Handler(OBDLooper) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                }
            }
        };
        OBDHandler3 = new Handler(OBDLooper) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                }
            }
        };
        OBDHandler4 = new Handler(OBDLooper) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                }
            }
        };


        OBDHandlerInit.post(OBDInit);       //Running OBDInit to initialize the ELM327 module
        stateG=1;       //variable put to 1 in order to stop all the code sending until the initialization is finished

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {     //method called when Values.java is created to update the ActionBar
        getMenuInflater().inflate(R.menu.menu_values, menu);        //adds the menu menu_values to the activity
        return true;
    }


    @Override
    public void onClick(View v) {       //===========================Selection of the action to do for each zone==================================
        int id = v.getId();
        switch (id) {
            case R.id.btnZone1:
                if(stateG == 1){        //if the initialization of the module isn't over, then we wait
                    Toast.makeText(this,"The module is being initialized",Toast.LENGTH_SHORT).show();
                }else{
                    dialogListCmd(0);       //calls the method dialogListCmd with 0 as an argument 
                }
                break;
            case R.id.btnZone2:
                if(stateG == 1){
                    Toast.makeText(this,"The module is being initialized",Toast.LENGTH_SHORT).show();
                }else{
                    dialogListCmd(1);   	//calls the method dialogListCmd with 1 as an argument 
                }
                break;
            case R.id.btnZone3:
                if(stateG == 1){        
                    Toast.makeText(this,"The module is being initialized",Toast.LENGTH_SHORT).show();
                }else{
                    dialogListCmd(2);       //calls the method dialogListCmd with 2 as an argument 
                }
                break;
            case R.id.btnZone4:
                if(stateG == 1){        
                    Toast.makeText(this,"The module is being initialized",Toast.LENGTH_SHORT).show();
                }else{
                    dialogListCmd(3);       //calls the method dialogListCmd with 3 as an argument 
                }
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){        //===========Selection of the action to do for each ActionBar Button=============
        switch (item.getItemId()) {
            case R.id.action_clearZones:
                dialogZoneDelete();     //calls dialogZoneDelete
                return true;
            case R.id.action_changeGraphType:	//"Change Graphs Type" Button
                dialogZoneGraphChange(0);       //calls dialogZoneGraphChange with 0 as argument
                return true;
            case R.id.action_changeDataType:	//"change datas" Button
                dialogZoneGraphChange(1);       //Appel de la méthode dialogZoneGraphChange avec 1 en argument
                return true;
            case R.id.action_help:	//"Help" Button
			
			
				//Procedure of Okonly type dialogs
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);       //creating a dialog
                builder1.setTitle("Need help ?");       //Creating the dialog title
                builder1.setMessage(stringHelp);        //puts the string values of stringHelp as the main message to display
                builder1.setCancelable(true);       //Authorizes the back key to close the dialog
                builder1.setNeutralButton(android.R.string.ok,      //the dialog has 1 button named "OK"
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();     //displays the dialog
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	
    public void dialogListCmd (final int adrCalled){        //=================Displays the list of commands categories======================
	
		//Procedure of singleChoice dialogs
        alertDialog = new AlertDialog.Builder(this);  //dialog creation
        adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, strListCmds); //puts the string values of strListCmds in an Array

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {       //adds the adapter Array in the dialog and configures it as singleChoice
            int choices;
            @Override
            public void onClick(DialogInterface dialog, int which)      //when one item of the dialog is clicked
            {
                dialog.dismiss();
                choices = ((AlertDialog) dialog).getListView().getCheckedItemPosition();        //choices takes the value of the position of the clicked item
                intAdr1 = choices;
                dialogListData(choices,adrCalled);      //calls dialogListData with the zone to affect and the category of data chosen
            }
        });
        alertDialog.setTitle("Choose type of data you want");
        alertDialog.show();     //displays the dialog
    }


    public void dialogListCmdChange (final int adrCalled){        //================Displays the list of commands categories to change==============
	
		//SEE LINE 322 to see the procedure of creating a singleChoice type dialog
        alertDialog = new AlertDialog.Builder(this); 
        adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, strListCmds); 

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() { 
            int choices;
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                choices = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                intAdr1 = choices;      
                dialogListDataChange(choices,adrCalled);       //calls dialogListDataChange 
            }
        });
        alertDialog.setTitle("Choose type of data you want");
        alertDialog.show();     
    }


    public void dialogListData(int cat, final int adrCalled){       //=====================Displays the list of datas============================
        alertDialog = new AlertDialog.Builder(this);  //Creates the dialog
        switch (cat){       //puts a different list in the depending on the choice the user has made
            case 0:
                adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, strListControl);   //puts the values of strListControl in the Array
                strT = strListControl;
                break;
            case 1:
                adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, strListEngine);  //puts the values of strListEngine in the Array
                strT = strListEngine;
                break;
            case 2:
                adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, strListFuel);     //puts the values of strListFuel in the Array
                strT = strListFuel;
                break;
            case 3:
                adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, strListPressure);  //puts the values of strListPressure in the Array
                strT = strListPressure;
                break;
            case 4:
                adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, strListSensors);  //puts the values of strListSensors in the Array
                strT = strListSensors;
                break;
        }

		//SEE LINE 322 to see the procedure of creating a singleChoice type dialog
        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() { 
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                int choices = ((AlertDialog) dialog).getListView().getCheckedItemPosition();        
                intAdr2 = choices;      //represents the second index of the strCmds Table
                switch (adrCalled){
                    case 0:
                        strCmd1 = strT[choices];	//Takes the name of the data of the zone 1
                        break;
                    case 1:
                        strCmd2 = strT[choices];	//Takes the name of the data of the zone 2
                        break;
                    case 2:
                        strCmd3 = strT[choices];	//Takes the name of the data of the zone 3
                        break;
                    case 3:
                        strCmd4 = strT[choices];	//Takes the name of the data of the zone 4
                        break;
                }
                getCode(adrCalled);     //calls the method getCode
                dialogGraphType(adrCalled);     //calls the method dialogGraphType
            }
        });
        alertDialog.setTitle("Choose the data you want");
        alertDialog.show(); 

    }


    public void dialogListDataChange(int cat, final int adrCalled){     //=============================displays the list of datas to change===================================
        //SEE LINE 390
		alertDialog = new AlertDialog.Builder(this);  //Creates a dialog
        switch (cat){       
            case 0:
                adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, strListControl);      
                strT = strListControl;
                break;
            case 1:
                adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, strListEngine);       
                strT = strListEngine;
                break;
            case 2:
                adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, strListFuel);     
                strT = strListFuel;
                break;
            case 3:
                adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, strListPressure);     
                strT = strListPressure;
                break;
            case 4:
                adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, strListSensors);      
                strT = strListSensors;
                break;
        }
		
		//SEE LINE 322 to see the procedure of creating a singleChoice type dialog
        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {       
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                int choices = ((AlertDialog) dialog).getListView().getCheckedItemPosition();        
                switch (adrCalled){
                    case 0:
                        strCmd1 = strT[choices];
                        break;
                    case 1:
                        strCmd2 = strT[choices];
                        break;
                    case 2:
                        strCmd3 = strT[choices];
                        break;
                    case 3:
                        strCmd4 = strT[choices];
                        break;
                }
                intAdr2 = choices;      
                getCodeChange(adrCalled);       //calls the method getCodeChange
            }
        });
        alertDialog.setTitle("Choose the data you want");
        alertDialog.show();     

    }


    public void getCode(int Zone){      //==========gets the code corresponding to the data selected by the user==============

        switch (Zone){      //Selection of the zone
            case 0:
                if (intAdr1==4){
                    sens1=1;
                    sensType1 = intAdr2;
                }else{
                    sens1=0;
                    strCmdZone1 = strCmds[intAdr1][intAdr2];        //attribution of the command of the zone 1 to the variable strZone1
                    state1=0;       //zone 1 put in sending mode
                    start1 = System.currentTimeMillis();        //reading of the start time
                }

                nbActiveZones++;        //incrementation of the active zones in order to synchronize all 4 zones
                OBDHandler1.post(OBDZone1);     //Running OBDHandler1 by adding it the Runnable OBDZone1
                break;
            case 1:		//same as line 481 but for zone 2
                if (intAdr1==4){
                    sens2=1;
                    sensType2 = intAdr2;
                }else{
                    sens2=0;
                    strCmdZone2 = strCmds[intAdr1][intAdr2];        
                    state2=0;       
                    start2 = System.currentTimeMillis();        
                }

                nbActiveZones++;        
                OBDHandler2.post(OBDZone2);     
                break;
            case 2:		//same as line 481 but for zone 3
                if (intAdr1==4){
                    sens3=1;
                    sensType3 = intAdr2;
                }else{
                    sens3=0;
                    strCmdZone3 = strCmds[intAdr1][intAdr2];        
                    state3=0;       
                    start3 = System.currentTimeMillis();        
                }

                nbActiveZones++;        
                OBDHandler3.post(OBDZone3);     
                break;
            case 3:		//same as line 481 but for zone 4
                if(intAdr1==4){
                    sens4=1;
                    sensType4 = intAdr2;
                }else{
                    sens4=0;
                    strCmdZone4 = strCmds[intAdr1][intAdr2];        
                    state4=0;       
                    start4 = System.currentTimeMillis();        
                }

                nbActiveZones++;        
                OBDHandler4.post(OBDZone4);    
                break;
        }

    }


    public void getCodeChange(int Zone){    //===========gets the code corresponding to the data to change=============

        switch (Zone){      //selection of the zone
            case 0:
                strCmdZone1 = strCmds[intAdr1][intAdr2];        //attribution of the command of the zone 1 to the variable strZone1
                state1=0;       //zone 1 put in sending mode
                start1 = System.currentTimeMillis();        //reading of the start time
                break;
            case 1:  	//same as line 545 but for zone 2
                strCmdZone2 = strCmds[intAdr1][intAdr2];        
                state2=0;       
                start2 = System.currentTimeMillis();        
                break;
            case 2:		//same as line 545 but for zone 3
                strCmdZone3 = strCmds[intAdr1][intAdr2];        
                state3=0;       
                start3 = System.currentTimeMillis();        
                break;
            case 3:		//same as line 545 but for zone 4
                strCmdZone4 = strCmds[intAdr1][intAdr2];        
                state4=0;       
                start4 = System.currentTimeMillis();        
                break;
        }

    }


    public void dialogZoneGraphChange (final int motivation){       //========Displays the zones list to change the display type================
		//SEE LINE 322 to see the procedure of creating a singleChoice type dialog
        alertDialog = new AlertDialog.Builder(this);
        adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, choiceZone); 

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            int choices;
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                choices = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                if (motivation==0){     //correspond to the action "Change the display type"
                    clearZones(choices);        //calls the method clearZones
                    dialogGraphType(choices);       //calls the method dialogGraphType
                }else{      //corresponds to the action "change the data"
                    dialogListCmdChange(choices);       //calls the method dialogListCmdChange
                }

            }
        });
        alertDialog.setTitle("Choose the zone");
        alertDialog.show();     
    }


    public void dialogZoneDelete () {    //==================Displays the zones to reset==================
		//SEE LINE 322 to see the procedure of creating a singleChoice type dialog
        alertDialog = new AlertDialog.Builder(this);  
        adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, choiceZone); 

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            int choices;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                choices = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                clearZones(choices);        //calls the method clearZones
            }
        });
        alertDialog.setTitle("Choose the zone");
        alertDialog.show();     
    }


    public void clearZones(int zone){       //=============Resets the chosen zone===================
        switch (zone){      //selection of the chosen zone
            case 0:		//For zone 1
                btnZone1.setVisibility(View.VISIBLE);       //Shows up the zone Button of zone 1
                both1.setVisibility(View.INVISIBLE);        //Hides the layout of the datas of zone 1
				Sens1.setVisibility(View.INVISIBLE);		//Hides the layout of sensors datas of zone 1
                break;
            case 1:		//same for zone 2
                btnZone2.setVisibility(View.VISIBLE);       
                both2.setVisibility(View.INVISIBLE);        
				Sens2.setVisibility(View.INVISIBLE);
                break;
            case 2:		//same for zone 3
                btnZone3.setVisibility(View.VISIBLE);       
                both3.setVisibility(View.INVISIBLE);        
				Sens3.setVisibility(View.INVISIBLE);
                break;
            case 3:		//same for zone 4
                btnZone4.setVisibility(View.VISIBLE);       
                both4.setVisibility(View.INVISIBLE);        
				Sens4.setVisibility(View.INVISIBLE);
                break;
            case 4:		//for all zones
                clearZones(0);   	//recursive call for the zone 1
                clearZones(1);      //recursive call for the zone 2
                clearZones(2);      //recursive call for the zone 3
                clearZones(3);      //recursive call for the zone 4
                break;
        }
    }


    public void dialogGraphType(final int zone){       //============Displays the list of all the data displays===============
		//SEE LINE 322 to see the procedure of creating a singleChoice type dialog
        alertDialog = new AlertDialog.Builder(this);
        adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, choiceGraphType); 

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                int choices = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                showPanel(zone,choices);        //calls the showPanel method
            }
        });
        alertDialog.setTitle("Choose your values showing");
        alertDialog.show();     
    }


    private void showPanel(int position,int choice){  //displays the choice made by the user
        switch (position) {     //selection of the zone
            case 0:  //zone 1
                switch(choice){     //selection of the display type
                    case 0:     //display type is a Graph
                        btnZone1.setVisibility(View.INVISIBLE);     //Applies the modifications of the TextBox
                        both1.setVisibility(View.VISIBLE);      //Shows the datas display
                        chart1Param.weight=0;       //Puts the graph in 100% of the zone
                        txt1Param.weight=1;     //Puts the TextBox in 0% of the zone
                        chart1.setLayoutParams(chart1Param);        //Applies the modifications of the graph
                        txtZone1.setLayoutParams(txt1Param);        //Applies the modifications of the TextBox
                        break;
                    case 1:		//display type is a TextBox
                        btnZone1.setVisibility(View.INVISIBLE);     //Applies the modifications of the TextBox
                        both1.setVisibility(View.VISIBLE);      //Shows the datas display
                        chart1Param.weight=1;       //Puts the graph in 0% of the zone
                        txt1Param.weight=0;     //Puts the TextBox in 100% of the zone
                        chart1.setLayoutParams(chart1Param);        //Applies the modifications of the graph
                        txtZone1.setLayoutParams(txt1Param);        //Applies the modifications of the TextBox
                        break;
                    case 2:		//displya type is a Graph and a Textbox
                        btnZone1.setVisibility(View.INVISIBLE);     //Applies the modifications of the TextBox
                        both1.setVisibility(View.VISIBLE);      //Shows the datas display
                        chart1Param.weight=1;       //Puts the graph in 66% (2/3) of the zone
                        txt1Param.weight=2;     //Puts the TextBox in 33% (1/3) of the zone
                        chart1.setLayoutParams(chart1Param);        //Applies the modifications of the graph
                        txtZone1.setLayoutParams(txt1Param);        //Applies the modifications of the TextBox
                        break;
                }
                break;
            case 1:  //same as zone 1 line 672
                switch(choice){
                    case 0:
                        btnZone2.setVisibility(View.INVISIBLE);
                        both2.setVisibility(View.VISIBLE);
                        chart2Param.weight=0;
                        txt2Param.weight=1;
                        chart2.setLayoutParams(chart2Param);
                        txtZone2.setLayoutParams(txt2Param);
                        break;
                    case 1:
                        btnZone2.setVisibility(View.INVISIBLE);
                        both2.setVisibility(View.VISIBLE);
                        chart2Param.weight=1;
                        txt2Param.weight=0;
                        chart2.setLayoutParams(chart2Param);
                        txtZone2.setLayoutParams(txt2Param);
                        break;
                    case 2:
                        btnZone2.setVisibility(View.INVISIBLE);
                        both2.setVisibility(View.VISIBLE);
                        chart2Param.weight=1;
                        txt2Param.weight=2;
                        chart2.setLayoutParams(chart2Param);
                        txtZone2.setLayoutParams(txt2Param);
                        break;
                }
                break;
            case 2:  //same as zone 1 line 672
                switch(choice){
                    case 0:
                        btnZone3.setVisibility(View.INVISIBLE);
                        both3.setVisibility(View.VISIBLE);
                        chart3Param.weight=0;
                        txt3Param.weight=1;
                        chart3.setLayoutParams(chart3Param);
                        txtZone3.setLayoutParams(txt3Param);
                        break;
                    case 1:
                        btnZone3.setVisibility(View.INVISIBLE);
                        both3.setVisibility(View.VISIBLE);
                        chart3Param.weight=1;
                        txt3Param.weight=0;
                        chart3.setLayoutParams(chart3Param);
                        txtZone3.setLayoutParams(txt3Param);
                        break;
                    case 2:
                        btnZone3.setVisibility(View.INVISIBLE);
                        both3.setVisibility(View.VISIBLE);
                        chart3Param.weight=1;
                        txt3Param.weight=2;
                        chart3.setLayoutParams(chart3Param);
                        txtZone3.setLayoutParams(txt3Param);
                        break;
                }
                break;
            case 3:  //same as zone 1 line 672
                switch(choice){
                    case 0:
                        btnZone4.setVisibility(View.INVISIBLE);
                        both4.setVisibility(View.VISIBLE);
                        chart4Param.weight=0;
                        txt4Param.weight=1;
                        chart4.setLayoutParams(chart4Param);
                        txtZone4.setLayoutParams(txt4Param);
                        break;
                    case 1:
                        btnZone4.setVisibility(View.INVISIBLE);
                        both4.setVisibility(View.VISIBLE);
                        chart4Param.weight=1;
                        txt4Param.weight=0;
                        chart4.setLayoutParams(chart4Param);
                        txtZone4.setLayoutParams(txt4Param);
                        break;
                    case 2:
                        btnZone4.setVisibility(View.INVISIBLE);
                        both4.setVisibility(View.VISIBLE);
                        chart4Param.weight=1;
                        txt4Param.weight=2;
                        chart4.setLayoutParams(chart4Param);
                        txtZone4.setLayoutParams(txt4Param);
                        break;
                }
                break;
        }

    }       //=============================SHOWS UP THE DATA AND THE GRAPH TYPE THE USER REQUIRED================================


    private Runnable OBDInit = new Runnable() {     //=================Handler responsible for the initialization of the module=============

        int index = 0;      //variable used to define the message to send
        @Override
        public void run() {
            synchronized (OBDInit) {
                switch (stateInit) {        //Selection of the state
                    case 0:   //===================SENDING MODE================
                        try {
                            BT.out.write((initCmds[index] + "\r\n").getBytes()); //sending the code of the question depending on the index value
                            BT.out.flush();     //Forces the message to be read and improve I/O performance
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stateInit = 1;      //going into reception mode
                        break;

                    case 1:  //================RECEIVING MODE====================
                        try {
                            strTmpInit = BT.buff.readLine();        //reading the data returned by the module
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        strTmpInit = strTmpInit.replaceAll("\\s", ""); //deletes the following characters : [ \t\n\x0B\f\r]
                        strTmpInit = strTmpInit.replaceAll(">", "");        //deletes the character >
                        strTmpInit = strTmpInit.replaceAll("(BUS INIT)|(BUSINIT)|(\\.)", "");  //deletes all the initialiation characters

                        if (strTmpInit.matches("([OK])+")) {        //If the message is OK that means that the commands has been understood by the module
                            if (index == 2) {       //If index is 2 that means that the initialization is finished
                                stateInit = 2;      //Stops the Handler
                                Toast.makeText(getApplicationContext(),"Initialization is finished",Toast.LENGTH_SHORT).show();
                                stateG = 0;     //initialization variable put to 0 to allox other commands to be sent
                            } else {        //else it means that the initialization is over
                                stateInit = 0;      //Handler is put back in the SENDING MODE 
                                index++;        //incrémentation of index to send the 
                            }
                        }
                        break;
                    case 2:     //State used to stop the Handler
                        OBDHandlerInit.removeCallbacks(OBDInit);    //Deletes the runnable, that means that the Handler is stopped
                        break;
                }
                if (stateInit != 2) {
                    OBDHandlerInit.postDelayed(OBDInit, 500); //Relaunches the Runnable every 500 ms of the state is different than 2
                }
            }
        }
    };

	
    private Runnable OBDZone1 = new Runnable() {        //=================Handler used to send the codes and receive the datas of the zone 1=======================

        @Override
        public void run() {
            if(sens1==1){       //If the data selected is a smartphone sensor
                int sensorType1=Sensor.TYPE_GYROSCOPE;   //creates a variable to select the sensor to use
                switch (sensType1){ //selects the sensor chosen by the user
                    case 0: //Accelerometer
                        sensorType1 = Sensor.TYPE_ACCELEROMETER;     //Using the Accelerometer
                        break;
                    case 1: //Gyroscope
                        sensorType1 = Sensor.TYPE_GYROSCOPE;     //Using the Gyroscope
                        break;
                    case 2: //GPS
                        sensorType1 = Sensor.TYPE_STATIONARY_DETECT;     //Using the GPS
                        break;
                }
                //creates and uses the ReactiveSensor object of the library to retrieve the data
                new ReactiveSensors(getApplicationContext()).observeSensor(sensorType1)
                        .subscribeOn(Schedulers.computation())
                        .filter(ReactiveSensorFilter.filterSensorChanged())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ReactiveSensorEvent>() {
                            @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
                                SensorEvent event = reactiveSensorEvent.getSensorEvent();  //when the sensor value gets updated

                                double x = round((double)event.values[0]);
                                double y = round((double)event.values[1]);
                                double z = round((double)event.values[2]);
                                both1.setVisibility(View.INVISIBLE);
                                Sens1.setVisibility(View.VISIBLE);
                                X1.setText(strCmd1+"  X  : "+x);
                                Y1.setText(strCmd1+"  Y  : "+y);
                                Z1.setText(strCmd1+"  Z  : "+z);

                                //TODO Add the option of displaying the data in graphs
                                if(activeZone==1){
                                    if(nbActiveZones>1){
                                        activeZone++;
                                    }else{
                                        activeZone=1;
                                    }
                                }
                            }
                        });

            }else{      //If the selected data is from the module
                switch (state1) {
                    case 0:   //===================SENDING MODE================
                        if(activeZone ==1){ //If the zone to refresh is the zone 1

                            try {
                                BT.out.write((strCmdZone1 + "\r\n").getBytes());    //Sends the code in the Outpustream socket
                                BT.out.flush();     //Forces the message to be read and improve I/O performance
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            state1 = 1;     //going into reception mode

                        }
                        break;
                    case 1:  //================RECEIVING MODE====================

                        try {
                            strTmp1 = BT.buff.readLine();   //reading the data returned by the module
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        strTmp1 = strTmp1.replaceAll("\\s", ""); //deletes the following characters : [ \t\n\x0B\f\r]
                        strTmp1 = strTmp1.replaceAll(">", "");      //deletes the character >//deletes all the initialiation characters
                        strTmp1 = strTmp1.replaceAll("(BUS INIT)|(BUSINIT)|(\\.)", "");     //deletes all the initialiation characters


                        if (strTmp1.matches("([0-9A-F])+")) {       //We can only receive a Hexadecimal code (from 0 to 9 and from A to F)
                            final String AnswerA = strTmp1.substring(0, 4);     //Gets the code returned by the module
                            //When the module returns the code, it adds the value 4000 in hexadecimal to it, so we compare the code received
                            //to the expected code to know if the received code corresponds to the expectation
                            final int AnswerExpected = Integer.parseInt(strCmdZone1.substring(1, 4), 16) + Integer.parseInt("4000", 16);   //Recreating the expected code
                            final int decCmd = Integer.parseInt(strCmdZone1.substring(1,4),16); //Gets value of the sent code in decimal
                            final int intAnswA = Integer.parseInt(AnswerA, 16);     //Gets the code returned by the module
                            if (intAnswA == AnswerExpected) {       //SIf both codes are the same, that means that the data needs to be used in this zone
                                end1 = System.currentTimeMillis();      //measure the reception time
                                time1 = (end1-start1);      //Measures the total acquisition time
                                runOnUiThread(new Runnable() {      //Runnable used to edit the views of the activity
                                    @Override
                                    public void run() {
                                        unityA = getResultUnit(Integer.parseInt(strCmdZone1, 16));      //Gets the data Unit 
                                        String tmpA = strTmp1.substring(4, strTmp1.length());       //Gets the data returned by the module
                                        Integer intTmp = Integer.parseInt(tmpA, 16);        //Converts the data in deciaml
                                        double dblTmp = 0.0;        //initialize th variable that will have the final data
                                        if(intTmp>255){     //If the data is bigger than 255, it is coded with 2 bytes
                                            float divider = intTmp/256f;        //gets the dvision ration by 256
                                            int sup = (int)Math.floor(divider);     //Rounds it at floor
                                            int byte1 = intTmp-(sup*256);       //Gets the decimal value of the smaller weight byte
                                            int byte2 = sup*256;    //Gets the decimal value of the Higher weight byte
											 
                                            dblTmp = getFormattedResult(decCmd,byte1,byte2);  //calls the method GetFormattedResult
                                        }else{      //else it is coded with 1 byte
                                            dblTmp = getFormattedResult(decCmd,intTmp);     //calls the method GetFormattedResult
                                        }
                                        txtZone1.setText(strCmd1+ "  "+Double.toString(dblTmp) + "   " + unityA);     //Displays the data and its unity
                                        entries1.add(new Entry(time1,(float)dblTmp));       //Adds the data and the time to the chart data
                                        createDataSet1();       //calls the method createDataSet1 to update the chart data
                                        createGraph1();     //calls the method createGraph1 to update the chart display
                                        chart1.notifyDataSetChanged();      //Updates the chart to notify it the change
                                        chart1.invalidate();        //Shows the graph once updated
                                        state1 = 0;     //Puts the Handler back into SEND mode
                                        if(nbActiveZones > 1){      //if there is more than one zone to update
                                            activeZone++;     //increments the variable to update the next zone the next time
                                        }

                                    }
                                });
                            }
                        }

                        break;
                    case 2:     //Stops the Handler
                        OBDHandler1.removeCallbacks(OBDZone1);      //Deletes the runnable, that means that the Handler is stopped
                        break;
                }
                if (state1!=2) {
                    OBDHandler1.postDelayed(OBDZone1, 400);    //Relaunches the Runnable every 400 ms of the state is different than 2
                }
            }


        }
    };


    //=================================================================================================================================//
    //=======================The way OBDZone2, OBDZone3 and OBDZone4 works is the same than OBDZone1 ==================================//
    //=======================except for the code for the active zones so it will be the only one commented=============================//
    //=================================================================================================================================//



    private Runnable OBDZone2 = new Runnable() {
        int i=0;
        @Override
        public void run() {
            if(sens2==1){
                int sensorType2=Sensor.TYPE_GYROSCOPE;
                switch (sensType2){
                    case 0:
                        sensorType2 = Sensor.TYPE_ACCELEROMETER;
                        break;
                    case 1:
                        sensorType2 = Sensor.TYPE_GYROSCOPE;
                        break;
                    case 2:
                        sensorType2 = Sensor.TYPE_STATIONARY_DETECT;
                        break;
                }
                new ReactiveSensors(getApplicationContext()).observeSensor(sensorType2)
                        .subscribeOn(Schedulers.computation())
                        .filter(ReactiveSensorFilter.filterSensorChanged())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ReactiveSensorEvent>() {
                            @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
                                SensorEvent event = reactiveSensorEvent.getSensorEvent();

                                double x = round((double)event.values[0]);
                                double y = round((double)event.values[1]);
                                double z = round((double)event.values[2]);
                                both2.setVisibility(View.INVISIBLE);
                                Sens2.setVisibility(View.VISIBLE);
                                X2.setText("X  : "+x);
                                Y2.setText("Y  : "+y);
                                Z2.setText("Z  : "+z);

                                if(activeZone==2){
                                    if(nbActiveZones>2){
                                        activeZone++;
                                    }else{
                                        activeZone=1;
                                    }
                                }
                            }
                        });

            }else {
                switch (state2) {
                    case 0:   //===================SENDING MODE================
                        if (activeZone == 2) {  //if the zone to update is the zone 2
                            try {
                                BT.out.write((strCmdZone2 + "\r\n").getBytes());
                                BT.out.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            state2 = 1;
                        }
                        break;
                    case 1:  //================RECEIVING MODE====================


                        try {
                            strTmp2 = BT.buff.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        strTmp2 = strTmp2.replaceAll("\\s", "");
                        strTmp2 = strTmp2.replaceAll(">", "");
                        strTmp2 = strTmp2.replaceAll("(BUS INIT)|(BUSINIT)|(\\.)", "");

                        if (strTmp2.matches("([0-9A-F])+")) {
                            String AnswerB = strTmp2.substring(0, 4);
                            int AnswerExpected2 = Integer.parseInt(strCmdZone2.substring(1, 4), 16) + Integer.parseInt("4000", 16);
                            int intAnswB = Integer.parseInt(AnswerB, 16);
                            if (intAnswB == AnswerExpected2) {
                                end2 = System.currentTimeMillis();
                                time2 = (end2 - start2) * 1000;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        unityB = getResultUnit(Integer.parseInt(strCmdZone2, 16));
                                        String tmp2 = strTmp2.substring(4, strTmp2.length());
                                        Integer intTmp2 = Integer.parseInt(tmp2, 16);
                                        double dblTmp2 = 0.0;
                                        if (intTmp2 > 255) {
                                            float divider = intTmp2 / 256f;
                                            int sup = (int) Math.floor(divider);
                                            int byte1 = intTmp2 - (sup * 256);
                                            int byte2 = sup * 256;
                                            dblTmp2 = getFormattedResult(Integer.parseInt(strCmdZone1, 16), byte1, byte2);
                                        } else {
                                            dblTmp2 = getFormattedResult(Integer.parseInt(strCmdZone1, 16), intTmp2);
                                        }
                                        txtZone2.setText(strCmd2+ "  "+Double.toString(dblTmp2) + "   " + unityB);
                                        entries2.add(new Entry(time2, (float) dblTmp2));
                                        createDataSet2();
                                        createGraph2();
                                        chart2.notifyDataSetChanged();
                                        chart2.invalidate();
                                        state2 = 0;
                                        if (nbActiveZones > 2) {        //if there is another zone to update
                                            activeZone++;       	//increments the variable of the next zone to update
                                        } else {
                                            activeZone = 1;     //next zone updated will be the zone 1
                                        }
                                    }
                                });
                            }
                        }
                        break;
                    case 2:
                        OBDHandler2.removeCallbacks(OBDZone2);
                        break;
                }
                if (state2 != 2) {
                    OBDHandler2.postDelayed(OBDZone2, 400);
                }
            }

        }
    };


    private Runnable OBDZone3 = new Runnable() {
        int i=0;
        @Override
        public void run() {
            if(sens3==1){
                int sensorType3=Sensor.TYPE_GYROSCOPE;
                switch (sensType2){
                    case 0:
                        sensorType3 = Sensor.TYPE_ACCELEROMETER;
                        break;
                    case 1:
                        sensorType3 = Sensor.TYPE_GYROSCOPE;
                        break;
                    case 2:
                        sensorType3 = Sensor.TYPE_STATIONARY_DETECT;
                        break;
                }
                new ReactiveSensors(getApplicationContext()).observeSensor(sensorType3)
                        .subscribeOn(Schedulers.computation())
                        .filter(ReactiveSensorFilter.filterSensorChanged())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ReactiveSensorEvent>() {
                            @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
                                SensorEvent event = reactiveSensorEvent.getSensorEvent();

                                double x = round((double)event.values[0]);
                                double y = round((double)event.values[1]);
                                double z = round((double)event.values[2]);
                                both3.setVisibility(View.INVISIBLE);
                                Sens3.setVisibility(View.VISIBLE);
                                X3.setText("X  : "+x);
                                Y3.setText("Y  : "+y);
                                Z3.setText("Z  : "+z);

                                if(activeZone==3){
                                    if(nbActiveZones>3){
                                        activeZone++;
                                    }else{
                                        activeZone=1;
                                    }
                                }

                            }
                        });

            }else {
                switch (state3) {
                    case 0:   //===================SENDING MODE================
                        if (activeZone == 3) {      //if the zone to update is the zone 3
                            try {
                                BT.out.write((strCmdZone3 + "\r\n").getBytes());
                                BT.out.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            state3 = 1;
                        }
                        break;

                    case 1:  //================RECEIVING MODE====================


                        try {
                            strTmp3 = BT.buff.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        strTmp3 = strTmp3.replaceAll("\\s", "");
                        strTmp3 = strTmp3.replaceAll(">", "");
                        strTmp3 = strTmp3.replaceAll("(BUS INIT)|(BUSINIT)|(\\.)", "");


                        if (strTmp3.matches("([0-9A-F])+")) {
                            String AnswerC = strTmp3.substring(0, 4);
                            int AnswerExpected3 = Integer.parseInt(strCmdZone3.substring(1, 4), 16) + Integer.parseInt("4000", 16);
                            int intAnswC = Integer.parseInt(AnswerC, 16);
                            if (intAnswC == AnswerExpected3) {
                                end3 = System.currentTimeMillis();
                                time3 = (end3 - start3) * 1000;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        unityC = getResultUnit(Integer.parseInt(strCmdZone3, 16));
                                        String tmp3 = strTmp3.substring(4, strTmp3.length());
                                        Integer intTmp3 = Integer.parseInt(tmp3, 16);
                                        double dblTmp3 = 0.0;
                                        if (intTmp3 > 255) {
                                            float divider = intTmp3 / 256f;
                                            int sup = (int) Math.floor(divider);
                                            int byte1 = intTmp3 - (sup * 256);
                                            int byte2 = sup * 256;
                                            dblTmp3 = getFormattedResult(Integer.parseInt(strCmdZone1, 16), byte1, byte2);
                                        } else {
                                            dblTmp3 = getFormattedResult(Integer.parseInt(strCmdZone1, 16), intTmp3);
                                        }
                                        txtZone3.setText(strCmd3+ "  "+Double.toString(dblTmp3) + "   " + unityC);
                                        entries3.add(new Entry(time3, (float) dblTmp3));
                                        createDataSet3();
                                        createGraph3();
                                        chart3.notifyDataSetChanged();
                                        chart3.invalidate();
                                        state3 = 0;
                                        if (nbActiveZones > 3) {        //if there is another zone to update
                                            activeZone++;       //increments the variable of the next zone to update
                                        } else {
                                            activeZone = 1;     //next zone updated will be the zone 1
                                        }

                                    }
                                });
                            }
                        }
                        break;
                    case 2:
                        OBDHandler3.removeCallbacks(OBDZone3);
                        break;
                }
                if (state3 != 2) {
                    OBDHandler3.postDelayed(OBDZone3, 400);
                }
            }

        }
    };


    private Runnable OBDZone4 = new Runnable() {
        int i=0;
        @Override
        public void run() {
            if(sens4==1){
                int sensorType4=Sensor.TYPE_GYROSCOPE;
                switch (sensType2){
                    case 0:
                        sensorType4 = Sensor.TYPE_ACCELEROMETER;
                        break;
                    case 1:
                        sensorType4 = Sensor.TYPE_GYROSCOPE;
                        break;
                    case 2:
                        sensorType4 = Sensor.TYPE_STATIONARY_DETECT;
                        break;
                }
                new ReactiveSensors(getApplicationContext()).observeSensor(sensorType4)
                        .subscribeOn(Schedulers.computation())
                        .filter(ReactiveSensorFilter.filterSensorChanged())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ReactiveSensorEvent>() {
                            @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
                                SensorEvent event = reactiveSensorEvent.getSensorEvent();

                                double x = round((double)event.values[0]);
                                double y = round((double)event.values[1]);
                                double z = round((double)event.values[2]);
                                both4.setVisibility(View.INVISIBLE);
                                Sens4.setVisibility(View.VISIBLE);
                                X4.setText("X  : "+x);
                                Y4.setText("Y  : "+y);
                                Z4.setText("Z  : "+z);

								
                                if (activeZone == 4) {
                                    activeZone = 1;
                                }

                            }
                        });

            }else {
                switch (state4) {
                    case 0:   //===================SENDING MODE================
                        if (activeZone == 4) {      //if the zone to update is the zone 4
                            try {
                                BT.out.write((strCmdZone4 + "\r\n").getBytes());
                                BT.out.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            state4 = 1;
                        }
                        break;

                    case 1:  //================RECEIVING MODE====================

                        try {
                            strTmp4 = BT.buff.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        strTmp4 = strTmp4.replaceAll("\\s", "");
                        strTmp4 = strTmp4.replaceAll(">", "");
                        strTmp4 = strTmp4.replaceAll("(BUS INIT)|(BUSINIT)|(\\.)", "");


                        if (strTmp4.matches("([0-9A-F])+")) {
                            String AnswerD = strTmp4.substring(0, 4);
                            int AnswerExpected4 = Integer.parseInt(strCmdZone4.substring(1, 4), 16) + Integer.parseInt("4000", 16);
                            int intAnswD = Integer.parseInt(AnswerD, 16);
                            if (intAnswD == AnswerExpected4) {
                                end4 = System.currentTimeMillis();
                                time4 = (end4 - start4) * 1000;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        unityD = getResultUnit(Integer.parseInt(strCmdZone4, 16));
                                        String tmp4 = strTmp4.substring(4, strTmp4.length());
                                        Integer intTmp4 = Integer.parseInt(tmp4, 16);
                                        double dblTmp4 = 0.0;
                                        if (intTmp4 > 255) {
                                            float divider = intTmp4 / 256f;
                                            int sup = (int) Math.floor(divider);
                                            int byte1 = intTmp4 - (sup * 256);
                                            int byte2 = sup * 256;
                                            dblTmp4 = getFormattedResult(Integer.parseInt(strCmdZone4, 16), byte1, byte2);
                                        } else {
                                            dblTmp4 = getFormattedResult(Integer.parseInt(strCmdZone1, 16), intTmp4);
                                        }
                                        txtZone4.setText(strCmd4+ "  "+Double.toString(dblTmp4) + "   " + unityD);
                                        entries4.add(new Entry(time4, (float) dblTmp4));
                                        createDataSet4();
                                        createGraph4();
                                        chart4.notifyDataSetChanged();
                                        chart4.invalidate();
                                        state4 = 0;
                                        activeZone = 1;    //next zone updated will be the zone 1
                                    }
                                });
                            }
                        }
                        break;
                    case 2:
                        OBDHandler4.removeCallbacks(OBDZone4);
                        break;
                }
                if (state4 != 2) {
                    OBDHandler4.postDelayed(OBDZone4, 400);
                }
            }

        }
    };


    private void createDataSet1(){      //creates the data of the chart1
        dataSet1 = new LineDataSet(entries1, strCmd1); //adds the data entries1 to the data already existing
        dataSet1.setColors(Color.RED);      //color of the chart line
        dataSet1.setLineWidth(1f);      //thickness of the line
        dataSet1.setValueTextColor(Color.BLACK);    //coloration of the text
    }

    //see the crateDataSet1 code
    private void createDataSet2(){      //creates the data of the chart2
        dataSet2 = new LineDataSet(entries2, strCmd2);
        dataSet2.setColors(Color.GREEN);
        dataSet2.setLineWidth(1f);
        dataSet2.setValueTextColor(Color.BLACK);
    }

    //see the crateDataSet1 code
    private void createDataSet3(){      //creates the data of the chart3
        dataSet3 = new LineDataSet(entries3, strCmd3);
        dataSet3.setColors(Color.BLUE);
        dataSet3.setLineWidth(1f);
        dataSet3.setValueTextColor(Color.BLACK);
    }

    //see the crateDataSet1 code
    private void createDataSet4(){      //creates the data of the chart4
        dataSet4 = new LineDataSet(entries4, strCmd4);
        dataSet4.setColors(Color.MAGENTA);
        dataSet4.setLineWidth(1f);
        dataSet4.setValueTextColor(Color.BLACK);
    }


    private void createGraph1(){        //Creates the Chart disposition of the zone 1

        LineData lineData1 = new LineData(dataSet1);       //Converts the datas to display them
        chart1.setData(lineData1);      //Adds the datas to the graph
        Description desc1 = new Description();      //Creates a description type object
        desc1.setText(unityA);      //La description aura pour texte l'unité des valeurs du graphe 1
        desc1.setTextColor(Color.RED);      //puts the description in colors
        chart1.setDescription(desc1);       //Adds the descrption to the graph
        XAxis chart1X = chart1.getXAxis();      //Obtention de l'Axe des abscisses du graphe
        YAxis chart1Yi = chart1.getAxisRight();     //Obtention de l'Axe des ordonnées du graphe
        chart1Yi.setEnabled(false);     //Disable the Y Axis 
        chart1X.setEnabled(true);       //Enable the X Axis 
        chart1X.setPosition(XAxis.XAxisPosition.BOTTOM);    //Adds the X axis to the graph
        chart1X.setDrawGridLines(false);        //Disables the X axis grid
        chart1.invalidate();        //Updates the graph
    }

    //see the crateGraph1 code
    private void createGraph2(){
        LineData lineData2 = new LineData(dataSet2);
        chart2.setData(lineData2);
        Description desc2 = new Description();
        desc2.setText(unityB);
        desc2.setTextColor(Color.BLUE);
        chart2.setDescription(desc2);
        XAxis chart2X = chart2.getXAxis();
        YAxis chart2Yi = chart2.getAxisRight();
        chart2Yi.setEnabled(false);
        chart2X.setEnabled(true);
        chart2X.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart2X.setDrawGridLines(false);
        chart2.invalidate(); // refresh
    }

    //see the crateGraph1 code
    private void createGraph3(){
        LineData lineData3 = new LineData(dataSet3);
        chart3.setData(lineData3);
        Description desc3 = new Description();
        desc3.setText(unityC);
        desc3.setTextColor(Color.GREEN);
        chart3.setDescription(desc3);
        XAxis chart3X = chart3.getXAxis();
        YAxis chart3Yi = chart3.getAxisRight();
        chart3Yi.setEnabled(false);
        chart3X.setEnabled(true);
        chart3X.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart3X.setDrawGridLines(false);
        chart3.invalidate(); // refresh
    }

    //see the crateGraph1 code
    private void createGraph4(){
        LineData lineData4 = new LineData(dataSet4);
        chart4.setData(lineData4);
        Description desc4 = new Description();
        desc4.setText(unityA);
        desc4.setTextColor(Color.MAGENTA);
        chart4.setDescription(desc4);
        XAxis chart4X = chart4.getXAxis();
        YAxis chart4Yi = chart4.getAxisRight();
        chart4Yi.setEnabled(false);
        chart4X.setEnabled(true);
        chart4X.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart4X.setDrawGridLines(false);
        chart4.invalidate(); // refresh
    }

    //transforms the data sent by the module to make it understandable (2 bytes)
    public double getFormattedResult(int Data,int byte1,int byte2){
        double output=0.0;
        switch (Data){
            case 289:
                output = byte1+byte2;
                break;
            case 305:
                output = byte1+byte2;
                break;
            case 257:
                output = byte1+byte2;
                break;
            case 324:
                output = round((byte1+byte2)/32768.0);
                break;
            case 322:
                output = round((byte1+byte2)/1000.0);
                break;
            case 2306:
                output = byte1+byte2;
                break;
            case 323:
                output = round((byte2 + byte1)/255.0);
                break;
            case 260:
                //single byte
                break;
            case 272:
                output = round((byte1+byte2)/100.0);
                break;
            case 348:
                //single byte
                break;
            case 268:
                output = round((byte1+byte2)/4.0);
                break;
            case 287 :
                output = byte1+byte2;
                break;
            case 273:
                //single byte
                break;
            //case 324:
                //same as upper
                //break;
            case 350:
                output = round((byte1+byte2)/20.0);
                break;
            case 337:
                //Fuel type
                output = byte1+byte2;
                break;
            case 303:
                //single byte
                break;
            case 308:
                //TODO LATER 0134
                break;
            case 307:
                //single byte
                break;
            case 266:
                //single byte
                break;
            case 291:
                output=10.0*(byte1+byte2);
                break;
            case 267:
                //single byte
                break;
        }

        return output;
    }

    //transforms the data sent by the module to make it understandable (1 bytes)
    public double getFormattedResult(int Data,int byte1){
        double output = 2.0;
        switch (Data){
            case 260:
                output=byte1/2.55;
                break;
            case 269:
                output = byte1;
                break;
            case 348:
                output = round(byte1-40.0);
                break;
            case 273:
                output = round(byte1/2.55);
                break;
            case 303:
                output = round(byte1/2.55);
                break;
            case 307:
                output = byte1;
                break;
            case 266:
                output = byte1*3.0;
                break;
            case 267:
                output = byte1;
                break;
            case 268:
                output = byte1;
                break;
        }
        return output;
    }

    //rounds a decimal to the third decimal
    public static double round (double value){
        if(3<0)throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(3,BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    //returns the unity of the data
    public String getResultUnit(int Data){
        String dataUnit = "";
        switch (Data){
            case 289:
                dataUnit = "Km";
                break;
            case 305:
                dataUnit = "Km";
                break;
            case 257:
                dataUnit = "";
                break;
            case 324:
                dataUnit = "ratio";
                break;
            case 322:
                dataUnit = "Volts";
                break;
            case 2306:
                dataUnit = "VIN";
                break;
            case 323:
                dataUnit = "%";
                break;
            case 260:
                dataUnit = "%";
                break;
            case 272:
                dataUnit = "grams/sec";
                break;
            case 348:
                dataUnit = "°C";
                break;
            case 268:
                dataUnit = "RPM";
                break;
            case 287 :
                dataUnit = "Seconds";
                break;
            case 273:
                dataUnit = "%";
                break;
            case 350:
                dataUnit = "L/h";
                break;
            case 337:
                dataUnit = "Fuel Type";
                break;
            case 303:
                dataUnit = "%";
                break;
            case 308:
                dataUnit = "ratio mA";
                break;
            case 307:
                dataUnit = "KPa";
                break;
            case 266:
                dataUnit = "KPa";
                break;
            case 291:
                dataUnit = "KPa";
                break;
            case 267:
                dataUnit = "KPa";
                break;
        }
        return dataUnit;
    }

}
