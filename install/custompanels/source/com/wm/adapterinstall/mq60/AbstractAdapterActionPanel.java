package com.wm.adapterinstall.mq60;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.wm.distman.install.DebugLog;
import com.wm.distman.install.DistManUtils;
import com.wm.distman.install.GenericInstallerActionPanel;
import com.wm.distman.install.InstallerProductProps;
import com.wm.distman.helpers.*;
/**
 * <p>Title: AbstractAdapterActionPanel</p>
 * <p>Description: Parent class for Adapter Action Panels</p>
 * <p>Copyright: Copyright (c) 1996-2003, webMethods Inc.  All Rights Reserved.</p>
 * <p>Company: webMethods</p>
 * @author Peter Fish
 * @version 1.0
 */

abstract public class AbstractAdapterActionPanel extends GenericInstallerActionPanel {

	private static final String JAVA_HOME_PROPERTY ="java.home";

	private String installDir;
	private String jarsDir;
    private String javaHomeExecutable;
    private String packageDir;
	private String uninstallObjectFileName;
	private ResourceBundle resourceBundle;

	public AbstractAdapterActionPanel() {
    }

	//Install Methods
	/**
	* Called to perform action during install.
	*<B>Override this method</B>
	*<P>
	*/
	abstract public boolean installExecute();
	/**
	* Called to initialize install panel sequence.
	*<B>Override this method with a call to installInit(String packageName) to perform initalization </B>
	*<P>
	*/
	abstract public boolean installInit();
    /**
    * Called to initialize install panel sequence.
    *<B>Call this method to perform initalization </B>
    *   @param packageName Package name .
    *<P>
    */
	protected boolean installInit(String packageName)
	{
		installDir = installerAccess.getInstallDir() + File.separator + AdapterUtils.getProductName();
        packageDir = installDir + File.separator + "packages" + File.separator + packageName;
		uninstallObjectFileName = packageDir + File.separator + "uninstallObject";
		jarsDir = packageDir + File.separator + "code" + File.separator + "jars";
        if ( DistManUtils.isWindows() ) {
            // A JVM is bundled with the Windows install.
             javaHomeExecutable = installDir + File.separator + "jvm" + File.separator + "bin" + File.separator + "java.exe";
        } else {
            // Note that from $WS/build/source/install/ReplaceTextProdAction.sh,
            // we use the System property, "java.home" for the JVM on UNIX platforms.
            // In effect, this gives us the JVM that was used for the install.
            javaHomeExecutable =  System.getProperty( JAVA_HOME_PROPERTY )  + File.separator + "bin" + File.separator + "java";
        }
		resourceBundle = installerAccess.getResourceBundle();
		return true;
	}

	/**
	* Called after installInit() to perform install console dialog.
	*/
	public boolean installSetupConsole()
    {
		return true;
    }
	/**
	* Called after installInit() to set up install GUI panel.
	*/
	public boolean installSetupGUI()
    {
		return true;
    }

	/**
	* Called to perform action during install.
	*<B>Override this method</B>
	*<P>
	*/
	abstract public boolean uninstallExecute();
    /*
	* Called to initialize uninstall panel sequence.
	*<B>Override this method with a call to uninstallInit(String packageName) to perform initalization </B>
	*<P>
	*/
	abstract public boolean uninstallInit();
            /**
    * Called to initialize uninstall panel sequence.
    *<B>Call this method to perform initalization </B>
    *   @param packageName Package name
    *<P>
    */
	protected boolean uninstallInit(String packageName)
	{
		installDir = installerAccess.getInstallDir() + File.separator + AdapterUtils.getProductName();
        packageDir = installDir + File.separator + "packages" + File.separator + packageName;
        uninstallObjectFileName = packageDir + File.separator + "uninstallObject";
		jarsDir = packageDir + File.separator + "code" + File.separator + "jars";
		resourceBundle = installerAccess.getResourceBundle();
		return true;
	}

	/**
	* Called after uninstallInit() to perform uninstall console dialog.
	*/
	public boolean uninstallSetupConsole()
    {
		return true;
    }
	/**
	* Called after installInit() to set up install GUI panel.
	*/
	public boolean uninstallSetupGUI()
    {
		return true;
    }

	/**
     * Gets install directory of IS.
	 * @return String value of <server> directory
    */
	public String getInstallDir()
	{
		return installDir;
	}

