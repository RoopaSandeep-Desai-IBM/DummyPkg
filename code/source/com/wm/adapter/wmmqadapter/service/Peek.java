/*
 * Peek.java
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
 * This class is an implementation of the Peek service template
 * for the wmMQAdapter. The Peek service retrieves a copy of message
 * without removing it from the queue. */
public class Peek extends wmMQBaseService
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 2487760436052315723L;

	//SharedMode property
    private boolean _sharedMode = true;

    //ConvertDataOption property
    private boolean _convertDataOption = true;
    
	//resetCursor property
	private String _resetCursor = "";

	//resetCursor type
	private String[] _resetCursorType = {"java.lang.String"};

	//resetCursor property
	private String _resetCursorName = "resetCursor";

    /*
     * Constructor.
     */
    public Peek()
    {
        _serviceName = wmMQAdapterConstant.PEEK_SERVICE;
		_overrideConnection = new wmMQOverrideConnection(_serviceName);
    }

	/*
	 * Gets the input field names. For Get/Peek services, these are
	 * known as 'Selection Criteria'
	 *
	 */
	public String[] getSelectionCriteria()
	{
		return _inputFieldNames;
	}

	/*
	 * Sets the input field names. For Get/Peek services, these are
	 * known as 'Selection Criteria'
	 *
	 * The inputFieldNames parameter is the input field names.
	 *
	 */
	public void setSelectionCriteria(String[] inputFieldNames)
	{
		_inputFieldNames = inputFieldNames;
	}

	/*
	 * Gets the resetCursor property. 
	 *
	 */
	public String getResetCursor()
	{
		return _resetCursor;
	}

	/*
	 * Sets the resetCursor property. 
	 *
	 * The resetcursor parameter is the property value.
	 *
	 */
	public void setResetCursor(String resetcursor)
	{
		_resetCursor = resetcursor;
	}

	/*
	 * Gets the resetCursor property type. 
	 *
	 */
	public String[] getResetCursorType()
	{
		return _resetCursorType; //NOSONAR
	}

	/*
	 * Sets the resetCursor property type. 
	 *
	 * The resetcursor parameter is the list of property types.
	 *
	 */
	public void setResetCursorType(String[] resetcursortypes)
	{
		_resetCursorType = resetcursortypes; //NOSONAR
	}

	/*
	 * Gets the resetCursorName property. 
	 *
	 */
	public String getResetCursorName()
	{
		return _resetCursorName;
	}

	/*
	 * Sets the resetCursor property. 
	 *
	 * The resetcursor parameter is the property value.
	 *
	 */
	public void setResetCursorName(String resetcursorname)
	{
		_resetCursorName = resetcursorname;
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
		log(ARTLogger.INFO, 1001, "Peek.fillWmTemplateDescriptor", "");

		overrideServiceGroupFieldNames(_serviceGroupFieldNames);
		overrideMQMDGroupFieldName(0, wmMQAdapterConstant.SELECTION_CRITERIA);
		overrideMQMDInputFieldMapName(0, wmMQAdapterConstant.SELECTION_CRITERIA);
		overrideMQMDInputTupleName(0, wmMQAdapterConstant.SELECTION_CRITERIA);
		overrideMQMDInputFieldsLabel(wmMQAdapterConstant.SELECTION_CRITERIA);

		super.fillWmTemplateDescriptor(d, l);

		d.setHidden(wmMQAdapterConstant.DEAD_LETTER_QUEUE, true);
		d.setHidden(wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER, true);
		d.setHidden(wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS, true);
		d.setHidden(wmMQAdapterConstant.SHARED_MODE, true);
		d.setHidden(wmMQAdapterConstant.BACKOUT_THRESHHOLD, true);
		//d.setHidden(wmMQAdapterConstant.RESET_CURSOR, true);
		d.setHidden(wmMQAdapterConstant.RESET_CURSOR_TYPE, true);
		d.setHidden(wmMQAdapterConstant.RESET_CURSOR_NAME, true);
		d.setHidden(wmMQAdapterConstant.MQMD_INPUT_FIELDS, true);

		d.createTuple( new String[] {wmMQAdapterConstant.RESET_CURSOR, 
									 wmMQAdapterConstant.RESET_CURSOR_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.RESET_CURSOR,
							wmMQAdapterConstant.RESET_CURSOR,
							null);

		d.setResourceDomain(wmMQAdapterConstant.RESET_CURSOR_TYPE,
							wmMQAdapterConstant.RESET_CURSOR_TYPE,
							null);

		d.setResourceDomain(wmMQAdapterConstant.RESET_CURSOR_NAME,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
				new String[] {wmMQAdapterConstant.RESET_CURSOR_TYPE});
//							  wmMQAdapterConstant.RESET_CURSOR});
	}

    /*
     * Peeks at an MQMessage on an MQSeries Queue.
     * connection is the connection handle.
     * input is the input record of the adapter service.
     */
	@SuppressWarnings("squid:S1541")
    public WmRecord execute(WmManagedConnection connection, WmRecord input) throws ResourceException
    {
        log(ARTLogger.INFO, 1001, "peek:execute()", "");
        IDataCursor idc = null;
        try
        {
            idc = input.getCursor();
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
                log(ARTLogger.INFO, 1003, "idc is null, input=" + input, "");
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

        //The connection handle.
        wmMQConnection conn = (wmMQConnection)connection; 

        //The output record to return.
        WmRecord output = WmRecordFactory.getFactory().createWmRecord("Output");

		boolean resetCursor = false;

        try
        {

			resetCursor = getResetCursor().equalsIgnoreCase("yes");
			idc = input.getCursor();
			if ((idc != null) && (idc.first("resetCursor")))
			{
				resetCursor = (((String)idc.getValue()).equals("yes")) ? true : false;
			}
			String queueName = _overrideConnection.getPropertyFromInput(input, wmMQOverrideConnection.QUEUE_NAME_LABEL);
			if(queueName == null || queueName.equals("")) {
				queueName = conn.getResolvedQueueName(false);
			}

            //Gets the connection handle.
			conn = (wmMQConnection) _overrideConnection.overrideConnection(connection, input, true);

            if (conn instanceof wmMQTransactionalConnection)
            {
				log(ARTLogger.ERROR, 1041, "", "");
				output.put("errormsg", wmMQException.format("1041", new Object[0]));
				conn.setInUse(false);
				return output; 
			}
             
            wmMQMessage msg = new wmMQMessage();

			//Pre-set the messageSequenceNumber to 0 so that the code can
			//determine if the user wants to match on sequence number.
			msg.getMQMessage().messageSequenceNumber = 0;

			//Pre-set the offset to -1 so that the code can
			//determine if the user wants to match on offset = 0.
			msg.getMQMessage().offset = -1;

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
			String[] constants = _mqmd.getMqmdInputConstants();
			for (int i = 0 ; i < constants.length ; i++)
			{
				if ((constants[i] != null) && (_inputFieldNames[i].length() > 0))
				{
					//Skip the "selectionCriteria." prefix from the outputFieldName
					header.put(_inputFieldNames[i].substring(18), constants[i]);
				}
			}

            msg.copyMQMDFieldsToMsg((WmRecord)input.get("selectionCriteria"),
                                    header,
                                    _inputFieldNames);

            int reasonCode = 0;

            try
            {
                log(ARTLogger.INFO, 1003, "Peeking at message", "");
                
        		int waitInterval = getWaitInterval();
				try {
					waitInterval = input.getInt(wmMQAdapterConstant.WAIT_INTERVAL);
				} catch (AdapterException e) {
					// Take the default value provided at the design time
				}
				
               reasonCode = conn.peek(msg, getSharedMode(), waitInterval, resetCursor, getConvertDataOption());
                log(ARTLogger.INFO, 1003, "Peeked at message", "");
            }
            catch (wmMQException mqe)
            {
                //mqe.printStackTrace();
                log(ARTLogger.ERROR,
                    1055,
                    "Peeking at messsage",
                    mqe.getMessage());
                if (getThrowExceptionOnFailure())
                {
                    if (WmMQAdapterUtils.fatalException(((MQException)mqe.getLinkedException()).reasonCode))
                        throw wmMQAdapter.getInstance().createAdapterConnectionException(1055, 
                        																 new String[]{"Get", mqe.getMessage()}, 
                        																 mqe);
                    else
                        throw mqe;
                }
                output.put("conditionCode", "" + ((MQException)mqe.getLinkedException()).completionCode);
                output.put("reasonCode", "" + ((MQException)mqe.getLinkedException()).reasonCode);
				conn.setInUse(false);
                return output;
            }

            header = new Values();
            
            if(wmMQAdapter.isInterospectHeaderDataTypes()){
            	HashMap headerDataTypes = WmMQAdapterUtils.findFieldDataTypes(getOutSignature());
                msg.setSplHeaderDataTypes(headerDataTypes);
            }
            
            
            //msg.copyMsgFieldsToMQMD(header, _outputFieldNames, _mqmd.getUseOutputMQMDFields());
			msg.copyMsgFieldsToMQMD(header, _outputFieldNames);

            wmMQAdapter.dumpValues("Received MQMD", header);

            boolean[] use = _mqmd.getUseOutputMQMDFields();

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
                    log(ARTLogger.INFO, 1003, "Getting field", _outputFieldNames[i]);
                    //Skip the "msgHeader." prefix from the outputFieldName
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
                    1040,
                    "" + reasonCode,
                    "" + MQConstants.MQCC_WARNING);
                output.put("conditionCode", "" + MQConstants.MQCC_WARNING);
            }

			copyJMSPropertiesFromMsg(output, msg);

			//Make sure that the msgBody is put into pipeline as an Object
			Object body = msg.getMsgBody();
			output.put("msgBody", body);
            output.put("msgBodyByteArray", msg.getMsgBodyByteArray());

			_overrideConnection.fillOverriddenConnection(conn, output, true);
            conn.setInUse(false);
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
            t.printStackTrace();
            WmMQAdapterUtils.logException(t);
            log(ARTLogger.ERROR,
                1055,
                "Peeking at message",
                t.getMessage());

			if (conn != null)
				conn.setInUse(false);
			if (getThrowExceptionOnFailure())
			{
				if (t instanceof AdapterConnectionException)
					throw (AdapterConnectionException)t;
				else
					throw wmMQAdapter.getInstance().createAdapterException(1055, 
																		   new String[]{"Peek", t.getMessage()}, 
																		   t);
			}
			
			if (t instanceof wmMQException)
			{
				int reasonCode = ((MQException)((wmMQException)t).getLinkedException()).reasonCode;
				output.put("reasonCode", "" + reasonCode);
				output.put("conditionCode", "" + MQConstants.MQCC_FAILED);
			}

			Object[] parms = { "Peek", t.getMessage() };
			String errormsg = wmMQException.format("1055", parms);
			output.put(wmMQAdapterConstant.ERROR_MSG, errormsg);
			return output;
        }
		return output;
    }

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
		wmMQAdapterConstant.BACKOUT_THRESHHOLD,
		wmMQAdapterConstant.SHARED_MODE,
        wmMQAdapterConstant.CONVERT_DATA_OPTION,
		wmMQAdapterConstant.THROW_EXCEPTION_ON_FAILURE,
		wmMQAdapterConstant.RESET_CURSOR,
		wmMQAdapterConstant.RESET_CURSOR_TYPE,
		wmMQAdapterConstant.RESET_CURSOR_NAME,
		wmMQAdapterConstant.MSG_BODY,
		wmMQAdapterConstant.MSG_BODY_NAME,
        wmMQAdapterConstant.MSG_BODY_TYPE,
        wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY,
        wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_NAME,
        wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_TYPE
	};

}
