/*
 * wmMQQueueManagerAttributes.java 
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

package com.wm.adapter.wmmqadapter.service;

import java.util.HashMap;

/*
 * This class encapsulates a lookup table of all QueueManager attributes
 */

public class wmMQQueueManagerAttributes {

	private static boolean initialized = false;

	//private static String monitor = "monitor";
	private static Object monitor = new Object();

	// Read-only properties, hence HashMap is sufficient
	//public static HashMap lut = new HashMap();
	protected static final HashMap lut = new HashMap();

	public static final String[] qMgrProperties = { "QueueManagerProperties." + "AccountingConnOverride", //NOSONAR
													"QueueManagerProperties." + "AccountingInterval",
													"QueueManagerProperties." + "AdoptNewMCACheck",
													"QueueManagerProperties." + "AdoptNewMCAType",
													"QueueManagerProperties." + "AlterationDate",
													"QueueManagerProperties." + "AlterationTime",
													"QueueManagerProperties." + "AuthorityEvent",
													"QueueManagerProperties." + "BridgeEvent",
													"QueueManagerProperties." + "ChannelAutoDef",
													"QueueManagerProperties." + "ChannelAutoDefEvent",
													"QueueManagerProperties." + "ChannelAutoDefExit",
													"QueueManagerProperties." + "ChannelEvent",
													"QueueManagerProperties." + "ChannelInitiatorControl",
													"QueueManagerProperties." + "ChannelMonitoring",
													"QueueManagerProperties." + "ChannelStatistics",
													"QueueManagerProperties." + "ChinitAdapters",
													"QueueManagerProperties." + "ChinitDispatchers",
													"QueueManagerProperties." + "ChinitTraceAutoStart",
													"QueueManagerProperties." + "ChinitTraceTableSize",
													"QueueManagerProperties." + "ClusterSenderMonitoringDefault",
													"QueueManagerProperties." + "ClusterSenderStatistics",
													"QueueManagerProperties." + "ClusterWorkloadData",
													"QueueManagerProperties." + "ClusterWorkloadExit",
													"QueueManagerProperties." + "ClusterWorkloadLength",
													"QueueManagerProperties." + "CLWLMRUChannels",
													"QueueManagerProperties." + "CLWLUseQ",
													"QueueManagerProperties." + "CodedCharSetId",
													"QueueManagerProperties." + "CommandEvent",
													"QueueManagerProperties." + "CommandInputQName",
													"QueueManagerProperties." + "CommandLevel",
													"QueueManagerProperties." + "CommandServerControl",
													"QueueManagerProperties." + "ConfigurationEvent",
													"QueueManagerProperties." + "DeadLetterQName",
													"QueueManagerProperties." + "DefXmitQName",
													"QueueManagerProperties." + "DistLists",
													"QueueManagerProperties." + "DNSGroup",
													"QueueManagerProperties." + "DNSWLM",
													"QueueManagerProperties." + "ExpiryInterval",
													"QueueManagerProperties." + "IGQPutAuthority",
													"QueueManagerProperties." + "IGQUserId",
													"QueueManagerProperties." + "InhibitEvent",
													"QueueManagerProperties." + "IntraGroupQueuing",
													"QueueManagerProperties." + "IPAddressVersion",
													"QueueManagerProperties." + "ListenerTimer",
													"QueueManagerProperties." + "LocalEvent",
													"QueueManagerProperties." + "LoggerEvent",
													"QueueManagerProperties." + "LUGroupName",
													"QueueManagerProperties." + "LUName",
													"QueueManagerProperties." + "LU62ARMSuffix",
													"QueueManagerProperties." + "LU62Channels",
													"QueueManagerProperties." + "MaxActiveChannels",
													"QueueManagerProperties." + "MaxChannels",
													"QueueManagerProperties." + "MaxHandles",
													"QueueManagerProperties." + "MaxMsgLength",
													"QueueManagerProperties." + "MaxPriority",
													"QueueManagerProperties." + "MaxUncommittedMsgs",
													"QueueManagerProperties." + "MQIAccounting",
													"QueueManagerProperties." + "MQIStatistics",
													"QueueManagerProperties." + "OutboundPortMax",
													"QueueManagerProperties." + "OutboundPortMin",
													"QueueManagerProperties." + "PerformanceEvent",
													"QueueManagerProperties." + "Platform",
													"QueueManagerProperties." + "QMgrDesc",
													"QueueManagerProperties." + "QMgrIdentifier",
													"QueueManagerProperties." + "QMgrName",
													"QueueManagerProperties." + "QSGName",
													"QueueManagerProperties." + "QueueAccounting",
													"QueueManagerProperties." + "QueueMonitoring",
													"QueueManagerProperties." + "QueueStatistics",
													"QueueManagerProperties." + "ReceiveTimeout",
													"QueueManagerProperties." + "ReceiveTimeoutMin",
													"QueueManagerProperties." + "ReceiveTimeoutType",
													"QueueManagerProperties." + "RemoteEvent",
													"QueueManagerProperties." + "RepositoryName",
													"QueueManagerProperties." + "RepositoryNamelist",
													"QueueManagerProperties." + "SharedQMgrName",
													"QueueManagerProperties." + "SSLEvent",
													"QueueManagerProperties." + "SSLFIPSRequired",
													"QueueManagerProperties." + "SSLKeyResetCount",
													"QueueManagerProperties." + "StartStopEvent",
													"QueueManagerProperties." + "StatisticsInterval",
													"QueueManagerProperties." + "SyncPoint",
													"QueueManagerProperties." + "TCPChannels",
													"QueueManagerProperties." + "TCPKeepAlive",
													"QueueManagerProperties." + "TCPName",
													"QueueManagerProperties." + "TCPStackType",
													"QueueManagerProperties." + "TraceRouteRecording",
													"QueueManagerProperties." + "TriggerInterval"};

