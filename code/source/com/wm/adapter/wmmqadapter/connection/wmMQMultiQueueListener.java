package com.wm.adapter.wmmqadapter.connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.resource.ResourceException;

import com.wm.adapter.wmmqadapter.WmMQAdapterUtils;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.wmMQAdapterConstant;
import com.wm.adk.error.AdapterException;
import com.wm.adk.log.ARTLogger;
import com.wm.adk.metadata.WmDescriptor;
import com.wm.adk.notification.WmNotificationListener;
import com.wm.app.b2b.server.NodeMaster;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSNode;
import com.wm.pkg.art.error.DetailedException;
import com.wm.pkg.art.ns.ListenerManager;
import com.wm.pkg.art.ns.ListenerNode;

/**
 * This class represents the Multi-Queue Listener type of WmMQAdapter.
 * 
 */
public class wmMQMultiQueueListener extends wmMQListener {

	// Property to distinguish the parent and child listeners. - Non editable
	private boolean _childListener = false;
	
	// Used by the child listener to keep a reference of its parent listener. - Non editable
	private String _parentListenerNodeName = null;
	
	// Used by the child listener to keep track of the queue 
	// which this instance is listening to. - Non editable
	private String _childListenerQueueName = null;
	
	// Flag used on the child listeners to specify that its properties would be
	// inherited from the parent listener. - Editable on the child listener
	private boolean _inheritParentProperties = false;

	// Cache the parent listener node instance
	private ListenerNode _parentListenerNode = null;
	
	// Cache all the child listener nodes
	private List _childListenerNodes = null;
	
	//Special characters with regular expression and the value with which it should be replaced with.
	private static final String[][] SPECIAL_CHARACTERS = {{".", "\\.","_"},{"%", "%","_"},{"/", "/","_"}};
	
	/**
	 * @return Returns the childListener.
	 */
	public boolean isChildListener() {
		return this._childListener;
	}

	/**
	 * @param childListener The childListener to set.
	 */
	public void setChildListener(boolean childListener) {
		this._childListener = childListener;
	}

	/**
	 * @return Returns the inheritParentProperties.
	 */
	public boolean isInheritParentProperties() {
		return this._inheritParentProperties;
	}

	/**
	 * @param inheritParentProperties The inheritParentProperties to set.
	 */
	public void setInheritParentProperties(boolean inheritParentProperties) {
		this._inheritParentProperties = inheritParentProperties;
	}

	/**
	 * @return Returns the parentListenerNodeName.
	 */
	public String getParentListenerNodeName() {
		return this._parentListenerNodeName;
	}

	/**
	 * @param parentListenerNodeName The parentListenerNodeName to set.
	 */
	public void setParentListenerNodeName(String parentListenerNodeName) {
		this._parentListenerNodeName = parentListenerNodeName;
	}

	/**
	 * @return Returns the _childListenerQueueName.
	 */
	public String getChildListenerQueueName() {
		return this._childListenerQueueName;
	}

	/**
	 * @param childListenerQueueName The queueName to set.
	 */
	public void setChildListenerQueueName(String queueName) {
		this._childListenerQueueName = queueName;
	}

	public void fillWmDescriptor(WmDescriptor d, Locale l) throws AdapterException {
        log(ARTLogger.INFO, 1001, "wmMQMultiQueueListener:fillWmDescriptor", "");
        
        super.fillWmDescriptor(d, l);
	    d.setGroupName(wmMQAdapterConstant.INHERIT_PARENT_PROPERTIES, wmMQAdapterConstant.LISTEN_SERVICE);
	    d.setGroupName(wmMQAdapterConstant.CHILD_LISTENER, wmMQAdapterConstant.LISTEN_SERVICE);
	    d.setGroupName(wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME, wmMQAdapterConstant.LISTEN_SERVICE);
	    d.setGroupName(wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME, wmMQAdapterConstant.LISTEN_SERVICE);
        
        d.setValidValues(wmMQAdapterConstant.INHERIT_PARENT_PROPERTIES, new String[] {"true", "false"});
        d.setValidValues(wmMQAdapterConstant.CHILD_LISTENER, new String[] {"true", "false"});
        
        d.setHidden(wmMQAdapterConstant.CHILD_LISTENER, true);
        d.setHidden(wmMQAdapterConstant.PARENT_LISTENER_NODE_NAME, true);
        d.setHidden(wmMQAdapterConstant.CHILD_LISTENER_QUEUE_NAME, true);
        
        d.setDescriptions(wmMQAdapter.getInstance().getAdapterResourceBundleManager(), l);
        
        log(ARTLogger.INFO, 1002, "wmMQMultiQueueListener:fillWmDescriptor", "");
	}

