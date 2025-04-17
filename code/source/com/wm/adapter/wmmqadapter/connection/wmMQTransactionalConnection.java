/*
 * wmMQTransactionalConnection.java
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.resource.ResourceException;
import javax.resource.spi.LocalTransaction;

import com.ibm.mq.MQException;
import com.wm.adapter.wmmqadapter.WmMQAdapterUtils;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adk.log.ARTLogger;

 /*
 * This class represents a Transactional connection to the wmMQAdapter's resource and
 * is returned by its corresponding ConnectionFactory's createManagedConnectionObject method.
 * The wmMQAdapter supports local transactionality.
 */
public class wmMQTransactionalConnection extends wmMQConnection implements LocalTransaction
{
    // Trax 1-14WOV9 : Begin
	// Since the connections with the same name can be part of the current connection
	// it is safe to use a List instead of a Hashtable
	private List _connectionsInTransaction = null;
    // Trax 1-14WOV9 : End
	
	private boolean _listenerConnection = false;

    /*
     * Constructor.
     */
    public wmMQTransactionalConnection()
    {
        super();
    }

	 /**
	  * Return a {@link javax.resource.spi.LocalTransaction} object if this connection type supports
	  * local transactions. If local transactions are supported, adapter writers must implement this
	  * method to return the <code>LocalTransaction</code> object associated with the resource.
	  *
	  * @return the resource's <code>LocalTransaction</code> object
	  * @throws ResourceException if a problem was encountered while retrieving the
	  * transaction object.
	  * @see javax.resource.spi.LocalTransaction
	  */
	 public LocalTransaction getLocalTransaction() throws ResourceException
	 {
		 //FIX:  1-99V7P: must through NotSupportedException
		 return this;
	 }

	/*
	 * Begin a Transaction (UOW)
	 */
	public void begin() throws ResourceException
	{
		log(ARTLogger.INFO, 1001, "wmMQTransactionalConnection.begin", "");
		_inTransaction = true;
		log(ARTLogger.INFO, 1003, "begin", "_inTransaction=" + ((_inTransaction) ? "true" : "false")); //NOSONAR
		log(ARTLogger.INFO, 1002, "wmMQTransactionalConnection.begin", "");
	}

    /*
     * Commit all uncommitted messages on the queue
     */
    public void commit() throws ResourceException
    {
        log(ARTLogger.INFO, 1001, "wmMQTransactionalConnection.commit", "");
        try
        {
//			Trax 1-XSO2S and 1-XHJ94. Make sure that the _qMgr is still connected
        	if (_qMgr != null) 
       			synchronized (_qMgr)
				{
//              if (this._openedForInput || this._openedForOutput)
       				if ((_qMgr.isOpen()) && (_qMgr.isConnected()))
       					_qMgr.commit();
				}
			_inTransaction = false;
			
        }
        catch (MQException mqe)
        {
            wmMQConnectionFactory connFactory = (wmMQConnectionFactory)_factory;
            String[] parms = {
                _queueName,
				connFactory.getQueueManagerName(),
                "" + mqe.completionCode,
                "" + mqe.reasonCode};
//            String errorMsg = wmMQException.format("3038", parms);
//			log(ARTLogger.ERROR, 1003, "wmMQTransactionalConnection.commit", errorMsg);
	        ARTLogger logger = ((wmMQAdapter)wmMQAdapter.getInstance()).getLogger();
	        logger.logDebugPlus(ARTLogger.ERROR, 3038, parms);
		    
			//Trax 1-VMK5X. Listener restart not performed if rollback() throws an exception
			//Instead, just return, and let the waitForData() throw the exception.
			if (_listenerConnection)
				return;
//			ResourceException re = new ResourceException(errorMsg);
//			re.setLinkedException(mqe);
//          throw re;
			if (WmMQAdapterUtils.fatalException(mqe.reasonCode))
				throw wmMQAdapter.getInstance().createAdapterConnectionException(1055, 
																				 new String[]{"Commit", mqe.getMessage()}, 
																				 mqe);
			else
				throw wmMQAdapter.getInstance().createAdapterException(1055, 
																	   new String[]{"Commit", mqe.getMessage()}, 
																	   mqe);
				
        } 
        finally {
            // Trax 1-14WOV9 : Begin 
            // For transactional connections reset the inUse flag.
    		setInUse(false);
            // Trax 1-14WOV9 : End
        }

		if (_connectionsInTransaction != null)
		{
		    // Trax 1-14WOV9 : Begin
			for (Iterator econns = _connectionsInTransaction.iterator() ; econns.hasNext(); )
			{
//				String connName = (String)econns.nextElement();
//				wmMQTransactionalConnection oneConn = (wmMQTransactionalConnection)_connectionsInTransaction.get(connName);
				wmMQTransactionalConnection oneConn = (wmMQTransactionalConnection)econns.next();
				try
				{
					log(ARTLogger.INFO, 1003, "wmMQTransactionalConnection:commit", "committing " + oneConn);
					oneConn.commit(); 
				}
				catch (ResourceException re)
				{
				}
			}
		    // Trax 1-14WOV9 : End
			
			_connectionsInTransaction.clear();
		}
        
        log(ARTLogger.INFO, 1002, "wmMQTransactionalConnection.commit", "");
    }

