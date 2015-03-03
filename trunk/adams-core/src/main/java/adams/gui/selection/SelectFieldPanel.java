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
 * SelectFieldPanel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.selection;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import adams.core.option.AbstractOption;
import adams.core.option.OptionUtils;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.FieldType;
import adams.data.report.FieldUtils;
import adams.gui.core.BasePanel;
import adams.gui.core.ParameterPanel;

/**
 * A panel for selecting a field (obtained from a database).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SelectFieldPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -3858687917199734023L;

  /** panel for the parameters. */
  protected ParameterPanel m_Panel;

  /** the textfield for entering a list of fields (blank separated). */
  protected JTextField m_TextFields;

  /** the data types to display. */
  protected JComboBox m_ComboBoxDataType;

  /** the type of fields to display. */
  protected FieldType m_FieldType;

  /** whether to allow multi-selection (blank-separated list). */
  protected boolean m_MultipleSelection;

  /**
   * Default constructor.
   */
  public SelectFieldPanel() {
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

    setLayout(new BorderLayout());

    m_Panel = new ParameterPanel();
    add(m_Panel, BorderLayout.CENTER);

    m_TextFields  = new JTextField(15);
    m_Panel.addParameter("Field _name", m_TextFields);

    m_ComboBoxDataType = new JComboBox();
    for (DataType type: DataType.values()) {
      m_ComboBoxDataType.addItem(type.toDisplay());
      if (type == DataType.NUMERIC)
	m_ComboBoxDataType.setSelectedIndex(m_ComboBoxDataType.getModel().getSize() - 1);
    }
    m_Panel.addParameter("Data _type", m_ComboBoxDataType);

    // update "manual" label
    setMultipleSelection(isMultipleSelection());
  }

  /**
   * Sets the field type.
   *
   * @param value	the new field type
   */
  public void setFieldType(FieldType value) {
    m_FieldType = value;
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
   * <p/>
   * The missing items are displayed in the "manual" field.
   *
   * @param missing	the missing items
   */
  protected void processMissing(Vector<Field> missing) {
    String[]	list;
    int		i;

    if (isMultipleSelection()) {
      list = new String[missing.size()];
      for (i = 0; i < missing.size(); i++)
	list[i] = missing.get(i).toString();
      m_TextFields.setText(OptionUtils.joinOptions(list));
    }
    else {
      m_TextFields.setText(missing.get(0).toString());
    }
  }

  /**
   * Sets the data type to display. Ignored if PrefixFields displayed.
   *
   * @param value	the type to display
   */
  public void setDataType(DataType value) {
    m_ComboBoxDataType.setSelectedItem(value.toDisplay());
  }

  /**
   * Returns the currently selected data type, if any.
   *
   * @return		the data type, null if "All" was selected (or PrefixFields displayed)
   */
  public DataType getDataType() {
    if (m_FieldType == FieldType.PREFIX_FIELD)
      return null;
    if (m_ComboBoxDataType.getSelectedIndex() < 0)
      return null;
    else
      return DataType.valueOf((AbstractOption) null, (String) m_ComboBoxDataType.getSelectedItem());
  }

  /**
   * Returns the current fields.
   *
   * @return		the currently entered fields
   */
  protected AbstractField[] getCurrentItems() {
    Field[]	result;
    String[]	fields;
    int		i;

    if (m_TextFields.getText().length() > 0) {
      try {
	if (isMultipleSelection())
	  fields = OptionUtils.splitOptions(m_TextFields.getText());
	else
	  fields = new String[]{m_TextFields.getText()};
	result = new Field[fields.length];
	for (i = 0; i < fields.length; i++)
	  result[i] = new Field(fields[i], getDataType());
      }
      catch (Exception e) {
	e.printStackTrace();
	result = null;
      }
    }
    else {
      result = new Field[0];
    }

    return result;
  }

  /**
   * Returns the current fields.
   *
   * @return		the fields
   */
  public AbstractField[] getItems() {
    return FieldUtils.fixClass(m_FieldType, getCurrentItems());
  }

  /**
   * Returns the item, null if none chosen or dialog canceled.
   *
   * @return		the selected item
   */
  public AbstractField getItem() {
    if (getItems().length == 0)
      return null;
    else
      return getItems()[0];
  }

  /**
   * Sets the initially selected item.
   *
   * @param value	the item to select
   */
  public void setItem(AbstractField value) {
    AbstractField[]		items;

    if (value != null)
      items = new AbstractField[]{value};
    else
      items = new AbstractField[0];

    setItems(items);
  }

  /**
   * Sets the initially selected items.
   *
   * @param value	the items to select
   */
  public void setItems(AbstractField[] value) {
    StringBuilder	builder;
    DataType		dtype;
    int			i;

    builder = new StringBuilder();
    dtype   = DataType.NUMERIC;
    for (i = 0; i < value.length; i++) {
      if (i == 0) {
	dtype = value[i].getDataType();
      }
      else {
	if (dtype != value[i].getDataType())
	  dtype = null;
      }
      if (builder.length() > 0)
	builder.append(" ");
      builder.append(value[i].toString());
    }

    // set the common data type of the fields
    if (dtype != null)
      setDataType(dtype);

    m_TextFields.setText(builder.toString());
  }

  /**
   * Sets whether multiple or single selection is used.
   *
   * @param value	if true multiple items can be selected
   */
  public void setMultipleSelection(boolean value) {
    m_MultipleSelection = value;
  }

  /**
   * Returns whether multiple or single selection is active.
   *
   * @return		true if multiple selection is active
   */
  public boolean isMultipleSelection() {
    return m_MultipleSelection;
  }

  /**
   * Requests that this Component get the input focus, and that this
   * Component's top-level ancestor become the focused Window. This component
   * must be displayable, visible, and focusable for the request to be
   * granted.
   */
  public void grabFocus() {
    m_TextFields.grabFocus();
  }
}
