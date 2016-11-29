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
 * LogTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.MessageCollection;
import adams.core.logging.LoggingLevel;
import adams.gui.core.SimpleLogPanel;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;

import java.awt.BorderLayout;
import java.util.Map;

/**
 * Just displays the log messages.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogTab
  extends AbstractInvestigatorTab {

  private static final long serialVersionUID = -94945456385486233L;

  public static final String KEY_LOG = "log";

  /** the log panel. */
  protected SimpleLogPanel m_LogPanel;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_ContentPanel.setLayout(new BorderLayout());

    m_LogPanel = new SimpleLogPanel();
    m_ContentPanel.add(m_LogPanel, BorderLayout.CENTER);
  }

  /**
   * Sets the owner for this tab.
   *
   * @param value	the owner
   */
  @Override
  public void setOwner(InvestigatorPanel value) {
    super.setOwner(value);
    m_LogPanel.setText(value.getLog().toString());
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Log";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "log.gif";
  }

  /**
   * Clears the log.
   */
  public void clearLog() {
    m_LogPanel.clear();
  }

  /**
   * Appends the message to the log.
   *
   * @param msg		the message
   */
  public void append(String msg) {
    m_LogPanel.append(LoggingLevel.INFO, msg);
  }

  /**
   * Notifies the tab that the data changed.
   *
   * @param e		the event
   */
  public void dataChanged(WekaInvestigatorDataEvent e) {
  }

  /**
   * Returns the objects for serialization.
   *
   * @return		the mapping of the objects to serialize
   */
  protected Map<String,Object> doSerialize() {
    Map<String,Object>	result;

    result = super.doSerialize();
    result.put(KEY_LOG, m_LogPanel.getText());

    return result;
  }

  /**
   * Restores the objects.
   *
   * @param data	the data to restore
   * @param errors	for storing errors
   */
  protected void doDeserialize(Map<String,Object> data, MessageCollection errors) {
    super.doDeserialize(data, errors);
    if (data.containsKey(KEY_LOG))
      m_LogPanel.setText((String) data.get(KEY_LOG));
  }
}
