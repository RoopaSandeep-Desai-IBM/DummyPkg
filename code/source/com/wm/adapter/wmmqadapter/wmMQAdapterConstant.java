/*
 * wmMQAdapterConstant.java
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

import com.wm.app.b2b.server.Server;
import com.wm.util.Config;

/*
 * A place holder for the String constants using in Tutorial2Adapter.
 */
public class wmMQAdapterConstant
{

    //Constructor - will never be used
    private wmMQAdapterConstant()
    {
    }

    //The Major Code. Each adapter has unique ID in the journal logging and exceptions.
    public static final int ADAPTER_MAJOR_CODE = 600;

    //The parameter, resource domain - wmMQConnectionFactory.
    public static final String MQSERIES_CONNECTION_FACTORY = "wmMQConnectionFactory";

    public static final boolean IBMMQ_IN_ILIVE = Server.inILive();
    public static final boolean IBMMQ_IN_ORIGIN = Config.getBooleanProperty(false, "watt.server.inOrigin");
 
    //The parameter, resource domain - queueManagerName.
    public static final String QUEUE_MANAGER_NAME = "queueManagerName";
    
    public static final String CCDT_FILE_PATH = "ccdtFilePath";
    
    // Name of queueManagerName name input field
    public static final String QUEUE_MANAGER_NAME_NAME = "queueManagerNameName";

    // Name of queueManagerName type input field
    public static final String QUEUE_MANAGER_NAME_TYPE = "queueManagerNameType";

    //The parameter, resource domain - queueName.
    public static final String QUEUE_NAME = "queueName";
    
    // Name of queueName name input field
    public static final String QUEUE_NAME_NAME = "queueNameName";

    // Name of queueName type input field
    public static final String QUEUE_NAME_TYPE = "queueNameType";

    //The parameter, resource domain - queueName.
    public static final String DYNAMIC_QUEUE_PREFIX = "dynamicQueuePrefix";

    //The parameter, resource domain - queueNames. - used by domainLookup
    public static final String QUEUE_NAMES = "queueNames";

    //The parameter, resource domain - hostName.
    public static final String HOST_NAME = "hostName";

    //The parameter, resource domain - port.
    public static final String PORT = "port";

    //The parameter, resource domain - channel.
    public static final String CHANNEL_NAME = "channel";

    //The parameter, resource domain - CCSID.
    public static final String CCSID = "CCSID";

    //The parameter, resource domain - Encoding.
    public static final String ENCODING = "encoding";

    //The parameter, resource domain - sendExit.
    public static final String SEND_EXIT_NAME = "sendExit";

    //The parameter, resource domain - sendExitInit.
    public static final String SEND_EXIT_INIT = "sendExitInit";

    //The parameter, resource domain - recvExit.
    public static final String RECV_EXIT_NAME = "recvExit";

    //The parameter, resource domain - recvExitInit.
    public static final String RECV_EXIT_INIT = "recvExitInit";

    //The parameter, resource domain - securityExit.
    public static final String SECURITY_EXIT_NAME = "securityExit";

    //The parameter, resource domain - securityExitInit.
    public static final String SECURITY_EXIT_INIT = "securityExitInit";

    //The parameter, resource domain - userId.
    public static final String USERID = "userId";

    //The parameter, resource domain - password.
    public static final String PASSWORD = "password";

    //The parameter, resource domain - QueueManagerSettings.
    public static final String QUEUE_MANAGER_SETTINGS = "QueueManagerSettings";

    //The parameter, resource domain - QueueSettings.
	public static final String QUEUE_SETTINGS = "QueueSettings";

//  Trax 1-QPSPQ - SSL Support
    //The parameter, resource domain - sslSettings.
	public static final String SSL = "sslSettings";

    //The parameter, resource domain - sslKeyStore.
    public static final String SSL_KEYSTORE = "sslKeyStore";
    public static final String SSL_OPTIONS = "sslOptions";

    //The parameter, resource domain - sslKeyStore.
   public static final String SSL_KEYSTORE_PASSWORD = "sslKeyStorePassword";
	
    //The parameter, resource domain - sslKeyStoreAlias.	
	public static final String SSL_KEYSTORE_ALIAS = "sslKeyStoreAlias";
	
    //The parameter, resource domain - sslTrustStoreAlias.	
	public static final String SSL_TRUSTSTORE_ALIAS = "sslTrustStoreAlias";