	public void listenerStartup() throws ResourceException {
        log(ARTLogger.INFO, 1001, "wmMQMultiQueueListener:listenerStartup", "");
        
		super.listenerStartup();
		
		// If this is a child listener then reset the connection to use the given queue.
		if(isChildListener()) {
			_conn.resetInboundQueue(getChildListenerQueueName());
		}
		
		// If this is a parent listener then cache all the child listener references
		if(!isChildListener()) {
			_childListenerNodes = new ArrayList();
			
            // Iterate through the queues in the queue name parameter build the child listener node name 
            StringTokenizer queueNames = new StringTokenizer(_conn.getQueueName());
            String parentListenerNodeName = getListenerNode().getNSName().getFullName();
            String childListenerNodePrefix = parentListenerNodeName + "_";
            while(queueNames.hasMoreTokens()) {
            	String queueName = queueNames.nextToken();
            	//queueName = getValidChildListernerName(parentListenerNodeName,queueName );
                String childListenerNodeName = getValidChildListenerName(parentListenerNodeName,queueName );
                ListenerNode childListenerNode = null;
				try {
					childListenerNode = ListenerManager.getManager().getListenerNode(childListenerNodeName);
				} catch (DetailedException e) {
					// Child listener for the queue not found. Ignore the exception.
				}
                if(childListenerNode != null && wmMQAdapterConstant.MULTI_QUEUE_LISTENER.equals(childListenerNode.getListenerClassName())) {
	                wmMQMultiQueueListener childMultiQueueListener = (wmMQMultiQueueListener)childListenerNode.getListener();
	                if(childMultiQueueListener.isChildListener() && childMultiQueueListener.getParentListenerNodeName().equals(parentListenerNodeName)) {
	                	_childListenerNodes.add(childListenerNode);
	                }
                }
            }
		}
        log(ARTLogger.INFO, 1002, "wmMQMultiQueueListener:listenerStartup", "");
	}

	/**
	 * This method retrieves the registered notifications. Especially if there are 
	 * no notifications registered directly with the child listener, this method returns
	 * the notifications registered with the parent listener.
	 */
	List retrieveRegisteredNotifications() {
        log(ARTLogger.INFO, 1001, "wmMQMultiQueueListener:getRegisteredNotifications", "");
        
		List registeredNotifications = null;
		try {
			registeredNotifications = super.retrieveRegisteredNotifications();
			if(isChildListener() && (registeredNotifications == null || registeredNotifications.isEmpty())) {
					registeredNotifications = getParentListenerNode().getRegisteredNotifications(); //NOSONAR
			}
		} catch (Exception e) {
			registeredNotifications = null;
			WmMQAdapterUtils.logException(e);
		}
		
		if(registeredNotifications == null) {
			registeredNotifications = new Vector(); 
		}
		
        log(ARTLogger.INFO, 1002, "wmMQMultiQueueListener:getRegisteredNotifications", "");
        
		return registeredNotifications;
	}
	
	/**
	 * This method changes the order of the registered notifications of a child listener 
	 * only if the child listener has atleast one directly registered notification(ones not
	 * inherited from the parent listener).
	 */
	void registerNotifications(List notifications) {
        log(ARTLogger.INFO, 1001, "wmMQMultiQueueListener:registerNotifications", "");
        
        if(!isChildListener()) {
        	super.registerNotifications(notifications);
        }
        else {
    		List existingRegisteredNotifications = null;
    		try {
    			// Get the notifications registered for this child listener
    			existingRegisteredNotifications = super.retrieveRegisteredNotifications();
    		} catch (Exception e) {
    			WmMQAdapterUtils.logException(e);
    		}
    		
    		// If there is atleast one notification registered directly with this child listener
    		// then only change the order of the registered notifications
    		if(existingRegisteredNotifications != null && !existingRegisteredNotifications.isEmpty()) {
            	super.registerNotifications(notifications);
    		}
        }
        
        log(ARTLogger.INFO, 1002, "wmMQMultiQueueListener:registerNotifications", "");
	}
	
