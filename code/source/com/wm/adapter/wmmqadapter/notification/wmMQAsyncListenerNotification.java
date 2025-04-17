/*
 * wmMQAsyncListenerNotification.java
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

import com.ibm.mq.MQC;
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
import com.wm.adk.notification.AsyncNotificationResults;
import com.wm.adk.notification.NotificationEvent;
import com.wm.adk.notification.NotificationResults;
import com.wm.adk.notification.WmAsyncListenerNotification;
import com.wm.adk.notification.WmNotification;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.util.tx.TXJobId;
import com.wm.data.IDataCursor;
import com.wm.lang.ns.NSRecord;
import com.wm.util.Values;

/*
 * This class is the implementation of the asynchronous listener notification template
 * for the WmSampleAdapter. It receive listener notification event of the types of either
 * check deposit or under balance wanring, and publishes the message.
 *
 * ????Note: The notification template provides callback methods to interact with the
 * Integration Server when the state of a notification changes. But this simple
 * example of a notification does not make use of these callbacks. For more
 * information on the available callbacks, refer to the Javadoc of the
 * WmNotification class.
 */
public class wmMQAsyncListenerNotification extends WmAsyncListenerNotification
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
	protected String[] _inputFieldNames = wmMQMQMD.mqmdFieldDisplayNames;

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

//	private boolean		forcingRollback = false;
//	private boolean		thisIsLastNotification = false;

    /* 
     * Default constructor
     */
    public wmMQAsyncListenerNotification() {
        super();
		_mqmd = new wmMQMQMD();
    }

	/*
	 * Gets the input field names.
	 *
	 */
	public String[] getInputFieldNames()
	{
		return wmMQMQMD.mqmdFieldDisplayNames;
	}

	/*
	 * Sets the input field names.
	 *
	 * The inputFieldNames parameter is the input field names.
	 *
	 */
	public void setInputFieldNames(String[] inputFieldNames)
	{
		_mqmd.setMqmdInputFields(inputFieldNames);
	}

	/*
	 * Gets the input field names.
	 *
	 */
	public String[] getFilterCriteria()
	{
		log(ARTLogger.INFO, 1001, "getFilterCriteria", "");
		return _inputFieldNames;
	}

	/*
	 * Sets the input field names.
	 *
	 * The inputFieldNames parameter is the input field names.
	 *
	 */
	public void setFilterCriteria(String[] inputFieldNames)
	{
		log(ARTLogger.INFO, 1001, "setFilterCriteria", "");
		_inputFieldNames = inputFieldNames;
		log(ARTLogger.INFO, 1002, "setFilterCriteria", "");
	}
/*
	public void setMqmdInputFields(String[] fields)
	{
		_mqmd.setMqmdInputFields(fields);
	}

	public String[] getMqmdInputFields()
	{
		return wmMQMQMD.mqmdFieldDisplayNames;
	}
*/
	public void setMqmdInputConstants(String[] constants)
	{
		_mqmd.setMqmdInputConstants(constants);
	}

	public void setMqmdInputFieldTypes(String[] types)
	{
		_mqmd.setMqmdInputFieldTypes(types);
	}

	public void setUseMQMDFields(boolean[] use)
	{
		log(ARTLogger.INFO, 1001, "setUseMQMDFields", "");
		_mqmd.setUseMQMDFields(use);
		log(ARTLogger.INFO, 1002, "setUseMQMDFields", "");
   }

   public boolean[] getUseMQMDFields()
   {
	   log(ARTLogger.INFO, 1001, "getUseMQMDFields", "");
	   return _mqmd.getUseMQMDFields();
   }

	public String[] getMqmdInputConstants()
	{
		return _mqmd.getMqmdInputConstants();
	}

	public String[] getMqmdInputFieldTypes()
	{
		return _mqmd.getMqmdInputFieldTypes();
	}

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
        _msgBodyByteArrayType = msgbodyByteArraytype; //NOSONAR
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
		return this._queueManagerNameType; //NOSONAR
	}

	public void setQueueManagerNameType(String[] queueManagerNameType) {
		this._queueManagerNameType = queueManagerNameType; //NOSONAR
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
		return this._queueNameType; //NOSONAR
	}

	public void setQueueNameType(String[] queueNameType) {
		this._queueNameType = queueNameType; //NOSONAR
	}
	
    public void setMqmdOutputFields(String[] fields)
    {
		log(ARTLogger.INFO, 1001, "setMqmdOutputFields", "");
        _mqmd.setMqmdOutputFields(fields);
		log(ARTLogger.INFO, 1002, "setMqmdOutputFields", "");
    }
