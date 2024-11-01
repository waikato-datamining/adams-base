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
 * FieldEditor.java
 * Copyright (C) 2008-2019 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.option.AbstractOption;
import adams.core.option.parsing.FieldParsing;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.FieldType;
import adams.data.report.PrefixOnlyField;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseComboBox;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.selection.SelectFieldPanel;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A PropertyEditor for Field objects that lets the user select a field.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see Field
 */
public class FieldEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, MultiSelectionEditor, InlineEditorSupport {

  /** The panel used for selecting fields. */
  protected SelectFieldPanel m_SelectFieldPanel;

  /** the OK button. */
  protected BaseButton m_ButtonOK;

  /** the close button. */
  protected BaseButton m_ButtonClose;

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return FieldParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return FieldParsing.valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String	result;
    Field 	field;

    field = (Field) getValue();

    if (field == null)
      result = "null";
    else
      result = "new " + Field.class.getName() + "(\"" + field.toString() + "\", " + DataType.class.getName() + "." + field.getDataType() + ")";

    return result;
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel 	panel;

    panel = new JPanel(new BorderLayout());
    m_SelectFieldPanel = new SelectFieldPanel();
    m_SelectFieldPanel.setFieldType(FieldType.FIELD);
    panel.add(m_SelectFieldPanel, BorderLayout.CENTER);

    JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(panelButtons, BorderLayout.SOUTH);

    m_ButtonOK = new BaseButton("OK");
    m_ButtonOK.setMnemonic('O');
    m_ButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_SelectFieldPanel.getItem() != null) {
	  AbstractField field = m_SelectFieldPanel.getItem();
	  AbstractField current = (AbstractField) getValue();
	  if (!current.toParseableString().equals(field.toParseableString()))
	    setValue(field);
	}
	closeDialog(APPROVE_OPTION);
      }
    });
    panelButtons.add(m_ButtonOK);

    m_ButtonClose = new BaseButton("Cancel");
    m_ButtonClose.setMnemonic('C');
    m_ButtonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	// select current one value again, to make sure that it is displayed
	// when the dialog is popped up again. otherwise the last selection
	// (but not ok-ed!) will be displayed.
	m_SelectFieldPanel.setItem((AbstractField) getValue());
	closeDialog(CANCEL_OPTION);
      }
    });
    panelButtons.add(m_ButtonClose);

    return panel;
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    super.initForDisplay();
    m_SelectFieldPanel.setItem((Field) getValue());
    m_SelectFieldPanel.grabFocus();
  }

  /**
   * Paints a representation of the current Object.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   */
  @Override
  public void paintValue(Graphics gfx, Rectangle box) {
    int[] 		offset;
    Field		curr;
    String 		val;

    curr = (Field) getValue();
    val  = curr.toDisplayString();
    if (!(curr instanceof PrefixOnlyField))
      val += "[" + curr.getDataType().toDisplay() + "]";
    GUIHelper.configureAntiAliasing(gfx, true);
    offset = GUIHelper.calculateFontOffset(gfx, box);
    gfx.drawString(val, offset[0], offset[1]);
  }

  /**
   * Checks whether inline editing is available.
   *
   * @return		true if editing available
   */
  public boolean isInlineEditingAvailable() {
    return true;
  }

  /**
   * Sets the value to use.
   *
   * @param value	the value to use
   */
  public void setInlineValue(String value) {
    if (isInlineValueValid(value))
      setValue(Field.parseField(value));
  }

  /**
   * Returns the current value.
   *
   * @return		the current value
   */
  public String getInlineValue() {
    return ((Field) getValue()).toParseableString();
  }

  /**
   * Checks whether the value id valid.
   *
   * @param value	the value to check
   * @return		true if valid
   */
  public boolean isInlineValueValid(String value) {
    try {
      return Field.isValid(value);
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Creates a new array of field objects from the strings.
   *
   * @param fields	the field names to use
   * @param type	the type of the fields
   * @return		the field array
   */
  protected AbstractField[] newArray(List<String> fields, DataType type) {
    Field[]	result;
    int		i;

    result = new Field[fields.size()];
    for (i = 0; i < fields.size(); i++)
      result[i] = new Field(fields.get(i), type);

    return result;
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    AbstractField[]		result;
    MultiLineValueDialog	dialog;
    JPanel			panelType;
    List<String>  		lines;
    BaseComboBox		combo;
    DataType			dtype;

    combo = new BaseComboBox();
    for (DataType type: DataType.values()) {
      combo.addItem(type.toDisplay());
      if (type == DataType.NUMERIC)
	combo.setSelectedIndex(combo.getModel().getSize() - 1);
    }

    if (GUIHelper.getParentDialog(parent) != null)
      dialog = new MultiLineValueDialog(GUIHelper.getParentDialog(parent));
    else
      dialog = new MultiLineValueDialog(GUIHelper.getParentFrame(parent));
    dialog.setPrefixCount("Field count: ");
    dialog.setInfoText("Enter the field names, one per line:");
    panelType = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    dialog.getBottomPanel().add(panelType, BorderLayout.CENTER);
    panelType.add(new JLabel("Data type"));
    panelType.add(combo);
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      lines = dialog.getValues();
      if (combo.getSelectedIndex() == -1)
	dtype = DataType.UNKNOWN;
      else
	dtype = DataType.valueOf((AbstractOption) null, (String) combo.getSelectedItem());
      result = newArray(lines, dtype);
    }
    else {
      result = newArray(new ArrayList<>(), DataType.UNKNOWN);
    }

    return result;
  }
}

