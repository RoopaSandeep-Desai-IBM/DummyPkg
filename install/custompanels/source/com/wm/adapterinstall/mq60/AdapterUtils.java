package com.wm.adapterinstall.mq60;

import com.wm.distman.install.*;
import com.wm.distman.helpers.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import javax.swing.*;

/**
 * <p>Title: AdapterUtils</p>
 * <p>Description: This contains static methods useful in Wizard, Progress or Action Panels
 * used in adapter custom panels.</p>
 * <p>Copyright (c) 1996-2003, webMethods Inc.  All Rights Reserved.</p>
 * @author $Author: pfish $
 */
public class AdapterUtils {
    // Not for localization.
    public static final String INTEGRATION_SERVER_NAME = "IntegrationServer";
	public static final int    NO                      = 0;
	public static final int    YES                     = 1;
	private static final int BLOCKSIZE = 8192;
    private static final String DISPLAY_NAME_VALUE="DisplayName";
    private static final int FULL_PERMISSIONS = 511;
    private static final String UNINSTALL_STRING_VALUE="UninstallString";
    private static final String PATH_KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall";
    private static boolean modifiedFlag;
     /**
   * Backups classic adapters where InstallShield has installed _uninst component
   *@param backupName Name of backup directory <server>/IntegrationServer/replicate/salvage/backupName
   *@param installDir Name of install directory <server>/IntegrationServer
   *@param packageDirectories Names of packages to be backed up
   *@param uninstallPackageDirectory  Name of uninstall package directory under _uninst
   *@return true if backup completed
   */
    public static boolean backupClassicAdapters(String backupName, String installDir, String [] packageDirectories, String uninstallPackageDirectoryName)
    {
        String backupDirectoryName = installDir + File.separator + "replicate"+ File.separator + "salvage"+ File.separator + backupName;
        File packageBackupDirectory = new File(backupDirectoryName + File.separator + INTEGRATION_SERVER_NAME + File.separator + "packages");
        if(!packageBackupDirectory.exists())
            packageBackupDirectory.mkdirs();
        for (int i = 0; i < packageDirectories.length; i++)
        {
            File adapterBackupDirectory = new File(packageBackupDirectory, packageDirectories[i]);
            adapterBackupDirectory.mkdir();
            if (copyDir(installDir + File.separator + "packages" + File.separator + packageDirectories[i], packageBackupDirectory + File.separator + packageDirectories[i]))
            {
                log(packageDirectories[i] + " copied");
     //             setManifestTagValue(installDir + File.separator + "packages" + File.separator + packageDirectories[i]  + File.separator + "manifest.v3", "version", "tbc");

              deleteDir(installDir + File.separator + "packages" + File.separator + packageDirectories[i]);
            }
        }

        if (dirExists(installDir + File.separator + "_uninst" + File.separator + uninstallPackageDirectoryName))
        {
            File uninstallBackupDirectory = new File (backupDirectoryName + File.separator + INTEGRATION_SERVER_NAME + File.separator + "_uninst");
            uninstallBackupDirectory.mkdir();
            if (copyDir(installDir + File.separator + "_uninst" + File.separator + uninstallPackageDirectoryName,  backupDirectoryName + File.separator + INTEGRATION_SERVER_NAME + File.separator + "_uninst"  + File.separator + uninstallPackageDirectoryName))
            {
                log(uninstallPackageDirectoryName + " uninstall directory copied");
                deleteDir(installDir + File.separator + "_uninst" + File.separator + uninstallPackageDirectoryName);
            }
        }
        return true;
    }

   /**
   * Backups classic adapters
   *@param backupName Name of backup directory <server>/IntegrationServer/replicate/salvage/backupAdapter
   *@param installDir Name of install directory <server>/IntegrationServer
   *@param packageDirectories Names of packages to be backed up
   *@return true if backup completed
   */
    public static boolean backupClassicAdapters(String backupName, String installDir, String [] packageDirectories)
    {
        String backupDirectoryName = installDir + File.separator + "replicate"+ File.separator + "salvage"+ File.separator + backupName;
        File packageBackupDirectory = new File(backupDirectoryName + File.separator + INTEGRATION_SERVER_NAME + File.separator + "packages");
        if(!packageBackupDirectory.exists())
            packageBackupDirectory.mkdirs();
        for (int i = 0; i < packageDirectories.length; i++)
        {
            File adapterBackupDirectory = new File(packageBackupDirectory, packageDirectories[i]);
            adapterBackupDirectory.mkdir();
            if (copyDir(installDir + File.separator + "packages" + File.separator + packageDirectories[i], packageBackupDirectory + File.separator + packageDirectories[i]))
            {
                log(packageDirectories[i] + " copied");
                deleteDir(installDir + File.separator + "packages" + File.separator + packageDirectories[i]);
            }
        }
        return true;
    }

