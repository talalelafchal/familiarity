package com.example.TrafficJam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.example.TrafficJam.Logic.*;


/**
 * Created with IntelliJ IDEA.
 * User: steinar
 * Date: 3/24/13
 * Time: 12:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectPuzzleActivity extends Activity {

    public  List<String> puzzles;
    public  List<String> challenges;

    GameLogic mGameLogic;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.puzzles);

        ListView puzzleListView = (ListView) findViewById(R.id.puzzle_list);
        Spinner challengeSpinner = (Spinner) findViewById(R.id.challenge_spinner);

        puzzles = new ArrayList<String>();
        challenges = new ArrayList<String>();
        mGameLogic = new GameLogic();


        readChallengeList("/assets/challengelist.xml", challenges);


        ArrayAdapter<String> challengeAdapter = new ArrayAdapter<String>(this,
                R.id.challenge_spinner, challenges);



        ArrayAdapter<String> puzzleAdapter = new ArrayAdapter<String>(this,
                R.id.puzzle_list, puzzles);




        puzzleListView.setAdapter(puzzleAdapter);
        challengeSpinner.setAdapter(challengeAdapter);

        challengeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                readPuzzleList("/assets/" + selectedItemView.toString(), puzzles);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        puzzleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent gameIntent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(gameIntent);

            }
        });






    }

    private void readChallengeList(String filename, List<String> challenges)
    {
        try {
            File xmlFile = new File( filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            System.out.println( doc.getDocumentElement().getNodeName() );
            NodeList nList = doc.getElementsByTagName("challenge");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("Challenge name: " + eElement.getAttribute("name"));
                    String challengeStr = eElement.getElementsByTagName("puzzles").item(0).getTextContent();
                    System.out.println("Challenge file: " + challengeStr );
                    challenges.add( challengeStr );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private static void readPuzzleList( String filename, List<String> puzzles ) {
        try {
            File xmlFile = new File( filename );
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            System.out.println( doc.getDocumentElement().getNodeName() );
            NodeList nList = doc.getElementsByTagName("puzzle");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("Puzzle id   : " + eElement.getAttribute("id"));
                    String puzzleStr = eElement.getElementsByTagName("setup").item(0).getTextContent();
                    System.out.println("Puzzle setup: " + puzzleStr );
                    puzzles.add( puzzleStr );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