    //The parameter, resource domain - sslKeyStore.
    public static final String SSL_CIPHER_SPEC = "sslCipherSpec";
//  Trax 1-QPSPQ - SSL Support

    //The parameter, resource domain - connectionManagementSettings.
    public static final String CONNECTION_MANAGEMENT_SETTINGS = "connectionManagementSettings";

    //The parameter, resource domain - actionBeforeDisconnect.
    public static final String ACTION_BEFORE_DISCONNECT = "actionBeforeDisconnect";
    
    //The parameter, resource domain - cacheOverriddenConnections.
    public static final String CACHE_OVERRIDDEN_CONNECTIONS = "cacheOverriddenConnections";
    
	//The parameter, resource domain - QueueSettings.
    public static final String QUEUE = "queue";

    //The parameter, resource domain - QueueSettings.
    public static final String SERVICE_SETTINGS = "serviceSettings";

    //The parameter, resource domain - retryLimit.
    public static final String RETRY_LIMIT = "retryLimit";

    //The parameter, resource domain - retryInterval.
    public static final String RETRY_INTERVAL = "retryInterval";

    //The parameter, resource domain - columnNames.
    public static final String COLUMN_NAMES = "columnNames";

    //The parameter - useColumn.
    public static final String USE_COLUMN = "useColumn";

    //The parameter - fieldNames.
    public static final String FIELD_NAMES = "fieldNames";

    //The parameter - inputFieldNames.
    public static final String INPUT_FIELD_NAMES = "inputFieldNames";

    //The parameter - inputFieldTypes.
    public static final String INPUT_FIELD_TYPES = "inputFieldTypes";

    //The parameter - outputFieldNames.
    public static final String OUTPUT_FIELD_NAMES = "outputFieldNames";

    //The parameter - outputFieldTypes.
    public static final String OUTPUT_FIELD_TYPES = "outputFieldTypes";

	//The parameter - requestFieldNames.
	public static final String REQUEST_FIELD_NAMES = "requestFieldNames";

	//The parameter - requestFieldTypes.
	public static final String REQUEST_FIELD_TYPES = "requestFieldTypes";

	//The parameter - replyFieldNames.
	public static final String REPLY_FIELD_NAMES = "replyFieldNames";

	//The parameter - replyFieldTypes.
	public static final String REPLY_FIELD_TYPES = "replyFieldTypes";

	//The resource domain - reasonCode.
	public static final String REASON_CODE = "reasonCode";

	//The resource domain - reasonCode.
	public static final String REASON_CODE_TYPE = "reasonCodeType";

	//The resource domain - reasonCode.
	public static final String REASON_CODE_NAME = "reasonCodeName";

	//The resource domain - conditionCode.
	public static final String CONDITION_CODE = "conditionCode";

	//The resource domain - conditionCode.
	public static final String CONDITION_CODE_TYPE = "conditionCodeType";

	//The resource domain - conditionCode.
	public static final String CONDITION_CODE_NAME = "conditionCodeName";

	//The resource domain - reasonCode.
	public static final String ERROR_MSG = "errorMsg";

	//The resource domain - reasonCode.
	public static final String ERROR_MSG_TYPE = "errorMsgType";

	//The resource domain - reasonCode.
	public static final String ERROR_MSG_NAME = "errorMsgName";

    public static final String INQUIRE_REMOTE_QUEUE = "remoteQueue";
	
	public static final String INQUIRE_REMOTE_QUEUE_NAME = "remoteQueueName";
	
	public static final String INQUIRE_REMOTE_TYPE = "remoteQueueType";
	
    //The resource domain - putService.
    public static final String PUT_SERVICE = "putService";

    //The resource domain - putService.
    public static final String GET_SERVICE = "getService";

    //The resource domain - putService.
    public static final String PEEK_SERVICE = "peekService";

    //The resource domain - putService.
    public static final String LISTEN_SERVICE = "listenService";
    
    //The resource domain - putService.
    public static final String REQUEST_REPLY_SERVICE = "requestReplyService";

    //The resource domain - inquireQueueManagerService.
    public static final String INQUIRE_QUEUE_MANAGER_SERVICE = "inquireQueueManagerService";
    
    //The resource domain - PCFService.
    public static final String PCF_COMMAND_SERVICE = "PCFCommandService";
    
    //The resource domain - waitInterval.
    public static final String WAIT_INTERVAL = "waitInterval";
    
    //The resource domain - waitIntervalName.
    public static final String WAIT_INTERVAL_NAME = "waitIntervalName";
    
