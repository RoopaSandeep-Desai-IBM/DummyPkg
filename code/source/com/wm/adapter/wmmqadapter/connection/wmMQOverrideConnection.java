/*
 * wmMQOverrideConnection.java
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

import java.util.Locale;

import javax.resource.ResourceException;
import javax.swing.KeyStroke;

import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adapter.wmmqadapter.admin.Service;
import com.wm.adk.cci.record.WmRecord;
import com.wm.adk.cci.record.WmRecordFactory;
import com.wm.adk.connection.WmManagedConnection;
import com.wm.adk.error.AdapterConnectionException;
import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;
import com.wm.adk.metadata.WmTemplateDescriptor;
import com.wm.data.IData;
import com.wm.data.IDataCursor;

/*
 * This class is an implementation of the lookup service template
 * for the wmMQAdapter. The lookup
 * takes a key as input, and returns the column values of the record.
 * An error is issued if there is no record for that key.
 *
 * This template demonstrates the use of combo boxes, allowing
 * a user to select catalogs, schemas, and tables.
 */
public class wmMQOverrideConnection {

	public static final String USE_LOCAL_QUEUE_MANAGER_LABEL = "useLocalQueueManager";
	public static final String QUEUE_MANAGER_NAME_LABEL = "queueManagerName";
	public static final String QUEUE_HOST_NAME_LABEL = "hostName";
	public static final String QUEUE_PORT_LABEL = "port";
	public static final String QUEUE_CHANNEL_LABEL = "channel";
	public static final String QUEUE_CCSID_LABEL = "CCSID";
	public static final String QUEUE_ENCODING_LABEL = "encoding";
	public static final String QUEUE_NAME_LABEL = "queueName";
	public static final String QUEUE_DYNAMIC_QUEUE_PREFIX_LABEL = "dynamicQueuePrefix";
	// public static final String QUEUE_USERID_LABEL = "Userid";
	// public static final String QUEUE_PASSWORD_LABEL = "Password";

	private String OVERRIDE_CONNECTION_LABEL = "overrideConnection";
	private String OVERRIDDEN_CONNECTION_LABEL = "overrideConnection";

	private String _overrideUseLocalQueueManager = OVERRIDE_CONNECTION_LABEL + "." + USE_LOCAL_QUEUE_MANAGER_LABEL;
	private String _overrideConnectionQueueManagerName = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_MANAGER_NAME_LABEL;
	private String _overrideConnectionHostName = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_HOST_NAME_LABEL;
	private String _overrideConnectionPort = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_PORT_LABEL;
	private String _overrideConnectionChannel = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_CHANNEL_LABEL;
	private String _overrideConnectionCCSID = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_CCSID_LABEL;
	private String _overrideConnectionEncoding = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_ENCODING_LABEL;
	private String _overrideConnectionQueueName = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_NAME_LABEL;
	private String _overrideConnectionDynamicQueuePrefix = OVERRIDE_CONNECTION_LABEL + "."
			+ QUEUE_DYNAMIC_QUEUE_PREFIX_LABEL;
	// private String _overrideConnectionUserid = OVERRIDE_CONNECTION_LABEL +
	// "." + QUEUE_USERID_LABEL;
	// private String _overrideConnectionPassword = OVERRIDE_CONNECTION_LABEL +
	// "." + QUEUE_PASSWORD_LABEL;

	private String _overriddenConnectionQueueManagerName = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_MANAGER_NAME_LABEL;
	private String _overriddenConnectionHostName = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_HOST_NAME_LABEL;
	private String _overriddenConnectionPort = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_PORT_LABEL;
	private String _overriddenConnectionChannel = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_CHANNEL_LABEL;
	private String _overriddenConnectionCCSID = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_CCSID_LABEL;
	private String _overriddenConnectionEncoding = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_ENCODING_LABEL;
	private String _overriddenConnectionQueueName = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_NAME_LABEL;
	private String _overriddenConnectionDynamicQueuePrefix = OVERRIDDEN_CONNECTION_LABEL + "."
			+ QUEUE_DYNAMIC_QUEUE_PREFIX_LABEL;
	// private String _overriddenConnectionUserid = OVERRIDDEN_CONNECTION_LABEL
	// + "." + QUEUE_USERID_LABEL;
	// private String _overriddenConnectionPassword =
	// OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_PASSWORD_LABEL;

	private String[] _overrideConnectionPropertyType = { "string" };

