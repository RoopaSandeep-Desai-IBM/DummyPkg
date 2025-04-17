/*
 * RequestReply.java
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

import java.util.HashMap;
import java.util.Locale;

import javax.resource.ResourceException;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.MQConstants;
import com.wm.adapter.wmmqadapter.WmMQAdapterUtils;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adapter.wmmqadapter.connection.wmMQConnection;
import com.wm.adapter.wmmqadapter.connection.wmMQConnectionFactory;
import com.wm.adapter.wmmqadapter.connection.wmMQException;
import com.wm.adapter.wmmqadapter.connection.wmMQMessage;
import com.wm.adapter.wmmqadapter.connection.wmMQOverrideConnection;
import com.wm.adapter.wmmqadapter.connection.wmMQTransactionalConnection;
import com.wm.adk.cci.record.WmRecord;
import com.wm.adk.cci.record.WmRecordFactory;
import com.wm.adk.connection.WmManagedConnection;
import com.wm.adk.error.AdapterConnectionException;
import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;
import com.wm.adk.metadata.WmTemplateDescriptor;
import com.wm.data.IDataCursor;
import com.wm.util.Config;
import com.wm.util.Values;

/*
 * This class is an implementation of the lookup service template
 * for the wmMQAdapter. The lookup
 * takes a key as input, and returns the column values of the record.
 * An error is issued if there is no record for that key.
 *
 * This template demonstrates the use of combo boxes, allowing
 * a user to select catalogs, schemas, and tables.
 */
public class RequestReply extends wmMQBaseService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8515346083199045713L;

	//SharedMode property
	private boolean _sharedMode = true;

    //ConvertDataOption property
    private boolean _convertDataOption = true;
    
	//Message body
	private Object _outputMsgBody = null;

	//Message body name
	private String _outputMsgBodyName = "replyMsgBody";

	// Trax 1-11YH1R : Begin
	//Message body field type	
	private String[] _outputMsgBodyType = {"java.lang.Object"};
	// Trax 1-11YH1R : End

	//Message body
	private Object _outputMsgBodyByteArray = null;

	//Message body name
	private String _outputMsgBodyByteArrayName = "replyMsgBodyByteArray";

	// Trax 1-11YH1R : Begin
	//Message body field type
	private String[] _outputMsgBodyByteArrayType = {"java.lang.Object"};
	// Trax 1-11YH1R : End

	protected wmMQOverrideConnection _overrideReplyToConnection = null;

    /*
     * Constructor.
     */
    public RequestReply()
    {
        _serviceName = wmMQAdapterConstant.REQUEST_REPLY_SERVICE;
		_overrideConnection = new wmMQOverrideConnection(_serviceName);
		_overrideReplyToConnection = new wmMQOverrideConnection(_serviceName,
															    "overrideReplyToConnection", 
																"overrideReplyToConnection");
    }
	 /*
	  * Sets the sharedMode property
	  *
	  * The shared parameter is the sharedMode property.
	  */
	 public void setSharedMode(boolean shared)
	 {
		 this._sharedMode = shared;
	 }

	 /*
	  * Gets the sharedMode property.
	  *
	  */
	 public boolean getSharedMode()
	 {
		 return _sharedMode;
	 }

    /*
     * Gets the convertDataOption property.
     *
     */
    public boolean getConvertDataOption() 
    {
        return _convertDataOption;
    }

    /*
     * Sets the convertDataOption property
     *
     * The convert parameter is the convertDataOption property.
     */
    public void setConvertDataOption(boolean convert) 
    {
        this._convertDataOption = convert;
    }

	/*
	 * Gets the Output message body.
	 *
	 */
	public Object getReplyMsgBody()
	{
		return _outputMsgBody;
	}

	/*
	 * Sets the Output message body.
	 *
	 * The msgbody parameter is the Output Message body property.
	 *
	 */
	public void setReplyMsgBody(Object msgbody)
	{
		_outputMsgBody = msgbody;
	}
	/*
	 * Gets the message body.
	 *
	 */
	public String getReplyMsgBodyName()
	{
		return _outputMsgBodyName;
	}

	/*
	 * Sets the Output message body.
	 *
	 * The msgbody parameter is the Output Message body property.
	 *
	 */
	public void setReplyMsgBodyName(String msgbodyname)
	{
		_outputMsgBodyName = msgbodyname;
	}

	/*
	 * Gets the message body.
	 *
	 */
	public String[] getReplyMsgBodyType()
	{
		return _outputMsgBodyType; //NOSONAR
	}

	/*
	 * Sets the message body.
	 *
	 * The msgbody parameter is the Message body property.
	 *
	 */
	public void setReplyMsgBodyType(String[] msgbodytype)
	{
		_outputMsgBodyType = msgbodytype; //NOSONAR
	}

	/*
	 * Gets the Output message body.
	 *
	 */
	public Object getReplyMsgBodyByteArray()
	{
		return _outputMsgBodyByteArray;
	}

	/*
	 * Sets the Output message body.
	 *
	 * The msgbody parameter is the Output Message body property.
	 *
	 */
	public void setReplyMsgBodyByteArray(Object msgbodyByteArray)
	{
		_outputMsgBodyByteArray = msgbodyByteArray;
	}
	/*
	 * Gets the message body.
	 *
	 */
	public String getReplyMsgBodyByteArrayName()
	{
		return _outputMsgBodyByteArrayName;
	}

	/*
	 * Sets the message body.
	 *
	 * The msgbody parameter is the Message body property.
	 *
	 */
	public void setReplyMsgBodyByteArrayName(String msgbodyByteArrayname)
	{
		_outputMsgBodyByteArrayName = msgbodyByteArrayname;
	}

	/*
	 * Gets the message body.
	 *
	 */
	public String[] getReplyMsgBodyByteArrayType()
	{
		return _outputMsgBodyByteArrayType; //NOSONAR
	}

	/*
	 * Sets the message body.
	 *
	 * The msgbody parameter is the Message body property.
	 *
	 */
	public void setReplyMsgBodyByteArrayType(String[] msgbodyByteArraytype)
	{
		_outputMsgBodyByteArrayType = msgbodyByteArraytype; //NOSONAR
	}

	public String[] getOverrideReplyToConnectionPropertyType()
	{
		return _overrideReplyToConnection.getOverrideConnectionPropertyType();
	}

	public void setOverrideReplyToConnectionPropertyType(String[] overrideReplyToConnectionPropertyType)
	{
		_overrideReplyToConnection.setOverrideConnectionPropertyType(overrideReplyToConnectionPropertyType);
	}

	public String getOverrideUseLocalQueueManager()
	{
		return _overrideConnection.getOverrideUseLocalQueueManager();
	}

	public void setOverrideUseLocalQueueManager(String overrideUseLocalQueueManager)
	{
		_overrideConnection.setOverrideUseLocalQueueManager(overrideUseLocalQueueManager);
	}

	public String getOverrideReplyToConnectionQueueManagerName()
	{
		return _overrideReplyToConnection.getOverrideConnectionQueueManagerName();
	}

	public void setOverrideReplyToConnectionQueueManagerName(String overrideReplyToConnectionQueueManagerName)
	{
		_overrideReplyToConnection.setOverrideConnectionQueueManagerName(overrideReplyToConnectionQueueManagerName);
	}

	public String getOverrideReplyToConnectionHostName()
	{
		return _overrideReplyToConnection.getOverrideConnectionHostName();
	}

	public void setOverrideReplyToConnectionHostName(String overrideReplyToConnectionHostName)
	{
		_overrideReplyToConnection.setOverrideConnectionHostName(overrideReplyToConnectionHostName);
	}

	public String getOverrideReplyToConnectionPort()
	{
		return _overrideReplyToConnection.getOverrideConnectionPort();
	}

	public void setOverrideReplyToConnectionPort(String overrideReplyToConnectionPort)
	{
		_overrideReplyToConnection.setOverrideConnectionPort(overrideReplyToConnectionPort);
	}

	public String getOverrideReplyToConnectionChannel()
	{
		return _overrideReplyToConnection.getOverrideConnectionChannel();
	}

	public void setOverrideReplyToConnectionChannel(String overrideReplyToConnectionChannel)
	{
		_overrideReplyToConnection.setOverrideConnectionChannel(overrideReplyToConnectionChannel);
	}

	public String getOverrideReplyToConnectionCCSID()
	{
		return _overrideReplyToConnection.getOverrideConnectionCCSID();
	}

	public void setOverrideReplyToConnectionCCSID(String overrideReplyToConnectionCCSID)
	{
		_overrideReplyToConnection.setOverrideConnectionCCSID(overrideReplyToConnectionCCSID);
	}

	public String getOverrideReplyToConnectionEncoding()
	{
		return _overrideReplyToConnection.getOverrideConnectionEncoding();
	}

	public void setOverrideReplyToConnectionEncoding(String overrideReplyToConnectionEncoding)
	{
		_overrideReplyToConnection.setOverrideConnectionEncoding(overrideReplyToConnectionEncoding);
	}

	public String getOverrideReplyToConnectionQueueName()
	{
		return _overrideReplyToConnection.getOverrideConnectionQueueName();
	}

	public void setOverrideReplyToConnectionQueueName(String overrideReplyToConnectionQueueName)
	{
		_overrideReplyToConnection.setOverrideConnectionQueueName(overrideReplyToConnectionQueueName);
	}

	public String getOverrideReplyToConnectionDynamicQueuePrefix()
	{
		return _overrideReplyToConnection.getOverrideConnectionDynamicQueuePrefix();
	}

	public void setOverrideReplyToConnectionDynamicQueuePrefix(String overrideReplyToConnectionDynamicQueuePrefix)
	{
		_overrideReplyToConnection.setOverrideConnectionDynamicQueuePrefix(overrideReplyToConnectionDynamicQueuePrefix);
	}
