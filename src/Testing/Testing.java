package src.Testing;

import jdk.internal.org.xml.sax.SAXException;
import org.w3c.dom.*;
import src.main.Database_Utility;
import src.main.IndexingManager;
import src.main.Cryptography;
import src.main.ObjReturn;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;

public class Testing {


    public static void main(String argv[]) {

        //Following block will test adding of an entry.

        // {

       IndexingManager IM= IndexingManager.getInstance();
        /* Cryptography S2 = Cryptography.getInstance();
       KeyStore k = S2.getKeyStore();
       Certificate c1 = null;
       try {
           c1 =  k.getCertificate("Certificate");}
        catch (KeyStoreException e) {
           e.printStackTrace();
        }*/

       // IM.addIndex("FD2051C7A9CD59A1BE822F699267C42DE64C0904","hoooo","3000",2,1,false,"new1",1,"1621911095568",c1);
       // IM.addIndex("DD2051C7A9CD59A1BE822F699267C42DE64C0904","hoooo","3000",1,2,false,"harry",1,"1621911095568",c1);

        //  }

       /* Database_Utility u=Database_Utility.getInstance();
        ObjReturn obj=new ObjReturn();
        obj=u.search_entry("3051C7A9CD59A1BE822F699267C42DE64C0904",1);
        System.out.println(obj.getValue1());*/



        // Following block will test searching of an entry.
        //  {

      /*  File f=IM.searchIndex("3051C7A9CD59A1BE822F699267C42DE64C0904",1);
        System.out.println(f.getName());*/
        //  }

        // Following block will test updation of an entry.
        //  {

        //IM.updateIndex("3051C7A9CD59A1BE822F699267C42DE64C0904",1);

        //}

        // Following block will test deletion of an entry.
        //  {

        //IM.deleteIndex("hiii",2);

        //}


        //This block will check transfer of entry to purge table.

        //{



        /* String filepath="ResponseToIndexM.xml";
       IM.transfertopurge(new File(filepath));
*/
        // }

        /*Certificate c=IM.fetchuserCerti("new1");
        System.out.println(c.getPublicKey());
*/

//       File f=IM.XMLforRoot(s[0],"HIII","helooo",2,2, (long) 34567890,true,"hooooo",System.currentTimeMillis(),c1);

//IM.resultForIndexingManager();
        //System.out.println(Thread.currentThread()+"This is main");
        //Database_Utility utility=Database_Utility.getInstance();

        //String filepath="Table1_RootNodeCheck.xml";
        //File f=responseForIndexingManager(filepath);
    }


    }
































































