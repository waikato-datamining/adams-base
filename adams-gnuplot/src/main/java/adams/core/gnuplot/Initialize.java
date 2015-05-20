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
 * Initialize.java
 * Copyright (C) 2011-2105 University of Waikato, Hamilton, New Zealand
 */
package adams.core.gnuplot;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Initializes the plotting.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-terminal &lt;java.lang.String&gt; (property: terminal)
 * &nbsp;&nbsp;&nbsp;The terminal to use: e.g., 'x11', 'gif', 'png', 'svg'; for any terminal 
 * &nbsp;&nbsp;&nbsp;type apart from 'x11', an output file needs to be supplied.
 * &nbsp;&nbsp;&nbsp;default: x11
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The output file to use if terminal other than 'x11' is used.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-title &lt;java.lang.String&gt; (property: title)
 * &nbsp;&nbsp;&nbsp;The title to use for the plot.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-x-label &lt;java.lang.String&gt; (property: XLabel)
 * &nbsp;&nbsp;&nbsp;The title for the X axis; gets ignored if empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-y-label &lt;java.lang.String&gt; (property: YLabel)
 * &nbsp;&nbsp;&nbsp;The title for the Y axis; gets ignored if empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-z-label &lt;java.lang.String&gt; (property: ZLabel)
 * &nbsp;&nbsp;&nbsp;The title for the Z axis; gets ignored if empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Initialize
  extends AbstractScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = -3540923217777778401L;

  /** the 'x11' terminal. */
  public final static String TERMINAL_X11 = "x11";

  /** the terminal to use. */
  protected String m_Terminal;

  /** the output file if not 'x11' as terminal. */
  protected PlaceholderFile m_OutputFile;

  /** the plot title. */
  protected String m_Title;

  /** the title of the X axis. */
  protected String m_XLabel;

  /** the title of the Y axis. */
  protected String m_YLabel;

  /** the title of the Z axis. */
  protected String m_ZLabel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Initializes the plotting.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "terminal", "terminal",
	    TERMINAL_X11);

    m_OptionManager.add(
	    "output", "outputFile",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "title", "title",
	    "");

    m_OptionManager.add(
	    "x-label", "XLabel",
	    "");

    m_OptionManager.add(
	    "y-label", "YLabel",
	    "");

    m_OptionManager.add(
	    "z-label", "ZLabel",
	    "");
  }

  /**
   * Sets the terminal to use.
   *
   * @param value	the terminal type
   */
  public void setTerminal(String value) {
    m_Terminal = value;
    reset();
  }

  /**
   * Returns the terminal to use.
   *
   * @return		the terminal type
   */
  public String getTerminal() {
    return m_Terminal;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String terminalTipText() {
    return
        "The terminal to use: e.g., '" + TERMINAL_X11 + "', 'gif', 'png', "
      + "'svg'; for any terminal type apart from '" + TERMINAL_X11 + "', an "
      + "output file needs to be supplied.";
  }

  /**
   * Sets the output file to use (if not x11 as terminal).
   *
   * @param value	the output file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file in use (if not x11 as terminal).
   *
   * @return		the output file
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String outputFileTipText() {
    return "The output file to use if terminal other than '" + TERMINAL_X11 + "' is used.";
  }

  /**
   * Sets the title to use.
   *
   * @param value	the title type
   */
  public void setTitle(String value) {
    m_Title = value;
    reset();
  }

  /**
   * Returns the title to use.
   *
   * @return		the title type
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String titleTipText() {
    return "The title to use for the plot.";
  }

  /**
   * Sets the title for the X axis.
   *
   * @param value	the title
   */
  public void setXLabel(String value) {
    m_XLabel = value;
    reset();
  }

  /**
   * Returns the title for the X axis.
   *
   * @return		the title
   */
  public String getXLabel() {
    return m_XLabel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String XLabelTipText() {
    return "The title for the X axis; gets ignored if empty.";
  }

  /**
   * Sets the title for the Y axis.
   *
   * @param value	the title
   */
  public void setYLabel(String value) {
    m_YLabel = value;
    reset();
  }

  /**
   * Returns the title for the Y axis.
   *
   * @return		the title
   */
  public String getYLabel() {
    return m_YLabel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String YLabelTipText() {
    return "The title for the Y axis; gets ignored if empty.";
  }

  /**
   * Sets the title for the Z axis.
   *
   * @param value	the title
   */
  public void setZLabel(String value) {
    m_ZLabel = value;
    reset();
  }

  /**
   * Returns the title for the Z axis.
   *
   * @return		the title
   */
  public String getZLabel() {
    return m_ZLabel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String ZLabelTipText() {
    return "The title for the Z axis; gets ignored if empty.";
  }

  /**
   * Hook method for performing checks.
   * <p/>
   * Makes sure that the output file (if terminal is not x11) is not a directory.
   *
   * @return		null if all checks passed, otherwise error message
   */
  public String check() {
    String	result;

    result = super.check();

    if (result == null) {
      if (!m_Terminal.equals(TERMINAL_X11)) {
	if (!m_OutputFile.isDirectory())
	  result = "Output '" + m_OutputFile + "' is pointing to a directory!";
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

    result = new StringBuilder();
    result.append("set term " + m_Terminal + "\n");
    if (!m_Terminal.equals(TERMINAL_X11))
      result.append("set output \"" + m_OutputFile.getAbsolutePath() + "\"\n");
    if (m_Title.length() > 0)
      result.append("set title \"" + Utils.backQuoteChars(m_Title) + "\"\n");
    if (m_XLabel.length() > 0)
      result.append("set xlabel \"" + Utils.backQuoteChars(m_XLabel) + "\"\n");
    if (m_YLabel.length() > 0)
      result.append("set ylabel \"" + Utils.backQuoteChars(m_YLabel) + "\"\n");
    if (m_ZLabel.length() > 0)
      result.append("set zlabel \"" + Utils.backQuoteChars(m_ZLabel) + "\"\n");
    result.append("clear\n");

    return result.toString();
  }
}
