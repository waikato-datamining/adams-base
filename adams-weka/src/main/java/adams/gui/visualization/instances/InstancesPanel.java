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
 * InstancesPanel.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances;

import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.SearchEvent;
import weka.core.Instances;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Panel displaying an Instances table. Complete with combobox to jump to
 * attributes and a search field.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InstancesPanel
  extends BasePanel {

  private static final long serialVersionUID = -6517998516164369299L;

  /** the table. */
  protected InstancesTable m_Table;

  /** the search panel. */
  protected SearchPanel m_PanelSearch;

  /** for listing the column names. */
  protected InstancesColumnComboBox m_ColumnComboBox;

  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    JPanel panel;
    JLabel label;

    super.initGUI();

    setLayout(new BorderLayout());

    m_Table = new InstancesTable(new InstancesTableModel());
    add(new BaseScrollPane(m_Table), BorderLayout.CENTER);

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_ColumnComboBox = new InstancesColumnComboBox(m_Table);
    label = new JLabel("Jump to");
    label.setLabelFor(m_ColumnComboBox);
    label.setDisplayedMnemonic('J');
    panel.add(label);
    panel.add(m_ColumnComboBox);
    add(panel, BorderLayout.NORTH);

    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, true);
    m_PanelSearch.addSearchListener((SearchEvent e) ->
      m_Table.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
    add(m_PanelSearch, BorderLayout.SOUTH);
  }

  /**
   * Returns the underlying table.
   *
   * @return		the table
   */
  public InstancesTable getTable() {
    return m_Table;
  }

  /**
   * Returns the underlying search panel.
   *
   * @return		the panel
   */
  public SearchPanel getSearchPanel() {
    return m_PanelSearch;
  }

  /**
   * Sets the instances to display.
   *
   * @param value	the data
   */
  public void setInstances(Instances value) {
    m_Table.setInstances(value);
    m_ColumnComboBox.update();
  }

  /**
   * Returns the currently displayed data.
   *
   * @return		the data
   */
  public Instances getInstances() {
    return m_Table.getInstances();
  }

  /**
   * Sets the model to use.
   *
   * @param model        the model to display
   */
  public synchronized void setModel(TableModel model) {
    m_Table.setModel(model);
    update();
  }

  /**
   * Returns the model in use.
   *
   * @return		the model
   */
  public TableModel getModel() {
    return m_Table.getModel();
  }

  /**
   * Updates the combobox.
   */
  public void update() {
    m_ColumnComboBox.update();
  }
}
