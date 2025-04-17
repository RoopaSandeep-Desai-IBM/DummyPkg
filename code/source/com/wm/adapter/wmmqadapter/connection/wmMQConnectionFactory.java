/*
 * wmMQConnectionFactory.java
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

package com.wm.adapter.wmmqadapter.connection;

import com.wm.adapter.wmmqadapter.service.Get;
import com.wm.adapter.wmmqadapter.service.PCFCommand;
import com.wm.adapter.wmmqadapter.service.Peek;
import com.wm.adapter.wmmqadapter.service.Put;
import com.wm.adapter.wmmqadapter.service.RequestReply;
import com.wm.adapter.wmmqadapter.service.InquireQueueManager;
import com.wm.adapter.wmmqadapter.service.wmMQMQMD;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adk.connection.WmManagedConnection;
import com.wm.adk.connection.WmManagedConnectionFactory;
import com.wm.adk.error.AdapterException;
import com.wm.adk.info.ResourceAdapterMetadataInfo;
import com.wm.adk.log.ARTLogger;
import com.wm.adk.metadata.WmDescriptor;


import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import javax.print.DocFlavor.STRING;
import javax.resource.ResourceException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * This class Represents the wmAdapter's non-transactional connection factory.
 */

public class wmMQConnectionFactory extends WmManagedConnectionFactory
{

    /* The Queue Manager's name. This is an optional property. If this property is not
     * specified, the default Queue Manager on the local machine will be identified
     * at runtime, and used.
     */
    protected String _queueManagerName = "";
    
    protected String sslOptions="";
     
    protected String ccdtFilePath="";

   	public String getCcdtFilePath() {
		return ccdtFilePath;
	}

	public void setCcdtFilePath(String ccdtFilePath) {
		this.ccdtFilePath = ccdtFilePath;
	}

	public String getSslOptions() {
		return sslOptions;
	}

	public void setSslOptions(String sslOptions) {
		this.sslOptions = sslOptions;
	}

	/* The name of the host where the Queue Manager executes This is an optional property.
     */
    protected String _hostName = "";

    /* The TCP/IP port to use to connect to this Queue Manager. This is an optional property.
     * If this property is not specified, the Queue Manager is assumed to be local, and the
     * adapter will use MQBindings mode.
     */
    protected String _port = "1414";

    /* The name of the Server Connection channel to be used when connecting to this Queue
     * Manager. This is an optional property.
     */
    protected String _channel = "";

    /* The default Code Page for this Queue Manager. This is an optional property.
     */
    private String _ccsid = "CP819";

    /* The default encoding. This is an optional property.
     */
    private String _encoding = wmMQMQMD.encodingsForDisplay[0]; //default is 00273-Native

    /* The name of a Java class that will be used as a Send exit. This is an optional property.
     * If specified, this class will be loaded, and its sendExit() method will be invoked by
     * the wmMQ Server before each message is sent.
     */
    private String _sendExitClass = "";

    /* Initialization parameters to be passed to the Send Exit. This is an optional property.
     * If _sendExitClass is not specified, this property is ignored.
     */
    private String _sendExitInit = "";

    /* The name of a Java class that will be used as a Receive exit. This is an optional property.
     * If specified, this class will be loaded, and its recvExit() method will be invoked by
     * the wmMQ Server after each message is received.
     */
    private String _recvExitClass = "";

    /* Initialization parameters to be passed to the Receive Exit. This is an optional property.
     * If _recvExitClass is not specified, this property is ignored.
     */
    private String _recvExitInit = "";

    /* The name of a Java class that will be used as a Security exit. This is an optional property.
     * If specified, this class will be loaded, and its securityExit() method will be invoked by
     * the wmMQ Server during connection processing
     */
    private String _securityExitClass = "";

    /* Initialization parameters to be passed to the Security Exit. This is an optional property.
     * If _securityExitClass is not specified, this property is ignored.
     */
    private String _securityExitInit = "";

    /* The userid to be used when connecting to this Queue Manager. This is an optional property.
     */
    private String _userid = "";

    /* The password to be used when connecting to this Queue Manager. This is an optional property.
     * If _userid is not specified, this property is ignored.
     */
    private String _password = "";

    /* The name of the queue. This is a required field
     */
    private String _queueName = "";

    /* The name of the queue. This is a required field
     */
    private String _dynamicQueuePreifx = "";

	//Each connFactory will have its own cache of override connections
	private Hashtable _overrideConnections = new Hashtable();

//  Trax 1-QPSPQ - SSL Support

	/* The file name of the SSL Keystore
     */
    protected String _sslKeyStore = "";

    /* The password associated with the SSL KeyStore
     */
    protected String _sslKeyStorePassword = "";
	
