package com.freakycoders.xmlparsing;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
 

import android.net.ParseException;
import android.os.AsyncTask;


import android.util.Log;

/* Litt missvisende, men denne klassen tar 1 argument, en URL som innkommando.
*  Den returnerer ett doc document, så hvis du har en side som inneholder XML
*  Send den til denne klassen, så kan den hente info fra siden og returnere den som ett dom dokument
*  Fant ut at android liker dårlig å gjøre parsing i mainklassen, så AsyncTask fungerte flott til dette.
*  
*  !! Notetoself: 
*  Brukes slik:
*  			// Vi må ha en URL inn (her hardkodet) 
*           URL test = new URL("http://www.freakycoders.com/android/t5_data/phonebook.xml");
*           (Vi kjører denne mot klassen (Setter her opp test2 som en AsyncTask)
        	AsyncTask<URL, Void, Document> test2 = new GetXmlandDoc().execute(test);
        	
        	(Før vi kan gjøre noe med dette, så må vi sette dette til ett Document!
        	doc = test2.get();
*  
*/

public class GetXmlandDoc extends AsyncTask<URL, Void, Document> {
	protected Document doInBackground(URL... urls){
		
	Document doc = null;
    String strXml="";

    HttpParams httpParameters = new BasicHttpParams();
 
    // Setting 15 sec timeout for connection to establish
    int timeoutConnection = 15000;
    HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);
 
    //Setting default socket timeout 15 sec to wait for data
    int timeoutSocket = 15000;
    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
 
    DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
    String url = urls[0].toString();
   
    HttpGet httpGet = new HttpGet(url);
    HttpResponse httpResponse;
    System.out.println(urls[0]);
    
    try {
    	
        httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
         
       // Assigning the value received to xm
        strXml = EntityUtils.toString(httpEntity);
         
        //Getting dom element of the XML file
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(strXml));
        doc = db.parse(is);
   
    // Vi har noen feilparametre som behandles av klassen    
    }
    catch (ParseException e) {
        Log.e("Error",e+"");
        doc=null;
    }
    catch (ClientProtocolException e) {
        Log.e("Error",e+"");
        doc=null;
    }
    catch (ParserConfigurationException e) {
        Log.e("Error",e+"");
        doc=null;
    }
    catch (SAXException e) {
       Log.e("Error",e+"");
        doc=null;
    }
    catch (IOException e) {
        Log.e("Error",e+"");
        doc=null;
    }
	
    
    return doc;
}   
}

