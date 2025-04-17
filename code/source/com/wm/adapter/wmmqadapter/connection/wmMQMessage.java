/*
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

package com.wm.adapter.wmmqadapter.connection;

// begin - system imports

import com.ibm.mq.MQC;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.constants.MQConstants;
import com.wm.adapter.wmmqadapter.service.wmMQMQMD;
import com.wm.adapter.wmmqadapter.WmMQAdapterUtils;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adk.cci.record.WmRecord;
import com.wm.adk.log.ARTLogger;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.services.DocumentToRecordService;
import com.wm.app.b2b.util.ServerIf;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.data.ValuesEmulator;
import com.wm.lang.xml.Document;
import com.wm.util.Values;



import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.TimeZone;

// end - system imports

/**
  This class is a wrapper for the MQMessage object
*/



public class wmMQMessage
{

    private MQMessage _msg;

    private String 	_msgString = null;
    private byte[] 	_msgBytes = null;
    private Object 	_msgObject = null;

    private int		_reasonCode = 0;

    //The offset into the message where the payload data starts.
    //This will be zero for non-JMS clients
    private int		_payloadOffset = 0;

    // 1-1KQMJN* : True if the user has explicitly set time or date
    private boolean		_userSetDateTime = false;
    // 1-1KQMJN
    
	private boolean  convertMsgBodyToString = true;

	private HashMap HeaderDataTypes = new HashMap();
	
	
    public wmMQMessage()
    {
        if (_msg == null)
            _msg = new MQMessage();
    }

    /**
     * Constructor using input parameters.
     *
     * @param header Values object containing login parameters.
     *
     */
    public wmMQMessage(Values header, String[] fields, boolean[] use) throws wmMQException
    {
        if (_msg == null)
            _msg = new MQMessage();

        copyMQMDFieldsToMsg(header, fields, use);

    }   //  wmMQMessage()


    /**
     * Retrieve the MQMessage in a Values object
     *
     * @return a Values object containing all properties from the MQMessage
     *
     */
    public Values getValues(String[] fields, boolean[] use) throws wmMQException
    {
        log( ARTLogger.INFO, 1001, "getValues", "");

        Values out = new Values();
        copyMsgFieldsToMQMD(out, fields, use);

        log( ARTLogger.INFO, 1002, "getValues", "");
        return out;
    }

    /**
     * Retrieve the MQMessage in a Values object
     *
     *
     *
     */
    public void setValues(Values in, String[] fields, boolean[]  use) throws wmMQException
    {
        log( ARTLogger.INFO, 1001, "setValues", "");

        copyMQMDFieldsToMsg(in, fields, use);

        log( ARTLogger.INFO, 1002, "setValues", "");
    }

    /**
     * Retrieve the MQMessage
     *
     * @return an MQMessage object
     *
     */
    public MQMessage getMQMessage() throws wmMQException
    {
        log( ARTLogger.INFO, 1001, "getMQMessage", "");
        return _msg;
    }

    /**
     * Set the MQMessage
     *
     * @param msg an MQMessage object
     *
     */
    public void setMQMessage(MQMessage msg) throws wmMQException
    {
        log( ARTLogger.INFO, 1001, "setMQMessage", "");
        _msg = msg;
        log( ARTLogger.INFO, 1002, "setMQMessage", "");
    }

    /**
     * Copy all MQMD header fields from the MQMessage object
     *
     * @param header a Values object into which the MQMD header fields will be placed.
     *
     */
    public void copyMQMDFieldsToMsg(WmRecord record, Values header, String[] fields, boolean[] use)
    {
        log(ARTLogger.INFO, 1001, "copyMQMDFieldsToMsg(WmRecord)", "");

        if (record != null)
        {

/* Something wierd is happening here - this code used to work, but it fails to compile now.
        Set keys = record.keySet();
        Iterator keysIter = keys.iterator();
        while (keysIter.hasNext())
        {
            String onekey = (String) keysIter.next();
            log(ARTLogger.INFO, 1003, "copyMQMDFieldsToMsg", "copying " + onekey);
            header.put(onekey, record.get(onekey));
        }
*/
	        IDataCursor idc = record.getCursor();
    	    if (idc.first())
        	{
	            do
    	        {
        	        String onekey = idc.getKey();
            	    header.put(onekey, idc.getValue());
           		} while (idc.next());
        	}
        	idc.destroy();
        }
        
        copyMQMDFieldsToMsg(header, fields, use);

        log(ARTLogger.INFO, 1002, "copyMQMDFieldsToMsg(WmRecord)", "");
    }

	/**
	 * Copy all MQMD header fields from the MQMessage object
	 *
	 * @param header a Values object containing the MQMD header fields.
	 *
	 */
	public void copyMsgFieldsToMQMD(Values header, String[] fields)
	{
		log(ARTLogger.INFO, 1001, "copyMsgFieldsToMQMD", "");
		int len = fields.length;
		boolean[] use = new boolean[len];
		for (int i = 0 ; i < len ; i++)
			use[i] = true;
		copyMsgFieldsToMQMD(header, fields, use);
	}

    /**
     * Copy all MQMD header fields from the MQMessage object
     *
     * @param header a Values object into which the MQMD header fields will be placed.
     *
     */
	@SuppressWarnings("squid:S1541")
    public void copyMsgFieldsToMQMD(Values header, String[] fields, boolean[] use)
    {
        log(ARTLogger.INFO, 1001, "copyMsgFieldsToMQMD", "");


        for (int f = 0 ; f < fields.length ; f++)
        {
			log(ARTLogger.INFO, 1003, "copyMsgFieldsToMQMD", "field= " + 
										  fields[f] + 
										  ",use=" +
										  ((use[f]) ? "true" : "false"));
            if (!use[f])
                continue;

            log(ARTLogger.INFO, 1003, "copyMsgFieldsToMQMD", "copying " + fields[f] + " to MQMD");

			int fieldindx = 0;
			for (fieldindx = 0 ; fieldindx < wmMQMQMD.mqmdFieldDisplayNames.length ; fieldindx++)
			{
				String fieldname = fields[f].substring(fields[f].indexOf('.') + 1);
				if (wmMQMQMD.mqmdFieldDisplayNames[fieldindx].endsWith(fieldname))
					break;
			}

            switch (fieldindx)
			{
                case 0: //accountingToken
                case 1: //accountingTokenByteArray
                    byte[] accountingToken = new byte[32];
                    System.arraycopy(_msg.accountingToken, 0, accountingToken, 0, _msg.accountingToken.length);
                    header.put(wmMQAdapterConstant.MQMD_ACCOUNTING_TOKEN_BYTE_ARRAY, accountingToken);
                    header.put(wmMQAdapterConstant.MQMD_ACCOUNTING_TOKEN, new String(_msg.accountingToken));
                    break;

                case 2: //applicationIdData
                    header.put(wmMQAdapterConstant.MQMD_APPLICATION_ID_DATA, _msg.applicationIdData);
                    break;

                case 3: //applicationOriginData
                    header.put(wmMQAdapterConstant.MQMD_APPLICATION_ORIGIN_DATA, _msg.applicationOriginData);
                    break;

                case 4: //backoutCount
                    header.put(wmMQAdapterConstant.MQMD_BACKOUT_COUNT, _msg.backoutCount);
                    break;

                case 5: //characterSet
                    header.put(wmMQAdapterConstant.MQMD_CHARACTER_SET, WmMQAdapterUtils.getCharacterSetString(_msg.characterSet));
                    break;

                case 6: //correlationId
                case 7: //correlationIdByteArray
                    byte[] correlationId = new byte[24];
                    System.arraycopy(_msg.correlationId, 0, correlationId, 0, _msg.correlationId.length);
                    header.put(wmMQAdapterConstant.MQMD_CORRELATION_ID_BYTE_ARRAY, correlationId);
					
					// Get the dataType from the Spl HeaderDatatypes map.
					// HeaderDataTypes will be populated only if 
					// watt.WmMQAdapter.instrospectHeaderDataTypes=true
                    Object dataType = HeaderDataTypes.get(wmMQAdapterConstant.MQMD_CORRELATION_ID);
					
					if (dataType != null) {
						if ("string".equals(dataType)) {
							header.put(wmMQAdapterConstant.MQMD_CORRELATION_ID,
									new String(_msg.correlationId));
						} else {
							header.put(wmMQAdapterConstant.MQMD_CORRELATION_ID,
									_msg.correlationId);
						}
					}
	
					else {
	
						// Check the DataType of the CorrelID if set explicitly set
						// to "string" using watt.WmMQAdapter.MQMD.CorrelID.DataType.String=true
						if ("string".equals(wmMQMQMD.CorrelID_DataType)) {
							header.put(wmMQAdapterConstant.MQMD_CORRELATION_ID,
									new String(_msg.correlationId));
	
						} else {
							header.put(wmMQAdapterConstant.MQMD_CORRELATION_ID,
									_msg.correlationId);
						}
					}
					break;
                    
                case 8: //encoding
                    String encoding = new String("00000" + new Integer(_msg.encoding).toString());
                    encoding = encoding.substring(encoding.length() - 5);
                    for (int i = 0; i < encodings.length; i++)
                    {
                        if (encodings[i].startsWith(encoding))
                            header.put(wmMQAdapterConstant.MQMD_ENCODING, encodings[i]);
                    }
                    break;

                case 9: //expiry
                    header.put(wmMQAdapterConstant.MQMD_EXPIRY, _msg.expiry);
                    break;

                case 10: //feedback
                    header.put(wmMQAdapterConstant.MQMD_FEEDBACK, "" + _msg.feedback); //Set default
                    for (int x = 0; x < predefined_feedback_codes.length; x++)
                    {
                        if (predefined_feedback_codes[x] == _msg.feedback)
                            header.put(wmMQAdapterConstant.MQMD_FEEDBACK, "" + wmMQMQMD.mqmdFeedbackOptions[x]);
                    }

                    break;

                case 11: //format
                    if ( (_msg.format == null) || (_msg.format.equals("        ")))
                        header.put(wmMQAdapterConstant.MQMD_FORMAT, " (NONE) ");
                    else
                        header.put(wmMQAdapterConstant.MQMD_FORMAT, _msg.format);
                    break;

                case 12: //groupId
                case 13: //groupIdByteArray
                    byte[] groupId = new byte[24];
                    System.arraycopy(_msg.groupId, 0, groupId, 0, _msg.groupId.length);
                    header.put(wmMQAdapterConstant.MQMD_GROUP_ID_BYTE_ARRAY, groupId);
                    //Use "ISO8859_1" to prevent the conversion of the bytes
                    try
                    {
                        header.put(wmMQAdapterConstant.MQMD_GROUP_ID, new String(_msg.groupId, "ISO8859_1"));
                    }
                    catch (UnsupportedEncodingException uee)
                    {
                        //Just leave it as a null string
                    }
                    break;

                case 14: //accountingTokenByteArray
                    String msgflags = "";
                    switch (_msg.messageFlags)
                    {
                        case MQC.MQMF_SEGMENTATION_ALLOWED:
                            msgflags = wmMQAdapterConstant.MQMD_MESSAGE_FLAGS_SEG_ALLOWED;
                            break;
                        case MQC.MQMF_SEGMENTATION_ALLOWED + MQC.MQMF_SEGMENT:
                            msgflags = wmMQAdapterConstant.MQMD_MESSAGE_FLAGS_SEGMENT;
                            break;
                        case MQC.MQMF_SEGMENTATION_ALLOWED + MQC.MQMF_SEGMENT + MQC.MQMF_LAST_SEGMENT:
                            msgflags = wmMQAdapterConstant.MQMD_MESSAGE_FLAGS_LAST_SEG;
                            break;
                        case MQC.MQMF_MSG_IN_GROUP:
                            msgflags = wmMQAdapterConstant.MQMD_MESSAGE_FLAGS_MSG_IN_GROUP;
                            break;
                        case MQC.MQMF_MSG_IN_GROUP + MQC.MQMF_LAST_MSG_IN_GROUP:
                            msgflags = wmMQAdapterConstant.MQMD_MESSAGE_FLAGS_LAST_MSG_IN_GROUP;
                            break;
                        case MQC.MQMF_NONE:
                        default:
                            msgflags = wmMQAdapterConstant.MQMD_MESSAGE_FLAGS_NONE;
                    }
                    header.put(wmMQAdapterConstant.MQMD_MESSAGE_FLAGS, msgflags);
                    break;

                case 15: //messageId
                case 16: //messageIdTokenByteArray
                    byte[] messageId = new byte[24];
                    System.arraycopy(_msg.messageId, 0, messageId, 0, _msg.messageId.length);
                    header.put(wmMQAdapterConstant.MQMD_MESSAGE_ID_BYTE_ARRAY,messageId);
				   
                    // Get the dataType from the Spl HeaderDatatypes map.
					// HeaderDataTypes will be populated if the
					// watt.WmMQAdapter.instrospectHeaderDataTypes=true
                    dataType = HeaderDataTypes.get(wmMQAdapterConstant.MQMD_MESSAGE_ID);
					
					if (dataType != null) {
						if ("string".equals(dataType)) {
							header.put(wmMQAdapterConstant.MQMD_MESSAGE_ID,
									new String(_msg.messageId));
						} else {
							header.put(wmMQAdapterConstant.MQMD_MESSAGE_ID,
									_msg.messageId);
						}
					}
	
					else {
	
						// Check the DataType of the CorrelID if set explicitly set
						// to "string" using watt.WmMQAdapter.MQMD.MessageID.DataType.String=true
						if ("string".equals(wmMQMQMD.MessageID_DataType)) {
							header.put(wmMQAdapterConstant.MQMD_MESSAGE_ID,
									new String(_msg.messageId));
	
						} else {
							header.put(wmMQAdapterConstant.MQMD_MESSAGE_ID,
									_msg.messageId);
						}
					}
                                    
                    break;

                case 17: //msgtype
                    String msgtype = "";
                    switch (_msg.messageType)
                    {
                        case MQC.MQMT_REQUEST:
                        	 msgtype = wmMQAdapterConstant.MQMD_MESSAGE_TYPE_REQUEST;
                            break;
                        case MQC.MQMT_REPLY:
                            msgtype = wmMQAdapterConstant.MQMD_MESSAGE_TYPE_REPLY;
                            break;
                        case MQC.MQMT_REPORT:
                            msgtype = wmMQAdapterConstant.MQMD_MESSAGE_TYPE_REPORT;
                            break;
                        case MQC.MQMT_APPL_FIRST:
                        	 msgtype = wmMQAdapterConstant.MQMD_MESSAGE_TYPE_APPL_FIRST;
                        	 break;
                        case MQC.MQMT_APPL_LAST:
                       	 	msgtype = wmMQAdapterConstant.MQMD_MESSAGE_TYPE_APPL_LAST;
                       	 	break;
                        case MQC.MQMT_SYSTEM_LAST:
                          	 msgtype = wmMQAdapterConstant.MQMD_MESSAGE_TYPE_SYSTEM_LAST;
                          	 break;
                        case MQC.MQMT_DATAGRAM:
                        	 msgtype = wmMQAdapterConstant.MQMD_MESSAGE_TYPE_DATAGRAM; 
                        	 break;
                        default:
                            msgtype = String.valueOf(_msg.messageType) ;    
                    }
                    header.put(wmMQAdapterConstant.MQMD_MESSAGE_TYPE, msgtype);
                    break;

                case 18: //messageSequenceNumber
                    header.put(wmMQAdapterConstant.MQMD_MESSAGE_SEQUENCE_NUMBER, _msg.messageSequenceNumber);
                    break;

                case 19: //offset
                    header.put(wmMQAdapterConstant.MQMD_OFFSET, _msg.offset);
                    break;

                case 20: //originalLength
                    header.put(wmMQAdapterConstant.MQMD_ORIGINAL_LENGTH, _msg.originalLength);
                    break;

                case 21: //persistence

                    String persistence = "";
                    switch (_msg.persistence)
                    {
                        case MQC.MQPER_NOT_PERSISTENT:
                            persistence = wmMQAdapterConstant.MQMD_PERSISTENCE_NOT_PERSISTENT;
                            break;
                        case MQC.MQPER_PERSISTENT:
                            persistence = wmMQAdapterConstant.MQMD_PERSISTENCE_PERSISTENT;
                            break;
                        case MQC.MQPER_PERSISTENCE_AS_Q_DEF:
                        default:
                            persistence = wmMQAdapterConstant.MQMD_PERSISTENCE_PERSISTENCE_AS_Q_DEF;
                    }
                    header.put(wmMQAdapterConstant.MQMD_PERSISTENCE, persistence);
                    break;

                case 22: //priority
                    header.put(wmMQAdapterConstant.MQMD_PRIORITY, _msg.priority);
                    break;

                case 23: //putApplicationName
                    header.put(wmMQAdapterConstant.MQMD_PUT_APPLICATION_NAME, _msg.putApplicationName);
                    break;

                case 24: //putapplicationtype
                    //Convert putApplicaitonType property from int to String
//                    for (int p = 0; p < PutApplicationTypesInt.length; p++)
//                    {
                   		String str = MQConstants.lookup(_msg.putApplicationType, "MQAT_.*");
                   		if(str == null){
                   			str = String.valueOf(_msg.putApplicationType);
                   		}
                   		else{
                   			str = str.substring(5, str.length());
                   		}
                   		
                        header.put(wmMQAdapterConstant.MQMD_PUT_APPLICATION_TYPE, str);
                   
                    break;

                case 25: //putDate
                case 26: //putTime
                    GregorianCalendar putDateTime = _msg.putDateTime;
                    if (putDateTime == null)
                        putDateTime = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

                    String s0, s1, s2, s3 = "";
                    s0 = "0" + new Integer(putDateTime.get(Calendar.MONTH) + 1).toString();
                    s1 = "0" + new Integer(putDateTime.get(Calendar.DAY_OF_MONTH));
                    s2 = new Integer(putDateTime.get(Calendar.YEAR)).toString();
                    header.put(wmMQAdapterConstant.MQMD_PUT_DATE,
                               s2 +
                               s0.substring(s0.length() - 2) +
                               s1.substring(s1.length() - 2));

                    s0 = "0" + new Integer(putDateTime.get(Calendar.HOUR_OF_DAY)).toString();
                    s1 = "0" + new Integer(putDateTime.get(Calendar.MINUTE)).toString();
                    s2 = "0" + new Integer(putDateTime.get(Calendar.SECOND)).toString();
                    s3 = "0" + new Integer(putDateTime.get(Calendar.MILLISECOND) / 10).toString();
                    header.put(wmMQAdapterConstant.MQMD_PUT_TIME,
                               s0.substring(s0.length() - 2) +
                               s1.substring(s1.length() - 2) +
                               s2.substring(s2.length() - 2) +
                               s3.substring(s3.length() - 2));

                    break;

                case 27: //accountingTokenByteArray
                    header.put(wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_MANAGER_NAME, _msg.replyToQueueManagerName);
                    break;

                case 28: //accountingTokenByteArray
                	header.put(wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_NAME, _msg.replyToQueueName);
                    break;

                case 29: //accountingTokenByteArray
                    header.put(wmMQAdapterConstant.MQMD_REPORT, _msg.report);
                    break;

                case 30: //accountingTokenByteArray
                    header.put(wmMQAdapterConstant.MQMD_USER_ID, _msg.userId);
                    break;

                default: //should never occur
                    break;
            }
        }

        log(ARTLogger.INFO, 1002, "copyMsgFieldsToMQMD", "");
    }

