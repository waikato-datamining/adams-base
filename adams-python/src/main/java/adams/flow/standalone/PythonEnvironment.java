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
 * PythonEnvironment.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;

import java.io.File;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Defines what Python executables to use within this context.<br>
 * On Linux, you can enforce using the system-wide Python 3 executables by supplying '3' as suffix, which gets appended to the executables.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: PythonEnvironment
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
 * <pre>-python-path-env-var &lt;adams.core.base.BaseString&gt; [-python-path-env-var ...] (property: pythonPathEnvVar)
 * &nbsp;&nbsp;&nbsp;The paths to use for the PYTHONPATH environment variable.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-python-path-update-type &lt;NO_UPDATE|REPLACE|APPEND|PREPEND&gt; (property: pythonPathUpdateType)
 * &nbsp;&nbsp;&nbsp;Determines how to update the PYTHONPATH environment variable.
 * &nbsp;&nbsp;&nbsp;default: NO_UPDATE
 * </pre>
 *
 * <pre>-python &lt;adams.core.io.PlaceholderFile&gt; (property: python)
 * &nbsp;&nbsp;&nbsp;The python executable, uses one on path if pointing to a directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-pip &lt;adams.core.io.PlaceholderFile&gt; (property: pip)
 * &nbsp;&nbsp;&nbsp;The pip executable, uses one on path if pointing to a directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-suffix &lt;java.lang.String&gt; (property: suffix)
 * &nbsp;&nbsp;&nbsp;The suffix (not extension!) to append to the executable when using the system-wide
 * &nbsp;&nbsp;&nbsp;ones; eg on Linux use '3' to enforce Python 3.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PythonEnvironment
  extends AbstractStandalone {

  private static final long serialVersionUID = 5148275104228911234L;

  /**
   * How to update the environment varibale.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum PythonPathUpdateType {
    /** does not change the environment variable. */
    NO_UPDATE,
    /** replaces the current path. */
    REPLACE,
    /** appends the path to the existing one. */
    APPEND,
    /** prepends the path to the existing pne. */
    PREPEND,
  }

  /** the placeholder for the environment variable. */
  public final static String PYTHONPATH = "PYTHONPATH";

  /** the PYTHONPATH environment variable. */
  protected BaseString[] m_PythonPathEnvVar;

  /** how to update the PYTHONPATH environment variable. */
  protected PythonPathUpdateType m_PythonPathUpdateType;

  /** the actual python path env var (null if not to use). */
  protected String m_ActualPythonPath;

  /** the python executable. */
  protected PlaceholderFile m_Python;

  /** the actual python executable. */
  protected String m_ActualPython;

  /** the pip executable. */
  protected PlaceholderFile m_Pip;

  /** the actual pip executable. */
  protected String m_ActualPip;

  /** the suffix to append to the executable. */
  protected String m_Suffix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defines what Python executables to use within this context.\n"
      + "On Linux, you can enforce using the system-wide Python 3 executables "
      + "by supplying '3' as suffix, which gets appended to the executables.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "python-path-env-var", "pythonPathEnvVar",
      new BaseString[0]);

    m_OptionManager.add(
      "python-path-update-type", "pythonPathUpdateType",
      PythonPathUpdateType.NO_UPDATE);

    m_OptionManager.add(
      "python", "python",
      new PlaceholderFile());

    m_OptionManager.add(
      "pip", "pip",
      new PlaceholderFile());

    m_OptionManager.add(
      "suffix", "suffix",
      "");
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualPythonPath = null;
    m_ActualPython     = null;
    m_ActualPip        = null;
  }

  /**
   * Sets the path(s) for the PYTHONPATH environment variable.
   *
   * @param value 	the path(s)
   */
  public void setPythonPathEnvVar(BaseString[] value) {
    m_PythonPathEnvVar = value;
    reset();
  }

  /**
   * Returns the path(s) for the PYTHONPATH environment variable.
   *
   * @return 		the path(s)
   */
  public BaseString[] getPythonPathEnvVar() {
    return m_PythonPathEnvVar;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pythonPathEnvVarTipText() {
    return "The paths to use for the " + PYTHONPATH + " environment variable.";
  }

  /**
   * Sets how to update the PYTHONPATH environment variable.
   *
   * @param value 	the update type
   */
  public void setPythonPathUpdateType(PythonPathUpdateType value) {
    m_PythonPathUpdateType = value;
    reset();
  }

  /**
   * Returns how to update the PYTHONPATH environment variable.
   *
   * @return 		the update type
   */
  public PythonPathUpdateType getPythonPathUpdateType() {
    return m_PythonPathUpdateType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pythonPathUpdateTypeTipText() {
    return "Determines how to update the " + PYTHONPATH + " environment variable.";
  }

  /**
   * Sets the python executable, uses one on path if pointing to directory.
   *
   * @param value 	the executable
   */
  public void setPython(PlaceholderFile value) {
    m_Python = value;
    reset();
  }

  /**
   * Returns the python executable, uses one on path if pointing to directory.
   *
   * @return 		the executable
   */
  public PlaceholderFile getPython() {
    return m_Python;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pythonTipText() {
    return "The python executable, uses one on path if pointing to a directory.";
  }

  /**
   * Sets the pip executable, uses one on path if pointing to directory.
   *
   * @param value 	the executable
   */
  public void setPip(PlaceholderFile value) {
    m_Pip = value;
    reset();
  }

  /**
   * Returns the pip executable, uses one on path if pointing to directory.
   *
   * @return 		the executable
   */
  public PlaceholderFile getPip() {
    return m_Pip;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pipTipText() {
    return "The pip executable, uses one on path if pointing to a directory.";
  }

  /**
   * Sets the suffix to append to the system-wide executables.
   *
   * @param value 	the suffix
   */
  public void setSuffix(String value) {
    m_Suffix = value;
    reset();
  }

  /**
   * Returns the suffix to append to the system-wide executables.
   *
   * @return 		the suffix
   */
  public String getSuffix() {
    return m_Suffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suffixTipText() {
    return "The suffix (not extension!) to append to the executable when "
      + "using the system-wide ones; eg on Linux use '3' to enforce Python 3.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "python", (m_Python.isDirectory() ? "-system-" : m_Python.toString()), "python: ");
    result += QuickInfoHelper.toString(this, "pip", (m_Pip.isDirectory() ? "-system-" : m_Pip.toString()), ", pip: ");
    result += QuickInfoHelper.toString(this, "suffix", (m_Suffix.isEmpty() ? "-none-" : m_Suffix), ", suffix: ");
    if (m_PythonPathUpdateType != PythonPathUpdateType.NO_UPDATE) {
      result += QuickInfoHelper.toString(this, "pythonPathUpdateType", m_PythonPathUpdateType, ", ");
      result += QuickInfoHelper.toString(this, "pythonPathEnvVar", (m_PythonPathEnvVar.length == 0 ? "-none-" : m_PythonPathEnvVar), ": ");
    }

    return result;
  }

  /**
   * Returns the actual PYTHONPATH environment variable.
   *
   * @return		the variable, null if not yet configured or not to be used
   */
  public String getActualPythonPath() {
    return m_ActualPythonPath;
  }

  /**
   * Updates the PYTHONPATH value in the environment if necessary.
   *
   * @param env		the environment to update
   */
  public void updatePythonPath(Map<String,String> env) {
    if (m_ActualPythonPath != null)
      env.put(PYTHONPATH, m_ActualPythonPath);
  }

  /**
   * Returns the actual python executable.
   * 
   * @return		the executable, null if not yet configured
   */
  public String getActualPython() {
    return m_ActualPython;
  }

  /**
   * Returns the actual pip executable.
   * 
   * @return		the executable, null if not yet configured
   */
  public String getActualPip() {
    return m_ActualPip;
  }

  /**
   * Returns the directory with the binaries, uses either the parent directory
   * of the actual python or pip executable to determine that.
   *
   * @return		the binary dir
   */
  public String getActualBinDir() {
    if (m_ActualPython != null)
      return new File(m_ActualPython).getParentFile().getAbsolutePath();
    else if (m_ActualPip != null)
      return new File(m_ActualPip).getParentFile().getAbsolutePath();
    else
      return null;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String	path;
    String	currPath;

    result = null;

    // python path
    if (m_PythonPathEnvVar.length > 0)
      path = Utils.flatten(m_PythonPathEnvVar, File.pathSeparator);
    else
      path = "";
    currPath = System.getenv(PYTHONPATH);
    switch (m_PythonPathUpdateType) {
      case NO_UPDATE:
        m_ActualPythonPath = null;
        break;
      case REPLACE:
        m_ActualPythonPath = path;
        break;
      case APPEND:
        if ((currPath == null) || currPath.isEmpty())
          m_ActualPythonPath = path;
        else
          m_ActualPythonPath = currPath + File.pathSeparator + path;
        break;
      case PREPEND:
        if ((currPath == null) || currPath.isEmpty())
          m_ActualPythonPath = path;
        else
          m_ActualPythonPath = path + File.pathSeparator + currPath;
        break;
      default:
        throw new IllegalStateException("Unhandled python path update type: " + m_PythonPathUpdateType);
    }

    // python
    if (m_Python.isDirectory()) {
      m_ActualPython = FileUtils.fixExecutable("python" + m_Suffix);
    }
    else {
      if (!m_Python.exists())
        result = "Python does not exist: " + m_Python;
      else
        m_ActualPython = FileUtils.fixExecutable(m_Python.getAbsolutePath());
    }

    // pip
    if (result == null) {
      if (m_Pip.isDirectory()) {
	m_ActualPip = FileUtils.fixExecutable("pip" + m_Suffix);
      }
      else {
	if (!m_Pip.exists())
	  result = "Pip does not exist: " + m_Pip;
	else
	  m_ActualPip = FileUtils.fixExecutable(m_Pip.getAbsolutePath());
      }
    }

    if (result == null) {
      if (isLoggingEnabled()) {
        getLogger().info("Python: " + m_ActualPython);
        getLogger().info("Pip: " + m_ActualPip);
      }
    }

    return result;
  }
}
