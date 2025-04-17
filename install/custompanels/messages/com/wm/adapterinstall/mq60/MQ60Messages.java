package com.wm.adapterinstall.mq60;
import com.wm.distman.install.DistManUtils;

public class MQ60Messages extends java.util.ListResourceBundle {


  private static Object loadResource( String aResource ) {
        return DistManUtils.loadResource( MQ60Messages.class.getClassLoader(), aResource );
  }


  public MQ60Messages() {
  }

  protected Object[][] getContents() {
        return this.contents;
  }

  static public final Object[][] contents = {

     {"MQ60.Readme",
       (DistManUtils.loadResource
    (MQ60Messages.class.getClassLoader(),
     "com/wm/adapterinstall/mq60/readme.html"))},

      {"MQ60.ReadMeEncoding",
       "ISO8859_1"},

      {"MQ60.displayName",
       "Documentation and Program Files"},

      {"MQ60.displayDescript",
       "webMethods WebSphere MQ Adapter"},

      {"MQ60.displayGroup",
       "Adapters/webMethods WebSphere MQ Adapter"},

       {"MQ60.dependencyInstallError",
          "webMethods WebSphere MQ Adapter requires Integration Server 6.0.1 SP2 or higher "  },

	   {"MQ60.validMQSeriesFound",
	      "The Installer detected an existing WebSphere MQ Adapter v3.0.  If you select yes, the existing adapter will be backed up. It can be restored by uninstalling the WebSphere MQ Adapter v6.0.  Continue?"  },

       {"MQ60.invalidMQSeriesFound",
          "The installer detected a previous version of the WebSphere MQ Adapter prior to v3.0.  This must be uninstalled or upgraded to version 3.0 for the installation to continue"  },

	   {"MQ60.warningTitle",
	      "webMethods Installer - WebSphere MQ Adapter"  },

       {"MQ60.prerequisiteAbsent",
       "This version of the WebSphere MQ Adapter requires IS 6.0.1 SP2 or better to be present. The installation of the WebSphere MQ Adapter cannot proceed."},

       {"MQ60.preexistingMQ",
       "There is a pre-existing WebSphere MQ Adapter ({0}). Uninstall it before installing this version of the WebSphere MQ Adapter"},

       {"MQ60.multipleSelection",
       "Only one version of the WebSphere MQ Adapter can be selected for installation"},

	   {"MQ60.preexistingAdapterRestored",
	      "A WebSphere MQ Adapter v3.0 that was installed before v6.0 has been restored."  },

	   {"MQ60.directoryNotExists",
	      "Directory {0} does not exist."  },

	   {"MQ60.fileNotExists",
	      "File {0} does not exist."  },

	   {"MQ60.couldNotCreateDirectory",
	   	      "Directory {0} could not be created.  Copy jars manually."  },

	   {"MQ60.couldNotCopyFile",
	      "File {0] could not be copied to {1}.  Copy jars manually."  },

	   {"MQ60.promptCopyJar",
	         "The WebSphere MQ Adapter requires the following IBM-supplied jars to be present in {0} : {1}.  The installer can incorporate them if they are available or you can add them later before using the adapter. See the adapter''s Installation Guide for more information.  Copy IBM jars?"},

      {"MQ60.promptJarDirectory",
       "Please enter directory containing IBM-supplied jars"},

      {"MQ60.title",
       "webMethods WebSphere MQ Adapter"},

	   {"MQ60.fontStyle",
       		"dialog"},
 	   {"MQ60.fontBoldStyle",
       		new Integer(1)},
 	   {"MQ60.fontPlainStyle",
       		new Integer(0)},
	   {"MQ60.fontSize",
       		new Integer(12)},

      {"MQ60.title",
       "webMethods WebSphere MQ Adapter"},

      {"MQ60.lblDescription",
       "The WebSphere MQ Adapter requires the following IBM-supplied jars to be present in {0} : {1}.  The installer can incorporate them if they are available or you can add them later before using the adapter. See the adapter''s Installation Guide for more information."},

	  {"MQ60.chkCopyJar",
   		"Copy IBM jars"},

   	  {"MQ60.lblJarDirectory",
   		"IBM-supplied jar directory:"},

	  {"MQ60.browse",
		  "Browse"},

	  {"MQ60.browseTitle",
		  "Select Jar Directory"},

	  {"MQ60.browseSelect",
		  "Select"}
    };

}
