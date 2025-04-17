/*
 * wmMQSyncListenerNotification.java
 *
 * Copyright 2003 webMethods, Inc.
 * ALL RIGHTS RESERVED
 *
 * UNPUBLISHED -- Rights reserved under the copyright laws of the United States.
 * Use of a copyright notice is precautionary only and does not imply publication or disclosure.
 *
 * THIS SOURCE CODE IS THE CONFIDENTIAL AND PROPRIETARY INFORMATION OF
 * WEBMETHODS, INC.  ANY REPRODUCTION, MODIFICATION, DISTRIBUTION,
 * OR DISCLOSURE IN ANY FORM, IN WHOLE, OR IN PART, IS STRICTLY PROHIBITED
 * WITHOUT THE PRIOR EXPRESS WRITTEN PERMISSION OF WEBMETHODS, INC.
 */
// this class is added at Phase 5 to support listener feature
package com.wm.adapter.wmmqadapter.notification;

import java.util.HashMap;
import java.util.Locale; 

import javax.resource.ResourceException;

import com.wm.adapter.wmmqadapter.WmMQAdapterUtils;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adapter.wmmqadapter.connection.wmMQException;
import com.wm.adapter.wmmqadapter.connection.wmMQMessage;
import com.wm.adapter.wmmqadapter.service.wmMQBaseService;
import com.wm.adapter.wmmqadapter.service.wmMQMQMD;
import com.wm.adk.cci.record.WmRecord;
import com.wm.adk.cci.record.WmRecordFactory;
import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;
import com.wm.adk.metadata.WmTemplateDescriptor;
import com.wm.adk.notification.NotificationEvent;
import com.wm.adk.notification.NotificationResults;
import com.wm.adk.notification.SyncNotificationResults;
import com.wm.adk.notification.WmNotification;
import com.wm.adk.notification.WmSyncListenerNotification;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.data.IDataCursor;
import com.wm.lang.ns.NSRecord;
import com.wm.util.Values;

/*
 * This class is the implementation of the synchronous listener notification template
 * for the WmSampleAdapter. It receive listener notification event of the types of either
 * check deposit or under balance wanring, and publishes the message.
 *
 * ????Note: The notification template provides callback methods to interact with the
 * Integration Server when the state of a notification changes. But this simple
 * example of a notification does not make use of these callbacks. For more
 * information on the available callbacks, refer to the Javadoc of the
 * WmNotification class.
 */
public class wmMQSyncListenerNotification extends WmSyncListenerNotification
{

	//Message body
	private Object _msgBody = null;
	
	private boolean showMsgBody = true;
	
	private boolean showMsgBodyByteArray = true;

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

	//Queue Manager Name
	private String _queueManagerName = null;

	//Queue Manager Name name
	private String _queueManagerNameName = wmMQAdapterConstant.QUEUE_MANAGER_NAME;

	//Queue Manager Name field type
	private String[] _queueManagerNameType = {"string"};
	
	//Queue Name
	private String _queueName = null;

	//Queue Name name
	private String _queueNameName = wmMQAdapterConstant.QUEUE_NAME;

	//Queue Name field type
	private String[] _queueNameType = {"string"};
	
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
	private boolean[] 	_useJMSProperties;
	
    //User defined folders and properties
    private String[] _userDefinedProperties;
    private String[] _userDefinedPropertyNames;
    private String[] _userDefinedPropertyTypes;
    private String[] _realUserDefinedPropertyNames;
    	
    protected String[]  _mqmdOutputFields;
    protected String[]  _mqmdOutputFieldTypes;
    private boolean[]   _useOutputMQMDFields;
	protected String[]  _mqmdInputFields;
	protected String[]  _mqmdInputFieldTypes;
	private boolean[]   _useMQMDFields;
	private String[]    _mqmdInputConstants;

	//Message body
	private Object _replyMsgBody = null;

	//Message body name
	private String _replyMsgBodyName = "replyMsgBody";

	// Trax 1-11YH1R : Begin
	//Message body field type
	private String[] _replyMsgBodyType = {"java.lang.Object"};
	// Trax 1-11YH1R : End

	//private boolean		forcingRollback = false;
	//private boolean		thisIsLastNotification = false;

    /*
     * Default constructor
     */
    public wmMQSyncListenerNotification() {
        super();
		_mqmd = new wmMQMQMD();
    }

	/*
	 * Gets the input field names.
	 *
	 */
	public String[] getRequestFieldNames()
	{
		log(ARTLogger.INFO, 1001, "getRequestFieldNames", "");
		return _inputFieldNames;
	}

	/*
	 * Sets the input field names.
	 *
	 * The inputFieldNames parameter is the input field names.
	 *
	 */
	public void setRequestFieldNames(String[] inputFieldNames)
	{
		log(ARTLogger.INFO, 1001, "setRequestFieldNames", "");
		_inputFieldNames = inputFieldNames;
		log(ARTLogger.INFO, 1002, "setRequestFieldNames", "");
	}

