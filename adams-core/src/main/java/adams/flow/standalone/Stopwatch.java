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
 * Stopwatch.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import java.awt.FlowLayout;
import java.awt.Font;
import java.io.Serializable;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import adams.core.QuickInfoHelper;
import adams.flow.core.AbstractDisplay;
import adams.gui.core.BasePanel;

/**
 <!-- globalinfo-start -->
 * Stops the execution time of the flow, till either the user stops the flow or the flow finishes by itself
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Stopwatch
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 56
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -3
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-update-interval &lt;int&gt; (property: updateInterval)
 * &nbsp;&nbsp;&nbsp;The wait period in milli-seconds before the stopwatch display is being updated.
 * &nbsp;&nbsp;&nbsp;default: 500
 * &nbsp;&nbsp;&nbsp;minimum: 10
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix text for the time display.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-suffix &lt;java.lang.String&gt; (property: suffix)
 * &nbsp;&nbsp;&nbsp;The suffix text for the time display.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-show-seconds (property: showSeconds)
 * &nbsp;&nbsp;&nbsp;If enabled, the seconds are displayed in the stopwatch output as well.
 * </pre>
 *
 * <pre>-font &lt;java.awt.Font&gt; (property: font)
 * &nbsp;&nbsp;&nbsp;The font to use for displaying the time.
 * &nbsp;&nbsp;&nbsp;default: monospaced-PLAIN-16
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Stopwatch
  extends AbstractDisplay {

  /** for serialization. */
  private static final long serialVersionUID = -6030616525436078513L;

  /**
   * Timer thread class.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Timer
    implements Runnable, Serializable {

    /** for serialization. */
    private static final long serialVersionUID = -9020703905185523399L;

    /** the owning panel. */
    protected StopwatchPanel m_Owner;

    /** whether the thread is running. */
    protected boolean m_Running;

    /**
     * Initializes the timer.
     *
     * @param owner	the panel this timer belongs to
     */
    public Timer(StopwatchPanel owner) {
      super();
      m_Owner = owner;
    }

    /**
     * Runs the timer.
     */
    public void run() {
      m_Running = true;
      while (m_Running) {
	try {
	  synchronized(this) {
	    wait(m_Owner.getOwner().getUpdateInterval());
	  }
	  if (m_Running) {
	    Runnable run = new Runnable() {
	      public void run() {
		m_Owner.updateTime(System.currentTimeMillis() - m_Owner.getStartTime());
	      }
	    };
	    SwingUtilities.invokeLater(run);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    /**
     * Stops the timer.
     */
    public void stop() {
      m_Running = false;
      synchronized(this) {
	notifyAll();
      }
    }
  }

  /**
   * Panel for displaying a stopwatch.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class StopwatchPanel
    extends BasePanel {

    /** for serialization. */
    private static final long serialVersionUID = 1725406518546756466L;

    /** the owning Stopwatch actor. */
    protected Stopwatch m_Owner;

    /** the label for displaying the time. */
    protected JLabel m_LabelTime;

    /** the start time. */
    protected long m_StartTime;

    /** the actual timer thread. */
    protected Timer m_Timer;

    /**
     * Initializes the panel.
     *
     * @param owner	the owning Stopwatch actor
     */
    public StopwatchPanel(Stopwatch owner) {
      super();
      m_Owner = owner;
      setTimeFont(getOwner().getFont());
      updateTime(0);
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      setLayout(new FlowLayout(FlowLayout.RIGHT));

      m_LabelTime = new JLabel();
      add(m_LabelTime);
    }

    /**
     * Returns the owning actor.
     *
     * @return		the actor
     */
    public Stopwatch getOwner() {
      return m_Owner;
    }

    /**
     * Sets the font for the time display.
     *
     * @param value	the font to use
     */
    public void setTimeFont(Font value) {
      m_LabelTime.setFont(value);
    }

    /**
     * Returns the font of the time display.
     *
     * @return		the font in use
     */
    public Font getTimeFont() {
      return m_LabelTime.getFont();
    }

    /**
     * Updates the time display with the given time
     */
    public void updateTime(long time) {
      long		secs;
      long		mins;
      long		hours;
      StringBuilder	text;

      time  = time / 1000;
      hours = time / 3600;
      time  = time % 3600;
      mins  = time / 60;
      time  = time % 60;
      secs  = time;

      text = new StringBuilder();
      text.append(getOwner().getPrefix());
      text.append(hours);
      text.append(":");
      if (mins < 10)
	text.append(0);
      text.append(mins);
      if (getOwner().getShowSeconds()) {
	text.append(":");
	if (secs < 10)
	  text.append(0);
	text.append(secs);
      }
      text.append(getOwner().getSuffix());

      m_LabelTime.setText(text.toString());
    }

    /**
     * Returns the start time of the timer.
     *
     * @return		the start time in msecs
     * @see		System#currentTimeMillis()
     */
    public long getStartTime() {
      return m_StartTime;
    }

    /**
     * Starts the timer.
     */
    public void startTimer() {
      if (m_Timer == null) {
	resetTimer();
	m_Timer = new Timer(this);
	new Thread(m_Timer).start();
      }
    }

    /**
     * Stops the timer.
     */
    public void stopTimer() {
      if (m_Timer != null) {
	m_Timer.stop();
	m_Timer = null;
      }
    }

    /**
     * Resets the timer.
     */
    public void resetTimer() {
      m_StartTime = System.currentTimeMillis();
    }
  }

  /** the update interval in msec. */
  protected int m_UpdateInterval;

  /** the text to prefix the time. */
  protected String m_Prefix;

  /** the text to suffix the time. */
  protected String m_Suffix;

  /** whether to show the seconds as well. */
  protected boolean m_ShowSeconds;

  /** the font for the display. */
  protected Font m_Font;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Stops the execution time of the flow, till either the user stops the "
      + "flow or the flow finishes by itself";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "update-interval", "updateInterval",
	    500, 10, null);

    m_OptionManager.add(
	    "prefix", "prefix",
	    "");

    m_OptionManager.add(
	    "suffix", "suffix",
	    "");

    m_OptionManager.add(
	    "show-seconds", "showSeconds",
	    false);

    m_OptionManager.add(
	    "font", "font",
	    new Font("monospaced", Font.PLAIN, 16));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;
    
    result  = "\"";
    value = QuickInfoHelper.toString(this, "prefix", m_Prefix);
    if (value != null)
      result += result;
    result += "0:00";
    result += QuickInfoHelper.toString(this, "showSeconds", m_ShowSeconds, ":00");
    value = QuickInfoHelper.toString(this, "suffix", m_Suffix);
    if (value != null)
      result += result;
    result += "\", updated every ";
    result += QuickInfoHelper.toString(this, "updateInterval", m_UpdateInterval);
    result += " msecs";

    return result;
  }

  /**
   * Returns the default X position for the dialog.
   *
   * @return		the default X position
   */
  @Override
  protected int getDefaultX() {
    return -3;
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 100;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 56;
  }

  /**
   * Sets the prefix for the time display.
   *
   * @param value	the prefix string
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix for the time display.
   *
   * @return		the prefix string
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix text for the time display.";
  }

  /**
   * Sets the suffix for the time display.
   *
   * @param value	the suffix string
   */
  public void setSuffix(String value) {
    m_Suffix = value;
    reset();
  }

  /**
   * Returns the suffix for the time display.
   *
   * @return		the suffix string
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
    return "The suffix text for the time display.";
  }

  /**
   * Sets whether to display seconds as well.
   *
   * @param value	if true then seconds are displayed as well
   */
  public void setShowSeconds(boolean value) {
    m_ShowSeconds = value;
    reset();
  }

  /**
   * Returns whether to display seconds as well.
   *
   * @return		true if seconds are displayed as well
   */
  public boolean getShowSeconds() {
    return m_ShowSeconds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showSecondsTipText() {
    return "If enabled, the seconds are displayed in the stopwatch output as well.";
  }

  /**
   * Sets the wait period before the display is updated.
   *
   * @param value	interval in msecs
   */
  public void setUpdateInterval(int value) {
    m_UpdateInterval = value;
    reset();
  }

  /**
   * Returns the wait period before updating the display.
   *
   * @return		interval in msecs
   */
  public int getUpdateInterval() {
    return m_UpdateInterval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updateIntervalTipText() {
    return "The wait period in milli-seconds before the stopwatch display is being updated.";
  }

  /**
   * Sets the font for the time display.
   *
   * @param value	the font
   */
  public void setFont(Font value) {
    m_Font = value;
    reset();
  }

  /**
   * Returns the font for the time display.
   *
   * @return		the font
   */
  public Font getFont() {
    return m_Font;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontTipText() {
    return "The font to use for displaying the time.";
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Panel != null)
      ((StopwatchPanel) m_Panel).resetTimer();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    StopwatchPanel	result;

    result = new StopwatchPanel(this);

    return result;
  }

  /**
   * Returns a runnable that displays frame, etc.
   * Must call notifyAll() on the m_Self object and set m_Updating to false.
   *
   * @return		the runnable
   */
  @Override
  protected Runnable newDisplayRunnable() {
    Runnable	result;

    result = new Runnable() {
      public void run() {
	if (m_CreateFrame && !m_Frame.isVisible())
	  m_Frame.setVisible(true);
	synchronized(m_Self) {
	  m_Self.notifyAll();
	}
	m_Updating = false;
      }
    };

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = super.doExecute();

    if (!m_Headless && (result == null))
      ((StopwatchPanel) m_Panel).startTimer();

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Panel != null)
      ((StopwatchPanel) m_Panel).stopTimer();
    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_Panel != null)
      ((StopwatchPanel) m_Panel).stopTimer();
    super.wrapUp();
  }
}
