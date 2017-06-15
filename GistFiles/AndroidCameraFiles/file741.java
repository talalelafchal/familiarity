package com.example.myfirstapp;
/**
 * @author Sai Valluri
 * This is a java class which will keep track of the meeting that the user can add
 */
public class Meeting {
		
	private static String name;
	private static String date;
	private static String time;
	private static String description;
	
	public Meeting(String name, String date, String time, String description) {
		
		this.name = name;
		this.date = date;
		this.time = time;
		this.description = description;
		
	}
	
	public static String getName() {
		return name;
	}
	public static String getDate() {
		return date;
	}
	public static String getTime() {
		return time;
	}
	public static String getDescription() {
		return description;
	}

}
