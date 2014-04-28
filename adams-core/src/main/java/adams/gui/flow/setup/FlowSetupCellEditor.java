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
 * FlowSetupCellEditor.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.setup;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * Custom cell renderer for the flow setups.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowSetupCellEditor
  extends DefaultCellEditor
  implements TableModelListener {
  
  /** for serialization. */
  private static final long serialVersionUID = -6644028610827361425L;

  /** the table model to use. */
  protected FlowSetupTableModel m_TableModel;
  
  /** the JCombobox used for displaying the flow setup names. */
  protected JComboBox m_ComboBox;
  
  /**
   * Initializes the cell editor.
   * 
   * @param model	the model to use
   */
  public FlowSetupCellEditor(FlowSetupTableModel model) {
    super(new JComboBox());
    
    m_ComboBox   = (JComboBox) getComponent();
    m_TableModel = model;
    m_TableModel.addTableModelListener(this);
    
    update();
  }
  
  /**
   * Updates the names listed in the combobox.
   */
  protected void update() {
    String[]	names;
    int		i;

    names    = new String[m_TableModel.getRowCount() + 1];
    names[0] = "";
    for (i = 0; i < m_TableModel.getRowCount(); i++)
      names[i + 1] = m_TableModel.getSetup(i).getName();
    
    m_ComboBox.setModel(new DefaultComboBoxModel(names));
  }
  
  /**
   * This fine grain notification tells listeners the exact range
   * of cells, rows, or columns that changed.
   * 
   * @param e		the event
   */
  public void tableChanged(TableModelEvent e) {
    update();
  }
}