/*
 * wmMQBaseService.java
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

import java.util.Enumeration;
import java.util.Locale;

import javax.resource.ResourceException;

import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adapter.wmmqadapter.connection.wmMQConnection;
import com.wm.adapter.wmmqadapter.connection.wmMQException;
import com.wm.adapter.wmmqadapter.connection.wmMQMessage;
import com.wm.adapter.wmmqadapter.connection.wmMQOverrideConnection;
import com.wm.adk.cci.interaction.WmAdapterService;
import com.wm.adk.cci.record.WmRecord;
import com.wm.adk.cci.record.WmRecordFactory;
import com.wm.adk.connection.WmManagedConnection;
import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;
import com.wm.adk.metadata.WmTemplateDescriptor;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.ValuesEmulator;
import com.wm.lang.ns.NSRecord;
import com.wm.pkg.art.ns.AdapterServiceNode;
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
public abstract class wmMQBaseService extends WmAdapterService
{
    //WaitInterval property
    private int _waitInterval = 60000;

    private String _waitIntervalName = wmMQAdapterConstant.WAIT_INTERVAL;

    private String _waitIntervalType = "java.lang.Integer";
    
    //
    private boolean _throwExceptionOnFailure = true;

    //
    private String _deadLetterQueue = "";

    //
    private String _deadLetterQueueName = wmMQAdapterConstant.DEAD_LETTER_QUEUE_NAME;

    //
    private String[] _deadLetterQueueType = {"java.lang.String"};

    //
    private String _deadLetterQueueManager = "";

    //
    private String _deadLetterQueueManagerName = wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_NAME;

    //
    private String[] _deadLetterQueueManagerType = {"java.lang.String"};

    //
    private String _deadLetterMessageHeaders = wmMQAdapter.getDeadLetterMessageHeaders();

    //
    private String _deadLetterMessageHeadersName = wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS;

    //
    private String[] _deadLetterMessageHeadersType = {"java.lang.String"};
    
    //
    private int _backoutThreshhold = 1;

    //Message body
    private Object _msgBody = null;

    //Message body name
    private String _msgBodyName = "msgBody";

	// Trax 1-11YH1R : Begin
    //Message body field type
    private String[] _msgBodyType = {"java.lang.Object"};
	// Trax 1-11YH1R : End

    //Message body
    private Object _msgBodyByteArray = null;

    //Message body name
    private String _msgBodyByteArrayName = "msgBodyByteArray";

	// Trax 1-11YH1R : Begin
    //Message body field type
    private String[] _msgBodyByteArrayType = {"java.lang.Object"};
	// Trax 1-11YH1R : End

    //Tab for MQMD
    protected wmMQMQMD _mqmd = null;

    //The list of output field names.
    protected String[] _inputFieldNames;

    //The list of output field names.
    protected String[] _outputFieldNames;

    //The list of output field names.
    private String[] _jmsPropertyNames;

    //JMS Properties
    private String[] _JMSProperties;

    //JMS Property types
    private String[] _JMSPropertyTypes;

    //Use JMS Properties
    private boolean[] _useJMSProperties;
    
    //User defined folders and properties
    private String[] _userDefinedProperties;
    private String[] _userDefinedPropertyNames;
    private String[] _userDefinedPropertyTypes;
    private String[] _realUserDefinedPropertyNames;
    
    //Service name of subclasses
    protected String _serviceName = "";

	//
	private String _reasonCode = "0";
	private String _reasonCodeName = wmMQAdapterConstant.REASON_CODE;
	private String[] _reasonCodeType = {"java.lang.String"};

	//
	private String _conditionCode = "0";
	private String _conditionCodeName = wmMQAdapterConstant.CONDITION_CODE;
	private String[] _conditionCodeType = {"java.lang.String"};

	//
	private String _errorMsg = "";
	private String _errorMsgName = wmMQAdapterConstant.ERROR_MSG;
	private String[] _errorMsgType = {"java.lang.String"};

	protected wmMQOverrideConnection _overrideConnection = null;

    /*
     * Constructor.
     */
    public wmMQBaseService()
    {
        _mqmd = new wmMQMQMD();
    }

    /*
     * Sets the waitInterval property
     *
     * The interval parameter is the waitInterval property.
     */
    public void setWaitInterval(int interval)
    {
        this._waitInterval = interval;
    }

    /*
     * Gets the waitInterval property.
     *
     */
    public int getWaitInterval()
    {
        return _waitInterval;
    }

	/*
	 * Sets the waitInterval property name. 
	 *
	 * The waitIntervalName parameter is the property name.
	 *
	 */
	public void setWaitIntervalName(String waitIntervalName)
	{
		_waitIntervalName = waitIntervalName;
	}
    
	/*
	 * Gets the waitInterval property name. 
	 *
	 */
	public String getWaitIntervalName()
	{
		return _waitIntervalName;
	}
	
	/*
	 * Sets the waitInterval property type. 
	 *
	 * The waitIntervalName parameter is the list of property types.
	 *
	 */
	public void setWaitIntervalType(String waitIntervalType)
	{
		_waitIntervalType = waitIntervalType;
	}
    
	/*
	 * Gets the waitInterval property type. 
	 *
	 */
	public String getWaitIntervalType()
	{
		return _waitIntervalType;
	}

    /*
     * Sets the 'throwExceptionOnFailure' property
     *
     * The sync parameter is the syncpointProcessing property.
     */
    public void setThrowExceptionOnFailure(boolean excp)
    {
        this._throwExceptionOnFailure = excp;
    }

    /*
     * Gets the 'throwExceptionOnFailure' property.
     *
     */
    public boolean getThrowExceptionOnFailure()
    {
        return _throwExceptionOnFailure;
    }

    /*
     * Sets the deadLetterQueue property.
     *
     * The dlq parameter is the deadLetterQueue property.
     */
    public void setDeadLetterQueue(String dlq)
    {
        this._deadLetterQueue = dlq;
    }

    /*
     * Gets the deadLetterQueue.
     *
     */
    public String getDeadLetterQueue()
    {
        return _deadLetterQueue;
    }

    /*
     * Sets the deadLetterQueueName property.
     *
     * The names parameter is the deadLetterQueueName property.
     */
    public void setDeadLetterQueueName(String name)
    {
        this._deadLetterQueueName = name;
    }

    /*
     * Gets the deadLetterQueueName.
     *
     */
    public String getDeadLetterQueueName()
    {
        return _deadLetterQueueName;
    }

    /*
     * Sets the deadLetterQueueType property.
     *
     * The type parameter is the deadLetterQueueType property.
     */
    public void setDeadLetterQueueType(String[] type)
    {
        this._deadLetterQueueType = type;//NOSONAR
    }

    /*
     * Gets the deadLetterQueueType.
     *
     */
    public String[] getDeadLetterQueueType()
    {
        return _deadLetterQueueType;//NOSONAR
    }

    /*
     * Sets the deadLetterQueue property.
     *
     * The dlq parameter is the deadLetterQueue property.
     */
    public void setDeadLetterQueueManager(String dlqmgr)
    {
        this._deadLetterQueueManager = dlqmgr;
    }

    /*
     * Gets the deadLetterQueue.
     *
     */
    public String getDeadLetterQueueManager()
    {
        return _deadLetterQueueManager;
    }

    /*
     * Sets the deadLetterQueueManagerName property.
     *
     * The names parameter is the deadLetterQueue property.
     */
    public void setDeadLetterQueueManagerName(String name)
    {
        this._deadLetterQueueManagerName = name;
    }

    /*
     * Gets the deadLetterQueueManagerName.
     *
     */
    public String getDeadLetterQueueManagerName()
    {
        return _deadLetterQueueManagerName;
    }

    /*
     * Sets the deadLetterQueueManagerType property.
     *
     * The type parameter is the deadLetterQueueManagerType property.
     */
    public void setDeadLetterQueueManagerType(String[] type)
    {
        this._deadLetterQueueManagerType = type;//NOSONAR
    }

    /*
     * Gets the deadLetterQueueManagerType.
     *
     */
    public String[] getDeadLetterQueueManagerType()
    {
        return _deadLetterQueueManagerType;//NOSONAR
    }

    /*
     * Sets the _deadLetterMessageHeaders property.
     *
     * The deadLetterMessageHeaders parameter is the _deadLetterMessageHeaders property.
     */
    public void setDeadLetterMessageHeaders(String deadLetterMessageHeaders) 
    {
        this._deadLetterMessageHeaders = deadLetterMessageHeaders;
    }

    /*
     * Gets the _deadLetterMessageHeaders.
     *
     */
    public String getDeadLetterMessageHeaders()
    {
        return _deadLetterMessageHeaders;
    }
    
    /*
     * Sets the deadLetterMessageHeadersType property.
     *
     * The type parameter is the deadLetterMessageHeadersType property.
     */
    public void setDeadLetterMessageHeadersType(String[] type)
    {
        this._deadLetterMessageHeadersType = type;//NOSONAR
    }

    /*
     * Gets the deadLetterMessageHeadersType.
     *
     */
    public String[] getDeadLetterMessageHeadersType()
    {
        return _deadLetterMessageHeadersType;//NOSONAR
    }

    /*
     * Sets the deadLetterMessageHeadersName property.
     *
     * The type parameter is the deadLetterMessageHeadersName property.
     */
    public void setDeadLetterMessageHeadersName(String name)
    {
        this._deadLetterMessageHeadersName = name;
    }

    /*
     * Gets the deadLetterMessageHeadersName.
     *
     */
    public String getDeadLetterMessageHeadersName()
    {
        return _deadLetterMessageHeadersName;
    }
    
    /*
     * Sets the backoutThreshhold property.
     *
     * The count parameter is the backoutThreshhold property.
     */
    public void setBackoutThreshhold(int count)
    {
        this._backoutThreshhold = count;
    }

    /*
     * Gets the backoutThreshhold property.
     *
     */
    public int getBackoutThreshhold()
    {
        return _backoutThreshhold;
    }

	/*
	 * Gets the input field names.
	 *
	 */
	public String[] getInputFieldNames()
	{
		return _inputFieldNames;
	}

	/*
	 * Sets the input field names.
	 *
	 * The inputFieldNames parameter is the input field names.
	 *
	 */
	public void setInputFieldNames(String[] inputFieldNames)
	{
		_inputFieldNames = inputFieldNames;
	}

    /*
     * Gets the message body.
     *
     */
    public Object getMsgBody()
    {
        return _msgBody;
    }

    /*
     * Sets the message body.
     *
     * The msgbody parameter is the Message body property.
     *
     */
    public void setMsgBody(Object msgbody)
    {
        _msgBody = msgbody;
    }

    /*
     * Gets the message body.
     *
     */
    public Object getMsgBodyByteArray()
    {
        return _msgBodyByteArray;
    }

    /*
     * Sets the message body.
     *
     * The msgbodyByteArray parameter is the Message body property.
     *
     */
    public void setMsgBodyByteArray(Object msgbodyByteArray)
    {
        _msgBodyByteArray = msgbodyByteArray;
    }

	public void setMqmdInputFields(String[] fields)
	{
		_mqmd.setMqmdInputFields(fields);
	}

	public String[] getMqmdInputFields()
	{
		return _mqmd.getMqmdInputFields();
	}

    public void setMqmdInputConstants(String[] constants)
    {
        _mqmd.setMqmdInputConstants(constants);
    }

    public void setUseMQMDFields(boolean[] use)
    {
        _mqmd.setUseMQMDFields(use);
    }

    public void setMqmdInputFieldTypes(String[] types)
    {
        _mqmd.setMqmdInputFieldTypes(types);
    }

    public void setMqmdOutputFields(String[] fields)
    {
        _mqmd.setMqmdOutputFields(fields);
    }

    public void setUseOutputMQMDFields(boolean[] use)
    {
        _mqmd.setUseOutputMQMDFields(use);
    }

    public void setMqmdOutputFieldTypes(String[] types)
    {
        _mqmd.setMqmdOutputFieldTypes(types);
    }

    public String[] getMqmdInputConstants()
    {
        return _mqmd.getMqmdInputConstants();
    }

    public boolean[] getUseMQMDFields()
    {
        return _mqmd.getUseMQMDFields();
    }
    /*
     * Gets the message body.
     *
     */
    public String getMsgBodyName()
    {
        return _msgBodyName;
    }

    /*
     * Sets the message body.
     *
     * The msgbody parameter is the Message body property.
     *
     */
    public void setMsgBodyName(String msgbodyname)
    {
        _msgBodyName = msgbodyname;
    }

    /*
     * Gets the message body.
     *
     */
    public String[] getMsgBodyType()
    {
        return _msgBodyType;//NOSONAR
    }

    /*
     * Sets the message body.
     *
     * The msgbody parameter is the Message body property.
     *
     */
    public void setMsgBodyType(String[] msgbodytype)
    {
        _msgBodyType = msgbodytype;//NOSONAR
    }
    /*
     * Gets the message body.
     *
     */
    public String getMsgBodyByteArrayName()
    {
        return _msgBodyByteArrayName;
    }

    /*
     * Sets the message body.
     *
     * The msgbody parameter is the Message body property.
     *
     */
    public void setMsgBodyByteArrayName(String msgbodyByteArrayname)
    {
        _msgBodyByteArrayName = msgbodyByteArrayname;
    }

    /*
     * Gets the message body.
     *
     */
    public String[] getMsgBodyByteArrayType()
    {
        return _msgBodyByteArrayType;//NOSONAR
    }

    /*
     * Sets the message body.
     *
     * The msgbody parameter is the Message body property.
     *
     */
    public void setMsgBodyByteArrayType(String[] msgbodyByteArraytype)
    {
        _msgBodyByteArrayType = msgbodyByteArraytype;//NOSONAR
    }

    public String[] getMqmdInputFieldTypes()
    {
        return _mqmd.getMqmdInputFieldTypes();
    }

    public String[] getMqmdOutputFields()
    {
        return _mqmd.getMqmdOutputFields();
    }

    public boolean[] getUseOutputMQMDFields()
    {
        return _mqmd.getUseOutputMQMDFields();
    }

    public String[] getMqmdOutputFieldTypes()
    {
        return _mqmd.getMqmdOutputFieldTypes();
    }

    /*
     * Gets the JMS property names.
     *
     */
    public String[] getJmsPropertyNames()
    {
        return _jmsPropertyNames;//NOSONAR
    }

    /*
     * Sets the JMS property names.
     *
     * The jmsPropertyNames parameter is the JMS property names.
     *
     */
    public void setJmsPropertyNames(String[] jmsPropertyNames)
    {
        _jmsPropertyNames = jmsPropertyNames;//NOSONAR
    }

    /*
     * Gets the input field names.
     *
     */
    public String[] getOutputFieldNames()
    {
        return _outputFieldNames;
    }

    /*
     * Sets the output field names.
     *
     * The inputFieldNames parameter is the input field names.
     *
     */
    public void setOutputFieldNames(String[] outputFieldNames)
    {
        _outputFieldNames = outputFieldNames;
    }

    public String[] getJmsProperties()
    {
        return _JMSProperties;//NOSONAR
    }

    public void setJmsProperties(String[] jmsproperties)
    {
        _JMSProperties = jmsproperties;//NOSONAR
    }

    public String[] getJmsPropertyTypes()
    {
        return _JMSPropertyTypes;//NOSONAR
    }

    public void setJmsPropertyTypes(String[] types)
    {
        _JMSPropertyTypes = types;//NOSONAR
    }

    public boolean[] getUseJmsProperties()
    {
        return _useJMSProperties;//NOSONAR
    }

    public void setUseJmsProperties(boolean[] use)
    {
        _useJMSProperties = use;//NOSONAR
    }

	public String[] getUserDefinedProperties()
	{
		return _userDefinedProperties;//NOSONAR
	}

	public void setUserDefinedProperties(String[] userDefinedProperties)
	{
		_userDefinedProperties = userDefinedProperties;//NOSONAR
	}
	
	public String[] getUserDefinedPropertyNames()
	{
		return _userDefinedPropertyNames;//NOSONAR
	}

	public void setUserDefinedPropertyNames(String[] userDefinedPropertyNames)
	{
		_userDefinedPropertyNames = userDefinedPropertyNames;//NOSONAR
	}

	public String[] getRealUserDefinedPropertyNames()
	{
		return _realUserDefinedPropertyNames;//NOSONAR
	}

	public void setRealUserDefinedPropertyNames(String[] realUserDefinedPropertyNames)
	{
		_realUserDefinedPropertyNames = realUserDefinedPropertyNames;//NOSONAR
	}

	public String[] getUserDefinedPropertyTypes()
	{
		return _userDefinedPropertyTypes;//NOSONAR
	}

	public void setUserDefinedPropertyTypes(String[] userDefinedPropertyTypes)
	{
		_userDefinedPropertyTypes = userDefinedPropertyTypes;//NOSONAR
	}

	public String getReasonCode()
	{
		return _reasonCode;
	}

	public void setReasonCode(String code)
	{
		_reasonCode = code;
	}

	public String[] getReasonCodeType()
	{
		return _reasonCodeType;//NOSONAR
	}

	public void setReasonCodeType(String[] names)
	{
		_reasonCodeType = names;//NOSONAR
	}

	public String getReasonCodeName()
	{
		return _reasonCodeName;
	}

	public void setReasonCodeName(String name)
	{
		_reasonCodeName = name;
	}

	public String getConditionCode()
	{
		return _conditionCode;
	}

	public void setConditionCode(String code)
	{
		_conditionCode = code;
	}

	public String[] getConditionCodeType()
	{
		return _conditionCodeType;//NOSONAR
	}

	public void setConditionCodeType(String[] names)
	{
		_conditionCodeType = names;//NOSONAR
	}

	public String getConditionCodeName()
	{
		return _conditionCodeName;
	}

	public void setConditionCodeName(String name)
	{
		_conditionCodeName = name;
	}

	public String getErrorMsg()
	{
		return _errorMsg;
	}

	public void setErrorMsg(String code)
	{
		_errorMsg = code;
	}

	public String[] getErrorMsgType()
	{
		return _errorMsgType;//NOSONAR
	}

	public void setErrorMsgType(String[] names)
	{
		_errorMsgType = names;//NOSONAR
	}

	public String getErrorMsgName()
	{
		return _errorMsgName;
	}

	public void setErrorMsgName(String name)
	{
		_errorMsgName = name;
	}

	public String[] getOverrideConnectionPropertyType()
	{
		return _overrideConnection.getOverrideConnectionPropertyType();
	}

	public void setOverrideConnectionPropertyType(String[] overrideConnectionPropertyType)
	{
		_overrideConnection.setOverrideConnectionPropertyType(overrideConnectionPropertyType);
	}

	public String getOverrideConnectionQueueManagerName()
	{
		return _overrideConnection.getOverrideConnectionQueueManagerName();
	}

	public void setOverrideConnectionQueueManagerName(String overrideConnectionQueueManagerName)
	{
		_overrideConnection.setOverrideConnectionQueueManagerName(overrideConnectionQueueManagerName);
	}

	public String getOverrideConnectionHostName()
	{
		return _overrideConnection.getOverrideConnectionHostName();
	}

	public void setOverrideConnectionHostName(String overrideConnectionHostName)
	{
		_overrideConnection.setOverrideConnectionHostName(overrideConnectionHostName);
	}

	public String getOverrideConnectionPort()
	{
		return _overrideConnection.getOverrideConnectionPort();
	}

	public void setOverrideConnectionPort(String overrideConnectionPort)
	{
		_overrideConnection.setOverrideConnectionPort(overrideConnectionPort);
	}

	public String getOverrideConnectionChannel()
	{
		return _overrideConnection.getOverrideConnectionChannel();
	}

	public void setOverrideConnectionChannel(String overrideConnectionChannel)
	{
		_overrideConnection.setOverrideConnectionChannel(overrideConnectionChannel);
	}

	public String getOverrideConnectionCCSID()
	{
		return _overrideConnection.getOverrideConnectionCCSID();
	}

	public void setOverrideConnectionCCSID(String overrideConnectionCCSID)
	{
		_overrideConnection.setOverrideConnectionCCSID(overrideConnectionCCSID);
	}

	public String getOverrideConnectionEncoding()
	{
		return _overrideConnection.getOverriddenConnectionEncoding();
	}

	public void setOverrideConnectionEncoding(String overrideConnectionEncoding)
	{
		_overrideConnection.setOverriddenConnectionEncoding(overrideConnectionEncoding);
	}

	public String getOverrideConnectionQueueName()
	{
		return _overrideConnection.getOverrideConnectionQueueName();
	}

	public void setOverrideConnectionQueueName(String overrideConnectionQueueName)
	{
		_overrideConnection.setOverrideConnectionQueueName(overrideConnectionQueueName);
	}

	public String getOverrideConnectionDynamicQueuePrefix()
	{
		return _overrideConnection.getOverrideConnectionDynamicQueuePrefix();
	}

	public void setOverrideConnectionDynamicQueuePrefix(String overrideConnectionDynamicQueuePrefix)
	{
		_overrideConnection.setOverrideConnectionDynamicQueuePrefix(overrideConnectionDynamicQueuePrefix);
	}
