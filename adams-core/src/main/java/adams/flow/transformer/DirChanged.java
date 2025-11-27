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
 * DirChanged.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.io.dirchanged.DirChangeMonitor;
import adams.core.io.dirchanged.NoChange;
import adams.flow.core.Token;

import java.io.File;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DirChanged
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 1704879993786242375L;

  /** the type of monitor to use. */
  protected DirChangeMonitor m_Monitor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Checks whether the content of the input directory has changed using the specified monitor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "monitor", "monitor",
      new NoChange());
  }

  /**
   * Sets the monitor to use.
   *
   * @param value	the monitor
   */
  public void setMonitor(DirChangeMonitor value) {
    m_Monitor = value;
    reset();
  }

  /**
   * Returns the monitor in use.
   *
   * @return		the monitor
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "monitor", m_Monitor);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted input
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated output
   */
  public Class[] generates() {
    return new Class[]{Boolean.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    File	file;
    boolean	changed;

    result = null;

    file = null;
    if (m_InputToken.getPayload() instanceof String)
      file = new PlaceholderFile((String) m_InputToken.getPayload());
    else if (m_InputToken.getPayload() instanceof File)
      file = new PlaceholderFile((File) m_InputToken.getPayload());
    else
      result = "Unhandled input: " + Utils.classToString(m_InputToken.getPayload());

    if (result == null) {
      if (!m_Monitor.isInitialized(file)) {
	result = m_Monitor.initialize(file);
      }
      else {
	changed = m_Monitor.hasChanged(file);
	result = m_Monitor.update(file);
	if (result == null) {
	  m_OutputToken = new Token(changed);
	  if (isLoggingEnabled())
	    getLogger().info(m_InputToken.getPayload() + " changed? " + changed);
	}
      }
    }

    return result;
  }
}
