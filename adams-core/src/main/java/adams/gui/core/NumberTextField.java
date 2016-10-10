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
 * NumberTextField.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.Utils;

import javax.swing.text.Document;

/**
 * A specialized text field for numbers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NumberTextField
  extends CheckedTextField {

  /** for serialization. */
  private static final long serialVersionUID = -2752548576627240791L;

  /**
   * The type of number to accomodate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Type {
    BYTE,
    SHORT,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE
  }

  /**
   * A check model for numbers. The type of number to check for can be
   * specified.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class NumberCheckModel
    extends AbstractCheckModel {

    /** for serialization. */
    private static final long serialVersionUID = -6009963184478082822L;

    /** the type of numbers to handle. */
    protected Type m_Type;

    /**
     * Initializes the model with 0 as default value.
     *
     * @param type	the type of number to handle
     */
    public NumberCheckModel(Type type) {
      this(type, null);
    }

    /**
     * Initializes the model with the specified default value.
     *
     * @param type	the type of number to handle
     * @param defValue	the default value, use "null" to use as null
     */
    public NumberCheckModel(Type type, Number defValue) {
      super();

      m_Type = type;

      if (defValue == null) {
	switch (m_Type) {
	  case BYTE:
	    defValue = new Byte((byte) 0);
	    break;
	  case SHORT:
	    defValue = new Short((short) 0);
	    break;
	  case INTEGER:
	    defValue = new Integer(0);
	    break;
	  case LONG:
	    defValue = new Long(0);
	    break;
	  case FLOAT:
	    defValue = new Float(0.0);
	    break;
	  case DOUBLE:
	    defValue = new Double(0.0);
	    break;
	  default:
	    throw new IllegalArgumentException("Unhandled number type: " + m_Type);
	}
      }

      m_DefaultValue = defValue.toString();
    }

    /**
     * Checks whether the content is valid.
     *
     * @param text	the string to check
     * @return		true if valid
     */
    @Override
    public boolean isValid(String text) {
      boolean	result;

      if (text.trim().length() == 0)
	return true;
      
      try {
	switch (m_Type) {
	  case BYTE:
	    Byte.parseByte(text);
	    break;
	  case SHORT:
	    Short.parseShort(text);
	    break;
	  case INTEGER:
	    Integer.parseInt(text);
	    break;
	  case LONG:
	    Long.parseLong(text);
	    break;
	  case FLOAT:
	    if (Utils.toFloat(text) == null)
	      throw new IllegalArgumentException();
	    break;
	  case DOUBLE:
	    if (Utils.toDouble(text) == null)
	      throw new IllegalArgumentException();
	    break;
	  default:
	    throw new IllegalArgumentException("Unhandled number type: " + m_Type);
	}
        result = true;
      }
      catch (Exception e) {
        result = false;
      }

      return result;
    }

    /**
     * Returns the type of numbers being checked for.
     *
     * @return		the type of numbers
     */
    public Type getType() {
      return m_Type;
    }

    /**
     * Returns a short string representation.
     *
     * @return		the string representation
     */
    @Override
    public String toString() {
      return super.toString() + ", type=" + m_Type;
    }
  }

  /**
   * A check model for numbers. The type of number to check for can be
   * specified.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class BoundedNumberCheckModel
    extends NumberCheckModel {

    /** for serialization. */
    private static final long serialVersionUID = -6009963184478082822L;

    /** the lower bound (null for unbounded). */
    protected Number m_LowerBound;

    /** the upper bound (null for unbounded). */
    protected Number m_UpperBound;

    /**
     * Initializes the model with 0 as default value.
     *
     * @param type	the type of number to handle
     * @param lower	the lower bound (null for unbounded)
     * @param upper	the upper bound (null for unbounded)
     */
    public BoundedNumberCheckModel(Type type, Number lower, Number upper) {
      this(type, lower, upper, null);
    }

    /**
     * Initializes the model with the specified default value.
     *
     * @param type	the type of number to handle
     * @param lower	the lower bound (null for unbounded)
     * @param upper	the upper bound (null for unbounded)
     * @param defValue	the default value, use "null" to use as null
     */
    public BoundedNumberCheckModel(Type type, Number lower, Number upper, Number defValue) {
      super(type, defValue);

      m_LowerBound = lower;
      m_UpperBound = upper;
    }

    /**
     * Checks whether the content is valid.
     *
     * @param text	the string to check
     * @return		true if valid
     */
    @Override
    public boolean isValid(String text) {
      boolean	result;
      Double	value;

      result = super.isValid(text);

      // check bounds
      if (result) {
	try {
	  value = Utils.toDouble(text);
	}
	catch (Exception e) {
	  value = null;
	}

	result = (value != null);

	if (result) {
	  if ((m_LowerBound != null) && (value.doubleValue() < m_LowerBound.doubleValue()))
	    result = false;

	  if ((m_UpperBound != null) && (value.doubleValue() > m_UpperBound.doubleValue()))
	    result = false;
	}
      }

      return result;
    }

    /**
     * Returns the lower bound, if any.
     *
     * @return		the lower bound, null if unbounded
     */
    public Number getLowerBound() {
      return m_LowerBound;
    }

    /**
     * Returns the upper bound, if any.
     *
     * @return		the upper bound, null if unbounded
     */
    public Number getUpperBound() {
      return m_UpperBound;
    }

    /**
     * Returns a short string representation.
     *
     * @return		the string representation
     */
    @Override
    public String toString() {
      return super.toString() + ", lower=" + m_LowerBound + ", upper=" + m_UpperBound;
    }
  }

  /**
   * Constructs a new <code>TextField</code>.  A default model is created,
   * the initial string is <code>null</code>,
   * and the number of columns is set to 0.
   *
   * @param type	the type of numbers to check for
   */
  public NumberTextField(Type type) {
    super(new NumberCheckModel(type));
  }

  /**
   * Constructs a new <code>TextField</code> initialized with the
   * specified text. A default model is created and the number of
   * columns is 0.
   *
   * @param type	the type of numbers to check for
   * @param text the text to be displayed, or <code>null</code>
   */
  public NumberTextField(Type type, String text) {
    super(text, new NumberCheckModel(type));
  }

  /**
   * Constructs a new empty <code>TextField</code> with the specified
   * number of columns.
   * A default model is created and the initial string is set to
   * <code>null</code>.
   *
   * @param type	the type of numbers to check for
   * @param columns  the number of columns to use to calculate
   *   the preferred width; if columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation
   */
  public NumberTextField(Type type, int columns) {
    super(columns, new NumberCheckModel(type));
  }

  /**
   * Constructs a new <code>TextField</code> initialized with the
   * specified text and columns.  A default model is created.
   *
   * @param type	the type of numbers to check for
   * @param text the text to be displayed, or <code>null</code>
   * @param columns  the number of columns to use to calculate
   *   the preferred width; if columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation
   */
  public NumberTextField(Type type, String text, int columns) {
    super(text, columns, new NumberCheckModel(type));
  }

  /**
   * Constructs a new <code>JTextField</code> that uses the given text
   * storage model and the given number of columns.
   * This is the constructor through which the other constructors feed.
   * If the document is <code>null</code>, a default model is created.
   *
   * @param type	the type of numbers to check for
   * @param doc  the text storage to use; if this is <code>null</code>,
   *		a default will be provided by calling the
   *		<code>createDefaultModel</code> method
   * @param text  the initial string to display, or <code>null</code>
   * @param columns  the number of columns to use to calculate
   *   the preferred width >= 0; if <code>columns</code>
   *   is set to zero, the preferred width will be whatever
   *   naturally results from the component implementation
   */
  public NumberTextField(Type type, Document doc, String text, int columns) {
    super(doc, text, columns, new NumberCheckModel(type));
  }

  /**
   * Returns the default model to use.
   *
   * @return		the default model (for type Type.DOUBLE)
   */
  @Override
  protected AbstractCheckModel getDefaultCheckModel() {
    return new NumberCheckModel(Type.DOUBLE);
  }

  /**
   * Sets the underlying check model.
   *
   * @param value	the model for checking the input
   */
  @Override
  public void setCheckModel(AbstractCheckModel value) {
    if (value instanceof NumberCheckModel)
      super.setCheckModel(value);
    else
      throw new IllegalArgumentException(
	  "Only " + NumberCheckModel.class.getName() + " models are allowed!");
  }

  /**
   * Sets the current value.
   *
   * @param value	the value to use
   */
  public void setValue(Number value) {
    if (value == null)
      setText("");
    else
      setText(value.toString());
  }

  /**
   * Returns the current value.
   *
   * @return		the current value, {@link Double#NaN} if empty
   */
  public Number getValue() {
    return getValue(Double.NaN);
  }

  /**
   * Returns the current value.
   *
   * @param defValue	the default value to return in case the text is empty
   * @return		the current value
   */
  public Number getValue(Number defValue) {
    if (getText().isEmpty())
      return defValue;
    else
      return Double.parseDouble(getText());
  }
}
