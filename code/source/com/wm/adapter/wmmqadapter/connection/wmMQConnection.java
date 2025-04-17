/*
 * wmMQConnection.java
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionRequestInfo;

import com.ibm.mq.MQDistributionList;
import com.ibm.mq.MQDistributionListItem;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMD;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.MQReceiveExit;
import com.ibm.mq.MQSecurityExit;
import com.ibm.mq.MQSendExit;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.pcf.PCFException;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;
import com.wm.adapter.wmmqadapter.WmMQAdapterUtils;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adapter.wmmqadapter.admin.Service;
import com.wm.adapter.wmmqadapter.service.wmMQMQMD;
import com.wm.adapter.wmmqadapter.service.wmMQProperty;
import com.wm.adapter.wmmqadapter.service.wmMQQueueAttributes;
import com.wm.adapter.wmmqadapter.service.wmMQQueueManagerAttributes;
import com.wm.adapter.wmmqadapter.service.wmPCFCommandMetadata;
import com.wm.adapter.wmmqadapter.service.wmPCFCommandMetadataFactory;
import com.wm.adk.connection.WmManagedConnection;
import com.wm.adk.error.AdapterConnectionException;
import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;
import com.wm.adk.metadata.ResourceDomainValues;
import com.wm.adk.metadata.WmAdapterAccess;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.lang.ns.NSNode;
import com.wm.pkg.art.isproxy.Server;
import com.wm.pkg.art.ssl.CertStoreHandler;
import com.wm.pkg.art.ssl.CertStoreManager;
import com.wm.util.ServerException;

/*
* This class represents a connection to the wmMQAdapter's resource. It is
* returned by its corresponding ConnectionFactory's createManagedConnectionObject method.
* The wmMQAdapter supports no transactionality.
*/
public class wmMQConnection extends WmManagedConnection {
	protected MQQueueManager _qMgr = null;
	private MQQueue _inboundQueue = null;
	private MQQueue _outboundQueue = null;
	private String _realQMgrName = null;
	protected String _queueName = null;
	private String _realInboundQueueName = null;
	private String _realOutboundQueueName = null;
	protected boolean _openedForInput = false;
	protected boolean _openedForOutput = false;
	private String _prefix = "";

	public boolean _inTransaction = false;
	protected String _transactionName = null;

	private Hashtable _replyToConnections = null;
	private Hashtable _DLQConnections = null;

	private MQDistributionListItem[] _DLItems = null;
	private MQDistributionList _distributionList = null;

	private Map _outboundQueues = new HashMap();
	private Map _queuesOpenedForInquire = new HashMap();
	private PCFMessageAgent _pcfMessageAgent = null;

	private boolean _inUse = true;
//	private static HashMap<String, MQQueue> referenceInboundQueue = new HashMap<String, MQQueue>(); 
//	private static HashMap<String,MQQueue> referenceOutboundQueue = new HashMap<String, MQQueue>(); 

	/*
	 * Constructor.
	 */
	public wmMQConnection() {
		super();
		_inTransaction = false;
	}

	/*
	 * Specifies that initialization is required immediately after a connection
	 * is created for the first time.
	 */
	@Override
	protected boolean initializationRequired() {
		return true;
	}

	// private void resetMQEnv(){
	// //MQEnvironment.hostname = null;
	// //MQEnvironment.port = 0;
	// //MQEnvironment.channel = null;
	// //MQEnvironment.CCSID = 0;
	// //MQEnvironment.userID = null;
	// //MQEnvironment.password = null;
	// MQEnvironment.securityExit = null;
	// MQEnvironment.sendExit = null;
	// MQEnvironment.receiveExit = null;
	// }

	/*
	 * Initializes a connection immediately after it is created for the first
	 * time. This method will be invoked during the creation of a new connection
	 * because the initializationRequired() method returns true.
	 */

	@Override
	@SuppressWarnings("squid:S1541")
	protected void initializeConnection(javax.security.auth.Subject subject, ConnectionRequestInfo cxRequestInfo)
			throws ResourceException {
		int hashCode = Thread.currentThread().hashCode();
		String temp = "[ Debug_Thread: " + hashCode + "]";
		log(ARTLogger.INFO, 1001, "wmMQConnection.initializeConnection", "factory=" + _factory);
		log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection", "factory=" + _factory + temp);
		// IData data=null;
		//
		// String useLocalQueueManager = null;
		String queueManagerName = null;
		// String hostName = null;
		// String port = null;
		// String channel = null;
		String CCSID = null;
		String queueName = null;
		String dynamicQueuePrefix = null;

		wmMQConnectionFactory connFactory = (wmMQConnectionFactory) _factory;

		String _qMgrName = queueManagerName;
		int mqexCompletionCode = 0;
		int mqexReasonCode = 0;
		String url = connFactory.getCcdtFilePath();
		URL ccdtUrl = null;
		if(url!=null && url.trim().length()>0){
			try {
				ccdtUrl = new URL("file:///"+url);
			} catch (MalformedURLException e) {
				// log the message and continue connection creation without CCDT
				log(ARTLogger.ERROR, 1055, "Invalid CCDT URL "+url+", Thus Creating Connection without CCDt URl ", e.getMessage());
				
			}
		}

		// Trax 1-XRUXO - Moved synchronized block to avoid problem with
		// multiple threads modifying
		// the MQEnvironment object at the same time.

		log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection, Synchronizing Adapter Instance ... ",
				temp);
		// synchronized (wmMQAdapter.getInstance())
		// {
		try {
			log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection, Synchronized Adapter Instance ... ",
					temp);
			Hashtable<String, Object> props = new Hashtable<String, Object>();

			if (connFactory.getHostName() == null || connFactory.getHostName().length() == 0) {

				// We need to use binding mode now.
				// With this statement, we instruct MQSeries to determine
				// whether to use Client-mode
				// or Bindings-mode, based on the value of the host name. If the
				// host name is null,
				// Bindings-mode will be used.

				props.put(MQConstants.TRANSPORT_PROPERTY, MQConstants.TRANSPORT_MQSERIES);
			} else {
				props.put(MQConstants.HOST_NAME_PROPERTY, connFactory.getHostName());
			}

			try{
				if(connFactory.getPort()!=null && connFactory.getPort().length()>0){
					props.put(MQConstants.PORT_PROPERTY, Integer.valueOf(connFactory.getPort()));
				}
			}catch (NumberFormatException e) {
				// The port number entered is not numeric
			}
			props.put(MQConstants.CHANNEL_PROPERTY, connFactory.getChannel());
			props.put(MQConstants.CCSID_PROPERTY, setCCSID(connFactory, CCSID));
			props = setSecurityExit(connFactory, temp, props);
			String _userid = connFactory.getUserId();

			if (_userid != null && _userid.length() > 0) {
				props.put(MQConstants.USER_ID_PROPERTY, _userid);
				String _password = connFactory.getPassword();
				if (_password != null && _password.length() > 0) {
					props.put(MQConstants.PASSWORD_PROPERTY, _password);
				}
			} else {
				props.put(MQConstants.USER_ID_PROPERTY, "");
			}

			if (_qMgrName == null)
				_qMgrName = connFactory.getQueueManagerName();
			if ((_qMgrName != null) && (_qMgrName.equals("default")))
				_qMgrName = null;
			
