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
 * BaseTextChooserPanel.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.Utils;
import adams.core.base.BaseText;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.TextPanel;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;

/**
 * A panel that contains a text field with the current text and a button
 * to bring up a text editor dialog.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseTextChooserPanel
  extends AbstractChooserPanel<BaseText> {

  /** for serialization. */
  private static final long serialVersionUID = -8755020252465094120L;

  /** the title for the text dialog. */
  protected String m_DialogTitle;

  /** the dialog size. */
  protected Dimension m_DialogSize;

  /**
   * Initializes the panel with no text.
   */
  public BaseTextChooserPanel() {
    this(new BaseText(""));
  }

  /**
   * Initializes the panel with the given text.
   *
   * @param text	the text to use
   */
  public BaseTextChooserPanel(BaseText text) {
    super();

    setCurrent(text);
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_DialogTitle = "Enter text";
    m_DialogSize  = new Dimension(400, 300);
  }

  /**
   * Sets the title for the text dialog.
   *
   * @param value	the title
   */
  public void setDialogTitle(String value) {
    m_DialogTitle = value;
  }

  /**
   * Returns the title for the text dialog.
   *
   * @return		the title
   */
  public String getDialogTitle() {
    return m_DialogTitle;
  }

  /**
   * Sets the size of the text dialog.
   *
   * @param value	the size
   */
  public void setDialogSize(Dimension value) {
    m_DialogSize = value;
  }

  /**
   * Returns the size of the text dialog.
   *
   * @return		the size
   */
  public Dimension getDialogSize() {
    return m_DialogSize;
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  protected BaseText doChoose() {
    ApprovalDialog	dialog;
    TextPanel			textPanel;

    if (getParentDialog() != null)
      dialog = ApprovalDialog.getDialog(getParentDialog());
    else
      dialog = ApprovalDialog.getDialog(getParentFrame());
    dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    textPanel = new TextPanel();
    textPanel.setContent(getCurrent().getValue());
    textPanel.setTitle(m_DialogTitle);
    dialog.getContentPane().add(textPanel, BorderLayout.CENTER);
    dialog.setSize(m_DialogSize);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION)
      return new BaseText(textPanel.getContent());
    else
      return null;
  }

  /**
   * Converts the value into its string representation, i.e., back quoted.
   *
   * @param value	the value to convert
   * @return		the generated string
   * @see		Utils#backQuoteChars(String)
   */
  protected String toString(BaseText value) {
    return Utils.backQuoteChars(value.getValue());
  }

  /**
   * Converts the string representation into its object representation.
   * The input string is expected to be backquoted.
   *
   * @param value	the string value to convert
   * @return		the generated object
   * @see		Utils#unbackQuoteChars(String)
   */
  protected BaseText fromString(String value) {
    return new BaseText(Utils.unbackQuoteChars(value));
  }
}
