/*
 * Put.java
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

package com.wm.adapter.wmmqadapter.service;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.MQConstants;
import com.wm.adapter.wmmqadapter.connection.wmMQConnection;
import com.wm.adapter.wmmqadapter.connection.wmMQConnectionFactory;
import com.wm.adapter.wmmqadapter.connection.wmMQException;
import com.wm.adapter.wmmqadapter.connection.wmMQMessage;
import com.wm.adapter.wmmqadapter.connection.wmMQOverrideConnection;
import com.wm.adapter.wmmqadapter.connection.wmMQTransactionalConnection;
import com.wm.adapter.wmmqadapter.WmMQAdapterUtils;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adk.cci.record.WmRecord;
import com.wm.adk.cci.record.WmRecordFactory;
import com.wm.adk.connection.WmManagedConnection;
import com.wm.adk.error.AdapterConnectionException;
import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;
import com.wm.adk.metadata.WmTemplateDescriptor;
import com.wm.data.IDataCursor;
import com.wm.util.Values;

import javax.resource.ResourceException;

import java.util.HashMap;
import java.util.Locale;

/*
 * This class is an implementation of the lookup service template
 * for the wmMQAdapter. The lookup
 * takes a key as input, and returns the column values of the record.
 * An error is issued if there is no record for that key.
 *
 * This template demonstrates the use of combo boxes, allowing
 * a user to select catalogs, schemas, and tables.
 */
