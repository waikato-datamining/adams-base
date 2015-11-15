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
 * BaseTextPaneWithButtons.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import java.awt.Font;

/**
 * {@link BaseTextPaneWithButtons} with additional support for buttons.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseTextPaneWithButtons
  extends AbstractComponentWithButtons<BaseTextPaneWithWordWrap>
  implements TextPaneComponent {

  private static final long serialVersionUID = -8562372761976614736L;

  /**
   * The default constructor.
   */
  public BaseTextPaneWithButtons() {
    super();
  }

  /**
   * Initializes the list with the given text.
   *
   * @param text	the text to use
   */
  public BaseTextPaneWithButtons(String text) {
    super();

    setText(text);
  }

  /**
   * Returns whether the component requires a JScrollPane around it.
   *
   * @return		true if the component requires a JScrollPane
   */
  @Override
  public boolean requiresScrollPane() {
    return true;
  }

  /**
   * Returns the underlying text area.
   *
   * @return		the underlying text area
   */
  @Override
  public BaseTextPaneWithWordWrap createComponent() {
    BaseTextPaneWithWordWrap	result;

    result = new BaseTextPaneWithWordWrap();
    result.setWordWrap(true);

    return result;
  }

  /**
   * Returns the underlying document.
   *
   * @return		the document
   */
  public Document getDocument() {
    return m_Component.getDocument();
  }

  /**
   * Sets the text.
   *
   * @param value	the text to display
   */
  public void setText(String value) {
    m_Component.setText(value);
  }

  /**
   * Returns the underlying text.
   *
   * @return		the underlying text
   */
  public String getText() {
    return m_Component.getText();
  }

  /**
   * Returns the underlying text.
   *
   * @return		the underlying text
   */
  public String getSelectedText() {
    return m_Component.getSelectedText();
  }

  /**
   * Sets whether the text area is editable or not.
   *
   * @param value	if true the text area is editable
   */
  public void setEditable(boolean value) {
    m_Component.setEditable(value);
  }

  /**
   * Returns whether the text area is editable or not.
   *
   * @return		true if the text area is editable
   */
  public boolean isEditable() {
    return m_Component.isEditable();
  }

  /**
   * Sets the text font.
   *
   * @param value	the font
   */
  public void setTextFont(Font value) {
    m_Component.setFont(value);
  }

  /**
   * Returns the text font in use.
   *
   * @return		the font
   */
  public Font getTextFont() {
    return m_Component.getFont();
  }

  /**
   * Sets the caret position.
   *
   * @param pos 	the position (0-based)
   */
  public void setCaretPosition(int pos) {
    m_Component.setCaretPosition(pos);
  }

  /**
   * Returns the current caret position.
   *
   * @return		the position (0-based)
   */
  public int getCaretPosition() {
    return m_Component.getCaretPosition();
  }

  /**
   * Sets the position of the cursor at the end.
   */
  @Override
  public void setCaretPositionLast() {
    m_Component.setCaretPositionLast();
  }

  /**
   * Appends the text at the end.
   *
   * @param text	the text to append
   */
  public synchronized void append(String text) {
    append(text, null);
  }

  /**
   * Appends the text at the end.
   *
   * @param text	the text to append
   * @param a		the attribute set, null if to use current
   */
  public synchronized void append(String text, AttributeSet a) {
    m_Component.append(text, a);
  }
}