    //The resource domain - waitIntervalType.
    public static final String WAIT_INTERVAL_TYPE = "waitIntervalType";

	//The resource domain - syncpointProcessing.
	public static final String SYNCPOINT_PROCESSING = "syncpointProcessing";

    //The resource domain - syncpointProcessing.
    public static final String THROW_EXCEPTION_ON_FAILURE = "throwExceptionOnFailure";

    //The resource domain - deadLetterQueueName.
    public static final String DEAD_LETTER_QUEUE = "deadLetterQueue";

    //The resource domain - deadLetterQueueName.
    public static final String DEAD_LETTER_QUEUE_NAME = "deadLetterQueueName";

    //The resource domain - deadLetterQueueType.
    public static final String DEAD_LETTER_QUEUE_TYPE = "deadLetterQueueType";

    //The resource domain - deadLetterQueueManager.
    public static final String DEAD_LETTER_QUEUE_MANAGER = "deadLetterQueueManager";

    //The resource domain - deadLetterQueueManagerName.
    public static final String DEAD_LETTER_QUEUE_MANAGER_NAME = "deadLetterQueueManagerName";
    
    //The resource domain - deadLetterQueueManagerType.
    public static final String DEAD_LETTER_QUEUE_MANAGER_TYPE = "deadLetterQueueManagerType";

    //The resource domain - deadLetterMessageHeaders.
    public static final String DEAD_LETTER_MESSAGE_HEADERS = "deadLetterMessageHeaders";
    
    //The resource domain - deadLetterMessageHeadersName.
    public static final String DEAD_LETTER_MESSAGE_HEADERS_NAME = "deadLetterMessageHeadersName";
    
    //The resource domain - deadLetterMessageHeadersType.
    public static final String DEAD_LETTER_MESSAGE_HEADERS_TYPE = "deadLetterMessageHeadersType";
    
    //The resource domain - backoutThreshhold.
    public static final String BACKOUT_THRESHHOLD = "backoutThreshhold";
    
    //The resource domain - handlebackoutRequeue.
    public static final String HANDLE_BACKOUT_REQUEUE = "handleBackoutRequeue";

    //The resource domain - sharedMode.
    public static final String SHARED_MODE = "sharedMode";

    //The resource domain - convertDataOption.
    public static final String CONVERT_DATA_OPTION = "convertDataOption";
    
    //The resource domain- useGrouping.
    public static final String USE_GROUPING="useGrouping";
    
    // The resource domain- mQGMOOptions
    public static final String MQGMO_OPTIONS="mQGMOOptions";

    //The resource domain - childListener.
    public static final String CHILD_LISTENER = "childListener";
    
    //The resource domain - parentNodeName.
    public static final String PARENT_LISTENER_NODE_NAME = "parentListenerNodeName";
    
    //The resource domain - inheritParentProperties.
    public static final String INHERIT_PARENT_PROPERTIES = "inheritParentProperties";
    
    //The resource domain - childListenerQueueName.
    public static final String CHILD_LISTENER_QUEUE_NAME = "childListenerQueueName";
        
	//The resource domain - syncpointProcessing.
	public static final String RESET_CURSOR = "resetCursor";

	//The resource domain - syncpointProcessing.
	public static final String RESET_CURSOR_TYPE = "resetCursorType";

	//The resource domain - syncpointProcessing.
	public static final String RESET_CURSOR_NAME= "resetCursorName";

    // Name of message body input field
    public static final String MSG_BODY = "msgBody";

    // Name of message body input field
    public static final String MSG_BODY_NAME = "msgBodyName";

    // Name of message body input field
    public static final String MSG_BODY_TYPE = "msgBodyType";

    // Name of message body input field
    public static final String MSG_BODY_BYTE_ARRAY = "msgBodyByteArray";

    // Name of message body input field
    public static final String MSG_BODY_BYTE_ARRAY_NAME = "msgBodyByteArrayName";

    // Name of message body input field
    public static final String MSG_BODY_BYTE_ARRAY_TYPE = "msgBodyByteArrayType";

	// Name of message body input field
	public static final String REPLY_MSG_BODY = "replyMsgBody";

	// Name of message body input field
	public static final String REPLY_MSG_BODY_NAME = "replyMsgBodyName";

	// Name of message body input field
	public static final String REPLY_MSG_BODY_TYPE = "replyMsgBodyType";