	/*
	 * Gets the input field names.
	 *
	 */
/*	public String[] getFilterCriteria()
	{
		log(ARTLogger.INFO, 1001, "getFilterCriteria", "");
		return _inputFieldNames;
	}
*/
	/*
	 * Sets the input field names.
	 *
	 * The inputFieldNames parameter is the input field names.
	 *
	 */
/*	public void setFilterCriteria(String[] inputFieldNames)
	{
		log(ARTLogger.INFO, 1001, "setFilterCriteria", "");
		_inputFieldNames = inputFieldNames;
		log(ARTLogger.INFO, 1002, "setFilterCriteria", "");
	}
*/
    /*
     * Gets the message body.
     *
     */
    public Object getMsgBody()
    {
		log(ARTLogger.INFO, 1001, "getMsgBody", "");
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
		log(ARTLogger.INFO, 1001, "getMsgBody", "");
        _msgBody = msgbody;
		log(ARTLogger.INFO, 1002, "getMsgBody", "");
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
        return _msgBodyByteArrayType; //NOSONAR
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

	public String getQueueManagerName() {
		return this._queueManagerName;
	}

	public void setQueueManagerName(String queueManagerName) {
		this._queueManagerName = queueManagerName;
	}

	public String getQueueManagerNameName() {
		return this._queueManagerNameName;
	}

	public void setQueueManagerNameName(String queueManagerNameName) {
		this._queueManagerNameName = queueManagerNameName;
	}

	public String[] getQueueManagerNameType() {
		return this._queueManagerNameType;//NOSONAR
	}

	public void setQueueManagerNameType(String[] queueManagerNameType) {
		this._queueManagerNameType = queueManagerNameType;//NOSONAR
	}

	public String getQueueName() {
		return this._queueName;
	}

	public void setQueueName(String queueName) {
		this._queueName = queueName;
	}

	public String getQueueNameName() {
		return this._queueNameName;
	}

	public void setQueueNameName(String queueNameName) {
		this._queueNameName = queueNameName;
	}

	public String[] getQueueNameType() {
		return this._queueNameType;//NOSONAR
	}

	public void setQueueNameType(String[] queueNameType) {
		this._queueNameType = queueNameType;//NOSONAR
	}
	
	public void setMqmdRequestFields(String[] fields)
	{
		log(ARTLogger.INFO, 1001, "setMqmdRequestFields", "");
		_mqmd.setMqmdInputFields(fields);
		log(ARTLogger.INFO, 1002, "setMqmdRequestFields", "");
	}

	public String[] getMqmdRequestFields()
	{
		log(ARTLogger.INFO, 1001, "getMqmdRequestFields", "");
		return _mqmd.getMqmdInputFields();
	}

    public void setMqmdRequestConstants(String[] constants)
    {
		log(ARTLogger.INFO, 1001, "setMqmdConstants", "");
       _mqmd.setMqmdInputConstants(constants);
	   log(ARTLogger.INFO, 1002, "setMqmdConstants", "");
   }

    public void setUseMQMDRequestFields(boolean[] use)
    {
		log(ARTLogger.INFO, 1001, "setUseMQMDRequestFields", "");
        _mqmd.setUseMQMDFields(use);
		log(ARTLogger.INFO, 1002, "setUseMQMDRequestFields", "");
   }

    public void setMqmdRequestFieldTypes(String[] types)
    {
		log(ARTLogger.INFO, 1001, "setMqmdRequestFieldTypes", "");
       _mqmd.setMqmdInputFieldTypes(types);
	   log(ARTLogger.INFO, 1002, "setMqmdRequestFieldTypes", "");
   }

    public void setMqmdReplyFields(String[] fields)
    {
		log(ARTLogger.INFO, 1001, "setMqmdReplyFields", "");
        _mqmd.setMqmdOutputFields(fields);
		log(ARTLogger.INFO, 1002, "setMqmdReplyFields", "");
    }

    public void setUseMQMDReplyFields(boolean[] use)
    {
		log(ARTLogger.INFO, 1001, "setUseMQMDReplyFields", "");
        _mqmd.setUseOutputMQMDFields(use);
		log(ARTLogger.INFO, 1002, "setUseMQMDReplyFields", "");
    }

    public void setMqmdReplyFieldTypes(String[] types)
    {
		log(ARTLogger.INFO, 1001, "setMqmdReplyFieldTypes", "");
        _mqmd.setMqmdOutputFieldTypes(types);
		log(ARTLogger.INFO, 1002, "setMqmdReplyFieldTypes", "");
    }

    public String[] getMqmdRequestConstants()
    {
		log(ARTLogger.INFO, 1001, "getMqmdRequestConstants", "");
        return _mqmd.getMqmdInputConstants();
    }

    public boolean[] getUseMQMDRequestFields()
    {
		log(ARTLogger.INFO, 1001, "getUseMQMDFields", "");
        return _mqmd.getUseMQMDFields();
    }
    /*
     * Gets the message body.
     *
     */
    public String getMsgBodyName()
    {
		log(ARTLogger.INFO, 1001, "getMsgBodyName", "");
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
		log(ARTLogger.INFO, 1001, "setMsgBodyName", "");
        _msgBodyName = msgbodyname;
		log(ARTLogger.INFO, 1002, "setMsgBodyName", "");
    }

    /*
     * Gets the message body.
     *
     */
    public String[] getMsgBodyType()
    {
		log(ARTLogger.INFO, 1001, "getMsgBodyType", "");
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
		log(ARTLogger.INFO, 1001, "setMsgBodyType", "");
        _msgBodyType = msgbodytype;//NOSONAR
		log(ARTLogger.INFO, 1002, "setMsgBodyType", "");
    }
/*
    public String[] getMqmdInputFieldTypes()
    {
		log(ARTLogger.INFO, 1001, "setMsgBodyType", "");
        return _mqmd.getMqmdInputFieldTypes();
  }
*/
    public String[] getMqmdReplyFields()
    {
	log(ARTLogger.INFO, 1001, "getMqmdOutputFields", "");
       return _mqmd.getMqmdOutputFields();
    }

    public boolean[] getUseMQMDReplyFields()
    {
		log(ARTLogger.INFO, 1001, "getUseOutputMQMDFields", "");
        return _mqmd.getUseOutputMQMDFields();
    }
/*
    public String[] getMqmdOutputFieldTypes()
    {
		log(ARTLogger.INFO, 1001, "getMqmdOutputFieldTypes", "");
        return _mqmd.getMqmdOutputFieldTypes();
    }
*/
    /*
     * Gets the JMS property names.
     *
     */
    public String[] getJmsPropertyNames()
    {
		log(ARTLogger.INFO, 1001, "getJmsPropertyNames", "");
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
		log(ARTLogger.INFO, 1001, "setJmsPropertyNames", "");
        _jmsPropertyNames = jmsPropertyNames;//NOSONAR
		log(ARTLogger.INFO, 1002, "setJmsPropertyNames", "");
    }

    /*
     * Gets the input field names.
     *
     */
    public String[] getReplyFieldNames()
    {
		log(ARTLogger.INFO, 1001, "getReplyFieldNames", "");
        return _outputFieldNames;
    }

    /*
     * Sets the output field names.
     *
     * The inputFieldNames parameter is the input field names.
     *
     */
    public void setReplyFieldNames(String[] outputFieldNames)
    {
		log(ARTLogger.INFO, 1001, "setReplyFieldNames", "");
        _outputFieldNames = outputFieldNames;
		log(ARTLogger.INFO, 1002, "setReplyFieldNames", "");
    }

    public String[] getJmsProperties()
    {
		log(ARTLogger.INFO, 1001, "setOutputFieldNames", "");
        return _JMSProperties;//NOSONAR
    }

    public void setJmsProperties(String[] jmsproperties)
    {
		log(ARTLogger.INFO, 1001, "setJmsProperties", "");
       _JMSProperties = jmsproperties;//NOSONAR
	   log(ARTLogger.INFO, 1002, "setJmsProperties", "");
    }

    public String[] getJmsPropertyTypes()
    {
		log(ARTLogger.INFO, 1001, "getJmsPropertyTypes", "");
        return _JMSPropertyTypes;//NOSONAR
    }

    public void setJmsPropertyTypes(String[] types)
    {
		log(ARTLogger.INFO, 1001, "setJmsPropertyTypes", "");
        _JMSPropertyTypes = types;//NOSONAR
		log(ARTLogger.INFO, 1002, "setJmsPropertyTypes", "");
   }

    public boolean[] getUseJmsProperties()
    {
		log(ARTLogger.INFO, 1001, "getUseJmsProperties", "");
        return _useJMSProperties;//NOSONAR
    }

    public void setUseJmsProperties(boolean[] use)
    {
		log(ARTLogger.INFO, 1001, "setUseJmsProperties", "");
        _useJMSProperties = use;//NOSONAR
		log(ARTLogger.INFO, 1002, "setUseJmsProperties", "");
    }

	public String[] getUserDefinedProperties()
	{
		log(ARTLogger.INFO, 1001, "getUserDefinedProperties", "");
		return _userDefinedProperties;//NOSONAR
	}

	public void setUserDefinedProperties(String[] userDefinedProperties)
	{
		log(ARTLogger.INFO, 1001, "setUserDefinedProperties", "");
		_userDefinedProperties = userDefinedProperties;//NOSONAR
		log(ARTLogger.INFO, 1002, "setUserDefinedProperties", "");
	}

	public String[] getUserDefinedPropertyNames()
	{
		log(ARTLogger.INFO, 1001, "getUserDefinedPropertyNames", "");
		return _userDefinedPropertyNames;//NOSONAR
	}

	public void setUserDefinedPropertyNames(String[] userDefinedPropertyNames)
	{
		log(ARTLogger.INFO, 1001, "setUserDefinedPropertyNames", "");
		_userDefinedPropertyNames = userDefinedPropertyNames;//NOSONAR
		log(ARTLogger.INFO, 1002, "setUserDefinedPropertyNames", "");
	}

	public String[] getRealUserDefinedPropertyNames()
	{
		log(ARTLogger.INFO, 1001, "getRealUserDefinedPropertyNames", "");
		return _realUserDefinedPropertyNames;//NOSONAR
	}

	public void setRealUserDefinedPropertyNames(String[] realUserDefinedPropertyNames)
	{
		log(ARTLogger.INFO, 1001, "setRealUserDefinedPropertyNames", "");
		_realUserDefinedPropertyNames = realUserDefinedPropertyNames;//NOSONAR
		log(ARTLogger.INFO, 1002, "setRealUserDefinedPropertyNames", "");
	}

	public String[] getUserDefinedPropertyTypes()
	{
		log(ARTLogger.INFO, 1001, "getUserDefinedPropertyTypes", "");
		return _userDefinedPropertyTypes;//NOSONAR
	}

	public void setUserDefinedPropertyTypes(String[] userDefinedPropertyTypes)
	{
		log(ARTLogger.INFO, 1001, "setUserDefinedPropertyTypes", "");
		_userDefinedPropertyTypes = userDefinedPropertyTypes;//NOSONAR
		log(ARTLogger.INFO, 1002, "setUserDefinedPropertyTypes", "");
	}
	
	/*
	 * Gets the Output message body.
	 *
	 */
	public Object getReplyMsgBody()
	{
		return _replyMsgBody;
	}

	/*
	 * Sets the Output message body.
	 *
	 * The msgbody parameter is the Output Message body property.
	 *
	 */
	public void setReplyMsgBody(Object msgbody)
	{
		_replyMsgBody = msgbody;
	}
	/*
	 * Gets the message body.
	 *
	 */
	public String getReplyMsgBodyName()
	{
		return _replyMsgBodyName;
	}

	/*
	 * Sets the message body.
	 *
	 * The msgbody parameter is the Message body property.
	 *
	 */
	public void setReplyMsgBodyName(String msgbodyname)
	{
		_replyMsgBodyName = msgbodyname;
	}

	/*
	 * Gets the message body.
	 *
	 */
	public String[] getReplyMsgBodyType()
	{
		return _replyMsgBodyType;//NOSONAR
	}

	/*
	 * Sets the message body.
	 *
	 * The msgbody parameter is the Message body property.
	 *
	 */
	public void setReplyMsgBodyType(String[] msgbodytype)
	{
		_replyMsgBodyType = msgbodytype;//NOSONAR
	}


    /**
     * This method populates the metadata object describing this listening
     * notification template in the specified locale.
     * This method will be called once for each listening notification template.
     *
     * @param d the metadata object describing this adapter service.
     * @param l the Locale in which the locale-specific metadata should be populated.
     */
    public void fillWmTemplateDescriptor(WmTemplateDescriptor d, Locale l) throws AdapterException
    {

		log(ARTLogger.INFO, 1001, "fillWmTemplateDescriptor", "");
		//First tab
		d.createGroup(wmMQAdapterConstant.SYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.MQMD,
					  new String[] {wmMQAdapterConstant.MQMD_REQUEST_FIELDS,
								   	wmMQAdapterConstant.REQUEST_FIELD_NAMES,
								  	wmMQAdapterConstant.MQMD_REQUEST_CONSTANTS,
									wmMQAdapterConstant.USE_MQMD_REQUEST_FIELDS,
								  	wmMQAdapterConstant.MQMD_REQUEST_FIELD_TYPES,
									wmMQAdapterConstant.MQMD_REPLY_FIELDS,
									wmMQAdapterConstant.USE_MQMD_REPLY_FIELDS,
									wmMQAdapterConstant.REPLY_FIELD_NAMES,
									wmMQAdapterConstant.MQMD_REPLY_FIELD_TYPES,
									wmMQAdapterConstant.MSG_BODY,
									wmMQAdapterConstant.MSG_BODY_NAME,
									wmMQAdapterConstant.MSG_BODY_TYPE,
									wmMQAdapterConstant.REPLY_MSG_BODY,
									wmMQAdapterConstant.REPLY_MSG_BODY_NAME,
									wmMQAdapterConstant.REPLY_MSG_BODY_TYPE,
				                    wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY,
				                    wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_NAME,
				                    wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE,
									wmMQAdapterConstant.QUEUE_MANAGER_NAME,
									wmMQAdapterConstant.QUEUE_MANAGER_NAME_NAME,
									wmMQAdapterConstant.QUEUE_MANAGER_NAME_TYPE,
									wmMQAdapterConstant.QUEUE_NAME,
									wmMQAdapterConstant.QUEUE_NAME_NAME,
									wmMQAdapterConstant.QUEUE_NAME_TYPE
				                    });

		//Third tab
        d.createGroup(wmMQAdapterConstant.SYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS,
					  new String[] {wmMQAdapterConstant.JMS_PROPERTIES,
								   	wmMQAdapterConstant.USE_JMS_PROPERTIES,
								    wmMQAdapterConstant.JMS_PROPERTY_NAMES,
					    			wmMQAdapterConstant.JMS_PROPERTY_TYPES});
        
        d.createGroup(wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.MSG_PROPERTY, new String[]{wmMQAdapterConstant.MSG_PROPERTY_MSGBODY,
				wmMQAdapterConstant.MSG_PROPERTY_MSGBODYBYTEARRAY});
        
		d.setGroupName(wmMQAdapterConstant.USER_DEFINED_PROPERTIES, wmMQAdapterConstant.SYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS);
		d.setGroupName(wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES, wmMQAdapterConstant.SYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS);
		d.setGroupName(wmMQAdapterConstant.USER_DEFINED_PROPERTY_TYPES, wmMQAdapterConstant.SYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS);
		d.setGroupName(wmMQAdapterConstant.REAL_USER_DEFINED_PROPERTY_NAMES, wmMQAdapterConstant.SYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS);        

		
        d.createFieldMap(
				new String[] {wmMQAdapterConstant.MQMD_REQUEST_FIELDS,
					 		  wmMQAdapterConstant.USE_MQMD_REQUEST_FIELDS,
							  wmMQAdapterConstant.REQUEST_FIELD_NAMES,
							  wmMQAdapterConstant.MQMD_REQUEST_CONSTANTS,
							  wmMQAdapterConstant.MQMD_REQUEST_FIELD_TYPES}, true);

        d.createFieldMap(
            new String[] { wmMQAdapterConstant.MQMD_REPLY_FIELDS,
                         wmMQAdapterConstant.USE_MQMD_REPLY_FIELDS,
                           wmMQAdapterConstant.REPLY_FIELD_NAMES,
                           wmMQAdapterConstant.MQMD_REPLY_FIELD_TYPES}, true);

        d.createFieldMap(
				new String[] {wmMQAdapterConstant.JMS_PROPERTIES,
							  wmMQAdapterConstant.USE_JMS_PROPERTIES,
							  wmMQAdapterConstant.JMS_PROPERTY_NAMES,
					    	  wmMQAdapterConstant.JMS_PROPERTY_TYPES}, true);
        d.createFieldMap(new String[] {wmMQAdapterConstant.USER_DEFINED_PROPERTIES, 
								       wmMQAdapterConstant.USER_DEFINED_PROPERTY_TYPES, 
								       wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES, 
								       wmMQAdapterConstant.REAL_USER_DEFINED_PROPERTY_NAMES}, 
					     true, false);
        d.disableAppendAll(wmMQAdapterConstant.USER_DEFINED_PROPERTIES);

        d.createTuple(
            new String[] { wmMQAdapterConstant.MQMD_REQUEST_FIELDS,
                           wmMQAdapterConstant.MQMD_REQUEST_FIELD_TYPES,
                           wmMQAdapterConstant.USE_MQMD_REQUEST_FIELDS
                         });

		d.createTuple(
			new String[] { wmMQAdapterConstant.MQMD_REPLY_FIELDS,
						   wmMQAdapterConstant.MQMD_REPLY_FIELD_TYPES,
						   wmMQAdapterConstant.USE_MQMD_REPLY_FIELDS
						 });

        d.createTuple(
            new String[] { wmMQAdapterConstant.JMS_PROPERTIES,
                           wmMQAdapterConstant.JMS_PROPERTY_TYPES,
                         wmMQAdapterConstant.USE_JMS_PROPERTIES
                         });

       d.createTuple(
            new String[] { wmMQAdapterConstant.MSG_BODY,
                           wmMQAdapterConstant.MSG_BODY_TYPE
                         });

		d.createTuple(
			new String[] { wmMQAdapterConstant.REPLY_MSG_BODY,
						   wmMQAdapterConstant.REPLY_MSG_BODY_TYPE
						 });

		d.createTuple(
            new String[] { wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY,
                           wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE
                         });
		
       d.createTuple(
               new String[] { wmMQAdapterConstant.QUEUE_MANAGER_NAME,
                              wmMQAdapterConstant.QUEUE_MANAGER_NAME_TYPE
                            });
       
       d.createTuple(
               new String[] { wmMQAdapterConstant.QUEUE_NAME,
                              wmMQAdapterConstant.QUEUE_NAME_TYPE
                            });
	       
		d.setHidden(wmMQAdapterConstant.MSG_BODY, true);
		d.setHidden(wmMQAdapterConstant.MSG_BODY_NAME, true);
		d.setHidden(wmMQAdapterConstant.MSG_BODY_TYPE, true);

		d.setHidden(wmMQAdapterConstant.REPLY_MSG_BODY, true);
		d.setHidden(wmMQAdapterConstant.REPLY_MSG_BODY_NAME, true);
		d.setHidden(wmMQAdapterConstant.REPLY_MSG_BODY_TYPE, true);

		d.setHidden(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY, true);
		d.setHidden(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_NAME, true);
		d.setHidden(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE, true);

		d.setHidden(wmMQAdapterConstant.QUEUE_MANAGER_NAME, true);
		d.setHidden(wmMQAdapterConstant.QUEUE_MANAGER_NAME_NAME, true);
		d.setHidden(wmMQAdapterConstant.QUEUE_MANAGER_NAME_TYPE, true);
		
		d.setHidden(wmMQAdapterConstant.QUEUE_NAME, true);
		d.setHidden(wmMQAdapterConstant.QUEUE_NAME_NAME, true);
		d.setHidden(wmMQAdapterConstant.QUEUE_NAME_TYPE, true);
		
		d.setHidden(wmMQAdapterConstant.REQUEST_FIELD_NAMES, true);
        d.setHidden(wmMQAdapterConstant.REPLY_FIELD_NAMES, true);
		d.setHidden(wmMQAdapterConstant.USE_MQMD_REQUEST_FIELDS, true);
		d.setHidden(wmMQAdapterConstant.USE_MQMD_REPLY_FIELDS, true);
		d.setHidden(wmMQAdapterConstant.MQMD_REQUEST_FIELD_TYPES, true);
		d.setHidden(wmMQAdapterConstant.MQMD_REPLY_FIELD_TYPES, true);
  
        d.setHidden(wmMQAdapterConstant.JMS_PROPERTY_TYPES, true);
        d.setHidden(wmMQAdapterConstant.JMS_PROPERTY_NAMES, true);
		d.setHidden(wmMQAdapterConstant.USE_JMS_PROPERTIES, true);
		
		d.setHidden(wmMQAdapterConstant.USER_DEFINED_PROPERTY_TYPES, true);
		d.setHidden(wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES, true);
		d.setHidden(wmMQAdapterConstant.REAL_USER_DEFINED_PROPERTY_NAMES, true);

		//This is stupid, but.....
//		d.setHidden(wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS, true);

		//Note: The WmTemplateDescriptor.OUTPUT_FIELD_NAMES label seems to be 
		//      associated with the RequestDocument for this notification, and the 
		//      WmTemplateDescriptor.INPUT_FIELD_NAMES label seems to be associated
		//      with the ReplyDocument.  

        d.setResourceDomain(wmMQAdapterConstant.MQMD_REQUEST_FIELDS,
                            wmMQAdapterConstant.MQMD_REQUEST_FIELDS,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.MQMD_REQUEST_FIELD_TYPES,
                            wmMQAdapterConstant.MQMD_REQUEST_FIELD_TYPES,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.MQMD_REQUEST_CONSTANTS,
                            wmMQAdapterConstant.MQMD_REQUEST_CONSTANTS,
                            new String[] {wmMQAdapterConstant.MQMD_REQUEST_FIELDS});

        d.setResourceDomain(wmMQAdapterConstant.REQUEST_FIELD_NAMES,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
		                    new String[] {wmMQAdapterConstant.MQMD_REQUEST_FIELDS,
                                          wmMQAdapterConstant.MQMD_REQUEST_FIELD_TYPES});
//                            wmMQAdapterConstant.USE_MQMD_REQUEST_FIELDS);

        d.setResourceDomain(wmMQAdapterConstant.MQMD_REPLY_FIELDS,
                            wmMQAdapterConstant.MQMD_REPLY_FIELDS,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.MQMD_REPLY_FIELD_TYPES,
                            wmMQAdapterConstant.MQMD_REPLY_FIELD_TYPES,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.REPLY_FIELD_NAMES,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
                            new String[] {wmMQAdapterConstant.MQMD_REPLY_FIELDS,
                                          wmMQAdapterConstant.MQMD_REPLY_FIELD_TYPES});
//                            wmMQAdapterConstant.USE_MQMD_REPLY_FIELDS);

        d.setResourceDomain(wmMQAdapterConstant.JMS_PROPERTIES,
                            wmMQAdapterConstant.JMS_PROPERTIES,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.JMS_PROPERTY_TYPES,
                            wmMQAdapterConstant.JMS_PROPERTY_TYPES,
                            null);

     	d.setResourceDomain(wmMQAdapterConstant.JMS_PROPERTY_NAMES,
     						WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
               	            new String[] {wmMQAdapterConstant.JMS_PROPERTIES,
                   	                      wmMQAdapterConstant.JMS_PROPERTY_TYPES});
//                       	    wmMQAdapterConstant.USE_JMS_PROPERTIES);

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
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES,
										  wmMQAdapterConstant.USER_DEFINED_PROPERTY_TYPES });