   /**
   * Backups local product
   *@param backupName Name of backup directory <server>/IntegrationServer/replicate/salvage/backupAdapter
   *@param installDir Name of install directory <server>/IntegrationServer
   *@param bms Prefix names of bms to be moved
   *@param products Names of .prop files to be moved
   *@param packageDirectories Names of package directories to be moved
   *@param docBackup set to true if doc directory only.
   *@return true if backup completed
   */
    public static boolean backupLocalProduct(String backupName, String installDir, String [] bms, String [] products, String [] packageDirectories, boolean docBackup)
    {
        File replicateDirectory = new File(installDir + File.separator + "replicate");
        if (replicateDirectory.isDirectory())
        {
        }
        else
            replicateDirectory.mkdir();
        File salvageDirectory = new File(installDir + File.separator + "replicate"+ File.separator + "salvage");
        if (salvageDirectory.isDirectory())
        {
        }
        else
            salvageDirectory.mkdir();
        String backupDirectoryName = installDir + File.separator + "replicate"+ File.separator + "salvage"+ File.separator + backupName;
        File backupDirectory = new File(backupDirectoryName);
        if (backupDirectory.isDirectory())
        {
            if (deleteDir(backupDirectoryName))
            {
                log(backupDirectoryName + " deleted");
            }
        }
        backupDirectory.mkdir();
        log(backupDirectoryName + " created");

        File installBackupDirectory = new File(backupDirectoryName + File.separator + "install");
        installBackupDirectory.mkdir();
        File bmsBackupDirectory = new File(installBackupDirectory, "bms");
        bmsBackupDirectory.mkdir();
        File bmsSourceDirectory = new File(installDir + File.separator + ".." + File.separator + "install" + File.separator + "bms");
        String list[] = bmsSourceDirectory.list();
        for (int i=0; i < list.length; i++)
        {
            for (int j=0; j < bms.length; j++)
            {
                if (list[i].startsWith(bms[j]))
                {

                    if (copyFile(bmsSourceDirectory.getAbsolutePath() + File.separator + list[i], bmsBackupDirectory.getAbsolutePath() + File.separator + list[i]))
                        log("copied " + bmsSourceDirectory.getAbsolutePath() + File.separator + list[i]);
                    else
                        log("Did not copy " + bmsSourceDirectory.getAbsolutePath() + File.separator + list[i]);
                }
            }
        }

        File productsDirectory = new File(installBackupDirectory, "products");
        productsDirectory.mkdir();
        File productsSourceDirectory = new File(installDir + File.separator + ".." + File.separator + "install" + File.separator + "products");
        for (int i = 0; i < products.length; i++)
        {
             if (copyFile(productsSourceDirectory.getAbsolutePath() + File.separator + products[i] + ".prop", productsDirectory.getAbsolutePath() + File.separator + products[i] + ".prop"))
                log("copied " + productsSourceDirectory.getAbsolutePath() + File.separator + products[i] + ".prop");
              else
                log("Did not " + productsSourceDirectory.getAbsolutePath() + File.separator + products[i] + ".prop");
         }

		File isBackupDirectory = new File(backupDirectoryName + File.separator + INTEGRATION_SERVER_NAME);
		isBackupDirectory.mkdir();
		File packageBackupDirectory = new File(backupDirectoryName + File.separator + INTEGRATION_SERVER_NAME + File.separator + "packages");
		packageBackupDirectory.mkdir();
		File adapterBackupDirectory = new File(packageBackupDirectory, packageDirectories[0]);
		adapterBackupDirectory.mkdir();
		File docBackupDirectory = new File(adapterBackupDirectory, "doc");
		if (docBackup)
		{
			docBackupDirectory.mkdir();
			if (copyDir(installDir + File.separator + "packages" + File.separator + packageDirectories[0] + File.separator + "doc", docBackupDirectory.getAbsolutePath()))
			{
				log(installDir + File.separator + "packages" + File.separator + packageDirectories[0] + File.separator + "doc copied");
			}
		}
		else
		{
			for (int i = 0; i < packageDirectories.length; i++)
			{
				if (copyDir(installDir + File.separator + "packages" + File.separator + packageDirectories[i], packageBackupDirectory + File.separator + packageDirectories[i]))
				{
					log(packageDirectories[i] + " copied");
				}
			}

			if (docBackupDirectory.isDirectory())
			{

				if(deleteDir(docBackupDirectory.getAbsolutePath()))
				{
					log(docBackupDirectory.getAbsolutePath() + " deleted");
				}
			}
		}

         if (deleteLocalProduct(installDir, bms, products, packageDirectories, docBackup))
         {
            log("Clean up of " + backupName);
         }
         else
         {
            log(backupName + " not cleaned up");
            return false;
         }
         return true;
    }

    /**
     * Check if propertyName.properties is installed.
     * @param propertyName Name of the propertyName file.
     * @param ipp InstallerProductProps from installerAccess.getInstallerProductProps()
     * @return boolean value as true if propertyName.properties file is installed.
     */
    public static boolean checkLocalProductProperties(String propertyName, InstallerProductProps ipp)
    {
        String localProducts [] = ipp.getLocalProducts();
        if (localProducts == null)
            log("Null local products");
        else
        {
            for (int i=0; i <localProducts.length; i++)
            {
              String propName = ipp.getNodeName(localProducts[i]);
              if (propName.equals(propertyName))
                return true;
            }
        }
        return false;
    }

     /**
   * Determines if package exists is a particular version based on manifest file
   *@param version String to be present in manifest file
   *@param packageDir Name of directory to be checked <server>/IntegrationServer/packages/package
   *@return Returns true if package exists and is version
   */
    public static boolean checkPackageManifestVersion(String version, String packageDir)
    {
       String manifestFileName = packageDir + File.separator +"manifest.v3";
       if (fileExists(manifestFileName))
       {
           String versionString = getManifestTagValue(manifestFileName, "version");
           if (versionString.equals(version))
            return true;
       }
       return false;
    }

 /**
   * Checks all files in a package recursively to see if it has any files modified outside a window
   * based on the modifed value of the manifest.v3
   *@param packageDir Name of directory to be checked <server>/IntegrationServer/packages/package
   *@return true if modification has been done
   */
    public static boolean checkPackageModified(String packageDir)
    {
        modifiedFlag = false;
        File directory = new File (packageDir);
        if (directory.isDirectory())
        {
            String manifestFileName = packageDir + File.separator + "manifest.v3";
            File manifestFile = new File (manifestFileName);
            if (manifestFile.isFile())
            {
                long upperBound = manifestFile.lastModified();
            //    long lowerBound = upperBound - 300000;
                long lowerBound=0;
                upperBound = upperBound + 300000;
                checkDirectoryModified(packageDir, upperBound, lowerBound);
            }
        }

        return modifiedFlag;
    }

   /**
   * Recursively copies directories
   *@param srcDirName Source directory name
   *@param destDirName Destination directory name
   *@return true if copy completed
   */
	public static boolean copyDir(String srcDirName, String destDirName)
	{

		File srcFile = new File(srcDirName);
		File destFile = new File(destDirName);
		String srcFileName ="";
		String destFileName ="";
		if (!destFile.isDirectory())
		{
			if(destFile.mkdir())
				log("directory " + destDirName + " created");
			else
				log("directory " + destDirName + " not created");
		}
		String fileName [] = srcFile.list();
		for (int i=0; i < fileName.length; i++ )
		{
			File file = new File(srcFile, fileName[i]);
			if (file.isDirectory())
			{
				copyDir(srcDirName + File.separator + fileName[i], destDirName + File.separator + fileName[i]);
			}
			else
			{
				srcFileName = srcDirName + File.separator + fileName[i];
				destFileName = destDirName + File.separator + fileName[i];
				if (copyFile(srcFileName, destFileName))
					log("file " + srcFileName + " copied to " + destFileName);
				else
					log("file " + srcFileName + " copied to " + destFileName);
			}
		}
		return true;
	}

