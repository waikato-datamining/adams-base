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
 * AttributeTypeList.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

/**
 * Wrapper for a comma-separated list of attribute types.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AttributeTypeList
  extends BaseList {

  /** for serialization. */
  private static final long serialVersionUID = -5853830144343397434L;

  /** the type string for numeric attributes. */
  public final static String ATT_NUMERIC = "NUM";

  /** the type string for nominal attributes. */
  public final static String ATT_NOMINAL = "NOM";

  /** the type string for string attributes. */
  public final static String ATT_STRING = "STR";

  /** the type string for date attributes. */
  public final static String ATT_DATE = "DAT";

  /**
   * Initializes the list with length 0.
   */
  public AttributeTypeList() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public AttributeTypeList(String s) {
    super(s);
  }

  /**
   * Returns the conversion of the string before setting its value.
   *
   * @return		the type of conversion to apply
   */
  @Override
  protected Conversion getConversion() {
    return Conversion.UPPER_CASE;
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		always true
   */
  @Override
  public boolean isValid(String value) {
    String[]	list;
    boolean	result;

    if (value == null)
      return false;
    
    result = true;

    if (value.length() == 0)
      return result;

    value = convert(value);
    if (value.indexOf(',') > -1)
      list = value.split(",");
    else
      list = new String[]{value};
    for (String item: list) {
      if (   item.equals(ATT_NUMERIC)
	  || item.equals(ATT_NOMINAL)
	  || item.equals(ATT_STRING)
	  || item.equals(ATT_DATE) )
	continue;

      result = false;
      break;
    }

    return result;
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return
        "A comma-separated list of attribute types "
      + "("
      + ATT_NUMERIC
      + "|"
      + ATT_NOMINAL
      + "|"
      + ATT_STRING
      + "|"
      + ATT_DATE
      + ").";
  }
}
