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
 * AdjustableGridPanel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel with {@link GridLayout} that allows user to adjust layout.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AdjustableGridPanel
  extends BasePanel {

  private static final long serialVersionUID = -4160391815317620105L;

  /** the rows. */
  protected int m_Rows;

  /** the columns. */
  protected int m_Columns;

  /** the items to display. */
  protected List<Component> m_Items;

  /** the panel with the dimensions. */
  protected JPanel m_PanelDimensions;

  /** the spinner for the rows. */
  protected JSpinner m_SpinnerRows;

  /** the spinner for the columns. */
  protected JSpinner m_SpinnerColumns;

  /** the button for applying the dimensions. */
  protected JButton m_ButtonApply;

  /** the panel for the actual content. */
  protected JPanel m_PanelContent;

  /**
   * Initializes the panel with just one row.
   */
  public AdjustableGridPanel() {
    this(1, 0);
  }

  /**
   * Initializes the panel with specified dimensions, no items.
   *
   * @param rows	the number of rows
   * @param cols	the number of columns
   */
  public AdjustableGridPanel(int rows, int cols) {
    this(rows, cols, new Component[0]);
  }

  /**
   * Initializes the panel with specified dimensions and items.
   *
   * @param rows	the number of rows
   * @param cols	the number of columns
   * @param items	the items to display
   */
  public AdjustableGridPanel(int rows, int cols, Component[] items) {
    this(rows, cols, Arrays.asList(items));
  }

  /**
   * Initializes the panel with specified dimensions and items.
   *
   * @param rows	the number of rows
   * @param cols	the number of columns
   * @param items	the items to display
   */
  public AdjustableGridPanel(int rows, int cols, List<Component> items) {
    super();
    m_Items.addAll(items);
    setGrid(rows, cols);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Items = new ArrayList<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JLabel	labelRows;
    JLabel 	labelColumns;

    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelDimensions = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(m_PanelDimensions, BorderLayout.NORTH);

    m_SpinnerRows = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerRows.getModel()).setMinimum(0);
    ((JSpinner.DefaultEditor) m_SpinnerRows.getEditor()).getTextField().setColumns(4);
    labelRows = new JLabel("Rows");
    labelRows.setLabelFor(m_SpinnerRows);
    m_PanelDimensions.add(labelRows);
    m_PanelDimensions.add(m_SpinnerRows);

    m_SpinnerColumns = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerColumns.getModel()).setMinimum(0);
    ((JSpinner.DefaultEditor) m_SpinnerColumns.getEditor()).getTextField().setColumns(4);
    labelColumns = new JLabel("Columns");
    labelColumns.setLabelFor(m_SpinnerColumns);
    m_PanelDimensions.add(labelColumns);
    m_PanelDimensions.add(m_SpinnerColumns);

    m_ButtonApply = new JButton("Apply");
    m_ButtonApply.addActionListener((ActionEvent e) -> {
      int rows = ((Number) m_SpinnerRows.getValue()).intValue();
      int cols = ((Number) m_SpinnerColumns.getValue()).intValue();
      setGrid(rows, cols);
    });
    m_PanelDimensions.add(m_ButtonApply);

    m_PanelContent = new JPanel(new GridLayout());
    add(m_PanelContent, BorderLayout.CENTER);
  }

  /**
   * Updates the layout.
   */
  public void updateLayout() {
    m_PanelContent.removeAll();
    m_PanelContent.setLayout(new GridLayout(m_Rows, m_Columns));
    for (Component item : m_Items)
      m_PanelContent.add(item);
    if (getParent() != null) {
      invalidate();
      validate();
      repaint();
    }
  }

  /**
   * Removes all components.
   */
  public void clear() {
    m_Items.clear();
    updateLayout();
  }

  /**
   * Adds the component and updates the layout.
   *
   * @param comp	the component to add
   */
  public void addItem(Component comp) {
    m_Items.add(comp);
    updateLayout();
  }

  /**
   * Adds the component and updates the layout.
   *
   * @param index 	the index to add the component at
   * @param comp	the component to add
   */
  public void additem(int index, Component comp) {
    m_Items.add(index, comp);
    updateLayout();
  }

  /**
   * Removes the component and updates the layout.
   *
   * @param index 	the index of the component to remove
   */
  public void removeItem(int index) {
    m_Items.remove(index);
    updateLayout();
  }

  /**
   * Returns the specified component.
   *
   * @param index 	the index of the component to return
   * @return		the component
   */
  public Component getItem(int index) {
    return m_Items.get(index);
  }

  /**
   * Returns the number of components.
   *
   * @return		the number
   */
  public int numItems() {
    return m_Items.size();
  }

  /**
   * Updates the rows to display.
   *
   * @param value	the new number of rows
   */
  public void setRows(int value) {
    m_Rows = value;
    m_SpinnerRows.setValue(m_Rows);
    updateLayout();
  }

  /**
   * Returns the number of rows displayed.
   *
   * @return		the rows
   */
  public int getRows() {
    return m_Rows;
  }

  /**
   * Updates the columns to display.
   *
   * @param value	the new number of columns
   */
  public void setColumns(int value) {
    m_Columns = value;
    m_SpinnerColumns.setValue(m_Columns);
    updateLayout();
  }

  /**
   * Returns the number of columns displayed.
   *
   * @return		the columns
   */
  public int getColumns() {
    return m_Columns;
  }

  /**
   * Updates rows and columns to display.
   *
   * @param rows  	the rows
   * @param cols	the columns
   */
  public void setGrid(int rows, int cols) {
    m_Rows    = rows;
    m_Columns = cols;
    m_SpinnerRows.setValue(m_Rows);
    m_SpinnerColumns.setValue(m_Columns);
    updateLayout();
  }
}
