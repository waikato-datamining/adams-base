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
 * AbstractTableActionWithDatabaseAccess.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import javax.swing.Icon;

import adams.core.Constants;
import adams.data.report.AbstractField;
import adams.gui.visualization.report.ReportFactory;

/**
 * Ancestor for actions that require a valid DB-ID and a ReportProvider.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTableActionWithDatabaseAccess
  extends AbstractTableAction {
  
  /** for serialization. */
  private static final long serialVersionUID = -2043816027261871298L;
  
  /**
   * Defines an <code>AbstractTableActionWithDatabaseAccess</code> object with a default
   * description string and default icon.
   */
  public AbstractTableActionWithDatabaseAccess() {
    super();
  }

  /**
   * Defines an <code>AbstractTableActionWithDatabaseAccess</code> object with the specified
   * description string and a default icon.
   *
   * @param name	the description
   */
  public AbstractTableActionWithDatabaseAccess(String name) {
    super(name);
  }

  /**
   * Defines an <code>AbstractTableActionWithDatabaseAccess</code> object with the specified
   * description string and a the specified icon.
   *
   * @param name	the description
   * @param icon	the icon
   */
  public AbstractTableActionWithDatabaseAccess(String name, Icon icon) {
    super(name, icon);
  }

  /**
   * Defines an <code>AbstractTableActionWithDatabaseAccess</code> object with the specified
   * description string and a the specified icon.
   *
   * @param name	the description
   * @param icon	the icon file (without path)
   */
  public AbstractTableActionWithDatabaseAccess(String name, String icon) {
    super(name, icon);
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
  public boolean isApplicable(ReportFactory.Table table, int row, AbstractField field, String value) {
    return    (table.getReport().getDatabaseID() != Constants.NO_ID) 
	   && (table.getReportProvider() != null);
  }
}
