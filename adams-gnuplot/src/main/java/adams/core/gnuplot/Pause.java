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
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core.gnuplot;

import adams.core.Utils;

/**
 <!-- globalinfo-start -->
 * Inserts a pause statement to keep the plot open; the use needs to press the enter key in the terminal to close the plot.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-waiting-period &lt;int&gt; (property: waitingPeriod)
 * &nbsp;&nbsp;&nbsp;The waiting period in seconds before closing the plot automatically; use 
 * &nbsp;&nbsp;&nbsp;-1 to keep open indefinitely.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-message &lt;java.lang.String&gt; (property: message)
 * &nbsp;&nbsp;&nbsp;The message to output in the terminal when pausing, ignored if empty.
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
