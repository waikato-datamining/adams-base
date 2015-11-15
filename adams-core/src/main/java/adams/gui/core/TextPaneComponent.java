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
 * TextPaneComponent.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.text.Document;

/**
 * TODO: What class does.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface TextPaneComponent {

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
   * Returns the selected text.
   *
   * @return		the selected text
   */
  public String getSelectedText();

  /**
   * Sets whether the text pane is editable or not.
   *
   * @param value if true the text pane is editable
   */
  public void setEditable(boolean value);

  /**
   * Returns whether the text pane is editable or not.
   *
   * @return true if the text pane is editable
   */
  public boolean isEditable();

  /**
   * Returns the underlying document.
   *
   * @return		the document
   */
  public Document getDocument();

  /**
   * Sets the position of the cursor.
   *
   * @param value	the position
   */
  public void setCaretPosition(int value);

  /**
   * Returns the current position of the cursor.
   *
   * @return		the cursor position
   */
  public int getCaretPosition();

  /**
   * Sets the position of the cursor at the end.
   */
  public void setCaretPositionLast();
}
