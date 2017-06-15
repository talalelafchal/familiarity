package utility;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;




////////////////// Data Library //////////////////////////////

// This method uses DataLibrary.xml to pass through data

///////////////////////////////////////////////////////////

    public class Utility {

        public static String returnXML(String tagname, int i) {


            File dataStreamFile = new File("a");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException dataStreamException) {
                dataStreamException.printStackTrace();
            }
            Document doc = null;
            try {
                doc = builder.parse(dataStreamFile);
            } catch (org.xml.sax.SAXException SAXException) {
                SAXException.printStackTrace();
            } catch (java.io.IOException IOException) {
                IOException.printStackTrace();
            }
            //node name should be changed according to xml file, item is index which return first value of xml once found
            String getXML = doc.getElementsByTagName(tagname).item(i).getFirstChild().getNodeValue();
            return getXML;
        }

        //Reload
        public static String returnXML(String tagname) {

            return returnXML(tagname, 0);
        }


//////////////////// Object Library ////////////////////////////

// This method uses ObjectLibrary.xml to pass through data

///////////////////////////////////////////////////////////////


        public static String returnObjectXML(String tagname, int i) {

            File dataStreamFile = new File("a");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException dataStreamException) {
                dataStreamException.printStackTrace();
            }
            Document doc = null;
            try {
                doc = builder.parse(dataStreamFile);
            } catch (SAXException SAXException) {
                SAXException.printStackTrace();
            } catch (IOException IOException) {
                IOException.printStackTrace();
            }
            //node name should be changed according to xml file, item is index which return first value of xml once found
            String objectXML = doc.getElementsByTagName(tagname).item(i).getFirstChild().getNodeValue();
            return objectXML;
        }

        //Reload
        public static String returnObjectXML(String tagname) {

            return returnObjectXML(tagname, 0);
        }


        /////////////////////////////////// Update XML Document //////////////////////////////////////////////////

// The below method is used to update the specified XML document based on values extrapolated from the app

        /////////////////////////////////////////////////////////////////////////////////////////////////////////



        public static void updateXML(IOSDriver driver, String tagname, String childNode, String elementNode){

            String target = returnObjectXML(tagname);

            WebElement updatedValue = driver.findElement(By.xpath(target));
            String value = updatedValue.getText();

            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(new File("a"));
                NodeList accountNumberNode = doc.getElementsByTagName(childNode);
                for (int nodesPresent = 0; nodesPresent < accountNumberNode.getLength(); nodesPresent++) {
                    //Get the staff element by tag name directly
                    Node nodes = doc.getElementsByTagName(childNode).item(nodesPresent);
                    //loop the staff child node
                    NodeList list = nodes.getChildNodes();

                    for (int currentNode = 0; currentNode != list.getLength(); ++currentNode) {
                        Node child = list.item(currentNode);

                        if (child.getNodeName().equals(elementNode)) {

                            child.getFirstChild().setNodeValue(value);
                            System.out.println("The XML node " + elementNode + " has been updated with the value " + value);
                        }

                    }
                }
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult("a");
                transformer.transform(source, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



///////////////////// Config File ////////////////////////////////

// This method uses EnvironmentConfig.xml to pass through data

/////////////////////////////////////////////////////////////////


        public static String returnDataConfigXML(String dataStreamXML, int i) {

            File dataStreamFile = new File("a");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException dataStreamException) {
                dataStreamException.printStackTrace();
            }
            Document doc = null;
            try {
                doc = builder.parse(dataStreamFile);
            } catch (SAXException SAXException) {
                SAXException.printStackTrace();
            } catch (IOException IOException) {
                IOException.printStackTrace();
            }
            //node name should be changed according to xml file, item is index which return first value of xml once found
            String objectXML = doc.getElementsByTagName(dataStreamXML).item(i).getFirstChild().getNodeValue();
            return objectXML;
        }

        //Reload
        public static String returnDataConfigXML(String dataStreamXML) {

            return returnDataConfigXML(dataStreamXML, 0);
        }


////////////////// Return Current Time //////////////////

// This method is used to retrieve the current time

////////////////////////////////////////////////////////

        public static String returnNowTime() {

            SimpleDateFormat timeStamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");//time format
            String getTime = timeStamp.format(new Date());// new Date() to get now time
            return getTime;
        }

////////////// Create Folders to store results and screenshots /////////////

// The below methods are used to create folders and take screenshots

///////////////////////////////////////////////////////////////////////////


        public static String createUniqueFolder(String foldername) {

            String curlocation = System.getProperty("user.dir");
            File dir = new File(curlocation + "/" + foldername + "_" + returnNowTime());
            String folderpath = dir.toString();//convert type from file to string
            if (!dir.exists()) {
                dir.mkdir();//create root folder only
                return folderpath;
            }
            return folderpath;
        }

        public static String createFolders(String foldername, String casename) {

            String curlocation = System.getProperty("user.dir");
            File dir = new File(curlocation + "/Screenshot/" + foldername + "/" + casename);
            String folderpath = dir.toString();//convert type from file to string
            if (!dir.exists()) {
                dir.mkdirs();//create sub folder with mkdirs
                return folderpath;
            }
            return folderpath;
        }

        public static void takeScreenShot(AppiumDriver dr, String location, String name) {

            try {
                File srcFile = ((TakesScreenshot) dr).
                        getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile
                        (srcFile, new File("/" + location + name + returnNowTime() + ".png"));
            } catch (Exception Exception) {
                Exception.printStackTrace();
            }
        }



        ////////////////// Launch and Close Appium Service /////////////////////////

// The below methods are used to launch the Appium service

        ///////////////////////////////////////////////////////////////////////////



        public String osType() {

            String os = System.getProperties().getProperty("os.name");
            String osName = "";
            if (os.contains("Windows")) {
                osName = "Windows";
            } else {
                osName = "Mac";
            }
            return osName;
        }

        public void launchAppiumService() throws IOException, InterruptedException {

            if (osType().equals("Mac")) {
                Runtime.getRuntime().exec("/usr/bin/open -a Terminal " + Utility.returnXML("macServiceLocation") + "");
            } else if (osType().contains("Windows")) {
                String curLocation = System.getProperty("user.dir");
                String service = "/launchWindowsService.bat";
                Desktop.getDesktop().open(new File(curLocation + service));
            }
            Thread.sleep(5000);
        }

        public void closeService() throws IOException, InterruptedException {

            if (osType().equals("Mac")) {
                Runtime.getRuntime().exec("killall -9 Terminal");
            } else if (osType().equals("Windows")) {
                Runtime.getRuntime().exec("taskkill /f /im node.exe");
            }
            Thread.sleep(2000);
        }

    }