    /*
     * rollback all uncommitted messages on the queue
     */
    public void rollback() throws ResourceException
	{
        log(ARTLogger.INFO, 1001, "wmMQTransactionalConnection.rollback", "");
        try 
        {
//			Trax 1-XSO2S and 1-XHJ94. Make sure that the _qMgr is still connected
        	if (_qMgr != null) 
       			synchronized (_qMgr)
				{
//              if (this._openedForInput || this._openedForOutput)
       				if ((_qMgr.isOpen()) && (_qMgr.isConnected()))
            	_qMgr.backout();
			_inTransaction = false;
			}
        }
        catch (MQException mqe) {
            wmMQConnectionFactory connFactory = (wmMQConnectionFactory)
                _factory;
            String[] parms = {
				_queueName,
                connFactory.getQueueManagerName(),
                "" + mqe.completionCode,
                "" + mqe.reasonCode};
//			String errorMsg = wmMQException.format("3039", parms);
//			log(ARTLogger.ERROR, 1003, "wmMQTransactionalConnection.rollback", errorMsg);
	        ARTLogger logger = ((wmMQAdapter)wmMQAdapter.getInstance()).getLogger();
	        logger.logDebugPlus(ARTLogger.ERROR, 3039, parms);
		    
			//Trax 1-VMK5X. Listener restart not performed if rollback() throws an exception
			//Instead, just return, and let the waitForData() throw the exception.
			if (_listenerConnection)
				return;
			//			ResourceException re = new ResourceException(errorMsg);
//			re.setLinkedException(mqe);
//			throw re;
			if (WmMQAdapterUtils.fatalException(mqe.reasonCode))
				throw wmMQAdapter.getInstance().createAdapterConnectionException(1055, 
																				 new String[]{"Rollback", mqe.getMessage()}, 
																				 mqe);
			else
				throw wmMQAdapter.getInstance().createAdapterException(1055, 
																	   new String[]{"Rollback", mqe.getMessage()}, 
																	   mqe);
        }
        finally {
            // Trax 1-14WOV9 : Begin 
            // For transactional connections reset the inUse flag.
    		setInUse(false);
            // Trax 1-14WOV9 : End
        }

		if (_connectionsInTransaction != null)
		{
		    // Trax 1-14WOV9 : Begin
			for (Iterator econns = _connectionsInTransaction.iterator() ; econns.hasNext(); )
			{
//				String connName = (String)econns.nextElement();
//				wmMQTransactionalConnection oneConn = (wmMQTransactionalConnection)_connectionsInTransaction.get(connName);
				wmMQTransactionalConnection oneConn = (wmMQTransactionalConnection)econns.next();
				try
				{
					log(ARTLogger.INFO, 1003, "wmMQTransactionalConnection:rollback", "rolling back " + oneConn);
					oneConn.rollback(); 
				}
				catch (ResourceException re)
				{
				}
			}
		    // Trax 1-14WOV9 : End
			
			_connectionsInTransaction.clear();
		}
        
        log(ARTLogger.INFO, 1002, "wmMQTransactionalConnection.rollback", "");
    }
    public void cacheConnectionInTransaction(wmMQTransactionalConnection conn)
    {
		log(ARTLogger.INFO, 1001, "wmMQTransactionalConnection:cacheConnectionInTransaction", "");
	    // Trax 1-14WOV9 : Begin
		// Since the connections with the same name can be part of the current connection
		// it is safe to use a List instead of a Hashtable. 
		// In a scenario where the same connections parameters are overriden twice 
		// in the same flow, it is possible to have two connections with the same name
		// in which case the first one will not be commited/rolledback when the parent
		// connection is commited or rolledback.
    	if (_connectionsInTransaction == null)
    		_connectionsInTransaction = new ArrayList();
		
		log(ARTLogger.INFO, 1003, "wmMQConnectionFactory:cacheConnectionInTransaction", "caching " + conn);
		_connectionsInTransaction.add(conn);
		log(ARTLogger.INFO, 1002, "wmMQTransactionalConnection.cacheConnectionInTransaction", "");
	    // Trax 1-14WOV9 : End
    }
    
    public boolean isListenerConnection()
    {
    	return _listenerConnection;
    }
    
    public void setListenerConnection(boolean flag)
    {
    	_listenerConnection = flag;
    }
}