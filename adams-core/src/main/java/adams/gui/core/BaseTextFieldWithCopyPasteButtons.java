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
 * BaseTextFieldWithCopyPasteButtons.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import com.github.fracpete.jclipboardhelper.ClipboardHelper;

/**
 * Text field with copy and paste button.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseTextFieldWithCopyPasteButtons
  extends BaseTextFieldWithButtons {

  /** the copy button. */
  protected BaseButton m_ButtonCopy;

  /** the paste button. */
  protected BaseButton m_ButtonPaste;

  /**
   * The default constructor.
   */
  public BaseTextFieldWithCopyPasteButtons() {
    super();
  }

  /**
   * Initializes the list with the given text.
   *
   * @param text	the text to use
   */
  public BaseTextFieldWithCopyPasteButtons(String text) {
    super();
    setText(text);
  }

  /**
   * Constructs a new empty <code>TextField</code> with the specified
   * number of columns.
   * A default model is created and the initial string is set to
   * <code>null</code>.
   *
   * @param columns  the number of columns to use to calculate
   *   the preferred width; if columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation
   */
  public BaseTextFieldWithCopyPasteButtons(int columns) {
    super();
    setColumns(columns);
  }

  /**
   * Constructs a new <code>TextField</code> initialized with the
   * specified text and columns.  A default model is created.
   *
   * @param text the text to be displayed, or <code>null</code>
   * @param columns  the number of columns to use to calculate
   *   the preferred width; if columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation
   */
  public BaseTextFieldWithCopyPasteButtons(String text, int columns) {
    super();
    setText(text);
    setColumns(columns);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_ButtonCopy = new BaseButton(ImageManager.getIcon("copy"));
    m_ButtonCopy.addActionListener(e -> copy());
    addToButtonsPanel(m_ButtonCopy);

    m_ButtonPaste = new BaseButton(ImageManager.getIcon("paste"));
    m_ButtonPaste.addActionListener(e -> paste());
    addToButtonsPanel(m_ButtonPaste);
  }

  /**
   * Copies the text to the clipboard
   */
  public void copy() {
    if (!isCopyButtonVisible())
      return;
    if ((m_Component.getSelectedText() == null) && !m_Component.getText().isEmpty())
      ClipboardHelper.copyToClipboard(m_Component.getText());
    else if (m_Component.getSelectedText() != null)
      ClipboardHelper.copyToClipboard(m_Component.getSelectedText());
  }

  /**
   * Pastes text from the clipboard.
   */
  public void paste() {
    if (!isPasteButtonVisible())
      return;
    m_Component.paste();
  }

  /**
   * Sets whether the copy button is visible.
   * 
   * @param value	true if visible
   */
  public void setCopyButtonVisible(boolean value) {
    m_ButtonCopy.setVisible(value);
  }

  /**
   * Returns whether the copy button is visible.
   * 
   * @return		true if visible
   */
  public boolean isCopyButtonVisible() {
    return m_ButtonCopy.isVisible();
  }

  /**
   * Sets whether the paste button is visible.
   *
   * @param value	true if visible
   */
  public void setPasteButtonVisible(boolean value) {
    m_ButtonPaste.setVisible(value);
  }

  /**
   * Returns whether the paste button is visible.
   *
   * @return		true if visible
   */
  public boolean isPasteButtonVisible() {
    return m_ButtonPaste.isVisible();
  }
}
