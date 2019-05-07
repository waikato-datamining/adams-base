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
 * BaseIntervalEditor.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.core.base.BaseInterval;
import adams.core.base.BaseObject;
import adams.core.option.parsing.BaseIntervalParsing;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseTextField;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Editor specifically designed for entering intervals pairs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseIntervalEditor
  extends BaseObjectEditor
  implements MultiSelectionEditor {

  /** whether to include the lower bound. */
  protected BaseCheckBox m_CheckBoxLower;

  /** The text field with the upper bound. */
  protected JTextComponent m_TextUpper;

  /** whether to include the upper bound. */
  protected BaseCheckBox m_CheckBoxUpper;

  /**
   * Accepts the input and closes the dialog.
   */
  protected void acceptInput() {
    String 	str;

    str = (m_CheckBoxLower.isSelected() ? "[" : "(")
      + m_TextValue.getText()
      + ";"
      + m_TextUpper.getText()
      + (m_CheckBoxUpper.isSelected() ? "]" : ")");
    if (isValid(str) && !isUnchanged(str))
      setValue(parse(str));
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
    BaseButton 		buttonClear;
    BaseButton 		buttonOK;
    BaseButton 		buttonClose;

    panelAll  = new JPanel(new BorderLayout());
    panelPair = new ParameterPanel();
    panelAll.add(panelPair, BorderLayout.CENTER);

    m_CheckBoxLower = new BaseCheckBox();
    m_TextValue     = new BaseTextField(10);
    m_TextUpper     = new BaseTextField(10);
    m_CheckBoxUpper = new BaseCheckBox();

    panelPair.addParameter("Incl. lower bound", m_CheckBoxLower);
    panelPair.addParameter("_Lower bound", m_TextValue);
    panelPair.addParameter("_Upper bound", m_TextUpper);
    panelPair.addParameter("Incl. upper bound", m_CheckBoxUpper);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    buttonClear = new BaseButton("Clear");
    buttonClear.setMnemonic('l');
    buttonClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        m_CheckBoxLower.setSelected(false);
	m_TextValue.setText(BaseInterval.NEGATIVE_INFINITY);
	m_TextUpper.setText(BaseInterval.POSITIVE_INFINITY);
        m_CheckBoxUpper.setSelected(false);
      }
    });
    panelButtons.add(buttonClear);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	acceptInput();
      }
    });
    panelButtons.add(buttonOK);

    buttonClose = new BaseButton("Cancel");
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
    BaseInterval	value;

    resetChosenOption();
    
    value = (BaseInterval) getValue();

    m_CheckBoxLower.setSelected(value.isLowerInclusive());
    try {
      if (Double.parseDouble(m_TextValue.getText()) != value.getLower())
	m_TextValue.setText("" + value.getLower());
    }
    catch (Exception e) {
      m_TextValue.setText("" + value.getLower());
    }
    try {
      if (Double.parseDouble(m_TextUpper.getText()) != value.getUpper())
	m_TextUpper.setText("" + value.getUpper());
    }
    catch (Exception e) {
      m_TextUpper.setText("" + value.getUpper());
    }
    m_CheckBoxUpper.setSelected(value.isUpperInclusive());

    m_CheckBoxLower.setToolTipText(((BaseObject) getValue()).getTipText());
    m_TextValue.setToolTipText(((BaseObject) getValue()).getTipText());
    m_TextUpper.setToolTipText(((BaseObject) getValue()).getTipText());
    m_CheckBoxUpper.setToolTipText(((BaseObject) getValue()).getTipText());

    m_TextValue.grabFocus();
  }

  /**
   * Returns the string to paint.
   *
   * @return		the string
   * @see		#paintValue(Graphics, Rectangle)
   */
  @Override
  protected String getStringToPaint() {
    return ((BaseInterval) getValue()).getValue();
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  @Override
  public String toCustomStringRepresentation(Object obj) {
    return BaseIntervalParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  @Override
  public Object fromCustomStringRepresentation(String str) {
    return BaseIntervalParsing.valueOf(null, str);
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    BaseInterval[]		result;
    MultiLineValueDialog	dialog;
    List<String> 		lines;
    int				i;

    if (GUIHelper.getParentDialog(parent) != null)
      dialog = new MultiLineValueDialog(GUIHelper.getParentDialog(parent));
    else
      dialog = new MultiLineValueDialog(GUIHelper.getParentFrame(parent));
    dialog.setInfoText("Enter the intervals, one per line (possible formats: [A;B] (C;D) [E;F) (G;H]):");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    lines  = dialog.getValues();
    result = new BaseInterval[lines.size()];
    for (i = 0; i < lines.size(); i++)
      result[i] = (BaseInterval) parse(lines.get(i));

    return result;
  }
}
