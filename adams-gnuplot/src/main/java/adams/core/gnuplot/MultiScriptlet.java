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
 * MultiScriptlet.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core.gnuplot;

/**
 <!-- globalinfo-start -->
 * Allows the user to chain multiple scriplets together.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-data-file &lt;adams.core.io.PlaceholderFile&gt; (property: dataFile)
 * &nbsp;&nbsp;&nbsp;The data file to use as basis for the plot.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-use-absolute-path &lt;boolean&gt; (property: useAbsolutePath)
 * &nbsp;&nbsp;&nbsp;If enabled, the absolute path of the data file is used, otherwise just its 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-scriptlet &lt;adams.core.gnuplot.AbstractScriptlet&gt; [-scriptlet ...] (property: scriptlets)
 * &nbsp;&nbsp;&nbsp;The scriplets to use for producing a single script.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-use-single-data-file &lt;boolean&gt; (property: useSingleDataFile)
 * &nbsp;&nbsp;&nbsp;If enabled, all sub-scriptlets get automatically updated to use this scriptlets 
 * &nbsp;&nbsp;&nbsp;data file.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiScriptlet
  extends AbstractScriptletWithDataFile {

  /** for serialization. */
  private static final long serialVersionUID = 6639840731369734498L;

  /** the custom script code. */
  protected AbstractScriptlet[] m_Scriptlets;

  /** whether to enforce all scriptlets to use the same data file. */
  protected boolean m_UseSingleDataFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Allows the user to chain multiple scriplets together.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "scriptlet", "scriptlets",
	    new AbstractScriptlet[0]);

    m_OptionManager.add(
	    "use-single-data-file", "useSingleDataFile",
	    false);
  }

  /**
   * Sets the scriptlets to use.
   *
   * @param value	the scriptlets
   */
  public void setScriptlets(AbstractScriptlet[] value) {
    m_Scriptlets = value;
    for (AbstractScriptlet scriptlet: m_Scriptlets)
      scriptlet.setOwner(getOwner());
    reset();
  }

  /**
   * Returns the scriptlets in use.
   *
   * @return		the scriptlets
   */
  public AbstractScriptlet[] getScriptlets() {
    return m_Scriptlets;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String scriptletsTipText() {
    return "The scriplets to use for producing a single script.";
  }

  /**
   * Sets whether to use a single data file only.
   *
   * @param value	if true only a single data file is used
   */
  public void setUseSingleDataFile(boolean value) {
    m_UseSingleDataFile = value;
    reset();
  }

  /**
   * Returns whether to use a single data file only.
   *
   * @return		true if only a single data file is used
   */
  public boolean getUseSingleDataFile() {
    return m_UseSingleDataFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String useSingleDataFileTipText() {
    return "If enabled, all sub-scriptlets get automatically updated to use this scriptlets data file.";
  }

  /**
   * Hook method for performing checks.
   * <p/>
   * Calls the check() methods of all scriptlets.
   *
   * @return		null if all checks passed, otherwise error message
   */
  public String check() {
    String	result;
    int		i;

    result = super.check();

    if (result == null) {
      if (m_UseSingleDataFile) {
	for (i = 0; i < m_Scriptlets.length; i++) {
          if (m_Scriptlets[i] instanceof AbstractScriptletWithDataFile)
            ((AbstractScriptletWithDataFile) m_Scriptlets[i]).setDataFile(getDataFile());
        }
      }

      for (i = 0; i < m_Scriptlets.length; i++) {
	result = m_Scriptlets[i].check();
	if (result != null)
	  break;
      }
    }

    return result;
  }

  /**
   * Generates the actual script code.
   *
   * @return		the script code, null in case of an error
   */
  protected String doGenerate() {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();

    for (i = 0; i < m_Scriptlets.length; i++) {
      result.append(COMMENT + " " + m_Scriptlets[i].getClass().getName() + "\n");
      result.append(m_Scriptlets[i].generate());
      result.append("\n");
    }

    return result.toString();
  }
}
