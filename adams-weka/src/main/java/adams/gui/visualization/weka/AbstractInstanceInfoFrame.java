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
 * AbstractInstanceInfoFrame.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.weka;

import adams.core.Constants;
import adams.core.Shortening;
import adams.data.weka.ArffUtils;
import adams.env.Environment;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.JTableHelper;
import adams.gui.core.SortableAndSearchableTable;
import weka.core.Attribute;
import weka.core.Instances;
import weka.gui.arffviewer.ArffSortedTableModel;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Vector;

/**
 * Ancestor for frames for displaying information on the displayed data, with
 * some more domain-specific functionality.
 * <br><br>
 * Methods to implement:
 * <ul>
 *   <li><code>getActions()</code> - returns an array of String objects to
 *       display in the combobox that allows the user to choose an action.
 *       (<code>m_ComboBoxActions</code>)</li>
 *   <li><code>getActionMethod(String)</code> - returns the Method object (no
 *       parameters allowed) that is associated with the given action string.
 *       <code>locateMethod(String)</code> can be used to determine the
 *       reflection object.</li>
 *   <li><code>generateNumPointsLabel(int)</code> - generates the text to
 *       display in the label for the number of data points.
 *       (<code>m_LabelNumPoints</code>)</li>
 * </ul>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInstanceInfoFrame
  extends weka.gui.visualize.InstanceInfoFrame {

  /** for serialization. */
  private static final long serialVersionUID = 1811456515654254510L;

  /** the tabbed pane for displaying the data. */
  protected BaseTabbedPane m_TabbedPane;

  /** the panel for the text tab. */
  protected BasePanel m_PanelTextAll;

  /** the number of data points. */
  protected JLabel m_LabelTextNumPoints;

  /** the combobox with all the instances. */
  protected JComboBox m_ComboBoxTextInstances;

  /** the combobox with all the actions. */
  protected JComboBox m_ComboBoxTextActions;

  /** the button for displaying the instances. */
  protected JButton m_ButtonTextGo;

  /** the panel with buttons etc. */
  protected JPanel m_PanelTextAction;

  /** the table with the dataset. */
  protected SortableAndSearchableTable m_TableData;

  /** the panel holding the table and buttons for the dataset. */
  protected BasePanel m_PanelData;

  /** the combobox with all the datasets. */
  protected JComboBox m_ComboBoxData;

  /** the panel for the combobox listing the datasets. */
  protected BasePanel m_PanelComboBoxData;

  /** the combobox with all the actions. */
  protected JComboBox m_ComboBoxDataActions;

  /** the button for displaying the instances. */
  protected JButton m_ButtonDataGo;

  /** the panel with buttons etc. */
  protected JPanel m_PanelDataAction;

  /**
   * Sets up the GUI components.
   */
  protected void initGUI() {
    super.initGUI();

    setTitle(getDialogTitle());

    // tabs
    m_TabbedPane = new BaseTabbedPane();

    m_PanelTextAll = new BasePanel(new BorderLayout());
    m_PanelTextAll.add(new BaseScrollPane(m_TextInfo), BorderLayout.CENTER);
    m_TabbedPane.addTab("Text", m_PanelTextAll);

    getContentPane().add(m_TabbedPane, BorderLayout.CENTER);

    m_PanelData = new BasePanel(new BorderLayout());
    m_TabbedPane.addTab("Table", m_PanelData);

    // data
    m_TableData = new SortableAndSearchableTable();
    m_TableData.setAutoResizeMode(SortableAndSearchableTable.AUTO_RESIZE_OFF);
    m_TableData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_TableData.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	m_ButtonDataGo.setEnabled(m_TableData.getSelectedRowCount() == 1);
      }
    });
    m_PanelData.add(new BaseScrollPane(m_TableData), BorderLayout.CENTER);

    m_ComboBoxData      = new JComboBox();
    m_ComboBoxData.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	updateDataTable();
      }
    });
    m_PanelComboBoxData = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelComboBoxData.add(new JLabel("Data"));
    m_PanelComboBoxData.add(m_ComboBoxData);
    m_PanelData.add(m_PanelComboBoxData, BorderLayout.NORTH);

    m_PanelDataAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_PanelData.add(m_PanelDataAction, BorderLayout.SOUTH);

    m_ComboBoxDataActions = new JComboBox(getActions());
    m_ComboBoxDataActions.setSelectedIndex(0);
    m_PanelDataAction.add(m_ComboBoxDataActions);

    m_ButtonDataGo = new JButton("Go");
    m_ButtonDataGo.setMnemonic('G');
    m_ButtonDataGo.setEnabled(false);
    m_ButtonDataGo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_ComboBoxDataActions.getSelectedIndex() == -1)
	  return;
	Method method = getActionMethod((String) m_ComboBoxDataActions.getSelectedItem());
	if (method != null) {
	  try {
	    method.invoke(AbstractInstanceInfoFrame.this, new Object[]{m_TabbedPane.getSelectedIndex()});
	  }
	  catch (Exception ex) {
	    ex.printStackTrace();
	  }
	}
	else {
	  throw new IllegalStateException(
	      "Unhandled action '" + m_ComboBoxDataActions.getSelectedItem() + "'!");
	}
      }
    });
    m_PanelDataAction.add(m_ButtonDataGo);

    // text
    m_PanelTextAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_PanelTextAll.add(m_PanelTextAction, BorderLayout.SOUTH);

    m_LabelTextNumPoints = new JLabel();
    m_PanelTextAction.add(m_LabelTextNumPoints);

    m_ComboBoxTextInstances = new JComboBox();
    m_PanelTextAction.add(m_ComboBoxTextInstances);

    m_ComboBoxTextActions = new JComboBox(getActions());
    m_ComboBoxTextActions.setSelectedIndex(0);
    m_PanelTextAction.add(m_ComboBoxTextActions);

    m_ButtonTextGo = new JButton("Go");
    m_ButtonTextGo.setMnemonic('G');
    m_ButtonTextGo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_ComboBoxTextActions.getSelectedIndex() == -1)
	  return;
	Method method = getActionMethod((String) m_ComboBoxTextActions.getSelectedItem());
	if (method != null) {
	  try {
	    method.invoke(AbstractInstanceInfoFrame.this, new Object[]{m_TabbedPane.getSelectedIndex()});
	  }
	  catch (Exception ex) {
	    ex.printStackTrace();
	  }
	}
	else {
	  throw new IllegalStateException(
	      "Unhandled action '" + m_ComboBoxTextActions.getSelectedItem() + "'!");
	}
      }
    });
    m_PanelTextAction.add(m_ButtonTextGo);

    setSize(400, 600);
  }

  /**
   * Returns the title for the dialog.
   *
   * @return		the dialog
   */
  protected String getDialogTitle() {
    return Environment.getInstance().getProject() + ": Instance info";
  }

  /**
   * Generates the string for the label displaying the number of points that
   * are currently selected.
   *
   * @param numPoints	the number of points currently being displayed
   * @return		the generated string
   */
  protected abstract String generateNumPointsLabel(int numPoints);

  /**
   * Returns the available actions to list.
   *
   * @return		the names of the actions
   * @see		#m_ComboBoxTextActions
   */
  protected abstract String[] getActions();

  /**
   * Returns the method associated with the specified action.
   *
   * @param action	the action to retrieve the method for
   * @return		the associated method, null if not available
   * @see		#locateMethod(String)
   */
  protected abstract Method getActionMethod(String action);

  /**
   * Locates the method with the specified name (method is expected to take
   * no parameters).
   *
   * @param name	the name of the method
   * @return		the method, null if none found
   */
  protected Method locateMethod(String name) {
    Method	result;

    result = null;

    try {
      result = getClass().getMethod(name, new Class[]{Integer.TYPE});
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Extracts the database ID from a string in the comboxbox.
   *
   * @param idStr	the string from the combobox
   * @return		the ID
   */
  protected int extractDatabaseID(String idStr) {
    return new Integer(idStr.split(":")[1].trim());
  }

  /**
   * Returns the index of the column with the database ID.
   *
   * @return		the column index, -1 if not found
   */
  protected int getDatabaseIDColumnIndex() {
    int		result;
    Attribute	att;

    result = -1;

    if (m_ComboBoxData.getSelectedIndex() > -1) {
      att = m_Data.get(m_ComboBoxData.getSelectedIndex()).attribute(ArffUtils.getDBIDName());
      if (att != null)
	result = att.index() + 1;   // +1 because of "row" column
    }

    return result;
  }

  /**
   * Returns the database ID of the currently selected item.
   *
   * @param tabIndex	the currently selected tab
   * @return		the database ID, NO_ID if none selected
   * @see		Constants#NO_ID
   */
  protected int getSelectedDatabaseID(int tabIndex) {
    int		result;
    int		col;

    result = Constants.NO_ID;

    // text
    if (tabIndex == 0) {
      if (m_ComboBoxTextInstances.getSelectedIndex() > -1)
	result = extractDatabaseID(m_ComboBoxTextInstances.getSelectedItem().toString());
    }
    // data
    else if (tabIndex == 1) {
      if (m_TableData.getSelectedRow() > -1) {
	col = getDatabaseIDColumnIndex();
	if (col > -1)
	  result = new Double(m_TableData.getValueAt(m_TableData.getSelectedRow(), col).toString()).intValue();
      }
    }
    else {
      System.err.println("Unhandled tab index: " + tabIndex);
    }

    return result;
  }

  /**
   * Returns the database IDs of the currently selected items.
   *
   * @param tabIndex	the currently selected tab
   * @return		array of database IDs, length 0 if none selected
   */
  protected int[] getSelectedDatabaseIDs(int tabIndex) {
    int[]	result;
    int		id;
    int[]	rows;
    int		i;
    int		col;

    result = new int[0];

    if (tabIndex == 0) {
      id = getSelectedDatabaseID(tabIndex);
      if (id != Constants.NO_ID)
	result = new int[]{id};
    }
    else if (tabIndex == 1) {
      rows = m_TableData.getSelectedRows();
      if (rows.length != 0) {
	col = getDatabaseIDColumnIndex();
	if (col != -1) {
	  result = new int[rows.length];
	  for (i = 0; i < rows.length; i++)
	    result[i] = new Double(m_TableData.getValueAt(rows[i], col).toString()).intValue();
	}
      }
    }
    else {
      System.err.println("Unhandled tab index: " + tabIndex);
    }

    return result;
  }

  /**
   * Returns all database IDs.
   *
   * @param tabIndex	the currently selected tab
   * @return		array of database IDs, length 0 if none available
   */
  protected int[] getAllDatabaseIDs(int tabIndex) {
    int[]	result;
    int		i;
    int		col;

    result = new int[0];

    if (tabIndex == 0) {
      result = new int[m_ComboBoxTextInstances.getItemCount()];
      for (i = 0; i < result.length; i++)
	result[i] = extractDatabaseID(m_ComboBoxTextInstances.getItemAt(i).toString());
    }
    else if (tabIndex == 1) {
      col = getDatabaseIDColumnIndex();
      if (col != -1) {
	result = new int[m_TableData.getRowCount()];
	for (i = 0; i < result.length; i++)
	  result[i] = new Double(m_TableData.getValueAt(i, col).toString()).intValue();
      }
    }
    else {
      System.err.println("Unhandled tab index: " + tabIndex);
    }

    return result;
  }

  /**
   * Sets the underlying data.
   *
   * @param data	the data of the info text
   */
  public void setInfoData(Vector<Instances> data) {
    Vector<String>	list;
    int			i;
    int			n;
    Attribute		att;
    long		id;
    HashSet<Long>	ids;
    String[]		datasets;

    super.setInfoData(data);

    // data
    datasets = new String[data.size()];
    for (i = 0; i < data.size(); i++)
      datasets[i] = (i+1) + " :" + Shortening.shortenEnd(data.get(i).relationName(), 30);
    m_ComboBoxData.setModel(new DefaultComboBoxModel(datasets));
    m_ComboBoxData.setSelectedIndex(0);
    m_PanelComboBoxData.setVisible(data.size() > 1);
    m_TableData.clearSelection();
    i = getDatabaseIDColumnIndex();
    if (i > -1)
      m_TableData.setOptimalColumnWidth(i);

    // text
    list = new Vector<String>();
    ids  = new HashSet<Long>();
    for (i = 0; i < data.size(); i++) {
      // database ID present?
      att = data.get(i).attribute(ArffUtils.getDBIDName());
      if (att == null)
	continue;
      if (!att.isNumeric())
	continue;

      for (n = 0; n < data.get(i).numInstances(); n++) {
	id = ((long) data.get(i).instance(n).value(att));
	if (ids.contains(id))
	  continue;
	ids.add(id);
	list.add(i + ": " + id);
      }
    }

    m_LabelTextNumPoints.setText(generateNumPointsLabel(list.size()));

    m_ComboBoxTextInstances.setModel(new DefaultComboBoxModel(list));
    m_ComboBoxTextInstances.setEnabled(list.size() > 0);
    m_ButtonTextGo.setEnabled(list.size() > 0);
    if (list.size() > 0)
      m_ComboBoxTextInstances.setSelectedIndex(0);
  }

  /**
   * Updates the data in the data table.
   */
  protected void updateDataTable() {
    TableModel	model;

    if (m_ComboBoxData.getSelectedIndex() == -1)
      model = new DefaultTableModel();
    else
      model = new ArffSortedTableModel(m_Data.get(m_ComboBoxData.getSelectedIndex()));

    m_TableData.setModel(model);
    JTableHelper.setOptimalHeaderWidth(m_TableData);
  }
}
