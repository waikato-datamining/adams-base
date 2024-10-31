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
 * Java.java
 * Copyright (C) 2010-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A helper class for Java (JRE/JDK) related things.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Java {

  /** the sub-directory with the binaries. */
  public final static String BIN_DIR = "bin";

  /** the java executable (no extension). */
  public final static String JAVA = "java";

  /** the javac executable (no extension). */
  public final static String JAVAC = "javac";

  /** the cleaned up, full classpath. */
  protected static String CLASSPATH_FULL;

  /** the shortened classpath. */
  protected static String CLASSPATH_SHORT;

  /** the major Java version. */
  protected static Integer MAJOR_VERSION;

  /**
   * Returns the major version of Java.
   *
   * @return	the major version, -1 if failed to determine
   */
  public static int getMajorVersion() {
    String 	versionStr;
    int 	versionInt;

    if (MAJOR_VERSION == null) {
      versionInt = -1;
      versionStr = System.getProperty("java.version");
      if (versionStr.startsWith("1."))
	versionStr = versionStr.substring(2);

      if (versionStr.contains(".")) {
	versionStr = versionStr.substring(0, versionStr.indexOf('.'));
	try {
	  versionInt = Integer.parseInt(versionStr);
	}
	catch (Exception e) {
	  System.err.println("Failed to determine major version in Java version string: " + System.getProperty("java.version"));
	}
      }
      else if (Utils.isInteger(versionStr)) {
	try {
	  versionInt = Integer.parseInt(versionStr);
	}
	catch (Exception e) {
	  System.err.println("Failed to determine major version in Java version string: " + System.getProperty("java.version"));
	}
      }

      MAJOR_VERSION = versionInt;
    }

    return MAJOR_VERSION;
  }

  /**
   * Returns the Java home directory of the current JVM.
   *
   * @return		the home directory
   */
  public static String getHome() {
    return System.getProperty("java.home");
  }

  /**
   * Checks whether Java home is pointing to a JRE instead of a JDK.
   * It is checking for the javac executable.
   *
   * @return		true if the JVM is just a JRE
   * @see		#isJRE(String)
   */
  public static boolean isJRE() {
    return isJRE(getHome());
  }

  /**
   * Checks whether directory is pointing to a JRE instead of a JDK.
   * It is checking for the javac executable.
   *
   * @param path	the path above the "bin" directory
   * @return		true if the JVM is just a JRE
   * @see		#JAVAC
   */
  public static boolean isJRE(String path) {
    String	file;

    file = path + File.separator + BIN_DIR + File.separator + FileUtils.fixExecutable(JAVAC);

    return !(new File(file).exists());
  }

  /**
   * Checks whether Java home is representing a JDK. If the directory is JRE,
   * it is checked whether it is just the JRE of a JDK.
   *
   * @return		true if the JVM is a JDK
   * @see		#isJDK(String)
   */
  public static boolean isJDK() {
    return isJDK(getHome());
  }

  /**
   * Checks whether directory is representing a JDK. If the directory is JRE,
   * it is checked whether it is just the JRE of a JDK.
   *
   * @param path	the path above the "bin" directory
   * @return		true if the JVM is a JDK
   * @see		#JAVAC
   */
  public static boolean isJDK(String path) {
    File	file;
    String	executable;

    file = new File(path);

    if (isJRE(path) && file.getParentFile().getName().matches("jdk.*"))
      file = file.getParentFile();

    executable = file.getAbsolutePath() + File.separator + BIN_DIR + File.separator + FileUtils.fixExecutable(JAVAC);

    return new File(executable).exists();
  }

  /**
   * Returns the "bin" directory of the Java home. In case of a JRE of a JDK,
   * this returns the JDK/bin directory and not the JRE/bin one.
   *
   * @return		the absolute path to "bin" (incl.)
   * @see		#getBinDir(String)
   */
  public static String getBinDir() {
    return getBinDir(getHome());
  }

  /**
   * Returns the "bin" directory. In case of a JRE of a JDK, this returns the
   * JDK/bin directory and not the JRE/bin one.
   *
   * @param 		path above the "bin"
   * @return		the absolute path to "bin" (incl.)
   * @see		#BIN_DIR
   */
  public static String getBinDir(String path) {
    String	result;
    File	file;

    file   = new File(path);
    result = file.getAbsolutePath() + File.separator + BIN_DIR;

    if (OS.isMac()) {
      if (!new File(result).exists())
	result = file.getParentFile().getAbsolutePath() + File.separator + BIN_DIR;
    }
    else {
      if (isJRE(path) && isJDK(path))
	result = file.getParentFile().getAbsolutePath() + File.separator + BIN_DIR;
    }

    return result;
  }

  /**
   * Returns the java executable.
   *
   * @return		the full path to the java executable
   * @see		#JAVA
   * @see		#getBinDir()
   */
  public static String getJavaExecutable() {
    String	result;

    result = getBinDir() + File.separator + FileUtils.fixExecutable(JAVA);

    return result;
  }

  /**
   * Executes the executable and returns the output.
   *
   * @param executable	the jvisualvm executable to use
   * @param options	additional options for jvisualvm
   * @return		the output
   */
  protected static String execute(String executable, String options) {
    CollectingProcessOutput proc;
    String		result;
    List<String>	cmd;

    try {
      // assemble command
      cmd = new ArrayList<>(Arrays.asList(OptionUtils.splitOptions(options)));
      cmd.add(0, executable);

      // execute command
      proc = ProcessUtils.execute(cmd.toArray(new String[0]));
      if (!proc.hasSucceeded())
	result = ProcessUtils.toErrorOutput(proc);
      else
	result = proc.getStdOut();
    }
    catch (Exception e) {
      result = e.toString();
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Returns the classpath of the current JVM. Does not check whether the
   * directories or jars actually exist, but removes "fake" jars that occur
   * on Mac OSX when unzipping a ZIP file containing jars (these files start
   * with "._").
   *
   * @param shorten 	if true then the paths get removed from the jars
   * @return 		the classpath
   */
  public static synchronized String getClassPath(boolean shorten) {
    String 		result;
    StringBuilder	shortCP;
    StringBuilder	fullCP;
    String[] 		parts;
    String 		sep;
    int 		i;
    File		entry;
    boolean		isJar;

    if (CLASSPATH_FULL == null) {
      sep     = System.getProperty("path.separator");
      parts   = System.getProperty("java.class.path").split(sep);
      shortCP = new StringBuilder();
      fullCP  = new StringBuilder();
      for (i = 0; i < parts.length; i++) {
	isJar = false;
	entry = new File(parts[i]);
	// fake jars when unzipping a ZIP file on Mac OSX start with "._"
	if (parts[i].toLowerCase().endsWith(".jar")) {
	  isJar = true;
	  if (entry.getName().startsWith("._"))
	    continue;
	}

	if (shortCP.length() > 0) {
	  shortCP.append(sep);
	  fullCP.append(sep);
	}

	if (isJar)
	  shortCP.append(entry.getName());
	else
	  shortCP.append(parts[i]);
	fullCP.append(parts[i]);
      }
      CLASSPATH_SHORT = shortCP.toString();
      CLASSPATH_FULL  = fullCP.toString();
    }

    if (shorten)
      result = CLASSPATH_SHORT;
    else
      result = CLASSPATH_FULL;

    return result;
  }

  /**
   * Returns whether we can call Thread.stop().
   *
   * @return		true if possible
   */
  public static boolean canStopThread() {
    return (getMajorVersion() <= 17);
  }

  /**
   * For testing only.
   *
   * @param args	each argument is interpreted as a directory to test
   * 			for JRE/JDK/etc.
   */
  public static void main(String[] args) {
    System.out.println("\n--> " + Java.getHome() + " (current java home)");
    System.out.println("JRE? " + isJRE());
    System.out.println("JDK? " + isJDK());
    System.out.println("bin dir? " + getBinDir());

    for (String arg: args) {
      System.out.println("\n--> " + arg);
      System.out.println("JRE? " + isJRE(arg));
      System.out.println("JDK? " + isJDK(arg));
      System.out.println("bin dir? " + getBinDir(arg));
    }
  }
}
