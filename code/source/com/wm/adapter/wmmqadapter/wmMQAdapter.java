/*
 * wmMQAdapter.java
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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;

import com.ibm.mq.MQException;
import com.wm.adapter.wmmqadapter.connection.wmMQConnectionFactory;
import com.wm.adapter.wmmqadapter.connection.wmMQTransactionalConnectionFactory;
import com.wm.adapter.wmmqadapter.notification.wmMQAsyncListenerNotification;
import com.wm.adapter.wmmqadapter.notification.wmMQSyncListenerNotification;
//import com.wm.adk.ADKGLOBAL;
import com.wm.adk.WmAdapter;
import com.wm.adk.error.AdapterException;
import com.wm.adk.info.AdapterTypeInfo;
import com.wm.adk.log.ARTLogger;
//import com.wm.pkg.art.log.ARTJLog;
import com.wm.util.Values;

/*
 * This adapter consists of:
 * -- A connection to the predefined flat-file database provided by the tutorial.
 * -- Three polling notifications (based on three different metadata designs) that monitor
 * the database at specified intervals, and publish a document each time an update or
 * insert is performed against the database.
 * -- An update service that updates and inserts records into the database, based on the
 * key field, id.
 * -- Three lookup services (based on different metadata designs) that retrieve rows from
 * the database, based on the key field.
 */
public class wmMQAdapter extends WmAdapter
{
    //Instance of wmMQAdapter.
    static wmMQAdapter _instance = null;

    //Logger instance for wmMQAdapter.
    private ARTLogger _logger;

    //The name of the adapter.
    private static final String _adapterName = "wmMQAdapter";

    //The version of the adapter.
    private static final String _adapterVersion = "6.5";     

    //The supported JCA version.
    private static final String _adapterJcaVersion = "1.0";

    //The resource bundle class used by wmMQAdapter.
    private static final String _adapterResourceBundleName = "com.wm.adapter.wmmqadapter.wmMQAdapterResourceBundle";

	//TRAX 1-1BVR53. Constant value for supressing all MQ client generated logs
	private static final String _hideMQMessagesALL = "ALL";  

	//TRAX 1-1BVR53. Constant value for displaying all MQ client generated logs
	private static final String _hideMQMessagesNONE = "NONE";  

	//The cache of known QMgr objects
	private static Hashtable _connectionFactories = new Hashtable();

	private static String _adapterType = "";

	private static boolean 	_ignoreSystemDefaultDeadLetterQueue = true;

	private static String 	_actionBeforeDisconnect = "COMMIT";

    //Trax 1-WVILA. What to include in the dead letter message sent to DLQ (None, DLH, MQMD, Both)
	//				Value computed from watt property watt.WmMQAdapter.deadLetterMessageHeaders
	private static String  	_deadLetterMessageHeaders = wmMQAdapterConstant.deadLetterMessageHeaderOptions[3];	//Default is both DLH and MQMD
	
    //Trax 1-WVILA. Whether to include the payload data from the original mesage in the dead letter message
	//				Value determined from watt property watt.WmMQAdapter.includeDataInError3041
	private static boolean	_includeDataIn3041 = false;

    //Trax 1-WVILA. HashMap of adapter message numbers, and the logging level the customer wants (0 - 10)
	//				Value computed from watt property watt.WmMQAdapter.adapterMessageLogLevelOverride
	private static HashMap	_logLevelOverrides = new HashMap();
	
//  Trax 1-QPSPQ - SSL Support
	private static boolean	_SSLSupport = false;
//  Trax 1-QPSPQ - SSL Support
	
	// List all supported SSL and TLS CipherSpecs
	private static String _SSLListing = "";

	//iTrac IMQ-923
	private static boolean _msgIDBasedUUID=true;
	
	//  Trax 1-VF52N - disable OverrideConnection cache
	private static boolean	_CacheOverridenConnections = true;
	//  Trax 1-VF52N - disable OverrideConnection cache
	
	// iTrac 852 [ SetAll permission ]
	private static boolean _addDateTimeStamp = true;
	// iTrac 852
	
	private static boolean useInternalDecodeXml = true;
	
