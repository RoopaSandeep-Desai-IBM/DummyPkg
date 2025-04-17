/*
 * Copyright 2012 by Software AG
 *
 * Uhlandstrasse 12, D-64297 Darmstadt, GERMANY
 *
 * All rights reserved
 *
 * This software is the confidential and proprietary
 * information of Software AG ('Confidential Information').
 * You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license
 * agreement you entered into with Software AG or its distributors.
 */
package com.wm.adapter.wmmqadapter.connection;

/**
 * @ author Murugavel
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import com.ibm.mq.MQException;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
import com.wm.pkg.art.isproxy.Server;
import com.wm.pkg.art.ssl.CertStoreHandler;
import com.wm.pkg.art.ssl.CertStoreManager;
import com.wm.security.keystore.ISKeyStoreAccessorUtil;
import com.wm.security.keystore.ISKeyStoreConstants;
import com.wm.security.keystore.ISKeyStoreManager;
import com.wm.security.keystore.ISKeyStoreWrapper;
import com.wm.security.keystore.ISKeyStoresHelper;
import com.wm.util.ServerException;

public class wmMQKeyStoreManager {

	final String DEFAULT_KEYSTORE_TYPE = "jks";
	String KEYSTORE_PART = "_ks";
	String TRUSTSTORE_PART = "_ts";
	String keyStoreHandle;

	File keyStoreFile = null;
	String keyStorePwd;
	String keyStoreType;
	String trustStoreHandle;
	File trustStoreFile = null;
	String trustStorePwd;
	String trustStoreType;
	public static final String WATT_SERVER_KEYSTORE_CONFIG_DIR_PROP = "watt.security.keyStore.configDir";

	public void init() throws ServerException, IOException {
		if (keyStoreHandle == null || keyStoreHandle.length() == 0||trustStoreHandle == null || trustStoreHandle.length() == 0) {
			log(ARTLogger.INFO, 5011, null,null );
		}
		initKeyStore();
		initTrustStore();
	}

	public void initKeyStore() throws ServerException, IOException {

		ISKeyStoreManager isKeyStrMgr = ISKeyStoreManager.getInstance();
		if (!isKeyStrMgr.isStoresLoaded) {
			isKeyStrMgr.getKeyStore(keyStoreHandle);
		}
		keyStorePwd = getPassword(keyStoreHandle, KEYSTORE_PART);
		IData keyStoreData = isKeyStrMgr.getKeyStoreAsIData(keyStoreHandle);
		if (keyStoreData != null) {
			IDataCursor idc = keyStoreData.getCursor();
			String filePath = IDataUtil.getString(idc,
					ISKeyStoreConstants.KEY_STORE_LOCATION);
			if (filePath != null) {
				keyStoreFile = new File(filePath);
			}
			keyStoreType = IDataUtil.getString(idc,
					ISKeyStoreConstants.KEY_STORE_TYPE);
		}
		if (keyStoreType == null || keyStoreType.length() == 0) {
			keyStoreType = DEFAULT_KEYSTORE_TYPE;
		}

	}
	
	/*
	 * This method gets the password from the file which IS generates when keystore and trustore alias are set 
	 */
	private String getPassword(String alias, String keystorePart) throws IOException {
		String KEYSTORE_CONFIG_FILE_NAME_SUFFIX = "_config.xml";
		
		File keyStoreDir;
		ISKeyStoresHelper ksHelper = ISKeyStoresHelper.getInstance();
		String KEYSTORE_CONFIG_DIR = com.wm.util.Config.getProperty("."
				+ File.separator + "config" + File.separator + "security"
				+ File.separator + "keystore",
				WATT_SERVER_KEYSTORE_CONFIG_DIR_PROP);
		String rootDir = System.getProperty("watt.app.dir");
		if (rootDir != null) {
			keyStoreDir = new File(rootDir, KEYSTORE_CONFIG_DIR);
		} else {
			keyStoreDir = new File(KEYSTORE_CONFIG_DIR);
		}
		String keyStoreConfigDirName = keyStoreDir.getCanonicalPath();

		String configFileName = keyStoreConfigDirName + File.separator
				+ alias.toLowerCase() + keystorePart
				+ KEYSTORE_CONFIG_FILE_NAME_SUFFIX;
		File configFile = new File(configFileName);
		ISKeyStoreWrapper ks = ksHelper.getKeyStoreWrapper(configFileName);
		if (ks == null && configFile.exists()) {
			ks = ksHelper.load(configFile);
		}
		if(ks != null) {
		if(ks.getPassword()!=null){
			return ks.getPassword().toString();
		}else{
			return null;
		}
		}else {
			return null;
		}
	}

	public void initTrustStore() throws ServerException, IOException {

		ISKeyStoreManager isKeyStrMgr = ISKeyStoreManager.getInstance();
		if (!isKeyStrMgr.isStoresLoaded) {

			isKeyStrMgr.getTrustStore(trustStoreHandle);
		}
		trustStorePwd = getPassword(trustStoreHandle, TRUSTSTORE_PART);
		IData keyStoreData = isKeyStrMgr.getTrustStoreAsIData(trustStoreHandle);
		if (keyStoreData != null) {
			IDataCursor idc = keyStoreData.getCursor();
			String filePath = IDataUtil.getString(idc,
					ISKeyStoreConstants.KEY_STORE_LOCATION);
			if (filePath != null) {
				trustStoreFile = new File(filePath);
			}
			trustStoreType = IDataUtil.getString(idc,
					ISKeyStoreConstants.KEY_STORE_TYPE);
		}
		if (trustStoreType == null || trustStoreType.length() == 0) {
			trustStoreType = DEFAULT_KEYSTORE_TYPE;
		}
	}

	public String getKeyStoreType() {
		return keyStoreType;
	}

	public void setKeyStoreType(String keyStoreType) {
		this.keyStoreType = keyStoreType;
	}

	public String getTrustStoreType() {
		return trustStoreType;
	}

	public void setTrustStoreType(String trustStoreType) {
		this.trustStoreType = trustStoreType;
	}

	public File getKeyStoreFile() {
		return keyStoreFile;
	}

	public File getTrustStoreFile() {
		return trustStoreFile;
	}

	public String getKeyStorePwd() {
		return keyStorePwd;
	}

	public String getTrustStorePwd() {
		return trustStorePwd;
	}

	public String getKeyStoreHandle() {
		return keyStoreHandle;
	}

	public String getTrustStoreHandle() {
		return trustStoreHandle;
	}

	public void setKeyStoreHandle(String keyStoreHandle) {
		this.keyStoreHandle = keyStoreHandle;
	}

	public void setTrustStoreHandle(String trustStoreHandle) {
		this.trustStoreHandle = trustStoreHandle;
	}

	public KeyStore getKeyStore() throws AdapterException{
		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance(keyStoreType);
			String filePath = keyStoreFile.getAbsolutePath();
			ks.load(new FileInputStream(filePath), keyStorePwd.toCharArray());
		} catch (IOException e) {
			log(ARTLogger.DEBUG, 5012, null,null );
			throw wmMQAdapter.getInstance().createAdapterException(5012, e);
		} catch (KeyStoreException e) {
			log(ARTLogger.DEBUG, 5013, null,null );
			throw wmMQAdapter.getInstance().createAdapterException(5013, e);
		} catch (NoSuchAlgorithmException e) {
			log(ARTLogger.DEBUG, 5011, null,null );
		} catch (CertificateException e) {
			log(ARTLogger.DEBUG, 5014, null,null );
			throw wmMQAdapter.getInstance().createAdapterException(5013, e);
		}
		
		return ks;

	}

	// wmio ssl
	public KeyStore getKeyStore(String pkgName, wmMQConnectionFactory connFactory) throws AdapterException{
		KeyStore ks = null;
		try {
			if (keyStoreType == null || keyStoreType.length() == 0) {
				keyStoreType = DEFAULT_KEYSTORE_TYPE;
			}
			ks = KeyStore.getInstance(keyStoreType);
			CertStoreHandler keyStore = CertStoreManager.getKeyStoreHandler(pkgName, connFactory.getSslKeyStoreAlias());
			String filePath = keyStore.getStoreFile().getAbsolutePath();
			String pwd = keyStore.getStorePassword();
			ks.load(new FileInputStream(filePath), pwd.toCharArray());
		
		} catch (IOException e) {
			log(ARTLogger.DEBUG, 5012, null,null );
			throw wmMQAdapter.getInstance().createAdapterException(5012, e);
		} catch (KeyStoreException e) {
			log(ARTLogger.DEBUG, 5013, null,null );
			throw wmMQAdapter.getInstance().createAdapterException(5013, e);
		} catch (NoSuchAlgorithmException e) {
			log(ARTLogger.DEBUG, 5011, null,null );
		} catch (CertificateException e) {
			log(ARTLogger.DEBUG, 5014, null,null );
			throw wmMQAdapter.getInstance().createAdapterException(5013, e);
		} catch (com.wm.pkg.art.error.ServerException e) {
			log(ARTLogger.DEBUG, 5014, null,null );
			e.printStackTrace();
			throw wmMQAdapter.getInstance().createAdapterException(5013, e);
		}
		
		return ks;

	}

	public KeyStore getTrustStore() throws AdapterException {
		KeyStore ts = null;
		try {
			ts = KeyStore.getInstance(trustStoreType);
			String filePath = trustStoreFile.getAbsolutePath();
			ts.load(new FileInputStream(filePath), trustStorePwd.toCharArray());
		} catch (IOException e) {
			log(ARTLogger.DEBUG, 5012, null,null );
			throw wmMQAdapter.getInstance().createAdapterException(5012, e);
		} catch (KeyStoreException e) {
			log(ARTLogger.INFO, 5013, null,null );
			throw wmMQAdapter.getInstance().createAdapterException(5013, e);
		} catch (NoSuchAlgorithmException e) {
			log(ARTLogger.DEBUG, 5011, null,null );
		} catch (CertificateException e) {
			log(ARTLogger.DEBUG, 5014, null,null );
			throw wmMQAdapter.getInstance().createAdapterException(5013, e);
		}
		return ts;
	}

	// wmio ssl
	public KeyStore getTrustStore(String pkgName, wmMQConnectionFactory connFactory) throws AdapterException {
		KeyStore ts = null;
		
		try {
			if (trustStoreType == null || trustStoreType.length() == 0) {
				trustStoreType = DEFAULT_KEYSTORE_TYPE;
			}
			ts = KeyStore.getInstance(trustStoreType);
			CertStoreHandler keyStore = CertStoreManager.getTrustStoreHandler(pkgName, connFactory.getSslTrustStoreAlias());
			String filePath = keyStore.getStoreFile().getAbsolutePath();
			String pwd = keyStore.getStorePassword();
			ts.load(new FileInputStream(filePath), pwd.toCharArray());
		
		} catch (IOException e) {
			log(ARTLogger.DEBUG, 5012, null,null );
			throw wmMQAdapter.getInstance().createAdapterException(5012, e);
		} catch (KeyStoreException e) {
			log(ARTLogger.INFO, 5013, null,null );
			throw wmMQAdapter.getInstance().createAdapterException(5013, e);
		} catch (NoSuchAlgorithmException e) {
			log(ARTLogger.DEBUG, 5011, null,null );
		} catch (CertificateException e) {
			log(ARTLogger.DEBUG, 5014, null,null );
			throw wmMQAdapter.getInstance().createAdapterException(5013, e);
		} catch (com.wm.pkg.art.error.ServerException e) {
			log(ARTLogger.DEBUG, 5014, null,null ); //service exception new type
			throw wmMQAdapter.getInstance().createAdapterException(5013, e);
		}
		return ts;
	}
	
	protected void log(int level, int minor, String arg0, String arg1)
    {
        ARTLogger logger = ( (wmMQAdapter) wmMQAdapter.getInstance()).getLogger();
        if (logger == null)
        {
            System.out.println("Logger is null");
            return;
        }
        
        if (wmMQAdapter.getLogLevelOverrides().containsKey("" + minor))
        	level =  Integer.parseInt((String)wmMQAdapter.getLogLevelOverrides().get("" + minor)) - ARTLogger.DEBUG;       	
       
       String[] args = new String[2];
        args[0] = arg0;
        args[1] = arg1;
       
        logger.logDebugPlus(level, minor, args);
    }
}
