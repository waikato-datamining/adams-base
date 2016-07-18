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
 * DataTableModel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.gui.core.AbstractBaseTableModel;
import adams.gui.tools.wekainvestigator.data.DataContainer;

import java.util.List;

/**
 * Model for displaying the loaded data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataTableModel
  extends AbstractBaseTableModel {

  private static final long serialVersionUID = 8586181476263855804L;

  /** the underlying data. */
  protected List<DataContainer> m_Data;

  /**
   * Initializes the model.
   *
   * @param data	the data to use
   */
  public DataTableModel(List<DataContainer> data) {
    super();
    m_Data = data;
  }

  /**
   * The number of datasets loaded.
   *
   * @return		the number of datasets
   */
  @Override
  public int getRowCount() {
    return m_Data.size();
  }

  /**
   * The number of columns.
   *
   * @return		the number of columns
   */
  @Override
  public int getColumnCount() {
    int	result;

    result = 0;
    result++;  // index
    result++;  // relation
    result++;  // class
    result++;  // source short
    result++;  // source

    return result;
  }

  @Override
  public String getColumnName(int column) {
    switch (column) {
      case 0:
	return "Index";
      case 1:
	return "Relation";
      case 2:
	return "Class";
      case 3:
	return "Source (short)";
      case 4:
	return "Source (full)";
      default:
	return null;
    }
  }

  /**
   * Returns the value at the specified position.
   *
   * @param rowIndex		the row
   * @param columnIndex	the column
   * @return			the value
   */
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    DataContainer	cont;

    cont = m_Data.get(rowIndex);

    switch (columnIndex) {
      case 0:
	return (rowIndex + 1);
      case 1:
	return cont.getData().relationName();
      case 2:
	return (cont.getData().classIndex() == -1) ? "<none>" : cont.getData().classAttribute().name();
      case 3:
	return cont.getSourceShort();
      case 4:
	return cont.getSourceFull();
      default:
	return null;
    }
  }
}
