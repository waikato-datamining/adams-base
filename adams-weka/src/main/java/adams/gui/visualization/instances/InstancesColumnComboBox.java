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
 * InstancesColumnComboBox.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.instances;

import adams.gui.core.BaseComboBox;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ComboBox that lists the attribute names of the associated Instances in
 * alphabetical order and when the user selects one, ensures that this 
 * column is visible.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class InstancesColumnComboBox
  extends BaseComboBox {

  /** for serialization. */
  private static final long serialVersionUID = 5256160332596403161L;
  
  /**
   * Container for storing column name and 
   */
  public static class ColumnContainer
    implements Serializable, Comparable<ColumnContainer> {
    
    /** for serialization. */
    private static final long serialVersionUID = 8359213625873465324L;

    /** the column name. */
    protected String m_Name;
    
    /** the lower case name. */
    protected String m_NameLowerCase;
    
    /** the column index. */
    protected int m_Column;
    
    /**
     * Initializes the container.
     * 
     * @param name	the name of the column
     * @param column	the column index
     */
    public ColumnContainer(String name, int column) {
      super();
      
      m_Name          = name;
      m_NameLowerCase = name.toLowerCase();
      m_Column        = column;
    }

    /**
     * Returns the name of the column.
     * 
     * @return		the name
     */
    public String getName() {
      return m_Name;
    }

    /**
     * Returns the name of the column in lowercase.
     * 
     * @return		the name in lowercase
     */
    public String getNameLowerCase() {
      return m_NameLowerCase;
    }
    
    /**
     * Returns the column index.
     * 
     * @return		the index
     */
    public int getColumn() {
      return m_Column;
    }
    
    /**
     * Compares itself to the other container.
     * 
     * @param o		the other container to compare against
     * @return		less than zero, zero or greater than zero if this
     * 			container is less than, equal or greater than the 
     * 			other one
     */
    @Override
    public int compareTo(ColumnContainer o) {
      int	result;
      
      if (o == null)
	return 1;
      
      result = getNameLowerCase().compareTo(o.getNameLowerCase());
      if (result == 0)
	result = Integer.compare(getColumn(), o.getColumn());
	
      return result;
    }
    
    /**
     * Returns the name and index of the column as string representation.
     * 
     * @return		the string representation
     */
    @Override
    public String toString() {
      return m_Name + " [" + (m_Column + 1) + "]";
    }
  }
  
  /** the associated table. */
  protected InstancesTable m_Table;
  
  /**
   * Initializes the combobox.
   * 
   * @param table	the associated table
   */
  public InstancesColumnComboBox(InstancesTable table) {
    super();
    
    m_Table = table;
    update();
    
    m_Table.getModel().addTableModelListener((TableModelEvent e) -> update());
    
    addActionListener((ActionEvent e) -> {
      if (getSelectedIndex() == -1)
	return;
      ColumnContainer cont = (ColumnContainer) getSelectedItem();
      int row = m_Table.getSelectedRow();
      if (row == -1)
	row = 0;
      int col = cont.getColumn();
      col++;  // row column
      m_Table.showCell(row, col);
    });
  }

  /**
   * Updates the content of the combobox.
   */
  protected synchronized void update() {
    List<ColumnContainer>	columns;
    int				i;
    Instances 			data;

    columns = new ArrayList<>();
    data    = m_Table.getInstances();
    if (data != null) {
      for (i = 0; i < data.numAttributes(); i++)
	columns.add(new ColumnContainer(data.attribute(i).name(), i));
      Collections.sort(columns);
    }
    setModel(new DefaultComboBoxModel<>(columns.toArray(new ColumnContainer[0])));
  }
}
