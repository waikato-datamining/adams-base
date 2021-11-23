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
 * AbstractFlowExecutionListenerWithTable.java
 * Copyright (C) 2013-2021 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SortableAndSearchableTable;

import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;

/**
 * Ancestor for graphical listeners that display their data in a table.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFlowExecutionListenerWithTable
  extends AbstractGraphicalFlowExecutionListener {
  
  /** for serialization. */
  private static final long serialVersionUID = 9209750914200226213L;
  
  /** the table to update. */
  protected SortableAndSearchableTable m_Table;

  /** the file chooser for saving the spreadsheet. */
  protected transient SpreadSheetFileChooser m_FileChooser;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Table = null;
  }

  /**
   * Creates a new table model with the current data.
   * 
   * @return		the model with the current data
   */
  protected abstract TableModel createTableModel();
  
  /**
   * Returns the panel to use.
   * 
   * @return		the panel, null if none available
   */
  @Override
  public BasePanel newListenerPanel() {
    BasePanel	result;
    
    result  = new BasePanel(new BorderLayout());
    m_Table = new SortableAndSearchableTable(createTableModel());
    m_Table.setShowSimpleCellPopupMenu(true);
    m_Table.setShowSimpleHeaderPopupMenu(true);
    result.add(new BaseScrollPane(m_Table), BorderLayout.CENTER);
    
    return result;
  }

  /**
   * Returns the filechooser for saving the table as spreadsheet.
   *
   * @return		the filechooser
   */
  protected synchronized SpreadSheetFileChooser getFileChooser() {
    if (m_FileChooser == null) {
      m_FileChooser = new SpreadSheetFileChooser();
      m_FileChooser.setMultiSelectionEnabled(false);
    }

    return m_FileChooser;
  }

  /**
   * Returns the table as spreadsheet.
   *
   * @return		the spread sheet
   */
  public SpreadSheet getSheet() {
    SpreadSheet	result;
    Row		row;
    int		i;
    int		n;
    Object	value;
    Cell	cell;
    
    result = new DefaultSpreadSheet();
    result.setDataRowClass(DenseDataRow.class);
    row    = result.getHeaderRow();
    for (n = 0; n < m_Table.getColumnCount(); n++)
      row.addCell("" + n).setContent(m_Table.getColumnName(n));
    
    for (i = 0; i < m_Table.getRowCount(); i++) {
      row = result.addRow();
      for (n = 0; n < m_Table.getColumnCount(); n++) {
	value = m_Table.getValueAt(i, n);
	cell  = row.addCell(n);
	if (value == null)
	  cell.setMissing();
	else
	  cell.setContent(value.toString());
      }
    }
    
    return result;
  }

  /**
   * Updates the table in the GUI with a new table model.
   * 
   * @see	AbstractFlowExecutionListenerWithTable#createTableModel()
   */
  @Override
  protected void updateGUI() {
    Runnable	run;

    if (m_Table != null) {
      run = new Runnable() {
	@Override
	public void run() {
	  m_Table.setModel(createTableModel());
	}
      };
      SwingUtilities.invokeLater(run);
    }
  }
}
