package src.Testing;

import src.com.ehelpy.brihaspati4.IndexingManagerAPI.IndexingManager;

public class Testing {


    public static void main(String argv[]) {


       IndexingManager IM= IndexingManager.getInstance();








       //Following code will check if table of a layerid exists or not
        // {
        /*boolean b=IM.checkTable(5);
        System.out.println(b);*/
        // }

        //Following block will test adding of an entry.

        // { Following Code generates a Demo certificate for addition as part of an Index entry.

        /* Cryptography S2 = Cryptography.getInstance();
        KeyStore k = S2.getKeyStore();
        Certificate c1 = null;
        try {
            c1 =  k.getCertificate("Certificate");}
        catch (KeyStoreException e) {
            e.printStackTrace();
        }*/

        //IM.addIndex("99267C42DE64C0904","peer","5000",2,1,false,"nks2",0,"1621911095568" ,c1);
        //IM.addIndex("DD2051C7A9CD59A1BE822F699267C42DE64C0904","network","4000",1,0,false,"harry1",0,"1621911095568",c1);
        //IM.addIndex("FD2051C7A9CD59A1BE822F699267C42DE64C0904","manager","3000",1,2,true,"sindhu",0,"1621911095568",c1);

        //  }


        //Following code will calculate root for Key whose copy number is 0.This is done using hashing.

       /* String[] test = new String[2];
        test=IM.rootcalc("key");
        System.out.println(test[0]);
        System.out.println(test[1]);*/

          /*Database_Utility u=Database_Utility.getInstance();
          ObjReturn obj=new ObjReturn();
          obj=u.search_entry("DD2051C7A9CD59A1BE822F699267C42DE64C0904",0);
          System.out.println(obj.getValue1());*/



        // Following block will test searching of an entry in XML format.
        //  {

        // Error - If not in main table

        /*File f=IM.searchIndex("FD2051C7A9CD59A1BE822F699267C42DE64C0904",2);
        System.out.println(f.getName());
*/
        //  }

        // Following block will test updation of an entry.
        //  {

        //IM.updateIndex("DD2051C7A9CD59A1BE822F699267C42DE64C0904",0);

        //}

        // Following block will test deletion of an entry.
        //  {

        //IM.deleteIndex("DD2051C7A9CD59A1BE822F699267C42DE64C0904",0);

        //}

        //Following thread will request Routing manager every 30 minutes to ascertain for which self is root or not.
        // {


         //IM.queryForRoutingManager();

        //  }

        //{

        //This block will check transfer of entry to purge table

            /*String filepath="ResponseToIndexM.xml";
            IM.transfertopurge(new File(filepath));*/

        //Following thread will delete entries whose timer has expired.
        // {

        //IM.maintenancethread();

        //  }

        //Following thread will delete entries from purge table every 90 minutes.
        // {

        // IM.maintenancethread2();

        //  }

        //Following method will make userid to certificate mapping.If userid is already present it will show an error that userid exists.
        // {

        // IM.userToCertMap("new",c1);

        //  }

        // Following code will fetch Certificate for userid

       /* Certificate c=IM.fetchuserCerti("harry1");
        System.out.println(c.getPublicKey());*/



    }


    }
































































