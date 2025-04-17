/*
 * wmMQAsyncListener.java
 *
 * Copyright 2003 webMethods, Inc.
 * ALL RIGHTS RESERVED
 *
 * UNPUBLISHED -- Rights reserved under the copyright laws of the United States.
 * Use of a copyright notice is precautionary only and does not imply
 * publication or disclosure.
 *
 * THIS SOURCE CODEqm IS THE CONFIDENTIAL AND PROPRIETARY INFORMATION OF
 * WEBMETHODS, INC.  ANY REPRODUCTION, MODIFICATION, DISTRIBUTION,
 * OR DISCLOSURE IN ANY FORM, IN WHOLE, OR IN PART, IS STRICTLY PROHIBITED
 * WITHOUT THE PRIOR EXPRESS WRITTEN PERMISSION OF WEBMETHODS, INC.
 */

// Note: this class is added at Phase 5 to support listener feature
package com.wm.adapter.wmmqadapter.connection;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.resource.ResourceException;


import com.ibm.mq.MQException;

import com.wm.adapter.wmmqadapter.WmMQAdapterUtils;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adk.cci.record.WmRecord;
import com.wm.adk.cci.record.WmRecordFactory;

import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;
import com.wm.adk.metadata.WmDescriptor;
import com.wm.adk.notification.NotificationResults;

import com.wm.adk.notification.WmNotification;
import com.wm.util.Values;

/*
 * This class represents WmSampleAdapter's listener connection type to Sample Server.
 * This class contains the property of the time out while wait to receive any event
 * notification from Sample Server.
 */
public class wmMQListener extends wmMQListenerAbstract {

	// WaitInterval property
	private int _waitInterval = 60000;

	// WaitInterval property
	private boolean _sharedMode = true;

	// ConvertDataOption property
	private boolean _convertDataOption = true;

	// This property allows the user to configure the listener so as to pull
	// messages only if
	// all the messages pertaining to a particular group are put in.
	private boolean _useGrouping = false;

	// For Additional MQGMO options
	// private String _mQGMOOptions= "";

	//
	private boolean _throwExceptionOnFailure = true;

	//
	private String _deadLetterQueue = "";

	//
	// private String _deadLetterQueueName = "deadLetterQueueName";

	//
	// private String[] _deadLetterQueueType = {"string"};

	//
	private String _deadLetterQueueManager = "";

	private String _deadLetterMessageHeaders = wmMQAdapter
			.getDeadLetterMessageHeaders();

	//
	// private String _deadLetterQueueManagerName =
	// "deadLetterQueueManagerName";

	//
	// private String[] _deadLetterQueueManagerType = {"string"};

	//
	private int _backoutThreshhold = 1;

	private boolean _handleBackoutRequeue = false;

	// Selection criteria for filtering messages
	private String _filterMsgId = "";
	private String _filterCorrelId = "";
	private String _filterGroupId = "";
	private String _filterSequenceNumber = "";
	private String _filterOffset = "";

	protected wmMQConnection _conn = null;

	private boolean _nonFatalExceptionOccurredInPreviousTry = false;
	/*
	 * Sets the deadLetterQueue property.
	 * 
	 * The dlq parameter is the deadLetterQueue property.
	 */
	public void setDeadLetterQueue(String dlq) {
		this._deadLetterQueue = dlq;
	}

	/*
	 * Gets the deadLetterQueue.
	 */
	public String getDeadLetterQueue() {
		return _deadLetterQueue;
	}

	/*
	 * Sets the deadLetterQueue property.
	 * 
	 * The dlqmgr parameter is the deadLetterQueueManager property.
	 */
	public void setDeadLetterQueueManager(String dlqmgr) {
		this._deadLetterQueueManager = dlqmgr;
	}

	/*
	 * Gets the deadLetterQueue.
	 */
	public String getDeadLetterQueueManager() {
		return _deadLetterQueueManager;
	}

