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
 * InstanceTable.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.instance;

import javax.swing.table.TableModel;

import weka.core.Instances;
import adams.gui.core.SortableAndSearchableTable;

/**
 * A specialized table for displaying an Instances object.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceTable
  extends SortableAndSearchableTable {

  /** for serialization. */
  private static final long serialVersionUID = -4771959490685793427L;

  /**
   * Initializes the table.
   *
     * @param data	the underlying data
   */
  public InstanceTable(Instances data) {
    super(new InstanceTableModel(data));
  }

  /**
   * Returns the initial setting of whether to set optimal column widths.
   *
   * @return		true
   */
  protected boolean initialUseOptimalColumnWidths() {
    return true;
  }

  /**
   * Returns the initial setting of whether to sort new models.
   *
   * @return		true
   */
  protected boolean initialSortNewTableModel() {
    return true;
  }

  /**
   * Returns the class of the table model that the models need to be derived
   * from. The default implementation just returns TableModel.class
   *
   * @return		the class the models must be derived from
   */
  protected Class getTableModelClass() {
    return InstanceTableModel.class;
  }

  /**
   * Creates an empty default model.
   *
   * @return		the model
   */
  protected TableModel createDefaultDataModel() {
    return new InstanceTableModel(null);
  }

  /**
   * Sets the Instances object to display.
   *
   * @param data	the Instances object
   */
  public void setData(Instances data) {
    setUnsortedModel(new InstanceTableModel(data));
  }

  /**
   * Returns the underlying Instances object.
   *
   * @return		the Instances object
   */
  public Instances getData() {
    return ((InstanceTableModel) getUnsortedModel()).getData();
  }
}