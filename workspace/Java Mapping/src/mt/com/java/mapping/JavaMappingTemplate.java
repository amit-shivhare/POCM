package mt.com.java.mapping;
import java.io.*;
import java.util.*;
import com.sap.aii.mapping.api.*;

//EXTERNAL API IMPORTS HERE
public class JavaMappingTemplate extends AbstractTransformation
{
          private Map param;
          TransformationInput transformationInput = null;
          InputAttachments inputAttachments = null;
          InputStream in = null;
          public JavaMappingTemplate(){  }
          public void setParameter (Map map)
          {
                    param = map;
                    if (param == null)
                       param = new HashMap();
          }
          public static void main(String args[])          //FOR EXTERNAL STANDALONE TESTING
          {
                    try
                    {
                       FileInputStream fin = new FileInputStream(args[0]); //INPUT FILE (PAYLOAD)
                       FileOutputStream fout = new FileOutputStream(args[1]); //OUTPUT FILE (PAYLOAD)
                       JavaMappingTemplate mapping = new JavaMappingTemplate();
                       mapping.execute(fin, fout);
                    }
                    catch (Exception e1)
                    {
                              e1.printStackTrace();
                    }
          }
          
        //FOR PI END-TO-END TESTING (OR) PI STANDALONE TESTING i.e., FROM OPERATION MAPPING. PI MAPPING RUNTIME CALLS THIS MEHTOD
        
          public void transform(TransformationInput tranIn, TransformationOutput tranOut) throws StreamTransformationException
          {
                    transformationInput = tranIn;
                    //GET INPUT ATTACHEMENTS OBJECT
                    inputAttachments = tranIn.getInputAttachments(); //FOR PI END-TO-END TESTING (OR ) PI STANDALONE TESTING (FROM OPERATION MAPPING)
                    this.getTrace().addInfo("in Transform Method"); //FOR PI END-TO-END TESTING (OR ) PI STANDALONE TESTING (FROM OPERATION MAPPING)
                    this.execute( (InputStream) tranIn.getInputPayload().getInputStream(), (OutputStream) tranOut.getOutputPayload().getOutputStream());           
          }
          
          public void execute(InputStream inputstream, OutputStream outputstream)
          {
                    try
                    {
                              if (transformationInput != null)          //FOR PI END-TO-END TESTING (OR ) PI STANDALONE TESTING (FROM OPERATION MAPPING) - PAYLOAD & ATTACHMENTS HANDLING LOGIC
                              {
                                        this.getTrace().addInfo("IN EXECUTE METHOD");
                                        this.getTrace().addInfo("PI END-TO-END TESTING (OR ) PI STANDALONE TESTING (FROM OPERATION MAPPING) - PAYLOAD & ATTACHMENTS HANDLING LOGIC");
                                        if( inputAttachments != null )  //FOR PI END-TO-END TESTING
                                        {
                                                  //Important: First Enable the "Read Attachments" check box in Operation mapping if your code needs to handle attachments.
                                                  if( inputAttachments.areAttachmentsAvailable() ) //IF ATTACHMENT ARE AVAILABLE. DYNAMIC HEADERS, PAYLOAD & ATTACHMENTS HANDLING LOGIC
                                                  {
                                                            this.getTrace().addInfo("PI END-TO-END TESTING");
                                                            this.getTrace().addInfo("ATTACHMENTS AVAILABLE - DYNAMIC HEADERS, PAYLOAD & ATTACHMENTS HANDLING LOGIC");
                                                            //REQUIREMENT LOGIC - PAYLOAD & ATTACHMENTS HANDLING LOGIC
                                                            //DYNAMIC CONFIGURATION HEADERS HANDLING LOGIC
                                                  }
                                                  else          //IF ATTACHMENTS ARE NOT AVAILABLE. DYNAMIC HEADERS, PAYLOAD LOGIC ONLY
                                                  {
                                                            this.getTrace().addInfo("PI END-TO-END TESTING");
                                                            this.getTrace().addInfo("ATTACHMENTS NOT AVAILABLE - DYNAMIC HEADERS, PAYLOAD HANDLING LOGIC");
                                                            //REQUIREMENT LOGIC - PAYLOAD LOGIC ONLY
                                                            //DYNAMIC CONFIGURATION HEADERS HANDLING LOGIC
                                                  }
                                        }
                                        else          //FOR PI STANDALONE TESTING (FROM OPERATION MAPPING): PAYLOAD HANDLING LOGIC ONLY
                                        {
                                                  this.getTrace().addInfo("PI STANDALONE TESTING (FROM OPERATION MAPPING): PAYLOAD HANDLING LOGIC ONLY");
                                                  in = (InputStream) getClass().getResourceAsStream("<FileNameFromImportedArchive/PI LocalDrive>");
                                                  //REQUIREMENT LOGIC - PAYLOAD LOGIC ONLY
                                        }
                              }
                              else //FOR EXTERNAL STANDALONE TESTING: PAYLOAD HANDLING LOGIC ONLY
                              {
                                        System.out.println("FOR EXTERNAL STANDALONE TESTING: PAYLOAD HANDLING LOGIC ONLY");
                                        in = (InputStream) getClass().getResourceAsStream("<FileNameFromLocalDrive>");
                                        //REQUIREMENT LOGIC - PAYLOAD LOGIC ONLY
                              }
                    }
                    catch (Exception e2)
                    {
                              e2.printStackTrace();
                              this.getTrace().addDebugMessage(e2.toString());
                    }
          }
} 
          