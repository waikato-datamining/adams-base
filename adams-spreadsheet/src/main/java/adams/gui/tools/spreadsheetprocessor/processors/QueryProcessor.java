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
import adams.gui.core.ImageManager;
import adams.gui.dialog.SpreadSheetQueryPanel;
import adams.gui.event.SpreadSheetProcessorEvent.EventType;
import adams.gui.tools.spreadsheetprocessor.AbstractWidget;
import adams.parser.SpreadSheetQuery;
import adams.parser.SpreadSheetQueryText;

import javax.swing.event.ChangeEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Uses an SQL-like query for processing the data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class QueryProcessor
  extends AbstractProcessor {

  private static final long serialVersionUID = 2926743330826433963L;

  public static final String KEY_QUERY = "query";

  /** the query panel. */
  protected SpreadSheetQueryPanel m_PanelQuery;

  /** the button for processing. */
  protected BaseButton m_ButtonExecute;

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
      m_PanelQuery.getQueryPanel().getTextPane().addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          if ((e.getKeyCode() == KeyEvent.VK_X) && (e.getModifiersEx() == KeyEvent.ALT_DOWN_MASK)) {
            e.consume();
            execute();
          }
          if (!e.isConsumed())
            super.keyPressed(e);
        }
      });

      m_ButtonExecute = new BaseButton(ImageManager.getIcon("run.gif"));
      m_ButtonExecute.addActionListener((ActionEvent e) -> execute());
      m_ButtonExecute.setToolTipText("Executes the query (Alt+X)");
      m_PanelQuery.getButtonsRight().add(m_ButtonExecute);
    }

    return m_PanelQuery;
  }

  /**
   * Executes the query.
   */
  protected void execute() {
    notifyOwner(EventType.PROCESS_DATA, "Execute query: " + m_PanelQuery.getQuery().getValue());
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
      widget.getWidget();
      setCurrentQuery(widget.getCurrentQuery());
    }
  }

  /**
   * Serializes the setup from the widget.
   *
   * @return		the generated setup representation
   */
  public Object serialize() {
    Map<String,Object> result;

    result = new HashMap<>();
    result.put(KEY_QUERY, getCurrentQuery().getValue());

    return result;
  }

  /**
   * Deserializes the setup and maps it onto the widget.
   *
  /**
   * Deserializes the setup and maps it onto the widget.
   *
   * @param data	the setup representation to use
   * @param errors	for collecting errors
   */
  public void deserialize(Object data, MessageCollection errors) {
    Map<String,Object>	map;

    if (data instanceof Map) {
      map = (Map<String,Object>) data;
      if (map.containsKey(KEY_QUERY))
        setCurrentQuery(new SpreadSheetQueryText((String) map.get(KEY_QUERY)));
      update();
    }
    else {
      errors.add(getClass().getName() + ": Deserialization data is not a map!");
    }
  }

  /**
   * Updates the widget.
   */
  public void update() {
    m_ButtonExecute.setEnabled((m_Owner.getSourceData() != null) && !getCurrentQuery().isEmpty());
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
