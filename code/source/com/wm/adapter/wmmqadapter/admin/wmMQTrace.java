/*
 * Copyright (c) 1996-2003, webMethods Inc.  All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * webMethods, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with WebMethods.
 *
 * webMethods MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. webMethods SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 *
 */

package com.wm.adapter.wmmqadapter.admin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Hashtable;

import com.ibm.mq.MQEnvironment;
import com.wm.adapter.wmmqadapter.wmMQAdapter;
import com.wm.adapter.wmmqadapter.connection.wmMQException;
import com.wm.adk.log.ARTLogger;
import com.wm.util.Values;

public final class wmMQTrace
{

	// ---( internal utility methods )---
    final static wmMQTrace _instance = new wmMQTrace();
    static wmMQTrace _newInstance() {
        return new wmMQTrace();
    }

    static wmMQTrace _cast(Object o) {
        return (wmMQTrace) o;
    }

    // ---( server methods )---

   // private static Hashtable _qmgrs = new Hashtable();

    private static String _traceState = "disabled";
    private static int _traceLevel = 0;

    private static ByteArrayOutputStream _traceOutput = new ByteArrayOutputStream();

    /** Enable the MQSeries-level trace
     *
     */
    public final static Values enableMQTrace(Values in) throws wmMQException
    {
        log(ARTLogger.INFO, 1001, "enableMQTrace", "");
        Values out = in;
        // Trax 1-1WLW30 (1-1WSOCD)
        int traceLevel = in.getInt("tracelevel");
        MQEnvironment.enableTracing(traceLevel, _traceOutput);
        // Update status after the API call
        _traceState = "enabled";
        _traceLevel = traceLevel;
        //! Trax 1-1WLW30 (1-1WSOCD)
        String levelstring = "0" + new Integer(_traceLevel).toString();
        levelstring = levelstring.substring(levelstring.length() - 2);

        out.put("tracestate", _traceState);
        out.put("tracelevel", levelstring);
        out.put("message", "TRACE_ENABLED - level=" + levelstring);

        log(ARTLogger.INFO, 1002, "enableMQTrace", "");
        return out;
    }

    /** Disable the MQSeries-level trace
     *
     */
    public final static Values disableMQTrace(Values in) throws wmMQException
    {
        log(ARTLogger.INFO, 1001, "disableMQTrace", "");
        Values out = in;
        // Trax 1-1WLW30 (1-1WSOCD)
        MQEnvironment.disableTracing();
        // Update status after the API call        
        _traceState = "disabled";
        _traceLevel = 0;
        //! Trax 1-1WLW30 (1-1WSOCD)
        String levelstring = "0" + new Integer(_traceLevel).toString();
        levelstring = levelstring.substring(levelstring.length() - 2);

        out.put("tracestate", _traceState);
        out.put("tracelevel", levelstring);
        out.put("message", "TRACE_DISABLED");

        log(ARTLogger.INFO, 1002, "disableMQTrace", "");

        return out;
    }

    /** Disable the MQSeries-level trace
     *
     */
    public final static Values setTraceLevel(Values in ) throws wmMQException
    {
        log(ARTLogger.INFO, 1001, "setTraceLevel", "");
        Values out = in;

        _traceLevel = in.getInt("tracelevel");

        String levelstring = "0" + new Integer(_traceLevel).toString();
        levelstring = levelstring.substring(levelstring.length() - 2);

        out.put("tracestate", _traceState);
        out.put("tracelevel", levelstring);
        out.put("message", "TRACE_SETLEVEL - level=" + levelstring);

        log(ARTLogger.INFO, 1002, "setTraceLevel", "");
        return out;
    }

    /** Retrieve the MQSeries-level trace output
     *
     */
    public final static Values getTraceOutput(Values in ) throws wmMQException
    {
        log(ARTLogger.INFO, 1001, "getTraceOutput", "");
        Values out = in;

        String s;
        StringBuffer traceBuffer = new StringBuffer();

        try
        {
                ByteArrayInputStream instream = new ByteArrayInputStream(_traceOutput.toByteArray());
                InputStreamReader isr = new InputStreamReader(instream);
                BufferedReader reader = new BufferedReader(isr);

                while ((s = reader.readLine()) != null)
                {
                        traceBuffer.append(s).append("\n");
                }

                reader.close();
        }
        catch(Exception e)
        {
                throw new wmMQException("1055", e.toString() );
        }

        String trace = traceBuffer.toString();

        String levelstring = "0" + new Integer(_traceLevel).toString();
        levelstring = levelstring.substring(levelstring.length() - 2);

        out.put("tracestate", _traceState);
        out.put("tracelevel", levelstring);
        out.put("traceoutput", trace);

        log(ARTLogger.INFO, 1002, "getTraceOutput", "");

        return out;
    }