/*
	public String getOverrideConnectionUserid()
	{
		return _overrideConnection.getOverrideConnectionUserid();
	}

	public void setOverrideConnectionUserid(String overrideConnectionUserid)
	{
		_overrideConnection.setOverrideConnectionUserid(overrideConnectionUserid);
	}

	public String getOverrideConnectionPassword()
	{
		return _overrideConnection.getOverrideConnectionPassword();
	}

	public void setOverrideConnectionPassword(String overrideConnectionPassword)
	{
		_overrideConnection.setOverrideConnectionPassword(overrideConnectionPassword);
	}
*/
	public String getOverriddenConnectionQueueManagerName()
	{
		return _overrideConnection.getOverriddenConnectionQueueManagerName();
	}

	public void setOverriddenConnectionQueueManagerName(String overriddenConnectionQueueManagerName)
	{
		_overrideConnection.setOverriddenConnectionQueueManagerName(overriddenConnectionQueueManagerName);
	}

	public String getOverriddenConnectionHostName()
	{
		return _overrideConnection.getOverriddenConnectionHostName();
	}

	public void setOverriddenConnectionHostName(String overriddenConnectionHostName)
	{
		_overrideConnection.setOverriddenConnectionHostName(overriddenConnectionHostName);
	}

	public String getOverriddenConnectionPort()
	{
		return _overrideConnection.getOverriddenConnectionPort();
	}

	public void setOverriddenConnectionPort(String overriddenConnectionPort)
	{
		_overrideConnection.setOverriddenConnectionPort(overriddenConnectionPort);
	}

	public String getOverriddenConnectionChannel()
	{
		return _overrideConnection.getOverriddenConnectionChannel();
	}

	public void setOverriddenConnectionChannel(String overriddenConnectionChannel)
	{
		_overrideConnection.setOverriddenConnectionChannel(overriddenConnectionChannel);
	}

	public String getOverriddenConnectionCCSID()
	{
		return _overrideConnection.getOverriddenConnectionCCSID();
	}

	public void setOverriddenConnectionCCSID(String overriddenConnectionCCSID)
	{
		_overrideConnection.setOverriddenConnectionCCSID(overriddenConnectionCCSID);
	}

	public String getOverriddenConnectionEncoding()
	{
		return _overrideConnection.getOverriddenConnectionEncoding();
	}

	public void setOverriddenConnectionEncoding(String overriddenConnectionEncoding)
	{
		_overrideConnection.setOverriddenConnectionEncoding(overriddenConnectionEncoding);
	}

	public String getOverriddenConnectionQueueName()
	{
		return _overrideConnection.getOverriddenConnectionQueueName();
	}

	public void setOverriddenConnectionQueueName(String overriddenConnectionQueueName)
	{
		_overrideConnection.setOverriddenConnectionQueueName(overriddenConnectionQueueName);
	}

	public String getOverriddenConnectionDynamicQueuePrefix()
	{
		return _overrideConnection.getOverriddenConnectionDynamicQueuePrefix();
	}

	public void setOverriddenConnectionDynamicQueuePrefix(String overriddenConnectionDynamicQueuePrefix)
	{
		_overrideConnection.setOverriddenConnectionDynamicQueuePrefix(overriddenConnectionDynamicQueuePrefix);
	}
