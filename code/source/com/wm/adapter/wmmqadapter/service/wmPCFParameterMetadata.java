/**
 * wmPCFParameterMetadata.java
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

public class wmPCFParameterMetadata {
	public static final int TYPE_STRING = 1;
	public static final int TYPE_STRING_ARRAY = 2;
	public static final int TYPE_INTEGER = 3;
	public static final int TYPE_INTEGER_ARRAY = 4;
	
	private String _parameterName = null;
	private int _parameterIntegerCode = -1;
	
	private int _parameterType = TYPE_STRING;
	
	private boolean _required = false;

	
	public wmPCFParameterMetadata(String parameterName, int parameterIntegerCode, int parameterType, boolean required) {
		_parameterName = parameterName;
		_parameterIntegerCode = parameterIntegerCode;
		_parameterType = parameterType;
		_required = required;
	}
	public wmPCFParameterMetadata(String parameterName, int parameterIntValue, int parameterType) {
		this(parameterName, parameterIntValue, parameterType, false);
	}
	
	public String getParameterName() {
		return this._parameterName;
	}

	public int getParameterIntegerCode() {
		return this._parameterIntegerCode;
	}

	public int getParameterType() {
		return this._parameterType;
	}
	
	public boolean isRequired() {
		return _required;
	}
}
