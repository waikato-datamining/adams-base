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
 * DirMonitor.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.flowrestart.trigger;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.dirchanged.DirChangeMonitor;
import adams.core.io.dirchanged.NoChange;
import adams.flow.control.Flow;
import adams.flow.core.RunnableWithLogging;

/**
 * Monitors a dir using the specified dir change monitor.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DirMonitor
  extends AbstractTrigger {

  private static final long serialVersionUID = -1011230298891411662L;

  /** the file to monitor. */
  protected PlaceholderDirectory m_MonitoredDir;

  /** the type of monitor to use. */
  protected DirChangeMonitor m_Monitor;

  /** the interval in milli-seconds. */
  protected int m_Interval;

  /** the runnable for the monitor. */
  protected RunnableWithLogging m_Runnable;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Monitors a dir using the specified dir change monitor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "monitored-dir", "monitoredDir",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "monitor", "monitor",
      new NoChange());

    m_OptionManager.add(
      "interval", "interval",
      1000);
  }

  /**
   * Sets the dir to monitor.
   *
   * @param value	the dir
   */
  public void setMonitoredDir(PlaceholderDirectory value) {
    m_MonitoredDir = value;
    reset();
  }

  /**
   * Returns the dir to monitor.
   *
   * @return		the dir
   */
  public PlaceholderDirectory getMonitoredDir() {
    return m_MonitoredDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String monitoredDirTipText() {
    return "The directory to monitor.";
  }

  /**
   * Sets the monitor to use.
   *
   * @param value	the type of monitor
   */
  public void setMonitor(DirChangeMonitor value) {
    m_Monitor = value;
    reset();
  }

  /**
   * Returns the monitor in use.
   *
   * @return		the type of monitor
   */
  public DirChangeMonitor getMonitor() {
    return m_Monitor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String monitorTipText() {
    return "The type of monitor to use.";
  }

  /**
   * Sets the interval in milli-seconds to wait.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    m_Interval = value;
    reset();
  }

  /**
   * Returns the interval to wait in milli-seconds.
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
    return "The interval in milli-seconds to wait before checking the file.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String  result;

    result = QuickInfoHelper.toString(this, "monitor", m_Monitor);
    result += QuickInfoHelper.toString(this, "interval", m_Interval, ", ") + "ms";

    return result;
  }

  /**
   * Starts the trigger.
   *
   * @param flow	the flow to handle
   * @return		null if successfully started, otherwise error message
   */
  @Override
  protected String doStart(Flow flow) {
    m_Runnable = new RunnableWithLogging() {
      private static final long serialVersionUID = -5013228919943678201L;
      @Override
      protected void doRun() {
	String msg;
	while (!isStopped()) {
	  if (!m_Monitor.isInitialized(m_MonitoredDir)) {
	    msg = m_Monitor.initialize(m_MonitoredDir);
	    if (msg != null)
	      getLogger().warning(msg);
	  }
	  Utils.wait(this, m_Interval, 100);
	  if (m_Monitor.hasChanged(m_MonitoredDir)) {
	    if (isLoggingEnabled())
	      getLogger().info("File has changed (" + m_MonitoredDir + "), triggering!");
	    getTriggerHandler().trigger();
	  }
	  m_Monitor.update(m_MonitoredDir);
	}
      }
    };
    new Thread(m_Runnable).start();
    return null;
  }

  /**
   * Stops the trigger.
   *
   * @return		null if successfully stopped, otherwise error message
   */
  @Override
  public String stop() {
    if (m_Runnable != null) {
      m_Runnable.stopExecution();
      m_Runnable = null;
    }
    return null;
  }
}