	/**
	 * Copy all MQMD header fields to the MQMessage object
	 *
	 * @param header a Values object containing the MQMD header fields.
	 *
	 */
	public void copyMQMDFieldsToMsg(WmRecord input, Values header, String[] fields)
	{
		log(ARTLogger.INFO, 1001, "copyMQMDFieldsToMsg", "");
		int len = fields.length;
		boolean[] use = new boolean[len];
		for (int i = 0 ; i < len ; i++)
			use[i] = true;
		copyMQMDFieldsToMsg(input, header, fields, use);
	}

    /**
     * Copy all MQMD header fields to the MQMessage object
     *
     * @param header a Values object containing the MQMD header fields.
     *
     */
	@SuppressWarnings("squid:S1541")
	public void copyMQMDFieldsToMsg(Values header, String[] fields, boolean[] use)
    {
        log(ARTLogger.INFO, 1001, "copyMQMDFieldsToMsg", "");
        
        wmMQAdapter.dumpValues("copyMQMDFieldsToMsg", header);

        if (header == null)
        {
            //set some default fields
            _msg.messageType = MQC.MQMT_DATAGRAM;
            _msg.messageSequenceNumber = 1;
            _msg.expiry = -1;

            //  Leaving the putDateTime field null seems to work best

            return;
        }

        String putdate = null;
        String puttime = null;

        for (int f = 0 ; f < fields.length ; f++)
        {
            if (!use[f])
                continue;

			//Parse the field name from its prefix
			int fieldindx = 0;
			String fieldname = fields[f];
			int indx = fieldname.indexOf('.');
			if (indx > -1)
				fieldname = fields[f].substring(indx + 1);
			for (fieldindx = 0 ; fieldindx < wmMQMQMD.mqmdFieldDisplayNames.length ; fieldindx++)
			{
				if (wmMQMQMD.mqmdFieldDisplayNames[fieldindx].endsWith(fieldname))
					break;
			}

            switch (fieldindx)
            {
                case 0:        //accountingToken
//Trax 1-????? - NullPointerException in wmMQConnection.put() if accountingToken value is
//				 less than 32 bytes long. Remove comments from next statement to resolve. 
//                	byte[] AccountingTokenByteArray = new byte[0]; 
                	String accountingToken = header.getString(wmMQAdapterConstant.MQMD_ACCOUNTING_TOKEN);
                    if (accountingToken != null)
					{	
						if (accountingToken.length() > MQC.MQ_ACCOUNTING_TOKEN_LENGTH)
							accountingToken = accountingToken.substring(0, MQC.MQ_ACCOUNTING_TOKEN_LENGTH);
                        //Use "ISO8859_1" to prevent the conversion of the bytes
                        try 
                        {
//                        	Trax 1-????? - Remove comments from next statement to resolve. 
//                        	AccountingTokenByteArray = accountingToken.getBytes("ISO8859_1");
//                        	Trax 1-????? - Comment out next statement to resolve. 
                        	_msg.accountingToken = accountingToken.getBytes("ISO8859_1");
                        }
                        catch (UnsupportedEncodingException uee) 
                        {
                        }
//                    	Trax 1-????? - Remove comments from next statement to resolve. 
//                       	System.arraycopy(AccountingTokenByteArray, 0,
//                                _msg.accountingToken,
//                                0, AccountingTokenByteArray.length);
		            }
                    break;

                case 1:        //accountingTokenByteArray
                    Object bytearrayvalue = header.getValue(wmMQAdapterConstant.MQMD_ACCOUNTING_TOKEN_BYTE_ARRAY);
                    if (bytearrayvalue != null)
                    	if ((bytearrayvalue instanceof byte[]) && (arrayIsNotNull((byte[])bytearrayvalue)))
                    	{
	                    	byte[] AccountingTokenByteArray = (byte[])bytearrayvalue; 
	    	                _msg.accountingToken = new byte[MQC.MQ_ACCOUNTING_TOKEN_LENGTH];
    	    	            	System.arraycopy(AccountingTokenByteArray, 0,
        	    	            	             _msg.accountingToken,
            	    	            	         0, AccountingTokenByteArray.length);
						}
						else if ((bytearrayvalue instanceof String) && (!((String)bytearrayvalue).trim().equals("")))
						{
							accountingToken = (String)bytearrayvalue;
							if (accountingToken.length() > MQC.MQ_ACCOUNTING_TOKEN_LENGTH)
								accountingToken = accountingToken.substring(0, MQC.MQ_ACCOUNTING_TOKEN_LENGTH);
							//Use "ISO8859_1" to prevent the conversion of the bytes
							try 
							{
								_msg.accountingToken = accountingToken.getBytes("ISO8859_1");
							}
							catch (UnsupportedEncodingException uee) {
							}
						}
                    break;

                case 2:        //applicationIdData
                    _msg.applicationIdData = header.getString(wmMQAdapterConstant.MQMD_APPLICATION_ID_DATA);
                    if ((_msg.applicationIdData != null ) && (_msg.applicationIdData.length() > MQC.MQ_APPL_IDENTITY_DATA_LENGTH))
						_msg.applicationIdData = _msg.applicationIdData.substring(0, MQC.MQ_APPL_IDENTITY_DATA_LENGTH);
                    break;

                case 3:        //applicationOriginData
                    _msg.applicationOriginData = header.getString(wmMQAdapterConstant.MQMD_APPLICATION_ORIGIN_DATA);
					if ((_msg.applicationOriginData != null ) && (_msg.applicationOriginData.length() > MQC.MQ_APPL_ORIGIN_DATA_LENGTH))
						_msg.applicationOriginData = _msg.applicationOriginData.substring(0, MQC.MQ_APPL_ORIGIN_DATA_LENGTH);
                    break;

                case 4:        //backoutCount
                    _msg.backoutCount = header.getInt(wmMQAdapterConstant.MQMD_BACKOUT_COUNT);
                    if (_msg.backoutCount < 0)
                        _msg.backoutCount = 0;
                    break;

                case 5:        //characterSet
                    //characterSet value in header is a string. Most characterSet values will be CPxxx.
                    //the getCharacterSetId() method converts the string to an integer
                    int characterSet = header.getInt(wmMQAdapterConstant.MQMD_CHARACTER_SET);
                    if (characterSet > -1)
                        _msg.characterSet = characterSet;
					else
					{                        
	                    String ccsid = header.getString(wmMQAdapterConstant.MQMD_CHARACTER_SET);
	                    
	                    // Assuming that the _msg.characterSet is already set to a default value
                        // reset it only if the provided ccsid value is a valid one
    	                _msg.characterSet = WmMQAdapterUtils.getCharacterSetId(ccsid, _msg.characterSet); 
					}
                   
                    break;

               case 6:        //correlationId
                   	byte[] CorrelationIdByteArray = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
									                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
									                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
									                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
                   	Object CorrelationId = header.get(wmMQAdapterConstant.MQMD_CORRELATION_ID);
                   	if (CorrelationId != null)
                   	{
                   		if(CorrelationId instanceof byte[]){
                            CorrelationIdByteArray = (byte[])header.get(wmMQAdapterConstant.MQMD_CORRELATION_ID);
                    	}else{
	                   		try 
	                       	{
	                        	CorrelationIdByteArray = ((String)CorrelationId).getBytes("ISO8859_1");
	                       	}
	                       	catch (UnsupportedEncodingException uee)
	                       	{
	                       	}
                    	}
	                       	
	                       	int corrIdByteArrayLength = CorrelationIdByteArray.length;
	                    	if(corrIdByteArrayLength > MQC.MQ_CORREL_ID_LENGTH){
	                    		corrIdByteArrayLength = _msg.correlationId.length;
	                    	}
	                       	System.arraycopy(CorrelationIdByteArray, 0,
	                                         _msg.correlationId,
	                                         0, corrIdByteArrayLength);
                    	}
                   	
                   break;

                case 7:        //CorrelationIdByteArray
					CorrelationIdByteArray = new byte[0];
					bytearrayvalue = header.getValue(wmMQAdapterConstant.MQMD_CORRELATION_ID_BYTE_ARRAY);
					if (bytearrayvalue != null)
						if ((bytearrayvalue instanceof byte[]) && (arrayIsNotNull((byte[])bytearrayvalue)))
						{
							CorrelationIdByteArray = (byte[])bytearrayvalue; 
						}
//				else if ((bytearrayvalue instanceof String) && (!((String)bytearrayvalue).trim().equals("")))
//						{
//							CorrelationId = (String)bytearrayvalue;
//							if (CorrelationId.length() > MQC.MQ_CORREL_ID_LENGTH)
//								CorrelationId = CorrelationId.substring(0, MQC.MQ_CORREL_ID_LENGTH);
//							//Use "ISO8859_1" to prevent the conversion of the bytes
//							try 
//							{
//								CorrelationIdByteArray = CorrelationId.getBytes("ISO8859_1");
//							}
//							catch (UnsupportedEncodingException uee) {
//							}
//						}
					if (_msg.correlationId == null)
    	                _msg.correlationId = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        	                                             0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            	                                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                	                                     0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

					if ((CorrelationIdByteArray != null) &&
						(CorrelationIdByteArray.length > 0) &&
						(CorrelationIdByteArray.length <= MQC.MQ_CORREL_ID_LENGTH))
                    {
						_msg.correlationId = new byte[MQC.MQ_CORREL_ID_LENGTH];
                        System.arraycopy(CorrelationIdByteArray, 
                        				 0, _msg.correlationId, 
                        				 0, CorrelationIdByteArray.length);
                    }
                    break;

                case 8:        //encoding
                    //Encoding value in header is taken from encodings array (see below). It must
                    //be converted into an integer
                    int encodevalue = header.getInt(wmMQAdapterConstant.MQMD_ENCODING);
                    if (encodevalue == -1)
                    {
                        String encoding = header.getString(wmMQAdapterConstant.MQMD_ENCODING);
                        
                        // Assuming that the _msg.encoding is already set to a default value
                        // reset it only if the provided encoding value is a valid one
                        if (encoding != null && !encoding.trim().equals("")) {
                        	if(encoding.trim().length() > 5) {
                        		encoding = encoding.trim().substring(0, 5);
                        	}
                            _msg.encoding = parseInt(encoding.trim(), _msg.encoding);
                        }
                    }
                    else
                        _msg.encoding = encodevalue;
                    break;

                case 9:        //expiry
                    _msg.expiry = header.getInt(wmMQAdapterConstant.MQMD_EXPIRY);
                    break;

                case 10:        //feedback
                    String feedback = header.getString(wmMQAdapterConstant.MQMD_FEEDBACK);
                    if (feedback != null)
                    {
                    	boolean feedbackIdentified = false;
                        for (int x = 0; x < wmMQMQMD.mqmdFeedbackOptions.length; x++)
                        {
                            if (feedback.equals(wmMQMQMD.mqmdFeedbackOptions[x])) {
                                _msg.feedback = predefined_feedback_codes[x];
                                feedbackIdentified = true;
                            }
                            if (!feedbackIdentified){
                            	try{
                            		_msg.feedback = Integer.parseInt(feedback);
                            	}catch(Exception e){
                            		//do nothing
                            	}
                            }
                        }
                    }
                    break;

                case 11:        //format
                    _msg.format = header.getString(wmMQAdapterConstant.MQMD_FORMAT);
                    if (_msg.format != null)
                    { 
						if (_msg.format.length() > MQC.MQ_FORMAT_LENGTH)
							_msg.format = _msg.format.substring(0, MQC.MQ_FORMAT_LENGTH);
                    	if(_msg.format.equals(" (NONE) "))
                        	_msg.format = "        ";
                    }
                    break;

                case 12:        //groupId
                    byte[] GroupIdByteArray = new byte[0];
                    String groupId = header.getString(wmMQAdapterConstant.MQMD_GROUP_ID);
                    if (groupId != null)
                    {
						if (groupId.length() > MQC.MQ_GROUP_ID_LENGTH)
							groupId = groupId.substring(0, MQC.MQ_GROUP_ID_LENGTH);
                        //Use "ISO8859_1" to prevent the conversion of the bytes
                        try {
//Trax 1-????? - NullPointerException in wmMQConnection.put() if groupId value is
//				 less than 24 bytes long. Remove comments from next statement to resolve. 
//                          GroupIdByteArray = groupId.getBytes("ISO8859_1");
//                        	Trax 1-????? - Comment out next statement to resolve. 
                            _msg.groupId = groupId.getBytes("ISO8859_1");
                        }
                        catch (UnsupportedEncodingException uee) 
                        {
                        }
//                    	Trax 1-????? - Remove comments from next statement to resolve. 
//                       	System.arraycopy(GroupIdByteArray, 0,
//                                _msg.groupId,
//                                0, GroupIdByteArray);
                    }
                    break;

                case 13:        //groupIdByteArray
				GroupIdByteArray = new byte[0];
				bytearrayvalue = header.getValue(wmMQAdapterConstant.MQMD_GROUP_ID_BYTE_ARRAY);
				if (bytearrayvalue != null)
					if ((bytearrayvalue instanceof byte[]) && (arrayIsNotNull((byte[])bytearrayvalue)))
					{
						GroupIdByteArray = (byte[])bytearrayvalue; 
					}
				else if ((bytearrayvalue instanceof String) && (!((String)bytearrayvalue).trim().equals("")))
					{
						groupId = (String)bytearrayvalue;
						if (groupId.length() > MQC.MQ_APPL_IDENTITY_DATA_LENGTH)
							groupId = groupId.substring(0, MQC.MQ_GROUP_ID_LENGTH);
						//Use "ISO8859_1" to prevent the conversion of the bytes
						try 
						{
							GroupIdByteArray = groupId.getBytes("ISO8859_1");
						}
						catch (UnsupportedEncodingException uee) {
						}
					}
                    if ((GroupIdByteArray != null) &&
                    	(GroupIdByteArray.length > 0) &&
						(GroupIdByteArray.length <= MQC.MQ_GROUP_ID_LENGTH))
                    {
                        _msg.groupId = new byte[MQC.MQ_GROUP_ID_LENGTH];
                        System.arraycopy(GroupIdByteArray, 
                        				 0, _msg.groupId, 
                        				 0, GroupIdByteArray.length);
                    }
                    break;

                case 14:        //messageFlags
                    //msgflags value in header is one of six predefined strings.
                    String msgflags = header.getString(wmMQAdapterConstant.MQMD_MESSAGE_FLAGS);
                    _msg.messageFlags = MQC.MQMF_NONE;
					if (msgflags != null)
                    {
                        if (msgflags.equals(wmMQAdapterConstant.MQMD_MESSAGE_FLAGS_NONE))
                            _msg.messageFlags = MQC.MQMF_NONE;
                        else if (msgflags.equals(wmMQAdapterConstant.MQMD_MESSAGE_FLAGS_SEG_ALLOWED))
                            _msg.messageFlags = MQC.MQMF_SEGMENTATION_ALLOWED;
                        else if (msgflags.equals(wmMQAdapterConstant.MQMD_MESSAGE_FLAGS_SEGMENT))
                            _msg.messageFlags = MQC.MQMF_SEGMENTATION_ALLOWED + MQC.MQMF_SEGMENT;
                        else if (msgflags.equals(wmMQAdapterConstant.MQMD_MESSAGE_FLAGS_LAST_SEG))
                            _msg.messageFlags = MQC.MQMF_SEGMENTATION_ALLOWED + MQC.MQMF_SEGMENT + MQC.MQMF_LAST_SEGMENT;
                        else if (msgflags.equals(wmMQAdapterConstant.MQMD_MESSAGE_FLAGS_MSG_IN_GROUP))
                            _msg.messageFlags = MQC.MQMF_MSG_IN_GROUP;
                        else if (msgflags.equals(wmMQAdapterConstant.MQMD_MESSAGE_FLAGS_LAST_MSG_IN_GROUP))
                            _msg.messageFlags = MQC.MQMF_MSG_IN_GROUP + MQC.MQMF_LAST_MSG_IN_GROUP;
						else 
							_msg.messageFlags = Integer.parseInt(msgflags);
                    }
                    break;

                case 15:        //messageId
                    Object messageId = header.get(wmMQAdapterConstant.MQMD_MESSAGE_ID);
	                byte[] MsgIdByteArray = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
                    if (messageId != null)
                    {
//						if (messageId.length() > MQC.MQ_MSG_ID_LENGTH)
//							messageId = messageId.substring(0, MQC.MQ_MSG_ID_LENGTH);
//                      
                    	
                    	if(messageId instanceof byte[]){
                            MsgIdByteArray = (byte[])header.get(wmMQAdapterConstant.MQMD_MESSAGE_ID);
                    	}else{
                    		  //Use "ISO8859_1" to prevent the conversion of the bytes
	                          try
	                          {
	                        	  MsgIdByteArray = ((String)messageId).getBytes("ISO8859_1");
	                          }
	                          catch (UnsupportedEncodingException uee)
	                          {
	                          }
                    	}
                    	int MsgIdByteArrayLength = MsgIdByteArray.length;
                    	if(MsgIdByteArrayLength > MQC.MQ_MSG_ID_LENGTH){
                    		MsgIdByteArrayLength = _msg.messageId.length;
                    	}
                        System.arraycopy(MsgIdByteArray, 
                        				 0, _msg.messageId, 
                        				 0,MsgIdByteArrayLength);
                    }
                    break;

                case 16:        //messageIdByteArray
                        //messageId can either be entered as a string or a byte[]
				MsgIdByteArray = null;
					//MsgIdByteArray = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
					//							 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
					//							 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
					//							 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
					bytearrayvalue = header.getValue(wmMQAdapterConstant.MQMD_MESSAGE_ID_BYTE_ARRAY);
					if (bytearrayvalue != null)
						if ((bytearrayvalue instanceof byte[]) && (arrayIsNotNull((byte[])bytearrayvalue)))
						{
							MsgIdByteArray = (byte[])bytearrayvalue; 
						}
//						else if ((bytearrayvalue instanceof String) && (!((String)bytearrayvalue).trim().equals("")))
//						{
//							messageId = (String)bytearrayvalue;
//							if (messageId.length() > MQC.MQ_MSG_ID_LENGTH)
//								messageId = messageId.substring(0, MQC.MQ_MSG_ID_LENGTH);
//							//Use "ISO8859_1" to prevent the conversion of the bytes
//							try 
//							{
//								MsgIdByteArray = messageId.getBytes("ISO8859_1");
//							}
//							catch (UnsupportedEncodingException uee) {
//							}
//						}
            
                        if (_msg.messageId == null)
	                        _msg.messageId = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    	                                                 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        	                                             0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            	                                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
						if ((MsgIdByteArray != null) &&
							(MsgIdByteArray.length > 0) &&
							(MsgIdByteArray.length <= MQC.MQ_MSG_ID_LENGTH))
                        {
                            _msg.messageId = new byte[MQC.MQ_MSG_ID_LENGTH];
                            System.arraycopy(MsgIdByteArray, 
                            				 0, _msg.messageId, 
                            				 0, MsgIdByteArray.length);
                        }
                    break;

                case 17:        //messageType
                    //msgtype value in header is one of four predefined strings.
                    String msgtype = header.getString(wmMQAdapterConstant.MQMD_MESSAGE_TYPE);
                    _msg.messageType = MQC.MQMT_DATAGRAM; //set default
					if (msgtype != null)
					{
                        if (msgtype.equals(wmMQAdapterConstant.MQMD_MESSAGE_TYPE_REQUEST))
                            _msg.messageType = MQC.MQMT_REQUEST;
                        else if (msgtype.equals(wmMQAdapterConstant.MQMD_MESSAGE_TYPE_REPLY))
                            _msg.messageType = MQC.MQMT_REPLY;
                        else if (msgtype.equals(wmMQAdapterConstant.MQMD_MESSAGE_TYPE_DATAGRAM))
                            _msg.messageType = MQC.MQMT_DATAGRAM;
                        else if (msgtype.equals(wmMQAdapterConstant.MQMD_MESSAGE_TYPE_REPORT))
                            _msg.messageType = MQC.MQMT_REPORT;
                        else if (msgtype.equals(wmMQAdapterConstant.MQMD_MESSAGE_TYPE_APPL_FIRST))
                        	_msg.messageType = MQC.MQMT_APPL_FIRST;
                        else if (msgtype.equals(wmMQAdapterConstant.MQMD_MESSAGE_TYPE_APPL_LAST))
                        	_msg.messageType = MQC.MQMT_APPL_LAST;
                        else if (msgtype.equals(wmMQAdapterConstant.MQMD_MESSAGE_TYPE_SYSTEM_LAST))
                        	_msg.messageType = MQC.MQMT_SYSTEM_LAST;
                        else 
                        	_msg.messageType = Integer.parseInt(msgtype);
                    }
                    break;

                case 18:        //messageSequenceNumber
                    _msg.messageSequenceNumber = header.getInt(wmMQAdapterConstant.MQMD_MESSAGE_SEQUENCE_NUMBER);
                    if (_msg.messageSequenceNumber < 0)
                        _msg.messageSequenceNumber = 0;  //Prevents matching on seqno=1
                    break;

                case 19:        //offset
                    _msg.offset = header.getInt(wmMQAdapterConstant.MQMD_OFFSET, -1);
                    //Trax 1-TDZIY
                    if (_msg.offset < 0)
                        _msg.offset = 0;
                    break;

                case 20:        //originalLength
                    _msg.originalLength = header.getInt(wmMQAdapterConstant.MQMD_ORIGINAL_LENGTH);
                    break;

                case 21:        //persistence
                    //persistence value in header is one of three predefined strings.
                    String persistence = header.getString(wmMQAdapterConstant.MQMD_PERSISTENCE);
                    _msg.persistence = MQC.MQPER_PERSISTENCE_AS_Q_DEF;
					if (persistence != null)
                    {
                        if (persistence.equals(wmMQAdapterConstant.MQMD_PERSISTENCE_NOT_PERSISTENT))
                            _msg.persistence = MQC.MQPER_NOT_PERSISTENT;
                        else if (persistence.equals(wmMQAdapterConstant.MQMD_PERSISTENCE_PERSISTENT))
                            _msg.persistence = MQC.MQPER_PERSISTENT;
                        else if (persistence.equals(wmMQAdapterConstant.MQMD_PERSISTENCE_PERSISTENCE_AS_Q_DEF))
                            _msg.persistence = MQC.MQPER_PERSISTENCE_AS_Q_DEF;
						else	
							_msg.persistence = Integer.parseInt(persistence);
                    }
                    break;

                case 22:        //priority
                    _msg.priority = header.getInt(wmMQAdapterConstant.MQMD_PRIORITY);
                    break;

                case 23:        //putApplicationName
                    _msg.putApplicationName = header.getString(wmMQAdapterConstant.MQMD_PUT_APPLICATION_NAME);
					if ((_msg.putApplicationName != null ) && (_msg.putApplicationName.length() > MQC.MQ_PUT_APPL_NAME_LENGTH))
						_msg.putApplicationName = _msg.putApplicationName.substring(0, MQC.MQ_PUT_APPL_NAME_LENGTH);
                    break;

                case 24:        //putApplicationType
                    //Convert putApplicaitonType property from String to int
                    String putapplicationtype = header.getString(wmMQAdapterConstant.MQMD_PUT_APPLICATION_TYPE);
                    // The input may not have the prefix MQAT_ thus add that and check if the value exists in the MQConstants
                    //If not then give a default value as 0.
                    if(!putapplicationtype.contains("MQAT_"))
                    {
                    	putapplicationtype = "MQAT_"+ putapplicationtype;
                    }
                	try{
                        _msg.putApplicationType = MQConstants.getIntValue(putapplicationtype);
                	}catch (NoSuchElementException e) {
                		_msg.putApplicationType = 0;
					}
                 
                    if (_msg.putApplicationType < 0)
                        _msg.putApplicationType = 0;
                    break;

                case 25:        //putdate
                    putdate = header.getString(wmMQAdapterConstant.MQMD_PUT_DATE);
					if ((putdate != null ) && (putdate.length() > 8))
						putdate = putdate.substring(0, 8);
                    break;

                case 26:        //puttime
                    puttime = header.getString(wmMQAdapterConstant.MQMD_PUT_TIME);
					if ((puttime != null ) && (puttime.length() > 8))
						puttime = puttime.substring(0, 8);
                    break;

                case 27:        //replyToQueueManagerName
                    _msg.replyToQueueManagerName = header.getString(wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_MANAGER_NAME);
					if ((_msg.replyToQueueManagerName != null ) && (_msg.replyToQueueManagerName.length() > MQC.MQ_Q_MGR_NAME_LENGTH))
						_msg.replyToQueueManagerName = _msg.replyToQueueManagerName.substring(0, MQC.MQ_Q_MGR_NAME_LENGTH);
                    break;

                case 28:        //replyToQueueName
                    _msg.replyToQueueName = header.getString(wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_NAME);
					if ((_msg.replyToQueueName != null ) && (_msg.replyToQueueName.length() > MQC.MQ_Q_MGR_NAME_LENGTH))
						_msg.replyToQueueName = _msg.replyToQueueName.substring(0, MQC.MQ_Q_MGR_NAME_LENGTH);
                    break;

                case 29:        //report
                    _msg.report = header.getInt(wmMQAdapterConstant.MQMD_REPORT);
                    if (_msg.report < 0)
                        _msg.report = 0;
                    break;

                case 30:        //userId
                    _msg.userId = header.getString(wmMQAdapterConstant.MQMD_USER_ID);
					if ((_msg.userId != null ) && (_msg.userId.length() > MQC.MQ_USER_ID_LENGTH))
						_msg.userId = _msg.userId.substring(0, MQC.MQ_USER_ID_LENGTH);
                    break;

                default:
                    break;

            } //switch
        } //for (int f.....
        
        // 1-1KQMJN*
        if((putdate != null && !putdate.trim().equals("")) || (puttime != null && !puttime.trim().equals(""))) {
            _userSetDateTime = true;
        }
        // 1-1KQMJN

        //putDateTime is a GregorianCalendar object that must be built from the putDate/putTime properties
        GregorianCalendar gregoriancalendar;
        Calendar now = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        if ( (putdate == null) || (putdate.trim().equals("")))
        {
            String y = new Integer(now.get(GregorianCalendar.YEAR)).toString();
            String m = "0" +
                new Integer(now.get(GregorianCalendar.MONTH) + 1).toString();
            String d = "0" +
                new Integer(now.get(GregorianCalendar.DAY_OF_MONTH)).toString();
            putdate = y + m.substring(m.length() - 2) +
                d.substring(d.length() - 2);
        }
        if ( (puttime == null) || (puttime.trim().equals("")))
        {
            String h = "0" +
                new Integer(now.get(GregorianCalendar.HOUR_OF_DAY)).toString();
            String n = "0" + new Integer(now.get(GregorianCalendar.MINUTE)).toString();
            String s = "0" + new Integer(now.get(GregorianCalendar.SECOND)).toString();
            String l = "0" +
                new Integer(now.get(GregorianCalendar.MILLISECOND) / 10).toString();
            puttime = h.substring(h.length() - 2) +
                n.substring(n.length() - 2) +
                s.substring(s.length() - 2) +
                l.substring(l.length() - 2);
        }
        now = null;

        try
        {
            int year = parseInt(putdate.substring(0, 4), 0);
            int month = parseInt(putdate.substring(4, 6), 0);
            int day = parseInt(putdate.substring(6, 8), 0);
            int hour = parseInt(puttime.substring(0, 2), 0);
            int minute = parseInt(puttime.substring(2, 4), 0);
            int second = parseInt(puttime.substring(4, 6), 0);
            int milli = parseInt(puttime.substring(6, 8), 0) * 10;
            gregoriancalendar = new GregorianCalendar(year, month - 1,
                day,
                hour, minute, second);
            gregoriancalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            gregoriancalendar.set(Calendar.MILLISECOND, milli);
        }
        catch (StringIndexOutOfBoundsException stringindexoutofboundsexception)
        {
            log(ARTLogger.INFO, 1003, "copyMQMDFieldsToMsg",
                "StringIndexOutOfBoundsException: " +
                stringindexoutofboundsexception.getLocalizedMessage());
            //stringindexoutofboundsexception.printStackTrace();
            gregoriancalendar = new GregorianCalendar();
        }
        catch (NumberFormatException numberformatexception)
        {
            log(ARTLogger.INFO, 1003, "copyMQMDFieldsToMsg",
                "NumberFormatException: " +
                numberformatexception.getLocalizedMessage());
            gregoriancalendar = new GregorianCalendar();
        }
	        
        // iTrac 852 [ SetAll permission ]
        if(wmMQAdapter.getAddDateTimeStamp() || _userSetDateTime) {
	        _msg.putDateTime = gregoriancalendar;
        }
        // iTrac 852
        
        log(ARTLogger.INFO, 1002, "copyMQMDFieldsToMsg", "");
    } //  copyMQMDFieldsToMsg()

