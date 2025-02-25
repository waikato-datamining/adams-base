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
 * EmailAddressEditor.java
 * Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.net.EmailAddress;
import adams.core.net.EmailAddressBook;
import adams.core.net.EmailContact;
import adams.core.option.parsing.EmailAddressParsing;
import adams.gui.core.BaseButton;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.DoubleClickEvent;
import adams.gui.event.DoubleClickListener;
import adams.gui.selection.SelectEmailAddressPanel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A PropertyEditor for EmailAddress objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see EmailAddress
 */
public class EmailAddressEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, MultiSelectionEditor {

  /** The panel used for selecting fields. */
  protected SelectEmailAddressPanel m_SelectPanel;

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
    return EmailAddressParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return EmailAddressParsing.valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String		result;
    EmailAddress 	email;

    email = (EmailAddress) getValue();

    if (email == null)
      result = "null";
    else
      result = "new " + EmailAddress.class.getName() + "(\"" + email.toString() + "\")";

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
    m_SelectPanel = new SelectEmailAddressPanel();
    m_SelectPanel.addDoubleClickListener(new DoubleClickListener() {
      public void doubleClickOccurred(DoubleClickEvent e) {
	if (m_SelectPanel.getItem() != null)
	  m_ButtonOK.doClick();
      }
    });
    panel.add(m_SelectPanel, BorderLayout.CENTER);

    JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(panelButtons, BorderLayout.SOUTH);

    m_ButtonOK = new BaseButton("OK");
    m_ButtonOK.setMnemonic('O');
    m_ButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_SelectPanel.getItem() != null) {
	  EmailAddress field = m_SelectPanel.getItem().toEmailAddress();
	  EmailAddress current = (EmailAddress) getValue();
	  if (!current.equals(field))
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
	m_SelectPanel.setItem(EmailAddressBook.getSingleton().getContact((EmailAddress) getValue()));
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
    m_SelectPanel.setMultipleSelection(false);
    m_SelectPanel.setItem(
	EmailAddressBook.getSingleton().getContact((EmailAddress) getValue()));
    m_SelectPanel.scrollIntoView();
    m_SelectPanel.grabFocus();
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
    EmailAddress	curr;
    String 		val;

    curr = (EmailAddress) getValue();
    val  = curr.stringValue();
    if (val == null)
      val = "";
    if (val.isEmpty())
      val = AbstractPropertyEditorSupport.EMPTY;
    GUIHelper.configureAntiAliasing(gfx, true);
    offset = GUIHelper.calculateFontOffset(gfx, box);
    gfx.drawString(val, offset[0], offset[1]);
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    EmailAddress[]			result;
    final ApprovalDialog		dialog;
    final SelectEmailAddressPanel	panel;
    EmailContact[]			contacts;
    int					i;

    dialog = ApprovalDialog.getDialog(GUIHelper.getParentDialog(parent));

    panel = new SelectEmailAddressPanel();
    panel.addDoubleClickListener(new DoubleClickListener() {
      public void doubleClickOccurred(DoubleClickEvent e) {
	if (panel.getItem() != null)
	  dialog.getApproveButton().doClick();
      }
    });
    panel.setMultipleSelection(true);
    panel.setItem(null);

    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      contacts = panel.getItems();
      result = new EmailAddress[contacts.length];
      for (i = 0; i < contacts.length; i++)
	result[i] = contacts[i].toEmailAddress();
    }
    else {
      result = new EmailAddress[0];
    }
    
    return result;
  }
}
