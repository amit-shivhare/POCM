package com.sap.pi;

import javax.ejb.Stateless;

/* copy from Blog */

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.Timer;
import com.sap.aii.af.lib.mp.module.Module;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;
import com.sap.engine.interfaces.messaging.api.PublicAPIAccessFactory;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.tc.logging.Location;

/**
 * Session Bean implementation class DynamicDirectoryBean
 */
public class DynamicDirectoryBean implements SessionBean, Module {
    public static final String VERSION_ID ="$Id://tc/aii/30_REL/src/_adapters/_sample/java/user/module/DynamicFileName.java#1 $";
   
   
    public ModuleData process(ModuleContext moduleContext, ModuleData inputModuleData)
    throws ModuleException {
                    Location location = null;
                    AuditAccess audit = null;
                    String filename = null;
                    String dynamicpath = null;
                    String filename1 = null;
                    String filename2= null;
                    String filename3= null;
                    String filename4= null;
                    String filename5= null;
                    String filename6= null;
                    String filename7= null;
                    String filename8= null;
                    String filename9= null;
                                                   
                    String filepath1= null;
                    String filepath2= null;
                    String filepath3= null;
                    String filepath4= null;
                    String filepath5= null;
                    String filepath6= null;
                    String filepath7= null;
                    String filepath8= null;
                    String filepath9= null;
   
                    Object obj = null;
                    Message msg = null;
                    MessageKey key = null;
                    MessagePropertyKey inpfile=null;
                    MessagePropertyKey dir = null;
                   
                    try{
                                    obj = inputModuleData.getPrincipalData();
                                   msg = (Message) obj;
key = new MessageKey(msg.getMessageId(), msg.getMessageDirection());
audit = PublicAPIAccessFactory.getPublicAPIAccess().getAuditAccess();
// creating object for audit log
audit.addAuditLogEntry(key, AuditLogStatus.SUCCESS, "DynamicDirestory: New Module called");       
// getting FileName and Directory from ASMA parameters
filename = msg.getMessageProperty("http://sap.com/xi/XI/System/File", "FileName");
dynamicpath = msg.getMessageProperty("http://sap.com/xi/XI/System/File", "Directory");
audit.addAuditLogEntry(key, AuditLogStatus.SUCCESS,"input FileName:" + filename);

/*Setting the runtime parameters which we can pass from Communication channel as Adapter Module
parameters to the Module Context*/

filename1 = moduleContext.getContextData("filename1");
filename2 = moduleContext.getContextData("filename2");
 filename3 = moduleContext.getContextData("filename3");
filename4 = moduleContext.getContextData("filename4");
  filename5 = moduleContext.getContextData("filename5");
 filename6 = moduleContext.getContextData("filename6");
  filename7 = moduleContext.getContextData("filename7");
  filename8 = moduleContext.getContextData("filename8");
filename9 = moduleContext.getContextData("filename9");

filepath1 = moduleContext.getContextData("filepath1");
filepath2 = moduleContext.getContextData("filepath2");
filepath3 = moduleContext.getContextData("filepath3");
filepath4 = moduleContext.getContextData("filepath4");
filepath5 = moduleContext.getContextData("filepath5");
filepath6 = moduleContext.getContextData("filepath6");
filepath7 = moduleContext.getContextData("filepath7");
filepath8 = moduleContext.getContextData("filepath8");
filepath9 = moduleContext.getContextData("filepath9");

// setting dynamic directory
dir = new MessagePropertyKey("Directory","http://sap.com/xi/XI/System/File");

if(filename.startsWith(filename1)){
   
    dynamicpath = filepath1;                   
}
else if(filename.startsWith(filename2)) {
   
    dynamicpath = filepath2; 
}
else if(filename.startsWith(filename3)) {
   
    dynamicpath = filepath3; 
}
else if(filename.startsWith(filename4)) {
   
    dynamicpath = filepath4; 
}
                                    else if(filename.startsWith(filename5)) {
   
    dynamicpath = filepath5; 
}
                                    else if(filename.startsWith(filename6)) {
   
    dynamicpath = filepath6; 
}
                                    else if(filename.startsWith(filename7)) {
   
    dynamicpath = filepath7; 
}
                                    else if(filename.startsWith(filename8)) {
   
    dynamicpath = filepath8; 
}
                                    else if(filename.startsWith(filename9)) {
   
    dynamicpath = filepath9; 
}
//Setting message property
msg.setMessageProperty(dir,dynamicpath);            

audit.addAuditLogEntry(key, AuditLogStatus.SUCCESS,"target Directory:" + dynamicpath);
return inputModuleData;
                    }
                    catch (Exception e) {
                                    ModuleException me = new ModuleException(e);
                                    throw me; 
                                    }        
    }
    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() throws EJBException, RemoteException {
                    // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
   
    public void ejbPassivate() throws EJBException, RemoteException {
                    // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbRemove()
     */
   
    public void ejbRemove() throws EJBException, RemoteException {
                    // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
   
    public void setSessionContext(SessionContext arg0) throws EJBException,
                                    RemoteException {
                    // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see javax.ejb.TimedObject#ejbTimeout(javax.ejb.Timer)
     */
    public void ejbTimeout(Timer arg0) {
                    // TODO Auto-generated method stub
    }
    public void ejbCreate() throws javax.ejb.CreateException {
    }
}