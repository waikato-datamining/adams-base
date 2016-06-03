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
 * RegExpConstrainedTextField.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.base.BaseRegExp;

/**
 * Text field designed for entering a string that is constrained by a regular
 * expression.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RegExpConstrainedTextField
  extends CheckedTextField {

  private static final long serialVersionUID = -6624338080908941975L;

  /**
   * A model for checking entered string again regexp.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 4584 $
   */
  public static class RegExpConstraintCheckModel
    extends AbstractCheckModel {

    /** for serialization. */
    private static final long serialVersionUID = -2579549735806129821L;

    /** the regular expression to use for checking. */
    protected BaseRegExp m_RegExp;

    /**
     * Initializes the model.
     *
     * @param regExp	the regular expression to use for checking
     */
    public RegExpConstraintCheckModel(BaseRegExp regExp) {
      super();
      m_RegExp = regExp;
    }

    /**
     * Sets the regular expression.
     *
     * @param value	the regular expression
     */
    public void setRegExp(BaseRegExp value) {
      m_RegExp = value;
    }

    /**
     * Returns the current regular expression.
     *
     * @return		the regular expression
     */
    public BaseRegExp getRegExp() {
      return m_RegExp;
    }

    /**
     * Checks whether the content is valid.
     *
     * @param text	the string to check
     * @return		true if valid
     */
    public boolean isValid(String text) {
      return m_RegExp.isMatch(text);
    }
  }

  /**
   * Constructs a new <code>TextField</code>. A default model is created,
   * the initial string is <code></code>,
   * and the number of columns is set to 0.
   */
  public RegExpConstrainedTextField(BaseRegExp regExp) {
    this(regExp,  "");
  }

  /**
   * Constructs a new <code>TextField</code>. A default model is created
   * and the number of columns is set to 0.
   *
   * @param text	the initial string
   */
  public RegExpConstrainedTextField(BaseRegExp regExp, String text) {
    super(text, new RegExpConstraintCheckModel(regExp));
  }

  /**
   * Sets the regular expression.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    ((RegExpConstraintCheckModel) m_CheckModel).setRegExp(value);
  }

  /**
   * Returns the current regular expression.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return ((RegExpConstraintCheckModel) m_CheckModel).getRegExp();
  }
}
