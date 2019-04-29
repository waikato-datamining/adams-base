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
 * QueryProcessor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor.processors;

import adams.core.MessageCollection;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseButton;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.SpreadSheetQueryPanel;
import adams.gui.event.SpreadSheetProcessorEvent.EventType;
import adams.gui.tools.spreadsheetprocessor.AbstractWidget;
import adams.parser.SpreadSheetQuery;
import adams.parser.SpreadSheetQueryText;

import javax.swing.event.ChangeEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashMap;

/**
 * Uses an SQL-like query for processing the data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class QueryProcessor
  extends AbstractProcessor {

  private static final long serialVersionUID = 2926743330826433963L;

  /** the query panel. */
  protected SpreadSheetQueryPanel m_PanelQuery;

  /** the button for processing. */
  protected BaseButton m_ButtonProcess;

  /**
   * Returns the name of the widget.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Query";
  }

  /**
   * Returns the widget.
   *
   * @return		the widget
   */
  @Override
  public Component getWidget() {
    if (m_PanelQuery == null) {
      m_PanelQuery = new SpreadSheetQueryPanel();
      m_PanelQuery.setHistoryVisible(true);
      m_PanelQuery.addQueryChangeListener((ChangeEvent e) -> update());

      m_ButtonProcess = new BaseButton(GUIHelper.getIcon("run.gif"));
      m_ButtonProcess.addActionListener((ActionEvent e) ->
	notifyOwner(EventType.PROCESS_DATA, "Execute query: " + m_PanelQuery.getQuery().getValue()));
      m_PanelQuery.getButtonsRight().add(m_ButtonProcess);
    }

    return m_PanelQuery;
  }

  /**
   * Sets the query.
   *
   * @param value	the query
   */
  public void setCurrentQuery(SpreadSheetQueryText value) {
    m_PanelQuery.setQuery(value);
  }

  /**
   * Returns the query.
   *
   * @return		the query
   */
  public SpreadSheetQueryText getCurrentQuery() {
    return m_PanelQuery.getQuery();
  }

  /**
   * Retrieves the values from the other widget, if possible.
   *
   * @param other	the other widget to get the values from
   */
  public void assign(AbstractWidget other) {
    QueryProcessor 	widget;

    if (other instanceof QueryProcessor) {
      widget = (QueryProcessor) other;
      setCurrentQuery(widget.getCurrentQuery());
    }
  }

  /**
   * Updates the widget.
   */
  public void update() {
    m_ButtonProcess.setEnabled((m_Owner.getSourceData() != null) && !getCurrentQuery().isEmpty());
  }

  /**
   * Processes the data.
   *
   * @param data	the input data
   * @param errors	for storing errors
   * @return		the generated data, null in case of an error
   */
  @Override
  protected SpreadSheet doProcess(SpreadSheet data, MessageCollection errors) {
    SpreadSheet			result;
    SpreadSheetQueryText	query;

    query = m_PanelQuery.getQuery();
    if (query.isEmpty()) {
      getLogger().warning("No query entered, just passing through the data!");
      result = data;
    }
    else {
      try {
        result = SpreadSheetQuery.evaluate(query.getValue(), new HashMap(), data);
        m_PanelQuery.addToHistory();
        notifyOwner(EventType.DATA_IS_PROCESSED, "Executed query: " + query.getValue());
      }
      catch (Exception e) {
        errors.add("Failed to process data using:\n" + query.getValue(), e);
        result = null;
      }
    }

    return result;
  }
}
