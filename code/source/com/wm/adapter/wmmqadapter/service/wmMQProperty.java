/*
 * wmMQProperty.java
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

/*
 * This class encapsulates properties of a Queue or QueueManager attribute
 */

public class wmMQProperty {

    // Denotes whether attribute is integer of char type
	private String type = null;

    // Denotes length of a char attribute. For int attributes
    // length is set to -1
	private int length = -1;

    // The index defined in the interface com.ibm.mq.MQC
    // by which the property can be identified in a selector
    // eg. the index for property "AlterationTime" will be
    // MQC.MQCA_ALTERATION_TIME
	private int MQCindex = -1;

	public wmMQProperty(String type, int length, int MQCindex) {

		this.type = type;
		this.length = length;
		this.MQCindex = MQCindex;

	}

	public String getType(){

		return type;

	}

	public int getLength(){

		return length;

	}

	public int getMQCindex(){

		return MQCindex;

	}

}