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
 * AbstractTableAction.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import adams.core.Utils;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.db.ReportProvider;
import adams.gui.action.AbstractBaseAction;
import adams.gui.chooser.AbstractReportFileChooser;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.report.ReportFactory;

import javax.swing.Icon;

/**
 * Ancestor for actions populating the popup menu of the Table class of the 
 * {@link ReportFactory}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTableAction
  extends AbstractBaseAction {

  /** for serialization. */
  private static final long serialVersionUID = 3352294109225825118L;

  /** the placeholder for a separator in the popup menu. */
  public final static String SEPARATOR = "-";

  /** the key for storing the table in the action's hashtable. */
  public final static String KEY_TABLE = "table";

  /** the key for storing the rows in the action's hashtable. */
  public final static String KEY_ROWS = "rows";

  /** the key for storing the fields in the action's hashtable. */
  public final static String KEY_FIELDS = "fields";

  /** the key for storing the values in the action's hashtable. */
  public final static String KEY_VALUES = "values";
  
  /**
   * Defines an <code>AbstractTableAction</code> object with a default
   * description string and default icon.
   */
  public AbstractTableAction() {
    super();
  }

  /**
   * Defines an <code>AbstractTableAction</code> object with the specified
   * description string and a default icon.
   *
   * @param name	the description
   */
  public AbstractTableAction(String name) {
    super(name);
  }

  /**
   * Defines an <code>AbstractTableAction</code> object with the specified
   * description string and a the specified icon.
   *
   * @param name	the description
   * @param icon	the icon
   */
  public AbstractTableAction(String name, Icon icon) {
    super(name, icon);
  }

  /**
   * Defines an <code>AbstractTableAction</code> object with the specified
   * description string and a the specified icon.
   *
   * @param name	the description
   * @param icon	the icon file (without path)
   */
  public AbstractTableAction(String name, String icon) {
    super(name, icon);
  }

  /**
   * Sets the table to use.
   * 
   * @param value	the table
   */
  protected void setTable(ReportFactory.Table value) {
    putValue(KEY_TABLE, value);
  }
  
  /**
   * Returns the currently table.
   * 
   * @return		the table, null if not applicable
   */
  protected ReportFactory.Table getTable() {
    return (ReportFactory.Table) getValue(KEY_TABLE);
  }

  /**
   * Sets the rows to use.
   * 
   * @param value	the rows
   */
  protected void setRows(int[] value) {
    putValue(KEY_ROWS, value);
  }

  /**
   * Returns the currently selected rows in the table.
   * 
   * @return		the rows, null if not applicable
   */
  protected int[] getRow() {
    return (int[]) getValue(KEY_ROWS);
  }

  /**
   * Sets the fields to use.
   * 
   * @param value	the fields
   */
  protected void setFields(AbstractField[] value) {
    putValue(KEY_FIELDS, value);
  }

  /**
   * Returns the currently selected fields in the table.
   * 
   * @return		the fields, null if not applicable
   */
  protected AbstractField[] getFields() {
    return (AbstractField[]) getValue(KEY_FIELDS);
  }

  /**
   * Sets the values to use.
   * 
   * @param value	the values
   */
  protected void setValues(String[] value) {
    putValue(KEY_VALUES, value);
  }

  /**
   * Returns the currently selected values in the table.
   * 
   * @return		the values, null if not applicable
   */
  protected String[] getValues() {
    return (String[]) getValue(KEY_VALUES);
  }

  /**
   * Returns the underlying report provider.
   * 
   * @return		the report provider, null if not available
   */
  protected ReportProvider getReportProvider() {
    if (getTable() != null)
      return getTable().getReportProvider();
    else
      return null;
  }

  /**
   * Sets the underlying report.
   * 
   * @param value	the report to set
   */
  protected void setReport(Report value) {
    if (getTable() != null)
      getTable().setReport(value);
  }

  /**
   * Returns the underlying report.
   * 
   * @return		the report, null if not available
   */
  protected Report getReport() {
    if (getTable() != null)
      return getTable().getReport();
    else
      return null;
  }
  
  /**
   * Returns the underlying report file chooser.
   * 
   * @return		the file chooser, null if not available
   */
  protected AbstractReportFileChooser getFileChooser() {
    if (getTable() != null)
      return getTable().getFileChooser();
    else
      return null;
  }

  /**
   * Parses a string as double. If parsing fails, an error dialog is
   * popped up.
   *
   * @param s		the string to parse
   * @return		the double or null in case of an error
   */
  protected Double parseDouble(String s) {
    Double	result;

    try {
      result = Utils.toDouble(s);
    }
    catch (Exception e) {
      result = null;
      e.printStackTrace();
      GUIHelper.showErrorMessage(
	  getTable(), "Error parsing value '" + s + "':\n" + e);
    }

    return result;
  }

  /**
   * Parses a string as boolean. If parsing fails, an error dialog is
   * popped up.
   *
   * @param s		the string to parse
   * @return		the boolean or null in case of an error
   */
  protected Boolean parseBoolean(String s) {
    Boolean 	result;

    try {
      result = Boolean.parseBoolean(s);
    }
    catch (Exception e) {
      result = null;
      e.printStackTrace();
      GUIHelper.showErrorMessage(
	  getTable(), "Error parsing value '" + s + "':\n" + e);
    }

    return result;
  }
  
  /**
   * Checks whether the action is applicable and should be added to the popup
   * menu.
   * 
   * @param table	the table the popup menu is for
   * @param row		the currently selected row
   * @param field	the field in the specified row
   * @param value	the current value
   * @return		true if the action is applicable, i.e., should be 
   * 			included in the popup menu
   */
  public abstract boolean isApplicable(ReportFactory.Table table, int row, AbstractField field, String value);
  
  /**
   * Assembles the popup menu for the table.
   * 
   * @param actions	the classnames of the actions, "-" indicates a separator
   * @param table	the table the popup menu is for
   * @param rows	the row(s) in the table this popup menu is for
   * @return		the popup menu
   * @see		#SEPARATOR
   */
  public static BasePopupMenu createPopup(String[] actions, ReportFactory.Table table, int[] rows) {
    BasePopupMenu	result;
    AbstractTableAction	taction;
    AbstractField[]	fields;
    Object[]		values;
    String[]		valuesStr;
    boolean		wasSeparator;
    int			i;
    boolean		multiAppl;
    boolean		singleAppl;
    
    result = new BasePopupMenu();

    fields    = new AbstractField[rows.length];
    values    = new Object[rows.length];
    valuesStr = new String[rows.length];
    for (i = 0; i < rows.length; i++) {
      fields[i] = table.getFieldAt(rows[i]);
      values[i] = table.getReport().getValue(fields[i]);
      if (values[i] == null)
        valuesStr[i] = "";
      else
        valuesStr[i] = "" + values[i];
    }
    wasSeparator = true;
    for (String action: actions) {
      if (action.equals(SEPARATOR) || action.equals(Separator.class.getName())) {
	if (!wasSeparator) {
	  result.addSeparator();
	  wasSeparator = true;
	}
      }
      else {
	try {
	  taction    = (AbstractTableAction) Class.forName(action).newInstance();
	  singleAppl = 
	      (rows.length == 1) 
	      && (taction.isApplicable(table, rows[0], fields[0], valuesStr[0]));
	  multiAppl  = 
	      (rows.length > 0) 
	      && (taction instanceof MultiSelectionTableAction)
	      && (((MultiSelectionTableAction) taction).isApplicable(table, rows, fields, valuesStr));
	  if (singleAppl || multiAppl) {
	    taction.setTable(table);
	    taction.setRows(rows);
	    taction.setFields(fields);
	    taction.setValues(valuesStr);
	    wasSeparator = false;
	    result.add(taction);
	  }
	}
	catch (Exception e) {
	  System.err.println("Failed to instantiate table action '" + action + "':");
	  e.printStackTrace();
	}
      }
    }
    
    return result;
  }

  /**
   * Creates a string for the menu based on the prefix and the fields.
   * 
   * @param prefix	the prefix to use (separated by a blank from fields)
   * @param fields	the fields
   * @param limit	the maximum number of characters for the fields
   * @return		the generated string
   */
  public static String createName(String prefix, AbstractField[] fields, int limit) {
    StringBuilder	str;
    int			i;
    
    str = new StringBuilder();
    for (i = 0; i < fields.length; i++) {
      if (i > 0)
	str.append(", ");
      str.append("'" + fields[i].toDisplayString() + "'");
    }

    return prefix + (fields.length != 1 ? "s" : "") + " " + Utils.shorten(str.toString(), limit);
  }
}
