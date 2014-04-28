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
 * ProcessUtils.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.core.management;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.HashMap;

import adams.core.Utils;
import adams.core.io.PlaceholderDirectory;

/**
 * A helper class for process related stuff.
 * 
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ProcessUtils {

  /** the constant for the auto PID. */
  public final static long AUTO_PID = -999;

  /** the constant for no PID. */
  public final static long NO_PID = -1;

  /**
   * A container class for the results obtained from executing a process.
   * 
   * @author fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public final static class ProcessResult
    implements Serializable {

    /** for serialization. */
    private static final long serialVersionUID = 1902809285333524039L;

    /** the command. */
    protected String[] m_Command;

    /** the environment variables. */
    protected String[] m_Environment;

    /** the exit code. */
    protected int m_ExitCode;

    /** the stdout content. */
    protected String m_StdOut;

    /** the stderr content. */
    protected String m_StdErr;

    /**
     * Initializes the container.
     * 
     * @param cmd
     *          the command that was used
     * @param env
     *          the environment
     * @param input
     *          the input to be written to the process
     * @param process
     *          the process to obtain the results from
     * @throws Execption
     *           if collection of data fails
     */
    public ProcessResult(String cmd, String[] env, String input, Process process)
	throws Exception {
      this(new String[] {cmd}, env, input, process);
    }

    /**
     * Initializes the container.
     * 
     * @param cmd
     *          the command that was used
     * @param env
     *          the environment
     * @param input
     *          the input to be written to the process
     * @param process
     *          the process to obtain the results from
     * @throws Execption
     *           if collection of data fails
     */
    public ProcessResult(String[] cmd, String[] env, String input,
	final Process process) throws Exception {
      // stderr
      final StringBuilder stde = new StringBuilder();
      Runnable rune = new Runnable() {

	@Override
	public void run() {
	  try {
	    String line;
	    BufferedReader reader = new BufferedReader(new InputStreamReader(
		process.getErrorStream()), 1024);
	    while ((line = reader.readLine()) != null) {
	      stde.append(line);
	      stde.append("\n");
	    }
	  }
	  catch (Exception e) {
	    System.err.println("Failed to reader stderr for process #"
		+ process.hashCode() + ":");
	    e.printStackTrace();
	  }
	}
      };
      Thread threade = new Thread(rune);
      threade.start();

      // stdout
      final StringBuilder stdo = new StringBuilder();
      Runnable runo = new Runnable() {

	@Override
	public void run() {
	  try {
	    String line;
	    BufferedReader reader = new BufferedReader(new InputStreamReader(
		process.getInputStream()), 1024);
	    while ((line = reader.readLine()) != null) {
	      stdo.append(line);
	      stdo.append("\n");
	    }
	  }
	  catch (Exception e) {
	    System.err.println("Failed to reader stdout for process #"
		+ process.hashCode() + ":");
	    e.printStackTrace();
	  }
	}
      };
      Thread threado = new Thread(runo);
      threado.start();

      // writing the input to the standard input of the process
      if (input != null) {
	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
	    process.getOutputStream()));
	writer.write(input);
	writer.close();
      }

      m_Command = cmd;
      m_Environment = env;
      m_ExitCode = process.waitFor();

      // wait for threads to finish
      while (threade.isAlive() || threado.isAlive()) {
	try {
	  synchronized (this) {
	    wait(100);
	  }
	}
	catch (Exception e) {
	}
      }
      m_StdOut = stdo.toString();
      m_StdErr = stde.toString();
    }

    /**
     * Returns whether the process has succeeded.
     * 
     * @return true if succeeded, i.e., exit code = 0
     */
    public boolean hasSucceeded() {
      return (m_ExitCode == 0);
    }

    /**
     * Returns the exit code.
     * 
     * @return the exit code
     */
    public int getExitCode() {
      return m_ExitCode;
    }

    /**
     * Returns the command that was used for the process.
     * 
     * @return the command
     */
    public String[] getCommand() {
      return m_Command;
    }

    /**
     * Returns the environment.
     * 
     * @return the environment, null if process inherited current one
     */
    public String[] getEnvironment() {
      return m_Environment;
    }

    /**
     * Returns the output on stdout.
     * 
     * @return the output
     */
    public String getStdOut() {
      return m_StdOut;
    }

    /**
     * Returns the output on stderr.
     * 
     * @return the output
     */
    public String getStdErr() {
      return m_StdErr;
    }

    /**
     * Returns a short description string.
     * 
     * @return the description
     */
    @Override
    public String toString() {
      return "exit code=" + m_ExitCode;
    }

    /**
     * Returns an error output based on the information stored.
     * 
     * @return the error output
     */
    public String toErrorOutput() {
      StringBuilder result;

      result = new StringBuilder();

      if (m_ExitCode == 0) {
	result.append("Command succeeded!");
      }
      else {
	result.append("Command failed with exit code " + m_ExitCode + "!\n");
	result.append("--> Command:\n");
	result.append(Utils.flatten(m_Command, "\n"));
	result.append("\n\n");
	if (m_Environment != null) {
	  result.append("--> Environemtn:\n");
	  result.append(Utils.flatten(m_Environment, "\n"));
	  result.append("\n\n");
	}
	result.append("--> Error output:\n");
	result.append(m_StdErr);
      }

      return result.toString();
    }
  }

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
  public static ProcessResult execute(String cmd) throws Exception {
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
  public static ProcessResult execute(String cmd, PlaceholderDirectory cwd)
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
  public static ProcessResult execute(String cmd, HashMap<String, String> env,
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
  public static ProcessResult execute(String cmd, HashMap<String, String> env,
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
  public static ProcessResult execute(String cmd, String[] env,
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
  public static ProcessResult execute(String[] cmd) throws Exception {
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
  public static ProcessResult execute(String[] cmd, PlaceholderDirectory cwd)
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
  public static ProcessResult execute(String[] cmd,
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
  public static ProcessResult execute(String[] cmd,
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
  public static ProcessResult execute(String[] cmd, String[] env,
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
  public static ProcessResult execute(String[] cmd, String[] env, String input,
      PlaceholderDirectory cwd) throws Exception {
    Runtime runtime;
    Process process;

    runtime = Runtime.getRuntime();
    process = runtime.exec(cmd, env,
	(cwd == null) ? null : cwd.getAbsoluteFile());

    return new ProcessResult(cmd, env, input, process);
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
  public static ProcessResult execute(String cmd, String[] env, String input,
      PlaceholderDirectory cwd) throws Exception {
    Runtime runtime;
    Process process;

    runtime = Runtime.getRuntime();
    process = runtime.exec(cmd, env,
	(cwd == null) ? null : cwd.getAbsoluteFile());

    return new ProcessResult(cmd, env, input, process);
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

    result = new HashMap<String, String>(System.getenv());

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
