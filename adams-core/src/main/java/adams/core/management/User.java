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

/*
 * User.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.management;

import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;

import java.util.Arrays;

/**
 * Class for returning information about the current user.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class User {

  /** environment variable for overriding the user name (user.name). */
  public static final String ADAMS_USERNAME = "ADAMS_USERNAME";

  /** environment variable for overriding the user home dir (user.home). */
  public static final String ADAMS_USERHOME = "ADAMS_USERHOME";

  /** environment variable for overriding the current working dir (user.dir). */
  public static final String ADAMS_USERDIR = "ADAMS_USERDIR";

  /** the user name. */
  protected static String m_UserName;

  /** the user home. */
  protected static String m_UserHome;

  /** the user dir (CWD). */
  protected static String m_UserDir;

  /** the user ID. */
  protected static Integer m_UserID;

  /** the user's group ID. */
  protected static Integer m_GroupID;

  /**
   * Returns the user name.
   *
   * Can be overridden with {@link #ADAMS_USERNAME} environment variable.
   *
   * @return		the name
   * @see		#ADAMS_USERHOME
   */
  public static String getName() {
    String	result;

    if (m_UserName != null)
      return m_UserName;

    result = null;

    try {
      result = System.getenv(ADAMS_USERNAME);
      if ((result != null) && result.isEmpty())
        result = null;
    }
    catch (Exception e) {
      // ignored
    }

    if (result == null)
      result = System.getProperty("user.name");
    else
      System.out.println("Using " + ADAMS_USERNAME + "=" + result);

    m_UserName = result;

    return result;
  }

  /**
   * Returns the user's home directory.
   *
   * Can be overridden with {@link #ADAMS_USERHOME} environment variable.
   *
   * @return		the directory
   * @see		#ADAMS_USERHOME
   */
  public static String getHomeDir() {
    String	result;

    if (m_UserHome != null)
      return m_UserHome;

    result = null;

    try {
      result = System.getenv(ADAMS_USERHOME);
      if ((result != null) && result.isEmpty())
	result = null;
    }
    catch (Exception e) {
      // ignored
    }

    if (result == null)
      result = System.getProperty("user.home");
    else
      System.out.println("Using " + ADAMS_USERHOME + "=" + result);

    m_UserHome = result;

    return result;
  }

  /**
   * Returns the user's current working directory.
   *
   * Can be overridden with {@link #ADAMS_USERDIR} environment variable.
   *
   * @return		the directory
   * @see		#ADAMS_USERDIR
   */
  public static String getCWD() {
    String	result;

    if (m_UserDir != null)
      return m_UserDir;

    result = null;

    try {
      result = System.getenv(ADAMS_USERDIR);
      if ((result != null) && result.isEmpty())
	result = null;
    }
    catch (Exception e) {
      // ignored
    }

    if (result == null)
      result = System.getProperty("user.dir");
    else
      System.out.println("Using " + ADAMS_USERDIR + "=" + result);

    m_UserDir = result;

    return result;
  }

  /**
   * Runs the specified command and returns the output from stdout.
   *
   * @param cmd		the command to run
   * @param defValue	the default output
   * @return		the generated output or default value if failed to execute
   */
  protected static String cmdOutput(String[] cmd, String defValue) {
    String			result;
    CollectingProcessOutput 	output;

    result = defValue;

    try {
      output = ProcessUtils.execute(new String[]{"id", "-u"});
      if (output.getExitCode() == 0)
	result = output.getStdOut().trim();
    }
    catch (Exception e) {
      System.err.println("Failed to execute: " + Arrays.asList(cmd));
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Returns the user's ID for *nix-like systems (id -u).
   *
   * @return		the user ID, -1 if not available
   */
  public static int getUserID() {
    int		result;
    String	output;

    result = -1;

    if (OS.isLinux() || OS.isMac() || OS.isAndroid()) {
      if (m_UserID == null) {
        output = cmdOutput(new String[]{"id", "-u"}, "-1");
        try {
	  m_UserID = Integer.parseInt(output);
	}
        catch (Exception e) {
	  m_UserID = -1;
	  System.err.println("Failed to parse user ID: " + output);
	  e.printStackTrace();
	}
      }

      result = m_UserID;
    }

    return result;
  }

  /**
   * Returns the user's group ID for *nix-like systems (id -g).
   *
   * @return		the group ID, -1 if not available
   */
  public static int getGroupID() {
    int		result;
    String	output;

    result = -1;

    if (OS.isLinux() || OS.isMac() || OS.isAndroid()) {
      if (m_GroupID == null) {
	output = cmdOutput(new String[]{"id", "-g"}, "-1");
	try {
	  m_GroupID = Integer.parseInt(output);
	}
	catch (Exception e) {
	  m_GroupID = -1;
	  System.err.println("Failed to parse user ID: " + output);
	  e.printStackTrace();
	}
      }

      result = m_GroupID;
    }

    return result;
  }
}
