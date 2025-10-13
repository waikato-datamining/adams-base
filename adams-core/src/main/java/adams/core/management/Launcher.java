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
 * Launcher.java
 * Copyright (C) 2011-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import adams.core.Utils;
import adams.core.logging.LoggingObject;
import adams.core.option.AbstractSimpleOptionParser;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.gui.core.GUIHelper;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Launches a new JVM process with the specified memory
 * (<code>-memory &lt;amount&gt;</code>), a Java agent
 * (<code>-javaagent &lt;jar-file&gt;</code>) and main class
 * (<code>-main &lt;classname&gt;</code>).
 * <br><br>
 * All other command-line arguments are passed on to the new process.
 * <br><br>
 * In addition to parameters from the commandline, additional parameters can
 * be defined in the <code>ADAMS_OPTS</code> environment variable.
 * Note: No checks are performed if the same parameter is defined in this
 * env variable and on the commandline.
 * <br><br>
 * The <code>ADAMS_LIBRARY_PATH</code> allows you to supply additional libraries
 * that you would normally supply to the JVM using '-Djava.library.path=...'.
 * <br><br>
 * Use <code>-help</code> to output all available parameters.
 * <br><br>
 * When run from commandline, the method <code>addShutdownHook()</code> gets
 * called automatically before <code>execute()</code>, which adds a hook
 * thread to the runtime, killing the launched process, e.g., when
 * using <code>ctrl+c</code>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see #ENV_ADAMS_OPTS
 */
public class Launcher {

  /** the return code for restarting the main class. */
  public final static int CODE_RESTART = 100;

  /** the return code for restarting the main class with 50% more heap. */
  public final static int CODE_RESTART_MORE_HEAP = 101;

  /** the environment variable with additional options. */
  public final static String ENV_ADAMS_OPTS = "ADAMS_OPTS";

  /** the environment variable for the library path. */
  public final static String ENV_ADAMS_LIBRARY_PATH = "ADAMS_LIBRARY_PATH";

  /** the amount of memory to use for the process. */
  protected String m_Memory;

  /** the main class to launch. */
  protected String m_MainClass;

  /** the java agent jar to use. */
  protected String m_JavaAgentJar;

  /** optional JVM options. */
  protected List<String> m_JVMOptions;

  /** optional classpath augmentations. */
  protected List<String> m_ClassPathAugmentations;

  /** optional environment modifiers. */
  protected List<EnvironmentModifier> m_EnvironmentModifiers;

  /** optional priority jars. */
  protected List<String> m_PriorityJars;

  /** optional environment variables. */
  protected List<String> m_EnvVars;

  /** whether to collapse the classpath. */
  protected boolean m_CollapseClassPath;

  /** the arguments for the process. */
  protected String[] m_Arguments;

  /** the runtime object in use. */
  protected Runtime m_Runtime;

  /** the process that got launched. */
  protected Process m_Process;

  /** the debug level. */
  protected int m_DebugLevel;

  /** whether to ignore the ADAMS environment options. */
  protected boolean m_IgnoreEnvironmentOptions;

  /** whether to suppress error dialog. */
  protected boolean m_SuppressErrorDialog;

  /** the output printer to use. */
  protected Class m_OutputPrinter;

  /** for stdout. */
  protected OutputProcessStream m_StdOut;

  /** for stderr. */
  protected OutputProcessStream m_StdErr;

  /** the console object that calls the launcher (if any). */
  protected LoggingObject m_ConsoleObject;

  /** for logging. */
  protected Logger m_Logger;

  /**
   * Initializes the launcher.
   */
  public Launcher() {
    super();

    m_Memory                   = "";
    m_MainClass                = "";
    m_JavaAgentJar             = "";
    m_JVMOptions               = new ArrayList<>();
    m_Arguments                = new String[0];
    m_Runtime                  = Runtime.getRuntime();
    m_ClassPathAugmentations   = new ArrayList<>();
    m_EnvironmentModifiers     = new ArrayList<>();
    m_PriorityJars             = new ArrayList<>();
    m_EnvVars                  = new ArrayList<>();
    for (String key: EnvVar.get().keySet())
      m_EnvVars.add(key + "=" + EnvVar.get(key));
    m_CollapseClassPath        = false;
    m_DebugLevel               = 0;
    m_IgnoreEnvironmentOptions = false;
    m_SuppressErrorDialog      = false;
    m_OutputPrinter            = DefaultOutputPrinter.class;
    m_ConsoleObject            = null;
    m_Logger                   = Logger.getLogger(getClass().getName());

    addClassPathAugmentations(new ImplicitClassPathAugmenter());
  }

