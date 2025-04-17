package com.wm.adapter.wmmqadapter.service;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.resource.ResourceException;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.MQConstants;
import com.wm.adapter.wmmqadapter.WmMQAdapterUtils;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adapter.wmmqadapter.connection.wmMQConnection;
import com.wm.adapter.wmmqadapter.connection.wmMQException;
import com.wm.adk.cci.interaction.WmAdapterService;
import com.wm.adk.cci.record.WmRecord;
import com.wm.adk.cci.record.WmRecordFactory;
import com.wm.adk.connection.WmManagedConnection;
import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;
import com.wm.adk.metadata.WmTemplateDescriptor;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;

/**
 * This class implements the functionality of inquiring
 * Queue Manager and Queue Properties
 */
public class InquireQueueManager extends WmAdapterService
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String _serviceName = wmMQAdapterConstant.INQUIRE_QUEUE_MANAGER_SERVICE;

	// Throw Exception on failure
    private boolean _throwExceptionOnFailure = true;

	//QueueMgr Properties
	private String[] _queueMgrProperties;

	//The list of QueueMgr property names.
	private String[] _queueMgrPropertyNames;

	//QueueMgr Property types
    private String[] _queueMgrPropertyTypes;

	//Queue Properties
	private String[] _queueProperties;
	
	//Queue Property Names
	private String[] _queuePropertyNames;
	
	//Queue Property Types
	private String[] _queuePropertyTypes;
	
	//Queue name to be provided as input
	private String _queueName = "";
	private String _queueNameName = wmMQAdapterConstant.QUEUE_NAME;
	private String[] _queueNameType = {"java.lang.String"};

	private String _conditionCode = "0";
	private String _conditionCodeName = wmMQAdapterConstant.CONDITION_CODE;
	private String[] _conditionCodeType = {"java.lang.String"};
	
	private String _reasonCode = "0";
	private String _reasonCodeName = wmMQAdapterConstant.REASON_CODE;
	private String[] _reasonCodeType = {"java.lang.String"};
	
	private String _errorMsg = "";
	private String _errorMsgName = wmMQAdapterConstant.ERROR_MSG;
	private String[] _errorMsgType = {"java.lang.String"};

	private String[] _serviceGroupFieldNames 			 = new String[] {wmMQAdapterConstant.THROW_EXCEPTION_ON_FAILURE,
																		 wmMQAdapterConstant.CONDITION_CODE,
																		 wmMQAdapterConstant.CONDITION_CODE_NAME,
																		 wmMQAdapterConstant.CONDITION_CODE_TYPE,
																		 wmMQAdapterConstant.REASON_CODE,
																		 wmMQAdapterConstant.REASON_CODE_NAME,
																		 wmMQAdapterConstant.REASON_CODE_TYPE,
																		 wmMQAdapterConstant.ERROR_MSG,
																		 wmMQAdapterConstant.ERROR_MSG_NAME,
																		 wmMQAdapterConstant.ERROR_MSG_TYPE,
																		 wmMQAdapterConstant.INQUIRE_REMOTE_QUEUE,
																		 wmMQAdapterConstant.INQUIRE_REMOTE_QUEUE_NAME,
																		 wmMQAdapterConstant.INQUIRE_REMOTE_TYPE
																		 };
	
	
	private String remoteQueue = "";
	private String remoteQueueName= wmMQAdapterConstant.INQUIRE_REMOTE_QUEUE;
	private String[] remoteQueueType = {"java.lang.Boolean"};
	
    public boolean getThrowExceptionOnFailure() {
        return _throwExceptionOnFailure;
    }
    public void setThrowExceptionOnFailure(boolean throwException) {
        this._throwExceptionOnFailure = throwException;
    }
	
    public String[] getQueueMgrPropertyNames()
    {
        return _queueMgrPropertyNames; //NOSONAR
    }

    public void setQueueMgrPropertyNames(String[] queueMgrPropertyNames)
    {
        _queueMgrPropertyNames = queueMgrPropertyNames; //NOSONAR
    }

    public String[] getQueueMgrProperties()
    {
        return _queueMgrProperties; //NOSONAR
    }

    public void setQueueMgrProperties(String[] queueMgrProperties)
    {
        _queueMgrProperties = queueMgrProperties;//NOSONAR
    }

    public String[] getQueueMgrPropertyTypes()
    {
        return _queueMgrPropertyTypes;//NOSONAR
    }

    public void setQueueMgrPropertyTypes(String[] types)
    {
        _queueMgrPropertyTypes = types;//NOSONAR
    }

	public String[] getQueueProperties()
	{
		return _queueProperties;//NOSONAR
	}

	public void setQueueProperties(String[] queueProperties)
	{
		_queueProperties = queueProperties;//NOSONAR
	}

	public String[] getQueuePropertyNames()
	{
		return _queuePropertyNames;//NOSONAR
	}

	public void setQueuePropertyNames(String[] queuePropertyNames)
	{
		_queuePropertyNames = queuePropertyNames;//NOSONAR
	}

	public String[] getQueuePropertyTypes()
	{
		return _queuePropertyTypes;//NOSONAR
	}

	public void setQueuePropertyTypes(String[] queuePropertyTypes)
	{
		_queuePropertyTypes = queuePropertyTypes;//NOSONAR
	}

	public String getQueueName()
	{
		return _queueName;
	}

	public void setQueueName(String queueName)
	{
		_queueName = queueName;
	}

	public String[] getQueueNameType()
	{
		return _queueNameType;//NOSONAR
	}

	public void setQueueNameType(String[] type)
	{
		_queueNameType = type;//NOSONAR
	}

	public String getQueueNameName()
	{
		return _queueNameName;
	}

	public void setQueueNameName(String name)
	{
		_queueNameName = name;
	}

	public String getReasonCode() {
		return _reasonCode;
	}
	public void setReasonCode(String code) {
		_reasonCode = code;
	}

	public String getReasonCodeName() {
		return _reasonCodeName;
	}
	public void setReasonCodeName(String name) {
		_reasonCodeName = name;
	}

	public String[] getReasonCodeType() {
		return _reasonCodeType;//NOSONAR
	}
	public void setReasonCodeType(String[] types) {
		_reasonCodeType = types;//NOSONAR
	}

	public String getConditionCode() {
		return _conditionCode;
	}
	public void setConditionCode(String code) {
		_conditionCode = code;
	}

	public String getConditionCodeName() {
		return _conditionCodeName;
	}
	public void setConditionCodeName(String name) {
		_conditionCodeName = name;
	}
	
	public String[] getConditionCodeType() {
		return _conditionCodeType;//NOSONAR
	}
	public void setConditionCodeType(String[] types) {
		_conditionCodeType = types;//NOSONAR
	}

	public String getErrorMsg() {
		return _errorMsg;
	}
	public void setErrorMsg(String msg) {
		_errorMsg = msg;
	}

	public String getErrorMsgName() {
		return _errorMsgName;
	}
	public void setErrorMsgName(String name) {
		_errorMsgName = name;
	}
	
	public String[] getErrorMsgType() {
		return _errorMsgType;//NOSONAR
	}
	public void setErrorMsgType(String[] types) {
		_errorMsgType = types;//NOSONAR
	}
	
	
	public String getRemoteQueue(){
		return remoteQueue;
	}
	
	public void setRemoteQueue(String value){
		remoteQueue = value;
	}
	
	public void fillWmTemplateDescriptor(WmTemplateDescriptor d, Locale l) throws AdapterException {
		// First tab
        d.createGroup(_serviceName, _serviceGroupFieldNames);
        
		// Second Tab
		String[] _queueMgrPropertyFieldNames = new String[] {wmMQAdapterConstant.QUEUE_MGR_PROPERTIES,
															 wmMQAdapterConstant.QUEUE_MGR_PROPERTY_NAMES,
															 wmMQAdapterConstant.QUEUE_MGR_PROPERTY_TYPES};
		d.createGroup(_serviceName + wmMQAdapterConstant.QUEUE_MANAGER_PROPERTIES_TAB, _queueMgrPropertyFieldNames);

		// Third Tab
		String[] _queuePropertyFieldNames = new String[] {wmMQAdapterConstant.QUEUE_PROPERTIES,
														  wmMQAdapterConstant.QUEUE_PROPERTY_NAMES,
														  wmMQAdapterConstant.QUEUE_PROPERTY_TYPES};
		d.createGroup(_serviceName + wmMQAdapterConstant.QUEUE_PROPERTIES_TAB, _queuePropertyFieldNames);
		
        d.setGroupName(wmMQAdapterConstant.QUEUE_NAME, _serviceName + wmMQAdapterConstant.QUEUE_PROPERTIES_TAB);
        d.setGroupName(wmMQAdapterConstant.QUEUE_NAME_NAME, _serviceName + wmMQAdapterConstant.QUEUE_PROPERTIES_TAB);
        d.setGroupName(wmMQAdapterConstant.QUEUE_NAME_TYPE, _serviceName + wmMQAdapterConstant.QUEUE_PROPERTIES_TAB);
        
		d.createFieldMap(_queueMgrPropertyFieldNames, true);
		d.createFieldMap(_queuePropertyFieldNames, true);
		d.createFieldMap(new String[] {wmMQAdapterConstant.QUEUE_NAME, 
									   wmMQAdapterConstant.QUEUE_NAME_NAME, 
									   wmMQAdapterConstant.QUEUE_NAME_TYPE}, 
						 true);

		d.createTuple(new String[] { wmMQAdapterConstant.QUEUE_MGR_PROPERTIES,
									 wmMQAdapterConstant.QUEUE_MGR_PROPERTY_TYPES});

		d.createTuple(new String[] { wmMQAdapterConstant.QUEUE_PROPERTIES,
									 wmMQAdapterConstant.QUEUE_PROPERTY_TYPES});

        d.createTuple(new String[] { wmMQAdapterConstant.QUEUE_NAME,
        							 wmMQAdapterConstant.QUEUE_NAME_TYPE});

        d.createTuple( new String[] {wmMQAdapterConstant.CONDITION_CODE, 
				 					 wmMQAdapterConstant.CONDITION_CODE_TYPE});
		d.createTuple( new String[] {wmMQAdapterConstant.REASON_CODE, 
				 					 wmMQAdapterConstant.REASON_CODE_TYPE});
		d.createTuple( new String[] {wmMQAdapterConstant.ERROR_MSG, 
				 					 wmMQAdapterConstant.ERROR_MSG_TYPE});
        
		d.setHidden(wmMQAdapterConstant.QUEUE_MGR_PROPERTY_TYPES, true);
        d.setHidden(wmMQAdapterConstant.QUEUE_MGR_PROPERTY_NAMES, true);
        
		d.setHidden(wmMQAdapterConstant.QUEUE_PROPERTY_TYPES, true);
        d.setHidden(wmMQAdapterConstant.QUEUE_PROPERTY_NAMES, true);
        
        d.setHidden(wmMQAdapterConstant.QUEUE_NAME, true);
        d.setHidden(wmMQAdapterConstant.QUEUE_NAME_NAME, true);
        d.setHidden(wmMQAdapterConstant.QUEUE_NAME_TYPE, true);

		d.setHidden(wmMQAdapterConstant.CONDITION_CODE, true);
		d.setHidden(wmMQAdapterConstant.CONDITION_CODE_NAME, true);
		d.setHidden(wmMQAdapterConstant.CONDITION_CODE_TYPE, true);
		
		d.setHidden(wmMQAdapterConstant.REASON_CODE, true);
		d.setHidden(wmMQAdapterConstant.REASON_CODE_NAME, true);
		d.setHidden(wmMQAdapterConstant.REASON_CODE_TYPE, true);
		
		d.setHidden(wmMQAdapterConstant.ERROR_MSG, true);
		d.setHidden(wmMQAdapterConstant.ERROR_MSG_NAME, true);
		d.setHidden(wmMQAdapterConstant.ERROR_MSG_TYPE, true);
		
		d.setHidden(wmMQAdapterConstant.INQUIRE_REMOTE_QUEUE_NAME, true);
		d.setHidden(wmMQAdapterConstant.INQUIRE_REMOTE_QUEUE, true);
		d.setHidden(wmMQAdapterConstant.INQUIRE_REMOTE_TYPE, true);
		
        d.setResourceDomain(wmMQAdapterConstant.QUEUE_MGR_PROPERTIES,
        					wmMQAdapterConstant.QUEUE_MGR_PROPERTIES_LOOKUP,
		                    null);

		d.setResourceDomain(wmMQAdapterConstant.QUEUE_MGR_PROPERTY_TYPES,
							wmMQAdapterConstant.QUEUE_MGR_PROPERTY_TYPES_LOOKUP,
		                    null);

		d.setResourceDomain(wmMQAdapterConstant.QUEUE_MGR_PROPERTY_NAMES,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
		               	    new String[] {wmMQAdapterConstant.QUEUE_MGR_PROPERTIES,
										  wmMQAdapterConstant.QUEUE_MGR_PROPERTY_TYPES});

        d.setResourceDomain(wmMQAdapterConstant.QUEUE_PROPERTIES,
        		wmMQAdapterConstant.QUEUE_PROPERTIES_LOOKUP,
			                null);
		
		d.setResourceDomain(wmMQAdapterConstant.QUEUE_PROPERTY_TYPES,
							wmMQAdapterConstant.QUEUE_PROPERTY_TYPES_LOOKUP,
			                null);
		
		d.setResourceDomain(wmMQAdapterConstant.QUEUE_PROPERTY_NAMES,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
			           	    new String[] {wmMQAdapterConstant.QUEUE_PROPERTIES,
										  wmMQAdapterConstant.QUEUE_PROPERTY_TYPES});

		d.setResourceDomain(wmMQAdapterConstant.QUEUE_NAME_NAME,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
			           	    new String[] {wmMQAdapterConstant.QUEUE_NAME,
										  wmMQAdapterConstant.QUEUE_NAME_TYPE});
		

		d.setResourceDomain(wmMQAdapterConstant.INQUIRE_REMOTE_QUEUE_NAME,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
			           	    new String[] {wmMQAdapterConstant.INQUIRE_REMOTE_QUEUE,
										  wmMQAdapterConstant.INQUIRE_REMOTE_TYPE});

		d.setResourceDomain(wmMQAdapterConstant.QUEUE_PROPERTIES,
							wmMQAdapterConstant.QUEUE_PROPERTIES_LOOKUP,
		                    null);

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

		d.setDescriptions((wmMQAdapter.getInstance()).getAdapterResourceBundleManager(), l);
	}

	public WmRecord execute(WmManagedConnection connection,
							WmRecord input) throws ResourceException {

		wmMQConnection mqConnection = (wmMQConnection)connection;
		IDataCursor idc = input.getCursor();
        String queueName = null;
        boolean isRemoteQueue;
		if (idc != null && idc.first(wmMQAdapterConstant.QUEUE_NAME)) {
			queueName = (String)idc.getValue();
		}
		
		isRemoteQueue = IDataUtil.getBoolean(idc, wmMQAdapterConstant.INQUIRE_REMOTE_QUEUE);
		
		if(idc != null) {
			idc.destroy();
		}
		
        WmRecord output = WmRecordFactory.getFactory().createWmRecord("Output");
        
        String[] selectedQueueManagerProperties = new String[getQueueMgrProperties().length];
        for(int i=0; i<getQueueMgrProperties().length; i++) {
        	selectedQueueManagerProperties[i] = getQueueMgrProperties()[i].substring("QueueManagerProperties.".length());
        }
        
        String[] selectedQueueProperties = new String[getQueueProperties().length];
        for(int i=0; i<getQueueProperties().length; i++) {
        	selectedQueueProperties[i] = getQueueProperties()[i].substring("QueueProperties.".length());
        }
        
		Map<String, Object> queueManagerProperties = null;
		Map<String, Object> queueProperties = null;
		try {
			queueManagerProperties = mqConnection.inquireQueueManagerProperties(selectedQueueManagerProperties);
			queueProperties = mqConnection.inquireQueueProperties(queueName, selectedQueueProperties,isRemoteQueue);
			
	        output.put(wmMQAdapterConstant.CONDITION_CODE, ""+MQConstants.MQCC_OK);
	        output.put(wmMQAdapterConstant.REASON_CODE, ""+MQConstants.MQRC_NONE);
		} catch (wmMQException mqe) {
            WmMQAdapterUtils.log(ARTLogger.ERROR,
				                 1055,
				                 "InquireQueueManager",
				                 mqe.getMessage());
            if(getThrowExceptionOnFailure()) {
	            if (WmMQAdapterUtils.fatalException(((MQException)mqe.getLinkedException()).reasonCode)) {
	                throw wmMQAdapter.getInstance().createAdapterConnectionException(1055, 
	                																 new String[]{"InquireQueueManager", mqe.getMessage()}, 
	                																 mqe);
	            }
	            else {
	            	throw wmMQAdapter.getInstance().createAdapterException(1055, 
																		   new String[]{"InquireQueueManager", mqe.getMessage()}, 
																		   mqe);
	            }
            }
            
            output.put(wmMQAdapterConstant.CONDITION_CODE, "" + ((MQException)mqe.getLinkedException()).completionCode);
            output.put(wmMQAdapterConstant.REASON_CODE, "" + ((MQException)mqe.getLinkedException()).reasonCode);
			output.put(wmMQAdapterConstant.ERROR_MSG, mqe.getMessage());
		}
		
		if(queueManagerProperties != null && !queueManagerProperties.isEmpty()) {
	        WmRecord queueManagerPropertiesRecord = WmRecordFactory.getFactory().createWmRecord("QueueManagerProperties");
	        output.put("QueueManagerProperties", queueManagerPropertiesRecord);
			
	        for(Iterator it = queueManagerProperties.entrySet().iterator(); it.hasNext(); ) {
	        	Map.Entry entry = (Map.Entry)it.next();
	        	if(entry.getValue() != null) {
		        	if(entry.getValue() instanceof Integer) {
		        		queueManagerPropertiesRecord.put(entry.getKey(), ((Integer)entry.getValue()).intValue());
		        	}
		        	else {
		        		queueManagerPropertiesRecord.put(entry.getKey(), entry.getValue().toString());
		        	}
	        	}
	        }
		}
		if(queueProperties != null && !queueProperties.isEmpty()) {
	        WmRecord queuePropertiesRecord = WmRecordFactory.getFactory().createWmRecord("QueueProperties");
	        output.put("QueueProperties", queuePropertiesRecord);
			
	        for(Iterator it = queueProperties.entrySet().iterator(); it.hasNext(); ) {
	        	Map.Entry entry = (Map.Entry)it.next();
	        	if(entry.getValue() != null) {
		        	if(entry.getValue() instanceof Integer) {
		        		queuePropertiesRecord.put(entry.getKey(), ((Integer)entry.getValue()).intValue());
		        	}
		        	else {
		        		queuePropertiesRecord.put(entry.getKey(), entry.getValue().toString());
		        	}
	        	}
	        }
		}

		return output;
	}
	public String getRemoteQueueName() {
		return remoteQueueName;
	}
	public void setRemoteQueueName(String remoteQueueName) {
		this.remoteQueueName = remoteQueueName;
	}
	public String[] getRemoteQueueType() {
		return remoteQueueType;//NOSONAR
	}
	public void setRemoteQueueType(String[] remoteQueueType) {
		this.remoteQueueType = remoteQueueType;//NOSONAR
	}
}