/*
	public String getOverrideReplyToConnectionUserid()
	{
		return _overrideReplyToConnection.getOverrideConnectionUserid();
	}

	public void setOverrideReplyToConnectionUserid(String overrideReplyToConnectionUserid)
	{
		_overrideReplyToConnection.setOverrideConnectionUserid(overrideReplyToConnectionUserid);
	}

	public String getOverrideReplyToConnectionPassword()
	{
		return _overrideReplyToConnection.getOverrideConnectionPassword();
	}

	public void setOverrideReplyToConnectionPassword(String overrideReplyToConnectionPassword)
	{
		_overrideReplyToConnection.setOverrideConnectionPassword(overrideReplyToConnectionPassword);
	}
*/
	public String getOverriddenReplyToConnectionQueueManagerName()
	{
		return _overrideReplyToConnection.getOverriddenConnectionQueueManagerName();
	}

	public void setOverriddenReplyToConnectionQueueManagerName(String overriddenReplyToConnectionQueueManagerName)
	{
		_overrideReplyToConnection.setOverriddenConnectionQueueManagerName(overriddenReplyToConnectionQueueManagerName);
	}

	public String getOverriddenReplyToConnectionHostName()
	{
		return _overrideReplyToConnection.getOverriddenConnectionHostName();
	}

	public void setOverriddenReplyToConnectionHostName(String overriddenReplyToConnectionHostName)
	{
		_overrideReplyToConnection.setOverriddenConnectionHostName(overriddenReplyToConnectionHostName);
	}

	public String getOverriddenReplyToConnectionPort()
	{
		return _overrideReplyToConnection.getOverriddenConnectionPort();
	}

	public void setOverriddenReplyToConnectionPort(String overriddenReplyToConnectionPort)
	{
		_overrideReplyToConnection.setOverriddenConnectionPort(overriddenReplyToConnectionPort);
	}

	public String getOverriddenReplyToConnectionChannel()
	{
		return _overrideReplyToConnection.getOverriddenConnectionChannel();
	}

	public void setOverriddenReplyToConnectionChannel(String overriddenReplyToConnectionChannel)
	{
		_overrideReplyToConnection.setOverriddenConnectionChannel(overriddenReplyToConnectionChannel);
	}

	public String getOverriddenReplyToConnectionCCSID()
	{
		return _overrideReplyToConnection.getOverriddenConnectionCCSID();
	}

	public void setOverriddenReplyToConnectionCCSID(String overriddenReplyToConnectionCCSID)
	{
		_overrideReplyToConnection.setOverriddenConnectionCCSID(overriddenReplyToConnectionCCSID);
	}

	public String getOverriddenReplyToConnectionEncoding()
	{
		return _overrideReplyToConnection.getOverriddenConnectionEncoding();
	}

	public void setOverriddenReplyToConnectionEncoding(String overriddenReplyToConnectionEncoding)
	{
		_overrideReplyToConnection.setOverriddenConnectionEncoding(overriddenReplyToConnectionEncoding);
	}

	public String getOverriddenReplyToConnectionQueueName()
	{
		return _overrideReplyToConnection.getOverriddenConnectionQueueName();
	}

	public void setOverriddenReplyToConnectionQueueName(String overriddenReplyToConnectionQueueName)
	{
		_overrideReplyToConnection.setOverriddenConnectionQueueName(overriddenReplyToConnectionQueueName);
	}

	public String getOverriddenReplyToConnectionDynamicQueuePrefix()
	{
		return _overrideReplyToConnection.getOverriddenConnectionDynamicQueuePrefix();
	}

	public void setOverriddenReplyToConnectionDynamicQueuePrefix(String overriddenReplyToConnectionDynamicQueuePrefix)
	{
		_overrideReplyToConnection.setOverriddenConnectionDynamicQueuePrefix(overriddenReplyToConnectionDynamicQueuePrefix);
	}
