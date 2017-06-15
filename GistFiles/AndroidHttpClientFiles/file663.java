package com.codepath.apps.mysimpletwitter.models;


import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by joanniehuang on 2017/3/3.
 */

@Parcel
public class User implements Parcelable{
    //list attribute
    String name;
    long userID;
    String screenName;
    String profileImgURL;
    String profileDescription;
    long followerCount;
    long followingCount;

    public User(){
    }

    protected User(android.os.Parcel in) {
        name = in.readString();
        userID = in.readLong();
        screenName = in.readString();
        profileImgURL = in.readString();
        profileDescription = in.readString();
        followerCount = in.readLong();
        followingCount = in.readLong();
    }


    //generate the User object
    public static User fromJSON(JSONObject jsonObject){
        //populate the json data
        User user = new User();
        try {
            user.name = jsonObject.getString("name");
            user.userID = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImgURL = jsonObject.getString("profile_image_url");
            user.profileDescription = jsonObject.getString("description");
            user.followerCount = jsonObject.getLong("followers_count");
            user.followingCount = jsonObject.getLong("friends_count");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public String getName() {
        return name;
    }

    public long getUserID() {
        return userID;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImgURL() {
        return profileImgURL;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public long getFollowerCount() {
        return followerCount;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel, int i) {
        parcel.writeString(this.getProfileDescription());
        parcel.writeString(this.getProfileImgURL());
        parcel.writeString(this.getName());
        parcel.writeString(this.getScreenName());
        parcel.writeLong(this.getFollowerCount());
        parcel.writeLong(this.getFollowingCount());
        parcel.writeLong(this.getUserID());
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>(){

        @Override
        public User createFromParcel(android.os.Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int i) {
            return new User[i];
        }
    };
}
