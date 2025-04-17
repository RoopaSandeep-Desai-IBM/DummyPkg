/**
 * Service.java
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
package com.wm.adapter.wmmqadapter.admin;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import com.wm.adapter.wmmqadapter.PCFQuery;
import com.wm.adapter.wmmqadapter.WmMQAdapterUtils;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adapter.wmmqadapter.wmMQAdapterResourceBundle;
import com.wm.adapter.wmmqadapter.connection.wmMQConnection;
import com.wm.adapter.wmmqadapter.connection.wmMQConnectionFactory;
import com.wm.adapter.wmmqadapter.connection.wmMQMultiQueueListener;
import com.wm.adapter.wmmqadapter.service.wmMQMQMD;
import com.wm.adk.ADKGLOBAL;
import com.wm.adk.admin.AdapterAdmin;
import com.wm.adk.error.AdapterConnectionException;
import com.wm.adk.error.AdapterException;
import com.wm.app.b2b.server.Build;
import com.wm.app.b2b.server.ServiceException;
import com.wm.app.b2b.server.Session;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.data.ValuesEmulator;
import com.wm.lang.ns.NSName;
import com.wm.pkg.art.AdapterRuntimeGlobals;
import com.wm.pkg.art.admin.ARTAdmin;
import com.wm.pkg.art.data.IDataExt;
import com.wm.pkg.art.deployment.AdapterType;
import com.wm.pkg.art.deployment.AdapterTypeManager;
import com.wm.pkg.art.error.DetailedException;
import com.wm.pkg.art.error.DetailedServiceException;
import com.wm.pkg.art.ns.ConnectionDataNode;
import com.wm.pkg.art.ns.ConnectionDataNodeManager;
import com.wm.pkg.art.ns.ListenerManager;
import com.wm.pkg.art.ns.ListenerNode;
import com.wm.pkg.art.ns.ListenerProperties;
import com.wm.pkg.art.util.StringUtil;
import com.wm.util.Values;



/*
 * This class contains Integration Server startup and shutdown
 * services which register and unregister the adapters in this package.
 */
public class Service {
	// Key values must correspond to those in com.wm.pkg.art.deployment.Service.
	public final static String ADAPTER_KEY = "adapter";
	public final static String ADAPTER_TYPE_NAME_KEY = "adapterTypeName";

	public final static String REGISTER_ADAPTER_SERVICE_NAME = "registerAdapterType";
	public final static String REGISTER_ADAPTER_SERVICE_INTERFACE_NAME = "wm.art.adapter.deployment";
	public final static String UNREGISTER_ADAPTER_SERVICE_NAME = "unregisterAdapterType";
	public final static String UNREGISTER_ADAPTER_SERVICE_INTERFACE_NAME = "wm.art.adapter.deployment";

	private static final String _RETRY_LIMIT = "retryLimit";
	private static final String _RETRY_BACKOFF = "retryBackoffTimeout";
	
	private static final int VERSION_107 = 29;

	/*
	 * A hashtable to cache all connectionFactory objects.
	 */
	private static Hashtable _connectionFactoryParameters = new Hashtable();

	private static Object[][] contents = null;
	
	public static boolean isServerSupported = false; //NOSONAR

	/*
	 * Registers the adapter package in the Integration Server.
	 */
	static public void registerAdapter(IData pipeline) throws ServiceException {

		try {
			AdapterAdmin.registerAdapter(wmMQAdapter.getInstance());
		} catch (Exception e) {
				WmMQAdapterUtils.logException(e);
			// exception.addReason(t);
			// t.printStackTrace();
		} finally {

			Values args = new Values();
			args.put("service", "wm.mqseries.admin:getMenu");
			args.put("name", "wmMQAdapter"); // not for display
			try {
				com.wm.app.b2b.server.Service.doInvoke(NSName.create("wm.server.ui:addMenu"), args);
			} catch (Exception e) {
				WmMQAdapterUtils.logException(e);
			}
		}

		// end generate menu
	}

	/*
	 * cache the ConnectionFactory properties. When the DSP invokes the
	 * findQueues() service, the pipeline contains only the properties of the
	 * queueName parameter. The findQueues() service requires the QMgr
	 * properties, so this method caches all of the ConnectionFactory
	 * properties. These properties are passed to this method in two ways.
	 * First, they are passed in the pipeline in an IData[] named "parameters".
	 * Second, they are passed in the pipeline as individual name/value pairs.
	 * 
	 * The key used to cache the properties is inserted into the queueName
	 * parameter's properties so that findQueues() can retrieve the properties
	 * from the cache.
	 */
	@SuppressWarnings("squid:S1541")
	static public void cacheConnectionFactoryProperties(IData pipeline) throws AdapterException, ServiceException {
		IDataCursor idc = null;
		try {
			idc = pipeline.getCursor();

			// Get connection alias if it exists
			String connectionAlias = "";
			if (idc.first("connectionAlias"))
				connectionAlias = (String) idc.getValue();
			else
				return; // The connection alias is not in the pipeline - just
						// leave
			IData[] parameters = new IData[0];
			if (idc.first("parameters")) {
				parameters = (IData[]) idc.getValue();
				// Mug the queueName parameter with the name of the connection
				for (int p = 0; p < parameters.length; p++) {
					IDataCursor pidc = parameters[p].getCursor();
					if (pidc.first("systemName")) {
						String systemName = (String) pidc.getValue();
						if (systemName.equals("queueName")) {
							pidc.last();
							pidc.insertAfter("connectionAlias", connectionAlias);
							pidc.destroy();
							break;
						}
					}
				}
				_connectionFactoryParameters.put(connectionAlias, parameters);
			} else {
				ArrayList parms = new ArrayList();
				idc.first();
				int parmcount = 0;
				Values oneparm = null;
				do {
					String systemName = idc.getKey();
					// Trax 1-QPSPQ - SSL Support
					// if ((systemName.startsWith("ssl")) &&
					// (!wmMQAdapter.getSSLSupport()) )
					// continue;
					if (systemName.indexOf("List") > -1)
						continue;
					// Trax 1-QPSPQ - SSL Support
					String value = (String) idc.getValue();
					oneparm = new Values();
					oneparm.put("systemName", systemName);
					oneparm.put("displayName", findDisplayName(systemName));
					oneparm.put("parameterType", "java.lang.String");
					// Trax 1-QPSPQ - SSL Support
					if (systemName.startsWith("ssl") && (wmMQAdapter.getSSLListing().trim().length() > 0 || wmMQAdapter.getSSLSupport()))
						oneparm.put("groupName", "sslSettings");
					else
						oneparm.put("groupName", "QueueManagerSettings");
					// Trax 1-QPSPQ - SSL Support
					oneparm.put("defaultValue", value);
					oneparm.put("value", value);
					if (systemName.equals("queueName"))
						oneparm.put("connectionAlias", connectionAlias);
					if (systemName.equalsIgnoreCase("password"))
						oneparm.put("isPassword", new Boolean(true));
					// Trax 1-QPSPQ - SSL Support
					if (systemName.equalsIgnoreCase("sslCipherSpec")) {
						Values[] resourceDomain = new Values[wmMQConnectionFactory.cipherSpecs.length];
						resourceDomain[0] = new Values();
						resourceDomain[0].put("resourceDomain", " ");
						for (int i = 1; i < wmMQConnectionFactory.cipherSpecs.length; i++) {
							resourceDomain[i] = new Values();
							resourceDomain[i].put("resourceDomain", wmMQConnectionFactory.cipherSpecs[i]);
						}
						oneparm.put("resourceDomain", resourceDomain);
					}
					// Trax 1-QPSPQ - SSL Support

					// Trax 1-149L47 -- Begin
					String[] domainValues = null;
					if (systemName.equalsIgnoreCase(wmMQAdapterConstant.CACHE_OVERRIDDEN_CONNECTIONS)) {
						domainValues = new String[] { "true", "false" };
					} else {
						if (systemName.equalsIgnoreCase(wmMQAdapterConstant.ENCODING)) {
							domainValues = wmMQMQMD.encodingsForDisplay;
						} else {
							if (systemName.equalsIgnoreCase(wmMQAdapterConstant.ACTION_BEFORE_DISCONNECT)) {
								domainValues = new String[] { "NONE", "COMMIT", "ROLLBACK" };
							}
						}
					}

					if (domainValues != null) {
						Values[] resourceDomain = new Values[domainValues.length];
						for (int i = 0; i < domainValues.length; i++) {
							resourceDomain[i] = new Values();
							resourceDomain[i].put("resourceDomain", domainValues[i]);
						}
						oneparm.put("resourceDomain", resourceDomain);
					}
					// Trax 1-149L47 -- End

					parms.add(parmcount++, oneparm);
				} while (idc.next());
				parameters = (IData[]) parms.toArray(parameters);
			}
			_connectionFactoryParameters.put(connectionAlias, parameters);
		} catch (Exception e) {
			// t.printStackTrace();
			if (e instanceof ServiceException)
				throw (ServiceException) e;
			else
				throw wmMQAdapter.getInstance().createAdapterException(2055, new String[] {
						com.wm.adapter.wmmqadapter.wmMQAdapter.class.getName(), e.getLocalizedMessage() }, e);

		} finally {
			if (idc != null)
				idc.destroy();
		}
	}