   /**
   * Copies file
   *@param srcFileName Source file name
   *@param destFileName Destination file name
   *@return true if copy completed
   */
	public static boolean copyFile( String srcFileName, String destFileName ) {
        if (!fileExists(srcFileName))
            return true;
		try {
			InputStream inputStream = new FileInputStream( srcFileName );
			OutputStream outputStream = new FileOutputStream( destFileName );
			int count = 0;
			byte buffer[] = new byte[ BLOCKSIZE ];
			while ( ( count = inputStream.read( buffer ) ) != -1 )
			{
				outputStream.write( buffer, 0, count );
			}
            inputStream.close();
            outputStream.close();
            return true;
		}
        catch ( IOException ex )
        {
             handleException(ex);
             return false;
        }
	}

   /**
   * Creates directory and any missing parent directory
   *@param directoryName Name of directory to be created
   *@return true if creation completed
   */

	public static boolean createDir(String directoryName)
	{
		File directory = new File (directoryName);
        directory.mkdirs();
		if (directory.isDirectory())
			return true;
		else
			return false;
	}

    /**
   * Create Windows Registry entry for InstallShield uninstall key
   *@param registryHelper Instance of IRegistryHelper created using IRegistryHelper registryHelper = installerAccess.createRegistryHelper()
   *@param createKeyName Name of key to be created under HKEY_LOCAL_MACHINE
   *@param createValues Values of "DisplayName" and "UninstallString" to be created
   *@returns true if key correctly removed
   */
    public static boolean createWindowsRegistryEntry(IRegistryHelper registryHelper, String createKeyName, String [] createValues)
    {
        String createValueNames [] = {DISPLAY_NAME_VALUE, UNINSTALL_STRING_VALUE};
        if(createRegistryEntry(registryHelper, createKeyName, createValueNames, createValues))
          return true;
        else
          return false;
    }

   /**
   * Deletes directory
   *@param directoryName Name of directory to be deleted
   *@return true if deletion completed
   */
	public static boolean deleteDir(String directoryName)
	{

			File dirFile = new File(directoryName);
			String temp = null;
			File dir = new File(directoryName);
			String fileName [] = dir.list();
			if (fileName.length == 0)
			{
				log("empty directory " + directoryName + " deleted");
				dir.delete();
			}
			else
			{
				for (int i=0; i < fileName.length; i++ )
				{
					File file = new File(dir, fileName[i]);
					if (file.isDirectory())
					{
						deleteDir(directoryName + File.separator + fileName[i]);
					}
					else
					{
						temp = directoryName + File.separator + fileName[i];
						if (file.delete())
							log("file " + temp + " deleted");
						else
							log("file " + temp + " not deleted");
					}
				}
				log("directory " + directoryName + " deleted");
				dir.delete();
			}
			return true;
	}

   /**
   * Deletes file
   *@param fileName Name of file to be deleted
   *@return true if deletion completed
   */
	public static boolean deleteFile(String fileName)
	{
		if (fileExists(fileName))
		{
			File file= new File (fileName);
			if (file.delete())
			{
				log("file " + fileName + " deleted");
				return true;
			}
			else
				log("file " + fileName + " not deleted");
		}
		return false;
	}

   /**
   * Deletes local product by removing .prop files from install/products, build modules form install/bms and removing specified directories
   *@param installDir Name of install directory <server>/IntegrationServer
   *@param bms prefix names of bms to be removed
   *@param products Names of .prop files to be removed
   *@param deleteDirectories Names of directories to be deleted
   *@return true if deletion completed
   */
	public static boolean deleteLocalProduct(String installDir, String [] bms, String [] products, String [] deleteDirectories, boolean docBackup)
	{
		File baseDirectory = new File(installDir + File.separator + ".." + File.separator + "install");
		if (baseDirectory.isDirectory())
		{
			deleteFileStarts(installDir + File.separator + ".." + File.separator + "install" + File.separator + "bms", bms);
			for (int i = 0; i < products.length; i++)
			{
				deleteFile(installDir + File.separator + ".." + File.separator + "install" + File.separator + "products" + File.separator + products[i] + ".prop");
				log("deleted " + installDir + File.separator + ".." + File.separator + "install" + File.separator + "products" + File.separator + products[i] + ".prop");
			}
		}
		else
		{
			log(baseDirectory.getAbsolutePath() + " not found");
			return false;
		}
		if (docBackup)
		{
			File docDirectory = new File(installDir + File.separator + "packages" + File.separator +deleteDirectories[0] + File.separator + "doc");
			if (docDirectory.isDirectory())
			{
				deleteDir(docDirectory.getAbsolutePath());
			}
		}
		else
		{
			for (int i = 0; i < deleteDirectories.length; i++)
			{
				File directory = new File(installDir + File.separator + "packages" + File.separator + deleteDirectories[i]);
				if (directory.isDirectory())
				{
    				log(deleteDirectories[i] + " deleted");
    				File packageDirectory = new File(installDir + File.separator + "packages" + File.separator + deleteDirectories[i]);
    				File docDirectory = new File(installDir + File.separator + "packages" + File.separator + deleteDirectories[i] + File.separator + "doc");
					if (docDirectory.isDirectory())
					{

						File tempDirectory = new File(installDir + File.separator + "packages" + File.separator + "temp");
						if (createDir(tempDirectory.getAbsolutePath()))
						{
							copyDir(docDirectory.getAbsolutePath(), tempDirectory.getAbsolutePath());
							deleteDir(directory.getAbsolutePath());
							if (!packageDirectory.isDirectory())
								packageDirectory.mkdir();
							copyDir(tempDirectory.getAbsolutePath(), docDirectory.getAbsolutePath());
							deleteDir(tempDirectory.getAbsolutePath());
							log(tempDirectory.getAbsolutePath() + " deleted ");
						}
					}
					else
					{
                    	deleteDir(directory.getAbsolutePath());
					}

				}
			}
		}
		return true;
	}

   /**
   * Determines if directory exists
   *@param directoryName Name of directory to be tested
   *@return true if directory exists
   */
	public static boolean dirExists(String directoryName)
	{
		File directory = new File(directoryName);
		if (directory.isDirectory())
			return true;
		else
			return false;
	}

   /**
   * Determines if file exists
   *@param fileName Name of file to be tested
   *@return true if file exists
   */
	public static boolean fileExists(String fileName)
	{
		File file = new File(fileName);
		if (file.isFile())
			return true;
		else
			return false;
	}

