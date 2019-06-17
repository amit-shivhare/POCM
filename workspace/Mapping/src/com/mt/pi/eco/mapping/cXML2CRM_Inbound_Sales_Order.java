package com.mt.pi.eco.mapping;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import com.sap.aii.af.service.auditlog.Audit;
import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.value.api.XIVMService;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.mw.jco.*;
import com.sap.mw.jco.JCO.Table;



public class cXML2CRM_Inbound_Sales_Order  extends AbstractTransformation{
      
      private String SystemRFCDestination="local";
      
      private MessageKey amk;
      
      private JCoTable table_SAP=null;
      private Table table_aux=null;
      
      
      
      private boolean Error=false;
      
      private String buffer="";
      private String to_read="";
        
    String RFC = "Z_FMRFC_ECO1850_BUYER_PROFILE";
  //RFC LookupMappings
    private String varContactID="";
    private String vareShopCode="";
    private String varProfNr="";
    private String varProtocol="";
    private String varActive=""; 
    private String varSoldTo="";
    private String varShipTo = "";
    private String varBillTo = "";
    private String varPreOrderID = "";
    private String varPreOrderType = "";
    private String varSoldToMatch = "";
    
    //Variables to obtain data from cXML
   
      private String varSharedSecret="";
      private String varAnidIdentity="";
      private String varBuyerID="";
      private String varToDomain="";
      private String varFromDomain="";
      private String varToIdentity="";
      private String varFromIdentity="";
      private String varBillToAddresID="";
      private String varSupplierPartAuxiliaryID="";
      private String varAddressID="";
      private String varMultipleItems=""; 
      
      private boolean varLocalTrace=false;

      
      // For values from ValueMappings
      private String varSalesOrg="";
      private String varDistChannel="";
      
      
      //For testing purpose
      private boolean jumpErrors=false;
      
      private void getSalesOrgANDDistribChannel(){
            
            String aux_SalesOrg=varFromDomain+"/"+varFromIdentity+"/"+varToDomain+"/"+varToIdentity+"/"+varBillToAddresID;
            String aux_DistChannel=varFromDomain+"/"+varFromIdentity+"/"+varToDomain+"/"+varToIdentity+"/"+varBillToAddresID;
            
            varSalesOrg=XIVMService.executeMapping("urn:mt.com:M:ECO:cXML2SalesOrg", "", "cXML", aux_SalesOrg, "urn:mt.com:M:ECO:cXML2SalesOrg", "", "MT_SalesOrg");
            varDistChannel=XIVMService.executeMapping("urn:mt.com:M:ECO:cXML2DistrChannel", "", "cXML", aux_DistChannel, "urn:mt.com:M:ECO:cXML2DistrChannel", "", "MT_DistrChannel");
      }
      
