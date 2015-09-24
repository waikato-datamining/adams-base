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
 * BaseHostname.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core.base;

/**
 * Wrapper for internet hostnames.
 * See also:
 * <a href="https://en.wikipedia.org/wiki/Hostname#Restrictions_on_valid_host_names" target="_blank">WikiPedia</a>
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseHostname
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -1171165120084607705L;

  /** the valid characters. */
  public final static String VALID_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789-.:";

  /**
   * Initializes the string with localhost and no port.
   */
  public BaseHostname() {
    super("localhost");
  }

  /**
   * Initializes the object with the hostname to parse.
   *
   * @param s		the string to parse
   */
  public BaseHostname(String s) {
    super(s);
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    boolean	result;
    int		i;
    String[]	parts;
    int		port;

    value  = value.toLowerCase();
    result = (value.length() > 0);

    if (result) {
      // only valid chars?
      for (i = 0; i < value.length(); i++) {
	if (VALID_CHARS.indexOf(value.charAt(i)) == -1) {
	  result = false;
	  break;
	}
      }
      // check optional port
      if (result) {
	if (value.indexOf(':') > -1) {
	  parts  = value.split(":");
	  result = (parts.length == 2);
	  if (result) {
	    try {
	      port   = Integer.parseInt(parts[1]);
	      result = (port >= 0) && (port <= 65535);
	    }
	    catch (Exception e) {
	      result = false;
	    }
	  }
	}
      }
    }

    return result;
  }

  /**
   * Returns the hostname part without the port.
   *
   * @return		the hostname
   */
  public String hostnameValue() {
    if (!getValue().contains(":"))
      return getValue();
    else
      return getValue().split(":")[0];
  }

  /**
   * Returns the port part of the hostname, if available.
   *
   * @return		the port, -1 if none specified
   */
  public int portValue() {
    return portValue(-1);
  }

  /**
   * Returns the port part of the hostname, if available.
   *
   * @param defPort	the default port to use
   * @return		the port, defPort if none specified
   */
  public int portValue(int defPort) {
    if (!getValue().contains(":"))
      return defPort;
    else
      return Integer.parseInt(getValue().split(":")[1]);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Hostname with optional port number (format: 'host:port')";
  }
}