    /**
     * Set the msgbody of the MQMessage
     *
     * @param msgbody an Object containing the message body
     *
     */
    public void setMsgBody(Object msgbody) throws wmMQException {

        log(ARTLogger.INFO, 1001, "setMsgBody", "");

        try {

            if (msgbody == null) {
                log(ARTLogger.INFO, 1003, "setMsgBody", "msgBody is null");
                _msg.clearMessage();
                return;
            }

            if (msgbody instanceof byte[]) {
                _msg.write( (byte[]) msgbody);
            }
            else if (msgbody instanceof String) {
                _msg.writeString( (String) msgbody);
            }
            else if ( (msgbody instanceof Values) ||
                     (msgbody instanceof Serializable)) {
                _msg.writeObject(msgbody);
            }
            else
                throw new wmMQException("3020", msgbody.getClass().getName());
        }
        catch (UnsupportedEncodingException uee) {
      		Object[] parms = {"(CCSID = " + _msg.characterSet + ")"};
            throw new wmMQException("1055", uee.getClass().getName(), parms);
        }
        catch (IOException ioe) {
            //ioe.printStackTrace();
			Object[] parms = {ioe.getLocalizedMessage()};
            throw new wmMQException("1055", ioe.getClass().getName(), parms);
        }

        log(ARTLogger.INFO, 1002, "setMsgBody", "");
    } //  setMsgBody()