	// Name of message body input field
	public static final String REPLY_MSG_BODY_BYTE_ARRAY = "replyMsgBodyByteArray";

	// Name of message body input field
	public static final String REPLY_MSG_BODY_BYTE_ARRAY_NAME = "replyMsgBodyByteArrayName";

	// Name of message body input field
	public static final String REPLY_MSG_BODY_BYTE_ARRAY_TYPE = "replyMsgBodyByteArrayType";

	// Selection Criteria
	public static final String SELECTION_CRITERIA = "selectionCriteria";

	// Selection Criteria
	public static final String FILTER_CRITERIA = "filterCriteria";

    // Parameters
    public static final String MQMD = "mqmd";

    public static final String MQMD_FIELDS = "mqmdFields";

    public static final String MQMD_INPUTS = "mqmdInputs";

    public static final String MQMD_INPUT_FIELDS = "mqmdInputFields";

    public static final String MQMD_INPUT_CONSTANTS = "mqmdInputConstants";

    public static final String MQMD_INPUT_FIELD_TYPES = "mqmdInputFieldTypes";

    public static final String USE_MQMD_INPUT_FIELDS = "useMQMDFields";

    public static final String MQMD_OUTPUTS = "mqmdOutputs";

    public static final String MQMD_OUTPUT_FIELDS = "mqmdOutputFields";

    public static final String MQMD_OUTPUT_CONSTANTS = "mqmdOutputConstants";

    public static final String USE_MQMD_OUTPUT_FIELDS = "useOutputMQMDFields";

    public static final String MQMD_OUTPUT_FIELD_TYPES = "mqmdOutputFieldTypes";

	public static final String MQMD_REQUEST_INPUTS = "mqmdRequestInputs";

	public static final String MQMD_REQUEST_FIELDS = "mqmdRequestFields";

	public static final String MQMD_REQUEST_CONSTANTS = "mqmdRequestConstants";

	public static final String MQMD_REQUEST_FIELD_TYPES = "mqmdRequestFieldTypes";

	public static final String USE_MQMD_REQUEST_FIELDS = "useMQMDRequestFields";

	public static final String MQMD_REPLY_OUTPUTS = "mqmdReplyOutputs";

	public static final String MQMD_REPLY_FIELDS = "mqmdReplyFields";

	public static final String MQMD_REPLY_FIELD_TYPES = "mqmdReplyFieldTypes";

	public static final String USE_MQMD_REPLY_FIELDS = "useMQMDReplyFields";

    public static final String MQMD_FIELD_TYPES = "mqmdFieldTypes";

    public static final String MQMD_HEADER = "msgHeader";

    public static final String MQMD_ACCOUNTING_TOKEN = "AccountingToken";

    public static final String MQMD_ACCOUNTING_TOKEN_BYTE_ARRAY = "AccountingTokenByteArray";

    public static final String MQMD_APPLICATION_ID_DATA = "ApplIdentityData";

    public static final String MQMD_APPLICATION_ORIGIN_DATA = "ApplOriginData";

    public static final String MQMD_BACKOUT_COUNT = "BackoutCount";

    public static final String MQMD_CHARACTER_SET = "CodedCharSetId";

    public static final String MQMD_CORRELATION_ID = "CorrelId";

    public static final String MQMD_CORRELATION_ID_BYTE_ARRAY = "CorrelationIdByteArray";

    public static final String MQMD_ENCODING = "Encoding";

    public static final String MQMD_EXPIRY = "Expiry";

    public static final String MQMD_FEEDBACK = "Feedback";

    public static final String MQMD_FORMAT = "Format";

    public static final String MQMD_GROUP_ID = "GroupId";

    public static final String MQMD_GROUP_ID_BYTE_ARRAY = "GroupIdByteArray";

    public static final String MQMD_MESSAGE_FLAGS = "MsgFlags";

    public static final String MQMD_MESSAGE_ID = "MsgId";

    public static final String MQMD_MESSAGE_ID_BYTE_ARRAY = "MsgIdByteArray";

    public static final String MQMD_MESSAGE_TYPE = "MsgType";

    public static final String MQMD_MESSAGE_SEQUENCE_NUMBER = "MsgSeqNumber";

    public static final String MQMD_OFFSET = "Offset";

    public static final String MQMD_ORIGINAL_LENGTH = "OriginalLength";

    public static final String MQMD_PERSISTENCE = "Persistence";

    public static final String MQMD_PRIORITY = "Priority";