    private void composePayload(OutputStream out) throws StreamTransformationException{
            //addInfo("Function composePayload start");
            
            if(varActive.equals("X")||jumpErrors||SystemRFCDestination.equals("local")){
                  try {
                        
                        String begincontent="<CXML_PATH>";
                        String endcontent="</CXML_PATH>";
                        String beginfieldname="<FIELDNAME>";
                        String endfieldname="</FIELDNAME>";
                        String beginfieldvalue="<FIELDVALUE>";
                        String endfieldvalue="</FIELDVALUE>";
                        
                   
                        buffer="<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns0:Messages xmlns:ns0=\"http://sap.com/xi/XI/SplitAndMerge\"><ns0:Message1>";
                  
                        buffer=buffer+to_read;
                        buffer=buffer+"</ns0:Message1><ns0:Message2>";
                        if(varActive.equals("X")){
                              buffer=buffer+"<ns1:xPathStructure xmlns:ns1=\"urn:mt.com:A:ARIBA:EPROCUREMENT\">";
                        
                              if(varSoldToMatch.equals("X"))
                                    buffer=buffer+begincontent+beginfieldname+"REFERENCE_ID"+endfieldname+beginfieldvalue+varSupplierPartAuxiliaryID+endfieldvalue+endcontent;
                              
                              
                              buffer=buffer+begincontent+beginfieldname+"ACTIVE"+endfieldname+beginfieldvalue+varActive+endfieldvalue+endcontent;
                              buffer=buffer+begincontent+beginfieldname+"CONTACT_PERSON"+endfieldname+beginfieldvalue+varContactID+endfieldvalue+endcontent;
                              buffer=buffer+begincontent+beginfieldname+"ESHOP_CODE"+endfieldname+beginfieldvalue+vareShopCode+endfieldvalue+endcontent;
                              buffer=buffer+begincontent+beginfieldname+"PROFILE_NR"+endfieldname+beginfieldvalue+varProfNr+endfieldvalue+endcontent;
                              buffer=buffer+begincontent+beginfieldname+"PROTOCOL"+endfieldname+beginfieldvalue+varProtocol+endfieldvalue+endcontent;
                              buffer=buffer+begincontent+beginfieldname+"SOLD_TO"+endfieldname+beginfieldvalue+varSoldTo+endfieldvalue+endcontent;
                              buffer=buffer+begincontent+beginfieldname+"SHIP_TO"+endfieldname+beginfieldvalue+varShipTo+endfieldvalue+endcontent;
                              buffer=buffer+begincontent+beginfieldname+"BILL_TO"+endfieldname+beginfieldvalue+varBillTo+endfieldvalue+endcontent;
                              if(!varMultipleItems.equals("X")){
                                    buffer=buffer+begincontent+beginfieldname+"PRE_ORDER_ID"+endfieldname+beginfieldvalue+varPreOrderID+endfieldvalue+endcontent;
                                    buffer=buffer+begincontent+beginfieldname+"PRE_ORDER_TYPE"+endfieldname+beginfieldvalue+varPreOrderType+endfieldvalue+endcontent;
                                    buffer=buffer+begincontent+beginfieldname+"SOLD_TO_MATCH"+endfieldname+beginfieldvalue+varSoldToMatch+endfieldvalue+endcontent;
                              }else{
                                    varPreOrderID="";
                                    varPreOrderType="";
                                    varSoldToMatch="";
                                    buffer=buffer+begincontent+beginfieldname+"PRE_ORDER_ID"+endfieldname+beginfieldvalue+varPreOrderID+endfieldvalue+endcontent;
                                    buffer=buffer+begincontent+beginfieldname+"PRE_ORDER_TYPE"+endfieldname+beginfieldvalue+varPreOrderType+endfieldvalue+endcontent;
                                    buffer=buffer+begincontent+beginfieldname+"SOLD_TO_MATCH"+endfieldname+beginfieldvalue+varSoldToMatch+endfieldvalue+endcontent;
                              }
                              
                              buffer=buffer+"</ns1:xPathStructure>";
                        }
                        buffer=buffer+"</ns0:Message2></ns0:Messages>";
                        
                  
            
                  
                  //    addInfo("Message:"+buffer);
                        out.write(buffer.getBytes());
                        
                  } catch (IOException e) {
                        // TODO Auto-generated catch block
                        addInfo("[ERROR]: In fuction composePayload(OutputStream out): Error writting the Payload");
                        addInfo("[ERROR]:"+ e.getMessage());
                        e.printStackTrace();
                        
                  }
            }else{
                  Error=true;
                  addInfo("[ERROR]: The BuyerProfile configuration does not exist in CRM, the flag ACTIVE is not active in the RFC call");
                  throw new StreamTransformationException("[ERROR]: The BuyerProfile configuration does not exist in CRM, the flag ACTIVE is not active in the RFC call");
            }
            
            
      }    
    


private void getValuesRFC( InputStream in, JCoParameterList outTables){
            
            
            addInfo("Enter in recover RFC values");


      try{
                  //    JCoParameterList outTables = RecoverDataFromRFC(targetSystem,in);

                    System.out.println("Test");
                    varActive = (String)outTables.getValue("ACTIVE");
                    varContactID = (String)outTables.getValue("CONTACT_PERSON");
                    vareShopCode = (String)outTables.getValue("ESHOP_CODE");
                    varProfNr = (String)outTables.getValue("PROFILE_NR");
                    varProtocol = (String)outTables.getValue("PROTOCOL");
                    varSoldTo = (String)outTables.getValue("SOLD_TO");
                    varShipTo = (String)outTables.getValue("SHIP_TO");
                    varBillTo = (String)outTables.getValue("BILL_TO");
                    if(!varMultipleItems.equals("X")){
                          varPreOrderID = (String)outTables.getValue("PRE_ORDER_ID");
                          varPreOrderType = (String)outTables.getValue("PRE_ORDER_TYPE");
                          varSoldToMatch = (String)outTables.getValue("SOLD_TO_MATCH");
                      }else{
                          varPreOrderID = "";
                            varPreOrderType = "";
                            varSoldToMatch = "";      
                      }
                    
                    addInfo("ACTIVE:"+varActive);
                    addInfo("CONTACT_PERSON:"+varContactID);
                    addInfo("ESHOP_CODE:"+vareShopCode);
                    addInfo("PROFILE_NR:"+varProfNr);
                    addInfo("PROTOCOL:"+varProtocol);
                    addInfo("SOLD_TO:"+varSoldTo);
                    addInfo("SHIP_TO:"+varShipTo);
                    addInfo("BILL_TO:"+varBillTo);
                    addInfo("PRE_ORDER_ID:"+varPreOrderID);
                    addInfo("PRE_ORDER_TYPE:"+varPreOrderType);
                    addInfo("SOLD_TO_MATCH:"+varSoldToMatch);
                    
                  
                    if(varSoldTo.equals("")&&!SystemRFCDestination.equals("local")){
                        Error=true;
                        throw new StreamTransformationException("[ERROR] No Sold To party found during Buyer profile determination");
                    }
                  
            }catch(Exception e){
                  Error=true;
                  addInfo("[ERROR]: In fuction callRFC(InputStream in): Error recovering the values from the RFC");
                  addInfo("[ERROR]:"+ e.getMessage());
                  e.printStackTrace();
      
            }


            // Only need for local connection
            
            
      }