/*
	public String getOverriddenReplyToConnectionUserid()
	{
		return _overrideReplyToConnection.getOverriddenConnectionUserid();
	}

	public void setOverriddenReplyToConnectionUserid(String overriddenReplyToConnectionUserid)
	{
		_overrideReplyToConnection.setOverriddenConnectionUserid(overriddenReplyToConnectionUserid);
	}

	public String getOverriddenReplyToConnectionPassword()
	{
		return _overrideReplyToConnection.getOverriddenConnectionPassword();
	}

	public void setOverriddenReplyToConnectionPassword(String overriddenReplyToConnectionPassword)
	{
		_overrideReplyToConnection.setOverriddenConnectionPassword(overriddenReplyToConnectionPassword);
	}
*/
	/*
	 * This method populates the metadata object describing
	 * this service template in the specified locale.
	 * This method overrides the fillWmTemplateDescriptor() method in
	 * the super class.
	 *
	 * The d parameter is the metadata object describing this adapter service.
	 * The l parameter is the Locale in which the locale-specific metadata
	 * should be populated.
	 * AdapterException is thrown if an error is encountered
	 * while populating the metadata.
	 */
	public void fillWmTemplateDescriptor(WmTemplateDescriptor d, Locale l)
		throws AdapterException
	{
		log(ARTLogger.INFO, 1001, "RequestReply.fillWmTemplateDescriptor", "");
		overrideServiceGroupFieldNames(_serviceGroupFieldNames);
		overrideWhereToPutMsgBody(WmTemplateDescriptor.INPUT_FIELD_NAMES);

		super.fillWmTemplateDescriptor(d, l);

		d.createTuple(
			new String[] { wmMQAdapterConstant.REPLY_MSG_BODY,
						   wmMQAdapterConstant.REPLY_MSG_BODY_TYPE
						 });

		d.createTuple(
			new String[] { wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY,
						   wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY_TYPE
						 });

		d.setHidden(wmMQAdapterConstant.REPLY_MSG_BODY, true);
		d.setHidden(wmMQAdapterConstant.REPLY_MSG_BODY_NAME, true);
		d.setHidden(wmMQAdapterConstant.REPLY_MSG_BODY_TYPE, true);
		d.setHidden(wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY, true);
		d.setHidden(wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY_NAME, true);
		d.setHidden(wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY_TYPE, true);

		d.setResourceDomain(wmMQAdapterConstant.REPLY_MSG_BODY,
							wmMQAdapterConstant.REPLY_MSG_BODY,
							null);

		d.setResourceDomain(wmMQAdapterConstant.REPLY_MSG_BODY_TYPE,
							wmMQAdapterConstant.REPLY_MSG_BODY_TYPE,
							null);

		d.setResourceDomain(wmMQAdapterConstant.REPLY_MSG_BODY_NAME,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.REPLY_MSG_BODY,
										  wmMQAdapterConstant.REPLY_MSG_BODY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY,
							wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY,
							null);

		d.setResourceDomain(wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY_TYPE,
							wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY_TYPE,
							null);

		d.setResourceDomain(wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY_NAME,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY,
										  wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY_TYPE});

//		d.setHidden(wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_REPLY_TO_PROPERTY_TYPE, true);
//		d.setHidden(wmMQAdapterConstant.OVERRIDE_PROPERTY_NAMES, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_QUEUE_MANAGER_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_HOST_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_PORT, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_CHANNEL_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_CCSID, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_ENCODING, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_QUEUE_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_DYNAMIC_QUEUE_PREFIX, true);
//		d.setHidden(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_USERID, true);
//		d.setHidden(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_PASSWORD, true);

		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_QUEUE_MANAGER_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_HOST_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_PORT, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_CHANNEL_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_CCSID, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_ENCODING, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_QUEUE_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_DYNAMIC_QUEUE_PREFIX, true);
