package com.mt.pi.hybris.mapping;

import java.io.IOException;
import java.io.InputStream;
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
import com.sap.conn.jco.*;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.mw.jco.*;
import com.sap.mw.jco.JCO.Table;

public class PunchOut_cXML2Hybris  extends AbstractTransformation{
          String DC2CLNT200 = "DC2CLNT200";
          String SC1CLNT200 = "SC1CLNT200";
          
          String SystemRFCDestination="";
          String SenderInterface="";
          
          String RFC = "Z_FMRFC_ECO1850_BUYER_PROFILE";
          
          String ABAP_MS = "ABAP_MS_WITHOUT_POOL";

          // For the direct mappings
          private String varBuyerCookie="";
          private String varOperation="";
          private String varOrderID="";
          private String varDunsDomain="";
          private String varDunsIdentity="";
          private String varAnidDomain="";
          private String varAnidIdentity="";
          private String varUserAgent="";
          private String varSupplierPartID="";
          private String varReturnURL="";
          private String varSharedSecret="";
          private String varCompanyCodeExtrinsic="";
         
          //RFC LookupMappings
          private String varContactID="";
          private String vareShopCode="";
          private String varProfNr="";
          private String varProtocol="";
          private String varActive="";

          private boolean Error=false;
           
          private MessageKey amk;
          
          private JCoTable table_SAP=null;
          private Table table_aux=null;
          
          
          /**
           * This example demonstrates the destination concept introduced with JCO 3.
           * The application does not deal with single connections anymore. Instead
           * it works with logical destinations like ABAP_AS and ABAP_MS which separates
           * the application logic from technical configuration.     
           * @throws JCoException
           */
          public void step1Connect() throws JCoException
          {
              JCoDestination destination = JCoDestinationManager.getDestination(SC1CLNT200);
                       
              
              System.out.println("Attributes:");
              System.out.println(destination.getAttributes());
              System.out.println();

              destination = JCoDestinationManager.getDestination(ABAP_MS);
              System.out.println("Attributes:");
              System.out.println(destination.getAttributes());
              System.out.println();
              
          }
          
          private void addInfo (String msg){
           if(!SystemRFCDestination.equals("local")){
                  AbstractTrace t=null;
                        t=getTrace();
                  t.addWarning(msg);
           }
              if (amk != null){

                Audit.addAuditLogEntry(amk, AuditLogStatus.SUCCESS,msg);

              }

              else{

                System.out.println(msg); 

              }

          }
          