    /**
     * Return the msgbody of the MQMessage
     *
     * @return msgbody an Object containing the message body
     *
     */
    public Object getMsgBody() throws wmMQException {

        log(ARTLogger.INFO, 1001, "getMsgBody", "");

        if (_msg == null) {
            log(ARTLogger.INFO, 1003, "MSGBODY_NULL", "");
            return null;
        }

        //The msgbody can be either a String, a byte[], or a Values Object or a Serializable Object
        //Unfortunately, the MQMessage object does not provide any way to determine which type of
        //message has been received.
        try {
            //_msgObject = null;
            //_msgBytes = null;
            //_msgString = null;
//			try
//			{
            _msg.seek(_payloadOffset);
            int datalength = _msg.getDataLength();
            if (datalength > 0) {
                _msgBytes = new byte[datalength];
                _msg.readFully(_msgBytes);
				/*
				 * Added the below if condition for IMQ-1020.
				 * charset.isSupported java api is taking around 600 msec in jvm
				 * 1.6 which is causing performance impact.To overcome that
				 * ToString conversion is skipped using watt property
				 */
				if (this.convertMsgBodyToString) {

					// Trax 1-TDU5E - the data should have already been
					// converted
					// _msgString = new String(_msgBytes); //swap this statement
					// for next one.
					log(ARTLogger.INFO, 1003,
							"getMsgBody converting to String using charset Encoding : "
									+ _msg.characterSet, "");
					
					_msgString = new String(_msgBytes,
							WmMQAdapterUtils
									.getCharacterSetString(_msg.characterSet));
					
					log(ARTLogger.INFO, 1003,
							"getMsgBody conversion completed", "");

				}
//					_msgString = _msg.readString(datalength);
//					_msgBytes = _msgString.getBytes();
                _msg.seek(_payloadOffset);
                _msgObject = _msg.readObject();
            }
//			}
            /*			catch (EOFException eof)
               {
                //Multi-Byte charactersets may cause a problem here. The datalength of the message does not
                //accurately reflect the number of characters that are read if the message contains Multi-byte
                //characters. If this is the case, an EOFException is thrown. This code sgement consumes the
                //EOFException, and attempts to read the message by recursively reading half as many characters
                //as datalength() indicates.
                 log( ARTLogger.INFO, "LOG_MSG", "getMsgBody", "EOFException caught"), "");
                _msgString = readByHalf();
                _msgBytes = _msgString.getBytes();
                _msg.seek(_payloadOffset);
                _msgObject = _msg.readObject();
               }
             */
        }
       /* catch (UnsupportedEncodingException ee) {
            //ee.printStackTrace();
            _msgObject = _msgString;
            log(ARTLogger.ERROR, 1055,
            		"MsgBody convertion failed. Reason : Unsupported Charset Encoding ",
            		ee.getMessage());
        } */
        catch (IOException ioe) {
            //ioe.printStackTrace();
            _msgObject = _msgString;
            //Object[] parms = {ioe.getLocalizedMessage()};
            //throw new wmMQException("1055", ioe.getClass().getName(), parms);
        }
        catch (ClassNotFoundException cnfe) {
            //cnfe.printStackTrace();
			Object[] parms = {cnfe.getLocalizedMessage()};
            throw new wmMQException("1055", cnfe.getClass().getName(), parms);
        }
		if (_msgObject != null)
			log(ARTLogger.INFO, 1003, "getMsgBody", "msgbody is a " + _msgObject.getClass().getName());
			
        log(ARTLogger.INFO, 1002, "getMsgBody", "");
        return _msgObject;
    } //  getMsgBody()

