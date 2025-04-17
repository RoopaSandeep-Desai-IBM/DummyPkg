/**
 * PCFCommand.java
 *
 * Copyright 2006 webMethods, Inc.
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

import java.util.Locale;

import javax.resource.ResourceException;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.pcf.PCFException;
import com.wm.adapter.wmmqadapter.WmMQAdapterUtils;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adapter.wmmqadapter.connection.wmMQConnection;
import com.wm.adapter.wmmqadapter.connection.wmMQException;
import com.wm.adapter.wmmqadapter.connection.wmPCFMessage;
import com.wm.adk.cci.interaction.WmAdapterService;
import com.wm.adk.cci.record.WmRecord;
import com.wm.adk.cci.record.WmRecordFactory;
import com.wm.adk.connection.WmManagedConnection;
import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;
import com.wm.adk.metadata.WmTemplateDescriptor;
import com.wm.data.IData;
import com.wm.data.IDataCursor;

/**
 * This class is an implementation of the PCFCommand service template
 * for the wmMQAdapter. 
 * The PCFCommand service sends a request to the queue manager to 
 * execute the given PCF command and process the responses if any.
 */
public class PCFCommand extends WmAdapterService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6568560317679206599L;

	// PCF Command to be executed
    private String _PCFCommand = "INQUIRE_Q"; // Default PCF Command
    
	// Throw Exception on failure
    private boolean _throwExceptionOnFailure = true;

	// Required Request parameters
	private String[] _requiredRequestParameters;
	
	// Required Request parameters names
	private String[] _requiredRequestParameterNames;
	
	// Required Request parameters types
	private String[] _requiredRequestParameterTypes;
	
	// Optional Request parameters
	private String[] _optionalRequestParameters;
	
	// Optional Request parameters names
	private String[] _optionalRequestParameterNames;
	
	// Optional Request parameters types
	private String[] _optionalRequestParameterTypes;
	
	// Response parameters
	private String[] _responseParameters;
	
	// Response parameter names
	private String[] _responseParameterNames;

	// Response parameter types
	private String[] _responseParameterTypes;

    // WaitInterval property
    private int _waitInterval = 60000;
    private String _waitIntervalName = wmMQAdapterConstant.WAIT_INTERVAL;
    private String _waitIntervalType = "java.lang.Integer";
    
	private String _conditionCode = "0";
	private String _conditionCodeName = wmMQAdapterConstant.CONDITION_CODE;
	private String[] _conditionCodeType = {"java.lang.String"};
	
	private String _reasonCode = "0";
	private String _reasonCodeName = wmMQAdapterConstant.REASON_CODE;
	private String[] _reasonCodeType = {"java.lang.String"};
	
	private String _errorMsg = "";
	private String _errorMsgName = wmMQAdapterConstant.ERROR_MSG;
	private String[] _errorMsgType = {"java.lang.String"};

	
	private String[] _serviceGroupFieldNames 			 = new String[] {wmMQAdapterConstant.PCF_COMMAND,
																		 wmMQAdapterConstant.WAIT_INTERVAL,
																         wmMQAdapterConstant.WAIT_INTERVAL_TYPE,
																         wmMQAdapterConstant.WAIT_INTERVAL_NAME,
														                 wmMQAdapterConstant.THROW_EXCEPTION_ON_FAILURE,
																		 wmMQAdapterConstant.CONDITION_CODE,
																		 wmMQAdapterConstant.CONDITION_CODE_NAME,
																		 wmMQAdapterConstant.CONDITION_CODE_TYPE,
																		 wmMQAdapterConstant.REASON_CODE,
																		 wmMQAdapterConstant.REASON_CODE_NAME,
																		 wmMQAdapterConstant.REASON_CODE_TYPE,
																		 wmMQAdapterConstant.ERROR_MSG,
																		 wmMQAdapterConstant.ERROR_MSG_NAME,
																		 wmMQAdapterConstant.ERROR_MSG_TYPE																		 
																		};
	
	private String[] _requiredRequestParameterFieldNames = new String[] {wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETERS,
																		 wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETER_NAMES,
																		 wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETER_TYPES
																		};
	private String[] _optionalRequestParameterFieldNames = new String[] {wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETERS,
																		 wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETER_NAMES,
																		 wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETER_TYPES
																		};
	private String[] _responseParameterFieldNames 		 = new String[] {wmMQAdapterConstant.RESPONSE_PARAMETERS,
																		 wmMQAdapterConstant.RESPONSE_PARAMETER_NAMES,
																		 wmMQAdapterConstant.RESPONSE_PARAMETER_TYPES
																		};
	
	
	
    public String getPCFCommand() {
		return this._PCFCommand;
	}
	public void setPCFCommand(String command) {
		this._PCFCommand = command;
	}

    public boolean getThrowExceptionOnFailure() {
        return _throwExceptionOnFailure;
    }
    public void setThrowExceptionOnFailure(boolean throwException) {
        this._throwExceptionOnFailure = throwException;
    }
	
	public String[] getRequiredRequestParameters() {
		return this._requiredRequestParameters; //NOSONAR
	}
	public void setRequiredRequestParameters(String[] requiredRequestParameters) {
		this._requiredRequestParameters = requiredRequestParameters;//NOSONAR
	}

	public String[] getRequiredRequestParameterNames() {
		return this._requiredRequestParameterNames;//NOSONAR
	}
	public void setRequiredRequestParameterNames(String[] requiredRequestParameterNames) {
		this._requiredRequestParameterNames = requiredRequestParameterNames;//NOSONAR
	}

	public String[] getRequiredRequestParameterTypes() {
		return this._requiredRequestParameterTypes;//NOSONAR
	}
	public void setRequiredRequestParameterTypes(String[] requiredRequestParameterTypes) {
		this._requiredRequestParameterTypes = requiredRequestParameterTypes;//NOSONAR
	}

	public String[] getOptionalRequestParameters() {
		return this._optionalRequestParameters;//NOSONAR
	}
	public void setOptionalRequestParameters(String[] optionalRequestParameters) {
		this._optionalRequestParameters = optionalRequestParameters;//NOSONAR
	}

    public String[] getOptionalRequestParameterNames() {
		return this._optionalRequestParameterNames;//NOSONAR
	}
	public void setOptionalRequestParameterNames(String[] optionalRequestParameterNames) {
		this._optionalRequestParameterNames = optionalRequestParameterNames;//NOSONAR
	}

	public String[] getOptionalRequestParameterTypes() {
		return this._optionalRequestParameterTypes;//NOSONAR
	}
	public void setOptionalRequestParameterTypes(String[] optionalRequestParameterTypes) {
		this._optionalRequestParameterTypes = optionalRequestParameterTypes;//NOSONAR
	}

	public String[] getResponseParameters() {
		return this._responseParameters;//NOSONAR
	}
	public void setResponseParameters(String[] responseParameters) {
		this._responseParameters = responseParameters;//NOSONAR
	}

	public String[] getResponseParameterNames() {
		return this._responseParameterNames;//NOSONAR
	}
	public void setResponseParameterNames(String[] responseParameterNames) {
		this._responseParameterNames = responseParameterNames;//NOSONAR
	}

	public String[] getResponseParameterTypes() {
		return this._responseParameterTypes;//NOSONAR
	}
	public void setResponseParameterTypes(String[] responseParameterTypes) {
		this._responseParameterTypes = responseParameterTypes;//NOSONAR
	}

    public int getWaitInterval() {
        return _waitInterval;
    }
    public void setWaitInterval(int interval) {
        this._waitInterval = interval;
    }

	public String getWaitIntervalName() {
		return _waitIntervalName;
	}
	public void setWaitIntervalName(String waitIntervalName) {
		_waitIntervalName = waitIntervalName;
	}

	public void setWaitIntervalType(String waitIntervalType) {
		_waitIntervalType = waitIntervalType;
	}
	public String getWaitIntervalType() {
		return _waitIntervalType;
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

	public void fillWmTemplateDescriptor(WmTemplateDescriptor d, Locale l) throws AdapterException {
		WmMQAdapterUtils.log(ARTLogger.INFO, 1001, "PCFService.fillWmTemplateDescriptor", "");
    	
		WmMQAdapterUtils.log(ARTLogger.INFO, 1003, "PCFService.fillWmTemplateDescriptor", "Creating group _serviceGroupFieldNames,service=" + wmMQAdapterConstant.PCF_COMMAND_SERVICE);
		//First tab
        d.createGroup(wmMQAdapterConstant.PCF_COMMAND_SERVICE, _serviceGroupFieldNames);
		
        WmMQAdapterUtils.log(ARTLogger.INFO, 1003, "PCFService.fillWmTemplateDescriptor", "Creating group _optionalRequestParameterNames");
		//Second tab
        d.createGroup(wmMQAdapterConstant.PCF_COMMAND_SERVICE + wmMQAdapterConstant.REQUEST_PARAMETERS_TAB, _optionalRequestParameterFieldNames);
		
        WmMQAdapterUtils.log(ARTLogger.INFO, 1003, "PCFService.fillWmTemplateDescriptor", "Creating group _responseParameterNames");
		//Third tab
        d.createGroup(wmMQAdapterConstant.PCF_COMMAND_SERVICE + wmMQAdapterConstant.RESPONSE_PARAMETERS_TAB, _responseParameterFieldNames);
        
        WmMQAdapterUtils.log(ARTLogger.INFO, 1003, "PCFService.fillWmTemplateDescriptor", "Creating field map _requiredRequestParameterFieldNames");
		d.createFieldMap(_requiredRequestParameterFieldNames, false);
		
        WmMQAdapterUtils.log(ARTLogger.INFO, 1003, "PCFService.fillWmTemplateDescriptor", "Creating field map _optionalRequestParameterFieldNames");
		d.createFieldMap(_optionalRequestParameterFieldNames, true);
		
        WmMQAdapterUtils.log(ARTLogger.INFO, 1003, "PCFService.fillWmTemplateDescriptor", "Creating field map _responseParameterFieldNames");
		d.createFieldMap(_responseParameterFieldNames, true);

        WmMQAdapterUtils.log(ARTLogger.INFO, 1003, "PCFService.fillWmTemplateDescriptor", "Creating _waitIntervalTupleNames");
		d.createTuple( new String[] {wmMQAdapterConstant.WAIT_INTERVAL, 
				 					 wmMQAdapterConstant.WAIT_INTERVAL_TYPE});
        
        WmMQAdapterUtils.log(ARTLogger.INFO, 1003, "PCFService.fillWmTemplateDescriptor", "Creating _requiredRequestParameterTupleNames");
        d.createTuple( new String[] {wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETERS,
                           			 wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETER_TYPES});
        
        WmMQAdapterUtils.log(ARTLogger.INFO, 1003, "PCFService.fillWmTemplateDescriptor", "Creating _optionalRequestParameterTupleNames");
        d.createTuple( new String[] {wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETERS,
                           			 wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETER_TYPES});
        
        WmMQAdapterUtils.log(ARTLogger.INFO, 1003, "PCFService.fillWmTemplateDescriptor", "Creating _responseParameterTupleNames");
        d.createTuple( new String[] {wmMQAdapterConstant.RESPONSE_PARAMETERS,
                           			 wmMQAdapterConstant.RESPONSE_PARAMETER_TYPES});

        WmMQAdapterUtils.log(ARTLogger.INFO, 1003, "PCFService.fillWmTemplateDescriptor", "Creating _ConditionCode");
        d.createTuple( new String[] {wmMQAdapterConstant.CONDITION_CODE, 
				 					 wmMQAdapterConstant.CONDITION_CODE_TYPE});
        WmMQAdapterUtils.log(ARTLogger.INFO, 1003, "PCFService.fillWmTemplateDescriptor", "Creating _ReasonCode");
		d.createTuple( new String[] {wmMQAdapterConstant.REASON_CODE, 
				 					 wmMQAdapterConstant.REASON_CODE_TYPE});
        WmMQAdapterUtils.log(ARTLogger.INFO, 1003, "PCFService.fillWmTemplateDescriptor", "Creating _ErrorMsg");
		d.createTuple( new String[] {wmMQAdapterConstant.ERROR_MSG, 
				 					 wmMQAdapterConstant.ERROR_MSG_TYPE});
        
		d.setHidden(wmMQAdapterConstant.WAIT_INTERVAL_TYPE, true);
		d.setHidden(wmMQAdapterConstant.WAIT_INTERVAL_NAME, true);
        
		d.setHidden(wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETERS, true);
		d.setHidden(wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETER_NAMES, true);
		d.setHidden(wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETER_TYPES, true);
		
		d.setHidden(wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETER_NAMES, true);
		d.setHidden(wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETER_TYPES, true);
		
		d.setHidden(wmMQAdapterConstant.RESPONSE_PARAMETER_NAMES, true);
		d.setHidden(wmMQAdapterConstant.RESPONSE_PARAMETER_TYPES, true);
		
		d.setHidden(wmMQAdapterConstant.CONDITION_CODE, true);
		d.setHidden(wmMQAdapterConstant.CONDITION_CODE_NAME, true);
		d.setHidden(wmMQAdapterConstant.CONDITION_CODE_TYPE, true);
		
		d.setHidden(wmMQAdapterConstant.REASON_CODE, true);
		d.setHidden(wmMQAdapterConstant.REASON_CODE_NAME, true);
		d.setHidden(wmMQAdapterConstant.REASON_CODE_TYPE, true);
		
		d.setHidden(wmMQAdapterConstant.ERROR_MSG, true);
		d.setHidden(wmMQAdapterConstant.ERROR_MSG_NAME, true);
		d.setHidden(wmMQAdapterConstant.ERROR_MSG_TYPE, true);
		
		d.setResourceDomain(wmMQAdapterConstant.PCF_COMMAND,
							wmMQAdapterConstant.PCF_COMMAND,
							null);
		
        d.setResourceDomain(wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETERS,
			                wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETERS,
			                new String[] {wmMQAdapterConstant.PCF_COMMAND});

		d.setResourceDomain(wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETER_TYPES,
			                wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETER_TYPES,
			                null);

		d.setResourceDomain(wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETER_NAMES,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
			   	            new String[] {wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETERS,
			       	                      wmMQAdapterConstant.REQUIRED_REQUEST_PARAMETER_TYPES});
		
        d.setResourceDomain(wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETERS,
			                wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETERS,
			                new String[] {wmMQAdapterConstant.PCF_COMMAND});

		d.setResourceDomain(wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETER_TYPES,
			                wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETER_TYPES,
			                null);
		
		d.setResourceDomain(wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETER_NAMES,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
			   	            new String[] {wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETERS,
			       	                      wmMQAdapterConstant.OPTIONAL_REQUEST_PARAMETER_TYPES});
		
        d.setResourceDomain(wmMQAdapterConstant.RESPONSE_PARAMETERS,
			                wmMQAdapterConstant.RESPONSE_PARAMETERS,
			                new String[] {wmMQAdapterConstant.PCF_COMMAND});
		
		d.setResourceDomain(wmMQAdapterConstant.RESPONSE_PARAMETER_TYPES,
			                wmMQAdapterConstant.RESPONSE_PARAMETER_TYPES,
			                null);
		
		d.setResourceDomain(wmMQAdapterConstant.RESPONSE_PARAMETER_NAMES,
							WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
			   	            new String[] {wmMQAdapterConstant.RESPONSE_PARAMETERS,
			       	                      wmMQAdapterConstant.RESPONSE_PARAMETER_TYPES});
		
		d.setResourceDomain(wmMQAdapterConstant.WAIT_INTERVAL_TYPE,
							wmMQAdapterConstant.WAIT_INTERVAL_TYPE,
							null);

		d.setResourceDomain(wmMQAdapterConstant.WAIT_INTERVAL_NAME,
							WmTemplateDescriptor.INPUT_FIELD_NAMES,
							new String[] {wmMQAdapterConstant.WAIT_INTERVAL_TYPE});
		
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
		
		WmMQAdapterUtils.log(ARTLogger.INFO, 1002, "PCFService.fillWmTemplateDescriptor", "");
    }
	
	@SuppressWarnings("squid:S1541")
    public WmRecord execute(WmManagedConnection connection, WmRecord input) throws ResourceException {
		WmMQAdapterUtils.log(ARTLogger.INFO, 1001, "PCFService.execute", "");

		wmPCFMessage pcfMessage = new wmPCFMessage(getPCFCommand());
		
		IDataCursor idc = input.getCursor();
		if(idc != null) {
			// Set the required parameters
			if(	   getRequiredRequestParameters() != null 
				&& getRequiredRequestParameters().length > 0) {
				if(idc.first("RequiredParameters")) {
					IData requiredParameterValues = (IData)idc.getValue();
					
					IDataCursor requiredIdc = requiredParameterValues.getCursor();
					if(requiredIdc != null) {
						for(int i=0; i<getRequiredRequestParameters().length; i++) {
							if(requiredIdc.first(getRequiredRequestParameters()[i].substring("RequiredParameters.".length()))) {
								pcfMessage.addRequestParameterValue(requiredIdc.getKey(), requiredIdc.getValue());
							}
						}
						requiredIdc.destroy();
					}
				}
			}
			
			// Set the optional parameters
			if(	   getOptionalRequestParameters() != null 
				&& getOptionalRequestParameters().length > 0) {
				if(idc.first("OptionalParameters")) {
					IData optionalParameterValues = (IData)idc.getValue();
					
					IDataCursor optionalIdc = optionalParameterValues.getCursor();
					if(optionalIdc != null) {
						for(int i=0; i<getOptionalRequestParameters().length; i++) {
							String oneOptionalParameter = getOptionalRequestParameters()[i];
							if(oneOptionalParameter != null && !oneOptionalParameter.equals("")) {
								if(optionalIdc.first(oneOptionalParameter.substring("OptionalParameters.".length()))) {
									pcfMessage.addRequestParameterValue(optionalIdc.getKey(), optionalIdc.getValue());
								}
							}
						}
						optionalIdc.destroy();
					}
				}
			}
			
			idc.destroy();
		}
		
        wmMQConnection mqConnection = null;
        WmRecord output = WmRecordFactory.getFactory().createWmRecord("Output");
		try {
			mqConnection = (wmMQConnection)connection;
    		int waitInterval = getWaitInterval();
			try {
				waitInterval = input.getInt(wmMQAdapterConstant.WAIT_INTERVAL);
			} catch (AdapterException e) {
				// Take the default value provided at the design time
			}
			
			mqConnection.executePCFCommand(pcfMessage, waitInterval);
			
			// Create and populate the output
	        int numberOfResponses = pcfMessage.countResponseMessages();
			String[] responseParameters = getResponseParameters();
	        if(	   numberOfResponses > 0 
	         	&& responseParameters != null
	         	&& responseParameters.length > 0
	         	) {
	        	IData[] responseRecords = new IData[numberOfResponses];
	        	for(int i=0; i<numberOfResponses; i++) {
	                WmRecord oneResponse = WmRecordFactory.getFactory().createWmRecord("Response");
	        		responseRecords[i] = oneResponse;
	        		
					for(int j=0; j<responseParameters.length; j++) {
						Object responseValue = null;
						String oneResponseParameter = responseParameters[j];
						if(oneResponseParameter != null && !oneResponseParameter.equals("")) {
							String parameterName = oneResponseParameter.substring("Responses[].".length());
							try {
								responseValue = pcfMessage.getResponseParameterValue(i, parameterName);
			        			oneResponse.put(parameterName, responseValue);
							} catch (wmMQException mqe) {
								// Ignore the exception as user has requested for a response 
								// parameter which is not returned in the response by the PCFCommand
								PCFException pcfe = (PCFException)mqe.getLinkedException();
								String[] params = {parameterName,
												   "" + pcfe.completionCode,
												   "" + pcfe.reasonCode,
												   pcfe.getMessage()};
								WmMQAdapterUtils.logWarning(5003,params);
							}
						}
	        		}
	        	}
	        	
		        output.put("Responses", responseRecords);
	        }
	        
	        output.put(wmMQAdapterConstant.CONDITION_CODE, ""+MQConstants.MQCC_OK);
	        output.put(wmMQAdapterConstant.REASON_CODE, ""+MQConstants.MQRC_NONE);
		} catch (wmMQException mqe) {
            WmMQAdapterUtils.log(ARTLogger.ERROR,
				                 1055,
				                 "PCFCommand",
				                 mqe.getMessage());
            
            if(getThrowExceptionOnFailure()) {
	            if (WmMQAdapterUtils.fatalException(((MQException)mqe.getLinkedException()).reasonCode)) {
	                throw wmMQAdapter.getInstance().createAdapterConnectionException(1055, 
	                																 new String[]{"PCFCommand", mqe.getMessage()}, 
	                																 mqe);
	            }
	            else {
	            	throw wmMQAdapter.getInstance().createAdapterException(1055, 
																		   new String[]{"PCFCommand", mqe.getMessage()}, 
																		   mqe);
	            }
            }
            
            output.put(wmMQAdapterConstant.CONDITION_CODE, "" + ((MQException)mqe.getLinkedException()).completionCode);
            output.put(wmMQAdapterConstant.REASON_CODE, "" + ((MQException)mqe.getLinkedException()).reasonCode);
			output.put(wmMQAdapterConstant.ERROR_MSG, mqe.getMessage());
		}
		
		WmMQAdapterUtils.log(ARTLogger.INFO, 1002, "PCFService.execute", "");
    	return output;
    }
}