	/*
	 * Sets the _deadLetterMessageHeaders property.
	 * 
	 * The deadLetterMessageHeaders parameter is the _deadLetterMessageHeaders
	 * property.
	 */
	public void setDeadLetterMessageHeaders(String deadLetterMessageHeaders) {
		this._deadLetterMessageHeaders = deadLetterMessageHeaders;
	}

	/*
	 * Gets the _deadLetterMessageHeaders.
	 */
	public String getDeadLetterMessageHeaders() {
		return _deadLetterMessageHeaders;
	}

	/*
	 * Sets the backoutThreshhold property.
	 * 
	 * The count parameter is the backoutThreshhold property.
	 */
	public void setBackoutThreshhold(int count) {
		this._backoutThreshhold = count;
	}

	/*
	 * Gets the backoutThreshhold property.
	 */
	public int getBackoutThreshhold() {
		return _backoutThreshhold;
	}

	public void setHandleBackoutRequeue(boolean handleBackoutRequeue) {
		this._handleBackoutRequeue = handleBackoutRequeue;
	}

	public boolean getHandleBackoutRequeue() {
		return _handleBackoutRequeue;
	}

	/*
	 * Sets the waitInterval property
	 * 
	 * The interval parameter is the waitInterval property.
	 */
	public void setWaitInterval(int interval) {
		this._waitInterval = interval;
	}

	/*
	 * Gets the waitInterval property.
	 */
	public int getWaitInterval() {
		return _waitInterval;
	}

	/*
	 * Sets the sharedMode property
	 * 
	 * The shared parameter is the sharedMode property.
	 */
	public void setSharedMode(boolean shared) {
		this._sharedMode = shared;
	}

	/*
	 * Gets the convertDataOption property.
	 */
	public boolean getConvertDataOption() {
		return _convertDataOption;
	}

	/*
	 * Sets the convertDataOption property
	 * 
	 * The convert parameter is the convertDataOption property.
	 */
	public void setConvertDataOption(boolean convert) {
		this._convertDataOption = convert;
	}

	/**
	 * Gets the Use Grouping property
	 * 
	 * @return boolean
	 */
	public boolean getUseGrouping() {
		return _useGrouping;
	}

	/**
	 * set the use grouping property
	 * 
	 * @param use
	 */
	public void setUseGrouping(boolean use) {
		this._useGrouping = use;
	}

	/*
	 * Gets the sharedMode property.
	 */
	public boolean getSharedMode() {
		return _sharedMode;
	}

	/*
	 * Sets the MsgId selection criteria
	 * 
	 * The shared parameter is the sharedMode property.
	 */
	public void setFilterMsgId(String msgid) {
		_filterMsgId = msgid;
	}

	/*
	 * Gets the MsgId selection criteria.
	 */
	public String getFilterMsgId() {
		return _filterMsgId;
	}

	/*
	 * Sets the CorrelId selection criteria
	 * 
	 * The shared parameter is the sharedMode property.
	 */
	public void setFilterCorrelId(String correlid) {
		_filterCorrelId = correlid;
	}

	/*
	 * Gets the CorrelId selection criteria.
	 */
	public String getFilterCorrelId() {
		return _filterCorrelId;
	}

	/*
	 * Sets the GroupId selection criteria
	 * 
	 * The shared parameter is the sharedMode property.
	 */
	public void setFilterGroupId(String groupid) {
		_filterGroupId = groupid;
	}

	/*
	 * Gets the MsgId selection criteria.
	 */
	public String getFilterGroupId() {
		return _filterGroupId;
	}

	/*
	 * Sets the SequenceNumber selection criteria
	 * 
	 * The shared parameter is the sharedMode property.
	 */
	public void setFilterSequenceNumber(String sequencenumber) {
		_filterSequenceNumber = sequencenumber;
	}

	/*
	 * Gets the MsgId selection criteria.
	 */
	public String getFilterSequenceNumber() {
		return _filterSequenceNumber;
	}

	/*
	 * Sets the Offset selection criteria
	 * 
	 * The shared parameter is the sharedMode property.
	 */
	public void setFilterOffset(String offset) {
		_filterOffset = offset;
	}

	/*
	 * Gets the MsgId selection criteria.
	 */
	public String getFilterOffset() {
		return _filterOffset;
	}