/*
    public void setUseOutputMQMDFields(boolean[] use)
    {
		log(ARTLogger.INFO, 1001, "setUseOutputMQMDFields", "");
        _mqmd.setUseOutputMQMDFields(use);
		log(ARTLogger.INFO, 1002, "setUseOutputMQMDFields", "");
    }
*/
    public void setMqmdOutputFieldTypes(String[] types)
    {
		log(ARTLogger.INFO, 1001, "setMqmdOutputFieldTypes", "");
        _mqmd.setMqmdOutputFieldTypes(types);
		log(ARTLogger.INFO, 1002, "setMqmdOutputFieldTypes", "");
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
        return _msgBodyType; //NOSONAR
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
        _msgBodyType = msgbodytype; //NOSONAR
		log(ARTLogger.INFO, 1002, "setMsgBodyType", "");
    }

    public String[] getMqmdOutputFields()
    {
	log(ARTLogger.INFO, 1001, "getMqmdOutputFields", "");
       return _mqmd.getMqmdOutputFields();
    }
/*
    public boolean[] getUseOutputMQMDFields()
    {
		log(ARTLogger.INFO, 1001, "getUseOutputMQMDFields", "");
        return _mqmd.getUseOutputMQMDFields();
    }
*/
    public String[] getMqmdOutputFieldTypes()
    {
		log(ARTLogger.INFO, 1001, "getMqmdOutputFieldTypes", "");
        return _mqmd.getMqmdOutputFieldTypes();
    }

    /*
     * Gets the JMS property names.
     *
     */
    public String[] getJmsPropertyNames()
    {
		log(ARTLogger.INFO, 1001, "getJmsPropertyNames", "");
        return _jmsPropertyNames; //NOSONAR
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
        _jmsPropertyNames = jmsPropertyNames; //NOSONAR
		log(ARTLogger.INFO, 1002, "setJmsPropertyNames", "");
    }

    /*
     * Gets the input field names.
     *
     */
    public String[] getOutputFieldNames()
    {
		log(ARTLogger.INFO, 1001, "getOutputFieldNames", "");
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
		log(ARTLogger.INFO, 1001, "setOutputFieldNames", "");
        _outputFieldNames = outputFieldNames;
		log(ARTLogger.INFO, 1002, "setOutputFieldNames", "");
    }

    public String[] getJmsProperties()
    {
		log(ARTLogger.INFO, 1001, "setOutputFieldNames", "");
        return _JMSProperties; //NOSONAR
    }

    public void setJmsProperties(String[] jmsproperties)
    {
		log(ARTLogger.INFO, 1001, "setJmsProperties", "");
       _JMSProperties = jmsproperties; //NOSONAR
	   log(ARTLogger.INFO, 1002, "setJmsProperties", "");
    }

    public String[] getJmsPropertyTypes()
    {
		log(ARTLogger.INFO, 1001, "getJmsPropertyTypes", "");
        return _JMSPropertyTypes; //NOSONAR
    }

    public void setJmsPropertyTypes(String[] types)
    {
		log(ARTLogger.INFO, 1001, "setJmsPropertyTypes", "");
        _JMSPropertyTypes = types;  //NOSONAR
		log(ARTLogger.INFO, 1002, "setJmsPropertyTypes", "");
   }

    public boolean[] getUseJmsProperties()
    {
		log(ARTLogger.INFO, 1001, "getUseJmsProperties", "");
        return _useJMSProperties; //NOSONAR
    }

    public void setUseJmsProperties(boolean[] use)
    {
		log(ARTLogger.INFO, 1001, "setUseJmsProperties", "");
        _useJMSProperties = use; //NOSONAR
		log(ARTLogger.INFO, 1002, "setUseJmsProperties", "");
    }

	public String[] getUserDefinedProperties()
	{
		log(ARTLogger.INFO, 1001, "getUserDefinedProperties", "");
		return _userDefinedProperties; //NOSONAR
	}

	public void setUserDefinedProperties(String[] userDefinedProperties)
	{
		log(ARTLogger.INFO, 1001, "setUserDefinedProperties", "");
		_userDefinedProperties = userDefinedProperties; //NOSONAR
		log(ARTLogger.INFO, 1002, "setUserDefinedProperties", "");
	}

	public String[] getUserDefinedPropertyNames()
	{
		log(ARTLogger.INFO, 1001, "getUserDefinedPropertyNames", "");
		return _userDefinedPropertyNames; //NOSONAR
	}

	public void setUserDefinedPropertyNames(String[] userDefinedPropertyNames)
	{
		log(ARTLogger.INFO, 1001, "setUserDefinedPropertyNames", "");
		_userDefinedPropertyNames = userDefinedPropertyNames;  //NOSONAR
		log(ARTLogger.INFO, 1002, "setUserDefinedPropertyNames", "");
	}

	public String[] getRealUserDefinedPropertyNames()
	{
		log(ARTLogger.INFO, 1001, "getRealUserDefinedPropertyNames", "");
		return _realUserDefinedPropertyNames; //NOSONAR
	}

	public void setRealUserDefinedPropertyNames(String[] realUserDefinedPropertyNames)
	{
		log(ARTLogger.INFO, 1001, "setRealUserDefinedPropertyNames", "");
		_realUserDefinedPropertyNames = realUserDefinedPropertyNames; //NOSONAR
		log(ARTLogger.INFO, 1002, "setRealUserDefinedPropertyNames", "");
	}

	public String[] getUserDefinedPropertyTypes()
	{
		log(ARTLogger.INFO, 1001, "getUserDefinedPropertyTypes", "");
		return _userDefinedPropertyTypes; //NOSONAR
	}

	public void setUserDefinedPropertyTypes(String[] userDefinedPropertyTypes)
	{
		log(ARTLogger.INFO, 1001, "setUserDefinedPropertyTypes", "");
		_userDefinedPropertyTypes = userDefinedPropertyTypes; //NOSONAR
		log(ARTLogger.INFO, 1002, "setUserDefinedPropertyTypes", "");
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
		d.createGroup(wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.MQMD,
					  new String[] {wmMQAdapterConstant.FILTER_CRITERIA,
									wmMQAdapterConstant.INPUT_FIELD_NAMES,
									wmMQAdapterConstant.MQMD_INPUT_CONSTANTS,
									wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS,
									wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES,
					  				wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
//									wmMQAdapterConstant.USE_MQMD_OUTPUT_FIELDS,
									wmMQAdapterConstant.OUTPUT_FIELD_NAMES,
									wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES,
									wmMQAdapterConstant.MSG_BODY,
									wmMQAdapterConstant.MSG_BODY_NAME,
				                    wmMQAdapterConstant.MSG_BODY_TYPE,
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
        d.createGroup(wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS,
					  new String[] {wmMQAdapterConstant.JMS_PROPERTIES,
								   	wmMQAdapterConstant.USE_JMS_PROPERTIES,
								    wmMQAdapterConstant.JMS_PROPERTY_NAMES,
					    			wmMQAdapterConstant.JMS_PROPERTY_TYPES});
        
        d.createGroup(wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.MSG_PROPERTY, new String[]{wmMQAdapterConstant.MSG_PROPERTY_MSGBODY,
        											wmMQAdapterConstant.MSG_PROPERTY_MSGBODYBYTEARRAY});
        
		d.setGroupName(wmMQAdapterConstant.USER_DEFINED_PROPERTIES, wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS);
		d.setGroupName(wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES, wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS);
		d.setGroupName(wmMQAdapterConstant.USER_DEFINED_PROPERTY_TYPES, wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS);
		d.setGroupName(wmMQAdapterConstant.REAL_USER_DEFINED_PROPERTY_NAMES, wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS);

		
        d.createFieldMap(
            new String[] { wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
//                           wmMQAdapterConstant.USE_MQMD_OUTPUT_FIELDS,
                           wmMQAdapterConstant.OUTPUT_FIELD_NAMES,
                           wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES
                         }, true);

		d.createFieldMap(
			new String[] {wmMQAdapterConstant.FILTER_CRITERIA,
						  wmMQAdapterConstant.INPUT_FIELD_NAMES,
				 		  wmMQAdapterConstant.MQMD_INPUT_CONSTANTS,
						  wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS,
						  wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES
						 }, false);

       d.createFieldMap(
			new String[] {wmMQAdapterConstant.JMS_PROPERTIES,
//						  wmMQAdapterConstant.USE_JMS_PROPERTIES,
							  wmMQAdapterConstant.JMS_PROPERTY_NAMES,
					    	  wmMQAdapterConstant.JMS_PROPERTY_TYPES}, true);
       
       d.createFieldMap(new String[] {wmMQAdapterConstant.USER_DEFINED_PROPERTIES, 
								      wmMQAdapterConstant.USER_DEFINED_PROPERTY_TYPES, 
								      wmMQAdapterConstant.USER_DEFINED_PROPERTY_NAMES, 
								      wmMQAdapterConstant.REAL_USER_DEFINED_PROPERTY_NAMES}, 
					    true, false);
       d.disableAppendAll(wmMQAdapterConstant.USER_DEFINED_PROPERTIES);

        d.createTuple(
            new String[] { wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
                           wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES //,
//                           wmMQAdapterConstant.USE_MQMD_OUTPUT_FIELDS
                         });
		
		d.createTuple(
			new String[] { wmMQAdapterConstant.FILTER_CRITERIA,
						   wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES,
						   wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS
						 });

        d.createTuple(
            new String[] { wmMQAdapterConstant.JMS_PROPERTIES,
                           wmMQAdapterConstant.JMS_PROPERTY_TYPES //,
//                           wmMQAdapterConstant.USE_JMS_PROPERTIES
                         });

       d.createTuple(
            new String[] { wmMQAdapterConstant.MSG_BODY,
                           wmMQAdapterConstant.MSG_BODY_TYPE
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
	       
		d.setHidden(wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES, true);
        d.setHidden(wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES, true);
        d.setHidden(wmMQAdapterConstant.OUTPUT_FIELD_NAMES, true);
		d.setHidden(wmMQAdapterConstant.INPUT_FIELD_NAMES, true);
//		d.setHidden(wmMQAdapterConstant.USE_MQMD_OUTPUT_FIELDS, true);

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

		d.setHidden(wmMQAdapterConstant.QUEUE_MANAGER_NAME, true);
		d.setHidden(wmMQAdapterConstant.QUEUE_MANAGER_NAME_NAME, true);
		d.setHidden(wmMQAdapterConstant.QUEUE_MANAGER_NAME_TYPE, true);
		
		d.setHidden(wmMQAdapterConstant.QUEUE_NAME, true);
		d.setHidden(wmMQAdapterConstant.QUEUE_NAME_NAME, true);
		d.setHidden(wmMQAdapterConstant.QUEUE_NAME_TYPE, true);
		
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

        d.setResourceDomain(wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
                            wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES,
                            wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES,
                            null);

        d.setResourceDomain(wmMQAdapterConstant.OUTPUT_FIELD_NAMES,
                            WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
                            new String[] {wmMQAdapterConstant.MQMD_OUTPUT_FIELDS,
                                          wmMQAdapterConstant.MQMD_OUTPUT_FIELD_TYPES}); 
//                            wmMQAdapterConstant.USE_MQMD_OUTPUT_FIELDS);

		d.setResourceDomain(wmMQAdapterConstant.FILTER_CRITERIA,
							wmMQAdapterConstant.FILTER_CRITERIA,
							null);

		d.setResourceDomain(wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES,
							wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES,
							null);

		d.setResourceDomain(wmMQAdapterConstant.MQMD_INPUT_CONSTANTS,
							wmMQAdapterConstant.MQMD_INPUT_CONSTANTS,
							new String[] {wmMQAdapterConstant.FILTER_CRITERIA});

//		d.setResourceDomain(wmMQAdapterConstant.INPUT_FIELD_NAMES,
//							WmTemplateDescriptor.INPUT_FIELD_NAMES,
//		null);
//							new String[] {wmMQAdapterConstant.MQMD_INPUT_FIELDS,
//										  wmMQAdapterConstant.MQMD_INPUT_FIELD_TYPES}, null);
//							  wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS);

//		d.setValidValues(WmTemplateDescriptor.INPUT_FIELD_NAMES, wmMQMQMD.mqmdFieldDisplayNames);

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
//                      	    wmMQAdapterConstant.USE_JMS_PROPERTIES);

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

    /**
     * Check whether this notification is able to handle the listener event
     *
     * @param o the listener event
     * @return ture if able to handle it, otherwise a false
     */
	@SuppressWarnings("squid:S1541")
    public boolean supports(Object o)
    {
		log(ARTLogger.INFO, 1001, "supports", "");

		long time = -System.currentTimeMillis();

		boolean thisIsLastNotification = false;
		
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
			//it should. Instead, it will set a forcingRollback flag in the record. Returning
			//true will cause WmART to invoke runNotification(), which will evaluate the
        	//forcingRollback proptery and return an error.   
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

			String[] fieldnames = getFilterCriteria();
			String[] constants = getMqmdInputConstants();
			boolean[] use = getUseMQMDFields();
			for (int b = 0 ; b < use.length ; b++)
			{
				if (fieldnames[b].startsWith("msgHeader"))
					fieldnames[b] = fieldnames[b].substring(10); //Strip the 'msgHeader.' prefix
				log(ARTLogger.INFO, 1003, "supports", "fieldname=" + fieldnames[b] + ", use=" + use[b]);
			}

			try
			{
				Values mqmd = msg.getValues(fieldnames, use);
				wmMQAdapter.dumpValues("supports mqmd", mqmd);
				for (int c = 0 ; c < constants.length ; c++)
				{
					if (use[c])
					{
						String value = mqmd.getString(fieldnames[c]);
						log(ARTLogger.INFO, 1003, "supports", "field=" + fieldnames[c] + ", value=" + value);
						if (value == null)
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
						if (constants[c].trim().equals(""))
							continue;

						if (fieldnames[c].equals(wmMQAdapterConstant.MQMD_CHARACTER_SET))
						{
							//These two values can never be retruned by the MQGet, so
							//ignore them.
							if ((constants[c].equals("MQCCSI_Q_MGR")) ||
								(constants[c].equals("MQCCSI_INHERIT")) )
								continue;
							
							int ccsid = Integer.parseInt(value);
							if (ccsid != WmMQAdapterUtils.getCharacterSetId(constants[c]))
								if (thisIsLastNotification)
								{
									((WmRecord)o).put("forcingRollback", new Boolean(true));
									return true;
								}
								else
									return false;
						}
						//There are 2 entries that start with 00273 - they are equal.
						if	(fieldnames[c].equals(wmMQAdapterConstant.MQMD_ENCODING))
						{
							if ((constants[c].startsWith("00273")) &&
								(value.startsWith("00273")))
								continue;
						}

						if ((constants[c].indexOf("NONE") > -1) &&
							(value.indexOf("NONE") > -1) )
							continue;
						
						
						if (!value.trim().equals(constants[c].trim()))
						{
							log(ARTLogger.INFO, 1003, "supports", "value=" + value.trim() + " does not match constant value " + constants[c].trim() + "!!!!!");
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

		AsyncNotificationResults asnr = null;

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
				asnr = new AsyncNotificationResults(this._notificationNodeName.getFullName(), 
													false, 
													null);
				asnr.setHadError(true); 
				return asnr;
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

//			boolean[] use = getUseOutputMQMDFields();
			String[] outputFields = getMqmdOutputFields();

            Values header = new Values();
            
            if(wmMQAdapter.isInterospectHeaderDataTypes()){
            	HashMap headerDataTypes = WmMQAdapterUtils.findFieldDataTypes(getDocument());
                msg.setSplHeaderDataTypes(headerDataTypes);
            }
            
            
            msg.copyMsgFieldsToMQMD(header, outputFields);

            wmMQAdapter.dumpValues("Received MQMD", header);

            WmRecord mqmd = WmRecordFactory.getFactory().createWmRecord("msgHeader");
            for (int i = 0 ; i < outputFields.length ; i++)
            {
//                if (use[i])
                {
                    log(ARTLogger.INFO, 1003, "Getting field", _outputFieldNames[i]);
                    //Skip the "msgHeader." prefix from the outputFieldName
                    Object o = header.get(outputFields[i].substring(10));
                    if (o == null)
                    {
                        if (_mqmdOutputFieldTypes[i].equals("string"))
                            mqmd.put(outputFields[i].substring(10), ""); //Blank field
                        else if (outputFields[i].equals("object"))
                        {
                            mqmd.put(outputFields[i].substring(10),
                                     ( (String) header.get(outputFields[i - 1].substring(10))).getBytes());
                        }
                    }
                    else
                    {
                        mqmd.put(_outputFieldNames[i].substring(10), o);
                    }
                }
            }

            output.put("reasonCode", new Integer(reasonCode));
            output.put("conditionCode", new Integer(conditionCode));
            output.put(wmMQAdapterConstant.QUEUE_MANAGER_NAME, queueManagerName);
            output.put(wmMQAdapterConstant.QUEUE_NAME, queueName);

            output.put("msgHeader", mqmd);

			wmMQBaseService.copyJMSPropertiesFromMsg(output, msg);
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
			//Make sure that the msgBody is put into pipeline as an Object
          
          

			if (msgbody != null)
			{
				log(ARTLogger.VERBOSE1, 1003, "runNotification", "msgBody is " + msgbody.getClass());
				log(ARTLogger.VERBOSE1, 1003, "runNotification", "msgBody=" + msgbody.toString());
			}
			else
				log(ARTLogger.VERBOSE1, 1003, "runNotification", "msgBody is null");

            try
            {
            	//changes related to IMQ-923
            	String uuid=null;
            	
            	if(!(wmMQAdapter.getMsgIDBasedUUID())){ 
                	uuid=TXJobId.get();   	
                }else{
	                //Create a unique identifier from the msgId. Assume that the msgId is unique.
	                byte[] msgid = msg.getMQMessage().messageId;
					StringBuffer sb = new StringBuffer("");
	
					for (int i = 0 ; i < msgid.length ; i++)
					{
						int onebyte = msgid[i];
						if (onebyte < 0)
							//Trax 1-TE7MG - wrong way to trim high order bit.
							//onebyte = -onebyte % 256; 
							onebyte += 256;  
						if (onebyte < 16)
							sb.append("0");
						sb.append(Integer.toHexString(onebyte).toUpperCase());
					}
					uuid=sb.toString();
                }
                log(ARTLogger.INFO, 1003, "runNotification", "publishing message with uuid= " + uuid);
              
                
                // Message Header logging based on property set to true
                String prop1 = System.getProperty("watt.WmMQAdapter.Notification.LogMessageHeaders");
                if(prop1!=null && prop1.equalsIgnoreCase("true")){
                	wmMQAdapter.dumpNotificationHeaders(prop1, "Received MQMD", header, uuid);
                }
                
                doNotify(output, uuid);
              
                asnr = new AsyncNotificationResults(
                  this._notificationNodeName.getFullName(), true, null);
            }
            catch (Exception e)
            {
                asnr = new AsyncNotificationResults(
                  this._notificationNodeName.getFullName(), false, e);
                asnr.setHadError(true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            WmMQAdapterUtils.logException(e);
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
		return asnr;
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
	
	private NSRecord getDocument(){
		
		NSRecord nsRec = (NSRecord)Namespace.current().getNode(this._pubRecNodeName);
		return nsRec;
	}
}