	// Service name
	private String _serviceName = "";

	/*
	 * Constructor.
	 */
	public wmMQOverrideConnection(String serviceName) {
		_serviceName = serviceName;
		setLabels();
	}

	public wmMQOverrideConnection(String serviceName, String overrideLabel, String overriddenLabel) {
		_serviceName = serviceName;
		OVERRIDE_CONNECTION_LABEL = overrideLabel;
		OVERRIDDEN_CONNECTION_LABEL = overriddenLabel;
		setLabels();
	}

	private void setLabels() {
		_overrideUseLocalQueueManager = OVERRIDE_CONNECTION_LABEL + "." + USE_LOCAL_QUEUE_MANAGER_LABEL;
		_overrideConnectionQueueManagerName = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_MANAGER_NAME_LABEL;
		_overrideConnectionHostName = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_HOST_NAME_LABEL;
		_overrideConnectionPort = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_PORT_LABEL;
		_overrideConnectionChannel = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_CHANNEL_LABEL;
		_overrideConnectionCCSID = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_CCSID_LABEL;
		_overrideConnectionEncoding = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_ENCODING_LABEL;
		_overrideConnectionQueueName = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_NAME_LABEL;
		_overrideConnectionDynamicQueuePrefix = OVERRIDE_CONNECTION_LABEL + "." + QUEUE_DYNAMIC_QUEUE_PREFIX_LABEL;
		// _overrideConnectionUserid = OVERRIDE_CONNECTION_LABEL + "." +
		// QUEUE_USERID_LABEL;
		// _overrideConnectionPassword = OVERRIDE_CONNECTION_LABEL + "." +
		// QUEUE_PASSWORD_LABEL;

		_overriddenConnectionQueueManagerName = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_MANAGER_NAME_LABEL;
		_overriddenConnectionHostName = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_HOST_NAME_LABEL;
		_overriddenConnectionPort = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_PORT_LABEL;
		_overriddenConnectionChannel = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_CHANNEL_LABEL;
		_overriddenConnectionCCSID = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_CCSID_LABEL;
		_overriddenConnectionEncoding = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_ENCODING_LABEL;
		_overriddenConnectionQueueName = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_NAME_LABEL;
		_overriddenConnectionDynamicQueuePrefix = OVERRIDDEN_CONNECTION_LABEL + "." + QUEUE_DYNAMIC_QUEUE_PREFIX_LABEL;
		// _overriddenConnectionUserid = OVERRIDDEN_CONNECTION_LABEL + "." +
		// QUEUE_USERID_LABEL;
		// _overriddenConnectionPassword = OVERRIDDEN_CONNECTION_LABEL + "." +
		// QUEUE_PASSWORD_LABEL;
	}

	public String getOverrideUseLocalQueueManager() {
		return _overrideUseLocalQueueManager;
	}

	public void setOverrideUseLocalQueueManager(String overrideUseLocalQueueManager) {
		_overrideUseLocalQueueManager = overrideUseLocalQueueManager;
	}

	public String getOverrideConnectionQueueManagerName() {
		return _overrideConnectionQueueManagerName;
	}

	public void setOverrideConnectionQueueManagerName(String overrideConnectionQueueManagerName) {
		_overrideConnectionQueueManagerName = overrideConnectionQueueManagerName;
	}

	public String getOverrideConnectionHostName() {
		return _overrideConnectionHostName;
	}

	public void setOverrideConnectionHostName(String overrideConnectionHostName) {
		_overrideConnectionHostName = overrideConnectionHostName;
	}

	public String getOverrideConnectionPort() {
		return _overrideConnectionPort;
	}

	public void setOverrideConnectionPort(String overrideConnectionPort) {
		_overrideConnectionPort = overrideConnectionPort;
	}

	public String getOverrideConnectionChannel() {
		return _overrideConnectionChannel;
	}

	public void setOverrideConnectionChannel(String overrideConnectionChannel) {
		_overrideConnectionChannel = overrideConnectionChannel;
	}

	public String getOverrideConnectionCCSID() {
		return _overrideConnectionCCSID;
	}

	public void setOverrideConnectionCCSID(String overrideConnectionCCSID) {
		_overrideConnectionCCSID = overrideConnectionCCSID;
	}

	public String getOverrideConnectionEncoding() {
		return _overrideConnectionEncoding;
	}

	public void setOverrideConnectionEncoding(String overrideConnectionEncoding) {
		_overrideConnectionEncoding = overrideConnectionEncoding;
	}