	protected String _sslKeyStoreAlias="";
	
	protected String _sslTrustStoreAlias="";

    /* The password associated with the SSL KeyStore
     */
    protected String _sslCipherSpec = "";

    /* HashMap for mapping the Cipher specs (what the user sees) to Cipher suites (What MQ needs)
     */
    private static HashMap<String, String> _Specs2Suites = new HashMap<String, String>();

    //  Trax 1-QPSPQ - SSL Support

    // Flag to cache the overridden connections
    
    private static final String CONFIG_FILE = File.separator+"packages" + File.separator + 
    											"WmMQAdapter" + File.separator + 
    											"config" + File.separator + 
    											"config.xml";	
    protected boolean _cacheOverriddenConnections = wmMQAdapter.getCacheOverriddenConnections();
    
    public wmMQConnectionFactory()
    {
        super();
        log(ARTLogger.INFO, 1001, "wmMQConnectionFactory:constructor", "");
		log(ARTLogger.INFO, 1003, "wmMQConnectionFactory:createManagedConnectionObject",
								  "_queueManagerName = "  + _queueManagerName +
								  ", _hostName = " + _hostName +
								  ", _port = " + _port +
								  ", _channel = " + _channel);

//      Trax 1-QPSPQ - SSL Support
		if (wmMQAdapter.getSSLListing().trim().length() > 0 && (_Specs2Suites.size() == 0))
    	{
			String root = com.wm.util.Config.getUserDir();
			Document doc = parseFile(new File(root+CONFIG_FILE)); 
			if(doc!=null){
				// wmio ssl
				if(wmMQAdapterConstant.IBMMQ_IN_ILIVE || wmMQAdapterConstant.IBMMQ_IN_ORIGIN){
					NodeList nodeList = doc.getElementsByTagName("AllCipherSpec");
					populateCipherProperties(nodeList);
				}
				else {
				String prop1 = System.getProperty("watt.WmMQAdapter.Connection.CiphersList");
				if(prop1!=null && prop1.equalsIgnoreCase("AllCipherSuite")){
					NodeList nodeList = doc.getElementsByTagName("AllCipherSuite");
					populateCipherSuite(nodeList);
				}else if(prop1!=null && prop1.equalsIgnoreCase("AllCipherSpec")){
					NodeList nodeList = doc.getElementsByTagName("AllCipherSpec");
					populateCipherProperties(nodeList);
				}else{
					if(prop1!=null && prop1.equalsIgnoreCase("TLS")){
						NodeList nodeList = doc.getElementsByTagName("TLS");
						populateCipherProperties(nodeList);				
						//tls
					}else{
						//SSL
						NodeList nodeList = doc.getElementsByTagName("SSL");
						populateCipherProperties(nodeList);
					}
				}
			 }
			}
    	}else if (wmMQAdapter.getSSLSupport() && (_Specs2Suites.size() == 0))
    	{
    		String root = com.wm.util.Config.getUserDir();
    		Document doc = parseFile(new File(root+CONFIG_FILE)); 
    		if(doc!=null){
    			String prop1 = System.getProperty("watt.WmMQAdapter.ShowCipherSuit");
    			if(prop1!=null && prop1.equalsIgnoreCase("true")){
    				NodeList nodeList = doc.getElementsByTagName("AllCipherSuite");
    				populateCipherSuite(nodeList);
    			}else{
    				String prop = System.getProperty("com.ibm.mq.cfg.useIBMCipherMappings");
    				if(prop!=null && prop.equalsIgnoreCase("false")){
    					NodeList nodeList = doc.getElementsByTagName("TLS");
    					populateCipherProperties(nodeList);				
    					//tls
    				}else{
    					//SSL
    					NodeList nodeList = doc.getElementsByTagName("SSL");
    					populateCipherProperties(nodeList);
    				}
    			}
    		}
    		//    		for (int i = 0 ; i < cipherSpecs.length ; i++)
    		//    			_Specs2Suites.put(cipherSpecs[i],cipherSuites[i]);
    	}
//      Trax 1-QPSPQ - SSL Support
    }

