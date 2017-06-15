import android.text.Html;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import boxman.peacesoft.com.boxman.R;

public class GMapV2Direction {
    public final static String MODE_DRIVING = "driving";
    public final static String MODE_WALKING = "walking";

    public GMapV2Direction() {
    }


    public Document getDocument(LatLng start, LatLng end, String mode) {
        String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=driving";
        Log.d("url", url);
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.parse(in);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDurationText(Document doc) {
        try {

            NodeList nl1 = doc.getElementsByTagName("duration");
            Node node1 = nl1.item(0);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "text"));
            Log.i("DurationText", node2.getTextContent());
            return node2.getTextContent();
        } catch (Exception e) {
            return "0";
        }
    }

    public int getDurationValue(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("duration");
            Node node1 = nl1.item(0);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.i("DurationValue", node2.getTextContent());
            return Integer.parseInt(node2.getTextContent());
        } catch (Exception e) {
            return -1;
        }
    }

    public String getDistanceText(Document doc) {
        /*
		 * while (en.hasMoreElements()) { type type = (type) en.nextElement();
		 * 
		 * }
		 */

        try {
            NodeList nl1;
            nl1 = doc.getElementsByTagName("distance");

            Node node1 = nl1.item(nl1.getLength() - 1);
            NodeList nl2 = null;
            nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.d("DistanceText", node2.getTextContent());
            return node2.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

		/*
		 * NodeList nl1; if(doc.getElementsByTagName("distance")!=null){ nl1=
		 * doc.getElementsByTagName("distance");
		 * 
		 * Node node1 = nl1.item(nl1.getLength() - 1); NodeList nl2 = null; if
		 * (node1.getChildNodes() != null) { nl2 = node1.getChildNodes(); Node
		 * node2 = nl2.item(getNodeIndex(nl2, "value")); Log.d("DistanceText",
		 * node2.getTextContent()); return node2.getTextContent(); } else return
		 * "-1";} else return "-1";
		 */
    }

    public int getDistanceValue(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("distance");
            Node node1 = null;
            node1 = nl1.item(nl1.getLength() - 1);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.i("DistanceValue", node2.getTextContent());
            return Integer.parseInt(node2.getTextContent());
        } catch (Exception e) {
            return -1;
        }
		/*
		 * NodeList nl1 = doc.getElementsByTagName("distance"); Node node1 =
		 * null; if (nl1.getLength() > 0) node1 = nl1.item(nl1.getLength() - 1);
		 * if (node1 != null) { NodeList nl2 = node1.getChildNodes(); Node node2
		 * = nl2.item(getNodeIndex(nl2, "value")); Log.i("DistanceValue",
		 * node2.getTextContent()); return
		 * Integer.parseInt(node2.getTextContent()); } else return 0;
		 */
    }

    public String getStartAddress(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("start_address");
            Node node1 = nl1.item(0);
            Log.i("StartAddress", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

    }

    public String getEndAddress(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("end_address");
            Node node1 = nl1.item(0);
            Log.i("StartAddress", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }
    }

    public String getCopyRights(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("copyrights");
            Node node1 = nl1.item(0);
            Log.i("CopyRights", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

    }

    public ArrayList<LatLng> getDirection(Document doc) {
        NodeList nl1, nl2, nl3;
        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
        nl1 = doc.getElementsByTagName("step");
        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {
                Node node1 = nl1.item(i);
                nl2 = node1.getChildNodes();

                Node locationNode = nl2
                        .item(getNodeIndex(nl2, "start_location"));
                nl3 = locationNode.getChildNodes();
                Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                double lat = Double.parseDouble(latNode.getTextContent());
                Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                double lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));

                locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "points"));
                ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
                for (int j = 0; j < arr.size(); j++) {
                    listGeopoints.add(new LatLng(arr.get(j).latitude, arr
                            .get(j).longitude));
                }

                locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "lat"));
                lat = Double.parseDouble(latNode.getTextContent());
                lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));
            }
        }

        return listGeopoints;
    }

    private int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }

    public void drawRoute(LatLng start, LatLng end, final GoogleMap map,
                          final OnDrawedRouteListener callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=driving&language=vi";
        Log.d("BALE", "URL:" + url);
        client.get(url, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                callback.onStart();
            }

            @Override
            public void onSuccess(int resultCode, Header[] arg1, String content) {
                try {
                    DocumentBuilder builder = DocumentBuilderFactory
                            .newInstance().newDocumentBuilder();
                    Document doc = builder.parse((new InputSource(new ByteArrayInputStream(content.getBytes("utf-8")))));
                    ArrayList<LatLng> directionPoint = getDirection(doc);
                    PolylineOptions rectLine = new PolylineOptions().width(8)
                            .color(R.color.blue_bg);

                    //TODO change color
                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    map.addPolyline(rectLine);
                    callback.onSuccess(doc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int resultCode, Header[] arg1, String arg2,
                                  Throwable arg3) {
                callback.onFail();
            }
        });

    }

    public interface OnDrawedRouteListener {
        public void onStart();

        public void onSuccess(Document dataRoute);

        public void onFail();
    }


    public ArrayList<InstructionObject> getRouteInstruction(Document doc) {
        ArrayList<InstructionObject> arrInstructionResult = new ArrayList<GMapV2Direction.InstructionObject>();
        NodeList allStep = doc.getElementsByTagName("step");
        if (allStep.getLength() > 0) {
            for (int i = 0; i < allStep.getLength(); i++) {
                Node step = allStep.item(i);
                NodeList childrenInStep = step.getChildNodes();

                InstructionObject instructionObject = new InstructionObject();

                // Get main instruction and hint instruction
                Node htmlInstruction = childrenInStep.item(getNodeIndex(childrenInStep, "html_instructions"));
                String strInstruction = htmlInstruction.getTextContent();
                String[] arrHintInstruction = strInstruction.split("<div");
                if (arrHintInstruction.length > 0) {
                    instructionObject.mainInstruction = Html.fromHtml(arrHintInstruction[0]).toString();
                    for (int j = 1; j < arrHintInstruction.length; j++) {
                        String hint = Html.fromHtml(arrHintInstruction[j]).toString();
                        int indexOfHtmlTag = hint.lastIndexOf(">");
                        if (hint.length() > indexOfHtmlTag) {
                            instructionObject.hintInstruction.add(hint.substring(indexOfHtmlTag + 1));
                        }
                    }
                }


                // Get distance and distance text
                NodeList distance = childrenInStep.item(getNodeIndex(childrenInStep, "distance")).getChildNodes();
                Node value = distance.item(getNodeIndex(distance, "value"));
                Node text = distance.item(getNodeIndex(distance, "text"));
                instructionObject.distance = Integer.parseInt(value.getTextContent());
                instructionObject.distanceText = text.getTextContent();

                arrInstructionResult.add(instructionObject);
            }
        }

        return arrInstructionResult;
    }

    public class InstructionObject {
        public String mainInstruction;
        public ArrayList<String> hintInstruction;
        public double distance; // 690 (m)
        public String distanceText; // 0.69 km

        public InstructionObject() {
            hintInstruction = new ArrayList<String>();
        }

        public String getMainInstruction() {
            return mainInstruction;
        }

        public void setMainInstruction(String mainInstruction) {
            this.mainInstruction = mainInstruction;
        }

        public ArrayList<String> getHintInstruction() {
            return hintInstruction;
        }

        public void setHintInstruction(ArrayList<String> hintInstruction) {
            this.hintInstruction = hintInstruction;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public String getDistanceText() {
            return distanceText;
        }

        public void setDistanceText(String distanceText) {
            this.distanceText = distanceText;
        }
    }
}