  /**
   * Sets the amount of memory for the process.
   *
   * @param value	the amount
   * @return		null if valid amount, otherwise error message
   */
  public String setMemory(String value) {
    String	result;

    result = null;
    value  = value.trim().toLowerCase();
    if (value.endsWith("k") || value.endsWith("m") || value.endsWith("g")) {
      try {
        Integer.parseInt(value.substring(0, value.length() - 1));
        m_Memory = value;
      }
      catch (Exception e) {
        result = "Failed to parse '" + value + "': " + e;
      }
    }
    else {
      result = "Memory amount must end with one of the following quantifiers: k, m, g";
    }

    return result;
  }

  /**
   * Sets the debug level (0 = off, >0 on).
   *
   * @param value	the level
   * @return		null if valid level, otherwise error message
   */
  public String setDebugLevel(String value) {
    String	result;

    result = null;
    try {
      m_DebugLevel = Integer.parseInt(value);
    }
    catch (Exception e) {
      result = "Failed to parse debug level '" + value + "': " + e;
    }

    return result;
  }

  /**
   * Increase the heap by 50%.
   */
  protected void increaseHeap() {
    String	suffix;
    String	amount;
    int		i;
    double	factor;

    // split up memory string
    suffix = "";
    for (i = m_Memory.length() - 1; i >= 0; i--) {
      if ((m_Memory.charAt(i) >= '0') && (m_Memory.charAt(i) <= '9'))
        break;
      else
        suffix = m_Memory.charAt(i) + suffix;
    }
    amount = m_Memory.substring(0, m_Memory.length() - suffix.length());
    factor = 1.5;
    if (suffix.equalsIgnoreCase("g")) {
      suffix = "m";
      factor = 1500;
    }

    m_Memory = (long) (Long.parseLong(amount) * factor) + suffix;
  }

  /**
   * Sets the main class of the process.
   *
   * @param value	the class name
   * @return		null if valid class, otherwise error message
   */
  public String setMainClass(String value) {
    String	result;

    result = null;

    try {
      Class.forName(value);
      m_MainClass = value;
    }
    catch (Exception e) {
      result = "Class not found: " + value;
    }

    return result;
  }

  /**
   * Sets the java agent jar of the process.
   *
   * @param value	the jar
   * @return		null if valid class, otherwise error message
   */
  public String setJavaAgentJar(String value) {
    String	result;
    File	file;

    result = null;

    try {
      file = new File(value);
      if (file.exists() && file.isFile())
        m_JavaAgentJar = value;
    }
    catch (Exception e) {
      result = "Java agent jar not found or not a file: " + value;
    }

    return result;
  }

  /**
   * Adds the JVM option.
   *
   * @param value	the option
   */
  public void addJVMOption(String value) {
    m_JVMOptions.add(value);
  }

  /**
   * Adds the augmentations that the classpath augmenter returns.
   *
   * @param cmdline	the classname+options of the augmenter
   * @see		ClassPathAugmenter
   */
  public void addClassPathAugmentations(String cmdline) {
    ClassPathAugmenter	augmenter;
    String[]		augmentations;

    try {
      augmenter     = (ClassPathAugmenter) OptionUtils.forCommandLine(ClassPathAugmenter.class, cmdline);
      augmentations = augmenter.getClassPathAugmentation();
      m_ClassPathAugmentations.addAll(Arrays.asList(augmentations));
    }
    catch (Exception e) {
      m_Logger.log(Level.SEVERE, "Error using classpath augmenter '" + cmdline + "'!", e);
    }
  }

  /**
   * Adds the augmentations that the classpath augmenter returns.
   *
   * @param cmdline	the classname+options of the augmenter
   * @see		ClassPathAugmenter
   */
  public void addEnvironmentModifier(String cmdline) {
    EnvironmentModifier	modifier;

    try {
      modifier = (EnvironmentModifier) AbstractSimpleOptionParser.forCommandline(cmdline);
      m_EnvironmentModifiers.add(modifier);
    }
    catch (Exception e) {
      m_Logger.log(Level.SEVERE, "Error instantiating environment modifier '" + cmdline + "'!", e);
    }
  }

