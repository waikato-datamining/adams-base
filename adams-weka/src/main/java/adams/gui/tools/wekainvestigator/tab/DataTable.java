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
 * DataTable.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.StringCompare;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.core.SortableAndSearchableWrapperTableModel;
import adams.gui.tools.wekainvestigator.data.DataContainer;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Specialized table with custom cell editors for the class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataTable
  extends SortableAndSearchableTable {

  private static final long serialVersionUID = -2329794525513037246L;

  /** the cell editors for the class attribute. */
  protected HashMap<Integer,TableCellEditor> m_ClassAttributeEditors;

  /**
   * Constructs a <code>DataTable</code> that is initialized with
   * <code>dm</code> as the data model, a default column model,
   * and a default selection model.
   *
   * @param dm        the data model for the table
   */
  public DataTable(DataTableModel dm) {
    super(dm);
  }

  /**
   * Initializes some GUI-related things.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_ClassAttributeEditors = new HashMap<>();
  }

  /**
   * Returns the cell editor for the specified cell.
   *
   * @param row		the row
   * @param column	the column
   * @return		the cell editor
   */
  @Override
  public TableCellEditor getCellEditor(int row, int column) {
    TableCellEditor	result;
    int			actRow;
    DataContainer 	cont;
    List<String>	atts;
    int			i;
    JComboBox<String>	combobox;
    TableModel		model;
    DataTableModel	dmodel;

    result = null;
    actRow = getActualRow(row);
    if (column == 3) {
      if (!m_ClassAttributeEditors.containsKey(actRow)) {
	model = getModel();
	if (model instanceof SortableAndSearchableWrapperTableModel)
	  dmodel = (DataTableModel) ((SortableAndSearchableWrapperTableModel) model).getUnsortedModel();
	else
	  dmodel = (DataTableModel) model;
	cont = dmodel.getData().get(actRow);
	atts = new ArrayList<>();
	atts.add("");  // no class
	for (i = 0; i < cont.getData().numAttributes(); i++)
	  atts.add(cont.getData().attribute(i).name());
	Collections.sort(atts, new StringCompare());
	combobox = new JComboBox<>(atts.toArray(new String[atts.size()]));
	result   = new DefaultCellEditor(combobox);
	m_ClassAttributeEditors.put(actRow, result);
      }
      else {
	result = m_ClassAttributeEditors.get(actRow);
      }
    }

    // get default
    if (result == null)
      result = super.getCellEditor(row, column);

    return result;
  }
}