	/**
	 * Return the msgbody of the MQMessage as a byte[]
	 *
	 * @return msgbody an Object containing the message body
	 *
	 */
	public Object getMsgBodyByteArray() throws wmMQException
	{

        log(ARTLogger.INFO, 1001, "getMsgBodyByteArray", "");
/*	
		if (_msg != null)
		{
			byte[] temp = _msgBytes;

			try
			{
				//Check for MQRHF2 header
				int datalen = _msg.getDataLength();
				if (datalen < 4)
					return _msgBytes;	//Can't contain MQRHF2 header
				String first4 = new String(_msgBytes, 0 , 4);
				if (first4.equals(MQC.MQRFH_STRUC_ID))
				{
					int rfh2len = Integer.parseInt(new String(_msgBytes, 8, 4));
					if (rfh2len > datalen)
						return _msgBytes;	//Error, return raw data
					_msgBytes = new byte[datalen - rfh2len];
					if (datalen > rfh2len)	//Allow zero length data
						System.arraycopy(temp, rfh2len, _msgBytes, 0, datalen - rfh2len);
				}
			}
			catch (IOException ioe)
			{
				_msgBytes = temp;	//reset to original array
			}
			
	        log(ARTLogger.INFO, 1002, "getMsgBodyByteArray", "");
		}
*/
		return _msgBytes; //NOSONAR
	}

    /*
     private String readByHalf()
     {
      String value = "";
      try
      {
       int length2read = _msg.getDataLength();
       while (length2read > 0)
       {
        length2read = (length2read / 2) + (length2read % 2);		//Divide the length in half
        try
        {
         value = value.concat(_msg.readString(length2read));
         length2read = _msg.getDataLength();
        }
        catch (EOFException eof)
        {
         if (length2read == 1)
          break;
         //value.concat(readByHalf(msg, length2read));
        }
       //length2read = variablelength - length2read;	//residual length
       }
      }
      catch (IOException ioe)
      {
      }
      return value;
     }
     */
	
	private boolean createRFH2Folder(Values folderData, StringBuffer folderDataBuffer) {
		boolean atLeastOneProperty = false;
		
		if(folderData != null && !folderData.isEmpty()) {
			for (Enumeration okeys = folderData.keys(); okeys.hasMoreElements(); ) {
				String propertyName = (String)okeys.nextElement();
				Object value = folderData.get(propertyName);
	
				if(value != null) {
					folderDataBuffer.append("<");
					folderDataBuffer.append(propertyName);
					folderDataBuffer.append(">");

					boolean newPropertyAdded = false;
					if(value instanceof Values) {
						newPropertyAdded = createRFH2Folder((Values)value, folderDataBuffer);
					}
					else {
						folderDataBuffer.append(jmsEscape(folderData.getString(propertyName)));
						newPropertyAdded = true;
					}
					
					if(newPropertyAdded) {
						folderDataBuffer.append("</");
						folderDataBuffer.append(propertyName);
						folderDataBuffer.append(">");
						
						atLeastOneProperty = true;
					}
					else {
						folderDataBuffer.setLength(folderDataBuffer.length() - (propertyName.length()+2));
					}
				}
			}
		}
		
		return atLeastOneProperty;
	}
	
    /**
     * Create an MQRFH2 header for JMS-compliant partners
     *
     * @param qMgrName   The name of the queue manager (for mcd header)
     * @param queueName  The name of the queue (for mcd header)
     * @param properties a Values object containing JMS property name/value pairs
     *
     */
	@SuppressWarnings("squid:S1541")
    public void setJMSHeader(String qMgrName,
                             String queueName,
                             Values properties) throws wmMQException {
        log(ARTLogger.INFO, 1001, "setJMSHeader",
            "CCSID=" + _msg.characterSet);
			final String fourblanks = "    ";

//			if (properties == null)
//				properties = new Values();	//use dummy to force defaults
			
			String mcd = "";
			String jms = "";
		
			if (properties == null)
				properties = new Values();
			
			Values jmsprops = properties.getValues("jms");
			Values mcdprops = properties.getValues("mcd");

			if (jmsprops == null)
				jmsprops = new Values();
			if (mcdprops == null)
				mcdprops = new Values();

			//First, create the <mcd> folder
			StringBuffer mcdbuffer = new StringBuffer("<mcd>");

			String msd = mcdprops.getString(wmMQAdapterConstant.MCD_MESSAGE_DOMAIN);
			if(msd != null) {
				mcdbuffer.append("<Msd>");
				mcdbuffer.append(jmsEscape(msd));
				mcdbuffer.append("</Msd>");
			}

			String set = mcdprops.getString(wmMQAdapterConstant.MCD_MESSAGE_SET);
			if(set != null) {
				mcdbuffer.append("<Set>");
				mcdbuffer.append(jmsEscape(set));
				mcdbuffer.append("</Set>");
			}
			
			String JMSType = jmsprops.getString(wmMQAdapterConstant.JMS_TYPE);
			//The following code is commented for TRAX 1-1M5TO3
			//<Type> tag need not be present by default
			//if (JMSType == null || JMSType.trim().equals("")) {
			//	JMSType = "jms_text";
			//}

			//The following condition added for TRAX 1-1M5TO3
			if (JMSType != null && !JMSType.trim().equals("")) {
				mcdbuffer.append("<Type>"); 
				mcdbuffer.append(jmsEscape(JMSType));
				mcdbuffer.append("</Type>");
			}
			
			String format = mcdprops.getString(wmMQAdapterConstant.MCD_MESSAGE_FORMAT);
			if(format != null) {
				mcdbuffer.append("<Fmt>");
				mcdbuffer.append(jmsEscape(format));
				mcdbuffer.append("</Fmt>");
			}
			
			mcdbuffer.append("</mcd>");
			
			if ((mcdbuffer.length() % 4) > 0)
				mcdbuffer.append(fourblanks.substring(0, 4 - (mcdbuffer.length() % 4)) );

			mcd = mcdbuffer.toString();

			//Next, create the <jms> folder

			StringBuffer jmsbuffer = new StringBuffer("<jms>");
		
			//JMSDestination property
			jmsbuffer.append("<" + JMSPropertyNames[JMS_DESTINATION_INDEX][1] + ">");
			String JMSDestination = jmsprops.getString(wmMQAdapterConstant.JMS_DESTINATION);
			if (JMSDestination != null)
			{
				jmsbuffer.append(jmsEscape(JMSDestination));
			}
			else
			{
				jmsbuffer.append("queue://");
				jmsbuffer.append(jmsEscape(qMgrName.trim()));
				jmsbuffer.append("/");
				jmsbuffer.append(jmsEscape(queueName.trim()));
			}
			jmsbuffer.append("</" + JMSPropertyNames[JMS_DESTINATION_INDEX][1] + ">");

			//See comments within the JMSPropertyNames array declaration below. They explain why this loop
			//stops before the end of the array
			for (int j = 0 ; j < JMSPropertyNames.length - 5 ; j++)
			{
				Object oneproperty = jmsprops.get(JMSPropertyNames[j][0]);

				//If the property does not have an XML tag, it is not allowed in the <jms> folder
				if (JMSPropertyNames[j][1].trim().equals(""))
					continue;

				//The JMSTimestamp property requires special processing
				if (j == JMS_TIMESTAMP_INDEX)
				{
					jmsbuffer.append("<" + JMSPropertyNames[j][1] + ">");
					jmsbuffer.append(System.currentTimeMillis());
					jmsbuffer.append("</" + JMSPropertyNames[j][1] + ">");
					continue;
				}

				//Some of the properties can be specified as either a String or byte array.
				//Skip the byteArray version of the property if it is null, to avoid duplicate
				//XML tabs.   
				if (JMSPropertyNames[j][0].endsWith("ByteArray"))
				{
					if (oneproperty == null)
					{
						continue;
					}
					else
					{
						String emptytag = "<" + JMSPropertyNames[j][1] + "></" + JMSPropertyNames[j][1] + ">";
						String tail = jmsbuffer.substring(jmsbuffer.length() - emptytag.length());
						if (tail.equals(emptytag))
							jmsbuffer.setLength(jmsbuffer.length() - emptytag.length());
					}
				}
				
				if (oneproperty != null)
				{
					jmsbuffer.append("<" + JMSPropertyNames[j][1] + ">");
					
					if (oneproperty instanceof byte[])
						jmsbuffer.append(jmsEscape(new String((byte[])oneproperty)));
					else
						jmsbuffer.append(jmsEscape(oneproperty.toString()));
					
					jmsbuffer.append("</" + JMSPropertyNames[j][1] + ">");
				}
				
			}

			// Add the user defined jms properties to the jms header
			// Befor that remove the properties that were already added
			for (int j = 0 ; j < JMSPropertyNames.length; j++) {
				jmsprops.remove(JMSPropertyNames[j][0]);
			}
			createRFH2Folder(jmsprops, jmsbuffer);
			
			jmsbuffer.append("</jms>");

			if ((jmsbuffer.length() % 4) > 0)
				jmsbuffer.append(fourblanks.substring(0, 4 - (jmsbuffer.length() % 4)) );

			jms = jmsbuffer.toString();
	
			//Finally, create the other RFH2 folders
			//Trax 1-Y72E9 - process all RFH2 headers 

			ArrayList otherHeaders = new ArrayList();
			String NameValueData = "";
	        for (Enumeration keys = properties.keys() ; keys.hasMoreElements() ; )
	        {
				String otherKey = (String)keys.nextElement();
				if (otherKey.trim().equalsIgnoreCase("jms"))
					continue;
				if (otherKey.trim().equalsIgnoreCase("mcd"))
					continue;
	        	Object otherObject = properties.get(otherKey);
 				if ((otherObject != null) && (otherObject instanceof Values))
				{ 
					Values otherValues = (Values)otherObject;
		        	wmMQAdapter.dumpValues(otherKey +" Properties", otherValues);
					
					StringBuffer NameValueDataBuffer = new StringBuffer("<" + otherKey + ">");
					boolean useinternal = wmMQAdapter.isUseInternalDecodeXml();
			    	if(!useinternal){
			    		IData pipeline = IDataFactory.create();
			    		ValuesEmulator.put(pipeline, "document", otherValues);
						ValuesEmulator.put(pipeline, "addHeader", "false");
						try{
							Service.doInvoke("pub.xml", "documentToXMLString", pipeline);
							String data = (String) ValuesEmulator.get(pipeline, "xmldata");
							// IS service is sending the string where each idata is separated by \n thus remove the \n 
							data = data.replaceAll("\\n", "");
							
							NameValueDataBuffer.append(data);
							NameValueDataBuffer.append("</" + otherKey + ">");
						}catch (Exception e) {
							log(ARTLogger.DEBUG,
									1003,
									"Exception while coverting JMS Headers XML to IData using IS pub Services :",
									e.getLocalizedMessage());
							throw new wmMQException("1003",
									"Exception while coverting JMS Headers XML to IData using IS pub Services :"
											+ e.getLocalizedMessage());
						}
						otherHeaders.add(NameValueDataBuffer.toString());
		    		}else{
						boolean atLeastOneProperty = createRFH2Folder(otherValues, NameValueDataBuffer);
			
	//					for (Enumeration okeys = otherValues.keys() ; okeys.hasMoreElements() ; )
	//					{
	//						String propertyName = (String)okeys.nextElement();
	//						String property = otherValues.getString(propertyName);
	//		
	//						if (propertyName.startsWith("jms"))	//skip the <jms> Values object
	//							continue;
	//		
	//						if (propertyName.startsWith("JMS"))
	//							continue;
	//						NameValueDataBuffer.append("<");
	//						NameValueDataBuffer.append(propertyName);
	//						NameValueDataBuffer.append(">");
	//						NameValueDataBuffer.append(jmsEscape(property));
	//						NameValueDataBuffer.append("</");
	//						NameValueDataBuffer.append(propertyName);
	//						NameValueDataBuffer.append(">");
	//		
	//						atLeastOneProperty = true;
	//					}
			
						if (atLeastOneProperty)
						{
							NameValueDataBuffer.append("</" + otherKey + ">");
			
							if ((NameValueDataBuffer.length() % 4) > 0)
								NameValueDataBuffer.append(fourblanks.substring(0, 4 - (NameValueDataBuffer.length() % 4)) );
			
							NameValueData = NameValueDataBuffer.toString();
							otherHeaders.add(NameValueData);
						}
		    		}
				} //if (otherObject != null)
	        } //for (Enumeration....

			//calculate length of MQRFH2 header
			int StrucLength = MQC.MQRFH_STRUC_LENGTH_FIXED_2; //initial length of RFH2 header only
			StrucLength += mcd.length() + 4;
			StrucLength += jms.length() + 4;
			String[] otherHeaderStrings = new String[otherHeaders.size()];
			otherHeaderStrings = (String[])otherHeaders.toArray(otherHeaderStrings);
			int NameValueCCSID1 = 1208;	//setting to UTF-8, as it was set same for headers from beginning			
			try
			{
				if (otherHeaderStrings.length > 0)
					for(int o = 0 ; o < otherHeaderStrings.length ; o++){
						//Changed length calculation, as it was failing for double byte character set representations 
						byte[] databyte1 = otherHeaderStrings[o].getBytes(WmMQAdapterUtils.getCharacterSetString(NameValueCCSID1));
						StrucLength += databyte1.length + 4;
					}
				//Write fixed portion of MQRFH2 header
				_msg.writeString(MQC.MQRFH_STRUC_ID);
				_msg.writeInt(MQC.MQRFH_VERSION_2);
				_msg.writeInt(StrucLength);
				_msg.writeInt(_msg.encoding);
				_msg.writeInt(_msg.characterSet);
				_msg.writeString(MQC.MQFMT_STRING);
				_msg.writeInt(MQC.MQRFH_NO_FLAGS);
				_msg.writeInt(1208);				//Set to UTF-8
				//Write variable part of MQRFH2 header
				_msg.writeInt(mcd.length());
				_msg.writeString(mcd);
				_msg.writeInt(jms.length());
				_msg.writeString(jms);
				if (otherHeaderStrings.length > 0)
					for(int o = 0 ; o < otherHeaderStrings.length ; o++)
					{	
						//Changed length calculation, as it was failing for double byte character set representations
						byte[] databyte2 = otherHeaderStrings[o].getBytes(WmMQAdapterUtils.getCharacterSetString(NameValueCCSID1));
						_msg.writeInt(databyte2.length);
						_msg.writeString(otherHeaderStrings[o]);
						//_msg.writeString(new String(databyte2, WmMQAdapterUtils.getCharacterSetString(NameValueCCSID2)));
					}
			}
			catch (UnsupportedEncodingException uee)
			{
				throw new wmMQException("2055", uee.toString() + "(CCSID = " + _msg.characterSet + ")");
			}
			catch (IOException ioe)
			{
				//ioe.printStackTrace();
				throw new wmMQException("2055", ioe.toString());
			}

        log(ARTLogger.INFO, 1002, "setJMSHeader", "");

    } //  setJMSHeader()

