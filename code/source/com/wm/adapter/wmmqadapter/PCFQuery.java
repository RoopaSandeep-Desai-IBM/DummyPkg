/*
 * Copyright (c) 1996-2003, webMethods Inc.  All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * webMethods, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with WebMethods.
 *
 * webMethods MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. webMethods SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 *
 */
package com.wm.adapter.wmmqadapter; 

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Hashtable;
import java.util.Vector;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;

import com.ibm.mq.pcf.PCFException;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;
import com.wm.adapter.wmmqadapter.admin.Service;
import com.wm.adapter.wmmqadapter.connection.wmMQConnectionFactory;
import com.wm.adapter.wmmqadapter.connection.wmMQKeyStoreManager;
import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;
import com.wm.util.ServerException;

public class PCFQuery
{
	private static String				_qMgrName = "";
	//private static String				_hostName = "";
	private static int					_port = 1414;
	//private static String				_channel = "";
	private static int					_ccsid = 819;
	private static MQQueueManager 		_qMgr = null;
	private static MQQueue				_adminQueue = null;
	private static MQQueue				_replyQueue = null;
	private static MQPutMessageOptions 	_pmo = null;
	private static MQGetMessageOptions 	_gmo = null;
	private static String				_replyToQueueName;
	private static String[]				_queuenames;

	public PCFQuery()
	{
	}


//	public static String[] listQueues(String qMgrName, 
//									  String hostName, 
//									  int port, 
//									  String channel, 
//									  int ccsid)
//							throws AdapterException
//	public static String[] listQueues(String qMgrName, 
//									  String hostName, 
//									  int port, 
//									  String channel, 
//									  int ccsid,
//									  String sslKeyStore,
//									  String sslPassword,
//									  String sslCipherSpec)
//						   throws AdapterException
//	{
//
//		//try
//		//{
//			connectToQMgr(qMgrName, hostName, port, channel, ccsid, sslKeyStore, sslPassword, sslCipherSpec);
//		//}
//		//catch (AdapterException e)
//		//{
//		//	e.printStackTrace();
//		//	return null;
//		//}
//
//		return listQueues();
//	}

	public static String[] listQueues(String qMgrName, int ccsid, String ccdtFilePath) throws AdapterException
	{
		//try
		//{
			connect(qMgrName, ccsid, ccdtFilePath);
		//}
		//catch (AdapterException e)
		//{
		//	e.printStackTrace();
		//	return null;
		//}

		return listQueues();
	}

	public static String[] listQueues() throws AdapterException
	{
		try {
			Vector names = new Vector();
			names.addAll(listLocalQueues());
			Vector clusterQueues = listClusterQueues();
			for (int i = 0 ; i < clusterQueues.size() ; i++)
			{
				String onequeue = (String)clusterQueues.elementAt(i);
				if (!names.contains(onequeue))
				names.add(onequeue);
			}
			String[] queuenames = new String[0];
			queuenames = (String[])names.toArray(queuenames);
			
			return queuenames;
		} 
		finally 
		{
			// Trax 1-1097NU : The code to disconnect from the queue manager is moved to the 
			// finally block so that the connection is closed even when an exception is thrown.
			try
			{
				if(_qMgr != null && _qMgr.isConnected()) {
					_qMgr.close();
					_qMgr.disconnect();
				}
			}
			catch (MQException mqe)
			{
				
			}
		}
	}