//		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_USERID, true);
//		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_PASSWORD, true);


		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE,
							wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE,
							null);

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_QUEUE_MANAGER_NAME,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_HOST_NAME,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_PORT,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_CHANNEL_NAME,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_CCSID,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_ENCODING,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_QUEUE_NAME,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_DYNAMIC_QUEUE_PREFIX,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});
/*
		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_USERID,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_PASSWORD,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});
*/
		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_QUEUE_MANAGER_NAME,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_HOST_NAME,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_PORT,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_CHANNEL_NAME,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_CCSID,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_ENCODING,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_QUEUE_NAME,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_DYNAMIC_QUEUE_PREFIX,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});
/*
		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_USERID,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_PASSWORD,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE});
*/
//		d.setPassword(wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_PASSWORD);
//		d.setPassword(wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_PASSWORD);
	}

    /*
     * Puts an MQMessage onto an wmMQ Queue.
     * connection is the connection handle.
     * input is the input record of the adapter service.
     */
	@SuppressWarnings("squid:S1541")
    public WmRecord execute(WmManagedConnection connection, WmRecord input) throws ResourceException
    {
        log(ARTLogger.INFO, 1001, "RequestReply:execute", "");
        IDataCursor idc = null;
        /*try
        {
            idc = input.getCursor();
            if ((idc != null) && (idc.first()))
            {
                do {
                    String key = idc.getKey();
                    Object o = idc.getValue();
                    if (o == null)
                        log(ARTLogger.INFO, 1003, "key=" + key + " is null", "");
                    else
                        log(ARTLogger.INFO, 1003, "key=" + key, ", value=" + o.toString());
                }
                while (idc.next());
            }
            else
                log(ARTLogger.INFO, 1003, "RequestReply:execute", "idc is null, input=" + input);
        }
        catch (Exception ex)
        {
            //ex.printStackTrace();
        }
        finally
        {
        	if (idc != null)
        		idc.destroy();
        }*/
		
		log(ARTLogger.INFO, 1003, "RequestReply execute input iData" , input.getIData().toString());
        //The connection handle.
        wmMQConnection conn = (wmMQConnection)connection;
		wmMQConnection replyconn = null;

        //The output record to return.
        WmRecord output = WmRecordFactory.getFactory().createWmRecord("Output");

		//Retrieve the msgbody from the input record
        //Trax 1-S15OV  & 1-RZDD0 - if multiple threads enter this code 
		//segment at the same time, the msgBody property in wmMQBaseService 
		//could be overlaid, resulting in messages begin written to the 
		//wrong queue.
		//setMsgBody(input.get(wmMQAdapterConstant.MSG_BODY));

		String dlq = (String)input.get(wmMQAdapterConstant.DEAD_LETTER_QUEUE_NAME);
		if (dlq == null)
			dlq = getDeadLetterQueue();

		String dlqQmgr = (String)input.get(wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_NAME); 
		if (dlqQmgr == null)
		dlqQmgr = getDeadLetterQueueManager();

        try
        {
			String queueManagerName = _overrideConnection.getPropertyFromInput(input, wmMQOverrideConnection.QUEUE_MANAGER_NAME_LABEL);
			String queueName = _overrideConnection.getPropertyFromInput(input, wmMQOverrideConnection.QUEUE_NAME_LABEL);
			String useLocalQueueManager = _overrideConnection.getPropertyFromInput(input, wmMQOverrideConnection.USE_LOCAL_QUEUE_MANAGER_LABEL);
			if(queueManagerName == null || queueManagerName.equals("")) {
				queueManagerName = conn.getResolvedQueueManagerName();
			}
			if(queueName == null || queueName.equals("")) {
				queueName = conn.getResolvedQueueName(false);
			}
			if(useLocalQueueManager == null || useLocalQueueManager.equals("")) {
				useLocalQueueManager = "no";
			}
			
			boolean useExistingConn = false;
			if(		useLocalQueueManager != null
					&&	useLocalQueueManager.equalsIgnoreCase("yes")  // Should have useLocalQueueManager as yes
					&&	queueManagerName != null
					&& !queueManagerName.equals(conn.getResolvedQueueManagerName())  // Provided queue manager name should be different form that of the connection
					&&  queueName.indexOf(' ') < 0)  // queueName should not be a distribution list 
				{
				
				useExistingConn = true;
			}
        	
            //Gets the connection handle.
			if(!useExistingConn) {
				conn = (wmMQConnection) _overrideConnection.overrideConnection(connection, input, false);
			}

			if (conn instanceof wmMQTransactionalConnection)
				throw wmMQAdapter.getInstance().createAdapterException(1052);

			wmMQMessage msg = new wmMQMessage();
			msg.getMQMessage().messageType = MQConstants.MQMT_REQUEST; //Set default

			//Trax log 1-R60VH. Allow user to override CCSID
			String ccsid = _overrideConnection.getPropertyFromInput(input, wmMQOverrideConnection.QUEUE_CCSID_LABEL );
			if ((ccsid == null) || (ccsid.trim().equals("")))
				ccsid = ((wmMQConnectionFactory)conn.getFactory()).getCCSID();
			msg.getMQMessage().characterSet = WmMQAdapterUtils.getCharacterSetId(ccsid);

            // Allow the user to override the Encoding
			String encoding = _overrideConnection.getPropertyFromInput(input, wmMQOverrideConnection.QUEUE_ENCODING_LABEL);
			if (encoding == null || encoding.trim().equals("")) {
				encoding = ((wmMQConnectionFactory)conn.getFactory()).getEncoding();
			}
			if(encoding.length() > 5) {
				encoding = encoding.substring(0, 5);
			}
			msg.getMQMessage().encoding = Integer.parseInt(encoding);
            
			Values header = new Values();
			
            //Copy predefined constants to the header object
			String[] constants = _mqmd.getMqmdInputConstants();
            for (int i = 0 ; i < constants.length ; i++)
            {
                //Remove the "msgHeader." prefix
                if ((constants[i] != null) && (_inputFieldNames[i].length() > 0))
                    //Skip the "msgHeader." prefix from the outputFieldName
                    header.put(_inputFieldNames[i].substring(10), constants[i]);
            }

			//Now, merge the constants and the piepline data into the MQMessage header
			msg.copyMQMDFieldsToMsg((WmRecord)input.get("msgHeader"),
									header,
									_inputFieldNames);
            //Required by the upgrade utility
			//String[] fields = new String[wmMQMQMD.mqmdFieldDisplayNames.length];
			//for (int s = 0 ; s < fields.length; s++)
			//	fields[s] = wmMQMQMD.mqmdFieldDisplayNames[s].substring(10);
            //msg.copyMQMDFieldsToMsg((WmRecord)input.get(wmMQAdapterConstant.MQMD_HEADER),
			//						header,
			//						fields);

			//Get the connection object for the replyToQueue, in case it represents a model queue
			
			String replyToQueue = "";
			String replyToQueueMgr = "";

			wmMQAdapter.dumpValues("put values", header);

			//Retrieve the replyToQueue and replyToQueueManager properties
			msg.copyMsgFieldsToMQMD(header, _putFields, _usePutFields);
			replyToQueue = header.getString(wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_NAME);
			replyToQueueMgr = header.getString(wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_MANAGER_NAME);

			if ((replyToQueue == null) || (replyToQueue.trim().equals("")))
			{
				replyToQueue = _overrideReplyToConnection.getPropertyFromInput(input, 
																			   wmMQOverrideConnection.QUEUE_NAME_LABEL);
				if ((replyToQueue == null) || (replyToQueue.trim().equals("")))
				{
					log(ARTLogger.ERROR,
						1051,
						wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_NAME, "");
					if (getThrowExceptionOnFailure())
					{
						throw wmMQAdapter.getInstance().createAdapterException(1051, 
																			   new String[] {wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_NAME});
					}
		            conn.setInUse(false);
					return null;
				}
			}

			if ((replyToQueueMgr == null) || (replyToQueueMgr.trim().equals("")))
			{
				replyToQueueMgr = _overrideReplyToConnection.getPropertyFromInput(input, 
																			      wmMQOverrideConnection.QUEUE_MANAGER_NAME_LABEL);
				if ((replyToQueueMgr == null) || (replyToQueueMgr.trim().equals("")))
					replyToQueueMgr = ((wmMQConnectionFactory)conn.getFactory()).getQueueManagerName();
			}

			//Trax 1-ZSMCG - The replyTo connection needs to be cached with key including hostname
			String hostName = _overrideReplyToConnection.getPropertyFromInput(input, 
				      wmMQOverrideConnection.QUEUE_HOST_NAME_LABEL);
			if ((hostName == null) || (hostName.trim().equals("")))
				hostName = ((wmMQConnectionFactory)conn.getFactory()).getHostName();
			if ((hostName == null) || (hostName.trim().equals("")))
				hostName="";
			
			//String replyconnname = replyToQueueMgr + "__" + replyToQueue;	
			String replyconnname = hostName + "__" + replyToQueueMgr + "__" + replyToQueue;
			//Trax 1-ZSMCG
			replyconn = conn.retrieveReplyToConnection(replyconnname);

			if (replyconn == null)
			{
				wmMQConnectionFactory connFactory = wmMQAdapter.findConnectionFactory(replyconnname);
				if (connFactory == null)
				{
					connFactory	= wmMQConnectionFactory.clone((wmMQConnectionFactory)conn.getFactory());
					wmMQAdapter.cacheConnectionFactory(replyconnname, connFactory);
					connFactory.setQueueManagerName(replyToQueueMgr);
					connFactory.setQueueName(replyToQueue);
				}

				replyconn = (wmMQConnection)connFactory.createManagedConnectionObject(null, null);
				replyconn.setFactory(connFactory);
				// Trax 1-1UQV4K [ RequestReply service continues to hang onto a stale connection after fatal error ]
				try {
					// Cache it to the reply connection pool, then override the connection.
					conn.cacheReplyToConnection(replyconn);
				}
				catch (wmMQException ex) {
					// Destroy the connection as it has not been cached in reply connection pool but
					// just the CF pool.
					if(replyconn != null) {
						replyconn.destroy();
					}
					
					throw ex;
				}
				//! Trax 1-1UQV4K
				replyconn = _overrideReplyToConnection.overrideConnection(replyconn, 
																		  input, 
																		  true);

				//Now, override the ReplyToQueueName and ReplyToQueueManagerName
				//properties in the MQMD with the real Queue/QMgr names
//				replyToQueue = replyconn.getResolvedQueueName(true);
//				replyToQueueMgr = ((wmMQConnectionFactory)replyconn.getFactory()).getQueueManagerName();
				header.put(wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_MANAGER_NAME, replyToQueueMgr);
				header.put(wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_NAME, replyToQueue);
				msg.copyMQMDFieldsToMsg(header, _putFields, _usePutFields);
			}
			else
			{
				//Override the connection if necessary
				replyconn = _overrideReplyToConnection.overrideConnection(replyconn, 
																		  input, 
																		  true);
				header.put(wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_MANAGER_NAME, replyToQueueMgr);
				header.put(wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_NAME, replyToQueue);
				msg.copyMQMDFieldsToMsg(header, _putFields, _usePutFields);
	        }
								 
            if(!useExistingConn) {
            	copyJMSPropertiesToMsg(input, msg, conn);
            }
            else {
            	copyJMSPropertiesToMsg(input, msg, queueManagerName, queueName);
            }
            
            //Trax 1-S15OV  & 1-RZDD0 - if multiple threads enter this code 
			//segment at the same time, the msgBody property in wmMQBaseService 
			//could be overlaid, resulting in messages begin written to the 
			//wrong queue.
            //msg.setMsgBody(getMsgBody());
			//Trax 1-SWHKJ. msgBodyByteArray is ignored.
			//msg.setMsgBody(input.get(wmMQAdapterConstant.MSG_BODY));
			Object msgbody = input.get(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY);
			if ((msgbody == null) || (!((msgbody instanceof byte[]))))
					msgbody = input.get(wmMQAdapterConstant.MSG_BODY);
			msg.setMsgBody(msgbody);
            int reasonCode = 0;
			int threshold = getBackoutThreshhold();
			
			if (threshold > 1)
				threshold = 1;

            do
            {
	            try
    	        {
        	        log(ARTLogger.INFO, 1003, "Sending Request message", "");
        	        if(!useExistingConn) {
                	    reasonCode = conn.put(msg, null, queueName);
        	        }
        	        else {
                	    reasonCode = conn.put(msg, queueManagerName, queueName);
        	        }
                	msg.getMQMessage();
					log(ARTLogger.INFO, 1003, "Sent Request message", "");
    	        }
        	    catch (wmMQException mqe)
            	{
                	//mqe.printStackTrace();
	                log(ARTLogger.ERROR,
    	                1055,
        	            "Putting Request messsage",
            	        mqe.getMessage());

					reasonCode = ((MQException)mqe.getLinkedException()).reasonCode;
	
					if (--threshold == 0)
					{
						if (getThrowExceptionOnFailure())
						{
							if (WmMQAdapterUtils.fatalException(((MQException)mqe.getLinkedException()).reasonCode))
								throw wmMQAdapter.getInstance().createAdapterConnectionException(1055, 
																								 new String[]{"Get", mqe.getMessage()}, 
																								 mqe);
							else
								throw mqe;
						}

						if ((dlq != null) && (!dlq.trim().equals("")))
						{
		            		String headersToIncludeInDeadLetterMessage = getDeadLetterMessageHeaders();
		                    idc = input.getCursor();
		        			if ((idc != null) && (idc.first(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS))) {
		        				headersToIncludeInDeadLetterMessage = (String)idc.getValue();
		        				if(headersToIncludeInDeadLetterMessage == null || headersToIncludeInDeadLetterMessage.trim().equals("")) {
		                    		headersToIncludeInDeadLetterMessage = getDeadLetterMessageHeaders();
		        				}
		        				else {
		            				headersToIncludeInDeadLetterMessage = headersToIncludeInDeadLetterMessage.trim();
		        				}
		        			}
		        			idc.destroy();
		        			if(!useExistingConn) {
								conn.moveMsgToDLQ(msg, 
											 	  dlq,
											 	  dlqQmgr,
											 	  "requestReply - Request msg",
											 	  false,
											 	  headersToIncludeInDeadLetterMessage);
		        			}
		        			else {
								conn.moveMsgToDLQ(msg, 
											 	  dlq,
											 	  dlqQmgr,
											 	  queueName,
											 	  queueManagerName,
											 	  "requestReply - Request msg",
											 	  headersToIncludeInDeadLetterMessage);
		        			}
							Object[] parms = {"requestReply - Request msg", dlq};
							String errmsg = wmMQException.format("3040", parms);
							output.put("reasonCode", "" + ((MQException)mqe.getLinkedException()).reasonCode);
							output.put(wmMQAdapterConstant.ERROR_MSG, errmsg);
				            conn.setInUse(false);
				            replyconn.setInUse(false);
							return output;
						}
					}
        	        //output.put("conditionCode", "" + ((MQException)mqe.getLinkedException()).completionCode);
            	    //output.put("reasonCode", "" + ((MQException)mqe.getLinkedException()).reasonCode);
                	//return output;
            	}
            } while ((dlq != null) && (reasonCode != 0) && (threshold > 0));
			
			if (reasonCode != 0)
			{
				//If the Request could not be sent, throw exception to outer catch block
				if (WmMQAdapterUtils.fatalException(reasonCode))
					throw new wmMQException("1050", "" + MQConstants.MQCC_FAILED, new Object[] {"" + reasonCode});
					
				Object[] parms = {"" + MQConstants.MQCC_FAILED, "" + reasonCode};
				String errmsg = wmMQException.format("1050", parms);
				output.put("reasonCode", "" + reasonCode);
				output.put("conditionCode", "" + MQConstants.MQCC_FAILED);
				output.put(wmMQAdapterConstant.ERROR_MSG, errmsg);
	            conn.setInUse(false);
	            replyconn.setInUse(false);
				return output;
			}

			header = new Values();
			msg.copyMsgFieldsToMQMD(header, _putFields, _usePutFields);

			msg = new wmMQMessage();

			//Trax 1-TDZIY
			//Pre-set the messageSequenceNumber to 0 so that the code can
			//determine if the user wants to match on sequence number.
			msg.getMQMessage().messageSequenceNumber = 0;

			//Trax 1-TDZIY
			//Pre-set the offset to -1 so that the code can
			//determine if the user wants to match on offset = 0.
			msg.getMQMessage().offset = -1;

			//Move the msgId to the correlId
			header.renameKey(wmMQAdapterConstant.MQMD_MESSAGE_ID_BYTE_ARRAY,
							 wmMQAdapterConstant.MQMD_CORRELATION_ID_BYTE_ARRAY);

			msg.copyMQMDFieldsToMsg(header, _getFields, _useGetFields);

            header = new Values();
            
            if(wmMQAdapter.isInterospectHeaderDataTypes()){
            	HashMap<String,String> headerDataTypes = WmMQAdapterUtils.findFieldDataTypes(getOutSignature());
                msg.setSplHeaderDataTypes(headerDataTypes);
            }
            
            
            //msg.copyMsgFieldsToMQMD(header, _outputFieldNames, _mqmd.getUseOutputMQMDFields());
            //Required by the Upgrade utility
			msg.copyMsgFieldsToMQMD(header, _outputFieldNames);
            //msg.copyMsgFieldsToMQMD(header, fields);

			//Trax log 1-R60VH. Allow user to override CCSID
			String replytoccsid = _overrideReplyToConnection.getPropertyFromInput(input, wmMQOverrideConnection.QUEUE_CCSID_LABEL );
			if ((replytoccsid == null) || (replytoccsid.trim().equals("")))
				replytoccsid = ((wmMQConnectionFactory)conn.getFactory()).getCCSID();
			msg.getMQMessage().characterSet = WmMQAdapterUtils.getCharacterSetId(replytoccsid);

            // Allow the user to override the Encoding
			String replytoencoding = _overrideReplyToConnection.getPropertyFromInput(input, wmMQOverrideConnection.QUEUE_ENCODING_LABEL);
			if (replytoencoding == null || replytoencoding.trim().equals(""))
				replytoencoding = ((wmMQConnectionFactory)conn.getFactory()).getEncoding();
			if(replytoencoding.length() > 5) {
				replytoencoding = replytoencoding.substring(0, 5);
			}
			msg.getMQMessage().encoding = Integer.parseInt(replytoencoding);
			
			try
			{
				log(ARTLogger.INFO, 1003, "Receiving Reply message", "");
                
        		int waitInterval = getWaitInterval();
				try {
					waitInterval = input.getInt(wmMQAdapterConstant.WAIT_INTERVAL);
				} catch (AdapterException e) {
					// Take the default value provided at the design time
				}
				
    		//	reasonCode = replyconn.get(msg, _sharedMode, waitInterval, getConvertDataOption(), replyToQueue);
				reasonCode = replyconn.get(msg, _sharedMode, waitInterval, getConvertDataOption());
				log(ARTLogger.INFO, 1003, "Received Reply message", "");
			}
			catch (wmMQException mqe)
			{
				reasonCode = ((MQException)mqe.getLinkedException()).reasonCode;
				//mqe.printStackTrace();
				log(ARTLogger.ERROR,
					1055,
					"Getting Reply messsage",
					mqe.getMessage());
	            conn.setInUse(false);
	            replyconn.setInUse(false);
				if (getThrowExceptionOnFailure())
				{
					if (WmMQAdapterUtils.fatalException(((MQException)mqe.getLinkedException()).reasonCode))
						throw wmMQAdapter.getInstance().createAdapterConnectionException(1055, 
																						 new String[]{"RequestReply reply message", mqe.getMessage()}, 
																						 mqe);
					else
						throw mqe;
				}
				Object[] parms = {"" + MQConstants.MQCC_FAILED, "" + reasonCode};
				String errmsg = wmMQException.format("1050", parms);
				output.put(wmMQAdapterConstant.ERROR_MSG, errmsg);
				output.put("conditionCode", "" + ((MQException)mqe.getLinkedException()).completionCode);
				output.put("reasonCode", "" + reasonCode);
	            conn.setInUse(false);
	            replyconn.setInUse(false);
				return output;
			}

			header = new Values();
			//msg.copyMsgFieldsToMQMD(header, _outputFieldNames, _mqmd.getUseOutputMQMDFields());
			msg.copyMsgFieldsToMQMD(header, _outputFieldNames);

			if (dlq != null)
			{
				int backoutcount = msg.getMQMessage().backoutCount;
				if (backoutcount == getBackoutThreshhold())
				{
            		String headersToIncludeInDeadLetterMessage = getDeadLetterMessageHeaders();
                    idc = input.getCursor();
        			if ((idc != null) && (idc.first(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS))) {
        				headersToIncludeInDeadLetterMessage = (String)idc.getValue();
        				if(headersToIncludeInDeadLetterMessage == null || headersToIncludeInDeadLetterMessage.trim().equals("")) {
                    		headersToIncludeInDeadLetterMessage = getDeadLetterMessageHeaders();
        				}
        				else {
            				headersToIncludeInDeadLetterMessage = headersToIncludeInDeadLetterMessage.trim();
        				}
        			}
        			idc.destroy();
					conn.moveMsgToDLQ(msg, 
									  dlq,
									  dlqQmgr,
									  "requestReply - Reply msg",
									  true,
									  headersToIncludeInDeadLetterMessage);
					Object[] parms = {"requestReply - Request msg", dlq};
					String errmsg = wmMQException.format("3040", parms);
					output.put(wmMQAdapterConstant.ERROR_MSG, errmsg);
		            conn.setInUse(false);
		            replyconn.setInUse(false);
					return output;
				}
			}

            wmMQAdapter.dumpValues("Received MQMD", header);

//            boolean[] use = _mqmd.getUseOutputMQMDFields();

            WmRecord mqmd = WmRecordFactory.getFactory().createWmRecord("msgHeader");
            //Required for Upgrade utility
            for (int i = 0 ; i < _outputFieldNames.length ; i++)
            //int ofnlen = _outputFieldNames.length;
            //if (ofnlen > wmMQMQMD.mqmdFieldDisplayNames.length)
            //	ofnlen = wmMQMQMD.mqmdFieldDisplayNames.length;
            //for (int i = 0 ; i < ofnlen ; i++)
            {
//                if (use[i])
                {
                    log(ARTLogger.INFO, 1003, "Returning field", _outputFieldNames[i]);
                    Object o = header.get(_outputFieldNames[i].substring(10));
                    mqmd.put(_outputFieldNames[i].substring(10), o);
                }
            }
            output.put("msgHeader", mqmd);

            output.put("reasonCode", "" + reasonCode);

            //If the 'get' completes with a warning, pass the reason code to the caller
            if (reasonCode > 0)
			{
				log(ARTLogger.ERROR,
					1050,
					"" + reasonCode,
					"" + MQConstants.MQCC_WARNING);
                output.put("conditionCode", "" + MQConstants.MQCC_WARNING);
			}

			/*
			 * Introduced this below watt properties for IMQ-1020. The issue is
			 * because of usage of Invalid CCSID due to which conversion to
			 * String is taking around 600 msec as charset.isSupported java api
			 * is taking time on jvm 1.6.
			 * 
			 * watt.WmMQAdapter.RequestReplyService.ignoreReplyJMSHeader is
			 * introduced to skip the conversion To String process in MQ libs
			 * while for reading JMS Headers
			 * 
			 * watt.MQAdapter.RequestReplyService.skipReplyMsgBodyToStringConversion
			 * is introduced to skip Message Body conversion To String.
			 */
			//Not interested in JMS properties skip it.
            String skipJMSHeaderParsing = Config.getProperty("watt.WmMQAdapter.RequestReplyService.ignoreReplyJMSHeader");
			if (!Boolean.valueOf(skipJMSHeaderParsing).booleanValue()){
                     copyJMSPropertiesFromMsg(output, msg);
			}   
			
			
			//Dont Convert Reply Msg Body To String.
			String skipToStringConversion = Config.getProperty("watt.MQAdapter.RequestReplyService.skipReplyMsgBodyToStringConversion");
			msg.setConvertMsgBodyToString(!Boolean.valueOf(skipToStringConversion).booleanValue());

			//Make sure that the msgBody is put into pipeline as an Object
			Object body = msg.getMsgBody();
			output.put(_outputMsgBodyName, body);
            output.put(_outputMsgBodyByteArrayName, msg.getMsgBodyByteArray());

            if(!useExistingConn) {
            	_overrideConnection.fillOverriddenConnection(conn, output, false);
            }
            else {
            	_overrideConnection.fillOverriddenConnection(conn, output, queueManagerName, queueName);
            }
            conn.setInUse(false);
			_overrideReplyToConnection.fillOverriddenConnection(replyconn, output, true);
			replyconn.setInUse(false);

            return output;
        }
        catch (Exception t)
        {
            //createAdapterException throws an AdapterException
            //using the Major Code and ResourceBundle already
            //associated with this adapter and a wrapped Throwable exception.
            //The minorCode parameter is an int representing the
            //Minor Code for this Exception.
            //The throwable parameter is a throwable object representing
            //the underlying error condition.
            //AdapterException issues the error message
            //"Put: lookup execution failed."
            //Refer to the Javadoc of the WmAdapter and
            //AdapterException classes for more details.
            //t.printStackTrace();
            log(ARTLogger.ERROR,
                1055,
                "During RequestReply",
                t.getMessage());
            if (conn != null)
            	conn.setInUse(false);
            if (replyconn != null)
            	replyconn.setInUse(false);

			if (getThrowExceptionOnFailure())
			{
				if (t instanceof AdapterConnectionException)
					throw (AdapterConnectionException)t;
				else
				// Trax 1-1UQV4K [ RequestReply service continues to hang onto a stale connection after fatal error ]
				{
					if(t instanceof wmMQException) {
						// Check if the 'fatal' flag needs to be set for the exception.
						if (WmMQAdapterUtils.fatalException(((MQException)((wmMQException)t).getLinkedException()).reasonCode)) {
							AdapterConnectionException ex = wmMQAdapter.getInstance().createAdapterConnectionException(1055, 
									 new String[]{"RequestReply reply message", t.getMessage()}, 
									 t);
							ex.setFatal(true);
							throw ex;
						}
					}
					
					throw wmMQAdapter.getInstance().createAdapterException(1055, 
																		   new String[]{"RequestReply", t.getMessage()}, 
																		   t);
				}
				//! Trax 1-1UQV4K
			}
			
			if (t instanceof wmMQException)
			{
				int reasonCode = ((MQException)((wmMQException)t).getLinkedException()).reasonCode;
				output.put("reasonCode", "" + reasonCode);
				output.put("conditionCode", "" + MQConstants.MQCC_FAILED);
			}

			Object[] parms = { "RequestReply", t.getMessage() };
			String errormsg = wmMQException.format("1055", parms);
			output.put(wmMQAdapterConstant.ERROR_MSG, errormsg);
			return output;
        }
    }

	private static final String[] _putFields = new String[] {
		/*"msgHeader." + */wmMQAdapterConstant.MQMD_MESSAGE_ID_BYTE_ARRAY,
		/*"msgHeader." + */wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_MANAGER_NAME,
		/*"msgHeader." + */wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_NAME};

	private static final boolean[] _usePutFields = new boolean[] {true, true, true};

	private static final String[] _getFields = new String[] {
		"msgHeader." + wmMQAdapterConstant.MQMD_CORRELATION_ID_BYTE_ARRAY};

	private static final boolean[] _useGetFields = new boolean[] {true};

	private String[] _serviceGroupFieldNames = new String[] {
                    wmMQAdapterConstant.WAIT_INTERVAL,
                    wmMQAdapterConstant.WAIT_INTERVAL_TYPE,
                    wmMQAdapterConstant.WAIT_INTERVAL_NAME,
                    wmMQAdapterConstant.DEAD_LETTER_QUEUE,
                    wmMQAdapterConstant.DEAD_LETTER_QUEUE_TYPE,
                    wmMQAdapterConstant.DEAD_LETTER_QUEUE_NAME,
                    wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER,
                    wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_TYPE,
                    wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_NAME,
                    wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS,
                    wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_TYPE,
                    wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_NAME,
                    wmMQAdapterConstant.BACKOUT_THRESHHOLD,
                    wmMQAdapterConstant.SHARED_MODE,
                    wmMQAdapterConstant.CONVERT_DATA_OPTION,
                    wmMQAdapterConstant.THROW_EXCEPTION_ON_FAILURE,
                    wmMQAdapterConstant.MSG_BODY,
                    wmMQAdapterConstant.MSG_BODY_NAME,
                    wmMQAdapterConstant.MSG_BODY_TYPE,
                    
                	// Trax 1-11YH1R : Begin Added MSG_BODY_BYTE_ARRAY to the input fields
                    wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY,
                    wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_NAME,
                    wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE,
                	// Trax 1-11YH1R : End
                    
					wmMQAdapterConstant.REPLY_MSG_BODY,
					wmMQAdapterConstant.REPLY_MSG_BODY_NAME,
					wmMQAdapterConstant.REPLY_MSG_BODY_TYPE,
					wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY,
					wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY_NAME,
					wmMQAdapterConstant.REPLY_MSG_BODY_BYTE_ARRAY_TYPE
					};

}
