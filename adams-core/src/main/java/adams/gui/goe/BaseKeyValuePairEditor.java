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
 * BaseKeyValuePairEditor.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.core.Utils;
import adams.core.base.BaseKeyValuePair;
import adams.core.base.BaseObject;
import adams.core.option.AbstractOption;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;

/**
 * Editor specifically designed for entering key/value pairs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseKeyValuePairEditor
  extends BaseObjectEditor
  implements MultiSelectionEditor {

  /** The text field with the key. */
  protected JTextComponent m_TextKey;

  /**
   * Returns the Compound as string.
   *
   * @param option	the current option
   * @param object	the Compound object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((BaseKeyValuePair) object).stringValue();
  }

  /**
   * Returns a Compound generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a Compound
   * @return		the generated Compound
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new BaseKeyValuePair(Utils.unbackQuoteChars(str));
  }

  /**
   * Accepts the input and closes the dialog.
   */
  protected void acceptInput() {
    String 	key;
    String 	value;
    String	pair;

    key   = m_TextKey.getText();
    value = m_TextValue.getText();
    pair  = key + "=" + value;
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
    ParameterPanel	panelPair;
    JPanel 		panelButtons;
    JButton 		buttonClear;
    JButton 		buttonOK;
    JButton 		buttonClose;

    panelAll  = new JPanel(new BorderLayout());
    panelPair = new ParameterPanel();
    panelAll.add(panelPair, BorderLayout.CENTER);

    m_TextKey   = new JTextField(10);
    m_TextValue = new JTextField(30);

    panelPair.addParameter("_Key", m_TextKey);
    panelPair.addParameter("_Value", m_TextValue);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    buttonClear = new JButton("Clear");
    buttonClear.setMnemonic('l');
    buttonClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_TextKey.setText("");
	m_TextValue.setText("");
      }
    });
    panelButtons.add(buttonClear);

    buttonOK = new JButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	acceptInput();
      }
    });
    panelButtons.add(buttonOK);

    buttonClose = new JButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	discardInput();
      }
    });
    panelButtons.add(buttonClose);

    return panelAll;
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    BaseKeyValuePair	value;

    resetChosenOption();
    
    value = (BaseKeyValuePair) getValue();

    if (!m_TextKey.getText().equals(value.getPairKey()))
      m_TextKey.setText(value.getPairKey());
    if (!m_TextValue.getText().equals(value.getPairValue()))
      m_TextValue.setText(value.getPairValue());
    m_TextKey.setToolTipText(((BaseObject) getValue()).getTipText());
    m_TextValue.setToolTipText(((BaseObject) getValue()).getTipText());
    m_TextKey.grabFocus();
  }

  /**
   * Returns the string to paint.
   *
   * @return		the string
   * @see		#paintValue(Graphics, Rectangle)
   */
  @Override
  protected String getStringToPaint() {
    return ((BaseKeyValuePair) getValue()).pairValue();
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
    BaseKeyValuePair[]		result;
    MultiLineValueDialog	dialog;
    Vector<String>		lines;
    int				i;

    dialog = new MultiLineValueDialog();
    dialog.setInfoText("Enter the key/value pairs, one per line (separator '='):");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      lines = new Vector<String>(Arrays.asList(dialog.getContent().split("\n")));
      Utils.removeEmptyLines(lines);
      result = new BaseKeyValuePair[lines.size()];
      for (i = 0; i < lines.size(); i++)
	result[i] = (BaseKeyValuePair) parse(lines.get(i));
    }
    else {
      result = new BaseKeyValuePair[0];
    }

    return result;
  }
}