    public static final String MQMD_PUT_APPLICATION_NAME = "PutApplName";

    public static final String MQMD_PUT_APPLICATION_TYPE = "PutApplType";

    public static final String MQMD_PUT_DATE = "PutDate";

    public static final String MQMD_PUT_TIME = "PutTime";

    public static final String MQMD_REPLYTO_QUEUE_MANAGER_NAME = "ReplyToQueueMgr";

    public static final String MQMD_REPLYTO_QUEUE_NAME = "ReplyToQ";

    public static final String MQMD_REPORT = "Report";

    public static final String MQMD_USER_ID = "UserIdentifier";


    public static final String MQMD_MESSAGE_FLAGS_NONE = "NONE";
    public static final String MQMD_MESSAGE_FLAGS_SEG_ALLOWED = "Segmentation allowed";
    public static final String MQMD_MESSAGE_FLAGS_SEGMENT = "Segment";
    public static final String MQMD_MESSAGE_FLAGS_LAST_SEG = "Last_Segment";
    public static final String MQMD_MESSAGE_FLAGS_MSG_IN_GROUP = "Message in Group";
    public static final String MQMD_MESSAGE_FLAGS_LAST_MSG_IN_GROUP = "Last Message in Group";
    public static final String MQMD_MESSAGE_TYPE_REQUEST = "Request";
    public static final String MQMD_MESSAGE_TYPE_REPLY = "Reply";
    public static final String MQMD_MESSAGE_TYPE_DATAGRAM = "Datagram";
    public static final String MQMD_MESSAGE_TYPE_REPORT = "Report";
    public static final String MQMD_MESSAGE_TYPE_APPL_FIRST = "Appl First";
    public static final String MQMD_MESSAGE_TYPE_APPL_LAST = "Appl Last";
    public static final String MQMD_MESSAGE_TYPE_SYSTEM_LAST = "System Last";

    public static final String MQMD_PERSISTENCE_NOT_PERSISTENT = "Not Persistent";
    public static final String MQMD_PERSISTENCE_PERSISTENT = "Persistent";
    public static final String MQMD_PERSISTENCE_PERSISTENCE_AS_Q_DEF = "Persistence as Queue defined";

    public static final String MQMD_FEEDBACK_NONE = "000 - NONE";
    public static final String MQMD_FEEDBACK_EXPIRATION = "258 - Expiration";
    public static final String MQMD_FEEDBACK_COA = "259 - COA";
    public static final String MQMD_FEEDBACK_COD = "260 - COD";
    public static final String MQMD_FEEDBACK_QUIT = "256 - QUIT";
    public static final String MQMD_FEEDBACK_CHANNEL_COMPLETED = "262 - Channel Completed";
    public static final String MQMD_FEEDBACK_CHANNEL_FAIL_RETRY = "263 - Channel Fail Retry";
    public static final String MQMD_FEEDBACK_CHANNEL_FAIL = "264 - Channel Fail";
    public static final String MQMD_FEEDBACK_APPL_CANNOT_BE_STARTED = "265 - Application cannot be started";
    public static final String MQMD_FEEDBACK_TM_ERROR = "266 - TM error";
    public static final String MQMD_FEEDBACK_APPL_TYPE_ERROR = "267 - Application type error";
    public static final String MQMD_FEEDBACK_STOPPED_BY_MSG_EXIT = "268 - Stopped by Msg exit";
    public static final String MQMD_FEEDBACK_XMIT_Q_MSG_ERROR = "271 - XMIT queue message error";
    public static final String MQMD_FEEDBACK_PAN = "275 - PAN (Positive Acknowledgment)";
    public static final String MQMD_FEEDBACK_NAN = "276 - NAN (Negative Acknowledgment)";
    public static final String MQMD_FEEDBACK_DATA_LENGTH_ZERO = "291 - Data length is zero";
    public static final String MQMD_FEEDBACK_DATA_LENGTH_NEGATIVE = "292 - Data length is negative";
    public static final String MQMD_FEEDBACK_DATA_LENGTH_TOO_BIG = "293 - Data length too big";
    public static final String MQMD_FEEDBACK_BUFFER_OVERFLOW = "294 - buffer overflow";
    public static final String MQMD_FEEDBACK_LENGTH_OFF_BY_ONE = "295 - Length off by one";
    public static final String MQMD_FEEDBACK_IIH_ERROR = "296 - IIH error";

    //The resource domain - JMS.
    public static final String JMS = "jms";

