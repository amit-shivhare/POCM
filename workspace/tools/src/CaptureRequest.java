

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CaptureRequest
 */
public class CaptureRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CaptureRequest() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String Box = request.getLocalName();
		int index = Box.indexOf(".");
		if(index>-1) 
			Box = Box.substring(0, index);
		else
			Box = "Unspecified file server";

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
		Date date = new Date();
		String dts = dateFormat.format(date);
		
		String FileName = "\\\\" + Box + "\\interfaces\\Tools\\CaptureRequest\\GET_request_" + dts + ".dat";
		String resp = "Processed successfully...\nStored to " + FileName;
		
    	try {
        	InputStream IS = request.getInputStream();
        	FileOutputStream FOS = new FileOutputStream(FileName);
        	
        	int byte_ = IS.read();
        	while(byte_>-1) {
        		FOS.write(byte_);
        		byte_ = IS.read();
        	}
        		
			FOS.close();
        }
        catch(Exception e) {
        	resp = e.getMessage();
        }	

    	PrintWriter PW = response.getWriter();
    	PW.write(resp);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String Box = request.getLocalName();
		int index = Box.indexOf(".");
		if(index>-1) 
			Box = Box.substring(0, index);
		else
			Box = "Unspecified file server";

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
		Date date = new Date();
		String dts = dateFormat.format(date);
		
		String FileName = "\\\\" + Box + "\\interfaces\\Tools\\CaptureRequest\\POST_request_" + dts + ".dat";
		String resp = "Processed successfully...\nStored to " + FileName;
		
    	try {
        	InputStream IS = request.getInputStream();
        	FileOutputStream FOS = new FileOutputStream(FileName);
        	
        	int byte_ = IS.read();
        	while(byte_>-1) {
        		FOS.write(byte_);
        		byte_ = IS.read();
        	}
        		
			FOS.close();
        }
        catch(Exception e) {
        	resp = e.getMessage();
        }	

    	PrintWriter PW = response.getWriter();
    	PW.write(resp);

	}

}
