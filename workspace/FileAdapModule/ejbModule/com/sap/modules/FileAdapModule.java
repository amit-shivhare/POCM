package com.sap.modules;

import java.rmi.RemoteException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.Stateless;

import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;

/**
 * Session Bean implementation class FileAdapModule
 */
@Stateless
public class FileAdapModule implements FileAdapModuleRemote, FileAdapModuleLocal {

    /**
     * Default constructor. 
     */
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
	public ModuleData process(ModuleContext moduleContext, ModuleData inputModuleData) throws ModuleException
	{
		
		return inputModuleData;	
	}

}
