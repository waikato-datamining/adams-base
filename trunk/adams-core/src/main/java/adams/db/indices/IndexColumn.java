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
 * IndexColumn.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db.indices;

/**
 * An IndexColumn holds the column name and whether ascending or descending
 * ordering. For some keys, like text blobs, a length must be provided.
 *
 * @author dale
 * @version $Revision$
 */
public class IndexColumn {

  /** ascending or descending? */
  protected String m_asc_or_desc; // default to ascending

  /** column name. */
  protected String m_columnName;

  /** the length of the key (e.g., for an index on a text blob). */
  protected int m_Length;

  /**
   * Constructor. Default to ascending.
   *
   * @param cname	column name
   */
  public IndexColumn(String cname) {
    this(cname, true);
  }

  /**
   * Constructor. Key length is -1.
   *
   * @param cname	column
   * @param ascending	ascending order?
   */
  public IndexColumn(String cname, boolean ascending) {
    this(cname, ascending, -1);
  }

  /**
   * Constructor.
   *
   * @param cname	column
   * @param ascending	ascending order?
   * @param length	the length of the key, use -1 to ignore
   */
  public IndexColumn(String cname, boolean ascending, int length) {
    this(cname, (ascending ? "A" : "D"), length);
  }

  /**
   * Constructor. Key length is -1.
   *
   * @param cname		column name
   * @param asc_or_desc		ordering string
   */
  public IndexColumn(String cname, String asc_or_desc) {
    this(cname, asc_or_desc, -1);
  }

  /**
   * Constructor.
   *
   * @param cname		column name
   * @param asc_or_desc		ordering string
   * @param length		the length of the index, use -1 to ignore
   */
  public IndexColumn(String cname, String asc_or_desc, int length) {
    super();

    m_columnName  = cname;
    m_asc_or_desc = asc_or_desc;
    m_Length      = length;
  }

  /**
   * Returns the name of the column.
   *
   * @return		the name of the column
   */
  public String getColumnName() {
    return m_columnName;
  }

  /**
   * Returns the order string.
   *
   * @return		the order string
   */
  public String getAscOrDesc() {
    return m_asc_or_desc;
  }

  /**
   * Returns the length of the key.
   *
   * @return		the key length
   */
  public int getLength() {
    return m_Length;
  }

  /**
   * Return true if given IndexColumn matches this one.
   *
   * @param ic		IndexCOlumn to compare
   * @return	equal?
   */
  public boolean equals(IndexColumn ic) {
    return toString().equals(ic.toString());
  }

  /**
   * Return string representation.
   *
   * @return string representation
   */
  public String toString() {
    String	result;

    result = m_columnName;

    if (m_Length != -1)
      result += "(" + m_Length + ")";

    if (m_asc_or_desc.equals("D"))
      result += " DESC";
    else
      result += " ASC";

    return result;
  }
}
