/**
 * 
 */
package com.sap.aii.af.lib.mp.module;

import javax.ejb.EJBLocalHome;

/**
 * @author valladares-fern-1
 *
 */
public interface ModuleLocalHome extends EJBLocalHome {

	public com.sap.aii.af.lib.mp.module.ModuleLocal create() throws javax.ejb.CreateException;

}