  /**
   * Adds the priority jar.
   *
   * @param value	the option
   */
  public void addPriorityJar(String value) {
    m_PriorityJars.add(value);
  }

  /**
   * Adds the environmental variable key-value pair (key=value).
   *
   * @param value	the option
   */
  public void addEnvVar(String value) {
    m_EnvVars.add(value);
  }

  /**
   * Adds the augmentations that the classpath augmenter returns.
   *
   * @param augmenter	the classname+options of the augmenter
   * @see		ClassPathAugmenter
   */
  public void addClassPathAugmentations(ClassPathAugmenter augmenter) {
    m_ClassPathAugmentations.addAll(Arrays.asList(augmenter.getClassPathAugmentation()));
  }

  /**
   * Sets whether to collapse the classpath (using '*' below dirs instead of
   * explicit jar names).
   *
   * @param value	true if to collapse
   */
  public void collapseClassPath(boolean value) {
    m_CollapseClassPath = value;
  }

  /**
   * Sets the arguments for the process.
   *
   * @param value	the arguments
   */
  public String setArguments(String[] value) {
    String	result;

    result = null;

    try {
      m_Arguments = OptionUtils.splitOptions(OptionUtils.joinOptions(value));
    }
    catch (Exception e) {
      result = "Failed to parse the arguments: " + e;
    }

    return result;
  }

  /**
   * Adds a shutdown hook, to kill the launched process.
   *
   * @see 		#m_Process
   */
  public void addShutdownHook() {
    Thread	thread;

    thread = new Thread() {
      @Override
      public void run() {
        if (m_Process != null)
          m_Process.destroy();
      }
    };
    m_Runtime.addShutdownHook(thread);
  }

  /**
   * Sets to ignore the ADAMS environment options. 
   */
  public void ignoreEnvironmentOptions() {
    m_IgnoreEnvironmentOptions = true;
  }

  /**
   * Suppresses the error dialog.
   */
  public void suppressErrorDialog() {
    m_SuppressErrorDialog = true;
  }

  /**
   * Sets the output printer class to use.
   *
   * @param cls		the class to use
   * @throws IllegalArgumentException	if the class is not derived from {@link AbstractOutputPrinter}
   */
  public void setOutputPrinter(Class cls) {
    if (!ClassLocator.isSubclass(AbstractOutputPrinter.class, cls))
      throw new IllegalArgumentException(
          "Class is not derived from " + AbstractOutputPrinter.class.getName() + ": " + cls.getName());
    m_OutputPrinter = cls;
  }

  /**
   * Sets the owning console object to use for the output printer if that
   * implements {@link LoggingObjectOwner}.
   *
   * @param owner	the owner for the output printer, can be null
   */
  public void setConsoleObject(LoggingObject owner) {
    m_ConsoleObject = owner;
  }

  protected List<String> collapseClassPath(List<String> cpath) {
    List<String>		result;
    Map<String,List<String>> 	jars;
    List<String>		dirs;
    File			file;
    String			path;

    result = new ArrayList<>();
    jars   = new HashMap<>();
    dirs   = new ArrayList<>();

    for (String part : cpath) {
      if (part.trim().isEmpty())
        continue;
      file = new File(part.trim());
      if (!file.isDirectory()) {
        if (file.getParentFile() != null) {
          path = file.getParentFile().getAbsolutePath();
          if (!jars.containsKey(path))
            jars.put(path, new ArrayList<>());
          jars.get(path).add(file.getName());
        }
        else {
          m_Logger.warning("Failed to determine parent path for '" + file + "', skipping!");
        }
      }
      else {
        dirs.add(part);
      }
    }

    result.addAll(dirs);
    for (String part: jars.keySet())
      result.add(part + File.separator + "*");

    return result;
  }