    /**
     * Process an MQRFH2 header from the received message
     *
     * The MQRFH2 header has a variable length portion consisting of JMS properties in
     * XML format. The header will always contain the <mcd> and <jms> folders, and optionally
     * a <usr> folder. The <jms> and <usr> folders will be returned to the user in the JMSProperties
     * Values object.<br><p>
	 * Structure of MQRFH2:|StrucId Version Struclength Encoding CodedCharSetId
	 * Format Flags NameValueCCSID|NameValueLength,NameValueData
     *
     * @return properties a Values object containing Values objects for the <jms> and <usr> folders
     */
    public Values getJMSHeader() throws wmMQException {
        log(ARTLogger.INFO, 1001, "getJMSHeader", "");

        Values JMSProperties = null;

        if (_msg == null) {
            log(ARTLogger.INFO, 1003, "getJMSHeader", "msgBody is null");
            return null;
        }
        try {
            int datalength = _msg.getDataLength();
            if (datalength < MQC.MQRFH_STRUC_LENGTH_FIXED_2) //Check for minimum length
                return null;

            _msg.seek(0);
            String rfh2 = _msg.readString(4);//FieldName:StrucId
            if (rfh2.equals(MQC.MQRFH_STRUC_ID)) {
                JMSProperties = new Values();
                int version = _msg.readInt();//FieldName:Version
                //Read the rest of the fixed portion of the MQRFH2 header
                int StrucLength = _msg.readInt();//FieldName:Struclength
                if (datalength < StrucLength) {
					//Big problem. A patrial RFH2 header was found. Skip past structure
					_msg.seek(datalength);
                    return null;
                }
                // Trax 1-153LVB $ Start
                //If the version of RFH is not 2, don't process the header
                if (version != MQC.MQRFH_VERSION_2) {
                	log(ARTLogger.INFO, 4081, "", "");
                	_msg.seek(0);
                    return null;
                }
                // Trax 1-153LVB $ End
				/**
				 * The fields in the MQRFH2 Header should be read one by one.To
				 * decode the NameValueData, we need Character set identifier of
				 * NameValueData. NameValueCCSID is the field which has this
				 * value. To read this all the fields should be read one by one.
				 */
                int Encoding = _msg.readInt();//FieldName:Encoding-Numeric
                int CodedCharSetId = _msg.readInt();//FieldName:CodedCharSetId
                String Format = _msg.readString(8);//FieldName:Format
                int Flags = _msg.readInt();//FieldName:Flags
                int NameValueCCSID = _msg.readInt();//FieldName:NameValueCCSID

                //Subtract length of fixed portion from length of MQRFH2 header
                _payloadOffset = StrucLength; //Where payload data starts
                StrucLength -= MQC.MQRFH_STRUC_LENGTH_FIXED_2;
                String JMSType = "";
				/**
				 * NameValueLength & NameValueData they must occur in the
				 * sequence ..... length1, data1, length2, data2, .......
				 */
                while (StrucLength > 0) {
                    int NameValueLength = _msg.readInt(); //length of subheader
                    //Read subheader
                    byte[] databytes = new byte[NameValueLength];
                    _msg.readFully(databytes);//reading the NameValueData  
                    String data = new String(databytes, WmMQAdapterUtils.getCharacterSetString(NameValueCCSID));
                    Values propertySet = decodeXML(data);
                    //wmMQAdmin.dumpValues("decoded properties header", propertySet);
                    if(propertySet!=null){
                    String[] propertySetNames = propertySet.getValueKeys();

            		//Trax 1-Y72E9 - process all RFH2 headers 
                    for (int p = 0 ; p < propertySetNames.length ; p++)
                    {
	                    //The JMSType property resides in the <mcd> folder under the <Msd> tag.
	                    //Move it to the <jms> folder.
	                    if (propertySetNames[p].equals("mcd")) {
	                        Values MCDValues = propertySet.getValues("mcd");
	                        if(MCDValues != null) {
	                        	MCDValues.renameKey(JMSPropertyNames[MCD_MESSAGE_DOMAIN_INDEX][1], JMSPropertyNames[MCD_MESSAGE_DOMAIN_INDEX][0]);
	                        	MCDValues.renameKey(JMSPropertyNames[MCD_MESSAGE_SET_INDEX][1], JMSPropertyNames[MCD_MESSAGE_SET_INDEX][0]);
	                        	MCDValues.renameKey(JMSPropertyNames[MCD_MESSAGE_FORMAT_INDEX][1], JMSPropertyNames[MCD_MESSAGE_FORMAT_INDEX][0]);
	                        	
			                    //The JMSType property resides in the <mcd> folder under the <Type> tag.
			                    //Move it to the <jms> folder.
		                        //Save the JMSType property for later
								JMSType = MCDValues.getString(JMSPropertyNames[JMS_TYPE_INDEX][1]);
								MCDValues.remove(JMSPropertyNames[JMS_TYPE_INDEX][1]);
	                        }
	                    }
	                    else if (propertySetNames[p].equals("jms")) {
	                        //The 'jms' Values object contains the JMS properties from the
	                        //<jms> folder. There are two issues:
	                        //1. The key names are the XML tags, not the JMSxxxxx names. If the
	                        //property is present, its key will be changed to the JMSxxxxx name.
	                        //Otherwise, it will be filled from the MQMD header.
	                        //2. Some of the JMSProperties do not have a corresponding XML tag. That
	                        //is, they cannot be present in the MQRFH2 header. They must be filled
	                        //from the MQMD fields.
	                        Values JMSValues = propertySet.getValues("jms");
	                        if(JMSValues!=null && !JMSValues.isEmpty()){
		                        for (int j = 0; j < JMSPropertyNames.length; j++) {
		                            if ( (JMSPropertyNames[j][1].trim().equals("")) ||
		                                (JMSValues.get(JMSPropertyNames[j][1]) != null)) 
		                            {
		                                setJMSPropertyFromMQMD(JMSValues,
		                                    JMSPropertyNames[j][0]);
		                            }
		                            else {
		                                JMSValues.renameKey(JMSPropertyNames[j][1],
		                                    JMSPropertyNames[j][0]);
		                            }
		                        }
	                        }
	                        //Add the JMSType property from the <mcd> folder

	                        //The conditon (JMSType != null) added for  TRAX 1-1IW7O9
	                        
	                        if (JMSType != null &&
	                        		(!JMSType.trim().equals(""))){
	                        	/*
	                        	 * Getting java.lang.ClassCastException: com.wm.app.b2b.services.CValues cannot be cast to com.wm.util.Values
	                        	 * As the propertySet.get(0) is returning an Object of type CValues. 
	                        	 * 
	                        	 */
	                        	
	                            /*( (Values) propertySet.get(0)).put(wmMQAdapterConstant.JMS_TYPE,
	                                JMSType);*/
	                        	
	                        	ValuesEmulator.put(propertySet.getIData(),wmMQAdapterConstant.JMS_TYPE,JMSType);
	                        	
	                        }
	                        	
	                    }
	
                        JMSProperties.put(propertySetNames[p], propertySet.get(p));
                    }
                   }

                    StrucLength -= (NameValueLength + 4);
                }
            }
            else {
                //This message does not contain an MQRFH2 header, reset cursor to start of message
                _msg.seek(0);
            }
        }
        catch (IOException ioe) {
            //ioe.printStackTrace();
            log(ARTLogger.ERROR,
                1055,
                "Getting JMS Header",
                ioe.getMessage());
            _msgObject = _msgString;
        }

        log(ARTLogger.INFO, 1002, "getJMSHeader", "");
        return JMSProperties;
    } //  getJMSHeader()

