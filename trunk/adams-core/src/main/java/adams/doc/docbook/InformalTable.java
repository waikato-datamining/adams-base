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
 * InformalTable.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.docbook;

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
public class InformalTable
  extends AbstractComplexTag {

  /** for serialization. */
  private static final long serialVersionUID = -1301634257381722080L;

  /** the table head. */
  protected AbstractComplexTag m_Head;

  /** the table body. */
  protected AbstractComplexTag m_Body;

  /** the number of columns in the table. */
  protected int m_NumCols;

  /**
   * Initializes the tag.
   *
   * @param numCols	the number of columns in the table
   */
  public InformalTable(int numCols) {
    super("informaltable");
    m_NumCols = numCols;
    DefaultComplexTag tgroup = new DefaultComplexTag("tgroup");
    tgroup.setAttribute("cols", "" + numCols);
    add(tgroup);
    m_Head = null;
    m_Body = new DefaultComplexTag("tbody");
    tgroup.add(m_Body);
  }

  /**
   * Initializes the tag.
   *
   * @param cells	the cells for the table header
   */
  public InformalTable(String[] cells) {
    super("informaltable");
    m_NumCols = cells.length;
    DefaultComplexTag tgroup = new DefaultComplexTag("tgroup");
    tgroup.setAttribute("cols", "" + cells.length);
    add(tgroup);
    m_Head = new DefaultComplexTag("thead");
    tgroup.add(m_Head);
    m_Body = new DefaultComplexTag("tbody");
    tgroup.add(m_Body);
    addRow(m_Head, cells);
  }

  /**
   * Initializes the tag.
   *
   * @param cells	the cells for the table header
   */
  public InformalTable(AbstractTag[] cells) {
    super("informaltable");
    m_NumCols = cells.length;
    DefaultComplexTag tgroup = new DefaultComplexTag("tgroup");
    tgroup.setAttribute("cols", "" + cells.length);
    add(tgroup);
    m_Head = new DefaultComplexTag("thead");
    tgroup.add(m_Head);
    m_Body = new DefaultComplexTag("tbody");
    tgroup.add(m_Body);
    addRow(m_Head, cells);
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
   * Checks whether a head element is available.
   *
   * @return		true if a head is available
   */
  public boolean hasHead() {
    return (m_Head != null);
  }

  /**
   * Returns the head element, which contains the row elements.
   *
   * @return		the head element, null if no head defined
   */
  public AbstractComplexTag getHead() {
    return m_Head;
  }

  /**
   * Returns the body element, which contains the row elements.
   *
   * @return		the body element
   */
  public AbstractComplexTag getBody() {
    return m_Body;
  }

  /**
   * Adds the row to the specified parent.
   *
   * @param parent	the parent to add the row to
   * @param cells	the cells of the row
   * @return		the row element
   */
  protected AbstractComplexTag addRow(AbstractComplexTag parent, String[] cells) {
    AbstractComplexTag	result;
    DefaultSimpleTag	entry;

    if (cells.length != m_NumCols)
      throw new IllegalArgumentException("Number of columns differ (expected/actual): " + m_NumCols + " != " + cells.length);

    result = new DefaultComplexTag("row");
    parent.add(result);
    for (String cell: cells) {
      entry = new DefaultSimpleTag("entry", cell);
      result.add(entry);
    }

    return result;
  }

  /**
   * Adds the row to the body.
   *
   * @param cells	the cells of the row
   * @return		the row element
   */
  public AbstractComplexTag addRow(String[] cells) {
    return addRow(getBody(), cells);
  }

  /**
   * Adds the row to the specified parent.
   *
   * @param parent	the parent to add the row to
   * @param cells	the cells of the row
   * @return		the row element
   */
  protected AbstractComplexTag addRow(AbstractComplexTag parent, AbstractTag[] cells) {
    AbstractComplexTag	result;
    DefaultComplexTag	entry;

    if (cells.length != m_NumCols)
      throw new IllegalArgumentException("Number of columns differ (expected/actual): " + m_NumCols + " != " + cells.length);

    result = new DefaultComplexTag("row");
    parent.add(result);
    for (AbstractTag cell: cells) {
      entry = new DefaultComplexTag("entry");
      entry.add(cell);
      result.add(entry);
    }

    return result;
  }

  /**
   * Adds the row to the body.
   *
   * @param cells	the cells of the row
   * @return		the row element
   */
  public AbstractComplexTag addRow(AbstractTag[] cells) {
    return addRow(getBody(), cells);
  }
}