	/*
	 * reload the cached ConnectionFactory properties. After the DSP invokes the
	 * findQueues() service, the DSP 'refreshes' itself. However, the
	 * "parameters" property in the pipeline has been lost. This method will
	 * reload the the "parameters" property from the cache.
	 * 
	 * The key used to cache the properties is resourceFolderName:resourceName
	 */
	static public void reloadConnectionFactoryProperties(IData pipeline) throws AdapterException, ServiceException {
		IDataCursor idc = null;
		try {
			idc = pipeline.getCursor();

			String connectionAlias = "";
			// Get connection alias if it exists
			if (idc.first("resourceFolderName"))
				connectionAlias = (String) idc.getValue();
			else
				return;

			if (idc.first("resourceName"))
				connectionAlias = connectionAlias + ":" + (String) idc.getValue();
			else
				return;

			IData[] parameters = (IData[]) _connectionFactoryParameters.get(connectionAlias);
			if (parameters != null) {
				if (idc.first("parameters"))
					idc.setValue(parameters);
				else {
					idc.last();
					idc.insertAfter("parameters", parameters);
				}
			}
		} catch (Exception e) {
			// t.printStackTrace();
			if (e instanceof ServiceException)
				throw (ServiceException) e;
			else
				throw wmMQAdapter.getInstance().createAdapterException(2055, new String[] {
						com.wm.adapter.wmmqadapter.wmMQAdapter.class.getName(), e.getLocalizedMessage() }, e);

		} finally {
			if (idc != null)
				idc.destroy();
		}
	}

	/*
	 * Find the queues associated with the the adapter package in the
	 * Integration Server.
	 */
	@SuppressWarnings("squid:S1541")
	static public void findQueues(IData pipeline) throws ServiceException, AdapterConnectionException {
		String selectedQueues = "";
		String[] queuenames = null;
		int sindx = 0;
		String qMgrName = null;
		String hostName = null;
		int port = 1414;
		String channel = null;
		int ccsid = 819;
		// Trax 1-QPSPQ - SSL Support
		String sslKeyStore = "";
		String sslKeyStorePassword = "";
		String sslCipherSpec = "";
		String SSLOption = "";
		String keystoreAlias = "";
		String truststoreAlias = "";
		String ccdtFilePath ="";
		String username="";
		String password="";
		String passkey="";
//		String nodename="";
		// Trax 1-QPSPQ - SSL Support

		IDataCursor idc = null;
		try {
			idc = pipeline.getCursor();
			qMgrName = ValuesEmulator.getString(pipeline, "queueManagerName");
			hostName = ValuesEmulator.getString(pipeline, "hostName");
			String strPort = ValuesEmulator.getString(pipeline, "port");
			try {
				port = Integer.parseInt(strPort);
			} catch (java.lang.NumberFormatException nfe) {
				port = 1414;
			}
			channel = ValuesEmulator.getString(pipeline, "channel");
			ccsid = WmMQAdapterUtils.getCharacterSetId(ValuesEmulator.getString(pipeline, "CCSID"));
			sslKeyStore = ValuesEmulator.getString(pipeline, "sslKeyStore");
			sslKeyStorePassword = ValuesEmulator.getString(pipeline, "sslKeyStorePassword");
			sslCipherSpec = ValuesEmulator.getString(pipeline, "sslCipherSpec");
			SSLOption = ValuesEmulator.getString(pipeline, "SSLOptions");
			keystoreAlias = ValuesEmulator.getString(pipeline, "sslKeyStoreAlias");
			truststoreAlias = ValuesEmulator.getString(pipeline, "sslTrustStoreAlias");
			ccdtFilePath = ValuesEmulator.getString(pipeline,"ccdtFilePath");
			username = ValuesEmulator.getString(pipeline,"userId");
			password = ValuesEmulator.getString(pipeline,"password");
//			nodename=ValuesEmulator.getString(pipeline, "connectionAlias");
			
//			ConnectionDataNode node = ConnectionDataNodeManager.getConnectionDataNode(nodename);
			
			if (qMgrName == null && channel == null) {
				if (idc.first("connectionAlias")) {
					String connectionAlias = (String) idc.getValue();
					IData[] parameters = (IData[]) _connectionFactoryParameters.get(connectionAlias);
					for (int p = 0; p < parameters.length; p++) {
						IDataCursor pidc = parameters[p].getCursor();

						if (pidc.first("systemName")) {
							String sysname = (String) pidc.getValue();
							if (sysname.equals("queueManagerName")) {
								pidc.first("value");
								qMgrName = (String) pidc.getValue();
							} else if (sysname.equals("hostName")) {
								pidc.first("value");
								hostName = (String) pidc.getValue();
							} else if (sysname.equals("port")) {
								pidc.first("value");
								try {
									port = Integer.parseInt((String) pidc.getValue());
								} catch (java.lang.NumberFormatException nfe) {
									port = 1414;
								}
							} else if (sysname.equals("channel")) {
								pidc.first("value");
								channel = (String) pidc.getValue();
							} else if (sysname.equals("CCSID")) {
								pidc.first("value");
								ccsid = WmMQAdapterUtils.getCharacterSetId((String) pidc.getValue());
							}
							// Trax 1-QPSPQ - SSL Support
							else if (sysname.equals("sslKeyStore")) {
								pidc.first("value");
								sslKeyStore = (String) pidc.getValue();
							} else if (sysname.equals("sslKeyStorePassword")) {
								pidc.first("value");
								sslKeyStorePassword = (String) pidc.getValue();
							} else if (sysname.equals("sslCipherSpec")) {
								pidc.first("value");
								sslCipherSpec = (String) pidc.getValue();
							} else if (sysname.equals("SSLOptions")) {
								pidc.first("value");
								SSLOption = (String) pidc.getValue();
							} else if (sysname.equals("sslKeyStoreAlias")) {
								pidc.first("value");
								keystoreAlias = (String) pidc.getValue();
							} else if (sysname.equals("sslTrustStoreAlias")) {
								pidc.first("value");
								truststoreAlias = (String) pidc.getValue();
							}else if (sysname.equals("ccdtFilePath")) {
								pidc.first("value");
								ccdtFilePath = (String) pidc.getValue();
							}else if (sysname.equals("userId")) {
								pidc.first("value");
								username = (String) pidc.getValue();
							}else if (sysname.equals("password")) {
								pidc.first("value");
								passkey = (String) pidc.getValue();
								if(null !=passkey)
									password = StringUtil.getFromPassman(passkey);

								/*	Passman access based on node version
								 	if(node.get_nodeVersion()==ConnectionDataNode.VERSION_ONE_VALUE)
										password = (String) pidc.getValue();
									else if(node.get_nodeVersion()==ConnectionDataNode.VERSION_TWO_VALUE){
										passkey = (String) pidc.getValue();
										password = StringUtil.getFromPassman(passkey);
								 */
							}
						}

					}
				}
			}
			if (qMgrName != null && hostName != null && channel != null) {
				if ((hostName == null) || (hostName.trim().equals("")))
					queuenames = PCFQuery.listQueues(qMgrName, ccsid, ccdtFilePath);
				else

					queuenames = PCFQuery.listQueuesByPCFCmd(qMgrName, hostName, port, channel, ccsid, sslKeyStore,
							sslKeyStorePassword, sslCipherSpec, keystoreAlias, truststoreAlias, ccdtFilePath, username, password);
			} else
				return;
			// }
			// Trax 1-QPSPQ - SSL Support

			if ((queuenames == null) || (queuenames.length == 0)) {
				return;
			}

			Values[] queues = new Values[queuenames.length];
			for (int i = 0; i < queuenames.length; i++) {
				queues[i] = new Values();
				queues[i].put("queuename", queuenames[i].trim());
				sindx = selectedQueues.indexOf(queuenames[i].trim());
				if ((sindx > 0) && (selectedQueues.charAt(sindx - 1) == ' ')
						&& (selectedQueues.charAt(sindx + queuenames[i].trim().length()) == ' '))
					queues[i].put("selected", "yes");
				else {
					queues[i].put("selected", "no");
				}
			}

			if (idc.first("queues"))
				idc.setValue(sortValues(queues, "queuename"));
			else {
				idc.last();
				idc.insertAfter("queues", sortValues(queues, "queuename"));
			}
		} catch (Exception e) {
			if (e instanceof AdapterException)
				throw wmMQAdapter.getInstance().createAdapterConnectionException(2055, new String[] {
						com.wm.adapter.wmmqadapter.wmMQAdapter.class.getName(), e.getLocalizedMessage() }, e);
			// t.printStackTrace();

			if (e instanceof ServiceException)
				throw (ServiceException) e;
			else
				throw wmMQAdapter.getInstance().createAdapterConnectionException(2055, new String[] {
						com.wm.adapter.wmmqadapter.wmMQAdapter.class.getName(), e.getLocalizedMessage() }, e);

		} finally {
			if (idc != null)
				idc.destroy();
		}
	}

