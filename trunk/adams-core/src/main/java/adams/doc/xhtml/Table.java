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
 * Table.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.xhtml;

import adams.doc.xml.AbstractComplexTag;
import adams.doc.xml.AbstractTag;
import adams.doc.xml.DefaultComplexTag;
import adams.doc.xml.DefaultSimpleTag;

/**
 * Represents the "informaltable" tag.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Table
  extends AbstractComplexTag {

  /** for serialization. */
  private static final long serialVersionUID = -1301634257381722080L;

  /** the number of columns in the table. */
  protected int m_NumCols;

  /**
   * Initializes the tag.
   *
   * @param numCols	the number of columns in the table
   */
  public Table(int numCols) {
    super("table");
    m_NumCols = numCols;
  }

  /**
   * Returns the number of columns in the table.
   *
   * @return		the number of columns
   */
  public int getNumCols() {
    return m_NumCols;
  }

  /**
   * Adds the row.
   *
   * @param cells	the cells of the row
   * @return		the row element
   */
  public AbstractComplexTag addRow(String[] cells) {
    return addRow(cells, "left", "top");
  }

  /**
   * Adds the row.
   *
   * @param cells	the cells of the row
   * @param align	the horizontal alignment
   * @param valign	the vertical alignment
   * @return		the row element
   */
  public AbstractComplexTag addRow(String[] cells, String align, String valign) {
    AbstractComplexTag	result;
    DefaultSimpleTag	entry;

    if (cells.length != m_NumCols)
      throw new IllegalArgumentException("Number of columns differ (expected/actual): " + m_NumCols + " != " + cells.length);

    result = new DefaultComplexTag("tr");
    add(result);
    for (String cell: cells) {
      entry = new DefaultSimpleTag("td", cell);
      entry.setAttribute("align", align);
      entry.setAttribute("valign", valign);
      result.add(entry);
    }

    return result;
  }

  /**
   * Adds the row.
   *
   * @param cells	the cells of the row
   * @return		the row element
   */
  public AbstractComplexTag addRow(AbstractTag[] cells) {
    return addRow(cells, "left", "top");
  }

  /**
   * Adds the row.
   *
   * @param cells	the cells of the row
   * @param align	the horizontal alignment
   * @param valign	the vertical alignment
   * @return		the row element
   */
  public AbstractComplexTag addRow(AbstractTag[] cells, String align, String valign) {
    AbstractComplexTag	result;
    DefaultComplexTag	entry;

    if (cells.length != m_NumCols)
      throw new IllegalArgumentException("Number of columns differ (expected/actual): " + m_NumCols + " != " + cells.length);

    result = new DefaultComplexTag("tr");
    add(result);
    for (AbstractTag cell: cells) {
      entry = new DefaultComplexTag("td");
      entry.setAttribute("align", align);
      entry.setAttribute("valign", valign);
      entry.add(cell);
      result.add(entry);
    }

    return result;
  }
}