		d.setResourceDomain(wmMQAdapterConstant.MSG_BODY,
							wmMQAdapterConstant.MSG_BODY,
							null);

		d.setResourceDomain(wmMQAdapterConstant.MSG_BODY_TYPE,
							wmMQAdapterConstant.MSG_BODY_TYPE,
							null);

		d.setResourceDomain(wmMQAdapterConstant.MSG_BODY_NAME,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.MSG_BODY,
										  wmMQAdapterConstant.MSG_BODY_TYPE});

        d.setResourceDomain(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY,
                            wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE,
                            wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE,
                            null);

		d.setResourceDomain(wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_NAME,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY,
										  wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.REPLY_MSG_BODY,
							wmMQAdapterConstant.REPLY_MSG_BODY,
							null);

		d.setResourceDomain(wmMQAdapterConstant.REPLY_MSG_BODY_TYPE,
							wmMQAdapterConstant.REPLY_MSG_BODY_TYPE,
							null);

		d.setResourceDomain(wmMQAdapterConstant.REPLY_MSG_BODY_NAME,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.REPLY_MSG_BODY,
										  wmMQAdapterConstant.REPLY_MSG_BODY_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.QUEUE_MANAGER_NAME,
							wmMQAdapterConstant.QUEUE_MANAGER_NAME,
							null);
		
		d.setResourceDomain(wmMQAdapterConstant.QUEUE_MANAGER_NAME_TYPE,
							wmMQAdapterConstant.QUEUE_MANAGER_NAME_TYPE,
							null);
		
		d.setResourceDomain(wmMQAdapterConstant.QUEUE_MANAGER_NAME_NAME,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.QUEUE_MANAGER_NAME,
										  wmMQAdapterConstant.QUEUE_MANAGER_NAME_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.QUEUE_NAME,
							wmMQAdapterConstant.QUEUE_NAME,
							null);
		
		d.setResourceDomain(wmMQAdapterConstant.QUEUE_NAME_TYPE,
							wmMQAdapterConstant.QUEUE_NAME_TYPE,
							null);
		
		d.setResourceDomain(wmMQAdapterConstant.QUEUE_NAME_NAME,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.QUEUE_NAME,
										  wmMQAdapterConstant.QUEUE_NAME_TYPE});

        //Retrieves the i18n metadata information from
        //the resource bundle and replaces the non-localized metadata.
        //The metadata that needs to be internationalized
        //(the parameter display name, description, group name, etc.)
        //will populate the adapter's administrative interface,
        //Adapter Service Editor, or Adapter Notification Editor.

        d.setDescriptions((wmMQAdapter.getInstance()).getAdapterResourceBundleManager(), l);

    }

	public SyncNotificationResults invokeService(WmRecord rec) throws AdapterException
	{
		log(ARTLogger.INFO, 1001, "invokeService", "");
		IDataCursor idc = null;
		try
		{
			idc = rec.getCursor();
			if ((idc != null) && (idc.first()))
			{
				do {
					String key = idc.getKey();
					Object o = idc.getValue();
					if (o == null)
						log(ARTLogger.INFO, 1003, "key=" + key, "is null");
					else
						log(ARTLogger.INFO, 1003, "key=" + key, "value=" + o.toString());
				}
				while (idc.next());
			}
			else
				log(ARTLogger.INFO, 1003, "idc is null, input=" + rec, "");
		}
		catch (Exception ex)
		{
			//ex.printStackTrace();
		}
		finally
		{
			if (idc != null)
				idc.destroy();
		}
		return super.invokeService(rec);
	}

    /**
     * Check whether this notification is able to handle the listener event
     *
     * @param o the listener event
     * @return ture if able to handle it, otherwise a false
     */
    public boolean supports(Object o)
    {
		log(ARTLogger.INFO, 1001, "supports", "");

		long time = -System.currentTimeMillis();
		
		boolean		thisIsLastNotification = false;
        
		if (o instanceof WmRecord)
        {
			//A condition can arise when a message is received that is not supported by any
			//of the notifications registered to this listener. This is a problem if the 
			//Listener is associated with a transactional connection. The problem is that there
			//is no way for the adapter to indicate to WmART that the transaction should be 
			//rolled back. 
			//
			//As a work-around, wmMQListener.java locates the last enabled notification 
			//registered to this Listener. It then caches a reference to that notification 
			//in the WmRecord. This code segment determines if it is the last notification 
			//registered to the listener. If so, this method will not retun false, even if
			//it should. Instead, it will set the forcingRollback flag to true. Returning
			//true will cause WmART to invoke runNotification().   
			WmNotification last = (WmNotification)((WmRecord)o).get("lastNotification");
			thisIsLastNotification = ((last == this) ? true : false);

			wmMQMessage msg = (wmMQMessage)((WmRecord)o).get("wmmqmessage");
			if (msg == null)
				if (thisIsLastNotification)
				{
					((WmRecord)o).put("forcingRollback", new Boolean(true));
					return true;
				}
				else
					return false;

			String[] fieldnames = getRequestFieldNames();
			String[] constants = getMqmdRequestConstants();
			for (int i = 0 ; i < constants.length ; i++)
				log(ARTLogger.INFO, 1003, "supports", "constants[" + i + "]=" + constants[i]);	
				
			boolean[] use = new boolean[fieldnames.length];
			for (int b = 0 ; b < use.length ; b++)
			{
				if (fieldnames[b].startsWith("msgHeader"))
					fieldnames[b] = fieldnames[b].substring(10); //Strip the 'msgHeader.' prefix
				use[b] = true;
				log(ARTLogger.INFO, 1003, "supports", "fieldname=" + fieldnames[b]);
			}
			try
			{
				Values mqmd = msg.getValues(fieldnames, use);
				wmMQAdapter.dumpValues("supports mqmd", mqmd);
				for (int c = 0 ; c < constants.length ; c++)
				{
					//Trax 1-R4HAX. Modifying the Request Fields table somehow
					//causes the pre-existing constants to contain a "" instead
					//of being null.
					//if (constants[c] == null)
					if ((constants[c] == null) || (constants[c].trim().equals("")))
						continue;
					String value = mqmd.getString(fieldnames[c]);
					if (value ==null)
					{
						log(ARTLogger.INFO, 1003, "supports", "message does not contain " + fieldnames[c]);
						if (thisIsLastNotification)
						{
							((WmRecord)o).put("forcingRollback", new Boolean(true));
							return true;
						}
						else
							return false;
					}
					if (!value.trim().equals(constants[c].trim()))
					{
						log(ARTLogger.INFO, 1003, "supports", "value=" + value.trim() + " does not match constant " + constants[c].trim() + " !!!!!");
						if (thisIsLastNotification)
						{
							((WmRecord)o).put("forcingRollback", new Boolean(true));
							return true;
						}
						else
							return false;
					}
				}
			}
			catch (wmMQException wmMqe)
			{
				//wmMqe.printStackTrace();
			}
			if (thisIsLastNotification)
				((WmRecord)o).put("forcingRollback", new Boolean(false));
			
			log(ARTLogger.INFO, 1003, "supports ", " elapsed time=" + (System.currentTimeMillis() + time));
			log(ARTLogger.INFO, 1002, "supports end", "");
            return true;
        }
        else
        {
			log(ARTLogger.INFO, 1002, "supports end", "");
			if (thisIsLastNotification)
			{
				((WmRecord)o).put("forcingRollback", new Boolean(true));
				return true;
			}
			else
				return false;
        }
    }
    /**
     * Process the notification event and publishes them.
     *
     * @return it always returns null because ansychrnonus listening only publishes
     * the event and does not execute a service.
     */
    @SuppressWarnings("squid:S1541")
    public NotificationResults runNotification(NotificationEvent event) throws ResourceException
    {
		log(ARTLogger.INFO, 1001, "runNotification", "");

		SyncNotificationResults snr = null;

        WmRecord record = (WmRecord)event.getData();
        WmNotification last = (WmNotification)record.get("lastNotification");
		if (last == this)
		{
			//If this flag is set, then none of the registered notifications could support
			//this message, and this is the last enabled notification in the list. So, this
			//code segment will return an AsyncNotificationResults object with its hadError
			//flag set to true. This will cause WmART to roll back the transaction.
			boolean forcingRollback = ((Boolean)record.get("forcingRollback")).booleanValue();
			if (forcingRollback)
			{			
				snr = new SyncNotificationResults(this._notificationNodeName.getFullName(), 
												  false, 
												  null);
				snr.setHadError(true);
				return snr;
			}
		}

		long time = -System.currentTimeMillis();

        wmMQMessage msg = null;
        String reasonCode = "0";
        String conditionCode = "0";
        String queueManagerName = null;
        String queueName = null;
        byte[] emptybytearray = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                             0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        WmRecord output = null;
		IDataCursor idc = null;
        try
        {
            idc = record.getCursor();

            if (idc.first("reasonCode"))
                reasonCode = (String)idc.getValue();

            if (idc.first("conditionCode"))
                conditionCode = (String)idc.getValue();

            if (idc.first("wmmqmessage"))
                msg = (wmMQMessage) idc.getValue();
                
            if (idc.first(wmMQAdapterConstant.QUEUE_MANAGER_NAME))
                queueManagerName = (String) idc.getValue();
            
            if (idc.first(wmMQAdapterConstant.QUEUE_NAME))
                queueName = (String) idc.getValue();
            
               idc.destroy();

            if (msg == null)
                return null;

            output = WmRecordFactory.getFactory().createWmRecord("NotificationOutput");

			boolean[] use = getUseMQMDReplyFields();
			String[] outputFields = getMqmdReplyFields();

            Values header = new Values();
            
            if(wmMQAdapter.isInterospectHeaderDataTypes()){
            	HashMap headerDataTypes = WmMQAdapterUtils.findFieldDataTypes(getReplyDocument());
                msg.setSplHeaderDataTypes(headerDataTypes);
            }
            
            
            msg.copyMsgFieldsToMQMD(header, outputFields);

            wmMQAdapter.dumpValues("Received MQMD", header);

            WmRecord mqmd = WmRecordFactory.getFactory().createWmRecord("msgHeader");
            for (int i = 0 ; i < outputFields.length ; i++)
            {
//                if (use[i])
                {
                    log(ARTLogger.INFO, 1003, "Getting field", outputFields[i]);
                    
                    String fieldname = _outputFieldNames[i];
                    if (fieldname.startsWith("msgHeader."))
                    	fieldname = _outputFieldNames[i].substring(10);
                    //Skip the "msgHeader." prefix from the outputFieldName
                                        Object o = header.get(fieldname);
                    if (o == null)
                    {
                        //Required for Upgrade utility
                    	if (_mqmdOutputFieldTypes[i].equals("string"))
                    	//if (wmMQMQMD.mqmdFieldTypes[i].equals("string"))
                            mqmd.put(fieldname, ""); //Blank field
                        else if (wmMQMQMD.mqmdFieldTypes[i].equals("object"))
                        {
                        	String previousfieldname = _outputFieldNames[i - 1];
                        	if (previousfieldname.startsWith("msgHeader."))
                        		previousfieldname = _outputFieldNames[i - 1].substring(10);
                        	String onestring = (String) header.get(previousfieldname);
							if (onestring != null)
								mqmd.put(fieldname, onestring.getBytes());
                        }
                    }
                    else
                    {
                        mqmd.put(fieldname, o);
                    }
                }
//                else
//                {
//                    if (_mqmdOutputFieldTypes[i].equals("string"))
//                        mqmd.put(outputFields[i].substring(10), ""); //Blank field
//                    else if (_mqmdOutputFieldTypes[i].equals("object"))
//                             mqmd.put(outputFields[i].substring(10), emptybytearray);
//                }
            }


            output.put("reasonCode", new Integer(reasonCode));
            output.put("conditionCode", new Integer(conditionCode));
            output.put(wmMQAdapterConstant.QUEUE_MANAGER_NAME, queueManagerName);
            output.put(wmMQAdapterConstant.QUEUE_NAME, queueName);

            output.put("msgHeader", mqmd);

			wmMQBaseService.copyJMSPropertiesFromMsg(output, msg);

			//Make sure that the msgBody is put into pipeline as an Object
			
			Object msgbody = msg.getMsgBody();
			Object msgbodyByteArray = msg.getMsgBodyByteArray();
			if(isShowMsgBody() && isShowMsgBodyByteArray()){
				 output.put("msgBody", msgbody);
				 output.put("msgBodyByteArray",msgbodyByteArray);
			}else if(isShowMsgBody() && !isShowMsgBodyByteArray()){
				 output.put("msgBody", msgbody);
				 output.put("msgBodyByteArray",null);
			}else if(!isShowMsgBody() && isShowMsgBodyByteArray()) {
				  output.put("msgBody", null);
		          output.put("msgBodyByteArray", msgbodyByteArray);
			}else{
				 output.put("msgBody", null);
		          output.put("msgBodyByteArray", null);
			}
		
			if (msgbody != null)
			{
				log(ARTLogger.VERBOSE1, 1003, "runNotification", "msgBody is " + msgbody.getClass());
				log(ARTLogger.VERBOSE1, 1003, "runNotification", "msgBody=" + msgbody.toString());
			}
			else
				log(ARTLogger.VERBOSE1, 1003, "runNotification", "msgBody is null");


            try
            {
//                doNotify(output);
				//Trax 1-QSH9I. Apparently, super.invokeService() now consumes
            	//exceptions thrown by the message service, and returns a 
            	//SyncNotificationResults object indicating an error.
            	//replaced:
            	//invokeService(output);
                //snr = new SyncNotificationResults(
                //  this._notificationNodeName.getFullName(), true, null);
            	//with:
            	snr = invokeService(output);
            }
            catch (Exception e)
            {
                //t.printStackTrace();
            	snr = new SyncNotificationResults(
                  this._notificationNodeName.getFullName(), false, e);
                snr.setHadError(true);
            }
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            throw wmMQAdapter.getInstance().createAdapterException(1007, e);
        }
        finally
        {
        	if (idc != null)
        		idc.destroy();
    		if(record != null){
    			log(ARTLogger.INFO, 1003, "runNotification ", "destorying input wmrecord explicitly.....");
        		record.destroy();
        	}
        	if(output != null){
        		log(ARTLogger.INFO, 1003, "runNotification ", "destorying output wmrecord explicitly.....");
        		output.destroy();
        	}
        }
		log(ARTLogger.INFO, 1003, "runNotification ", "elapsed time=" + (System.currentTimeMillis() + time));
		log(ARTLogger.INFO, 1002, "runNotification", "");
		return snr;
    }

    protected void log(int level, int minor, String arg0, String arg1)
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

	public void setShowMsgBody(boolean showMsgBody) {
		this.showMsgBody = showMsgBody;
	}

	public boolean isShowMsgBody() {
		return showMsgBody;
	}

	public void setShowMsgBodyByteArray(boolean showMsgBodyByteArray) {
		this.showMsgBodyByteArray = showMsgBodyByteArray;
	}

	public boolean isShowMsgBodyByteArray() {
		return showMsgBodyByteArray;
	}
	
	/**
	 * Ideally we have to introspect on the request Document, but not sure why
	 * the Reply fields are populated while running the Sync notification. As
	 * the reasons are unknown follow the existing path. 
	 * 
	 * @return
	 */
	private NSRecord getReplyDocument() {

		NSRecord nsRec = (NSRecord) Namespace.current().getNode(
				this._repRecNodeName);
		
		return nsRec;
	}
	
	
}