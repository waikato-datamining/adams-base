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
 * BaseTextField.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * Custom BaseTextField component.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BaseTextField
  extends JTextField {

  private static final long serialVersionUID = 1849599255714467739L;

  /**
   * Constructs a new <code>TextField</code>.  A default model is created,
   * the initial string is <code>null</code>,
   * and the number of columns is set to 0.
   */
  public BaseTextField() {
    super();
    initTextField();
  }

  /**
   * Constructs a new <code>TextField</code> initialized with the
   * specified text. A default model is created and the number of
   * columns is 0.
   *
   * @param text the text to be displayed, or <code>null</code>
   */
  public BaseTextField(String text) {
    super(text);
    initTextField();
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
  public BaseTextField(int columns) {
    super(columns);
    initTextField();
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
  public BaseTextField(String text, int columns) {
    super(text, columns);
    initTextField();
  }

  /**
   * Constructs a new <code>BaseTextField</code> that uses the given text
   * storage model and the given number of columns.
   * This is the constructor through which the other constructors feed.
   * If the document is <code>null</code>, a default model is created.
   *
   * @param doc  the text storage to use; if this is <code>null</code>,
   *          a default will be provided by calling the
   *          <code>createDefaultModel</code> method
   * @param text  the initial string to display, or <code>null</code>
   * @param columns  the number of columns to use to calculate
   *   the preferred width &gt;= 0; if <code>columns</code>
   *   is set to zero, the preferred width will be whatever
   *   naturally results from the component implementation
   * @exception IllegalArgumentException if <code>columns</code> &lt; 0
   */
  public BaseTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
    initTextField();
  }

  /**
   * Initializes members.
   */
  protected void initTextField() {
  }
}