    /** Retrieve the MQSeries-level trace settings
     *  Kept for compatibility reasons - maybe
     */
    public final static Values getTraceSettings(Values in ) throws wmMQException
    {
        log(ARTLogger.INFO, 1001, "getTraceSettings", "");

        return getTraceOutput(in);
    }

    /** Clear the MQSeries-level trace file
     *
     */
    public final static Values clearTraceFile(Values in ) throws wmMQException
    {
        log(ARTLogger.INFO, 1001, "clearTraceFile", "");
        Values out = in;

        _traceOutput = new ByteArrayOutputStream();
        // Trax 1-1WLW30 (1-1WSOCD)
        // Restart tracing only if it was previously enabled
        MQEnvironment.disableTracing();
        
        if(_traceState.equals("enabled")) {
	        MQEnvironment.enableTracing(_traceLevel, _traceOutput);
        }
        //! Trax 1-1WLW30 (1-1WSOCD)
        String levelstring = "0" + new Integer(_traceLevel).toString();
        levelstring = levelstring.substring(levelstring.length() - 2);

        out.put("tracestate", _traceState);
        out.put("tracelevel", levelstring);
        out.put("message", "TRACE_CLEARED");

        log(ARTLogger.INFO, 1002, "clearTraceFile", "");

        return out;
    }

    /** Archive the MQSeries-level trace file
     *
     */
    public final static Values archiveTraceFile(Values in ) throws wmMQException
    {
        log(ARTLogger.INFO, 1001, "archiveTraceFile", "");
        Values out = in;

        String archiveFileName = in.getString("archiveFileName");
        int archiveOpStatus = -1;
        String s;
        if ((archiveFileName == null) || (archiveFileName.equals("")))
        	archiveFileName = "MQTraceArchive.txt";
        String installPath = System.getProperty("user.dir");
        //System.out.println(installPath);
       if ((archiveFileName.toLowerCase()).startsWith(installPath.toLowerCase())){
        try
        {
        	// create the archive file
            File archiveFile = new File(archiveFileName);

            // open trace file for archiving
            ByteArrayInputStream instream = new ByteArrayInputStream(_traceOutput.toByteArray());
            InputStreamReader isr = new InputStreamReader(instream);
            BufferedReader reader = new BufferedReader(isr);

            // open the destination file for writing
            PrintWriter output = new PrintWriter ( new BufferedWriter (new FileWriter(archiveFile) ) ); //NOSONAR

            while ((s = reader.readLine()) != null)
            {
                    output.println(s);
            }
            //close print writer for archive file
            output.close();
            // close buffered reader for source file
            reader.close();
            // set archive status to 1 indicates success
            archiveOpStatus = 1;
            //Now, clear the trace file
            _traceOutput = new ByteArrayOutputStream();
            // Trax 1-1WLW30 (1-1WSOCD)
            // Restart tracing only if it was previously enabled
	        MQEnvironment.disableTracing();
	        
            if(_traceState.equals("enabled")) {
    	        MQEnvironment.enableTracing(_traceLevel, _traceOutput);
            }
            //! Trax 1-1WLW30 (1-1WSOCD)
        }
        catch(IOException e)
		{
        	Object[] parms = new Object[] {e.getMessage()};
       		throw( new wmMQException("1055", e.toString() , parms));
		}

       } else{
    	   throw new wmMQException("1056", "Path should be relative to Integrationserver path" );
       }
       
       String levelstring = "0" + new Integer(_traceLevel).toString();
        levelstring = levelstring.substring(levelstring.length() - 2);

        out.put("tracestate", _traceState);
        out.put("tracelevel", levelstring);
        out.put("message", "TRACE_ARCHIVED - file=" + archiveFileName);
        
        log(ARTLogger.INFO, 1002, "archiveTraceFile", "");

        return out;
    }

	protected static void log(int level, int minor, String arg0, String arg1)
	{
		ARTLogger logger = ((wmMQAdapter)wmMQAdapter.getInstance()).getLogger();
		if (logger == null)
		{
			System.out.println("Logger is null");
			return;
		}
        
        //Trax 1-WVILA. Allow user to override the logging level of adapter messages. 
        if (wmMQAdapter.getLogLevelOverrides().containsKey("" + minor))
        	level =  Integer.parseInt((String)wmMQAdapter.getLogLevelOverrides().get("" + minor)) - ARTLogger.DEBUG;       	
       
		String[] args = new String[2];
		args[0] = arg0;
		args[1] = arg1;
        //Trax log 1-S4OHA - move adapter logging to DEBUG PLUS
        logger.logDebugPlus(level, minor, args);
	}
}