	private static boolean interospectHeaderDataTypes = false;
	
    /*
     * Constructor.
     */
    public wmMQAdapter() throws AdapterException
    {
        super();
    }

    /*
     * Gets the adapter instance. The Integration Server registers the instance
     * in the adapter package.
     */
    public static WmAdapter getInstance()
    {
        if (_instance != null)
            return _instance;
        else
        {
            synchronized (wmMQAdapter.class)
            {
                if (_instance != null)
                {
                    return _instance;
                }

                try
                {
                    _instance = new wmMQAdapter();
                    return _instance;
                }
                catch (Exception e)
                {
                    //If an exception ever occurs, this statement will cause a 
                    //StackOverFlowException, because the log() method invokes
                    //this getInstance() method, causing an ugly loop.  
                    //log(ARTLogger.ERROR,
                    //    1055,
                    //    "Instantiating wmMQAdapter",
                    //    t.getMessage());
					WmMQAdapterUtils.logException(e);
                    return null;
                }
            }
        }
    }

    /*
     * Gets the non Locale-specific name for this adapter.
     */
    public String getAdapterName()
    {
        return _adapterName;
    }

    /*
     * Gets the version of the adapter.
     */
    public String getAdapterVersion()
    {
        return _adapterVersion;
    }

    /*
     * Gets the version of the J2EE JCA specification this adapter implements.
     */
    public String getAdapterJCASpecVersion()
    {
        return _adapterJcaVersion;
    }

    /*
     * Gets the base Java class name for the adapter's resource bundle.
     */
    public String getAdapterResourceBundleName()
    {
        return _adapterResourceBundleName;
    }


    /*
    * Gets the Major Code, which is an integer code (or facility code)
    * that the adapter will use to perform journal logging.
    * The Major Code must be unique across all adapters installed
    * on the Integration Server.
    */
    public int   getAdapterMajorCode()
    {
        return wmMQAdapterConstant.ADAPTER_MAJOR_CODE;
    }

    /*
     * Gets the ARTLogger
     */
    public ARTLogger   getLogger()
    {
        return this._logger;
    }

    /*
     * Gets the __ignoreSystemDefaultDeadLetterQueue property
     */
    public static boolean  getIgnoreSystemDefaultDeadLetterQueue()
    {
        return _ignoreSystemDefaultDeadLetterQueue;
    }

    /*
     * Gets the __actionBeforeDisconnect property
     */
    public static String getActionBeforeDisconnect()
    {
        return _actionBeforeDisconnect;
    }

    /*
     * Gets the _deadLetterMessageHeaders property
     */
    public static String getDeadLetterMessageHeaders()
    {
        return _deadLetterMessageHeaders;
    }
    
    /*
     * Gets the _deadLetterMessageHeaders property
     */
    public static boolean getIncludeDataIn3041()
    {
        return _includeDataIn3041;
    }

    /*
     * Gets the _deadLetterMessageHeaders property
     */
    public static HashMap getLogLevelOverrides()
    {
        return _logLevelOverrides;
    }

//  Trax 1-QPSPQ - SSL Support
    /*
     * Gets the watt.WmMQAdapter.SSL.Support property
     */
    public static boolean getSSLSupport()
    {
        return _SSLSupport;
    }

    /*
     * Gets the watt.WmMQAdapter.Connection.CiphersList property
     * MQ Adapter Fix 44
     */
    public static String getSSLListing()
    {
    	return _SSLListing;
    }
    //iTrac IMQ-923
    /*
     * Gets the System Property watt.WmMQAdapter.UUID.msgIdBasedUUID
     */
    public static boolean getMsgIDBasedUUID(){
    	return _msgIDBasedUUID;
    }
//  Trax 1-QPSPQ - SSL Support
    
    //	  Trax 1-VF52N - disable OverrideConnection cache
    public static boolean getCacheOverriddenConnections()
    {
        return _CacheOverridenConnections;
    }
	//  Trax 1-VF52N - disable OverrideConnection cache
    
