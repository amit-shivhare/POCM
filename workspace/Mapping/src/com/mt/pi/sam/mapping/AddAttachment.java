package com.mt.pi.sam.mapping;

import java.io.InputStream;
import java.io.OutputStream;
import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.Attachment;
import com.sap.aii.mapping.api.DynamicConfigurationKey;
import com.sap.aii.mapping.api.OutputAttachments;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import  com.sap.aii.mapping.api.DynamicConfiguration;
public abstract class AddAttachment extends AbstractTransformation{
	
	
	private static final DynamicConfigurationKey KEY_FILENAME = DynamicConfigurationKey.create("http://sap.com/xi/XI/System/File","FileName"); 
		public void transform(TransformationInput transformationInput, TransformationOutput transformationOutput) {
			//System.out.println("A");
			
			 // access dynamic configuration

			  InputStream inputstream = transformationInput.getInputPayload().getInputStream();
			  OutputStream outputstream = transformationOutput.getOutputPayload().getOutputStream();
			  DynamicConfiguration conf = transformationInput.getDynamicConfiguration();
			  String fileName = "";

			   fileName = conf.get(KEY_FILENAME);
			   
			 //System.out.println(“C”+fileName);

			   // set file name

			   conf.put(KEY_FILENAME, fileName);
			   try {

				   // a) Populate XML for XIPAYLOAD

				   String fresult= "";

				   // creating the xml

				   fresult="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

				   fresult = fresult.concat("<ns0:MT_Payload xmlns:ns0=\"http://test\">");

				   fresult = fresult.concat("<FileName>");

				   fresult = fresult.concat(fileName);

				   fresult = fresult.concat("</FileName>");

				   fresult = fresult.concat("</ns0:MT_Payload>");

				   outputstream.write(fresult.getBytes("UTF-8"));
				   
				// Write attachment

				   OutputAttachments outputAttachments = transformationOutput.getOutputAttachments();

				   byte[] b = new byte[inputstream.available()];

				   inputstream.read(b);

				   Attachment newAttachment = outputAttachments.create(fileName,"application/pdf", b);

				   outputAttachments.setAttachment(newAttachment);
			   }
				   
				   catch (Exception e) {

					   getTrace().addDebugMessage(e.getMessage());

					   }

				   
		}
	
}
	