    public void populateCipherProperties(NodeList nodeList){
    	for (int i = 0; i < nodeList.getLength(); i++) {
			   Node node = nodeList.item(i);
			  NodeList childNode=  node.getChildNodes();
			   for(int j=0;j<childNode.getLength();j++){
				  Node child =  childNode.item(j);
				  if(child.getAttributes()!=null){
					 NamedNodeMap map =  child.getAttributes();
					 if(map.getLength()==2){
						if( map.item(0).getNodeName().equals("cipherSpec")&&map.item(1).getNodeName().equals("cipherSuit")){
							_Specs2Suites.put(map.item(0).getNodeValue(),map.item(1).getNodeValue());
						}
					 }
				  }
			   }  
		   }
    	if(_Specs2Suites!=null){
    		cipherSpecs = new String[_Specs2Suites.size()];
    		cipherSuites = new String[_Specs2Suites.size()];
    		int i=0;
    		for (Map.Entry<String, String> entry : _Specs2Suites.entrySet()) {
    	         cipherSpecs[i] = (String)entry.getKey();  // Get the key from the entry.
    	         cipherSuites[i] = (String)entry.getValue();  // Get the value.
    	         i++;
    		}
//    		for(int i=0;i<_Specs2Suites.size();i++){
//    			cipherSpecs[i]= _Specs2Suites.keySet();
//    		}
    	}
    }
    
    public void populateCipherSuite(NodeList nodeList){
    	for (int i = 0; i < nodeList.getLength(); i++) {
			   Node node = nodeList.item(i);
			  NodeList childNode=  node.getChildNodes();
			   for(int j=0;j<childNode.getLength();j++){
				  Node child =  childNode.item(j);
				  if(child.getAttributes()!=null){
					 NamedNodeMap map =  child.getAttributes();
					 if(map.getLength()==1){
						if( map.item(0).getNodeName().equals("cipherSuit")){
							_Specs2Suites.put(map.item(0).getNodeValue(),map.item(0).getNodeValue());
						}
					 }
				  }
			   }  
		   }
    	if(_Specs2Suites!=null){
    		cipherSpecs = new String[_Specs2Suites.size()];
    		cipherSuites = new String[_Specs2Suites.size()];
    		int i=0;
    		for (Map.Entry<String, String> entry : _Specs2Suites.entrySet()) {
    	         cipherSpecs[i] = entry.getKey();  // Get the key from the entry.
    	         cipherSuites[i] = entry.getValue();  // Get the value.
    	         i++;
    		}
//    		for(int i=0;i<_Specs2Suites.size();i++){
//    			cipherSpecs[i]= _Specs2Suites.keySet();
//    		}
    	}
    }
    /*
     * Returns a connection to the resource.
     *
     * The subject parameter is not used in this release.
     * The cxRequestInfo parameter is not used in this release.
     * Returns an object of Tutorial2Connection that represents a connection to the resource.
     *
     * Throws AdapterConnectionException if an error occurred while creating a connection to the resource.
     */
    public WmManagedConnection createManagedConnectionObject(
                               javax.security.auth.Subject subject,
                               javax.resource.spi.ConnectionRequestInfo connectionRequestInfo)
        throws AdapterException
    {
        log(ARTLogger.INFO, 1001, "wmMQConnectionFactory:createManagedConnectionObject", "");
        log(ARTLogger.INFO, 1003, "wmMQConnectionFactory:createManagedConnectionObject",
                                  "_queueManagerName = "  + _queueManagerName +
                                  ", _hostName = " + _hostName +
                                  ", _port = " + _port +
                                  ", _channel = " + _channel);
//        Exception eeee = new Exception ("wmMQConnectionFactory:createManagedConnectionObject call stack");
//        eeee.printStackTrace();

        return new wmMQConnection();
    }

    public Document parseFile(File sourceFile) {
       // log(level, minor, arg0, arg1)("Parsing XML file... " + sourceFile.getAbsolutePath());
        DocumentBuilder docBuilder;
        Document doc = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance(); //NOSONAR
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            //log("Wrong parser configuration: " + e.getMessage());
            return null;
        }

        try {
            doc = docBuilder.parse(sourceFile);
        }
        catch (SAXException e) {
          //  log("Wrong XML file structure: " + e.getMessage());
            return null;
        }
        catch (IOException e) {
          //  log("Could not read source file: " + e.getMessage());
        }
       // log("XML file parsed");
        return doc;
    }


    /*
     * Returns the types of transactions that are supported by the connections
     * that are created by this factory. There is no transaction support for
     * wmMQAdapter because of the limitations of the flat-file database provided with wmMQAdapter.
     * Returns a flag indicating which transaction types are supported by this connection.
     */

    public int queryTransactionSupportLevel()
	{
		return NO_TRANSACTION_SUPPORT;
    }
    