	/*
	 * Unregisters the adapter package from Integration Server.
	 */
	static public void unregisterAdapter(IData pipeline) throws ServiceException {
		AdapterAdmin.unregisterAdapter(wmMQAdapter.getInstance());
	}

	/**
	 * Helper method sortValues This method sorts an array of Values objects
	 * based on the value of the specified string property
	 * 
	 * @param values
	 *            the array of Values Objects
	 * @param propertyname
	 *            the name of the string property to sort on
	 * 
	 * @return an array of Values objects
	 * 
	 */
	public static Values[] sortValues(Values[] values, String propertyname) {
		if ((values == null) || (values.length == 0))
			return null;

		Values[] sorted = new Values[values.length];
		SortableEntry[] entries = new SortableEntry[values.length];

		// Create an array f SortableEtnry objects
		for (int x = 0; x < values.length; x++) {
			entries[x] = new SortableEntry(values[x].getString(propertyname), values[x]);
		}

		// sort the array of SortableEntry objects
		Arrays.sort(entries, entries[0]);

		// move the sorted Values objects to output array
		for (int x = 0; x < entries.length; x++) {
			sorted[x] = entries[x].getValue();
		}
		return sorted;
	}

	private static String findDisplayName(String systemName) {
		if (contents == null) {
			String bundlename = wmMQAdapter.getInstance().getAdapterResourceBundleName();
			wmMQAdapterResourceBundle bundle = (wmMQAdapterResourceBundle) wmMQAdapterResourceBundle
					.getBundle(bundlename);
			contents = bundle.getContents();
		}
		for (int i = 0; i < contents.length; i++) {
			if (((String) contents[i][0]).equals(systemName + ADKGLOBAL.RESOURCEBUNDLEKEY_DISPLAYNAME))
				return (String) contents[i][1];
		}
		return "";
	}

	public final static Values getMenu(Values in) throws DetailedServiceException

	{
		Values out = in;

		// [i] field:0:required name
		// [o] field:0:required name
		// [o] field:0:required text
		// [o] field:0:required url
		// [o] record:1:required tabs
		// [o] - field:0:required name
		// [o] - field:0:required url
		// [o] - field:0:required help
		// [o] field:0:required highlight

		wmMQAdapter.dumpValues("getMenu", in);

		Locale locale = getSessionLocale();
		String adapterTypeName = in.getString("name");
		String adapterTypeDisplay = adapterTypeName;
		AdapterType adapterType = null;

		if(adapterTypeName == null)
			return out;
		try {

			adapterType = AdapterTypeManager.getAdapterType(adapterTypeName);
			adapterTypeDisplay = adapterType.getAdapterDisplayName(locale);
		} catch (Exception ex) {
			WmMQAdapterUtils.logException(ex);
			// ex.printStackTrace();
			// throw new DetailedServiceException(ARTCodes.FAC_ART,
			// ARTCodes.ART9,
			// new String[] {adapterTypeName},
			// ARTCodes.ART_BUNDLE_NAME,
			// ex);

		}

		String dspDirectory = "";
		int version = com.wm.app.b2b.server.Build.getInternalVersion();
		
		if (version < 21){
			//Below version IS_9.10 
			dspDirectory = "/WmMQAdapter/pubIS99";
		}else{
			//IS_9.10 version and above 
			dspDirectory = "/WmMQAdapter";
		}
		
		String connDSPDir = dspDirectory;
		
		if(version >= VERSION_107){
			// IS 10.7 or above version
			connDSPDir = "/WmMQAdapter/pubIS107";
		}
		
		// build the tabset; The first tab is the highlighted by default.
		Object t[][][] = { {
				{ "name",
						AdapterRuntimeGlobals.UI_RESOURCE_MANAGER
								.getStringResource("Deployment.Service.connectionNameMenu", locale) },
				{ "url", connDSPDir + "/ListResources.dsp?" + ADAPTER_TYPE_NAME_KEY + "="
						+ URLEncoder.encode(adapterTypeName) + "&" + AdapterRuntimeGlobals.DSPNAME + "="
						+ URLEncoder.encode(AdapterRuntimeGlobals.LISTRESOURCES) },
				{ "tabhelp", URLEncoder.encode("/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_Conn") },
				// {"tabhelp",
				// "/WmART/doc/OnlineHelp/FRM_TAB_ListResources.html"},
				},
				// {
				// {"name",
				// AdapterRuntimeGlobals.UI_RESOURCE_MANAGER.getStringResource("Deployment.Service.pollingNotificationNameMenu",
				// locale)},
				// {"url",
				// "/WmART/ListPollingNotifications.dsp?"+ADAPTER_TYPE_NAME_KEY+"="+URLEncoder.encode(adapterTypeName)+"&"+AdapterRuntimeGlobals.DSPNAME+"="+URLEncoder.encode(AdapterRuntimeGlobals.LISTPOLLINGNOTIFICATIONS)},
				// {"tabhelp",
				// URLEncoder.encode("/WmART/doc/OnlineHelp/FRM_TAB_ListPollingNotification.html")},
				// },
				{ { "name",
						AdapterRuntimeGlobals.UI_RESOURCE_MANAGER
								.getStringResource("Deployment.Service.listenerNameMenu", locale) },
						{ "url", dspDirectory + "/ListListeners.dsp?" + ADAPTER_TYPE_NAME_KEY + "="
								+ URLEncoder.encode(adapterTypeName) + "&" + AdapterRuntimeGlobals.DSPNAME + "="
								+ URLEncoder.encode(AdapterRuntimeGlobals.LISTLISTENERS) },
						// {"tabhelp",
						// URLEncoder.encode("/WmART/doc/OnlineHelp/FRM_TAB_ListListeners.html")},
						{ "tabhelp",
								URLEncoder
										.encode("/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_List") }, },
				{ { "name",
						AdapterRuntimeGlobals.UI_RESOURCE_MANAGER
								.getStringResource("Deployment.Service.listenerNotificationNameMenu", locale) },
						{ "url", "/WmART/ListListenerNotifications.dsp?" + ADAPTER_TYPE_NAME_KEY + "="
								+ URLEncoder.encode(adapterTypeName) + "&" + AdapterRuntimeGlobals.DSPNAME + "="
								+ URLEncoder.encode(AdapterRuntimeGlobals.LISTLISTENERNOTIFICATIONS) },
						// {"tabhelp",
						// URLEncoder.encode("/WmART/doc/OnlineHelp/FRM_TAB_ListListenerNotification.html")},
						{ "tabhelp",
								URLEncoder
										.encode("/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_ListNotifs") }, },
				{ { "name", "IBM webMethods Adapter for MQ-Level Tracing" }, { "url",
					dspDirectory + "/Tracing.dsp?" + ADAPTER_TYPE_NAME_KEY + "=" + URLEncoder.encode(adapterTypeName)
								+ "&" + AdapterRuntimeGlobals.DSPNAME + "=" + URLEncoder.encode(".TRACING") },
						{ "tabhelp",
								URLEncoder
										.encode("/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_Trace") }, },
				{ { "name",
						AdapterRuntimeGlobals.UI_RESOURCE_MANAGER.getStringResource("Deployment.Service.aboutMenu",
								locale) },
						// Trax 1-VHTSV. Use the WmART About page
						// {"url",
						// "/WmMQAdapter/About.dsp?"+ADAPTER_TYPE_NAME_KEY+"="+URLEncoder.encode(adapterTypeName)+"&"+AdapterRuntimeGlobals.DSPNAME+"="+URLEncoder.encode(AdapterRuntimeGlobals.ABOUT)},
						// We are pointing it back to the WmMQAdapter's about
						// page as it is now maintaing the about pages for
						// individual IS versions (6.1 and 6.5)
						{ "url", dspDirectory + "/About.dsp?" + ADAPTER_TYPE_NAME_KEY + "="
								+ URLEncoder.encode(adapterTypeName) + "&" + AdapterRuntimeGlobals.DSPNAME + "="
								+ URLEncoder.encode(AdapterRuntimeGlobals.ABOUT) },
						{ "tabhelp", URLEncoder
								.encode("/WmMQAdapter/doc/OnlineHelp/wwhelp.htm?context=Help&topic=MQ_About") }, } };
		String url = connDSPDir + "/ListResources.dsp?" + ADAPTER_TYPE_NAME_KEY + "=" + URLEncoder.encode(adapterTypeName)+ "&" + AdapterRuntimeGlobals.DSPNAME + "=" + URLEncoder.encode(AdapterRuntimeGlobals.LISTRESOURCES);
		boolean isCSRFGuardEnabled = false;
		try {
			IData input = IDataFactory.create();
			IData output = com.wm.app.b2b.server.Service.doInvoke(NSName.create("wm.server.csrfguard:isCSRFGuardEnabled"), input);
			isCSRFGuardEnabled = ValuesEmulator.getBoolean(output, "isEnabled");
		} catch (Exception e) {
			WmMQAdapterUtils.logException(e);
		}
		if(isCSRFGuardEnabled) {
			try {
				IData input = IDataFactory.create();
				IData output = com.wm.app.b2b.server.Service.doInvoke(NSName.create("wm.server.csrfguard:getCSRFSecretToken"), input);
				String token = ValuesEmulator.getString(output, "TOKEN_VALUE");
				if(token != null)
				{
					url += "&" + "secureCSRFToken" + "=" + token;
				}
			} catch (Exception e) {
				WmMQAdapterUtils.logException(e);
			}
		}
		Values tabs[] = new Values[t.length];
		for (int i = 0; i < t.length; i++)
			tabs[i] = new Values(t[i]);

		out.put("name", adapterTypeName);
		out.put("text", adapterTypeDisplay);
		out.put("url",url);
		out.put("tabs", tabs);
		out.put("highlight", AdapterRuntimeGlobals.UI_RESOURCE_MANAGER
				.getStringResource("Deployment.Service.connectionNameMenu", locale));
		return out;
	}

