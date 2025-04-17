package com.wm.adapterinstall.mq60;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
// import com.wm.adapterinstall.*;
import com.wm.distman.install.*;
import com.wm.distman.helpers.*;

public class ProgramFilesWizardPanel extends AbstractAdapterWizardPanel implements ActionListener {

    boolean isToBeCopied;
	DMLabel lblTitle;
    DMLabel lblDescription;
	DMLabel lblJarDirectory;
	DMTextField fldJarDirectory;
	DMButton btnBrowse;
	
	JCheckBox chkCopyJar;

	String jarSourceDir;
	String jarString;
	private String mqJarFileName [] = {"com.ibm.mq.jar", "com.ibm.mq.pcf.jar"};

	private static final String PACKAGE_NAME ="WmMQAdapter";
	
    private static final String DISPLAY_NAME_VALUE="DisplayName";
    private static final String UNINSTALL_STRING_VALUE="UninstallString";
	public boolean installInit ()
	{
		super.installInit(PACKAGE_NAME);
		isToBeCopied=true;
		jarSourceDir="";

		jarString = mqJarFileName[0] +", " + mqJarFileName[1];

//		Coding to override the setting in the properties file of the resource bundle
//		this.setResourceBundle("com.wm.adapterinstall.mq60.ProgramFilesMessages");
		return true;
	}

	public boolean installOkToContinue()
	{
        String packageDirectories [] = {"MQSeries"};
        String copyParameter = "false";
        String uninstallObjectFileName = getUninstallObjectFileName();
		if (!this.silentInstall)
		{
	        String message="";
			if (isToBeCopied)
			{
			    if(!(consoleInstall || silentInstall))
			        jarSourceDir = fldJarDirectory.getText().trim();
				if (!AdapterUtils.dirExists(jarSourceDir))
				{
						message = getMessage("MQ60.directoryNotExists", jarSourceDir);  //"Directory {0} does not exist"

						AdapterUtils.showWarning( topPanel,
							message,
							getMessage("MQ60.warningTitle"), // "webMethods Installer - webSphere MQ Adapter"
			            	this.consoleInstall,
			            	this.silentInstall );
				    return false;
				}
				for (int i=0; i < mqJarFileName.length; i++)
				{
					String fileName = jarSourceDir + File.separator + mqJarFileName[i];
					if (!AdapterUtils.fileExists(fileName))
					{
						message = getMessage("MQ60.fileNotExists", fileName); // "File {0} does not exist"
						AdapterUtils.showWarning( topPanel,
							message,
							getMessage("MQ60.warningTitle"),  // "webMethods Installer - webSphere MQ Adapter"
	            			this.consoleInstall,
	            			this.silentInstall );
						return false;
					}
				}

                String jarDestDir = getInstallDir() + File.separator + "packages" + File.separator + PACKAGE_NAME + File.separator + "code" + File.separator + "jars";
                if (!AdapterUtils.dirExists(jarDestDir))
                {
                    if(AdapterUtils.createDir(jarDestDir))
                    {
                        DebugLog.println ("created directory " + jarDestDir);
                    }
                    else
                    {
                        DebugLog.println(jarDestDir + " directory not created");
                        message = getMessage("MQ60.couldNotCreateDirectory", jarDestDir); // "Directory {0} could not be created.  Copy jars manually"
                        AdapterUtils.showWarning( topPanel,
                            message,
                            getMessage("MQ60.warningTitle"),  // "webMethods Installer - Siebel Adapter"
                            this.consoleInstall,
                            this.silentInstall );
                    }
                }
                 if (AdapterUtils.dirExists(jarDestDir))
                {
                    copyParameter="true";
                    for (int i=0; i < mqJarFileName.length; i++)
                    {
                        String sourceFileName = jarSourceDir + File.separator + mqJarFileName[i];
                        String destFileName = jarDestDir + File.separator + mqJarFileName[i];
                        if (AdapterUtils.copyFile(sourceFileName, destFileName))
                        {
                            DebugLog.println(sourceFileName + " copied to " + destFileName);
                        }
                        else
                        {
                            DebugLog.println(sourceFileName + " not copied to " + destFileName);
                            message = getMessage("MQ60.couldNotCopyFile", sourceFileName, destFileName); // "File {0] could not be copied to {1}.  Copy jars manually"
                            AdapterUtils.showWarning( topPanel,
                                message,
                                getMessage("MQ60.warningTitle"),  // "webMethods Installer - webSphere MQ Adapter"
                                this.consoleInstall,
                                this.silentInstall );
                            copyParameter= "false";
                        }
                    }
                }
			}
		}

        AdapterUtils.setUninstallParameter("wasCopied", copyParameter, uninstallObjectFileName);
        
        
        return true;
    }

