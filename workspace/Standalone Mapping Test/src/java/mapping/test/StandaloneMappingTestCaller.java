package java.mapping.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StandaloneMappingTestCaller {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	       final String TRANSFORM_METHOD_NAME = "transform";
	       final String SRC_TYPE_IN_STREAM = "java.io.InputStream";
	       final String TGT_TYPE_OUT_STREAM = "java.io.OutputStream";
	       if (args.length < 3) {
	           System.err.println("One or several mandatory arguments are missing");
	           System.exit(1);
	       }
	       
	       String mappingClassName = args[0];
	       String srcMsgFileName = args[1];
	       String tgtMsgFileName = args[2];
	       try {
	            System.out.println("Starting mapping test");
	            
	       }
	       finally ("fdg","ghjn");
	       
	           
	}

}