	private ListenerNode getParentListenerNode() throws DetailedException {
		if(isChildListener()) {
			if(_parentListenerNode == null) {
				_parentListenerNode = ListenerManager.getManager().getListenerNode(_parentListenerNodeName);
			}
			return _parentListenerNode;
		}
		return null;
	}

	public Object waitForData() throws ResourceException {
        log(ARTLogger.INFO, 1001, "wmMQMultiQueueListener:waitForData", "");
        
        Object returnValue = null;
        
		if(isChildListener()) {
			returnValue = super.waitForData();
		}
		else {
			// Check if all the child listeners are disabled.
			boolean allDisabled = true;
			for(Iterator it = _childListenerNodes.iterator(); it.hasNext(); ) {
				ListenerNode oneChildNode = (ListenerNode)it.next();
				if(oneChildNode.getEnabledStatus()) {
					allDisabled = false;
				}
			}
			
			if(!allDisabled) {
				try {
					int waitInterval = getWaitInterval(); 
					if(waitInterval < 0) {
						waitInterval = 60000;
					}
					Thread.sleep(waitInterval);
				} catch (InterruptedException ie) {
					// Ignore
				}
			}
			else {
				// As all child nodes are disabled, disable the parent listener as well
				if(!getListenerNode().isCancelled()) {
					// Log a warning message and disable the parent listener
					WmMQAdapterUtils.logWarning(3060, new String[] {getListenerNode().getNSName().getFullName()});
					
					// Disable the parent listener
					getListenerNode().disable();
				}
			}
		}
		
        log(ARTLogger.INFO, 1002, "wmMQMultiQueueListener:waitForData", "");
        
		return returnValue;
	}
	
	/**
	 * This method is added to support queue names with special character such as dot('.'). 
	 * When queue name contains special characters, the special characters has to be 
	 * replaced with valid characters.
	 * Replacing special characters may result in duplicity of node name, hence valid name 
	 * of child needs to looked for with this methods.    
	 */
	public static String getValidChildListenerName(String multiQueueListenerNodeName, String queueName) {
		String queueNameNoSpChar = queueName;
		int length = SPECIAL_CHARACTERS.length;
		for(int ii = 0; ii < length ; ii++){
			String[] ch = SPECIAL_CHARACTERS[ii];
			if(queueNameNoSpChar.indexOf((ch[0]))>-1){
				queueNameNoSpChar = queueNameNoSpChar.replaceAll(ch[1], ch[2]);
			}
		}
		String childListenerNodeName = multiQueueListenerNodeName + "_" + queueNameNoSpChar ;
		String childListenerName = findChildListenerName(childListenerNodeName, queueName);
		if(childListenerName != null){
			return childListenerName;
		}
		int ii = 0;
		//If the child listener doesn't exist, then find its possible name. 
		while(true){
            NSName name = NSName.create(childListenerNodeName);
            NSNode node = name == null ? null : Namespace.current().getNode(name);
            if(node != null){
            	childListenerNodeName = multiQueueListenerNodeName + "_" + queueNameNoSpChar + ++ii;
            }else{
            	break;
            }
		}
		return childListenerNodeName ;
	}

	
	/**
	 * This method finds child listener from queueName and listener's name prefix. 
	 */
	private static String findChildListenerName(String listenerPrefix, String queueName){
		String[] allNodes = ListenerManager.getListenerNames();
		for(int i=0; i<allNodes.length; i++) {
			if(allNodes[i].startsWith(listenerPrefix)) {
				ListenerNode childListenerNode = null;
				try {
					childListenerNode = ListenerManager.getManager().getListenerNode(allNodes[i]);
					if(childListenerNode != null ) {
						WmNotificationListener listener = childListenerNode.getListener();
						if(listener instanceof wmMQMultiQueueListener && 
								((wmMQMultiQueueListener)listener).isChildListener() ){
							wmMQMultiQueueListener childListener = (wmMQMultiQueueListener)listener ;
							if (queueName.equals(childListener.getChildListenerQueueName())){
					        	return childListener.getListenerNode().getNSName().getFullName();
					        }
						}
					}
				}
				catch (DetailedException e) {
					//Ignoring the error continuing with the loop if it didn't find node.
				}			
			}
		}
		return null;
	}
}
