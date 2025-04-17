package com.wm.adapterinstall.mq60;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.swing.*;

import com.wm.distman.install.*;
import com.wm.distman.helpers.*;

public class ProgramFilesActionPanel extends AbstractAdapterActionPanel {

    
    public static final String PACKAGE_NAME  = "WmMQAdapter";
	

	public boolean installExecute ()
	{
       // Pre requisite check
      
       InstallerProductProps ipp = this.getInstallerAccess().getInstallerProductProps();
      
         //  Determine if WmMQAdapter is installed and if it is version 6.0
         String localProducts [] = ipp.getLocalProducts();
         if (localProducts == null)
            DebugLog.println("Null Local products");
         else
         {
            for (int j=0; j <localProducts.length; j++)
            {
              String propName = ipp.getNodeName(localProducts[j]);
              if (propName.startsWith("mq"))
              {
                  String version = AdapterUtils.getLocalProductPropertyVersion(propName, ipp, installerAccess.getEncryptionLevel());
                  DebugLog.println("Version" + version + " found for prop file " + propName);
                  if(!version.startsWith("6.0.0"))
                  {
                     AdapterUtils.showWarning( topPanel,
                        getMessage("MQ60.preexistingMQ", version), //"There is a pre-existing WebSphere MQ Adapter ({0}). Uninstall it before installing this version of the WebSphere MQ Adapter."
                        getMessage("MQ60.warningTitle"),  // "webMethods Installer - WebSphere MQ Adapter"
                        this.consoleInstall,
                        this.silentInstall );
                     return false;
                  }
               }
            }
         }

       

	   

	    return true;

	}


	public boolean installInit ()
	{
		super.installInit(PACKAGE_NAME);
//		Coding to override the setting in the properties file of the resource bundle
//		this.setResourceBundle("com.wm.adapterinstall.MQProgramFilesMessages");
		return true;
	}

	public boolean uninstallInit ()
	{
		super.uninstallInit(PACKAGE_NAME);
//		Coding to override the setting in the properties file of the resource bundle
//		this.setResourceBundle("com.wm.adapterinstall.MQProgramFilesMessages");
		return true;
	}

    public boolean uninstallExecute()
    {
        String uninstallObjectFileName = getUninstallObjectFileName();

        if(!AdapterUtils.fileExists(uninstallObjectFileName))
        {
            DebugLog.println("Uninstall object not found");
            return true;
        }
        
        String wasCopied = (String)AdapterUtils.getUninstallParameter("wasCopied",uninstallObjectFileName);

        
        DebugLog.println("wasCopied              " + wasCopied);

        
        String mqJarFileName [] = {"com.ibm.mq.jar", "com.ibm.mq.pcf.jar"};
        if (wasCopied.equals("true"))
        {
            String jarDestDir = getInstallDir() + File.separator + "packages" + File.separator + PACKAGE_NAME + File.separator + "code" + File.separator + "jars";
            for (int i=0; i < mqJarFileName.length; i++)
            {
                String fileName = jarDestDir + File.separator + mqJarFileName[i];
                if (AdapterUtils.deleteFile(fileName))
                {
                    DebugLog.println(fileName + " deleted");
                }
            }
        }

        AdapterUtils.deleteFile(uninstallObjectFileName);

		return true;
    }

}
