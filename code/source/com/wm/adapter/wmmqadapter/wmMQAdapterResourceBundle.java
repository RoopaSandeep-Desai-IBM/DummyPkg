/**
 * wmMQAdapterResourceBundle.java
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

import java.util.ListResourceBundle;

import com.wm.adk.ADKGLOBAL;
import com.wm.pkg.art.AdapterRuntimeGlobals;

/*
 * A resource bundle class for wmMQAdapter. This class contains information that can
 * be internationalized, including:
 * -- The display name and descriptions of the properties of the adapter, connection, adapter
 * service templates, and adapter notification templates
 * -- Online help links
 * -- Log message strings
 * -- Error messages in exceptions
 * -- Parameter names
 * -- Group names
 */
public class wmMQAdapterResourceBundle extends ListResourceBundle 
{
    public Object[][]  getContents() { return contents; }

    //The adapter type name.
    private static final String ADAPTERINFO="wmMQAdapter";
	private static final String IS_PKG_NAME = "/WmMQAdapter/";

    //The connection type name (the class name of connection type).
    private static final String CONNECTIONTYPE1="com.wm.adapter.wmmqadapter.connection.wmMQConnectionFactory";
	private static final String CONNECTIONTYPE2="com.wm.adapter.wmmqadapter.connection.wmMQTransactionalConnectionFactory";

	//The Interaction type names (the class name of Interaction types).
	private static final String INTERACTIONTYPE1="com.wm.adapter.wmmqadapter.service.Put";
	private static final String INTERACTIONTYPE2="com.wm.adapter.wmmqadapter.service.Get";
	private static final String INTERACTIONTYPE3="com.wm.adapter.wmmqadapter.service.Peek";
	private static final String INTERACTIONTYPE4="com.wm.adapter.wmmqadapter.service.RequestReply";

	//private static final String NOTIFICATIONTYPE1="com.wm.adapter.wmmqadapter.notification.wmMQAsyncListenerNotification";
	//private static final String NOTIFICATIONTYPE2="com.wm.adapter.wmmqadapter.notification.wmMQSyncListenerNotification";

	private static final String INTERACTIONTYPE5="com.wm.adapter.wmmqadapter.notification.wmMQAsyncListenerNotification";
	private static final String INTERACTIONTYPE6="com.wm.adapter.wmmqadapter.notification.wmMQSyncListenerNotification";
	
	private static final String INTERACTIONTYPE7="com.wm.adapter.wmmqadapter.service.InquireQueueManager";
	private static final String INTERACTIONTYPE8="com.wm.adapter.wmmqadapter.service.PCFCommand";

