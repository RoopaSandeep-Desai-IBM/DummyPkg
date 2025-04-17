
           webMethods WebSphere MQ Adapter 3.0

                          November 2002

Copyright (c) 2001-2002, webMethods, Inc.  All Rights Reserved.
_________________________________________________________________

Welcome to the webMethods WebSphere MQ Adapter 3.0, the
webMethods solution for IBM WebSphere MQ integration. This
release of the adapter is a revision of the product formerly
called the "webMethods MQSeries Adapter."  The adapter name has
been changed to remain consistent with IBM's product branding.

This file provides important information for using the webMethods
WebSphere MQ Adapter 3.0.

You use the webMethods WebSphere MQ Adapter with the webMethods
Integration Server and webMethods Developer.  For information
about webMethods Integration Server and webMethods Developer, see
the Readme files and user documentation in the directory in which
you installed the software.  Install the webMethods Integration
Server before installing the WebSphere MQ Adapter.



Contents:

1.0 Getting Started
2.0 Documentation
3.0 Known Problems and Limitations
4.0 Usage Notes
5.0 Globalization
    5.1 Encoding Configuration
6.0 Deprecated Components
7.0 Fixes and Enhancements
    7.1 Fixes in webMethods MQSeries Adapter 2.1
8.0 Copyright Information
9.0 Contacting Us



1.0 Getting Started

The webMethods WebSphere MQ Adapter Installation Guide provides
software requirements and installation instructions for the
WebSphere MQ Adapter.

The webMethods WebSphere MQ Adapter User's Guide provides
information for using the WebSphere MQ Adapter.

See Section 2.0, "Documentation" for information about locating
and viewing the documentation.



2.0 Documentation

Documentation for WebSphere MQ Adapter is located in the
<ServerDirectory>\packages\MQSeries\doc directory where you
installed the server. The following manuals are provided as PDF
files:

- webMethods WebSphere MQ Adapter Installation Guide
  Version 3.0

- webMethods WebSphere MQ Adapter User's Guide Version 3.0

To view the documentation supplied with this version of
webSphere MQ Adapter you must have Adobe Acrobat Reader 4.0 or
later. You can download and install Acrobat Reader 4.0 from:

   http://www.adobe.com/support/downloads/main.html

Help is also available in the WebSphere MQ Help Link at the top
right corner of the screen on each related Server Administrator
UI page.



3.0 Known Problems and Limitations

When you select a Queue Manager Alias, all of the Queue Manager
Alias' message handlers are displayed instead of only the
selected Queue Manager Alias' message handlers.



4.0 Usage Notes

None.



5.0 Globalization

The WebSphere MQ Adapter conforms to the international standards
of webMethods and includes support for operation in any country,
locale, or language.  Support for character encodings and proper
formatting, display, and validation of data (such as number and
date formats) has been provided.  The MQSeries Adapter is fully
compliant with the requirements of the Unicode Standard, version
2.1.8 (see http://www.unicode.org).

If you expect to display non-English data in your webMethods
tools, you may need to ensure that an appropriate font is
installed in your operating environment and modify your JRE
font.properties file to reference that font.  You should first
test each tool before attempting to modify this file.  In most
cases you should not need to modify font.properties file.
However, if you see hollow boxes or black squares where you would
expect to see non-English data, then changing or adding fonts is
indicated.  Information about modifying your font.properties file
can be found on the Javasoft website and in the FAQ at
http://www.unicode.org/help/display_problems.html.


5.1 Encoding Configuration

WebSphere MQ encodes data using IBM CCSIDs (sometimes called
"character encodings" or "code pages").  The WebSphere MQ Adapter
requires you to configure this external character encoding in
order to convert data between webMethods internal Unicode format
and MQ's encoding.  The CCSID you choose will control how data
is encoded in MQ messages; characters outside that CCSID will be
lost when passed to WebSphere MQ.

Consult your WebSphere MQ documentation and WebSphere MQ
administrator for more information about which CCSID(s) to use.
WebSphere MQ Adapter support for CCSIDs is documented in the
adapter's User's Guide. 



6.0 Deprecated Components

None.



7.0 Fixes and Enhancements


7.1 Fixes in webMethods MQSeries Adapter 2.1

The webMethods WebSphere MQ Adapter 3.0 includes fixes for the
following problems found in the webMethods MQSeries Adapter 2.1:

BMQA_20_FIX1
A com.wm.app.b2b.client.ServiceException is received if the GD
service is invoked from an MQSeries Listener.

BMQA_20_FIX2
MQ listener does not reconnect if the Queue Manager is restarted.

BMQA_20_FIX3
MQ listener does not reconnect if the Queue Manager is restarted,
even with BMQA_20_FIX2 installed.
    


8.0 Copyright Information

webMethods Administrator, webMethods Broker, webMethods
Developer, webMethods Installer, webMethods Integration Server,
webMethods Mainframe, webMethods Manager, webMethods Modeler,
webMethods Monitor, webMethods Workflow, webMethods Trading
Networks, and the webMethods logo are trademarks of webMethods,
Inc. "webMethods" is a registered trademark of webMethods, Inc.

All other marks are the property of their respective owners.

Copyright (c) 2001-2002 by webMethods, Inc. All rights reserved,
including the right of reproduction in whole or in part in any
form.



9.0 Contacting Us

You can reach webMethods Technical Services for support via:
  
  Advantage: http://advantage.webMethods.com
  E-mail: support@webMethods.com
  US Phone: 1-888-222-8215 
  Europe/EMEA Phone: +800-963-84-637 or +31-356462770
  Asia/Pacific Phone: +612-8913-1198 or +656-389-3222