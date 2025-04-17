/**
 * WmMQAdapterUtils.java
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
package com.wm.adapter.wmmqadapter;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.HashMap;
import java.util.Map;

import com.wm.adk.log.ARTLogger;
import com.wm.lang.ns.NSField;
import com.wm.lang.ns.NSRecord;

/*
 * This class contains all the utility methods of the WmMQAdapter
 * 
 */
public class WmMQAdapterUtils {

	static Map  _javaEncodingNameMap;

	static {
		String[] _javaEncodingNameTable = {
											"UTF8", 	"1208",
											"ISO8859_1", 	"819",
											"ISO8859_2", 	"912",
											"ISO8859_3", 	"913",
											"ISO8859_5", 	"915",
											"ISO8859_6", 	"1089",
											"ISO8859_7", 	"813",
											"ISO8859_8", 	"916",
											"ISO8859_9", 	"920",
											"ISO8859_13",	"921",
											"ISO8859_15",	"923",
											"8859_1", 	"819",
											"8859_2", 	"912",
											"8859_3", 	"913",
											"8859_5", 	"915",
											"8859_6", 	"1089",
											"8859_7", 	"813",
											"8859_8", 	"916",
											"8859_9", 	"920",
											"8859_13",	"921",
											"8859_15",	"923",
											"Big5", 	"950",
											"EUC_CN", 	"1383",
											"EUC-CN", 	"1383",
											"EUC_JP", 	"954",
											"EUC-JP", 	"954",
											"EUC_TW", 	"964",
											"EUC-TW", 	"964",
											"EUC_KR", 	"970",
											"EUC-KR", 	"970",
											"PCK", 		"943",
											"SJIS", 	"932",
											"GBK", 		"1386"
										  };
		
		_javaEncodingNameMap = new HashMap(_javaEncodingNameTable.length/2, 1);
		for (int i=0; i<_javaEncodingNameTable.length; i+=2) {
			_javaEncodingNameMap.put(_javaEncodingNameTable[i], Integer.valueOf(_javaEncodingNameTable[i+1]));
		}
	}
	
	//The order of HeaderNames & Headerpaths has to be in sync.
	private static String[] Headerpaths = {"msgHeader/MsgId","msgHeader/CorrelId"};
	private static String[] HeaderNames = {wmMQAdapterConstant.MQMD_MESSAGE_ID,wmMQAdapterConstant.MQMD_CORRELATION_ID};

	
	 /**
	  * Derive Coded Character Set Id from string.
	  *
	  * @param ccsid string containing name of ccsid
	  *
	  */
	 public static int getCharacterSetId(String ccsid, int defaultValue)
	 {
	     if ((ccsid == null) || (ccsid.trim().equals("")))
	         return defaultValue;

	     //First, check some pre-defined names
	     if (ccsid.equals("Cp037"))
	         return 37;
	     else if (ccsid.equals("JIS"))
	         return 2022;
	     else if (ccsid.equals("SJIS"))
	         return 932;
	     else if (ccsid.equals("EUCJIS"))
	         return 954;
	     else if (ccsid.equals("UTF-8"))
	         return 1208;
            // Trax 1-1RWSM4
	     else if (ccsid.equals("UTF-16"))
	         return 1200;
            // Trax 1-1RWSM4*
	     else if (ccsid.equals("KSC5601"))
	         return 5601;
	     else if (ccsid.equals("MQCCSI_Q_MGR"))
	         return 0; //MQCCSI_Q_MGR;
	     else if (ccsid.equals("MQCCSI_INHERIT"))
			// Trax 1-12K2HK : Since MQCCSI_INHERIT on the MQMD is same as MQCCSI_Q_MGR 
	    	// return MQCCSI_Q_MGR instead of MQCCSI_INHERIT.
			// This is done as the msg.setMsgBody(msgbody) throws an UnsupportedEncodingException if the 
			// characterSet is set to MQCCSI_INHERIT
//	         return MQC.MQCCSI_INHERIT;
	         return 0; //MQCCSI_Q_MGR;

	     int value = defaultValue;
	     try
	     {
	         //Second, check for 'CP' or 'MS' prefix
	         if (ccsid.toUpperCase().startsWith("CP") || ccsid.toUpperCase().startsWith("MS"))
	             value = Integer.parseInt(ccsid.substring(2));
	         else
	             //oh well, assume that its just a number
	             value = Integer.parseInt(ccsid);
	     }
	     catch (java.lang.NumberFormatException nfe)
	     {
	         value = defaultValue;
	     }

	     return value;

	 }
	 
	 public static int getCharacterSetId(String ccsid) {
		 return getCharacterSetId(ccsid, 819);
	 }	
	 
	 public static int getSystemDefaultCharacterSetId() {
		 String ccsid = System.getProperty("file.encoding");
		 Integer characterSetId = (Integer)_javaEncodingNameMap.get(ccsid.trim());
		 if(characterSetId == null) {
			 return getCharacterSetId(ccsid);
		 }
		 else {
			 return characterSetId.intValue();
		 }
	 }
		