	/*
	 * Default constructor
	 */
	public wmMQListener() {
		super();
	}

	/*
	 * Start up and initialize the listener
	 */
	public void listenerStartup() throws ResourceException {
		log(ARTLogger.INFO, 1001, "listenerStartup", "");

		_conn = (wmMQConnection) retrieveConnection();
		if (_conn instanceof wmMQTransactionalConnection)
			((wmMQTransactionalConnection) _conn).setListenerConnection(true);

		log(ARTLogger.INFO, 1002, "listenerStartup", "");
	}

	// should allow to throw exception??
	/*
	 * Shut down the listener
	 */
	public void listenerShutdown() {
		log(ARTLogger.INFO, 1001, "listenerShutdown", "");
		try {
			if (_conn != null)
				_conn.destroyConnection();
		} catch (AdapterException ae) {
			
		}
		log(ARTLogger.INFO, 1002, "listenerShutdown", "");
	}

	/*
	 * This method populates the metadata object describing this listener in the
	 * specified locale.
	 * 
	 * d is the metadata object describing this adapter service. l is the Locale
	 * in which the locale-specific metadata should be populated.
	 * AdapterException is thrown if an error is encountered while populating
	 * the metadata.
	 */
	public void fillWmDescriptor(WmDescriptor d, Locale l)
			throws AdapterException {
		log(ARTLogger.INFO, 1001, "fillWmDescriptor", "");
		d.createGroup(wmMQAdapterConstant.LISTEN_SERVICE, new String[] {
				wmMQAdapterConstant.WAIT_INTERVAL,
				wmMQAdapterConstant.FILTER_MESSAGE_ID,
				wmMQAdapterConstant.FILTER_CORRELATION_ID,
				wmMQAdapterConstant.FILTER_GROUP_ID,
				wmMQAdapterConstant.FILTER_SEQUENCENUMBER,
				wmMQAdapterConstant.FILTER_OFFSET,
				wmMQAdapterConstant.HANDLE_BACKOUT_REQUEUE,
				wmMQAdapterConstant.DEAD_LETTER_QUEUE,
				wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER,
				wmMQAdapterConstant.BACKOUT_THRESHHOLD,
				wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS,
				wmMQAdapterConstant.SHARED_MODE,
				wmMQAdapterConstant.CONVERT_DATA_OPTION,
				wmMQAdapterConstant.USE_GROUPING });
		
		d.setValidValues(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS,
				wmMQAdapterConstant.deadLetterMessageHeaderOptions);
		d.setValidValues(wmMQAdapterConstant.SHARED_MODE, new String[] {
				"true", "false" });
		d.setValidValues(wmMQAdapterConstant.CONVERT_DATA_OPTION, new String[] {
				"true", "false" });
		d.setValidValues(wmMQAdapterConstant.USE_GROUPING, new String[] {
				"false", "true" });
		d.setValidValues(wmMQAdapterConstant.HANDLE_BACKOUT_REQUEUE,
				new String[] { "false", "true" });

		d.setDescriptions(wmMQAdapter.getInstance()
				.getAdapterResourceBundleManager(), l);
		log(ARTLogger.INFO, 1002, "fillWmDescriptor", "");
	}

	@SuppressWarnings("squid:S1541")
	public Object waitForData() throws ResourceException {
		log(ARTLogger.INFO, 1001, "waitForData", "");

		// Trax 1-UEK9U, 1-UE4JO and Feature Request 1-UE4TG.
		// WmART allows the user to enable the listener, even if the Listener
		// has zero enabled notifications associated with it. 
		// This means that a message can be received that
		// is not supported by any of the enabled notifications registered to
		// this listener.
		// This condition creates the problem that the Listener will remove
		// messages that
		// are never processed.
		//
		// As a work-around, this code segment locates the last enabled
		// notification
		// registered to this Listener. The Listener will not retrieve any
		// messages until
		// at least 1 enabled notification exists. If one is found, this code
		// segmentcaches
		// a reference to that notification in the WmRecord. See the supports()
		// method in
		// wmMQAsyncNotification.java for more information on how this
		// work-around works.
		WmNotification last = null;
		List notifications = getRegisteredNotifications();

		int numberOfNotifications = notifications.size();
		while (numberOfNotifications > 0) {
			// Get last notification in list
			last = (WmNotification) notifications
					.get(numberOfNotifications - 1);
			if (((WmNotification) last).enabled())
				break;
			numberOfNotifications--;
		}

		if (numberOfNotifications == 0) {
			log(ARTLogger.INFO, 4051, this._listenerNodeName.getFullName(), "");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException ie) {
				// who cares?
			}
			return null;
		}

