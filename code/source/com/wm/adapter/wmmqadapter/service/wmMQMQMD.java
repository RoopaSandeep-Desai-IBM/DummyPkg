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

package com.wm.adapter.wmmqadapter.service;

import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.pkg.art.isproxy.Config;

/**
 * <p>Title: webMethods webSphere MQ adapter</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: webMethods, Inc.</p>
 * @author Patrick S. Hayes
 * @version 6.0
 */

public class wmMQMQMD
{
  public wmMQMQMD()
  {
  }

    //private String[]    mqmdFields;
    private String[]    mqmdInputFields;
    private String[]    mqmdInputFieldTypes;
    private boolean[]   useMQMDFields;
    private String[]    mqmdOutputFields;
    private String[]    mqmdOutputFieldTypes;
    private boolean[]   useOutputMQMDFields;
    private String[]    mqmdInputConstants;
//    private String[]    mqmdOutputConstants;
    public static String CorrelID_DataType = "object";//NOSONAR
    public static String MessageID_DataType = "object";//NOSONAR

    public void setMqmdInputFields(String[] fields)
    {
        mqmdInputFields = fields;//NOSONAR
    }

    public void setUseMQMDFields(boolean[] use)
    {
        useMQMDFields = use;//NOSONAR
    }

    public void setMqmdInputFieldTypes(String[] types)
    {
        mqmdInputFieldTypes = types;//NOSONAR
    }

    public void setMqmdOutputFields(String[] fields)
    {
        mqmdOutputFields = fields;//NOSONAR
    }

    public void setUseOutputMQMDFields(boolean[] use)
    {
        useOutputMQMDFields = use;//NOSONAR
    }

    public void setMqmdOutputFieldTypes(String[] types)
    {
        mqmdOutputFieldTypes = types;//NOSONAR
    }

    public void setMqmdInputConstants(String[] constants)
    {
        mqmdInputConstants = constants;//NOSONAR
    }

    public String[] getMqmdInputFields()
    {
        return mqmdInputFields;//NOSONAR
    }

    public boolean[] getUseMQMDFields()
    {
        return useMQMDFields;//NOSONAR
    }

    public String[] getMqmdInputFieldTypes()
    {
        return mqmdInputFieldTypes;//NOSONAR
    }

    public String[] getMqmdOutputFields()
    {
        return mqmdOutputFields;//NOSONAR
    }

    public boolean[] getUseOutputMQMDFields()
    {
        return useOutputMQMDFields;//NOSONAR
    }

    public String[] getMqmdOutputFieldTypes()
    {
        return mqmdOutputFieldTypes;//NOSONAR
    }

    public String[] getMqmdInputConstants() {
        return mqmdInputConstants;//NOSONAR
    }

    /*PIEAR-616,IMQ-1079*/
	static {

		CorrelID_DataType = Boolean
				.valueOf(
						Config.getProperty("watt.WmMQAdapter.MQMD.CorrelID.DataType.String"))
				.booleanValue() ? "string" : "object";
		
		MessageID_DataType = Boolean
				.valueOf(
						Config.getProperty("watt.WmMQAdapter.MQMD.MessageID.DataType.String"))
				.booleanValue() ? "string" : "object";
	}
    
    public static String[] supportedCCSIDs = { //NOSONAR
        "MQCCSI_Q_MGR", "MQCCSI_INHERIT",
        "CP037",  "CP273",  "CP277",  "CP278",  "CP280",  "CP284",  "CP285",
        "CP297",  "CP420",  "CP424",  "CP437",  "CP500",  "CP737",  "CP775",
        "CP813",  "CP819",  "CP838",  "CP850",  "CP852",  "CP855",  "CP856",
        "CP857",  "CP860",  "CP861",  "CP862",  "CP863",  "CP864",  "CP865",
        "CP866",  "CP868",  "CP869",  "CP870",  "CP871",  "CP874",  "CP875",
        "CP912",  "CP913",  "CP914",  "CP915",  "CP916",  "CP918",  "CP920",
        "CP921",  "CP922",  "CP930",  "CP932",  "CP933",  "CP935",  "CP937",
        "CP939",  "CP942",  "CP948",  "CP949",  "CP950",  "CP954",  "CP964",
        "CP970",  "CP1006", "CP1025", "CP1026", "CP1089", "CP1097", "CP1098",
        "CP1112", "CP1122", "CP1123", "CP1124", "CP1200", "CP1208", "CP1250",
        "CP1251", "CP1252", "CP1253", "CP1254", "CP1255", "CP1256", "CP1257",
        "CP1258", "CP1381", "CP1383", "CP2022", "CP5601", "CP33722"};