  /**
   * Assembles the classpath.
   *
   * @return		the full classpath
   */
  protected String getClassPath() {
    String		result;
    List<String>	cpath;
    String		sep;
    String		os;
    File		file;
    String[]		parts;
    String[]		jars;
    int			i;

    cpath = new ArrayList<>();
    sep   = System.getProperty("path.separator");

    // add platform-specific path (if available)
    if (OS.isMac() && OS.isArm64())
      os = "macosxarm";
    else if (OS.isMac() && !OS.isArm64())
      os = "macosx";
    else if (OS.isWindows())
      os = "windows";
    else if (OS.isLinux() && OS.isArm64())
      os = "linuxarm";
    else if (OS.isLinux())
      os = "linux";
    else
      os = null; // no native libraries supported
    if (os != null)
      os += OS.getBitness();
    parts = System.getProperty("java.class.path").split(sep);
    cpath.addAll(Arrays.asList(parts));
    if (os != null) {
      for (String part: parts) {
        // platform specific sub-directory present?
        file = new File(part);
        if (file.isFile())
          file = file.getParentFile();
        file = new File(file.getAbsolutePath() + File.separator + os);
        if (file.exists() && file.isDirectory()) {
          jars = file.list((File dir, String name) -> {
            return name.endsWith(".jar");
          });
          for (String jar : jars) {
            cpath.add(file.getAbsolutePath() + File.separator + jar);
          }
          break;
        }
      }
    }

    // add augmentations
    cpath.addAll(m_ClassPathAugmentations);

    // collapse?
    if (m_CollapseClassPath)
      cpath = collapseClassPath(cpath);

    for (i = 0; i < m_PriorityJars.size(); i++)
      cpath.add(i, m_PriorityJars.get(i));

    result = Utils.flatten(cpath, sep);
    if (m_DebugLevel > 1)
      System.err.println("Classpath:\n" + result);

    return result;
  }

  /**
   * Returns the stdout handler.
   *
   * @return		the handler
   */
  public OutputProcessStream getStdOut() {
    return m_StdOut;
  }

  /**
   * Returns the stderr handler.
   *
   * @return		the handler
   */
  public OutputProcessStream getStdErr() {
    return m_StdErr;
  }

  /**
   * Allows killing the process.
   */
  public void destroy() {
    if (m_Process != null)
      m_Process.destroy();
  }

