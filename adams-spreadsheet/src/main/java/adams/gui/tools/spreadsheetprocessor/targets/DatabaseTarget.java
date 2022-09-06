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
 * DatabaseTarget.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor.targets;

import adams.core.MessageCollection;
import adams.core.base.BaseInteger;
import adams.core.base.BasePassword;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.ColumnNameConversion;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.sql.AbstractTypeMapper;
import adams.data.spreadsheet.sql.DefaultTypeMapper;
import adams.data.spreadsheet.sql.Writer;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.SQLF;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BaseObjectTextField;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextField;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.ParameterPanel;
import adams.gui.core.SqlConnectionPanel;
import adams.gui.event.SpreadSheetProcessorEvent;
import adams.gui.event.SpreadSheetProcessorEvent.EventType;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.spreadsheetprocessor.AbstractWidget;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * For storing the data in a database.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DatabaseTarget
  extends AbstractTarget {

  private static final long serialVersionUID = 6535516712611654393L;

  public static final String KEY_URL = "url";

  public static final String KEY_USER = "user";

  public static final String KEY_PASSWORD = "password";

  public static final String KEY_TYPEMAPPER = "typemapper";

  public static final String KEY_TABLE = "table";

  public static final String KEY_COLUMNNAMECONVERSION = "columnnameconversion";

  public static final String KEY_MAXSTRINGLENGTH = "maxstringlength";

  public static final String KEY_STRINGCOLUMNSQL = "stringcolumnsql";

  public static final String KEY_BATCHSIZE = "batchsize";

  /** the widget. */
  protected BasePanel m_Widget;

  /** the connection panel. */
  protected SqlConnectionPanel m_PanelConnection;

  /** the panel for the type mapper. */
  protected GenericObjectEditorPanel m_PanelTypeMapper;

  /** the table to use. */
  protected BaseTextField m_TextTable;

  /** the column name conversion. */
  protected BaseComboBox<ColumnNameConversion> m_ComboBoxColumnNameConversion;

  /** the max string length. */
  protected BaseObjectTextField<BaseInteger> m_TextMaxStringLength;

  /** the SQL for creating string columns. */
  protected BaseTextField m_TextStringColumnSQL;

  /** the batch size. */
  protected BaseObjectTextField<BaseInteger> m_TextBatchSize;

  /** the button for uploading the data. */
  protected BaseButton m_ButtonExecute;

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
    JPanel		panel;
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

      m_TextTable = new BaseTextField();
      paramsPanel.addParameter("Table", m_TextTable);

      m_ComboBoxColumnNameConversion = new BaseComboBox<>(ColumnNameConversion.values());
      paramsPanel.addParameter("Column name conversion", m_ComboBoxColumnNameConversion);

      m_TextMaxStringLength = new BaseObjectTextField<>(new BaseInteger(50));
      paramsPanel.addParameter("Max string length", m_TextMaxStringLength);

      m_TextStringColumnSQL = new BaseTextField("VARCHAR(" +  Writer.PLACEHOLDER_MAX + ")");
      paramsPanel.addParameter("String column SQL", m_TextStringColumnSQL);

      m_TextBatchSize = new BaseObjectTextField<>(new BaseInteger(1));
      paramsPanel.addParameter("Batch size", m_TextBatchSize);

      m_ButtonExecute = new BaseButton(ImageManager.getIcon("run.gif"));
      m_ButtonExecute.addActionListener((ActionEvent e) -> generate());
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      panel.add(m_ButtonExecute);
      paramsPanel.addParameter("Execute", panel);
    }

    return m_Widget;
  }

  /**
   * Updates the widget.
   */
  public void update() {
    m_ButtonExecute.setEnabled((m_Owner.getProcessorData() != null) && !getCurrentTable().isEmpty());
  }

  /**
   * Loads the data
   */
  protected void generate() {
    SwingWorker worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
        m_ButtonExecute.setEnabled(false);
	m_Owner.processorStateChanged(new SpreadSheetProcessorEvent(m_Owner, EventType.OUTPUT_DATA, "Store data: " + getCurrentConnection().toCommandLine() + "/" + getCurrentTable()));
	return null;
      }

      @Override
      protected void done() {
	super.done();
        m_ButtonExecute.setEnabled(true);
      }
    };
    worker.execute();
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
   * Sets the current table.
   *
   * @param value	the table
   */
  public void setCurrentTable(String value) {
    m_TextTable.setText(value);
  }

  /**
   * Returns the current table.
   *
   * @return		the table
   */
  public String getCurrentTable() {
    return m_TextTable.getText();
  }

  /**
   * Sets the current column name conversion.
   *
   * @param value	the conversion
   */
  public void setCurrentColumnNameConversion(ColumnNameConversion value) {
    m_ComboBoxColumnNameConversion.setSelectedItem(value);
  }

  /**
   * Returns the current column name conversion.
   *
   * @return		the conversion
   */
  public ColumnNameConversion getCurrentColumnNameConversion() {
    return m_ComboBoxColumnNameConversion.getSelectedItem();
  }

  /**
   * Sets the current maximum length for strings.
   *
   * @param value	the maximum
   */
  public void setCurrentMaxStringLength(BaseInteger value) {
    m_TextMaxStringLength.setObject(value);
  }

  /**
   * Returns the current maximum length for strings.
   *
   * @return		the maximum
   */
  public BaseInteger getCurrentMaxStringLength() {
    return m_TextMaxStringLength.getObject();
  }

  /**
   * Sets the current SQL template for string columns.
   *
   * @param value	the template
   */
  public void setCurrentStringColumnSQL(String value) {
    m_TextStringColumnSQL.setText(value);
  }

  /**
   * Returns the current SQL template for string columns.
   *
   * @return		the template
   */
  public String getCurrentStringColumnSQL() {
    return m_TextStringColumnSQL.getText();
  }

  /**
   * Sets the current batch size.
   *
   * @param value	the size
   */
  public void setCurrentBatchSize(BaseInteger value) {
    m_TextBatchSize.setObject(value);
  }

  /**
   * Returns the current batch size.
   *
   * @return		the size
   */
  public BaseInteger getCurrentBatchSize() {
    return m_TextBatchSize.getObject();
  }

  /**
   * Retrieves the values from the other widget, if possible.
   *
   * @param other	the other widget to get the values from
   */
  public void assign(AbstractWidget other) {
    DatabaseTarget widget;

    if (other instanceof DatabaseTarget) {
      widget = (DatabaseTarget) other;
      widget.getWidget();
      setCurrentConnection(widget.getCurrentConnection());
      setCurrentTypeMapper(widget.getCurrentTypeMapper());
      setCurrentTable(widget.getCurrentTable());
      setCurrentColumnNameConversion(widget.getCurrentColumnNameConversion());
      setCurrentMaxStringLength(widget.getCurrentMaxStringLength());
      setCurrentStringColumnSQL(widget.getCurrentStringColumnSQL());
      setCurrentBatchSize(widget.getCurrentBatchSize());
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
    result.put(KEY_URL, getCurrentConnection().getURL());
    result.put(KEY_USER, getCurrentConnection().getUser());
    result.put(KEY_PASSWORD, getCurrentConnection().getPassword().stringValue());
    result.put(KEY_TYPEMAPPER, OptionUtils.getCommandLine(m_PanelTypeMapper.getCurrent()));
    result.put(KEY_TABLE, getCurrentTable());
    result.put(KEY_COLUMNNAMECONVERSION, getCurrentColumnNameConversion().toString());
    result.put(KEY_MAXSTRINGLENGTH, getCurrentMaxStringLength());
    result.put(KEY_STRINGCOLUMNSQL, getCurrentStringColumnSQL());
    result.put(KEY_BATCHSIZE, getCurrentBatchSize());

    return result;
  }

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
      if (map.containsKey(KEY_TYPEMAPPER)) {
        try {
	  setCurrentTypeMapper((AbstractTypeMapper) OptionUtils.forAnyCommandLine(AbstractTypeMapper.class, (String) map.get(KEY_TYPEMAPPER)));
	}
	catch (Exception e) {
	  errors.add(getClass().getName() + ": Failed to instantiate type mapper from: " + map.get(KEY_TYPEMAPPER));
	}
      }
      if (map.containsKey(KEY_TABLE))
        setCurrentTable((String) map.get(KEY_TABLE));
      if (map.containsKey(KEY_COLUMNNAMECONVERSION))
        setCurrentColumnNameConversion(ColumnNameConversion.valueOf((String) map.get(KEY_COLUMNNAMECONVERSION)));
      if (map.containsKey(KEY_MAXSTRINGLENGTH))
        setCurrentMaxStringLength(new BaseInteger((String) map.get(KEY_MAXSTRINGLENGTH)));
      if (map.containsKey(KEY_STRINGCOLUMNSQL))
        setCurrentStringColumnSQL((String) map.get(KEY_STRINGCOLUMNSQL));
      if (map.containsKey(KEY_BATCHSIZE))
        setCurrentBatchSize(new BaseInteger((String) map.get(KEY_BATCHSIZE)));
      update();
    }
    else {
      errors.add(getClass().getName() + ": Deserialization data is not a map!");
    }
  }

  /**
   * Processes the data.
   *
   * @param data	the input data
   * @param errors	for storing errors
   */
  @Override
  protected void doProcess(final SpreadSheet data, MessageCollection errors) {
    final AbstractDatabaseConnection 	conn;
    final String 			table;
    final AbstractTypeMapper		typeMapper;
    final ColumnNameConversion		conversion;
    final String			strColSQL;
    final int				maxStrLen;
    final int				batchSize;
    SwingWorker	  			worker;

    conn       = getCurrentConnection();
    typeMapper = (AbstractTypeMapper) m_PanelTypeMapper.getCurrent();
    table      = m_TextTable.getText();
    conversion = m_ComboBoxColumnNameConversion.getSelectedItem();
    strColSQL  = m_TextStringColumnSQL.getText();
    maxStrLen  = m_TextMaxStringLength.getObject().intValue();
    batchSize  = m_TextBatchSize.getObject().intValue();

    worker = new SwingWorker() {
      String error = null;

      @Override
      protected Object doInBackground() throws Exception {
        m_ButtonExecute.setEnabled(false);
        SQLF sql = SQLF.getSingleton(m_PanelConnection.getDatabaseConnection());
        // configure writer
	Writer writer;
	try {
	  writer = new Writer(
	    data,
	    typeMapper,
	    table,
	    sql.getMaxColumnNameLength(),
	    conversion,
	    strColSQL,
	    maxStrLen,
	    batchSize);
	  writer.setLoggingLevel(getLoggingLevel());
	}
	catch (Exception e) {
	  writer = null;
	  error  = LoggingHelper.handleException(DatabaseTarget.this, "Failed to store data!" + conn.toCommandLine() + "/" + table, e);
	}
	// write data
	if (writer != null) {
	  if (!sql.tableExists(table))
	    error = writer.createTable(sql);
	  if (error == null)
	    error = writer.writeData(sql);
	}
        return error;
      }

      @Override
      protected void done() {
        super.done();
        if (error != null)
          GUIHelper.showErrorMessage(m_Owner, error);
        else
          notifyOwner(EventType.DATA_IS_OUTPUT, "Stored data: " + conn.toCommandLine() + "/" + table);
      }
    };

    worker.execute();
  }
}
