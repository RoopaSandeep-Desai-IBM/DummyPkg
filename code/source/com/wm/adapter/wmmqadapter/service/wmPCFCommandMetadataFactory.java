/**
 * wmPCFCommandMetadataFactory.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adk.error.AdapterException;
import com.wm.app.b2b.server.Service;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;

/**
 * Reads the WmPCFCommandMetadataFile XML file. This file 
 * contains the PCF commands and their request and response parameters 
 * that the PCFCommand service of WebSphere MQ Adapter supports.
 *  
 * The file format is as follows:
 *	<?xml version="1.0" encoding="UTF-8"?>
 *	<!DOCTYPE PCFCommands [
 *		<!ELEMENT PCFCommands (Command+)>
 *		<!ELEMENT Command (RequestParameters?, ResponseParameters?)>
 *		<!ATTLIST Command name CDATA #REQUIRED
 *						  integerCode CDATA #REQUIRED
 *						  identifier CDATA #IMPLIED
 *		>
 *		<!ELEMENT RequestParameters (Parameter+)>
 *		<!ELEMENT ResponseParameters (Parameter+)>
 *		
 *		<!ELEMENT Parameter EMPTY>
 *		<!ATTLIST Parameter name CDATA #REQUIRED
 *							type CDATA #REQUIRED
 *							integerCode CDATA #REQUIRED
 *							identifier CDATA #IMPLIED
 *							required CDATA #IMPLIED
 *		>					
 *	]>
 *	<PCFCommands>
 *		<Command name="COMMAND_1" integerCode="18" identifier="MQCMD_COMMAND_1">
 *			<RequestParameters>
 *				<Parameter name="ParamString" integerCode="2016" type="string" required="true" identifier="MQCA_PARAM_STRING"/> 
 *				<Parameter name="ParamStringArray" integerCode="2017" type="string[]" identifier="MQCA_PARAM_STRING_ARRAY"/>
 *				<Parameter name="ParamInt" integerCode="10" type="int" identifier="MQIA_PARAM_INT"/>
 *				<Parameter name="ParamIntArray" integerCode="20" type="int[]" identifier="MQIA_PARAM_INT_ARRAY"/>
 *				.....
 *			</RequestParameters>
 *			<ResponseParameters>
 *				<Parameter name="ResponseParamString" integerCode="2016" type="string" required="true" identifier="MQCA_RESP_PARAM_STRING"/> 
 *				<Parameter name="ResponseParamStringArray" integerCode="2017" type="string[]" identifier="MQCA_RESP_PARAM_STRING_ARRAY"/>
 *				<Parameter name="ResponseParamInt" integerCode="10" type="int" identifier="MQIA_RESP_PARAM_INT"/>
 *				<Parameter name="ResponseParamIntArray" integerCode="20" type="int[]" identifier="MQIA_RESP_PARAM_INT_ARRAY"/>
 *				.....
 *			</ResponseParameters>
 *		</Command>
 *		
 *		.....
 *		
 *	</PCFCommands>
 */
public class wmPCFCommandMetadataFactory {
	private static final String FILESTREAM			= "$filestream";
    private static final String PUBXML_FLDR			= "pub.xml";
    private static final String XMLSTRTONODE_SVC	= "xmlStringToXMLNode";
    private static final String NODE				= "node";
    private static final String MAKEARRAYS			= "makeArrays";
    private static final String XMLNODETODOC_SVC	= "xmlNodeToDocument";
    private static final String DOCUMENT			= "document";
    
    private static final String PCF_COMMANDS		= "PCFCommands";
    private static final String COMMAND				= "Command";
	private static final String REQUEST_PARAMETERS  = "RequestParameters";
	private static final String RESPONSE_PARAMETERS = "ResponseParameters";
	private static final String PARAMETER 			= "Parameter";
	private static final String REQUIRED 			= "@required";
	private static final String TYPE 				= "@type";
	private static final String INTEGER_CODE 		= "@integerCode";
	private static final String NAME 				= "@name";
    
