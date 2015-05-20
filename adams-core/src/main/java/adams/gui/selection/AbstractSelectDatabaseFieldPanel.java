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
 * AbstractSelectDatabaseFieldPanel.java
 * Copyright (C) 2008-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.selection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adams.core.option.AbstractOption;
import adams.core.option.OptionUtils;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.FieldType;
import adams.db.FieldProvider;
import adams.gui.event.FieldCacheUpdateEvent;
import adams.gui.event.FieldCacheUpdateListener;

/**
 * A panel for selecting a field (obtained from a database).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSelectDatabaseFieldPanel<T extends AbstractField>
  extends AbstractDatabaseSelectionPanel<T>
  implements FieldCacheUpdateListener {

  /** for serialization. */
  private static final long serialVersionUID = -3858687917199734023L;

  /** The caches to manage. */
  protected static AbstractFieldCacheManager m_CacheManager;

  /** the label for the manual list. */
  protected JLabel m_LabelManualFields;

  /** the textfield for manually entering a list of fields (blank separated). */
  protected JTextField m_TextManualFields;

  /** the label for the data type combobox. */
  protected JLabel m_LabelDataType;

  /** the data types to display. */
  protected JComboBox m_ComboBoxDataType;

  /** the type of fields to display. */
  protected FieldType m_FieldType;

  /**
   * Default constructor.
   */
  public AbstractSelectDatabaseFieldPanel() {
    super();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_FieldType = FieldType.FIELD;
  }

  /**
   * Initializes the GUI elements.
   */
  protected void initGUI() {
    super.initGUI();

    m_TableData.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	if (m_TableData.getSelectedRowCount() > 0)
	  m_TextManualFields.setText("");
      }
    });

    m_TextManualFields  = new JTextField(15);
    m_LabelManualFields = new JLabel();
    m_LabelManualFields.setLabelFor(m_LabelManualFields);

    m_ComboBoxDataType = new JComboBox();
    m_ComboBoxDataType.addItem("All");
    for (DataType type: DataType.values()) {
      m_ComboBoxDataType.addItem(type.toDisplay());
      if (type == DataType.NUMERIC)
	m_ComboBoxDataType.setSelectedIndex(m_ComboBoxDataType.getModel().getSize() - 1);
    }
    m_ComboBoxDataType.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	refreshModel();
      }
    });
    m_LabelDataType = new JLabel("Data type");
    m_LabelDataType.setLabelFor(m_ComboBoxDataType);

    m_SearchPanel.addToWidgetsPanel(m_LabelManualFields);
    m_SearchPanel.addToWidgetsPanel(m_TextManualFields);
    m_SearchPanel.addToWidgetsPanel(m_LabelDataType);
    m_SearchPanel.addToWidgetsPanel(m_ComboBoxDataType);

    // update "manual" label
    setMultipleSelection(isMultipleSelection());

    m_TableData.setOptimalColumnWidth();
  }

  /**
   * finishes the initialization.
   */
  protected void finishInit() {
    super.finishInit();
    addCacheListener(m_FieldType);
    sort();
  }

  /**
   * Creates a new instance of the cache manager.
   *
   * @return		the new manager
   */
  protected abstract AbstractFieldCacheManager newFieldCacheManager();

  /**
   * Returns the cache manager.
   *
   * @return		the cache manager
   */
  protected AbstractFieldCacheManager getFieldCacheManager() {
    if (m_CacheManager == null)
      m_CacheManager = newFieldCacheManager();

    return m_CacheManager;
  }

  /**
   * Returns the field provider to use.
   *
   * @return		the field provider
   */
  protected abstract FieldProvider getFieldProvider();

  /**
   * Returns an empty table model.
   *
   * @return		the model
   */
  protected AbstractSelectionTableModel<T> newTableModel() {
    return (AbstractSelectionTableModel<T>) new FieldCacheTableModel(null, getFieldType(), getDataType());
  }

  /**
   * Returns the class of the items displayed, same as "T".
   *
   * @return		the class of the items
   */
  protected abstract Class getItemClass();

  /**
   * Creates a new field instance.
   *
   * @param name	the name of the field
   * @param type	the type of the field
   * @return		the generated field
   */
  protected abstract T newInstance(String name, DataType type);

  /**
   * Sets the field type.
   *
   * @param value	the new field type
   */
  public void setFieldType(FieldType value) {
    removeCacheListener(m_FieldType);
    m_FieldType = value;
    m_ComboBoxDataType.setVisible(m_FieldType != FieldType.PREFIX_FIELD);
    m_LabelDataType.setVisible(m_ComboBoxDataType.isVisible());
    addCacheListener(m_FieldType);
    // update model
    refreshModel();
  }

  /**
   * Refreshes the data model.
   */
  protected void refreshModel() {
    FieldCacheTableModel	model;

    // can we skip refresh?
    if (m_TableDataModel instanceof FieldCacheTableModel) {
      model = (FieldCacheTableModel) m_TableDataModel;
      if ((model.getFieldType() == getFieldType()) && (model.getDataType() == getDataType()))
	return;
    }

    m_TableDataModel.removeTableModelListener(m_TableData);
    m_TableDataModel = (AbstractSelectionTableModel<T>) new FieldCacheTableModel(
	getFieldCacheManager().get(getFieldProvider()),
	m_FieldType, getDataType());

    m_TableData.setModel(m_TableDataModel);
    m_TableDataModel.addTableModelListener(m_TableData);
    m_TableData.setOptimalColumnWidth();
  }

  /**
   * Returns the field type.
   *
   * @return		the current field type
   */
  public FieldType getFieldType() {
    return m_FieldType;
  }

  /**
   * Hook method for processing items that were not found when trying to
   * select them initially.
   * <br><br>
   * The missing items are displayed in the "manual" field.
   *
   * @param missing	the missing items
   */
  protected void processMissing(Vector<T> missing) {
    String[]	list;
    int		i;

    if (isMultipleSelection()) {
      list = new String[missing.size()];
      for (i = 0; i < missing.size(); i++)
	list[i] = missing.get(i).toString();
      m_TextManualFields.setText(OptionUtils.joinOptions(list));
    }
    else {
      m_TextManualFields.setText(missing.get(0).toString());
    }
  }

  /**
   * Checks whether the item is valid.
   * <br><br>
   * Checks for null and whether field has a name.
   *
   * @param item	the item to check
   * @return		true if valid
   */
  protected boolean isValidItem(T item) {
    return (item != null) && (item.getName() != null) && (item.getName().length() > 0);
  }

  /**
   * Sets the data type to display. Ignored if PrefixFields displayed.
   *
   * @param value	the type to display, use null to display all
   */
  public void setDataType(DataType value) {
    if (m_FieldType == FieldType.PREFIX_FIELD) {
      m_ComboBoxDataType.setSelectedIndex(0);
    }
    else {
      if (value == null)
	m_ComboBoxDataType.setSelectedIndex(0);
      else
	m_ComboBoxDataType.setSelectedItem(value.toDisplay());
    }
  }

  /**
   * Returns the currently selected data type, if any.
   *
   * @return		the data type, null if "All" was selected (or PrefixFields displayed)
   */
  public DataType getDataType() {
    if (m_FieldType == FieldType.PREFIX_FIELD)
      return null;
    if (m_ComboBoxDataType == null)
      return null;
    if (m_ComboBoxDataType.getSelectedIndex() <= 0)
      return null;
    else
      return DataType.valueOf((AbstractOption) null, (String) m_ComboBoxDataType.getSelectedItem());
  }

  /**
   * Returns the current fields. Manually entered ones have precedence over
   * the selected ones.
   *
   * @return		the currently entered/selected fields
   */
  protected T[] getCurrentItems() {
    T[]		result;
    String[]	fields;
    int		i;

    if (m_TextManualFields.getText().length() > 0) {
      try {
	if (isMultipleSelection())
	  fields = OptionUtils.splitOptions(m_TextManualFields.getText());
	else
	  fields = new String[]{m_TextManualFields.getText()};
	result = (T[]) Array.newInstance(getItemClass(), fields.length);
	for (i = 0; i < fields.length; i++) {
	  result[i] = newInstance(fields[i], getDataType());
	}
      }
      catch (Exception e) {
	e.printStackTrace();
	result = null;
      }
    }
    else {
      result = super.getCurrentItems();
    }

    return result;
  }

  /**
   * Fixes the class of the fields (regular, prefix or suffix).
   *
   * @param items	the items to fix
   * @return		the fixed items
   */
  protected abstract T[] fixClass(T[] items);

  /**
   * Sets the initially selected set names.
   *
   * @param value	the set names to select
   */
  public void setItems(T[] value) {
    int		i;
    DataType	dtype;

    super.setItems(value);

    dtype = DataType.NUMERIC;
    for (i = 0; i < value.length; i++) {
      if (i == 0) {
	dtype = value[i].getDataType();
      }
      else {
	if (dtype != value[i].getDataType())
	  dtype = null;
      }
    }

    // set the common data type of the fields
    if (dtype != null)
      setDataType(dtype);
  }

  /**
   * Returns the current fields.
   *
   * @return		the fields
   */
  public T[] getItems() {
    return fixClass(getCurrentItems());
  }

  /**
   * Sets whether multiple or single selection is used.
   *
   * @param value	if true multiple IDs can be selected
   */
  public void setMultipleSelection(boolean value) {
    super.setMultipleSelection(value);

    if (value)
      m_LabelManualFields.setText("Manual fields");
    else
      m_LabelManualFields.setText("Manual field");
  }

  /**
   * Simulates a click on the refresh button, if necessary (i.e., the cache
   * is null).
   *
   * @see	#refresh()
   */
  public void refreshIfNecessary() {
    if (    !getFieldCacheManager().get(getFieldProvider()).isInitialized()
	 || !getDatabaseConnection().getURL().equals(((FieldCacheTableModel) m_TableDataModel).getDatabaseURL())
	 || !m_DataDisplayed)
      refresh();
  }

  /**
   * Performs the actual refresh.
   */
  protected void doRefresh() {
    updateTableModel();
  }

  /**
   * Sorts the table.
   */
  public void sort() {
    m_TableData.sort(0);
  }

  /**
   * Gets called when the database connection gets disconnected.
   */
  protected void databaseDisconnected() {
    getFieldCacheManager().get(getFieldProvider()).clear();
  }

  /**
   * Updates the table model.
   */
  protected void updateTableModel() {
    m_TableDataModel.removeTableModelListener(m_TableData);
    m_TableDataModel = (AbstractSelectionTableModel<T>) new FieldCacheTableModel(
	getFieldCacheManager().get(getFieldProvider()),
	m_FieldType, getDataType());

    m_TableData.setModel(m_TableDataModel);
    m_TableData.setOptimalColumnWidth();

    int[] indices = new int[Array.getLength(m_Current)];
    for (int i = 0; i < indices.length; i++)
      indices[i] = m_TableDataModel.indexOf((T) Array.get(m_Current, i));

    sort();

    if (indices.length > 0) {
      m_TableData.getSelectionModel().clearSelection();
      for (int i = 0; i < indices.length; i++)
	m_TableData.getSelectionModel().addSelectionInterval(
	    m_TableData.getDisplayRow(indices[i]),
	    m_TableData.getDisplayRow(indices[i]));
    }
  }

  /**
   * Gets called when the cache gets refreshed.
   *
   * @param event	the event
   */
  public void cacheUpdated(FieldCacheUpdateEvent event) {
    updateTableModel();
  }

  /**
   * Adds a cache listener.
   *
   * @param l		the listener to add
   */
  protected void addCacheListener(FieldType fieldtype) {
    getFieldCacheManager().get(getFieldProvider()).addCacheListener(m_FieldType, this);
  }

  /**
   * Removes a cache listener.
   *
   * @param fieldtype	the fieldtype to unregister the listener for
   * @param l		the listener to remove
   */
  protected void removeCacheListener(FieldType fieldtype) {
    getFieldCacheManager().get(getFieldProvider()).removeCacheListener(m_FieldType, this);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();
    removeCacheListener(m_FieldType);
  }
}