		long time = -System.currentTimeMillis();
		wmMQMessage msg = new wmMQMessage();

		// Pre-set the messageSequenceNumber to 0 so that the code can
		// determine if the user wants to match on sequence number.
		
		// Pre-set the offset to -1 so that the code can
		// determine if the user wants to match on offset = 0.
		try {
			msg.getMQMessage().messageSequenceNumber = 0;
			msg.getMQMessage().offset = -1;

			// Trax log 1-R60VH. Allow user to override CCSID
			msg.getMQMessage().characterSet = WmMQAdapterUtils
					.getCharacterSetId(((wmMQConnectionFactory) _conn
							.getFactory()).getCCSID());

			// Allow user to override Encoding
			msg.getMQMessage().encoding = Integer
					.parseInt(((wmMQConnectionFactory) _conn.getFactory())
							.getEncoding().substring(0, 5));
		} catch (wmMQException wmqe) {
		}

		Values header = new Values();
		String[] filternames = new String[] {
				wmMQAdapterConstant.MQMD_MESSAGE_ID,
				wmMQAdapterConstant.MQMD_CORRELATION_ID,
				wmMQAdapterConstant.MQMD_GROUP_ID,
				wmMQAdapterConstant.MQMD_MESSAGE_SEQUENCE_NUMBER,
				wmMQAdapterConstant.MQMD_OFFSET };

		boolean[] filteruses = new boolean[] { false, false, false, false,
				false };
		if (!_filterMsgId.trim().equals("")) {
			header.put(wmMQAdapterConstant.MQMD_MESSAGE_ID, _filterMsgId);
			filteruses[0] = true;
		}
		if (!_filterCorrelId.trim().equals("")) {
			header.put(wmMQAdapterConstant.MQMD_CORRELATION_ID, _filterCorrelId);
			filteruses[1] = true;
		}
		if (!_filterGroupId.trim().equals("")) {
			header.put(wmMQAdapterConstant.MQMD_GROUP_ID, _filterGroupId);
			filteruses[2] = true;
		}
		if (!_filterSequenceNumber.trim().equals("")) {
			header.put(wmMQAdapterConstant.MQMD_MESSAGE_SEQUENCE_NUMBER,
					_filterSequenceNumber);
			filteruses[3] = true;
		}
		if (!_filterOffset.trim().equals("")) {
			header.put(wmMQAdapterConstant.MQMD_OFFSET, _filterOffset);
			filteruses[4] = true;
		}

		msg.copyMQMDFieldsToMsg(header, filternames, filteruses);

		WmRecord output = WmRecordFactory.getFactory().createWmRecord("Output");

