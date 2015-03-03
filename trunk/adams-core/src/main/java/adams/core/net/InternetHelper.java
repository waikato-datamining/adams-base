/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * InetAddressHelper.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.net;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

/**
 * Helper class for internet related stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InternetHelper {

  /** IP (from network intf) for faster access. */
  protected static String m_IPNetworkInterface;

  /** host name (from network intf) for faster access. */
  protected static String m_HostnameNetworkInterface;

  /** IP for faster access. */
  protected static String m_IP;

  /** host name for faster access. */
  protected static String m_Hostname;

  /**
   * Returns the IP address determined from the network interfaces (using
   * the IP address of the one with a proper host name).
   *
   * @return 		the IP address
   */
  public static synchronized String getIPFromNetworkInterface() {
    String				result;
    List<String>			list;
    Enumeration<NetworkInterface>	enmI;
    NetworkInterface			intf;
    Enumeration<InetAddress>		enmA;
    InetAddress				addr;
    boolean				found;

    result = null;

    if (m_IPNetworkInterface == null) {
      list   = new ArrayList<String>();
      found  = false;
      try {
	enmI = NetworkInterface.getNetworkInterfaces();
	while (enmI.hasMoreElements()) {
	  intf = enmI.nextElement();
	  enmA = intf.getInetAddresses();
	  while (enmA.hasMoreElements()) {
	    addr = enmA.nextElement();
	    list.add(addr.getHostAddress());
	    if (addr.getHostName().indexOf(':') == -1) {
	      result = addr.getHostAddress();
	      found  = true;
	      break;
	    }
	  }
	  if (found)
	    break;
	}
      }
      catch (Exception e) {
	// ignored
      }

      if (result == null) {
	if (list.size() > 0)
	  result = list.get(0);
	else
	  result = "<unknown>";
      }

      m_IPNetworkInterface = result;
    }
    else {
      result = m_IPNetworkInterface;
    }

    return result;
  }

  /**
   * Returns the host name determined from the network interfaces.
   *
   * @return 		the host name, null if none found
   */
  public static synchronized String getHostnameFromNetworkInterface() {
    String				result;
    List<String>			list;
    Enumeration<NetworkInterface>	enmI;
    NetworkInterface			intf;
    Enumeration<InetAddress>		enmA;
    InetAddress				addr;
    boolean				found;

    result = null;

    if (m_HostnameNetworkInterface == null) {
      list   = new ArrayList<String>();
      found  = false;
      try {
	enmI = NetworkInterface.getNetworkInterfaces();
	while (enmI.hasMoreElements()) {
	  intf = enmI.nextElement();
	  enmA = intf.getInetAddresses();
	  while (enmA.hasMoreElements()) {
	    addr = enmA.nextElement();
	    list.add(addr.getHostName());
	    if (addr.getHostName().indexOf(':') == -1) {
	      result = addr.getHostName();
	      found  = true;
	      break;
	    }
	  }
	  if (found)
	    break;
	}
      }
      catch (Exception e) {
	// ignored
      }

      if (result == null) {
	if (list.size() > 0)
	  result = list.get(0);
	else
	  result = "<unknown>";
      }

      m_HostnameNetworkInterface = result;
    }
    else {
      result = m_HostnameNetworkInterface;
    }

    return result;
  }

  /**
   * Returns the IP address of the local host as string.
   *
   * @return		the IP address, null if not available
   */
  public static synchronized String getLocalHostIP() {
    String	result;

    if (m_IP == null) {
      try {
	result = InetAddress.getLocalHost().getHostAddress();
      }
      catch (Exception e) {
	result = getIPFromNetworkInterface();
      }

      m_IP = result;
    }
    else {
      result = m_IP;
    }

    return result;
  }

  /**
   * Returns the machine name of the local host as string.
   *
   * @return		the machine name, null if not available
   */
  public static synchronized String getLocalHostName() {
    String	result;

    if (m_Hostname == null) {
      try {
	result = InetAddress.getLocalHost().getHostName();
      }
      catch (Exception e) {
	result = getHostnameFromNetworkInterface();
      }

      m_Hostname = result;
    }
    else {
      result = m_Hostname;
    }

    return result;
  }

  /**
   * Turns the base64 encoded string into plain text.
   *
   * @param base64	the encoded string
   * @return		the decoded string, null in case of an error
   */
  public static String decodeBase64(String base64) {
    String	result;

    try {
      result = new String(Base64.decodeBase64(base64.getBytes()));
    }
    catch (Exception e) {
      System.err.println("Failed to decode base64 string: " + base64);
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Encodes the given string in base64.
   *
   * @param raw		the string to encode
   * @return		the encoded string
   */
  public static String encodeBase64(String raw) {
    return Base64.encodeBase64String(raw.getBytes());
  }

  /**
   * Encodes the given bytes in base64.
   *
   * @param raw		the bytes to encode
   * @return		the encoded string
   */
  public static String encodeBase64(byte[] raw) {
    return Base64.encodeBase64String(raw);
  }
  
  /**
   * Turns a URL into a valid filename.
   * Converts the following characters:
   * <pre>
   * : -> (c)
   * / -> (s)
   * & -> (a)
   * ? -> (q)
   * </pre>
   * 
   * @param url		the URL to convert
   * @return		the valid filename
   */
  public static String encodeUrlAsFilename(String url) {
    String	result;
    
    result = url;
    result = result.replace(":", "(c)");
    result = result.replace("/", "(s)");
    result = result.replace("&", "(a)");
    result = result.replace("?", "(q)");
    
    return result;
  }
  
  /**
   * Converts a URL encoded in a filename back into a URL.
   * <pre>
   * (c) -> :
   * (s) -> /
   * (a) -> &
   * (q) -> ?
   * </pre>
   * 
   * @param filename	the filename to convert
   * @return		the URL
   */
  public static String decodeUrlFromFilename(String filename) {
    String	result;
    
    result = filename;
    result = result.replace("(c)", ":");
    result = result.replace("(s)", "/");
    result = result.replace("(a)", "&");
    result = result.replace("(q)", "?");
    
    return result;
  }
  
  /**
   * Extracts the URL stored in the full filename provided. If no occurrences
   * of "((" and "))" in the string, the complete string is interpreted as 
   * URL. Otherwise, the substring surrounded by "((" and "))" is extracted
   * first. The URL is then decoded into a proper URL.
   * 
   * @param filename	the filename to extract the URL from
   * @return		the extracted/decoded URL
   * @see		#decodeUrlFromFilename(String)
   */
  public static String extractUrlFromFilename(String filename) {
    String	result;
    int		start;
    int		end;
    
    start = filename.indexOf("((");
    end   = filename.lastIndexOf("))");
    if ((start > -1) && (end > start))
      result = decodeUrlFromFilename(filename.substring(start + 2, end));
    else
      result = decodeUrlFromFilename(filename);
    
    return result;
  }
  
  /**
   * Constructs a filename, ensuring that the URL is encoded properly.
   * 
   * @param path	the path to use, ignored if null (separator gets added automatically)
   * @param prefix	the prefix for the name, ignored if null
   * @param url		the URL to encode
   * @param suffix	the suffix for the name, ignored if null
   * @param ext		the file extension, ignored if null (dot gets automatically)
   */
  public static String createUrlFilename(String path, String prefix, String url, String suffix, String ext) {
    StringBuilder	result;
    
    result = new StringBuilder();
    if (path != null) {
      result.append(path);
      if (!path.endsWith(File.separator))
	result.append(File.separator);
    }
    if (prefix != null)
      result.append(prefix);
    result.append("((" + encodeUrlAsFilename(url) + "))");
    if (suffix != null)
      result.append(suffix);
    if (ext != null) {
      if (!ext.startsWith("."))
	result.append(".");
      result.append(ext);
    }
    
    return result.toString();
  }
}
