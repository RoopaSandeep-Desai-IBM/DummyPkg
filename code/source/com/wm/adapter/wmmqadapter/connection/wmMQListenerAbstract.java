package com.wm.adapter.wmmqadapter.connection;

import java.util.List;

import com.wm.adk.error.AdapterException;
import com.wm.adk.metadata.AdapterParameters;
import com.wm.adk.notification.WmConnectedListener;

/**
 * This class is introduced to basically to avoid the getRegisteredNotifications()
 * and setRegisteredNotifications() methods(that needs to be overridden for 
 * Multi-Queue Listener implementation) to be treated as an adapter parameter. 
 * The discoverParameters() method is overridden so that a different stop class 
 * can be provided for discovering the adapter parameters.
 */
public abstract class wmMQListenerAbstract extends WmConnectedListener {

    public void discoverParameters(AdapterParameters ap) throws AdapterException {
        ap.discoverParameters(this, wmMQListenerAbstract.class);
    }
	
	public List getRegisteredNotifications() {
		return retrieveRegisteredNotifications();
	}

	/**
	 * Default implementation is forward the request to the 
	 * getRegisteredNotifications() method of the super class.
	 * @return List registered notifications
	 */
	List retrieveRegisteredNotifications() {
		return super.getRegisteredNotifications();
	}
	
    /**
     * Sets the registration order of notifications (as WmListenerNotification objects).
     */
    public void setRegisteredNotifications(List notifications) {
    	registerNotifications(notifications);
    }	
    
	/**
	 * Default implementation is forward the request to the 
	 * setRegisteredNotifications() method of the super class.
	 */
	void registerNotifications(List notifications) {
		super.setRegisteredNotifications(notifications);
	}
}