    private static final String IMPORT_FILE 		= "packages" + File.separator + 
    										 		  "WmMQAdapter" + File.separator + 
    										 		  "config" + File.separator + 
    										 		  "WmPCFCommandMetadataFile.xml";	
	
    private static wmPCFCommandMetadataFactory _instance = null;
	private Map _pcfCommandsMetadata 					 = new HashMap();
	private String[] _supportedCommands 				 = null;
	
	
	public static wmPCFCommandMetadataFactory getInstance() throws AdapterException {
		if(_instance == null) {
			synchronized (wmPCFCommandMetadataFactory.class) {
					_instance = new wmPCFCommandMetadataFactory();
					_instance.initialize();
			}
		}
		return _instance;
	}

	public wmPCFCommandMetadata getPCFCommandMetadata(String commandName) {
		return (wmPCFCommandMetadata)_pcfCommandsMetadata.get(commandName);
	}
	
	public String[] getSupportedPCFCommands() {
		if(_supportedCommands == null) {
			_supportedCommands = (String[])_pcfCommandsMetadata.keySet().toArray(new String[0]);
			Arrays.sort(_supportedCommands);
		}
		return _supportedCommands; //NOSONAR
	}
	
    private void initialize() throws AdapterException {
        IDataCursor inputCursor = null;
        IDataCursor outputCursor = null;
        IDataCursor documentCursor = null;

        try {
            InputStream xmldata = new FileInputStream(new File(IMPORT_FILE)); //NOSONAR

            IData input = IDataFactory.create(); 
            inputCursor = input.getCursor();
            IDataUtil.put(inputCursor, FILESTREAM, xmldata);
            inputCursor.destroy();
            IData output = IDataFactory.create();
            output = Service.doInvoke(PUBXML_FLDR, XMLSTRTONODE_SVC, input);
            outputCursor = output.getCursor();
            Object xmlnode = IDataUtil.get(outputCursor, NODE);
            outputCursor.destroy();
            output = null;

            input = IDataFactory.create();
            inputCursor = input.getCursor();
            IDataUtil.put(inputCursor, NODE, xmlnode);
            IDataUtil.put(inputCursor, MAKEARRAYS, "true");
            inputCursor.destroy();
            output = Service.doInvoke(PUBXML_FLDR, XMLNODETODOC_SVC, input);
            outputCursor = output.getCursor();
            IData document = IDataUtil.getIData(outputCursor, DOCUMENT);
            outputCursor.destroy();
            output = null;

            documentCursor = document.getCursor();
            IData pcfCommands = IDataUtil.getIData(documentCursor,PCF_COMMANDS);
            documentCursor.destroy();

            documentCursor = pcfCommands.getCursor();
            IData[] commands = null;
            Object value = IDataUtil.get(documentCursor, COMMAND);
            documentCursor.destroy();

            
            if (value instanceof IData[]) 
            	commands = (IData[]) value;
            else if (value instanceof IData) {
            	commands = new IData[1];
            	commands[0] = (IData) value;
            }
            
            if(commands != null) {
            	for(int i = 0; i < commands.length; i++) {
            		wmPCFCommandMetadata metadata = createWmPCFCommandMetadata(commands[i]);
            		_pcfCommandsMetadata.put(metadata.getCommandName(), metadata);
            	}
            }

        } catch (AdapterException ae) {
            throw wmMQAdapter.getInstance().createAdapterException(1005, new String[] {IMPORT_FILE, ae.getMessage()}, ae);
        } catch (Exception e) {
            throw wmMQAdapter.getInstance().createAdapterException(1005, new String[] {IMPORT_FILE, e.getMessage()}, e);
        } finally {
            if (inputCursor != null) { 
            	inputCursor.destroy(); 
            }
            if (outputCursor != null) { 
            	outputCursor.destroy(); 
            }
            if (documentCursor != null) { 
            	documentCursor.destroy(); 
            }
        }
    }

