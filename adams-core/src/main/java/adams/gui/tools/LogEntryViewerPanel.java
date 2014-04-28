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
 * LogEntryViewerPanel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.util.List;

import adams.core.Properties;
import adams.db.LogEntry;
import adams.db.LogEntryHandler;
import adams.env.Environment;
import adams.env.LogEntryViewerPanelDefinition;
import adams.gui.dialog.TextDialog;

/**
 * Panel for displaying LogEntry records.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogEntryViewerPanel
  extends AbstractLogEntryViewerPanel {

  /** for serialization. */
  private static final long serialVersionUID = -6159575511977628201L;

  /** the name of the props file. */
  public final static String FILENAME = "LogEntryViewer.props";

  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    Properties	props;

    super.initGUI();

    props = getProperties();

    m_SplitPane.setDividerLocation(props.getInteger("DividerLocation", 250));
  }

  /**
   * Initializes the details dialog.
   *
   * @return		the dialog
   */
  protected TextDialog createDetailsDialog() {
    TextDialog	result;
    Properties	props;

    result = super.createDetailsDialog();

    props  = getProperties();
    result.setSize(
	  props.getInteger("DetailsDialog.Width", 400),
	  props.getInteger("DetailsDialog.Height", 300));

    return result;
  }

  /**
   * Sets the entries to display.
   *
   * @param entries	the log entries to display
   */
  public void display(List<LogEntry> entries) {
    m_TableModelEntries.clear();
    m_TableModelEntries.addAll(entries);
    m_TableEntries.setOptimalColumnWidth();
  }

  /**
   * Sets the entries to display, obtained from the specified handler.
   *
   * @param handler	the handler to obtain the records from
   */
  public void display(LogEntryHandler handler) {
    display(handler.getLogEntries());
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(LogEntryViewerPanelDefinition.KEY);

    return m_Properties;
  }
}
