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
 * DateTextField.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;


import java.util.Date;

import javax.swing.text.Document;

import adams.core.DateFormat;

/**
 * A specialized text field for dates.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DateTextField
  extends CheckedTextField {

  /** for serialization. */
  private static final long serialVersionUID = 662410175911423633L;

  /**
   * A model for checking dates. It allows a custom date format.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DateCheckModel
    extends AbstractCheckModel {

    /** for serialization. */
    private static final long serialVersionUID = -2579549735806129821L;

    /** the format in use. */
    protected String m_Format;

    /** the formatter object. */
    protected transient DateFormat m_DateFormat;

    /**
     * Initializes the check model with the current date/time as default
     * date and with default date format.
     *
     * @see		DateFormat#DEFAULT_FORMAT
     */
    public DateCheckModel() {
      this(DateFormat.DEFAULT_FORMAT);
    }

    /**
     * Initializes the check model with the current date/time as default
     * date.
     *
     * @param format	the format to use
     */
    public DateCheckModel(String format) {
      this(format, new Date());
    }

    /**
     * Initializes the check model with the current date/time as default
     * date.
     *
     * @param format	the format to use
     * @param defValue	the default date to use
     */
    public DateCheckModel(String format, Date defValue) {
      super();

      m_Format       = format;
      m_DefaultValue = getDateFormat().format(defValue);
    }

    /**
     * Returns the DateFormat object to use, creates it if necessary.
     *
     * @return		the date formatter
     */
    protected synchronized DateFormat getDateFormat() {
      if (m_DateFormat == null)
	m_DateFormat = new DateFormat(m_Format);

      return m_DateFormat;
    }

    /**
     * Checks whether the content is valid.
     *
     * @param text	the string to check
     * @return		true if valid
     */
    public boolean isValid(String text) {
      boolean	result;

      try {
        getDateFormat().parse(text);
        result = true;
      }
      catch (Exception e) {
        result = false;
      }

      return result;
    }

    /**
     * Returns the underlying format.
     *
     * @return		format
     */
    public String getFormat() {
      return m_Format;
    }

    /**
     * Returns a short string representation.
     *
     * @return		the string representation
     */
    public String toString() {
      return super.toString() + ", format=" + m_Format;
    }
  }

  /**
   * Constructs a new <code>TextField</code>.  A default model is created,
   * the initial string is <code>null</code>,
   * and the number of columns is set to 0.
   *
   * @param format	the date format to use
   */
  public DateTextField(String format) {
    super(new DateCheckModel(format));
  }

  /**
   * Constructs a new <code>TextField</code> initialized with the
   * specified text. A default model is created and the number of
   * columns is 0.
   *
   * @param format	the date format to use
   * @param text the text to be displayed, or <code>null</code>
   */
  public DateTextField(String format, String text) {
    super(text, new DateCheckModel(format));
  }

  /**
   * Constructs a new empty <code>TextField</code> with the specified
   * number of columns.
   * A default model is created and the initial string is set to
   * <code>null</code>.
   *
   * @param format	the date format to use
   * @param columns  the number of columns to use to calculate
   *   the preferred width; if columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation
   */
  public DateTextField(String format, int columns) {
    super(columns, new DateCheckModel(format));
  }

  /**
   * Constructs a new <code>TextField</code> initialized with the
   * specified text and columns.  A default model is created.
   *
   * @param format	the date format to use
   * @param text the text to be displayed, or <code>null</code>
   * @param columns  the number of columns to use to calculate
   *   the preferred width; if columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation
   */
  public DateTextField(String format, String text, int columns) {
    super(text, columns, new DateCheckModel(format));
  }

  /**
   * Constructs a new <code>JTextField</code> that uses the given text
   * storage model and the given number of columns.
   * This is the constructor through which the other constructors feed.
   * If the document is <code>null</code>, a default model is created.
   *
   * @param format	the date format to use
   * @param doc  the text storage to use; if this is <code>null</code>,
   *		a default will be provided by calling the
   *		<code>createDefaultModel</code> method
   * @param text  the initial string to display, or <code>null</code>
   * @param columns  the number of columns to use to calculate
   *   the preferred width >= 0; if <code>columns</code>
   *   is set to zero, the preferred width will be whatever
   *   naturally results from the component implementation
   */
  public DateTextField(String format, Document doc, String text, int columns) {
    super(doc, text, columns, new DateCheckModel(format));
  }

  /**
   * Returns the default model to use.
   *
   * @return		the default model
   */
  protected AbstractCheckModel getDefaultCheckModel() {
    return new DateCheckModel();
  }

  /**
   * Sets the underlying check model.
   *
   * @param value	the model for checking the input
   */
  public void setCheckModel(AbstractCheckModel value) {
    if (value instanceof DateCheckModel)
      super.setCheckModel(value);
    else
      throw new IllegalArgumentException(
	  "Only " + DateCheckModel.class.getName() + " models are allowed!");
  }
}