	public boolean installSetupConsole ()
	{
		boolean valid=false;
		System.out.println(getMessage("MQ60.title")); // "webMethods Installer - webSphere MQ Adapter"
		System.out.println("");
        String message = getMessage("MQ60.promptCopyJar", getJarsDir(), jarString); //"The WebSphere MQ Adapter requires the following IBM-supplied jars to be present in {0} : {1}.  The installer can incorporate them if they are available or you can add them later before using the adapter. See the adapter''s Installation Guide for more information.  Copy IBM jars?"
		int intCopy = Console.YesNoQuery(message, Console.NO); // ""
		if (intCopy==Console.YES)
		{
			isToBeCopied = true;
			valid = false;
			jarSourceDir="";
			while (!valid)
			{
				jarSourceDir = Console.TextQuery("",  getMessage("MQ60.promptJarDirectory"), jarSourceDir); // "Please enter directory containing IBM-supplied jars"
				if (AdapterUtils.dirExists(jarSourceDir))
				{
					valid=true;
					for (int i=0; i < mqJarFileName.length; i++)
					{
						String fileName = jarSourceDir + File.separator + mqJarFileName[i];
						if (!AdapterUtils.fileExists(fileName))
						{
							message = getMessage("MQ60.fileNotExists", fileName); // "File {0} does not exist"
							AdapterUtils.showWarning( topPanel,
								message,
								getMessage("MQ60.warningTitle"),  // "webMethods Installer - webSphere MQ Adapter"
		            			this.consoleInstall,
		            			this.silentInstall );
							valid = false;
						}
					}
				}
				else
				{
					message = getMessage("MQ60.directoryNotExists", jarSourceDir); //"Directory {0} does not exist"
					AdapterUtils.showWarning( topPanel,
						message,
						getMessage("MQ60.warningTitle"),  // "webMethods Installer - webSphere MQ Adapter"
		            	this.consoleInstall,
		            	this.silentInstall );
				}
			}
		}
		else
		{
			isToBeCopied=false;
		}

		return true;
	}

    public boolean installSetupGUI()
    {
		GridBagLayout gridBagLayout1 = new GridBagLayout();
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(gridBagLayout1);

		lblTitle = new DMLabel();
		lblTitle.setFont(new java.awt.Font(getMessage("MQ60.fontStyle"), getMessageInt("MQ60.fontBoldStyle"),getMessageInt("MQ60.fontSize")));
		lblTitle.setText(getMessage("MQ60.title"));   // "webMethods webSphere MQ Adapter"
		innerPanel.add(lblTitle,   new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));

		lblDescription = new DMLabel();
        lblDescription.setText(getMessage("MQ60.lblDescription", getJarsDir(), jarString)); //"The WebSphere MQ Adapter requires the following IBM-supplied jars to be present in {0} : {1}.  The installer can incorporate them if they are available or you can add them later before using the adapter. See the adapter''s Installation Guide for more information."
		innerPanel.add(lblDescription,    new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));

		JPanel spacer = new JPanel();
   //     innerPanel.add(spacer,    new GridBagConstraints(0, 2, 4, 1, 1.0, 1.0
   //        ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(11, 0, 0, 5), 0, 0));
		
		innerPanel.add(spacer,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 10,0));
        JPanel spacer1 = new JPanel();
        innerPanel.add(spacer1,    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 75,0));
        JPanel spacer2 = new JPanel();
        innerPanel.add(spacer2,    new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 75,0));
        JPanel spacer3 = new JPanel();
        innerPanel.add(spacer3,    new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 75,0));
        JPanel spacer4 = new JPanel();
        innerPanel.add(spacer4,    new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 75,0));
        JPanel spacer5 = new JPanel();
        innerPanel.add(spacer5,    new GridBagConstraints(5, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 75,0));

		chkCopyJar = new JCheckBox();
		chkCopyJar.setText(getMessage("MQ60.chkCopyJar")); // "Copy IBM jars"

		isToBeCopied=true;
		chkCopyJar.setSelected(isToBeCopied);
		chkCopyJar.addItemListener(new java.awt.event.ItemListener()
		{
			public void itemStateChanged(java.awt.event.ItemEvent event)
			{
				Object source = event.getSource();
				if (source.equals(chkCopyJar))
				{
					isToBeCopied = chkCopyJar.isSelected();
					lblJarDirectory.setVisible(isToBeCopied);
					fldJarDirectory.setVisible(isToBeCopied);
					btnBrowse.setVisible(isToBeCopied);
				}
			}
		});
		innerPanel.add(chkCopyJar,    new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		lblJarDirectory=new DMLabel();
		lblJarDirectory.setText(getMessage("MQ60.lblJarDirectory"));   // "IBM-supplied jar directory:"

		innerPanel.add(lblJarDirectory,    new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		fldJarDirectory = new DMTextField();
		innerPanel.add(fldJarDirectory,   new GridBagConstraints(2, 4, GridBagConstraints.REMAINDER, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		btnBrowse = new DMButton();
		btnBrowse.setText(getMessage("MQ60.browse")); //"Browse
		btnBrowse.addActionListener(this);
		innerPanel.add(btnBrowse,    new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		topPanel.setLayout(new BorderLayout());
		topPanel.add(innerPanel, BorderLayout.NORTH);
		return true;
    }

    public boolean uninstallExecute()
    {

        DebugLog.println("Program Files panel uninstall");
		return true;
    }


	public boolean uninstallInit()
	{
		super.uninstallInit(PACKAGE_NAME);
		return true;
	}

    public boolean isUninstallPanelEnabled()

    {
        return false;
    }

	public boolean uninstallOkToContinue() {

        return true;
    }

	public boolean uninstallSetupConsole()
	{
		return true;
	}

	public boolean uninstallSetupGUI()
	{

		return true;
	}

	public void actionPerformed(ActionEvent evt) {

	    Object source = evt.getSource();

	    if (source == btnBrowse)
		{

			DebugLog.println("Browse button pushed");
			JFileChooser fileChooser = new JFileChooser ();
	    	fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    	if (!fldJarDirectory.getText().equals(""))
			    fileChooser.setCurrentDirectory(new File (fldJarDirectory.getText()));
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setDialogTitle(getMessage("MQ60.browseTitle")); //"Select IBM Jar Directory"
			fileChooser.setApproveButtonToolTipText(msg("MQ60.browseTitle"));
			fileChooser.setApproveButtonText(msg("MQ60.browseSelect")); //"Select"
			if (fileChooser.showOpenDialog(topPanel) == JFileChooser.APPROVE_OPTION)
			{
			  	fldJarDirectory.setText(fileChooser.getSelectedFile ().getPath ());
			}
	   }
	}
}