	// iTrac 852 [ SetAll permission ]
    public static boolean getAddDateTimeStamp()
    {
    	return _addDateTimeStamp;
    }
    // iTrac 852
    
    /*
     * Initializes properties/resources associated with this adapter type.
     * We instantiate the ARTLogger class, which will dynamically
     * insert the Major Code into the Integration Server
     * Journal Consumer Listener.
     * The given resource bundle name tells the Journal logger
     * where to find the Journal entry text.
     * The logger:
     * -- Ensures that there is only one ARTLogger object in the Integration Server
     * Journal Consumer Listener, and that the ARTLogger object closes when the adapter is disabled.
     * -- Displays the debug message or any further information.
     * For example, in the Tutorial2Connection, we have one log entry for initializeConnection().
     * The log entry indicates whether a connection is established.
     */
    @SuppressWarnings("squid:S1541")
    public void initialize() throws AdapterException
    {
        //Instantiates the logger.
        _logger = new ARTLogger(getAdapterMajorCode(),
                                getAdapterName(),
                                getAdapterResourceBundleName());
		String temp = System.getProperty("watt.MQSeries.ignore.System.Default.DeadLetterQueue", "true");
		_ignoreSystemDefaultDeadLetterQueue = (temp.equalsIgnoreCase("true") ? true : false);
		//Trax 1-SYJSX - allow user to specify what to do when disconnecting active transactions
		_actionBeforeDisconnect = System.getProperty("watt.WmMQAdapter.actionBeforeDisconnect", "COMMIT");
		if (   _actionBeforeDisconnect == null 
			|| !( 	_actionBeforeDisconnect.trim().equalsIgnoreCase("COMMIT") 
				 || _actionBeforeDisconnect.trim().equalsIgnoreCase("ROLLBACK")
				)
		   )
			_actionBeforeDisconnect = "COMMIT"; // Default the invalid action to COMMIT
		_actionBeforeDisconnect = _actionBeforeDisconnect.trim().toUpperCase();
	    //Trax 1-WVILA. Determine what should be included in dead letter message (None, DLH, MQMD, both)
		_deadLetterMessageHeaders = System.getProperty("watt.WmMQAdapter.deadLetterMessageHeaders", wmMQAdapterConstant.deadLetterMessageHeaderOptions[3]);
		if ((_deadLetterMessageHeaders == null) || (_deadLetterMessageHeaders.trim().equals("")))
			_deadLetterMessageHeaders = wmMQAdapterConstant.deadLetterMessageHeaderOptions[3];
		_deadLetterMessageHeaders = _deadLetterMessageHeaders.trim().toUpperCase();
		if(_deadLetterMessageHeaders.equalsIgnoreCase("BOTH")) {
			_deadLetterMessageHeaders = wmMQAdapterConstant.deadLetterMessageHeaderOptions[3]; 
		}
	    //Trax 1-WVILA. Determine if Payload data from the original message should be included in dead letter message
		temp = System.getProperty("watt.WmMQAdapter.includeDataInError3041", "false");
		_includeDataIn3041 = (temp.trim().equalsIgnoreCase("true"));
	    //Trax 1-WVILA. Load HashMap with log level override properties
		temp = System.getProperty("watt.WmMQAdapter.logLevelOverride", "");
		if (!temp.trim().equals(""))
		{
			StringTokenizer st = new StringTokenizer(temp, "=,\\");
			for ( ; st.countTokens() > 1 ; )
			{
				String adaptermsg = st.nextToken();
				String loglevel = st.nextToken();
				int overrideloglevel = Integer.parseInt(loglevel);
				if ((overrideloglevel > -1) && (overrideloglevel < 11))
					_logLevelOverrides.put(adaptermsg, loglevel);
			}
		}
		// wmio ssl
		if(wmMQAdapterConstant.IBMMQ_IN_ILIVE || wmMQAdapterConstant.IBMMQ_IN_ORIGIN){
			_SSLSupport = true;
			_SSLListing = "AllCipherSpec";
		}
		else {
	//	  Trax 1-QPSPQ - SSL Support
			temp = System.getProperty("watt.WmMQAdapter.SSL.Support", "false");
			_SSLSupport = (temp.equalsIgnoreCase("true") ? true : false);
	//	  Trax 1-QPSPQ - SSL Support
			
			temp = System.getProperty("watt.WmMQAdapter.Connection.CiphersList", "");
			if(temp != null && temp.trim().length() > 0) {
				_SSLListing = temp;
			}
		}
		//-----------------------
		
		//iTrac IMQ-923
		temp=System.getProperty("watt.WmMQAdapter.UUID.msgIdBasedUUID","true");
		_msgIDBasedUUID=(temp.equalsIgnoreCase("false") ? false:true); 
		
		// Trax 1-VF52N - disable OverrideConnection cache
		temp = System.getProperty("watt.WmMQAdapter.Cache.Overridden.Connections", "true");
		_CacheOverridenConnections = (temp.equalsIgnoreCase("false") ? false : true);
		//  Trax 1-VF52N - disable OverrideConnection cache

		//Modification for TRAX 1-1BVR53 $Starts
		// Suppress the display of MQJE Messages, based on watt property 
		String messagesToHide=System.getProperty("watt.WmMQAdapter.Log.MQJE.ExcludeList", "NONE");
		if (!messagesToHide.equalsIgnoreCase(_hideMQMessagesNONE)){
			if (messagesToHide.equalsIgnoreCase(_hideMQMessagesALL)){
				MQException.log=null;
			}else {
					StringTokenizer st = new StringTokenizer(messagesToHide, ",");
					while (st.hasMoreTokens())
					{
						String messageCode = null;
						try {
							messageCode = st.nextToken();
							MQException.logExclude(new Integer(messageCode.trim()));
						}catch(NumberFormatException n){
							log(_logger,ARTLogger.WARNING, 2057,  messageCode,"");
						}catch(Exception e){
							log(_logger,ARTLogger.WARNING, 2056, messageCode,
									e.getMessage());
						}
					}
			}
		}
		//Modification for TRAX 1-1BVR53 $Ends
		
		// iTrac 852 [ SetAll permission ]
		temp = System.getProperty("watt.WmMQAdapter.Message.AddDateTimeStamp", "true");
		_addDateTimeStamp = (temp.equalsIgnoreCase("false") ? false : true);
		// iTrac 852
		
		//iTrac IMQ-981
		String value = com.wm.util.Config.getProperty("true", "watt.WmMQAdapter.useInternalDecodeXML");
		useInternalDecodeXml = value.equalsIgnoreCase("false") ? false : true ;
		
		value = com.wm.util.Config.getProperty("false", "watt.WmMQAdapter.instrospectHeaderDataTypes");
		interospectHeaderDataTypes = value.equalsIgnoreCase("false") ? false : true ;
		
    }

