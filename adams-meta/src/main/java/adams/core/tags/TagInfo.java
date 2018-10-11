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
 * TagInfo.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.tags;

import java.io.Serializable;

/**
 * Stores information about a tag.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TagInfo
  implements Serializable, Comparable<TagInfo> {

  private static final long serialVersionUID = -9049328563317394497L;

  /** the name. */
  protected String m_Name;

  /** the information. */
  protected String m_Information;

  /** the data type. */
  protected TagDataType m_DataType;

  /** the classes this tag applies to. */
  protected Class[] m_AppliesTo;

  /**
   * Initializes the tag.
   *
   * @param name	the name, ie., the string to use when tagging an object
   * @param info	the help text
   * @param dataType	the data type
   * @param appliesTo	the classes this tag applies to
   */
  public TagInfo(String name, String info, TagDataType dataType, Class[] appliesTo) {
    super();

    m_Name        = name;
    m_Information = info;
    m_DataType    = dataType;
    m_AppliesTo   = appliesTo;
  }

  /**
   * Returns the name of the tag.
   *
   * @return		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns the help for the tag.
   *
   * @return		the help text
   */
  public String getInformation() {
    return m_Information;
  }

  /**
   * Returns the data type of the tag.
   *
   * @return		the data type
   */
  public TagDataType getDataType() {
    return m_DataType;
  }

  /**
   * Returns the classes that this tag applies to.
   *
   * @return		the classes
   */
  public Class[] getAppliesTo() {
    return m_AppliesTo;
  }

  /**
   * Simply compares the names of the tags.
   *
   * @param o		the other tag to compare with
   * @return		less than 0, equal to 0 or greater than 0 if this name
   * 			is less than, equal to or greater than the other one
   */
  @Override
  public int compareTo(TagInfo o) {
    return getName().compareTo(o.getName());
  }

  /**
   * Checks whether to the objects are identical.
   *
   * @param obj		the object to compare with
   * @return		true if the same (ie name)
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof TagInfo) && (compareTo((TagInfo) obj) == 0);
  }

  /**
   * Returns the hashcode of the tag (ie name).
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  /**
   * Returns a short description of the tag.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    return getName() + "/" + getDataType();
  }
}