	private wmPCFCommandMetadata createWmPCFCommandMetadata(IData commandData) {

        IDataCursor commandDataCursor = commandData.getCursor();
        String name = IDataUtil.getString(commandDataCursor, NAME);
        String integerCode = IDataUtil.getString(commandDataCursor, INTEGER_CODE);

        wmPCFCommandMetadata commandMetadataData = new wmPCFCommandMetadata(name, Integer.parseInt(integerCode));

        // Parse Request Parameters
        IData requestParametersNode = IDataUtil.getIData(commandDataCursor, REQUEST_PARAMETERS);

        if(requestParametersNode != null) {
            IDataCursor nodeCursor = requestParametersNode.getCursor();
	        IData[] requestParameters = null;
	        Object value = IDataUtil.get(nodeCursor, PARAMETER);
	        nodeCursor.destroy();
	        
	        if(value != null) {
		        if (value instanceof IData[]) 
		        	requestParameters = (IData[]) value;
		        else if (value instanceof IData) {
		        	requestParameters = new IData[1];
		        	requestParameters[0] = (IData) value;
		        }
		
		        if(requestParameters != null) {
			        for(int i=0; i<requestParameters.length; i++) {
			            IDataCursor parameterCursor = requestParameters[i].getCursor();
			            String paramName = IDataUtil.getString(parameterCursor, NAME);
			            String paramIntegerCode = IDataUtil.getString(parameterCursor, INTEGER_CODE);
			            String paramType = IDataUtil.getString(parameterCursor, TYPE);
			            String paramRequired = IDataUtil.getString(parameterCursor, REQUIRED);
			            parameterCursor.destroy();
			            
			            boolean required = false;
			            if(paramRequired != null && paramRequired.equalsIgnoreCase("true")) {
			            	required = true;
			            }
			            commandMetadataData.addRequestParameter(new wmPCFParameterMetadata(paramName, 
										            									   Integer.parseInt(paramIntegerCode), 
										            									   parseParameterType(paramType),
										            									   required));
			        }
		        }
	        }
        }
        
        // Parse Response Parameters
        IData responseParametersNode = IDataUtil.getIData(commandDataCursor, RESPONSE_PARAMETERS);
        
        if(responseParametersNode != null) {
        	IDataCursor nodeCursor = responseParametersNode.getCursor();
        	IData[] responseParameters = null;
        	Object value = IDataUtil.get(nodeCursor, PARAMETER);
	        nodeCursor.destroy();
	        
	        if(value != null) {
		        if (value instanceof IData[]) 
		        	responseParameters = (IData[]) value;
		        else if (value instanceof IData) {
		        	responseParameters = new IData[1];
		        	responseParameters[0] = (IData) value;
		        }
		
		        if(responseParameters != null) {
			        for(int i=0; i<responseParameters.length; i++) {
			            IDataCursor parameterCursor = responseParameters[i].getCursor();
			            String paramName = IDataUtil.getString(parameterCursor, NAME);
			            String paramIntegerCode = IDataUtil.getString(parameterCursor, INTEGER_CODE);
			            String paramType = IDataUtil.getString(parameterCursor, TYPE);
			            parameterCursor.destroy();
			            
			            commandMetadataData.addResponseParameter(new wmPCFParameterMetadata(paramName, 
										            										Integer.parseInt(paramIntegerCode), 
										            										parseParameterType(paramType)));
			        }
		        }
	       }
        }
        
        commandDataCursor.destroy();
        
		return commandMetadataData;
	}

	private int parseParameterType(String paramType) {
		int type = 0;
		if(paramType.equalsIgnoreCase("string")) {
			type = wmPCFParameterMetadata.TYPE_STRING;
		}
		else if(paramType.equalsIgnoreCase("string[]")) {
			type = wmPCFParameterMetadata.TYPE_STRING_ARRAY;
		}
		else if(paramType.equalsIgnoreCase("int")) {
			type = wmPCFParameterMetadata.TYPE_INTEGER;
		}
		else if(paramType.equalsIgnoreCase("int[]")) {
			type = wmPCFParameterMetadata.TYPE_INTEGER_ARRAY;
		}
		return type;
	}
}
