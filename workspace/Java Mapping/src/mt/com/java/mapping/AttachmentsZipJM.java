package mt.com.java.mapping;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.*;
import java.util.zip.*;
import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.*;
import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.api.StreamTransformationException;

public class AttachmentsZipJM extends AbstractTransformation {
    private static final DynamicConfigurationKey contentIDSKey = DynamicConfigurationKey.create("http://test.com", "contentIDS");
public void transform(TransformationInput in, TransformationOutput out) throws StreamTransformationException
              {
                          try
                          {
                                      DynamicConfiguration conf = (DynamicConfiguration) in.getDynamicConfiguration();
                                      // get message ID
                                      String msgID = (String) in.getInputHeader().getMessageId();
                                      String s = null;
                                      int len = 0;
                                      Object[] arrayObj = null;
                                      byte[] buffer = new byte[1024];
                                      byte[] attachmentBuffer = new byte[1024];
                                      String attachmentID = null;
                                      ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                      ZipOutputStream zos = new ZipOutputStream(baos);
                                      ZipEntry anEntry = null;
                                      InputStream is = (InputStream) in.getInputPayload().getInputStream();
                                      while ((len = is.read(buffer)) > 0)
                                      {
                                                  out.getOutputPayload().getOutputStream().write(buffer, 0, len);                                                  
                                      }
                                      InputAttachments inputAttachments = in.getInputAttachments();
                                      if(inputAttachments.areAttachmentsAvailable())
                                      {                                                          
                                                  //gets the attachmentIds and store it in an Object array
                                                  Collection<String> collectionIDs = inputAttachments.getAllContentIds(true);
                                                  arrayObj = collectionIDs.toArray();
                                                  //Object[] arrayObj = collectionIDs.toArray();
                                                  String contentIDS = "";
                                                  //Loops at the input attachments to get the content of the attachment and then write it to zip output stream wrapper.
                                                  for(int i =0;i<arrayObj.length;i++)
                                                  {
                                                              attachmentID =(String)arrayObj[i];
                                                              contentIDS = contentIDS + (i+1) + ") " + attachmentID + "; ";
                                                              Attachment attachment = inputAttachments.getAttachment(attachmentID);            
                                                              byte[] attachmentBytes = attachment.getContent();
                                                              zos.putNextEntry(new ZipEntry(attachmentID));
                                                              zos.write(attachmentBytes);
                                                              zos.closeEntry();          
                                                              //Close each zipentry after writing it to stream
                                                              //remove each attachment if required. Uncommonent below line
                                                              //out.getOutputAttachments().removeAttachment(attachmentID);                                                 
                                                  }
                                                  zos.close();       //Close Zip Stream
                                                  conf.put(contentIDSKey, contentIDS);
                                      }
                                      Attachment newopAttachment = out.getOutputAttachments().create(msgID + ".zip", baos.toByteArray());
                                      out.getOutputAttachments().setAttachment(newopAttachment);
                          }
                          catch(Exception e)
                          {
                                      e.printStackTrace();
                          }
}
}