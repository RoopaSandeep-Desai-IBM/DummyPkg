/*
 * wmMQTransactionalConnectionFactory.java
 *
 * Copyright 2002 webMethods, Inc.
 * ALL RIGHTS RESERVED
 *
 * UNPUBLISHED -- Rights reserved under the copyright laws of the United States.
 * Use of a copyright notice is precautionary only and does not imply
 * publication or disclosure.
 *
 * THIS SOURCE CODE IS THE CONFIDENTIAL AND PROPRIETARY INFORMATION OF
 * WEBMETHODS, INC.  ANY REPRODUCTION, MODIFICATION, DISTRIBUTION,
 * OR DISCLOSURE IN ANY FORM, IN WHOLE, OR IN PART, IS STRICTLY PROHIBITED
 * WITHOUT THE PRIOR EXPRESS WRITTEN PERMISSION OF WEBMETHODS, INC.
 */

package com.wm.adapter.wmmqadapter.connection;

import java.util.Locale;

import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adk.connection.WmManagedConnection;
import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;
import com.wm.adk.metadata.WmDescriptor;

/*
 * This class Represents the wmAdapter's Transactional connection Factory. It extends the
 * non-transactional wmMQConnectionFactory class by overriding only those methods that
 * deal with transactionality.
 */

public class wmMQTransactionalConnectionFactory extends wmMQConnectionFactory
{

	// If this property is set to "rollback", any active transaction
	// is rolled before the adapter disconnects. If this property
	// is set to "commit", any active transaction is committed
	// before the adapter disconnects. The default value of "none"
	// instructs the adapter to do nothing before disconnecting.    
    protected String _actionBeforeDisconnect = wmMQAdapter.getActionBeforeDisconnect();

	
    /*
     * Returns a connection to the resource.
     *
     * The subject parameter is not used in this release.
     * The cxRequestInfo parameter is not used in this release.
     * Returns an object of Tutorial2Connection that represents a connection to the resource.
     *
     * Throws AdapterConnectionException if an error occurred while creating a connection to the resource.
     */
    public WmManagedConnection createManagedConnectionObject(
                               javax.security.auth.Subject subject,
                               javax.resource.spi.ConnectionRequestInfo connectionRequestInfo)
        throws AdapterException
    {
        log(ARTLogger.INFO, 1001, "wmMQConnectionFactory:createManagedConnectionObject", "");
        log(ARTLogger.INFO, 1003, "wmMQConnectionFactory:createManagedConnectionObject",
                                  "_queueManagerName = "  + _queueManagerName +
                                  ", _hostName = " + _hostName +
                                  ", _port = " + _port +
                                  ", _channel = " + _channel);
        return new wmMQTransactionalConnection();
    }


    
	public String getActionBeforeDisconnect() {
		return this._actionBeforeDisconnect;
	}

	public void setActionBeforeDisconnect(String actionBeforeDisconnect) {
		this._actionBeforeDisconnect = actionBeforeDisconnect;
	}

    /*
     * Returns the types of transactions that are supported by the connections
     * that are created by this factory. There is no transaction support for
     * wmMQAdapter because of the limitations of the flat-file database provided with wmMQAdapter.
     * Returns a flag indicating which transaction types are supported by this connection.
     */

    public int queryTransactionSupportLevel()
	{
        return LOCAL_TRANSACTION_SUPPORT;
    }
    
    /*
     * Populates the descriptor.
     *
     * This method will be called once for each ManagedConnectionFactory.
     *
     * The d parameter is the descriptor.
     * AdapterException is thrown if the method encounters an error.
     */
    public void fillWmDescriptor(WmDescriptor d, Locale l) throws AdapterException {
        log(ARTLogger.INFO, 1001, "wmMQTransactionalConnectionFactory:fillWmDescriptor", "");
        
        fillNonSSLProperties(d, l);
		d.setGroupName(wmMQAdapterConstant.ACTION_BEFORE_DISCONNECT, wmMQAdapterConstant.CONNECTION_MANAGEMENT_SETTINGS);
		d.setValidValues(wmMQAdapterConstant.ACTION_BEFORE_DISCONNECT, new String[] {"COMMIT", "ROLLBACK"});
        
		// Fill the three SSL related properties if SSL is enabled
		fillSSLProperties(d, l);
		
        d.setDescriptions(wmMQAdapter.getInstance().getAdapterResourceBundleManager(), l);
        
        log(ARTLogger.INFO, 1002, "wmMQTransactionalConnectionFactory:fillWmDescriptor", "");
    }
}