	 /**
     * Get value of key set by other installer or uninstaller panel.
     * @param key The key as an Object.
	 * @return Object set previously by setInstallVariable call
    */
    public Object getInstallVariable( Object key) {
        try {
            return installerAccess.getInstallVar( key );
        } catch ( IllegalAccessException anIllegalAccessException ) {
            this.handleException( anIllegalAccessException );
            return null;
        }
    }
	/**
     * Gets jars directory.
	 * @return String value of <server>/lib/jars directory
    */
	public String getJarsDir()
	{
		return jarsDir;
	}

     /**
     * Gets java home executable.
     * @return String value of java home executable
    */
    public String getJavaHomeExecutable()
    {
        return javaHomeExecutable;
    }


     /**
     * Gets package directory.
     * @return String value of package directory
    */
    public String getPackageDir()
    {
        return packageDir;
    }
	 /**
     * Get String message from ResourceBundle.
     * @param key The key to a String in the resource bundle.
	 * @return String corresponding to key supplied.
     */
	public String getMessage(String key)
	{
		String mess="none";
		try
		{
			return DistManUtils.msg(resourceBundle, key);
		}
		catch (MissingResourceException ex)
		{
			this.handleException(ex);
		}
		return mess;
    }
	 /**
     * Get int message from ResourceBundle.
     * @param key The key to an Integer in the resource bundle.
	 * @return int corresponding to key supplied.
     */
	public int getMessageInt(String key)
	{
		int mess=0;
		try
		{
			mess = ((Integer) resourceBundle.getObject(key)).intValue();
		}
		catch (MissingResourceException ex)
		{
			this.handleException(ex);
		}
		return mess;
    }
	 /**
     * Get String message from ResourceBundle with substituted String argument.
     * @param key The key to a String in the resource bundle.
	 * @param arg1 The String value of the argument to be substituted in the message in the resource bundle.
	 * @return String corresponding to key supplied with substituted argument.
     */
	public String getMessage(String key, String arg1)
	{
		Object args[] =new Object[1];
		args[0] = arg1;
		return MessageFormat.format(getMessage(key), args);
	}
	 /**
     * Get String message from ResourceBundle with 2 substituted String arguments.
     * @param key The key to a String in the resource bundle.
	 * @param arg1 The String value of the first argument to be substituted in the message in the resource bundle.
 	 * @param arg2 The String value of the second argument to be substituted in the message in the resource bundle.
     * @return String corresponding to key supplied with substituted arguments.
	 */
	public String getMessage(String key, String arg1, String arg2)
	{
		Object args[] =new Object[2];
		args[0] = arg1;
		args[1]= arg2;
		return MessageFormat.format(getMessage(key), args);
	}


	/**
     * Gets uninstall object file name.
	 * @return String value of <server>/packages/<packageName>/uninstallObject
    */
	public String getUninstallObjectFileName()
	{
		return uninstallObjectFileName;
	}

    /**
	* Sets install panel enabled
	*/
	public boolean isInstallPanelEnabled() {
        return true;
    }
	/**
	* Sets console mode  enabled
	*/
	public boolean isConsoleEnabled() {
        return true;
    }
	/**
	* Sets isilent mode enabled
	*/
	public boolean isSilentEnabled () {
		return true;
	}

	/**
	* Sets uninstall panel enabled
	*/
    public boolean isUninstallPanelEnabled() {
        return true;
    }

	 /**
     * Set key value pair to be retrieved by other installer panel.
     * @param key The key as an Object.
	 * @param value The value to be set as an Object.
    */
    public void setInstallVariable( Object key, Object value ) {
        try {
			 installerAccess.setInstallVar( key, value );
        } catch ( IllegalAccessException anIllegalAccessException ) {
            this.handleException( anIllegalAccessException );
        }
    }

	 /**
     * Set resource bundle to className.
     * @param className The name of the resource bundle class.
    */
	protected void setResourceBundle(String className)
	{
		try
		{
			resourceBundle = ResourceBundle.getBundle(className);
		}
		catch (MissingResourceException ex)
		{
			this.handleException( ex );
            resourceBundle = null;
		}
	}

     /**
     * Handle an exception gracefully.
     * @param exception The exception to handle.
     */
    private void handleException( Exception exception ) {
        this.log( "<exception class=\"" + this.getClass().getName()
                  + " message=\"" + exception.getLocalizedMessage() + "\">" );
        DebugLog.printStackTrace( exception );
        this.log( "</exception>" );
    }

     /**
     * Log string.
     * @param string the string to log.
    */
    private void log( String string ) {
        log(string );
    }
}