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
 * Pause.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.gnuplot;

import adams.core.Utils;

/**
 <!-- globalinfo-start -->
 * Pauses the plotting.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-data-file &lt;adams.core.io.PlaceholderFile&gt; (property: dataFile)
 * &nbsp;&nbsp;&nbsp;The data file to use as basis for the plot.
 * &nbsp;&nbsp;&nbsp;default: .
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
 * &nbsp;&nbsp;&nbsp;default: .
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
public class Pause
  extends AbstractScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = -3540923217777778401L;

  /** the 'x11' terminal. */
  public final static String TERMINAL_X11 = "x11";

  /** the time in seconds to wait before the plot closes. */
  protected int m_WaitingPeriod;

  /** the message to display in the terminal. */
  protected String m_Message;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Inserts a pause statement to keep the plot open; the use needs to "
      + "press the enter key in the terminal to close the plot.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "waiting-period", "waitingPeriod",
	    -1, -1, null);

    m_OptionManager.add(
	    "message", "message",
	    "");
  }

  /**
   * Sets the waiting period in seconds before closing the plot.
   *
   * @param value	the waiting period, use -1 for indefinite
   */
  public void setWaitingPeriod(int value) {
    m_WaitingPeriod = value;
    reset();
  }

  /**
   * Returns the waiting period in seconds before closing the plot.
   *
   * @return		the waiting period, -1 if indefinite
   */
  public int getWaitingPeriod() {
    return m_WaitingPeriod;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String waitingPeriodTipText() {
    return
        "The waiting period in seconds before closing the plot automatically; "
      + "use -1 to keep open indefinitely.";
  }

  /**
   * Sets the message to display in the terminal when pausing.
   *
   * @param value	the message to display
   */
  public void setMessage(String value) {
    m_Message = value;
    reset();
  }

  /**
   * Returns the message to display in the terminal when pausing.
   *
   * @return		the message to display
   */
  public String getMessage() {
    return m_Message;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String messageTipText() {
    return "The message to output in the terminal when pausing, ignored if empty.";
  }

  /**
   * Generates the actual script code.
   *
   * @return		the script code, null in case of an error
   */
  protected String doGenerate() {
    StringBuilder	result;

    result = new StringBuilder();
    result.append("pause " + m_WaitingPeriod);
    if (m_Message.length() > 0)
      result.append(" \"" + Utils.backQuoteChars(m_Message) + "\"");
    result.append("\n");

    return result.toString();
  }
}