		int reasonCode = 0;
		try {
			log(ARTLogger.INFO, 1003, "Listening for message",
					"queue=" + _conn.getResolvedQueueName(true));
			if (_useGrouping) {
				reasonCode = _conn.listenerGet(msg, getSharedMode(),
						_waitInterval, getConvertDataOption());
			} else {
				reasonCode = _conn.get(msg, getSharedMode(), _waitInterval,
				getConvertDataOption());
//				reasonCode = _conn.get(msg, getSharedMode(), _waitInterval,
//						getConvertDataOption(), null);
			}
			log(ARTLogger.INFO, 1003, "Not Listening for message", "queue="
					+ _conn.getResolvedQueueName(true));
		} catch (wmMQException mqe) {
			// Trax 1-1031MB.
			// This code is added to avoid the logs being flooded with the error messages
			// when a non-fatal (recoverable) exception occurs. Instead an error message
			// is logged once and subsequent exceptions are logged as info  messages.
			// The listener sleeps for some time before it tries to get the data again
			// from the queue.
			// For instance we could have made the MQRC_GET_INIHIBITED as a
			// fatal exception, which would have
			// disabled the listener after certain number of tries. But this
			// solution was droped
			// as the user when sets the queue back to get allowed has to
			// restart all the mq listeners,
			// which is not a reasonable option; as usaully the mq queues are
			// handled by
			// mq administrator who might be not aware of the listeners polling
			// on that queue.
			MQException realmqe = (MQException) mqe.getLinkedException();
			if (!WmMQAdapterUtils.fatalException(realmqe.reasonCode)) {
				// If the previous mq get failed with a no-fatal(recoverable)
				// exception
				if (_nonFatalExceptionOccurredInPreviousTry) {
					// In the subsequent tries, log the no-fatal(recoverable)
					// exception as an info message
					log(ARTLogger.INFO,
							4071,
							new String[] {
									this._listenerNodeName.getFullName(),
									_conn.getResolvedQueueName(true),
									_conn.getResolvedQueueManagerName(),
									"" + realmqe.completionCode,
									"" + realmqe.reasonCode });
				} else {
					_nonFatalExceptionOccurredInPreviousTry = true;

					// For the first time, log the no-fatal(recoverable)
					// exception as an error
					log(ARTLogger.ERROR, 1055, "Listening for messsage",
							mqe.getMessage());
				}

				// Make the thread to wait for certain amount of time before
				// retrying the mqget operation
				try {
					Thread.sleep(getListenerNode().getSecondsBetweenRetries()
							.intValue() * 1000);
				} catch (InterruptedException ie) {
					// who cares?
				}
			} else {
				_nonFatalExceptionOccurredInPreviousTry = false;

				log(ARTLogger.ERROR, 1055, "Listening for messsage",
						mqe.getMessage());

				throw wmMQAdapter.getInstance()
						.createAdapterConnectionException(1055,
								new String[] { "Listen", mqe.getMessage() },
								mqe);
			}
			return null;
		}

		// Trax 1-1031MB.
		_nonFatalExceptionOccurredInPreviousTry = false;

		/**
		 * Move msg to Backout Requeue Queue based on Backout Threshhold and
		 * Backout Requeue set at MQ server queue
		 */
		if (getHandleBackoutRequeue()) {
			try {
				int backoutcount = msg.getMQMessage().backoutCount;
				String[] queueProperty = { "BackoutThreshold",
						"BackoutRequeueQName" };
				String backOutRequeue = null;
				int backOutThreshold = 1;

				/**
				 * Assuming BackoutRequeue is not of remote Queue
				 */
				Map queueProperties = _conn.inquireQueueProperties(_conn.getResolvedQueueName(true),
						queueProperty,false);
				for (Iterator it = queueProperties.entrySet().iterator(); it
						.hasNext();) {
					Map.Entry entry = (Map.Entry) it.next();
					if (entry.getValue() != null) {
						if (entry.getValue() instanceof Integer) {
							backOutThreshold = ((Integer) entry.getValue())
									.intValue();
						} else {
							backOutRequeue = entry.getValue().toString();
						}
					}
				}
				if (backoutcount >= backOutThreshold) {
					
					_conn.moveMsgToDLQ(msg, backOutRequeue,  _conn.getResolvedQueueManagerName(), "Get",
							true, getDeadLetterMessageHeaders());
					return null;
				}
			} catch (wmMQException wmqe) {
				WmMQAdapterUtils
						.log(ARTLogger.ERROR,
								1055,
								"Error while getting Backout requeue and Backout threshold",
								wmqe.getMessage());

				throw wmMQAdapter.getInstance()
						.createAdapterException(
								1055,
								new String[] { "InquireQueueManager",
										wmqe.getMessage() }, wmqe);
			}
		}