    static final Object[][] contents  = {
     //The adapter type display name.
    {ADAPTERINFO + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "IBM webMethods Adapter for MQ"}
    // "WebSphere MQ Adapter"

     //The adapter type description.
    ,{ADAPTERINFO + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "IBM webMethods Adapter for MQ"}

     //The adapter type vendor.
    ,{ADAPTERINFO + ADKGLOBAL.RESOURCEBUNDLEKEY_VENDORNAME ,  "IBM"}

//	,{ADAPTERINFO + ADKGLOBAL.RESOURCEBUNDLEKEY_THIRDPARTYCOPYRIGHTURL,
//	  IS_PKG_NAME + "copyright.html"}
	// copyright page encoding

	,{ADAPTERINFO + ADKGLOBAL.RESOURCEBUNDLEKEY_COPYRIGHTENCODING,
	  "UTF-8"}
	// release note url

	,{ADAPTERINFO + ADKGLOBAL.RESOURCEBUNDLEKEY_RELEASENOTEURL,
	  IS_PKG_NAME + "ReleaseNotes.html"}
	//The online help link to the adapter's "about" information.
		

     //The connection type display name. Key syntax: "[Connection Type Name]" + RESOURCEBUNDLEKEY_DISPLAYNAME
    ,{CONNECTIONTYPE1 + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "IBM webMethods Adapter for MQ Connection"}
	,{CONNECTIONTYPE2 + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "IBM webMethods Adapter for MQ Transactional Connection"}

     //The connection type description. Key syntax: "[Connection Type Name]" + RESOURCEBUNDLEKEY_DESCRIPTION
    ,{CONNECTIONTYPE1 + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "Connection for IBM webMethods Adapter for MQ Adapter"}
	,{CONNECTIONTYPE2 + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "Transactional connection for IBM webMethods Adapter for MQ Adapter"}

	//The connection type display name. Key syntax: "[Connection Type Name]" + RESOURCEBUNDLEKEY_DISPLAYNAME
    ,{INTERACTIONTYPE1 + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Put Service"}
    ,{INTERACTIONTYPE2 + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Get Service"}
	,{INTERACTIONTYPE3 + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Peek Service"}
	,{INTERACTIONTYPE4 + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Request/Reply Service"}
//	,{NOTIFICATIONTYPE1 + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "WebSphere MQ Asynchronous Listener Notification"}
//	,{NOTIFICATIONTYPE2 + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "WebSphere MQ Synchronous Listener Notification"}
	,{INTERACTIONTYPE7 + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Inquire Queue Manager/Queue Service"}
	,{INTERACTIONTYPE8 + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "PCF Command Service"}
	
	//The connection type description. Key syntax: "[Connection Type Name]" + RESOURCEBUNDLEKEY_DESCRIPTION
    ,{INTERACTIONTYPE1 + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "Put message to queue"}
    ,{INTERACTIONTYPE2 + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "Get message from queue"}
	,{INTERACTIONTYPE3 + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "Peek at message on queue"}
	,{INTERACTIONTYPE4 + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "Put Request message and get Reply message"}
	,{INTERACTIONTYPE7 + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "Inquire QueueManager and Queue Properties"}
	,{INTERACTIONTYPE8 + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "Generic Service to execute PCF commands"}

   ,{"tracing" + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "MQSeries-Level Trace"}
	//Trax 1-UUTYF - Resolves NullPointerException in ArtAdmin.getOnlineHelp()
	//Remove comments when ready
   //,{"TRACING" + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "MQSeries-Level Trace"}

    //The display names for the templates. Key syntax : "[Adapter Type Name]" + RESOURCEBUNDLEKEY_DISPLAYNAME
    ,{wmMQAdapterConstant.SINGLE_QUEUE_LISTENER + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "IBM webMethods Adapter for MQ Single-Queue Listener Service" }
    ,{wmMQAdapterConstant.MULTI_QUEUE_LISTENER + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "IBM webMethods Adapter for MQ Multi-Queue Listener Service" }
    ,{wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "IBM webMethods Adapter for MQ Asynchronous Listener Notification" }
	,{wmMQAdapterConstant.SYNC_LISTENER_NOTIFICATION + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "IBM webMethods Adapter for MQ Synchronous Listener Notification" }

     //The descriptions for the templates. Key syntax : "[Adapter Type Name]" + RESOURCEBUNDLEKEY_DESCRIPTION
	,{wmMQAdapterConstant.SINGLE_QUEUE_LISTENER + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "Listen for Messages on a Single Queue" }
	,{wmMQAdapterConstant.MULTI_QUEUE_LISTENER + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "Listen for Messages on Multiple Queues" }
	,{wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "Asynchronous Listener Notification" }
	,{wmMQAdapterConstant.SYNC_LISTENER_NOTIFICATION + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "Synchronous Listener Notification" }

	,{wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
	  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_EditNotifs"}
	,{wmMQAdapterConstant.SYNC_LISTENER_NOTIFICATION + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
	"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_EditNotifs"}

     //The parameter display name. Key syntax :
     //"[Template Name]" + "[parameter name]" + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME
     //Omitting the [Template Name] will affect all templates containing that parameter.
    ,{wmMQAdapterConstant.QUEUE_MANAGER_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Queue Manager Name" }
    ,{wmMQAdapterConstant.CCDT_FILE_PATH + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "CCDT File Path (Absolute path)" }
    ,{wmMQAdapterConstant.MQSERIES_CONNECTION_FACTORY + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "wmMQConnectionFactory" }
    ,{wmMQAdapterConstant.QUEUE_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Queue Name(s)" }
    ,{wmMQAdapterConstant.DYNAMIC_QUEUE_PREFIX + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Dynamic Queue Prefix" }
    ,{wmMQAdapterConstant.HOST_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Host Name" }
    ,{wmMQAdapterConstant.PORT + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "TCP/IP Port" }
    ,{wmMQAdapterConstant.CHANNEL_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Server Connection Channel" }
    ,{wmMQAdapterConstant.CCSID + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "CCSID" }
    ,{wmMQAdapterConstant.ENCODING + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Encoding" }
    ,{wmMQAdapterConstant.SEND_EXIT_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Send Exit Name" }
    ,{wmMQAdapterConstant.SEND_EXIT_INIT + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Send Exit Init Parms" }
    ,{wmMQAdapterConstant.RECV_EXIT_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Receive Exit Name" }
    ,{wmMQAdapterConstant.RECV_EXIT_INIT + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Receive Exit Init Parms" }
    ,{wmMQAdapterConstant.SECURITY_EXIT_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Security Exit Name" }
    ,{wmMQAdapterConstant.SECURITY_EXIT_INIT + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Security Exit Init Parms" }
    ,{wmMQAdapterConstant.USERID + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "User Id" }
    ,{wmMQAdapterConstant.PASSWORD + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Password" }
    ,{wmMQAdapterConstant.RETRY_LIMIT + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Connection Retry Limit" }
    ,{wmMQAdapterConstant.RETRY_INTERVAL + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Connection Retry Interval" }

    ,{wmMQAdapterConstant.MQMD_FIELDS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Message Header" }
    ,{wmMQAdapterConstant.USE_MQMD_INPUT_FIELDS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Use for Input" }
    ,{wmMQAdapterConstant.MQMD_INPUT_FIELDS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Input Field Name" }
    ,{wmMQAdapterConstant.SELECTION_CRITERIA + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Selection Criteria" }
	,{wmMQAdapterConstant.FILTER_CRITERIA + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Filter Criteria" }
    ,{wmMQAdapterConstant.MQMD_INPUT_CONSTANTS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Constant Value" }
    ,{wmMQAdapterConstant.USE_MQMD_OUTPUT_FIELDS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Use for Output" }
    ,{wmMQAdapterConstant.MQMD_OUTPUT_FIELDS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Output Field Name" }
    ,{wmMQAdapterConstant.MQMD_OUTPUT_CONSTANTS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Constant Value" }
    ,{wmMQAdapterConstant.MQMD_INPUTS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Message Input Fields" }
    ,{wmMQAdapterConstant.MQMD_OUTPUTS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Message Output Fields" }
    ,{wmMQAdapterConstant.USE_JMS_PROPERTIES + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Use for Input" }
    ,{wmMQAdapterConstant.JMS_PROPERTIES + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "JMS Properties" }
    ,{wmMQAdapterConstant.USER_DEFINED_PROPERTIES + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "User-Defined Properties" }
    ,{wmMQAdapterConstant.WAIT_INTERVAL + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Wait Interval (msec)" }
	,{wmMQAdapterConstant.WAIT_INTERVAL_TYPE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "waitIntervalType" }
	,{wmMQAdapterConstant.WAIT_INTERVAL_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "waitIntervalName" }
    
    ,{wmMQAdapterConstant.SYNCPOINT_PROCESSING + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Perform task under Syncpoint?" }
    ,{wmMQAdapterConstant.BACKOUT_THRESHHOLD + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Deadletter Backout Threshold" }
    ,{wmMQAdapterConstant.HANDLE_BACKOUT_REQUEUE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Handle Backout ReQueue" }

    ,{wmMQAdapterConstant.SHARED_MODE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Open Queue in Shared Mode?" }
    ,{wmMQAdapterConstant.CONVERT_DATA_OPTION + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Convert Application Data?" }
    ,{wmMQAdapterConstant.USE_GROUPING + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Retrieve only when all messages available" }
//    ,{wmMQAdapterConstant.MQGMO_OPTIONS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Additional MQGMO options" }
    ,{wmMQAdapterConstant.THROW_EXCEPTION_ON_FAILURE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Throw Exception on Failure?" }
    ,{wmMQAdapterConstant.CHILD_LISTENER+ ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Child Listener? (Read only)" }
    ,{wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME+ ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Parent Listener's Name (Read only)" }
    ,{wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME+ ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Queue Name (Read only)" }
    ,{wmMQAdapterConstant.INHERIT_PARENT_PROPERTIES+ ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Inherit Parent Listener's Properties?" }
	,{wmMQAdapterConstant.USE_MQMD_REQUEST_FIELDS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Use for Request" }
	,{wmMQAdapterConstant.MQMD_REQUEST_FIELDS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Request Field Name" }
	,{wmMQAdapterConstant.MQMD_REQUEST_CONSTANTS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Constant Value" }
	,{wmMQAdapterConstant.MQMD_REQUEST_INPUTS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Request Input Fields" }
	,{wmMQAdapterConstant.USE_MQMD_REPLY_FIELDS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Use for Reply" }
	,{wmMQAdapterConstant.MQMD_REPLY_FIELDS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Reply Field Name" }
	,{wmMQAdapterConstant.MQMD_REPLY_OUTPUTS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Reply Output Fields" }

	,{wmMQAdapterConstant.REASON_CODE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "reasonCode" }
	,{wmMQAdapterConstant.REASON_CODE_TYPE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "reasonCodeType" }
	,{wmMQAdapterConstant.REASON_CODE_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "reasonCodeName" }
	,{wmMQAdapterConstant.CONDITION_CODE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "conditionCode" }
	,{wmMQAdapterConstant.CONDITION_CODE_TYPE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "conditionCodeType" }
	,{wmMQAdapterConstant.CONDITION_CODE_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "conditionCodeName" }
	,{wmMQAdapterConstant.RESET_CURSOR + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Reset Cursor before Operation?" }
	,{wmMQAdapterConstant.RESET_CURSOR_TYPE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "resetCursorType" }
	,{wmMQAdapterConstant.RESET_CURSOR_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "resetCursorName" }

	,{wmMQAdapterConstant.DEAD_LETTER_QUEUE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Dead Letter Queue Name" }
	,{wmMQAdapterConstant.DEAD_LETTER_QUEUE_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "deadLetterQueueName" }
	,{wmMQAdapterConstant.DEAD_LETTER_QUEUE_TYPE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "deadLetterQueueType" }
	,{wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Dead Letter Queue Manager Name" }
	,{wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "deadLetterQueueManagerName" }
	,{wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER_TYPE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "deadLetterQueueManagerType" }
	,{wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Headers to include in Dead Letter Message" }
	,{wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "deadLetterMessageHeadersName" }
	,{wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS_TYPE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "deadLetterMessageHeadersType" }
	,{wmMQAdapterConstant.MSG_PROPERTY_MSGBODY + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Message Body as Object"}
	,{wmMQAdapterConstant.MSG_PROPERTY_MSGBODYBYTEARRAY + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Message Body as Byte[]"}


	,{wmMQAdapterConstant.OVERRIDE_USE_LOCAL_QUEUE_MANAGER + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.useLocalQueueManager" }
	,{wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_MANAGER_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.queueManagerName" }
	,{wmMQAdapterConstant.OVERRIDE_CONNECTION_HOST_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.hostName" }
	,{wmMQAdapterConstant.OVERRIDE_CONNECTION_PORT + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.port" }
	,{wmMQAdapterConstant.OVERRIDE_CONNECTION_CHANNEL_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.channel" }
	,{wmMQAdapterConstant.OVERRIDE_CONNECTION_CCSID + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.CCSID" }
	,{wmMQAdapterConstant.OVERRIDE_CONNECTION_ENCODING + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.encoding" }
	,{wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.queuename" }
	,{wmMQAdapterConstant.OVERRIDE_CONNECTION_DYNAMIC_QUEUE_PREFIX + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.dynamicQueuePrefix" }
	,{wmMQAdapterConstant.OVERRIDE_CONNECTION_USERID + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.userid" }
	,{wmMQAdapterConstant.OVERRIDE_CONNECTION_PASSWORD + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.password" }


	,{wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_MANAGER_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.queueManagerName" }
	,{wmMQAdapterConstant.OVERRIDDEN_CONNECTION_HOST_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.hostName" }
	,{wmMQAdapterConstant.OVERRIDDEN_CONNECTION_PORT + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.port" }
	,{wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CHANNEL_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.channel" }
	,{wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CCSID + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.CCSID" }
	,{wmMQAdapterConstant.OVERRIDDEN_CONNECTION_ENCODING + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.encoding" }
	,{wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.queuename" }
	,{wmMQAdapterConstant.OVERRIDDEN_CONNECTION_DYNAMIC_QUEUE_PREFIX + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.dynamicQueuePrefix" }
	,{wmMQAdapterConstant.OVERRIDDEN_CONNECTION_USERID + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.userid" }
	,{wmMQAdapterConstant.OVERRIDDEN_CONNECTION_PASSWORD + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideConnection.password" }

	,{wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_QUEUE_MANAGER_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.queueManagerName" }
	,{wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_HOST_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.hostName" }
	,{wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_PORT + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.port" }
	,{wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_CHANNEL_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.channel" }
	,{wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_CCSID + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.CCSID" }
	,{wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_ENCODING + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.encoding" }
	,{wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_QUEUE_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.queuename" }
	,{wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_DYNAMIC_QUEUE_PREFIX + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.dynamicQueuePrefix" }
	,{wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_USERID + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.userid" }
	,{wmMQAdapterConstant.OVERRIDE_REPLY_TO_CONNECTION_PASSWORD + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.password" }


	,{wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_QUEUE_MANAGER_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.queueManagerName" }
	,{wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_HOST_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.hostName" }
	,{wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_PORT + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.port" }
	,{wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_CHANNEL_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.channel" }
	,{wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_CCSID + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.CCSID" }
	,{wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_ENCODING + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.encoding" }
	,{wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_QUEUE_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.queuename" }
	,{wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_DYNAMIC_QUEUE_PREFIX + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.dynamicQueuePrefix" }
	,{wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_USERID + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.userid" }
	,{wmMQAdapterConstant.OVERRIDDEN_REPLY_TO_CONNECTION_PASSWORD + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "overrideReplyToConnection.password" }
	
	,{wmMQAdapterConstant.QUEUE_MGR_PROPERTIES + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Queue Manager Properties" }
	,{wmMQAdapterConstant.QUEUE_MGR_PROPERTY_NAMES + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Queue Manager Property Names"}

	,{wmMQAdapterConstant.QUEUE_PROPERTIES + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Queue Properties" }
	,{wmMQAdapterConstant.QUEUE_PROPERTY_NAMES + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Queue Property Names"}
	
	,{wmMQAdapterConstant.PCF_COMMAND + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "PCF Command" }
	,{wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETERS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Optional Request Parameters" }
	,{wmMQAdapterConstant.RESPONSE_PARAMETERS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Response Parameters" }
	
//  Trax 1-QPSPQ - SSL Support
    ,{wmMQAdapterConstant.SSL_KEYSTORE + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "SSL Keystore file" }
    ,{wmMQAdapterConstant.SSL_KEYSTORE_PASSWORD + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "SSL Keystore Password" }
    
    ,{wmMQAdapterConstant.SSL_KEYSTORE_ALIAS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "SSL Keystore Alias" }
    ,{wmMQAdapterConstant.SSL_TRUSTSTORE_ALIAS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "SSL TrustStore Alias" }
    ,{wmMQAdapterConstant.SSL_OPTIONS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "SSL Option" }
	
	,{wmMQAdapterConstant.SSL_CIPHER_SPEC + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Cipher Spec" }
	
	,{wmMQAdapterConstant.SSL_KEYSTORE + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
		"Specify the fully-qualified file name of the SSL KeyStore"}
	,{wmMQAdapterConstant.SSL_KEYSTORE_PASSWORD + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
		"Specify the password associated with the SSL KeyStore"}
	
	,{wmMQAdapterConstant.SSL_KEYSTORE_ALIAS + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
		"Specify the SSL KeyStore alias"}
	,{wmMQAdapterConstant.SSL_TRUSTSTORE_ALIAS + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
		"Specify the SSL truststore alias"}
	
	
	,{wmMQAdapterConstant.SSL_CIPHER_SPEC + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
	"Select the Cipher Spec to use"}
//  Trax 1-QPSPQ - SSL Support

    ,{wmMQAdapterConstant.ACTION_BEFORE_DISCONNECT + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Action before Disconnecting an in-transaction Connection" }
    ,{wmMQAdapterConstant.CACHE_OVERRIDDEN_CONNECTIONS + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME, "Cache Overridden Connections" }
	
	,{wmMQAdapterConstant.ACTION_BEFORE_DISCONNECT + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
		"Action (COMMIT|ROLLBACK) that needs to be taken at the time of disconnect if the connection is in transaction"}
	,{wmMQAdapterConstant.CACHE_OVERRIDDEN_CONNECTIONS + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
		"Check this box to instruct the adapter to cache the overridden connections"}
	
   	,{wmMQAdapterConstant.OUTPUT_FIELD_NAMES + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME,
     "Field Names" }
   	,{wmMQAdapterConstant.INPUT_FIELD_NAMES + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME,
     "Field Names"}
   	,{wmMQAdapterConstant.JMS_PROPERTY_NAMES + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME,
     "Property Names"}
   	,{wmMQAdapterConstant.MSG_BODY_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME,
       "msgBody"}
   	,{wmMQAdapterConstant.MSG_BODY_BYTE_ARRAY_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME,
       "msgBodyByteArray"}
   	,{wmMQAdapterConstant.QUEUE_MANAGER_NAME_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME,
   	   "queueManagerName"}
   	,{wmMQAdapterConstant.QUEUE_NAME_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME,
	   "queueName"}

   	,{wmMQAdapterConstant.FILTER_MESSAGE_ID + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME,
		"Filter on Message ID"}
	,{wmMQAdapterConstant.FILTER_CORRELATION_ID + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME,
		"Filter on Correlation ID"}
	,{wmMQAdapterConstant.FILTER_GROUP_ID + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME,
		"Filter on Group ID"}
	,{wmMQAdapterConstant.FILTER_SEQUENCENUMBER + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME,
		"Filter on Sequence Number"}
	,{wmMQAdapterConstant.FILTER_OFFSET + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME,
		"Filter on Offset"}

	,{wmMQAdapterConstant.FILTER_MESSAGE_ID + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
		"Specify Message ID to filter messages by Message ID"}
	,{wmMQAdapterConstant.FILTER_CORRELATION_ID + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
		"Specify Correlation ID to filter messages by Correlation ID"}
	,{wmMQAdapterConstant.FILTER_GROUP_ID + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
		"Specify Group ID to filter messages by Group ID"}
	,{wmMQAdapterConstant.FILTER_SEQUENCENUMBER + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
		"Specify Sequence Number to filter messages by Sequence Number"}
	,{wmMQAdapterConstant.FILTER_OFFSET + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
		"Specify Offset to filter messages by Offset"}

     //The parameter description information. Key syntax :
     //"[Template Name]" + "[parameter name]" + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION
     //Omitting the [Template Name] will affect all templates containing that parameter.
    ,{wmMQAdapterConstant.INPUT_FIELD_NAMES + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
      "list of field names in the input document that contain the data for the MQMD header fields" }
    ,{wmMQAdapterConstant.JMS_PROPERTY_NAMES + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
        "list of field names in the input document that contain the JMS properties" }
    ,{wmMQAdapterConstant.OUTPUT_FIELD_NAMES + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
      "list of field names in the output document to store the data from the MQMD header fields" }
    ,{wmMQAdapterConstant.USE_COLUMN + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "Use column"}

    ,{wmMQAdapterConstant.WAIT_INTERVAL + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
    "Number of milliseconds to wait for a message to arrive on the queue." }
    ,{wmMQAdapterConstant.DEAD_LETTER_QUEUE + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION,
    "Name of Dead Letter Queue to which undeliverable messages will be moved." }
    ,{wmMQAdapterConstant.DEAD_LETTER_QUEUE_MANAGER + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, 
	"Name of the Queue Manager on which the Dead Letter Queue is defined." }
    ,{wmMQAdapterConstant.DEAD_LETTER_MESSAGE_HEADERS + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, 
	"Specifies which headers (NONE|DLH|MQMD|DLH & MQMD) to include in the message sent to the dead letter queue." }
    ,{wmMQAdapterConstant.BACKOUT_THRESHHOLD + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, 
	"The maximum number of times a message may be backed-out before it is moved to the Dead Letter Queue." }
    ,{wmMQAdapterConstant.SHARED_MODE + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, 
	"Check this box to open the Queue in Shared Mode." }
    ,{wmMQAdapterConstant.CONVERT_DATA_OPTION + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, 
	"Check this box to request the application data to be converted." }
    ,{wmMQAdapterConstant.THROW_EXCEPTION_ON_FAILURE + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, 
	"Check this box to instruct the adapter to throw an Exception if this service fails." }
    ,{wmMQAdapterConstant.INHERIT_PARENT_PROPERTIES + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, 
	"Check this box to instruct the listener to inherit the listener properties from its parent. Updatable only on the child listener." }
    ,{wmMQAdapterConstant.CHILD_LISTENER + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, 
	"Check this box to specify that this is a child listener. Read only property." }
    ,{wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, 
	"Node name of the parent listener. Read only property." }
    ,{wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, 
	"Name of the queue that this child listener listens to. Read only property." }

	,{wmMQAdapterConstant.QUEUE_MGR_PROPERTY_NAMES + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "list of Q Mgr properties" }
    ,{wmMQAdapterConstant.QUEUE_PROPERTY_NAMES + ADKGLOBAL.RESOURCEBUNDLEKEY_DESCRIPTION, "list of Q properties" }
    
	//Help URLS
//		  ,{ADAPTERINFO + AdapterRuntimeGlobals.LISTLISTENERTYPES + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL,
//	  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ListTypes"}
//The current AdapterRuntimeGlobals does not contain LISTLISTENERTYPES
	,{ADAPTERINFO + ".LISTLISTENERTYPES" + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL,
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ListTypes"}
		
 	,{ADAPTERINFO + ADKGLOBAL.RESOURCEBUNDLEKEY_LISTRESOURCES + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL,
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_Conn"}

	,{ADAPTERINFO + ADKGLOBAL.RESOURCEBUNDLEKEY_LISTCONNECTIONTYPES + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL,
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ConnTypes"}

	,{ADAPTERINFO + ADKGLOBAL.RESOURCEBUNDLEKEY_ABOUT + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL,
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_About"}

	 ,{ADAPTERINFO +AdapterRuntimeGlobals.LISTLISTENERS + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL,
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_List"}

	,{ADAPTERINFO +AdapterRuntimeGlobals.LISTLISTENERNOTIFICATIONS + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL,
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ListNotifs"}

	,{ADAPTERINFO + ".tracing" + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL,
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_Trace"}

	//Trax 1-UUTYF - Resolves NullPointerException in ArtAdmin.getOnlineHelp()
	//Remove comments when ready
	//,{ADAPTERINFO + ".TRACING" + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL,
	//"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_Trace"}

//	,{CONNECTIONTYPE1 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
//		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ConfConnTypes"}

//	,{CONNECTIONTYPE2 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
//		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ConfConnTypes"}
		
	,{wmMQAdapterConstant.SINGLE_QUEUE_LISTENER + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ConfListType"}
		
	,{wmMQAdapterConstant.MULTI_QUEUE_LISTENER + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ConfListType"}
	
//	,{NOTIFICATIONTYPE1 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
//		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ListNotifs"}
		
//	,{NOTIFICATIONTYPE2 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
//		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ListNotifs"}

	,{INTERACTIONTYPE5 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ListNotifs"}
		
	,{INTERACTIONTYPE6 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ListNotifs"}

    //The group display names. Key syntax: "[Template Name]" + "[Group Name]" + RESOURCEBUNDLEKEY_GROUP
    ,{wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "MQMD Header" }
	,{wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
 	  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_PUT_MQMD"}
	,{wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "JMS Properties"}
	,{wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
	  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_PUT_JMS"}
    ,{wmMQAdapterConstant.PUT_SERVICE + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "Put Service" }
	,{wmMQAdapterConstant.PUT_SERVICE + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
	  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_PUT_PUT"}
    ,{wmMQAdapterConstant.GET_SERVICE + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "Get Service" }
	,{wmMQAdapterConstant.GET_SERVICE + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
	  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_GET_GET"}
    ,{wmMQAdapterConstant.PEEK_SERVICE + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "Peek Service" }
	,{wmMQAdapterConstant.PEEK_SERVICE + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
	  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_PEEK_PEEK"}
    ,{wmMQAdapterConstant.REQUEST_REPLY_SERVICE + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "Request/Reply Service" }
	,{wmMQAdapterConstant.REQUEST_REPLY_SERVICE + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
	  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_RR_RR"}

	,{wmMQAdapterConstant.INQUIRE_QUEUE_MANAGER_SERVICE + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "Inquire Queue Manager Service" }
	,{wmMQAdapterConstant.INQUIRE_QUEUE_MANAGER_SERVICE + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
	  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_Inquire_Inquire"}
	,{wmMQAdapterConstant.PCF_COMMAND_SERVICE + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "PCF Command Service" }
	,{wmMQAdapterConstant.PCF_COMMAND_SERVICE + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
	  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_PCF_PCF"}
	
	  ,{wmMQAdapterConstant.PUT_SERVICE + wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "MQMD Header" }
	  ,{wmMQAdapterConstant.PUT_SERVICE + wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_PUT_MQMD"}
	  ,{wmMQAdapterConstant.GET_SERVICE + wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "MQMD Header" }
	  ,{wmMQAdapterConstant.GET_SERVICE + wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_GET_MQMD"}
	  ,{wmMQAdapterConstant.PEEK_SERVICE + wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "MQMD Header" }
	  ,{wmMQAdapterConstant.PEEK_SERVICE + wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_PEEK_MQMD"}
	  ,{wmMQAdapterConstant.REQUEST_REPLY_SERVICE + wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "MQMD Header" }
	  ,{wmMQAdapterConstant.REQUEST_REPLY_SERVICE + wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_RR_MQMD"}
	  ,{wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "MQMD Header" }
	  ,{wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
	    "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ASYNC_MQMD"}
	  ,{wmMQAdapterConstant.SYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "MQMD Header" }
	  ,{wmMQAdapterConstant.SYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.MQMD + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_SYNC_MQMD"}

	  ,{wmMQAdapterConstant.INQUIRE_QUEUE_MANAGER_SERVICE + wmMQAdapterConstant.QUEUE_MANAGER_PROPERTIES_TAB + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "Queue Manager Properties"}
	  ,{wmMQAdapterConstant.INQUIRE_QUEUE_MANAGER_SERVICE + wmMQAdapterConstant.QUEUE_MANAGER_PROPERTIES_TAB + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_Inquire_QM"}
	  ,{wmMQAdapterConstant.INQUIRE_QUEUE_MANAGER_SERVICE + wmMQAdapterConstant.QUEUE_PROPERTIES_TAB + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "Queue Properties"}
	  ,{wmMQAdapterConstant.INQUIRE_QUEUE_MANAGER_SERVICE + wmMQAdapterConstant.QUEUE_PROPERTIES_TAB + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_Inquire_Q"}
	  
	  ,{wmMQAdapterConstant.PCF_COMMAND_SERVICE + wmMQAdapterConstant.REQUEST_PARAMETERS_TAB + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "Optional Request Parameters"}
	  ,{wmMQAdapterConstant.PCF_COMMAND_SERVICE + wmMQAdapterConstant.REQUEST_PARAMETERS_TAB + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
	    "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_PCF_OPT"}
	  ,{wmMQAdapterConstant.PCF_COMMAND_SERVICE + wmMQAdapterConstant.RESPONSE_PARAMETERS_TAB + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "Response Parameters"}
	  ,{wmMQAdapterConstant.PCF_COMMAND_SERVICE + wmMQAdapterConstant.RESPONSE_PARAMETERS_TAB + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
	    "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_PCF_RESP"}
	  
	  ,{wmMQAdapterConstant.PUT_SERVICE + wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "JMS Properties"}
	  ,{wmMQAdapterConstant.PUT_SERVICE + wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
	  	"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_PUT_JMS"}
	  ,{wmMQAdapterConstant.GET_SERVICE + wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "JMS Properties"}
	  ,{wmMQAdapterConstant.GET_SERVICE + wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_GET_JMS"}
	  ,{wmMQAdapterConstant.PEEK_SERVICE + wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "JMS Properties"}
	  ,{wmMQAdapterConstant.PEEK_SERVICE + wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_PEEK_JMS"}
	  ,{wmMQAdapterConstant.REQUEST_REPLY_SERVICE + wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "JMS Properties"}
	  ,{wmMQAdapterConstant.REQUEST_REPLY_SERVICE + wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_RR_JMS"}
	  ,{wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "JMS Properties"}
	  ,{wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ASYNC_JMS"}
	  ,{wmMQAdapterConstant.SYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "JMS Properties"}
	  ,{wmMQAdapterConstant.SYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.JMS + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUPURL, 
		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_SYNC_JMS"}
	  ,{wmMQAdapterConstant.ASYNC_LISTENER_NOTIFICATION + wmMQAdapterConstant.MSG_PROPERTY + ADKGLOBAL.RESOURCEBUNDLEKEY_GROUP, "Msg Properties"}
	
//	  ,{INTERACTIONTYPE1 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
//	    "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ConfListType"}
//	  ,{INTERACTIONTYPE2 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
//		  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ConfListType"}
//	  ,{INTERACTIONTYPE3 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
//		  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ConfListType"}
//	  ,{INTERACTIONTYPE4 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
//		  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ConfListType"}
		
	  ,{CONNECTIONTYPE1 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
			"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ConfConnType"}
	  ,{CONNECTIONTYPE2 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
			"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ConfConnType"}
//	  ,{INTERACTIONTYPE5 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
//			  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_EditNotifs"}
//	  ,{INTERACTIONTYPE6 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
//			  "/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_EditNotifs"}
//	,{LISTENERTYPE1 + ADKGLOBAL.RESOURCEBUNDLEKEY_HELPURL, 
//		"/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ConfListType"}
				
		
		
    //The error messages.
    ,{"2055", "Exception caught: {0}"}
	//TRAX 1-1BVR53 $Start
	,{"2056", "Exception caught while supressing mq messages. Message Code : {0}, Exception : {1}"}
	,{"2057", "The message code to be excluded is not a valid integer. Message Code : {0}"}
	//TRAX 1-1BVR53 $End
    ,{"2062", "PCFQuery could not connect to the Queue Manager {0} - reasonCode={1}"}
    ,{"2063", "PCFQuery command sent to {0} failed, conditionCode= {1}, reasonCode={2}"}

    ,{"1001", "{0}() entered"}          //LOG_ENTERED
    ,{"1002", "{0}() exit"}             //LOG_EXIT
    ,{"1003", "{0} {1}"}                //LOG_MSG
    ,{"1004", "Matching on {0}={1}"}
    ,{"1005", "{0}"}					//INFO_LOG_MSG
    ,{"1020", "Get completed with conditionCode={0}, reasonCode={1}"}
    ,{"1030", "Put completed with conditionCode={0}, reasonCode={1}"}
    ,{"1040", "Peek completed with conditionCode={0}, reasonCode={1}"}
	,{"1041", "Peek cannot operate with a transactional connection"}
    ,{"1050", "Request/Reply completed with conditionCode={0}, reasonCode={1}"}
    ,{"1051", "Request/Reply requires a valid, non-null value in the {0} property of the msgHeader"}
	,{"1052", "Request/Reply requires a non-transactional connection"}
    ,{"1055", "Exception caught {0} : {1}"}  //Log Exception
    ,{"1056", "Input path should be relative to Integrationserver path"}
    ,{"1099", "Log Level Override pair \'{0}={1}\' is invalid, ignored"}

	,{"3008", "Connection could not be initialized; condition code is \"{0}\", reason code is \"{1}\"."}
	,{"3020", "MsgBody must be a byte[], a String, or a Serializable Object. Found {0}"}
	,{"3032", "Queue Manager {0} has disconnected, waiting {1} milliseconds before reconnecting"}
	,{"3033", "Connection to Queue Manager {0} could not be initialized; {1}."}
	,{"3034", "Queue {0} could not be opened for {1}; {2}."}
    ,{"3035", "Unable to put message to queue {0} on {1}; condition code is \"{2}\", reason code is \"{3}\"."}
    ,{"3036", "Unable to get message from queue {0} on {1}; condition code is \"{2}\", reason code is \"{3}\"."}
    ,{"3037", "Unable to peek message on queue {0} on {1}; condition code is \"{2}\", reason code is \"{3}\"."}
    ,{"3038", "Unable to commit message(s) on queue {0} on {1}; condition code is \"{2}\", reason code is \"{3}\"."}
    ,{"3039", "Unable to rollback message(s) on queue {0} on {1}; condition code is \"{2}\", reason code is \"{3}\"."}
	,{"3040", "Service {0} moved a message to the DeadLetterQueue {1}."}
	,{"3041", "Failure attempting to move message to the DeadLetterQueue {0}. Error: {1}"}
    ,{"3044", "A warning was received while putting a message to queue {0} : {1}"}
    ,{"3045", "Unable to inquire the queue manager {0};  condition code is \"{1}\", reason code is \"{2}\"."}
    ,{"3046", "Unable to inquire the queue {0} on {1};  condition code is \"{2}\", reason code is \"{3}\"."}
    ,{"3060", "Multi-Queue Listener {0} shutting down as none of its child listeners are running"}
	,{"3099", "The message received by Listener {0} was not processed by any Notification"}
	,{"4011", "Exception caught while loading exit {1}. ({0}){1}"}  //Log Exception

    //Trax 1-UEK9U, 1-UE4JO and Feature Request 1-UE4TG.
	,{"4051", "Listener {0} waiting for at least 1 enabled notification."}  

//  Trax 1-QPSPQ - SSL Support
	,{"4061", "Connection to {0} failed. SSL is not supported in Bindings Mode"}  
//  Trax 1-QPSPQ - SSL Support
	
	// Trax 1-1031MB. 
	,{"4071", "Non-Fatal error occurred in the listener {0} while reading from the queue {1} on {2}; conditionCode={3}, reasonCode={4}"}  
	// Trax 1-1031MB. 

    // Trax 1-153LVB $ Start
	,{"4081", "The RFH header in the message is not version 2. Only RFH2 Header is supported."}
	// Trax 1-153LVB $ End

	,{"5001", "PCFMessageAgent could not connect to QueueManager {0}; conditionCode={1}, reasonCode={2}; {3}"}  
	,{"5002", "Unable to execute {0}; conditionCode={1}, reasonCode={2}; {3}"}  
	,{"5003", "Error while reading the parameter {0} from the response. Completion Code : {1}; Reason Code : {2}; {3}"}  
	,{"5004", "Error while parsing the PCFCommands metadata file {0}. {1}"}
	
	,{"5011", "Keystore alias or truststore alias is not set in the connection."}
	,{"5012", "Keystore alias or truststore alias is not created in IS"}
	,{"5013", "Unable to get a keystore instance"}
	,{"5014", "Invalid certificates"}
	,{"5015", "Can not add Keystore alias and truststore alias along with keystore path and password. Either add truststore and keystore alias or add the keystore path and password. "}
    };
}










