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

import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import java.awt.BorderLayout;

/**
 * Just displays the log messages.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogTab
  extends AbstractInvestigatorTab {

  private static final long serialVersionUID = -94945456385486233L;

  /** the text area for the log. */
  protected BaseTextArea m_TextLog;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TextLog = new BaseTextArea();
    m_TextLog.setTextFont(Fonts.getMonospacedFont());
    add(new BaseScrollPane(m_TextLog), BorderLayout.CENTER);
  }

  /**
   * Sets the owner for this tab.
   *
   * @param value	the owner
   */
  @Override
  public void setOwner(InvestigatorPanel value) {
    super.setOwner(value);
    m_TextLog.setText(value.getLog().toString());
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
   * Appends the message to the log.
   *
   * @param msg		the message
   */
  public void append(String msg) {
    m_TextLog.append(msg);
    m_TextLog.append("\n");
  }

  /**
   * Notifies the tab that the data changed.
   */
  @Override
  public void dataChanged() {
  }
}
