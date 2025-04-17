/*
 * wmMQQueueAttributes.java 
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
 * This class encapsulates a lookup table of all Queue attributes
 */

public class wmMQQueueAttributes {

		private static boolean initialized = false;

		//private static String monitor = "monitor";
		private static Object monitor = new Object();

		// Read-only properties, hence HashMap is sufficient
		//public static HashMap lut = new HashMap();
		protected static final HashMap lut = new HashMap();

		public static final String[] qProperties = { //NOSONAR
				"QueueProperties." + "AlterationDate",
				"QueueProperties." + "AlterationTime",
				"QueueProperties." + "BackoutRequeueQName",
				"QueueProperties." + "BackoutThreshold",
				"QueueProperties." + "BaseQName",
				"QueueProperties." + "CFStrucName",
				"QueueProperties." + "ClusterName",
				"QueueProperties." + "ClusterNamelist",
				"QueueProperties." + "CLWLQueuePriority",
				"QueueProperties." + "CLWLQueueRank",
				"QueueProperties." + "CLWLUseQ",
				"QueueProperties." + "CreationDate",
		        "QueueProperties." + "CreationTime",
		        "QueueProperties." + "CurrentQDepth",
				"QueueProperties." + "DefBind",
				"QueueProperties." + "DefinitionType",
				"QueueProperties." + "DefInputOpenOption",
				"QueueProperties." + "DefPersistence",
				"QueueProperties." + "DefPriority",
				"QueueProperties." + "DistLists",
				"QueueProperties." + "HardenGetBackout",
				"QueueProperties." + "IndexType",
		        "QueueProperties." + "InhibitGet",
		        "QueueProperties." + "InhibitPut",
		        "QueueProperties." + "InitiationQName",
		        "QueueProperties." + "MaxMsgLength",
		        "QueueProperties." + "MaxQDepth",
		        "QueueProperties." + "MsgDeliverySequence",
		        "QueueProperties." + "NonPersistentMessageClass",
		        "QueueProperties." + "OpenInputCount",
		        "QueueProperties." + "OpenOutputCount",
		        "QueueProperties." + "ProcessName",
		        "QueueProperties." + "QDepthHighEvent",
		        "QueueProperties." + "QDepthHighLimit",
		        "QueueProperties." + "QDepthLowEvent",
		        "QueueProperties." + "QDepthLowLimit",
		        "QueueProperties." + "QDepthMaxEvent",
		        "QueueProperties." + "QDesc",
		        "QueueProperties." + "QName",
		        "QueueProperties." + "QServiceInterval",
		        "QueueProperties." + "QServiceIntervalEvent",
		        "QueueProperties." + "QSGDisp",
		        "QueueProperties." + "QueueAccounting",
		        "QueueProperties." + "QueueMonitoring",
		        "QueueProperties." + "QueueStatistics",
		        "QueueProperties." + "QType",
		        "QueueProperties." + "RemoteQMgrName",
		        "QueueProperties." + "RemoteQName",
		        "QueueProperties." + "RetentionInterval",
		        "QueueProperties." + "Scope",
		        "QueueProperties." + "Shareability",
		        "QueueProperties." + "StorageClass",
		        "QueueProperties." + "TriggerControl",
		        "QueueProperties." + "TriggerData",
		        "QueueProperties." + "TriggerDepth",
		        "QueueProperties." + "TriggerMsgPriority",
		        "QueueProperties." + "TriggerType",
		        "QueueProperties." + "Usage",
		        "QueueProperties." + "XmitQName"
		        };

		public static final String[] qPropertyTypes = { //NOSONAR
			 "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.Integer", "java.lang.String", "java.lang.String",
			 "java.lang.String", "java.lang.String", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.String",
			 "java.lang.String", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer",
			 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer",
			 "java.lang.String", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer",
			 "java.lang.Integer", "java.lang.String", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer",
			 "java.lang.Integer", "java.lang.String", "java.lang.String", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer",
			 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.String", "java.lang.String",
			 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.String", "java.lang.Integer", "java.lang.String",
			 "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.String"
        	};