    /**
     * Fill a JMSProperty from the MQMD header
     *
     * Some JMS properties must be filled from the MQMD header instead of the <jms> folder
     * of the MQRFH2 header.
     *
         * @param jmsvalues A Values object into which the JMSProperty will be filled.
     * @param property A String containing the name of the property to fill
     */
    public void setJMSPropertyFromMQMD(Values jmsvalues, String property) throws wmMQException 
    {
        log(ARTLogger.INFO, 1001, "setJMSPropertyFromMQMD", "");

        try {
            //Of course, nothing is ever easy. Certain properties cannot be set from the MQMD header.
            if ( (property.equals("JMS_EXPIRATION")) ||
                (property.equals("JMS_TYPE")) ||
                (property.equals("JMS_DESTINATION")))
                return;

            if (property.equals("JMS_PRIORITY"))
                jmsvalues.put("JMS_PRIORITY", _msg.priority);
            if (property.equals("JMS_DELIVERY_MODE"))
                jmsvalues.put("JMS_DELIVERY_MODE", _msg.persistence);
            if (property.equals("JMS_CORRELATION_ID"))
                jmsvalues.put("JMS_CORRELATION_ID",
                              new String(_msg.correlationId, "UTF-8"));
            if (property.equals("JMS_REPLYTO"))
                jmsvalues.put("JMS_REPLYTO", "queue://" +
                              _msg.replyToQueueManagerName.trim() +
                              "/" +
                              _msg.replyToQueueName.trim());
            if (property.equals("JMSX_GROUP_ID"))
                jmsvalues.put("JMSX_GROUP_ID", new String(_msg.groupId, "UTF-8"));
            if (property.equals("JMSX_GROUP_SEQ"))
                jmsvalues.put("JMSX_GROUP_SEQ", _msg.messageSequenceNumber);
            if (property.equals("JMSX_USER_ID"))
                jmsvalues.put("JMSX_USER_ID", _msg.userId);
            if (property.equals("JMSX_APP_ID"))
                jmsvalues.put("JMSX_APP_ID", _msg.putApplicationName);
            if (property.equals("JMS_MESSAGE_ID"))
                jmsvalues.put("JMS_MESSAGE_ID", new String(_msg.messageId));
            if (property.equals("JMS_TIMESTAMP"))
                jmsvalues.put("JMS_TIMESTAMP",
                              _msg.putDateTime.getTime().getTime());
            if (property.equals("JMS_REDELIVERED"))
                jmsvalues.put("JMS_REDELIVERED", _msg.backoutCount);
            if (property.equals("JMSX_DELIVERY_COUNT"))
                jmsvalues.put("JMSX_DELIVERY_COUNT", _msg.backoutCount);

        }
        catch (java.io.UnsupportedEncodingException uee) {
            //uee.printStackTrace();
            log(ARTLogger.ERROR,
                1055,
                "Setting JMS Properties",
                uee.getMessage());
        }

        log(ARTLogger.INFO, 1002, "setJMSPropertyFromMQMD", "");
    }

	/**
	 * escape certain characters in a JMSProperty
	 *
	 * The following characters must be escaped into the corresponding strings:
	 *  
	 * '&'	"&amp;" 
	 * '<'	"&lt;" 
	 * '>'	"&gt;" 
	 * '\''	"&apos;" 
	 * '\"'	"&quot;" 
	 *
	 * @param property A String containing the value of a property
	 */
	public String jmsEscape(String property)
	{
		log( ARTLogger.INFO, 1003, "jmsEscape", "");

		String temp = property;
		for (int i = 0 ; i < jmsEscapeCharacters.length ; i++)
		{
			StringBuffer sb = new StringBuffer();
			int start = 0;
			int end = temp.length();
			int indx = 0;
			while (start < end)
			{
				indx = temp.indexOf(jmsEscapeCharacters[i][0], start);
				if (indx ==-1)
				{
					sb.append(temp.substring(start));
					start = end;
				}
				else
				{
					sb.append(temp.substring(start, indx));
					sb.append(jmsEscapeCharacters[i][1]);
					start = indx + 1;
				}
			}
			temp = sb.toString();
		}
		log( ARTLogger.INFO, 1002, "jmsEscape", "");
		return temp;
	}

	/**
	 * unescape certain characters in a JMSProperty
	 *
	 * The following strings must be unescaped back into their original characters:
	 *  
	 * "&amp;"	'&' 
	 * "&lt;"	'<' 
	 * "&gt;"	'>'	 
	 * "&apos;"	'\''
	 * "&quot;"	'\"'
	 *
	 * @param property A String containing the value of a property
	 */
	public String jmsUnEscape(String property)
	{
		log( ARTLogger.INFO, 1001, "jmsUnEscape", "");

		String temp = property;
		for (int i = 0 ; i < jmsEscapeCharacters.length ; i++)
		{
			StringBuffer sb = new StringBuffer();
			int start = 0;
			int end = temp.length();
			int indx = 0;
			while (start < end)
			{
				indx = temp.indexOf(jmsEscapeCharacters[i][1], start);
				if (indx ==-1)
				{
					sb.append(temp.substring(start));
					start = end;
				}
				else
				{
					sb.append(temp.substring(start, indx));
					sb.append(jmsEscapeCharacters[i][0]);
					start = indx + jmsEscapeCharacters[i][1].length();
				}
			}
			temp = sb.toString();
		}
		log( ARTLogger.INFO, 1002, "jmsUnEscape", "");
		return temp;
	}

	/*
	 * Utility method to safelt parse an int from a string
	 */
	private int parseInt(String in, int defaultvalue)
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
	
	/**
	 * IMQ-981,974 <br>
	 * MQ Adapter has its own logic to convert JMS properties which are in XML
	 * format into IData/Values. This logic will not be able to handle as & when
	 * the complexity of xml grows,to address this issue decodeXMLUsingISService
	 * is added which uses IS public services to convert XML into Values.This
	 * routine is called when watt property
	 * watt.WmMQAdapter.useInternalDecodeXML is set to false
	 * 
	 * @param xml
	 * @return
	 * @throws wmMQException 
	 */
	protected Values decodeXML(String xml) throws wmMQException {
		return decodeXML(xml, wmMQAdapter.isUseInternalDecodeXml());
	}
	 
	protected Values decodeXML(String xml, boolean useInternal) throws wmMQException {
		return useInternal ? decodeXMLUsingInternal(xml)
				: decodeXMLUsingISApis(xml);
	}
	
	
	//Refer IMQ-1155 
/*	*//**
	 * Converts the xml String to com.wm.util.Values using the IS public
	 * services pub.xml:xmlStringToXMLNode & pub.xml:xmlNodeToDocument
	 * 
	 * @param xml
	 * @return Values of the corresponding xml String, NULL incase of any
	 *         Exception
	 * @throws wmMQException
	 *             Throws MQ Exception incase when the IS Public services throws
	 *             any exception while converting xml to IData
	 *//*
	private Values decodeXMLUsingISService(String xml) throws wmMQException {

		if (xml != null && xml.length() > 0) {
			IData pipeline = IDataFactory.create();
			ValuesEmulator.put(pipeline, "xmldata", xml);
			ValuesEmulator.put(pipeline, "isXML", "true");

			try {
						
				Service.doInvoke("pub.xml", "xmlStringToXMLNode", pipeline);
				ValuesEmulator.put(pipeline, "makeArrays", "false");
				Service.doInvoke("pub.xml", "xmlNodeToDocument", pipeline);
				IData values = (IData) ValuesEmulator.get(pipeline, "document");
				return Values.use(values);
			} catch (Exception e) {
				log(
						ARTLogger.DEBUG,
						1003,
						"Exception while coverting JMS Headers XML to IData using IS pub Services :",
						e.getLocalizedMessage());
				throw new wmMQException("1003",
						"Exception while coverting JMS Headers XML to IData using IS pub Services :"
								+ e.getLocalizedMessage());
			}
		}

		return null;
	}*/
	
	/**
	 * <b>IMQ-1155</b> <br>
	 * Usage of IS public services in the Listener Flow is causing Transaction
	 * issue, as the IS service completion listener is trying the cleanup the
	 * top transaction while the Listener has not yet completed the
	 * Transactional action(commit/rollback) . processing of JMS Headers in the MQ
	 * Message comes in runNotificaiton flow for creating a Notification document</br>
	 * 
	 * 
	 * @param xmldata
	 * @return
	 * @throws wmMQException
	 */
	private Values decodeXMLUsingISApis(String xmldata) throws wmMQException{
		
		// The following piece of code is taken from on 2 IS public services
		// pub.xml:xmlStringToXMLNode, pub.xml:xmlNodeToDocument
		if (xmldata != null && xmldata.length() > 0) {
			
			try {
				
				//xmlStringToXMLNode
				Document node = new Document(xmldata, null, null, false, null, true);
				
				IData in = IDataFactory.create();
				ValuesEmulator.put(in, "node", node);
				
				//xmlNodeToDocument
				DocumentToRecordService dtrs = new DocumentToRecordService(in, false);
				dtrs.setIsXTD( true );

				
				IDataCursor cursor = in.getCursor();
				cursor.first(ServerIf.RULEBIND_NODE);
				Object val = cursor.getValue();
				Object ret = dtrs.bind(val);
				
				if (ret instanceof IData) {
					if (cursor.first(ServerIf.RULEBIND_DOCUMENT)) {
						cursor.setValue(ret);
					} else {
						cursor.last();
						cursor.insertAfter(ServerIf.RULEBIND_DOCUMENT, ret);
					}
				}
								
				IData values = (IData) ValuesEmulator.get(in, "document");
				return Values.use(values);
			} catch (Exception e) {
				log(
						ARTLogger.DEBUG,
						1003,
						"Exception while coverting JMS Headers XML to IData using IS pub Apis :",
						e.getLocalizedMessage());
				throw new wmMQException("1003",
						"Exception while coverting JMS Headers XML to IData using IS pub Apis :"
								+ e.getLocalizedMessage());
			}
		}
		
		return null;
	}
	
	

    /**
     * Decode XML string
     *
     * Note: The XMLCoder object consistently threw a NullPointerException when
     *		 passed a string containing just XML data <xxx>.....</xxx> without any header info.
     *		 Rather than fight with the XMLCoder object, the author chose to create this routine.
     *
     * @param xml string containing XML data
     *
     * @return a Values object with each field from the XML string
     */
    protected Values decodeXMLUsingInternal(String xml) {

        log(ARTLogger.INFO, 1001, "decodeXMLUsingInternal", xml);

        Values xmldata = new Values();

        int length = xml.length();
        int offset = 0;

        int rightangle = -1;
        int endofproperty = -1;
        int propertylength;
        String tag;
        String endtag;
        String property;

        while (length > 0) {
            if (xml.charAt(offset) == '<') {
                rightangle = xml.indexOf(">", offset); //locate end of tag
                if (rightangle == -1)
                    return null;
                tag = xml.substring(offset + 1, rightangle); //construct endtag
                //changed if the tag contains a empty tag which happens in case of user defined jms tags
                /*if(tag.charAt(0)=='<'){
                	tag = tag.substring(1,tag.length());
                }
                int space = tag.indexOf(" ");
                if(space !=-1){
                	tag = tag.substring(0, space);
                }*/
                endtag = "</" + tag + ">";
                
                // iTrac IMQ-860 [Publish fails when custom JMS properties are empty]
                // Check if we are dealing with <tag/> element tag
                if(endtag.endsWith("/>")) {
                	// Adjust the element tag text 'tag/'
                	tag = tag.substring(0, tag.length() - 1);
                	xmldata.put(tag, "");
                	
                	offset+= tag.length() + 3;
                	length-= tag.length() + 3;
                	// Break at the current level
                	continue;
                }
                // iTrac IMQ-860 
                
                // Trax 1-1WMSHB [ XML code from RFH2 header is wrongly parsed ]
                //	endofproperty = xml.indexOf(endtag);
                	endofproperty = xml.indexOf(endtag, offset); //locate endtag
                //! Trax 1-1WMSHB
                	
                if (endofproperty == -1)
                    return null;
                propertylength = endofproperty - rightangle - endtag.length(); //determine length of data
                property = xml.substring(rightangle + 1, endofproperty); //extract data
                
                // Trax 1-1WMSHB [ XML code from RFH2 header is wrongly parsed ]
                if (property.indexOf("<") > -1) {
                	Values newValue = decodeXMLUsingInternal(property);
                	Object existingValue = xmldata.get(tag);
                	// Check if there is an existing element
                	if(existingValue == null) {
                		xmldata.put(tag, newValue); //property is not lowest level
                	}
                	else {
                		Values[] newValues = null;
                		// Existing element found. Create/Update the array 
                		if(existingValue instanceof Values) {
                			// Create and add array values
                			newValues = new Values[2];
                			newValues[0] = (Values) existingValue;
                			newValues[1] = newValue;
                		}
                		else if(existingValue instanceof Values[]) {
                			Values[] existingValues = (Values[]) existingValue;
                			// Copy the values to new array and append the new value
                			newValues = new Values[existingValues.length + 1];
                			System.arraycopy(existingValues, 0, newValues, 0, existingValues.length);
                			newValues[newValues.length - 1] = newValue;
                		}
                		// Update the data
                		if(newValues != null) {
                			xmldata.put(tag, newValues);
                		}
                	}
                }
                else {
                	String newProperty = jmsUnEscape(property);
                	Object existingProperty = xmldata.get(tag);
                	// Check if there is an existing element                	
                	if(existingProperty == null) {
                		xmldata.put(tag, newProperty); //property is lowest level
                	}
                	else {
                		String[] newProperties = null;
                		// Existing element found. Create/Update the array                		
                		if(existingProperty instanceof String) {
                			// Create and add array of properties
                			newProperties = new String[2];
                			newProperties[0] = (String) existingProperty;
                			newProperties[1] = newProperty;
                		}
                		else if(existingProperty instanceof String[]) {
                			String[] existingProperties = (String[]) existingProperty;
                			// Copy the properties to new array and append the new property                			
                			newProperties = new String[existingProperties.length + 1];
                			System.arraycopy(existingProperties, 0, newProperties, 0, existingProperties.length);
                			newProperties[newProperties.length - 1] = jmsUnEscape(property);
                		}
                		// Update the data                		
                		if(newProperties != null) {
                			xmldata.put(tag, newProperties);
                		}
                	}
                }
                //! Trax 1-1WMSHB
                
                length -= (endofproperty + endtag.length() - offset);
                offset = (endofproperty + endtag.length());
            }
            else {
                offset++;
                length--;
            }
        }

        log(ARTLogger.INFO, 1002, "decodeXMLUsingInternal", "");
        return xmldata;
    }

