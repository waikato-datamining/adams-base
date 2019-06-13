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
 * ProcessUtils.java
 * Copyright (C) 2009-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.core.management;

import adams.core.Utils;
import adams.core.base.BaseKeyValuePair;
import adams.core.io.PlaceholderDirectory;
import com.github.fracpete.processoutput4j.core.EnvironmentUtils;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;

import java.lang.management.ManagementFactory;
import java.util.HashMap;

/**
 * A helper class for process related stuff.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ProcessUtils {

  /** the constant for the auto PID. */
  public final static long AUTO_PID = -999;

  /** the constant for no PID. */
  public final static long NO_PID = -1;

  /**
   * Returns the PID of the virtual machine. Caution: it's a hack and can break
   * anytime. Do NOT rely on it. Based on <a href=
   * "http://blog.igorminar.com/2007/03/how-java-application-can-discover-its.html"
   * target="_blank">this blog entry</a>.
   *
   * @return the PID or -1 in case of an error
   */
  public static long getVirtualMachinePID() {
    long result;
    String name;

    name = ManagementFactory.getRuntimeMXBean().getName();

    try {
      result = Long.parseLong(name.replaceAll("@.*", ""));
    }
    catch (Exception e) {
      result = -1;
    }

    return result;
  }

  /**
   * Executes the command and returns a result container.
   *
   * @param cmd
   *          the command to execute
   * @return the results of the proces: exit code, stdout, stderr
   * @throws Exception
   *           if something goes wrong
   */
  public static CollectingProcessOutput execute(String cmd) throws Exception {
    return execute(cmd, null);
  }

  /**
   * Executes the command and returns a result container.
   *
   * @param cmd
   *          the command to execute
   * @param cwd
   *          the working directory for the command, null to ignore
   * @return the results of the proces: exit code, stdout, stderr
   * @throws Exception
   *           if something goes wrong
   */
  public static CollectingProcessOutput execute(String cmd, PlaceholderDirectory cwd)
    throws Exception {
    return execute(cmd, (String[]) null, cwd);
  }

  /**
   * Executes the command and returns a result container.
   *
   * @param cmd
   *          the command to execute
   * @param env
   *          the environment variables, null if to inherit current ones
   * @param cwd
   *          the working directory for the command, null to ignore
   * @return the results of the proces: exit code, stdout, stderr
   * @throws Exception
   *           if something goes wrong
   */
  public static CollectingProcessOutput execute(String cmd, HashMap<String, String> env,
						PlaceholderDirectory cwd) throws Exception {
    return execute(cmd, convertHashMap(env), cwd);
  }

  /**
   * Executes the command and returns a result container.
   *
   * @param cmd
   *          the command to execute
   * @param env
   *          the environment variables, null if to inherit current ones
   * @param input
   *          the input to write to the process
   * @param cwd
   *          the working directory for the command, null to ignore
   * @return the results of the proces: exit code, stdout, stderr
   * @throws Exception
   *           if something goes wrong
   */
  public static CollectingProcessOutput execute(String cmd, HashMap<String, String> env,
						String input, PlaceholderDirectory cwd) throws Exception {
    return execute(cmd, convertHashMap(env), input, cwd);
  }

  /**
   * Executes the command and returns a result container.
   *
   * @param cmd
   *          the command to execute
   * @param env
   *          the environment variables, null if to inherit current ones
   * @param cwd
   *          the working directory for the command, null to ignore
   * @return the results of the proces: exit code, stdout, stderr
   * @throws Exception
   *           if something goes wrong
   */
  public static CollectingProcessOutput execute(String cmd, String[] env,
						PlaceholderDirectory cwd) throws Exception {
    return execute(cmd, env, null, cwd);
  }

  /**
   * Executes the commandline array and returns a result container.
   *
   * @param cmd
   *          the commandline array to execute
   * @return the results of the proces: exit code, stdout, stderr
   * @throws Exception
   *           if something goes wrong
   */
  public static CollectingProcessOutput execute(String[] cmd) throws Exception {
    return execute(cmd, null);
  }

  /**
   * Executes the commandline array and returns a result container.
   *
   * @param cmd
   *          the commandline array to execute
   * @param cwd
   *          the working directory for the command, null to ignore
   * @return the results of the proces: exit code, stdout, stderr
   * @throws Exception
   *           if something goes wrong
   */
  public static CollectingProcessOutput execute(String[] cmd, PlaceholderDirectory cwd)
    throws Exception {
    return execute(cmd, (String[]) null, cwd);
  }

  /**
   * Executes the commandline array and returns a result container.
   *
   * @param cmd
   *          the commandline array to execute
   * @param env
   *          the environment variables, null if to inherit current ones
   * @param cwd
   *          the working directory for the command, null to ignore
   * @return the results of the proces: exit code, stdout, stderr
   * @throws Exception
   *           if something goes wrong
   */
  public static CollectingProcessOutput execute(String[] cmd,
						HashMap<String, String> env, PlaceholderDirectory cwd) throws Exception {
    return execute(cmd, convertHashMap(env), cwd);
  }

  /**
   * Executes the commandline array and returns a result container.
   *
   * @param cmd
   *          the commandline array to execute
   * @param env
   *          the environment variables, null if to inherit current ones
   * @param input
   *          the input to write to the process
   * @param cwd
   *          the working directory for the command, null to ignore
   * @return the results of the proces: exit code, stdout, stderr
   * @throws Exception
   *           if something goes wrong
   */
  public static CollectingProcessOutput execute(String[] cmd,
						HashMap<String, String> env, String input, PlaceholderDirectory cwd)
    throws Exception {
    return execute(cmd, convertHashMap(env), input, cwd);
  }

  /**
   * Executes the commandline array and returns a result container.
   *
   * @param cmd
   *          the commandline array to execute
   * @param env
   *          the environment variables, null if to inherit current ones
   * @param cwd
   *          the working directory for the command, null to ignore
   * @return the results of the proces: exit code, stdout, stderr
   * @throws Exception
   *           if something goes wrong
   */
  public static CollectingProcessOutput execute(String[] cmd, String[] env,
						PlaceholderDirectory cwd) throws Exception {
    return execute(cmd, env, null, cwd);
  }

  /**
   * Executes the commandline array and returns a result container.
   *
   * @param cmd
   *          the commandline array to execute
   * @param env
   *          the environment variables, null if to inherit current ones
   * @param input
   *          the input to write to the process
   * @param cwd
   *          the working directory for the command, null to ignore
   * @return the results of the proces: exit code, stdout, stderr
   * @throws Exception
   *           if something goes wrong
   */
  public static CollectingProcessOutput execute(String[] cmd, String[] env, String input,
						PlaceholderDirectory cwd) throws Exception {
    CollectingProcessOutput result;
    ProcessBuilder builder;

    builder = new ProcessBuilder();
    builder.command(cmd);
    builder.environment().putAll(EnvironmentUtils.envArrayToMap(env));
    if (cwd != null)
      builder.directory(cwd.getAbsoluteFile());

    result = new CollectingProcessOutput();
    result.monitor(input, builder);

    return result;
  }

  /**
   * Executes the commandline array and returns a result container.
   *
   * @param cmd
   *          the commandline array to execute
   * @param env
   *          the environment variables, null if to inherit current ones
   * @param input
   *          the input to write to the process
   * @param cwd
   *          the working directory for the command, null to ignore
   * @return the results of the proces: exit code, stdout, stderr
   * @throws Exception
   *           if something goes wrong
   */
  public static CollectingProcessOutput execute(String cmd, String[] env, String input,
						PlaceholderDirectory cwd) throws Exception {
    CollectingProcessOutput result;
    ProcessBuilder builder;

    builder = new ProcessBuilder();
    builder.command(cmd);
    builder.environment().putAll(EnvironmentUtils.envArrayToMap(env));
    if (cwd != null)
      builder.directory(cwd.getAbsoluteFile());

    result = new CollectingProcessOutput();
    result.monitor(input, builder);

    return result;
  }

  /**
   * Returns an error output based on the information stored.
   *
   * @return the error output
   */
  public static String toErrorOutput(CollectingProcessOutput output) {
    StringBuilder result;

    result = new StringBuilder();

    if (output.getExitCode() == 0) {
      result.append("Command succeeded!");
    }
    else {
      result.append("Command failed with exit code " + output.getExitCode() + "!\n");
      result.append("--> Command:\n");
      result.append(Utils.flatten(output.getCommand(), "\n"));
      result.append("\n\n");
      if (output.getEnvironment() != null) {
	result.append("--> Environment:\n");
	result.append(Utils.flatten(output.getEnvironment(), "\n"));
	result.append("\n\n");
      }
      result.append("--> Error output:\n");
      result.append(output.getStdErr());
    }

    return result.toString();
  }

  /**
   * Returns the number of available processors.
   *
   * @return the number of processors of the machine
   */
  public static int getAvailableProcessors() {
    return Runtime.getRuntime().availableProcessors();
  }

  /**
   * Returns the system's environment variables. Returns the same content as
   * {@link System#getenv()}, but this hashmap can be modified.
   *
   * @return the environment variables as key-value pairs
   */
  public static HashMap<String, String> getEnvironment() {
    HashMap<String, String> result;

    result = new HashMap<>(System.getenv());

    return result;
  }

  /**
   * Returns the system's environment variables with the provided ones overlayed
   * on top.
   *
   * @param envVars		the environment variables to overlay
   * @param nullIfEmpty 	if true and no custom environment variables, then returns null
   * @return 			the environment variables as key-value pairs or
   * 				null if nullIfEmpty is true and no custom environment variables
   */
  public static HashMap<String, String> getEnvironment(BaseKeyValuePair[] envVars, boolean nullIfEmpty) {
    HashMap<String, String> result;

    if (nullIfEmpty && (envVars.length == 0))
      return null;

    result = getEnvironment();
    if (envVars.length > 0) {
      for (BaseKeyValuePair envVar: envVars)
        result.put(envVar.getPairKey(), envVar.getPairValue());
    }

    return result;
  }

  /**
   * Returns a flattened environment with key=value pairs.
   *
   * @param env		the environment to flatten, can be null
   * @return 		the flattened environment
   */
  public static String[] flattenEnvironment(HashMap<String, String> env) {
    String[]	result;
    int		i;

    if (env == null)
      return null;

    result = new String[env.size()];
    i      = 0;
    for (String key: env.keySet()) {
      result[i] = key + "=" + env.get(key);
      i++;
    }

    return result;
  }

  /**
   * Converts the environment variables stored in the hashmap to a string array
   * ("key=value"). If the hashmap is null, null is returned as well.
   *
   * @param env
   *          the hashmap to convert
   * @return the generated string array, null if hashmap was null
   */
  protected static String[] convertHashMap(HashMap<String, String> env) {
    String[] result;
    int i;

    result = null;

    if (env != null) {
      result = new String[env.size()];
      i = 0;
      for (String key: env.keySet()) {
	result[i] = key + "=" + env.get(key);
	i++;
      }
    }

    return result;
  }

  /**
   * Escapes blanks in the path for Windows using the caret ("^"). Other
   * platforms are not affected.n
   *
   * @param path	the path to escape
   * @return		the escaped path
   */
  public static String escapeBlanks(String path) {
    if (OS.isWindows())
      return path.replace(" ", "^ ");
    else
      return path;
  }
}