          private void convert_cXML2Xpath (InputStream in){
            
            
                try {
                
                  SAXParserFactory factory = SAXParserFactory.newInstance();
                  SAXParser saxParser = factory.newSAXParser();
                  
                  
                  
                  DefaultHandler handler = new DefaultHandler() {
            
      
                  
                  String path[]=new String[1000];
                  int pathcounter=0;
            
                  
                  boolean buyerCookie=false;
                  boolean URL=false;
                  boolean OrderID=false;
                  boolean Identity=false;
                  boolean UserAgent=false;
                  boolean SharedSecret=false;
                  //boolean Extrinsic=false;
                  boolean SupplierPartID=false;
                  String content="";
                  
                  
                  
                  public void startElement(String uri, String localName,String qName, 
                            Attributes attributes) throws SAXException {
            
                  
                        
                        try {
                              
                              content=""; //initiate the content buffer
                              
                              String contentLabel = new String(qName);
                        
                              if (contentLabel.trim().length() >0 & !contentLabel.equals("cXML")){
                                    
                                    // Section to do direct mapping of FIELDS
                                    if(contentLabel.equals("URL"))
                                          URL=true;
                                    if(contentLabel.equals("SupplierPartAuxiliaryID"))
                                          OrderID=true;
                                    if(contentLabel.equals("Identity"))
                                          Identity=true;
                                    if(contentLabel.equals("UserAgent"))
                                          UserAgent=true;
                                    if(contentLabel.equals("BuyerCookie"))
                                          buyerCookie=true;
                                    if(contentLabel.equals("SharedSecret"))
                                        SharedSecret=true;
                                    if(contentLabel.equals("SupplierPartID"))
                                        SupplierPartID=true;
//                                  if(contentLabel.equals("Extrinsic"))
//                                        Extrinsic=true;
                                    
                                    
                                    
                                    if (attributes.getQName(0)!= null){
                                          
                                          path[pathcounter]=new String(contentLabel);
                                          pathcounter++;
                                          for (int i=0;i<attributes.getLength();i++){     
                                                path[pathcounter]="[@"+attributes.getQName(i)+"='"+attributes.getValue(attributes.getQName(i))+"']";
                                                pathcounter++;
                                    
                                                // Section to do direct mapping of ATTRIBUTES
                                                if(attributes.getQName(i).equals("operation")){
                                                      varOperation=attributes.getValue(attributes.getQName(i));
                                                }
                                                if(attributes.getQName(i).equals("domain")){
                                                     if (pathcounter>2){
                                                            if(path[0].equals("Header")&& path[1].equals("To")&& path[2].equals("Credential"))
                                                                  varDunsDomain=attributes.getValue(attributes.getQName(i));
                                                            if(path[0].equals("Header")&& path[1].equals("From")&& path[2].equals("Credential"))
                                                                  varAnidDomain=attributes.getValue(attributes.getQName(i));
                                                
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
                              
                              if (content.trim().length() >0){    
                                    
                                    
                                  for (int i=0;i<pathcounter;i++){
                                    pathcontent=pathcontent+path[i];
                                    if(pathcounter-1>i){
                                          pathcontent=pathcontent+"/";
                                    }
                                  }
                                  
                                  // Storing the values for the field for the direct mappings
                                  
                                  //this is to check if extrinsic exist if yes take the value of Company Code extrinsic. 
                                  if(pathcontent.contains("Request")&& pathcontent.contains("PunchOutSetupRequest")&& pathcontent.contains("Extrinsic") && pathcontent.contains("CompanyCode")){
                                    varCompanyCodeExtrinsic=content;
                                    //Extrinsic=false;
                                  }
                                  
                                    if(URL==true && pathcontent.contains("Request")&& pathcontent.contains("PunchOutSetupRequest")&&pathcontent.contains("BrowserFormPost")){
                                          varReturnURL=content;
                                          URL=false;
                                    }
                                    if(OrderID==true && pathcontent.contains("Request") && pathcontent.contains("PunchOutSetupRequest") && pathcontent.contains("ItemOut") && pathcontent.contains("SupplierPartAuxiliaryID") ){
                                          String tempContent = content.substring(0, Math.min(10, content.length()));
                                          varOrderID=tempContent;
                                          OrderID=false;
                                    }
                                    if(Identity==true && pathcontent.contains("Header") && pathcontent.contains("To") && pathcontent.contains("Credential") ){
                                          varDunsIdentity=content;
                                          Identity=false;
                                    }
                                    if(Identity==true && pathcontent.contains("Header") && pathcontent.contains("From") && pathcontent.contains("Credential") ){
                                          varAnidIdentity=content;
                                          Identity=false;
                                    }
                                    if(SharedSecret==true && pathcontent.contains("Header") && pathcontent.contains("Sender") && pathcontent.contains("Credential") ){
                                          varSharedSecret=content;
                                          SharedSecret=false;
                                    }
                                    if(UserAgent==true && pathcontent.contains("Header") && pathcontent.contains("Sender")){
                                        varUserAgent=content;
                                        UserAgent=false;
                                    }
                                    if(SupplierPartID==true && pathcontent.contains("Request") && pathcontent.contains("PunchOutSetupRequest") && pathcontent.contains("SelectedItem") && pathcontent.contains("ItemID")) {
                                    	varSupplierPartID=content;
                                    	SupplierPartID=false;
                                    }
                                    if(buyerCookie==true && pathcontent.contains("Request") && pathcontent.contains("PunchOutSetupRequest")){
                                          varBuyerCookie=content;
                                          buyerCookie=false;
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
                              addInfo("[ERROR]: In fuction characters(char ch[], int start, int length) : Error during conversion of characters");
                              addInfo("[ERROR]:"+ e.getMessage());
                              e.printStackTrace();
                        }
                        
                        
            
                  }
               
                 };
                
                 saxParser.parse(in, handler);
            
                 
                   
                 } catch (Throwable e) {
                   addInfo("[ERROR]: In fuction convert_cXML2Xpath (InputStream in): Unknown error, call an expert");
                        addInfo("[ERROR]:"+ e.getMessage());
                        e.printStackTrace();
                 }
                // return table_aux;
          }
          
          private void RecoverDataFromRFC (String targetSystem, InputStream in) throws JCoException, StreamTransformationException{
            
            //String paramS = in.getInputParameters().getString("System");
            
            JCoFunction function=null;
                  
            if(targetSystem.equals("local")){
                        String CLIENT = "600";
                        String USERID = "radzki-1";//RFC4OI3_01";
                        String PASSWORD = "radzkip08";//init1234"; 
                        String LANGUAGE = "EN";
                        String HOSTNAME = "CH00SOC1A.EU.MT.MTNET";//Message server for QC2 = CH00VQC2S.EU.MT.MTNET / CH00SOC1A.EU.MT.MTNET
                        //String GROUP = "OC1";
                        //String MSHOST = "CH00VOC1S.EU.MT.MTNET";//ch00voc1s.eu.mt.mtnet
                        String SYSTEMNUMBER = "02";//for QC2 00
                        
                        JCO.Function function_local=null;
                        JCO.Repository mRepository=null;
                        
                        JCO.Client mConnection = JCO.createClient(CLIENT, // SAP client

                              USERID, // User ID
                              PASSWORD, // Password
                              LANGUAGE, // Language
                              HOSTNAME, // Host name
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
                  
            
                  
                        mConnection.execute( function_local );

                        //function=(JCoFunction) function_local;
                        JCO.ParameterList outTables = null;
                        outTables = function_local.getExportParameterList();
                        getValuesLocalRFC(in,outTables);
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
                        destination = JCoDestinationManager.getDestination("XI_IDOC_DEFAULT_DESTINATION_" + targetSystem.substring(0, 3) + "_" +targetSystem.substring(7));
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
                            function.execute(destination);
                            
                            JCoParameterList outTables = null;
                              outTables = function.getExportParameterList();
                              
                              addInfo("Table Active"+outTables.getValue("ACTIVE"));
                              
                              getValuesRFC(in, outTables);
                              addInfo("Function call RFC end");
                              //return (Table) outTables;
                  }catch (Exception e){
                        addInfo("[ERROR]: In fuction  RecoverDataFromRFC (String targetSystem, InputStream in): During connection RFC");
                              addInfo("[ERROR]:"+ e.getMessage());
                              e.printStackTrace();
                  }
                  
                  //JCoRepository mRepository=destination.getRepository();
                  
                //      JCoFunctionTemplate ft = mRepository.getFunctionTemplate(RFC);
                  
               //       function = mRepository.getFunction(RFC);
                  
                  
            }
          }
          private void validateSharedSecret() throws StreamTransformationException{
          
            if(!SenderInterface.equals("OCI")){
                  if(varSharedSecret.equals("")){
                        Error=true;
                        throw new StreamTransformationException("[ERROR] No SharedSecret in the Source payload message ");
                  }
                  
                        //EXCEPTION FOR IOWA STATE UNIVERCITY - two different SharedSecrets needs to be checked. 
                        //SharedSecret for Punchout and SharedSecret for Order is different. 
                        //Interface will check SharedSecret for Punchout with constant Identity IOWA_PUNCHOUT when Identity = IOWASTATEUNIV for this interface. 
                        
                        if(varAnidIdentity.equals("IOWASTATEUNIV")){
                              varAnidIdentity = "IOWA_PUNCHOUT";
                        }
                        //END
                        
                        //EXCEPTION FOR ASTRAZENCE AND MEDIMMUNE
                        //AstraZeneca and Medimmune are sending the same credentials but are authenticated with different shared secret. 
                        //The difference is Extrinsic company code field in the message body of cXML 
                        //if identity in the message contain AN01006719155 additional check needs to be performed and 
                        //Corresponding Identity needs to be assigned in this case: ASTRAZENECA or MEDIMMUNE 
                        // Adding more company codes "0010", "GB10" to check for Anachem UK eshop
                        if(varAnidIdentity.matches("(?i).*AN01006719155.*")){
                              if(varCompanyCodeExtrinsic.equals("4314")){
                                    varAnidIdentity = "ASTRAZENECA";
                              }
                              if(varCompanyCodeExtrinsic.equals("4315")){
                                    varAnidIdentity = "ASTRAZENECA";
                              }
                              
                              if(varCompanyCodeExtrinsic.equals("0010")){
                                  varAnidIdentity = "ASTRAZENECA_UK";
                            }
                              
                              if(varCompanyCodeExtrinsic.equals("GB10")){
                                  varAnidIdentity = "ASTRAZENECA_UK";
                            }
                              if(varCompanyCodeExtrinsic.equals("5351")){
                                    varAnidIdentity = "MEDIMMUNE";
                              }
                              if(varCompanyCodeExtrinsic.equals("5352")){
                                    varAnidIdentity = "MEDIMMUNE";
                             }
                        }
                        //END
                        
                        //EXCEPTION FOR REGENERON - two different SharedSecrets needs to be checked. 
                        //SharedSecret for Punchout and SharedSecret for Order is different. 
                        //Interface will check SharedSecret for Punchout with constant Identity REGENERON_PUNCHOUT when Identity = 194873139 for this interface. 
                        
                        if(varAnidIdentity.equals("194873139")){
                              varAnidIdentity = "REGENERON_PUNCHOUT";
                        }
                        //END
                        
                  
                        String senderSecret_aux=XIVMService.executeMapping("http://mt.com/pi/bo/lc/ch/VMR/cXMLSharedSecret", "", "cXML_Identity", varAnidIdentity, "http://mt.com/pi/bo/lc/ch/VMR/cXMLSharedSecret", "", "cXML_SharedSecret");
                        
                        if(senderSecret_aux==null){
                        Error=true;
                              throw new StreamTransformationException("[ERROR] No SharedSecret in the Value Mapping for: "+varAnidIdentity);
                        }
                        if(!senderSecret_aux.equals(varSharedSecret)){
                        Error=true;
                              throw new StreamTransformationException("[Error] The SharedSecret in the VM ("+ varAnidIdentity+","+senderSecret_aux+") is not equal to the received one "+varSharedSecret);
                        }
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
                          
                          addInfo("ACTIVE:"+varActive);
                          addInfo("CONTACT_PERSON:"+varContactID);
                          addInfo("ESHOP_CODE:"+vareShopCode);
                          addInfo("PROFILE_NR:"+varProfNr);
                          addInfo("PROTOCOL:"+varProtocol);
                        
                        
                  }catch(Exception e){
                        addInfo("[ERROR]: In fuction callRFC(InputStream in): Error recovering the values from the RFC");
                        addInfo("[ERROR]:"+ e.getMessage());
                        e.printStackTrace();
            
                  }


                  // Only need for local connection
                  
                  
            }

            private void getValuesLocalRFC( InputStream in, JCO.ParameterList outTables){
                  
                  
                  


                  try{
                              //    JCoParameterList outTables = RecoverDataFromRFC(targetSystem,in);

                                System.out.println("Test");
                                varActive = (String)outTables.getValue("ACTIVE");
                                varContactID = (String)outTables.getValue("CONTACT_PERSON");
                                vareShopCode = (String)outTables.getValue("ESHOP_CODE");
                                varProfNr = (String)outTables.getValue("PROFILE_NR");
                                varProtocol = (String)outTables.getValue("PROTOCOL");
                                
                                System.out.println("ACTIVE:"+varActive);
                                System.out.println("CONTACT_PERSON:"+varContactID);
                                System.out.println("ESHOP_CODE:"+vareShopCode);
                                System.out.println("PROFILE_NR:"+varProfNr);
                                System.out.println("PROTOCOL:"+varProtocol);
                              
                        }catch(Exception e){
                              addInfo("[ERROR]: In fuction callRFC(InputStream in): Error recovering the values from the RFC");
                              addInfo("[ERROR]:"+ e.getMessage());
                              e.printStackTrace();
                  
                        }


                        // Only need for local connection
                        
                        
                  }
      private void composePayload(OutputStream out){
            //addInfo("Function composePayload start");
            
            if(varActive.equals("X")){
                  try {
                        String buffer="<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns0:HybrisPunchoutRequest xmlns:ns0=\"urn:mt.com:A:HYBRIS:eCommerce\"><Header>";
                        //out.write(buffer.getBytes());
                  
                        if (varContactID !=""){
                              buffer=buffer+"<ContactID>"+varContactID+"</ContactID>";
                        //    out.write(buffer.getBytes());             
                        }
                        if (vareShopCode !=""){
                              buffer=buffer+"<eShopCode>"+vareShopCode+"</eShopCode>";
                              //out.write(buffer.getBytes());                 
                        }
                        if (varOperation !=""){
                              if(varOperation.trim().equals("create"))
                                    varOperation="01";
                              if(varOperation.trim().equals("edit"))
                                    varOperation="02";
                              if(varOperation.trim().equals("inspect"))
                                    varOperation="03";
                              
                              buffer=buffer+"<Operation>"+varOperation+"</Operation>";
                        //    out.write(buffer.getBytes());             
                        }
                        if (varProfNr !=""){
                              buffer=buffer+"<ProfNr>"+varProfNr+"</ProfNr>";
                              //out.write(buffer.getBytes());                 
                        }
                        if (varReturnURL !=""){
                              buffer=buffer+"<ReturnURL>"+varReturnURL+"</ReturnURL>";
                              //out.write(buffer.getBytes());                 
                        }
                        if (varProtocol !=""){
                              buffer=buffer+"<Protocol>"+varProtocol+"</Protocol>";
                        //    out.write(buffer.getBytes());             
                        }
                        if (varOrderID !=""){
                              buffer=buffer+"<OrderID>"+varOrderID+"</OrderID>";
                        //    out.write(buffer.getBytes());             
                        }
                        
                        buffer=buffer+"</Header><cXML>";
                        //out.write(buffer.getBytes());
                        if (varDunsDomain !=""){
                              buffer=buffer+"<DunsDomain>"+varDunsDomain+"</DunsDomain>";
                        //    out.write(buffer.getBytes());             
                        }
                        if (varDunsIdentity !=""){
                              buffer=buffer+"<DunsIdentity>"+varDunsIdentity+"</DunsIdentity>";
                        //    out.write(buffer.getBytes());             
                        }
                        if (varAnidDomain !=""){
                              buffer=buffer+"<AnidDomain>"+varAnidDomain+"</AnidDomain>";
                              //out.write(buffer.getBytes());                 
                        }
                        if (varAnidIdentity !=""){
                              buffer=buffer+"<AnidIdentity>"+varAnidIdentity+"</AnidIdentity>";
                        //    out.write(buffer.getBytes());             
                        }
                        if (varUserAgent !=""){
                            buffer=buffer+"<UserAgent>"+varUserAgent+"</UserAgent>";
                      //    out.write(buffer.getBytes());             
                        }
                        if (varBuyerCookie !=""){
                              buffer=buffer+"<BuyerCookie>"+varBuyerCookie+"</BuyerCookie>";
                              //out.write(buffer.getBytes());                 
                        }
                        if (varSupplierPartID !=""){
                            buffer=buffer+"<SelectedItem><ItemID><SupplierPartID>"+varSupplierPartID+"</SupplierPartID></ItemID></SelectedItem>";
                      //    out.write(buffer.getBytes());             
                        }
                        buffer=buffer+"</cXML></ns0:HybrisPunchoutRequest>";
                       addInfo("Message:"+buffer);
                        out.write(buffer.getBytes());
                        
                  } catch (IOException e) {
                        // TODO Auto-generated catch block
                        addInfo("[ERROR]: In fuction composePayload(OutputStream out): Error writting the Payload");
                        addInfo("[ERROR]:"+ e.getMessage());
                        e.printStackTrace();
                        
                  }
            }else{
                  addInfo("[ERROR]: The BuyerProfile configuration does not exist in CRM, the flag ACTIVE is not active in the RFC call");
            }
            
            
      }
            
      public void execute(TransformationInput in, final OutputStream out)
      throws StreamTransformationException, JCoException {
                  try{
               // callRFC(in.getInputPayload().getInputStream(),RecoverDataFromRFC(in.getInputParameters().getString("System"),in.getInputPayload().getInputStream()));
                SystemRFCDestination=in.getInputParameters().getString("System");
                SenderInterface=in.getInputHeader().getSenderService();
                RecoverDataFromRFC(in.getInputParameters().getString("System"), in.getInputPayload().getInputStream());
                if(!Error)
                  composePayload(out);
                  //step1Connect();
//            AbstractTrace t=null;
//            t=getTrace();
//            t.addWarning("Inicio");
//            System.out.println("Inicio:");
//            try{
//                    JCoDestination destination = JCoDestinationManager.getDestination("SC1CLNT200_RFC");
//                    t.addWarning("destination.getAttributes(): "+destination.getAttributes());
//                  System.out.println("Attributes:");
//                  System.out.println(destination.getAttributes());
//                  System.out.println();
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
                  addInfo("ReceiverPartyAgency:"+arg0.getInputHeader().getSenderParytAgency());
                  addInfo("ReceiverPartyScheme:"+arg0.getInputHeader().getSenderPartyScheme());
                  addInfo("Interface:"+arg0.getInputHeader().getInterface());
                  addInfo("Service:"+arg0.getInputHeader().getSenderService());
                  this.execute(arg0,arg1.getOutputPayload().getOutputStream());
            } catch (JCoException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
            }
            
      }

}