	public String getOverrideConnectionQueueName() {
		return _overrideConnectionQueueName;
	}

	public void setOverrideConnectionQueueName(String overrideConnectionQueueName) {
		_overrideConnectionQueueName = overrideConnectionQueueName;
	}

	public String getOverrideConnectionDynamicQueuePrefix() {
		return _overrideConnectionDynamicQueuePrefix;
	}

	public void setOverrideConnectionDynamicQueuePrefix(String overrideConnectionDynamicQueuePrefix) {
		_overrideConnectionDynamicQueuePrefix = overrideConnectionDynamicQueuePrefix;
	}

	/*
	 * public String getOverrideConnectionUserid() { return
	 * _overrideConnectionUserid; }
	 * 
	 * public void setOverrideConnectionUserid(String overrideConnectionUserid)
	 * { _overrideConnectionUserid = overrideConnectionUserid; }
	 * 
	 * public String getOverrideConnectionPassword() { return
	 * _overrideConnectionPassword; }
	 * 
	 * public void setOverrideConnectionPassword(String
	 * overrideConnectionPassword) { _overrideConnectionPassword =
	 * overrideConnectionPassword; }
	 */
	public String getOverriddenConnectionQueueManagerName() {
		return _overriddenConnectionQueueManagerName;
	}

	public void setOverriddenConnectionQueueManagerName(String overriddenConnectionQueueManagerName) {
		_overriddenConnectionQueueManagerName = overriddenConnectionQueueManagerName;
	}

	public String getOverriddenConnectionHostName() {
		return _overriddenConnectionHostName;
	}

	public void setOverriddenConnectionHostName(String overriddenConnectionHostName) {
		_overriddenConnectionHostName = overriddenConnectionHostName;
	}

	public String getOverriddenConnectionPort() {
		return _overriddenConnectionPort;
	}

	public void setOverriddenConnectionPort(String overriddenConnectionPort) {
		_overriddenConnectionPort = overriddenConnectionPort;
	}

	public String getOverriddenConnectionChannel() {
		return _overriddenConnectionChannel;
	}

	public void setOverriddenConnectionChannel(String overriddenConnectionChannel) {
		_overriddenConnectionChannel = overriddenConnectionChannel;
	}

	public String getOverriddenConnectionCCSID() {
		return _overriddenConnectionCCSID;
	}

	public void setOverriddenConnectionCCSID(String overriddenConnectionCCSID) {
		_overriddenConnectionCCSID = overriddenConnectionCCSID;
	}

	public String getOverriddenConnectionEncoding() {
		return _overriddenConnectionEncoding;
	}

	public void setOverriddenConnectionEncoding(String overriddenConnectionEncoding) {
		_overriddenConnectionEncoding = overriddenConnectionEncoding;
	}

	public String getOverriddenConnectionQueueName() {
		return _overriddenConnectionQueueName;
	}

	public void setOverriddenConnectionQueueName(String overriddenConnectionQueueName) {
		_overriddenConnectionQueueName = overriddenConnectionQueueName;
	}

	public String getOverriddenConnectionDynamicQueuePrefix() {
		return _overriddenConnectionDynamicQueuePrefix;
	}

	public void setOverriddenConnectionDynamicQueuePrefix(String overriddenConnectionDynamicQueuePrefix) {
		_overriddenConnectionDynamicQueuePrefix = overriddenConnectionDynamicQueuePrefix;
	}

	public String[] getOverrideConnectionPropertyType() {
		return _overrideConnectionPropertyType; //NOSONAR
	}

	public void setOverrideConnectionPropertyType(String[] overrideConnectionPropertyType) {
		_overrideConnectionPropertyType = overrideConnectionPropertyType;  //NOSONAR
	}

