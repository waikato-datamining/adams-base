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
 * AbstractBaseTableModel.java
 * Copyright (C) 2009-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.Utils;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingLevelHandler;
import adams.core.logging.LoggingSupporter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;

import javax.swing.table.AbstractTableModel;
import java.util.logging.Level;

/**
 * Abstract ancestor for table models. The models are automatically sortable.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractBaseTableModel
  extends AbstractTableModel 
  implements SpreadSheetSupporter, LoggingSupporter, LoggingLevelHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1379439060928152100L;

  /** the logging level. */
  protected LoggingLevel m_LoggingLevel;

  /** the logger in use. */
  protected transient Logger m_Logger;

  /** whether logging is enabled. */
  protected transient Boolean m_LoggingIsEnabled;

  /**
   * Initializes the model.
   */
  protected AbstractBaseTableModel() {
    super();
    initializeLogging();
  }

  /**
   * Pre-configures the logging.
   */
  protected void initializeLogging() {
    m_LoggingLevel = LoggingHelper.getLoggingLevel(getClass(), LoggingLevel.INFO);
  }

  /**
   * Initializes the logger.
   * <br><br>
   * Default implementation uses the class name.
   */
  protected void configureLogger() {
    m_Logger = LoggingHelper.getLogger(getClass());
    m_Logger.setLevel(m_LoggingLevel.getLevel());
  }

  /**
   * Returns the logger in use.
   *
   * @return		the logger
   */
  public synchronized Logger getLogger() {
    if (m_Logger == null)
      configureLogger();
    return m_Logger;
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public synchronized void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel     = value;
    m_Logger           = null;
    m_LoggingIsEnabled = null;
  }

  /**
   * Returns the logging level.
   *
   * @return 		the level
   */
  public LoggingLevel getLoggingLevel() {
    return m_LoggingLevel;
  }

  /**
   * Returns whether logging is enabled.
   *
   * @return		true if at least {@link Level#INFO}
   */
  public boolean isLoggingEnabled() {
    if (m_LoggingIsEnabled == null)
      m_LoggingIsEnabled = LoggingHelper.isAtLeast(m_LoggingLevel.getLevel(), Level.INFO);
    return m_LoggingIsEnabled;
  }

  /**
   * Returns the content as spreadsheet.
   * 
   * @return		the content
   */
  public SpreadSheet toSpreadSheet() {
    SpreadSheet 	result;
    Row			row;
    int			i;
    int			n;
    Object		value;
    
    result = new DefaultSpreadSheet();
    
    // header
    row = result.getHeaderRow();
    for (i = 0; i < getColumnCount(); i++)
      row.addCell("" + i).setContent(getColumnName(i));
    
    // data
    for (n = 0; n < getRowCount(); n++) {
      row = result.addRow("" + result.getRowCount());
      for (i = 0; i < getColumnCount(); i++) {
	value = getValueAt(n, i);
	if (value == null)
	  row.addCell("" + i).setContent(SpreadSheet.MISSING_VALUE);
	else if (value.getClass().isArray())
	  row.addCell("" + i).setContent(Utils.arrayToString(value));
	else if (value instanceof Integer)
	  row.addCell("" + i).setContent((Integer) value);
	else if (value instanceof Double)
	  row.addCell("" + i).setContent((Double) value);
	else
	  row.addCell("" + i).setContent("" + value);
      }
    }
    
    return result;
  }
  
  /**
   * Returns the table content as spreadsheet.
   * 
   * @return		the content
   */
  @Override
  public String toString() {
    return toSpreadSheet().toString();
  }
}
