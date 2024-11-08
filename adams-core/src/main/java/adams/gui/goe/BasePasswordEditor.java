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
 * BasePasswordEditor.java
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.core.base.BasePassword;
import adams.core.option.parsing.BasePasswordParsing;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BasePasswordField;
import adams.gui.core.GUIHelper;

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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Editor specifically designed for entering passwords.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BasePasswordEditor
  extends BaseObjectEditor
  implements MultiSelectionEditor {

  /** the checkbox for "show password". */
  protected BaseCheckBox m_CheckBoxShowPassword;

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  protected JComponent createCustomEditor() {
    JPanel	panelAll;
    JPanel	panelCheck;
    JPanel	panel;
    JLabel	label;
    JPanel 	panelButtons;
    BaseButton 	buttonOK;
    BaseButton 	buttonClose;

    panelAll = new JPanel(new BorderLayout());
    panel    = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelAll.add(panel, BorderLayout.NORTH);
    m_TextValue = new BasePasswordField(20);
    ((BasePasswordField) m_TextValue).setShowPopupMenu(true);
    m_TextValue.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	  e.consume();
	  acceptInput();
	}
	else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	  e.consume();
	  discardInput();
	}
	else {
	  super.keyPressed(e);
	}
      }
    });
    label = new JLabel("Password");
    label.setDisplayedMnemonic('p');
    label.setLabelFor(m_TextValue);
    panel.add(label);
    panel.add(m_TextValue);

    panelCheck = new JPanel(new BorderLayout());
    panelAll.add(panelCheck, BorderLayout.CENTER);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelCheck.add(panel, BorderLayout.NORTH);
    m_CheckBoxShowPassword = new BaseCheckBox("Show password");
    m_CheckBoxShowPassword.setMnemonic('S');
    m_CheckBoxShowPassword.setToolTipText("If checked, the password will be shown in clear text as you type it");
    m_CheckBoxShowPassword.addActionListener((ActionEvent e) ->
	((BasePasswordField) m_TextValue).setPasswordVisible(m_CheckBoxShowPassword.isSelected()));
    panelCheck.add(m_CheckBoxShowPassword);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);

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
   * Returns the string to paint.
   *
   * @return		the string
   * @see		#paintValue(Graphics, Rectangle)
   */
  protected String getStringToPaint() {
    return ((BasePassword) getValue()).stringValue();
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return BasePasswordParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return BasePasswordParsing.valueOf(null, str);
  }
  
  /**
   * Checks whether inline editing is available.
   * 
   * @return		true if editing available
   */
  public boolean isInlineEditingAvailable() {
    return false;
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    BasePassword[]		result;
    MultiLineValueDialog	dialog;
    List<String> 		lines;
    int				i;

    if (GUIHelper.getParentDialog(parent) != null)
      dialog = new MultiLineValueDialog(GUIHelper.getParentDialog(parent));
    else
      dialog = new MultiLineValueDialog(GUIHelper.getParentFrame(parent));
    dialog.setInfoText("Enter the passwords, one per line:");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    lines  = dialog.getValues();
    result = new BasePassword[lines.size()];
    for (i = 0; i < lines.size(); i++)
      result[i] = new BasePassword(lines.get(i));

    return result;
  }
}
