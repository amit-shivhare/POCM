package com.sap.module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;

import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.sap.aii.af.lib.mp.module.ModuleContext;  
import com.sap.aii.af.lib.mp.module.ModuleData;  
import com.sap.aii.af.lib.mp.module.ModuleException;  
import com.sap.engine.interfaces.messaging.api.Message;  
import com.sap.engine.interfaces.messaging.api.MessageKey;  
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;  
import com.sap.engine.interfaces.messaging.api.Payload;
import com.sap.engine.interfaces.messaging.api.PublicAPIAccess;
import com.sap.engine.interfaces.messaging.api.PublicAPIAccessFactory;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.exception.MessagingException;

/**
 * Session Bean implementation class FileAdapModuleArcTrig
 */
@Stateless
public class FileAdapModuleArcTrig {
	private static final String C_PATH_STRING = "path";  
	private static final String C_TRIGGERFILENAME_STRING = "triggerName";
	
	   /**
  * Default constructor. 
  */
 public FileAdapModuleArcTrig() {
 }

 public ModuleData process(ModuleContext moduleContext, ModuleData inputModuleData)  
 																		throws ModuleException {
	   
	 String path = null;
	 String trigName = null;
	 Object obj = null;  
	 Message msg = null;  
	 MessageKey msgKey = null;
	 String archiveFileName = null;
	 String out = "";
	 String nroId = "";
	 String messageFileName = null;
	 String tempArchiveFileName = "";
	 //  String senderId = "";

  try {
			 // Retrieves the current principle data, usually the message , Return type is Object  
			 obj = inputModuleData.getPrincipalData(); 
			 //A Message is what an application sends or receives when interacting with the Messaging System.  
			 msg = (Message) obj;  
			 //MessageKey consists of a message Id string and the MessageDirection  
			 msgKey = new MessageKey(msg.getMessageId(),msg.getMessageDirection());  
			 PublicAPIAccess pa;
				try 
				{
					pa = PublicAPIAccessFactory.getPublicAPIAccess();
				} 
				catch (MessagingException me)
				{
						throw new ModuleException("Internal SAP PI 7.31 Error -> Accessing PublicAPIAccessFactory Failed!");					
				}
				AuditAccess audit = pa.getAuditAccess();															
				audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS,"ReadFiles: MODULE called");
		 
			 //Reading file name from message header  
			 MessagePropertyKey arcFileKey = new MessagePropertyKey("ArchiveFileName","http://sap.com/xi/XI/System/File");
			 MessagePropertyKey msgFileKey = new MessagePropertyKey("FileName","http://sap.com/xi/XI/System/File");

			 path = moduleContext.getContextData(C_PATH_STRING);
			 trigName = moduleContext.getContextData(C_TRIGGERFILENAME_STRING);
			 audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS, "triggerName: " + trigName);
			 audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS, "path: " + path);
			 
			 byte[] trigOutByt = null;
			 if ((path != null) && (trigName != null)){
			 
				 if (!path.endsWith("\\")){
					 path = path.concat("\\");
				 }
				 
				 archiveFileName = msg.getMessageProperty(arcFileKey);
				 FileOutputStream trigOut = null;
				 try{
					 	Payload payload = msg.getDocument();
					 	InputStream inps = (InputStream) payload.getInputStream();
					 	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					 	DocumentBuilder db = dbf.newDocumentBuilder();
					 	Document docTrig = db.parse(inps);
					 	docTrig.getDocumentElement().normalize();
					 	//Get the list of files present in root directory
//				 		File folder = new File(path);
//				 		File[] listOfFiles = folder.listFiles();
//						audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS, "List read");			
//				 		if (listOfFiles != null){
//				 			for (int k = 0; k <listOfFiles.length; k++) {
//								if(listOfFiles[k].getName().contentEquals(trigName)){
//								audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS, "Read file content");			
//								out = readDoc(listOfFiles[k]);
//								audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS, "Old content: " + out);	
//								out = out.concat("~");
//							}
//						}
//		 }
			 
				 
// set file name
					 	out = out.concat("FileName:");
					 	NodeList nodeLst1 = docTrig.getElementsByTagName("D_0020");
						if (nodeLst1 != null){
//							 	for (int i = 0; nodeLst1.getLength() > i;i++){
//								nroId = nodeLst1.item(i).getFirstChild().getNodeValue();
							 	nroId = nodeLst1.item(0).getFirstChild().getNodeValue();
//								 }
						}
						tempArchiveFileName = archiveFileName.concat("_" + nroId);
						out = out.concat(tempArchiveFileName.concat(".pdf")+";"); 
				 
// 						set Reference object type (perhaps hardcoded for this adaptermodule??)
						out = out.concat("ReferenceObjectType:INVOIC;");
				 
// set Reference id(s) : 1-n!!!
						NodeList nodeLst2 = docTrig.getElementsByTagName("D_1004");
						if (nodeLst2 != null){
						for (int i = 0; nodeLst2.getLength() > i;i++){
								out = out.concat("ReferenceObjectId:");
								out = out.concat(nodeLst2.item(0).getFirstChild().getNodeValue());
								out = out.concat(";");
						}
			 }
				 
// set date
				 
			 
//			 out = out.substring(0, (out.length()-1));
			 trigOutByt = out.getBytes();
				 trigOut = new FileOutputStream(path+nroId+"_"+trigName);    
				 trigOut.write(trigOutByt);
				 }
			 finally{
				 trigOut.close();
					}
			 archiveFileName = archiveFileName.concat("_" + nroId);
			 msg.setMessageProperty(arcFileKey, archiveFileName);

			 
			 	if (msgFileKey != null){
			 		messageFileName = msg.getMessageProperty(msgFileKey);
			 		messageFileName = messageFileName.substring(0, 
						 (messageFileName.length()-4)).concat("_" + nroId).concat((messageFileName.substring((messageFileName.length()-4), messageFileName.length())));
			 		msg.setMessageProperty(msgFileKey, messageFileName);
			 	}
			 
			 }
			 else {
					audit.addAuditLogEntry(msgKey,AuditLogStatus.WARNING, "No adapter module configuration - pdf conversion not executed.");				 
			 }
					//Sets the principle data that represents the message to be processed.
			 inputModuleData.setPrincipalData(msg);
  }
		catch (Exception excep)
	    {  
			 ModuleException modulExcep = new ModuleException( excep.getMessage());
			 throw modulExcep;  
	    }  

	 return inputModuleData;
 }
 
}