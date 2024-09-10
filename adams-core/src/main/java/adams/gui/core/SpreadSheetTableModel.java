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
 * SpreadSheetTableModel.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.CleanUpHandler;
import adams.core.DateTime;
import adams.core.DateTimeMsec;
import adams.core.Shortening;
import adams.core.Time;
import adams.core.TimeMsec;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.gui.core.spreadsheettable.CellRenderingCustomizer;
import adams.gui.core.spreadsheettable.DefaultCellRenderingCustomizer;

import java.util.Date;

/**
 * The table model for displaying a SpreadSheet object.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetTableModel
  extends AbstractBaseTableModel
  implements ComparableTableModel, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8062515320279133441L;

  /** the default maximum column width. */
  public final static int DEFAULT_MAX_COLUMN_CHARS = 40;

  /** the key in GUIHelper.props for looking up the maximum chars in columns. */
  public final static String MAX_COLUMN_CHARS_KEY = "SpreadSheetTableMaxColumnNameChars";

  /** the maximum column width. */
  protected static Integer MAX_COLUMN_CHARS;

  /** the underlying spreadsheet. */
  protected SpreadSheet m_Sheet;

  /** the number of decimals to display. */
  protected int m_NumDecimals;

  /** for customizing the cell rendering. */
  protected CellRenderingCustomizer m_CellRenderingCustomizer;

  /** whether to show formulas rather than the result. */
  protected boolean m_ShowFormulas;
  
  /** whether to show the row column as well. */
  protected boolean m_ShowRowColumn;
  
  /** whether to use a simple header. */
  protected boolean m_UseSimpleHeader;

  /** whether the model is read-only (default). */
  protected boolean m_ReadOnly;

  /** whether the model is modified. */
  protected boolean m_Modified;

  /** whether to show the types instead of the values. */
  protected boolean m_ShowCellTypes;

  /** the types of the columns. */
  protected ContentType[] m_ColumnType;

  /**
   * Initializes the model with an empty spread sheet.
   */
  public SpreadSheetTableModel() {
    this(new DefaultSpreadSheet());
  }

  /**
   * Initializes the model with the given spread sheet.
   *
   * @param sheet	the spread sheet to display
   */
  public SpreadSheetTableModel(SpreadSheet sheet) {
    this(sheet, -1);
  }

  /**
   * Initializes the model with the given spread sheet and number of decimals
   * to display.
   *
   * @param sheet	the spread sheet to display
   * @param numDec	the number of decimals to display
   */
  public SpreadSheetTableModel(SpreadSheet sheet, int numDec) {
    super();

    m_Sheet                   = sheet;
    m_NumDecimals             = numDec;
    m_CellRenderingCustomizer = new DefaultCellRenderingCustomizer();
    m_ShowFormulas            = false;
    m_ShowRowColumn           = true;
    m_UseSimpleHeader         = false;
    m_ReadOnly                = true;
    m_Modified                = false;
    m_ShowCellTypes           = false;
    m_ColumnType              = new ContentType[sheet.getColumnCount()];
  }

  /**
   * Whether to display the column with the row numbers.
   * 
   * @param value	true if to display column
   */
  public void setShowRowColumn(boolean value) {
    m_ShowRowColumn = value;
    fireTableStructureChanged();
  }
  
  /**
   * Returns whether the column with the row numbers is displayed.
   * 
   * @return		true if column displayed
   */
  public boolean getShowRowColumn() {
    return m_ShowRowColumn;
  }

  /**
   * Whether to display a simple header or an HTML one with the column indices.
   * 
   * @param value	true if to display simple header
   */
  public void setUseSimpleHeader(boolean value) {
    m_UseSimpleHeader = value;
    fireTableStructureChanged();
  }
  
  /**
   * Returns whether to display a simple header or an HTML one with the column indices.
   * 
   * @return		true if simple header displayed
   */
  public boolean getUseSimpleHeader() {
    return m_UseSimpleHeader;
  }

  /**
   * Sets whether the table model is read-only.
   *
   * @param value	true if read-only
   */
  public void setReadOnly(boolean value) {
    m_ReadOnly = value;
  }

  /**
   * Returns whether the table model is read-only.
   *
   * @return		true if read-only
   */
  public boolean isReadOnly() {
    return m_ReadOnly || m_ShowCellTypes;
  }

  /**
   * Sets whether the table model has been modified.
   *
   * @param value	true if modified
   */
  public void setModified(boolean value) {
    m_Modified = value;
  }

  /**
   * Returns whether the table model has been modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Sets whether to show the cell types rather than the cell values.
   *
   * @param value	true if to show cell types
   */
  public void setShowCellTypes(boolean value) {
    m_ShowCellTypes = value;
    fireTableDataChanged();
  }

  /**
   * Returns whether to show the cell types rather than the cell values.
   *
   * @return		true if showing the cell types
   */
  public boolean getShowCellTypes() {
    return m_ShowCellTypes;
  }

  /**
   * Returns the number of rows in the sheet.
   *
   * @return		the number of rows
   */
  public int getRowCount() {
    if (m_Sheet == null)
      return 0;
    return m_Sheet.getRowCount();
  }

  /**
   * Returns the number of columns in the sheet.
   *
   * @return		the number of columns
   */
  public int getColumnCount() {
    if (m_Sheet == null)
      return 0;
    if (m_ShowRowColumn)
      return m_Sheet.getColumnCount() + 1;
    else
      return m_Sheet.getColumnCount();
  }

  /**
   * Shortens the column name, if necessary.
   *
   * @param name 	the original name
   * @return		the shortened name
   * @see		#DEFAULT_MAX_COLUMN_CHARS
   */
  protected synchronized String shortenColumnName(String name) {
    if (MAX_COLUMN_CHARS == null)
      MAX_COLUMN_CHARS = GUIHelper.getInteger(MAX_COLUMN_CHARS_KEY, DEFAULT_MAX_COLUMN_CHARS);

    if (MAX_COLUMN_CHARS <= 0)
      return name;
    else
      return Shortening.shortenEnd(name, MAX_COLUMN_CHARS);
  }

  /**
   * Returns the name of the column at <code>columnIndex</code>.  This is used
   * to initialize the table's column header name.  Note: this name does
   * not need to be unique; two columns in a table can have the same name.
   *
   * @param	columnIndex	the index of the column
   * @return  the name of the column
   */
  @Override
  public String getColumnName(int columnIndex) {
    String	result;
    Row	row;

    if (m_ShowRowColumn && (columnIndex == 0)) {
      if (m_UseSimpleHeader)
	result = "Row";
      else
	result = "<html><center>Row<br>1</center></html>";
    }
    else {
      if (m_ShowRowColumn)
	columnIndex--;
      row = m_Sheet.getHeaderRow();
      if (m_UseSimpleHeader)
	result = row.getCell(columnIndex).getContent();
      else
	result = "<html>" 
	    + "<left>"
	    + "<b>" + shortenColumnName(row.getCell(columnIndex).getContent()) + "</b>"
	    + "<br>" 
	    + SpreadSheetUtils.getColumnPosition(columnIndex) + " / " + (columnIndex + 1)
	    + "</left>"
	    + "</html>";
    }

    return result;
  }

  /**
   * Determines the content type of the specified column.
   * 
   * @param columnIndex	the index of the column to determine the type for
   * @return		the content type
   */
  protected ContentType determineContentType(int columnIndex) {
    if (m_ColumnType[columnIndex] == null) {
      if (m_Sheet.isNumeric(columnIndex, true))
	m_ColumnType[columnIndex] = ContentType.DOUBLE;
      else
	m_ColumnType[columnIndex] = m_Sheet.getContentType(columnIndex);
      if (m_ColumnType[columnIndex] == null)
	m_ColumnType[columnIndex] = ContentType.STRING;
    }
    
    return m_ColumnType[columnIndex];
  }
  
  /**
   * Returns the most specific superclass for all the cell values in the
   * column.
   *
   * @param columnIndex     the index of the column
   * @return                the class of the specified column
   */
  @Override
  public Class getColumnClass(int columnIndex) {
    if (m_ShowRowColumn && (columnIndex == 0))
      return Integer.class;
    else
      return String.class;
  }

  /**
   * Returns true if the cell at <code>rowIndex</code> and
   * <code>columnIndex</code>
   * is editable.  Otherwise, <code>setValueAt</code> on the cell will not
   * change the value of that cell.
   *
   * @param   rowIndex        the row whose value to be queried
   * @param   columnIndex     the column whose value to be queried
   * @return  true if the cell is editable
   */
  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    if (isReadOnly())
      return false;
    if (m_ShowRowColumn)
      return (columnIndex > 0);
    return true;
  }

  /**
   * Sets the value in the cell at <code>columnIndex</code> and
   * <code>rowIndex</code> to <code>aValue</code>.
   *
   * @param   aValue           the new value
   * @param   rowIndex         the row whose value is to be changed
   * @param   columnIndex      the column whose value is to be changed
   */
  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    Cell	cell;

    if (isReadOnly())
      return;
    if (m_ShowRowColumn && (columnIndex == 0))
      return;

    cell = getCellAt(rowIndex, columnIndex);
    if (aValue instanceof String)
      cell.setContent((String) aValue);
    else
      cell.setNative(aValue);
    m_Modified = true;

    fireTableCellUpdated(rowIndex, columnIndex);
  }

  /**
   * Returns the value for the cell at columnIndex and rowIndex.
   *
   * @param rowIndex      the row
   * @param columnIndex   the column
   * @return              the value of the sepcified cell
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    Object	result;
    Row		row;
    Cell	cell;
    double	d;
    double	factor;

    if (m_ShowRowColumn && (columnIndex == 0)) {
      result = rowIndex + 2;
    }
    else {
      if (m_ShowRowColumn)
	columnIndex--;
      row     = m_Sheet.getRow(rowIndex);
      cell    = row.getCell(m_Sheet.getHeaderRow().getCellKey(columnIndex));
      if (cell == null) {
	result = null;
      }
      else {
	if (cell.isMissing()) {
	  result = null;
	}
        else if (m_ShowCellTypes) {
          result = "" + cell.getContentType();
        }
	else if (cell.isFormula() && m_ShowFormulas) {
	  result = cell.getFormula();
	}
	else if (cell.getContentType() == ContentType.LONG) {
	  result = cell.toLong();
	}
	else if (cell.getContentType() == ContentType.DOUBLE) {
	  d = cell.toDouble();
	  if (Double.isNaN(d)) {
	    result = Utils.NAN;
	  }
	  else if (Double.isInfinite(d)) {
            if (d < 0)
              result = Utils.NEGATIVE_INFINITY;
            else
              result = Utils.POSITIVE_INFINITY;
	  }
	  else {
	    if (m_NumDecimals > -1) {
	      factor = Math.pow(10, m_NumDecimals);
	      d      = Math.round(d * factor) / factor;
	    }
	    result = Utils.doubleToStringFixed(d, m_NumDecimals);
	  }
	}
	else {
	  result = cell.getContent();
	}
      }
    }

    return result;
  }

  /**
   * Returns the spread sheet cell at the specified location.
   *
   * @param rowIndex		the current display row index
   * @param columnIndex	the column index
   * @return			the cell or null if invalid coordinates
   */
  public Cell getCellAt(int rowIndex, int columnIndex) {
    Cell	result;

    result = null;

    if (m_ShowRowColumn)
      columnIndex--;
    
    if (columnIndex >= 0)
      result = m_Sheet.getCell(rowIndex, columnIndex);

    return result;
  }

  /**
   * Sets the number of decimals to display. Use -1 to display all.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    if (value >= -1) {
      if (m_NumDecimals != value) {
	m_NumDecimals = value;
	fireTableDataChanged();
      }
    }
    else {
      System.err.println(
	  "Number of displayed decimals must be >=0 or -1 to display all - "
	  + "provided: " + value);
    }
  }

  /**
   * Returns the currently set number of decimals. -1 if displaying all.
   *
   * @return		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Sets the customizer for cell rendering.
   * 
   * @param value	the customizer
   */
  public void setCellRenderingCustomizer(CellRenderingCustomizer value) {
    m_CellRenderingCustomizer = value;
    fireTableDataChanged();
  }
  
  /**
   * Returns the customizer for cell rendering.
   * 
   * @return		the customizer
   */
  public CellRenderingCustomizer getCellRenderingCustomizer() {
    return m_CellRenderingCustomizer;
  }

  /**
   * Sets whether to display the formulas or their calculated values.
   *
   * @param value	true if to display the formulas rather than the calculated values
   */
  public void setShowFormulas(boolean value) {
    if (m_ShowFormulas != value) {
      m_ShowFormulas = value;
      fireTableDataChanged();
    }
  }

  /**
   * Returns whether to display the formulas or their calculated values.
   *
   * @return		true if to display the formulas rather than the calculated values
   */
  public boolean getShowFormulas() {
    return m_ShowFormulas;
  }
  
  /**
   * Returns the underlying sheet.
   *
   * @return		the spread sheet
   */
  @Override
  public SpreadSheet toSpreadSheet() {
    return m_Sheet;
  }

  /**
   * Returns the class type of the column that is used for comparisons.
   *
   * @param columnIndex	the column to get the class for
   * @return		the class for the column
   */
  public Class getComparisonColumnClass(int columnIndex) {
    if (m_ShowRowColumn && (columnIndex == 0)) {
      return Integer.class;
    }
    else {
      if (m_ShowRowColumn)
	columnIndex--;
      switch (determineContentType(columnIndex)) {
	case BOOLEAN:
	  return Boolean.class;
	case LONG:
	  return Double.class;  // see getComparisonValueAt
	case DOUBLE:
	  return Double.class;
	case DATE:
	  return Date.class;
	case TIME:
	  return Time.class;
	case TIMEMSEC:
	  return TimeMsec.class;
	case DATETIME:
	  return DateTime.class;
	case DATETIMEMSEC:
	  return DateTimeMsec.class;
	case OBJECT:
	  return Comparable.class;
	default:
	  return String.class;
      }
    }
  }

  /**
   * Returns the field at the given position.
   *
   * @param row		the row
   * @param column	the column (ignored, since only 1 column)
   * @return		the field
   */
  public Object getComparisonValueAt(int row, int column) {
    Object	result;

    result = null;

    if (m_ShowRowColumn && (column == 0)) {
      result = row + 2;
    }
    else {
      if (m_ShowRowColumn)
	column--;
      if (m_Sheet.hasCell(row, column) && !m_Sheet.getCell(row, column).isMissing()) {
	result = m_Sheet.getCell(row, column).getNative();
	if (result instanceof Long)  // see getComparisonColumnClass
	  result = ((Long) result).doubleValue();
      }
    }

    return result;
  }
  
  /**
   * Creates a new model with the same settings for the provided spreadsheet.
   * 
   * @param sheet	the spreadsheet to create a new model for
   * @return		the new model
   */
  public SpreadSheetTableModel newModel(SpreadSheet sheet) {
    SpreadSheetTableModel	result;
    
    result = new SpreadSheetTableModel(sheet);
    result.setNumDecimals(getNumDecimals());
    result.setCellRenderingCustomizer((CellRenderingCustomizer) OptionUtils.shallowCopy(getCellRenderingCustomizer()));
    result.setShowFormulas(getShowFormulas());
    result.setShowRowColumn(getShowRowColumn());
    result.setUseSimpleHeader(getUseSimpleHeader());
    
    return result;
  }

  /**
   * Returns the underlying spreadsheet.
   *
   * @return		the spreadsheet
   */
  public SpreadSheet getSheet() {
    return m_Sheet;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_Sheet = null;
  }
}
