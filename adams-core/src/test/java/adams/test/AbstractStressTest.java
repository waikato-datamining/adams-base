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
 * AbstractStressTest.java
 * Copyright (C) 2009-2013 University of Waikato
 */

package adams.test;

import java.util.Date;

import javax.swing.SwingWorker;

import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.management.JMap;
import adams.core.management.ProcessUtils;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.db.AbstractDatabaseObjectWithOptionHandling;
import adams.env.Environment;

/**
 * Abstract ancestor of classes for stress-testing.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractStressTest
  extends AbstractDatabaseObjectWithOptionHandling {

  /** for serialization. */
  private static final long serialVersionUID = -2535320030771462923L;

  /**
   * Specialized worker class for executing an actual iteration.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static abstract class Worker
    extends SwingWorker {

    /** the owner. */
    protected AbstractStressTest m_Owner;

    /** the current iteration. */
    protected int m_Iteration;

    /** indicates whether the thread has finished. */
    protected boolean m_Finished;

    /**
     * Initializes the worker thread.
     *
     * @param owner	the owning stress tester object
     * @param iteration	the current iteration
     */
    public Worker(AbstractStressTest owner, int iteration) {
      super();

      m_Owner     = owner;
      m_Iteration = iteration;
    }

    /**
     * Runs jmap and logs/outputs the result.
     *
     * @see 		AbstractStressTest#getRegExp()
     * @see		AbstractStressTest#log(String)
     */
    protected void runJmap() {
      String 	out;
      String[] 	lines;
      int 	i;

      out   = JMap.execute(m_Owner.getJmapOptions(), ProcessUtils.getVirtualMachinePID());
      lines = out.split("\n");
      for (i = 0; i < lines.length; i++) {
	if (m_Owner.getRegExp().isMatch(lines[i]))
	  m_Owner.log("   " + lines[i]);
      }
    }

    /**
     * Gets called after the thread has finished.
     */
    @Override
    protected void done() {
      m_Finished = true;

      m_Owner.log("\nIteration " + (m_Iteration+1) + "/" + m_Owner.getNumIterations());
      if (m_Owner.getUseJmap())
	runJmap();

      m_Owner = null;

      super.done();
    }

    /**
     * Returns whether the thread has finished.
     *
     * @return		true if the thread has finished normally
     * @see 		#done()
     */
    public boolean hasFinished() {
      return m_Finished;
    }

    /**
     * Stops the execution of the iteration.
     */
    public abstract void stopExecution();
  }

  /** the optional log for storing the jmap output. */
  protected PlaceholderFile m_Log;

  /** the number of iterations to perform. */
  protected int m_NumIterations;

  /** the number of seconds to wait before stopping. */
  protected int m_NumSeconds;

  /** the regular expression that the jmap output must meet. */
  protected BaseRegExp m_RegExp;

  /** whether the log file is really pointing to a file. */
  protected Boolean m_LogIsFile;

  /** whether to run jmap. */
  protected Boolean m_UseJmap;

  /** the optional jmap options. */
  protected String m_JmapOptions;

  /** indicates whether the actual execution took place. */
  protected boolean m_Excecuted;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "log", "log",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "num-iter", "numIterations",
	    10);

    m_OptionManager.add(
	    "num-sec", "numSeconds",
	    10);

    m_OptionManager.add(
	    "reg-exp", "regExp",
	    new BaseRegExp(".*adams\\..*"));

    m_OptionManager.add(
	    "jmap", "useJmap",
	    false);

    m_OptionManager.add(
	    "jmap-options", "jmapOptions",
	    "");
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_LogIsFile = null;
  }

  /**
   * Sets the log to store the jmap output in.
   *
   * @param value 	the log
   */
  public void setLog(PlaceholderFile value) {
    m_Log = value;
    reset();
  }

  /**
   * Returns the log to store the jmap output in.
   *
   * @return 		the log
   */
  public PlaceholderFile getLog() {
    return m_Log;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logTipText() {
    return
        "The optional log file to store the jmap output in; gets ignored if "
      + "pointing to a directory.";
  }

  /**
   * Sets the number of times to execute.
   *
   * @param value 	the number of iterations
   */
  public void setNumIterations(int value) {
    m_NumIterations = value;
    reset();
  }

  /**
   * Returns the number of times to execute.
   *
   * @return 		the number of iterations
   */
  public int getNumIterations() {
    return m_NumIterations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numIterationsTipText() {
    return "The number of iterations to perform.";
  }

  /**
   * Sets the number of seconds before stopping the thread.
   *
   * @param value 	the number of seconds
   */
  public void setNumSeconds(int value) {
    m_NumSeconds = value;
    reset();
  }

  /**
   * Returns the number of seconds before stopping the thread.
   *
   * @return 		the number of seconds
   */
  public int getNumSeconds() {
    return m_NumSeconds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numSecondsTipText() {
    return "The number of seconds before stopping the thread again; use -1 to let thread finish.";
  }

  /**
   * Sets the regular expression that the jmap output must match.
   *
   * @param value 	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression that the jmap output must match.
   *
   * @return 		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression that the jmap output must match.";
  }

  /**
   * Sets whether to use jmap or not.
   *
   * @param value 	if true then jmap is used
   */
  public void setUseJmap(boolean value) {
    m_UseJmap = value;
    reset();
  }

  /**
   * Returns whether jmap is used or not.
   *
   * @return 		true if jmap is used
   */
  public boolean getUseJmap() {
    return m_UseJmap;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useJmapTipText() {
    return "Whether to run jmap.";
  }

  /**
   * Sets the jmap commandline options.
   *
   * @param value 	the commandline options
   */
  public void setJmapOptions(String value) {
    m_JmapOptions = value;
    reset();
  }

  /**
   * Returns the jmap commandline options.
   *
   * @return 		the commandline options
   */
  public String getJmapOptions() {
    return m_JmapOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String jmapOptionsTipText() {
    return "The commandline options for jmap, eg '-histo:live'.";
  }

  /**
   * Logs the given message. If the log is pointing to a directory, then the
   * output is only output on stdout.
   *
   * @param msg		the message to log
   */
  public synchronized void log(String msg) {
    boolean	append;

    if (m_LogIsFile == null) {
      m_LogIsFile = !m_Log.isDirectory();
      append      = false;
    }
    else {
      append = true;
    }

    if (m_LogIsFile)
      FileUtils.writeToFile(m_Log.getAbsolutePath(), msg, append);
    System.out.println(msg);
    getLogger().info(msg);
  }

  /**
   * Connects to the database.
   *
   * @return		true if things can proceed
   */
  protected boolean preExecute() {
    establishDatabaseConnection();

    return m_dbc.isConnected();
  }

  /**
   * Sets up a Worker instance, ready to be executed.
   *
   * @param iteration	the current iteration
   * @return		the Worker instance
   */
  protected abstract Worker setupWorker(int iteration);

  /**
   * Performs the actual stress-testing.
   */
  protected void doExecute() {
    int		i;
    int		n;
    int		m;
    Worker	worker;

    log("Setup: " + OptionUtils.getCommandLine(this));
    log("Start: " + new Date());

    for (i = 0; i < m_NumIterations; i++) {
      // start thread
      worker = setupWorker(i);
      worker.execute();

      // wait before stopping thread?
      if (m_NumSeconds > -1) {
	for (n = 0; i < m_NumSeconds; n++) {
	  // thread already finished?
	  if (worker.hasFinished())
	    break;

	  // wait for a second
	  for (m = 0; m < 5; m++) {
	    // thread already finished?
	    if (worker.hasFinished())
	      break;

	    try {
	      synchronized(this) {
		wait(200);
	      }
	    }
	    catch (Exception e) {
	      // ignored
	    }
	  }
	}
	if (!worker.hasFinished())
	  worker.stopExecution();
      }
      // let thread finish?
      else {
	while (!worker.hasFinished()) {
	  try {
	    synchronized(this) {
	      wait(200);
	    }
	  }
	  catch (Exception e) {
	    // ignored
	  }
	}
      }
    }

    log("\nFinish: " + new Date());
  }

  /**
   * For cleaning up. Gets always executed, even if actual execution didn't
   * occur.
   * <br><br>
   * Default implementation does nothing.
   *
   * @see		#m_Excecuted
   */
  protected void postExecute() {
  }

  /**
   * Performs the stress-testing.
   */
  public void execute() {
    if (preExecute()) {
      m_Excecuted = true;
      doExecute();
    }
    postExecute();
  }

  /**
   * Instantiates the stress-tester with the given options.
   *
   * @param classname	the classname of the stress-tester to instantiate
   * @param options	the options for the stress-tester
   * @return		the instantiated stress-tester or null if an error occurred
   */
  public static AbstractStressTest forName(String classname, String[] options) {
    AbstractStressTest	result;

    try {
      result = (AbstractStressTest) OptionUtils.forName(AbstractStressTest.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Runs the stress-tester.
   *
   * @param env		the environment class to use
   * @param tester	the stress-tester class to execute
   * @param args	the commandline arguments, use -help to display all
   */
  public static void runStressTester(Class env, Class tester, String[] args) {
    AbstractStressTest	testerInst;

    Environment.setEnvironmentClass(env);

    testerInst = forName(tester.getName(), args);

    try {
      if (OptionUtils.helpRequested(args)) {
	System.out.println("\nHelp requested:\n");
	System.out.println(OptionUtils.list(testerInst));
      }
      else {
	ArrayConsumer.setOptions(testerInst, args);
	testerInst.execute();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