    public static String[] encodings = { //NOSONAR
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
        "00802 - Integer reversed,  Decimal reversed,  Float S390" };

    public static String[] encodingsForDisplay = { //NOSONAR
	    "00273-Native",
	    "00273-Integer_Normal,Decimal_Normal,Float_IEEE_Normal",
	    "00274-Integer_Reversed,Decimal_Normal,Float_IEEE_Normal",
	    "00289-Integer_Normal,Decimal_Reversed,Float_IEEE_Normal",
	    "00290-Integer_Reversed,Decimal_Reversed,Float_IEEE_Normal",
	    "00529-Integer_Normal,Decimal_Normal,Float_IEEE_Reversed",
	    "00530-Integer_Reversed,Decimal_Normal,Float_IEEE_Reversed",
	    "00545-Integer_Normal,Decimal_Reversed,Float_IEEE_Reversed",
	    "00546-Integer_Reversed,Decimal_Reversed,Float_IEEE_Reversed",
	    "00785-Integer_Normal,Decimal_Normal,Float_S390",
	    "00786-Integer_Reversed,Decimal_Normal,Float_S390",
	    "00801-Integer_Normal,Decimal_Reversed,Float_S390",
	    "00802-Integer_Reversed,Decimal_Reversed,Float_S390" };
    
    public static final String[] mqmdFieldTypes = { //NOSONAR
        "string", "object", "string", "string", "string", "string",
        CorrelID_DataType , "object", "string", "string", "string", "string",
        "string", "object", "string", MessageID_DataType, "object", "string",
        "string", "string", "string", "string", "string", "string",
        "string", "string", "string", "string", "string", "string",
        "string"};

    public static final String[] selectionCriteria = new String[] { //NOSONAR
        "selectionCriteria." + wmMQAdapterConstant.MQMD_CORRELATION_ID,
        "selectionCriteria." + wmMQAdapterConstant.MQMD_CORRELATION_ID_BYTE_ARRAY,
        "selectionCriteria." + wmMQAdapterConstant.MQMD_GROUP_ID,
        "selectionCriteria." + wmMQAdapterConstant.MQMD_GROUP_ID_BYTE_ARRAY,
        "selectionCriteria." + wmMQAdapterConstant.MQMD_MESSAGE_ID,
        "selectionCriteria." + wmMQAdapterConstant.MQMD_MESSAGE_ID_BYTE_ARRAY,
        "selectionCriteria." + wmMQAdapterConstant.MQMD_MESSAGE_SEQUENCE_NUMBER,
        "selectionCriteria." + wmMQAdapterConstant.MQMD_OFFSET};

	public static final String[] selectionCriteriaTypes = { //NOSONAR
		CorrelID_DataType, "object", "string", "object", MessageID_DataType, "object",
		"string", "string"};