//  Trax 1-QPSPQ - SSL Support

    /* Convert the CipherSpec name into the CipherSuite name
     */
    public static String suite2Spec(String suite)
    {
    	return (String)_Specs2Suites.get(suite);
    }
    
    /*
     * Gets the keyStore file name.
     */
    public String getSslKeyStore()
    {
        return this._sslKeyStore;
    }

    /*
     * Sets the keyStore file name.
     *
     * The name parameter is the keyStore file name.
     */
    public void setSslKeyStore(String name)
    {
    	if(name == null || (name!=null && name.equals(""))){
    		name = " ";
    	}
        this._sslKeyStore = name;
    }

    /*
     * Gets the keyStorePassword.
     */
    public String getSslKeyStorePassword()
    {
        return this._sslKeyStorePassword;
    }

    /*
     * Sets the keyStorePassword name.
     *
     * The password parameter is the keyStorePassword.
     */
    public void setSslKeyStorePassword(String password)
    {
    	if(password == null || (password!=null && password.equals(""))){
    		password = " ";
    	}
        this._sslKeyStorePassword = password;
    }

    
    public String getSslKeyStoreAlias () {
    	return this._sslKeyStoreAlias;
    }
    
    public void setSslKeyStoreAlias(String keyStoreAlias) {
        
    	if (keyStoreAlias == null || (keyStoreAlias !=null && keyStoreAlias.equals(""))) {
    		keyStoreAlias=" ";
    	}
    	this._sslKeyStoreAlias = keyStoreAlias;
    }
    
    public String getSslTrustStoreAlias() {
    	return this._sslTrustStoreAlias;
    }
    
    
    public void setSslTrustStoreAlias(String trustStoreAlias) {
    	if (trustStoreAlias == null || (trustStoreAlias !=null && trustStoreAlias.equals(""))) {
    		trustStoreAlias=" ";
    	}
    	this._sslTrustStoreAlias = trustStoreAlias;
    	
    }
    
    /*
     * Gets the SSL Cipher Spec name.
     */
    public String getSslCipherSpec()
    {
        return this._sslCipherSpec;
    }

    /*
     * Sets the SSL Cipher Spec name.
     *
     * The name parameter is the SSL Cipher Spec name.
     */
    public void setSslCipherSpec(String name)
    {
    	if(name == null || (name!=null && name.equals(""))){
    		name = " ";
    	}    
        this._sslCipherSpec = name;
    }