    //The resource domain - JMS.
    public static final String JMS_PROPERTIES = "jmsProperties";

    //The resource domain - JMS.
    public static final String USE_JMS_PROPERTIES = "useJmsProperties";

    //The resource domain - JMS.
    public static final String JMS_PROPERTY_TYPES = "jmsPropertyTypes";

    //The resource domain - JMS.
    public static final String JMS_PROPERTY_NAMES = "jmsPropertyNames";
    
    // The resource domain msg properties
   public static final String MSG_PROPERTY_MSGBODY = "showMsgBody";
   public static final String MSG_PROPERTY_MSGBODYBYTEARRAY = "showMsgBodyByteArray";
   public static final String MSG_PROPERTY = "MsgProperty";
    

    //The resource domain - userDefinedProperties.
    public static final String USER_DEFINED_PROPERTIES = "userDefinedProperties";
    public static final String USER_DEFINED_PROPERTY_NAMES = "userDefinedPropertyNames";
    public static final String USER_DEFINED_PROPERTY_TYPES = "userDefinedPropertyTypes";
    public static final String REAL_USER_DEFINED_PROPERTY_NAMES = "realUserDefinedPropertyNames";
    
    public static final String JMS_DESTINATION = "JMSDestination";
    public static final String JMS_EXPIRATION = "JMSExpiration";
    public static final String JMS_PRIORITY = "JMSPriority";
    public static final String JMS_DELIVERY_MODE = "JMSDeliveryMode";
    public static final String JMS_CORRELATION_ID = "JMSCorrelationID";
    public static final String JMS_CORRELATION_ID_BYTE_ARRAY = "JMSCorrelationIDByteArray";
    public static final String JMS_REPLY_TO = "JMSReplyTo";
    public static final String JMS_TYPE = "JMSType";
    public static final String JMS_MESSAGE_ID = "JMSMessageID";
    public static final String JMS_TIMESTAMP = "JMSTimestamp";
    public static final String JMS_REDELIVERED = "JMSRedelivered";
    public static final String JMSX_GROUP_ID = "JMSXGroupID";
    public static final String JMSX_GROUP_ID_BYTE_ARRAY = "JMSXGroupIdByteArray";
    public static final String JMSX_GROUP_SEQ = "JMSXGroupSeq";
    public static final String JMSX_USER_ID = "JMSXUserID";
    public static final String JMSX_APPL_ID = "JMSXAppID";
    public static final String JMSX_DELIVERY_COUNT = "JMSXDeliveryCount";

    public static final String MCD_MESSAGE_DOMAIN = "MCDMessageDomain";
    public static final String MCD_MESSAGE_SET = "MCDMessageSet";
    public static final String MCD_MESSAGE_FORMAT = "MCDMessageFormat";
    
    public static final String SINGLE_QUEUE_LISTENER = com.wm.adapter.wmmqadapter.connection.wmMQListener.class.getName();
    public static final String MULTI_QUEUE_LISTENER = com.wm.adapter.wmmqadapter.connection.wmMQMultiQueueListener.class.getName();
	public static final String ASYNC_LISTENER_NOTIFICATION = com.wm.adapter.wmmqadapter.notification.wmMQAsyncListenerNotification.class.getName();
	public static final String SYNC_LISTENER_NOTIFICATION = com.wm.adapter.wmmqadapter.notification.wmMQSyncListenerNotification.class.getName();
    public static final String RESOLVED_QUEUE_NAME = "resolvedQueueName";

	public static final String FILTER_MESSAGE_ID = "filterMsgId";
	public static final String FILTER_CORRELATION_ID = "filterCorrelId";
	public static final String FILTER_GROUP_ID = "filterGroupId";
	public static final String FILTER_SEQUENCENUMBER = "filterSequenceNumber";
	public static final String FILTER_OFFSET = "filterOffset";

	//The parameter, resource domain - queueManagerName.
	public static final String OVERRIDE_CONNECTION_QUEUE_MANAGER_NAME = "overrideConnectionQueueManagerName";
	public static final String OVERRIDDEN_CONNECTION_QUEUE_MANAGER_NAME = "overriddenConnectionQueueManagerName";
	public static final String OVERRIDE_REPLY_TO_CONNECTION_QUEUE_MANAGER_NAME = "overrideReplyToConnectionQueueManagerName";
	public static final String OVERRIDDEN_REPLY_TO_CONNECTION_QUEUE_MANAGER_NAME = "overriddenReplyToConnectionQueueManagerName";