      private void getValuesLocalRFC( InputStream in, JCO.ParameterList outTables){
            
            
            


            try{
                        //    JCoParameterList outTables = RecoverDataFromRFC(targetSystem,in);

                    varActive = (String)outTables.getValue("ACTIVE");
                    varContactID = (String)outTables.getValue("CONTACT_PERSON");
                    vareShopCode = (String)outTables.getValue("ESHOP_CODE");
                    varProfNr = (String)outTables.getValue("PROFILE_NR");
                    varProtocol = (String)outTables.getValue("PROTOCOL");
                    varSoldTo = (String)outTables.getValue("SOLD_TO");
                    varShipTo = (String)outTables.getValue("SHIP_TO");
                    varBillTo = (String)outTables.getValue("BILL_TO");
                    if(!varMultipleItems.equals("X")){
                        varPreOrderID = (String)outTables.getValue("PRE_ORDER_ID");
                        varPreOrderType = (String)outTables.getValue("PRE_ORDER_TYPE");
                        varSoldToMatch = (String)outTables.getValue("SOLD_TO_MATCH");
                    }else{
                        varPreOrderID = "";
                        varPreOrderType = "";
                        varSoldToMatch = "";    
                    }
                    
                    System.out.println("ACTIVE:"+varActive);
                    System.out.println("CONTACT_PERSON:"+varContactID);
                    System.out.println("ESHOP_CODE:"+vareShopCode);
                    System.out.println("PROFILE_NR:"+varProfNr);
                    System.out.println("PROTOCOL:"+varProtocol);
                    System.out.println("SOLD_TO:"+varSoldTo);
                    System.out.println("SHIP_TO:"+varShipTo);
                    System.out.println("BILL_TO:"+varBillTo);
                    System.out.println("PRE_ORDER_ID:"+varPreOrderID);
                    System.out.println("PRE_ORDER_TYPE:"+varPreOrderType);
                    System.out.println("SOLD_TO_MATCH:"+varSoldToMatch);
                    
                  
                    if(varSoldTo.equals("")&& !SystemRFCDestination.equals("local")){
                        Error=true;
                        throw new StreamTransformationException("[ERROR] No Sold To party found during Buyer profile determination");
                    }
                        
                  }catch(Exception e){
                        Error=true;
                        addInfo("[ERROR]: In fuction callRFC(InputStream in): Error recovering the values from the RFC");
                        addInfo("[ERROR]:"+ e.getMessage());
                        e.printStackTrace();
            
                  }


                  // Only need for local connection
                  
                  
            }
      
