/**
 * wmPCFCommandMetadata.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class encapsulates the meta data required to create a PCF Command.
 *
 */
public class wmPCFCommandMetadata {
	private String _commandName = null;
	private int _commandIntegerCode = -1;
	
	private Map _requestParameters = new HashMap();
	private Map _responseParameters = new HashMap();
	
	private List _requiredRequestParameterDisplayNames = new ArrayList();
	private List _requiredRequestParameterTypes = new ArrayList();
	private List _optionalRequestParameterDisplayNames = new ArrayList();
	private List _optionalRequestParameterTypes = new ArrayList();
	private List _responseParameterDisplayNames 		= new ArrayList();
	private List _responseParameterTypes 		= new ArrayList();

	public wmPCFCommandMetadata(String commandName, int commandIntegerCode) {
		_commandName = commandName;
		_commandIntegerCode = commandIntegerCode;
	}
	
	public int getCommandIntegerCode() {
		return this._commandIntegerCode;
	}
	
	public String getCommandName() {
		return this._commandName;
	}
	
	public Map getRequestParameters() {
		return this._requestParameters;
	}
	
	public Map getResponseParameters() {
		return this._responseParameters;
	}
	
	public void addRequestParameter(wmPCFParameterMetadata parameterMetadata) {
		_requestParameters.put(parameterMetadata.getParameterName(), parameterMetadata);
		
		List parameterTypesList = null;
		if(parameterMetadata.isRequired()) {
			_requiredRequestParameterDisplayNames.add("RequiredParameters." + parameterMetadata.getParameterName());
			parameterTypesList = _requiredRequestParameterTypes;
		}
		else {
			_optionalRequestParameterDisplayNames.add("OptionalParameters." + parameterMetadata.getParameterName());
			parameterTypesList = _optionalRequestParameterTypes;
		}
		
		switch (parameterMetadata.getParameterType()) {
			case wmPCFParameterMetadata.TYPE_STRING :
				parameterTypesList.add("java.lang.String");
				break;
			case wmPCFParameterMetadata.TYPE_STRING_ARRAY :
				parameterTypesList.add("java.lang.String[]");
				break;
			case wmPCFParameterMetadata.TYPE_INTEGER :
				parameterTypesList.add("java.lang.Integer");
				break;
			case wmPCFParameterMetadata.TYPE_INTEGER_ARRAY :
				parameterTypesList.add("java.lang.Integer[]");
				break;
		}
	}
	public wmPCFParameterMetadata getRequestParameter(String parameterName) {
		return (wmPCFParameterMetadata)_requestParameters.get(parameterName);
	}
	
	public void addResponseParameter(wmPCFParameterMetadata parameterMetadata) {
		_responseParameters.put(parameterMetadata.getParameterName(), parameterMetadata);
		
		_responseParameterDisplayNames.add("Responses[]." + parameterMetadata.getParameterName());
		switch (parameterMetadata.getParameterType()) {
			case wmPCFParameterMetadata.TYPE_STRING :
				_responseParameterTypes.add("java.lang.String[]");
				break;
			case wmPCFParameterMetadata.TYPE_STRING_ARRAY :
				_responseParameterTypes.add("java.lang.String[][]");
				break;
			case wmPCFParameterMetadata.TYPE_INTEGER :
				_responseParameterTypes.add("java.lang.Integer[]");
				break;
			case wmPCFParameterMetadata.TYPE_INTEGER_ARRAY :
				_responseParameterTypes.add("java.lang.Integer[][]");
				break;
		}
	}
	
	public wmPCFParameterMetadata getResponseParameter(String parameterName) {
		return (wmPCFParameterMetadata)_responseParameters.get(parameterName);
	}
	
	public String[] getRequiredRequestParameterDisplayNames() {
		return (String[])_requiredRequestParameterDisplayNames.toArray(new String[0]);
	}
	public String[] getRequiredRequestParameterTypes() {
		return (String[])_requiredRequestParameterTypes.toArray(new String[0]);
	}
	
	public String[] getOptionalRequestParameterDisplayNames() {
		return (String[])_optionalRequestParameterDisplayNames.toArray(new String[0]);
	}
	public String[] getOptionalRequestParameterTypes() {
		return (String[])_optionalRequestParameterTypes.toArray(new String[0]);
	}
	
	public String[] getResponseParameterDisplayNames() {
		return (String[])_responseParameterDisplayNames.toArray(new String[0]);
	}
	public String[] getResponseParameterTypes() {
		return (String[])_responseParameterTypes.toArray(new String[0]);
	}
}