    /*
     * Cleans up any adapter-specific resources during termination.
     * This method is invoked when the adapter is reloaded or disabled.
     * This method closes the ARTLogger object, which causes the dynamically inserted
     * Major Code to be removed from the Integration Server Journal Log Consumer.
     * If we do not close the ARTLogger object, the next time the adapter is reloaded or enabled,
     * another logger instance would be created (by the initialize() method) and inserted into the
     * Integration Server, causing an error due to a duplicated Major Code (facility code).
     */
    public void cleanup() {
        if (_logger != null)
            _logger.close();
    }

    /*
     * Fills the ResourceAdapterMetadataInfo object with the adapter-specific
     * data in the specified locale.
     */
    public void fillAdapterTypeInfo(AdapterTypeInfo info, Locale locale)
    {
        info.addConnectionFactory(wmMQConnectionFactory.class.getName());
		info.addConnectionFactory(wmMQTransactionalConnectionFactory.class.getName());
        info.addListenerType(com.wm.adapter.wmmqadapter.connection.wmMQListener.class.getName());
        info.addListenerType(com.wm.adapter.wmmqadapter.connection.wmMQMultiQueueListener.class.getName()); 
        info.addNotificationType(wmMQAsyncListenerNotification.class.getName());
		info.addNotificationType(wmMQSyncListenerNotification.class.getName());
    }


