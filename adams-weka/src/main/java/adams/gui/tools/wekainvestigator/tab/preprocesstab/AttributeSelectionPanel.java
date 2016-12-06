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
 *    Copyright (C) 1999-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.tools.wekainvestigator.tab.preprocesstab;

import adams.gui.core.BaseTable;
import adams.gui.core.GUIHelper;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Instances;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
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
 * @version $Revision: 10216 $
 */
public class AttributeSelectionPanel
  extends JPanel
  implements TableModelListener {

  /** for serialization */
  private static final long serialVersionUID = 627131485290359194L;

  /**
   * A table model that looks at the names of attributes and maintains a list of
   * attributes that have been "selected".
   */
  class AttributeTableModel extends AbstractTableModel {

    /** for serialization */
    private static final long serialVersionUID = -4152987434024338064L;

    /** The instances who's attribute structure we are reporting */
    protected Instances m_Instances;

    /** The flag for whether the instance will be included */
    protected HashMap<String,Boolean> m_Selected;

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
      m_Selected  = new HashMap<>();
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
      if (row >= m_Instances.numAttributes())
        return null;
      switch (column) {
        case 0:
          return (row + 1);
        case 1:
          return m_Selected.containsKey(m_Instances.attribute(row).name()) && m_Selected.get(m_Instances.attribute(row).name());
        case 2:
          return m_Instances.attribute(row).name();
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
          return "";
        case 2:
          return "Name";
        default:
          return null;
      }
    }

    /**
     * Sets the value at a cell.
     *
     * @param value the new value.
     * @param row the row index.
     * @param col the column index.
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
      if (col == 1) {
	m_Selected.put(m_Instances.attribute(row).name(), (Boolean) value);
        fireTableRowsUpdated(0, getRowCount());
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
      return getValueAt(0, col).getClass();
    }

    /**
     * Returns true if the column is the "selected" column.
     *
     * @param row ignored
     * @param col the column index.
     * @return true if col == 1.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
      return (col == 1);
    }

    /**
     * Gets an array containing the indices of all selected attributes.
     *
     * @return the array of selected indices.
     */
    public int[] getSelectedAttributes() {
      TIntList		result;
      int		i;
      String		name;

      result = new TIntArrayList();
      if (m_Instances != null) {
	for (i = 0; i < m_Instances.numAttributes(); i++) {
	  name = m_Instances.attribute(i).name();
	  if (m_Selected.containsKey(name) && m_Selected.get(name))
	    result.add(i);
	}
      }

      return result.toArray();
    }

    /**
     * Sets the state of all attributes to selected.
     */
    public void selectAll() {
      if (m_Instances == null)
	return;
      for (int i = 0; i < m_Instances.numAttributes(); i++)
        m_Selected.put(m_Instances.attribute(i).name(), true);
      fireTableRowsUpdated(0, getRowCount());
    }

    /**
     * Deselects all attributes.
     */
    public void selectNone() {
      if (m_Instances == null)
	return;
      for (int i = 0; i < m_Instances.numAttributes(); i++)
        m_Selected.put(m_Instances.attribute(i).name(), false);
      fireTableRowsUpdated(0, getRowCount());
    }

    /**
     * Inverts the selected status of each attribute.
     */
    public void invert() {
      boolean	current;
      String	name;

      if (m_Instances == null)
	return;
      for (int i = 0; i < m_Instances.numAttributes(); i++) {
	name    = m_Instances.attribute(i).name();
	current = m_Selected.containsKey(name) && m_Selected.get(name);
	m_Selected.put(name, !current);
      }
      fireTableRowsUpdated(0, getRowCount());
    }

    /**
     * applies the perl regular expression pattern to select the attribute names
     * (expects a valid reg expression!)
     *
     * @param pattern a perl reg. expression
     */
    public void pattern(String pattern) {
      String	name;

      if (m_Instances == null)
	return;
      for (int i = 0; i < getRowCount(); i++) {
	name = m_Instances.attribute(i).name();
	m_Selected.put(name, Pattern.matches(pattern, name));
      }
      fireTableRowsUpdated(0, getRowCount());
    }

    public void setSelectedAttributes(boolean[] selected) throws Exception {
      if (m_Instances == null)
	return;
      if (selected.length != getRowCount())
        throw new Exception(
	  "Supplied array does not have the same number of elements as there are attributes!");
      for (int i = 0; i < selected.length; i++)
        m_Selected.put(m_Instances.attribute(i).name(), selected[i]);
      fireTableRowsUpdated(0, getRowCount());
    }
  }

  /** Press to select all attributes */
  protected JButton m_ButtonAll = new JButton("All");

  /** Press to deselect all attributes */
  protected JButton m_ButtonNone = new JButton("None");

  /** Press to invert the current selection */
  protected JButton m_ButtonInvert = new JButton("Invert");

  /** Press to enter a perl regular expression for selection */
  protected JButton m_ButtonPattern = new JButton("Pattern");

  /** The table displaying attribute names and selection status */
  protected BaseTable m_Table = new BaseTable();

  /** The table model containing attribute names and selection status */
  protected AttributeTableModel m_Model;

  /** The current regular expression. */
  protected String m_PatternRegEx = "";

  /** the listeners for changes in the selection. */
  protected Set<ChangeListener> m_ChangeListeners;

  /**
   * Creates the attribute selection panel with no initial instances.
   */
  public AttributeSelectionPanel() {
    this(true, true, true, true);
  }

  /**
   * Creates the attribute selection panel with no initial instances.
   *
   * @param include true if the include button is to be shown
   * @param remove true if the remove button is to be shown
   * @param invert true if the invert button is to be shown
   * @param pattern true if the pattern button is to be shown
   */
  public AttributeSelectionPanel(boolean include, boolean remove,
                                 boolean invert, boolean pattern) {

    m_ChangeListeners = new HashSet<>();

    m_ButtonAll.setToolTipText("Selects all attributes");
    m_ButtonAll.setEnabled(false);
    m_ButtonAll.addActionListener((ActionEvent e) -> m_Model.selectAll());
    m_ButtonNone.setToolTipText("Unselects all attributes");
    m_ButtonNone.setEnabled(false);
    m_ButtonNone.addActionListener((ActionEvent e) -> m_Model.selectNone());
    m_ButtonInvert.setToolTipText("Inverts the current attribute selection");
    m_ButtonInvert.setEnabled(false);
    m_ButtonInvert.addActionListener((ActionEvent e) -> m_Model.invert());
    m_ButtonPattern.setToolTipText("Selects all attributes that match a reg. expression");
    m_ButtonPattern.setEnabled(false);
    m_ButtonPattern.addActionListener((ActionEvent e) -> {
      String patternStr = GUIHelper.showInputDialog(m_ButtonPattern.getParent(),
	"Enter a Perl regular expression", m_PatternRegEx);
      if (patternStr != null) {
	try {
	  Pattern.compile(patternStr);
	  m_PatternRegEx = patternStr;
	  m_Model.pattern(patternStr);
	}
	catch (Exception ex) {
	  GUIHelper.showErrorMessage(m_ButtonPattern.getParent(), "'" + patternStr
	      + "' is not a valid Perl regular expression!", ex,
	    "Error in Pattern...");
	}
      }
    });
    m_Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_Table.setColumnSelectionAllowed(false);
    m_Table.setPreferredScrollableViewportSize(new Dimension(250, 150));

    // Set up the layout
    JPanel p1 = new JPanel();
    p1.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    p1.setLayout(new GridLayout(1, 4, 5, 5));
    if (include)
      p1.add(m_ButtonAll);
    if (remove)
      p1.add(m_ButtonNone);
    if (invert)
      p1.add(m_ButtonInvert);
    if (pattern)
      p1.add(m_ButtonPattern);

    setLayout(new BorderLayout());
    if (include || remove || invert || pattern)
      add(p1, BorderLayout.NORTH);
    add(new JScrollPane(m_Table), BorderLayout.CENTER);
  }

  public Dimension getPreferredScrollableViewportSize() {
    return m_Table.getPreferredScrollableViewportSize();
  }

  public void setPreferredScrollableViewportSize(Dimension d) {
    m_Table.setPreferredScrollableViewportSize(d);
  }

  /**
   * Sets the instances who's attribute names will be displayed.
   *
   * @param newInstances the new set of instances
   */
  public void setInstances(Instances newInstances) {
    if (m_Model == null) {
      m_Model = new AttributeTableModel(newInstances);
      m_Model.addTableModelListener(this);
      m_Table.setModel(m_Model);
      TableColumnModel tcm = m_Table.getColumnModel();
      tcm.getColumn(0).setMaxWidth(60);
      tcm.getColumn(1).setMaxWidth(tcm.getColumn(1).getMinWidth());
      tcm.getColumn(2).setMinWidth(100);
    }
    else {
      m_Model.setInstances(newInstances);
      m_Table.clearSelection();
    }
    m_ButtonAll.setEnabled(true);
    m_ButtonNone.setEnabled(true);
    m_ButtonInvert.setEnabled(true);
    m_ButtonPattern.setEnabled(true);
    m_Table.sizeColumnsToFit(2);
    m_Table.revalidate();
    m_Table.repaint();
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
   * @return the array of selected indices.
   */
  public int[] getSelectedAttributes() {
    return (m_Model == null) ? null : m_Model.getSelectedAttributes();
  }

  /**
   * Set the selected (ie checked) attributes in the widget. Note that setInstances() must
   * have been called first.
   *
   * @param selected an array of boolean indicating which attributes are to have
   *          their check boxes selected.
   * @throws Exception if the supplied array of booleans does not have the same
   *           number of elements as there are attributes.
   */
  public void setSelectedAttributes(boolean[] selected) throws Exception {
    if (m_Model != null) {
      m_Model.setSelectedAttributes(selected);
    }
  }

  /**
   * Returns the table.
   *
   * @return		the table
   */
  public BaseTable getTable() {
    return m_Table;
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
   * Adds the listener to the list of selection listeners.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the listener from the list of selection listeners.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies all listeners that the selection has changed.
   */
  protected void notifyChangeListeners() {
    ChangeEvent 	e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(e);
  }

  /**
   * Gets called when the table model changes.
   *
   * @param e		the event
   * @see 		#notifyChangeListeners()
   */
  @Override
  public void tableChanged(TableModelEvent e) {
    notifyChangeListeners();
  }

  /**
   * Tests the attribute selection panel from the command line.
   *
   * @param args must contain the name of an arff file to load.
   */
  public static void main(String[] args) {
    try {
      if (args.length == 0) {
        throw new Exception("supply the name of an arff file");
      }
      Instances i = new Instances(new java.io.BufferedReader(
        new java.io.FileReader(args[0])));
      weka.gui.AttributeSelectionPanel asp = new weka.gui.AttributeSelectionPanel();
      final javax.swing.JFrame jf = new javax.swing.JFrame(
        "Attribute Selection Panel");
      jf.getContentPane().setLayout(new BorderLayout());
      jf.getContentPane().add(asp, BorderLayout.CENTER);
      jf.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent e) {
          jf.dispose();
          System.exit(0);
        }
      });
      jf.pack();
      jf.setVisible(true);
      asp.setInstances(i);
    } catch (Exception ex) {
      ex.printStackTrace();
      System.err.println(ex.getMessage());
    }
  }
}