	//The parameter, resource domain - queueName.
	public static final String OVERRIDE_CONNECTION_QUEUE_NAME = "overrideConnectionQueueName";
	public static final String OVERRIDDEN_CONNECTION_QUEUE_NAME = "overriddenConnectionQueueName";
	public static final String OVERRIDE_REPLY_TO_CONNECTION_QUEUE_NAME = "overrideReplyToConnectionQueueName";
	public static final String OVERRIDDEN_REPLY_TO_CONNECTION_QUEUE_NAME = "overriddenReplyToConnectionQueueName";

	//The parameter, resource domain - queueName.
	public static final String OVERRIDE_CONNECTION_DYNAMIC_QUEUE_PREFIX = "overrideConnectionDynamicQueuePrefix";
	public static final String OVERRIDDEN_CONNECTION_DYNAMIC_QUEUE_PREFIX = "overriddenConnectionDynamicQueuePrefix";
	public static final String OVERRIDE_REPLY_TO_CONNECTION_DYNAMIC_QUEUE_PREFIX = "overrideReplyToConnectionDynamicQueuePrefix";
	public static final String OVERRIDDEN_REPLY_TO_CONNECTION_DYNAMIC_QUEUE_PREFIX = "overriddenReplyToConnectionDynamicQueuePrefix";

	//The parameter, resource domain - hostName.
	public static final String OVERRIDE_CONNECTION_HOST_NAME = "overrideConnectionHostName";
	public static final String OVERRIDDEN_CONNECTION_HOST_NAME = "overriddenConnectionHostName";
	public static final String OVERRIDE_REPLY_TO_CONNECTION_HOST_NAME = "overrideReplyToConnectionHostName";
	public static final String OVERRIDDEN_REPLY_TO_CONNECTION_HOST_NAME = "overriddenReplyToConnectionHostName";

	//The parameter, resource domain - port.
	public static final String OVERRIDE_CONNECTION_PORT = "overrideConnectionPort";
	public static final String OVERRIDDEN_CONNECTION_PORT = "overriddenConnectionPort";
	public static final String OVERRIDE_REPLY_TO_CONNECTION_PORT = "overrideReplyToConnectionPort";
	public static final String OVERRIDDEN_REPLY_TO_CONNECTION_PORT = "overriddenReplyToConnectionPort";

	//The parameter, resource domain - channel.
	public static final String OVERRIDE_CONNECTION_CHANNEL_NAME = "overrideConnectionChannel";
	public static final String OVERRIDDEN_CONNECTION_CHANNEL_NAME = "overriddenConnectionChannel";
	public static final String OVERRIDE_REPLY_TO_CONNECTION_CHANNEL_NAME = "overrideReplyToConnectionChannel";
	public static final String OVERRIDDEN_REPLY_TO_CONNECTION_CHANNEL_NAME = "overriddenReplyToConnectionChannel";

	//The parameter, resource domain - CCSID.
	public static final String OVERRIDE_CONNECTION_CCSID = "overrideConnectionCCSID";
	public static final String OVERRIDDEN_CONNECTION_CCSID = "overriddenConnectionCCSID";
	public static final String OVERRIDE_REPLY_TO_CONNECTION_CCSID = "overrideReplyToConnectionCCSID";
	public static final String OVERRIDDEN_REPLY_TO_CONNECTION_CCSID = "overriddenReplyToConnectionCCSID";
	
	//The parameter, resource domain - Encoding.
	public static final String OVERRIDE_CONNECTION_ENCODING = "overrideConnectionEncoding";
	public static final String OVERRIDDEN_CONNECTION_ENCODING = "overriddenConnectionEncoding";
	public static final String OVERRIDE_REPLY_TO_CONNECTION_ENCODING = "overrideReplyToConnectionEncoding";
	public static final String OVERRIDDEN_REPLY_TO_CONNECTION_ENCODING = "overriddenReplyToConnectionEncoding";
	
	//The parameter, resource domain - useLocalQueueManager.
	public static final String OVERRIDE_USE_LOCAL_QUEUE_MANAGER = "overrideUseLocalQueueManager";

	//The parameter, resource domain - queueName.
	public static final String OVERRIDE_CONNECTION_USERID = "overrideConnectionUserid";
	public static final String OVERRIDDEN_CONNECTION_USERID = "overriddenConnectionUserid";
	public static final String OVERRIDE_REPLY_TO_CONNECTION_USERID = "overrideReplyToConnectionUserid";
	public static final String OVERRIDDEN_REPLY_TO_CONNECTION_USERID = "overriddenReplyToConnectionUserid";