		// Lazy initialization of lookup table
		public static void initialize() {

			if (initialized)
				return;

			synchronized(monitor) {

				if (!initialized) {
					lut.put("AlterationDate", new wmMQProperty("String", 12, 2027));
					lut.put("AlterationTime", new wmMQProperty("String", 8, 2028));
					lut.put("BackoutRequeueQName", new wmMQProperty("String", 48, 2019));
					lut.put("BackoutThreshold", new wmMQProperty("int", -1, 22));
					lut.put("BaseQName", new wmMQProperty("String", 48, 2002));
					lut.put("CFStrucName", new wmMQProperty("String", 12, 2039));
					lut.put("ClusterName", new wmMQProperty("String", 48, 2029));
					lut.put("ClusterNamelist", new wmMQProperty("String", 48, 2030));
					lut.put("CLWLQueuePriority", new wmMQProperty("int", -1, 96));
					lut.put("CLWLQueueRank", new wmMQProperty("int", -1, 95));
					lut.put("CLWLUseQ", new wmMQProperty("int", -1, 98));
					lut.put("CreationDate", new wmMQProperty("String", 12, 2004));
					lut.put("CreationTime", new wmMQProperty("String", 8, 2005));
					lut.put("CurrentQDepth", new wmMQProperty("int", -1, 3));
					lut.put("DefBind", new wmMQProperty("int", -1, 61));
					lut.put("DefinitionType", new wmMQProperty("int", -1, 7));
					lut.put("DefInputOpenOption", new wmMQProperty("int", -1, 4));
					lut.put("DefPersistence", new wmMQProperty("int", -1, 5));
					lut.put("DefPriority", new wmMQProperty("int", -1, 6));
					lut.put("DistLists", new wmMQProperty("int", -1, 34));
					lut.put("HardenGetBackout", new wmMQProperty("int", -1, 8));
					lut.put("IndexType", new wmMQProperty("int", -1, 57));
					lut.put("InhibitGet", new wmMQProperty("int", -1, 9));
					lut.put("InhibitPut", new wmMQProperty("int", -1, 10));
					lut.put("InitiationQName", new wmMQProperty("String", 48, 2008));
					lut.put("MaxMsgLength", new wmMQProperty("int", -1, 13));
					lut.put("MaxQDepth", new wmMQProperty("int", -1, 15));
					lut.put("MsgDeliverySequence", new wmMQProperty("int", -1, 16));
					lut.put("NonPersistentMessageClass", new wmMQProperty("int", -1, 78));
					lut.put("OpenInputCount", new wmMQProperty("int", -1, 17));
					lut.put("OpenOutputCount", new wmMQProperty("int", -1, 18));
					lut.put("ProcessName", new wmMQProperty("String", 48, 2012));
					lut.put("QDepthHighEvent", new wmMQProperty("int", -1, 43));
					lut.put("QDepthHighLimit", new wmMQProperty("int", -1, 40));
					lut.put("QDepthLowEvent", new wmMQProperty("int", -1, 44));
					lut.put("QDepthLowLimit", new wmMQProperty("int", -1, 41));
					lut.put("QDepthMaxEvent", new wmMQProperty("int", -1, 42));
					lut.put("QDesc", new wmMQProperty("String", 64, 2013));
					lut.put("QName", new wmMQProperty("String", 48, 2016));
					lut.put("QServiceInterval", new wmMQProperty("int", -1, 54));
					lut.put("QServiceIntervalEvent", new wmMQProperty("int", -1, 46));
					lut.put("QSGDisp", new wmMQProperty("int", -1, 63));
					lut.put("QueueAccounting", new wmMQProperty("int", -1, 134));
					lut.put("QueueMonitoring", new wmMQProperty("int", -1, 123));
					lut.put("QueueStatistics", new wmMQProperty("int", -1, 128));
					lut.put("QType", new wmMQProperty("int", -1, 20));
					lut.put("RemoteQMgrName", new wmMQProperty("String", 48, 2017));
					lut.put("RemoteQName", new wmMQProperty("String", 48, 2018));
					lut.put("RetentionInterval", new wmMQProperty("int", -1, 21));
					lut.put("Scope", new wmMQProperty("int", -1, 45));
					lut.put("Shareability", new wmMQProperty("int", -1, 23));
					lut.put("StorageClass", new wmMQProperty("String", 8, 2022));
					lut.put("TriggerControl", new wmMQProperty("int", -1, 24));
					lut.put("TriggerData", new wmMQProperty("String", 64, 2023));
					lut.put("TriggerDepth", new wmMQProperty("int", -1, 29));
					lut.put("TriggerMsgPriority", new wmMQProperty("int", -1, 26));
					lut.put("TriggerType", new wmMQProperty("int", -1, 28));
					lut.put("Usage", new wmMQProperty("int", -1, 12));
					lut.put("XmitQName", new wmMQProperty("String", 48, 2024));
					
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