	@SuppressWarnings("squid:S1541")
	public wmMQConnection overrideConnection(WmManagedConnection connection, WmRecord input, boolean inbound)
			throws AdapterConnectionException {
		log(ARTLogger.INFO, 1001, "overrideConnection", "");
		wmMQConnection conn = (wmMQConnection) connection;
		IData override = null;
		IDataCursor idc = input.getCursor();
		// String temp = "";
		// String originalqueuename = "";

		try {
			if ((idc == null) || (!idc.first(OVERRIDE_CONNECTION_LABEL)))
				return conn;

			override = (IData) idc.getValue();
			idc = override.getCursor();
			if (idc == null)
				return conn;

			String qname = "";
			String qmgrname = "";
			String ccsid = "";
			String encoding = "";
			String hostname = "";
			String port = "";
			String channel = "";
			String prefix = "";
			String securityExit = "";
			String securityExitParam = "";
			String keyStoreAlias = "";
			String trustStoreAlias = "";
			String keyStoreFile = "";
			String keyStorePassword = "";
			String sslCipherSpec = "";
			String userId = "";
			String password = "";
			
			if (idc.first(QUEUE_NAME_LABEL))
				qname = (String) idc.getValue();
			if (idc.first(QUEUE_MANAGER_NAME_LABEL))
				qmgrname = (String) idc.getValue();
			if (idc.first(QUEUE_HOST_NAME_LABEL))
				hostname = (String) idc.getValue();
			if (idc.first(QUEUE_PORT_LABEL))
				port = (String) idc.getValue();
			if (idc.first(QUEUE_CHANNEL_LABEL))
				channel = (String) idc.getValue();
			if (idc.first(QUEUE_CCSID_LABEL))
				ccsid = (String) idc.getValue();
			if (idc.first(QUEUE_ENCODING_LABEL))
				encoding = (String) idc.getValue();
			if (idc.first(QUEUE_DYNAMIC_QUEUE_PREFIX_LABEL))
				prefix = (String) idc.getValue();
			if ((qmgrname == null) || (qmgrname.trim().equals("")) && (hostname == null || hostname.trim().equals(""))
					&& (port == null || port.trim().equals("")) && (channel == null || channel.trim().equals(""))
					&& qname != null && (qname.trim().equals(""))) {
				return conn;
			}

			log(ARTLogger.INFO, 1003, "overrideConnection", "qmgrname= " + qmgrname);

			wmMQConnectionFactory connFactory = (wmMQConnectionFactory) conn.getFactory();

			// Trax # 1-14WOV9 : Synchronize on the connection factory so that
			// no other threads
			// change the factory values while the current thread is creating
			// the connection.
			//synchronized (connFactory) {
				if (conn != null) {
					if ((qname == null) || (qname.trim().equals(""))) {
						qname =  conn.getResolvedQueueName(inbound).trim();
						if ((qname == null) || (qname.equals("")))
							qname =  conn.getQueueName();
					}

					if ((qmgrname == null) || (qmgrname.trim().equals("")))
						qmgrname = conn.getResolvedQueueManagerName().trim();
					// This one is special. If the user wants to override a
					// TCP/IP
					// connection with a Bindings mode connection, they will
					// have to
					// specify a blank hostname.
					if ((hostname == null) || (hostname.trim().equals(""))) {
						hostname = connFactory.getHostName().trim();
					} else {
						hostname = hostname.trim();
					}
					if ((port == null) || (port.trim().equals(""))) {
						port = connFactory.getPort().trim();
					} else {
						port = port.trim();
					}
					if ((channel == null) || (channel.trim().equals(""))) {
						channel = connFactory.getChannel().trim();
					} else {
						channel = channel.trim();
					}
					if ((securityExit == null) || (securityExit.trim().equals("")))
						securityExit = connFactory.getSecurityExit().trim();
					if ((securityExitParam == null) || (securityExitParam.trim().equals("")))
						securityExitParam = connFactory.getSecurityExitInit().trim();

					// Trax 1-10G93N : Check if the user has specified the
					// prefix, ccsid and encoding
					if ((ccsid == null) || (ccsid.trim().equals(""))) {
						ccsid = connFactory.getCCSID().trim();
					} else {
						ccsid = ccsid.trim();
					}
					if ((encoding == null) || (encoding.trim().equals(""))) {
						encoding = connFactory.getEncoding().trim();
					} else {
						encoding = encoding.trim();
					}
					if ((prefix == null) || (prefix.trim().equals(""))) {
						prefix = connFactory.getDynamicQueuePrefix().trim();
					} else {
						prefix = prefix.trim();
					}
					if ((keyStoreAlias == null) || (keyStoreAlias.trim().equals(""))) {
						keyStoreAlias = connFactory.getSslKeyStoreAlias().trim();
					} else {
						keyStoreAlias = keyStoreAlias.trim();
					}
					if ((trustStoreAlias == null) || (trustStoreAlias.trim().equals(""))) {
						trustStoreAlias = connFactory.getSslTrustStoreAlias().trim();
					} else {
						trustStoreAlias = trustStoreAlias.trim();
					}
					if ((keyStoreFile == null) || (keyStoreFile.trim().equals(""))) {
						keyStoreFile = connFactory.getSslKeyStore().trim();
					} else {
						keyStoreFile = keyStoreFile.trim();
					}
					if ((keyStorePassword == null) || (keyStorePassword.trim().equals(""))) {
						keyStorePassword = connFactory.getSslKeyStorePassword().trim();
					} else {
						keyStorePassword = keyStorePassword.trim();
					}
					if ((sslCipherSpec == null) || (sslCipherSpec.trim().equals(""))) {
						sslCipherSpec = connFactory.getSslCipherSpec().trim();
					} else {
						sslCipherSpec = sslCipherSpec.trim();
					}

					if ((userId == null) || (userId.trim().equals(""))) {
						userId = connFactory.getUserId().trim();
					} else {
						userId = userId.trim();
					}
					if ((password == null) || (password.trim().equals(""))) {
						password = connFactory.getPassword().trim();
					} else {
						password = password.trim();
					}

					log(ARTLogger.INFO, 1003, "OverrideConnection", "override queue manager name =" + qmgrname);
							
					boolean sslKeystoreAlias = false;
					boolean sslKeystoreFile = false;

					// Checking if connection is SSL and configured either keystoreAlias and keystoreFile 
					if ((wmMQAdapter.getSSLListing().trim().length() > 0 || wmMQAdapter.getSSLSupport()) && Service.isServerSupported()
							&& ((connFactory.getSslKeyStoreAlias() != null) 
									&& (connFactory.getSslKeyStoreAlias().trim().length() > 0))
							&& ((connFactory.getSslTrustStoreAlias() != null) 
									&& (connFactory.getSslTrustStoreAlias().trim().length() > 0))){
						sslKeystoreAlias = true;

					} else if ((wmMQAdapter.getSSLListing().trim().length() > 0 || wmMQAdapter.getSSLSupport()) && ((connFactory.getSslKeyStore() != null) 
									&& (connFactory.getSslKeyStore().trim().length() > 0))) {
						sslKeystoreFile = true;
					}

					if ((!qmgrname.equals(((wmMQConnection) conn).getResolvedQueueManagerName().trim()))
							|| (!hostname.equals(connFactory.getHostName().trim()))
							|| (!port.equals(connFactory.getPort().trim()))
							|| (!channel.equals(connFactory.getChannel().trim()))
							|| (!qname.equals(connFactory.getQueueName().trim()))) {
						log(ARTLogger.INFO, 1003, "OverrideConnection",
								"find connection for " + qmgrname + hostname + port + channel + "/" + prefix + qname);

						// Trax 1-VF52N Number of connections increasing.
						// Connection is overridden even when it should not be.

						if(sslKeystoreAlias){
							// For ssl connection with keystore alias
							conn = connFactory.findConnection(qmgrname + hostname + port + channel, prefix + qname + keyStoreAlias + trustStoreAlias + sslCipherSpec);
							
						} else if (sslKeystoreFile){
							//ssl connection with keystore file
							conn = connFactory.findConnection(qmgrname + hostname + port + channel,	prefix + qname + keyStoreFile + sslCipherSpec);
						} else {
							// for non-ssl connection
							conn = connFactory.findConnection(qmgrname + hostname + port + channel, prefix + qname);
						}
	
						if (conn == null) {
							
							log(ARTLogger.INFO, 1003, " Could not find OverrideConnection for key : " + qmgrname + hostname + port + channel, prefix + qname + "caching= " + conn + ",connectionFactory= " + connFactory);
							
							wmMQConnectionFactory newconnFactory = null;

							if (connection instanceof wmMQTransactionalConnection) {
								newconnFactory = new wmMQTransactionalConnectionFactory();
							} else {
								newconnFactory = new wmMQConnectionFactory();
							}

							newconnFactory.setQueueManagerName(qmgrname.trim());
							newconnFactory.setQueueName(qname.trim());
							newconnFactory.setHostName(hostname.trim());
							newconnFactory.setPort(port.trim());
							newconnFactory.setChannel(channel.trim());
							newconnFactory.setSecurityExit(securityExit.trim());
							newconnFactory.setSecurityExitInit(securityExitParam.trim());
							newconnFactory.setCCSID(ccsid.trim());
							newconnFactory.setEncoding(encoding.trim());
							newconnFactory.setDynamicQueuePrefix(prefix.trim());
							newconnFactory.setUserId(userId);
							newconnFactory.setPassword(password);
							
							if (sslKeystoreAlias) {
								// Override SSL connection with keystore alias
								newconnFactory.setSslKeyStoreAlias(keyStoreAlias.trim());
								newconnFactory.setSslTrustStoreAlias(trustStoreAlias.trim());
								newconnFactory.setSslCipherSpec(sslCipherSpec);
								
								log(ARTLogger.INFO, 1003, "Create a new connection ",
										"hostname=" + hostname + ",port=" + port + ",channel=" + channel + ",QmanagerName="
												+ qmgrname + ",Qname=" + qname + ",ccsid=" + ccsid + ",encoding=" + encoding
												+ ",securityExit=" + securityExit + ",securityExitInit=" + securityExitParam
												+ ",dynamicQueuePrefix=" + prefix  + ",keyStoreAlias=" + keyStoreAlias
												 + ",trustStoreAlias=" + trustStoreAlias + ",sslCipherSpec=" + sslCipherSpec);
								
							} else if(sslKeystoreFile){
								//Override SSL connection with keystore file
								newconnFactory.setSslKeyStore(keyStoreFile);
								newconnFactory.setSslKeyStorePassword(keyStorePassword);
								newconnFactory.setSslCipherSpec(sslCipherSpec);
								
								log(ARTLogger.INFO, 1003, "Create a new connection ",
										"hostname=" + hostname + ",port=" + port + ",channel=" + channel + ",QmanagerName="
												+ qmgrname + ",Qname=" + qname + ",ccsid=" + ccsid + ",encoding=" + encoding
												+ ",securityExit=" + securityExit + ",securityExitInit=" + securityExitParam
												+ ",dynamicQueuePrefix=" + prefix  + ",keyStoreFile=" + keyStoreFile
												 + ",sslCipherSpec=" + sslCipherSpec);
								
							} else {
								log(ARTLogger.INFO, 1003, "Create a new connection ", 
										"hostname=" + hostname + ",port=" + port + ",channel=" + channel + ",QmanagerName="
												+ qmgrname + ",Qname=" + qname + ",ccsid=" + ccsid + ",encoding=" + encoding
												+ ",securityExit=" + securityExit + ",securityExitInit=" + securityExitParam
												 + ",dynamicQueuePrefix=" + prefix);
								
							}

							try {

								if (connection instanceof wmMQTransactionalConnection) {
									conn = (wmMQTransactionalConnection) newconnFactory.createManagedConnection(null,
											null);
								} else {
									conn = (wmMQConnection) newconnFactory.createManagedConnection(null, null);
								}

							} catch (ResourceException re) {
								// re.printStackTrace();
								throw wmMQAdapter.getInstance().createAdapterConnectionException(1055,
										new String[] { "Overriding Connection", re.getMessage() },
										re.getLinkedException());
							}

							if (conn == null) {
								throw wmMQAdapter.getInstance().createAdapterConnectionException(1055,
										new String[] { "Overriding Connection", "No connection returned" });
							} else {
								// Now, cache the new connection to the old
								// connFactory
								
								if(sslKeystoreAlias){
									((wmMQConnectionFactory) connection.getFactory()).cacheConnection(conn,
											qmgrname.trim() + hostname.trim() + port.trim() + channel.trim(),
											prefix.trim() + qname.trim() + keyStoreAlias.trim() + trustStoreAlias.trim() + sslCipherSpec.trim());
								
								} else if (sslKeystoreFile){
									((wmMQConnectionFactory) connection.getFactory()).cacheConnection(conn,
											qmgrname.trim() + hostname.trim() + port.trim() + channel.trim(),
											prefix.trim() + qname.trim() + keyStoreFile.trim() + sslCipherSpec.trim());
								
								} else {
									((wmMQConnectionFactory) connection.getFactory()).cacheConnection(conn,
											qmgrname.trim() + hostname.trim() + port.trim() + channel.trim(),
											prefix.trim() + qname.trim());
								}

								log(ARTLogger.INFO, 1003, "OverrideConnection",
										"caching " + conn + ",connectionFactory=" + connFactory);
							}
						}
					}
				}

			//} // End of synchronized(connFactory)

			// if ((newConn) && (((wmMQConnection)connection).InTransaction()))
			if ((((wmMQConnection) connection).InTransaction()) && (!conn.equals(connection))) {
				wmMQTransactionalConnection wtc = (wmMQTransactionalConnection) conn;
				wtc.begin();
				log(ARTLogger.INFO, 1003, "OverrideConnection", "caching " + conn + " (in transaction)");
				((wmMQTransactionalConnection) connection).cacheConnectionInTransaction(wtc);
			}

		} catch (AdapterConnectionException ace) {
			// ace.printStackTrace();
			throw ace;
		} catch (Exception ex) {
			// ex.printStackTrace();
			throw wmMQAdapter.getInstance().createAdapterConnectionException(1055,
					new String[] { "Overriding Connection", ex.getMessage() }, ex);
			// conn = (wmMQConnection)connection; //return original connection
		} finally {
			if (idc != null)
				idc.destroy();
		}

		log(ARTLogger.INFO, 1002, "OverrideConnection", "");
		return conn;
	}

