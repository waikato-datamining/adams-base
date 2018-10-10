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
 * BaseClassnameEditor.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.base.BaseClassname;
import adams.core.base.BaseObject;
import adams.gui.core.GUIHelper;
import adams.gui.tools.ClassHelpPanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A PropertyEditor for BaseClassname objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see BaseClassname
 */
public class BaseClassnameEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, InlineEditorSupport {

  /** The class panel. */
  protected ClassHelpPanel m_PanelHelp;

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return ((BaseClassname) obj).getValue();
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return new BaseClassname(str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String	result;

    result = "new " + getValue().getClass().getName() + "(\"" + getValue() + "\")";

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
    FontMetrics 	fm;
    int 		vpad;
    String 		val;

    fm   = gfx.getFontMetrics();
    vpad = (box.height - fm.getHeight()) / 2;
    if (getValue() == null)
      val = AbstractPropertyEditorSupport.NULL;
    else
      val = ((BaseClassname) getValue()).getValue();
    if (val.isEmpty())
      val = AbstractPropertyEditorSupport.EMPTY;
    gfx.drawString(val, 2, fm.getHeight() + vpad);
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
    JButton 		buttonClose;
    JButton 		buttonOK;
    JPanel		panel;

    panelAll    = new JPanel(new BorderLayout());
    m_PanelHelp = new ClassHelpPanel();
    m_PanelHelp.setPreferredSize(GUIHelper.getDefaultDialogDimension());
    panelAll.add(m_PanelHelp, BorderLayout.CENTER);
    panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    panelButtons = new JPanel(new BorderLayout());
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(panel, BorderLayout.WEST);

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(panel, BorderLayout.EAST);
    buttonOK = new JButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.setEnabled(false);
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String s = m_PanelHelp.getSelectedClass();
	if ((s != null) && ((BaseClassname) getValue()).isValid(s) && !s.equals(((BaseObject) getValue()).getValue()))
	  setValue(new BaseClassname(s));
	closeDialog(APPROVE_OPTION);
      }
    });
    panel.add(buttonOK);
    m_PanelHelp.addChangeListener((ChangeEvent e) -> buttonOK.setEnabled(m_PanelHelp.getSelectedClass() != null));

    buttonClose = new JButton("Cancel");
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
    if ((m_PanelHelp.getSelectedClass() == null) || !m_PanelHelp.getSelectedClass().equals("" + getValue()))
      m_PanelHelp.setSelectedClass("" + getValue());
    m_PanelHelp.setToolTipText(((BaseObject) getValue()).getTipText());
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
    setValue(new BaseClassname(value));
  }

  /**
   * Returns the current value.
   * 
   * @return		the current value
   */
  public String getInlineValue() {
    return ((BaseClassname) getValue()).getValue();
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