  /**
   * Launches the main class.
   *
   * @return		null if OK, otherwise an error message
   */
  public String execute() {
    String		result;
    List<String>	cmd;
    int			retVal;
    boolean		enableRestart;
    String		msg;
    Thread		thStdOut;
    Thread		thStdErr;

    result = null;

    enableRestart = false;
    try {
      if (ClassLocator.hasInterface(RestartableApplication.class, Class.forName(m_MainClass)))
        enableRestart = true;
    }
    catch (Exception e) {
      m_Logger.log(Level.SEVERE, "Failed to instantiate class '" + m_MainClass + "'!", e);
    }

    cmd = new ArrayList<>();
    cmd.add(Java.getJavaExecutable());
    cmd.add("-Xmx" + m_Memory);
    cmd.addAll(m_JVMOptions);
    cmd.add("-classpath");
    cmd.add(getClassPath());
    if (!m_JavaAgentJar.isEmpty())
      cmd.add("-javaagent:" + m_JavaAgentJar);

    if (!m_IgnoreEnvironmentOptions) {
      if (EnvVar.get(ENV_ADAMS_LIBRARY_PATH) != null)
        cmd.add("-Djava.library.path=" + EnvVar.get(ENV_ADAMS_LIBRARY_PATH));
    }

    cmd.add(m_MainClass);
    if (enableRestart)
      cmd.add(RestartableApplication.FLAG_ENABLE_RESTART);
    cmd.addAll(Arrays.asList(m_Arguments));

    if (!m_IgnoreEnvironmentOptions) {
      if (EnvVar.get(ENV_ADAMS_OPTS) != null) {
        try {
          cmd.addAll(Arrays.asList(OptionUtils.splitOptions(EnvVar.get(ENV_ADAMS_OPTS))));
        }
        catch (Exception e) {
	  m_Logger.log(Level.SEVERE, "Error parsing environment variable '" + ENV_ADAMS_OPTS + "'!", e);
        }
      }
    }

    // update env variables
    for (EnvironmentModifier mod: m_EnvironmentModifiers)
      mod.updateEnvironment(m_EnvVars);

    try {
      if (m_DebugLevel > 1) {
        System.out.println("\nGenerated command-line:\n" + Utils.flatten(cmd, "\n"));
        System.out.println("\nEnvironment variables:\n" + Utils.flatten(m_EnvVars, "\n"));
      }

      m_Process = m_Runtime.exec(
          cmd.toArray(new String[0]),
          m_EnvVars.toArray(new String[0]),
          new File(System.getProperty("basedir", ".")));
      m_StdOut  = new OutputProcessStream(m_Process, m_OutputPrinter, true);
      m_StdErr  = new OutputProcessStream(m_Process, m_OutputPrinter, false);
      if (m_ConsoleObject != null) {
        if (m_StdOut.getPrinter() instanceof LoggingObjectOwner)
          ((LoggingObjectOwner) m_StdOut.getPrinter()).setOwner(m_ConsoleObject);
        if (m_StdErr.getPrinter() instanceof LoggingObjectOwner)
          ((LoggingObjectOwner) m_StdErr.getPrinter()).setOwner(m_ConsoleObject);
      }
      thStdOut = new Thread(m_StdOut);
      thStdErr = new Thread(m_StdErr);
      thStdOut.start();
      thStdErr.start();

      retVal = m_Process.waitFor();

      if (m_DebugLevel > 0)
        System.out.println("Exit code: " + retVal);
      if (retVal != 0)
        result = "Exit code=" + retVal;

      while (thStdOut.isAlive()) {
        try {
          synchronized(this) {
            wait(50);
          }
        }
        catch (Exception e) {
          // ignored
        }
      }
      while (thStdErr.isAlive()) {
        try {
          synchronized(this) {
            wait(50);
          }
        }
        catch (Exception e) {
          // ignored
        }
      }

      m_Process = null;

      if (retVal == CODE_RESTART) {
        return execute();
      }
      else if (retVal == CODE_RESTART_MORE_HEAP) {
        increaseHeap();
        return execute();
      }
      else if (retVal != 0) {
        msg = "Application exited unexpectedly with exit code " + retVal + ", "
            + "options used for starting process:\n\n"
            + Utils.flatten(cmd, "\n");
        System.err.println(msg);
        if (!m_SuppressErrorDialog && !GUIHelper.isHeadless())
          GUIHelper.showErrorMessage(
              null, msg, "Application exited unexpectedly!");
      }
    }
    catch (Exception e) {
      result = "Exception occurred launching " + m_MainClass + ": ";
      m_Logger.log(Level.SEVERE, result, e);
      result += e;
    }

    return result;
  }

  /**
   * Configures the launcher.
   *
   * @param args	the commandline arguments to use for launching
   * @param launcher	the launcher to configure
   */
  protected static String configure(String[] args, Launcher launcher) {
    String		result;
    String		value;
    List<String>	options;

    options = new ArrayList<>(Arrays.asList(args));

    // debug
    value = OptionUtils.removeOption(options, "-debug");
    if (value != null)
      launcher.setDebugLevel(value);

    // collapse
    launcher.collapseClassPath(OptionUtils.removeFlag(options, "-collapse"));

    // memory
    value = OptionUtils.removeOption(options, "-memory");
    if (value != null)
      result = launcher.setMemory(value);
    else
      result = "Missing option: -memory";

    // main class
    if (result == null) {
      value = OptionUtils.removeOption(options, "-main");
      if (value != null)
        result = launcher.setMainClass(value);
      else
        result = "Missing option: -main";
    }

    // java agent jar
    if (result == null) {
      value = OptionUtils.removeOption(options, "-javaagent");
      if (value != null)
        result = launcher.setJavaAgentJar(value);
    }

    // JVM options
    if (result == null) {
      while ((value = OptionUtils.removeOption(options, "-jvm")) != null)
        launcher.addJVMOption(value);
      if (Java.getMajorVersion() >= 17)
	launcher.addJVMOption("--enable-native-access=ALL-UNNAMED");
    }

    // classpath augmenters
    if (result == null) {
      while ((value = OptionUtils.removeOption(options, "-cpa")) != null)
        launcher.addClassPathAugmentations(value);
    }

    // priority jars
    if (result == null) {
      while ((value = OptionUtils.removeOption(options, "-priority")) != null)
        launcher.addPriorityJar(value);
    }

    // environment variable
    if (result == null) {
      while ((value = OptionUtils.removeOption(options, "-env")) != null)
        launcher.addEnvVar(value);
    }

    // environment modifiers
    if (result == null) {
      while ((value = OptionUtils.removeOption(options, "-env-modifier")) != null)
        launcher.addEnvironmentModifier(value);
    }

    if (result == null)
      result = launcher.setArguments(options.toArray(new String[0]));

    return result;
  }

