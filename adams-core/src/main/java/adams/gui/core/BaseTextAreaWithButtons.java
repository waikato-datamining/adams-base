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
 * BaseTextAreaWithButtons.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import javax.swing.text.Document;
import java.awt.Font;

/**
 * Graphical component that consists of a BaseTable with buttons on the
 * right-hand side.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseTextAreaWithButtons
  extends AbstractComponentWithButtons<BaseTextArea>
  implements TextAreaComponent {

  /** for serialization. */
  private static final long serialVersionUID = 1935542795448084154L;

  /**
   * The default constructor.
   */
  public BaseTextAreaWithButtons() {
    super();
  }

  /**
   * Initializes the list with the given text.
   *
   * @param text	the text to use
   */
  public BaseTextAreaWithButtons(String text) {
    super();

    setText(text);
  }

  /**
   * Initializes the list with the given rows/columns.
   *
   * @param rows	the rows to use
   * @param columns	the columns to use
   */
  public BaseTextAreaWithButtons(int rows, int columns) {
    super();

    setRows(rows);
    setColumns(columns);
  }

  /**
   * Initializes the list with the given text.
   *
   * @param text	the text to use
   * @param rows	the rows to use
   * @param columns	the columns to use
   */
  public BaseTextAreaWithButtons(String text, int rows, int columns) {
    super();

    setText(text);
    setRows(rows);
    setColumns(columns);
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
  public BaseTextArea createComponent() {
    BaseTextArea	result;
    
    result = new BaseTextArea();
    result.setWrapStyleWord(true);
    
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
   * Sets the rows.
   *
   * @param value	the rows
   */
  public void setRows(int value) {
    m_Component.setRows(value);
  }

  /**
   * Returns the rows.
   *
   * @return		the rows
   */
  public int getRows() {
    return m_Component.getRows();
  }

  /**
   * Sets the columns.
   *
   * @param value	the columns
   */
  public void setColumns(int value) {
    m_Component.setColumns(value);
  }

  /**
   * Returns the columns.
   *
   * @return		the columns
   */
  public int getColumns() {
    return m_Component.getColumns();
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
   * Sets whether to line wrap or not.
   *
   * @param value	if true line wrap is enabled
   */
  public void setLineWrap(boolean value) {
    m_Component.setLineWrap(value);
  }

  /**
   * Returns whether line wrap is enabled.
   *
   * @return		true if line wrap wrap is enabled
   */
  public boolean getLineWrap() {
    return m_Component.getLineWrap();
  }

  /**
   * Sets whether to word wrap or not.
   *
   * @param value	if true word wrap is enabled
   */
  public void setWrapStyleWord(boolean value) {
    m_Component.setWrapStyleWord(value);
  }

  /**
   * Returns whether word wrap is enabled.
   *
   * @return		true if word wrap wrap is enabled
   */
  public boolean getWrapStyleWord() {
    return m_Component.getWrapStyleWord();
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
}
