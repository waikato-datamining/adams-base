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
 * SpreadSheetColumnComboBox.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;

/**
 * ComboBox that lists the column names of the associated spreadsheet in
 * alphabetical order and when the user selects one, ensures that this 
 * column is visible.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetColumnComboBox
  extends JComboBox {

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
	result = new Integer(getColumn()).compareTo(o.getColumn());
	
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
  protected SpreadSheetTable m_Table;
  
  /**
   * Initializes the combobox.
   * 
   * @param table	the associated table
   */
  public SpreadSheetColumnComboBox(SpreadSheetTable table) {
    super();
    
    m_Table = table;
    update();
    
    m_Table.getModel().addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
	update();
      }
    });
    
    addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (getSelectedIndex() == -1)
	  return;
	ColumnContainer cont = (ColumnContainer) getSelectedItem();
	int row = m_Table.getSelectedRow();
	if (row == -1)
	  row = 0;
	int col = cont.getColumn();
	if (m_Table.getShowRowColumn())
	  col++;
	m_Table.showCell(row, col);
      }
    });
  }

  /**
   * Updates the content of the combobox.
   */
  protected synchronized void update() {
    List<ColumnContainer>	columns;
    int				i;
    Row				row;
    Cell			cell;
    
    columns = new ArrayList<ColumnContainer>();
    row     = m_Table.toSpreadSheet().getHeaderRow();
    for (i = 0; i < row.getCellCount(); i++) {
      if (!row.hasCell(i) || row.getCell(i).isMissing())
	continue;
      cell = row.getCell(i);
      columns.add(new ColumnContainer(cell.getContent(), i));
    }
    Collections.sort(columns);
    setModel(new DefaultComboBoxModel<ColumnContainer>(columns.toArray(new ColumnContainer[columns.size()])));
  }
}