    /**
     * Helper method dumpValues
     * This method logs each field in the Values object
     *
     * @param in - a Values object
     *
     */
    public static void dumpValues(String methodname, Values in)
    {
        if (in == null)
            return;

        log(ARTLogger.INFO, 1003, "dumpValues", "Dumping parameters for " +  methodname);
        int i = 0;
        for (Enumeration keys = in.keys() ; keys.hasMoreElements() ; i++)
        {
            String onekey = (String)keys.nextElement();
            if (onekey.endsWith("List"))
                continue;
            Object o = in.get(onekey);
            if (o == null)
                log(ARTLogger.INFO, 1003, "key=" + onekey + " is null", "");
            else
            {
                if (o instanceof byte[])
                {
                    log(ARTLogger.INFO, 1003, onekey+ "=" + new String((byte[])o), "");
                    byte[] bytes = (byte[])o;
                    for (int z = 0 ; z < bytes.length ; z++)
                    {
                        int b = new Integer(bytes[z]).intValue();
                        if (b < 0)
                            b += 256;
                        log(ARTLogger.INFO, 1003, "dumpValues", "byte[" + z + "]=" +  Integer.toHexString(b));
                    }

                }
                else
                    log(ARTLogger.INFO, 1003, "key=" + onekey, ",value=" + o.toString());
            }
        }
        log(ARTLogger.INFO, 1003, "dumpvalues", i + " parameters found for " + methodname);
    }

    /**
     * Helper method dumpNotificationHeaders
     * This method logs each header field selected in notification from the Values object
     *
     * watt.WmMQAdapter.Notification.LogMessageHeaders should be set to true
     * 
     * @param in - a Values object
     *
     */
    public static void dumpNotificationHeaders(String prop1, String methodname, Values in, String uuid) {
        if (in == null)
            return;
    
        WmMQAdapterUtils.logInfoMsg(1005, "LogMessageHeaders for Notification is set to: " +  prop1.toString());

        int i = 0;      
        for (Enumeration keys = in.keys() ; keys.hasMoreElements() ; i++)
        {
            String onekey = (String)keys.nextElement();
            if (onekey.endsWith("List"))
                continue;
            Object o = in.get(onekey);
            if (o == null)
            	WmMQAdapterUtils.logInfoMsg(1005, "key=" + onekey + " is null");
            else
            	  if (o instanceof byte[])
                  {
            		  WmMQAdapterUtils.logInfoMsg(1005, onekey+ "=" + new String((byte[])o));
                  }
                  else
                	  WmMQAdapterUtils.logInfoMsg(1005, onekey+ "=" + o.toString());
        }
        WmMQAdapterUtils.logInfoMsg(1005, "LogMessageHeaders for Notification found "+ i + " parameters for " + methodname);
        WmMQAdapterUtils.logInfoMsg(1005, "runNotification publishing message with uuid= " + uuid);
    }
    
	public static wmMQConnectionFactory findConnectionFactory(String qmgrname)
	{
		if (_connectionFactories.containsKey(qmgrname))
			return (wmMQConnectionFactory)_connectionFactories.get(qmgrname);

		return null;
	}

	public static void cacheConnectionFactory(String qmgrname, wmMQConnectionFactory connFactory)
	{
		//System.out.println("caching connectionFactory " + connFactory.toString());
		_connectionFactories.put(qmgrname, connFactory);
	}
    
