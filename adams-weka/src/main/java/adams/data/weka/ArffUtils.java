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
 * ArffUtils.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.data.weka;

import adams.data.report.AbstractField;

/**
 * A helper class for ARFF related stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArffUtils {

  /** the prefix for notes. */
  public final static String PREFIX_NOTE = "note-";

  /** the prefix for additional fields. */
  public final static String PREFIX_ADDITIONALFIELDS = "additional-";

  /**
   * Returns the name of the attribute containing the database ID.
   *
   * @return		the attribute name
   */
  public static String getDBIDName() {
    return "db_id";
  }

  /**
   * Returns the name of the attribute containing the ID of the data container.
   *
   * @return		the attribute name
   */
  public static String getIDName() {
    return "id";
  }

  /**
   * Returns the name of an attribute for a field.
   *
   * @param field	the field to generate the name for
   * @return		the attribute name
   */
  public static String getFieldName(AbstractField field) {
    return field.getName();
  }

  /**
   * Returns the name of an attribute for an additional field. Gets prefixed
   * with "additional-".
   *
   * @param field	the field to generate the name for
   * @return		the attribute name
   * @see		#PREFIX_ADDITIONALFIELDS
   */
  public static String getAdditionalFieldName(AbstractField field) {
    return PREFIX_ADDITIONALFIELDS + field.getName();
  }

  /**
   * Returns the name of an attribute for a note. Gets prefixed
   * with "note-".
   *
   * @param prefix	the note prefix to generate the name for
   * @return		the attribute name
   * @see		#PREFIX_NOTE
   */
  public static String getNoteName(String prefix) {
    return PREFIX_NOTE + prefix;
  }
}
