package com.matpompili.settle;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by matteo on 21/10/14.
 */
public class BuildingObject implements Serializable{
    public String name;
    public String buildingID;
    public String imageURL;
    public Integer roomCount;
    public ArrayList<RoomObject> rooms;

    public BuildingObject(){
        this.rooms = new ArrayList<RoomObject>();
        this.roomCount = 0;
        this.imageURL = null;
    }

    public BuildingObject(String name, String buildingID) {
        this.name = name;
        this.buildingID = buildingID;
        this.rooms = new ArrayList<RoomObject>();
        this.roomCount = 0;
    }

    public void addRoom(RoomObject room) {
        room.setBuildingID(this.buildingID);
        this.rooms.add(room);
    }

    public void setName(String name) {
        if (name != null) {this.name = name;}
    }

    public void setBuildingID(String buildingID) {
        if (buildingID != null){this.buildingID = buildingID;}
    }

    public void setRoomCount(Integer roomCount) {
        if (roomCount >= 0) {this.roomCount = roomCount;}
    }

    public void getRooms() throws XmlPullParserException, IOException {
        this.rooms.clear();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        String url = App.getContext().getString(R.string.xml_remote_url)+"rooms="+this.buildingID;
        InputStream is = new URL(url).openConnection().getInputStream();
        parser.setInput(is, null);
        //System.out.print("Finished Download");
        int eventType = parser.getEventType();
        String text = null;
        String tagName;
        RoomObject room = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            tagName = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (tagName.equalsIgnoreCase("room")) {
                        // create a new instance of employee
                        room = new RoomObject();
                        room.setBuildingID(this.buildingID);
                    }
                    break;
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tagName.equalsIgnoreCase("room")) {
                        assert room != null;
                        this.addRoom(room);
                    } else if (tagName.equalsIgnoreCase("name")) {
                        assert room != null;
                        room.setName(text);
                    } else if (tagName.equalsIgnoreCase("id")) {
                        assert room != null;
                        room.setRoomID(text);
                    } else if (tagName.equalsIgnoreCase("availability")) {
                        assert room != null;
                        room.setAvailability(text);
                    } else if (tagName.equalsIgnoreCase("quietness")) {
                        assert room != null;
                        room.setQuietness(text);
                    } else if (tagName.equalsIgnoreCase("lastUpdate")) {
                        assert room != null;
                        room.setLastUpdate(text);
                    } else if (tagName.equalsIgnoreCase("isLectureGoing")) {
                        assert room != null;
                        assert text != null;
                        room.setLectureGoing(text.equalsIgnoreCase("yes"));
                    }
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
    }
}


