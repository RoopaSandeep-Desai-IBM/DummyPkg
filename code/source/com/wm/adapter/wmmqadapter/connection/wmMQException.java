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

package com.wm.adapter.wmmqadapter.connection;

import com.wm.util.Values;
import com.wm.adapter.wmmqadapter.wmMQAdapterResourceBundle;

import java.util.Hashtable;

/**
 * <P>This is the root class of all wmMQ exceptions.
 *
 * <P>It provides following information:
 * <UL>
 *   <LI> A string describing the error - This string is
 *        the standard Java exception message, and is available via
 *        getMessage().
 *   <LI> A string error code
 *   <LI> A reference to another exception - Often a wmMQ exception will
 *        be the result of a lower level problem. If appropriate, this
 *        lower level exception can be linked to the this exception.
 * </UL>
 **/

public class wmMQException extends Exception
{

    /** error code  **/
    private String errorCode;

    /** Exception reference  **/
    private Exception linkedException;

    // The errorCode values used in defining the exception.
    public final static String NO_ERROR_CODE = "No Error Code";

    private String _serverMsg;
    private String _clientMsg;


    private static Hashtable _msgs = new Hashtable();

	/** Construct a wmMQException with reason and errorCode for exception
   *
   *  @param  reason        a description of the exception
   *  @param  errorCode     a string specifying the vendor specific
   *                        error code
   **/
	public wmMQException(String reason, String errorCode) {
		super(reason);
		this.errorCode = errorCode;
		linkedException = null;
		Object[] pArray = new Object[1];
		pArray[0] = errorCode;
		setMessages(reason, pArray);
	}

	/** Construct a wmMQException with reason and errorCode, and an array of paramters for exception
   *
   *  @param  reason        a description of the exception
   *  @param  errorCode     a string specifying the vendor specific
   *                        error code
   *  @param  parms			an array of objects
   **/
	public wmMQException(String reason, String errorCode, Object[] parms) {
		super(reason);
		this.errorCode = errorCode;
		linkedException = null;
		Object[] pArray = new Object[parms.length + 1];
		pArray[0] = errorCode;
		System.arraycopy(parms, 0, pArray, 1, parms.length);
		setMessages(reason, pArray);
	}

	/** Construct a wmMQException with reason and with error code defaulting
   *  to null
   *
   *  @param  reason        a description of the exception
   **/
	public wmMQException(String reason) {
		super(reason);
		this.errorCode = null;
		linkedException = null;
		Object[] pArray = null;
		setMessages(reason, pArray);
	}

	/** Get the error code
   *  @return   a string specifying error code
  **/
	public String getErrorCode() {
		return this.errorCode;
	}

   /* Set the error code
   *  @param   a string specifying error code
   */
//	private void setErrorCode(String errorCode)
//	{
//	  if(errorCode == null)
//		this.errorCode = NO_ERROR_CODE;
//	  else
//		this.errorCode = errorCode;
//	}

	/**
   * Get the exception linked to this one.
   *
   * @return the linked Exception, null if none
  **/
	public Exception getLinkedException() {
		return (linkedException);
	}

	/**
   * Add a linked Exception.
   *
   * @param ex       the linked Exception
  **/
	public synchronized void setLinkedException(Exception ex) {
		linkedException = ex;
	}


	public String getMessage()
	{
		StringBuffer emsg = new StringBuffer(_serverMsg);
		if (linkedException != null)
		{
			emsg.append(linkedException.getMessage());
		}
	  return(emsg.toString());
	}

	public String getLocalizedMessage()
	{
		StringBuffer emsg = new StringBuffer(_clientMsg);
		if (linkedException != null)
		{
			emsg.append(" (Linked Exception:");
			emsg.append(linkedException.getLocalizedMessage());
			emsg.append(")");
		}
	  return(emsg.toString());
	}

	private void setMessages(String msgID, Object[] pArray)
	{
	  setServerMessage(msgID, pArray);
	  setClientMessage(msgID, pArray);
	}

	private void setServerMessage(String msgID, Object[] pArray)
	{
	  if (pArray != null)
	  {
		// If we have a values object use $error for the message
		for(int i=0; i<pArray.length; i++)
		{
		  if (pArray[i] instanceof Values)
		  {
			pArray[i] = ((Values)pArray[i]).getString("$error");
		  }
		  else
		  {
			if (pArray[i] instanceof Throwable)
			{
              pArray[i] = ((Throwable)pArray[i]).getLocalizedMessage();
			}
          }
		}
      }

      // Set the String returned by getMessage() in the servers locale.
	  if(pArray != null)
		  _serverMsg = format(msgID, pArray);
	}

    private void setClientMessage(String msgID, Object[] pArray)
	{
//      IDMessageFormatter cFormatter = IDMessageFormatter.getIDClientFormatter(bundle);

      // If we have a values object use $localizedError, if present, for the message
	  if (pArray!= null)
      {
		for(int i=0; i<pArray.length; i++)
        {
		  if (pArray[i] instanceof Values)
          {
			if (((Values)pArray[i]).containsKey("$localizedError"))
            {
			  pArray[i] = ((Values)pArray[i]).getString("$localizedError");
            }
			else
            {
              pArray[i] = ((Values)pArray[i]).getString("$error");
			}
          }
          else
		  {
            if (pArray[i] instanceof Throwable)
            {
			  pArray[i] = ((Throwable)pArray[i]).getLocalizedMessage();
            }
          }
		}
      }

	  // Set the String returned by getLocalizedMessage() in the clients locale.
	  if(pArray != null)
		  _clientMsg = format(msgID, pArray);
    }

    public static String format(String msgID, Object[] pArray)
    {
        String msg2format = (String)_msgs.get(msgID);
        if (msg2format == null)
            return "Unknown Message Id - " + msgID;

        StringBuffer sb = new StringBuffer();

        int start = 0;
        int end = msg2format.length();

        while (start < end)
        {
            int indx = msg2format.indexOf("{", start);
            if (indx > -1)
            {
                //Assume no more than 10 substitutions - index is a single digit
                try
                {
                int arrayindx = Integer.parseInt(msg2format.substring(indx + 1, indx + 2));
                sb.append(msg2format.substring(start, indx));
                sb.append(pArray[arrayindx]);
				}
				catch (NumberFormatException nfe)
				{
					//Substitution could not be performed. Leave {n} in the message
					sb.append(msg2format.substring(indx, indx+3));
				}
				finally
				{
					start = indx + 3;
				}
            }
            else
            {
                sb.append(msg2format.substring(start, end));
                start = end;
            }
        }

        return sb.toString();
    }

    static
    {
        Object[][] bundlemsgs = ((wmMQAdapterResourceBundle)wmMQAdapterResourceBundle.getBundle("com.wm.adapter.wmmqadapter.wmMQAdapterResourceBundle")).getContents();
        for (int i = 0 ; i < bundlemsgs.length ; i++)
            _msgs.put(bundlemsgs[i][0], bundlemsgs[i][1]);
    }


}
