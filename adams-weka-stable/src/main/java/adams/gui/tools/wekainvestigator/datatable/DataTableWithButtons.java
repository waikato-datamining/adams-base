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
 * DataTableWithButtons.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.datatable;

import adams.gui.core.BaseTableWithButtons;

import javax.swing.event.ListSelectionEvent;
import java.util.ArrayList;

/**
 * Specialized table with buttons.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataTableWithButtons
  extends BaseTableWithButtons {

  private static final long serialVersionUID = -4226940020931531155L;

  /**
   * Constructs a <code>DataTableWithButtons</code> that is initialized with
   * <code>dm</code> as the data model, a default column model,
   * and a default selection model.
   *
   * @param dm        the data model for the table
   */
  public DataTableWithButtons(DataTableModel dm) {
    super(dm);
  }

  /**
   * Creates the component to use in the panel. If a
   *
   * @return		the component
   */
  protected DataTable createComponent() {
    DataTable	result;

    result = new DataTable(new DataTableModel(new ArrayList<>(), true));
    result.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> updateCounts());

    return result;
  }

  /**
   * Sets whether to sort the attributes alphabetically for the dropdown list.
   *
   * @param value	true if to sort
   */
  public void setSortAttributes(boolean value) {
    ((DataTable) m_Component).setSortAttributes(value);
  }

  /**
   * Returns whether to sort the attributes alphabetically for the dropdown list.
   *
   * @return		true if to sort
   */
  public boolean getSortAttributes() {
    return ((DataTable) m_Component).getSortAttributes();
  }
}
