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
 * VariableNameValuePairEditor.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.core.Utils;
import adams.core.VariableNameValuePair;
import adams.core.base.BaseObject;
import adams.core.option.AbstractOption;
import adams.gui.core.BaseButton;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Editor specifically designed for entering variable name/value pairs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariableNameValuePairEditor
  extends BaseObjectEditor
  implements MultiSelectionEditor {

  /** The text field with the key. */
  protected JTextComponent m_TextName;

  /** the panel with name/value. */
  protected ParameterPanel m_PanelPair;

  /** the default background color of the panel. */
  protected Color m_DefaultBackground;

  /**
   * Returns the Compound as string.
   *
   * @param option	the current option
   * @param object	the Compound object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((VariableNameValuePair) object).stringValue();
  }

  /**
   * Returns a Compound generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a Compound
   * @return		the generated Compound
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new VariableNameValuePair(Utils.unbackQuoteChars(str));
  }

  protected String currentValue() {
    if (m_TextName.getText().isEmpty() && m_TextValue.getText().isEmpty())
      return "";
    else
      return m_TextName.getText() + VariableNameValuePair.SEPARATOR + m_TextValue.getText();
  }

  /**
   * Accepts the input and closes the dialog.
   */
  protected void acceptInput() {
    String	pair;

    pair  = currentValue();
    if (isValid(pair) && !isUnchanged(pair))
      setValue(parse(pair));
    closeDialog(APPROVE_OPTION);
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel		panelAll;
    JPanel 		panelButtons;
    BaseButton 		buttonClear;
    BaseButton 		buttonOK;
    BaseButton 		buttonClose;

    panelAll  = new JPanel(new BorderLayout());
    m_PanelPair = new ParameterPanel();
    m_DefaultBackground = m_PanelPair.getBackground();
    m_PanelPair.setBorder(BorderFactory.createLineBorder(m_DefaultBackground));
    panelAll.add(m_PanelPair, BorderLayout.CENTER);

    m_TextName  = new JTextField(10);
    m_TextName.getDocument().addDocumentListener(new DocumentListener() {
      public void removeUpdate(DocumentEvent e) {
	indicateValidity();
      }
      public void insertUpdate(DocumentEvent e) {
	indicateValidity();
      }
      public void changedUpdate(DocumentEvent e) {
	indicateValidity();
      }
    });
    m_TextValue = new JTextField(30);

    m_PanelPair.addParameter("_Name", m_TextName);
    m_PanelPair.addParameter("_Value", m_TextValue);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    buttonClear = new BaseButton("Clear");
    buttonClear.setMnemonic('l');
    buttonClear.addActionListener((ActionEvent e) -> {
      m_TextName.setText("");
      m_TextValue.setText("");
    });
    panelButtons.add(buttonClear);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener((ActionEvent e) -> acceptInput());
    panelButtons.add(buttonOK);

    buttonClose = new BaseButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener((ActionEvent e) -> discardInput());
    panelButtons.add(buttonClose);

    return panelAll;
  }

  /**
   * Updates the color of the border, indicating with RED if the
   * input is invalid.
   */
  protected void indicateValidity() {
    Color 	curColor;
    Color	newColor;

    curColor = ((LineBorder) m_PanelPair.getBorder()).getLineColor();
    if (isValid(currentValue()))
      newColor = m_DefaultBackground;
    else
      newColor = Color.RED;

    if (!newColor.equals(curColor))
      m_PanelPair.setBorder(BorderFactory.createLineBorder(newColor));
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    VariableNameValuePair	value;

    resetChosenOption();
    
    value = (VariableNameValuePair) getValue();

    if (!m_TextName.getText().equals(value.varName().getValue()))
      m_TextName.setText(value.varName().getValue());
    if (!m_TextValue.getText().equals(value.varValue()))
      m_TextValue.setText(value.varValue());
    m_TextName.setToolTipText(((BaseObject) getValue()).getTipText());
    m_TextValue.setToolTipText(((BaseObject) getValue()).getTipText());
    m_TextName.grabFocus();
  }

  /**
   * Returns the string to paint.
   *
   * @return		the string
   * @see		#paintValue(Graphics, Rectangle)
   */
  @Override
  protected String getStringToPaint() {
    return ((VariableNameValuePair) getValue()).pairValue();
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  @Override
  public String toCustomStringRepresentation(Object obj) {
    return toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  @Override
  public Object fromCustomStringRepresentation(String str) {
    return valueOf(null, str);
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    VariableNameValuePair[]	result;
    MultiLineValueDialog	dialog;
    List<String> 		lines;
    int				i;

    dialog = new MultiLineValueDialog();
    dialog.setInfoText("Enter the variable name/value pairs, one per line (separator '='):");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      lines = new ArrayList<>(Arrays.asList(dialog.getContent().split("\n")));
      Utils.removeEmptyLines(lines);
      result = new VariableNameValuePair[lines.size()];
      for (i = 0; i < lines.size(); i++)
	result[i] = (VariableNameValuePair) parse(lines.get(i));
    }
    else {
      result = new VariableNameValuePair[0];
    }

    return result;
  }
}
