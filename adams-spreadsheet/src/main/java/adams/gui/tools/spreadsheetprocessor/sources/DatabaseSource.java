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
 * DatabaseSource.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor.sources;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.base.BasePassword;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.sql.AbstractTypeMapper;
import adams.data.spreadsheet.sql.DefaultTypeMapper;
import adams.data.spreadsheet.sql.Reader;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.SQLF;
import adams.db.SQLStatement;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.core.SqlConnectionPanel;
import adams.gui.dialog.SQLStatementPanel;
import adams.gui.event.SpreadSheetProcessorEvent.EventType;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.spreadsheetprocessor.AbstractWidget;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * For retrieving data from a database.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DatabaseSource
  extends AbstractSource {

  private static final long serialVersionUID = -4475860171792209905L;

  public static final String KEY_URL = "url";

  public static final String KEY_USER = "user";

  public static final String KEY_PASSWORD = "password";

  public static final String KEY_QUERY = "query";

  public static final String KEY_TYPEMAPPER = "typemapper";

  public static final String KEY_DATAROW = "datarow";

  /** the widget. */
  protected BasePanel m_Widget;

  /** the connection panel. */
  protected SqlConnectionPanel m_PanelConnection;

  /** the panel for the type mapper. */
  protected GenericObjectEditorPanel m_PanelTypeMapper;

  /** the panel for the row class. */
  protected GenericObjectEditorPanel m_PanelDataRow;

  /** the query panel. */
  protected SQLStatementPanel m_PanelQuery;

  /** the button for executing the query. */
  protected BaseButton m_ButtonExecute;

  /** the data. */
  protected SpreadSheet m_Data;

  /**
   * Returns the name of the widget.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Database";
  }

  /**
   * Returns the widget.
   *
   * @return		the widget
   */
  @Override
  public Component getWidget() {
    JPanel		topPanel;
    ParameterPanel 	paramsPanel;

    if (m_Widget == null) {
      m_Widget = new BasePanel(new BorderLayout());

      topPanel = new JPanel(new BorderLayout());
      m_Widget.add(topPanel, BorderLayout.NORTH);

      m_PanelConnection = new SqlConnectionPanel();
      m_PanelConnection.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      topPanel.add(m_PanelConnection, BorderLayout.NORTH);

      paramsPanel = new ParameterPanel();
      topPanel.add(paramsPanel, BorderLayout.CENTER);
      m_PanelTypeMapper = new GenericObjectEditorPanel(AbstractTypeMapper.class, new DefaultTypeMapper(), true);
      paramsPanel.addParameter("Type mapper", m_PanelTypeMapper);
      m_PanelDataRow = new GenericObjectEditorPanel(DataRow.class, new DenseDataRow(), true);
      paramsPanel.addParameter("Data row", m_PanelDataRow);

      m_PanelQuery = new SQLStatementPanel();
      m_PanelQuery.addQueryChangeListener((ChangeEvent e) -> update());
      m_Widget.add(m_PanelQuery, BorderLayout.CENTER);
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

      m_ButtonExecute = new BaseButton(GUIHelper.getIcon("run.gif"));
      m_ButtonExecute.addActionListener((ActionEvent e) -> execute());
      m_ButtonExecute.setToolTipText("Executes the query (Alt+X)");
      m_PanelQuery.getButtonsLeft().add(m_ButtonExecute);
    }

    return m_Widget;
  }

  /**
   * Loads the data
   */
  protected void execute() {
    SwingWorker	worker;

    worker = new SwingWorker() {
      String error;
      @Override
      protected Object doInBackground() throws Exception {
        m_ButtonExecute.setEnabled(false);
        String query = m_PanelQuery.getStatement().getValue();
        SQLF sql = SQLF.getSingleton(m_PanelConnection.getDatabaseConnection());
        Reader reader = new Reader((AbstractTypeMapper) m_PanelTypeMapper.getCurrent(), m_PanelDataRow.getCurrent().getClass());
	try {
	  m_Data = reader.read(sql.getResultSet(query));
	  if (m_Data == null)
	    error = "Failed to execute query: " + query;
	  else
	    notifyOwner(EventType.DATA_IS_AVAILABLE, "Query executed: " + query);
	}
	catch (Exception e) {
	  error  = Utils.handleException(DatabaseSource.this, "Failed to execute query: " + query, e);
	  m_Data = null;
	}
	return null;
      }

      @Override
      protected void done() {
	super.done();
        m_ButtonExecute.setEnabled(true);
	if (error != null)
	  GUIHelper.showErrorMessage(m_Widget.getParent(), error);
      }
    };
    worker.execute();
  }

  /**
   * Checks whether data is available.
   *
   * @return		true if available
   */
  @Override
  public boolean hasData() {
    return (m_Data != null);
  }

  /**
   * Returns the currently available data
   *
   * @return		the data, null if none available
   */
  @Override
  public SpreadSheet getData() {
    return m_Data;
  }

  /**
   * Sets the current query.
   *
   * @param value	the current query
   */
  public void setCurrentQuery(SQLStatement value) {
    m_PanelQuery.setStatement(value);
  }

  /**
   * Returns the current query.
   *
   * @return		the current query
   */
  public SQLStatement getCurrentQuery() {
    return m_PanelQuery.getStatement();
  }

  /**
   * Sets the current connection.
   *
   * @param value	the connection
   */
  public void setCurrentConnection(AbstractDatabaseConnection value) {
    m_PanelConnection.setDatabaseConnection(value);
  }

  /**
   * Returns the current connection.
   *
   * @return		the connection
   */
  public AbstractDatabaseConnection getCurrentConnection() {
    return m_PanelConnection.getDatabaseConnection();
  }

  /**
   * Sets the current type mapper.
   *
   * @param value	the mapper
   */
  public void setCurrentTypeMapper(AbstractTypeMapper value) {
    m_PanelTypeMapper.setCurrent(value);
  }

  /**
   * Returns the current type mapper.
   *
   * @return		the mapper
   */
  public AbstractTypeMapper getCurrentTypeMapper() {
    return (AbstractTypeMapper) m_PanelTypeMapper.getCurrent();
  }

  /**
   * Sets the current data row.
   *
   * @param value	the data row
   */
  public void setCurrentDataRow(DataRow value) {
    m_PanelDataRow.setCurrent(value);
  }

  /**
   * Returns the current data row.
   *
   * @return		the data row
   */
  public DataRow getCurrentDataRow() {
    return (DataRow) m_PanelDataRow.getCurrent();
  }

  /**
   * Retrieves the values from the other widget, if possible.
   *
   * @param other	the other widget to get the values from
   */
  public void assign(AbstractWidget other) {
    DatabaseSource widget;

    if (other instanceof DatabaseSource) {
      widget = (DatabaseSource) other;
      widget.getWidget();
      setCurrentConnection(widget.getCurrentConnection());
      setCurrentQuery(widget.getCurrentQuery());
      setCurrentTypeMapper(widget.getCurrentTypeMapper());
      setCurrentDataRow(widget.getCurrentDataRow());
    }
  }

  /**
   * Serializes the setup from the widget.
   *
   * @return		the generated setup representation
   */
  public Object serialize() {
    Map<String,Object> 	result;

    result = new HashMap<>();
    result.put(KEY_URL, getCurrentConnection().getURL());
    result.put(KEY_USER, getCurrentConnection().getUser());
    result.put(KEY_PASSWORD, getCurrentConnection().getPassword().stringValue());
    result.put(KEY_QUERY, getCurrentQuery().getValue());
    result.put(KEY_TYPEMAPPER, OptionUtils.getCommandLine(m_PanelTypeMapper.getCurrent()));
    result.put(KEY_DATAROW, OptionUtils.getCommandLine(m_PanelDataRow.getCurrent()));

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
    DatabaseConnection  conn;

    if (data instanceof Map) {
      map = (Map<String,Object>) data;
      if (map.containsKey(KEY_URL) && map.containsKey(KEY_USER) && map.containsKey(KEY_PASSWORD)) {
        conn = new DatabaseConnection((String) map.get(KEY_URL), (String) map.get(KEY_USER), new BasePassword((String) map.get(KEY_PASSWORD)));
        setCurrentConnection(conn);
      }
      if (map.containsKey(KEY_QUERY))
        setCurrentQuery(new SQLStatement((String) map.get(KEY_QUERY)));
      if (map.containsKey(KEY_TYPEMAPPER)) {
        try {
	  setCurrentTypeMapper((AbstractTypeMapper) OptionUtils.forAnyCommandLine(AbstractTypeMapper.class, (String) map.get(KEY_TYPEMAPPER)));
	}
	catch (Exception e) {
	  errors.add(getClass().getName() + ": Failed to instantiate type mapper from: " + map.get(KEY_TYPEMAPPER));
	}
      }
      if (map.containsKey(KEY_DATAROW)) {
        try {
	  setCurrentDataRow((DataRow) OptionUtils.forAnyCommandLine(DataRow.class, (String) map.get(KEY_DATAROW)));
	}
	catch (Exception e) {
	  errors.add(getClass().getName() + ": Failed to instantiate data row from: " + map.get(KEY_DATAROW));
	}
      }
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
    m_ButtonExecute.setEnabled(!m_PanelQuery.getStatement().isEmpty());
  }
}