   /**
   * Creates file from String[]
   *@param fileName Name of file to be created
   *@param lines Lines to be included in file.
   *@return true if file written
   */
	public static boolean fileWrite (String fileName, String [] lines)
    {
		try
		{
			File file = new File( fileName );
			FileWriter fileWriter = new FileWriter( file );
			BufferedWriter bufferedWriter = new BufferedWriter( fileWriter );

			for (int i=0; i < lines.length; i++)
			{
				String line = lines[i];
				bufferedWriter.write( line);
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
			fileWriter.close();
		}
		catch ( FileNotFoundException ex )
		{
			handleException(ex);
			return false;
		}
		catch ( IOException ex )
		{
			handleException(ex);
			return false;
		}
		return true;
	}

      /**
     * Returns propertyName for a particular prettyName.
     * @param prettyName getPrettyNameUS() of local product
     * @param ipp InstallerProductProps from installerAccess.getInstallerProductProps()
     * @return propertyName name of the propertyName file.
     */
    public static String getLocalProductPropertyName(String prettyName, InstallerProductProps ipp)
    {
        String propName = "";
        String localProducts [] = ipp.getLocalProducts();
        for (int i=0; i <localProducts.length; i++)
        {
            if ((ipp.getPrettyNameUS(localProducts[i])).equals(prettyName))
            {
               propName = ipp.getNodeName(localProducts[i]);
               break;
            }
        }
        return propName;
    }

     /**
     * Returns version of propertyName.properties in local product.
     * @param propertyName Name of the property file.
     * @param ipp InstallerProductProps from installerAccess.getInstallerProductProps()
     * @param encryptionLevel EncryptionLevel from installerAccess.getEncryptionLevel()
     * @return version of local product.
     */
    public static String getLocalProductPropertyVersion(String propertyName, InstallerProductProps ipp, String encryptionLevel)
    {
        String localProducts [] = ipp.getLocalProducts();

        String version = "0.0.0";
        if (localProducts == null)
            log("Null local products");
        else
        {
            boolean flag= false;
            for (int i=0; i <localProducts.length; i++)
            {
              String propName = ipp.getNodeName(localProducts[i]);
              if (propName.equals(propertyName))
              {
                version = ipp.productVersionString(localProducts[i], encryptionLevel);
                flag=true;
                break;
              }
            }
            if(!flag)
                log(propertyName + " local product property not found");
        }
        return version;
    }

   /**
   * Gets value of a tag in the manifest file
   *@param manifestFileName Name of manifest file
   *@param tagName Name of tag prefixed by <value name="
   *@return String value of tag retruns None if not found
   */
    public static String getManifestTagValue (String manifestFileName, String tagName)
    {
        String searchString = "<value name=\"" + tagName + "\">";
        int searchOffset = searchString.length();
        String tagValue="None";

        try
        {
             FileReader fr = new FileReader(manifestFileName);
             BufferedReader br = new BufferedReader(fr);
             String str = br.readLine();
             while (str != null)
             {
                str=str.trim();
                int k = str.indexOf(searchString);
                if (k > -1)
                {
                    int i = k + searchOffset;
                    int j = str.indexOf("<", i+1);
                    if ((i > -1) && (j > i))
                    {
                        tagValue = str.substring(i, j);
                        break;
                    }
                }
                str = br.readLine();
             }
             br.close();
             fr.close();
          }
          catch (IOException ex)
          {
            handleException(ex);
          }

        return tagValue;
   }

	/**
   * Gets Integration Server product name
   *@return Product name of Integration Server
   */
    public static String getProductName()
	{
        return INTEGRATION_SERVER_NAME;
    }
    
        /**
     * Returns version of propertyName.properties in remote product.
     * @param remoteProduct Product.
     * @param ipp InstallerProductProps from installerAccess.getInstallerProductProps()
     * @param encryptionLevel EncryptionLevel from installerAccess.getEncryptionLevel()
     * @return version of local product.
     */
     public static String getRemoteProductPropertyVersion(String remoteProduct, InstallerProductProps ipp, String encryptionLevel)
    {
        String version = "0.0.0";
        String [] buildModules = ipp.supportedBuildModules(remoteProduct, encryptionLevel);
        for (int j=0; j < buildModules.length; j++)
        {
            String buildModuleVersion = ipp.buildModuleVersion(buildModules[j]);
            if(DistManUtils.compareVersions(version, buildModuleVersion) < 0)
            {
                  version = buildModuleVersion;
             }
         }
         return version;
    }
 /**
    *  Gets the server version string contained in the build modules in integratnServer.prop
    * @param installDir Installation directory of the IS <server>/IntegraiontionServer
    * @param ipp InstallerProductProps from installerAccess.getInstallerProductProps()
    * @param encryptionLevel EncryptionLevel from installerAccess.getEncryptionLevel()   
    *  @return String value of the IS Server version.
    */

     
    public static String getServerVersion(String installDir, InstallerProductProps ipp, String encryptionLevel)
    {
        String propName="integrationServer";
        String version = "0.0";
        String propFileName = installDir + File.separator + ".." + File.separator + "install" + File.separator + "products" + File.separator +  propName + ".prop";
        if(fileExists(propFileName))
        {
            version = getLocalProductPropertyVersion(propName, ipp, encryptionLevel);
            String vers [] = new String [8];
            vers = DistManUtils.extractVersionStrings(version);
            version = vers[0] + "." + vers[1] + vers[2];
        }
        return version;
    }

    /**
    *  Gets the server version string contained in the update.cnf module in the server.jar
    *  @param installDir Installation directory of the IS <server>/IntegrationServer
    *  @return String value of the IS Server version.
    */
    public static String getServerVersion(String installDir)
    {
        String version = "None";
        String libDir = installDir + File.separator + "lib";
        if (AdapterUtils.dirExists(libDir))
        {
            String serverJarName = libDir + File.separator + "server.jar";
            if (AdapterUtils.fileExists(serverJarName))
            {
                try
                {
                    ZipFile zipFile=null;
                    ZipEntry zipEntry=null;
                    String line = null;
                    BufferedReader bufferedReader=null;
                    zipFile    = new ZipFile(serverJarName);
                    zipEntry   = zipFile.getEntry("update.cnf");
                    bufferedReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
                    while ((line=bufferedReader.readLine()) != null)
                    {
                        if (line.startsWith("updateProductVersion="))
                        {
                            version = line.substring(line.indexOf("=")+1);
                            int firstIndex= version.indexOf('.');
                            int lastIndex= version.lastIndexOf('.');
                            if (lastIndex > firstIndex)
                            {
                                StringBuffer sb = new StringBuffer(version.substring(firstIndex +1));
                                StringBuffer sb1 = new StringBuffer(version.substring(0,firstIndex+1));
                                for (int i =0; i < sb.length();++i)
                                {
                                    if (sb.charAt(i)!= '.')
                                        sb1.append(sb.charAt(i));
                                }
                                version=sb1.toString();
                            }
                        }
                    }
                    bufferedReader.close();
                }
                catch (IOException ex)
                {
                    handleException(ex);
                }
            }
        }
        return version;
      }

    /**
    * Gets and creates an uninstall script file from  a String [] object stored as an ArrayList in the uninstall object
    * @param key Key to a the  object in the uninstall object.
    * @param uninstallObjectFileName Name of uninstall object file  <server>/IntegrationServer/packages/package/uninstallObject
    * @param uninstallFileName  Name of the uninstall script file. <server>/IntegrationServer/packages/package/uninstall.sh
    * @param installerAccess Installer access instance.
    */
    public static void getUninstallFile(Object key, String uninstallObjectFileName, String uninstallFileName, IInstallerAccess installerAccess)
    {
        Object [] objectArray = null;
        String [] stringArray = null;
        Hashtable hashtable = new Hashtable();
        IFileHelper fileHelper = installerAccess.createFileHelper(new File(uninstallFileName));
        if (fileExists(uninstallObjectFileName))
        {
            try
            {
                FileInputStream fis = new FileInputStream(uninstallObjectFileName);
                ObjectInputStream ois = new ObjectInputStream(fis);
                hashtable = (Hashtable) ois.readObject();
            }
            catch (Exception ex)
            {
                log( "Error in getUninstallFile() " + ex.getMessage());
                return;
            }
        }
        if (hashtable.containsKey(key))
        {
            Object object = hashtable.get(key);

            if (object instanceof ArrayList)
            {
                objectArray =  ((ArrayList) object).toArray();
                stringArray = new String[objectArray.length];
                for (int i=0; i <objectArray.length; i++)
                {
                    stringArray[i] = (String) objectArray[i];
                }
                if (AdapterUtils.fileExists(uninstallFileName))
                {
                    if (AdapterUtils.deleteFile(uninstallFileName))
                        log("Deleted output file " + uninstallFileName);
                }
                if (AdapterUtils.fileWrite(uninstallFileName, stringArray))
                {
                        log(uninstallFileName + " written");
                        if (!DistManUtils.isWindows ())
                        {
                            setFilePermission(uninstallFileName, fileHelper );
                        }
                }
            }
            else
                log("Invalid type in getUninstallFile()");
        }
        else
            log("Key not found in getUninstallFile()");
     }

    /**
    * Gets a parameter from the uninstall object
    * @param key Key to a the  object in the uninstall object.
    * @param uninstallObjectFileName Name of uninstall object file  <server>/IntegrationServer/packages/package/uninstallObject
    * @return Object corresponding to key supplied. Returns null if not found
    */
     public static Object getUninstallParameter(Object key, String uninstallObjectFileName)
     {
        Hashtable hashtable = new Hashtable();
        Object object = new Object();
        if (fileExists(uninstallObjectFileName))
        {
            try
            {
                FileInputStream fis = new FileInputStream(uninstallObjectFileName);
                ObjectInputStream ois = new ObjectInputStream(fis);
                hashtable = (Hashtable) ois.readObject();
                ois.close();
                fis.close();
            }
            catch (Exception ex)
            {
                log("Exception in getUninstallParameter() " + ex.getMessage());
                return object;
            }
            if (hashtable.containsKey(key))
            {
                object = hashtable.get(key);
            }
            else
                log("Key not found in getUninstallParameter()");

         }
         else
            log("Uninstall object file not found in getUninstallParameter()");
        return  object;
     }

       /**
   * Remove Windows Registry entry put in by InstallShield for unisntall key.
   *@param windowsUninstallDisplayName Name displayed of classic adapter running remove/uninstall programs in ControlPanel
   *@param uninstallObjectFileName Name of uninstall object file  <server>/IntegrationServer/packages/package/uninstallObject
   *@returns true if key correctly removed
   */
     public static boolean removeWindowsRegistryEntry(IRegistryHelper registryHelper, String windowsUninstallDisplayName, String uninstallObjectFileName)
     {

        try
        {
            String uninstallKeys [] = registryHelper.listKeyNames(IRegistryHelper.HKEY_LOCAL_MACHINE, PATH_KEY);
            for (int i=0; i < uninstallKeys.length; i++)
            {
                String keyName = PATH_KEY + "\\" + uninstallKeys[i];
                String valueNames[] = registryHelper.listValueNames(IRegistryHelper.HKEY_LOCAL_MACHINE, keyName);
                if (valueNames.length > 0)
                {
                    int jDisplayName = -1;
                    int jUninstallString = -1;
                    for (int j=0; j < valueNames.length; j++)
                    {
                        if (valueNames[j].equals(DISPLAY_NAME_VALUE))
                        {
                            int valueType = registryHelper.getValueType(IRegistryHelper.HKEY_LOCAL_MACHINE, keyName, valueNames[j]);
                            if (valueType == IRegistryHelper.REG_SZ)
                                jDisplayName=j;
                        }
                        if (valueNames[j].equals(UNINSTALL_STRING_VALUE))
                        {
                            int valueType = registryHelper.getValueType(IRegistryHelper.HKEY_LOCAL_MACHINE, keyName, valueNames[j]);
                            if (valueType == IRegistryHelper.REG_SZ)
                                jUninstallString = j;
                        }
                     }

                     if ((jDisplayName >= 0) && (jUninstallString >=0))
                     {
                         String displayName = registryHelper.getStringValue(IRegistryHelper.HKEY_LOCAL_MACHINE, keyName, valueNames[jDisplayName]);

                         if (displayName.equals(windowsUninstallDisplayName))
                         {
                            String uninstallString = registryHelper.getStringValue(IRegistryHelper.HKEY_LOCAL_MACHINE, keyName, valueNames[jUninstallString]);
                            log("**      " + keyName + " found " + displayName + "/" + uninstallString);
                            log("**     " + valueNames[0] + "/" +valueNames[1]);
                            log("**     " + uninstallObjectFileName);
                            if (removeRegistryEntry(registryHelper, keyName, valueNames))
                            {
                                if(!setUninstallParameter("addRegistryEntry", "true", uninstallObjectFileName))
                                    log("addRegistryEntry not set");;
                                if(!setUninstallParameter("registryDisplayName", displayName, uninstallObjectFileName))
                                    log("registryDisplayName not set");
                                if(!setUninstallParameter("registryUninstallString", uninstallString, uninstallObjectFileName))
                                    log("registryUninstallString not set");
                                if(!setUninstallParameter("registryKeyName", keyName, uninstallObjectFileName))
                                    log("registryKeyName not set");
                              //  System.out.println("** saved in uninstallParameters");
                                return true;
                             }
                             else
                                log("registry entry not removed");
                             break;
                          }
                     }
                }

            }
            return false;
        }
        catch (RegistryHelperException ex)
        {
            log ("Registry Exception "  + ex.getMessage());
            return false;
        }
    }

    /**
   * Restores classic adapters
   *@param backupName Name of backup directory <server>/IntegrationServer/replicate/salvage/backupAdapter
   *@param installDir Name of install directory <server>/IntegrationServer
   *@param packageDirectories Names of packages to be restored
   *@param uninstallDirectoryName Flag set to true if the _uninst is to be also restored
   *@return true if restoration completed
   */
    public static boolean restoreClassicAdapters(String backupName, String installDir, String [] packageDirectories, boolean uninstallDirectoryFlag)
    {
        String backupDirectoryName = installDir + File.separator + "replicate"+ File.separator + "salvage"+ File.separator + backupName;
        File backupDirectory = new File(backupDirectoryName);
        if(backupDirectory.isDirectory())
        {
            for (int i = 0; i < packageDirectories.length; i++)
            {
                String destDirectoryName = installDir + File.separator + "packages" + File.separator + packageDirectories[i];
                File destDirectory = new File(destDirectoryName);
                if (destDirectory.isDirectory())
                {
                    deleteDir(destDirectoryName);
                }
                String sourceDirectoryName = backupDirectoryName + File.separator + INTEGRATION_SERVER_NAME + File.separator + "packages" + File.separator + packageDirectories[i];
                log ("Restore of " + sourceDirectoryName + " to " + destDirectoryName);
                if (copyDir(sourceDirectoryName,destDirectoryName))
                {
                    log("Restore of package " + packageDirectories[i]);
                    if (deleteDir(sourceDirectoryName))
                    {
                        log("Deletion of package " + packageDirectories[i]);
                    }
                    else
                    {
                        log(packageDirectories[i] + " not deleted");
                    }
                 }
                 else
                 {
                    log(packageDirectories[i] + " not restored");
                    return false;
                }
            }
            if (uninstallDirectoryFlag)
            {
                String sourceDirectoryName = backupDirectoryName + File.separator + INTEGRATION_SERVER_NAME + File.separator + "_uninst";
                File sourceDirectory = new File(sourceDirectoryName);
                if(sourceDirectory.exists())
                {
                    String destDirectoryName = installDir + File.separator + "_uninst";
                    File destDirectory = new File(destDirectoryName);
                    if (copyDir(sourceDirectoryName,destDirectoryName))
                    {
                        log("Restore of uninstall directory");
                     }
                     else
                     {
                        log("Uninstall directory not restored");
                     }
                }
            }
            // This is a temp to remove everything irrespective of packages restored
            deleteDir(backupDirectoryName);
        }

        return true;
    }

   /**
   * Restores local product
   *@param backupName Name of backup directory <server>/IntegrationServer/replicate/salvage/backupAdapter
   *@param installDir Name of install directory <server>/IntegrationServer
   *@param deleteBms Build modules to be deleted
   *@param docBackup Flag set to true if documentation backup only required.
   *@return true if restoration completed
   */
    public static boolean restoreLocalProduct(String backupName, String installDir, String [] deleteBms, String [] deleteDirectories, boolean docBackup)
    {
        String backupDirectoryName = installDir + File.separator + "replicate"+ File.separator + "salvage"+ File.separator + backupName;
        File backupDirectory = new File(backupDirectoryName);
        if(backupDirectory.isDirectory())
        {
			deleteFileStarts(installDir + File.separator + ".." + File.separator + "install" + File.separator + "bms", deleteBms);
			for (int i = 0; i < deleteDirectories.length; i++)
			{
				File directory = new File(installDir + File.separator + "packages" + File.separator + deleteDirectories[i]);
				if (directory.isDirectory())
				{
    				File docDirectory = new File(installDir + File.separator + "packages" + File.separator +deleteDirectories[i] + File.separator + "doc");
    				if (docBackup)
    				{
					      deleteDir(docDirectory.getAbsolutePath());
					}
					else
    				{
						if (docDirectory.isDirectory())
						{
							File packageDirectory = new File(installDir + File.separator + "packages" + File.separator +deleteDirectories[i]);
							File tempDirectory = new File(installDir + File.separator + "packages" + File.separator + "temp");
							if (tempDirectory.isDirectory())
								deleteDir(tempDirectory.getAbsolutePath());
							if (createDir(tempDirectory.getAbsolutePath()))
							{
								copyDir(docDirectory.getAbsolutePath(), tempDirectory.getAbsolutePath());
								deleteDir(directory.getAbsolutePath());
								if (!packageDirectory.isDirectory())
									packageDirectory.mkdir();
								copyDir(tempDirectory.getAbsolutePath(), docDirectory.getAbsolutePath());
								deleteDir(tempDirectory.getAbsolutePath());
							}
						}
						else
							deleteDir(directory.getAbsolutePath());
					}
				}
			}

			String rootDirectoryName = (new File(installDir + File.separator + "..")).getAbsolutePath();
			log ("Restore of " + backupDirectory.getAbsolutePath() + " to " + rootDirectoryName);
            if (copyDir(backupDirectory.getAbsolutePath(), rootDirectoryName))
            {
                log("Restore of " + backupName);
                if (deleteDir(backupDirectory.getAbsolutePath()))
                {
					log("Deletion of " + backupName);
				}
				else
				{
					log(backupName + " not deleted");
				}
                return true;
             }
             else
             {
                log(backupName + " not restored");
			}
        }
        else
        {
            log(backupDirectoryName + " not found");
        }
        return false;
    }

	/**
   * Displays warning either as JOptionPane or as string
   *@param parent Component parent if GUI
   *@param message Warning message
   *@param title Warning title
   *@param isConsoleInstall Flag set to true if console install
   *@param isSilentInstall Flag set to true if silent install
   */
	public static void showWarning( Component parent, String message, String title,
                      boolean isConsoleInstall, boolean isSilentInstall ) {
		if ( !isConsoleInstall && !isSilentInstall )
		{
			DMTextArea text = new DMTextArea( message ); 
              text.setEditable(false);
              text.setBackground(parent.getBackground() );
              text.setForceTrimLongStrings(true);

			DMOptionPane.showMessageDialog( parent, text, title, DMOptionPane.WARNING_MESSAGE );
			/*
			JTextArea msgArea = new JTextArea(message);
			msgArea.setLineWrap(true);
			msgArea.setWrapStyleWord(true);
			msgArea.setEditable(false);
			msgArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
            msgArea.setColumns(40 );
            msgArea.setBackground(parent.getBackground() );
            msgArea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            JScrollPane sp=new JScrollPane(msgArea);
            sp.setPreferredSize(new Dimension(320,240));
            DMOptionPane.showMessageDialog( parent, sp, title, DMOptionPane.WARNING_MESSAGE );
            */
		}

		if (isConsoleInstall)
		{
			System.out.print(title + " ** ");
			System.out.println(message);
			log(message);
		}
	}

    /**
   * Performs yes/no choice as JOptionPane or as string
   *@param parent Component parent if GUI
   *@param message Message
   *@param title Title
   *@param isConsoleInstall Flag set to true if console install
   *@param isSilentInstall Flag set to true if silent install
   *@returns AdapterUtils.NO or AdapterUtils.YES as integer
   ***/
	public static int showYesNoChoice( Component parent, String message, String title,
                                       boolean isConsoleInstall, boolean isSilentInstall ) {
		int returnVal = NO;

		if ( !isConsoleInstall && !isSilentInstall ) {
			DMTextArea text = new DMTextArea( message ); 
              text.setEditable(false);
              text.setBackground(parent.getBackground() );
              text.setForceTrimLongStrings(true);
			returnVal = DMOptionPane.showConfirmDialog( parent, text, title, DMOptionPane.YES_NO_OPTION );

            if ( returnVal == DMOptionPane.YES_OPTION ) {
				returnVal = YES;
			}
			else {
				returnVal = NO;
			}
		}
		else if ( !isSilentInstall ) {
			returnVal = Console.YesNoQuery( message, Console.YES );

            if ( returnVal == Console.YES ) {
				returnVal = YES;
			}
			else {
				returnVal = NO;
			}
		}
			return returnVal;
	}


  /**
   * Checks all directories in a package recursively to see if it has any files modified outside a window
   * based on and upper and lower bound - used by checkPackageModified()
   *@param directoryName Directory to be checked
   *@param upperBound Upper bound of check window
   *@param lowerBound Lower bound of check window
   *@return true if modification has been done
   */
    private static void checkDirectoryModified(String directoryName, long upperBound, long lowerBound)
    {
        File dirFile = new File(directoryName);
        String temp = null;
        File dir = new File(directoryName);
        String fileName [] = dir.list();
        if (fileName.length == 0)
        {
        }
        else
        {
            for (int i=0; i < fileName.length; i++ )
            {
                File file = new File(dir, fileName[i]);
                if (file.isDirectory())
                {
                    checkDirectoryModified(directoryName + File.separator + fileName[i], upperBound, lowerBound);
                }
                else
                {
                    if (!fileName[i].endsWith("uninstallObject"))
                    {
                        temp = directoryName + File.separator + fileName[i];
                        long modifiedTime = (new File(temp)).lastModified();
                        if ((modifiedTime < lowerBound) || (modifiedTime > upperBound))
                        {
                          modifiedFlag = true;
                          break;
                        }
                    }
                }
            }
        }
    }

    /**
   * Create Windows Registry entry - used by createWindowsRegistryEntry()
   *@param registryHelper Instance of IRegistryHelper
   *@param keyName Name of key to be created under HKEY_LOCAL_MACHINE
   *@param names Names in key
   *@param values Values corresponding to names in key
   *@returns true if key correctly created
   */
     private static boolean createRegistryEntry(IRegistryHelper registryHelper, String keyName, String [] names, String [] values)
     {
       try
       {
           if (registryHelper.existsKey(IRegistryHelper.HKEY_LOCAL_MACHINE, keyName))
           {
                log("Key " + keyName + " already exists");
           }
           else
           {
               registryHelper.createKey(IRegistryHelper.HKEY_LOCAL_MACHINE, keyName);
               for (int i=0; i < names.length; i++)
               {
                   registryHelper.setStringValue(IRegistryHelper.HKEY_LOCAL_MACHINE, keyName, names[i], values[i]);
               }
               return true;
            }
            return false;
        }
        catch (RegistryHelperException ex)
        {
            handleException(ex);
            return false;
        }
     }
  /**
   * Deletes files in directory having prefix - used by deleteLocalProduct() and restoreLocalProduct()
   *@param directoryName Name of directory to be searched
   *@param filePrefixes Prefixes of files removed
   *@return true if deletion completed
   */
    private static void deleteFileStarts(String directoryName, String filePrefixes [])
    {
        File directory=new File(directoryName);
        String list[] = directory.list();
        for (int i=0; i < list.length; i++)
        {
            for (int j=0; j < filePrefixes.length; j++)
            {
                if (list[i].startsWith(filePrefixes[j]))
                {
                    deleteFile(directoryName + File.separator + list[i]);
                    log("deleted " + directoryName + File.separator + list[i]);
                }
            }
        }
    }
   /**
   * Remove Windows Registry entry.
   *@param registryHelper Instance of IRegistryHelper
   *@param keyName name of key to be deleted under HKEY_LOCAL_MACHINE
   *@param names  Names in key
   *@returns true if key correctly removed
   */

    private static boolean removeRegistryEntry(IRegistryHelper registryHelper, String keyName, String [] names)
    {
       try
       {
           for (int i=0; i < names.length; i++)
           {
               registryHelper.deleteValue(IRegistryHelper.HKEY_LOCAL_MACHINE, keyName, names[i]);
           }
           registryHelper.deleteKey(IRegistryHelper.HKEY_LOCAL_MACHINE, keyName);
           return true;
        }
        catch (RegistryHelperException ex)
        {
            handleException(ex);
            return false;
        }
     }

        /**
     * Runs synchronously the script file supplied.  Returns a String array  - the standard output and the standard error output.
     * @param fileName The String full hierarchic name of the file to be run.
     * @return String array - first element standard output, second element error output.
     * @throws java.utils.IOException  IOException
     */
    public static String [] runFile (String fileName) throws IOException
    {
        int retValue = 0;
        String returnStrings [] = new String [2];
        Process  process = Runtime.getRuntime().exec(fileName);
        InputStream errorStream = process.getErrorStream();
        StringBuffer errorString = new StringBuffer();
        InputStream inputStream = process.getInputStream();
        StringBuffer outputString = new StringBuffer();
        boolean finished= false;
        while (!finished)
        {
            try
            {
                while (inputStream.available()>0)
                {
                    Character c = new Character((char)inputStream.read());
                    outputString.append(c);
                }
                while (errorStream.available()>0)
                {
                    Character c = new Character((char)errorStream.read());
                    errorString.append(c);
                }
                retValue = process.exitValue();
                finished=true;
            }
            catch (IllegalThreadStateException e)
            {
                    try
                    {
                        Thread.currentThread().sleep(500);
                    }
                    catch (InterruptedException ex)
                    {
                    }
            }
        }
        /*
        try
        {
            process.waitFor();
        }
        catch (InterruptedException ex)
        {
        }
        */
        try
        {
            Thread.currentThread().sleep(500);
        }
        catch (InterruptedException ex)
        {
        }
        inputStream.close();
        errorStream.close();
        returnStrings [0] = new String(outputString);
        returnStrings [1] = new String(errorString);
        return returnStrings;
    }

    /**
    * Set full permissions on UNIX file.
    * @param fileName The file as a String.
    * @param installerAccess Installer access instance.
    */
    public static void setFilePermission(String fileName, IFileHelper fileHelper) {
        try {
            fileHelper.chmod(FULL_PERMISSIONS);
        } catch ( Exception ex ) {
            log("Exception in setFilePermissions() for file " + fileName );
            handleException(ex);
        }
    }

    /**
    * Sets a parameter in the uninstall object
    * @param key The key to a the  object in the uninstall object.
    * @param value The value of the object in the uninstall object.
    * @param uninstallObjectFileName The name of uninstall object file
    * @ returns true if parameter set in uninstall object
    */
    public static boolean setUninstallParameter(Object key, Object value, String uninstallObjectFileName)
    {
        Hashtable hashtable = new Hashtable();
        if (!fileExists(uninstallObjectFileName))
        {
            String parentDirectoryName = (new File(uninstallObjectFileName)).getParent();
            if(!(new File(parentDirectoryName)).isDirectory())
                createDir(parentDirectoryName);
            Date now = new Date();
            DateFormat dateFormat = new SimpleDateFormat( "EEE MMM dd HH:mm:ss zzz yyyy" );
            String dateString = dateFormat.format( now );
            hashtable.put("timestamp", dateString);
            try
            {
                FileOutputStream fos = new FileOutputStream(uninstallObjectFileName);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(hashtable);
                oos.flush();
            }
            catch (Exception ex)
            {
                handleException(ex);
                return false;
            }

        }
        if(fileExists(uninstallObjectFileName))
        {
            try
            {
                FileInputStream fis = new FileInputStream(uninstallObjectFileName);
                ObjectInputStream ois = new ObjectInputStream(fis);
                hashtable = (Hashtable) ois.readObject();
            }
            catch (Exception ex)
            {
                handleException(ex);
                return false;
            }

            if (value instanceof String [])
            {
                String x [] = (String []) value;
                ArrayList arrayList = new ArrayList();
                for (int i=0; i < x.length; i++)
                {
                    arrayList.add(x[i]);
                }
                hashtable.put(key, arrayList);
            }
            else
                hashtable.put(key, value);

            try
            {
                FileOutputStream fos = new FileOutputStream(uninstallObjectFileName);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(hashtable);
                oos.flush();
            }
            catch (Exception ex)
            {
                handleException(ex);
                return false;
            }
            return true;
        }
        return false;
    }


     /**
     * Handle an exception gracefully.
     * @param exception The exception to handle.
     */
    private static void handleException( Exception exception ) {
        log( "<exception class=\""
                  + "AdapterUtils\" message=\"" + exception.getLocalizedMessage() + "\">" );
        DebugLog.printStackTrace( exception );
        log( "</exception>" );
    }

     /**
     * Log string.
     * @param string the string to log.
    */
    private static void log( String string ) {
        DebugLog.println(string );
    }

     /**
   * Appends a value onto value of a tag in the manifest file
   *@param manifestFileName Name of manifest file
   *@param tagName Name of tag prefixed by <value name="
   *@param appendValue Value to be appended
   */
    public static void setManifestTagValue (String manifestFileName, String tagName, String appendValue)
    {
        String searchString = "<value name=\"" + tagName + "\">";
        int searchOffset = searchString.length();
    //    String tagValue="None";
        String tempManifestFileName = manifestFileName + "temp";
        if (fileExists(tempManifestFileName))
             {
                deleteFile(tempManifestFileName);
            }
        try
        {
             FileReader fr = new FileReader(manifestFileName);
             FileWriter fw = new FileWriter(manifestFileName+"temp");
             BufferedReader br = new BufferedReader(fr);
             BufferedWriter bw = new BufferedWriter(fw);
             String str = br.readLine();
             while (str != null)
             {
                str=str.trim();
                int k = str.indexOf(searchString);
                if (k > -1)
                {
                    int i = k + searchOffset;
                    int j = str.indexOf("<", i+1);
                    if ((i > -1) && (j > i))
                    {
                        // tagValue = str.substring(i, j);
                        int leng = str.length();
                        str = str.substring(0, j) + appendValue + str.substring(j, leng);
                    }
                }
                bw.write(str);
                bw.newLine();
                str = br.readLine();
             }
             br.close();
             bw.close();
             fw.close();
             fr.close();
             if (deleteFile(manifestFileName))
             {
                copyFile(tempManifestFileName, manifestFileName);
                deleteFile(tempManifestFileName);
            }
          }
          catch (IOException ex)
          {
            handleException(ex);
          }

   }
}
