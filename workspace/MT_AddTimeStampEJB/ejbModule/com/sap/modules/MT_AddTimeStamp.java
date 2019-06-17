package com.sap.modules;

import java.rmi.RemoteException;
import javax.ejb.EJBException;
import javax.ejb.Local;
import javax.ejb.LocalHome;
import javax.ejb.Remote;
import javax.ejb.RemoteHome;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.aii.af.lib.mp.module.ModuleHome;
import com.sap.aii.af.lib.mp.module.ModuleLocal;
import com.sap.aii.af.lib.mp.module.ModuleLocalHome;
import com.sap.aii.af.lib.mp.module.ModuleRemote;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;
import com.sap.engine.interfaces.messaging.api.auditlog.*;

@Stateless(name="MT_MT_AddTimeStamp")
@Local(value ={ModuleLocal.class} )
@LocalHome(value=ModuleLocalHome.class)
@Remote(value ={ModuleRemote.class} )
@RemoteHome(value=ModuleHome.class)




public class MT_AddTimeStamp implements SessionBean, TimedObject {
    /**
     *
     */
    private static final long serialVersionUID = 8087133996851499898L;
    private static final String C_FILENAME_PREFIX_TAG         = "FILENAME_PREFIX";
    private static final String C_FILENAME_SUFIX_TAG          = "FILENAME_SUFIX";
    private static final String C_TIMESTAMP_FORMAT_TAG    = "TIMESTAMP_FORMAT";
    private static MessageKey amk;
    private static AuditAccess Audit = null;
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
    private void addInfo (String msg){
        if (amk != null){
          Audit.addAuditLogEntry(amk, AuditLogStatus.SUCCESS,msg);
          
          
        }
        else{
          System.out.println(msg);
        }
    }
    public ModuleData process(ModuleContext moduleContext, ModuleData inputModuleData)    throws ModuleException {
    
         String fileNamePrefix   = moduleContext.getContextData(C_FILENAME_PREFIX_TAG);
         String fileNameSufix    = moduleContext.getContextData(C_FILENAME_SUFIX_TAG);
         String timeStampFormat  = moduleContext.getContextData(C_TIMESTAMP_FORMAT_TAG);
     
         String timeStamp;
         String fileName;
         Message msg;
         MessagePropertyKey key;
     
         try{         
              msg = (Message) inputModuleData.getPrincipalData();
             amk               = new MessageKey(msg.getMessageId(), msg.getMessageDirection());
         
             addInfo("--> Start AddTimeStamp Module");
         
               if ( fileNamePrefix == null ){
                 fileNamePrefix = "";
            }
        
            if ( fileNameSufix == null){
                 fileNameSufix = "";
            }
     
            if (timeStampFormat == null) {
                 timeStampFormat = "yyyyMMdd-HHmmss";
             }
         
             addInfo("--> " + C_FILENAME_PREFIX_TAG    + " = " + fileNamePrefix);
             addInfo("--> " + C_FILENAME_SUFIX_TAG     + " = " + fileNameSufix);
             addInfo("--> " + C_TIMESTAMP_FORMAT_TAG   + " = " + timeStampFormat);
         
             try{
                 timeStamp = new java.text.SimpleDateFormat(timeStampFormat).format(new java.util.Date ());             
             }         
             catch( Exception e){
                 addInfo("--> TimeStamp could not be generated.");
                 timeStamp = "19000101-000000-" + Math.random();
             }
         
             addInfo("--> TimeStamp = " + timeStamp);         
         
             fileName = fileNamePrefix + timeStamp + fileNameSufix;
         
             addInfo("--> FileName = " + fileName);         
             key = new MessagePropertyKey("FileName","http://sap.com/xi/XI/System/File");
            msg.removeMessageProperty(key);
            msg.setMessageProperty(key,fileName);
        
             addInfo("--> Finish AddTimeStamp Module");
 
         }
        catch (Exception e) {
            addInfo ("Exception: " + e.getMessage());
            StackTraceElement[] stack = e.getStackTrace();
            for ( int i=0; i<stack.length; i++){
                addInfo (stack[i].toString());
            }    
            throw new ModuleException(e.getMessage(),e);
        }    
    
        return inputModuleData;
    }
}