	public static final String[] mqmdFieldDisplayNames = new String[] { //NOSONAR
		"msgHeader." + wmMQAdapterConstant.MQMD_ACCOUNTING_TOKEN,
		"msgHeader." + wmMQAdapterConstant.MQMD_ACCOUNTING_TOKEN_BYTE_ARRAY,
		"msgHeader." + wmMQAdapterConstant.MQMD_APPLICATION_ID_DATA,
		"msgHeader." + wmMQAdapterConstant.MQMD_APPLICATION_ORIGIN_DATA,
		"msgHeader." + wmMQAdapterConstant.MQMD_BACKOUT_COUNT,
		"msgHeader." + wmMQAdapterConstant.MQMD_CHARACTER_SET,
		"msgHeader." + wmMQAdapterConstant.MQMD_CORRELATION_ID,
		"msgHeader." + wmMQAdapterConstant.MQMD_CORRELATION_ID_BYTE_ARRAY,
		"msgHeader." + wmMQAdapterConstant.MQMD_ENCODING,
		"msgHeader." + wmMQAdapterConstant.MQMD_EXPIRY,
		"msgHeader." + wmMQAdapterConstant.MQMD_FEEDBACK,
		"msgHeader." + wmMQAdapterConstant.MQMD_FORMAT,
		"msgHeader." + wmMQAdapterConstant.MQMD_GROUP_ID,
		"msgHeader." + wmMQAdapterConstant.MQMD_GROUP_ID_BYTE_ARRAY,
		"msgHeader." + wmMQAdapterConstant.MQMD_MESSAGE_FLAGS,
		"msgHeader." + wmMQAdapterConstant.MQMD_MESSAGE_ID,
		"msgHeader." + wmMQAdapterConstant.MQMD_MESSAGE_ID_BYTE_ARRAY,
		"msgHeader." + wmMQAdapterConstant.MQMD_MESSAGE_TYPE,
		"msgHeader." + wmMQAdapterConstant.MQMD_MESSAGE_SEQUENCE_NUMBER,
		"msgHeader." + wmMQAdapterConstant.MQMD_OFFSET,
		"msgHeader." + wmMQAdapterConstant.MQMD_ORIGINAL_LENGTH,
		"msgHeader." + wmMQAdapterConstant.MQMD_PERSISTENCE,
		"msgHeader." + wmMQAdapterConstant.MQMD_PRIORITY,
		"msgHeader." + wmMQAdapterConstant.MQMD_PUT_APPLICATION_NAME,
		"msgHeader." + wmMQAdapterConstant.MQMD_PUT_APPLICATION_TYPE,
		"msgHeader." + wmMQAdapterConstant.MQMD_PUT_DATE,
		"msgHeader." + wmMQAdapterConstant.MQMD_PUT_TIME,
		"msgHeader." + wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_MANAGER_NAME,
		"msgHeader." + wmMQAdapterConstant.MQMD_REPLYTO_QUEUE_NAME,
		"msgHeader." + wmMQAdapterConstant.MQMD_REPORT,
		"msgHeader." + wmMQAdapterConstant.MQMD_USER_ID,};

	public static final boolean[] allMQMDFields = {true, true, true, true, true, true, //NOSONAR
												   true, true, true, true, true, true,	
												   true, true, true, true, true, true,	
												   true, true, true, true, true, true,	
												   true, true, true, true, true, true,	
												   true	
												  };

    public static final String[] mqmdFeedbackOptions = { //NOSONAR
        "MQFB_NONE",
        "MQFB_EXPIRATION",
        "MQFB_COA",
        "MQFB_COD",
        "MQFB_QUIT",
        "MQFB_CHANNEL_COMPLETED",
        "MQFB_CHANNEL_FAIL_RETRY",
        "MQFB_CHANNEL_FAIL",
        "MQFB_APPL_CANNOT_BE_STARTED",
        "MQFB_TM_ERROR",
        "MQFB_APPL_TYPE_ERROR",
        "MQFB_STOPPED_BY_MSG_EXIT",
        "MQFB_XMIT_Q_MSG_ERROR",
		"MQFB_PAN",
		"MQFB_NAN",
        "MQFB_DATA_LENGTH_ZERO",
        "MQFB_DATA_LENGTH_NEGATIVE",
        "MQFB_DATA_LENGTH_TOO_BIG",
        "MQFB_BUFFER_OVERFLOW",
        "MQFB_LENGTH_OFF_BY_ONE",
        "MQFB_IIH_ERROR",
        "MQFB_SYSTEM_FIRST",
        "MQFB_SYSTEM_LAST",
        "MQFB_APPL_FIRST",
        "MQFB_APPL_LAST"};

