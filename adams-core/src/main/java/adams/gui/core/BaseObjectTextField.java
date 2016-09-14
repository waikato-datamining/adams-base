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
 * BaseObjectTextField.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.base.BaseObject;

/**
 * Text field designed for entering a strings checked by a BaseObject derived
 * object.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseObjectTextField<T extends BaseObject>
  extends CheckedTextField {

  private static final long serialVersionUID = -6624338080908941975L;

  /**
   * A model for checking regexp values.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 4584 $
   */
  public static class BaseObjectCheckModel<T extends BaseObject>
    extends AbstractCheckModel {

    /** for serialization. */
    private static final long serialVersionUID = -2579549735806129821L;

    /** the object used for the checks. */
    protected T m_Check;

    /**
     * Initializes the model.
     *
     * @param check	the object to use for performing the checks
     */
    public BaseObjectCheckModel(T check) {
      super();
      m_Check = check;
    }

    /**
     * Checks whether the content is valid.
     *
     * @param text	the string to check
     * @return		true if valid
     */
    public boolean isValid(String text) {
      return m_Check.isValid(text);
    }

    /**
     * Instantiates a new object with the given text.
     *
     * @param text	the text to use (only if valid)
     * @return		the generated object
     */
    public T newObject(String text) {
      T		result;

      result = (T) m_Check.getClone();
      if (isValid(text))
	result.setValue(text);

      return result;
    }
  }

  /**
   * Constructs a new <code>TextField</code>. The initial value is the
   * current value of the check object.
   */
  public BaseObjectTextField(T check) {
    this(check, check.getValue());
  }

  /**
   * Constructs a new <code>TextField</code>.
   *
   * @param initial	the initial string
   */
  public BaseObjectTextField(T check, String initial) {
    super(initial, new BaseObjectCheckModel<>(check));
    if (check.getTipText() != null)
      setToolTipText(check.getTipText());
  }

  /**
   * Sets the object.
   *
   * @param value	the object
   */
  public void setObject(T value) {
    setText(value.getValue());
  }

  /**
   * Returns the current object.
   *
   * @return		the object
   */
  public T getObject() {
    return ((BaseObjectCheckModel<T>) m_CheckModel).newObject(getText());
  }
}