	public static Vector listLocalQueues() throws AdapterException
	{
		Vector localnames = new Vector();
		try
		{
			//Create/send PCF command message to Admin queue
			MQMessage msg = createPCFMessage(MQConstants.MQCMD_INQUIRE_Q_NAMES, MQConstants.MQQT_ALL);
			_adminQueue.put(msg, _pmo);

			//The MQCMD_INQUIRE_Q_NAMES command returns one message with a String[] of queue names
			byte[] correlId = msg.correlationId;
			_gmo.options = 16385;
			_gmo.waitInterval = 30000;
			MQMessage reply = new MQMessage();
			reply.correlationId = correlId;
			reply.characterSet = _ccsid;
			_replyQueue.get(reply, _gmo);

			if (reply.getDataLength() > 56)
			{
				reply.skipBytes(52);
				int count = reply.readInt();
				int qnamelen = reply.readInt();
				for (int q = 0 ; q < count ; q++)
				{
					byte[] qname = new byte[qnamelen];
					reply.readFully(qname);
                    //System.out.println("Found queue " + new String(qname, "CP" + _ccsid));
                    if (!(new String(qname).startsWith("COM.IBM.MQ.PCF")))
                    {
                        //If the local JVM does not support the CCSID, this
                    	//statement generates an UnsupportedEncodingException
                    	try
						{
                        	localnames.addElement(new String(qname, "CP" + _ccsid));
						}
                        catch (UnsupportedEncodingException uee)
						{
                        	localnames.addElement(new String(qname));
                        }
                    }
				}
			}
			else
			{
				reply.skipBytes(24);
				String[] parray = new String[3];
                                parray[0] = _qMgrName;
				parray[1] = Integer.toString(reply.readInt());
				parray[2] = Integer.toString(reply.readInt());
				throw wmMQAdapter.getInstance().createAdapterException(2063, parray);
			}
		}
		catch (MQException mqe)
		{
			//mqe.printStackTrace();
                        String[] parray = new String[3];
                        parray[0] = _qMgrName;
                        parray[1] = Integer.toString(mqe.completionCode);
                        parray[2] = Integer.toString(mqe.reasonCode);
                        throw wmMQAdapter.getInstance().createAdapterException(2063, parray);
		}
		catch (IOException ioe)
		{
			//ioe.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return localnames;
	}

	public static Vector listClusterQueues()  throws AdapterException
	{
		Vector clusternames = new Vector();
		try
		{
			//Create/send PCF command message to Admin queue
			MQMessage msg = createPCFMessage(MQConstants.MQCMD_INQUIRE_Q, MQConstants.MQQT_CLUSTER);
			_adminQueue.put(msg, _pmo);

			//The MQCMD_INQUIRE_Q command returns an array of  MQMessage objects, each describing one cluster queue
			byte[] correlId = msg.correlationId;
			_gmo.options = 16385;
			_gmo.waitInterval = 30000;
			boolean done = false;
			do
			{
				MQMessage reply = new MQMessage();
				reply.characterSet = _ccsid;
				reply.correlationId = correlId;
				_replyQueue.get(reply, _gmo);
				clusternames.addElement(reply);
				reply.skipBytes(20);
				done = reply.readInt() == 1;
				reply.seek(0);
			} while(!done);

			int vsize = clusternames.size();
			for (int q = 0 ; q < vsize ; q++)
			{
				MQMessage reply = (MQMessage)clusternames.remove(0);
				reply.skipBytes(24);
				int compcode = reply.readInt();
				int reasoncode = reply.readInt();
				if (compcode != MQException.MQCC_OK)
				{
					if (reasoncode == MQConstants.MQRC_UNKNOWN_OBJECT_NAME)
						return new Vector();

					String[] parray = new String[3];
                                        parray[0] = _qMgrName;
					parray[1] = "" + compcode;
					parray[2] = "" + reasoncode;
                                        throw wmMQAdapter.getInstance().createAdapterException(2063, parray);
				}

				int parmcount = reply.readInt();
				for (int p = 0 ; p < parmcount ; p++)
				{
					int type = reply.readInt();
					int len = reply.readInt();
					int parmtype = reply.readInt();
//                    if (q == 0)
//                        System.out.println("queue parameter=" + parmtype);
					if (parmtype == MQConstants.MQCA_Q_NAME)
					{
						String ccsid = "CP" + reply.readInt();
						int valuelen = reply.readInt();;
						byte[] valuebytes = new byte[valuelen];
						reply.readFully(valuebytes);
						String onename = "";
                        //If the local JVM does not support the CCSID, this
                    	//statement generates an UnsupportedEncodingException
                    	try
						{
                    		onename = new String(valuebytes, ccsid);
						}
                        catch (UnsupportedEncodingException uee)
						{
                        	onename = new String(valuebytes);
                        }
						clusternames.add(onename);
                                                //System.out.println("Found queue " + onename);
					}
					else
						reply.skipBytes(len - 12);
				}
			}
		}
		catch (MQException mqe)
		{
			mqe.printStackTrace();
                        String[] parray = new String[3];
                        parray[0] = _qMgrName;
                        parray[1] = Integer.toString(mqe.completionCode);
                        parray[2] = Integer.toString(mqe.reasonCode);
                        throw wmMQAdapter.getInstance().createAdapterException(2063, parray);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return clusternames;
	}

	public static MQMessage createPCFMessage(int cmd, int qtype) throws AdapterException
	{
		MQMessage msg = new MQMessage();

		try
		{
			msg.characterSet = _ccsid;
			msg.messageType = 1;
			msg.expiry = 30000;
			msg.feedback = 0;
			msg.format = "MQADMIN ";
			msg.replyToQueueName = _replyToQueueName;

			msg.writeInt(1);
			msg.writeInt(36);
			msg.writeInt(1);
			msg.writeInt(cmd);
			msg.writeInt(1);
			msg.writeInt(1);
			msg.writeInt(0);
			msg.writeInt(0);
			msg.writeInt(2);
			msg.writeInt(4);
			msg.writeInt(24);
			msg.writeInt(2016);
			msg.writeInt(_ccsid);
			msg.writeInt(1);
			msg.writeString("*   ");
			msg.writeInt(3);
			msg.writeInt(16);
			msg.writeInt(20);
			msg.writeInt(qtype);
		}
		catch (IOException ioe)
		{
                    String[] parray = new String[1];
                    parray[0] = ioe.getMessage();
                    throw wmMQAdapter.getInstance().createAdapterException(2055, parray);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return msg;
	}

//  Trax 1-QPSPQ - SSL Support
//	private static void connectToQMgr(String qMgrName, 
//			  String hostName, 
//			  int port, 
//			  String channel, 
//			  int ccsid) throws AdapterException
	private static MQQueueManager connectToQMgrWithKeystoreFile(String qMgrName,
			String sslKeyStore, String sslPassword, String sslCipherSpec, URL ccdtUrl, Hashtable<String, Object> props)
			throws AdapterException {

		// Temp storage for SSL-related system properties
		String originalKeystore = "";
		String originalKeystorePassword = "";
		String originalCipherSuite = "";
		MQQueueManager qmanager=null;
		// Trax 1-QPSPQ - SSL Support
		// This class temporarily modifies the SSL-related System properties.
		// Therefore,
		// this code segment must be synchronized to make sure no other
		// connection
		// requests are processed while the System properties are modified.
		try {
			synchronized (wmMQAdapter.getInstance()) {
				// SSL Specific parameters
				originalKeystore = System.getProperty("javax.net.ssl.keyStore");
				if (originalKeystore == null)
					originalKeystore = "";

        		originalKeystorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        		if (originalKeystorePassword == null)
        			originalKeystorePassword = "";

                originalCipherSuite = MQEnvironment.sslCipherSuite;
                if (originalCipherSuite == null)
                	originalCipherSuite = "";

                System.setProperty("javax.net.ssl.keyStore", sslKeyStore);
    	    	System.setProperty("javax.net.ssl.keyStorePassword", sslPassword);
				System.setProperty("javax.net.ssl.trustStore", sslKeyStore);
				System.setProperty("javax.net.ssl.trustStorePassword",
						sslPassword);
				MQEnvironment.sslCipherSuite = wmMQConnectionFactory
						.suite2Spec(sslCipherSpec);
				if(ccdtUrl!=null){
					qmanager = new MQQueueManager(qMgrName, props, ccdtUrl);
				}else{
					qmanager = new MQQueueManager(qMgrName, props);
				}
			}
		} catch (MQException e) {
			log(ARTLogger.INFO, 1003, "connect", "Failed to connect to "
					+ qMgrName);
			if ((_qMgr == null) || (!_qMgr.isConnected())) {
				String[] parray = new String[2];
				parray[0] = qMgrName;
				parray[1] = String.valueOf(e.reasonCode);
				throw wmMQAdapter.getInstance().createAdapterException(2062,
						parray);
			}
		} finally {
			if (wmMQAdapter.getSSLListing().trim().length() > 0 || wmMQAdapter.getSSLSupport()) {
				// Reset SSL-related system properties
				System.setProperty("javax.net.ssl.keyStore", originalKeystore);
				System.setProperty("javax.net.ssl.keyStorePassword",
						originalKeystorePassword);
				MQEnvironment.sslCipherSuite = originalCipherSuite;
			}
		}
		return qmanager;

	}

//	private static void connectToQMgr(String qMgrName, int ccsid, String ccdtFilePath) throws AdapterException
//    {
//
////	  Trax 1-QPSPQ - SSL Support
//        //This class temporarily modifies the SSL-related System properties. Therefore, 
//        //this code segment must be synchronized to make sure no other connection  
//        //requests are processed while the System properties are modified.
//       Hashtable
//                MQEnvironment.hostname = null;
//                MQEnvironment.port = 0;
//                MQEnvironment.channel = null;
//                connect(qMgrName, ccsid, ccdtFilePath);
//		}
////      Trax 1-QPSPQ - SSL Support
//    }

    private static void connect(String qMgrName, int ccsid, String ccdtFilePath) throws AdapterException
	{
		_ccsid = ccsid;
		_qMgrName = qMgrName;
        String reasoncode = "0";
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(MQConstants.HOST_NAME_PROPERTY, "");
        props.put(MQConstants.PORT_PROPERTY, 0);
        props.put(MQConstants.CHANNEL_PROPERTY, "");
        
		try
		{
			// Trax 1-12K2HK : Setting the CCSID on the MQEnvironment with MQCCSI_Q_MGR and MQCCSI_INHERIT 
			//				   is not allowed. Instead set the CCSID on the mq message in the Get, Peek, Put, 
			// 				   RequestReply and Listener services which is already done. 
			//				   If the CCSID provided on the connection factory is not MQCCSI_Q_MGR or MQCCSI_INHERIT
			//				   set the MQEnvironment's CCSID with that value, otherwise set it with the encoding
			//				   property of the jvm.			
			if(	   ccsid == MQConstants.MQCCSI_Q_MGR
				|| ccsid == MQConstants.MQCCSI_INHERIT
			  ) {
				ccsid = WmMQAdapterUtils.getSystemDefaultCharacterSetId();
			}
			// Trax 1-12K2HK : End
			
			MQEnvironment.CCSID = ccsid;
			
			URL ccdtUrl = null;
			if(ccdtFilePath!=null && ccdtFilePath.trim().length()>0){
				try {
					ccdtUrl = new URL("file:///"+ccdtFilePath);
				} catch (MalformedURLException e) {
					// log the message and continue connection creation without CCDT
					log(ARTLogger.ERROR, 1055, "Invalid CCDT URL "+ccdtFilePath+", Thus Creating Connection without CCDt URl ", e.getMessage());
					
				}
			}
			if(ccdtUrl!=null){
				_qMgr = new MQQueueManager(_qMgrName, props, ccdtUrl);
			}else{
				_qMgr = new MQQueueManager(_qMgrName, props);
			}
			if(_qMgr!=null){
				_ccsid = _qMgr.getCharacterSet();
	
				_replyToQueueName = "COM.IBM.MQ.PCF.PCFMESSAGEAGENT" + Long.toString(System.currentTimeMillis(), 10) + "...";
				_adminQueue = _qMgr.accessQueue(_qMgr.getCommandInputQueueName(), 16, "", "", "mqm");
				_replyQueue = _qMgr.accessQueue("SYSTEM.DEFAULT.MODEL.QUEUE", 4, "", _replyToQueueName, "mqm");
				_pmo = new MQPutMessageOptions();
				_gmo = new MQGetMessageOptions();
			}
		}
		catch (MQException mqe)
		{
                    log(ARTLogger.INFO, 1003, "connect", "Failed to connect to " + qMgrName);
                        if (MQEnvironment.hostname != null)
                            log(ARTLogger.INFO, 1003, "connect",
                                                        "hostname=" + MQEnvironment.hostname +
                                                        "port=" + MQEnvironment.port +
                                                        ",channel=" + MQEnvironment.channel);
			//mqe.printStackTrace();
                        reasoncode = Integer.toString(mqe.reasonCode);
                        _qMgr = null;
		}
		if ((_qMgr == null) ||(!_qMgr.isConnected()))
                {
                    String[] parray = new String[2];
                    parray[0] = qMgrName;
                    parray[1] = reasoncode;
                    throw wmMQAdapter.getInstance().createAdapterException(2062, parray);
                }
	}

    protected static void log(int level, int minor, String arg0, String arg1)
	{
       	ARTLogger logger = ( (wmMQAdapter) wmMQAdapter.getInstance()).getLogger();
        if (logger == null)
        {
            System.out.println("Logger is null");
            return;
        }
        
        //Trax 1-WVILA. Allow user to override the logging level of adapter messages. 
        if (wmMQAdapter.getLogLevelOverrides().containsKey("" + minor))
        	level =  Integer.parseInt((String)wmMQAdapter.getLogLevelOverrides().get("" + minor)) - ARTLogger.DEBUG;       	
       
        String[] args = new String[2];
        args[0] = arg0;
        args[1] = arg1;
        //Trax log 1-S4OHA - move adapter logging to DEBUG PLUS
        logger.logDebugPlus(level, minor, args);
    }

        public static void parseArgs(String[] args)
	{
        String _hostName = "";
        String _channel = "";
		if (args.length < 1)
		{
			System.out.println("Usage:");
			System.out.println("newPCFQuery <qmgrname> <hostname> <port> <channel> <CCSID>");
			System.exit(1); //NOSONAR
		}
		_qMgrName = args[0];
		if (args.length > 1)
		{
			_hostName = args[1];
			if (args.length > 2)
			{
				_port = parseInt(args[2], _port);
				if (args.length > 3)
				{
					_channel = args[3];
					if (args.length > 4)
						_ccsid = parseInt(args[4], _ccsid);
				}
			}
		}
	}

	//Utility method to safelt parse an int from a string
	private static int parseInt(String in, int defaultvalue)
	{
		int returnvalue = 0;
		try
		{
			returnvalue = Integer.parseInt(in);
		}
		catch (java.lang.NumberFormatException nfe)
		{
			returnvalue = defaultvalue;
		}
		return returnvalue;
	}

//	public static void main(String[] args)
//	{
//		PCFQuery.parseArgs(args);
//		//PCFQuery pcfQuery = new PCFQuery();
//
//		//String[] queuenames = new String[0];
//		try
//		{
//			PCFQuery._queuenames = PCFQuery.listQueues(PCFQuery._qMgrName, 
//													   PCFQuery._hostName, 
//													   PCFQuery._port, 
//													   PCFQuery._channel, 
//													   PCFQuery._ccsid, 
////													  Trax 1-QPSPQ - SSL Support
//													   "",					//SSLKeyStore 
//													   "",					//SSLKeyStorePassword 
//													   "");					//SSLCipherSpec
//		}
//		catch (AdapterException mqe)
//		{
//			//mqe.printStackTrace();
//		}
//
//		for (int i = 0 ; i < PCFQuery._queuenames.length ; i++)
//			System.out.println("queue: " + PCFQuery._queuenames[i]);
//	}


	
	/**
	 * 
	 * Method that lists the queue present in the specified host name , server connection channel and queue manager.
	 * It is implemented using the examples provided by the IBM websphere mq 
	 * 
	 * @param qMgrName
	 * @param hostName
	 * @param port
	 * @param channel
	 * @param ccsid
	 * @param sslKeyStore
	 * @param sslPassword
	 * @param sslCipherSpec
	 * @return
	 * @throws AdapterException
	 */
	public static String[] listQueuesByPCFCmd(String qMgrName, String hostName,
			int port, String channel, int ccsid, String sslKeyStore,
			String sslPassword, String sslCipherSpec, String sslKeyStoreAlias,
			String sslTrustStoreAlias, String ccdtFilePath, String userId, String password) throws AdapterException {
		
		PCFMessageAgent	agent = null;
		PCFMessage 	request;
		PCFMessage [] 	responses;
		String [] 	names = null;
		MQQueueManager qManager = null;
		try {

			 log(ARTLogger.DEBUG, 1003, "connect", "Connecting to  Queue Manager :" + qMgrName
					 +" Host/port : "+hostName+":"+port+" Server Connection Channel : "+channel);
			
			//connecting to the queue manager
			qManager = getQmanager(qMgrName, hostName, port, channel, ccsid, sslKeyStore,
					sslPassword, sslCipherSpec, sslKeyStoreAlias,
					sslTrustStoreAlias, ccdtFilePath, userId, password);

			// creating a PCF Agent
			agent = new PCFMessageAgent(qManager);

			 log(ARTLogger.DEBUG, 1003,"connect" ,"PCF Agent created");
			
			request = new PCFMessage (MQConstants.MQCMD_INQUIRE_Q_NAMES);
			request.addParameter (MQConstants.MQCA_Q_NAME, "*");
			request.addParameter (MQConstants.MQIA_Q_TYPE, MQConstants.MQQT_ALL);
			
			//sending the inquire query message to the server connection channel
			responses = agent.send (request);
			log(ARTLogger.DEBUG, 1003,"connect" ,"PCF Message Sent");
			
			//Response to the inquire query message will be array of queue names in the specified server connection channel
			names = (String []) responses [0].getParameterValue (MQConstants.MQCACF_Q_NAMES);
			log(ARTLogger.DEBUG, 1003,"connect" ,"List of queues : "+names);
			
		} catch (PCFException pcfe)
		{
			PCFMessage[] errResponses = (PCFMessage []) pcfe.exceptionSource;
			String[] parray = new String[errResponses.length];
			for (int i = 0; i < errResponses.length; i++) {
				parray [i] = errResponses [i].toString();
			}
			log(ARTLogger.ERROR, 1003,"connect" ,"Error while finding the queues : "+pcfe.getCause());
			throw wmMQAdapter.getInstance().createAdapterException(2055, parray);
		} catch (MQException mqException) {
			
			String[] parray = new String[1];
            parray[0] = mqException.getMessage();
            log(ARTLogger.ERROR, 1003,"connect" ,"Error while finding the queues : "+mqException.getCause());
            throw wmMQAdapter.getInstance().createAdapterException(2055, parray);
            
		} catch (IOException ioException) {
			
			String[] parray = new String[1];
            parray[0] = ioException.getMessage();
            log(ARTLogger.ERROR, 1003,"connect" ,"Error while finding the queues : "+ioException.getCause());
            throw wmMQAdapter.getInstance().createAdapterException(2055, parray);
            
		} finally {
			if ( agent != null) {
			try {
					agent.disconnect();
				} catch (MQException mqException) {
					
					String[] parray = new String[1];
		            parray[0] = mqException.getMessage();
		            log(ARTLogger.ERROR, 1003,"connect" ,"Error while releasing PCF Agent : "+mqException.getCause());
				}
			} 
			if(_qMgr != null && _qMgr.isConnected()) {
			 	try {
					_qMgr.close();
					_qMgr.disconnect();
				} catch (MQException mqException) {
					log(ARTLogger.ERROR, 1003,"connect" ,"Error while releasing queue manager : "+mqException.getCause());
				}
			}
			if(qManager != null && qManager.isConnected()) {
			 	try {
			 		qManager.close();
			 		qManager.disconnect();
				} catch (MQException mqException) {
					log(ARTLogger.ERROR, 1003,"connect" ,"Error while releasing queue manager : "+mqException.getCause());
				}
			}
			
		}
		
		return names;
	}
	
	@SuppressWarnings("squid:S1541")
	public static MQQueueManager getQmanager(String qMgrName, String hostName, int port,
			String channel, int ccsid, String sslKeyStore, String sslPassword,
			String sslCipherSpec, String sslKeyStoreAlias,
			String sslTrustStoreAlias, String ccdtFilePath, String userId, String password) throws AdapterException {
		MQQueueManager qmanager = null;
		URL ccdtUrl = null;
		int mqexCompletionCode = 0;
		int mqexReasonCode = 0;
		
		if(ccdtFilePath!=null && ccdtFilePath.trim().length()>0){
			try {
				ccdtUrl = new URL("file:///"+ccdtFilePath);
			} catch (MalformedURLException e) {
				// log the message and continue connection creation without CCDT
				log(ARTLogger.ERROR, 1055, "Invalid CCDT URL "+ccdtFilePath+", Thus Creating Connection without CCDt URl ", e.getMessage());
				
			}
		}
		try {
			Hashtable<String, Object> props = new Hashtable<String, Object>();
			props.put(MQConstants.HOST_NAME_PROPERTY, hostName);
			props.put(MQConstants.PORT_PROPERTY, port);
			props.put(MQConstants.CHANNEL_PROPERTY, channel);
			if (userId != null && userId.length() > 0) {
				props.put(MQC.USER_ID_PROPERTY, userId);
				if (password != null && password.length() > 0) {
					props.put(MQC.PASSWORD_PROPERTY, password);
				}
			} else {
				props.put(MQC.USER_ID_PROPERTY, "");
			}
			
			if (ccsid == MQConstants.MQCCSI_Q_MGR || ccsid == MQConstants.MQCCSI_INHERIT) {
				ccsid = WmMQAdapterUtils.getSystemDefaultCharacterSetId();
			}
			MQEnvironment.CCSID = ccsid;
			if ((wmMQAdapter.getSSLListing().trim().length() > 0 || wmMQAdapter.getSSLSupport())
					&& Service.isServerSupported()
					&& ((sslKeyStoreAlias != null) && (sslKeyStoreAlias.trim()
							.length() > 0))
					&& ((sslTrustStoreAlias != null) && (sslTrustStoreAlias
							.trim().length() > 0))) {
				qmanager = connectToQMgrWithKeystoreAlias(channel, hostName, port,
						sslCipherSpec, sslKeyStoreAlias, sslTrustStoreAlias,
						qMgrName, ccdtUrl, props);
			} else if ((wmMQAdapter.getSSLListing().trim().length() > 0 || wmMQAdapter.getSSLSupport())
					&& ((sslKeyStore != null) && (sslKeyStore.trim().length() > 0))) {
				qmanager = connectToQMgrWithKeystoreFile(qMgrName, sslKeyStore,
						sslPassword, sslCipherSpec, ccdtUrl, props);
			} else {
				if(ccdtUrl!=null){
					qmanager = new MQQueueManager(qMgrName,props, ccdtUrl);
				}else{
					qmanager = new MQQueueManager(qMgrName, props);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
 	/* Not throwing proper exception for failed QueueManager connection
 	 	} catch (MQException exc) {
			 // TODO: handle exception
	 		String[] parray = new String[1];
	 		parray[0] = exc.getMessage();
	 		log(ARTLogger.ERROR, 1003,"connect" ,"Error while connecting to Queue Manager : "+exc.getCause());
	 		throw wmMQAdapter.getInstance().createAdapterException(2055, parray);
		//	 exc.printStackTrace();
		 }
	*/	
		return qmanager;
	}


	public static MQQueueManager connectToQMgrWithKeystoreAlias(String channel, String hostName,
			int port, String cipherSpec, String keystoreAlias,
			String trustStoreAlias, String qmanagerName, URL ccdtUrl, Hashtable<String, Object> props)
			throws AdapterException {
		
		MQQueueManager qmanager = null;
		
		props.put(MQC.CHANNEL_PROPERTY, channel);
		props.put(MQC.HOST_NAME_PROPERTY, hostName);
		props.put(MQC.PORT_PROPERTY, port);
		props.put(MQC.SSL_CIPHER_SUITE_PROPERTY,
				wmMQConnectionFactory.suite2Spec(cipherSpec));
		wmMQKeyStoreManager keyStoreLoader = new wmMQKeyStoreManager();
		keyStoreLoader.setKeyStoreHandle(keystoreAlias);
		keyStoreLoader.setTrustStoreHandle(trustStoreAlias);
		try {
			keyStoreLoader.init();
			KeyStore ks = keyStoreLoader.getKeyStore();
			KeyStore ts = keyStoreLoader.getTrustStore();
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());

			trustManagerFactory.init(ts);
			keyManagerFactory.init(ks, keyStoreLoader.getKeyStorePwd()
					.toCharArray());

			SSLContext sslContext = SSLContext.getInstance("SSL");

			sslContext.init(keyManagerFactory.getKeyManagers(),
					trustManagerFactory.getTrustManagers(), null);

			// Get an SSLSocketFactory to pass to WMQ
			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

			// Set the socket factory in our WMQ parameters
			props.put(MQC.SSL_SOCKET_FACTORY_PROPERTY, sslSocketFactory);
			if(ccdtUrl!=null){
				qmanager = new MQQueueManager(qmanagerName, props, ccdtUrl);
			}else{
			// Connect to WMQ
				qmanager = new MQQueueManager(qmanagerName, props);
			}
		} catch (KeyStoreException e) {
			log(ARTLogger.ERROR, 5013, null, null);
			throw wmMQAdapter.getInstance().createAdapterConnectionException(
					1055,
					new String[] { "Initializing Connection", e.getMessage() },
					e);
		} catch (NoSuchAlgorithmException e) {
			log(ARTLogger.ERROR, 1003, e.getLocalizedMessage(), null);
			throw wmMQAdapter.getInstance().createAdapterConnectionException(
					1055,
					new String[] { "Initializing Connection", e.getMessage() },
					e);
		} catch (UnrecoverableKeyException e) {
			log(ARTLogger.ERROR, 1003, e.getLocalizedMessage(), e.getMessage());
			throw wmMQAdapter.getInstance().createAdapterConnectionException(
					1055,
					new String[] { "Initializing Connection", e.getMessage() },
					e);
		} catch (KeyManagementException e) {
			log(ARTLogger.ERROR, 1003, e.getLocalizedMessage(), e.getMessage());
			throw wmMQAdapter.getInstance().createAdapterConnectionException(
					1055,
					new String[] { "Initializing Connection", e.getMessage() },
					e);
		} catch (ServerException e) {
			log(ARTLogger.ERROR, 1003, e.getLocalizedMessage(), e.getMessage());
			throw wmMQAdapter.getInstance().createAdapterConnectionException(
					1055,
					new String[] { "Initializing Connection", e.getMessage() },
					e);
		} catch (IOException e) {
			log(ARTLogger.ERROR, 1003, e.getLocalizedMessage(), e.getMessage());
			throw wmMQAdapter.getInstance().createAdapterConnectionException(
					1055,
					new String[] { "Initializing Connection", e.getMessage() },
					e);
		} catch (MQException e) {
			// TODO: handle exception
		}
		return qmanager;
	}
}
