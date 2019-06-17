package com.sap.adaptermodule;
// Classes for EJB
import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
// Classes for Module development & Trace
import com.sap.aii.af.lib.mp.module.*;
import com.sap.engine.interfaces.messaging.api.*;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;

// XML parsing and transformation classes
import javax.xml.parsers.*;

import org.w3c.dom.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DynamicQueueBean implements SessionBean, Module {
    public static final String VERSION_ID = "$Id://tc/aii/30_REL/src/_adapters/_sample/java/user/module/DynamicQueue.java#1 $";
    static final long serialVersionUID = 7435850550539048631L;
 
    public static final String UTF8_BOM = "\uFEFF";
 
    private SessionContext myContext;
 
    public void ejbRemove() {
    }
 
    public void ejbActivate() {
    }
 
    public void ejbPassivate() {
    }
 
    public void setSessionContext(SessionContext context) {
        myContext = context;
    }
 
    public void ejbCreate() throws CreateException {
    }
 
    public ModuleData process(ModuleContext moduleContext, ModuleData inputModuleData) throws ModuleException {
        String content = null;
        MessageKey key = null;
        AuditAccess audit = null;
                DateFormat dateFormat = new SimpleDateFormat("ddHHmmssSSS");
                        Date date = new Date();
        try {
 
            String param1 = (String) moduleContext.getContextData("parentName");
 
            String param2 = (String) moduleContext.getContextData("nodeCheckValue");
 
            String param3 = (String) moduleContext.getContextData("nodeCheckName");
 
            String param4 = (String) moduleContext.getContextData("queueNameField");
 
            String param5 = (String) moduleContext.getContextData("queuePrefix");
			
			String param6 = (String) moduleContext.getContextData("defaultQueue");
			
			
			
            Message msg = (Message) inputModuleData.getPrincipalData();
 
            Payload payload = msg.getDocument();
 
            key = new MessageKey(msg.getMessageId(), msg.getMessageDirection());
            audit = PublicAPIAccessFactory.getPublicAPIAccess().getAuditAccess();
 
            InputStream inps_bom = (InputStream) payload.getInputStream();
            // convert the inputStream to string
            StringBuffer out = new StringBuffer();
            String outString = null;
            String ret = null;
            byte[] b = new byte[4096];
            for (int n; (n = inps_bom.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }
            outString = out.toString();
            content = outString;
            if (outString.startsWith(UTF8_BOM)) {
                outString = outString.substring(1);
            }
            ret = outString;
            // convert the string to inputStream
            InputStream inps = new ByteArrayInputStream(ret.getBytes("UTF-8"));
 
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
 
            DocumentBuilder db = dbf.newDocumentBuilder();
 
            Document doc = db.parse(inps);
 
            doc.getDocumentElement().normalize();
                        String queueName = "Empty";
                        
            String nodeCheckValue = "Empty";
            int preFixLen = 0;
                        
            
 
 
 
            // param3 is optional
            if (param3 == null || param3.length() <= 0) {
                param3 = "";
            }
            // param5 is optional
            if (param5 == null || param5.length() <= 0) {
                param5 = "";
            } else {
                preFixLen = 16 - param5.length();
            }
			//param6 is optional
			if (param6 == null || param6.length() <= 0) {
                param6 = "";
            }
			
			if(param6.equals("true")) // enhancement for ECO interfaces to create the queue with queue prefix and dateTime.
			{
			queueName = dateFormat.format(date);
			queueName = queueName.replaceAll("[^A-Za-z0-9]+", "");
			 
			 if (queueName.length() > preFixLen) // Sequence Id Should always be less than 16 chars
                {
                    queueName = queueName.substring(queueName.length() - preFixLen, queueName.length());
                }
			queueName = param5 + queueName;
			queueName = queueName.toUpperCase();
				msg.setDeliverySemantics(DeliverySemantics.ExactlyOnceInOrder);
                msg.setSequenceId(queueName);
			}
			else
			{
			try {
                      
			
			NodeList nodeLst = doc.getElementsByTagName(param1);
 
            Element element = (Element) nodeLst.item(0);
			
            NodeList nodeLst_aux = element.getChildNodes();
 
            for (int i = 0; i < nodeLst_aux.getLength(); i++) {
                        
                Node nodoaux = nodeLst_aux.item(i);
 
                if (nodoaux.getNodeName().equals(param3) && !(param2.equals("noNodeCheck"))) {
                    nodeCheckValue = nodoaux.getFirstChild().getNodeValue();
                }
                if (nodoaux.getNodeName().equals(param4)) {
 
                            queueName = nodoaux.getFirstChild().getNodeValue();
							queueName = queueName.replaceAll("[^A-Za-z0-9]+", "");
                        }
                                
 
            }
                                
            if (queueName.equals("Empty") | queueName.equals("") | queueName == null)
                        {
                queueName = "ZDEF"+dateFormat.format(date);
				msg.setDeliverySemantics(DeliverySemantics.ExactlyOnceInOrder);
                                msg.setSequenceId(queueName);
                        }
                        else
                        {
            if (param2.equals("noNodeCheck") | nodeCheckValue.equals(param2)) // noNodeCheck is constant value passed in parm2 in case if we dont need node check value
             {
                if (queueName.length() > preFixLen) // Sequence Id Should always be less than 16 chars
                {
                    queueName = queueName.substring(queueName.length() - preFixLen, queueName.length());
                }
                // Sequence Id Should be in Upper Case.
                                queueName = param5 + queueName;
								 queueName = queueName.toUpperCase();
             }
                        }
                        } catch (Exception e) {
                                                        queueName = "ZDEF"+dateFormat.format(date);
                                                        ModuleException me = new ModuleException("Queue Name is being set to default based on time:"+e.getMessage() );
                                                                  }
                                                        finally{
														msg.setDeliverySemantics(DeliverySemantics.ExactlyOnceInOrder);
                                                        msg.setSequenceId(queueName);
                                                        inputModuleData.setPrincipalData(msg);
                                                       return inputModuleData;
                                                        }
														
														}
} catch (Exception e) {
            e.printStackTrace();
            ModuleException me = new ModuleException("Unable to create Queue:" + e.getMessage() + "content:" + content, e);
             throw me;
         }
		return inputModuleData;
 
    }
 
}
	