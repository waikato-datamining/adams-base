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
 * Sleep.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.flow.core.ControlActor;
import adams.flow.core.Unknown;
import adams.flow.transformer.AbstractTransformer;

/**
 <!-- globalinfo-start -->
 * Waits for a fixed amount of milliseconds.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Sleep
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-interval &lt;int&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The interval in milliseconds to wait before continuing with the execution;
 * &nbsp;&nbsp;&nbsp; use &lt;= 0 for indefinite sleep.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Sleep
  extends AbstractTransformer
  implements ControlActor {

  /** for serialization. */
  private static final long serialVersionUID = -3383735680425581504L;

  /** the interval in milliseconds. */
  protected int m_Interval;

  /**
   * Default constructor.
   */
  public Sleep() {
    super();
  }

  /**
   * Initializes actor with the specified interval.
   *
   * @param interval	the interval to use
   */
  public Sleep(int interval) {
    this();
    setInterval(interval);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Waits for a fixed amount of milliseconds.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "interval", "interval",
      1000, -1, null);
  }

  /**
   * Sets the interval in milliseconds to wait.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    if (getOptionManager().isValid("interval", value)) {
      m_Interval = value;
      reset();
    }
  }

  /**
   * Returns the interval to wait in milliseconds.
   *
   * @return		the interval
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return "The interval in milliseconds to wait before continuing with the execution; use <= 0 for indefinite sleep.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "interval", (m_Interval <= 0 ? "indefinite" : (m_Interval + "ms")));
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    int		interval;
    boolean	indefinite;

    result     = null;
    indefinite = (m_Interval <= 0);
    interval   = indefinite ? 1000 : m_Interval;

    do {
      try {
	synchronized (m_Self) {
	  wait(interval);
	}
      }
      catch (InterruptedException e) {
	// ignored
      }
      catch (Exception e) {
	result = handleException("Failed to sleep:", e);
      }
    }
    while (indefinite && !isStopped());

    m_OutputToken = m_InputToken;

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    try {
      synchronized(m_Self) {
	m_Self.notifyAll();
      }
    }
    catch (Exception e) {
      // ignored
    }

    super.stopExecution();
  }
}