    /**
     * Trax 1-UUTYF - NullPointerException when Tracing.dsp is refreshed. This 
     * method must be overridden because the superclass' method does not 
     * recognize ".TRACING".
     * 
     * Gets the adapter's base online help url.
     *
     * @return the adapter base online help url.
     * @throws AdapterException if a problem was encountered getting the adapter help url.
     * /
    public String getAdapterOnlineHelp(Locale locale,String dspName) throws AdapterException {
        String help = null;
        ARTJLog.entered(getClass().getName() + ".getAdapterOnlineHelp()");
        if (_resourceBundleManager == null) {
            throw new AdapterException(ADKGLOBAL.ERROR_RESOURCE_BUNDLE_MGR, "114.524", null, null, null);
        }
        
        String helpUrl = null;
        try {
            String dspPageName = null;
            if (dspName != null) {
                if (dspName.equals(".TRACING"))
                    dspPageName = ".TRACING";
                else if (dspName.equals(ADKGLOBAL.RESOURCEBUNDLEKEY_LISTRESOURCES))
                    dspPageName = ADKGLOBAL.RESOURCEBUNDLEKEY_LISTRESOURCES;
                else if (dspName.equals(ADKGLOBAL.RESOURCEBUNDLEKEY_LISTCONNECTIONTYPES)) 
                    dspPageName = ADKGLOBAL.RESOURCEBUNDLEKEY_LISTCONNECTIONTYPES; 
                else if (dspName.equals(ADKGLOBAL.RESOURCEBUNDLEKEY_LISTPOLLINGNOTIFICATIONS))
                    dspPageName = ADKGLOBAL.RESOURCEBUNDLEKEY_LISTPOLLINGNOTIFICATIONS;
// saa question - is this used?  should this link be done in developer - it doesn't get it here does it?                    
                else if (dspName.equals(ADKGLOBAL.RESOURCEBUNDLEKEY_LISTPOLLINGNOTIFICATIONDETAILS))
                    dspPageName = ADKGLOBAL.RESOURCEBUNDLEKEY_LISTPOLLINGNOTIFICATIONDETAILS;
                else if (dspName.equals(ADKGLOBAL.RESOURCEBUNDLEKEY_LISTLISTENERS)) 
                    dspPageName = ADKGLOBAL.RESOURCEBUNDLEKEY_LISTLISTENERS; 
                else if (dspName.equals(ADKGLOBAL.RESOURCEBUNDLEKEY_LISTLISTENERTYPES)) 
                    dspPageName = ADKGLOBAL.RESOURCEBUNDLEKEY_LISTLISTENERTYPES; 
                else if (dspName.equals(ADKGLOBAL.RESOURCEBUNDLEKEY_LISTLISTENERNOTIFICATIONS)) 
                    dspPageName = ADKGLOBAL.RESOURCEBUNDLEKEY_LISTLISTENERNOTIFICATIONS; 

                else 
                    dspPageName = ADKGLOBAL.RESOURCEBUNDLEKEY_ABOUT;
                
                helpUrl = getAdapterName()+dspPageName+ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL;
            }
            help = _resourceBundleManager.getStringResource(helpUrl, locale);
            ARTJLog.returned(getClass().getName()+".getAdapterOnlineHelp()");
            return help;
        }
        catch (Exception e) {
            throw new AdapterException(ADKGLOBAL.ERROR_RESOURCE_BUNDLE_MGR, "114.525", null, null, e);
        }
    }
*/ 
    public static void log(int level, int minor, String arg0, String arg1)
    {
        ARTLogger logger = ( (wmMQAdapter) wmMQAdapter.getInstance()).getLogger(); //NOSONAR
        if (logger == null)
        {
            System.out.println("Logger is null");
            return;
        }
        log(logger,level,minor,arg0,arg1);
    }
    
    //This function is created as part of modification for TRAX 1-1BVR53
    //since the existing log function calls wmMQAdapter.getInstance()each time
    //and hence cannot be called from initialize()
    public static void log(ARTLogger logger,int level, int minor, String arg0, String arg1)
    {
        //Trax 1-WVILA. Allow user to override the logging level of adapter messages. 
        if (_logLevelOverrides.containsKey("" + minor))
        	level =  Integer.parseInt((String)_logLevelOverrides.get("" + minor)) - ARTLogger.DEBUG;       	
       
        String[] args = new String[2];
        args[0] = arg0;
        args[1] = arg1;
        //Trax log 1-S4OHA - move adapter logging to DEBUG PLUS
        logger.logDebugPlus(level, minor, args);
    }

	public static boolean isUseInternalDecodeXml() {
		return useInternalDecodeXml;
	}

	/**
	 * IMQ-1079
	 * @return the interospectHeaderDataTypes
	 */
	public static boolean isInterospectHeaderDataTypes() {
		return interospectHeaderDataTypes;
	}    
}
