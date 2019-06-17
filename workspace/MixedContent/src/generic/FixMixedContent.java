package generic;

import java.io.*;
import com.sap.aii.mapping.api.*;

public class FixMixedContent extends AbstractTransformation {
		
	@Override

	public void transform(TransformationInput transformationInput, TransformationOutput transformationOutput) throws StreamTransformationException {
        try {
            InputStream inputstream = transformationInput.getInputPayload().getInputStream();
            OutputStream outputstream = transformationOutput.getOutputPayload().getOutputStream();
            
            fixMessage(inputstream, outputstream);
            //outputstream.close();
       
        } catch (Exception exception) {
            getTrace().addDebugMessage(exception.getMessage());
            throw new StreamTransformationException(exception.toString());
        }
    }
    

    private void fixMessage(InputStream is, OutputStream os) throws Exception {

	    String ProcessingXML = "";
    	String ProcessingText = "";
    	String ResultXML = "";
    	int ESP = 0;
    	
    	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    	
	   	String line = bufferedReader.readLine();
	    while(line != null){
	        ProcessingXML += line + "\n";
	        line = bufferedReader.readLine();
	    }
	    
		String elName = "";
	    
    	// processing of message
    	while(ProcessingXML.length()>0) {
    		
    		String ProcessingChar = ProcessingXML.substring(0,1);
    		
    		if(ProcessingChar.equals("<")) 
   				ESP = 1;
    		else if(ProcessingChar.equals(">")) {
    			if(ESP==3) ESP=4;
    			else
       				ESP = 2;
    		}
    		else if(ProcessingChar.equals("/")) {
    			if(ESP==1) ESP = 3;
    		}
    		//else if(ESP==2) ESP = 0;

			ProcessingText += ProcessingChar;

    		//processing decision block
			if(ESP==2) {
				elName = getElName(ProcessingText);
				if(!ProcessingText.trim().startsWith("<")) {
					ResultXML += fixMixedText(ProcessingText, "");
					ProcessingText = "";
					ESP = 0;
				}
				else {
					ResultXML += ProcessingText;
					ProcessingText = "";
					ESP = 0;
				}
			}
    		else if(ESP==4) {
    			if(ProcessingText.trim().startsWith("<")) {
					ResultXML += ProcessingText;
					ProcessingText = "";
					ESP = 0;
    			}
    			else {
    				//fix if there is a mixed text before closing an element
    				ResultXML += fixMixedText(ProcessingText, elName);
    				ProcessingText = "";
					ESP = 0;
    			}
    			elName = "";
    		}
    		
    		//shortening further processing
    		ProcessingXML = ProcessingXML.substring(1);
    		
    	}
    	
    	//Writing result to outputstream
        os.write(ResultXML.getBytes());
    	
    }
    
    private String fixMixedText(String s, String startElement) {
    	String endEl = getElName(s);
    	if(endEl.equals(startElement)) return s;
    	int i = s.indexOf("<");
    	String text = s.substring(0,i);
    	return "<MixedContent>" + text + "</MixedContent>" + s.substring(i);
    }
    
    private String getElName(String s) {
    	int i = s.indexOf("<");
    	int j = 0;
    	if(i<0) return null;
    	else {
    		j = s.indexOf(" ", i);
    		int k = s.indexOf(">", i);
    		if(j==-1) j=k;
    	}
    	return s.substring(i+1, j).replaceAll("/", "");
    }

}