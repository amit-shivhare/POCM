package com.sap.module;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;

import javax.ejb.Stateless;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ICC_Profile;
import com.itextpdf.text.pdf.PdfAConformanceLevel;
import com.itextpdf.text.pdf.PdfAWriter;
import com.sap.aii.af.lib.mp.module.ModuleContext;  
import com.sap.aii.af.lib.mp.module.ModuleData;  
import com.sap.aii.af.lib.mp.module.ModuleException;  
import com.sap.engine.interfaces.messaging.api.Message;  
import com.sap.engine.interfaces.messaging.api.MessageKey;  
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;  
import com.sap.engine.interfaces.messaging.api.PublicAPIAccess;
import com.sap.engine.interfaces.messaging.api.PublicAPIAccessFactory;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.exception.MessagingException;
import com.sap.tc.logging.Location;

/**
 * Session Bean implementation class FileAdapModuleArcPdf
 */
@Stateless
public class FileAdapModuleArcPdf {
	private static final String C_PATH_STRING = "path";  
	private static final String C_CONVERT_BOOLEAN = "convert"; 
	
	   /**
  * Default constructor. 
  */
 public FileAdapModuleArcPdf() {
     }
 
 public ModuleData process(ModuleContext moduleContext, ModuleData inputModuleData)  
 throws ModuleException {
	   
  String path = null;
  String convert = "false";
	 Object obj = null;  
	 Message msg = null;  
	 MessageKey msgKey = null;
  String archiveFileName = null;
  String nfsFileName = null;
  Location location = null;

  
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
		 
				  try {
					  location = 
						  Location.getLocation(this.getClass().getClassLoader());
				  } catch (Exception e) {
					  audit.addAuditLogEntry(msgKey,AuditLogStatus.WARNING,"Location cannot be read.");
				  }
				  audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS,"Location: " + location.getDCName() + " + Description: " + location.getDescription());
			//Reading file name from message header   
			 MessagePropertyKey fileKey = new MessagePropertyKey("ArchiveFileName","http://sap.com/xi/XI/System/File");
			 path = moduleContext.getContextData(C_PATH_STRING);
			 convert = moduleContext.getContextData(C_CONVERT_BOOLEAN);
			 if (path != null){
			 
			 if (!path.endsWith("\\")){
				 path = path.concat("\\");
			 }
			 if (convert != null){
			 if (convert.toUpperCase().contentEquals("TRUE")){
			 archiveFileName = msg.getMessageProperty(fileKey).concat(".arc");
			 File folder = new File(path);
			 File[] listOfFiles = folder.listFiles();

			 //Get the list of files present in root directory
			 for (int k = 0; k <listOfFiles.length; k++) 
			{				
						audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS, "File in list-> " + listOfFiles[k].getName() + "dynamic for archivename: " + archiveFileName);
						nfsFileName = listOfFiles[k].getName();
						if(nfsFileName.contentEquals(archiveFileName)){
						audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS, "Reading file from root dir-> " + nfsFileName);
							//Reading the contents of the Main file from the NFS folder						
//							byte[] ftpFileDocContent = null;
							BufferedReader br = new BufferedReader(new FileReader(listOfFiles[k]));
							StringBuilder sb = null;
							String line = null;
//							 String everything = null;
							try
							{
								sb = new StringBuilder();
						        line = br.readLine();
//						        while (line != null) {
						            sb.append(line);
//						            sb.append(System.getProperty("line.separator"));
//						            line = br.readLine();
//						        } 
//						        everything = sb.toString();
//							ftpFileDocContent = everything.getBytes();
//							byte by [] = everything.getBytes(); 
						     Document document = new Document();
							 ByteArrayOutputStream output = new ByteArrayOutputStream();
							 PdfAWriter writer = PdfAWriter.getInstance(document,
									    output,
									    PdfAConformanceLevel.PDF_A_3B);
							 writer.createXmpMetadata();
	
						ClassLoader classLoader = getClass().getClassLoader();
  						 InputStream fileIS =  classLoader.getResourceAsStream("sRGBColorSpaceProfileicm");
						 audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS, "InsteamStream File created: -> " + fileIS.available());

							 document.open();
							 
							 ICC_Profile icc = ICC_Profile.getInstance(
									    		fileIS);
									writer.setOutputIntents(
									    "Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", icc);
									
									Font normal9 = FontFactory.getFont(
									"FreeSans.ttf",
									BaseFont.WINANSI, BaseFont.EMBEDDED, 9);
						
							 document.add(new Paragraph(line,normal9));
							 document.close(); 
							 byte byt[] = output.toByteArray();
							 audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS, "PDF document created");
								FileOutputStream fos = new FileOutputStream(path+msg.getMessageProperty(fileKey)+".pdf");    
								fos.write(byt);
								fos.close();
								audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS, "fos write complete-> ");
							}
							finally{
								br.close();
								// delete org files
								Boolean bool = false; 
								bool = listOfFiles[k].delete();
								audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS, "Deleting the org archive file from root dir-> result:" + bool);
							}
						}//End IF for checking filename begins with Unique Key
						
						else {
							audit.addAuditLogEntry(msgKey,AuditLogStatus.SUCCESS, "Filenames dont match-> " );
						}
					}// End For Loop
			 }
			 }
			 }
			 else {
					audit.addAuditLogEntry(msgKey,AuditLogStatus.WARNING, "No path specified in adapter module configuration - pdf conversion not executed.");				 
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