		// Move msg to Dead letter Queue
		// !getDeadLetterQueue().isEmpty()
		else if ((getDeadLetterQueue() != null)
				&& (getDeadLetterQueue().length() > 0)) {
			try {
				int backoutcount = msg.getMQMessage().backoutCount;
				if (backoutcount >= getBackoutThreshhold()) {
					_conn.moveMsgToDLQ(msg, getDeadLetterQueue(),
							getDeadLetterQueueManager(), "Get", true,
							getDeadLetterMessageHeaders());
					return null;
				}
			} catch (wmMQException wmqe) {
				WmMQAdapterUtils.log(ARTLogger.ERROR, 1055, "Error while putting message to Dead Letter Queue",
						wmqe.getMessage());
				//IMQ-1219
				throw wmMQAdapter.getInstance().createAdapterException(1055,
						new String[] { "Error while putting message to Dead Letter Queue" }, wmqe);

			}
		}

		output.put("conditionCode", "" + MQException.MQCC_OK);
		output.put("reasonCode", "" + reasonCode);

		if (reasonCode == MQException.MQRC_NO_MSG_AVAILABLE) {
			output.put("conditionCode", "" + MQException.MQCC_FAILED);
			log(ARTLogger.INFO, 1002, "waitForData", "");
			// return output;
			return null;
		}

		// If the 'get' completes with a warning, pass the reason code to the
		// caller
		if (reasonCode > 0) {
			log(ARTLogger.WARNING, 1020, "" + reasonCode, ""
					+ MQException.MQCC_WARNING);
			output.put("conditionCode", "" + MQException.MQCC_WARNING);
		}

		output.put("wmmqmessage", msg);
		output.put(wmMQAdapterConstant.QUEUE_MANAGER_NAME,
				_conn.getResolvedQueueManagerName());
		output.put(wmMQAdapterConstant.QUEUE_NAME,
				_conn.getResolvedQueueName(true));

		output.put("lastNotification", last); // pass reference on to
												// notifications

		log(ARTLogger.INFO, 1003, "waitForData",
				"elapsed time=" + (System.currentTimeMillis() + time));
		log(ARTLogger.INFO, 1002, "waitForData", "");
		return output;
	}

	/**
	 * Post-processing of the notification results.
	 */
	public void processNotificationResults(NotificationResults results)
			throws ResourceException {
		log(ARTLogger.INFO, 1001, "processNotificationResults", "");

		if (results == null) {
			// Trax 1-UEK9U - The only scenario under which this code executes
			// is if a message is received, but zero
			// enabled notifications exist. In this case, rollback the message
			// to the queue.
			//
			log(ARTLogger.INFO, 1003, "processNotificationResults",
					"results is null");
			if (_conn instanceof wmMQTransactionalConnection) {
				((wmMQTransactionalConnection) _conn).rollback();
			}
		} else if (results.hadError()) {
			log(ARTLogger.INFO, 1003, "processNotificationResults",
					"results.hadError() returned true");
			Object errorinfo = results.getErrorInfo();
			if (errorinfo != null)
				log(ARTLogger.INFO, 1003, "processNotificationResults",
						"errorinfo=" + errorinfo.toString());
		}
		log(ARTLogger.INFO, 1002, "processNotificationResults", "");
	}

	protected void log(int level, int minor, String arg0, String arg1) {
		String[] args = new String[2];
		args[0] = arg0;
		args[1] = arg1;

		log(level, minor, args);
	}

	protected void log(int level, int minor, String[] args) {
		ARTLogger logger = ((wmMQAdapter) wmMQAdapter.getInstance())
				.getLogger();
		if (logger == null) {
			System.out.println("Logger is null");
			return;
		}

		// Trax 1-WVILA. Allow user to override the logging level of adapter
		// messages.
		if (wmMQAdapter.getLogLevelOverrides().containsKey("" + minor))
			level = Integer.parseInt((String) wmMQAdapter
					.getLogLevelOverrides().get("" + minor)) - ARTLogger.DEBUG;

		// Trax log 1-S4OHA - move adapter logging to DEBUG PLUS
		logger.logDebugPlus(level, minor, args);
	}

}