			if ((wmMQAdapter.getSSLListing().trim().length() > 0 || wmMQAdapter.getSSLSupport()) && Service.isServerSupported()
					&& ((connFactory.getSslKeyStoreAlias() != null)
							&& (connFactory.getSslKeyStoreAlias().trim().length() > 0))
					&& ((connFactory.getSslTrustStoreAlias() != null)
							&& (connFactory.getSslTrustStoreAlias().trim().length() > 0))) {

				log(ARTLogger.INFO, 1003,
						"Debug_wmMQConnection.initializeConnection, Creating MQQueueManager WITH SSL using keystorealias and truststorealias... ",
						temp);
				_qMgr = createSSLQMgrWithKeyAlias(connFactory, _qMgrName, temp, ccdtUrl, props);
				log(ARTLogger.INFO, 1003,
						"Debug_wmMQConnection.initializeConnection, MQQueueManager created WITH SSL... ", temp);
			} else if ((wmMQAdapter.getSSLListing().trim().length() > 0 || wmMQAdapter.getSSLSupport())
					&& ((connFactory.getSslKeyStore() != null) && (connFactory.getSslKeyStore().trim().length() > 0))) {
				log(ARTLogger.INFO, 1003,
						"Debug_wmMQConnection.initializeConnection, Creating MQQueueManager WITH SSL... ", temp);
				_qMgr = createSSLQMgr(connFactory, _qMgrName, temp, props, ccdtUrl);
				log(ARTLogger.INFO, 1003,
						"Debug_wmMQConnection.initializeConnection, MQQueueManager created WITH SSL... ", temp);
			} else {
				// Creating QM Mgr without SSL
				log(ARTLogger.INFO, 1003,
						"Debug_wmMQConnection.initializeConnection, Creating MQQueueManager WITHOUT SSL... ", temp);
				if(ccdtUrl!=null){
					_qMgr = new MQQueueManager(_qMgrName, props, ccdtUrl);
				}else{
					_qMgr = new MQQueueManager(_qMgrName, props);
				}
				log(ARTLogger.INFO, 1003,
						"Debug_wmMQConnection.initializeConnection, MQQueueManager created WITHOUT SSL... ", temp);

			}
		} catch (MQException exc) {
			log(ARTLogger.ERROR, 1055, "Instantiating MQQueueManager(" + _qMgrName + ")", exc.getMessage());
			mqexCompletionCode = exc.completionCode;
			mqexReasonCode = exc.reasonCode;
			log(ARTLogger.ERROR, 3033, _qMgrName, "cc=" + mqexCompletionCode + ",rc=" + mqexReasonCode);
			exc.printStackTrace();

		} catch (wmMQException mqe) {
			log(ARTLogger.ERROR, 1055, "Instantiating MQQueueManager(" + _qMgrName + ")", mqe.getMessage());
			mqe.printStackTrace();
			throw wmMQAdapter.getInstance().createAdapterConnectionException(1055,
					new String[] { "Initializing Connection", mqe.getMessage() }, mqe);

		}

		// } //End of synchronized block

		log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection, End of synchronized block ", temp);
		if (_qMgr == null) {
			// Object[] parms = {_qMgrName, "cc=" + mqexCompletionCode + ",rc="
			// + mqexReasonCode};
			// throw new ResourceException(wmMQException.format("3033", parms));
			// Trax 1-RYX27 - Throw an AdapterConnectionException instead of a
			// ResourceException
			throw wmMQAdapter.getInstance().createAdapterConnectionException(3033,
					new String[] { _qMgrName, "cc=" + mqexCompletionCode + ",rc=" + mqexReasonCode });
		}

		
		_realQMgrName = _qMgr.name;
		
		log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection, _realQMgrName : " + _realQMgrName, temp);
		if ((_realQMgrName == null) || (_realQMgrName.trim().equals("")))
			_realQMgrName = _qMgrName;

		if (queueName == null)
			_queueName = connFactory.getQueueName();
		else
			_queueName = queueName;
		log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection, _queueName : " + _queueName, temp);

		if (dynamicQueuePrefix == null)
			_prefix = connFactory.getDynamicQueuePrefix();
		else
			_prefix = dynamicQueuePrefix;
		log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection, _prefix : " + _prefix, temp);
		log(ARTLogger.INFO, 1002, "wmMQConnection.initializeConnection", "");

	}

	/*
	 * Open the queue for input
	 */
	protected boolean openQueueForInput(boolean shared, boolean browse, int inquire) throws wmMQException {
		log(ARTLogger.INFO, 1001, "wmMQConnection.openQueueForInput", "");

		// int openoptions = MQC.MQOO_INQUIRE;
		int openoptions = inquire;

		if (browse)
			openoptions |= MQConstants.MQOO_BROWSE;

		if (shared)
			openoptions |= MQConstants.MQOO_INPUT_SHARED;
		else
			openoptions |= MQConstants.MQOO_INPUT_EXCLUSIVE;

		// Trax 1-SJZNY : Open options set to fail if the Queue manager is
		// quiescing
		openoptions |= MQConstants.MQOO_FAIL_IF_QUIESCING;
		// Trax 1-SJZNY

		// int retryCount = 10;
		String oneQueue = "";

		if ((_prefix == null) || (_prefix.trim().equals("")))
			_prefix = "WM*";
	//	MQQueue inboundQueue = null;
		// while ((!_openedForInput) && (retryCount > 0))
		// {
		try {
			// Added for JUnit testing
			if (_qMgr == null)
				initializeConnection(null, null);

			oneQueue = _queueName.trim();
			if (_realInboundQueueName != null)
				oneQueue = _realInboundQueueName.trim();
			int spaceindx = oneQueue.indexOf(" ");
			if (spaceindx > -1)
				oneQueue = oneQueue.substring(0, spaceindx);

			log(ARTLogger.INFO, 1003, "wmMQConnection.openQueueForInput",
					"instantiating MQQueue, options=" + openoptions);
			_inboundQueue = new MQQueue(_qMgr, oneQueue, openoptions, "", _prefix, null);
			_openedForInput = true;
			log(ARTLogger.INFO, 1003, "wmMQConnection.openQueueForInput", "instantiated MQQueue");
			_openedForInput = true;
						
			_realInboundQueueName = _inboundQueue.name;
	
		} catch (MQException mqe) {
			log(ARTLogger.ERROR, 1055, "Instantiating MQQueue(" + oneQueue + ")", mqe.getMessage());
			// mqe.printStackTrace();
			if (mqe.completionCode == MQConstants.MQCC_FAILED) {
				// Not all versions of WebSphereMQ support MQOO_INQUIRE.
				if ((inquire > 0) && (mqe.reasonCode == MQConstants.MQRC_OPTION_NOT_VALID_FOR_TYPE)) {
					return openQueueForInput(shared, browse, 0);
				}
				// if (retryCount == 0) {
				Object[] parms = { "Input", mqe.toString() };
				wmMQException wmqe = new wmMQException("3034", oneQueue, parms);
				wmqe.setLinkedException(mqe);
				throw wmqe;
				
			}
			_openedForInput = true;
			
			_realInboundQueueName = _inboundQueue.name;
			// else
			// _realInboundQueueName = _queueName;
		} catch (Exception ex) {
			log(ARTLogger.ERROR, 1055, "Instantiating MQQueue(" + oneQueue + ")", ex.getMessage());
			if (ex instanceof wmMQException)
				throw (wmMQException) ex;
			
		}
		// } //while()
		log(ARTLogger.INFO, 1003, "openQueueForInput", "_realInboundQueueName=" + _realInboundQueueName);
		log(ARTLogger.INFO, 1002, "openQueueForInput", "");
		return _openedForInput;
	}

	/*
	 * Put a message onto the queue
	 */
	@SuppressWarnings("squid:S1541")
	public int put(wmMQMessage msg, String queueManagerName, String queueName) throws wmMQException {
		log(ARTLogger.INFO, 1001, "wmMQConnection.put", "");

		MQMessage mqmsg = msg.getMQMessage();
		wmMQConnectionFactory connFactory = (wmMQConnectionFactory) _factory;

		boolean sendToDefaultQueue = true;
		MQQueue outboundQueue = null;
		if (queueManagerName != null && queueName != null) {
			String key = queueManagerName + "|" + queueName;
			outboundQueue = (MQQueue) _outboundQueues.get(key);

			if (outboundQueue == null || !outboundQueue.isOpen()) {
				int identity = evaluateOpenOptions(mqmsg, connFactory);
				outboundQueue = openQueueForOutput(identity, MQConstants.MQOO_INQUIRE, queueManagerName, queueName);
				_outboundQueues.put(key, outboundQueue);
			}

			sendToDefaultQueue = false;
        }
        else {
            if ((_outboundQueue == null) || (!_outboundQueue.isOpen()))
            {
                int identity = evaluateOpenOptions(mqmsg, connFactory);
                openQueueForOutput(identity, MQConstants.MQOO_INQUIRE);
            }

        	outboundQueue = _outboundQueue;
        }

		if (mqmsg.messageSequenceNumber < 1)
			mqmsg.messageSequenceNumber = 1;

		if (mqmsg.offset < 0)
			mqmsg.offset = 0;

		int reasonCode = 0;
		MQPutMessageOptions pmo = new MQPutMessageOptions();

		pmo.options = MQConstants.MQPMO_DEFAULT_CONTEXT;

		// Trax 1-QMX0C
		// if ((mqmsg.applicationIdData != null) &&
		// (mqmsg.applicationIdData.length() > 0))
		// Trax log 1-ST7P3. ApplOriginData and ApplicationIdData ignored
		// if ((mqmsg.applicationIdData != null) &&
		// (!mqmsg.applicationIdData.trim().equals("")))
		if (((mqmsg.applicationIdData != null) && (!mqmsg.applicationIdData.trim().equals("")))
				|| ((mqmsg.applicationOriginData != null) && (!mqmsg.applicationOriginData.trim().equals(""))))
			pmo.options = MQConstants.MQPMO_SET_ALL_CONTEXT;
		// Trax 1-150GZN : Start
		// Modifications for allowing user to set putdate and puttime in
		// MQHeader.

		// iTrac 852 [ SetAll permission ]
		else if (mqmsg.putDateTime != null) {
			pmo.options = MQConstants.MQPMO_SET_ALL_CONTEXT;
		}
		// iTrac 852 [ SetAll Permission ]

		// Trax 1-150GZN : End

		// TRAX 1-1KQMJN : MsgID and CorrelID are not part of Identity Context
		// 1-1KQMJN : Also, mqmsg.userId should be used instead of
		// connFactory.getUserId()
		// Trax log 1-ST7P3. ApplOriginData and ApplicationIdData ignored
		// else if (((connFactory.getUserId() != null) &&
		// (connFactory.getUserId().length() > 0)) ||
		// mqmsg.correlationId != null) ||
		// (mqmsg.accountingToken != null) ||
		// (mqmsg.messageId != null) )
		// (arrayIsNotNull(mqmsg.correlationId)) ||
		// (arrayIsNotNull(mqmsg.messageId)) ||
		// 1-1KQMJN
		else if ((mqmsg.userId != null && (!mqmsg.userId.trim().equals(""))) || (arrayIsNotNull(mqmsg.accountingToken)))
			pmo.options = MQConstants.MQPMO_SET_IDENTITY_CONTEXT;

		// It has been determined that the MQPMO_NEW_CORREL_ID and
		// MQPMO_NEW_MSG_ID
		// options are not supported by all platforms.

		// Request a new correlationId if the application did not supply one
		// if (!arrayIsNotNull(mqmsg.correlationId))
		// pmo.options |= MQC.MQPMO_NEW_CORREL_ID;

		// Request a new messageId if the application did not supply one
		// if (!arrayIsNotNull(mqmsg.messageId))
		// pmo.options |= MQC.MQPMO_NEW_MSG_ID;

		// if (sync)
		if (_inTransaction)
			pmo.options |= MQConstants.MQPMO_SYNCPOINT;

		// Trax 1-SJZNY : MQPMO_FAIL_IF_QUIESCING added
		pmo.options |= MQConstants.MQPMO_FAIL_IF_QUIESCING;

		if (sendToDefaultQueue && _distributionList != null) {
			// Copy the MQMD fields into each distribution list item
			for (int i = 0; i < _DLItems.length; i++) {
				_DLItems[i].accountingToken = new byte[32];
				System.arraycopy(mqmsg.accountingToken, 0, _DLItems[i].accountingToken, 0, 32);
				_DLItems[i].correlationId = new byte[24];
				System.arraycopy(mqmsg.correlationId, 0, _DLItems[i].correlationId, 0, 24);
				_DLItems[i].groupId = new byte[24];
				System.arraycopy(mqmsg.groupId, 0, _DLItems[i].groupId, 0, 24);
				_DLItems[i].messageId = new byte[24];
				System.arraycopy(mqmsg.messageId, 0, _DLItems[i].messageId, 0, 24);
				_DLItems[i].feedback = mqmsg.feedback;
			}
		}

		// long time = -System.currentTimeMillis();
		try {
			// if ((mqmsg.userId != null) && (!mqmsg.userId.equals("")))
			// MQEnvironment.userID = mqmsg.userId;

			// log(ARTLogger.CRITICAL, 1003, "put", "sending message to queue="
			// + _outboundQueue.name + "/" +
			// _outboundQueue.toString().substring(18) + "/" +
			// msg.getMsgBody());

			log(ARTLogger.INFO, 1003, "wmMQConnection.put",
					"sending message to queue=" + _queueName + ",pmo=" + pmo.options);
			if (sendToDefaultQueue && _distributionList != null)
				_distributionList.put(mqmsg, pmo);
			else
				outboundQueue.put(mqmsg, pmo);
			log(ARTLogger.INFO, 1003, "wmMQConnection.put", "sent message to queue=" + _queueName);
			msg.setMQMessage(mqmsg);
		} catch (MQException mqe) {
			// mqe.printStackTrace();
			if (mqe.completionCode == MQConstants.MQCC_WARNING) {
				msg.setMQMessage(new MQMessage());
			}

			if (mqe.completionCode == MQConstants.MQCC_WARNING) {
				reasonCode = mqe.reasonCode;
				log(ARTLogger.WARNING, 3044, "put", _queueName + ":" + mqe.getLocalizedMessage());
				if (mqe.reasonCode == MQConstants.MQRC_MULTIPLE_REASONS) {
					log(ARTLogger.WARNING, 1003, "put", "Failed with multiple reasons");
					updateDistributionList();
					int openoptions = MQConstants.MQOO_OUTPUT | MQConstants.MQOO_FAIL_IF_QUIESCING;
					// Trax 1-QMX0C
					// if ((mqmsg.applicationIdData != null) &&
					// (mqmsg.applicationIdData != null))
					if ((mqmsg.applicationIdData != null) && (!mqmsg.applicationIdData.trim().equals(""))) {
						openoptions += MQConstants.MQOO_SET_ALL_CONTEXT;
					}
					// Trax 1-150GZN : Start
					// Modifications for allowing user to set putdate and
					// puttime in MQHeader.
					else if (mqmsg.putDateTime != null) {
						openoptions += MQConstants.MQOO_SET_ALL_CONTEXT;
					}
					// Trax 1-150GZN : End
					// 1-1KQMJN : mqmsg.userId should be used instead of
					// connFactory.getUserId()
					// else if ((connFactory.getUserId() != null) &&
					// (connFactory.getUserId().length() > 0))
					else if ((mqmsg.userId != null) && (mqmsg.userId.trim().length() > 0)) {
						openoptions += MQConstants.MQOO_SET_IDENTITY_CONTEXT;
					}
					// 1-1KQMJN* : MsgID and CorrelID are not part of Identity
					// Context. Accounting token is...
					// else if ((mqmsg.correlationId != null) ||
					// (mqmsg.messageId != null) )
					else if (arrayIsNotNull(mqmsg.accountingToken))
					// 1-1KQMJN
					{
						openoptions += MQConstants.MQOO_SET_IDENTITY_CONTEXT;
					}

					try {
						_distributionList = _qMgr.accessDistributionList(_DLItems,
								openoptions + MQConstants.MQOO_OUTPUT + MQConstants.MQOO_FAIL_IF_QUIESCING);
					} catch (MQException mqe2) {
						// Throw original MQException
						Object[] parms = { connFactory.getQueueManagerName(), "" + mqe.completionCode,
								"" + mqe.reasonCode };
						wmMQException mqse = new wmMQException("3035", _queueName, parms);
						mqse.setLinkedException(mqe);
						throw mqse;
					}
				}
			} else {
				// close();
				Object[] parms = { connFactory.getQueueManagerName(), "" + mqe.completionCode, "" + mqe.reasonCode };
				wmMQException mqse = new wmMQException("3035", _queueName, parms);
				mqse.setLinkedException(mqe);
				throw mqse;
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
		log(ARTLogger.INFO, 1003, "wmMQConnection.put exit", "reasonCode=" + reasonCode);
		return reasonCode;
	}

	private int evaluateOpenOptions(MQMessage mqmsg, wmMQConnectionFactory connFactory) {
		int identity = 0;
		// Trax 1-QMX0C
		// if ((mqmsg.applicationIdData != null) && (mqmsg.applicationIdData !=
		// null))
		// Trax log 1-ST7P3. ApplOriginData and ApplicationIdData ignored
		// if ((mqmsg.applicationIdData != null) &&
		// (!mqmsg.applicationIdData.trim().equals("")))
		if (((mqmsg.applicationIdData != null) && (!mqmsg.applicationIdData.trim().equals("")))
				|| ((mqmsg.applicationOriginData != null) && (!mqmsg.applicationOriginData.trim().equals("")))) {
			identity = MQConstants.MQOO_SET_ALL_CONTEXT;
		}
		// Trax 1-150GZN : Start
		// Modification for allowing user to set putdate and puttime in
		// MQHeader.
		else if (mqmsg.putDateTime != null) {
			identity = MQConstants.MQOO_SET_ALL_CONTEXT;
		}
		// Trax 1-150GZN : End
		// 1-1KQMJN : mqmsg.userId should be used instead of
		// connFactory.getUserId()
		// else if ((connFactory.getUserId() != null) &&
		// (connFactory.getUserId().length() > 0))
		else if ((mqmsg.userId != null) && (mqmsg.userId.trim().length() > 0)) {
			identity = MQConstants.MQOO_SET_IDENTITY_CONTEXT;
		}
		// Trax log 1-ST7P3. ApplOriginData and ApplicationIdData ignored
		// else if ((mqmsg.correlationId != null) ||
		// (mqmsg.accountingToken != null) ||
		// (mqmsg.messageId != null) )

		else if (
		// 1-1KQMJN* : MsgID and CorrelID are not part of Identity Context
		// (arrayIsNotNull(mqmsg.correlationId)) ||
		// (arrayIsNotNull(mqmsg.messageId)) ||
		// 1-1KQMJN
		(arrayIsNotNull(mqmsg.accountingToken))) {
			identity = MQConstants.MQOO_SET_IDENTITY_CONTEXT;
		}
		return identity;
	}

	/*
	 * Get a message from the queue
	 */
	public int get(wmMQMessage msg, boolean shared, int waitinterval, boolean convertData) throws wmMQException {
		log(ARTLogger.INFO, 1001, "wmMQConnection.get", "");
		MQMessage mqmsg = msg.getMQMessage();
		wmMQConnectionFactory connFactory = (wmMQConnectionFactory) _factory;
	
        if ( (_inboundQueue == null) || (!_inboundQueue.isOpen()))
            openQueueForInput(shared, false, MQConstants.MQOO_INQUIRE);
		
		int reasonCode = 0;
		MQGetMessageOptions gmo = new MQGetMessageOptions();

		// The MQGMO_LOGICAL_ORDER option causes a get() to fail with reason
		// code = 2394.
		// gmo.options = MQC.MQGMO_WAIT | MQC.MQGMO_CONVERT |
		// MQC.MQGMO_LOGICAL_ORDER;
		// Trax 1-T5J73. To implement, remove MQC.MQGMO_CONVERT.
		gmo.options = MQConstants.MQGMO_WAIT | MQConstants.MQGMO_FAIL_IF_QUIESCING; // Trax
																	// 1-SJZNY :
																	// MQGMO_FAIL_IF_QUIESCING
																	// added

		if (convertData) {
			gmo.options |= MQConstants.MQGMO_CONVERT;
		}
		// Change corresponding to IMQ-906
		// The value for MQConstants.MQGMO_PROPERTIES_FORCE_MQRFH2 is 33554432
		// which is used in case of MQ 6 backend
		String forceMQRFH2 = System.getProperty("watt.WmMQAdapter.forceMQRFH2");
		if (forceMQRFH2 != null && forceMQRFH2.equalsIgnoreCase("true")) {
			gmo.options |= 33554432;
		}
		if (_inTransaction)
			gmo.options |= MQConstants.MQGMO_SYNCPOINT;

		gmo.waitInterval = waitinterval;

		gmo.matchOptions = setMatchOptions(mqmsg);

		// long time = -System.currentTimeMillis();
		try {
			log(ARTLogger.INFO, 1003, "wmMQConnection.get",
					"receiving msg on queue=" + _queueName + ", options=" + gmo.options + ",match=" + gmo.matchOptions);

			// Trax 1-12K2HK : Value 0 for characterSet is a valid value
			// (MQCCSI_Q_MGR)
			// if (mqmsg.characterSet == 0)
			// mqmsg.characterSet = 819;

			// Trax 1-QFULC - seqno must be 1 and offset must be 0 for mainframe
			// QMgr.
			if (mqmsg.messageSequenceNumber < 1)
				mqmsg.messageSequenceNumber = 1;

			if (mqmsg.offset < 0)
				mqmsg.offset = 0;

			_inboundQueue.get(mqmsg, gmo);
			log(ARTLogger.INFO, 1003, "wmMQConnection.get",
					"received msg on queue=" + _queueName + " - ccsid=" + mqmsg.characterSet);
		} catch (MQException mqe) {
			// mqe.printStackTrace();

			if (mqe.reasonCode == MQConstants.MQRC_NO_MSG_AVAILABLE) {
				reasonCode = mqe.reasonCode;
				msg.setMsgBody(null);
			} else if (mqe.completionCode == MQConstants.MQCC_WARNING) {
				reasonCode = mqe.reasonCode;
				log(ARTLogger.WARNING, 3044, "get", _queueName + ":" + mqe.getLocalizedMessage());
			} else {
				// close();
				Object[] parms = { connFactory.getQueueManagerName(), "" + mqe.completionCode, "" + mqe.reasonCode };
				wmMQException mqse = new wmMQException("3036", _queueName, parms);
				mqse.setLinkedException(mqe);
				throw mqse;
			}
		}
		// log(ARTLogger.INFO, 1003, "wmMQConnection.get exit", "this=" + this);
		log(ARTLogger.INFO, 1003, "wmMQConnection.get exit", "reasonCode=" + reasonCode);
		return reasonCode;
	}

	/*
	 * Get a message from the queue
	 */
	public int listenerGet(wmMQMessage msg, boolean shared, int waitinterval, boolean convertData)
			throws wmMQException {
		log(ARTLogger.INFO, 1001, "wmMQConnection.listenerGet", "");
		MQMessage mqmsg = msg.getMQMessage();
		wmMQConnectionFactory connFactory = (wmMQConnectionFactory) _factory;
	
        if ( (_inboundQueue == null) || (!_inboundQueue.isOpen()))
            openQueueForInput(shared, false, MQConstants.MQOO_INQUIRE);
		
		int reasonCode = 0;
		MQGetMessageOptions gmo = new MQGetMessageOptions();

		// The MQGMO_LOGICAL_ORDER option causes a get() to fail with reason
		// code = 2394.
		// gmo.options = MQC.MQGMO_WAIT | MQC.MQGMO_CONVERT |
		// MQC.MQGMO_LOGICAL_ORDER;
		// Trax 1-T5J73. To implement, remove MQC.MQGMO_CONVERT.
		// gmo.options = MQC.MQGMO_WAIT | MQC.MQGMO_FAIL_IF_QUIESCING |
		// MQC.MQGMO_ALL_MSGS_AVAILABLE | MQC.MQGMO_BROWSE_FIRST |
		// MQC.MQOO_BROWSE ; // Trax 1-SJZNY : MQGMO_FAIL_IF_QUIESCING added
		gmo.options = MQConstants.MQGMO_WAIT | MQConstants.MQGMO_FAIL_IF_QUIESCING | MQConstants.MQGMO_ALL_MSGS_AVAILABLE;

		if (convertData) {
			gmo.options |= MQConstants.MQGMO_CONVERT;
		}
		// Change corresponding to IMQ-906
		// The value for MQConstants.MQGMO_PROPERTIES_FORCE_MQRFH2 is 33554432
		// which is used in case of MQ 6 backend
		String forceMQRFH2 = System.getProperty("watt.WmMQAdapter.forceMQRFH2");
		if (forceMQRFH2 != null && forceMQRFH2.equalsIgnoreCase("true")) {
			gmo.options |= 33554432;
		}

		if (_inTransaction)
			gmo.options |= MQConstants.MQGMO_SYNCPOINT;
		/*
		 * try{ for(String gmoOption:mqgmoOptions.split(",")){ gmo.options |=
		 * Integer.parseInt(gmoOption); } }catch(NullPointerException ex){
		 * ex.printStackTrace(); }
		 */
		gmo.waitInterval = waitinterval;

		gmo.matchOptions = setMatchOptions(mqmsg);

		// long time = -System.currentTimeMillis();
		try {
			log(ARTLogger.INFO, 1003, "wmMQConnection.listenerGet",
					"receiving msg on queue=" + _queueName + ", options=" + gmo.options + ",match=" + gmo.matchOptions);

			// Trax 1-12K2HK : Value 0 for characterSet is a valid value
			// (MQCCSI_Q_MGR)
			// if (mqmsg.characterSet == 0)
			// mqmsg.characterSet = 819;

			// Trax 1-QFULC - seqno must be 1 and offset must be 0 for mainframe
			// QMgr.
			if (mqmsg.messageSequenceNumber < 1)
				mqmsg.messageSequenceNumber = 1;

			if (mqmsg.offset < 0)
				mqmsg.offset = 0;

			_inboundQueue.get(mqmsg, gmo);
			log(ARTLogger.INFO, 1003, "wmMQConnection.listenerGet",
					"received msg on queue=" + _queueName + " - ccsid=" + mqmsg.characterSet);
		} catch (MQException mqe) {
			// mqe.printStackTrace();

			if (mqe.reasonCode == MQConstants.MQRC_NO_MSG_AVAILABLE) {
				reasonCode = mqe.reasonCode;
				msg.setMsgBody(null);
			} else if (mqe.completionCode == MQConstants.MQCC_WARNING) {
				reasonCode = mqe.reasonCode;
				log(ARTLogger.WARNING, 3044, "listenerGet", _queueName + ":" + mqe.getLocalizedMessage());
			} else {
				// close();
				Object[] parms = { connFactory.getQueueManagerName(), "" + mqe.completionCode, "" + mqe.reasonCode };
				wmMQException mqse = new wmMQException("3036", _queueName, parms);
				mqse.setLinkedException(mqe);
				throw mqse;
			}
		}
		// log(ARTLogger.INFO, 1003, "wmMQConnection.get exit", "this=" + this);
		log(ARTLogger.INFO, 1003, "wmMQConnection.listenerGet exit", "reasonCode=" + reasonCode);
		return reasonCode;
	}

	/*
	 * Peek at a message on the queue
	 */
	public int peek(wmMQMessage msg, boolean shared, int waitinterval, boolean resetCursor, boolean convertData)
			throws wmMQException {
		log(ARTLogger.INFO, 1001, "wmMQConnection.peek", "");

		boolean first = true;

		MQMessage mqmsg = msg.getMQMessage();
		wmMQConnectionFactory connFactory = (wmMQConnectionFactory) _factory;

		// Trax log 1-R559V
		// resetCursor=yes causes a new connection to be created each time,
		// exceeding
		// the max. open handles.
		// if ( (resetCursor) || (_inboundQueue == null) ||
		// (!_inboundQueue.isOpen()))
		// openQueueForInput(shared, true, MQC.MQOO_INQUIRE);
		// else
		// first = false;
		
	
		
		if ((_inboundQueue == null) || (!_inboundQueue.isOpen())){
			openQueueForInput(shared, true, MQConstants.MQOO_INQUIRE);
		
		}else if (!resetCursor)
			first = false;

		int reasonCode = 0;
		MQGetMessageOptions gmo = new MQGetMessageOptions();

		// The MQGMO_LOGICAL_ORDER option causes a get() to fail with reason
		// code = 2394.
		// gmo.options = MQC.MQGMO_WAIT | MQC.MQGMO_CONVERT |
		// MQC.MQGMO_LOGICAL_ORDER;
		gmo.options = MQConstants.MQGMO_WAIT;

		if (convertData) {
			gmo.options |= MQConstants.MQGMO_CONVERT;
		}

		if (first)
			gmo.options |= MQConstants.MQGMO_BROWSE_FIRST;
		else
			gmo.options |= MQConstants.MQGMO_BROWSE_NEXT;

		gmo.waitInterval = waitinterval;

		gmo.matchOptions = setMatchOptions(mqmsg);

		// long time = -System.currentTimeMillis();
		try {
			log(ARTLogger.INFO, 1003, "wmMQConnection.peek", "peeking at msg on queue=" + _queueName + ", options="
					+ gmo.options + ",match=" + gmo.matchOptions);

			// Trax 1-12K2HK : Value 0 for characterSet is a valid value
			// (MQCCSI_Q_MGR)
			// if (mqmsg.characterSet == 0)
			// mqmsg.characterSet = 819;

			// Trax 1-QFULC - seqno must be 1 and offset must be 0 for mainframe
			// QMgr.
			if (mqmsg.messageSequenceNumber < 1)
				mqmsg.messageSequenceNumber = 1;

			if (mqmsg.offset < 0)
				mqmsg.offset = 0;

			_inboundQueue.get(mqmsg, gmo);
			log(ARTLogger.INFO, 1003, "wmMQConnection.peek",
					"peeked at msg on queue=" + _queueName + " - ccsid=" + mqmsg.characterSet);
		} catch (MQException mqe) {
			// mqe.printStackTrace();
			if (mqe.completionCode == MQConstants.MQCC_WARNING) {
				reasonCode = mqe.reasonCode;
				log(ARTLogger.WARNING, 3044, "peek", _queueName + ":" + mqe.getLocalizedMessage());
			} else {
				// close();
				Object[] parms = { connFactory.getQueueManagerName(), "" + mqe.completionCode, "" + mqe.reasonCode };
				wmMQException mqse = new wmMQException("3037", _queueName, parms);
				mqse.setLinkedException(mqe);
				throw mqse;
			}
		}
		log(ARTLogger.INFO, 1003, "wmMQConnection.peek exit", "reasonCode=" + reasonCode);
		return reasonCode;
	}

	/**
	 * This method inquires the properties of the QueueManager and return a map
	 * with the values.
	 *
	 */
	public Map inquireQueueManagerProperties(String[] properties) throws wmMQException {
		if (properties == null || properties.length == 0) {
			return null;
		}

		int numOfIntAttrs = 0;
		int numOfCharAttrs = 0;
		int curCharOffset = 0;

		// Indicates where in intAttrs OR charAttrs
		// the result of the query will be found
		Map whereToFindResult = new HashMap();

		int[] selectors = new int[properties.length];

		for (int i = 0; i < properties.length; i++) {
			String attrName = properties[i];
			wmMQProperty prop = wmMQQueueManagerAttributes.lookup(attrName);
			selectors[i] = prop.getMQCindex();

			if ("int".equals(prop.getType())) {
				ResultLocationDesc locInt = new ResultLocationDesc("int", numOfIntAttrs, -1, -1, -1);
				whereToFindResult.put(attrName, locInt);
				numOfIntAttrs++;
			} else if ("String".equals(prop.getType())) {
				ResultLocationDesc locChar = new ResultLocationDesc("char", -1, numOfCharAttrs, curCharOffset,
						prop.getLength());
				whereToFindResult.put(attrName, locChar);
				numOfCharAttrs++;
				curCharOffset += prop.getLength();
			}
		}

		int[] intAttrs = new int[numOfIntAttrs];
		byte[] charAttrs = new byte[curCharOffset];

		try {
			_qMgr.inquire(selectors, intAttrs, charAttrs);
		} catch (MQException mqe) {
			log(ARTLogger.ERROR, 1055, "while inquiring queue manager properties", mqe.getMessage());
			wmMQConnectionFactory connFactory = (wmMQConnectionFactory) _factory;
			Object[] parms = { "" + mqe.completionCode, "" + mqe.reasonCode };
			wmMQException mqse = new wmMQException("3045", connFactory.getQueueManagerName(), parms);
			mqse.setLinkedException(mqe);
			throw mqse;
		}

		Map queueManagerProperties = new HashMap();
		for (int i = 0; i < properties.length; i++) {
			ResultLocationDesc loc = (ResultLocationDesc) whereToFindResult.get(properties[i]);
			if ("int".equals(loc.arrayType)) {
				queueManagerProperties.put(properties[i], new Integer(intAttrs[loc.intAttrPos]));
			} else if ("char".equals(loc.arrayType)) {
				queueManagerProperties.put(properties[i], new String(charAttrs, loc.charOffset, loc.length));
			}
		}

		return queueManagerProperties;
	}

	/**
	 * This method inquires the properties of a queue with the given name and
	 * return a map with the values.
	 *
	 */
	public Map inquireQueueProperties(String queueName, String[] properties,boolean isRemoteQueue) throws wmMQException {
		if (properties == null || properties.length == 0) {
			return null;
		}

		int numOfIntAttrs = 0;
		int numOfCharAttrs = 0;
		int curCharOffset = 0;

		// Indicates where in intAttrs OR charAttrs
		// the result of the query will be found
		Map whereToFindResult = new HashMap();

		int[] selectors = new int[properties.length];

		for (int i = 0; i < properties.length; i++) {
			String attrName = properties[i];
			wmMQProperty prop = wmMQQueueAttributes.lookup(attrName);
			selectors[i] = prop.getMQCindex();

			if ("int".equals(prop.getType())) {
				ResultLocationDesc locInt = new ResultLocationDesc("int", numOfIntAttrs, -1, -1, -1);
				whereToFindResult.put(attrName, locInt);
				numOfIntAttrs++;
			} else if ("String".equals(prop.getType())) {
				ResultLocationDesc locChar = new ResultLocationDesc("char", -1, numOfCharAttrs, curCharOffset,
						prop.getLength());
				whereToFindResult.put(attrName, locChar);
				numOfCharAttrs++;
				curCharOffset += prop.getLength();
			}
		}

		int[] intAttrs = new int[numOfIntAttrs];
		byte[] byteAttrs = new byte[curCharOffset];
		char[] charAttrs = new char[curCharOffset];

		if (queueName == null || queueName.trim().equals("")) {
			queueName = _queueName.trim();
		} else {
			queueName = queueName.trim();
		}
		MQQueue queueToInquire = (MQQueue) _queuesOpenedForInquire.get(queueName);
		if (queueToInquire == null) {
			queueToInquire = openQueueForInquire(queueName,isRemoteQueue);
			_queuesOpenedForInquire.put(queueName, queueToInquire);
		}
		String mqVAbove71 = System.getProperty("watt.WmMQAdapter.MQServer75AndAbove");

		try {
			// queueToInquire
			if (mqVAbove71 != null && mqVAbove71.equals("true")) {
				queueToInquire.inquire(selectors, intAttrs, charAttrs);
			} else {
				queueToInquire.inquire(selectors, intAttrs, byteAttrs);
			}
		} catch (MQException mqe) {
			log(ARTLogger.ERROR, 1055, "while inquiring queue properties", mqe.getMessage());
			wmMQConnectionFactory connFactory = (wmMQConnectionFactory) _factory;
			Object[] parms = { connFactory.getQueueManagerName(), "" + mqe.completionCode, "" + mqe.reasonCode };
			wmMQException mqse = new wmMQException("3046", queueName, parms);
			mqse.setLinkedException(mqe);
			throw mqse;
		}

		Map queueProperties = new HashMap();
		for (int i = 0; i < properties.length; i++) {
			ResultLocationDesc loc = (ResultLocationDesc) whereToFindResult.get(properties[i]);
			if ("int".equals(loc.arrayType)) {
				queueProperties.put(properties[i], new Integer(intAttrs[loc.intAttrPos]));
			} else if ("char".equals(loc.arrayType)) {
				if (mqVAbove71 != null && mqVAbove71.equals("true")) {
					queueProperties.put(properties[i], new String(charAttrs, loc.charOffset, loc.length));
				} else {
					queueProperties.put(properties[i], new String(byteAttrs, loc.charOffset, loc.length));
				}
			}
		}

		return queueProperties;
	}

	/**
	 * Executes the PCF command represented by the PCF message using the
	 * PCFMessageAgent
	 * 
	 */
	public void executePCFCommand(wmPCFMessage pcfMessage, int waitInterval) throws wmMQException {
		if (_pcfMessageAgent == null) {
			wmMQConnectionFactory connFactory = (wmMQConnectionFactory) _factory;

			try {
				_pcfMessageAgent = new PCFMessageAgent(_qMgr);

				// Since the older versions of the PCFMessageAgent class
				// did not expose setCharacterSet() and setEncoding() methods
				// we are setting them using introspection.
				// _pcfMessageAgent.setCharacterSet(WmMQAdapterUtils.getCharacterSetId(connFactory.getCCSID()));
				// _pcfMessageAgent.setEncoding(Integer.parseInt(connFactory.getEncoding().substring(0,
				// 5)));
				try {
					Method setCharacterSetMethod = PCFMessageAgent.class.getMethod("setCharacterSet",
							new Class[] { Integer.TYPE });
					if (setCharacterSetMethod != null) {
						setCharacterSetMethod.invoke(_pcfMessageAgent, new Object[] {
								new Integer(WmMQAdapterUtils.getCharacterSetId(connFactory.getCCSID())) });
					}

					Method setEncodingMethod = PCFMessageAgent.class.getMethod("setEncoding",
							new Class[] { Integer.TYPE });
					if (setEncodingMethod != null) {
						setEncodingMethod.invoke(_pcfMessageAgent,
								new Object[] { new Integer(connFactory.getEncoding().substring(0, 5)) });
					}
				} catch (Exception e) {
					// Ignore
				}
			} catch (MQException mqe) {
				log(ARTLogger.ERROR, 1055, "while creating PCFMessageAgent", mqe.getMessage());

				Object[] parms = { "" + mqe.completionCode, "" + mqe.reasonCode, mqe.getMessage() };
				wmMQException wmqe = new wmMQException("5001", connFactory.getQueueManagerName(), parms);
				wmqe.setLinkedException(mqe);
				throw wmqe;
			}
		}

		_pcfMessageAgent.setWaitInterval(waitInterval);
		try {
			PCFMessage[] responses = _pcfMessageAgent.send(pcfMessage.getRequestMessage());
			pcfMessage.setResponseMessages(responses);
		} catch (PCFException pcfe) {
			log(ARTLogger.ERROR, 1055, "while executing PCF command", pcfe.getMessage());
			Object[] parms = { "" + pcfe.completionCode, "" + pcfe.reasonCode, pcfe.getMessage() };
			wmMQException mqse = new wmMQException("5002", "PCFCommand", parms);
			mqse.setLinkedException(pcfe);
			throw mqse;
		} catch (MQException mqe) {
			log(ARTLogger.ERROR, 1055, "while executing PCF command", mqe.getMessage());
			Object[] parms = { "" + mqe.completionCode, "" + mqe.reasonCode, mqe.getMessage() };
			wmMQException mqse = new wmMQException("5002", "PCFCommand", parms);
			mqse.setLinkedException(mqe);
			throw mqse;
		} catch (IOException ioe) {
			log(ARTLogger.ERROR, 1055, "while executing PCF command", ioe.getMessage());
			Object[] parms = { ioe.getMessage() };
			wmMQException mqse = new wmMQException("1005", "PCFCommand", parms);
			mqse.setLinkedException(ioe);
			throw mqse;
		}
	}

	/**
	 * Open the queue for inquire
	 */
	protected MQQueue openQueueForInquire(String queueName,boolean isRemoteQueue) throws wmMQException {
		log(ARTLogger.INFO, 1001, "wmMQConnection.openQueueForInquire", "");

		MQQueue inquireQueue = null;
		int openoptions = MQConstants.MQOO_INQUIRE;
		openoptions |= MQConstants.MQOO_FAIL_IF_QUIESCING;

		/**
		 * IMQ-1196. You cannot browse messages on a remote queue; do not open a
		 * remote queue using the MQOO_BROWSE option.
		 */
		if(!isRemoteQueue){
			openoptions |= MQConstants.MQOO_BROWSE;
		}
		
		if ((_prefix == null) || (_prefix.trim().equals(""))) {
			_prefix = "WM*";
		}

		try {
			// Added for JUnit testing
			if (_qMgr == null) {
				initializeConnection(null, null);
			}

			int spaceindx = queueName.indexOf(" ");
			if (spaceindx > -1)
				queueName = queueName.substring(0, spaceindx);

			inquireQueue = _qMgr.accessQueue(queueName, openoptions, "", _prefix, null);
		} catch (MQException mqe) {
			log(ARTLogger.ERROR, 1055, "Opening " + queueName + " for inquire", mqe.getMessage());

			Object[] parms = { "Inquire", mqe.toString() };
			wmMQException wmqe = new wmMQException("3034", queueName, parms);
			wmqe.setLinkedException(mqe);
			throw wmqe;
		} catch (Exception e) {
			log(ARTLogger.ERROR, 1055, "Opening " + queueName + " for inquire", e.getMessage());

			if (e instanceof wmMQException) {
				throw (wmMQException) e;
			} else {
				Object[] parms = { "Inquire", e.getMessage() };
				wmMQException wmqe = new wmMQException("3034", queueName, parms);
				wmqe.setLinkedException(e);
				throw wmqe;
			}
		}

		log(ARTLogger.INFO, 1002, "openQueueForInquire", "");
		return inquireQueue;
	}

	public int setMatchOptions(MQMessage mqmsg) {
		int matchOptions = 0;
		// Trax 1-TDZIY - Reason code = 2186 if matchOpthion != 3
		// (MQMO_MATCH_MSG_ID + MQMO_MATCH_CORREL_ID)
		// when connected to an OS/390. (Always set MQMO_MATCH_MSG_ID +
		// MQMO_MATCH_CORREL_ID)
		// if (arrayIsNotNull(mqmsg.correlationId))
		{
			log(ARTLogger.INFO, 1004, "correlationId", new String(mqmsg.correlationId));
			matchOptions = MQConstants.MQMO_MATCH_CORREL_ID;
		}
		// if (arrayIsNotNull(mqmsg.messageId))
		{
			log(ARTLogger.INFO, 1004, "messageId", new String(mqmsg.messageId));

			matchOptions |= MQConstants.MQMO_MATCH_MSG_ID;
		}
		if (arrayIsNotNull(mqmsg.groupId)) {
			log(ARTLogger.INFO, 1004, "groupId", new String(mqmsg.groupId));
			matchOptions |= MQConstants.MQMO_MATCH_GROUP_ID;
		}
		if (mqmsg.messageSequenceNumber > 0) {
			log(ARTLogger.INFO, 1004, "messageSequenceNumber", "" + mqmsg.messageSequenceNumber);
			matchOptions |= MQConstants.MQMO_MATCH_MSG_SEQ_NUMBER;
		}
		if (mqmsg.offset > -1) {
			log(ARTLogger.INFO, 1004, "offset", "" + mqmsg.offset);
			matchOptions |= MQConstants.MQMO_MATCH_OFFSET;
		}
		return matchOptions;
	}

//	// determine if byte array is not null
//	public String getQueueAddress(boolean inbound) {
//		String addr = "";
//		if ((inbound) || (_inboundQueue != null))
//			addr = _inboundQueue.toString().substring(18);
//		else if (_outboundQueue != null)
//			addr = _outboundQueue.toString().substring(18);
//		return addr;
//	}

	// determine if byte array is not null
	private boolean arrayIsNotNull(byte[] bytes) {
		if ((bytes == null) || (bytes.length == 0))
			return false;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] != 0)
				return true;
		}
		return false;
	}

	/**
	 * Open the given remote queue for out
	 */
	protected MQQueue openQueueForOutput(int identityoption, int inquire, String queueManagerName, String queueName)
			throws wmMQException {

		log(ARTLogger.INFO, 1001, "wmMQConnection.openQueueForOutput", "");

		MQQueue outboundQueue = null;
		int openoptions = MQConstants.MQOO_OUTPUT | MQConstants.MQOO_FAIL_IF_QUIESCING + identityoption;

		queueManagerName = queueManagerName.trim();
		queueName = queueName.trim();
		log(ARTLogger.INFO, 1003, "wmMQConnection.openQueueForOutput", "queueManagerName=" + queueManagerName);
		log(ARTLogger.INFO, 1003, "wmMQConnection.openQueueForOutput", "queueName=" + queueName);

		try {
			// Added for JUnit testing
			if (_qMgr == null) {
				initializeConnection(null, null);
			}

			log(ARTLogger.INFO, 1003, "wmMQConnection.openQueueForOutput",
					"invoking accessqueue, options=" + openoptions);
			outboundQueue = _qMgr.accessQueue(queueName, openoptions, queueManagerName, null, null);
			log(ARTLogger.INFO, 1003, "wmMQConnection.openQueueForOutput", "invoked accessqueue");
		} catch (MQException mqe) {
			if (mqe.completionCode == MQConstants.MQCC_FAILED) {
				// Not all versions of WebSphereMQ support MQOO_INQUIRE.
				if (mqe.reasonCode == MQConstants.MQRC_OPTION_NOT_VALID_FOR_TYPE) {
					return openQueueForOutput(identityoption, 0, queueManagerName, queueName);
				}

				Object[] parms = { "Output", mqe.toString() };
				wmMQException wmqe = new wmMQException("3034", queueName, parms);
				wmqe.setLinkedException(mqe);
				throw wmqe;
			}
		} catch (Exception ex) {
			log(ARTLogger.ERROR, 1055, "Instantiating MQQueue(" + queueName + ")", ex.getMessage());
			if (ex instanceof wmMQException)
				throw (wmMQException) ex;
		}

		log(ARTLogger.INFO, 1003, "wmMQConnection.openQueueForOutput exiting, opened=",
				((_openedForOutput) ? "true" : "false"));

		return outboundQueue;
	}

	/*
	 * Open the queue for input
	 */
	protected boolean openQueueForOutput(int identityoption, int inquire) throws wmMQException {
		log(ARTLogger.INFO, 1001, "wmMQConnection.openQueueForOutput", "");
		
		int openoptions = MQConstants.MQOO_OUTPUT | MQConstants.MQOO_FAIL_IF_QUIESCING + identityoption;
		
		String oneQueue = _queueName.trim();
		if (_realOutboundQueueName != null)
			oneQueue = _realOutboundQueueName.trim();
		if (oneQueue == null)
			return false;
		else
			oneQueue.trim(); //NOSONAR

		log(ARTLogger.INFO, 1003, "wmMQConnection.openQueueForOutput", "oneQueue=" + oneQueue);

		if ((_prefix == null) || (_prefix.trim().equals("")))
			_prefix = "WM*";
		//MQQueue outboundQueue = null;
		int spaceindex = oneQueue.indexOf(" ");
		if ((spaceindex == -1) || (spaceindex == (oneQueue.length() - 1))) {
			try {
				// Added for JUnit testing
				if (_qMgr == null)
					initializeConnection(null, null);

				log(ARTLogger.INFO, 1003, "wmMQConnection.openQueueForOutput",
						"invoking accessqueue, options=" + openoptions);
			
				_outboundQueue = _qMgr.accessQueue(oneQueue, openoptions, null, _prefix, null);
				log(ARTLogger.INFO, 1003, "wmMQConnection.openQueueForOutput", "invoked accessqueue");
				
				_openedForOutput = true;
				_realOutboundQueueName = _outboundQueue.name;
			//	referenceOutboundQueue.put(getResolvedQueueManagerName()+oneQueue, outboundQueue);
				
			} catch (MQException mqe) {
				// mqe.printStackTrace();
				if (mqe.completionCode == MQConstants.MQCC_FAILED) {
					// Not all versions of WebSphereMQ support MQOO_INQUIRE.
					if (mqe.reasonCode == MQConstants.MQRC_OPTION_NOT_VALID_FOR_TYPE) {
						return openQueueForOutput(identityoption, 0);
					}
					// if (retryCount == 0)
					// {
					Object[] parms = { "Output", mqe.toString() };
					wmMQException wmqe = new wmMQException("3034", oneQueue, parms);
					wmqe.setLinkedException(mqe);
					throw wmqe;
					
				}
				_openedForOutput = true;
				
				_realOutboundQueueName = _outboundQueue.name;
				// else
				// _realOutboundQueueName = _queueName;
			} catch (Exception ex) {
				log(ARTLogger.ERROR, 1055, "Instantiating MQQueue(" + oneQueue + ")", ex.getMessage());
				// ex.printStackTrace();
				
				if (ex instanceof wmMQException)
					throw (wmMQException) ex;

			}
		} else {
			try {
				// User has specified more than 1 queue name. Use a distribution
				// list.
				if (_distributionList == null) {
					StringTokenizer st = new StringTokenizer(oneQueue);
					int tokens = st.countTokens();

					if (tokens > 0)
						_DLItems = new MQDistributionListItem[tokens];
					while (st.hasMoreTokens()) {
						MQDistributionListItem oneDLI = new MQDistributionListItem();
						oneDLI.queueManagerName = _realQMgrName;
						oneDLI.queueName = st.nextToken();
						_DLItems[--tokens] = oneDLI;
						log(ARTLogger.INFO, 1003, "wmMQConnection.openQueueForOutput",
								"creating DLI for=" + oneDLI.queueName);
					}
					do {
						// Open all queues in the distribution list
						_distributionList = _qMgr.accessDistributionList(_DLItems, openoptions);

						// Did all queues fail?
						if (_distributionList.getInvalidDestinationCount() == _DLItems.length)
							return false;

						_openedForOutput = updateDistributionList();

					} while (!_openedForOutput);
				}
			} catch (MQException mqe) {
				// mqe.printStackTrace();
				// if (--retryCount == 0)
				// {
				Object[] args = new Object[2];
				args[0] = "Output";
				args[1] = mqe.toString();
				wmMQException wmqe = new wmMQException("3034", oneQueue, args);
				wmqe.setLinkedException(mqe);
				throw wmqe;
				// }
			} catch (Exception ex) {
				// ex.printStackTrace();
				log(ARTLogger.ERROR, 1055, "Instantiating MQDistributionList(" + oneQueue + ")", ex.getMessage());
				if (ex instanceof wmMQException)
					throw (wmMQException) ex;

				/*
				 * if (--retryCount == 0) { Object[] args = new Object[2];
				 * args[0] = "Output"; args[1] = ex.toString(); wmMQException
				 * wmqe = new wmMQException("3034", oneQueue, args);
				 * wmqe.setLinkedException(ex); throw wmqe; }
				 */
			}
		}
		// }
		log(ARTLogger.INFO, 1003, "wmMQConnection.openQueueForOutput exiting, opened=",
				((_openedForOutput) ? "true" : "false"));
		return _openedForOutput;
	}

	private boolean updateDistributionList() {
		int invalidsubs = _distributionList.getInvalidDestinationCount();
		String realInboundQueueNames = "";
		// Rebuild the array of MQDistributionListItems
		if (invalidsubs > 0) {
			MQDistributionListItem[] temp = _DLItems;
			_DLItems = new MQDistributionListItem[_DLItems.length - invalidsubs];
			int newItems = 0;
			for (int i = 0; i < temp.length; i++) {
				if (temp[i].completionCode == MQConstants.MQCC_OK) {
					_DLItems[newItems++] = temp[i];
					realInboundQueueNames.concat(temp[i].queueName);
				}
			}
		} else {
			_realInboundQueueName = realInboundQueueNames;
			_openedForOutput = true;
		}

		return _openedForOutput;
	}

	/*
	 * Cache the wmMQConnection object representing a replyToQueue
	 */
	public void cacheReplyToConnection(wmMQConnection replyTo) throws wmMQException {
		log(ARTLogger.INFO, 1001, "wmMQConnection.cacheReplyToConnection", "");

		// Trax 1-1UQV4K [ RequestReply service continues to hang onto a stale
		// connection after fatal error ]
		// Use the replyTo CF... not the _factory CF. The key generated needs to
		// have the right hostName and
		// Queue manager name.
		wmMQConnectionFactory connFactory = (wmMQConnectionFactory) replyTo.getFactory();
		// ! Trax 1-1UQV4K

		// Open the queue for input so that a model name can be generated.
		// This is done here because openQueueForInput() is protected.
		replyTo.openQueueForInput(true, false, MQConstants.MQOO_INQUIRE);

		// Use the original queue name instead of the resolved queue name
		// This will help to locate dynamically created queues
		String resolvedQueueName = replyTo.getResolvedQueueName(true);

		if (_replyToConnections == null)
			_replyToConnections = new Hashtable();

		// Trax 1-RV4RN retrieveReplyToConnection() always fails to find
		// connection.
		// _replyToConnections.put(connFactory.getQueueManagerName() + "__" +
		// resolvedQueueName,

		// Trax 1-ZSMCG The replyTo connection needs to be cached with key
		// including hostname
		/*
		 * _replyToConnections.put(new String(connFactory.getQueueManagerName()
		 * + "__" + resolvedQueueName).trim(), replyTo);
		 */
		String hostName = connFactory.getHostName();
		if ((hostName == null) || (hostName.trim().equals("")))
			hostName = "";
		_replyToConnections.put(
				new String(hostName + "__" + connFactory.getQueueManagerName() + "__" + resolvedQueueName).trim(),
				replyTo);
		// Trax 1-ZSMCG

		log(ARTLogger.INFO, 1002, "wmMQConnection.cacheReplyToConnection", "");
	}

	public void moveMsgToDLQ(wmMQMessage msg, String deadLetterQ, String deadLetterQMgr, String service,
			boolean inbound, String deadLetterMessageHeaders) throws wmMQException {
		moveMsgToDLQ(msg, deadLetterQ, deadLetterQMgr, getResolvedQueueName(inbound), getResolvedQueueManagerName(),
				service, deadLetterMessageHeaders);
	}

	/**
	 * Move the MQMessage to the DeadLetterQueue
	 *
	 * @param msg
	 *            an wmMQMessage object containing the message to be moved
	 * @param queue
	 *            a Values object representing the original queue
	 * @param deadLetterQ
	 *            a String containing the name of the Dead Letter Queue
	 * @param service
	 *            a String containing the name of the service invoking this
	 *            method
	 * @param deadLetterMessageHeaders
	 *            a String (NONE|DLH|MQMD|DLH & MQMD) tospecify which headers to
	 *            include along with the payload.
	 */
	@SuppressWarnings("squid:S1541")
	public void moveMsgToDLQ(wmMQMessage msg, String deadLetterQ, String deadLetterQMgr, String destinationQueueName,
			String destinationQueueMgrName, String service, String deadLetterMessageHeaders) throws wmMQException {
		log(ARTLogger.INFO, 1001, "moveMsgToDLQ", "");
		// MQSeriesAdmin.dumpValues("moveMsgToDLQ queue", queue);

		wmMQConnection DLQconn = null;

		if ((deadLetterQMgr == null) || (deadLetterQMgr.trim().equals("")))
			deadLetterQMgr = this.getResolvedQueueManagerName();

		if ((deadLetterQ == null) || (deadLetterQ.trim().equals("")))
			deadLetterQ = "SYSTEM.DEAD.LETTER.QUEUE";

		try {
			msg.setMQMessage(buildDeadLetterHeader(msg.getMQMessage(), destinationQueueName, destinationQueueMgrName,
					deadLetterQ, service, deadLetterMessageHeaders));

			// String connname = getResolvedQueueManagerName() + "__" +
			// getResolvedQueueName(inbound);
			String connname = deadLetterQMgr + "__" + deadLetterQ;

			if (_DLQConnections == null)
				_DLQConnections = new Hashtable();
			else
				DLQconn = (wmMQConnection) _DLQConnections.get(connname);

			if (DLQconn == null) {
				wmMQConnectionFactory connFactory = wmMQAdapter.findConnectionFactory(connname);
				if (connFactory == null) {
					connFactory = wmMQConnectionFactory.clone((wmMQConnectionFactory) getFactory());
					wmMQAdapter.cacheConnectionFactory(connname, connFactory);
					connFactory.setQueueManagerName(deadLetterQMgr);
					connFactory.setQueueName(deadLetterQ);
				}

				DLQconn = (wmMQConnection) connFactory.createManagedConnectionObject(null, null);
				DLQconn.setFactory(connFactory);
				DLQconn.initializeConnection(null, null);
				log(ARTLogger.INFO, 1003, "wmMQConnection.moveMsgToDLQ", "caching DLQconn " + DLQconn);
				_DLQConnections.put(connname, DLQconn);
			}

			DLQconn.put(msg, null, null);

			if (InTransaction()) {
				// Remove the message from the queue
				((wmMQTransactionalConnection) this).commit();
				((wmMQTransactionalConnection) this).begin();
			}

			log(ARTLogger.INFO, 3040, service, deadLetterQ);
		} catch (Exception ex) {
			// ex.printStackTrace();
			// Trax 1-WVILA. Add hex dump of message that was not written to the
			// DLQ to the 3041 message
			StringBuffer sb = new StringBuffer(ex.getMessage());

			if (wmMQAdapter.getIncludeDataIn3041()) {
				sb.append("\nMessage not moved to " + deadLetterQ + ":\n");

				byte[] dumpbytes = null;
				try {
					msg.getMQMessage().seek(0);
					dumpbytes = new byte[msg.getMQMessage().getMessageLength()];
					msg.getMQMessage().readFully(dumpbytes);
				} catch (IOException ioe) {
					dumpbytes = new byte[0];
				}

				int len = dumpbytes.length;
				// int loops = len / 16;
				for (int i = 0; len > 0; i++) {
					// 1234567890123456
					String display = new String(dumpbytes, (i * 16), ((len > 16) ? 16 : len)) + "                ";
					String offsetstart = "00000000" + Integer.toHexString(i * 16).toUpperCase();
					String offsetend = "00000000" + Integer.toHexString(i * 16 + ((len > 15) ? 15 : len)).toUpperCase();
					sb.append("Bytes[" + offsetstart.substring(offsetstart.length() - 8) + " - "
							+ offsetend.substring(offsetend.length() - 8) + "] = ");
					int j = 0;
					for (j = 0; (j < 16) && (len > 0); j++, len--) {
						int onebyte = dumpbytes[i * 16 + j];
						if (onebyte < 0)
							onebyte += 256;
						if (onebyte < 16)
							sb.append("0");
						sb.append(Integer.toHexString(onebyte).toUpperCase() + " ");
						// These characters mess up the display
						if ((onebyte == 7) || (onebyte == 8) || (onebyte == 9) || (onebyte == 10) || (onebyte == 13))
							display = ((j == 0) ? "" : display.substring(0, j)) + "?"
									+ ((j == 16) ? "" : display.substring(j + 1));
					}
					if (j < 16)
						for (; j < 16; j++)
							sb.append("   ");
					sb.append("* " + display.substring(0, 16) + "* <<<\n");
				}
			}
			log(ARTLogger.INFO, 3041, deadLetterQ, sb.toString());
			throw new wmMQException("3041", deadLetterQ, new Object[] { sb.toString() });
		}
		log(ARTLogger.INFO, 1002, "moveMsgToDLQ", "");
	}

	/*
	 * Build the Dead Letter Header structure and append it to the beginning of
	 * the payload data.
	 *
	 * @param m a IncomingMsg object containing the undeliverable message
	 * 
	 * @param qName a String object containing the original queue name
	 */
	public MQMessage buildDeadLetterHeader(MQMessage msg, String queuename, String qmgrname, String dlqName,
			String service, String deadLetterMessageHeaders) {
		log(ARTLogger.INFO, 1001, "buildDeadLetterHeader", "");

		// 000000000111111111122222222223333333333444444444
		// 123456789012345678901234567890123456789012345678
		String fortyEightBlanks = "                                                ";
		String putApplName = "com.wm.adapter.wmmqadapter.service." + service;
		putApplName = putApplName.substring(putApplName.length() - 28);

		String qName = queuename + fortyEightBlanks;
		String qMgrName = qmgrname + fortyEightBlanks;

		String format = msg.format + fortyEightBlanks;
		format = format.substring(0, 8);

		// Create PutDate and PutTime strings
		Calendar rightNow = Calendar.getInstance();
		String s0, s1, s2, s3 = "";
		String putDate = "";
		String putTime = "";
		s0 = "0" + new Integer(rightNow.get(Calendar.MONTH) + 1).toString();
		s1 = "0" + new Integer(rightNow.get(Calendar.DAY_OF_MONTH));
		putDate = new Integer(rightNow.get(Calendar.YEAR)).toString() + s0.substring(s0.length() - 2)
				+ s1.substring(s1.length() - 2);

		s0 = "0" + new Integer(rightNow.get(Calendar.HOUR_OF_DAY)).toString();
		s1 = "0" + new Integer(rightNow.get(Calendar.MINUTE)).toString();
		s2 = "0" + new Integer(rightNow.get(Calendar.SECOND)).toString();
		s3 = "0" + new Integer(rightNow.get(Calendar.MILLISECOND) / 10).toString();
		putTime = s0.substring(s0.length() - 2) + s1.substring(s1.length() - 2) + s2.substring(s2.length() - 2)
				+ s3.substring(s3.length() - 2);

		if (null == qMgrName) //NOSONAR
			qMgrName = "Unavailable" + fortyEightBlanks;

		qName = qName.substring(0, 48); // Pad queue name to 48 characters
		qMgrName = qMgrName.substring(0, 48); // Pad queue name to 48 characters

		try {
			// Feature Request 1-W3XEN - include the original message's MQMD
			// header in the message written to the DLQ
			// The MQMD header is written as a byte[] in this format:

			/*
			 * offset Property Type ------- ----------- --------- +0000
			 * Identifier ("MD  ") String(4) +0004 Version Int +0008 Report Int
			 * +000C MessageType Int +0010 Expiry Int +0014 Feedback Int +0018
			 * Encoding Int +001C CharacterSet Int +0020 Format String(8) +0028
			 * Priority Int +002C Persistence Int +0030 MessageId byte[24] +0048
			 * CorrelationId byte[24] +0060 BackoutCount Int +0064
			 * ReplyToQueueName String(48) +0094 ReplyToQueueManagerName
			 * String(48) +00C4 UserId String +00D0 AccountingToken byte[32]
			 * +00F0 ApplicationIdData String(32) +0110 PutApplicationType Int
			 * +0114 PutApplicationName String(28) +0130 PutDate String(8) +0138
			 * PutTime String(8) +0140 ApplicationOriginData String(4)
			 *
			 * If the value of the Version property is greater than 1, these
			 * fields will be included:
			 *
			 * +0144 GroupId byte[24] +015C MessageSequenceNumber Int +0160
			 * Offset Int +0164 MessageFlags Int +0168 OriginalLength Int
			 *
			 * The numbers in parenthesis after the String type indicate the
			 * length of the string. The number in brackets after the byte type
			 * indicate the length of the byte array.
			 */
			byte[] mqmdBuf = new byte[0];
			int headers = getDeadLetterMessageHeadersIndex(deadLetterMessageHeaders);
			// Trax 1-WVILA. Include MQMD header if requested
			// Unfortunately, the signature of the MQMD.writeTo() method has
			// changed between versions of the com.ibm.mq.jar file. The
			// getMQMDFields()
			// method determines the method's signature then invokes the method
			// with
			// the proper arguments.
			if (headers > 1)
				mqmdBuf = getMQMDFields(msg);

			byte[] msgBuf = new byte[msg.getMessageLength()];
			msg.seek(0);
			msg.readFully(msgBuf);
			msg.seek(0);

			// Trax 1-WVILA. Include DLH if requested
			if ((headers % 2) > 0) {
				msg.writeString("DLH "); // MQDLH_STRUC_ID
				msg.writeInt(1); // MQDLH_VERSION_1
				msg.writeInt(MQConstants.MQFB_APPL_TYPE_ERROR); // MQDLH_REASON
				msg.writeString(qName); // MQDLH_DESTQNAME
				msg.writeString(qMgrName); // MQDLH_DESTQMGRNAME
				msg.writeInt(msg.encoding); // MQDLH_ENCODING
				msg.writeInt(msg.characterSet); // MQDLH_CODEDCHARSETID
				msg.writeString(format); // MQDLH_FORMAT
				msg.writeInt(0); // MQDLH_PUTAPPLTYPE
				msg.writeString(putApplName); // MQDLH_PUTAPPLNAME
				msg.writeString(putDate); // MQDLH_PUTDATE
				msg.writeString(putTime); // MQDLH_PUTDATE
			}
			// Feature Request 1-W3XEN - include the original message's MQMD
			// header in the message written to the DLQ
			// Trax 1-WVILA. Include MQMD header if requested
			if (headers > 1)
				msg.write(mqmdBuf);
			msg.write(msgBuf);

			if (headers > 0)
				msg.format = MQConstants.MQFMT_DEAD_LETTER_HEADER; // Original payload
															// data
		} catch (MQException mqe) {
			log(ARTLogger.INFO, 3041, dlqName, mqe.getMessage());
		} catch (IOException ioe) {
			log(ARTLogger.INFO, 3041, dlqName, ioe.getMessage());
		}

		log(ARTLogger.INFO, 1001, "buildDeadLetterHeader", "");

		return msg;
	}

	/*
	 * Copy the MQMD header files to a byte array
	 * 
	 * Unfortunately, the signature of the MQMD.writeTo() method has changed
	 * between versions of the com.ibm.mq.jar file. This method determines the
	 * method's signature then invokes the method with the proper arguments.
	 * 
	 * There may be other variations, but these are the known signatures
	 * 
	 * mqmd.writeTo(DataOutputStream daos)
	 * 
	 * mqmd.writeTo(DataOutputStream daos, boolean flag)
	 * 
	 * mqmd.writeTo(DataOutputStream daos, int ccsid, boolean flag)
	 */
	public byte[] getMQMDFields(MQMessage msg) throws MQException {
		log(ARTLogger.INFO, 1001, "getMQMDFields", "");
		byte[] mqmdBuf = new byte[0];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream daos = new DataOutputStream(baos);
		try {
			MQMD mqmd = msg;
			Method[] methods = mqmd.getClass().getMethods();
			for (int m = 0; m < methods.length; m++) {
				if (methods[m].getName().equals("writeTo")) {
					Class[] args = methods[m].getParameterTypes();
					switch (args.length) {
					case 1:
						methods[m].invoke(mqmd, new Object[] { daos });
						break;
					case 2:
						methods[m].invoke(mqmd, new Object[] { daos, new Boolean(false) });
						break;
					case 3:
						methods[m].invoke(mqmd,
								new Object[] { daos, new Integer(msg.characterSet), new Boolean(false) });
						break;
					default:
						// Do nothing
						log(ARTLogger.ERROR, 1003, "getMQMDFields", "could not invoke writeTo()");
						for (int a = 0; a < args.length; a++)
							log(ARTLogger.ERROR, 1003, "getMQMDFields", "arg[" + a + "]=" + args.getClass().getName());
						break;
					}
					break;
				}
			}
			mqmdBuf = baos.toByteArray();
		} catch (InvocationTargetException ite) {
		} catch (IllegalAccessException iae) {
		}
		log(ARTLogger.INFO, 1002, "getMQMDFields", "");
		return mqmdBuf;
	}

	/*
	 * Retrieve cached wmMQConnection object representing a replyToQueue
	 */
	public wmMQConnection retrieveReplyToConnection(String replyTo) throws wmMQException {
		log(ARTLogger.INFO, 1001, "wmMQConnection.retrieveReplyToConnection", "");

		if ((_replyToConnections != null) && (_replyToConnections.containsKey(replyTo)))
			return (wmMQConnection) _replyToConnections.get(replyTo);

		return null;
	}

	/*
	 * Cleans the cache of dynamically built connections
	 *
	 */
	public void cleanConnectioncache() {
		log(ARTLogger.INFO, 1001, "wmMQConnection:cleanConnectioncache", "");
		if (_replyToConnections != null) {
			for (Enumeration econns = _replyToConnections.keys(); econns.hasMoreElements();) {
				String connName = (String) econns.nextElement();
				wmMQConnection oneConn = (wmMQConnection) _replyToConnections.get(connName);
				try {
					log(ARTLogger.INFO, 1003, "wmMQConnection:cleanConnectioncache", "destroying " + oneConn);
					oneConn.destroy();
				} catch (ResourceException re) {
				}
			}
			_replyToConnections = null;
		}
		log(ARTLogger.INFO, 1002, "wmMQConnection:cleanConnectioncache", "");
	}

	/*
	 * Destroys the connection.
	 */
	@Override
	@SuppressWarnings("squid:S1541")
	protected void destroyConnection() throws AdapterException {
		log(ARTLogger.INFO, 1001, "wmMQConnection.destroyConnection", "");
		log(ARTLogger.INFO, 1003, "wmMQConnection.destroyConnection", "this=" + this);
		try {
			// Trax 1-SYJSX. The Queue Manager will commit active transactions
			// when _qMgr.disconnect() is invoked.
			if ((this instanceof wmMQTransactionalConnection)
					&& (((wmMQTransactionalConnection) this).InTransaction())) {
				String actionBeforeDisconnect = ((wmMQTransactionalConnectionFactory) this._factory)
						.getActionBeforeDisconnect();
				if ((_qMgr != null) && (_qMgr.isConnected())) {
					try {
						if (actionBeforeDisconnect.equalsIgnoreCase("COMMIT"))
							((wmMQTransactionalConnection) this).commit();
						else if (actionBeforeDisconnect.equalsIgnoreCase("ROLLBACK"))
							((wmMQTransactionalConnection) this).rollback();
					} catch (ResourceException ex) {
						log(ARTLogger.INFO, 1003, "wmMQConnection.destroyConnection",
								"Could not" + actionBeforeDisconnect + " active transaction");
						ex.printStackTrace();
					}
				}
			}

			// Trax 1-1UQV4K. Catch exception so that rest of the cleanup can
			// proceed...
			try {

	        	if (_inboundQueue != null)
	                _inboundQueue.close();
	            if (_outboundQueue != null)
	                _outboundQueue.close();
			    
				if (_distributionList != null)
					_distributionList.close();
			} catch (MQException ex) {
				log(ARTLogger.WARNING, 1055, "Destroying Connection", ex.getMessage());
			}

			// Close outboundQueues map
			if (_outboundQueues != null && !_outboundQueues.isEmpty()) {
				for (Iterator queues = _outboundQueues.values().iterator(); queues.hasNext();) {
					MQQueue oneQueue = (MQQueue) queues.next();

					// Trax 1-1UQV4K. Catch exception so that rest of the
					// cleanup can proceed...
					try {

						if (oneQueue.isOpen()) {
							oneQueue.close();
						}
					} catch (MQException ex) {
						log(ARTLogger.WARNING, 1055, "Destroying Connection", ex.getMessage());
					}
				}
				_outboundQueues.clear();
			}

			// Close the queues opened for Inquire
			if (_queuesOpenedForInquire != null && !_queuesOpenedForInquire.isEmpty()) {
				for (Iterator queues = _queuesOpenedForInquire.values().iterator(); queues.hasNext();) {
					MQQueue oneQueue = (MQQueue) queues.next();

					// Trax 1-1UQV4K. Catch exception so that rest of the
					// cleanup can proceed...
					try {

						if (oneQueue.isOpen()) {
							oneQueue.close();
						}
					} catch (MQException ex) {
						log(ARTLogger.WARNING, 1055, "Destroying Connection", ex.getMessage());
					}
				}
				_queuesOpenedForInquire.clear();
			}

			// Trax 1-XHJ94 - Stale Connections - Probably caused by
			// NullPointerException
			try {
				((wmMQConnectionFactory) getFactory()).cleanConnectioncache(this);
			} catch (Exception ex) {
				log(ARTLogger.CRITICAL, 1055, "Cleaning connection cache", ex.getMessage());
			}

			// Trax 1-1UQV4K. Catch exception so that rest of the cleanup can
			// proceed...
			try {
				// Disconnect the PCFMessageAgent if it has been created
				if (_pcfMessageAgent != null) {
					_pcfMessageAgent.disconnect();
					_pcfMessageAgent = null;
				}
			} catch (MQException ex) {
				log(ARTLogger.WARNING, 1055, "Destroying Connection", ex.getMessage());
			}

			// Trax 1-1UQV4K. Catch exception so that rest of the cleanup can
			// proceed...
			try {

				if ((_qMgr != null) && (_qMgr.isConnected())) {
					_qMgr.close();
					// Trax 1-QJXA3 - added disconnect() invocation to remove
					// channel connection.
					_qMgr.disconnect();
				}
			} catch (MQException ex) {
				log(ARTLogger.WARNING, 1055, "Destroying Connection", ex.getMessage());
			}

			if (_DLQConnections != null) {
				for (Enumeration econns = _DLQConnections.keys(); econns.hasMoreElements();) {
					String connname = (String) econns.nextElement();
					wmMQConnection oneconn = (wmMQConnection) _DLQConnections.get(connname);
					log(ARTLogger.INFO, 1003, "wmMQConnection.destroyConnection",
							"destroying DLQconn name " + oneconn.getQueueName());
					oneconn.destroyConnection();
					_DLQConnections.remove(connname);
				}
			}

			cleanConnectioncache(); // Clean the _replyToConnections
		}
		// Trax log 1-SGRKK. A NullPointerException can occur if this method is
		// invoked recursively, or multiple times.
		catch (NullPointerException npe) {
			// Possibly a recursion problem. Just clean up
		} finally {
    		_inboundQueue = null;
    		_outboundQueue = null;
			_distributionList = null;

			_openedForInput = false;
			_openedForOutput = false;

			_DLQConnections = null;

			_qMgr = null;
		}

		log(ARTLogger.INFO, 1002, "wmMQConnection.destroyConnection", "");
	}

	// Trax 1-VF52N - max connections property ignored if
	// overrideConnection is used.
	/*
	 * Overridden from wmManagedConnection if the
	 * watt.WmMQAdapter.Cache.Overridden.Connections property is set to false,
	 * then indicate that cleanup is required after connection use.
	 */
	@Override
	protected boolean cleanupRequiredAfterConnectionUse() {
		return (!((wmMQConnectionFactory) this._factory).isCacheOverriddenConnections());
	}

	/*
	 * Overridden from wmManagedConnection Clean up the overridden connection
	 * cache. This method will only be invoked if the
	 * watt.WmMQAdapter.Cache.Overridden.Connections property is set to false.
	 */
	@Override
	protected void cleanupConnectionAfterUse() throws ResourceException {
		((wmMQConnectionFactory) this._factory).cleanConnectioncache(this);
	}
	// Trax 1-VF52N

	/*
	 * Checks a resourceDomain value. This method returns Boolean.TRUE if
	 * testValue is valid in this context, Boolean.FALSE if it is not valid, or
	 * null if the value cannot be confirmed by the adapter. The TutorialAdapter
	 * doesn't support this feature. Implement this method if your adapter
	 * implements resourceDomains. This method is called when a user types a
	 * value into the Adapter Service Editor or Adapter Notification Editor
	 * while the editor is displaying an incomplete resourceDomain
	 * (ResourceDomainValues.setComplete is false) and the
	 * ResourceDomainValues.canValidate flag is set to true.
	 *
	 * serviceName is the name of the service template. resourceDomainName is
	 * the name of the resourceDomain. values are the values for the
	 * resourceDomain parameters. testValue is the value to test. Returns
	 * Boolean.TRUE if testValue is valid in this context, Boolean.FALSE if it
	 * is not valid, or null if the value cannot be confirmed by the adapter.
	 *
	 * AdapterException is thrown if the method encounters an error.
	 */

	@Override
	@SuppressWarnings("squid:S1541")
	public Boolean adapterCheckValue(String serviceName, String resourceDomainName, String[][] values, String testValue)
			throws AdapterException {
		log(ARTLogger.INFO, 1001, "wmMQConnection.adapterCheckValue - serviceName=" + serviceName
				+ ",resourceDomainName=" + resourceDomainName, "");
		if ((values != null) && (values.length > 0)) {
			for (int i = 0; i < values.length; i++)
				if ((values[i] != null) && (values[i].length > 0))
					for (int z = 0; z < values[i].length; z++)
						log(ARTLogger.INFO, 1003, "values[" + i + "][" + z + "]=", values[i][z]);
		}

		if (resourceDomainName.equals(wmMQAdapterConstant.USER_DEFINED_PROPERTIES)) {
			if (testValue != null && !testValue.trim().equals("")) {
				testValue = testValue.trim();
				if (testValue.indexOf(".") <= 0 || testValue.startsWith("JMSProperties.")
						|| testValue.startsWith("mcd.") || testValue.endsWith(".")) {
					return Boolean.FALSE;
				}

				StringTokenizer tokens = new StringTokenizer(testValue, ".", true);
				boolean previousTokenWasADot = false;
				while (tokens.hasMoreTokens()) {
					String oneToken = tokens.nextToken().trim();
					if (oneToken.equals("") || (oneToken.equals(".") && previousTokenWasADot)) {
						return Boolean.FALSE;
					}

					if (oneToken.equals(".")) {
						previousTokenWasADot = true;
					} else {
						previousTokenWasADot = false;
					}
				}

				// Check if the entered properties are already defined in the
				// JMSProperties
				String checkValue = "JMSProperties." + testValue;
				for (int i = 0; i < wmMQMQMD.jmsProperties.length; i++) {
					if (wmMQMQMD.jmsProperties[i].equals(checkValue)) {
						return Boolean.FALSE;
					}
				}
			}
		}

		return new Boolean(true);
	}

	/*
	 * Looks up values for a resourceDomain. The Adapter Service Editor or
	 * Adapter Notification Editor will call this method for the resource
	 * domains that are registered using addResourceDomainLookup and return the
	 * values for tools to fill in each display widget. Implement this method if
	 * your adapter implements resourceDomains. Returns a
	 * com.wm.adk.metadata.ResourceDomainValues object with the proper data.
	 * Returns multiple objects if you are using tuples.
	 *
	 * serviceName is the name of the adapter service/notification template.
	 * resourceDomainName is the name of the resourceDomain. values are the
	 * values for the dependency resourceDomain parameters. AdapterException is
	 * thrown if the method encounters an error.
	 */
	@Override
	@SuppressWarnings("squid:S1541")
	public ResourceDomainValues[] adapterResourceDomainLookup(String serviceName, String resourceDomainName,
			String[][] values) throws AdapterException {
		log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup", " - serviceName=" + serviceName);
		log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
				" - resourceDomainName=" + resourceDomainName);
		try {
			if ((values != null) && (values.length > 0)) {
				for (int i = 0; i < values.length; i++)
					if ((values[i] != null) && (values[i].length > 0))
						for (int z = 0; z < values[i].length; z++)
							log(ARTLogger.INFO, 1003, "values[" + i + "][" + z + "]=", values[i][z]);
			}

			if (resourceDomainName.equals(wmMQAdapterConstant.WAIT_INTERVAL_TYPE)) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning " + resourceDomainName);
				return new ResourceDomainValues[] {
						new ResourceDomainValues(resourceDomainName, new String[] { "java.lang.Integer" }) };
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.MQMD_REPLY_FIELDS))
					|| (resourceDomainName.equals(wmMQAdapterConstant.MQMD_REPLY_FIELD_TYPES))) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup ",
						"- processing" + resourceDomainName);
				// Returns a list of Queue names for a specified Queue Manager.
				// values[0][0] is Queue Manager name.
				// These dependencies are defined in the setResourceDomain
				// methods.
				return new ResourceDomainValues[] {
						new ResourceDomainValues(wmMQAdapterConstant.MQMD_REPLY_FIELDS, wmMQMQMD.mqmdFieldDisplayNames),
						new ResourceDomainValues(wmMQAdapterConstant.MQMD_REPLY_FIELD_TYPES, wmMQMQMD.mqmdFieldTypes) };
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.MQMD_REQUEST_FIELDS))
					|| (resourceDomainName.equals(wmMQAdapterConstant.MQMD_REQUEST_FIELD_TYPES))) {

				ResourceDomainValues rdv0 = new ResourceDomainValues(wmMQAdapterConstant.MQMD_REQUEST_FIELDS,
						wmMQMQMD.mqmdFieldDisplayNames);
				ResourceDomainValues rdv1 = new ResourceDomainValues(wmMQAdapterConstant.MQMD_REQUEST_FIELD_TYPES,
						wmMQMQMD.mqmdFieldTypes);

				return new ResourceDomainValues[] { rdv0, rdv1 };
			}

			else if ((resourceDomainName.equals(wmMQAdapterConstant.MQMD_OUTPUT_FIELDS))
					|| (resourceDomainName.equals(wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES))) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup ",
						"- processing" + resourceDomainName);
				return new ResourceDomainValues[] {
						new ResourceDomainValues(wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
								wmMQMQMD.mqmdFieldDisplayNames),
						new ResourceDomainValues(wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES,
								wmMQMQMD.mqmdFieldTypes) };
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.MQMD_INPUT_FIELDS))
					|| (resourceDomainName.equals(wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES))
					|| (resourceDomainName.equals(wmMQAdapterConstant.SELECTION_CRITERIA))) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - processing " + resourceDomainName);
				String[] domainvalues0 = new String[0];
				String domainName0 = "";
				String[] domainvalues1 = new String[0];
				String domainName1 = "";

				if ((serviceName.endsWith("Put")) || (serviceName.endsWith("RequestReply"))) {
					domainName0 = wmMQAdapterConstant.MQMD_INPUT_FIELDS;
					domainvalues0 = wmMQMQMD.mqmdFieldDisplayNames;
					domainName1 = wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES;
					domainvalues1 = wmMQMQMD.mqmdFieldTypes;

				} else if (serviceName.endsWith("Get") || (serviceName.endsWith("Peek"))) {
					domainName0 = wmMQAdapterConstant.SELECTION_CRITERIA;
					domainvalues0 = wmMQMQMD.selectionCriteria;
					domainName1 = wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES;
					domainvalues1 = wmMQMQMD.selectionCriteriaTypes;
				} else {
					// Test
					domainName0 = wmMQAdapterConstant.MQMD_INPUT_FIELDS;
					domainvalues0 = wmMQMQMD.mqmdFieldDisplayNames;
					domainName1 = wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES;
					domainvalues1 = wmMQMQMD.mqmdFieldTypes;
					ResourceDomainValues rdv0 = new ResourceDomainValues(domainName0, domainvalues0);
					ResourceDomainValues rdv1 = new ResourceDomainValues(domainName1, domainvalues1);
					ResourceDomainValues rdv2 = new ResourceDomainValues(wmMQAdapterConstant.INPUT_FIELD_NAMES,
							domainvalues0);
					return new ResourceDomainValues[] { rdv0, rdv1, rdv2 };
				}

				ResourceDomainValues rdv0 = new ResourceDomainValues(domainName0, domainvalues0);
				ResourceDomainValues rdv1 = new ResourceDomainValues(domainName1, domainvalues1);

				return new ResourceDomainValues[] { rdv0, rdv1 };
			} else if (resourceDomainName.equals(wmMQAdapterConstant.FILTER_CRITERIA)) {
				return new ResourceDomainValues[] {
						new ResourceDomainValues(wmMQAdapterConstant.FILTER_CRITERIA, wmMQMQMD.mqmdFieldDisplayNames) };
			}
			// else if
			// (resourceDomainName.equals(wmMQAdapterConstant.SELECTION_CRITERIA))
			// {
			// return new ResourceDomainValues[] {new
			// ResourceDomainValues(wmMQAdapterConstant.SELECTION_CRITERIA,
			// wmMQMQMD.selectionCriteria)};
			// }
			else if (resourceDomainName.equals(wmMQAdapterConstant.MQMD_INPUT_CONSTANTS)) {
				String[] domainvalues = setMQMDDefaultValues(values[0][0]);
				ResourceDomainValues rdv = new ResourceDomainValues(resourceDomainName, domainvalues);

				// if ((domainvalues.length <= 1) ||
				// (values[0][0].equals(wmMQAdapterConstant.MQMD_FEEDBACK)) ||
				// (values[0][0].equals(wmMQAdapterConstant.MQMD_FORMAT)))
				// {
				rdv.setComplete(false);
				rdv.setCanValidate(false);
				// }

				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup", " - returning " + values[0][0]);
				return new ResourceDomainValues[] { rdv };
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.MSG_BODY))
					|| (resourceDomainName.equals(wmMQAdapterConstant.REPLY_MSG_BODY))
					|| (resourceDomainName.equals(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY))
					|| (resourceDomainName.equals(wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY))) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning " + resourceDomainName);

				// Trax 1-11YH1R : Begin
				// return new ResourceDomainValues[] {new
				// ResourceDomainValues(resourceDomainName,
				// new String[] {resourceDomainName})};
				return new ResourceDomainValues[] {
						new ResourceDomainValues(resourceDomainName, new String[] { resourceDomainName }),
						new ResourceDomainValues(resourceDomainName + "Type", typeObject) };
				// Trax 1-11YH1R : End
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.QUEUE_MANAGER_NAME))
					|| (resourceDomainName.equals(wmMQAdapterConstant.QUEUE_NAME))) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning " + resourceDomainName);
				return new ResourceDomainValues[] {
						new ResourceDomainValues(resourceDomainName, new String[] { resourceDomainName }) };
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.CONDITION_CODE))
					|| (resourceDomainName.equals(wmMQAdapterConstant.ERROR_MSG))
					|| (resourceDomainName.equals(wmMQAdapterConstant.REASON_CODE))) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning " + resourceDomainName);
				return new ResourceDomainValues[] {
						new ResourceDomainValues(resourceDomainName, new String[] { resourceDomainName }) };
			}

			// Trax 1-11YH1R : Begin
			// else if
			// ((resourceDomainName.equals(wmMQAdapterConstant.MSG_BODY_TYPE))
			// ||
			// (resourceDomainName.equals(wmMQAdapterConstant.REPLY_MSG_BODY_TYPE))
			// ||
			// (resourceDomainName.equals(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE))
			// ||
			// (resourceDomainName.equals(wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY_TYPE))
			// )
			// {
			// log(ARTLogger.INFO, 1003,
			// "wmMQConnection.adapterResourceDomainLookup", " - returning " +
			// resourceDomainName);
			// return new ResourceDomainValues[] {new
			// ResourceDomainValues(resourceDomainName,
			// typeObject)};
			// }
			// Trax 1-11YH1R : End

			else if ((resourceDomainName.equals(wmMQAdapterConstant.QUEUE_MANAGER_NAME_TYPE))
					|| (resourceDomainName.equals(wmMQAdapterConstant.QUEUE_NAME_TYPE))) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning " + resourceDomainName);
				return new ResourceDomainValues[] { new ResourceDomainValues(resourceDomainName, typeString) };
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.REASON_CODE_TYPE))
					|| (resourceDomainName.equals(wmMQAdapterConstant.ERROR_MSG_TYPE))
					|| (resourceDomainName.equals(wmMQAdapterConstant.CONDITION_CODE_TYPE))) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning " + resourceDomainName);
				return new ResourceDomainValues[] { new ResourceDomainValues(resourceDomainName, typeString) };
			} else if (resourceDomainName.equals(wmMQAdapterConstant.RESET_CURSOR)) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning " + resourceDomainName);
				return new ResourceDomainValues[] {
						new ResourceDomainValues(resourceDomainName, new String[] { "yes", "no" }) };
			} else if (resourceDomainName.equals(wmMQAdapterConstant.RESET_CURSOR_TYPE)) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup", " - returning resetCursortype");
				return new ResourceDomainValues[] {
						new ResourceDomainValues(resourceDomainName, new String[] { "java.lang.String" }) };
			}

			else if (resourceDomainName.equals(wmMQAdapterConstant.DEAD_LETTER_QUEUE)) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup", " - returning deadLetterQueue");
				ResourceDomainValues rdv = new ResourceDomainValues(resourceDomainName, new String[] { "" });
				rdv.setComplete(false);
				rdv.setCanValidate(false);

				return new ResourceDomainValues[] { rdv };
			} else if (resourceDomainName.equals(wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER)) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning deadLetterQueueManager");
				ResourceDomainValues rdv = new ResourceDomainValues(resourceDomainName, noConstantValues);
				rdv.setComplete(false);
				rdv.setCanValidate(false);

				return new ResourceDomainValues[] { rdv };
			} else if (resourceDomainName.equals(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS)) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning deadLetterMessageHeader");
				return new ResourceDomainValues[] { new ResourceDomainValues(resourceDomainName,
						wmMQAdapterConstant.deadLetterMessageHeaderOptions) };
			} else if (resourceDomainName.equals(wmMQAdapterConstant.DEAD_LETTER_QUEUE_TYPE)) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning deadLetterQueueType");
				return new ResourceDomainValues[] {
						new ResourceDomainValues(wmMQAdapterConstant.DEAD_LETTER_QUEUE_TYPE, typeString) };
			} else if (resourceDomainName.equals(wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_TYPE)) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning deadLetterQueueManagerType");
				return new ResourceDomainValues[] { new ResourceDomainValues(
						wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_TYPE, new String[] { "java.lang.String" }) };
			} else if (resourceDomainName.equals(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_TYPE)) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning deadLetterMessageHeaderType");
				return new ResourceDomainValues[] { new ResourceDomainValues(
						wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_TYPE, new String[] { "java.lang.String" }) };
			} else if (resourceDomainName.equals(wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES)) {
				String name = values[0][0];

				if (name == null || name.equals(""))
					name = "noname";

				name = "JMSProperties." + name;

				return new ResourceDomainValues[] {
						new ResourceDomainValues(resourceDomainName, new String[] { name }) };
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.JMS_PROPERTIES))
					|| (resourceDomainName.equals(wmMQAdapterConstant.JMS_PROPERTY_TYPES))) {
				ResourceDomainValues rdv1 = new ResourceDomainValues(wmMQAdapterConstant.JMS_PROPERTIES,
						wmMQMQMD.jmsProperties);
				rdv1.setComplete(false);
				rdv1.setCanValidate(false);

				ResourceDomainValues rdv2 = new ResourceDomainValues(wmMQAdapterConstant.JMS_PROPERTY_TYPES,
						wmMQMQMD.jmsPropertyTypes);

				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup", " - returning jmsProperties");
				return new ResourceDomainValues[] { rdv1, rdv2 };
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE))
					|| (resourceDomainName.equals(wmMQAdapterConstant.OVERRIDE_REPLY_TO_PROPERTY_TYPE))) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning OVERRIDE_PROPERTY_TYPE");
				return new ResourceDomainValues[] {
						new ResourceDomainValues(resourceDomainName,
								new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
										"java.lang.String", "java.lang.String", "java.lang.String",
										"java.lang.String" }) };
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.QUEUE_MGR_PROPERTIES_LOOKUP))
					|| (resourceDomainName.equals(wmMQAdapterConstant.QUEUE_MGR_PROPERTY_TYPES_LOOKUP))) {
				ResourceDomainValues rdvQM1 = new ResourceDomainValues(wmMQAdapterConstant.QUEUE_MGR_PROPERTIES_LOOKUP,
						wmMQQueueManagerAttributes.qMgrProperties);
				rdvQM1.setComplete(false);
				rdvQM1.setCanValidate(false);

				ResourceDomainValues rdvQM2 = new ResourceDomainValues(
						wmMQAdapterConstant.QUEUE_MGR_PROPERTY_TYPES_LOOKUP,
						wmMQQueueManagerAttributes.qMgrPropertyTypes);

				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning queueMgrProperties");
				return new ResourceDomainValues[] { rdvQM1, rdvQM2 };
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.QUEUE_PROPERTIES_LOOKUP))
					|| (resourceDomainName.equals(wmMQAdapterConstant.QUEUE_PROPERTY_TYPES_LOOKUP))) {
				ResourceDomainValues rdvQM1 = new ResourceDomainValues(wmMQAdapterConstant.QUEUE_PROPERTIES_LOOKUP,
						wmMQQueueAttributes.qProperties);
				rdvQM1.setComplete(false);
				rdvQM1.setCanValidate(false);

				ResourceDomainValues rdvQM2 = new ResourceDomainValues(wmMQAdapterConstant.QUEUE_PROPERTY_TYPES_LOOKUP,
						wmMQQueueAttributes.qPropertyTypes);

				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup", " - returning queueProperties");
				return new ResourceDomainValues[] { rdvQM1, rdvQM2 };
			} else if (resourceDomainName.equals(wmMQAdapterConstant.PCF_COMMAND)) {
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning List of allowed PCFCommands");
				ResourceDomainValues rdv = new ResourceDomainValues(resourceDomainName,
						wmPCFCommandMetadataFactory.getInstance().getSupportedPCFCommands());

				return new ResourceDomainValues[] { rdv };
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETERS))
					|| (resourceDomainName.equals(wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETER_TYPES))) {
				String pcfCommand = values[0][0];

				wmPCFCommandMetadata pcfCommandMetadata = wmPCFCommandMetadataFactory.getInstance()
						.getPCFCommandMetadata(pcfCommand);

				ResourceDomainValues rdv1 = new ResourceDomainValues(wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETERS,
						pcfCommandMetadata.getRequiredRequestParameterDisplayNames());
				ResourceDomainValues rdv2 = new ResourceDomainValues(
						wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETER_TYPES,
						pcfCommandMetadata.getRequiredRequestParameterTypes());

				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning requiredRequestParameters");
				return new ResourceDomainValues[] { rdv1, rdv2 };
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETERS))
					|| (resourceDomainName.equals(wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETER_TYPES))) {
				String pcfCommand = values[0][0];

				wmPCFCommandMetadata pcfCommandMetadata = wmPCFCommandMetadataFactory.getInstance()
						.getPCFCommandMetadata(pcfCommand);

				String[] displayNames = pcfCommandMetadata.getOptionalRequestParameterDisplayNames();
				String[] types = pcfCommandMetadata.getOptionalRequestParameterTypes();
				if (displayNames.length == 0) {
					displayNames = new String[] { "" };
				}
				if (types.length == 0) {
					types = new String[] { "" };
				}
				ResourceDomainValues rdv1 = new ResourceDomainValues(wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETERS,
						displayNames);
				ResourceDomainValues rdv2 = new ResourceDomainValues(
						wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETER_TYPES, types);
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning requiredRequestParameters");
				return new ResourceDomainValues[] { rdv1, rdv2 };
			} else if ((resourceDomainName.equals(wmMQAdapterConstant.RESPONSE_PARAMETERS))
					|| (resourceDomainName.equals(wmMQAdapterConstant.RESPONSE_PARAMETER_TYPES))) {
				String pcfCommand = values[0][0];

				wmPCFCommandMetadata pcfCommandMetadata = wmPCFCommandMetadataFactory.getInstance()
						.getPCFCommandMetadata(pcfCommand);
				String[] displayNames = pcfCommandMetadata.getResponseParameterDisplayNames();
				String[] types = pcfCommandMetadata.getResponseParameterTypes();
				if (displayNames.length == 0) {
					displayNames = new String[] { "" };
				}
				if (types.length == 0) {
					types = new String[] { "" };
				}

				ResourceDomainValues rdv1 = new ResourceDomainValues(wmMQAdapterConstant.RESPONSE_PARAMETERS,
						displayNames);
				ResourceDomainValues rdv2 = new ResourceDomainValues(wmMQAdapterConstant.RESPONSE_PARAMETER_TYPES,
						types);

				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - returning requiredRequestParameters");
				return new ResourceDomainValues[] { rdv1, rdv2 };
			} else
				log(ARTLogger.INFO, 1003, "wmMQConnection.adapterResourceDomainLookup",
						" - did not process " + resourceDomainName);
		} catch (Exception excp) {
			// excp.printStackTrace();
			log(ARTLogger.ERROR, 1055, "In adapterResourceDomainLookup()", excp.getMessage());
		}
		return null;
	}

	/*
	 * Sets a reference to the WmAdapterAccess object. Will be called after the
	 * constructor has been called, but before any properties are set. The
	 * reference to WmAdapterAccess should be stored for later use, if needed.
	 * Register resourceDomain, using WmAdapterAccess.addResourceDomainLookup(),
	 * if the values need to query the resource.
	 *
	 * The parameter access is a reference to a WmAdapterAccess object.
	 * AdapterException is thrown if the method encounters an error.
	 */
	@Override
	public void registerResourceDomain(WmAdapterAccess access) throws AdapterException {
		try {
			// access.addResourceDomainLookup(wmMQAdapterConstant.WAIT_INTERVAL,
			// this);
			access.addResourceDomainLookup(wmMQAdapterConstant.WAIT_INTERVAL_TYPE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.MQMD_INPUT_CONSTANTS, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.MQMD_INPUT_FIELDS, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.SELECTION_CRITERIA, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.FILTER_CRITERIA, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.MQMD_OUTPUT_FIELDS, this);
			// access.addResourceDomainLookup(wmMQAdapterConstant.DEAD_LETTER_QUEUE,
			// this);
			// access.addResourceDomainLookup(wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER,
			// this);
			access.addResourceDomainLookup(wmMQAdapterConstant.DEAD_LETTER_QUEUE_TYPE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_TYPE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_TYPE, this);

			// Trax 1-11YH1R : Begin
			access.addResourceDomainLookup(wmMQAdapterConstant.MSG_BODY, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.MSG_BODY_TYPE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.REPLY_MSG_BODY, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.REPLY_MSG_BODY_TYPE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY_TYPE, this);
			// Trax 1-11YH1R : End

			access.addResourceDomainLookup(wmMQAdapterConstant.QUEUE_MANAGER_NAME, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.QUEUE_MANAGER_NAME_TYPE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.QUEUE_NAME, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.QUEUE_NAME_TYPE, this);

			access.addResourceDomainWithCheck(wmMQAdapterConstant.USER_DEFINED_PROPERTIES, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES, this);

			ResourceDomainValues rdValues = new ResourceDomainValues(wmMQAdapterConstant.USER_DEFINED_PROPERTY_TYPES,
					new String[] { "java.lang.String" });
			access.addResourceDomain(new ResourceDomainValues[] { rdValues });

			access.addResourceDomainLookup(wmMQAdapterConstant.JMS_PROPERTIES, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.JMS_PROPERTY_TYPES, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.RESET_CURSOR, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.RESET_CURSOR_TYPE, this);
			access.addResourceDomainWithCheck(wmMQAdapterConstant.MQMD_REQUEST_CONSTANTS, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.MQMD_REQUEST_FIELD_TYPES, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.MQMD_REPLY_FIELD_TYPES, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.MQMD_REQUEST_FIELDS, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.MQMD_REPLY_FIELDS, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.REASON_CODE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.REASON_CODE_TYPE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.CONDITION_CODE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.CONDITION_CODE_TYPE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.ERROR_MSG, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.ERROR_MSG_TYPE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.OVERRIDE_REPLY_TO_PROPERTY_TYPE, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.QUEUE_MGR_PROPERTIES_LOOKUP, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.QUEUE_MGR_PROPERTY_TYPES_LOOKUP, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.QUEUE_PROPERTIES_LOOKUP, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.QUEUE_PROPERTY_TYPES_LOOKUP, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.PCF_COMMAND, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETERS, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETER_TYPES, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETERS, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETER_TYPES, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.RESPONSE_PARAMETERS, this);
			access.addResourceDomainLookup(wmMQAdapterConstant.RESPONSE_PARAMETER_TYPES, this);
		} catch (Exception ex) {
			throw wmMQAdapter.getInstance().createAdapterException(1015, ex);
		}
	}

	// This Routine is used to instantiate an wmMQ User exit
	// It will create an instance of the named class using either the default
	// constructor
	// or a Constructor accepting a String parameter, and then determine if the
	// correct
	// wmMQ Interface is implemented by the loaded class.
	//
	// It is called with:
	// The type of Exit to load, for type checking
	// The name of the Exit to load
	// A String to be used as init parms for the exit

	private Object loadMQExit(Class exitType, String exitName, String exitParms) throws wmMQException {
		Object exitInstance = null;
		Class exitClass;
		Class[] parmList = { String.class };
		Object[] parms = { exitParms };

		String excType = "";
		try {
			// Load the Requested Class
			exitClass = Class.forName(exitName);

			// Use Default Constructor if no Init parms
			if (exitParms == null || exitParms.length() < 1) {
				exitInstance = exitClass.newInstance();
			}
			// Find the Constructor that takes a String if we have parms
			else {
				Constructor exitConstructor = exitClass.getConstructor(parmList);
				exitInstance = exitConstructor.newInstance(parms);
			}

			// Ensure the Class implements the requested MQ Interface
			if (exitType.isInstance(exitInstance)) {
				return exitInstance;
			} else {
				excType = "ClassCast";
			}
		}

		catch (ClassNotFoundException ex) {
			excType = "ClassNotFound";
		} catch (InstantiationException ex) {
			excType = "Instantiation";
		} catch (IllegalArgumentException ex) {
			excType = "IllegalArgument";
		} catch (IllegalAccessException ex) {
			excType = "IllegalAccess";
		} catch (NoSuchMethodException ex) {
			excType = "NoSuchMethod";
		} catch (SecurityException ex) {
			excType = "Security";
		} catch (InvocationTargetException ex) {
			excType = "InvocationTarget";
		} finally {
			if (!excType.trim().equals("")) {
				Object[] args = new Object[2];
				args[0] = exitName;
				args[1] = exitType.getName();
				throw new wmMQException("4011", excType, args); //NOSONAR
			}
		}

		return exitInstance;
	}

	private String[] setMQMDDefaultValues(String resourceDomainName) {
		log(ARTLogger.INFO, 1003, "wmMQConnection.setMQMDDefaultValues", " - resourceDomainName=" + resourceDomainName);
		String[] values = noConstantValues;
		if (resourceDomainName.endsWith(wmMQAdapterConstant.MQMD_CHARACTER_SET))
			values = wmMQMQMD.supportedCCSIDs;
		else if (resourceDomainName.endsWith(wmMQAdapterConstant.MQMD_ENCODING))
			values = wmMQMQMD.encodings;
		else if (resourceDomainName.endsWith(wmMQAdapterConstant.MQMD_FEEDBACK))
			values = wmMQMQMD.mqmdFeedbackOptions;
		else if (resourceDomainName.endsWith(wmMQAdapterConstant.MQMD_FORMAT))
			values = wmMQMQMD.mqmdFormatOptions;
		else if (resourceDomainName.endsWith(wmMQAdapterConstant.MQMD_MESSAGE_FLAGS))
			values = wmMQMQMD.mqmdMsgFlagOptions;
		else if (resourceDomainName.endsWith(wmMQAdapterConstant.MQMD_MESSAGE_TYPE))
			values = wmMQMQMD.mqmdMsgTypeOptions;
		else if (resourceDomainName.endsWith(wmMQAdapterConstant.MQMD_PERSISTENCE))
			values = wmMQMQMD.mqmdPersistenceOptions;
		else if (resourceDomainName.endsWith(wmMQAdapterConstant.MQMD_PRIORITY))
			values = wmMQMQMD.mqmdPriorityOptions;
		// Trax log 1-ST7P3. PutApplType predefined constants
		else if (resourceDomainName.endsWith(wmMQAdapterConstant.MQMD_PUT_APPLICATION_TYPE))
			values = wmMQMQMD.PutApplicationTypes;
		return values;
	}

	/**
	 * Return the resolved queue name - useful for model queues
	 *
	 */
	public String getResolvedQueueName(boolean inbound) {
		String name = "";
		if (inbound)
			if ((_realInboundQueueName == null) || (_realInboundQueueName.trim().equals("")))
				name = _queueName;
			else
				name = _realInboundQueueName;
		else if ((_realOutboundQueueName == null) || (_realOutboundQueueName.trim().equals("")))
			name = _queueName;
		else
			name = _realOutboundQueueName;

		if (name == null)
			name = ((wmMQConnectionFactory) getFactory()).getQueueName();

		return name;
	}

	/**
	 * Return the resolved queue name - useful for model queues
	 *
	 */
	public String getQueueName() {
		return _queueName;
	}

	/**
	 * Return the resolved queue manager name
	 *
	 */
	public String getResolvedQueueManagerName() {
		if ((_realQMgrName == null) || (_realQMgrName.trim().equals(""))) {
			wmMQConnectionFactory connFactory = (wmMQConnectionFactory) _factory;
			_realQMgrName = connFactory.getQueueManagerName();
		}

		return _realQMgrName;
	}

	public String getTransactionName() {
		return _transactionName;
	}

	synchronized public boolean InTransaction() {
		return _inTransaction;
	}

	synchronized public boolean inUse() {
		return _inUse;
	}

	public void setInUse(boolean inuse) {
		_inUse = inuse;
	}

	protected void log(int level, int minor, String arg0, String arg1) {
		ARTLogger logger = ((wmMQAdapter) wmMQAdapter.getInstance()).getLogger();
		if (logger == null) {
			System.out.println("Logger is null");
			return;
		}

		// Trax 1-WVILA. Allow user to override the logging level of adapter
		// messages.
		if (wmMQAdapter.getLogLevelOverrides().containsKey("" + minor))
			level = Integer.parseInt((String) wmMQAdapter.getLogLevelOverrides().get("" + minor)) - ARTLogger.DEBUG;

		String[] args = new String[2];
		args[0] = arg0;
		args[1] = arg1;
		// Trax log 1-S4OHA - move adapter logging to DEBUG PLUS
		logger.logDebugPlus(level, minor, args);
	}

	private String[] typeString = new String[] { "java.lang.String", "lang" };
	// Trax 1-11YH1R : Begin
	private String[] typeObject = new String[] { "java.lang.Object" };
	// Trax 1-11YH1R : End
	// private String[] typeBoolean = new String[] {"boolean"};
	private String[] noConstantValues = new String[] { "" };

	/**
	 * This method resets the inbound queue with the provided queue. This is
	 * used for setting a different inbound queue incase of the multi-queue
	 * child listener
	 * 
	 * @param queueName
	 * @throws ResourceException
	 */
	protected void resetInboundQueue(String queueName) throws ResourceException {
		log(ARTLogger.INFO, 1001, "wmMQConnection.resetInboundQueue", "");

		try {
			if (_qMgr == null) {
				initializeConnection(null, null);
		
    	    }
    	    else {
    			if (_inboundQueue != null && _inboundQueue.isOpen()) {
    			    _inboundQueue.close();
			    }
			}
		} catch (MQException mqe) {
			log(ARTLogger.ERROR, 1055, "Closing inbound queue", mqe.getMessage());

			throw wmMQAdapter.getInstance().createAdapterConnectionException(1055,
					new String[] { "Closing inbound queue", mqe.getMessage() }, mqe);
		}

		_openedForInput = false;
		_inboundQueue = null;
		_realInboundQueueName = null;
		_queueName = queueName;

		log(ARTLogger.INFO, 1002, "wmMQConnection.resetInboundQueue", "");
	}

	private static int getDeadLetterMessageHeadersIndex(String dlhOption) {
		if (dlhOption != null && !dlhOption.equals("")) {
			for (int i = 0; i < wmMQAdapterConstant.deadLetterMessageHeaderOptions.length; i++) {
				if (dlhOption.equalsIgnoreCase(wmMQAdapterConstant.deadLetterMessageHeaderOptions[i])) {
					return i;
				}
			}
		}

		return 3; // Return default; Both DLH and MQMD
	}

	// private void setHostName(wmMQConnectionFactory connFactory, String
	// hostName){
	//
	// if(hostName==null){
	// MQEnvironment.hostname = connFactory.getHostName();
	//
	// if(connFactory.getHostName() == null){
	//
	// // We need to use binding mode now.
	// //With this statement, we instruct MQSeries to determine whether to use
	// Client-mode
	// //or Bindings-mode, based on the value of the host name. If the host name
	// is null,
	// //Bindings-mode will be used.
	//
	// MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,
	// MQC.TRANSPORT_MQSERIES);
	// }
	//
	// }else{
	// MQEnvironment.hostname = hostName;
	// }
	// }

	// private void setPort(wmMQConnectionFactory connFactory, String port){
	//
	// if (port == null){
	// port = connFactory.getPort();
	// }
	//
	// try {
	// MQEnvironment.port = Integer.parseInt(port);
	// } catch (NumberFormatException ex) {
	// MQEnvironment.port = 1414;
	// }
	// }

	// private void setChannel(wmMQConnectionFactory connFactory, String
	// channel){
	//
	// if (channel == null)
	// MQEnvironment.channel = connFactory.getChannel();
	// else
	// MQEnvironment.channel = channel;
	// }

	private int setCCSID(wmMQConnectionFactory connFactory, String CCSID) {

		// Trax 1-12K2HK : Setting the CCSID on the MQEnvironment with
		// MQCCSI_Q_MGR and MQCCSI_INHERIT
		// is not allowed. Instead set the CCSID on the mq message in the Get,
		// Peek, Put,
		// RequestReply and Listener services which is already done.
		// If the CCSID provided on the connection factory is not MQCCSI_Q_MGR
		// or MQCCSI_INHERIT
		// set the MQEnvironment's CCSID with that value, otherwise set it with
		// the encoding
		// property of the jvm.
		// Trax 1-TDU5E leave the CCSID as the default. Comment out next line
		// when ready.
		// MQEnvironment.CCSID = getCharacterSetId(connFactory.getCCSID());

		int ccsid2;
		if (CCSID == null)
			ccsid2 = WmMQAdapterUtils.getCharacterSetId(connFactory.getCCSID());
		else
			ccsid2 = Integer.parseInt(CCSID);

		if (ccsid2 == MQConstants.MQCCSI_Q_MGR || ccsid2 == MQConstants.MQCCSI_INHERIT) {
			ccsid2 = WmMQAdapterUtils.getSystemDefaultCharacterSetId();
		}
		return ccsid2;

	}

	private Hashtable<String, Object> setSecurityExit(wmMQConnectionFactory connFactory, String temp,
			Hashtable<String, Object> props) throws wmMQException {

		log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection, MQEnvironment.sendExit ... ", temp);
		String _exitClass = connFactory.getSendExit();
		String _exitInit = connFactory.getSendExitInit();

		log(ARTLogger.INFO, 1003,
				"Debug_wmMQConnection.initializeConnection, MQEnvironment.sendExit_exitClass :" + _exitClass, temp);
		log(ARTLogger.INFO, 1003,
				"Debug_wmMQConnection.initializeConnection, MQEnvironment.sendExit_exitInit :" + _exitInit, temp);

		if (_exitClass != null && _exitClass.trim().length() > 0) {
			props.put(MQConstants.SEND_EXIT_PROPERTY, loadMQExit(MQSendExit.class, _exitClass, _exitInit));
		}

		_exitClass = connFactory.getRecvExit();
		_exitInit = connFactory.getRecvExitInit();

		log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection, MQEnvironment.receiveExit ... ", temp);
		log(ARTLogger.INFO, 1003,
				"Debug_wmMQConnection.initializeConnection, MQEnvironment.receiveExit_exitClass :" + _exitClass, temp);
		log(ARTLogger.INFO, 1003,
				"Debug_wmMQConnection.initializeConnection, MQEnvironment.receiveExit_exitInit :" + _exitInit, temp);
		if (_exitClass != null && _exitClass.trim().length() > 0) {
			props.put(MQConstants.RECEIVE_EXIT_PROPERTY,
					loadMQExit(MQReceiveExit.class, _exitClass, _exitInit));
		}

		_exitClass = connFactory.getSecurityExit();
		_exitInit = connFactory.getSecurityExitInit();

		log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection, MQEnvironment.securityExit ... ", temp);
		log(ARTLogger.INFO, 1003,
				"Debug_wmMQConnection.initializeConnection, MQEnvironment.securityExit_exitClass :" + _exitClass, temp);
		log(ARTLogger.INFO, 1003,
				"Debug_wmMQConnection.initializeConnection, MQEnvironment.securityExit_exitInit :" + _exitInit, temp);
		if (_exitClass != null && _exitClass.trim().length() > 0) {
			props.put(MQConstants.SECURITY_EXIT_PROPERTY,
					loadMQExit(MQSecurityExit.class, _exitClass, _exitInit));

		}
		return props;
	}

	// private void setUserCredential(wmMQConnectionFactory connFactory){
	//
	// String _userid = connFactory.getUserId();
	//
	// if (_userid != null && _userid.length() > 0)
	// {
	// MQEnvironment.userID = _userid;
	// String _password = connFactory.getPassword();
	// if (_password != null && _password.length() > 0)
	// {
	// MQEnvironment.password = _password;
	// }
	// }
	// else
	// {
	// MQEnvironment.userID = "";
	// }
	// }

	protected String getPackageName() {
		NSNode node = Namespace.current().getNode(getFactory().getNodeName());
		if(node == null)
			return null;
		return node.getPackage().getName();
	}
	
	private MQQueueManager createSSLQMgrWithKeyAlias(wmMQConnectionFactory connFactory, String _qMgrName, String temp, URL ccdtUrl, Hashtable<String, Object> props)
			throws MQException, AdapterException {
		String qmgrName = connFactory.getQueueManagerName();
		props.put(MQConstants.SSL_CIPHER_SUITE_PROPERTY, wmMQConnectionFactory.suite2Spec(connFactory.getSslCipherSpec()));

		wmMQKeyStoreManager keyStoreLoader = new wmMQKeyStoreManager();
		keyStoreLoader.setKeyStoreHandle(connFactory.getSslKeyStoreAlias());
		keyStoreLoader.setTrustStoreHandle(connFactory.getSslTrustStoreAlias());
		
		MQQueueManager qmgr = null;
		String pkgName = null;
		
		if (Server.inILive()) {
			pkgName = getPackageName();
			log(ARTLogger.INFO, 1005, "Package name of cert store: " + pkgName, temp);
		}
		
		try {
			// wmio ssl
			KeyStore ks;
			KeyStore ts;
			
			if(wmMQAdapterConstant.IBMMQ_IN_ILIVE || wmMQAdapterConstant.IBMMQ_IN_ORIGIN){
				ks = keyStoreLoader.getKeyStore(pkgName, connFactory);
				ts = keyStoreLoader.getTrustStore(pkgName, connFactory);
				
			} else {
				keyStoreLoader.init();			
			
				ks = keyStoreLoader.getKeyStore();
				ts  = keyStoreLoader.getTrustStore();
			}
			
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());

			trustManagerFactory.init(ts);
			if(wmMQAdapterConstant.IBMMQ_IN_ILIVE || wmMQAdapterConstant.IBMMQ_IN_ORIGIN) {
				CertStoreHandler keyStore = CertStoreManager.getKeyStoreHandler(pkgName, connFactory.getSslKeyStoreAlias());
				keyManagerFactory.init(ks, keyStore.getStorePassword().toCharArray());
			} else
				keyManagerFactory.init(ks, keyStoreLoader.getKeyStorePwd().toCharArray());
						
			SSLContext sslContext = SSLContext.getInstance("SSL");

			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

			// Get an SSLSocketFactory to pass to WMQ
			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

			// Set the socket factory in our WMQ parameters
			props.put(MQConstants.SSL_SOCKET_FACTORY_PROPERTY, sslSocketFactory);
			
			// Connect to WMQ
			if(ccdtUrl!=null){
				qmgr = new MQQueueManager(qmgrName, props, ccdtUrl);
			}else{
				qmgr = new MQQueueManager(qmgrName, props);
			}
			// Query the description
			String desc = qmgr.getDescription();

			// Output the description
		}
		catch (KeyStoreException e) {
			log(ARTLogger.ERROR, 5013, null, null);
			throw wmMQAdapter.getInstance().createAdapterConnectionException(1055,
					new String[] { "Initializing Connection", e.getMessage() }, e);
		} catch (NoSuchAlgorithmException e) {
			log(ARTLogger.ERROR, 1003, e.getLocalizedMessage(), null);
			throw wmMQAdapter.getInstance().createAdapterConnectionException(1055,
					new String[] { "Initializing Connection", e.getMessage() }, e);
		} catch (UnrecoverableKeyException e) {
			log(ARTLogger.ERROR, 1003, e.getLocalizedMessage(), e.getMessage());
			throw wmMQAdapter.getInstance().createAdapterConnectionException(1055,
					new String[] { "Initializing Connection", e.getMessage() }, e);
		} catch (KeyManagementException e) {
			log(ARTLogger.ERROR, 1003, e.getLocalizedMessage(), e.getMessage());
			throw wmMQAdapter.getInstance().createAdapterConnectionException(1055,
					new String[] { "Initializing Connection", e.getMessage() }, e);
		} catch (ServerException e) {
			log(ARTLogger.ERROR, 1003, e.getLocalizedMessage(), e.getMessage());
			throw wmMQAdapter.getInstance().createAdapterConnectionException(1055,
					new String[] { "Initializing Connection", e.getMessage() }, e);
		} catch (IOException e) {
			log(ARTLogger.ERROR, 1003, e.getLocalizedMessage(), e.getMessage());
			throw wmMQAdapter.getInstance().createAdapterConnectionException(1055,
					new String[] { "Initializing Connection", e.getMessage() }, e);
		} catch (com.wm.pkg.art.error.ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return qmgr;
	}

	private MQQueueManager createSSLQMgr(wmMQConnectionFactory connFactory, String _qMgrName, String temp,
			Hashtable<String, Object> props, URL ccdtUrl) throws MQException {

		// Temp storage for SSL-related system properties
		String originalKeystore = "";
		String originalKeystorePassword = "";
		String originalTrustStore = "";
		String originalTrustStorePassword = "";
		String originalCipherSuite = "";

		log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection, Using SSL ... ", temp);
		// SSL Specific parameters
		originalKeystore = System.getProperty("javax.net.ssl.keyStore");
		if (originalKeystore == null)
			originalKeystore = "";
		log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection, OriginalKeyStore is: " + originalKeystore,
				temp);

		originalKeystorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
		if (originalKeystorePassword == null)
			originalKeystorePassword = "";
		log(ARTLogger.INFO, 1003,
				"Debug_wmMQConnection.initializeConnection, OriginalKeyStore password is: " + originalKeystorePassword,
				temp);

		originalTrustStore = System.getProperty("javax.net.ssl.trustStore");
		if (originalTrustStore == null)
			originalTrustStore = "";
		log(ARTLogger.INFO, 1003,
				"Debug_wmMQConnection.initializeConnection, originalTrustStore is: " + originalTrustStore, temp);

		originalTrustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
		if (originalTrustStorePassword == null)
			originalTrustStorePassword = "";
		log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection, originalTrustStore password is: "
				, temp);

		originalCipherSuite = (String) props.get(MQConstants.SSL_CIPHER_SUITE_PROPERTY);
		if (originalCipherSuite == null)
			originalCipherSuite = "";
		log(ARTLogger.INFO, 1003,
				"Debug_wmMQConnection.initializeConnection, originalCipherSuite is: " + originalCipherSuite, temp);

		System.setProperty("javax.net.ssl.keyStore", connFactory.getSslKeyStore());
		System.setProperty("javax.net.ssl.keyStorePassword", connFactory.getSslKeyStorePassword());
		System.setProperty("javax.net.ssl.trustStore", connFactory.getSslKeyStore());
		System.setProperty("javax.net.ssl.trustStorePassword", connFactory.getSslKeyStorePassword());

		// MQEnvironment.sslCertStores=null;

		props.put(MQConstants.SSL_CIPHER_SUITE_PROPERTY, wmMQConnectionFactory.suite2Spec(connFactory.getSslCipherSpec()));
		log(ARTLogger.INFO, 1003, "Debug_wmMQConnection.initializeConnection, MQEnvironment.sslCipherSuite created... ",
				temp);

		try {
			log(ARTLogger.INFO, 1003,
					"Debug_wmMQConnection.initializeConnection, Creating MQQueueManager using SSL ... ", temp);
			if(ccdtUrl!=null){
				_qMgr = new MQQueueManager(_qMgrName, props, ccdtUrl);
			}else{
				_qMgr = new MQQueueManager(_qMgrName, props);
			}

			String sslValues = "javax.net.ssl.keyStore = " + System.getProperty("javax.net.ssl.keyStore") + ", "
					+ "javax.net.ssl.keyStorePassword = " + ", "
					+ "javax.net.ssl.trustStore = " + System.getProperty("javax.net.ssl.trustStore") + ", "
					+ "javax.net.ssl.trustStorePassword = ";

			log(ARTLogger.INFO, 1003,
					"Debug_wmMQConnection.initializeConnection, MQQueueManager created using SSL values = " + sslValues,
					temp);
		} catch (MQException exc) {
			throw exc;
		} finally {
			// Reset SSL-related system properties
			log(ARTLogger.INFO, 1003,
					"Debug_wmMQConnection.initializeConnection, Resetting SSL-related system properties... ", temp);
			System.setProperty("javax.net.ssl.keyStore", originalKeystore);
			System.setProperty("javax.net.ssl.keyStorePassword", originalKeystorePassword);
			// System.setProperty("javax.net.ssl.trustStore",
			// originalTrustStore);
			// System.setProperty("javax.net.ssl.trustStorePassword",
			// originalTrustStorePassword);
			props.put(MQConstants.SSL_CIPHER_SUITE_PROPERTY, originalCipherSuite);

			String sslValues = "javax.net.ssl.keyStore = " + System.getProperty("javax.net.ssl.keyStore") + ", "
					+ "javax.net.ssl.keyStorePassword = " + ", "
					+ "javax.net.ssl.trustStore = " + System.getProperty("javax.net.ssl.trustStore") + ", "
					+ "javax.net.ssl.trustStorePassword = ";

			log(ARTLogger.INFO, 1003,
					"Debug_wmMQConnection.initializeConnection, Reset complete for SSL-related system properties = "
							+ sslValues,
					temp);
		}

		return _qMgr;

	}
	
	protected void verifyConnection() throws AdapterConnectionException {
		try {
			_qMgr.getDescription();
		} catch (MQException mqe) {
			throw wmMQAdapter.getInstance().createAdapterConnectionException(1055,
					new String[] { "HeartBeat Connectivity Check", mqe.getMessage() }, mqe);
		}
	}

}

class ResultLocationDesc {
	String arrayType = null;
	int intAttrPos = -1;
	int charAttrPos = -1;
	int charOffset = -1;
	int length = -1;

	ResultLocationDesc(String arrayType, int intAttrPos, int charAttrPos, int charOffset, int length) {

		this.arrayType = arrayType;
		this.intAttrPos = intAttrPos;
		this.charAttrPos = charAttrPos;
		this.charOffset = charOffset;
		this.length = length;
	}
}