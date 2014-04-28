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
 * Field.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.data.report;

/**
 * A single report field identifier.
 *
 * @author  dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class Field
  extends AbstractField
  implements RegularField {

  /** suid. */
  private static final long serialVersionUID = -5720659277852926115L;

  /**
   * Constructor. Sets the name to null and the type to UNKNOWN.
   */
  public Field() {
    super();
  }

  /**
   * Uses the values from the given field.
   *
   * @param field	the field to use as basis
   */
  public Field(AbstractField field) {
    super(field);
  }

  /**
   * Constructor.
   *
   * @param prefix	the prefix of the compound field
   * @param suffix	the prefix of the compound field
   * @param dt		the type of the field, UNKNOWN is used if null
   */
  public Field(String prefix, String suffix, DataType dt) {
    super(prefix, suffix, dt);
  }

  /**
   * Constructor.
   *
   * @param name	the name of the field
   * @param dt		the type of the field, UNKNOWN is used if null
   */
  public Field(String name, DataType dt) {
    super(name, dt);
  }

  /**
   * Returns a new field.
   *
   * @param name	the name of the field
   * @param dtype	the data type of the field
   * @return		the new field
   */
  protected AbstractField newField(String name, DataType dtype) {
    return new Field(name, dtype);
  }

  /**
   * Replaces the prefix of a Field and returns a new Field object. The Field
   * must be a compound object, if not a clone of the field is returned.
   *
   * @param prefix	the new prefix
   * @param dt		the data type
   * @return		the generated field
   */
  public AbstractField replacePrefix(String prefix, DataType dt) {
    if (!isCompound())
      return getClone();
    else
      return new Field(prefix, getSuffix(), dt);
  }

  /**
   * Replaces the suffix of a Field and returns a new Field object. The Field
   * must be a compound object, if not a clone of the field is returned.
   *
   * @param suffix	the new suffix
   * @param dt		the data type
   * @return		the generated field
   */
  public AbstractField replaceSuffix(String suffix, DataType dt) {
    if (!isCompound())
      return getClone();
    else
      return new Field(getPrefix(), suffix, dt);
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
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    String s;
    AbstractField f;

    s = "hello_world";
    f = Field.parseField(s);
    System.out.println(s + " -> " + f.getName() + "/" + f.getDataType());
    System.out.println("  -> toString: " + f);
    System.out.println("  -> toParseableString: " + f.toParseableString());

    s = "hello[S]";
    f = Field.parseField(s);
    System.out.println(s + " -> " + f.getName() + "/" + f.getDataType());
    System.out.println("  -> toString: " + f);
    System.out.println("  -> toParseableString: " + f.toParseableString());

    s = "hello[s]world[B]";
    f = Field.parseField(s);
    System.out.println(s + " -> " + f.getName() + "/" + f.getDataType());
    System.out.println("  -> toString: " + f);
    System.out.println("  -> toParseableString: " + f.toParseableString());
  }
}
