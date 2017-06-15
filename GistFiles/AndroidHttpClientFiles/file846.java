package com.mapexample.mapexample;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.android.maps.GeoPoint;

public class Directions {

  public static ArrayList<GeoPoint> getDirections(double lat1, double lon1,
			double lat2, double lon2) {
double lat,lng;
		String url = String.format("%s=%s,%s&destination=%s,%s%s",
				"http://maps.googleapis.com/maps/api/directions/xml?origin",
				lat1, lon1, lat2, lon2, "&sensor=false");
		String tag[] = { "lat", "lng" };
		ArrayList<GeoPoint> list_of_geopoints = new ArrayList<GeoPoint>();
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url);
			response = httpClient.execute(httpPost, localContext);
			InputStream in = response.getEntity().getContent();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(in);
			if (doc != null) {
				NodeList nl1, nl2;
				nl1 = doc.getElementsByTagName(tag[0]);
				nl2 = doc.getElementsByTagName(tag[1]);
				list_of_geopoints.add(new GeoPoint((int) (lat1 * 1E6),
						(int) (lon1 * 1E6)));
				if (nl1.getLength() > 0) {
					list_of_geopoints = new ArrayList<GeoPoint>();
					for (int i = 0; i < nl1.getLength(); i++) {
						Node node1 = nl1.item(i);
						Node node2 = nl2.item(i);
						lat = Double.parseDouble(node1.getTextContent());
						  lng = Double.parseDouble(node2.getTextContent());
						list_of_geopoints.add(new GeoPoint((int) (lat * 1E6),
								(int) (lng * 1E6)));
					}
				} else {
					// No points found
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list_of_geopoints;
	}
}