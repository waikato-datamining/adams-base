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
 * RegExpTextField.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.base.BaseRegExp;

/**
 * Text field designed for entering a regular expression.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RegExpTextField
  extends BaseObjectTextField<BaseRegExp> {

  private static final long serialVersionUID = -6624338080908941975L;

  /**
   * Constructs a new <code>TextField</code>.
   */
  public RegExpTextField() {
    this(BaseRegExp.MATCH_ALL);
  }

  /**
   * Constructs a new <code>TextField</code>.
   *
   * @param initial	the initial string
   */
  public RegExpTextField(String initial) {
    super(new BaseRegExp(initial), initial);
  }

  /**
   * Sets the regular expression.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    setObject(value);
  }

  /**
   * Returns the current regular expression.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return getObject();
  }
}
