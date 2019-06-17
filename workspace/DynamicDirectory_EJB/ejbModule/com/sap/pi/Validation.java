package com.sap.pi;

import javax.ejb.Stateless;
import java.rmi.RemoteException;
import java.util.Enumeration;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

/**
 * Session Bean implementation class Validation
 */
@Stateless(name="ValidationBean")


public class Validation {

    /**
     * Default constructor. 
     */
    public Validation() {
        // TODO Auto-generated constructor stub
    }

}
