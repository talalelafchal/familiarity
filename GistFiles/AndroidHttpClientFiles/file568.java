package com.android.demo.notepad3;




import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;



import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;
import android.widget.Toast;

public class HttpRequest extends Activity {
	

    private NotesDbAdapter mDbHelper;

	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        
        mDbHelper = new NotesDbAdapter(this);

        mDbHelper.open();
        
        
        Bundle extras = getIntent().getExtras();
        String passedType = extras != null ? extras.getString("myPassedType")
                                 : null;
        String passedTable = extras != null ? extras.getString("myPassedTable")
                : null;
        
        if(passedType.equals("Update")){
        	if(passedTable.equals("routes")){
        		XMLParserRoutes(getPage("routes"));
        		Toast.makeText(HttpRequest.this, "Updated Routes", Toast.LENGTH_LONG).show();
        		  setResult(RESULT_OK);
        		    finish();
        	
        	}
        	else if(passedTable.equals("trips")){
        		XMLParserTrips(getPage("trips"));
        		Toast.makeText(HttpRequest.this, "Updated Trips", Toast.LENGTH_LONG).show();
        		  setResult(RESULT_OK);
        		    finish();
        		
        	}
        	else if(passedTable.equals("stop_times")){
        		Toast.makeText(HttpRequest.this, "Updating Stop Times", Toast.LENGTH_LONG).show();
        		XMLParserStopTimes(getPage("stop_times"));
        		  setResult(RESULT_OK);
        		    finish();
        		
        	}
        	else if(passedTable.equals("stops")){
        		Toast.makeText(HttpRequest.this, "Updating Stops", Toast.LENGTH_LONG).show();
        		XMLParserStops(getPage("stops"));
        		  setResult(RESULT_OK);
        		    finish();
        		
        	}
        	else if(passedTable.equals("checkVersions")){
        		Toast.makeText(HttpRequest.this, "Checking Versions", Toast.LENGTH_LONG).show();
        		XMLParserCheckVersion(getPage("checkVersion"));
        		
        	}
        	
        	
        }
        else{
        
        XMLParserRoutes(getPage("routes"));
        XMLParserTrips(getPage("trips"));
        XMLParserStopTimes(getPage("stop_times"));
       XMLParserStops(getPage("stops"));
        XMLParserVersion(getPage("version"));
        XMLParserCheckVersion(getPage("checkVersion"));
        mDbHelper.close();
        }
    }
    
    public InputStream getPage(String type) {
    	String str = "***";
    	InputStream stream = null;
    	HttpPost post = null;
        try
    	{
    		HttpClient hc = new DefaultHttpClient();
    		
    		if(type.equals("routes"))post = new HttpPost("http://homepages.ecs.vuw.ac.nz/~ian/nwen304/routes.xml");
    		else if(type.equals("trips"))post = new HttpPost("http://homepages.ecs.vuw.ac.nz/~ian/nwen304/trips.xml");
    		else if (type.equals("stop_times"))post = new HttpPost("http://homepages.ecs.vuw.ac.nz/~ian/nwen304/stop_times.xml");
    		else if (type.equals("stops"))post = new HttpPost("http://homepages.ecs.vuw.ac.nz/~ian/nwen304/stops.xml");
    		else if (type.equals("version"))post = new HttpPost("http://homepages.ecs.vuw.ac.nz/~ian/nwen304/versions.xml");
    		else if (type.equals("checkVersion"))post = new HttpPost("http://ecs.victoria.ac.nz/twiki/pub/Courses/NWEN304_2011T1/AndroidProject2/versions-3.xml");
    		HttpResponse rp = hc.execute(post);

    		if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
    		{
    			HttpEntity entity = rp.getEntity();
        		 stream = entity.getContent();
    		}
    	}catch(IOException e){
    		e.printStackTrace();
    	}  
    	
    	return stream;
    }
   
   public void XMLParserRoutes(InputStream inputStream){

    try {
    	XmlPullParser parser = Xml.newPullParser();
		System.out.println("Parsing Routes");
		// auto-detect the encoding from the stream
		parser.setInput(inputStream, null);
		int eventType = parser.getEventType();
		Route currentRoute = null;
		boolean done = false;

		while (eventType != XmlPullParser.END_DOCUMENT && !done){
			
			String name = null;
			switch (eventType){
			case XmlPullParser.START_DOCUMENT:
			
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				System.out.println(name);

				if (name.equalsIgnoreCase("document")){
					//skip over it
				} 
				if (name.equalsIgnoreCase("record")){
					currentRoute = new Route();
				} 
				else if (currentRoute != null){
					if (name.equalsIgnoreCase("route_id")){
						currentRoute.routeID = parser.nextText();
						//System.out.println(parser.nextText());
					} else if (name.equalsIgnoreCase("agency_id")){
						currentRoute.agencyID = parser.nextText();
						//System.out.println(parser.nextText());
					} else if (name.equalsIgnoreCase("route_short_name")){
						currentRoute.shortName = parser.nextText();
						//System.out.println(parser.nextText());
					} else if (name.equalsIgnoreCase("route_long_name")){
						currentRoute.longName = parser.nextText();
						//System.out.println(parser.nextText());
				} else if (name.equalsIgnoreCase("departure_desc")){
					System.out.println(parser.nextText()); //need to get rid of it.
				} else if (name.equalsIgnoreCase("route_type")){
					currentRoute.routeType = parser.nextText();
					//System.out.println(parser.nextText());
				} 
			}
			break;
		case XmlPullParser.END_TAG:
			name = parser.getName();
			if (name.equalsIgnoreCase("record") && currentRoute != null){
				// call addRoute in database and pass currentRoute.
				mDbHelper.insertRoute(currentRoute.routeID, currentRoute.agencyID, currentRoute.shortName, currentRoute.longName, currentRoute.routeType);
				System.out.println("--------------------------------------------------------------Adding a Record");
			} else if (name.equalsIgnoreCase("document")){
				//Outties!!
				System.out.println("--------------------------------------------------------------END");
				done = true;
			}
			break;
		}

		eventType = parser.next();
	}

} catch (Exception e) {
	Log.e("AndroidNews::PullFeedParser", e.getMessage(), e);
	throw new RuntimeException(e);
}

}
   
   /** ************************* TRIPS ******************************** **/
   public void XMLParserTrips(InputStream inputStream){

	    try {
	    	XmlPullParser parser = Xml.newPullParser();
			System.out.println("Parsing Trips");
			// auto-detect the encoding from the stream
			parser.setInput(inputStream, null);
			int eventType = parser.getEventType();
			Trip currentTrip = null;
			boolean done = false;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){
				
				String name = null;
				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
			
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					System.out.println(name);

					if (name.equalsIgnoreCase("document")){
						//skip over it
					} 
					if (name.equalsIgnoreCase("record")){
						currentTrip = new Trip();
					} 
					else if (currentTrip != null){
						if (name.equalsIgnoreCase("route_id")){
							currentTrip.routeID = parser.nextText();
							//System.out.println(parser.nextText());
						} else if (name.equalsIgnoreCase("service_id")){
							currentTrip.serviceID = parser.nextText();
							//System.out.println(parser.nextText());
						} else if (name.equalsIgnoreCase("trip_id")){
							currentTrip.tripID = parser.nextText();
							//System.out.println(parser.nextText());
						} else if (name.equalsIgnoreCase("direction_id")){
							currentTrip.directionID = parser.nextText();
							//System.out.println(parser.nextText());
					} else if (name.equalsIgnoreCase("block_id")){
						currentTrip.blockID = parser.nextText();
						//System.out.println(parser.nextText());
					}  else if (name.equalsIgnoreCase("shape_id")){
						currentTrip.shapeID = parser.nextText();
						//System.out.println(parser.nextText());
					} 
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("record") && currentTrip != null){
					// call addTrip in database and pass currentTrip.
					mDbHelper.insertTrip(currentTrip.routeID, currentTrip.serviceID, currentTrip.tripID, currentTrip.directionID, currentTrip.blockID,currentTrip.shapeID);
					System.out.println("--------------------------------------------------------------Adding a Record");
				} else if (name.equalsIgnoreCase("document")){
					//Outties!!
					System.out.println("--------------------------------------------------------------END");
					done = true;
				}
				break;
			}

			eventType = parser.next();
		}

	} catch (Exception e) {
		Log.e("AndroidNews::PullFeedParser", e.getMessage(), e);
		throw new RuntimeException(e);
	}

	}
   
   
   /** ************************* STOP TIMES ******************************** **/
   public void XMLParserStopTimes(InputStream inputStream){

	    try {
	    	XmlPullParser parser = Xml.newPullParser();
			System.out.println("Parsing StopTimes");
			// auto-detect the encoding from the stream
			parser.setInput(inputStream, null);
			int eventType = parser.getEventType();
			StopTime currentStopTime = null;
			boolean done = false;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){
				
				String name = null;
				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
			
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					System.out.println(name);

					if (name.equalsIgnoreCase("document")){
						//skip over it
					} 
					if (name.equalsIgnoreCase("record")){
						currentStopTime = new StopTime();
					} 
					else if (currentStopTime != null){
						if (name.equalsIgnoreCase("trip_id")){
							currentStopTime.tripID = parser.nextText();
							//System.out.println(currentStopTime.tripID);
						} else if (name.equalsIgnoreCase("arrival_time")){
							currentStopTime.arrivalTime = parser.nextText();
							//System.out.println(parser.nextText());
						} else if (name.equalsIgnoreCase("departure_time")){
							currentStopTime.departureTime = parser.nextText();
							//System.out.println(parser.nextText());
						} else if (name.equalsIgnoreCase("stop_id")){
							currentStopTime.stopID = parser.nextText();
							//System.out.println(parser.nextText());
					} 

				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("record") && currentStopTime != null){
					//System.out.println(currentStopTime.tripID+ " " +  currentStopTime.arrivalTime + " " + currentStopTime.departureTime + " " + currentStopTime.stopID);
					mDbHelper.insertStopTime(currentStopTime.tripID, currentStopTime.arrivalTime, currentStopTime.departureTime, currentStopTime.stopID);
					System.out.println("--------------------------------------------------------------Adding a Record");
				} else if (name.equalsIgnoreCase("document")){
					//Outties!!
					System.out.println("--------------------------------------------------------------END");
					done = true;
				}
				break;
			}

			eventType = parser.next();
		}

	} catch (Exception e) {
		Log.e("AndroidNews::PullFeedParser", e.getMessage(), e);
		throw new RuntimeException(e);
	}

	}
   
   
   /** ************************* STOPS ******************************** **/
   public void XMLParserStops(InputStream inputStream){

	    try {
	    	XmlPullParser parser = Xml.newPullParser();
			System.out.println("Parsing Stops");
			// auto-detect the encoding from the stream
			parser.setInput(inputStream, null);
			int eventType = parser.getEventType();
			Stop currentStop = null;
			boolean done = false;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){
				
				String name = null;
				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
			
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					System.out.println(name);

					if (name.equalsIgnoreCase("document")){
						//skip over it
					} 
					if (name.equalsIgnoreCase("record")){
						currentStop = new Stop();
					} 
					else if (currentStop != null){
						if (name.equalsIgnoreCase("stop_id")){
							currentStop.stopID = parser.nextText();
							//System.out.println(currentStopTime.tripID);
						} else if (name.equalsIgnoreCase("stop_name")){
							currentStop.stopName = parser.nextText();
							//System.out.println(parser.nextText());
						} 
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("record") && currentStop != null){
					//System.out.println(currentStopTime.tripID+ " " +  currentStopTime.arrivalTime + " " + currentStopTime.departureTime + " " + currentStopTime.stopID);
					mDbHelper.insertStop(currentStop.stopID, currentStop.stopName);
					System.out.println("--------------------------------------------------------------Adding a Record");
				} else if (name.equalsIgnoreCase("document")){
					//Outties!!
					System.out.println("--------------------------------------------------------------END");
					done = true;
				}
				break;
			}

			eventType = parser.next();
		}

	} catch (Exception e) {
		Log.e("AndroidNews::PullFeedParser", e.getMessage(), e);
		throw new RuntimeException(e);
	}

	}
   
   
   /** ************************* VERSION ******************************** **/
   public void XMLParserVersion(InputStream inputStream){

	    try {
	    	XmlPullParser parser = Xml.newPullParser();
			System.out.println("Parsing Version");
			// auto-detect the encoding from the stream
			parser.setInput(inputStream, null);
			int eventType = parser.getEventType();
			Version currentVersion = null;
			boolean done = false;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){
				
				String name = null;
				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
			
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					System.out.println(name);

					if (name.equalsIgnoreCase("document")){
						//skip over it
					} 
					if (name.equalsIgnoreCase("record")){
						currentVersion = new Version();
					} 
					else if (currentVersion != null){
						if (name.equalsIgnoreCase("data")){
							currentVersion.file = parser.nextText();
							//System.out.println(currentStopTime.tripID);
						} else if (name.equalsIgnoreCase("version")){
							currentVersion.version = parser.nextText();
							//System.out.println(parser.nextText());
						} 
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("record") && currentVersion != null){
					//System.out.println(currentStopTime.tripID+ " " +  currentStopTime.arrivalTime + " " + currentStopTime.departureTime + " " + currentStopTime.stopID);
					mDbHelper.insertVersion(currentVersion.file, currentVersion.version);
					System.out.println("--------------------------------------------------------------Adding a Record");
				} else if (name.equalsIgnoreCase("document")){
					//Outties!!
					System.out.println("--------------------------------------------------------------END");
					done = true;
				}
				break;
			}

			eventType = parser.next();
		}

	} catch (Exception e) {
		Log.e("AndroidNews::PullFeedParser", e.getMessage(), e);
		throw new RuntimeException(e);
	}

	}
   
   
   /** ************************* CHECK VERSION ******************************** **/
   public void XMLParserCheckVersion(InputStream inputStream){
	   mDbHelper.open();
	    try {
	    	XmlPullParser parser = Xml.newPullParser();
			System.out.println("Parsing CheckVersion");
			// auto-detect the encoding from the stream
			parser.setInput(inputStream, null);
			int eventType = parser.getEventType();
			Version currentVersion = null;
			boolean done = false;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){
				
				String name = null;
				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
			
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					System.out.println(name);

					if (name.equalsIgnoreCase("document")){
						//skip over it
					} 
					if (name.equalsIgnoreCase("record")){
						currentVersion = new Version();
					} 
					else if (currentVersion != null){
						if (name.equalsIgnoreCase("data")){
							currentVersion.file = parser.nextText();
							//System.out.println(currentStopTime.tripID);
						} else if (name.equalsIgnoreCase("version")){
							currentVersion.version = parser.nextText();
							//System.out.println(parser.nextText());
						} 
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("record") && currentVersion != null){
					//System.out.println(currentStopTime.tripID+ " " +  currentStopTime.arrivalTime + " " + currentStopTime.departureTime + " " + currentStopTime.stopID);
					mDbHelper.insertCheckVersion(currentVersion.file, currentVersion.version);
					System.out.println("--------------------------------------------------------------Adding a Record");
				} else if (name.equalsIgnoreCase("document")){
					//Outties!!
					System.out.println("--------------------------------------------------------------END");
					done = true;
				}
				break;
			}

			eventType = parser.next();
		}

	} catch (Exception e) {
		Log.e("AndroidNews::PullFeedParser", e.getMessage(), e);
		throw new RuntimeException(e);
	}
	mDbHelper.close();
	  setResult(RESULT_OK);
	    finish();
	}
   
   
   
    
}
