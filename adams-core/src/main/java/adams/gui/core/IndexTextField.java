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
 * IndexTextField.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.Index;

/**
 * Text field designed for entering a single index, eg for attributes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IndexTextField
  extends CheckedTextField {

  private static final long serialVersionUID = -6624338080908941975L;

  /**
   * A model for checking Index values. It allows a custom date format.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 4584 $
   */
  public static class IndexCheckModel
    extends AbstractCheckModel {

    /** for serialization. */
    private static final long serialVersionUID = -2579549735806129821L;

    /**
     * Checks whether the content is valid.
     *
     * @param text	the string to check
     * @return		true if valid
     */
    public boolean isValid(String text) {
      boolean 	result;
      Index	index;

      try {
	index  = new Index(text, Integer.MAX_VALUE);
	result = (index.getIntIndex() != -1);
      }
      catch (Exception e) {
	e.printStackTrace();
	result = false;
      }

      return result;
    }
  }

  /**
   * Constructs a new <code>TextField</code>. A default model is created,
   * the initial string is <code>first</code>,
   * and the number of columns is set to 0.
   */
  public IndexTextField() {
    this(Index.FIRST);
  }

  /**
   * Constructs a new <code>TextField</code>. A default model is created
   * and the number of columns is set to 0.
   *
   * @param index	the initial string
   */
  public IndexTextField(String index) {
    super(index, new IndexCheckModel());
  }
}