	public static final String[] qMgrPropertyTypes = { //NOSONAR
		 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.String",
		 "java.lang.String", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer",
		 "java.lang.String", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer",
		 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer",
		 "java.lang.Integer", "java.lang.String", "java.lang.String", "java.lang.Integer", "java.lang.Integer",
		 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.String", "java.lang.Integer",
		 "java.lang.Integer", "java.lang.Integer", "java.lang.String", "java.lang.String", "java.lang.Integer",
		 "java.lang.String", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.String",
		 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer",
		 "java.lang.Integer", "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.Integer",
		 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer",
		 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer",
		 "java.lang.Integer", "java.lang.Integer", "java.lang.String", "java.lang.String", "java.lang.String",
		 "java.lang.String", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer",
		 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.String", "java.lang.String",
		 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer",
		 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.String",
		 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer" };

	// Lazy initialization of lookup table
	public static void initialize() {

		if (initialized)
			return;

		synchronized(monitor) {

			if (!initialized) {

				lut.put("AccountingConnOverride", new wmMQProperty("int", -1, 136));
				lut.put("AccountingInterval", new wmMQProperty("int", -1, 135));
				lut.put("AdoptNewMCACheck", new wmMQProperty("int", -1, 102));
				lut.put("AdoptNewMCAType", new wmMQProperty("int", -1, 103));
				lut.put("AlterationDate", new wmMQProperty("String", 12, 2027));
				lut.put("AlterationTime", new wmMQProperty("String", 8, 2028));
				lut.put("AuthorityEvent", new wmMQProperty("int", -1, 47));
				lut.put("BridgeEvent", new wmMQProperty("int", -1, 74));
				lut.put("ChannelAutoDef", new wmMQProperty("int", -1, 55));
				lut.put("ChannelAutoDefEvent", new wmMQProperty("int", -1, 56));
				lut.put("ChannelAutoDefExit", new wmMQProperty("String", 128, 2026));
				lut.put("ChannelEvent", new wmMQProperty("int", -1, 73));
				lut.put("ChannelInitiatorControl", new wmMQProperty("int", -1, 119));
				lut.put("ChannelMonitoring", new wmMQProperty("int", -1, 122));
				lut.put("ChannelStatistics", new wmMQProperty("int", -1, 129));
				lut.put("ChinitAdapters", new wmMQProperty("int", -1, 101));
				lut.put("ChinitDispatchers", new wmMQProperty("int", -1, 105));
				lut.put("ChinitTraceAutoStart", new wmMQProperty("int", -1, 117));
				lut.put("ChinitTraceTableSize", new wmMQProperty("int", -1, 118));
				lut.put("ClusterSenderMonitoringDefault", new wmMQProperty("int", -1, 124));
				lut.put("ClusterSenderStatistics", new wmMQProperty("int", -1, 130));
				lut.put("ClusterWorkloadData", new wmMQProperty("String", 32, 2034));
				lut.put("ClusterWorkloadExit", new wmMQProperty("String", 128, 2033));
				lut.put("ClusterWorkloadLength", new wmMQProperty("int", -1, 58));
				lut.put("CLWLMRUChannels", new wmMQProperty("int", -1, 97));
				lut.put("CLWLUseQ", new wmMQProperty("int", -1, 98));
				lut.put("CodedCharSetId", new wmMQProperty("int", -1, 2));
				lut.put("CommandEvent", new wmMQProperty("int", -1, 99));
				lut.put("CommandInputQName", new wmMQProperty("String", 48, 2003));
				lut.put("CommandLevel", new wmMQProperty("int", -1, 31));
				lut.put("CommandServerControl", new wmMQProperty("int", -1, 120));
				lut.put("ConfigurationEvent", new wmMQProperty("int", -1, 51));
				lut.put("DeadLetterQName", new wmMQProperty("String", 48, 2006));
				lut.put("DefXmitQName", new wmMQProperty("String", 48, 2025));
				lut.put("DistLists", new wmMQProperty("int", -1, 34));
				lut.put("DNSGroup", new wmMQProperty("String", 18, 2071));
				lut.put("DNSWLM", new wmMQProperty("int", -1, 106));
				lut.put("ExpiryInterval", new wmMQProperty("int", -1, 39));
				lut.put("IGQPutAuthority", new wmMQProperty("int", -1, 65));
				lut.put("IGQUserId", new wmMQProperty("String", 12, 2041));
				lut.put("InhibitEvent", new wmMQProperty("int", -1, 48));
				lut.put("IntraGroupQueuing", new wmMQProperty("int", -1, 64));
				lut.put("IPAddressVersion", new wmMQProperty("int", -1, 93));
				lut.put("ListenerTimer", new wmMQProperty("int", -1, 107));
				lut.put("LocalEvent", new wmMQProperty("int", -1, 49));
				lut.put("LoggerEvent", new wmMQProperty("int", -1, 94));
				lut.put("LUGroupName", new wmMQProperty("String", 8, 2072));
				lut.put("LUName", new wmMQProperty("String", 8, 2073));
				lut.put("LU62ARMSuffix", new wmMQProperty("String", 2, 2074));
				lut.put("LU62Channels", new wmMQProperty("int", -1, 108));
				lut.put("MaxActiveChannels", new wmMQProperty("int", -1, 100));
				lut.put("MaxChannels", new wmMQProperty("int", -1, 109));
				lut.put("MaxHandles", new wmMQProperty("int", -1, 11));
				lut.put("MaxMsgLength", new wmMQProperty("int", -1, 13));
				lut.put("MaxPriority", new wmMQProperty("int", -1, 14));
				lut.put("MaxUncommittedMsgs", new wmMQProperty("int", -1, 33));
				lut.put("MQIAccounting", new wmMQProperty("int", -1, 133));
				lut.put("MQIStatistics", new wmMQProperty("int", -1, 127));
				lut.put("OutboundPortMax", new wmMQProperty("int", -1, 140));
				lut.put("OutboundPortMin", new wmMQProperty("int", -1, 110));
				lut.put("PerformanceEvent", new wmMQProperty("int", -1, 53));
				lut.put("Platform", new wmMQProperty("int", -1, 32));
				lut.put("QMgrDesc", new wmMQProperty("String", 64, 2014));
				lut.put("QMgrIdentifier", new wmMQProperty("String", 48, 2032));
				lut.put("QMgrName", new wmMQProperty("String", 48, 2015));
				lut.put("QSGName", new wmMQProperty("String", 4, 2040));
				lut.put("QueueAccounting", new wmMQProperty("int", -1, 134));
				lut.put("QueueMonitoring", new wmMQProperty("int", -1, 123));
				lut.put("QueueStatistics", new wmMQProperty("int", -1, 128));
				lut.put("ReceiveTimeout", new wmMQProperty("int", -1, 111));
				lut.put("ReceiveTimeoutMin", new wmMQProperty("int", -1, 113));
				lut.put("ReceiveTimeoutType", new wmMQProperty("int", -1, 112));
				lut.put("RemoteEvent", new wmMQProperty("int", -1, 50));
				lut.put("RepositoryName", new wmMQProperty("String", 48, 2035));
				lut.put("RepositoryNamelist", new wmMQProperty("String", 48, 2036));
				lut.put("SharedQMgrName", new wmMQProperty("int", -1, 77));
				lut.put("SSLEvent", new wmMQProperty("int", -1, 75));
				lut.put("SSLFIPSRequired", new wmMQProperty("int", -1, 92));
				lut.put("SSLKeyResetCount", new wmMQProperty("int", -1, 76));
				lut.put("StartStopEvent", new wmMQProperty("int", -1, 52));
				lut.put("StatisticsInterval", new wmMQProperty("int", -1, 131));
				lut.put("SyncPoint", new wmMQProperty("int", -1, 30));
				lut.put("TCPChannels", new wmMQProperty("int", -1, 114));
				lut.put("TCPKeepAlive", new wmMQProperty("int", -1, 115));
				lut.put("TCPName", new wmMQProperty("String", 8, 2075));
				lut.put("TCPStackType", new wmMQProperty("int", -1, 116));
				lut.put("TraceRouteRecording", new wmMQProperty("int", -1, 137));
				lut.put("TriggerInterval", new wmMQProperty("int", -1, 25));

				initialized = true;
			}

		}

	}


	public static wmMQProperty lookup(String propName) {

		if (!initialized)
			initialize();

		return (wmMQProperty) lut.get(propName);

	}

}