	/**
	 * Derive display value of Coded Character Set Id from integer.
	 * 
	 * @param int
	 *            string containing name of ccsid
	 * 
	 */
    public static String getCharacterSetString(int ccsid) {

        switch (ccsid) {
            case 37:
                return "Cp037";
            case 2022:
                return "JIS";
            case 932:
                return "SJIS";
            case 954:
                return "EUCJIS";
            case 1208:
                return "UTF-8";
            // Trax 1-1RWSM4
            case 1200:
        	return "UTF-16";
            // Trax 1-1RWSM4*
            case 5601:
                return "KSC5601";
            default:
                return "CP" + String.valueOf(ccsid);
        }
    }
    
    /**
     * Determine if MQException's reason code is fatal
     *
     * @param reasonCode the reason code from an MQException
     *
     */
    public static boolean fatalException(int reasonCode) {
        if ( (reasonCode == 2009) ||  // MQRC_CONNECTION_BROKEN
             (reasonCode == 2012) ||  // MQRC_ENVIRONMENT_ERROR
             (reasonCode == 2018) ||  // MQRC_HCONN_ERROR
//Trax 1-14CUA5 Adds 2019 as fatal exception
             (reasonCode == 2019) ||  // MQRC_HOBJ_ERROR
//Trax 1-1031MB Adds 2042 as fatal exception(removed explicit check made in the wmMQListener)             
             (reasonCode == 2042) ||  // MQRC_OBJECT_IN_USE
             (reasonCode == 2059) ||  // MQRC_Q_MGR_NOT_AVAILABLE
//Trax 1-????? Addes 2085 as fatal exception
			 (reasonCode == 2085) ||  // MQRC_UNKNOWN_OBJECT_NAME
             (reasonCode == 2086) ||  // MQRC_UNKNOWN_OBJECT_Q_MGR
             (reasonCode == 2087) ||  // MQRC_UNKNOWN_REMOTE_Q_MGR
             (reasonCode == 2101) ||  // MQRC_OBJECT_DAMAGED
             (reasonCode == 2161) ||  // MQRC_Q_MSG_QUIESCING
             (reasonCode == 2162) ||  // MQRC_Q_MSG_STOPPING
             (reasonCode == 2195) ||  // MQRC_UNEXPECTED_ERROR
             (reasonCode == 2202) ||  // MQRC_CONNECTION_QUIESCING
             (reasonCode == 2203) )   // MQRC_CONNECTION_STOPPING

            return true;

        return false;
    }
 
    public static void logCritical(int minor, String[] args) {
        log(ARTLogger.CRITICAL - ARTLogger.DEBUG, minor, args);
    }
    
    public static void logError(int minor, String[] args) {
        log(ARTLogger.ERROR - ARTLogger.DEBUG, minor, args);
    }
    
    public static void logException(Throwable e) {
    	ARTLogger logger = ((wmMQAdapter)wmMQAdapter.getInstance()).getLogger();
    	if(logger == null)
    		return;
    	logger.logDebugPlus(ARTLogger.VERBOSE4, 1055,getStackTrace(e));
    }
    
    public static String getStackTrace(Throwable e) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);
    	pw.close();
    	return sw.toString();
    	
    }
    
    public static void logWarning(int minor, String[] args) {
        log(ARTLogger.WARNING - ARTLogger.DEBUG, minor, args);
    }
    
    public static void log(int level, int minor, String arg0, String arg1) {
        log(level, minor, new String[]{arg0, arg1});
    }
    
    public static void log(int level, int minor, String[] args) {
        ARTLogger logger = ((wmMQAdapter)wmMQAdapter.getInstance()).getLogger();
        if (logger == null) {
            System.out.println("Logger is null");
            return;
        }
        
        //Trax 1-WVILA. Allow user to override the logging level of adapter messages. 
        if (wmMQAdapter.getLogLevelOverrides().containsKey("" + minor)) {
        	level =  Integer.parseInt((String)wmMQAdapter.getLogLevelOverrides().get("" + minor)) - ARTLogger.DEBUG;
        }
        
        //Trax log 1-S4OHA - move adapter logging to DEBUG PLUS
        logger.logDebugPlus(level, minor, args);
    }
    
    /**
     * 
     * @param minor
     * @param args
     */
    public static void logInfoMsg(int minor, String args) {
        ARTLogger logger = ((wmMQAdapter)wmMQAdapter.getInstance()).getLogger();
        if (logger == null) {
            System.out.println("Logger is null");
            return;
        }

        //Logging to message at INFO for specific request
        logger.logInfo(minor, args);
    }
    
	/**
	 * IMQ-1079
	 * 
	 * Introspect the Record(nsRec) to identify the dataType of the Special
	 * header fields. MsgID, CorrelID dataTypes are changed to Object from Fix
	 * 20 onwards. To make sure services, notifications to work as is after
	 * applying latest fixes, these fields are introspected to find out their
	 * dataTypes & the data is passed is accordingly
	 * 
	 * @param nsRec
	 * @return
	 */
	public static HashMap findFieldDataTypes(NSRecord nsRec) {

		HashMap types = new HashMap(Headerpaths.length);

		if (nsRec != null) {
			for (int i = 0; i < Headerpaths.length; i++) {
				NSField field = nsRec.find(Headerpaths[i], null, false, true,
						true);
				if (field != null) {
					int type = field.getType();
					String value = type == 1 ? "string" : "object";
					types.put(HeaderNames[i], value);
				}
			}
		}
		return types;
	}
    
}
