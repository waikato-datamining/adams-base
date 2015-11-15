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
 * TextAreaComponent.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.text.Document;
import java.awt.Font;

/**
 * Common interface for TextArea-like components.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface TextAreaComponent {

  /**
   * Sets the text to display.
   *
   * @param value the text
   */
  public void setText(String value);

  /**
   * Returns the text to display.
   *
   * @return the text
   */
  public String getText();

  /**
   * Returns the underlying document.
   *
   * @return the document
   */
  public Document getDocument();

  /**
   * Returns the underlying text.
   *
   * @return the underlying text
   */
  public String getSelectedText();

  /**
   * Sets the rows.
   *
   * @param value the rows
   */
  public void setRows(int value);

  /**
   * Returns the rows.
   *
   * @return the rows
   */
  public int getRows();

  /**
   * Sets the columns.
   *
   * @param value the columns
   */
  public void setColumns(int value);

  /**
   * Returns the columns.
   *
   * @return the columns
   */
  public int getColumns();

  /**
   * Sets whether the text area is editable or not.
   *
   * @param value if true the text area is editable
   */
  public void setEditable(boolean value);

  /**
   * Returns whether the text area is editable or not.
   *
   * @return true if the text area is editable
   */
  public boolean isEditable();

  /**
   * Sets whether to line wrap or not.
   *
   * @param value if true line wrap is enabled
   */
  public void setLineWrap(boolean value);

  /**
   * Returns whether line wrap is enabled.
   *
   * @return true if line wrap wrap is enabled
   */
  public boolean getLineWrap();

  /**
   * Sets whether to word wrap or not.
   *
   * @param value if true word wrap is enabled
   */
  public void setWrapStyleWord(boolean value);

  /**
   * Returns whether word wrap is enabled.
   *
   * @return true if word wrap wrap is enabled
   */
  public boolean getWrapStyleWord();

  /**
   * Sets the text font.
   *
   * @param value the font
   */
  public void setTextFont(Font value);

  /**
   * Returns the text font in use.
   *
   * @return the font
   */
  public Font getTextFont();

  /**
   * Sets the caret position.
   *
   * @param pos the position (0-based)
   */
  public void setCaretPosition(int pos);

  /**
   * Returns the current caret position.
   *
   * @return the position (0-based)
   */
  public int getCaretPosition();
}