	public String getPropertyFromInput(WmRecord input, String property) {
		log(ARTLogger.INFO, 1001, "getPropertyFromInput", "");
		IData override = null;
		IDataCursor idc = input.getCursor();
		String oneproperty = "";
		try {
			if ((idc == null) || (!idc.first(OVERRIDE_CONNECTION_LABEL)))
				return null;

			override = (IData) idc.getValue();
			idc = override.getCursor();
			if (idc == null)
				return null;

			if (idc.first(property))
				oneproperty = (String) idc.getValue();
			if (oneproperty != null)
				oneproperty = oneproperty.trim();
		} catch (Exception ex) {
			// ex.printStackTrace();
		} finally {
			if (idc != null)
				idc.destroy();
		}

		log(ARTLogger.INFO, 1002, "getPropertyFromInput", "");
		return oneproperty;
	}

	public void fillOverriddenConnection(WmManagedConnection connection, WmRecord output, boolean inbound) {
		wmMQConnection conn = (wmMQConnection) connection;
		fillOverriddenConnection(conn, output, conn.getResolvedQueueManagerName(), conn.getResolvedQueueName(inbound));
	}

	public void fillOverriddenConnection(wmMQConnection connection, WmRecord output, String queueManagerName,
			String queueName) {
		log(ARTLogger.INFO, 1001, "fillOverriddenConnection", "");

		wmMQConnectionFactory connFactory = (wmMQConnectionFactory) connection.getFactory();

		WmRecord overridden = WmRecordFactory.getFactory().createWmRecord(OVERRIDDEN_CONNECTION_LABEL);

		overridden.put(QUEUE_MANAGER_NAME_LABEL, queueManagerName);
		overridden.put(QUEUE_HOST_NAME_LABEL, connFactory.getHostName());
		overridden.put(QUEUE_PORT_LABEL, connFactory.getPort());
		overridden.put(QUEUE_CHANNEL_LABEL, connFactory.getChannel());
		overridden.put(QUEUE_CCSID_LABEL, connFactory.getCCSID());
		overridden.put(QUEUE_ENCODING_LABEL, connFactory.getEncoding());
		overridden.put("securityExit", connFactory.getSendExit());

		overridden.put(QUEUE_NAME_LABEL, queueName);
		overridden.put(QUEUE_DYNAMIC_QUEUE_PREFIX_LABEL, connFactory.getDynamicQueuePrefix());

		output.put(OVERRIDDEN_CONNECTION_LABEL, overridden);
		log(ARTLogger.INFO, 1002, "fillOverriddenConnection", "");
	}

