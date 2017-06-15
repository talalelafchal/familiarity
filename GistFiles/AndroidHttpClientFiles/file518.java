package com.matpompili.settle;

import java.io.Serializable;

/**
 * Created by matteo on 21/10/14.
 */
public class RoomObject implements Serializable{
    public boolean isLectureGoing;
    public boolean isUpdateAvailable;
    public String name;
    public String roomID;
    public String buildingID;
    public Float availability;
    public Float quietness;
    public Long lastUpdate;

    public RoomObject() {
        this.name = "Aula senza nome";
        this.availability = (float) 0;
        this.quietness = (float) 0;
        this.isLectureGoing = false;
        this.lastUpdate = (long) 0;

    }

    public RoomObject(String name, float availability, float quietness, long lastUpdate) {
        this.name = name;
        if (availability < 0) {
            this.availability = availability;
            this.isLectureGoing = false;
        } else {
            this.isLectureGoing = true;
        }
        this.quietness = quietness;
        this.lastUpdate = lastUpdate;
    }

    public void setName(String name) {
        if (name != null) {this.name = name;}
    }

    public void setRoomID(String roomID) {
        if (roomID != null) {this.roomID = roomID;}
    }

    public void setAvailability(String availability) {
        this.availability = Float.parseFloat(availability);
    }

    public void setQuietness(String quietness) {
        this.quietness = Float.parseFloat(quietness);
    }

    public void setLectureGoing(boolean isLectureGoing) {
        this.isLectureGoing = isLectureGoing;
    }

    public void setLastUpdate(String lastUpdate) {
        isUpdateAvailable = true;
        if (Long.parseLong(lastUpdate) == -1) {
            isUpdateAvailable = false;
        }
        this.lastUpdate = Long.parseLong(lastUpdate);
    }

    public void setBuildingID (String buildingID) {
        this.buildingID = buildingID;
    }

    public String getLastUpdate () {
        if (lastUpdate < 60) {
            return lastUpdate.toString() + " sec fa";
        } else {
            return Long.toString(lastUpdate.intValue()/60) + " min fa";
        }
    }

}