    public static final String[] mqmdFormatOptions = { //NOSONAR
        " (NONE) ",
        "MQADMIN ",
        "MQCHCOM ",
        "MQCMD1  ",
        "MQCMD2  ",
        "MQDEAD  ",
        "MQEVENT ",
        "MQPCF   ",
        "MQSTR   ",
        "MQTRIG  ",
        "MQXMIT  ",
        "MQCICS  ",
        "MQIMS   ",
        "MQIMSVS ",
        "MQHRF   ",
        "MQHRF2  ",
        "MQHDIST ",
        "MQHMDE  ",
        "MQHREF  "};

    public static final String[] mqmdMsgFlagOptions = { //NOSONAR
        "NONE",
        "Segmentation allowed",
        "Segment",
        "Last_Segment",
        "Message in Group",
        "Last Message in Group"};

    public static final String[] mqmdMsgTypeOptions = { //NOSONAR
		"Datagram",
		"Request",
		"Reply",
        "Report",
        "Appl First",
        "Appl Last",
        "System Last"};

    public static final String[] mqmdPersistenceOptions = { //NOSONAR
        "Not Persistent",
        "Persistent",
        "Persistence as Queue defined"};

    public static final String[] mqmdPriorityOptions = { //NOSONAR
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    //These two arrays specify the names and integer values of all predefined values
    //for the PutApplicationType field. If either of these arrays is modified, the
    //other array must also be modified to keep the entries in sync.
    public static String[] PutApplicationTypes = { //NOSONAR
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
        "DEFAULT" };

    public static int[] PutApplicationTypesInt	= { //NOSONAR
        -1 ,0, 1, 2, 3, 4, 5, 6, 6, 7, 8, 9, 10, 11, 12, 13, 13, 14, 19, 20, 21, 28, 28, 0x10000};

    public static final String[] jmsProperties = { //NOSONAR
        "JMSProperties.jms." + wmMQAdapterConstant.JMS_DESTINATION,
        "JMSProperties.jms." + wmMQAdapterConstant.JMS_EXPIRATION,
        "JMSProperties.jms." + wmMQAdapterConstant.JMS_PRIORITY,
        "JMSProperties.jms." + wmMQAdapterConstant.JMS_DELIVERY_MODE,
        "JMSProperties.jms." + wmMQAdapterConstant.JMS_CORRELATION_ID,
        "JMSProperties.jms." + wmMQAdapterConstant.JMS_CORRELATION_ID_BYTE_ARRAY,
        "JMSProperties.jms." + wmMQAdapterConstant.JMS_REPLY_TO,
        "JMSProperties.jms." + wmMQAdapterConstant.JMS_TYPE,
        "JMSProperties.jms." + wmMQAdapterConstant.JMS_MESSAGE_ID,
        "JMSProperties.jms." + wmMQAdapterConstant.JMS_TIMESTAMP,
        "JMSProperties.jms." + wmMQAdapterConstant.JMS_REDELIVERED,
        "JMSProperties.jms." + wmMQAdapterConstant.JMSX_GROUP_ID,
        "JMSProperties.jms." + wmMQAdapterConstant.JMSX_GROUP_ID_BYTE_ARRAY,
        "JMSProperties.jms." + wmMQAdapterConstant.JMSX_GROUP_SEQ,
        "JMSProperties.jms." + wmMQAdapterConstant.JMSX_USER_ID,
        "JMSProperties.jms." + wmMQAdapterConstant.JMSX_APPL_ID,
        "JMSProperties.jms." + wmMQAdapterConstant.JMSX_DELIVERY_COUNT,
        "JMSProperties.mcd." + wmMQAdapterConstant.MCD_MESSAGE_DOMAIN,
        "JMSProperties.mcd." + wmMQAdapterConstant.MCD_MESSAGE_SET,
        "JMSProperties.mcd." + wmMQAdapterConstant.MCD_MESSAGE_FORMAT
        };

    public static final String[] jmsPropertyTypes = { //NOSONAR
        "string", "string", "string", "string", "string", "string",
        "string", "string", "string", "string", "string", "string",
        "string", "string", "string", "string", "string", "string",
        "string", "string"
        };

	public static final String[] overrideConnectionTypes = { //NOSONAR
		"string", "string", "string", "string", "string", "string", "string"
		};
}
