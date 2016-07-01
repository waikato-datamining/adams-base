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
 * TextDialog.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import adams.gui.core.BaseDialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;

/**
 * A simple dialog for displaying text.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextDialog
  extends BaseDialog {

  /** for serialization. */
  private static final long serialVersionUID = -5335911070392516986L;

  /** for displaying the text. */
  protected TextPanel m_TextPanel;

  /**
   * Creates a modeless dialog without a title and without a specified Frame
   * owner.
   */
  public TextDialog() {
    super();
  }

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public TextDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public TextDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public TextDialog(Dialog owner, String title) {
    super(owner, title);
    setDialogTitle(title);
  }

  /**
   * Creates a dialog with the specified title, modality and the specified
   * owner Dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   * @param modality	the type of modality
   */
  public TextDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
    setDialogTitle(title);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public TextDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public TextDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public TextDialog(Frame owner, String title) {
    super(owner, title);
    setDialogTitle(title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public TextDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    setDialogTitle(title);
  }

  /**
   * For initializing the GUI.
   */
  protected void initGUI() {
    super.initGUI();

    getContentPane().setLayout(new BorderLayout());

    m_TextPanel = new TextPanel();
    getContentPane().add(m_TextPanel, BorderLayout.CENTER);

    setJMenuBar(m_TextPanel.getMenuBar());

    setSize(600, 800);
  }

  /**
   * Sets the (base) title to use.
   * 
   * @param value	the base title
   * @see		TextPanel#setTitle(String)
   */
  public void setDialogTitle(String value) {
    m_TextPanel.setTitle(value);
  }
  
  /**
   * Returns the (base) title in use.
   *
   * @return		the base title
   * @see		TextPanel#getTitle()
   */
  public String getDialogTitle() {
    return m_TextPanel.getTitle();
  }
  
  /**
   * Sets the modified state.
   *
   * @param value 	if true then the content is flagged as modified
   */
  public void setModified(boolean value) {
    m_TextPanel.setModified(value);
  }

  /**
   * Returns whether the content has been modified.
   *
   * @return		true if the content was modified
   */
  public boolean isModified() {
    return m_TextPanel.isModified();
  }

  /**
   * Sets the content to display. Resets the modified state.
   *
   * @param value	the text
   */
  public void setContent(String value) {
    m_TextPanel.setContent(value);
  }

  /**
   * Returns the content to display.
   *
   * @return		the text
   */
  public String getContent() {
    return m_TextPanel.getContent();
  }

  /**
   * Sets whether the text area is editable or not.
   *
   * @param value	if true then the text will be editable
   */
  public void setEditable(boolean value) {
    m_TextPanel.setEditable(value);
  }

  /**
   * Returns whether the text area is editable or not.
   *
   * @return		true if the text is editable
   */
  public boolean isEditable() {
    return m_TextPanel.isEditable();
  }

  /**
   * Sets the font of the text area.
   *
   * @param value	the font to use
   */
  public void setTextFont(Font value) {
    m_TextPanel.setTextFont(value);
  }

  /**
   * Returns the font currently in use by the text area.
   *
   * @return		the font in use
   */
  public Font getTextFont() {
    return m_TextPanel.getTextFont();
  }

  /**
   * Sets the tab size, i.e., the number of maximum width characters.
   *
   * @param value	the number of maximum width chars
   */
  public void setTabSize(int value) {
    m_TextPanel.setTabSize(value);
  }

  /**
   * Returns the tab size, i.e., the number of maximum width characters.
   *
   * @return		the number of maximum width chars
   */
  public int getTabSize() {
    return m_TextPanel.getTabSize();
  }

  /**
   * Enables/disables line wrap.
   *
   * @param value	if true line wrap gets enabled
   */
  public void setLineWrap(boolean value) {
    m_TextPanel.setLineWrap(value);
  }

  /**
   * Returns whether line wrap is enabled.
   *
   * @return		true if line wrap enabled
   */
  public boolean getLineWrap() {
    return m_TextPanel.getLineWrap();
  }

  /**
   * Sets whether to update the parent's title.
   *
   * @param value	if true the parent's title will get updated
   */
  public void setUpdateParentTitle(boolean value) {
    m_TextPanel.setUpdateParentTitle(value);
  }

  /**
   * Returns whether to update the parent's title.
   *
   * @return		true if to update the parent's title
   */
  public boolean getUpdateParentTitle() {
    return m_TextPanel.getUpdateParentTitle();
  }
}