  /**
   * Removes unnecessary double quotes.
   *
   * @param args	the arguments to clean
   */
  protected static void cleanOptions(String[] args) {
    int		i;

    for (i = 0; i < args.length; i++) {
      if (args[i].startsWith("\"") && args[i].endsWith("\""))
        args[i] = args[i].substring(1, args[i].length() - 1);
    }
  }

  /**
   * Executes the class from the command-line.
   *
   * @param args	the command-line arguments
   */
  public static void main(String[] args) throws Exception {
    Launcher 	launcher;
    String 	error;
    String	debug;

    cleanOptions(args);

    if (OptionUtils.helpRequested(args)) {
      System.out.println("Environment variables:");
      System.out.println("- ADAMS_OPTS - additional options to the main class");
      System.out.println("- ADAMS_LIBRARY_PATH - paths supplied to JVM via '-Djava.library.path=...'");
      System.out.println();
      System.out.println("Options:");
      System.out.println("-debug <level>");
      System.out.println("\tThe debug level with 0=off and the higher the number");
      System.out.println("\tthe more output (optional parameter; default: 0).");
      System.out.println("-memory <amount>");
      System.out.println("\tSpecifies the maximum amount of memory to allocate for");
      System.out.println("\tthe heap in the JVM for the process that is being launch.");
      System.out.println("\tUse 'k' for kilobytes, 'm' for megabytes and 'g' for ");
      System.out.println("\tgigabytes. Examples: 1000m, 2g");
      System.out.println("-main <classname>");
      System.out.println("\tThe class to launch as main class in the new JVM process.");
      System.out.println("-javaagent <jar-file>");
      System.out.println("\tThe jar file containing the Java Agent.");
      System.out.println("\tExample: -javaagent /some/where/sizeofag-1.0.0.jar).");
      System.out.println("[-jvm <option>]");
      System.out.println("\tOptional arguments for the JVM.");
      System.out.println("\tCan be supplied multiple times.");
      System.out.println("\tExample: -jvm -javaagent:sizeofag.jar");
      System.out.println("[-cpa <classname>]");
      System.out.println("\tOptional classpath augmenters (classname + options).");
      System.out.println("\tCan be supplied multiple times.");
      System.out.println("\tNote: adams.core.management.MultiClassPathAugmenter gets added automatically");
      System.out.println("\tExample: -cpa adams.core.management.SystemClassPathAugmenter");
      System.out.println("[-priority <jar>]");
      System.out.println("\tOptional jar (with path) that should be added at start of classpath.");
      System.out.println("\tCan be supplied multiple times.");
      System.out.println("\tExample: -priority ./lib/activation-1.1.jar");
      System.out.println("[-env <key=value>]");
      System.out.println("\tOptional environment variable key-value pair.");
      System.out.println("\tCan be supplied multiple times.");
      System.out.println("\tExample: -env weka.packageManager.loadPackages=false");
      System.out.println("[-env-modifier <classname+options>]");
      System.out.println("\tOptional environment variable modifier.");
      System.out.println("\tCan be supplied multiple times.");
      System.out.println("\tExample: -env-modifier adams.core.management.WekaHomeEnvironmentModifier");
      System.out.println("[-collapse");
      System.out.println("\tOptional directive to collapse the classpath, using '*' below a directory.");
      System.out.println("-...");
      System.out.println("\tAny other option will get passed on to the main class.");
      return;
    }

    // output commandline options
    debug = OptionUtils.getOption(args, "-debug");
    if ((debug != null) && Utils.isInteger(debug) && (Integer.parseInt(debug) > 1))
      System.out.println(Utils.flatten(args, "\n"));

    Environment.setEnvironmentClass(Environment.class);
    launcher = new Launcher();
    error    = configure(args, launcher);
    if (error == null) {
      launcher.addShutdownHook();
      launcher.execute();
    }
    else {
      System.err.println("Failed to execute launcher:");
      System.err.println(error);
      System.exit(1);
    }
  }
}
