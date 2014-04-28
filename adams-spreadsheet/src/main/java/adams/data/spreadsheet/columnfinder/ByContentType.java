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
 * ByContentType.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.columnfinder;

import gnu.trove.list.array.TIntArrayList;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Returns indices of columns that satisfy the specified content types.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByContentType
  extends AbstractColumnFinder {

  /** for serialization. */
  private static final long serialVersionUID = 2989233908194930918L;
  
  /** the regular expression to match the attribute names against. */
  protected ContentType[] m_ContentTypes;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the indices of columns that match any of the specified content types.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "content-type", "contentTypes",
	    new ContentType[]{ContentType.DOUBLE});
  }

  /**
   * Sets the content types that the columns can have.
   *
   * @param value	the allowed types
   */
  public void setContentTypes(ContentType[] value) {
    m_ContentTypes = value;
    reset();
  }

  /**
   * Returns the content types that the columns can have.
   *
   * @return		the allowed types
   */
  public ContentType[] getContentTypes() {
    return m_ContentTypes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String contentTypesTipText() {
    return "The content types that the columns can have.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "contentTypes", Utils.arrayToString(m_ContentTypes), "types: ");
  }

  /**
   * Returns the columns of interest in the spreadsheet.
   * 
   * @param data	the spreadsheet to inspect
   * @return		the columns of interest
   */
  @Override
  protected int[] doFindColumns(SpreadSheet data) {
    TIntArrayList		result;
    HashSet<ContentType>	types;
    int				i;
    Collection<ContentType>	colTypes;
    
    result = new TIntArrayList();
    
    types  = new HashSet<ContentType>(Arrays.asList(m_ContentTypes));
    for (i = 0; i < data.getColumnCount(); i++) {
      colTypes = data.getContentTypes(i);
      if (types.containsAll(colTypes))
	result.add(i);
    }
    
    return result.toArray();
  }
}