    public int getReasonCode() {
        return _reasonCode;
    }

    public void setReasonCode(int reason) {
        _reasonCode = reason;
    }

    public boolean isQueueEmpty() {
        return (_reasonCode == MQException.MQRC_NO_MSG_AVAILABLE);
    }

    /**
     * Append instance contents to the input buffer in a human-readable format.
     *
     * @param destBuf Buffer to which contents are appended.
     *
     */
    public void dump(StringBuffer destBuf) {
        if (destBuf != null) {
            destBuf.append("wmMQMessage contents:\n");

            //  TO DO it isn't required that you add more here, but it can come
            //    in real handy for debugging.   Maybe dump parameter values,
            //    or state flags or....

            destBuf.append("wmMQMessage contents end\n");
        }

    } //  dump()

	// determine if byte array is not null
	private boolean arrayIsNotNull(byte[] bytes)
	{
		if ((bytes == null) || (bytes.length == 0))
			return false;
		for (int i = 0 ; i < bytes.length ; i++)
		{
			if (bytes[i] != 0)
			{
				return true;
			}
		}
		return false;
	}


    private static int[] predefined_feedback_codes = 
    {	0,			//MQC.MQFB_NONE
       	258,		//MQC.MQFB_EXPIRATION
		259,		//MQC.MQFB_COA
		260,		//MQC.MQFB_COD
		256,		//MQC.MQFB_QUIT
		262,		//MQC.MQFB_CHANNEL_COMPLETED
		263,		//MQC.MQFB_CHANNEL_FAIL_RETRY
		264,		//MQC.MQFB_CHANNEL_FAIL
		265,		//MQC.MQFB_APPL_CANNOT_BE_STARTED
		266,		//MQC.MQFB_TM_ERROR
		267,		//MQC.MQFB_APPL_TYPE_ERROR
		268,		//MQC.MQFB_STOPPED_BY_MSG_EXIT
		271,		//MQC.MQFB_XMIT_Q_MSG_ERROR
		275,		//MQC.MQFB_PAN
		276,		//MQC.MQFB_NAN
		291,		//MQC.MQFB_DATA_LENGTH_ZERO
		292,		//MQC.MQFB_DATA_LENGTH_NEGATIVE
		293,		//MQC.MQFB_DATA_LENGTH_TOO_BIG
		294,		//MQC.MQFB_BUFFER_OVERFLOW
		295,		//MQC.MQFB_LENGTH_OFF_BY_ONE
		296,		//MQC.MQFB_IIH_ERROR
		1,			//MQFB_SYSTEM_FIRST
		65535,		//MQFB_SYSTEM_LAST 
		65536,		//MQFB_APPL_FIRST
		999999999	//MQFB_APPL_LAST 
    };
    
 //   public static String[] encodings = {
    private static final String[] encodings = {		
        "00273 - Native",
        "00273 - Integer normal,    Decimal normal,    Float IEEE normal",
        "00274 - Integer reversed,  Decimal normal,    Float IEEE normal",
        "00289 - Integer normal,    Decimal reversed,  Float IEEE normal",
        "00290 - Integer reversed,  Decimal reversed,  Float IEEE normal",
        "00529 - Integer normal,    Decimal normal,    Float IEEE reversed",
        "00530 - Integer reversed,  Decimal normal,    Float IEEE reversed",
        "00545 - Integer normal,    Decimal reversed,  Float IEEE reversed",
        "00546 - Integer reversed,  Decimal reversed,  Float IEEE reversed",
        "00785 - Integer normal,    Decimal normal,    Float S390",
        "00786 - Integer reversed,  Decimal normal,    Float S390",
        "00801 - Integer normal,    Decimal reversed,  Float S390",
        "00802 - Integer reversed,  Decimal reversed,  Float S390"};

    //JMSProperty names and their corresponding XML tags
    public static String[][] JMSPropertyNames = { //NOSONAR
        {
        wmMQAdapterConstant.JMS_EXPIRATION, "Exp"}
        , {
        wmMQAdapterConstant.JMS_PRIORITY, "Pri"}
        , {
        wmMQAdapterConstant.JMS_DELIVERY_MODE, "Dlv"}
        , {
        wmMQAdapterConstant.JMS_CORRELATION_ID, "Cid"}
        , {
        wmMQAdapterConstant.JMS_CORRELATION_ID_BYTE_ARRAY, "Cid"}
        , {
        wmMQAdapterConstant.JMS_REPLY_TO, "Rto"}
        , {
        wmMQAdapterConstant.JMSX_GROUP_ID, "Gid"}
        , {
        wmMQAdapterConstant.JMSX_GROUP_ID_BYTE_ARRAY, "Gid"}
        , {
        wmMQAdapterConstant.JMSX_GROUP_SEQ, "Seq"}
        , {
        wmMQAdapterConstant.JMSX_USER_ID, ""}
        , {
        wmMQAdapterConstant.JMSX_APPL_ID, ""}
        , {
        wmMQAdapterConstant.JMS_MESSAGE_ID, ""}
        , {
        wmMQAdapterConstant.JMS_TIMESTAMP, "Tms"}
        , {
        wmMQAdapterConstant.JMS_REDELIVERED, ""}
        , {
        wmMQAdapterConstant.JMSX_DELIVERY_COUNT, ""}
        
        // The following are the properties in the <mcd> folder of MQRFH2 header. 
        , {
        wmMQAdapterConstant.MCD_MESSAGE_DOMAIN, "Msd"}
        , {
        wmMQAdapterConstant.MCD_MESSAGE_SET, "Set"}
        , {
        wmMQAdapterConstant.MCD_MESSAGE_FORMAT, "Fmt"}
        ,
        //These last two properties are special:
        //The JMSDestination property will always be set, even if it is not supplied.
        //It has a default value of queue://QMgrName/QueueName.
        //The JMSType property resides in the <mcd> folder in the MQRFH2 header, not
        //the <jms> folder.
        //
        //The setJMSHeader processes these last properties outside its
        //processing loop.
        {
        wmMQAdapterConstant.JMS_DESTINATION, "Dst"}
        , {
        wmMQAdapterConstant.JMS_TYPE, "Type"}
    };
	public static final int JMS_DESTINATION_INDEX = JMSPropertyNames.length - 2;
	public static final int JMS_TYPE_INDEX = JMSPropertyNames.length - 1;
	public static final int JMS_TIMESTAMP_INDEX = JMSPropertyNames.length - 8;
	public static final int MCD_MESSAGE_FORMAT_INDEX = JMSPropertyNames.length - 3;
	public static final int MCD_MESSAGE_SET_INDEX = JMSPropertyNames.length - 4;
	public static final int MCD_MESSAGE_DOMAIN_INDEX = JMSPropertyNames.length - 5;

	private static final String[][] jmsEscapeCharacters = {{"&", "&amp;"},
													{"<", "&lt;"},
													{">", "&gt;"},
													{"\'", "&apos;"},
													{"\"", "&quot;"}};

    private static final String[] supportedCCSIDs = {
        "MQCCSI_Q_MGR", "MQCCSI_INHERIT",
        "CP037", "CP273", "CP277", "CP278", "CP280", "CP284", "CP285",
        "CP297", "CP420", "CP424", "CP437", "CP500", "CP737", "CP775",
        "CP813", "CP819", "CP838", "CP850", "CP852", "CP855", "CP856",
        "CP857", "CP860", "CP861", "CP862", "CP863", "CP864", "CP865",
        "CP866", "CP868", "CP869", "CP870", "CP871", "CP874", "CP875",
        "CP912", "CP913", "CP914", "CP915", "CP916", "CP918", "CP920",
        "CP921", "CP922", "CP930", "CP932", "CP933", "CP935", "CP937",
        "CP939", "CP942", "CP948", "CP949", "CP950", "CP954", "CP964",
        "CP970", "CP1006", "CP1025", "CP1026", "CP1089", "CP1097", "CP1098",
        "CP1112", "CP1122", "CP1123", "CP1124", "CP1200", "CP1208", "CP1250",
        "CP1251", "CP1252", "CP1253", "CP1254", "CP1255", "CP1256", "CP1257",
        "CP1258", "CP1381", "CP1383", "CP2022", "CP5601", "CP33722"};

    //These two arrays specify the names and integer values of all predefined values
    //for the PutApplicationType field. If either of these arrays is modified, the
    //other array must also be modified to keep the entries in sync.
    private static final String[] PutApplicationTypes = {
        "UNKNOWN",
        "NO_CONTEXT",
        "CICS",
        "MVS",
        "IMS",
        "OS2",
        "DOS",
        "AIX",
        "UNIX",
        "QMGR",
        "OS400",
        "WINDOWS",
        "CICS_VSE",
        "WINDOWS_NT",
        "VMS",
        "NSK",
        "GUARDIAN",
        "VOS",
        "IMS_BRIDGE",
        "XCF",
        "CICS_BRIDGE",
        "NOTES_AGENT",
        "JAVA",
        "DEFAULT"};
    private static final int PutApplicationTypesInt[] = {
         -1, 0, 1, 2, 3, 4, 5, 6, 6, 7, 8, 9, 10,
        11, 12, 13, 13, 14, 19, 20, 21, 22, 28, 0x10000};

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
    
    // 1-1KQMJN*
    /**
     * True if the user has explicitly set the Date or Time field of the MH
     * 
     * @return
     */
    public boolean isUserSetDateTime() {
	return _userSetDateTime;
    }
    // 1-1KQMJN

	/**
	 * @return the convertMsgBodyToString
	 */
	public boolean isConvertMsgBodyToString() {
		return convertMsgBodyToString;
	}

	/**
	 * @param convert
	 *            Sets whether to convert the Message Body to String in the
	 *            MQMessage
	 */
	public void setConvertMsgBodyToString(boolean convert) {
		this.convertMsgBodyToString = convert;
	}
	
	/**
	 * IMQ-1079
	 * As the signature of Message Headers MsgId, CorrelId is changed to Object.
	 * It became inevitable to support the older, already created
	 * notification/services which has their dataType as Strings for MsgId,
	 * CorrelId. HeadeDataTypes will contain the dataType of these special
	 * fields.The dataType is obtained by introspecting the field dataType in
	 * PublishableDocument/Output signature of the service.
	 * 
	 * 
	 */
	public void setSplHeaderDataTypes(HashMap dataTypes) {

		this.HeaderDataTypes = dataTypes;
	}

} //   wmMQMessage class