package src.main;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.sql.*;
import java.util.ArrayList;

//This is main class of IndexingManager API. Glue code will interact with this class for
// adding,deleting,updating and searching an index.It has methods for doing these tasks.

public class IndexingManager {
    private static IndexingManager indexManager;
    private static String origkey;
    private static String origvalue;
    private static String origtimer;
    private static int origtotalCopies;
    private static int origcopyNum;
    private static boolean origtimerType;
    private static String origuserId;
    private static int origLayerId;
    private static String origTime;
    private static java.security.cert.Certificate origCerti;
    private Connection conn;
    private Database_Utility utility;
    private IndexingManagerBuffer IMbuffer;

    //This method will check whether table for layerID requested exists or not.Returns true if table exists.


    public boolean checkTable(int layerId)

    {
        boolean isAvailable = false;
        try {

            //This statement will fetch all tables available in database.

            ResultSet rs1 = conn.getMetaData().getTables(null, null, null, null);
            while (rs1.next()) {

                String ld = rs1.getString("TABLE_NAME");

                //This statement will extract digits from table names.

                String intValue = ld.replaceAll("[^0-9]", "");
                int v;
                if (intValue != null) {
                    v = Integer.parseInt(intValue);
                    if (v == layerId) {
                        isAvailable = true;
                    }
                }
                //This statement will compare layerid with digits of table names.

            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isAvailable;
    }

    //This method will check whether index which is to be added is Copy0/Copy1/Copy2.If Copy0 it will return true.

    private boolean checkiforiginal(int copynum) {
        boolean isAvailable = false;
        if (copynum == 0) {
            isAvailable = true;
        }
        return isAvailable;
    }


    // This method is used to add an index entry to database. Glue code will call this method and pass required arguments.

    public void addIndex(String key, String value, String timer, int totalCopies, int copyNum, boolean timerType, String userId, int layerID, String time, java.security.cert.Certificate c) {
        origkey = key;
        origvalue = value;
        origtimer = timer;
        origtotalCopies = totalCopies;
        origcopyNum = copyNum;
        origtimerType = timerType;
        origuserId = userId;
        origLayerId = layerID;
        origTime = time;
        origCerti = c;


        //Verifying Digital Signature of Value using Certificate

        Verif v = new Verif();

        boolean b1 = v.Verify_Digital_Signature(origCerti, origvalue);

        // If signature is verified
        if (b1) {

            // This statement will check whether table exists or not.

            boolean b2 = checkTable(origLayerId);
            if (b2) {

                //If table exists

                boolean b3 = checkiforiginal(copyNum);
                if (!b3) {
                    utility.add_entry(origLayerId, origkey, origvalue, origtimer, origtotalCopies, origcopyNum, origtimerType, origuserId, origTime, origCerti);
                    System.out.println("Entry added");
                } else {

                    String[] s = rootcalc(origkey);
                    File f1 = XMLforRoot(s[0], origkey, origvalue, origLayerId, origcopyNum, origtimer, origtimerType, origuserId, origTime, origCerti);
                    File f2 = XMLforRoot(s[1], origkey, origvalue, origLayerId, origcopyNum, origtimer, origtimerType, origuserId, origTime, origCerti);
                    IMbuffer.addToIMOutputBuffer(f1);
                    IMbuffer.addToIMOutputBuffer(f2);
                }
            } else {

                //If table doesn't exist then create table and add entry.

                utility.createtable(origLayerId);

                boolean b4 = checkiforiginal(copyNum);
                if (!b4) {

                    utility.add_entry(origLayerId, origkey, origvalue, origtimer, origtotalCopies, origcopyNum, origtimerType, origuserId, origTime, origCerti);
                } else {
                    String[] s = rootcalc(origkey);
                    File f1 = XMLforRoot(s[0], origkey, origvalue, origLayerId, origcopyNum, origtimer, origtimerType, origuserId, origTime, origCerti);
                    File f2 = XMLforRoot(s[1], origkey, origvalue, origLayerId, origcopyNum, origtimer, origtimerType, origuserId, origTime, origCerti);
                    IMbuffer.addToIMOutputBuffer(f1);
                    IMbuffer.addToIMOutputBuffer(f2);

                }

            }

        } else {
            System.out.println("Signature not verified");
        }

    }

    //This method is used to calculate root nodes for Copy1 and Copy2,returns an array containing them.

    public String[] rootcalc(String key) {
        String s1 = "Copy1";
        String concat1 = key.concat(s1);
        String hashId1 = null;
        String hashId2 = null;
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(concat1.getBytes());
            byte[] digest = messageDigest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte bytes : digest) {
                hexString.append(String.format("%02x", bytes).toUpperCase());
            }
            hashId1 = hexString.toString();
            System.out.println(hashId1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        String s2 = "Copy2";
        String concat2 = key.concat(s2);
        MessageDigest messageDigest1 = null;
        try {
            messageDigest1 = MessageDigest.getInstance("SHA-1");
            messageDigest1.update(concat2.getBytes());
            byte[] digest1 = messageDigest1.digest();
            StringBuilder hexString1 = new StringBuilder();
            for (byte bytes : digest1) {
                hexString1.append(String.format("%02x", bytes).toUpperCase());
            }
            hashId2 = hexString1.toString();
            System.out.println(hashId2);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        String[] strArray1 = new String[2];
        strArray1[0] = hashId1;
        strArray1[1] = hashId2;
        return strArray1;

    }

    //This method is used to create XML files containing root node,other details for key and add to output buffer for Glue code.

    public File XMLforRoot(String hashid, String key, String value, int LayerId, int copyNum, String timer, boolean timerType, String userid, String Time, Certificate Certi) {
        String xmlFilePath = "C:\\Users\\a\\Pictures\\IndexingManagerAPI\\For Root Node.xml";
        try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("Root_Node_For_Copy_and_Data");
            document.appendChild(root);

            Element hashId = document.createElement("HashID");
            hashId.appendChild(document.createTextNode(hashid));
            root.appendChild(hashId);

            Element layerid = document.createElement("layerid");
            hashId.appendChild(layerid);
            layerid.setAttribute("Id", String.valueOf(LayerId));

            Element key1 = document.createElement("Key");
            hashId.appendChild(key1);
            key1.setAttribute("Key", key);

            Element Value = document.createElement("Value");
            Value.appendChild(document.createTextNode(value));
            hashId.appendChild(Value);

            Element copynum = document.createElement("copynum");
            copynum.appendChild(document.createTextNode(String.valueOf(copyNum)));
            hashId.appendChild(copynum);

            Element timer2 = document.createElement("timer");
            timer2.appendChild(document.createTextNode(String.valueOf(timer)));
            hashId.appendChild(timer2);

            Element timertype = document.createElement("timertype");
            timertype.appendChild(document.createTextNode(String.valueOf(timerType)));
            hashId.appendChild(timertype);

            Element time2 = document.createElement("time");
            time2.appendChild(document.createTextNode(String.valueOf(Time)));
            hashId.appendChild(time2);

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

        File file = new File(xmlFilePath);
        return file;
    }


    // This method is used to delete index entry to database.
   
 /*public void deleteEntry( String Key){

 utility.delete_entry(Key);


  }*/

    // This method is used to update current time for perpetual index entry an index entry.

    public void updateEntry(String Key, int layerID) {

        utility.update_entry(Key, layerID);

    }

    // This method is used to search index entry using key and layerID in database. Also it will put details of object in an output buffer as XML file.

    public File searchEntry(String Key, int layerID) {

        ObjReturn obj = utility.search_entry(Key, layerID);
        boolean b = obj.timerType1;

        if (!b) {
            updateEntry(Key, layerID);
        }
        File f = makeXML(Key, layerID, obj.value1, obj.time1, obj.totalCopies1, obj.copyNum1, obj.timerType1, obj.userId, obj.time);
        IMbuffer.addToIMOutputBuffer(f);
        return f;

    }

    public File makeXML(String key, int layerID, String value1,String time1, int totalCopies1, int copyNum1, boolean timerType1, String userId, String time) {
        String xmlFilePath = "C:\\Users\\a\\Pictures\\IndexingManagerAPI\\Search Result for Key.xml";
        try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element key1 = document.createElement("Search_Result_for_key");
            document.appendChild(key1);
            key1.setAttribute("Key", key);

            // employee element
            Element layerid = document.createElement("layerid");

            key1.appendChild(layerid);
            layerid.setAttribute("Id", String.valueOf(layerID));

            // firstname element
            Element Value = document.createElement("Value");
            Value.appendChild(document.createTextNode(value1));
            layerid.appendChild(Value);

            // second element
            Element timer = document.createElement("timer");
            timer.appendChild(document.createTextNode(String.valueOf(time1)));
            layerid.appendChild(timer);

            // third element
            Element totcopies = document.createElement("totcopies");
            totcopies.appendChild(document.createTextNode(String.valueOf(totalCopies1)));
            layerid.appendChild(totcopies);

            // fourth element
            Element copynum = document.createElement("copynum");
            copynum.appendChild(document.createTextNode(String.valueOf(copyNum1)));
            layerid.appendChild(copynum);

            // fifth element
            Element timertype = document.createElement("timertype");
            timertype.appendChild(document.createTextNode(String.valueOf(timerType1)));
            layerid.appendChild(timertype);

            // sixth element
            Element userid = document.createElement("userid");
            userid.appendChild(document.createTextNode(userId));
            layerid.appendChild(userid);

            // seventh element
            Element time2 = document.createElement("time");
            time2.appendChild(document.createTextNode(String.valueOf(time1)));
            layerid.appendChild(time2);

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

        File file = new File(xmlFilePath);
        return file;
    }


//    This method is used to delete entries which are of type fixed as per timer associated with it.

    public void maintenancethread() {

        Thread maintenanceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread());
                PreparedStatement pst = null;
                int rowid;
                long timer = 0;
                long time = 0;
                String key;


                try {
                    ResultSet rs = conn.getMetaData().getTables(null, null, null, null);

                    while (rs.next()) {

                        String ld = rs.getString("TABLE_NAME");
                        String intValue = ld.replaceAll("[^0-9]", "");
                        int layerid = Integer.parseInt(intValue);
                        String filename = "Table" + layerid;
                        pst = conn.prepareStatement("SELECT * FROM " + filename);
                        ResultSet rs2 = pst.executeQuery();
                        while(rs2.next()) {
                        timer = Long.parseLong(rs2.getString("timer"));
                        time = Long.parseLong(rs2.getString("time"));
                        key=rs2.getString("Key");

                            if (!(timer == 0)) {
                                if (System.currentTimeMillis() - time > timer) {
                                    utility.delete_entry(filename, key);
                                }
                            }
                        }
                        rs2.close();
                    }
                    pst.close();
                    rs.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }); maintenanceThread.start();
    }

//    Constructor function of Main class.

    private IndexingManager() {
        Provider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        utility = Database_Utility.getInstance();
        conn = utility.getConnection();
        IMbuffer = IndexingManagerBuffer.getInstance();

       // This statement is to create purge table. I have kept its layer id as 100.

        boolean k=checkTable(100);

        if(!k) {
            utility.createtable(100);
        }

      //  This statement is to run maintenance thread on loading of class to purge entries whose timer has expired.

      // maintenancethread();

      // This statement will request Routing manager to ascertain keys for which I am root node.

      //  ArrayList<File> AL=resultForIndexingManager();
      //  for(int j=0;j<=AL.size();j++) {
      //      IMbuffer.addToIMOutputBuffer(AL.get(j));
       // }
    }

//  Creating Singleton object of IndexingManager class.

    public static synchronized IndexingManager getInstance() {
        if (indexManager == null) {
            indexManager = new IndexingManager();
            return indexManager;
        } else {
            return indexManager;
        }
    }

    //This method is used to generate xml file for routing manager to ascertain for which nodes I am root or not.After file generation
    // objects of file are made and added to output buffer.

    public ArrayList<File> resultForIndexingManager() {
        PreparedStatement stmt = null;
        int rowlength;
        ArrayList<File> A = null;

        try {
            //This statement will fetch all tables available in database.
            ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
            while (rs.next()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.newDocument();

                String ld = rs.getString("TABLE_NAME");
                System.out.println(ld);

                //This statement will extract digits from table names.

                String intValue = ld.replaceAll("[^0-9]", "");
                int v = Integer.parseInt(intValue);
                System.out.println(v);

                //create root Element

                Element root = doc.createElement("CheckingRootNodeForIndex");
                doc.appendChild(root);

                root.setAttribute("LayerID", intValue);

                int i =1;
                    stmt = conn.prepareStatement("select key from " + ld);
                    ResultSet rs2 = stmt.executeQuery();
                    while (rs2.next()) {
                        Element row1 = doc.createElement("DATA");
                        root.appendChild(row1);
                        row1.setAttribute("INDEX", "[" + i + "]");

                        Element nodeID = doc.createElement("KEY");
                        nodeID.appendChild(doc.createTextNode(rs2.getString("key")));
                        row1.appendChild(nodeID);

                        Element nodePub = doc.createElement("NEXTHOP");
                        nodePub.appendChild(doc.createTextNode(""));
                        row1.appendChild(nodePub);
                        i+=1;
                    }
                    rs2.close();

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(doc);

                StreamResult streamResult = new StreamResult(new File(ld + "_RootNodeCheck" + ".xml"));
                transformer.transform(domSource, streamResult);
                System.out.println("Root Node checking file is generated");

               // File f=new File(ld + "_RootNodeCheck" + ".xml");
               // A.add(f);
            }
            rs.close();


        } catch (TransformerException | SQLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return A;
    }
}
