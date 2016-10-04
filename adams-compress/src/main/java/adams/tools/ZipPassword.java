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
 * ZipPassword.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.tools;

import adams.core.BruteForcePasswordGenerator;
import adams.core.Performance;
import adams.core.ThreadLimiter;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.core.logging.LoggingHelper;
import adams.env.Environment;
import adams.flow.core.RunnableWithLogging;
import adams.multiprocess.PausableFixedThreadPoolExecutor;
import net.lingala.zip4j.core.ZipFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Attempts to determine the password of a password protected ZIP file.<br>
 * If no dictionary file has been provided, a brute force attack is carried out.<br>
 * The brute force attack can be run in parallel, default is two threads.<br>
 * The dictionary approach also tests lower&#47;upper case version of the passwords and the reverse of them.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-zip &lt;adams.core.io.PlaceholderFile&gt; (property: zip)
 * &nbsp;&nbsp;&nbsp;The ZIP file to process.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-dictionary &lt;adams.core.io.PlaceholderFile&gt; (property: dictionary)
 * &nbsp;&nbsp;&nbsp;The dictionary file to process.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-chars &lt;java.lang.String&gt; (property: characters)
 * &nbsp;&nbsp;&nbsp;The characters to use for brute force attack.
 * &nbsp;&nbsp;&nbsp;default: abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,;:\'\"-_!&#64;#$%^&amp;*()[]{}
 * </pre>
 * 
 * <pre>-max-length &lt;int&gt; (property: maxLength)
 * &nbsp;&nbsp;&nbsp;The maximum length for password strings when performing brute force attack.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-start &lt;java.lang.String&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;The starting password for the brute force attack.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-num-threads &lt;int&gt; (property: numThreads)
 * &nbsp;&nbsp;&nbsp;The number of threads to use for parallel execution; &gt; 0: specific number 
 * &nbsp;&nbsp;&nbsp;of cores to use (capped by actual number of cores available, 1 = sequential 
 * &nbsp;&nbsp;&nbsp;execution); = 0: number of cores; &lt; 0: number of free cores (eg -2 means 
 * &nbsp;&nbsp;&nbsp;2 free cores; minimum of one core is used)
 * &nbsp;&nbsp;&nbsp;default: 2
 * </pre>
 * 
 * <pre>-password &lt;adams.core.io.PlaceholderFile&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The file to store the password in (if one found).
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ZipPassword
  extends AbstractTool
  implements ThreadLimiter {

  private static final long serialVersionUID = 3018437869824414157L;

  /** the default characters. */
  public final static String DEFAULT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,;:'\"-_!@#$%^&*()[]{}";

  /**
   *
   */
  public static class BruteForceJob
    extends RunnableWithLogging {

    private static final long serialVersionUID = -4788228040850442732L;

    /** the owner. */
    protected ZipPassword m_Owner;

    /** the ID. */
    protected int m_ID;

    /** the password generator to use. */
    protected BruteForcePasswordGenerator m_Generator;

    /** the number of passwords to skip. */
    protected int m_Skip;

    /** the zip file. */
    protected File m_Zip;

    /**
     * Initializes the brute force job.
     *
     * @param owner 	the owner
     * @param id 	the ID of the job
     * @param generator	the configured password geenrator to use
     * @param skip	the number of passwords to skip when testing
     * @param zip	the file to test
     */
    public BruteForceJob(ZipPassword owner, int id, BruteForcePasswordGenerator generator, int skip, File zip) {
      super();
      m_Owner     = owner;
      m_ID        = id;
      m_Generator = generator;
      m_Skip      = skip;
      m_Zip       = zip;
      m_Logger    = null;
    }

    /**
     * Initializes the logger.
     */
    protected void configureLogger() {
      m_Logger = LoggingHelper.getLogger(getClass() + "-" + m_ID);
      m_Logger.setLevel(m_LoggingLevel.getLevel());
    }

    /**
     * Performs the actual execution.
     */
    @Override
    protected void doRun() {
      ZipFile	zipfile;
      int	count;
      String	password;
      String	tmpDir;
      int	i;

      try {
	zipfile = new ZipFile(m_Zip.getAbsolutePath());
	if (!zipfile.isEncrypted()) {
	  getLogger().warning("ZIP file is not encrypted: " + m_Zip);
	  m_Owner.outputPassword(null);
	  return;
	}
	count  = 0;
	tmpDir = TempUtils.getTempDirectoryStr();
	while (!m_Stopped && m_Generator.hasNext()) {
	  count++;
	  password = m_Generator.next();
	  try {
	    zipfile.setPassword(password);
	    zipfile.extractAll(tmpDir);
	    m_Owner.outputPassword(password);
	    return;
	  }
	  catch (Exception e) {
	    // ignored
	  }
	  if (count % 10000 == 0) {
	    getLogger().info(password);
	    count = 0;
	  }
	  for (i = 0; i < m_Skip; i++)
	    m_Generator.next();
	}
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Error processing ZIP file: " + m_Zip, e);
      }
    }
  }

  /** the zip file to use. */
  protected PlaceholderFile m_Zip;

  /** the dictionary file to use. */
  protected PlaceholderFile m_Dictionary;

  /** the characters to use for brute force. */
  protected String m_Characters;

  /** the maximum length for passwords to test. */
  protected int m_MaxLength;

  /** the starting password. */
  protected String m_Start;

  /** the number of threads to use for parallel execution. */
  protected int m_NumThreads;

  /** the file to store the determined password in (if successful). */
  protected PlaceholderFile m_Password;

  /** whether the search has terminated. */
  protected boolean m_Finished;

  /** the executor service to use for parallel execution. */
  protected PausableFixedThreadPoolExecutor m_Executor;

  /** the brute force jobs. */
  protected List<BruteForceJob> m_Jobs;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Attempts to determine the password of a password protected ZIP file.\n"
	+ "If no dictionary file has been provided, a brute force attack is carried out.\n"
	+ "The brute force attack can be run in parallel, default is two threads.\n"
	+ "The dictionary approach also tests lower/upper case version of the "
	+ "passwords and the reverse of them.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "zip", "zip",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "dictionary", "dictionary",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "chars", "characters",
      DEFAULT_CHARS);

    m_OptionManager.add(
      "max-length", "maxLength",
      10, 1, null);

    m_OptionManager.add(
      "start", "start",
      "");

    m_OptionManager.add(
      "num-threads", "numThreads",
      2);

    m_OptionManager.add(
      "password", "password",
      new PlaceholderFile("."));
  }

  /**
   * Sets the ZIP file to use.
   *
   * @param value	the zip file
   */
  public void setZip(PlaceholderFile value) {
    m_Zip = value;
    reset();
  }

  /**
   * Returns the ZIP file to use.
   *
   * @return 		the zip file
   */
  public PlaceholderFile getZip() {
    return m_Zip;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String zipTipText() {
    return "The ZIP file to process.";
  }

  /**
   * Sets the dictionary file to use.
   *
   * @param value	the dictionary file
   */
  public void setDictionary(PlaceholderFile value) {
    m_Dictionary = value;
    reset();
  }

  /**
   * Returns the dictionary file to use.
   *
   * @return 		the dictionary file
   */
  public PlaceholderFile getDictionary() {
    return m_Dictionary;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String dictionaryTipText() {
    return "The dictionary file to process.";
  }

  /**
   * Sets the characters to use for brute force attack.
   *
   * @param value	the characters
   */
  public void setCharacters(String value) {
    m_Characters = value;
    reset();
  }

  /**
   * Returns the characters to use for brute force attack.
   *
   * @return 		the characters
   */
  public String getCharacters() {
    return m_Characters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *			displaying in the GUI or for listing the options.
   */
  public String charactersTipText() {
    return "The characters to use for brute force attack.";
  }

  /**
   * Sets the maximum length of password string to generate for brute force
   * attack.
   *
   * @param value	the maximum length
   */
  public void setMaxLength(int value) {
    m_MaxLength = value;
    reset();
  }

  /**
   * Returns the maximum length of password string to generate for brute force
   * attack.
   *
   * @return 		the maximum length
   */
  public int getMaxLength() {
    return m_MaxLength;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *			displaying in the GUI or for listing the options.
   */
  public String maxLengthTipText() {
    return "The maximum length for password strings when performing brute force attack.";
  }

  /**
   * Sets the starting password for the brute force attack.
   *
   * @param value	the starting password
   */
  public void setStart(String value) {
    m_Start = value;
    reset();
  }

  /**
   * Returns the starting password for the brute force attack.
   *
   * @return 		the starting password
   */
  public String getStart() {
    return m_Start;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *			displaying in the GUI or for listing the options.
   */
  public String startTipText() {
    return "The starting password for the brute force attack.";
  }

  /**
   * Sets the number of threads to use for executing the branches.
   *
   * @param value 	the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  public void setNumThreads(int value) {
    m_NumThreads = value;
    reset();
  }

  /**
   * Returns the number of threads to use for executing the branches.
   *
   * @return 		the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  public int getNumThreads() {
    return m_NumThreads;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numThreadsTipText() {
    return Performance.getNumThreadsHelp();
  }

  /**
   * Sets the file for outputting the password.
   *
   * @param value	the password file
   */
  public void setPassword(PlaceholderFile value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the file for outputting the password.
   *
   * @return 		the password file
   */
  public PlaceholderFile getPassword() {
    return m_Password;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String passwordTipText() {
    return "The file to store the password in (if one found).";
  }

  /**
   * Before the actual run is executed. Default implementation only resets the stopped flag.
   */
  @Override
  protected void preRun() {
    super.preRun();

    if (!m_Zip.exists())
      throw new IllegalArgumentException("ZIP file does not exist: " + m_Zip);
    if (m_Zip.isDirectory())
      throw new IllegalArgumentException("ZIP file points to directory: " + m_Zip);
  }

  /**
   * Performs brute force attack.
   */
  protected void doRunBruteForce() {
    BruteForcePasswordGenerator generator;
    int				numThreads;
    int				i;
    int				n;
    BruteForceJob		job;

    numThreads = Performance.determineNumThreads(m_NumThreads);
    m_Executor = new PausableFixedThreadPoolExecutor(numThreads);
    m_Jobs     = new ArrayList<>();
    for (i = 0; i < numThreads; i++) {
      generator = new BruteForcePasswordGenerator(m_Characters, m_MaxLength, m_Start.isEmpty() ? null : m_Start);
      // offset generators
      for (n = 0; n < i; n++)
	generator.next();
      job = new BruteForceJob(this, i, generator, numThreads - 1, m_Zip);
      job.setLoggingLevel(getLoggingLevel());
      m_Jobs.add(job);
    }

    for (BruteForceJob j: m_Jobs)
      m_Executor.submit(j);

    while (!m_Stopped && !m_Executor.isTerminated()) {
      Utils.wait(this, this, 1000, 1000);
    }
  }

  /**
   * Uses dictionary for attak.
   */
  protected void doRunDictionary() {
    ZipFile 		zipfile;
    List<String> 	passwords;
    String[]		variations;
    int			count;
    String		tmpDir;

    try {
      zipfile   = new ZipFile(m_Zip.getAbsolutePath());
      if (!zipfile.isEncrypted()) {
	getLogger().warning("ZIP file is not encrypted: " + m_Zip);
	outputPassword(null);
	return;
      }
      tmpDir     = TempUtils.getTempDirectoryStr();
      passwords  = FileUtils.loadFromFile(m_Dictionary);
      getLogger().info("");
      variations = new String[6];
      count      = 0;
      for (String password : passwords) {
	if (m_Stopped) {
	  getLogger().severe("Interrupted!");
	  outputPassword(null);
	  return;
	}
	count++;
	variations[0] = password;
	variations[1] = variations[0].toLowerCase();
	variations[2] = variations[0].toUpperCase();
	variations[3] = new StringBuilder(password).reverse().toString();
	variations[4] = variations[3].toLowerCase();
	variations[5] = variations[3].toUpperCase();
	for (String variation: variations) {
	  try {
	    zipfile.setPassword(variation);
	    zipfile.extractAll(tmpDir);
	    outputPassword(variation);
	    return;
	  }
	  catch (Exception e) {
	    // ignored
	  }
	}
	if (count % 10000 == 0) {
	  getLogger().info(password);
	  count = 0;
	}
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Error accessing ZIP file: " + m_Zip, e);
    }
  }

  /**
   * Outputs the password.
   *
   * @param password	the password, null if failed to determine
   */
  protected synchronized void outputPassword(String password) {
    if (m_Finished)
      return;

    m_Finished = true;

    stopExecution();

    if (password == null) {
      getLogger().severe("Failed to determine password!");
    }
    else {
      if (m_Password.isDirectory())
	System.out.println(password);
      else
	FileUtils.writeToFile(m_Password.getAbsolutePath(), password, false);
    }
  }

  /**
   * Contains the actual run code.
   */
  @Override
  protected void doRun() {
    m_Finished = false;
    if (!m_Dictionary.exists() || m_Dictionary.isDirectory())
      doRunBruteForce();
    else
      doRunDictionary();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    if (m_Executor != null) {
      for (BruteForceJob job: m_Jobs)
	job.stopExecution();
      try {
	if (m_Executor.isPaused())
	  m_Executor.resumeExecution();
	m_Executor.shutdown();
      }
      catch (Exception e) {
	// ignored
      }
      m_Executor = null;
    }
  }

  /**
   * Runs the tool from commandline.
   *
   * @param args	the parameters
   * @throws Exception	if anything goes wrong
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    ZipPassword tool = (ZipPassword) forName(ZipPassword.class.getName(), args);
    tool.run();
  }
}
