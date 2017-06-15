package com.barkhappy.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;  

import com.parse.ParseACL;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class ParseUserInfo {
	
	private String flag;
	private String fbuserId;
	private String city;
	private Date birthdate;
	private String firstname;
	private String lastname;
	private boolean male;
	private ParseGeoPoint geoLocation;
	private Date lastLocationUpdateTime;
	private boolean online;
	private ParseFile profileImageFile;
	private String relationship;
	private String state;
	private String username;
	private String zipcode;
	private String objectId;
	private Date createdAt;
	private Date updatedAt;	
	private ParseACL	acl;
	private List<ParseObject> lstMyDogs = new ArrayList<ParseObject>();
		
	public List<ParseObject> getLstMyDogs() {
		return lstMyDogs;
	}
	public void setLstMyDogs(List<ParseObject> lstMyDogs) {
		this.lstMyDogs = lstMyDogs;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getFbuserId() {
		return fbuserId;
	}
	public void setFbuserId(String fbuserId) {
		this.fbuserId = fbuserId;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Date getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public boolean isMale() {
		return male;
	}
	public void setMale(boolean male) {
		this.male = male;
	}
	public ParseGeoPoint getGeoLocation() {
		return geoLocation;
	}
	public void setGeoLocation(ParseGeoPoint geoLocation) {
		this.geoLocation = geoLocation;
	}
	public Date getLastLocationUpdateTime() {
		return lastLocationUpdateTime;
	}
	public void setLastLocationUpdateTime(Date lastLocationUpdateTime) {
		this.lastLocationUpdateTime = lastLocationUpdateTime;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	public ParseFile getProfileImageFile() {
		return profileImageFile;
	}
	public void setProfileImageFile(ParseFile profileImageFile) {
		this.profileImageFile = profileImageFile;
	}
	public String getRelationship() {
		return relationship;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public ParseACL getAcl() {
		return acl;
	}
	public void setAcl(ParseACL acl) {
		this.acl = acl;
	}
}