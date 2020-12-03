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
 *    AttributeSelectionPanel.java
 *    Copyright (C) 1999-2019 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.tools.wekainvestigator.tab.preprocesstab;

import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTable;
import adams.gui.core.FilterPanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.SortableAndSearchableTableWithButtons;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Attribute;
import weka.core.Instances;

import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Creates a panel that displays the attributes contained in a set of instances,
 * letting the user toggle whether each attribute is selected or not (eg: so
 * that unselected attributes can be removed before classification). <br>
 * Besides the All, None and Invert button one can also choose attributes which
 * names match a regular expression (Pattern button). E.g. for removing all
 * attributes that contain an ID and therefore unwanted information, one can
 * match all names that contain "id" in the name:<br>
 *
 * <pre>
 * (.*_id_.*|.*_id$|^id$)
 * </pre>
 *
 * This does not match e.g. "humidity", which could be an attribute we would
 * like to keep.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AttributeSelectionPanel
  extends BasePanel
  implements ListSelectionListener {

  /** for serialization */
  private static final long serialVersionUID = 627131485290359194L;

  /**
   * A table model that looks at the names of attributes and maintains a list of
   * attributes that have been "selected".
   */
  public static class AttributeTableModel
    extends AbstractTableModel {

    /** for serialization */
    private static final long serialVersionUID = -4152987434024338064L;

    /** The instances who's attribute structure we are reporting */
    protected Instances m_Instances;

    /**
     * Creates the tablemodel with the given set of instances.
     *
     * @param instances the initial set of Instances
     */
    public AttributeTableModel(Instances instances) {
      setInstances(instances);
    }

    /**
     * Sets the tablemodel to look at a new set of instances.
     *
     * @param instances the new set of Instances.
     */
    public void setInstances(Instances instances) {
      m_Instances = instances;
      fireTableStructureChanged();
    }

    /**
     * Returns the underlying instances.
     *
     * @return		the data
     */
    public Instances getInstances() {
      return m_Instances;
    }

    /**
     * Gets the number of attributes.
     *
     * @return the number of attributes.
     */
    @Override
    public int getRowCount() {
      if (m_Instances == null)
	return 0;
      else
	return m_Instances.numAttributes();
    }

    /**
     * Gets the number of columns: 3
     *
     * @return 3
     */
    @Override
    public int getColumnCount() {
      return 3;
    }

    /**
     * Gets a table cell
     *
     * @param row the row index
     * @param column the column index
     * @return the value at row, column
     */
    @Override
    public Object getValueAt(int row, int column) {
      if (m_Instances == null)
        return null;
      if (row >= m_Instances.numAttributes())
	return null;
      switch (column) {
	case 0:
	  return (row + 1);
	case 1:
	  return m_Instances.attribute(row).name();
	case 2:
	  return Attribute.typeToString(m_Instances.attribute(row).type());
	default:
	  return null;
      }
    }

    /**
     * Gets the name for a column.
     *
     * @param column the column index.
     * @return the name of the column.
     */
    @Override
    public String getColumnName(int column) {
      switch (column) {
	case 0:
	  return "No.";
	case 1:
	  return "Name";
	case 2:
	  return "Type";
	default:
	  return null;
      }
    }

    /**
     * Gets the class of elements in a column.
     *
     * @param col the column index.
     * @return the class of elements in the column.
     */
    @Override
    public Class<?> getColumnClass(int col) {
      if (m_Instances == null)
        return Object.class;
      else
	return getValueAt(0, col).getClass();
    }
  }

  /** to select all attributes */
  protected BaseButton m_ButtonAll;

  /** to deselect all attributes */
  protected BaseButton m_ButtonNone;

  /** to invert the current selection */
  protected BaseButton m_ButtonInvert;

  /** for entering a regular expression for selection */
  protected BaseButton m_ButtonPattern;

  /** the filter panel. */
  protected FilterPanel m_PanelFilter;

  /** The table displaying attribute names and selection status */
  protected SortableAndSearchableTableWithButtons m_Table;

  /** The table model containing attribute names and selection status */
  protected AttributeTableModel m_Model;

  /** The current regular expression. */
  protected String m_PatternRegEx;

  /** the listeners for changes in the selection. */
  protected Set<ListSelectionListener> m_SelectionListeners;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_PatternRegEx       = "";
    m_SelectionListeners = new HashSet<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_Table = new SortableAndSearchableTableWithButtons();
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_Table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_Table.getSelectionModel().addListSelectionListener(this);
    add(m_Table, BorderLayout.CENTER);

    m_ButtonAll = new BaseButton("All");
    m_ButtonAll.setToolTipText("Selects all attributes");
    m_ButtonAll.addActionListener((ActionEvent e) -> m_Table.selectAll());
    m_Table.addToButtonsPanel(m_ButtonAll);

    m_ButtonNone = new BaseButton("None");
    m_ButtonNone.setToolTipText("Unselects all attributes");
    m_ButtonNone.addActionListener((ActionEvent e) -> m_Table.selectNone());
    m_Table.addToButtonsPanel(m_ButtonNone);

    m_ButtonInvert = new BaseButton("Invert");
    m_ButtonInvert.setToolTipText("Inverts the current attribute selection");
    m_ButtonInvert.addActionListener((ActionEvent e) -> m_Table.invertSelection());
    m_Table.addToButtonsPanel(m_ButtonInvert);

    m_ButtonPattern = new BaseButton("Pattern");
    m_ButtonPattern.setToolTipText("Selects all attributes that match a reg. expression");
    m_ButtonPattern.addActionListener((ActionEvent e) -> {
      String patternStr = GUIHelper.showInputDialog(m_ButtonPattern.getParent(),
	"Enter a regular expression", m_PatternRegEx);
      if (patternStr != null) {
	try {
	  Pattern.compile(patternStr);
	  m_PatternRegEx = patternStr;
	  Pattern pattern = Pattern.compile(patternStr);
	  TIntList rows = new TIntArrayList();
	  for (int i = 0; i < m_Model.getInstances().numAttributes(); i++) {
	    if (pattern.matcher(m_Model.getInstances().attribute(i).name()).matches())
	      rows.add(m_Table.getDisplayRow(i));
	  }
	  m_Table.setSelectedRows(rows.toArray());
	}
	catch (Exception ex) {
	  GUIHelper.showErrorMessage(m_ButtonPattern.getParent(), "'" + patternStr
	      + "' is not a valid regular expression!", ex,
	    "Error in Pattern...");
	}
      }
    });
    m_Table.addToButtonsPanel(m_ButtonPattern);

    m_PanelFilter = new FilterPanel(FilterPanel.HORIZONTAL);
    m_PanelFilter.setToolTipText("For filtering the attribute names");
    m_PanelFilter.addChangeListener((ChangeEvent e) -> search());
    m_PanelFilter.setPreferredSize(new Dimension(150, GUIHelper.getPreferredButtonHeight()));
    m_Table.addToButtonsPanel(m_PanelFilter);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateWidgets();
  }

  /**
   * Updates the enabled state of the buttons/combobox.
   */
  protected void updateWidgets() {
    boolean 	dataLoaded;

    dataLoaded = (getInstances() != null);

    m_ButtonAll.setEnabled(dataLoaded);
    m_ButtonNone.setEnabled(dataLoaded);
    m_ButtonInvert.setEnabled(dataLoaded);
    m_ButtonPattern.setEnabled(dataLoaded);
    m_PanelFilter.setEnabled(dataLoaded);
  }

  /**
   * Sets the instances to display the attribute names for.
   *
   * @param data  	the instances
   */
  public void setInstances(Instances data) {
    TableColumnModel 	colModel;

    m_Model = new AttributeTableModel(data);
    m_Table.setModel(m_Model);

    colModel = m_Table.getColumnModel();
    colModel.getColumn(0).setMinWidth(30);
    colModel.getColumn(1).setMinWidth(100);

    updateWidgets();
    if (!m_PanelFilter.getFilter().isEmpty())
      search();
  }

  /**
   * Returns the currently displayed instances.
   *
   * @return		the instances, null if none available
   */
  public Instances getInstances() {
    if (m_Model == null)
      return null;
    else
      return m_Model.getInstances();
  }

  /**
   * Searches the panel with the filter.
   */
  protected void search() {
    if (getInstances() == null)
      return;

    m_Table.search(m_PanelFilter.getFilter(), false);
  }

  /**
   * Gets an array containing the indices of all selected rows.
   *
   * @return the array of selected indices.
   */
  public int[] getSelectedRows() {
    return m_Table.getSelectedRows();
  }

  /**
   * Gets an array containing the indices of all selected (ie checked) attributes.
   *
   * @return the array of selected indices, null if no model present.
   */
  public int[] getSelectedAttributes() {
    int[]	rows;
    int		i;

    if (m_Model == null)
      return null;

    rows = m_Table.getSelectedRows();
    for (i = 0; i < rows.length; i++)
      rows[i] = m_Table.getActualRow(rows[i]);

    return rows;
  }

  /**
   * Returns the table.
   *
   * @return		the table
   */
  public BaseTable getTable() {
    return m_Table.getComponent();
  }

  /**
   * Get the table model in use (or null if no instances have been set yet).
   *
   * @return the table model in use or null if no instances have been seen yet.
   */
  public TableModel getTableModel() {
    return m_Model;
  }

  /**
   * Gets the selection model used by the table.
   *
   * @return a value of type 'ListSelectionModel'
   */
  public ListSelectionModel getSelectionModel() {
    return m_Table.getSelectionModel();
  }

  /**
   * Add a listener to the list that's notified each time a change
   * to the selection occurs.
   *
   * @param l the ListSelectionListener
   */
  public void addSelectionListener(ListSelectionListener l) {
    m_SelectionListeners.add(l);
  }

  /**
   * Remove a listener from the list that's notified each time a
   * change to the selection occurs.
   *
   * @param l the ListSelectionListener
   */
  public void removeSelectionListener(ListSelectionListener l) {
    m_SelectionListeners.remove(l);
  }

  /**
   * Notifies all listeners that the selection has changed.
   */
  protected void notifySelectionListeners(ListSelectionEvent e) {
    for (ListSelectionListener l: m_SelectionListeners)
      l.valueChanged(e);
  }

  /**
   * Called whenever the value of the selection changes.
   *
   * @param e the event that characterizes the change.
   * @see #notifySelectionListeners(ListSelectionEvent)
   */
  public void valueChanged(ListSelectionEvent e) {
    notifySelectionListeners(e);
  }
}