	// Returns the current session's locale.
	private static Locale getSessionLocale() {
		Session s = com.wm.app.b2b.server.Service.getSession();
		Locale l = null;
		if (s != null)
			l = s.getLocale();
		if (l != null)
			return l;
		else
			return Locale.US;
	}

	/**
	 * This service, if the listener type is not a multi-queue listener then
	 * just creates a new listener node. If the listener type is a multi-queue
	 * listener, creates the multi-queue listener and also a set of child
	 * listeners one for each queue mentioned in the connection alias.
	 * 
	 * @param pipeline
	 * @throws DetailedServiceException
	 * @throws DetailedException
	 */
	public static void saveMultiQueueListener(IData pipeline) throws DetailedServiceException, DetailedException {
		String listenerTypeName = (String) IDataExt.get(pipeline, AdapterRuntimeGlobals.LISTENER_TEMPLATE);
		if (!wmMQAdapterConstant.MULTI_QUEUE_LISTENER.equals(listenerTypeName)) {
			ARTAdmin.saveListener(pipeline);
			return;
		}

		// Create Multi-Queue Parent Listener
		String multiQueueListenerNodeName = (String) IDataExt.get(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME);

		ARTAdmin.validateListenerName(multiQueueListenerNodeName, pipeline);

		// Create the multi-queue listener
		multiQueueListenerNodeName = (String) IDataExt.get(pipeline, "listenerFolderName") + ":"
				+ multiQueueListenerNodeName;
		IDataExt.set(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME, multiQueueListenerNodeName);

		IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.CHILD_LISTENER,
				"false");
		IDataExt.set(pipeline,
				ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.CHILD_LISTENER + "List",
				new String[] { "false" });
		IDataExt.set(pipeline,
				ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.INHERIT_PARENT_PROPERTIES,
				"false");
		IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
				+ wmMQAdapterConstant.INHERIT_PARENT_PROPERTIES + "List", new String[] { "false" });
		IDataExt.set(pipeline,
				ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME, "");
		IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
				+ wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME + "List", new String[] { "" });
		IDataExt.set(pipeline,
				ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME, "");
		IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
				+ wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME + "List", new String[] { "" });

		ListenerManager.createListener(pipeline);
		//IDataExt.add(pipeline, "listenerName", multiQueueListenerNodeName);
		//ExtendedListenerUtils.updateListenerNode(pipeline);
		
		ARTAdmin.setListenerProperties(multiQueueListenerNodeName, pipeline);

		try {
			// Create the child listeners
			createChildListenerNodes(multiQueueListenerNodeName, pipeline, false);
		} catch (DetailedException de) {
			// Delete the parent listener node that is already created
			IData deleteListener = IDataFactory.create();
			IDataExt.set(deleteListener, AdapterRuntimeGlobals.LISTENER_NODE_NAME, multiQueueListenerNodeName);
			ListenerManager.deleteAdapterListener(deleteListener);

			throw de;
		} catch (DetailedServiceException dse) {
			// Delete the parent listener node that is already created
			IData deleteListener = IDataFactory.create();
			IDataExt.set(deleteListener, AdapterRuntimeGlobals.LISTENER_NODE_NAME, multiQueueListenerNodeName);
			ListenerManager.deleteAdapterListener(deleteListener);

			throw dse;
		}
	}

	/**
	 * This methods creates ot updates all the child listeners of the given
	 * parent listener.
	 * 
	 * @param multiQueueListenerNodeName
	 * @param pipeline
	 * @param updateIfAlreadyExist
	 * @throws DetailedException
	 * @throws DetailedServiceException
	 */
	private static List createChildListenerNodes(String multiQueueListenerNodeName, IData pipeline,
			boolean updateIfAlreadyExist) throws DetailedException, DetailedServiceException {
		// Create multi-queue child listeners one for each queue defined in the
		// connection alias
		String newConnectionDataNodeName = (String) IDataExt.get(pipeline, ListenerNode.CONN_DATA_NODE_NAME_KEY);
		IData connectionProperties = ConnectionDataNodeManager.getConnectionDataNode(newConnectionDataNodeName)
				.getConnectionProperties();
		// Iterate through the queues in the queue name parameter and
		// create a multi-queue child listener node for every queue
		StringTokenizer queueNames = new StringTokenizer(
				(String) IDataExt.get(connectionProperties, wmMQAdapterConstant.QUEUE_NAME));

		IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.CHILD_LISTENER,
				"true");
		IDataExt.set(pipeline,
				ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.CHILD_LISTENER + "List",
				new String[] { "true" });
		IDataExt.set(pipeline,
				ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME,
				multiQueueListenerNodeName);
		IDataExt.set(
				pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
						+ wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME + "List",
				new String[] { multiQueueListenerNodeName });
		IDataExt.set(pipeline,
				ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.INHERIT_PARENT_PROPERTIES,
				"true");
		IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
				+ wmMQAdapterConstant.INHERIT_PARENT_PROPERTIES + "List", new String[] { "true" });

		List newListenerNodeNames = new ArrayList();
		try {
			while (queueNames.hasMoreTokens()) {
				String queueName = queueNames.nextToken();
				String childListenerNodeName = wmMQMultiQueueListener
						.getValidChildListenerName(multiQueueListenerNodeName, queueName);// multiQueueListenerNodeName
																							// +
																							// "_"
																							// +
																							// queueName;
				ListenerNode childListenerNode = null;
				try {
					childListenerNode = ListenerManager.getManager().getListenerNode(childListenerNodeName);
					if (childListenerNode != null && wmMQAdapterConstant.MULTI_QUEUE_LISTENER
							.equals(childListenerNode.getListenerClassName())) {
						wmMQMultiQueueListener childListener = (wmMQMultiQueueListener) childListenerNode.getListener();
						if (!childListener.isChildListener()
								|| !childListener.getParentListenerNodeName().equalsIgnoreCase(multiQueueListenerNodeName)) {
							childListenerNode = null;
						}
					} else {
						childListenerNode = null;
					}
				} catch (DetailedException e) {
					// Child listener for the queue not found. Ignore the
					// exception.
					childListenerNode = null;
					WmMQAdapterUtils.logException(e);
				}
				IDataExt.set(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME, childListenerNodeName);
				IDataExt.set(pipeline,
						ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME,
						queueName);
				IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
						+ wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME + "List", new String[] { queueName });
				String threadConfigurator = (String)ValuesEmulator.get(pipeline, "threadConfiguration");
				Object threadCount = ValuesEmulator.get(pipeline, "threadConunt");
				IDataExt.set(pipeline, "threadConfiguration", threadConfigurator);
				IDataExt.set(pipeline,"threadConunt", threadCount);
				
				IDataExt.set(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME, childListenerNodeName);
				IDataExt.set(pipeline,
						ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME,
						queueName);
				
				if (childListenerNode != null && updateIfAlreadyExist) {
					// Update the node if it already exists
					if (((wmMQMultiQueueListener) childListenerNode.getListener()).isInheritParentProperties()) {
						ARTAdmin.setListenerProperties(childListenerNodeName, pipeline);
					} else {
						// If the connection data node name has been changed
						// then update the
						// child listener with the new connection data node
						// name.
						if (!childListenerNode.getConnectionDataNodeName().equals(newConnectionDataNodeName)) {
							IData clonedProperties = IDataUtil.clone(childListenerNode.getListenerProperties());
							IDataExt.each(clonedProperties, new IDataListenerPropSwapper(clonedProperties));

							IDataExt.set(clonedProperties, ListenerNode.CONN_DATA_NODE_NAME_KEY,
									newConnectionDataNodeName);
							IDataExt.set(clonedProperties, _RETRY_LIMIT, "" + childListenerNode.getRetryMaxFailures());
							IDataExt.set(clonedProperties, _RETRY_BACKOFF,
									"" + childListenerNode.getSecondsBetweenRetries());

							ARTAdmin.setListenerProperties(childListenerNode.getNSName().getFullName(),
									clonedProperties);
						}
					}
				} else {
					// Create if does not exist or always create
					ListenerManager.createListener(pipeline);
					ListenerNode ln = ListenerManager.getManager().getListenerNode(childListenerNodeName);
					
					ARTAdmin.setListenerProperties(childListenerNodeName, pipeline);
					
					ARTAdmin.setListenerProperties(childListenerNodeName, pipeline);
					
					if (ln != null && wmMQAdapterConstant.MULTI_QUEUE_LISTENER
							.equals(ln.getListenerClassName())) {
						wmMQMultiQueueListener childMultiQueueListener = (wmMQMultiQueueListener) ln
								.getListener();
						childMultiQueueListener.setParentListenerNodeName(multiQueueListenerNodeName);
						childMultiQueueListener.setChildListener(true);
						//ln.setSuspendStatus(true);
					}
					
					newListenerNodeNames.add(childListenerNodeName);
				}
			}
		} catch (DetailedException de) {
			// Rollback all the listener nodes that are created
			IData deleteListener = IDataFactory.create();
			for (Iterator it = newListenerNodeNames.iterator(); it.hasNext();) {
				String newChildListenerNodeName = (String) it.next();
				IDataExt.set(deleteListener, AdapterRuntimeGlobals.LISTENER_NODE_NAME, newChildListenerNodeName);
				ListenerManager.deleteAdapterListener(deleteListener);
			}

			throw de;
		} catch (DetailedServiceException dse) {
			// Rollback all the listener nodes that are created
			IData deleteListener = IDataFactory.create();
			for (Iterator it = newListenerNodeNames.iterator(); it.hasNext();) {
				String newChildListenerNodeName = (String) it.next();
				IDataExt.set(deleteListener, AdapterRuntimeGlobals.LISTENER_NODE_NAME, newChildListenerNodeName);
				ListenerManager.deleteAdapterListener(deleteListener);
			}

			throw dse;
		}
		return newListenerNodeNames;
	}

	/**
	 * This service updates the multi-queue listeners both parent and child
	 * listeners.
	 * 
	 * @param pipeline
	 * @throws DetailedServiceException
	 * @throws DetailedException
	 */
	public static void updateMultiQueueListenerData(IData pipeline) throws DetailedServiceException, DetailedException {
		String listenerNodeName = (String) IDataExt.get(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME);
		ARTAdmin.validateListenerName(listenerNodeName, pipeline);

		ListenerNode listenerNode = ListenerManager.getManager().getListenerNode(listenerNodeName);
		if (wmMQAdapterConstant.MULTI_QUEUE_LISTENER.equals(listenerNode.getListenerClassName())) {
			// Find the child listeners and change the properties of each one of
			// them
			IData clonedPipeline = IDataUtil.clone(pipeline);

			wmMQMultiQueueListener multiQueueListener = (wmMQMultiQueueListener) listenerNode.getListener();
			IDataExt.set(pipeline, "requiresConnection", "yes");
			IDataExt.set(pipeline, "addSuspended", "yes");
			

			if (!multiQueueListener.isChildListener()) {
				// For a parent listener check if the connection alias has been
				// changed
				// If so, delete all the child listeners and recreate the child
				// listeners
				// depending on the changed connection alias
				String originalConnectionAlias = listenerNode.getConnectionDataNodeName();
				String changedConnectionAlias = (String) IDataExt.get(clonedPipeline,
						ListenerNode.CONN_DATA_NODE_NAME_KEY);
				// get the listener node data and add it to pipeline
				IData listnerNodeData = listenerNode.getAsData();

				String packageNAme = ValuesEmulator.getString(listnerNodeData, "node_pkg");
				ValuesEmulator.put(pipeline, "packageName", packageNAme);
				

				if (originalConnectionAlias.equals(changedConnectionAlias.trim())) {
					// If the connection alias has not been changed then update
					// the parent listener and
					// all the child listeners for which inherit parent
					// properties flag is set to true.
					// In this case also check if any new queues have been added
					// or deleted
					// from the connection alias. If so either create or delete
					// corresponding child listener.
					String encodedListenerData = ValuesEmulator.getString(listnerNodeData, "IRTNODE_PROPERTY");
					try {
						java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(
								com.wm.util.Base64.decode(encodedListenerData.getBytes("UTF8")));
						com.wm.util.coder.IDataBinCoder coder = new com.wm.util.coder.IDataBinCoder();
						IData value = coder.decode(bis);
						IDataCursor valueCurser = value.getCursor();
					
						while (valueCurser.next()) {
							String key =  valueCurser.getKey();
							if(!IDataExt.hasKey(pipeline, key)){
								IDataExt.add(pipeline, key, valueCurser.getValue());
					}
						}				
						IData listProp =  (IData)ValuesEmulator.get(value, "listenerProperties");
					IDataCursor listenerCursor = listProp.getCursor();
					
					while (listenerCursor.next()) {
							String key = listenerCursor.getKey();
							Object valued = listenerCursor.getValue();
							if(!IDataExt.hasKey(pipeline, ".CPROP."+key)){
								ValuesEmulator.put(pipeline,".CPROP."+ key, String.valueOf(valued));
					}
							
						}
					} catch (Exception e) {
						WmMQAdapterUtils.logException(e);
					}
					Map childListenersBeforeUpdate = searchAllChildListenerNodes(listenerNode);
					IDataExt.set(pipeline, "listenerTypeName", ValuesEmulator.get(pipeline, "listenerClassName"));
					ARTAdmin.setListenerProperties(listenerNodeName, pipeline);

					// Update the parent listener
					IDataExt.set(pipeline,
							ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.CHILD_LISTENER,
							"false");
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.CHILD_LISTENER + "List", new String[] { "false" });
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.INHERIT_PARENT_PROPERTIES, "false");
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.INHERIT_PARENT_PROPERTIES + "List", new String[] { "false" });
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME, "");
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME + "List", new String[] { "" });
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME, "");
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME + "List", new String[] { "" });

					// Create or Update the child listeners
					// Creation is necessary if a a Queue is added to the
					// connection alias
					IDataExt.set(clonedPipeline, ListenerNode.PACKAGE_NAME, listenerNode.getPackageName());
					IDataExt.set(clonedPipeline, ListenerNode.PACKAGE_NAME + "List",
							new String[] { listenerNode.getPackageName() });
					IDataExt.set(clonedPipeline, AdapterRuntimeGlobals.LISTENER_TEMPLATE,
							wmMQAdapterConstant.MULTI_QUEUE_LISTENER);
					IDataExt.set(clonedPipeline, AdapterRuntimeGlobals.LISTENER_TEMPLATE + "List",
							new String[] { wmMQAdapterConstant.MULTI_QUEUE_LISTENER });
					
					
					createChildListenerNodes(listenerNodeName, pipeline, true);
					
					Map childListenersAfterUpdate = getChildListenerNodes(listenerNode);

					// Delete the child listeners that are for the queues which
					// are removed from the updated connection alias
					for (Iterator it = childListenersBeforeUpdate.keySet().iterator(); it.hasNext();) {
						String queueName = (String) it.next();
						if (!childListenersAfterUpdate.containsKey(queueName)) {
							ListenerNode tobeDeletedNode = (ListenerNode) childListenersBeforeUpdate.get(queueName);
							IDataExt.set(clonedPipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME,
									tobeDeletedNode.getNSName().getFullName());
							ARTAdmin.deleteListener(clonedPipeline);
						}
					}
				} else {
					// Get all the child listener nodes which were configured
					// for the old connection alias
					Map childListenersBeforeUpdate = searchAllChildListenerNodes(listenerNode);

					// Update the parent listener
					IDataExt.set(pipeline,
							ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.CHILD_LISTENER,
							"false");
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.CHILD_LISTENER + "List", new String[] { "false" });
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.INHERIT_PARENT_PROPERTIES, "false");
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.INHERIT_PARENT_PROPERTIES + "List", new String[] { "false" });
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME, "");
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME + "List", new String[] { "" });
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME, "");
					IDataExt.set(pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
							+ wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME + "List", new String[] { "" });
					ARTAdmin.setListenerProperties(listenerNodeName, pipeline);
					
					

					// Create or Update the child listeners
					// Creation is necessary if a new Queue is added to the
					// connection alias
					IDataExt.set(clonedPipeline, ListenerNode.PACKAGE_NAME, listenerNode.getPackageName());
					IDataExt.set(clonedPipeline, ListenerNode.PACKAGE_NAME + "List",
							new String[] { listenerNode.getPackageName() });
					IDataExt.set(clonedPipeline, AdapterRuntimeGlobals.LISTENER_TEMPLATE,
							wmMQAdapterConstant.MULTI_QUEUE_LISTENER);
					IDataExt.set(clonedPipeline, AdapterRuntimeGlobals.LISTENER_TEMPLATE + "List",
							new String[] { wmMQAdapterConstant.MULTI_QUEUE_LISTENER });
					createChildListenerNodes(listenerNodeName, pipeline, true);

					Map childListenersAfterUpdate = getChildListenerNodes(listenerNode);

					// Delete the child listeners that are for the queues which
					// are removed from the updated connection alias
					for (Iterator it = childListenersBeforeUpdate.keySet().iterator(); it.hasNext();) {
						String queueName = (String) it.next();
						if (!childListenersAfterUpdate.containsKey(queueName)) {
							ListenerNode tobeDeletedNode = (ListenerNode) childListenersBeforeUpdate.get(queueName);
							IDataExt.set(clonedPipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME,
									tobeDeletedNode.getNSName().getFullName());
							ARTAdmin.deleteListener(clonedPipeline);
						}
					}
				}
			} else {
				// Update the child listener node
				IDataExt.set(pipeline,
						ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.CHILD_LISTENER, "true");
				IDataExt.set(pipeline,
						ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.CHILD_LISTENER + "List",
						new String[] { "true" });
				IDataExt.set(pipeline,
						ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME,
						multiQueueListener.getParentListenerNodeName());
				IDataExt.set(
						pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
								+ wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME + "List",
						new String[] { multiQueueListener.getParentListenerNodeName() });
				IDataExt.set(pipeline,
						ListenerProperties.CONNECTION_PROPERTIES_PREFIX + wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME,
						multiQueueListener.getChildListenerQueueName());
				IDataExt.set(
						pipeline, ListenerProperties.CONNECTION_PROPERTIES_PREFIX
								+ wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME + "List",
						new String[] { multiQueueListener.getChildListenerQueueName() });
				IDataExt.set(pipeline, ListenerNode.CONN_DATA_NODE_NAME_KEY, multiQueueListener.getConnDataNodeName());
				IDataExt.set(pipeline, ListenerNode.CONN_DATA_NODE_NAME_KEY + "List",
						new String[] { multiQueueListener.getConnDataNodeName() });

				ARTAdmin.setListenerProperties(listenerNodeName, pipeline);

				// If the Inherit Parent properties property is set to true,
				// copy the parent node's properties to the child node
				if (multiQueueListener.isInheritParentProperties()) {
					// Copy the parent node's properties and change the
					// properties that are specific to a child listener
					ListenerNode parentListenerNode = ListenerManager.getManager()
							.getListenerNode(multiQueueListener.getParentListenerNodeName());
					IData clonedParentProperties = IDataUtil.clone(parentListenerNode.getListenerProperties());
				
					IDataExt.set(clonedParentProperties, wmMQAdapterConstant.CHILD_LISTENER, "true");
					IDataExt.set(clonedParentProperties, wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME,
							multiQueueListener.getParentListenerNodeName());
					IDataExt.set(clonedParentProperties, wmMQAdapterConstant.INHERIT_PARENT_PROPERTIES, "true");
					IDataExt.set(clonedParentProperties, wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME,
							multiQueueListener.getChildListenerQueueName());

					IDataExt.each(clonedParentProperties, new IDataListenerPropSwapper(clonedParentProperties));

					IDataExt.set(clonedParentProperties, AdapterRuntimeGlobals.LISTENER_NODE_NAME, listenerNodeName);
					IDataExt.set(clonedParentProperties, _RETRY_LIMIT, "" + parentListenerNode.getRetryMaxFailures());
					IDataExt.set(clonedParentProperties, _RETRY_BACKOFF,
							"" + parentListenerNode.getSecondsBetweenRetries());
					IDataExt.set(clonedParentProperties, ListenerNode.CONN_DATA_NODE_NAME_KEY,
							parentListenerNode.getConnectionDataNodeName());

					ARTAdmin.setListenerProperties(listenerNodeName, clonedParentProperties);
				}
			}
		} else {
			// Update the listener node
			ARTAdmin.setListenerProperties(listenerNodeName, pipeline);
		}
	}

	/**
	 * This service updates the status of the multi-queue listener. If given
	 * listener is a parent listener the status update is also applied to all
	 * the child listeners.
	 * 
	 * @param pipeline
	 * @throws DetailedServiceException
	 * @throws DetailedException
	 */
	public static void setMultiQueueListenerStatus(IData pipeline)
			throws DetailedServiceException, DetailedException, Exception {

		String listenerNodeName = (String) IDataExt.get(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME);
		ARTAdmin.validateListenerName(listenerNodeName, pipeline);

		// Find the child listeners and change the status of each one of them
		ListenerNode listenerNode = ListenerManager.getManager().getListenerNode(listenerNodeName);
		Map childListenerNodes = searchAllChildListenerNodes(listenerNode);
		for (Iterator it = childListenerNodes.values().iterator(); it.hasNext();) {
			String childListenerNodeName = ((ListenerNode) it.next()).getNSName().getFullName();
			IDataExt.set(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME, childListenerNodeName);
			IDataExt.set(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME + "List", childListenerNodeName);
			ARTAdmin.setListenerStatus(pipeline);
		}

		// Set the status of the parent listener
		IDataExt.set(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME, listenerNodeName);
		IDataExt.set(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME + "List", listenerNodeName);
		ARTAdmin.setListenerStatus(pipeline);
	}

	/**
	 * This service deletes a multi-queue listener. If the given listener is a
	 * parent listener all its child listeners are also deleted.
	 * 
	 * @param pipeline
	 * @throws DetailedServiceException
	 * @throws DetailedException
	 */
	public static void deleteMultiQueueListener(IData pipeline) throws DetailedServiceException, DetailedException {

		String listenerNodeName = (String) IDataExt.get(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME);
		ARTAdmin.validateListenerName(listenerNodeName, pipeline);

		// Find all the child listeners associated with this parent listener and
		// delete each one of them
		ListenerNode listenerNode = ListenerManager.getManager().getListenerNode(listenerNodeName);
		Map childListenerNodes = searchAllChildListenerNodes(listenerNode);
		for (Iterator it = childListenerNodes.values().iterator(); it.hasNext();) {
			String childListenerNodeName = ((ListenerNode) it.next()).getNSName().getFullName();
			IDataExt.set(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME, childListenerNodeName);
			IDataExt.set(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME + "List", childListenerNodeName);
			ARTAdmin.deleteListener(pipeline);
		}

		// Delete the parent listener
		IDataExt.set(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME, listenerNodeName);
		IDataExt.set(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME + "List", listenerNodeName);
		ARTAdmin.deleteListener(pipeline);
	}

	/**
	 * Given the listener node name this service return whether the listener is
	 * a multi-queue listener and if it is a multi-queue listener, whether it is
	 * a child listener
	 * 
	 * @param pipeline
	 * @throws DetailedServiceException
	 * @throws DetailedException
	 */
	public static void isMultiQueueChildListener(IData pipeline)
			throws DetailedServiceException, DetailedException, Exception {
		String listenerNodeName = ((NSName) IDataExt.get(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME))
				.getFullName();
		ARTAdmin.validateListenerName(listenerNodeName, pipeline);

		ListenerNode listenerNode = ListenerManager.getManager().getListenerNode(listenerNodeName);
		if (wmMQAdapterConstant.MULTI_QUEUE_LISTENER.equals(listenerNode.getListenerClassName())) {
			IDataExt.set(pipeline, "isMultiQueueListener", "true");

			wmMQMultiQueueListener multiQueueListener = (wmMQMultiQueueListener) listenerNode.getListener();
			if (multiQueueListener.isChildListener()) {
				IDataExt.set(pipeline, "isChildListener", "true");
			} else {
				IDataExt.set(pipeline, "isChildListener", "false");
			}
		} else {
			IDataExt.set(pipeline, "isMultiQueueListener", "false");
		}
	}

	/**
	 * Given the listener node name this service return whether the multi-queue
	 * child listener is inheriting the notifications from its parent or not.
	 * 
	 * @param pipeline
	 * @throws DetailedServiceException
	 * @throws DetailedException
	 */
	public static void isChildListenerInheritingNotifications(IData pipeline)
			throws DetailedServiceException, DetailedException, Exception {
		String listenerNodeName = (String) IDataExt.get(pipeline, AdapterRuntimeGlobals.LISTENER_NODE_NAME);
		ARTAdmin.validateListenerName(listenerNodeName, pipeline);

		ListenerNode listenerNode = ListenerManager.getManager().getListenerNode(listenerNodeName);
		if (wmMQAdapterConstant.MULTI_QUEUE_LISTENER.equals(listenerNode.getListenerClassName())) {
			wmMQMultiQueueListener multiQueueListener = (wmMQMultiQueueListener) listenerNode.getListener();
			if (multiQueueListener.isChildListener() && listenerNode.getRegisteredNotifications().isEmpty()) {
				IDataExt.set(pipeline, "isInheritingNotifications", "true");
			} else {
				IDataExt.set(pipeline, "isInheritingNotifications", "false");
			}
		} else {
			IDataExt.set(pipeline, "isInheritingNotifications", "false");
		}
	}

	/**
	 * Gets the child listener nodes of a parent listener by iterating through
	 * all the listener nodes and including only the child nodes that have the
	 * given node as the parent.
	 * 
	 * @param listenerNode
	 * @return
	 * @throws DetailedException
	 */
	private static Map searchAllChildListenerNodes(ListenerNode listenerNode) throws DetailedException {

		Map childListenerNodes = new HashMap();

		String listenerNodeName = listenerNode.getNSName().getFullName();
		if (wmMQAdapterConstant.MULTI_QUEUE_LISTENER.equals(listenerNode.getListenerClassName())) {
			wmMQMultiQueueListener multiQueueListener = (wmMQMultiQueueListener) listenerNode.getListener();

			if (!multiQueueListener.isChildListener()) {
				String childNodeNamePrefix = listenerNodeName + "_";
				String[] allNodes = ListenerManager.getListenerNames();
				for (int i = 0; i < allNodes.length; i++) {
					if (allNodes[i].startsWith(childNodeNamePrefix)) {
						try {
							ListenerNode childListenerNode = ListenerManager.getManager().getListenerNode(allNodes[i]);
							if (wmMQAdapterConstant.MULTI_QUEUE_LISTENER
									.equals(childListenerNode.getListenerClassName())) {
								wmMQMultiQueueListener childMultiQueueListener = (wmMQMultiQueueListener) childListenerNode
										.getListener();
								if (childMultiQueueListener.isChildListener() && listenerNodeName
										.equals(childMultiQueueListener.getParentListenerNodeName())) {
									childListenerNodes.put(childMultiQueueListener.getChildListenerQueueName(),
											childListenerNode);
								}
							}
						} catch (DetailedException e) {
							// Child listener for the queue not found. Ignore
							// the exception.
						}
					}
				}
			}
		}

		return childListenerNodes;
	}

	/**
	 * Gets all the children of a given node depending on the queues defined on
	 * the connection alias.
	 * 
	 * @param listenerNode
	 * @return
	 * @throws DetailedException
	 */
	private static Map getChildListenerNodes(ListenerNode listenerNode) throws DetailedException {

		Map childListenerNodes = new HashMap();

		String listenerNodeName = listenerNode.getNSName().getFullName();
		if (wmMQAdapterConstant.MULTI_QUEUE_LISTENER.equals(listenerNode.getListenerClassName())) {
			wmMQMultiQueueListener multiQueueListener = (wmMQMultiQueueListener) listenerNode.getListener();

			if (!multiQueueListener.isChildListener()) {
				IData connectionProperties = ConnectionDataNodeManager
						.getConnectionDataNode(listenerNode.getConnectionDataNodeName()).getConnectionProperties();
				// Iterate through the queues in the queue name parameter build
				// the child listener node name
				StringTokenizer queueNames = new StringTokenizer(
						(String) IDataExt.get(connectionProperties, wmMQAdapterConstant.QUEUE_NAME));
				while (queueNames.hasMoreTokens()) {
					String queueName = queueNames.nextToken();
					String childListenerNodeName = wmMQMultiQueueListener.getValidChildListenerName(listenerNodeName,
							queueName);
					ListenerNode childListenerNode = null;
					try {
						childListenerNode = ListenerManager.getManager().getListenerNode(childListenerNodeName);
					} catch (DetailedException e) {
						// Child listener for the queue not found. Ignore the
						// exception.
					}
					if (childListenerNode != null && wmMQAdapterConstant.MULTI_QUEUE_LISTENER
							.equals(childListenerNode.getListenerClassName())) {
						wmMQMultiQueueListener childMultiQueueListener = (wmMQMultiQueueListener) childListenerNode
								.getListener();
						if (childMultiQueueListener.isChildListener()
								&& childMultiQueueListener.getParentListenerNodeName().equals(listenerNodeName)) {
							childListenerNodes.put(queueName, childListenerNode);
						}
					}
				}
			}
		}

		return childListenerNodes;
	}

	/***********************************************************************************/

	// Internal class used to sort Values objects based on the contents of a
	// single field
	public static class SortableEntry implements Comparator {

		private String _name;
		private Values _value;

		public SortableEntry(String name, Values value) {
			_value = value;
			_name = name;
		}

		/**
		 * method compare This method implements the Comparator interface
		 * 
		 * @param o1
		 *            an Object
		 * @param o2
		 *            another Object
		 * 
		 * @return -1 if e1 < e2 ; 0 if e1 == e2 ; 1 if e1 > e2
		 * 
		 */
		public int compare(Object o1, Object o2) {
			String aliasName1 = ((SortableEntry) o1)._name;
			String aliasName2 = ((SortableEntry) o2)._name;

			return aliasName1.compareToIgnoreCase(aliasName2);
		}

		/**
		 * method getValue Return the Values associated with this object
		 * 
		 * @return a Values Object
		 * 
		 */
		public Values getValue() {
			return _value;
		}

		/**
		 * method getName Return the name associated with this object
		 * 
		 * @return a String
		 * 
		 */
		public String getName() {
			return _name;
		}

	}

	/**
	 * Prepends '.CPROP.' to adapter-specific listener key names in _basevals.
	 * It also forces the values of these same properties to String.
	 */
	static class IDataListenerPropSwapper implements com.wm.pkg.art.data.IDataExt.Proc {
		IData _baseVals = null;

		IDataListenerPropSwapper(IData baseVals) {
			_baseVals = baseVals;
		}

		// Replace the given key/value pair in the baseline prop values
		public void sub(String key, Object value) {
			// Skip the built-in properties for listeners
			if (!(key.equals(_RETRY_LIMIT) || key.equals(_RETRY_BACKOFF)
					|| key.equals(ListenerNode.CONN_DATA_NODE_NAME_KEY))) {
				IDataExt.remove(_baseVals, key);
				IDataExt.set(_baseVals, ListenerProperties.CONNECTION_PROPERTIES_PREFIX + key, value.toString());
				IDataExt.set(_baseVals, ListenerProperties.CONNECTION_PROPERTIES_PREFIX + key + "List",
						new String[] { value.toString() });
			}
		}
	}

	/**
	 * This service will read the SSL watt property and append it to the
	 * pipeline
	 * 
	 * @param pipeline
	 * @throws DetailedServiceException
	 */
	public static void getSSLWattProperty(IData pipeline) throws DetailedServiceException {
		String sslEnabled = "false";
		// wmio ssl
		if(wmMQAdapterConstant.IBMMQ_IN_ILIVE || wmMQAdapterConstant.IBMMQ_IN_ORIGIN){
			sslEnabled = "true";
		}
		else {
		String temp = System.getProperty("watt.WmMQAdapter.Connection.CiphersList", "");
		if(temp != null && temp.trim().length() > 0) {
			sslEnabled = "true";
		}
		else
			sslEnabled = com.wm.util.Config.getProperty("false", "watt.WmMQAdapter.SSL.Support");
		}
		
		IDataExt.append(pipeline, "sslEnabled", sslEnabled);
	}
	

	public static boolean isServerSupported() {
		if(!isServerSupported){
			
			//IMQ-1252 Getting IS version from IS public API, instead of doInvoke on getSystemAttributes.
			String serverVersion =Build.getVersion();
			String version = serverVersion.substring(0, serverVersion.indexOf(".", 2));
			float ver = Float.parseFloat(version);
	
			if (ver >= 8.0f) {
				isServerSupported = true;
				
			} else {
	
				isServerSupported = false;
			}
		}
		return isServerSupported;
	}

	/**
	 * SSL Option keystore alias is displayed in the connections page only for
	 * IS 8.2 & above
	 * 
	 * @param pipeline
	 * @throws DetailedServiceException
	 */
	public static void isSSLAliasSupported(IData pipeline) throws DetailedServiceException {

		IDataExt.append(pipeline, "sslAliasSupported", Boolean.valueOf(isServerSupported()));

	}
	
	public static void updateListenerData(IData pipeline) throws DetailedServiceException, DetailedException{
		
		IData requestData = IDataFactory.create();
	     IDataCursor rc = requestData.getCursor();
	     ValuesEmulator.put(requestData, "adapterTypeName", "wmMQAdapter");
	     IDataCursor pc = pipeline.getCursor();
  
        ARTAdmin.retrieveListeners(requestData);
        IData[] requestedNotifications = IDataUtil.getIDataArray(rc,
                  AdapterRuntimeGlobals.LISTENER_DATA_LIST);
        IData[] responseNotifications = null;
        if(requestedNotifications != null && 
                  requestedNotifications.length > 0){
              int count = requestedNotifications.length;
            
              responseNotifications = new IData[count];
              for(int i = 0; i < count; i++ )
              {
                 IDataCursor rqc = requestedNotifications[i].getCursor();
                 String listenerNodeName =  IDataUtil.getString(rqc,AdapterRuntimeGlobals.LISTENER_NODE_NAME);
                	
                 ListenerNode listenerNode = ListenerManager.getManager().getListenerNode(listenerNodeName);
                 IData listnerNodeData = listenerNode.getAsData();
                 String encodedListenerData = ValuesEmulator.getString(listnerNodeData, "IRTNODE_PROPERTY");
                 try{
					java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(
							com.wm.util.Base64.decode(encodedListenerData.getBytes("UTF8")));
					com.wm.util.coder.IDataBinCoder coder = new com.wm.util.coder.IDataBinCoder();
					IData value = coder.decode(bis);
					IData listProp =  (IData)ValuesEmulator.get(value, "listenerProperties");
					if(!IDataExt.hasKey(listProp, "handleBackoutRequeue")){
						ValuesEmulator.put(listProp, "handleBackoutRequeue", "false");
						if(!IDataExt.hasKey(listProp, "useGrouping")){
							ValuesEmulator.put(listProp, "useGrouping", "false");
						}
						IDataCursor listenerCursor = listProp.getCursor();
						while (listenerCursor.next()) {
							String key = listenerCursor.getKey();
							Object valued = listenerCursor.getValue();
							if(!IDataExt.hasKey(value, ".CPROP."+key)){
								ValuesEmulator.put(value,".CPROP."+ key, String.valueOf(valued));
							}
						}
						ValuesEmulator.put(pipeline, "listenerProperties",listProp);
						ARTAdmin.setListenerProperties(listenerNodeName, value);	
						responseNotifications[i] = IDataFactory.create();
		                IDataCursor rsc = responseNotifications[i].getCursor();
		                try
		                  {
		                      IDataUtil.put(rsc,AdapterRuntimeGlobals.LISTENER_NODE_NAME,
		                              IDataUtil.getString(rqc,AdapterRuntimeGlobals.LISTENER_NODE_NAME));
		                      IDataUtil.put(rsc,AdapterRuntimeGlobals.PKG_NAME,
		                              IDataUtil.getString(rqc,AdapterRuntimeGlobals.PKG_NAME));
		                      IDataUtil.put(rsc,ListenerNode.ENABLED_STATUS_KEY,
		                              IDataUtil.getString(rqc,ListenerNode.ENABLED_STATUS_KEY));
		                  }
		                  finally
		                  {
		                      rqc.destroy();
		                      rsc.destroy();
		                  }
					}	
				} catch(IOException e){
					
				}
			}
            IDataUtil.put(pc,"UpdatedListenerNode", 
                      responseNotifications);
		}
	}

}