	//The parameter, resource domain - queueName.
	public static final String OVERRIDE_CONNECTION_PASSWORD = "overrideConnectionPassword";
	public static final String OVERRIDDEN_CONNECTION_PASSWORD = "overriddenConnectionPassword";
	public static final String OVERRIDE_REPLY_TO_CONNECTION_PASSWORD = "overrideReplyToConnectionPassword";
	public static final String OVERRIDDEN_REPLY_TO_CONNECTION_PASSWORD = "overriddenReplyToConnectionPassword";

	//The parameter, resource domain - overrideConnectionPropertyType.
	public static final String OVERRIDE_PROPERTY_NAMES = "overrideConnectionPropertyNames";
	public static final String OVERRIDE_PROPERTY_TYPE = "overrideConnectionPropertyType";
	public static final String OVERRIDE_REPLY_TO_PROPERTY_NAMES = "overrideReplyToConnectionPropertyNames";
	public static final String OVERRIDE_REPLY_TO_PROPERTY_TYPE = "overrideReplyToConnectionPropertyType";
	
    //Trax 1-WVILA. Options for watt.WmMQAdapter.deadLetterMessageHeaders property 
    public static final String[] deadLetterMessageHeaderOptions = { "NONE", "DLH", "MQMD", "DLH_&_MQMD" }; //NOSONAR
    

	//The parameter, resource domain - queueProperties.
	public static final String QUEUE_PROPERTIES_TAB = "QueueProperties";
	public static final String QUEUE_PROPERTIES = "queueProperties";
	public static final String QUEUE_PROPERTY_NAMES = "queuePropertyNames";
	public static final String QUEUE_PROPERTY_TYPES = "queuePropertyTypes";
	public static final String QUEUE_PROPERTIES_LOOKUP = "queuePropertiesLookup";
    public static final String QUEUE_PROPERTY_TYPES_LOOKUP = "queuePropertyTypesLookup";

	//The parameter, resource domain - queueManagerProperties.
	public static final String QUEUE_MANAGER_PROPERTIES_TAB = "QueueManagerProperties";
	public static final String QUEUE_MGR_PROPERTIES = "queueMgrProperties";
	public static final String QUEUE_MGR_PROPERTY_NAMES = "queueMgrPropertyNames";
	public static final String QUEUE_MGR_PROPERTY_TYPES = "queueMgrPropertyTypes";
	public static final String QUEUE_MGR_PROPERTIES_LOOKUP = "queueMgrPropertiesLookup";
	public static final String QUEUE_MGR_PROPERTY_TYPES_LOOKUP = "queueMgrPropertyTypesLookup";
	
    //The resource domain - PCFCommand.
    public static final String PCF_COMMAND = "PCFCommand";

	public static final String REQUEST_PARAMETERS_TAB = "Request";
    //The resource domain - requiredRequestParameters.
    public static final String REQUIRED_REQUEST_PARAMETERS = "requiredRequestParameters";
    public static final String REQUIRED_REQUEST_PARAMETER_NAMES = "requiredRequestParameterNames";
    public static final String REQUIRED_REQUEST_PARAMETER_TYPES = "requiredRequestParameterTypes";
    
    //The resource domain - optionalRequestParameters.
    public static final String OPTIONAL_REQUEST_PARAMETERS = "optionalRequestParameters";
    public static final String OPTIONAL_REQUEST_PARAMETER_NAMES = "optionalRequestParameterNames";
    public static final String OPTIONAL_REQUEST_PARAMETER_TYPES = "optionalRequestParameterTypes";
    
	public static final String RESPONSE_PARAMETERS_TAB = "Response";
	
    //The resource domain - responseParameters.
    public static final String RESPONSE_PARAMETERS = "responseParameters";
    public static final String RESPONSE_PARAMETER_NAMES = "responseParameterNames";
    public static final String RESPONSE_PARAMETER_TYPES = "responseParameterTypes";
    
    
    public static final String MQOPEN_MQOO_INQUIRE = "MQOO_INQUIRE";
    public static final String MQOPEN_MQOO_FAIL_IF_QUIESCING = "MQOO_FAIL_IF_QUIESCING";
    public static final String MQOPEN_MQOO_BROWSE = "MQOO_BROWSE";
    
    
    
}
