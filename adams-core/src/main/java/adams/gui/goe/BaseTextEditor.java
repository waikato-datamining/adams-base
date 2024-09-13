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
 * BaseTextEditor.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.core.base.BaseText;
import adams.core.option.parsing.BaseTextParsing;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.UISettings;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A PropertyEditor for BaseText objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see adams.core.base.BaseText
 */
public class BaseTextEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, InlineEditorSupport {

  /** The text area with the value. */
  protected BaseTextArea m_TextValue;

  /** The checkbox for the line wrap. */
  protected BaseCheckBox m_CheckLineWrap;

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return BaseTextParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return BaseTextParsing.valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String	result;

    result = "new " + getValue().getClass().getName() + "(\"" + BaseTextParsing.toString(null, getValue()) + "\")";

    return result;
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
    String 		val;

    if (getValue() == null)
      val = AbstractPropertyEditorSupport.NULL;
    else
      val = BaseTextParsing.toString(null, getValue());
    if (val.isEmpty())
      val = AbstractPropertyEditorSupport.EMPTY;
    GUIHelper.configureAntiAliasing(gfx, true);
    offset = GUIHelper.calculateFontOffset(gfx, box);
    gfx.drawString(val, offset[0], offset[1]);
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
    BaseButton 		buttonClose;
    BaseButton 		buttonOK;
    BaseButton		buttonClear;
    JPanel		panel;

    panelAll    = new JPanel(new BorderLayout());
    m_TextValue = new BaseTextArea(30, 60);
    panelAll.add(new BaseScrollPane(m_TextValue), BorderLayout.CENTER);
    panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    panelButtons = new JPanel(new BorderLayout());
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(panel, BorderLayout.WEST);
    buttonClear = new BaseButton("Clear");
    buttonClear.setMnemonic('l');
    buttonClear.setIcon(ImageManager.getIcon("new.gif"));
    buttonClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_TextValue.setText("");
      }
    });
    panel.add(buttonClear);

    m_CheckLineWrap = new BaseCheckBox("Line wrap");
    m_CheckLineWrap.setSelected(UISettings.get(getClass(), "LineWrap", false));
    m_CheckLineWrap.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextValue.setLineWrap(m_CheckLineWrap.isSelected());
        UISettings.set(BaseTextEditor.this.getClass(), "LineWrap", m_TextValue.getLineWrap());
      }
    });
    panel.add(m_CheckLineWrap);
    
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(panel, BorderLayout.EAST);
    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String s = m_TextValue.getText();
	if (((BaseText) getValue()).isValid(s) && !s.equals(((BaseObject) getValue()).getValue()))
	  setValue(new BaseText(s));
	closeDialog(APPROVE_OPTION);
      }
    });
    panel.add(buttonOK);

    buttonClose = new BaseButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	closeDialog(CANCEL_OPTION);
      }
    });
    panel.add(buttonClose);

    return panelAll;
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    super.initForDisplay();
    if (!m_TextValue.getText().equals("" + getValue()))
      m_TextValue.setText("" + getValue());
    m_TextValue.setToolTipText(((BaseObject) getValue()).getTipText());
    m_TextValue.setLineWrap(m_CheckLineWrap.isSelected());
  }
  
  /**
   * Checks whether inline editing is available.
   * 
   * @return		always true
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
    setValue(new BaseText(Utils.unbackQuoteChars(value)));
  }

  /**
   * Returns the current value.
   * 
   * @return		the current value
   */
  public String getInlineValue() {
    return Utils.backQuoteChars(((BaseText) getValue()).getValue());
  }

  /**
   * Checks whether the value id valid.
   * 
   * @param value	the value to check
   * @return		always true
   */
  public boolean isInlineValueValid(String value) {
    return true;
  }
}
