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
 * AbstractField.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.report;

import adams.core.CloneHandler;
import adams.core.Utils;

import java.io.Serializable;

/**
 * A single report field identifier.
 *
 * @author  dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractField
  implements Serializable, Comparable, CloneHandler<AbstractField> {

  /** suid. */
  private static final long serialVersionUID = -5720659277852926115L;

  /** the separator. */
  public final static String SEPARATOR = "\t";

  /** the escaped separator. */
  public final static String SEPARATOR_ESCAPED = "\\t";

  /** the replacement for tabs in the fields when displaying in the GUI. */
  public final static String SEPARATOR_DISPLAY = " | ";

  /** the name of the field. */
  protected String m_Name;

  /** the prefix part of the name (if applicable). */
  protected String m_Prefix;

  /** the suffix part of the name (if applicable). */
  protected String m_Suffix;

  /** the data type. */
  protected DataType m_DataType;

  /**
   * Constructor. Sets the name to null and the type to UNKNOWN.
   */
  public AbstractField() {
    this((String) null, DataType.UNKNOWN);
  }

  /**
   * Uses the values from the given field.
   *
   * @param field	the field to use as basis
   */
  public AbstractField(AbstractField field) {
    this(field.getName(), field.getDataType());
  }

  /**
   * Constructor.
   *
   * @param prefix	the prefix of the compound field
   * @param suffix	the prefix of the compound field
   * @param dt		the type of the field, UNKNOWN is used if null
   */
  public AbstractField(String prefix, String suffix, DataType dt) {
    this(prefix + SEPARATOR + suffix, dt);
  }

  /**
   * Constructor.
   *
   * @param name	the name of the field
   * @param dt		the type of the field, UNKNOWN is used if null
   */
  public AbstractField(String name, DataType dt) {
    if (name != null)
      m_Name = fixString(unescape(name));

    if (dt == null)
      m_DataType = DataType.UNKNOWN;
    else
      m_DataType = dt;

    m_Prefix = null;
    m_Suffix = null;
  }

  /**
   * Get field name.
   *
   * @return		the field name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Get datatype.
   *
   * @return		the data type
   */
  public DataType getDataType() {
    return m_DataType;
  }

  /**
   * Checks whether the name is a compound one, i.e., contains a SEPARATOR.
   *
   * @return		true if name is a compound one
   * @see		#SEPARATOR
   */
  public boolean isCompound() {
    if ((m_Name == null) || (m_Name.length() == 0))
      return false;
    else
      return (m_Name.indexOf(SEPARATOR) > -1);
  }

  /**
   * Returns the name split up in its single parts.
   *
   * @return		the parts of the compound name
   * @see		#SEPARATOR
   */
  public String[] split() {
    if (m_Name == null)
      return new String[0];
    else
      return m_Name.split(SEPARATOR);
  }

  /**
   * Returns the name and the type (format: name[type]). Can be restored with parseField.
   *
   * @return		the name and type
   * @see		#parseField(String)
   */
  public String toParseableString() {
    if (m_Name == null)
      return "";
    else
      return escape(m_Name, SEPARATOR_ESCAPED) + "[" + getDataType() + "]";
  }

  /**
   * Returns the name of the field.
   *
   * @return		the name
   */
  @Override
  public String toString() {
    if (m_Name == null)
      return "";
    else
      return escape(m_Name, SEPARATOR_ESCAPED);
  }

  /**
   * Returns the name of the field.
   *
   * @return		the name
   */
  public String toDisplayString() {
    if (m_Name == null)
      return "";
    else
      return escape(m_Name, SEPARATOR_DISPLAY);
  }

  /**
   * String representation.
   *
   * @param o		the object to turn into a string
   * @return		the string
   */
  public String toString(Object o) {
    return o.toString();
  }

  /**
   * Parse string to appropriate datatype.
   *
   * @param s		the string to parse
   * @return		the parsed object: STRING, NUMERIC or BOOLEAN
   */
  public Object valueOf(String s) {
    Object	result;

    result = null;

    try {
      switch(m_DataType) {
	case STRING:
	  result = fixString(s);
	  break;
	case NUMERIC:
	  result = Utils.toDouble(s);
	  break;
	case BOOLEAN:
	  result = Boolean.parseBoolean(s);
	  break;
	default:
	  throw new IllegalStateException("Unhandlded type '" + m_DataType + "'!");
      }
    }
    catch(Exception e) {
      result = null;
    }

    return result;
  }

  /**
   * Escapes the name.
   *
   * @param name	the name to escape
   * @param separator	the separator to use
   * @return		the escaped name
   */
  protected static String escape(String name, String separator) {
    String	result;

    result = name;
    result = result.replace(SEPARATOR, separator);
    result = result.replace("[", "\\[");
    result = result.replace("]", "\\]");

    return result;
  }

  /**
   * Unescapes the name.
   *
   * @param name	the name to unescape
   * @return		the unescaped name
   */
  protected static String unescape(String name) {
    String	result;

    result = name;
    result = result.replace(SEPARATOR_ESCAPED, SEPARATOR);
    result = result.replace("\\[", "[");
    result = result.replace("\\]", "]");

    return result;
  }

  /**
   * Parses the given string and returns the field. The type of the field
   * can be append with parentheses: name[type]. Otherwise, UNKNOWN is used
   * as type.
   *
   * @param s		the string to parse
   * @return		the parsed field
   */
  public static AbstractField parseField(String s) {
    Field	result;
    String	name;
    DataType	type;
    String	typeStr;

    name = s;
    type = DataType.UNKNOWN;
    if ((s.length() > 3) && s.endsWith("]")) {
      typeStr = s.substring(s.length() - 3);
      if (typeStr.startsWith("[") && (typeStr.length() == 3)) {
	typeStr = typeStr.substring(1, 2);
	for (DataType t: DataType.values()) {
	  if (t.toDisplay().equals(typeStr)) {
	    type = t;
	    name = s.substring(0, s.length() - 3);
	    break;
	  }
	}
      }
    }

    result = new Field(unescape(name), type);

    return result;
  }

  /**
   * Returns a cloned copy of itself.
   *
   * @return		the clone
   */
  public AbstractField getClone() {
    return newField(m_Name, m_DataType);
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

    if (!(o instanceof AbstractField))
      return -1;

    f = (AbstractField) o;

    if ((getName() == null) && (f.getName() == null))
      return 0;

    if (getName() == null)
      return -1;

    if (f.getName() == null)
      return 1;

    return getName().toLowerCase().compareTo(f.getName().toLowerCase());
  }

  /**
   * The equals method (only uses the name).
   *
   * @param o		the object to compare with
   * @return		true if the same name
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * name.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    if (m_Name == null)
      return -1;
    else
      return getName().hashCode();
  }

  /**
   * Returns the prefix for compound fields.
   *
   * @return		the prefix, null if not compound field
   */
  public String getPrefix() {
    if (!isCompound()) {
      return null;
    }
    else {
      if (m_Prefix == null)
	m_Prefix = split()[0];
      return m_Prefix;
    }
  }

  /**
   * Returns the suffix for compound fields.
   *
   * @return		the suffix, null if not compound field
   */
  public String getSuffix() {
    if (!isCompound()) {
      return null;
    }
    else {
      if (m_Suffix == null)
	m_Suffix = split()[1];
      return m_Suffix;
    }
  }

  /**
   * Returns a new field.
   *
   * @param name	the name of the field
   * @param dtype	the data type of the field
   * @return		the new field
   */
  public abstract AbstractField newField(String name, DataType dtype);

  /**
   * Replaces the prefix of a Field and returns a new Field object. The Field
   * must be a compound object, if not a clone of the field is returned.
   *
   * @param prefix	the new prefix
   * @return		the generated field
   */
  public AbstractField replacePrefix(String prefix) {
    return replacePrefix(prefix, m_DataType);
  }

  /**
   * Replaces the prefix of a Field and returns a new Field object. The Field
   * must be a compound object, if not a clone of the field is returned.
   *
   * @param prefix	the new prefix
   * @param dt		the data type to use
   * @return		the generated field
   */
  public abstract AbstractField replacePrefix(String prefix, DataType dt);

  /**
   * Replaces the suffix of a Field and returns a new Field object. The Field
   * must be a compound object, if not a clone of the field is returned.
   *
   * @param suffix	the new suffix
   * @return		the generated field
   */
  public AbstractField replaceSuffix(String suffix) {
    return replaceSuffix(suffix, m_DataType);
  }

  /**
   * Replaces the suffix of a Field and returns a new Field object. The Field
   * must be a compound object, if not a clone of the field is returned.
   *
   * @param suffix	the new suffix
   * @param dt		the data type to use
   * @return		the generated field
   */
  public abstract AbstractField replaceSuffix(String suffix, DataType dt);

  /**
   * Replaces ' with `.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  public static String fixString(String s) {
    return s.replace('\'', '`');
  }
}