/*
	public String getOverriddenConnectionUserid()
	{
		return _overrideConnection.getOverriddenConnectionUserid();
	}

	public void setOverriddenConnectionUserid(String overriddenConnectionUserid)
	{
		_overrideConnection.setOverriddenConnectionUserid(overriddenConnectionUserid);
	}

	public String getOverriddenConnectionPassword()
	{
		return _overrideConnection.getOverriddenConnectionPassword();
	}

	public void setOverriddenConnectionPassword(String overriddenConnectionPassword)
	{
		_overrideConnection.setOverriddenConnectionPassword(overriddenConnectionPassword);
	}
*/
    /*
     * Perform service - This method must be implemented in any subclass
     * connection is the connection handle.
     * input is the input record of the adapter service.
     */
    public abstract WmRecord execute(WmManagedConnection connection, WmRecord input) throws ResourceException;

    /*
     * This method populates the metadata object describing
     * this service template in the specified locale.
     * This method will be called once for each service template.
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
		log(ARTLogger.INFO, 1001, "fillWmTemplateDescriptor", "");
		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "service=" + _serviceName);
        //Organizes and sorts all the parameters in one group.
        //To localize the group name, add the local message in the
        //resource bundle with the following key:
        //{"Lookup0" + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP,""}.
        //Refer to the ADKGLOBAL Javadoc for more information.

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating _serviceGroupFieldNames,service=" + _serviceName);
		//First tab
        d.createGroup(_serviceName, _serviceGroupFieldNames);

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating _mqmdGroupFieldNames");
		//Second tab
		if (_serviceName.equals(wmMQAdapterConstant.REQUEST_REPLY_SERVICE))
			d.createGroup(_serviceName + wmMQAdapterConstant.MQMD, _requestReplyMqmdGroupFieldNames);
		else
			if (_serviceName.equals(wmMQAdapterConstant.PUT_SERVICE))
				d.createGroup(_serviceName + wmMQAdapterConstant.MQMD, _putMqmdGroupFieldNames);
			else
				d.createGroup(_serviceName + wmMQAdapterConstant.MQMD, _mqmdGroupFieldNames);

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating _jmsPropertyFieldNames");
		//Third tab
        d.createGroup(_serviceName + wmMQAdapterConstant.JMS, _jmsPropertyFieldNames);
		d.setGroupName(wmMQAdapterConstant.USER_DEFINED_PROPERTIES, _serviceName + wmMQAdapterConstant.JMS);
		d.setGroupName(wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES, _serviceName + wmMQAdapterConstant.JMS);
		d.setGroupName(wmMQAdapterConstant.USER_DEFINED_PROPERTY_TYPES, _serviceName + wmMQAdapterConstant.JMS);
		d.setGroupName(wmMQAdapterConstant.REAL_USER_DEFINED_PROPERTY_NAMES, _serviceName + wmMQAdapterConstant.JMS);

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating _mqmdInputFieldMap");
		
		if (	_serviceName.equals(wmMQAdapterConstant.PUT_SERVICE)
			 ||	_serviceName.equals(wmMQAdapterConstant.REQUEST_REPLY_SERVICE)) {
			d.createFieldMap(_putMqmdInputFieldMap, true);
		}
		else {
			d.createFieldMap(_mqmdInputFieldMap, true);
		}

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating _mqmdOutputFieldMap");
		d.createFieldMap(new String[] { 
						 wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
						 wmMQAdapterConstant.USE_MQMD_OUTPUT_FIELDS,
						 wmMQAdapterConstant.OUTPUT_FIELD_NAMES,
						 wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES,
						 wmMQAdapterConstant.REASON_CODE,
						 wmMQAdapterConstant.CONDITION_CODE,
						 wmMQAdapterConstant.ERROR_MSG,
						 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_MANAGER_NAME,
						 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_HOST_NAME,
						 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_PORT,
						 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CHANNEL_NAME,
						 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CCSID,
						 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_ENCODING,
						 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_NAME,
						 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_DYNAMIC_QUEUE_PREFIX
//			 			 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_USERID,
//						 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_PASSWORD
						}, true);

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating _jmsPropertyFieldNames");
        d.createFieldMap(_jmsPropertyFieldNames, true);
        d.createFieldMap(new String[] {wmMQAdapterConstant.USER_DEFINED_PROPERTIES, 
        							   wmMQAdapterConstant.USER_DEFINED_PROPERTY_TYPES, 
        							   wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES, 
        							   wmMQAdapterConstant.REAL_USER_DEFINED_PROPERTY_NAMES}, 
        				 true, false);
        d.disableAppendAll(wmMQAdapterConstant.USER_DEFINED_PROPERTIES);

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating _mqmdInputTupleNames");
		d.createTuple(_mqmdInputTupleNames);

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating _mqmdOutputTupleNames");
        d.createTuple(
            new String[] { wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
                           wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES
//                           wmMQAdapterConstant.USE_MQMD_OUTPUT_FIELDS
                         });

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating _jmsTupleNames");
        d.createTuple(
            new String[] { wmMQAdapterConstant.JMS_PROPERTIES,
                           wmMQAdapterConstant.JMS_PROPERTY_TYPES
//                           wmMQAdapterConstant.USE_JMS_PROPERTIES
                         });

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating _waitIntervalTupleNames");
		d.createTuple( new String[] {wmMQAdapterConstant.WAIT_INTERVAL, 
				 					 wmMQAdapterConstant.WAIT_INTERVAL_TYPE});
		
		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating _msgbodyTupleNames");
        d.createTuple(
            new String[] { wmMQAdapterConstant.MSG_BODY,
                           wmMQAdapterConstant.MSG_BODY_TYPE
                         });

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating _msgbodyByteArrayTupleNames");
        d.createTuple(
            new String[] { wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY,
                           wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE
                         });

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating DEAD_LETTER_QUEUE tuple");
        d.createTuple(
            new String[] { wmMQAdapterConstant.DEAD_LETTER_QUEUE,
                           wmMQAdapterConstant.DEAD_LETTER_QUEUE_TYPE
                         });

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating DEAD_LETTER_QUEUE_MANAGER tuple");
        d.createTuple(
            new String[] { wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER,
                           wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_TYPE
                         });

		log(ARTLogger.INFO, 1003, "fillWmTemplateDescriptor", "Creating DEAD_LETTER_MESSAGE_HEADERS tuple");
        d.createTuple(
                new String[] { wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS,
                               wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_TYPE
                             });

		d.setHidden(wmMQAdapterConstant.WAIT_INTERVAL_TYPE, true);
		d.setHidden(wmMQAdapterConstant.WAIT_INTERVAL_NAME, true);
		
        d.setHidden(wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES, true);
        d.setHidden(wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES, true); //Don't ask
		d.setHidden(wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS, true);
		d.setHidden(wmMQAdapterConstant.USE_MQMD_OUTPUT_FIELDS, true);
        d.setHidden(wmMQAdapterConstant.OUTPUT_FIELD_NAMES, true);
        d.setHidden(wmMQAdapterConstant.JMS_PROPERTY_TYPES, true);
        d.setHidden(wmMQAdapterConstant.JMS_PROPERTY_NAMES, true);
		d.setHidden(wmMQAdapterConstant.USE_JMS_PROPERTIES, true);
		
		d.setHidden(wmMQAdapterConstant.USER_DEFINED_PROPERTY_TYPES, true);
		d.setHidden(wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES, true);
		d.setHidden(wmMQAdapterConstant.REAL_USER_DEFINED_PROPERTY_NAMES, true);

        d.setHidden(wmMQAdapterConstant.MSG_BODY, true);
        d.setHidden(wmMQAdapterConstant.MSG_BODY_NAME, true);
        d.setHidden(wmMQAdapterConstant.MSG_BODY_TYPE, true);
		d.setHidden(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY, true);
		d.setHidden(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_NAME, true);
		d.setHidden(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE, true);

		d.setHidden(wmMQAdapterConstant.INPUT_FIELD_NAMES, true);

        d.setHidden(wmMQAdapterConstant.DEAD_LETTER_QUEUE_NAME, true);
        d.setHidden(wmMQAdapterConstant.DEAD_LETTER_QUEUE_TYPE, true);
        d.setHidden(wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_NAME, true);
        d.setHidden(wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_TYPE, true);
        d.setHidden(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_NAME, true);
        d.setHidden(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_TYPE, true);

		d.setHidden(wmMQAdapterConstant.REASON_CODE, true);
		d.setHidden(wmMQAdapterConstant.REASON_CODE_TYPE, true);
		d.setHidden(wmMQAdapterConstant.REASON_CODE_NAME, true);
		d.setHidden(wmMQAdapterConstant.CONDITION_CODE, true);
		d.setHidden(wmMQAdapterConstant.CONDITION_CODE_TYPE, true);
		d.setHidden(wmMQAdapterConstant.CONDITION_CODE_NAME, true);
		d.setHidden(wmMQAdapterConstant.ERROR_MSG, true);
		d.setHidden(wmMQAdapterConstant.ERROR_MSG_TYPE, true);
		d.setHidden(wmMQAdapterConstant.ERROR_MSG_NAME, true);


//		d.setResourceDomain(wmMQAdapterConstant.DEAD_LETTER_QUEUE,
//							wmMQAdapterConstant.DEAD_LETTER_QUEUE,
//							null);

        d.setResourceDomain(wmMQAdapterConstant.DEAD_LETTER_QUEUE_TYPE,
                            wmMQAdapterConstant.DEAD_LETTER_QUEUE_TYPE,
                            null);

//		d.setResourceDomain(wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER,
//							wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER,
//							null);

        d.setResourceDomain(wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_TYPE,
                            wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_TYPE,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS,
			                wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS,
			                null);
        d.setResourceDomain(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_TYPE,
			                wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_TYPE,
			                null);

		if (!_serviceName.equals(wmMQAdapterConstant.PEEK_SERVICE))
		{
			d.setResourceDomain(wmMQAdapterConstant.DEAD_LETTER_QUEUE_NAME,
								WmTemplateDescriptor.INPUT_FIELD_NAMES,
//								  new String[] {wmMQAdapterConstant.DEAD_LETTER_QUEUE,
								new String[] {wmMQAdapterConstant.DEAD_LETTER_QUEUE_TYPE});

	        d.setResourceDomain(wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_NAME,
    	                        WmTemplateDescriptor.INPUT_FIELD_NAMES,
//								new String[] {wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER,
            	                new String[] {wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_TYPE});
	        
	        d.setResourceDomain(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_NAME,
			                    WmTemplateDescriptor.INPUT_FIELD_NAMES,
				                new String[] {wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_TYPE});
		}

//		d.setResourceDomain(wmMQAdapterConstant.WAIT_INTERVAL,
//							wmMQAdapterConstant.WAIT_INTERVAL,
//							null);

		d.setResourceDomain(wmMQAdapterConstant.WAIT_INTERVAL_TYPE,
							wmMQAdapterConstant.WAIT_INTERVAL_TYPE,
							null);

		if (!_serviceName.equals(wmMQAdapterConstant.PUT_SERVICE)) {
			d.setResourceDomain(wmMQAdapterConstant.WAIT_INTERVAL_NAME,
								WmTemplateDescriptor.INPUT_FIELD_NAMES,
								new String[] {wmMQAdapterConstant.WAIT_INTERVAL_TYPE});
		}

        d.setResourceDomain(wmMQAdapterConstant.MSG_BODY,
                            wmMQAdapterConstant.MSG_BODY,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.MSG_BODY_TYPE,
                            wmMQAdapterConstant.MSG_BODY_TYPE,
                            null);

		d.setResourceDomain(wmMQAdapterConstant.MSG_BODY_NAME,
							_whereToPutMsgBody,
							new String[] {wmMQAdapterConstant.MSG_BODY,
										  wmMQAdapterConstant.MSG_BODY_TYPE});

        d.setResourceDomain(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY,
                            wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE,
                            wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE,
                            null);

		d.setResourceDomain(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_NAME,
							_whereToPutMsgBody,
							new String[] {wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY,
										  wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE});

        d.setResourceDomain(_mqmdInputFieldsLabel,
                            _mqmdInputFieldsLabel,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES,
                            wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.MQMD_INPUT_CONSTANTS,
                            wmMQAdapterConstant.MQMD_INPUT_CONSTANTS,
                            new String[] {_mqmdInputFieldsLabel});

        d.setResourceDomain(wmMQAdapterConstant.INPUT_FIELD_NAMES,
                            WmTemplateDescriptor.INPUT_FIELD_NAMES,
                            new String[] {_mqmdInputFieldsLabel,
                                          wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES}, null);
//                            wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS);

        d.setResourceDomain(wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
                            wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES,
                            wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.OUTPUT_FIELD_NAMES,
                            WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
                            new String[] {wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
                                          wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES}, null);
//                            wmMQAdapterConstant.USE_MQMD_OUTPUT_FIELDS);

        d.setResourceDomain(wmMQAdapterConstant.JMS_PROPERTIES,
                            wmMQAdapterConstant.JMS_PROPERTIES,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.JMS_PROPERTY_TYPES,
                            wmMQAdapterConstant.JMS_PROPERTY_TYPES,
                            null);

     	d.setResourceDomain(wmMQAdapterConstant.JMS_PROPERTY_NAMES,
           	                _whereToPutJMSProperties,
               	            new String[] {wmMQAdapterConstant.JMS_PROPERTIES,
                   	                      wmMQAdapterConstant.JMS_PROPERTY_TYPES});
                       	    //wmMQAdapterConstant.USE_JMS_PROPERTIES);

    	d.setResourceDomain(wmMQAdapterConstant.USER_DEFINED_PROPERTIES,
    						wmMQAdapterConstant.USER_DEFINED_PROPERTIES,
			    	  		null);
    	d.setResourceDomain(wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES,
			    			wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES,
			    	  		new String[] {wmMQAdapterConstant.USER_DEFINED_PROPERTIES});

		d.setResourceDomain(wmMQAdapterConstant.USER_DEFINED_PROPERTY_TYPES,
							wmMQAdapterConstant.USER_DEFINED_PROPERTY_TYPES,
					  		null);

		d.setResourceDomain(wmMQAdapterConstant.REAL_USER_DEFINED_PROPERTY_NAMES,
							_whereToPutJMSProperties,
							new String[] {wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES,
										  wmMQAdapterConstant.USER_DEFINED_PROPERTY_TYPES });

		d.setResourceDomain(wmMQAdapterConstant.REASON_CODE,
							wmMQAdapterConstant.REASON_CODE,
							null);

		d.setResourceDomain(wmMQAdapterConstant.REASON_CODE_TYPE,
							wmMQAdapterConstant.REASON_CODE_TYPE,
							null);

		d.setResourceDomain(wmMQAdapterConstant.REASON_CODE_NAME,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.REASON_CODE,
										  wmMQAdapterConstant.REASON_CODE_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.CONDITION_CODE,
							wmMQAdapterConstant.CONDITION_CODE,
							null);

		d.setResourceDomain(wmMQAdapterConstant.CONDITION_CODE_TYPE,
							wmMQAdapterConstant.CONDITION_CODE_TYPE,
							null);

		d.setResourceDomain(wmMQAdapterConstant.CONDITION_CODE_NAME,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.CONDITION_CODE,
										  wmMQAdapterConstant.CONDITION_CODE_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.ERROR_MSG,
							wmMQAdapterConstant.ERROR_MSG,
							null);

		d.setResourceDomain(wmMQAdapterConstant.ERROR_MSG_TYPE,
							wmMQAdapterConstant.ERROR_MSG_TYPE,
							null);

		d.setResourceDomain(wmMQAdapterConstant.ERROR_MSG_NAME,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.ERROR_MSG,
										  wmMQAdapterConstant.ERROR_MSG_TYPE});

        //Retrieves the i18n metadata information from
        //the resource bundle and replaces the non-localized metadata.
        //The metadata that needs to be internationalized
        //(the parameter display name, description, group name, etc.)
        //will populate the adapter's administrative interface,
        //Adapter Service Editor, or Adapter Notification Editor.
		
		//complet the descriptor with the override connection properties
		_overrideConnection.fillWmTemplateDescriptor(d, l);
        
        d.setDescriptions((wmMQAdapter.getInstance()).getAdapterResourceBundleManager(), l);
    } //execute()

	public void overrideServiceGroupFieldNames(String[] serviceGroupFieldNames)
	{
		_serviceGroupFieldNames = serviceGroupFieldNames; //NOSONAR
	}

	public void overrideMQMDGroupFieldName(int indx, String oneFieldName)
	{
		if ((indx < 0) || (indx > _mqmdGroupFieldNames.length))
			return;

		_mqmdGroupFieldNames[indx] = oneFieldName;
	}

	public void overrideMQMDInputFieldMapName(int indx, String oneFieldName)
	{
		if ((indx < 0) || (indx > _mqmdInputFieldMap.length))
			return;

		_mqmdInputFieldMap[indx] = oneFieldName;
	}

	public void overrideMQMDInputTupleName(int indx, String oneFieldName)
	{
		if ((indx < 0) || (indx > _mqmdInputTupleNames.length))
			return;

		_mqmdInputTupleNames[indx] = oneFieldName;
	}

	public void overrideMQMDInputFieldsLabel(String fieldName)
	{
		_mqmdInputFieldsLabel = fieldName;
	}

	public void overrideWhereToPutJMSProperties(String where)
	{
		_whereToPutJMSProperties = where;
	}

	public void overrideWhereToPutMsgBody(String where)
	{
		_whereToPutMsgBody = where;
	}

	public void addToMQMDGroupFieldNames(Object[] newfieldnames)
	{
		int newsize = _mqmdGroupFieldNames.length + newfieldnames.length;
		Object[] temp =	new String[newsize];
		System.arraycopy(_mqmdGroupFieldNames, 0, temp, 0, _mqmdGroupFieldNames.length);
		System.arraycopy(newfieldnames, 0, temp, _mqmdGroupFieldNames.length, newfieldnames.length);
		_mqmdGroupFieldNames = (String[])temp;
		for (int i = 0 ; i < _mqmdGroupFieldNames.length ; i++)
			log(ARTLogger.INFO, 1003, "baseService:addToMQMDGroupFieldNames", 
									  "_mqmdGroupFieldNames[" + i + "]=" + _mqmdGroupFieldNames[i]);

	}
	
	private Values extractAllHeaders(IData record, boolean skipNonIDataValues, String keyPrefix) {
		if(record == null) {
			return null;
		}
		IDataCursor idcTemp = record.getCursor();
		if(idcTemp == null || !idcTemp.first()) {
			return null;
		}
		Values returnValue = new Values();
		do {
			String key = idcTemp.getKey();
			Object value = idcTemp.getValue();
			String compoundKey = keyPrefix+"."+key;
			if(value != null) {
				if (value instanceof IData) {
					returnValue.put(key, extractAllHeaders((IData)value, false, compoundKey));
				}
				else {
					if(!skipNonIDataValues) {
						String[] jmsfields = getJmsPropertyNames();
						int i = 0;
						for (i = 0 ; i < jmsfields.length ; i++) {
							if(jmsfields[i].equals(compoundKey)) {
								returnValue.put(key, value);
								break;
							}
						}
						
						// If not found in the jmsProperties search in the user-defined fields
						if(i == jmsfields.length) {
							String[] userDefinedFields = getRealUserDefinedPropertyNames();
							for (i = 0 ; i < userDefinedFields.length ; i++) {
								if(userDefinedFields[i].contains(compoundKey) || userDefinedFields[i].equals(compoundKey) ) {
									returnValue.put(key, value);
									break;
								}
							}
						}
					}
				}
			}
		} while(idcTemp.next());
		
		idcTemp.destroy();
		
		return returnValue;
	}
	
	public void copyJMSPropertiesToMsg(WmRecord input, 
									   wmMQMessage msg,
									   wmMQConnection conn) throws wmMQException {
		copyJMSPropertiesToMsg(input, msg, conn.getResolvedQueueManagerName(), conn.getResolvedQueueName(false));
	}
	
	/**
	 * Copy the JMSProperties to the message
	 *
	 * @param input - the input WmRecord
	 * @param msg   - the wmMQMessage object
	 * @param conn  - the wmMQConnection object
	 *
	 */

	public void copyJMSPropertiesToMsg(WmRecord input, 
									   wmMQMessage msg,
									   String queueManagerName,
									   String queueName) throws wmMQException
	{
		
		try
		{
			IDataCursor idctemp = null;
			/*try
			{
				idctemp = input.getCursor();
				if ((idctemp != null) && (idctemp.first()))
				{
					do {
						String key = idctemp.getKey();
						Object o = idctemp.getValue();
						if (o == null)
							log(ARTLogger.INFO, 1003, "input key=" + key + " is null", "");
						else
							log(ARTLogger.INFO, 1003, "input key=" + key, ", value=" + o.toString());
					}
					while (idctemp.next());
					idctemp.destroy();
				}
				else
					log(ARTLogger.INFO, 1003, "baseService:copyJMSPropertiesToMsg", "idc is null, input=" + input.toString());
			}
			catch (Exception ex)
			{
				//ex.printStackTrace();
			}*/
			
			log(ARTLogger.INFO, 1003, "baseService:copyJMSPropertiesToMsg",  input.getIData().toString());
			
			//TODO : Remove unnecessary iteration.
			WmRecord jmsproperties = (WmRecord)input.get("JMSProperties");
			if (jmsproperties != null)
			{ 
				WmRecord jmsheaderproperties = (WmRecord)jmsproperties.get("jms");
				try
				{
					idctemp = jmsheaderproperties.getCursor();
					if ((idctemp != null) && (idctemp.first()))
					{
						do {
							String key = idctemp.getKey();
							Object o = idctemp.getValue();
							if (o == null)
								log(ARTLogger.INFO, 1003, "jmsheaderproperties key=" + key + " is null", "");
							else
								log(ARTLogger.INFO, 1003, "jmsheaderproperties key=" + key, ", value=" + o.toString());
						}
						while (idctemp.next());
					}
					else
						log(ARTLogger.INFO, 1003, "baseService:copyJMSPropertiesToMsg", "idc is null, input=" + input.toString());
				}
				catch (Exception ex)
				{
					//ex.printStackTrace();
				}
				
				Values JMSHeader = extractAllHeaders(jmsproperties, true, "JMSProperties"); 
						
//				if (jmsheaderproperties != null)
//				{
//					Values jms = new Values();
//					String[] jmsfields = getJmsPropertyNames();
//					for (int i = 0 ; i < jmsfields.length ; i++)
//					{
//						if(jmsfields[i].startsWith("JMSProperties.jms.")) {
//							String fieldname = jmsfields[i].substring("JMSProperties.jms.".length());
//							if(fieldname.indexOf('.') >= 0) {
//								fieldname = fieldname.substring(0, fieldname.indexOf('.'));
//							}
//							jms.put(fieldname, 
//									jmsheaderproperties.get(fieldname));
//						}
//					}
//					JMSHeader.put("jms", jms);
//				}
	
				//Trax 1-Y72E9 - process all RFH2 headers 
				//Loop through all other headers under the JMSProperties object
//				IDataCursor jmsidc = null;
//	            jmsidc = jmsproperties.getCursor();
//	            if ((jmsidc != null) && (jmsidc.first()))
//	            {
//		            do 
//		            {
//		                String key = jmsidc.getKey(); 
//		                if (key.trim().equalsIgnoreCase("jms"))
//		                	continue;
//		                
//		                Object o = jmsidc.getValue();
//		               
//		                if ((o != null) && (o instanceof IData))
//		                {
//							IData otherheaderproperties = (IData)o;
//							Values otherValues = new Values();
//							IDataCursor otheridc = otherheaderproperties.getCursor();
//							try
//							{
//								if ((otheridc != null) && (otheridc.first()))
//								{
//									do {
//										String otherkey = otheridc.getKey();
//										String fieldname = otherkey.substring(otherkey.lastIndexOf('.') + 1); 
//										Object otherobject = otheridc.getValue();
//										if (otherobject != null)
//											otherValues.put(fieldname, otherobject);
//									}
//									while (otheridc.next());
//								}
//							}	
//							catch (Exception ex)
//							{
//								//ex.printStackTrace();
//							}
//							finally
//							{
//								if (otheridc != null)
//									otheridc.destroy();	
//							}
//							JMSHeader.put(key, otherValues);
//		                }
//		            }
//		            while (jmsidc.next());
//	            }
	            
				wmMQAdapter.dumpValues("JMS Properties", JMSHeader);
				msg.setJMSHeader(queueManagerName, 
							 	 queueName, 
								 JMSHeader);
									  
			} //if (jmsproperties != null)
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * Copy the JMSProperties from the message
	 *
	 * @param output - the output WmRecord
	 * @param msg   - the wmMQMessage object
	 *
	 */

	public static void copyJMSPropertiesFromMsg(WmRecord output, 
									   	 wmMQMessage msg) throws wmMQException
	{
		log(ARTLogger.INFO, 1001, "copyJMSPropertiesFromMsg", "");
		
		long time = -System.currentTimeMillis();
		
		Values JMSHeader = msg.getJMSHeader();
		if (JMSHeader == null)
		{
			log(ARTLogger.INFO, 1003, "copyJMSPropertiesFromMsg", "elapsed time=" + (System.currentTimeMillis() + time));
			return;
		}
			
		WmRecord jmsproperties = WmRecordFactory.getFactory().createWmRecord("JMSProperties");
		Values jms = JMSHeader.getValues("jms");

		if (jms != null)
		{ 
			wmMQAdapter.dumpValues("copyJMSPropertiesFromMsg jms", jms);
			WmRecord jmsrecord = WmRecordFactory.getFactory().createWmRecord("jms");
			for (Enumeration ekeys = jms.keys() ; ekeys.hasMoreElements() ; )
			{
				String onekey = (String)ekeys.nextElement();
				Object o = jms.get(onekey);
				if (o != null)
				{
					//Convert XML tag to property name, if possible
					for (int p = 0 ; p < wmMQMessage.JMSPropertyNames.length; p++)
					{
						if (onekey.trim().equals(wmMQMessage.JMSPropertyNames[p][1].trim()))
							onekey = wmMQMessage.JMSPropertyNames[p][0];
					}
					jmsrecord.put(onekey, o);
				}
			}
			jmsproperties.put("jms", jmsrecord);					  
		} //if (jms != null)

		//TODO Try to see if the time complexity of below(L*M*N) can be reduced. 
		//Trax 1-Y72E9 - process all RFH2 headers 
		for (Enumeration keys = JMSHeader.keys() ; keys.hasMoreElements() ; )
        {
			String otherKey = (String)keys.nextElement();
			if (otherKey.trim().equalsIgnoreCase("jms"))
				continue;
        	Object otherObject = JMSHeader.get(otherKey);
			if ((otherObject != null) && (otherObject instanceof Values))
			{ 
				Values otherValues = (Values)otherObject;
				WmRecord otherRecord = WmRecordFactory.getFactory().createWmRecord(otherKey);
				
//				for (Enumeration ekeys = otherValues.keys() ; ekeys.hasMoreElements() ; )
//				{
//					String onekey = (String)ekeys.nextElement();
//					//Trax 1-Y72E9 NullPointerException or missing data.
//					//				Object o = jms.get(onekey);
//					Object o = otherValues.get(onekey);
//					
//					//Convert XML tag to property name, if possible
//					for (int p = 0 ; p < wmMQMessage.JMSPropertyNames.length; p++)
//					{
//						if (onekey.trim().equals(wmMQMessage.JMSPropertyNames[p][1].trim()))
//							onekey = wmMQMessage.JMSPropertyNames[p][0];
//					}
//					if (o != null)
//						otherRecord.put(onekey, o);
//					//otherRecord.put(otherKey, otherValues);
//				}
				jmsproperties.put(otherKey, otherValues);			  
			} //if (otherObject != null)
        } //for (Enumeration....

		output.put("JMSProperties", jmsproperties);
		
		log(ARTLogger.INFO, 1003, "copyJMSPropertiesFromMsg", "elapsed time=" + (System.currentTimeMillis() + time));
		log(ARTLogger.INFO, 1002, "copyJMSPropertiesFromMsg", "");
	}
	
    protected static void log(int level, int minor, String arg0, String arg1)
    {
        ARTLogger logger = ((wmMQAdapter)wmMQAdapter.getInstance()).getLogger();
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

	private String[] _serviceGroupFieldNames = new String[0];

	private String[] _mqmdGroupFieldNames = new String[] {
				  	 wmMQAdapterConstant.MQMD_INPUT_FIELDS,
				  	 wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS,
				  	 wmMQAdapterConstant.INPUT_FIELD_NAMES,
				 	 wmMQAdapterConstant.MQMD_INPUT_CONSTANTS,
				 	 wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES,
				 	 wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
					 wmMQAdapterConstant.USE_MQMD_OUTPUT_FIELDS,
				 	 wmMQAdapterConstant.OUTPUT_FIELD_NAMES,
				 	 wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES,
					 wmMQAdapterConstant.REASON_CODE,
					 wmMQAdapterConstant.REASON_CODE_TYPE,
					 wmMQAdapterConstant.REASON_CODE_NAME,
					 wmMQAdapterConstant.CONDITION_CODE,
					 wmMQAdapterConstant.CONDITION_CODE_TYPE,
					 wmMQAdapterConstant.CONDITION_CODE_NAME,
					 wmMQAdapterConstant.ERROR_MSG,
					 wmMQAdapterConstant.ERROR_MSG_TYPE,
					 wmMQAdapterConstant.ERROR_MSG_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_MANAGER_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_HOST_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_PORT,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_CHANNEL_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_CCSID,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_ENCODING,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_DYNAMIC_QUEUE_PREFIX,
//					 wmMQAdapterConstant.OVERRIDE_CONNECTION_USERID,
//					 wmMQAdapterConstant.OVERRIDE_CONNECTION_PASSWORD,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_MANAGER_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_HOST_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_PORT,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CHANNEL_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CCSID,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_ENCODING,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_DYNAMIC_QUEUE_PREFIX
//					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_USERID,
//					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_PASSWORD
					 };

	private String[] _putMqmdGroupFieldNames = new String[] {
				  	 wmMQAdapterConstant.MQMD_INPUT_FIELDS,
				  	 wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS,
				  	 wmMQAdapterConstant.INPUT_FIELD_NAMES,
				 	 wmMQAdapterConstant.MQMD_INPUT_CONSTANTS,
				 	 wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES,
				 	 wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
					 wmMQAdapterConstant.USE_MQMD_OUTPUT_FIELDS,
				 	 wmMQAdapterConstant.OUTPUT_FIELD_NAMES,
				 	 wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES,
					 wmMQAdapterConstant.REASON_CODE,
					 wmMQAdapterConstant.REASON_CODE_TYPE,
					 wmMQAdapterConstant.REASON_CODE_NAME,
					 wmMQAdapterConstant.CONDITION_CODE,
					 wmMQAdapterConstant.CONDITION_CODE_TYPE,
					 wmMQAdapterConstant.CONDITION_CODE_NAME,
					 wmMQAdapterConstant.ERROR_MSG,
					 wmMQAdapterConstant.ERROR_MSG_TYPE,
					 wmMQAdapterConstant.ERROR_MSG_NAME,
					 wmMQAdapterConstant.OVERRIDE_USE_LOCAL_QUEUE_MANAGER,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_MANAGER_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_HOST_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_PORT,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_CHANNEL_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_CCSID,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_ENCODING,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_DYNAMIC_QUEUE_PREFIX,
		//			 wmMQAdapterConstant.OVERRIDE_CONNECTION_USERID,
		//			 wmMQAdapterConstant.OVERRIDE_CONNECTION_PASSWORD,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_MANAGER_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_HOST_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_PORT,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CHANNEL_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CCSID,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_ENCODING,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_DYNAMIC_QUEUE_PREFIX
		//			 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_USERID,
		//			 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_PASSWORD
					 };

	private String[] _requestReplyMqmdGroupFieldNames = new String[] {
					 wmMQAdapterConstant.MQMD_INPUT_FIELDS,
					 wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS,
					 wmMQAdapterConstant.INPUT_FIELD_NAMES,
					 wmMQAdapterConstant.MQMD_INPUT_CONSTANTS,
					 wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES,
					 wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
					 wmMQAdapterConstant.USE_MQMD_OUTPUT_FIELDS,
					 wmMQAdapterConstant.OUTPUT_FIELD_NAMES,
					 wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES,
					 wmMQAdapterConstant.REASON_CODE,
					 wmMQAdapterConstant.REASON_CODE_TYPE,
					 wmMQAdapterConstant.REASON_CODE_NAME,
					 wmMQAdapterConstant.CONDITION_CODE,
					 wmMQAdapterConstant.CONDITION_CODE_TYPE,
					 wmMQAdapterConstant.CONDITION_CODE_NAME,
					 wmMQAdapterConstant.ERROR_MSG,
					 wmMQAdapterConstant.ERROR_MSG_TYPE,
					 wmMQAdapterConstant.ERROR_MSG_NAME,
					 wmMQAdapterConstant.OVERRIDE_USE_LOCAL_QUEUE_MANAGER,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_MANAGER_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_HOST_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_PORT,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_CHANNEL_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_CCSID,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_ENCODING,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_DYNAMIC_QUEUE_PREFIX,
//					 wmMQAdapterConstant.OVERRIDE_CONNECTION_USERID,
//					 wmMQAdapterConstant.OVERRIDE_CONNECTION_PASSWORD,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_MANAGER_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_HOST_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_PORT,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CHANNEL_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CCSID,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_ENCODING,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_DYNAMIC_QUEUE_PREFIX,
//					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_USERID,
//					 wmMQAdapterConstant.OVERRIDDEN_CONNECTION_PASSWORD,
					 wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_QUEUE_MANAGER_NAME,
					 wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_HOST_NAME,
					 wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_PORT,
					 wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_CHANNEL_NAME,
					 wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_CCSID,
					 wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_ENCODING,
					 wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_QUEUE_NAME,
					 wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_DYNAMIC_QUEUE_PREFIX,
					 wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_QUEUE_MANAGER_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_HOST_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_PORT,
					 wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_CHANNEL_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_CCSID,
					 wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_ENCODING,
					 wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_QUEUE_NAME,
					 wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_DYNAMIC_QUEUE_PREFIX
//					 wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_USERID,
//					 wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_PASSWORD
					 };

	private String[] _mqmdInputFieldMap = new String[] {
					 wmMQAdapterConstant.MQMD_INPUT_FIELDS,
					 wmMQAdapterConstant.INPUT_FIELD_NAMES,
					 wmMQAdapterConstant.MQMD_INPUT_CONSTANTS,
			 		 wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS,
					 wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_MANAGER_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_HOST_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_PORT,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_CHANNEL_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_CCSID,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_ENCODING,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_DYNAMIC_QUEUE_PREFIX
//					 wmMQAdapterConstant.OVERRIDE_CONNECTION_USERID,
//					 wmMQAdapterConstant.OVERRIDE_CONNECTION_PASSWORD
				};

	private String[] _putMqmdInputFieldMap = new String[] {
					 wmMQAdapterConstant.MQMD_INPUT_FIELDS,
					 wmMQAdapterConstant.INPUT_FIELD_NAMES,
					 wmMQAdapterConstant.MQMD_INPUT_CONSTANTS,
			 		 wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS,
					 wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES,
					 wmMQAdapterConstant.OVERRIDE_USE_LOCAL_QUEUE_MANAGER,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_MANAGER_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_HOST_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_PORT,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_CHANNEL_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_CCSID,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_ENCODING,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_NAME,
					 wmMQAdapterConstant.OVERRIDE_CONNECTION_DYNAMIC_QUEUE_PREFIX
		//			 wmMQAdapterConstant.OVERRIDE_CONNECTION_USERID,
		//			 wmMQAdapterConstant.OVERRIDE_CONNECTION_PASSWORD
				};
	
	private String[] _mqmdInputTupleNames = new String[] {
					 wmMQAdapterConstant.MQMD_INPUT_FIELDS,
					 wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES,
					 wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS};

	private String[] _jmsPropertyFieldNames = new String[] {
					 wmMQAdapterConstant.JMS_PROPERTIES,
				   	 wmMQAdapterConstant.USE_JMS_PROPERTIES,
				     wmMQAdapterConstant.JMS_PROPERTY_NAMES,
				     wmMQAdapterConstant.JMS_PROPERTY_TYPES};

	private String _mqmdInputFieldsLabel = wmMQAdapterConstant.MQMD_INPUT_FIELDS;

	private String _whereToPutJMSProperties = WmTemplateDescriptor.OUTPUT_FIELD_NAMES;

	private String _whereToPutMsgBody = WmTemplateDescriptor.OUTPUT_FIELD_NAMES;
	
	protected NSRecord getOutSignature() {

		String svcName = serviceName();
		
		AdapterServiceNode node = (AdapterServiceNode) Namespace.current()
				.getNode(svcName);
		
		NSRecord output = node.getSignature().getOutput();

		return (NSRecord) output.getFieldByName(output.getName());

	}
}