    private void RecoverDataFromRFC (String targetSystem, InputStream in) throws JCoException, StreamTransformationException{
      
      //String paramS = in.getInputParameters().getString("System");
      
      JCoFunction function=null;
      

            
            
            
            //openxPath= "</Message>";
            //out.write(openxPath.getBytes());
            
            
            
      if(targetSystem.equals("local")){
            /*String CLIENT = "200";
                  String USERID = "RFC4DI3_01";
                  String PASSWORD = "init1122";
                  String LANGUAGE = "EN";
                  String HOSTNAME = "CH00VDC2S.EU.MT.MTNET";
                  String SYSTEMNUMBER = "00";*/
                  
                  String CLIENT = "600";
                  String USERID = "RFC4OI3_01";
                  String PASSWORD = "init1234"; 
                  String LANGUAGE = "EN";
                  String HOSTNAME = "CH00SOC1A.EU.MT.MTNET";//Message server for QC2 = CH00VQC2S.EU.MT.MTNET
                  //String GROUP = "OC1";
                  //String MSHOST = "CH00VOC1S.EU.MT.MTNET";//ch00voc1s.eu.mt.mtnet
                  String SYSTEMNUMBER = "02";//for QC2 00
                  
                  System.out.println("Enter in the local function");
                  
                  JCO.Function function_local=null;
                  JCO.Repository mRepository=null;
                  
                  JCO.Client mConnection = JCO.createClient(CLIENT, // SAP client

                        USERID, // User ID
                        PASSWORD, // Password
                        LANGUAGE, // Language
                        HOSTNAME, // Host name
                        //MSHOST, //
                        //GROUP,
                        SYSTEMNUMBER); // System number
                  
                  // connect to SAP
                  mConnection.connect();
                  // create repository
                  mRepository = new JCO.Repository( "SAPLookupToo", mConnection);
                  
                  // Create function
                  IFunctionTemplate ft = mRepository.getFunctionTemplate( RFC );
                  

                  if (ft != null)
     
                         function_local =  ft.getFunction();
                  
            //    JCoParameterList input = (JCoParameterList) function_local.getImportParameterList();
                  JCO.ParameterList input = function_local.getImportParameterList();
                  //Structure structure_aux=input.getStructure(0);
                  table_aux=(Table) input.getTable("CXML_PATH");
                  
                  convert_cXML2Xpath(in);
                  validateSharedSecret();
                  // Pass IMPORT function parameters
                  input.setValue( table_aux, "CXML_PATH" );
                  input.setValue(varSupplierPartAuxiliaryID, "REFERENCE_ID");
            
      
            
                  mConnection.execute( function_local );

                  //function=(JCoFunction) function_local;
                  JCO.ParameterList outTables = null;
                  outTables = function_local.getExportParameterList();
                  getValuesLocalRFC(in,outTables);
                  
                  /*  SECOND RFC CALL
                  ft = mRepository.getFunctionTemplate( "ZFECO1615_PRE_ORDER_CHECK" );
                  

                  if (ft != null)
     
                         function_local =  ft.getFunction();
                  
            //    JCoParameterList input = (JCoParameterList) function_local.getImportParameterList();
                  input = function_local.getImportParameterList();
                  //Structure structure_aux=input.getStructure(0);
                  input.setValue(varSupplierPartAuxiliaryID, "REFERENCE_ID");
                  input.setValue(varSupplierPartAuxiliaryID, "SOLD_TO_ID");
                  */
                  
                  mConnection.disconnect();
                  
            //    return (Table)outTables;
      }else{
             JCoDestination destination=null;
            
            if(targetSystem.equals("SC1CLNT200")){
                  destination = JCoDestinationManager.getDestination("SC1CLNT200_RFC");
            }
            if(targetSystem.equals("DC2CLNT200")){
                  destination = JCoDestinationManager.getDestination("XI_IDOC_DEFAULT_DESTINATION_DC2");
            }
            if(targetSystem.equals("DC1CLNT200")){
                  destination = JCoDestinationManager.getDestination("XI_IDOC_DEFAULT_DESTINATION_DC1");
            }
            if(targetSystem.equals("OC1CLNT100")){
                  destination = JCoDestinationManager.getDestination("XI_IDOC_DEFAULT_DESTINATION_OC1");
            }
            if(targetSystem.equals("OC1CLNT600")){
                  destination = JCoDestinationManager.getDestination("XI_IDOC_DEFAULT_DESTINATION_OC1_600");
            }
            if(targetSystem.equals("OC2CLNT200")){
                  destination = JCoDestinationManager.getDestination("XI_IDOC_DEFAULT_DESTINATION_OC2");
            }
            if(targetSystem.equals("OC3CLNT100")){
                  destination = JCoDestinationManager.getDestination("XI_IDOC_DEFAULT_DESTINATION_OC3");
            }
            if(targetSystem.equals("OC4CLNT100")){
                  destination = JCoDestinationManager.getDestination("XI_IDOC_DEFAULT_DESTINATION_OC4");
            }
            if(targetSystem.equals("QC2CLNT100")){
                  destination = JCoDestinationManager.getDestination("XI_IDOC_DEFAULT_DESTINATION_QC2");
            }
            if(targetSystem.equals("PC1CLNT100")){
                  destination = JCoDestinationManager.getDestination("XI_IDOC_DEFAULT_DESTINATION_PC1");
            }

            if (destination==null){
                  destination = JCoDestinationManager.getDestination("XI_IDOC_DEFAULT_DESTINATION_"+targetSystem.substring(1, 3));
            }
            //TODO: ADD more systems!!!
            try{
                  AbstractTrace t=null;
                        t=getTrace();
                  t.addWarning("destination.getAttributes(): "+destination.getAttributes());
                  
                  //JCoDestination destination = JCoDestinationManager.getDestination("SC1CLNT200_RFC");
                  function = destination.getRepository().getFunction(RFC);
                  t.addWarning("Function Name: "+function.getName());
                  t.addWarning("Table number of colums: "+function.getImportParameterList().getTable("CXML_PATH").getNumColumns());
                  JCoParameterList input =  function.getImportParameterList();
                        
                        //Structure structure_aux=input.getStructure(0);
                        table_SAP= input.getTable("CXML_PATH");
                        convert_cXML2Xpath(in);
                        validateSharedSecret();
                        addInfo("Table N of records"+table_SAP.getNumRows());
                        // Pass IMPORT function parameters
                        input.setValue( "CXML_PATH", table_SAP );
                        input.setValue("REFERENCE_ID",varSupplierPartAuxiliaryID);
                      function.execute(destination);
                      
                      JCoParameterList outTables = null;
                        outTables = function.getExportParameterList();
                        
                        addInfo("Table Active"+outTables.getValue("ACTIVE"));
                        
                        getValuesRFC(in, outTables);
                        
                  
                        
                        addInfo("Function call RFC end");
                        //return (Table) outTables;
            }catch (Exception e){
                  Error=true;
                  addInfo("[ERROR]: In fuction  RecoverDataFromRFC (String targetSystem, InputStream in): During connection RFC");
                        addInfo("[ERROR]:"+ e.getMessage());
                        e.printStackTrace();
            }
            
            //JCoRepository mRepository=destination.getRepository();
            
          //      JCoFunctionTemplate ft = mRepository.getFunctionTemplate(RFC);
            
         //       function = mRepository.getFunction(RFC);
            
            
      }
    }
    
      
private void convert_cXML2Xpath (InputStream in){
      
      
    try {
    
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      
      //read the first line to avoid to populate the 
      
      
            byte[] buffer_aux = new byte[1024];
            int len;
      
            while ((len = in.read(buffer_aux)) > -1 ) 
                  baos.write(buffer_aux, 0, len);
            
            baos.flush();

            
            // Open new InputStreams using the recorded bytes
            InputStream is1 = new ByteArrayInputStream(baos.toByteArray()); //this is for the message
            InputStream is2 = new ByteArrayInputStream(baos.toByteArray()); //this is for the Parser to generate the xPath
            
      try{  
            BufferedReader br = new BufferedReader(new InputStreamReader(is1));
            
            
            String line;
            StringBuilder aux_buffer = new StringBuilder();
            boolean insert=false;
            while ((line = br.readLine()) != null) {
                  if(line.startsWith("<cXML"))
                        insert=true;
                  if(insert)
                        aux_buffer.append(line);
            }
            
            to_read=aux_buffer.toString();      
            
      }catch(Exception e){
            Error=true;
            addInfo("[ERROR]: During Reading Payload from InputStream  RecoverDataFromRFC (String targetSystem, InputStream in): During connection RFC");
            addInfo("[ERROR]:"+ e.getMessage());
            e.printStackTrace();
      
      }     
      
      DefaultHandler handler = new DefaultHandler() {


      
      String path[]=new String[1000];
      int pathcounter=0;

      
      boolean OrderID=false;
      boolean Identity=false;
      boolean SharedSecret=false;
      boolean SupplierPartAuxiliaryID=false;

      String content="";
      
      
      
      public void startElement(String uri, String localName,String qName, 
                Attributes attributes) throws SAXException {

      
            
            try {
                  
                  content=""; //initiate the content buffer
                  
                  String contentLabel = new String(qName);
            
                  if (contentLabel.trim().length() >0 & !contentLabel.equals("cXML")){
                        
                        // Section to do direct mapping of FIELDS
                        if(contentLabel.equals("SupplierPartAuxiliaryID"))
                              OrderID=true;
                        if(contentLabel.equals("Identity"))
                              Identity=true;
                        if(contentLabel.equals("SharedSecret"))
                              SharedSecret=true;
                        if(contentLabel.equals("SupplierPartAuxiliaryID"))
                              SupplierPartAuxiliaryID=true;
                        
                        
                        if (attributes.getQName(0)!= null){
                              
                              path[pathcounter]=new String(contentLabel);
                              pathcounter++;
                              for (int i=0;i<attributes.getLength();i++){     
                                    path[pathcounter]="[@"+attributes.getQName(i)+"='"+attributes.getValue(attributes.getQName(i))+"']";
                                    pathcounter++;
                        
                                    String auxPath="";
                                    for(int aux_count=0;aux_count<pathcounter;aux_count++)
                                          auxPath=auxPath+path[aux_count];                
                                    
                                    // Section to do direct mapping of ATTRIBUTES
                                    if(pathcounter>2){
                                    if(attributes.getQName(i).equals("orderID")&& auxPath.contains("Request")&& auxPath.contains("OrderRequest")&& auxPath.contains("OrderRequestHeader"))
                                          varBuyerID=attributes.getValue(attributes.getQName(i));
                                          varBuyerID=varBuyerID.replace("|", "\\");
                                    }
                                    
                                    
                                    if(attributes.getQName(i).equals("domain")){
                                          if (pathcounter>2){
                                                if(auxPath.contains("Header")&& auxPath.contains("To")&& auxPath.contains("Credential"))
                                                      varToDomain=attributes.getValue(attributes.getQName(i));
                                                if(auxPath.contains("Header")&& auxPath.contains("From")&& auxPath.contains("Credential"))
                                                      varFromDomain=attributes.getValue(attributes.getQName(i));
                                    
                                          }
                                    }
                                    
                                    
                                    
                              }
                              
                              
                        }
                        else{
                                    
                                    path[pathcounter]=new String(contentLabel);
                                    pathcounter++;
                              
                              
                        
                        }
                  }
            } catch (Throwable e) {
                  // TODO Auto-generated catch block
                  addInfo("[ERROR]: In fuction startElement(String uri, String localName,String qName,Attributes attributes) : Error during Parsing the message.");
                  addInfo("[ERROR]:"+ e.getMessage());
                  e.printStackTrace();
            }
            
            




      }

      public void endElement(String uri, String localName,String qName) throws SAXException {

                                    
            try {
                  

                  String pathcontent="";
                  String begincontent="<CXML_PATH>";
                  String endcontent="</CXML_PATH>";
                  String beginfieldname="<FIELDNAME>";
                  String endfieldname="</FIELDNAME>";
                  String beginfieldvalue="<FIELDVALUE>";
                  String endfieldvalue="</FIELDVALUE>";
                  
                  if (content.trim().length() >0){    
                        
                        
                      for (int i=0;i<pathcounter;i++){
                        pathcontent=pathcontent+path[i];
                        if(pathcounter-1>i){
                              pathcontent=pathcontent+"/";
                        }
                      }
                      
                      buffer=buffer+begincontent+beginfieldname+pathcontent+endfieldname+beginfieldvalue+content+endfieldvalue+endcontent;
                     
                      // Storing the values for the fields that are needed to do the RFC calls
                      
      
                        if(Identity==true && pathcontent.contains("Header") && pathcontent.contains("To") && pathcontent.contains("Credential") ){
                              varToIdentity=content;
                              Identity=false;
                        }
                        if(Identity==true && pathcontent.contains("Header") && pathcontent.contains("From") && pathcontent.contains("Credential") ){
                              varFromIdentity=content;
                              Identity=false;
                        }
                        if(Identity==true && pathcontent.contains("Header") && pathcontent.contains("Sender") && pathcontent.contains("Credential") ){
                              varAnidIdentity=content;
                              Identity=false;
                        }
                        if(SharedSecret==true && pathcontent.contains("Header") && pathcontent.contains("Sender") && pathcontent.contains("Credential") ){
                              varSharedSecret=content;
                              SharedSecret=false;
                        }
                        if(SupplierPartAuxiliaryID==true && pathcontent.contains("Request") && pathcontent.contains("OrderRequest") && pathcontent.contains("ItemOut") 
                                    && pathcontent.contains("ItemID")&& pathcontent.contains("SupplierPartAuxiliaryID")){
                              
                              //check if we have multiple items with different reference if yes do not update the order - create the new one. 
                              if(!varSupplierPartAuxiliaryID.equals("")){
                                    if(varMultipleItems.equals("")){
                                          String tempContent = content.substring(0, Math.min(10, content.length()));
                                          String tempSupplierPartAuxiliaryID = varSupplierPartAuxiliaryID.substring(0, Math.min(10, varSupplierPartAuxiliaryID.length()));
                                          if(!tempContent.equals(tempSupplierPartAuxiliaryID)){
                                                varMultipleItems="X";// multiple items coming from different pre orders found 
                                          }
                                    }
                              }else{
                                    varSupplierPartAuxiliaryID=content;
                                    SupplierPartAuxiliaryID=false;
                              }
                              
                        }
                        //EXCEPTION FOR ASTRAZENCE AND MEDIMMUNE
                        //AstraZeneca and Medimmune are sending the same credentials but are authenticated with different shared secret. 
                        //The difference is Address ID on Bill To level in the message body of cXML 
                        //if identity in the message contain AN01006719155 additional check needs to be performed and 
                        //Corresponding Identity needs to be assigned in this case: ASTRAZENECA or MEDIMMUNE 
                        
                        if(pathcontent.contains("Request")&& pathcontent.contains("OrderRequest") && pathcontent.contains("OrderRequestHeader") && pathcontent.contains("BillTo")  && pathcontent.contains("addressID='4314'") ){
                              varFromIdentity="ASTRAZENECA";
                        }
                        if(pathcontent.contains("Request")&& pathcontent.contains("OrderRequest") && pathcontent.contains("OrderRequestHeader") && pathcontent.contains("BillTo")  && pathcontent.contains("addressID='4315'") ){
                              varFromIdentity="ASTRAZENECA";
                        }
                        if(pathcontent.contains("Request")&& pathcontent.contains("OrderRequest") && pathcontent.contains("OrderRequestHeader") && pathcontent.contains("BillTo")  && pathcontent.contains("addressID='5351'") ){
                              varFromIdentity="MEDIMMUNE";
                        }
                        if(pathcontent.contains("Request")&& pathcontent.contains("OrderRequest") && pathcontent.contains("OrderRequestHeader") && pathcontent.contains("BillTo")  && pathcontent.contains("addressID='5352'") ){
                              varFromIdentity="MEDIMMUNE";
                        }
                              
                        
                      //addInfo("For:"+pathcontent+" content:"+content);
                      
                      if( SystemRFCDestination.equals("local")){
                        table_aux.appendRow();
                        table_aux.setValue(pathcontent, "FIELDNAME");
                        table_aux.setValue(content, "FIELDVALUE");
                      }else{
                        table_SAP.appendRow();
                        table_SAP.setValue("FIELDNAME",pathcontent);
                        table_SAP.setValue("FIELDVALUE",content);
                      }
                        
                        
            }
                  String contentLabel = new String(qName);
                  if (contentLabel.trim().length() >0 & !contentLabel.equals("cXML")){
      
                        
                              while(path[pathcounter-1].startsWith("[@")){                                        
                                    pathcounter--;
                              }
                              if(contentLabel.equals(path[pathcounter-1])){
                                    pathcounter--;
                              }
                                          
                  }
                  content="";             
                  
            } catch (Throwable e) {
                  // TODO Auto-generated catch block
                  Error=true;
                  addInfo("[ERROR]: In fuction endElement(String uri, String localName,String qName) : Error during Parsing the message.");
                  addInfo("[ERROR]:"+ e.getMessage());
                  e.printStackTrace();
            }

      }

      public void characters(char ch[], int start, int length) throws SAXException {

            try {             
                  String strResultline=new String(ch, start, length);
                  strResultline = strResultline.replaceAll("&", "&amp;");
                  strResultline = strResultline.replaceAll("<" , "&lt;");
                  strResultline = strResultline.replaceAll(">" , "&gt;");
                  strResultline = strResultline.replaceAll("\"", "&quot;");  //Changed, the previous one was \"
                  strResultline = strResultline.replaceAll("'", "&#39;"); //Changed, the previous one was \"
                  
                  content = content+ strResultline;
                  
            } catch (Throwable e) {
                  // TODO Auto-generated catch block
                  Error=true;
                  addInfo("[ERROR]: In fuction characters(char ch[], int start, int length) : Error during conversion of characters");
                  addInfo("[ERROR]:"+ e.getMessage());
                  e.printStackTrace();
            }
            
            

      }
   
     };
    
     saxParser.parse(is2, handler);

     
       
     } catch (Throwable e) {
       addInfo("[ERROR]: In fuction convert_cXML2Xpath (InputStream in): Unknown error, call an expert");
            addInfo("[ERROR]:"+ e.getMessage());
            e.printStackTrace();
     }
    // return table_aux;
}

private void validateSharedSecret() throws StreamTransformationException{
    
      if(!SystemRFCDestination.equals("local")){
            if(varSharedSecret.equals("")){
                  Error=true;
                  throw new StreamTransformationException("[ERROR] No SharedSecret in the Source payload message ");
            }
            
            //EXCEPTION FOR IOWA STATE UNIVERCITY - two different SharedSecrets needs to be checked. 
            //SharedSecret for Punchout and SharedSecret for Order is different. 
            //Interface will check SharedSecret for Order with constant Identity REGENERON_ORDER when Identity = 194873139 for this interface. 
            
            if(varFromIdentity.equals("IOWASTATEUNIV")){
                  varFromIdentity = "IOWA_ORDER";
            }
            // END
            
            //EXCEPTION FOR REGENERON - two different SharedSecrets needs to be checked. 
            //SharedSecret for Punchout and SharedSecret for Order is different. 
            //Interface will check SharedSecret for Order with constant Identity REGENERON_ORDER when Identity = 194873139 for this interface. 
            
            if(varFromIdentity.equals("194873139")){
                  varFromIdentity = "REGENERON_ORDER";
            }
            // END
                        
            String senderSecret_aux=XIVMService.executeMapping("http://mt.com/pi/bo/lc/ch/VMR/cXMLSharedSecret", "", "cXML_Identity", varFromIdentity, "http://mt.com/pi/bo/lc/ch/VMR/cXMLSharedSecret", "", "cXML_SharedSecret");
            
            if(senderSecret_aux==null || senderSecret_aux.equals("")){
                  Error=true;
                  throw new StreamTransformationException("[ERROR] No SharedSecret in the Value Mapping for: "+varFromIdentity);
            }
            if(!senderSecret_aux.equals(varSharedSecret)){
                  Error=true;
                  throw new StreamTransformationException("[Error] The SharedSecret"+varSharedSecret+" in the VM ("+ varFromIdentity+","+senderSecret_aux+") is not equal to the received one "+varSharedSecret);
            }
      }
}

private void addInfo (String msg){
    if(!varLocalTrace){
            AbstractTrace t=null;
                  t=getTrace();
            t.addWarning(msg);
    }
    
     if (amk != null && !Error){

       Audit.addAuditLogEntry(amk, AuditLogStatus.SUCCESS,msg);

     }

     else{

       System.out.println(msg);

     }

}


public void execute(TransformationInput in, final OutputStream out)
throws StreamTransformationException, JCoException {
            try{
         // callRFC(in.getInputPayload().getInputStream(),RecoverDataFromRFC(in.getInputParameters().getString("System"),in.getInputPayload().getInputStream()));
            System.out.println("--------------------");
            addInfo("--------------------");
          SystemRFCDestination=in.getInputParameters().getString("SystemRFCDestination");
        //  SenderInterface=in.getInputHeader().getSenderService();
          System.out.println("RFC System:"+SystemRFCDestination);
          RecoverDataFromRFC(in.getInputParameters().getString("SystemRFCDestination"), in.getInputPayload().getInputStream());
          if(!Error)
            composePayload(out);
            //step1Connect();
//      AbstractTrace t=null;
//      t=getTrace();
//      t.addWarning("Inicio");
//      System.out.println("Inicio:");
//      try{
//              JCoDestination destination = JCoDestinationManager.getDestination("SC1CLNT200_RFC");
//              t.addWarning("destination.getAttributes(): "+destination.getAttributes());
//            System.out.println("Attributes:");
//            System.out.println(destination.getAttributes());
//            System.out.println();
  }catch (Exception e) {
            // TODO: handle exception
            // addWarning("Error en la Destination"+e.getMessage());
            e.printStackTrace();
      }
        
       
      //JCoDestination destination = JCoDestinationManager.getDestination("SAPSLDAPI_SC3FAKE");
         
         }

@Override
public void transform(TransformationInput arg0, TransformationOutput arg1)
            throws StreamTransformationException {
      // TODO Auto-generated method stub
      try {
            System.out.println(arg0.getInputPayload().toString());
            System.out.println("--------------------");
            addInfo("--------------------");
            this.execute(arg0,arg1.getOutputPayload().getOutputStream());
      } catch (JCoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
      }
      
}

}

