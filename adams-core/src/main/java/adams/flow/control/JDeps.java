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
 * JDeps.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import adams.flow.transformer.AbstractTransformer;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Runs jdeps on the classname arriving at the input.<br>
 * The application's classpath is automatically added to the command-line if no classpath directories or jars are provided.<br>
 * Classpath directories and jars get combined, but directories take precedence over jars.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: JDeps
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-executable &lt;adams.core.io.PlaceholderFile&gt; (property: executable)
 * &nbsp;&nbsp;&nbsp;The full path to the jdeps executable.
 * </pre>
 * 
 * <pre>-classpath-dir &lt;adams.core.io.PlaceholderDirectory&gt; [-classpath-dir ...] (property: classpathDirs)
 * &nbsp;&nbsp;&nbsp;The directories to use instead of the application's classpath.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-classpath-jar &lt;adams.core.io.PlaceholderFile&gt; [-classpath-jar ...] (property: classpathJars)
 * &nbsp;&nbsp;&nbsp;The jars to use instead of the application's classpath.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-additional &lt;java.lang.String&gt; (property: additionalOptions)
 * &nbsp;&nbsp;&nbsp;Additional options for the jdeps execution, supports inline variables.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JDeps
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -4497496140953116320L;

  /** the jdeps executable. */
  protected PlaceholderFile m_Executable;

  /** the classpath directories. */
  protected PlaceholderDirectory[] m_ClasspathDirs;

  /** the classpath jars. */
  protected PlaceholderFile[] m_ClasspathJars;

  /** additional options for jdeps. */
  protected String m_AdditionalOptions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Runs jdeps on the classname arriving at the input.\n"
	+ "The application's classpath is automatically added to the command-line "
	+ "if no classpath directories or jars are provided.\n"
	+ "Classpath directories and jars get combined, but directories take "
	+ "precedence over jars.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "executable", "executable",
      new PlaceholderFile(getJDepsExecutablePath()), false);

    m_OptionManager.add(
      "classpath-dir", "classpathDirs",
      new PlaceholderDirectory[0]);

    m_OptionManager.add(
      "classpath-jar", "classpathJars",
      new PlaceholderFile[0]);

    m_OptionManager.add(
      "additional", "additionalOptions",
      "");
  }

  /**
   * Returns the full path of the JDeps executable, if possible.
   *
   * @return		the full path of the executable if possible, otherwise
   * 			just the executable
   */
  protected String getJDepsExecutablePath() {
    return Utils.unDoubleQuote(adams.core.management.JDeps.getExecutablePath());
  }

  /**
   * Sets the jdeps executable.
   *
   * @param value	the executable
   */
  public void setExecutable(PlaceholderFile value) {
    m_Executable = value;
    reset();
  }

  /**
   * Returns the jdeps executable.
   *
   * @return		the executable
   */
  public PlaceholderFile getExecutable() {
    return m_Executable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String executableTipText() {
    return "The full path to the jdeps executable.";
  }

  /**
   * Sets the classpath directories to use instead of the application's
   * classpath.
   *
   * @param value	the directories
   */
  public void setClasspathDirs(PlaceholderDirectory[] value) {
    m_ClasspathDirs = value;
    reset();
  }

  /**
   * Returns the classpath directories to use instead of the application's
   * classpath.
   *
   * @return		the directories
   */
  public PlaceholderDirectory[] getClasspathDirs() {
    return m_ClasspathDirs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classpathDirsTipText() {
    return "The directories to use instead of the application's classpath.";
  }

  /**
   * Sets the classpath jars to use instead of the application's
   * classpath.
   *
   * @param value	the jars
   */
  public void setClasspathJars(PlaceholderFile[] value) {
    m_ClasspathJars = value;
    reset();
  }

  /**
   * Returns the classpath jars to use instead of the application's
   * classpath.
   *
   * @return		the jars
   */
  public PlaceholderFile[] getClasspathJars() {
    return m_ClasspathJars;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classpathJarsTipText() {
    return "The jars to use instead of the application's classpath.";
  }

  /**
   * Sets the additional options for jdeps.
   *
   * @param value	the additional options
   */
  public void setAdditionalOptions(String value) {
    m_AdditionalOptions = value;
    reset();
  }

  /**
   * Returns the additional options for jdeps.
   *
   * @return		the additional options
   */
  public String getAdditionalOptions() {
    return m_AdditionalOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalOptionsTipText() {
    return "Additional options for the jdeps execution, supports inline variables.";
  }

  /**
   * Returns whether jdeps can be run.
   *
   * @return		true if jdeps can be executed
   */
  protected boolean canRunJDeps() {
    return FileUtils.fileExists(getJDepsExecutablePath());
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (!canRunJDeps())
	result = "Cannot run jdeps, executable not present? " + m_Executable;
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	outputStr;
    File[]	classpath;
    int		i;
    int		n;
    String	additional;

    additional = getVariables().expand(m_AdditionalOptions);

    // assemble classpath
    classpath = new File[m_ClasspathDirs.length + m_ClasspathJars.length];
    n         = 0;
    for (i = 0; i < m_ClasspathDirs.length; i++) {
      classpath[n] = m_ClasspathDirs[i];
      n++;
    }
    for (i = 0; i < m_ClasspathJars.length; i++) {
      classpath[n] = m_ClasspathJars[i];
      n++;
    }

    // execute
    outputStr = adams.core.management.JDeps.execute(
      m_Executable.getAbsolutePath(),
      classpath,
      additional + " " + m_InputToken.getPayload());

    if (isLoggingEnabled())
      getLogger().info("output: " + outputStr);

    m_OutputToken = new Token(outputStr);

    return null;
  }
}