	/*
	 * This method populates the metadata object describing this service
	 * template in the specified locale. This method will be called once for
	 * each service template.
	 *
	 * The d parameter is the metadata object describing this adapter service.
	 * The l parameter is the Locale in which the locale-specific metadata
	 * should be populated. AdapterException is thrown if an error is
	 * encountered while populating the metadata.
	 */
	public void fillWmTemplateDescriptor(WmTemplateDescriptor d, Locale l) throws AdapterException {

		d.setHidden(wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE, true);
		// d.setHidden(wmMQAdapterConstant.OVERRIDE_PROPERTY_NAMES, true);
		if (_serviceName.equals(wmMQAdapterConstant.PUT_SERVICE)
				|| _serviceName.equals(wmMQAdapterConstant.REQUEST_REPLY_SERVICE)) {
			d.setHidden(wmMQAdapterConstant.OVERRIDE_USE_LOCAL_QUEUE_MANAGER, true);
		}
		d.setHidden(wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_MANAGER_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_CONNECTION_HOST_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_CONNECTION_PORT, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_CONNECTION_CHANNEL_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_CONNECTION_CCSID, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_CONNECTION_ENCODING, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDE_CONNECTION_DYNAMIC_QUEUE_PREFIX, true);

		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_MANAGER_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_HOST_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_PORT, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CHANNEL_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CCSID, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_ENCODING, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_NAME, true);
		d.setHidden(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_DYNAMIC_QUEUE_PREFIX, true);

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE, wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE,
				null);

		if (_serviceName.equals(wmMQAdapterConstant.PUT_SERVICE)
				|| _serviceName.equals(wmMQAdapterConstant.REQUEST_REPLY_SERVICE)) {
			d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_USE_LOCAL_QUEUE_MANAGER,
					WmTemplateDescriptor.INPUT_FIELD_NAMES,
					new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });
		}

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_MANAGER_NAME,
				WmTemplateDescriptor.INPUT_FIELD_NAMES, new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_CONNECTION_HOST_NAME, WmTemplateDescriptor.INPUT_FIELD_NAMES,
				new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_CONNECTION_PORT, WmTemplateDescriptor.INPUT_FIELD_NAMES,
				new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_CONNECTION_CHANNEL_NAME,
				WmTemplateDescriptor.INPUT_FIELD_NAMES, new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_CONNECTION_CCSID, WmTemplateDescriptor.INPUT_FIELD_NAMES,
				new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_CONNECTION_ENCODING, WmTemplateDescriptor.INPUT_FIELD_NAMES,
				new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_NAME, WmTemplateDescriptor.INPUT_FIELD_NAMES,
				new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDE_CONNECTION_DYNAMIC_QUEUE_PREFIX,
				WmTemplateDescriptor.INPUT_FIELD_NAMES, new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_MANAGER_NAME,
				WmTemplateDescriptor.OUTPUT_FIELD_NAMES, new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_HOST_NAME,
				WmTemplateDescriptor.OUTPUT_FIELD_NAMES, new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_PORT, WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
				new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CHANNEL_NAME,
				WmTemplateDescriptor.OUTPUT_FIELD_NAMES, new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_CCSID, WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
				new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_ENCODING, WmTemplateDescriptor.OUTPUT_FIELD_NAMES,
				new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_QUEUE_NAME,
				WmTemplateDescriptor.OUTPUT_FIELD_NAMES, new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });

		d.setResourceDomain(wmMQAdapterConstant.OVERRIDDEN_CONNECTION_DYNAMIC_QUEUE_PREFIX,
				WmTemplateDescriptor.OUTPUT_FIELD_NAMES, new String[] { wmMQAdapterConstant.OVERRIDE_PROPERTY_TYPE });
	}

	public static void log(int level, int minor, String arg0, String arg1) {
		ARTLogger logger = ((wmMQAdapter) wmMQAdapter.getInstance()).getLogger();
		if (logger == null) {
			System.out.println("Logger is null");
			return;
		}

		// Trax 1-WVILA. Allow user to override the logging level of adapter
		// messages.
		if (wmMQAdapter.getLogLevelOverrides().containsKey("" + minor))
			level = Integer.parseInt((String) wmMQAdapter.getLogLevelOverrides().get("" + minor)) - ARTLogger.DEBUG;

		String[] args = new String[2];
		args[0] = arg0;
		args[1] = arg1;
		// Trax log 1-S4OHA - move adapter logging to DEBUG PLUS
		logger.logDebugPlus(level, minor, args);
	}

	private static final String[] overrideConnectionPropertyNames = {
			wmMQAdapterConstant.OVERRIDE_USE_LOCAL_QUEUE_MANAGER,
			wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_MANAGER_NAME,
			wmMQAdapterConstant.OVERRIDE_CONNECTION_HOST_NAME, wmMQAdapterConstant.OVERRIDE_CONNECTION_PORT,
			wmMQAdapterConstant.OVERRIDE_CONNECTION_CHANNEL_NAME, wmMQAdapterConstant.OVERRIDE_CONNECTION_CCSID,
			wmMQAdapterConstant.OVERRIDE_CONNECTION_ENCODING, wmMQAdapterConstant.OVERRIDE_CONNECTION_QUEUE_NAME,
			wmMQAdapterConstant.OVERRIDE_CONNECTION_DYNAMIC_QUEUE_PREFIX

	};
}