public class Put extends wmMQBaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2015135462856372337L;

	/*
	 * Constructor.
	 */
	public Put() {
		_serviceName = wmMQAdapterConstant.PUT_SERVICE;
		_overrideConnection = new wmMQOverrideConnection(_serviceName);
	}

	public String getOverrideUseLocalQueueManager() {
		return _overrideConnection.getOverrideUseLocalQueueManager();
	}

	public void setOverrideUseLocalQueueManager(String overrideUseLocalQueueManager) {
		_overrideConnection.setOverrideUseLocalQueueManager(overrideUseLocalQueueManager);
	}

	/*
	 * This method populates the metadata object describing this service
	 * template in the specified locale. This method overrides the
	 * fillWmTemplateDescriptor() method in the super class.
	 *
	 * The d parameter is the metadata object describing this adapter service.
	 * The l parameter is the Locale in which the locale-specific metadata
	 * should be populated. AdapterException is thrown if an error is
	 * encountered while populating the metadata.
	 */
	public void fillWmTemplateDescriptor(WmTemplateDescriptor d, Locale l) throws AdapterException {
		log(ARTLogger.INFO, 1001, "Put.fillWmTemplateDescriptor", "");
		overrideServiceGroupFieldNames(_serviceGroupFieldNames);
		overrideWhereToPutJMSProperties(WmTemplateDescriptor.INPUT_FIELD_NAMES);
		overrideWhereToPutMsgBody(WmTemplateDescriptor.INPUT_FIELD_NAMES);

		super.fillWmTemplateDescriptor(d, l);

		d.setHidden(wmMQAdapterConstant.WAIT_INTERVAL, true);
	}

	/*
	 * Puts an MQMessage onto an wmMQ Queue. connection is the connection
	 * handle. input is the input record of the adapter service.
	 */
	@SuppressWarnings("squid:S1541")
	public WmRecord execute(WmManagedConnection connection, WmRecord input) throws ResourceException {
		log(ARTLogger.INFO, 1001, "put:execute", "");
		IDataCursor idc = null;
		try {
			idc = input.getCursor();
			if ((idc != null) && (idc.first())) {
				do {
					String key = idc.getKey();
					Object o = idc.getValue();
					if (o == null)
						log(ARTLogger.INFO, 1003, "key=" + key + " is null", "");
					else
						log(ARTLogger.INFO, 1003, "key=" + key, ", value=" + o.toString());
				} while (idc.next());
			} else
				log(ARTLogger.INFO, 1003, "put:execute", "idc is null, input=" + input.toString());
		} catch (Exception ex) {
			// ex.printStackTrace();
		} finally {
			if (idc != null)
				idc.destroy();
		}

		boolean putFailed = false;

		// The connection handle.
		wmMQConnection conn = null;
		if (connection instanceof wmMQTransactionalConnection) {
			conn = (wmMQTransactionalConnection) connection;
		} else {
			conn = (wmMQConnection) connection;
		}

		String dlq = (String) input.get(wmMQAdapterConstant.DEAD_LETTER_QUEUE_NAME);
		if (dlq == null)
			dlq = getDeadLetterQueue();

		String dlqQmgr = (String) input.get(wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_NAME);
		if ((dlqQmgr == null) || (dlqQmgr.trim().equals("")))
			dlqQmgr = getDeadLetterQueueManager();

		// The output record to return.
		WmRecord output = WmRecordFactory.getFactory().createWmRecord("Output");

		try {

			String askedfor = _overrideConnection.getPropertyFromInput(input, wmMQOverrideConnection.QUEUE_NAME_LABEL);

			String queueManagerName = _overrideConnection.getPropertyFromInput(input,
					wmMQOverrideConnection.QUEUE_MANAGER_NAME_LABEL);
			String queueName = _overrideConnection.getPropertyFromInput(input, wmMQOverrideConnection.QUEUE_NAME_LABEL);
			String useLocalQueueManager = _overrideConnection.getPropertyFromInput(input,
					wmMQOverrideConnection.USE_LOCAL_QUEUE_MANAGER_LABEL);
			if (queueManagerName == null || queueManagerName.equals("")) {
				queueManagerName = conn.getResolvedQueueManagerName();
			}
			if (queueName == null || queueName.equals("")) {
				queueName = conn.getResolvedQueueName(false);

			}
			if (useLocalQueueManager == null || useLocalQueueManager.equals("")) {
				useLocalQueueManager = "no";
			}

			boolean useExistingConn = false;
			if (useLocalQueueManager != null && useLocalQueueManager.equalsIgnoreCase("yes") // Should
																								// have
																								// useLocalQueueManager
																								// as
																								// yes
					&& queueManagerName != null && !queueManagerName.equals(conn.getResolvedQueueManagerName()) // Provided
																												// queue
																												// manager
																												// name
																												// should
																												// be
																												// different
																												// form
																												// that
																												// of
																												// the
																												// connection
					&& queueName.indexOf(' ') < 0) // queueName should not be a
													// distribution list
			{

				useExistingConn = true;
			}

			// Gets the connection handle.
			if (!useExistingConn) {
				conn = (wmMQConnection) _overrideConnection.overrideConnection(connection, input, false);
			}
			if (!conn.getResolvedQueueName(false).equals(askedfor)) {
				log(ARTLogger.INFO, 1003, "put:execute",
						"asked for " + askedfor + ",got" + conn.getResolvedQueueName(false));
			}
			String connaddr = conn.toString();
			log(ARTLogger.INFO, 1003, "PE", "returning conn=" + connaddr.substring(connaddr.indexOf("@")) + "/"
					+ askedfor + "/" + conn.getResolvedQueueName(false));

			// Retrieve the msgbody from the input record
			// Trax 1-S15OV & 1-RZDD0 - if multiple threads enter this code
			// segment at the same time, the msgBody property in wmMQBaseService
			// could be overlaid, resulting in messages begin written to the
			// wrong queue.
			// setMsgBody(input.get(wmMQAdapterConstant.MSG_BODY));

			wmMQMessage msg = new wmMQMessage();

			// Trax 1-12K2HK : Allow the user to override the ccsid from the
			// input.
			// This fix should have been done with Trax 1-R60VH. Done in all the
			// services
			// except the put service.
			String ccsid = _overrideConnection.getPropertyFromInput(input, wmMQOverrideConnection.QUEUE_CCSID_LABEL);
			if ((ccsid == null) || (ccsid.trim().equals("")))
				ccsid = ((wmMQConnectionFactory) conn.getFactory()).getCCSID();
			msg.getMQMessage().characterSet = WmMQAdapterUtils.getCharacterSetId(ccsid);

			// Allow the user to override the Encoding
			String encoding = _overrideConnection.getPropertyFromInput(input,
					wmMQOverrideConnection.QUEUE_ENCODING_LABEL);
			if (encoding == null || encoding.trim().equals("")) {
				encoding = ((wmMQConnectionFactory) conn.getFactory()).getEncoding();
			}
			if (encoding.length() > 5) {
				encoding = encoding.substring(0, 5);
			}
			msg.getMQMessage().encoding = Integer.parseInt(encoding);

			Values header = new Values();

			String[] constants = _mqmd.getMqmdInputConstants();
			// boolean[] constants_use = _mqmd.getUseMQMDFields();
			for (int i = 0; i < constants.length; i++) {
				// Remove the "msgHeader." prefix
				if ((constants[i] != null) && (_inputFieldNames[i].length() > 0)) {
					// Skip the "msgHeader." prefix from the outputFieldName
					header.put(_inputFieldNames[i].substring(10), constants[i]);
				}
			}

			msg.copyMQMDFieldsToMsg((WmRecord) input.get(wmMQAdapterConstant.MQMD_HEADER), header, _inputFieldNames);

			if (!useExistingConn) {
				copyJMSPropertiesToMsg(input, msg, conn);
			} else {
				copyJMSPropertiesToMsg(input, msg, queueManagerName, queueName);
			}

			// Trax 1-S15OV & 1-RZDD0 - if multiple threads enter this code
			// segment at the same time, the msgBody property in wmMQBaseService
			// could be overlaid, resulting in messages begin written to the
			// wrong queue.
			// msg.setMsgBody(getMsgBody());
			// Trax 1-SWHKJ. msgBodyByteArray is ignored.
			// msg.setMsgBody(wmMQAdapterConstant.MSG_BODY);
			Object msgbody = input.get(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY);
			if ((msgbody == null) || (!((msgbody instanceof byte[]))))
				msgbody = input.get(wmMQAdapterConstant.MSG_BODY);
			msg.setMsgBody(msgbody);
			// log(ARTLogger.CRITICAL, 1003, "put:execute", askedfor + "/" +
			// input.get(wmMQAdapterConstant.MSG_BODY));

			int reasonCode = 0;

			int threshold = this.getBackoutThreshhold();

			do {
				try {
					putFailed = false;
					log(ARTLogger.INFO, 1003, "Sending message", "");
					if (!useExistingConn) {
						reasonCode = conn.put(msg, null, null);
					} else {
						reasonCode = conn.put(msg, queueManagerName, queueName);
					}
					log(ARTLogger.INFO, 1003, "Sent message", "");
				} catch (wmMQException mqe) {
					// mqe.printStackTrace();
					log(ARTLogger.ERROR, 1055, "Putting messsage", mqe.getMessage());

					reasonCode = ((MQException) mqe.getLinkedException()).reasonCode;

					if (getThrowExceptionOnFailure()) {
						if (WmMQAdapterUtils.fatalException(((MQException) mqe.getLinkedException()).reasonCode))
							throw wmMQAdapter.getInstance().createAdapterConnectionException(1055,
									new String[] { "Get", mqe.getMessage() }, mqe);
						else
							throw mqe;
					}

					// Believe it or not, the statement
					// if ((--threshold == 0) && (!dlq.equals("")))
					// does not decrement the threshold property unless the
					// whole
					// statement is true.
					threshold--;
					// if ((threshold == 0) && (dlq != null) &&
					// (!dlq.trim().equals("")))
					if (threshold == 0) {
						if ((!wmMQAdapter.getIgnoreSystemDefaultDeadLetterQueue())
								|| ((dlq != null) && (!dlq.trim().equals("")))) {
							String headersToIncludeInDeadLetterMessage = getDeadLetterMessageHeaders();
							idc = input.getCursor();
							if ((idc != null) && (idc.first(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS))) {
								headersToIncludeInDeadLetterMessage = (String) idc.getValue();
								if (headersToIncludeInDeadLetterMessage == null
										|| headersToIncludeInDeadLetterMessage.trim().equals("")) {
									headersToIncludeInDeadLetterMessage = getDeadLetterMessageHeaders();
								} else {
									headersToIncludeInDeadLetterMessage = headersToIncludeInDeadLetterMessage.trim();
								}
							}
							idc.destroy();
							if (!useExistingConn) {
								conn.moveMsgToDLQ(msg, dlq, dlqQmgr, "Put", false, headersToIncludeInDeadLetterMessage);
							} else {
								conn.moveMsgToDLQ(msg, dlq, dlqQmgr, queueName, queueManagerName, "Put",
										headersToIncludeInDeadLetterMessage);
							}
							Object[] parms = { "Put", dlq };
							String errmsg = wmMQException.format("3040", parms);
							output.put(wmMQAdapterConstant.ERROR_MSG, errmsg);
						}
						output.put(wmMQAdapterConstant.REASON_CODE, new Integer(reasonCode));
						output.put("conditionCode", "" + MQConstants.MQCC_FAILED);

						// Trax 1-14WOV9 : Begin
						// For transactional connections reset the inUse flag
						// while commit or rollback.
						if (!(conn instanceof wmMQTransactionalConnection)) {
							conn.setInUse(false);
						}
						// Trax 1-14WOV9 : End

						return output;
					}
					// Allow the MQMD header to be copied to the output
					// pipeline. This is useful
					// when broadcasting messages to multiple queues.

					// output.put("conditionCode",
					// ((MQException)mqe.getLinkedException()).completionCode);
					// output.put("reasonCode",
					// ((MQException)mqe.getLinkedException()).reasonCode);
					// return output;
					putFailed = true;
				}
			} while ((retryableError(reasonCode)) && ((wmMQAdapter.getIgnoreSystemDefaultDeadLetterQueue())
					|| ((dlq != null) && (!dlq.trim().equals("")))));

			header = new Values();

			if (wmMQAdapter.isInterospectHeaderDataTypes()) {
				HashMap headerDataTypes = WmMQAdapterUtils.findFieldDataTypes(getOutSignature());
				msg.setSplHeaderDataTypes(headerDataTypes);
			}

			msg.copyMsgFieldsToMQMD(header, _outputFieldNames);

			wmMQAdapter.dumpValues("Received MQMD", header);

			// boolean[] use = _mqmd.getUseOutputMQMDFields();

			WmRecord mqmd = WmRecordFactory.getFactory().createWmRecord("msgHeader");

			// Required for Upgrade utility
			for (int i = 0; i < _outputFieldNames.length; i++)
			// int ofnlen = _outputFieldNames.length;
			// if (ofnlen > wmMQMQMD.mqmdFieldDisplayNames.length)
			// ofnlen = wmMQMQMD.mqmdFieldDisplayNames.length;
			// for (int i = 0 ; i < ofnlen ; i++)
			{
				// if (use[i])
				{
					log(ARTLogger.INFO, 1003, "Returning field", _outputFieldNames[i]);
					Object o = header.get(_outputFieldNames[i].substring(10));
					mqmd.put(_outputFieldNames[i].substring(10), o);
				}
			}
			output.put("msgHeader", mqmd);

			output.put("reasonCode", "" + reasonCode);

			// If the 'get' completes with a warning, pass the reason code to
			// the caller
			if (putFailed)
				output.put("conditionCode", "" + MQConstants.MQCC_FAILED);
			else if (reasonCode > 0)
				output.put("conditionCode", "" + MQConstants.MQCC_WARNING);

			if (!useExistingConn) {
				_overrideConnection.fillOverriddenConnection(conn, output, false);
			} else {
				_overrideConnection.fillOverriddenConnection(conn, output, queueManagerName, queueName);
			}

			// Trax 1-14WOV9 : Begin
			// For transactional connections reset the inUse flag while commit
			// or rollback.
			if (!(conn instanceof wmMQTransactionalConnection)) {
				conn.setInUse(false);
			}
			// Trax 1-14WOV9 : End
		} catch (Exception t) {
			// createAdapterException throws an AdapterException
			// using the Major Code and ResourceBundle already
			// associated with this adapter and a wrapped Throwable exception.
			// The minorCode parameter is an int representing the
			// Minor Code for this Exception.
			// The throwable parameter is a throwable object representing
			// the underlying error condition.
			// AdapterException issues the error message
			// "Put: lookup execution failed."
			// Refer to the Javadoc of the WmAdapter and
			// AdapterException classes for more details.
			// t.printStackTrace();
			log(ARTLogger.ERROR, 1055, "Putting messsage", t.getMessage());
			if (conn != null) {
				// Trax 1-14WOV9 : Begin
				// For transactional connections reset the inUse flag while
				// commit or rollback.
				if (!(conn instanceof wmMQTransactionalConnection)) {
					conn.setInUse(false);
				}
				// Trax 1-14WOV9 : End
			}
			if (getThrowExceptionOnFailure()) {
				if (t instanceof AdapterConnectionException)
					throw (AdapterConnectionException) t;
				else
					throw wmMQAdapter.getInstance().createAdapterException(1055, new String[] { "Put", t.getMessage() },
							t);
			}

			if (t instanceof wmMQException) {
				int reasonCode = ((MQException) ((wmMQException) t).getLinkedException()).reasonCode;
				output.put("reasonCode", "" + reasonCode);
				output.put("conditionCode", "" + MQConstants.MQCC_FAILED);
			}

			Object[] parms = { "Put", t.getMessage() };
			String errormsg = wmMQException.format("1055", parms);
			output.put(wmMQAdapterConstant.ERROR_MSG, errormsg);
			return output;
		}
		return output;

	}

	private boolean retryableError(int reasonCode) {
		if ((reasonCode == 2016) || // MQRC_GET_INHIBITED
				(reasonCode == 2024) || // MQRC_SYNCPOINT_LIMIT_REACHED
				(reasonCode == 2051) || // MQRC_PUT_INHIBITED
				(reasonCode == 2268)) // MQRC_CLUSTER_PUT_INHIBITED
			return true;

		return false;
	}

	private String[] _serviceGroupFieldNames = new String[] { wmMQAdapterConstant.DEAD_LETTER_QUEUE,
			wmMQAdapterConstant.DEAD_LETTER_QUEUE_TYPE, wmMQAdapterConstant.DEAD_LETTER_QUEUE_NAME,
			wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER, wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_TYPE,
			wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_NAME, wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS,
			wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_TYPE, wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_NAME,
			wmMQAdapterConstant.BACKOUT_THRESHHOLD, wmMQAdapterConstant.THROW_EXCEPTION_ON_FAILURE,
			wmMQAdapterConstant.MSG_BODY, wmMQAdapterConstant.MSG_BODY_NAME, wmMQAdapterConstant.MSG_BODY_TYPE,

			// Trax 1-11YH1R : Begin Added MSG_BODY_BYTE_ARRAY to the input
			// fields
			wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY, wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_NAME,
			wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE
			// Trax 1-11YH1R : End

	};

}
