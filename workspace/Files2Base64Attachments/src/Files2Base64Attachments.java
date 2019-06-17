import org.apache.commons.codec.binary.Base64;
import com.sap.aii.mapping.api.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Files2Base64Attachments extends AbstractTransformation {

	AbstractTrace at = null;
	
	String path_ = "";
	
	public void transform(TransformationInput transformationInput, TransformationOutput transformationOutput) throws StreamTransformationException {  
      InputStream is = transformationInput.getInputPayload().getInputStream();  
      OutputStream os = transformationOutput.getOutputPayload().getOutputStream();
      
      at = getTrace();
      
      String pathParamName = "FilesToAttachPath";
      path_ = transformationInput.getInputParameters().getString(pathParamName);
      if(path_ == null) throw new StreamTransformationException("Parameter FilesToAttachPath not defined!");

      String result = processMapping(getStringFromInputStream(is));
      
      try {
    	  os.write(result.getBytes(Charset.forName("UTF-8")));
    	  os.close();
    	  is.close();
      }
      catch(IOException e) {
    	  throw new StreamTransformationException(e.getMessage());
      }
      
	}  

	private String processMapping(String input) throws StreamTransformationException {
		
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
		result += "  <ns1:CustomerMailRequest xmlns:ns1=\"urn:mt.com:A:SVC:Notifications\">\r\n";
		result += "    <ServiceOrderID>" + getElementValue("ServiceOrderID", input) + "</ServiceOrderID>\r\n";
		result += "    <ServiceAssignmentID>" + getElementValue("ServiceAssignmentID", input) + "</ServiceAssignmentID>\r\n";
		result += "    <SalesOrg>" + getAttributeValue("salesOrg", input) + "</SalesOrg>\r\n";
		result += "    <DistrChannel>" + getAttributeValue("distrChannel", input) + "</DistrChannel>\r\n";
		result += "    <From>" + getElementValue("From", input) + "</From>\r\n";
		result += "    <SendTo>" + getElementValue("SendTo", input) + "</SendTo>\r\n";
		result += "    <Subject>" + getElementValue("Subject", input) + "</Subject>\r\n";
		result += "    <Content>" + getElementValue("Content", input) + "</Content>\r\n";
		
		int attIndex = input.indexOf("FilesToAttach>");
		
		if(attIndex>-1) {
			
			result += "    <Attachments>\r\n";

			ArrayList<String> dirsToDelete = new ArrayList<String>();

			String subinput = input.substring(attIndex);
			int hIndex = subinput.indexOf("<File");
			subinput = subinput.substring(hIndex);
			if(hIndex>-1 )hIndex = 1;
			while(hIndex>0) {

				//at.addInfo("Subinput: " + subinput);
				
				hIndex = subinput.substring(1).indexOf("<File")+1;
				String fileString = "";

				//at.addInfo("hIndex: " + hIndex);
				
				if(hIndex>0) {
					fileString = subinput.substring(0, hIndex);
					subinput = subinput.substring(hIndex);
				}
				else {
					fileString = subinput;
				}
				
				//at.addInfo("Processing: " + fileString);
				
				String filename = getAttributeValue("name", fileString);
				String filepath = getAttributeValue("path", fileString);
				
				String b64 = "n/a";
				String ct = "n/a";
				
				//***checking inapropriate path movements
				if(filepath.indexOf(".")>-1) throw new StreamTransformationException("Dot character '.' is not allowed in subpath!");
				if(filepath.equals("n/a")) throw new StreamTransformationException("Subpath has to be specified! Using parent folder is not allowed!");

				//***getting fulll filepath including parameter
				if(!filepath.equals("n/a")) {
					filepath = path_ + filepath;
				}
				
				//***encoding the file to Base64
				if(!filename.equals("n/a")) {
					try {
						b64 = encodeFileToBase64Binary(filepath + filename);
					}
					catch(Exception e) {
						throw new StreamTransformationException(e.getMessage());
					}
				}
				
				//***getting file content type
				int ctIndex = filename.lastIndexOf(".");
				ct = filename.substring(ctIndex+1).toLowerCase();
				if(ct.equals("jpg")) ct = "image/jpeg";
				else
					if(ct.equals("txt")) ct = "text/plain";
					else
						if(ct.equals("pdf")) ct = "application/pdf";
						else
							if(ct.equals("png")) ct = "image/png";
							else
								if(ct.equals("tiff")) ct = "image/tiff";
								else
									ct = "application/" + ct;

				result += "      <Attachment>\r\n";
				result += "        <FileName>" + filename + "</FileName>\r\n";
				result += "        <ContentType>" + ct + "</ContentType>\r\n";
				result += "        <Base64Content>" + b64 + "</Base64Content>\r\n";
				result += "      </Attachment>\r\n";

				if(!dirsToDelete.contains(filepath)) dirsToDelete.add(filepath);
			}
			
			for(int k=0; k<dirsToDelete.size(); k++) {
				String dirToDelete = dirsToDelete.get(k);
					deleteDirFile(new File(dirToDelete.substring(0, dirToDelete.length()-1)));
			}
		
			result += "    </Attachments>\r\n";

		}
		
		result += "</ns1:CustomerMailRequest>";
		
		return result;		
	}
	
	private String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line + "\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}	


	private String encodeFileToBase64Binary(String fileName) throws IOException {

		File file = new File(fileName);
		byte[] bytes = loadFile(file);
		byte[] encoded = Base64.encodeBase64(bytes);
		String encodedString = new String(encoded);

		return encodedString;
	}
	
	private String getElementValue(String element, String doc) {
		String response = "n/a";
		int StartIndex = doc.indexOf("<" + element + ">");
		if(StartIndex>-1) {
			String endTag = "</" + element + ">";
			int EndIndex = doc.indexOf(endTag);
			response = doc.substring(StartIndex + element.length() + 2, EndIndex);
		}
		
		return response;
	}

	private String getAttributeValue(String att, String doc) {
		at.addInfo("Checking:" + att + ", " + doc);
		String response = "n/a";
		int StartIndex = doc.indexOf(" " + att + "=\"");
		if(StartIndex>-1) {
			String doc2 = doc.substring(StartIndex + att.length() + 3);
			response = doc2.substring(0, doc2.indexOf("\""));		
		}
		else {
			StartIndex = doc.indexOf(" " + att + "='");
			if(StartIndex>-1) {
				String doc2 = doc.substring(StartIndex + att.length() + 3);
				response = doc2.substring(0, doc2.indexOf("'"));		
			}
		}
		
		return response;
	}

	private static byte[] loadFile(File file) throws IOException {

		InputStream is = new FileInputStream(file);

		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}
		byte[] bytes = new byte[(int)length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}

		is.close();
		return bytes;
	}
	
	private void deleteDirFile(File file) {

		try {

			if(file.isFile()) {
				file.delete();
			}
			else {
		        File[] files = file.listFiles();
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirFile(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	            file.delete();
			}

		}
		catch(Exception e) {
			//no action/exception taken if the path does not exist
		}
		
	}
	
}