//  Trax 1-QPSPQ - SSL Support

	public boolean isCacheOverriddenConnections() {
		return this._cacheOverriddenConnections;
	}

	public void setCacheOverriddenConnections(boolean cacheOverriddenConnections) {
		this._cacheOverriddenConnections = cacheOverriddenConnections;
	}

    /*
     * Gets the Queue Manager name.
     */
    public String getQueueManagerName()
    {
        return this._queueManagerName;
    }

    /*
     * Sets the Queue Manager name.
     *
     * The name parameter is the Queue Manager name.
     */
    public void setQueueManagerName(String name)
    {
        this._queueManagerName = name;
    }

    /*
     * Gets the Host name.
     */
    public String getHostName()
    {
        return this._hostName;
    }

    /*
     * Sets the Host name.
     *
     * The name parameter is the Host name.
     */
    public void setHostName(String name)
    {
        this._hostName = name;
    }

    /*
     * Gets the port number.
     */
    public String getPort()
    {
        return this._port;
    }

    /*
     * Sets the port number.
     *
     * The port parameter is the port number.
     */
    public void setPort(String port)
    {
        this._port = port;
    }

    /*
     * Gets the Server Connection Channel name.
     */
    public String getChannel()
    {
        return this._channel;
    }

    /*
     * Sets the Server Connection Channel name.
     *
     * The name parameter is the Channel name.
     */
    public void setChannel(String name)
    {
        this._channel = name;
    }

    /*
     * Gets the CCSID.
     */
    public String getCCSID()
    {
        return this._ccsid;
    }

    /*
     * Sets the CCSID.
     *
     * The ccsid parameter is the CCSID.
     */
    public void setCCSID(String ccsid)
    {
        this._ccsid = ccsid;
    }

    /*
     * Gets the Encoding.
     */
    public String getEncoding()
    {
        return this._encoding;
    }

    /*
     * Sets the Encoding.
     *
     * The encoding parameter is the Encoding.
     */
    public void setEncoding(String encoding)
    {
        this._encoding = encoding;
    }
    
    /*
     * Gets the Send Exit name.
     */
    public String getSendExit()
    {
        return this._sendExitClass;
    }

    /*
     * Sets the Send Exit name.
     *
     * The name parameter is the Send Exit name.
     */
    public void setSendExit(String name)
    {
        this._sendExitClass = name;
    }

    /*
     * Gets the Send Exit Initialization parms.
     */
    public String getSendExitInit()
    {
        return this._sendExitInit;
    }

    /*
     * Sets the Send Exit Initialization parms.
     *
     * The init parameter is the Send Exit Initialization parms.
     */
    public void setSendExitInit(String init)
    {
        this._sendExitInit = init;
    }

    /*
     * Gets the Recv Exit name.
     */
    public String getRecvExit()
    {
        return this._recvExitClass;
    }

    /*
     * Sets the Recv Exit name.
     *
     * The name parameter is the Recv Exit name.
     */
    public void setRecvExit(String name)
    {
        this._recvExitClass = name;
    }

    /*
     * Gets the Recv Exit Initialization parms.
     */
    public String getRecvExitInit()
    {
        return this._recvExitInit;
    }

    /*
     * Sets the Recv Exit Initialization parms.
     *
     * The init parameter is the Recv Exit Initialization parms.
     */
    public void setRecvExitInit(String init)
    {
        this._recvExitInit = init;
    }

    /*
     * Gets the Security Exit name.
     */
    public String getSecurityExit()
    {
        return this._securityExitClass;
    }

    /*
     * Sets the Security Exit name.
     *
     * The name parameter is the Send Exit name.
     */
    public void setSecurityExit(String name)
    {
        this._securityExitClass = name;
    }

    /*
     * Gets the Security Exit Initialization parms.
     */
    public String getSecurityExitInit()
    {
        return this._securityExitInit;
    }

    /*
     * Sets the Security Exit Initialization parms.
     *
     * The init parameter is the Security Exit Initialization parms.
     */
    public void setSecurityExitInit(String init)
    {
        this._securityExitInit = init;
    }

    /*
     * Gets the Userid.
     */
    public String getUserId()
    {
        return this._userid;
    }

    /*
     * Sets the Userid.
     *
     * The id parameter is the Userid.
     */
    public void setUserId(String id)
    {
        this._userid = id;
    }

    /*
     * Gets the password.
     */
    public String getPassword()
    {
        return this._password;
    }

    /*
     * Sets the password.
     *
     * The password parameter is the password.
     */
    public void setPassword(String password)
    {
        this._password = password;
    }

    /*
     * Gets the Queue name.
     */
    public String getQueueName()
    {
        return this._queueName;
    }

    /*
     * Sets the Queue name.
     *
     * The name parameter is the Queue name.
     */
    public void setQueueName(String name)
    {
        this._queueName = name;
    }

    /*
     * Gets the prefix for dynamic queue names.
     */
    public String getDynamicQueuePrefix()
    {
        return this._dynamicQueuePreifx;
    }

    /*
     * Sets the prefix for dynamic queue names.
     *
     * The name parameter is the prefix for dynamic queue names.
     */
    public void setDynamicQueuePrefix(String prefix)
    {
        this._dynamicQueuePreifx = prefix;
    }

	public wmMQConnection findConnection(String qmgrname, String qname)
	{
        //Trax 1-S15OV  & 1-RZDD0 - It is possible for multiple threads to 
		//override a connection with the same properties, resulting in 
		//multiple connection objects with the same name. Storing them directly
		//in a hashtable strands all but the last connection. Instead, use a 
		//Stack.
		log(ARTLogger.INFO, 1001, "wmMQConnectionFactory:findConnection", "");
		wmMQConnection conn = null;
		
		if (_overrideConnections != null)
		{
			synchronized(_overrideConnections){ //	  Trax 1-VF52N            
			Stack connsForThisQueue = (Stack)_overrideConnections.get(qmgrname + "__" + qname);
			if (connsForThisQueue != null)
			{
				//Loop through the stack to find a connection not in use
				for (int c = 0 ; c < connsForThisQueue.size() ; c++)
				{
					conn = (wmMQConnection)connsForThisQueue.pop();
					if (conn.inUse())
					{
						log(ARTLogger.INFO, 1003, "wmMQConnectionFactory:findConnection", "conn in use " + conn.toString());
			            // Trax 1-14WOV9 : Begin 
//						connsForThisQueue.add(conn); //Put conn back on stack
						connsForThisQueue.add(0, conn); //Put conn back at the end of the stack
			            // Trax 1-14WOV9 : End
						conn = null;
					}
					else
					{
						log(ARTLogger.INFO, 1003, "wmMQConnectionFactory:findConnection", "conn available " + conn.toString());
						conn.setInUse(true);
			            // Trax 1-14WOV9 : Begin 
//						connsForThisQueue.add(conn); //Put conn back on stack
						connsForThisQueue.add(0, conn); //Put conn back at the end of the stack
			            // Trax 1-14WOV9 : End
						break;
					}
				}
			}
			else
				log(ARTLogger.INFO, 1003, "wmMQConnectionFactory:findConnection", "connsForThisQueue is null");
			}//	  END OF SYNC Trax 1-VF52N
		}
		else
			log(ARTLogger.INFO, 1003, "wmMQConnectionFactory:findConnection", "_overrideConnections is null");

//		log(ARTLogger.CRITICAL, 1003, "wmMQConnectionFactory:findConnection", "returning " + conn);
		log(ARTLogger.INFO, 1002, "wmMQConnectionFactory:findConnection", "");
		return conn;
	}

	public void cacheConnection(wmMQConnection conn, String qmgrname, String qname)
	{
        //Trax 1-S15OV  & 1-RZDD0 - It is possible for multiple threads to 
		//override a connection with the same properties, resulting in 
		//multiple connection objects with the same name. Storing them directly
		//in a hashtable strands all but the last connection. Instead, use a 
		//Stack.
		log(ARTLogger.INFO, 1001, "wmMQConnectionFactory:cacheConnection", "");
		if (_overrideConnections == null)
		_overrideConnections = new Hashtable();
		synchronized(_overrideConnections){ //	  Trax 1-VF52N 
		Stack connsForThisQueue = (Stack)_overrideConnections.get(qmgrname + "__" + qname);
		if (connsForThisQueue == null)
		{
			connsForThisQueue = new Stack();
			_overrideConnections.put(qmgrname + "__" + qname,connsForThisQueue);
		}
		
		log(ARTLogger.INFO, 1003, "wmMQConnectionFactory:cacheConnection", "caching " + conn);
		connsForThisQueue.add(conn);		
		}//	  END OF SYNC Trax 1-VF52N
		log(ARTLogger.INFO, 1002, "wmMQConnectionFactory:cacheConnection", "");
	}

	/*
	 * Cleans the cache of dynamically built connections
	 *
	 */
	public void cleanConnectioncache(wmMQConnection top)
	{
		log(ARTLogger.INFO, 1001, "wmMQConnectionFactory:cleanConnectioncache", "");
		Stack connsForOneQueue = null;
		if (_overrideConnections != null)
		{
			synchronized(_overrideConnections){ //	  Trax 1-VF52N
			for (Enumeration econns = _overrideConnections.keys() ; econns.hasMoreElements() ; ) 
			{
				String connName = (String)econns.nextElement();
				connsForOneQueue = (Stack)_overrideConnections.get(connName);
//Trax 1-XHJ94 - Stale Connections - Probably caused by NullPointerException
				if (connsForOneQueue == null)
					continue;
//Trax 1-?????  Loop if all connections are still in use.
				int conns = connsForOneQueue.size();
				while ((!connsForOneQueue.empty()) && (conns > 0))
				{
					wmMQConnection oneConn = (wmMQConnection)connsForOneQueue.pop();
					conns--;
					if (oneConn.inUse())
						connsForOneQueue.add(oneConn);
					else
						if (oneConn != top)
						{				
							try
							{
								log(ARTLogger.INFO, 1003, "wmMQConnectionFactory:cleanConnectioncache", 
										"Destroying connection for " + oneConn.getResolvedQueueManagerName() + "/" + oneConn.getQueueName());
								oneConn.destroyConnection();
							}
							catch (ResourceException re)
							{
							}
						}
				}
//Trax 1-XHJ94 - Stale Connections - Probably caused by NullPointerException
/*				
				//Double check because this code may be recursed.
				if ((connsForOneQueue != null) &&
					(connsForOneQueue.size() == 0) &&
					(_overrideConnections != null) && 
					(_overrideConnections.containsKey(connName)))
					_overrideConnections.remove(connName); 
*/
			}
			}//	  END OF SYNC Trax 1-VF52N
		}
		
		//Trax 1-NW13R - NullPointerException caused by recursion
		//_overrideConnections = null;
		
		log(ARTLogger.INFO, 1002, "wmMQConnectionFactory:cleanConnectioncache", "");
	}

    /*
     * Populates the descriptor.
     *
     * This method will be called once for each ManagedConnectionFactory.
     *
     * The d parameter is the descriptor.
     * AdapterException is thrown if the method encounters an error.
     */
    public void fillWmDescriptor(WmDescriptor d, Locale l)
        throws AdapterException
    {
        log(ARTLogger.INFO, 1001, "wmMQConnectionFactory:fillWmDescriptor", "");
        //Retrieves the i18n metadata information from the resource bundle and replaces
        //the non-localized metadata.
        //The metadata that needs to be internationalized (the parameter display name,
        //description, group name, etc.) will populate the administrative interface,
        //Adapter Service Editor, or Adapter Notification Editor.
		fillNonSSLProperties(d, l);
		
		// Fill the three SSL related properties if SSL is enabled
		fillSSLProperties(d, l);
		
        d.setDescriptions(wmMQAdapter.getInstance().getAdapterResourceBundleManager(), l);
        log(ARTLogger.INFO, 1002, "wmMQConnectionFactory:fillWmDescriptor", "");
    }
    
    protected void fillNonSSLProperties(WmDescriptor d, Locale l) throws AdapterException {
        d.createGroup(wmMQAdapterConstant.QUEUE_MANAGER_SETTINGS,
                      new String[] { wmMQAdapterConstant.CCDT_FILE_PATH,
                    		  		wmMQAdapterConstant.QUEUE_MANAGER_NAME,
                                    wmMQAdapterConstant.HOST_NAME,
                                    wmMQAdapterConstant.PORT,
                                    wmMQAdapterConstant.CHANNEL_NAME,
                                    wmMQAdapterConstant.CCSID,
                                    wmMQAdapterConstant.USERID,
                                    wmMQAdapterConstant.PASSWORD,
                                    wmMQAdapterConstant.QUEUE_NAME,
									wmMQAdapterConstant.DYNAMIC_QUEUE_PREFIX,
        							wmMQAdapterConstant.ENCODING});
		d.createGroup(wmMQAdapterConstant.QUEUE_SETTINGS,
					  new String[] {wmMQAdapterConstant.SEND_EXIT_NAME,
						wmMQAdapterConstant.SEND_EXIT_INIT,
						wmMQAdapterConstant.RECV_EXIT_NAME,
						wmMQAdapterConstant.RECV_EXIT_INIT,
						wmMQAdapterConstant.SECURITY_EXIT_NAME,
						wmMQAdapterConstant.SECURITY_EXIT_INIT});

		d.setValidValues(wmMQAdapterConstant.ENCODING, wmMQMQMD.encodingsForDisplay);
		
		d.createGroup(wmMQAdapterConstant.CONNECTION_MANAGEMENT_SETTINGS,
				  	  new String[] {wmMQAdapterConstant.CACHE_OVERRIDDEN_CONNECTIONS});
		d.setValidValues(wmMQAdapterConstant.CACHE_OVERRIDDEN_CONNECTIONS, new String[] {"true", "false"});
    	
        d.setPassword(wmMQAdapterConstant.PASSWORD);
        //d.setHidden(wmMQAdapterConstant.QUEUE_NAMES);
    }
    
    protected void fillSSLProperties(WmDescriptor d, Locale l) throws AdapterException {
//      Trax 1-QPSPQ - SSL Support
		if (wmMQAdapter.getSSLListing().trim().length() > 0 || wmMQAdapter.getSSLSupport())
		{
			d.createGroup(wmMQAdapterConstant.SSL,
					new String[] {
								  wmMQAdapterConstant.SSL_OPTIONS,
								  wmMQAdapterConstant.SSL_KEYSTORE,
 								  wmMQAdapterConstant.SSL_KEYSTORE_PASSWORD,
								  wmMQAdapterConstant.SSL_KEYSTORE_ALIAS,
								  wmMQAdapterConstant.SSL_TRUSTSTORE_ALIAS,
								  wmMQAdapterConstant.SSL_CIPHER_SPEC
								  });	

			d.setPassword(wmMQAdapterConstant.SSL_KEYSTORE_PASSWORD);

			d.setValidValues(wmMQAdapterConstant.SSL_CIPHER_SPEC, cipherSpecs);
		}
//      Trax 1-QPSPQ - SSL Support
    }

	public static wmMQConnectionFactory clone(wmMQConnectionFactory connFactoryToClone)
	{
		wmMQAdapter.log(ARTLogger.INFO, 1001, "wmMQConnectionFactory:clone", "");
		wmMQConnectionFactory connFactory = new wmMQConnectionFactory();
		
		if (connFactoryToClone != null)
		{
			connFactory.setQueueManagerName(connFactoryToClone.getQueueManagerName());
			connFactory.setHostName(connFactoryToClone.getHostName());
			connFactory.setPort(connFactoryToClone.getPort());
			connFactory.setChannel(connFactoryToClone.getChannel());
			connFactory.setCCSID(connFactoryToClone.getCCSID());
			connFactory.setQueueName(connFactoryToClone.getQueueName());
			connFactory.setDynamicQueuePrefix(connFactoryToClone.getDynamicQueuePrefix());
			connFactory.setUserId(connFactoryToClone.getUserId());
			connFactory.setPassword(connFactoryToClone.getPassword());
			connFactory.setSendExit(connFactoryToClone.getSendExit());
			connFactory.setSendExitInit(connFactoryToClone.getSendExitInit());
			connFactory.setRecvExit(connFactoryToClone.getRecvExit());
			connFactory.setRecvExitInit(connFactoryToClone.getRecvExitInit());
			connFactory.setSecurityExit(connFactoryToClone.getSecurityExit());
			connFactory.setSecurityExitInit(connFactoryToClone.getSecurityExitInit());
		}
//      Trax 1-QPSPQ - SSL Support
		if (wmMQAdapter.getSSLListing().trim().length() > 0 || wmMQAdapter.getSSLSupport())
		{
			if(connFactoryToClone != null) {
			connFactory.setSslKeyStore(connFactoryToClone.getSslKeyStore());
			connFactory.setSslKeyStorePassword(connFactoryToClone.getSslKeyStorePassword());
			
			connFactory.setSslKeyStoreAlias(connFactoryToClone.getSslKeyStoreAlias());
			connFactory.setSslTrustStoreAlias(connFactoryToClone.getSslTrustStoreAlias());
			connFactory.setSslCipherSpec(connFactoryToClone.getSslCipherSpec());
			}
		}
//      Trax 1-QPSPQ - SSL Support
		
		wmMQAdapter.log(ARTLogger.INFO, 1002, "wmMQConnectionFactory:clone", "");
		return connFactory;
	}


    /*
     * Registers the adapter service templates that this connection supports.
     *
     * The info parameter is the metadata object used to register the adapter service
     * templates supported by this connection.
     * The locale parameter is not used.
     * AdapterException is thrown if an error occurs while
     * registering the adapter service templates.
     *
     */
    public void fillResourceAdapterMetadataInfo(ResourceAdapterMetadataInfo info,
                                                Locale locale) throws AdapterException
    {
        log(ARTLogger.INFO, 1001, "wmMQConnectionFactory:fillResourceAdapterMetadataInfo", "");
        wmMQAdapter adapterInstance = (wmMQAdapter)wmMQAdapter.getInstance();
        adapterInstance.fillResourceAdapterMetadataInfo(info,locale);

        //Adds a list of adapter service templates.
        //info.addServiceTemplate(Commit.class.getName());
        info.addServiceTemplate(Get.class.getName());
        info.addServiceTemplate(Peek.class.getName());
        info.addServiceTemplate(Put.class.getName());
		info.addServiceTemplate(RequestReply.class.getName());
		info.addServiceTemplate(InquireQueueManager.class.getName());
		info.addServiceTemplate(PCFCommand.class.getName());
		//info.addServiceTemplate(Rollback.class.getName());
       log(ARTLogger.INFO, 1002, "wmMQConnectionFactory:fillResourceAdapterMetadataInfo", "");
    } //fillResourceAdapterMetadataInfo()

    protected void log(int level, int minor, String arg0, String arg1)
    {
        ARTLogger logger = ((wmMQAdapter)wmMQAdapter.getInstance()).getLogger();
        if (logger == null)
        {
            System.out.println("Logger is null");
            return;
        }
        
        //Trax 1-WVILA. Allow user to override the logging level of adapter messages. 
        if (wmMQAdapter.getLogLevelOverrides().containsKey("" + minor))
        	level =  Integer.parseInt((String)wmMQAdapter.getLogLevelOverrides().get("" + minor)) - ARTLogger.DEBUG;       	
       
        String[] args = new String[2];
        args[0] = arg0;
        args[1] = arg1;
        //Trax log 1-S4OHA - move adapter logging to DEBUG PLUS
        logger.logDebugPlus(level, minor, args);
    }

