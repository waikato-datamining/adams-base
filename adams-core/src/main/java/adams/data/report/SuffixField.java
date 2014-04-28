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
 * SuffixField.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.data.report;


/**
 * A compound filed that only displays the second half of the name.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SuffixField
  extends Field
  implements SuffixOnlyField {

  /** for serialization. */
  private static final long serialVersionUID = 8407023575940681345L;

  /** the dummy prefix. */
  public final static String DUMMY_PREFIX = "DUMMY";

  /**
   * Constructor. Sets the name to null and the type to UNKNOWN.
   */
  public SuffixField() {
    this(null, DataType.UNKNOWN);
  }

  /**
   * Uses the values from the given field.
   *
   * @param field	the field to use as basis
   */
  public SuffixField(AbstractField field) {
    this(field.getName(), field.getDataType());
  }

  /**
   * Constructor.
   *
   * @param name	the name of the field
   * @param dt		the type of the field
   */
  public SuffixField(String name, DataType dt) {
    super(name, dt);

    if (m_Name != null) {
      if (!isCompound())
	m_Name = DUMMY_PREFIX + SEPARATOR + m_Name;
      else
	m_Name = DUMMY_PREFIX + SEPARATOR + getSuffix();
    }
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  public int compareTo(Object o) {
    AbstractField	f;

    if (o == null)
      return 1;

    f = (AbstractField) o;

    if ((getSuffix() == null) && (f.getSuffix() == null))
      return 0;

    if (getSuffix() == null)
      return -1;

    if (f.getSuffix() == null)
      return 1;

    return getSuffix().toLowerCase().compareTo(f.getSuffix().toLowerCase());
  }

  /**
   * Returns the prefix of the field.
   *
   * @return		the prefix
   */
  public String toString() {
    if ((m_Name == null) || (m_Name.length() == 0))
      return "";
    else
      return getSuffix();
  }

  /**
   * Returns the suffix of the field.
   *
   * @return		the suffix
   */
  public String toDisplayString() {
    return toString();
  }

  /**
   * Returns a new field.
   *
   * @param name	the name of the field
   * @param dtype	the data type of the field
   * @return		the new field
   */
  protected AbstractField newField(String name, DataType dtype) {
    return new SuffixField(name, dtype);
  }

  /**
   * Parses the given string and returns the field. The type of the field
   * can be append with parentheses: name[type]. Otherwise, UNKNOWN is used
   * as type.
   *
   * @param s		the string to parse
   * @return		the parsed field
   */
  public static SuffixField parseField(String s) {
    SuffixField		result;
    AbstractField	tmp;

    tmp    = adams.data.report.Field.parseField(s);
    result = new SuffixField(tmp);

    return result;
  }
}
