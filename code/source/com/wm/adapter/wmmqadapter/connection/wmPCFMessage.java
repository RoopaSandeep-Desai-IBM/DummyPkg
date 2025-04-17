/**
 * wmPCFMessage.java
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

package com.wm.adapter.wmmqadapter.connection;

import java.util.Arrays;

import com.ibm.mq.pcf.PCFException;
import com.ibm.mq.pcf.PCFMessage;
import com.wm.adapter.wmmqadapter.WmMQAdapterUtils;
import com.wm.adapter.wmmqadapter.service.wmPCFCommandMetadata;
import com.wm.adapter.wmmqadapter.service.wmPCFCommandMetadataFactory;
import com.wm.adapter.wmmqadapter.service.wmPCFParameterMetadata;
import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;

/**
 * This class is warapper for the PCFMessage object.
 */
public class wmPCFMessage {
	private wmPCFCommandMetadata _commandMetadata = null;
	
	private PCFMessage _requestMessage = null;
	
	private PCFMessage[] _responseMessages = null;
	
	public wmPCFMessage(String commandName) throws AdapterException {
		_commandMetadata = wmPCFCommandMetadataFactory.getInstance().getPCFCommandMetadata(commandName);
		
		_requestMessage = new PCFMessage(_commandMetadata.getCommandIntegerCode());
	}
	
	public PCFMessage getRequestMessage() {
		return _requestMessage;
	}
	
	public void addRequestParameterValue(String parameterName, Object value) {
		if(value != null) {
			wmPCFParameterMetadata parameterMetadata = _commandMetadata.getRequestParameter(parameterName);
			switch(parameterMetadata.getParameterType()) {
				case wmPCFParameterMetadata.TYPE_STRING :
					_requestMessage.addParameter(parameterMetadata.getParameterIntegerCode(), 
											 	 value.toString());
					break;
				case wmPCFParameterMetadata.TYPE_STRING_ARRAY :
					_requestMessage.addParameter(parameterMetadata.getParameterIntegerCode(), 
							 				 	 (String[])value);
					break;
				case wmPCFParameterMetadata.TYPE_INTEGER :
					_requestMessage.addParameter(parameterMetadata.getParameterIntegerCode(), 
			 				 				 	 ((Integer)value).intValue());
					break;
				case wmPCFParameterMetadata.TYPE_INTEGER_ARRAY :
					Integer[] integerValues = (Integer[])value;
					int[] intValues = new int[integerValues.length];
					for(int i=0; i<integerValues.length; i++) {
						intValues[i] = integerValues[i].intValue();
					}
					_requestMessage.addParameter(parameterMetadata.getParameterIntegerCode(), 
			 				 					 intValues);
					break;
			}
		}
	}
	
	public void setResponseMessages(PCFMessage[] responseMessages) {
		_responseMessages = responseMessages; //NOSONAR
	}
	
	public int countResponseMessages() {
		if(_responseMessages == null) {
			return 0;
		}
		return _responseMessages.length;
	}
	
	public Object getResponseParameterValue(int responseIndex, String parameterName) throws wmMQException {
		PCFMessage responseMessage = _responseMessages[responseIndex];
		Object returnValue = null;
		if(responseMessage != null) {
			wmPCFParameterMetadata parameterMetadata = _commandMetadata.getResponseParameter(parameterName);
			try {
				switch(parameterMetadata.getParameterType()) {
					case wmPCFParameterMetadata.TYPE_STRING :
						returnValue = responseMessage.getStringParameterValue(parameterMetadata.getParameterIntegerCode());
						break;
					case wmPCFParameterMetadata.TYPE_STRING_ARRAY :
						returnValue = responseMessage.getStringListParameterValue(parameterMetadata.getParameterIntegerCode());
						break;
					case wmPCFParameterMetadata.TYPE_INTEGER :
						returnValue = new Integer(responseMessage.getIntParameterValue(parameterMetadata.getParameterIntegerCode()));
						break;
					case wmPCFParameterMetadata.TYPE_INTEGER_ARRAY :
						int[] intValues = (int[])responseMessage.getIntListParameterValue(parameterMetadata.getParameterIntegerCode());
						Integer[] integerValues = new Integer[intValues.length];
						for(int i=0; i<intValues.length; i++) {
							integerValues[i] = new Integer(intValues[i]);
						}
						returnValue = integerValues;
						break;
				}
			} catch (PCFException pcfe) {
			   Object[] parms = {  "" + pcfe.completionCode,
				   				   "" + pcfe.reasonCode,
				   				   pcfe.getMessage()};
			
			   wmMQException mqse = new wmMQException("5003", // Error while reading the parameter {0} from the response. Completion Code : {1}; Reason Code : {2} 
			   									   	  parameterName,
										              parms);
			   mqse.setLinkedException(pcfe);
			   throw mqse;
			}
		}
		
		return returnValue;
	}
}