//  Trax 1-QPSPQ - SSL Support
    public static String[] cipherSpecs = null;//NOSONAR
////    		"No_Encryption",
//   	    "DES_SHA_EXPORT",
// 		"NULL_MD5",
// 		"NULL_SHA",
// 		"RC2_MD5_EXPORT",
// 		"RC4_MD5_US",
// 		"RC4_MD5_EXPORT",
// 		"RC4_SHA_US",
// 		"TRIPLE_DES_SHA_US",  
//    };
    public static String[] cipherSuites = null;//NOSONAR
////    		"",
// 		"SSL_RSA_WITH_DES_CBC_SHA",
// 		"SSL_RSA_WITH_NULL_MD5",
// 		"SSL_RSA_WITH_NULL_SHA",
// 		"SSL_RSA_EXPORT_WITH_RC2_CBC_40_MD5",
// 		"SSL_RSA_WITH_RC4_128_MD5",
// 		"SSL_RSA_EXPORT_WITH_RC4_40_MD5",
// 		"SSL_RSA_WITH_RC4_128_SHA",
// 		"SSL_RSA_WITH_3DES_EDE_CBC_SHA",
//    };
//  Trax 1-QPSPQ - SSL Support

    protected boolean idleConnectionVerificationRequired() {
		return true;
	}
}
